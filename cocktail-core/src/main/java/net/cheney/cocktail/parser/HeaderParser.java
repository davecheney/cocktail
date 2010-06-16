package net.cheney.cocktail.parser;

import static java.lang.String.format;

import java.nio.ByteBuffer;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Deque;
import java.util.Iterator;

import com.google.common.base.Function;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Multimap;

import net.cheney.cocktail.message.Header;
import net.cheney.cocktail.message.Headers;

public class HeaderParser extends HttpParser<Headers> {

	private enum State {
		REQUEST_END, HEADER_KEY, HEADER_DELIMITER, HEADER_VALUE, HEADER_VALUE_END, WHITESPACE, HEADER_VALUE_ESCAPED
	}

	private Deque<State> state = new ArrayDeque<HeaderParser.State>(3);
	private Header header = null;
	private HeaderParser.Headers headers = new HeaderParser.Headers();

	public HeaderParser() {
		state.push(State.REQUEST_END);
		reset();
	}

	@Override
	public Headers parse(ByteBuffer buffer) {
		int offset = buffer.position();
		while (buffer.hasRemaining()) {
			switch (state.peek()) {
			case HEADER_KEY:
				byte b = buffer.get();
				if (isTokenChar(b)) {
					continue;
				} else if (b == ':') {
					int length = buffer.position() - offset;
					String s = new String(buffer.array(), buffer.arrayOffset() + offset, --length, US_ASCII);
					header = parseHeader(s);
					offset = buffer.position();
					state.pop();
				} else if (b == '\r') {
					state.pop();
					state.pop();
					state.pop();
				} else {
					throw new IllegalArgumentException(format("Illegal character '%s' in %s", (char) b, state.peek()));
				}
				break;

			case HEADER_VALUE:
				byte t = buffer.get();
				if (t == ',') {
					int length = buffer.position() - offset;
					String value = new String(buffer.array(), buffer.arrayOffset() + offset, --length, US_ASCII);
					headers.header(header).add(value.trim());
					offset = buffer.position();
				} else if (t == '"') {
					state.push(State.HEADER_VALUE_ESCAPED);
				} else if (isVisibleCharacter(t) || isWhitespace(t)) {
					continue;
				} else if (t == '\r') {
					int length = buffer.position() - offset;
					String value = new String(buffer.array(), buffer.arrayOffset() + offset, --length, US_ASCII);
					headers.header(header).add(value.trim());
					offset = buffer.position();
					state.pop();
				} else {
					throw new IllegalArgumentException(format("Illegal character '%x' in %s", t, state.peek()));
				}
				break;
				
			case HEADER_VALUE_ESCAPED:
				if(buffer.get() == '"') {
					state.pop();
				}
				break;

			case WHITESPACE:
				if (!isWhitespace(buffer.get())) {
					state.pop();
				}
				break;

			case HEADER_VALUE_END:
				byte k = buffer.get();
				if (k == '\n') {
					offset = buffer.position();
					state.pop();
					reset();
				} else {
					throw new IllegalArgumentException(String.format("Illegal character '%h' in %s", k, state.peek()));
				}
				break;
				
			case REQUEST_END:
				byte o = buffer.get();
				if( o == '\n') {
					state.pop();
					return headers;
				}
			}
		}
		buffer.position(offset);
		return null;
	}

	private Header parseHeader(final String s) {
		return new Header() {

			@Override
			public net.cheney.cocktail.message.Header.Type type() {
				return Type.REQUEST;
			}

			@Override
			public String name() {
				return s;
			}
			
			@Override
			public String toString() {
				return name();
			}

		};	
	}

	public void reset() {
		state.push(State.HEADER_VALUE_END);
		state.push(State.HEADER_VALUE);
		state.push(State.HEADER_KEY);
	}
	
	public static class Headers implements net.cheney.cocktail.message.Headers {

		private final Multimap<Header, String> headerMap = emptyMultiMap();
		
		private Multimap<Header, String> emptyMultiMap() {
			return ArrayListMultimap.create();
		}

		public class HeaderAccessor extends Header.Accessor {

			private final Header header;

			public HeaderAccessor(Header header) {
				this.header = header;
			}

			public void add(String value) {
				headerMap.put(header, value);
			}

			@Override
			protected Collection<String> get() {
				return headerMap.get(header);
			}

			@Override
			public Header header() {
				return header;
			}
			
		}

		@Override
		public Iterable<Header> keys() {
			return headerMap.keySet();
		}

		@Override
		public HeaderAccessor header(Header header) {
			return new HeaderAccessor(header);
		}
		
		@Override
		public String toString() {
			return headerMap.toString();
		}

		@Override
		public Iterator<Header.Accessor> iterator() {
			return Iterables.transform(keys(), new Function<Header, Header.Accessor>() {

				@Override
				public Header.Accessor apply(Header header) {
					return new HeaderAccessor(header);
				}

			}).iterator();
		}
		
	}

}
