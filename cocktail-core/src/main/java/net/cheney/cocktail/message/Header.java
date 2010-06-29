package net.cheney.cocktail.message;

import java.util.Collection;
import java.util.Iterator;

import com.google.common.collect.Iterables;

public abstract class Header {
	
	public enum Type {
		GENERAL,
		REQUEST,
		RESPONSE,
		ENTITY;
	}
	
	public static final Header
	ACCEPT = builder("Accept", Type.REQUEST),
	ACCEPT_RANGES = builder("Accept-Ranges", Type.REQUEST),
	ACCEPT_CHARSET = builder("Accept-Charset", Type.REQUEST),
	ACCEPT_ENCODING = builder("Accept-Encoding", Type.REQUEST),
	ACCEPT_LANGUAGE = builder("Accept-Language", Type.REQUEST),
	AGE = builder("Age", Type.RESPONSE),
	ALLOW = builder("Allow", Type.ENTITY),
	AUTHORIZATION = builder("Authorization", Type.REQUEST),
	CACHE_CONTROL = builder("Cache-Control", Type.GENERAL),
	COOKIE = builder("Cookie", Type.REQUEST),
	COOKIE2 = builder("Cookie2", Type.REQUEST),
	
	CONNECTION = builder("Connection", Type.GENERAL),
	
	CONTENT_ENCODING = builder("Content-Encoding", Type.ENTITY), 
	CONTENT_LANGUAGE = builder("Content-Language", Type.ENTITY),
	CONTENT_LENGTH = builder("Content-Length", Type.ENTITY),
	
	CONTENT_LOCATION = builder("Content-Location", Type.ENTITY),
	
	CONTENT_MD5 = builder("Content-MD5", Type.ENTITY),
	CONTENT_RANGE = builder("Content-Range", Type.ENTITY),
	CONTENT_TYPE = builder("Content-Type", Type.ENTITY),
	
	DATE = builder("Date", Type.GENERAL),
	
	DAV = builder("Dav", Type.RESPONSE), 
	DEPTH = builder("Depth", Type.REQUEST), 
	DESTINATION = builder("Destination", Type.REQUEST),
	
	ETAG = builder("ETag", Type.RESPONSE),
	
	EXPECT = builder("Expect", Type.REQUEST),
	
	EXPIRES = builder("Expires", Type.ENTITY),
	
	FROM = builder("From", Type.REQUEST),
	
	HOST = builder("Host", Type.REQUEST),
	
	IF = builder("If", Type.REQUEST),
	IF_MATCH = builder("If-Match", Type.REQUEST), 
	
	IF_MODIFIED_SINCE = builder("If-Modified-Since", Type.REQUEST),
	
	IF_NONE_MATCH = builder("If-None-Match", Type.REQUEST), 
	IF_RANGE = builder("If-Range", Type.REQUEST),
	
	IF_UNMODIFIED_SINCE = builder("If-Unmodified-Since", Type.REQUEST),
	
	LAST_MODIFIED = builder("Last-Modified", Type.ENTITY),
	
	LOCATION = builder("Location", Type.RESPONSE),
	
	LOCK_TOKEN = builder("Lock-Token", Type.REQUEST),
	
	MAX_FORWARDS = builder("Max-Forwards", Type.REQUEST),
	
	OVERWRITE = builder("Overwrite", Type.REQUEST),
	PRAGMA = builder("Pragma", Type.GENERAL),
	
	PROXY_AUTHORIZATION = builder("Proxy-Authorization", Type.REQUEST),
	PROXY_AUTHENTICATE = builder("Proxy-Authenticate", Type.RESPONSE),
	RANGE = builder("Range", Type.REQUEST),
	REFERER = builder("Referer", Type.REQUEST),
	RETRY_AFTER = builder("Retry-After", Type.RESPONSE),
	
	SERVER = builder("Server", Type.RESPONSE),
	
	SET_COOKIE = builder("Set-Cookie", Type.RESPONSE),
	SET_COOKIE2 = builder("Set-Cookie2", Type.RESPONSE),
	TE = builder("TE", Type.REQUEST),
	TIMEOUT = builder("Timeout", Type.REQUEST),
	TRAILER = builder("Trailer", Type.GENERAL),	
	TRANSFER_ENCODING = builder("Transfer-Encoding", Type.GENERAL),
	UPGRADE = builder("Upgrade", Type.GENERAL),
	VARY = builder("Vary", Type.RESPONSE),
	VIA = builder("Via", Type.GENERAL),
	USER_AGENT = builder("User-Agent", Type.REQUEST),
	WARNING = builder("Warning", Type.GENERAL),
	WWW_AUTHENTICATE = builder("WWW-Authenticate", Type.RESPONSE),
	KEEP_ALIVE = builder("Keep-Alive", Type.GENERAL); 
	
	public abstract Type type();
	
	public abstract String name();
	
	@Override
	public final boolean equals(Object that) {
		return that instanceof Header ? ((Header)that).name().equalsIgnoreCase(this.name()) : false;
	}
	
	@Override
	public final int hashCode() {
		return name().toLowerCase().hashCode();
	}
	
	private static Header builder(final String name, final Header.Type type) {
		return new Builder(name, type);
	}
	
	public abstract static class Accessor implements Iterable<String> {

		protected abstract Collection<String> get();

		public abstract Header header();

		@Override
		public final Iterator<String> iterator() {
			return get().iterator();
		}

		public final String getOnlyElement() {
			return Iterables.getOnlyElement(this);
		}
		
		public final boolean any() {
			return get().size() > 0;
		}

		public final String getOnlyElementWithDefault(String defaultValue) {
			return Iterables.getOnlyElement(this, defaultValue);
		}

		@Override
		public final String toString() {
			return String.format("%s=%s", header().name(), get());
		}

		public final boolean contains(String string) {
			return Iterables.contains(this, string);
		}
		
	}
	
	public static final class Builder extends Header {

		private final String name;
		private final Type type;

		public Builder(String name, Type type) {
			this.name = name;
			this.type = type;
		}

		@Override
		public Type type() {
			return type;
		}

		@Override
		public String name() {
			return name;
		}
		
		@Override
		public String toString() {
			return name();
		}
	}
}
