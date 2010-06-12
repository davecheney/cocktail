package net.cheney.cocktail.parser;

import java.io.IOException;
import java.net.URI;
import java.nio.ByteBuffer;

import net.cheney.cocktail.message.Header;
import net.cheney.cocktail.message.Header.Accessor;
import net.cheney.cocktail.message.Request;
import net.cheney.cocktail.message.Request.RequestLine;
import net.cheney.cocktail.message.Version;

public class RequestParser extends HttpParser<Request>{

	private final RequestLineParser requestLineParser = new RequestLineParser();
	private final HeaderParser headerParser = new HeaderParser();
	
	private RequestLine requestLine;
	private Headers headers;

	public Request parse(ByteBuffer buffer) {
		while(buffer.hasRemaining()) {
			if(requestLine == null) {
				requestLine = requestLineParser.parse(buffer);
			} else {
				if(headers == null) {
					headers = headerParser.parse(buffer);
					if(headers != null) {
						Request request = createRequest(requestLine, headers);
						requestLine = null;
						headers = null;
						return request;
					}
				}
			}
		}
		return null;
	}

	private Request createRequest(final RequestLine rl, final Headers h){
		return new Request() {

			@Override
			public Method method() {
				return rl.method();
			}

			@Override
			public long contentLength() throws IOException {
				return Long.parseLong(header(Header.CONTENT_LENGTH).getOnlyElementWithDefault("-1"));
			}

			@Override
			public Version version() {
				return rl.version();
			}
			
			@Override
			public URI uri() {
				return rl.uri();
			}

			@Override
			public Iterable<Header> headers() {
				return h.keys();
			}

			@Override
			public Accessor header(Header header) {
				return h.header(header);
			}
			
		};
	}

	@Override
	public void reset() {
		requestLineParser.reset();
		headerParser.reset();
	}
	
}
