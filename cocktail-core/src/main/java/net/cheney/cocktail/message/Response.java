package net.cheney.cocktail.message;

import static net.cheney.cocktail.message.Response.Status.REDIRECTION_NOT_MODIFIED;
import static net.cheney.cocktail.message.Response.Status.SUCCESS_NO_CONTENT;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

import javax.annotation.Nonnull;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import com.google.common.base.Function;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Multimap;

public abstract class Response extends Message  {
	
	public interface StatusCode {

		int code();

		boolean isServerError();

		boolean isClientError();

		boolean isError();

		boolean isRedirect();

		boolean isSuccess();

		boolean isInformational();

		String reason();
		
	}
	
	public enum Status implements StatusCode {
		INFO_CONTINUE(100, "Continue"), INFO_SWITCHING_PROTOCOL(101,
				"Switching Protocols"), INFO_PROCESSING(102, "Processing"),

		SUCCESS_OK(200, "OK"), SUCCESS_CREATED(201, "Created"), SUCCESS_ACCEPTED(
				202, "Accepted"), SUCCESS_NON_AUTHORITATIVE(203,
				"Non-Authoritative Information"), SUCCESS_NO_CONTENT(204,
				"No Content"), SUCCESS_RESET_CONTENT(205, "Reset Content"), SUCCESS_PARTIAL_CONTENT(
				206, "Partial Content"), SUCCESS_MULTI_STATUS(207, "Multi-Status"),

		REDIRECTION_MULTIPLE_CHOICES(300, "Multiple Choices"), REDIRECTION_MOVED_PERMANENTLY(
				301, "Moved Permanently"), REDIRECTION_MOVED_TEMPORARILY(302,
				"Found"), REDIRECTION_SEE_OTHER(303, "See Other"), REDIRECTION_NOT_MODIFIED(
				304, "Not Modified"), REDIRECTION_USE_PROXY(305, "Use Proxy"), REDIRECTION_TEMPORARY_REDIRECT(
				307, "Temporary Redirect"),

		CLIENT_ERROR_BAD_REQUEST(400, "Bad Request"), CLIENT_ERROR_UNAUTHORIZED(
				401, "Unauthorized"), CLIENT_ERROR_PAYMENT_REQUIRED(402,
				"Payment Required"), CLIENT_ERROR_FORBIDDEN(403, "Forbidden"), CLIENT_ERROR_NOT_FOUND(
				404, "Not found"), CLIENT_ERROR_METHOD_NOT_ALLOWED(405,
				"Method Not Allowed"), CLIENT_ERROR_NOT_ACCEPTABLE(406,
				"Not Acceptable"), CLIENT_ERROR_PROXY_AUTHENTIFICATION_REQUIRED(
				407, "Proxy Authentication Required"), CLIENT_ERROR_REQUEST_TIMEOUT(
				408, "Request Time-out"), CLIENT_ERROR_CONFLICT(409, "Conflict"), CLIENT_ERROR_GONE(
				410, "Gone"), CLIENT_ERROR_LENGTH_REQUIRED(411, "Length Required"), CLIENT_ERROR_PRECONDITION_FAILED(
				412, "Precondition Failed"), CLIENT_ERROR_REQUEST_ENTITY_TOO_LARGE(
				413, "Request Entity Too Large"), CLIENT_ERROR_REQUEST_URI_TOO_LONG(
				414, "Request-URI Too Large"), CLIENT_ERROR_UNSUPPORTED_MEDIA_TYPE(
				415, "Unsupported Media Type"), CLIENT_ERROR_REQUESTED_RANGE_NOT_SATISFIABLE(
				416, "Requested range not satisfiable"), CLIENT_ERROR_EXPECTATION_FAILED(
				417, "Expectation Failed"),
//		 CLIENT_ERROR_UNPROCESSABLE_ENTITY(422),
		CLIENT_ERROR_LOCKED(423, "Locked"),
		// CLIENT_ERROR_FAILED_DEPENDENCY(424),

