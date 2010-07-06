package net.cheney.cocktail.httpsimple;

import static java.lang.String.format;
import static org.apache.commons.lang.StringUtils.join;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Locale;
import java.util.TimeZone;

import net.cheney.cocktail.application.Application;
import net.cheney.cocktail.application.Environment;
import net.cheney.cocktail.application.Path;
import net.cheney.cocktail.io.Channel;
import net.cheney.cocktail.message.Header;
import net.cheney.cocktail.message.Header.Accessor;
import net.cheney.cocktail.message.Request;
import net.cheney.cocktail.message.Request.Method;
import net.cheney.cocktail.message.Response;
import net.cheney.cocktail.message.Response.Status;
import net.cheney.cocktail.message.Version;
import net.cheney.cocktail.parser.RequestParser;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.apache.commons.lang.time.FastDateFormat;
import org.apache.log4j.Logger;

import com.google.common.collect.Iterables;

public class HttpConnection implements Channel.Registration.Handler {
	
	private static final Logger LOG = Logger.getLogger(HttpConnection.class);
	
	private static final String RFC1123_DATE_FORMAT_PATTERN = "EEE, dd MMM yyyy HH:mm:ss zzz";
	private static final FastDateFormat RFC1123_DATE_FORMAT = FastDateFormat.getInstance(RFC1123_DATE_FORMAT_PATTERN, TimeZone.getTimeZone("GMT"), Locale.US);

	
	enum ReadState {
		READ_HEADER, READ_BODY, PANIC;
	}

	private static final Charset US_ASCII = Charset.forName("US-ASCII");

	private ReadState readState = ReadState.READ_HEADER;

	private RequestParser requestParser;

	private final Channel.Registration channel;
	private final Application application;
	private Channel.Reader reader;
	private Channel.Writer writer;

	public HttpConnection(SocketChannel sc, Selector selector, Application application) throws IOException {
		this.channel = Channel.register(selector, sc, SelectionKey.OP_READ, this);
		this.reader = channel.reader(); 
		this.writer = channel.writer();
		this.application = application;
		reset();
	}

	private void reset() throws IOException {
		this.requestParser = new RequestParser();
	}

	@Override
	public void onReadyOps(int readyOps) {
		try {
			switch (readyOps) {
			case SelectionKey.OP_READ:
				doRead();
				break;

			case SelectionKey.OP_WRITE:
				doWrite();
				break;

			case SelectionKey.OP_READ | SelectionKey.OP_WRITE:
				doRead();
				doWrite();
				break;

			default:
				throw new IllegalStateException();
			}
		} catch (IOException e) {
			LOG.error(toString(), e);
			close();
		}
	}

	private void close() {
		channel.close();
	}

	private void doWrite() throws IOException {
		writer = writer.write();
		enableWriteInterestIfThereIsMoreToWrite();
	}

	private void doRead() throws IOException {
		readState = doRead(readState);
	}

	private ReadState doRead(ReadState state) throws IOException {
		switch (state) {
		case READ_HEADER:
			return readHeader();

		case READ_BODY:
			return readBody();
			
		default:
			return panic(state);
		}
	}

	private ReadState readHeader() throws IOException {
//		LOG.debug("readHeader()");
		Request request = requestParser.parse(reader.read());
		if (request == null) {
			return waitForMoreData(ReadState.READ_HEADER);
		} else {
			handleExpect(request);
			return readBody();
		}
	}
	
	private ReadState readBody() throws IOException {
//		LOG.debug("readBody()");
		Request request = requestParser.parse(reader.read());
		if (request == null) {
			return waitForMoreData(ReadState.READ_BODY);
		} else {
			return handleRequest(request);
		}
	}

	private ReadState waitForMoreData(ReadState state) {
		LOG.debug(String.format("waitforMoreData(%s)", state));
		channel.enableReadInterest();
		return state;
	}

	private ReadState handleRequest(Request request) throws IOException {
		LOG.info(request);
		Response response = application.call(createEnvironment(request));
		LOG.info(response);
		sendResponse(response, closeRequested(request, response));
		reset();
		return waitForMoreData(ReadState.READ_HEADER);
	}

	private Environment createEnvironment(final Request request) {
		return new Environment() {

			@Override
			public Accessor header(Header header) {
				return request.header(header);
			}

			@Override
			public Iterable<Header> keys() {
				return request.keys();
			}

			@Override
			public Iterator<Accessor> iterator() {
				return request.iterator();
			}

			@Override
			public Method method() {
				return request.method();
			}

			@Override
			public Version version() {
				return request.version();
			}

			@Override
			public Path path() {
				return Path.fromURI(request.uri());
			}

			@Override
			public Path contextPath() {
				return Path.emptyPath();
			}

			@Override
			public ByteBuffer body() {
				return request.body();
			}

			@Override
			public boolean hasBody() {
				return request.hasBody();
			}
			
		};
	}

