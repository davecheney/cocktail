package net.cheney.cocktail.parser;

import java.io.IOException;
import java.net.URI;
import java.nio.ByteBuffer;
import java.util.Iterator;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import net.cheney.cocktail.application.Environment;
import net.cheney.cocktail.application.Path;
import net.cheney.cocktail.message.Header;
import net.cheney.cocktail.message.Headers;
import net.cheney.cocktail.message.Header.Accessor;
import net.cheney.cocktail.message.Request;
import net.cheney.cocktail.message.Request.RequestLine;
import net.cheney.cocktail.message.Version;

public class RequestParser extends HttpParser<Request> {

	private final RequestLineParser requestLineParser = new RequestLineParser();
	private final HeaderParser headerParser = new HeaderParser();

	private RequestLine requestLine;
	private Headers headers;

	public RequestParser.Request parse(ByteBuffer buffer) {
		while (buffer.hasRemaining()) {
			if (requestLine == null) {
				requestLine = requestLineParser.parse(buffer);
			} else {
				if (headers == null) {
					headers = headerParser.parse(buffer);
					if (headers != null) {
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

	private RequestParser.Request createRequest(final RequestLine rl,
			final Headers h) {
		return new RequestParser.Request(rl, h);
	}

	@Override
	public void reset() {
		requestLineParser.reset();
		headerParser.reset();
	}

	public class Request extends net.cheney.cocktail.message.Request implements Environment {

		private final RequestLine rl;
		private final Headers h;
		private ByteBuffer body = null;

		public Request(RequestLine rl, Headers h) {
			this.rl = rl;
			this.h = h;
		}

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
		public Accessor header(Header header) {
			return h.header(header);
		}

		@Override
		public ByteBuffer body() {
			return body;
		}

		public void setBody(ByteBuffer body) {
			this.body = body;
		}

		@Override
		public Path path() {
			return Path.fromURI(uri());
		}

		@Override
		public Path contextPath() {
			return Path.emptyPath();
		}

		@Override
		public boolean hasBody() {
			return body != null;
		}
		
		@Override
		public String toString() {
			return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
		}

		@Override
		public Iterable<Header> keys() {
			return h.keys();
		}

		@Override
		public Iterator<Accessor> iterator() {
			return h.iterator();
		}
	}
}
