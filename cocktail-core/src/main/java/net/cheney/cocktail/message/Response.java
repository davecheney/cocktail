package net.cheney.cocktail.message;

import static net.cheney.cocktail.message.Response.Status.REDIRECTION_NOT_MODIFIED;
import static net.cheney.cocktail.message.Response.Status.SUCCESS_NO_CONTENT;
import static org.apache.commons.lang.builder.EqualsBuilder.reflectionEquals;
import static org.apache.commons.lang.builder.HashCodeBuilder.reflectionHashCode;
import static org.apache.commons.lang.builder.ToStringStyle.MULTI_LINE_STYLE;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.util.Collection;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

import net.cheney.cocktail.message.Header.Accessor;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

public abstract class Response extends Message {

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

	public StatusCode status() {
		return statusLine().status();
	}
	
	@Immutable
	public
	static class StatusLine extends Message.StartLine {

		private final StatusCode status;

		public StatusLine(@Nonnull Version version, @Nonnull StatusCode status) {
			super(version);
			this.status = status;
		}
		
		public StatusCode status() {
			return this.status;
		}

		@Override
		public String toString() {
			return ReflectionToStringBuilder.toString(this, MULTI_LINE_STYLE);
		}
		
		@Override
		public boolean equals(Object that) {
			return reflectionEquals(this, that);
		}
		
		@Override
		public int hashCode() {
			return reflectionHashCode(this);
		}
	}

	public static Response successNoContent() {
		return Response.builder(SUCCESS_NO_CONTENT).build();
	}
	
//	public interface ResponseBuilder {
//		Response build();
//	}
//	
//	public interface EntityResponseBuilder extends ResponseBuilder {
//		MimeTypedResponseBuilder entity(String s);
//	}
//	
//	public interface MimeTypedResponseBuilder extends ResponseBuilder {
//		ResponseBuilder type(Mime mime);
//	}
//	
//	public static EntityResponseBuilder status(Status status) {
//		class Builder implements EntityResponseBuilder, MimeTypedResponseBuilder {
//
//			private Status status;
//			private String entity;
//			private Mime mime;
//
//			public Builder(Status status) {
//				this.status = status;
//			}
//
//			@Override
//			public Response build() {
//				return new Response() {
//
//					@Override
//					public Status status() {
//						return status;
//					}
//
//					@Override
//					public HeaderAccessor header(Header header) {
//						// TODO Auto-generated method stub
//						return null;
//					}
//
//					@Override
//					public long contentLength() throws IOException {
//						return entity.length();
//					}
//
//					@Override
//					public boolean hasBody() {
//						// TODO Auto-generated method stub
//						return false;
//					}
//					
//				};
//			}
//
//			@Override
//			public MimeTypedResponseBuilder entity(String s) {
//				this.entity = s;
//				return this;
//			}
//
//			@Override
//			public ResponseBuilder type(Mime mime) {
//				this.mime = mime;
//				return this;
//			}
//			
//		}
//		
//		return new Builder(status);
//	}

	public abstract boolean hasBody();

	public abstract ByteBuffer buffer();

	public abstract FileChannel channel();

	public Version version() {
		return statusLine().version();
	}

	protected abstract StatusLine statusLine();

	public boolean mayContainBody() {
		// http://tools.ietf.org/html/draft-ietf-httpbis-p1-messaging-08#section-3.4
		return (!status().isInformational() && !status().equals(SUCCESS_NO_CONTENT) && !status().equals(REDIRECTION_NOT_MODIFIED));
	}

	public static Response.Builder builder(StatusCode status) {
		return new Response.Builder(status);
	}


	public static class Builder {

		private final StatusCode status;
		private ByteBuffer buffer;
		private Multimap<Header, String> headers = ArrayListMultimap.create();

		private Builder(StatusCode status) {
			this.status = status;
		}
		
		public Builder body(File file) throws IOException {
			FileInputStream fis = null;
			try {
				fis = new FileInputStream(file);
				this.buffer = fis.getChannel().map(MapMode.READ_ONLY, 0, file.length());
			} finally {
				closeQuietly(fis);
				return this;
			}
			
		}

		private void closeQuietly(FileInputStream fis) {
			try {
				fis.close();
			} catch (IOException ignored) {
			}
		}

		public Response build() {
			return new Response() {

				@Override
				public boolean hasBody() {
					return buffer != null;
				}

				@Override
				public ByteBuffer buffer() {
					return buffer.asReadOnlyBuffer();
				}

				@Override
				public FileChannel channel() {
					return null;
				}

				@Override
				protected StatusLine statusLine() {
					return new StatusLine(Version.HTTP_1_1, status);
				}

				@Override
				public long contentLength() throws IOException {
					return hasBody() ? buffer.remaining() : 0;
				}

				@Override
				public Iterable<Header> headers() {
					return headers.keySet();
				}

				@Override
				public Accessor header(Header header) {
					return new HeaderAccessor(header);
				}
				
				@Override
				public String toString() {
					return ReflectionToStringBuilder.toString(this, ToStringStyle.SIMPLE_STYLE);
				}
				
			};
		}

		public HeaderAccessor header(final Header header) {
			return new HeaderAccessor(header);
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
		}

		public Builder body(ByteBuffer buffer) {
			this.buffer = buffer;
			return this;
		}
	}
}