	// Expect: is stupid
	private void handleExpect(Request request) throws IOException {
//		LOG.debug("handleExpect()");
		if(request.header(Header.EXPECT).any()) {
			sendExpect();
		}
	}

	private void sendExpect() throws IOException {
		write(US_ASCII.encode(format("HTTP/1.1 %s %s\r\n\r\n", Status.INFO_CONTINUE.code(), Status.INFO_CONTINUE.reason())));		
	}

	private boolean closeRequested(Request request, Response response) {
		return request.closeRequested() ? true : response.closeRequested();
	}

	private void sendResponse(Response response, boolean closeRequested) throws IOException {
		ByteBuffer header = buildHeaderBuffer(response, closeRequested);
		if (response.hasBody()) {
//			if(response.hasChannel()) {
//				write(header, response.channel());
//			} else {
				write(header, response.body());
//			}
		} else {
			write(header);
		}
	}

	private void write(ByteBuffer header, FileChannel source) throws IOException {
		writer = writer.write(header).write(source);
		enableWriteInterestIfThereIsMoreToWrite();
	}

	private void enableWriteInterestIfThereIsMoreToWrite() {
		if (writer.hasRemaning()) {
			channel.enableWriteInterest();
		}		
	}

	private void write(ByteBuffer buffer) throws IOException {
		writer = writer.write(buffer);
		enableWriteInterestIfThereIsMoreToWrite();
	}
	
	private void write(ByteBuffer header, ByteBuffer body) throws IOException {
		writer = writer.write(header, body);
		enableWriteInterestIfThereIsMoreToWrite();
	}

	private final ByteBuffer buildHeaderBuffer(Response response, boolean requestClose) throws IOException {
		CharBuffer buffer = CharBuffer.allocate(8192);
		buffer.append(responseLine(response));

		// http://tools.ietf.org/html/draft-ietf-httpbis-p1-messaging-08#section-3.4
		// elide Content-Length header where not permitted
		// TODO: needs unit test

		Iterable<Header.Accessor> standardHeaders = standardHeaders(response, requestClose);

		for (Header.Accessor header : Iterables.concat(standardHeaders, response)) {
			buffer.append(format("%s: %s\r\n", header.header().name(),
					join(header.iterator(), ',')));
		}
		buffer.append("\r\n");
		return US_ASCII.encode((CharBuffer) buffer.flip());
	}

	private Iterable<Header.Accessor> standardHeaders(final Response response, boolean requestClose) {
		Header.Accessor date = new Header.Accessor() {
			
			@Override
			public Header header() {
				return Header.DATE;
			}
			
			@Override
			protected Collection<String> get() {
				return Arrays.asList(RFC1123_DATE_FORMAT.format(System.currentTimeMillis()));
			}
		};
		Header.Accessor connection = new Header.Accessor() {
			
			@Override
			public Header header() {
				return Header.CONNECTION;
			}
			
			@Override
			protected Collection<String> get() {
				return Arrays.asList("close");
			}
		};
		Header.Accessor contentLength = new Header.Accessor() {
			
			@Override
			public Header header() {
				return Header.CONTENT_LENGTH;
			}
			
			@Override
			protected Collection<String> get() {
				if (response.hasBody()) {
					return Arrays.asList(contentLength());
				} else {
					return Arrays.asList("0");
				}
			}

			private String contentLength() {
				return Long.toString(response.contentLength());
			}
		};
		if(response.mayContainBody()) {
			if(requestClose) {
				if(response.header(Header.CONTENT_LENGTH).any()) {
					return Arrays.asList(date, connection);
				} else{
					return Arrays.asList(date, connection, contentLength);
				} 
			} else {
				if(response.header(Header.CONTENT_LENGTH).any()) {
					return Arrays.asList(date);
				} else {
					return Arrays.asList(date, contentLength);
				}
			}
		} else {
			if(requestClose) {
				return Arrays.asList(date, connection);
			} else {
				return Arrays.asList(date);
			}
		}
	}

	private CharSequence responseLine(Response response) {
		return format("%s %s %s\r\n", response.version(), response.status().code(), response.status().reason());
	}

	protected <T> T panic(T t) {
		close();
		throw new IllegalStateException(t.toString());
	}

	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this,
				ToStringStyle.SHORT_PREFIX_STYLE);
	}
}
