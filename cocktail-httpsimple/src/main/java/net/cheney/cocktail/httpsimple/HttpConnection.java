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

import net.cheney.cocktail.application.Application;
import net.cheney.cocktail.channelio.ChannelReader;
import net.cheney.cocktail.channelio.ChannelWriter;
import net.cheney.cocktail.message.Header;
import net.cheney.cocktail.message.Request;
import net.cheney.cocktail.message.Response;
import net.cheney.cocktail.message.Response.Status;
import net.cheney.cocktail.parser.RequestParser;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.apache.log4j.Logger;

import com.google.common.collect.Iterables;

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
	private RequestParser.Request request;

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
			close();
		}
	}

	private void close() {
		try {
			sk.channel().close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void doWrite() throws IOException {
		channelWriter = channelWriter.write();
		enableWriteInterestIfThereIsMoreToWrite();
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
			return enableReadInterest(ReadState.READ_REQUEST_LINE);
		} else {
			handleExpect();
			if(request.mayHaveBody()) {
				long contentLength = request.contentLength();
				request.setBody(ByteBuffer.allocate((int) contentLength));
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
		LOG.debug("readBody: "+request.body());
		request.body().put(channelReader.read());
		return request.body().hasRemaining() ? enableReadInterest(ReadState.READ_BODY) : handleRequest();
	}

	private ReadState enableReadInterest(ReadState state) {
		enableReadInterest();
		return state;
	}

	private ReadState handleRequest() throws IOException {
		Response response = application.call(request);
		sendResponse(response, closeRequested(request, response));
		reset();
		return enableReadInterest(ReadState.READ_REQUEST_LINE);
	}

	// Expect: is stupid
	private void handleExpect() throws IOException {
		if(request.header(Header.EXPECT).any()) {
			sendExpect();
		}
	}

	private void sendExpect() throws IOException {
		CharBuffer buffer = CharBuffer.allocate(8192);
		buffer.append(format("HTTP/1.1 %s %s\r\n\r\n", Status.INFO_CONTINUE.code(), Status.INFO_CONTINUE.reason()));
		write(US_ASCII.encode((CharBuffer) buffer.flip()));		
	}

	private boolean closeRequested(Request request, Response response) {
		return request.closeRequested() ? true : response.closeRequested();
	}

	private void sendResponse(Response response, boolean closeRequested) throws IOException {
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

	private void write(ByteBuffer header, FileChannel channel, long count) throws IOException {
		channelWriter = channelWriter.write(header).write(channel, count);
		enableWriteInterestIfThereIsMoreToWrite();
	}

	private void enableWriteInterestIfThereIsMoreToWrite() {
		if (channelWriter.hasRemaning()) {
			enableWriteInterest();
		}		
	}

	private void write(ByteBuffer... buffs) throws IOException {
		for (ByteBuffer buffer : buffs) {
			channelWriter = channelWriter.write(buffer);
		}
		enableWriteInterestIfThereIsMoreToWrite();
	}

	private final ByteBuffer buildHeaderBuffer(Response response, boolean requestClose) throws IOException {
		CharBuffer buffer = CharBuffer.allocate(8192);
		buffer.append(responseLine(response));

		// http://tools.ietf.org/html/draft-ietf-httpbis-p1-messaging-08#section-3.4
		// elide Content-Length header where not permitted
		// TODO: needs unit test

		Iterable<Header.Accessor> standardHeaders = standardHeaders(response, requestClose);

		for (Header.Accessor header : Iterables.concat(standardHeaders, response.headers())) {
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
				return Arrays.asList("Sun, 23 May 2010 10:02:46 GMT");
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
				try {
					return Long.toString(response.contentLength());
				} catch (IOException e) {
					// COCK
					throw new RuntimeException(e);
				}
			}
		};
		
		return response.mayContainBody() ? Arrays.asList(date, connection, contentLength) : Arrays.asList(date, connection);
	}

	private CharSequence responseLine(Response response) {
		return format("%s %s %s\r\n", response.version(), response.status().code(), response.status().reason());
	}

	protected <T> T panic() {
		try {
			sk.channel().close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		throw new RuntimeException("PANIC: " + toString());
	}

	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this,
				ToStringStyle.SHORT_PREFIX_STYLE);
	}
}