		SERVER_ERROR_INTERNAL(500, "Internal Server Error"), SERVER_ERROR_NOT_IMPLEMENTED(
				501, "Not Implemented"), SERVER_ERROR_BAD_GATEWAY(502,
				"Bad Gateway"), SERVER_ERROR_SERVICE_UNAVAILABLE(503,
				"Service Unavailable"), SERVER_ERROR_GATEWAY_TIMEOUT(504,
				"Gateway Time-out"), SERVER_ERROR_HTTP_VERSION_NOT_SUPPORTED(505,
				"HTTP Version not supported");
		// SERVER_ERROR_INSUFFICIENT_STORAGE(507);

		private final int code;
		private final String reason;

		private Status(@Nonnull int code, @Nonnull String reason) {
			this.code = code;
			this.reason = reason;
		}

		@Override
		public int code() {
			return code;
		}

		@Override
		public String reason() {
			return reason;
		}

		@Override
		public boolean isInformational() {
			return (compareTo(INFO_CONTINUE) >= 0 && compareTo(SUCCESS_OK) < 0);
		}

		@Override
		public boolean isSuccess() {
			return (compareTo(SUCCESS_OK) >= 0 && compareTo(REDIRECTION_MULTIPLE_CHOICES) < 0);
		}

		@Override
		public boolean isRedirect() {
			return (compareTo(REDIRECTION_MULTIPLE_CHOICES) >= 0 && compareTo(CLIENT_ERROR_BAD_REQUEST) < 0);
		}

		@Override
		public boolean isError() {
			return (compareTo(CLIENT_ERROR_BAD_REQUEST) >= 0);
		}

		@Override
		public boolean isClientError() {
			return (compareTo(CLIENT_ERROR_BAD_REQUEST) >= 0 && compareTo(SERVER_ERROR_INTERNAL) < 0);
		}

		@Override
		public boolean isServerError() {
			return (compareTo(SERVER_ERROR_INTERNAL) >= 0);
		}

	}

	public abstract StatusCode status();
	
	public boolean hasBody() {
		return body() != null;
	}

	public abstract Version version();

	public boolean mayContainBody() {
		// http://tools.ietf.org/html/draft-ietf-httpbis-p1-messaging-08#section-3.4
		return (!status().isInformational() && !status().equals(SUCCESS_NO_CONTENT) && !status().equals(REDIRECTION_NOT_MODIFIED));
	}

	public static Response.Builder builder(StatusCode status) {
		return builder(StatusLine.builder().status(status).version(Version.HTTP_1_1));
	}
	
	public static Response.Builder builder(StatusLine statusLine) {
		return new Response.Builder(statusLine);
	}
	
	public static class Builder extends Response {

		private final StatusLine statusLine;
		private final Multimap<Header, String> headers = ArrayListMultimap.create();
		private ByteBuffer body = null;
		private File file = null;

		public Builder(StatusLine statusLine) {
			this.statusLine = statusLine;
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
		public StatusCode status() {
			return statusLine.status();
		}

		@Override
		public Version version() {
			return statusLine.version();
		}
		
		@Override
		public ByteBuffer body() {
			return this.body;
		}
		
		public Builder body(ByteBuffer body) {
			this.file = null;
			this.body = body;
			return this;
		}
		
		public Builder body(File file) {
			this.body = null;
			this.file = file;
			return this;
		}
		
		@Override
		public long contentLength() {
			return body == null ? file == null ? 0 : file.length() : body.remaining();
		}

		@Override
		public FileChannel channel() throws FileNotFoundException {
			return new FileInputStream(file).getChannel();
		}
		
		@Override
		public String toString() {
			return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
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
				headers.get(key).add(value);
				return this;
			}

			public void set(Iterable<String> values) {
				headers.replaceValues(key, values);
			}
			
			public void set(String value) {
				set(Arrays.asList(value));
			}
		}

		public Response build() {
			return this;
		}

	}


}
