package net.cheney.cocktail.message;

import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Collection;
import java.util.Iterator;

import javax.annotation.Nonnull;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import com.google.common.base.Function;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Multimap;


public abstract class Request extends Message {

	public enum Method {
		// RFC 2516

		OPTIONS, GET, DELETE, HEAD, PUT, POST, TRACE,

		// RFC 2518

		// DAV Level 1

		COPY, MOVE, MKCOL, PROPFIND, PROPPATCH,

		// DAV Level 2

		LOCK, UNLOCK,

		// RFC 3744

		ACL,

		// DeltaV RFC 3253

		REPORT, MKACTIVITY, MERGE, CHECKIN, UNCHECKOUT, UPDATE, LABEL, MKWORKSPACE, VERSION_CONTROL, CHECKOUT, SEARCH,

		// CALDAV RFC 4791

		MKCALENDAR;

		public static Method parse(@Nonnull CharSequence method) {
			return valueOf(method.toString());
		}
	}
	
	public abstract Method method();

	public abstract URI uri();
	
//	public boolean mayHaveBody() {
//		try {
//			return contentLength() > 0;
//		} catch (IOException e) {
//			return false;
//		}
//	}
	
	public static Request.Builder builder(RequestLine requestLine) {
		return new Request.Builder(requestLine);
	}
	
	public static class Builder extends Request {

		private final RequestLine requestLine;
		private final Multimap<Header, String> headers = ArrayListMultimap.create();
		private ByteBuffer body = null;

		private Builder(RequestLine requestLine) {
			this.requestLine = requestLine;
		}

		@Override
		public Method method() {
			return requestLine.method();
		}

		@Override
		public URI uri() {
			return requestLine.uri();
		}

		@Override
		public HeaderAccessor header(Header header) {
			return new HeaderAccessor(header);
		}

		@Override
		public Iterable<Header> keys() {
			return headers.keySet();
		}

		@Override
		public Iterator<Header.Accessor> iterator() {
			return Iterables.transform(keys(), new Function<Header, Header.Accessor>() {
				public Header.Accessor apply(Header header) {
					return new HeaderAccessor(header);
				};
			}).iterator();
		}

		@Override
		public Version version() {
			return requestLine.version();
		}

		@Override
		public ByteBuffer body() {
			return this.body;
		}
		
		public Builder body(ByteBuffer body) {
			this.body = body;
			return this;
		}
		
		public long contentLength() {
			return hasBody() ? body().remaining() : 0 ;
		}
		
		@Override
		public String toString() {
			return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
		}
		
		@Override
		public boolean hasBody() {
			return body() != null;
		}
		
		public class HeaderAccessor extends Header.Accessor {
			
			private final Header key;

			public HeaderAccessor(Header header) {
				this.key = header;
			}

			@Override
			protected Collection<String> get() {
				return headers.get(key);
			}

			@Override
			public Header header() {
				return key;
			}

			public HeaderAccessor add(String value) {
				get().add(value);
				return this;
			}

			public void set(Iterable<String> values) {
				Iterables.addAll(get(), values);
			}

			public void delete() {
				headers.removeAll(key);
			}
			
			public int intValue() {
				return Integer.parseInt(getOnlyElement());
			}
		}

		public Request build() {
			return this;
		}

		@Override
		public FileChannel channel() {
			return null;
		}
		
	}
}
