package net.cheney.cocktail.message;

import java.net.URI;
import java.net.URISyntaxException;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

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
	
	@Immutable
	public static final class RequestLine extends Message.StartLine {

		private final Method method;
		private final URI uri;
		
		public RequestLine(@Nonnull Method method, @Nonnull String uri, @Nonnull Version version) throws URISyntaxException {
			this(method, uriFromString(uri), version);
		}

		public RequestLine(@Nonnull Method method, @Nonnull URI uri, @Nonnull Version version) {
			super(version);
			this.method = method;
			this.uri = uri;
		}
		
		private static URI uriFromString(String uri) throws URISyntaxException {
			return new URI(null, null, uri, null);
		}

		public Method method() {
			return this.method;
		}

		public URI uri() {
			return this.uri;
		}
		
		@Override
		public int hashCode() {
			return HashCodeBuilder.reflectionHashCode(this);
		}
		
		@Override
		public boolean equals(Object obj) {
			return EqualsBuilder.reflectionEquals(this, obj);
		}
		
		@Override
		public String toString() {
			return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
		}

	}

	public abstract Method method();

	public abstract URI uri();
	
}
