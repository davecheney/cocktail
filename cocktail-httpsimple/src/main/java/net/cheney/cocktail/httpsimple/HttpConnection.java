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

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.apache.log4j.Logger;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import net.cheney.cocktail.application.Application;
import net.cheney.cocktail.application.Environment;
import net.cheney.cocktail.application.Path;
import net.cheney.cocktail.channelio.ChannelReader;
import net.cheney.cocktail.channelio.ChannelWriter;
import net.cheney.cocktail.message.Header;
import net.cheney.cocktail.message.Header.Accessor;
import net.cheney.cocktail.message.Request;
import net.cheney.cocktail.message.Response;
import net.cheney.cocktail.message.Version;
import net.cheney.cocktail.message.Request.Method;
import net.cheney.cocktail.parser.RequestParser;

public class HttpConnection {
	
	private static final Logger LOG = Logger.getLogger(HttpConnection.class);

	enum ReadState {
		READ_REQUEST_LINE, READ_BODY, PANIC
	}

	private static final Charset US_ASCII = Charset.forName("US-ASCII");

	private ReadState readState = ReadState.READ_REQUEST_LINE;

	private RequestParser requestParser;

	private final SelectionKey sk;
	private final Application application;
	private ChannelReader channelReader;
	private ChannelWriter channelWriter;
	private Request request;
	private ByteBuffer body;

	public HttpConnection(SocketChannel sc, Selector selector,
			Application application) throws IOException {
		this.sk = sc.register(selector, SelectionKey.OP_READ, this);
		this.channelReader = new ChannelReader(sc);
		this.channelWriter = ChannelWriter.forChannel(sc);
		this.application = application;
		reset();
	}

	private void reset() throws IOException {
		this.request = null;
		this.body = null;
		this.requestParser = new RequestParser();
	}

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
			panic(e);
		}
	}

	private void doWrite() throws IOException {
		channelWriter = channelWriter.write();
		if (channelWriter.hasRemaning()) {
			enableWriteInterest();
		}
	}

	private void enableWriteInterest() {
		enableInterest(SelectionKey.OP_WRITE);
	}

	private void doRead() throws IOException {
		readState = doRead(readState);
	}

	private ReadState doRead(ReadState state) throws IOException {
		switch (state) {
		case READ_REQUEST_LINE:
			return readRequestLine();

		case READ_BODY:
			return readBody();
		
		default:
			return panic();
		}
	}

	private ReadState readRequestLine() throws IOException {
		request = requestParser.parse(channelReader.read());
		if (request == null) {
			enableReadInterest();
			return ReadState.READ_REQUEST_LINE;
		} else {
			long contentLength = request.contentLength();
			if(contentLength > 0 ) {
				body = ByteBuffer.allocate((int) contentLength);
				return readBody();
			} else {
				return handleRequest();
			}
		}
	}

	private void enableReadInterest() {
		enableInterest(SelectionKey.OP_READ);
	}

	private void enableInterest(int ops) {
		sk.interestOps(sk.interestOps() | ops);
	}

	private ReadState readBody() throws IOException {
		LOG.debug("readBody: "+body);
		body.put(channelReader.read());
		return body.hasRemaining() ? enableReadInterest(ReadState.READ_BODY) : handleRequest();
	}

	private ReadState enableReadInterest(ReadState s) {
		enableReadInterest();
		return s;
	}

	private ReadState handleRequest() throws IOException {
		Response response = application.call(createEnvironment(request));
		sendResponse(response, closeRequested(request, response));
		reset();
		enableReadInterest();
		return ReadState.READ_REQUEST_LINE;
	}

	private boolean closeRequested(Request request, Response response) {
		return request.closeRequested() ? true : response.closeRequested();
	}

	private void sendResponse(Response response, boolean closeRequested) throws IOException {
		log(response);
		ByteBuffer header = buildHeaderBuffer(response, closeRequested);
		if (response.hasBody()) {
			if (response.buffer() != null) {
				write(header, response.buffer());
			} else {
				write(header, response.channel(), response.contentLength());
			}
		} else {
			write(header);
		}
	}

	private void log(Response response) {
		LOG.debug(format("%s %s: %s", response.version(), response.status(), response.headers()));
	}

	private void write(ByteBuffer header, FileChannel channel, long count) throws IOException {
		channelWriter = channelWriter.write(header).write(channel, count);
	}

	private void write(ByteBuffer... buffs) throws IOException {
		for (ByteBuffer buffer : buffs) {
			channelWriter = channelWriter.write(buffer);
		}
	}

	private final ByteBuffer buildHeaderBuffer(Response response,
			boolean requestClose) throws IOException {
		CharBuffer buffer = CharBuffer.allocate(8192);
		buffer.append(format("%s %s %s\r\n", response.version(), response
				.status().code(), response.status().reason()));

		// http://tools.ietf.org/html/draft-ietf-httpbis-p1-messaging-08#section-3.4
		// elide Content-Length header where not permitted
		// TODO: needs unit test

		Multimap<Header, String> standardHeaders = createStandardHeaders(
				response, requestClose);
		for (Header header : standardHeaders.keySet()) {
			buffer.append(format("%s: %s\r\n", header.name(),
					join(standardHeaders.get(header).iterator(), ',')));
		}

		for (Header header : response.headers()) {
			buffer.append(format("%s: %s\r\n", header.name(),
					join(response.header(header).iterator(), ',')));
		}
		buffer.append("\r\n");
		return US_ASCII.encode((CharBuffer) buffer.flip());
	}

	private Multimap<Header, String> createStandardHeaders(Response response, boolean requestClose) throws IOException {
		Multimap<Header, String> standardHeaders = emptyMultiMap();
		standardHeaders.put(Header.DATE, "Sun, 23 May 2010 10:02:46 GMT");
		if (response.mayContainBody()) {
			if (response.hasBody()) {
				standardHeaders.put(Header.CONTENT_LENGTH,
						Long.toString(response.contentLength()));
			} else {
				standardHeaders.put(Header.CONTENT_LENGTH, "0");
			}
		}
		if (requestClose) {
			standardHeaders.put(Header.CONNECTION, "close");
		}
		return standardHeaders;
	}

	private Multimap<Header, String> emptyMultiMap() {
		return ArrayListMultimap.create();
	}

	private Environment createEnvironment(final Request request) {
		return Environment.fromRequest(request);
	}

	protected <T> T panic() {
		throw new RuntimeException("PANIC: " + toString());
	}

	protected void panic(Throwable e) {
		throw new RuntimeException("PANIC: " + toString(), e);
	}

	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this,
				ToStringStyle.SHORT_PREFIX_STYLE);
	}
}
