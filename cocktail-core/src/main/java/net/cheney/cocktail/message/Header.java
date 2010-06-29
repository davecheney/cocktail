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
	ACCEPT = $("Accept", Type.REQUEST),
	ACCEPT_RANGES = $("Accept-Ranges", Type.REQUEST),
	ACCEPT_CHARSET = $("Accept-Charset", Type.REQUEST),
	ACCEPT_ENCODING = $("Accept-Encoding", Type.REQUEST),
	ACCEPT_LANGUAGE = $("Accept-Language", Type.REQUEST),
	AGE = $("Age", Type.RESPONSE),
	ALLOW = $("Allow", Type.ENTITY),
	AUTHORIZATION = $("Authorization", Type.REQUEST),
	CACHE_CONTROL = $("Cache-Control", Type.GENERAL),
	COOKIE = $("Cookie", Type.REQUEST),
	COOKIE2 = $("Cookie2", Type.REQUEST),
	
	CONNECTION = $("Connection", Type.GENERAL),
	
	CONTENT_ENCODING = $("Content-Encoding", Type.ENTITY), 
	CONTENT_LANGUAGE = $("Content-Language", Type.ENTITY),
	CONTENT_LENGTH = $("Content-Length", Type.ENTITY),
	
	CONTENT_LOCATION = $("Content-Location", Type.ENTITY),
	
	CONTENT_MD5 = $("Content-MD5", Type.ENTITY),
	CONTENT_RANGE = $("Content-Range", Type.ENTITY),
	CONTENT_TYPE = $("Content-Type", Type.ENTITY),
	
	DATE = $("Date", Type.GENERAL),
	
	DAV = $("Dav", Type.RESPONSE), 
	DEPTH = $("Depth", Type.REQUEST), 
	DESTINATION = $("Destination", Type.REQUEST),
	
	ETAG = $("ETag", Type.RESPONSE),
	
	EXPECT = $("Expect", Type.REQUEST),
	
	EXPIRES = $("Expires", Type.ENTITY),
	
	FROM = $("From", Type.REQUEST),
	
	HOST = $("Host", Type.REQUEST),
	
	IF = $("If", Type.REQUEST),
	IF_MATCH = $("If-Match", Type.REQUEST), 
	
	IF_MODIFIED_SINCE = $("If-Modified-Since", Type.REQUEST),
	
	IF_NONE_MATCH = $("If-None-Match", Type.REQUEST), 
	IF_RANGE = $("If-Range", Type.REQUEST),
	
	IF_UNMODIFIED_SINCE = $("If-Unmodified-Since", Type.REQUEST),
	
	LAST_MODIFIED = $("Last-Modified", Type.ENTITY),
	
	LOCATION = $("Location", Type.RESPONSE),
	
	LOCK_TOKEN = $("Lock-Token", Type.REQUEST),
	
	MAX_FORWARDS = $("Max-Forwards", Type.REQUEST),
	
	OVERWRITE = $("Overwrite", Type.REQUEST),
	PRAGMA = $("Pragma", Type.GENERAL),
	
	PROXY_AUTHORIZATION = $("Proxy-Authorization", Type.REQUEST),
	PROXY_AUTHENTICATE = $("Proxy-Authenticate", Type.RESPONSE),
	RANGE = $("Range", Type.REQUEST),
	REFERER = $("Referer", Type.REQUEST),
	RETRY_AFTER = $("Retry-After", Type.RESPONSE),
	
	SERVER = $("Server", Type.RESPONSE),
	
	SET_COOKIE = $("Set-Cookie", Type.RESPONSE),
	SET_COOKIE2 = $("Set-Cookie2", Type.RESPONSE),
	TE = $("TE", Type.REQUEST),
	TIMEOUT = $("Timeout", Type.REQUEST),
	TRAILER = $("Trailer", Type.GENERAL),	
	TRANSFER_ENCODING = $("Transfer-Encoding", Type.GENERAL),
	UPGRADE = $("Upgrade", Type.GENERAL),
	VARY = $("Vary", Type.RESPONSE),
	VIA = $("Via", Type.GENERAL),
	USER_AGENT = $("User-Agent", Type.REQUEST),
	WARNING = $("Warning", Type.GENERAL),
	WWW_AUTHENTICATE = $("WWW-Authenticate", Type.RESPONSE),
	KEEP_ALIVE = $("Keep-Alive", Type.GENERAL); 
	
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
	
	private static Header $(final String name, final Header.Type type) {
		return new Header() {

			@Override
			public Header.Type type() {
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
		};
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
}
