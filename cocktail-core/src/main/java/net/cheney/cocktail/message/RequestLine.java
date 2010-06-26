package net.cheney.cocktail.message;

import java.net.URI;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import net.cheney.cocktail.message.Request.Method;

public abstract class RequestLine extends StartLine {

	public abstract Method method();

	public abstract URI uri();
	
	public static RequestLine.Builder builder() {
		return new RequestLine.Builder();
	}
	
	public static class Builder extends RequestLine {

		private Method method;
		private URI uri;
		private Version version;

		@Override
		public Method method() {
			return method;
		}
		
		public Builder method(Method method) {
			this.method = method;
			return this;
		}

		@Override
		public URI uri() {
			return uri;
		}
		
		public Builder uri(URI uri) {
			this.uri = uri;
			return this;
		}

		@Override
		public Version version() {
			return version;
		}
		
		public Builder version(Version version) {
			this.version = version;
			return this;
		}

		public RequestLine build() {
			return this;
		}

	}
	
	@Override
	public int hashCode() {
		return method().hashCode() * 37 + uri().hashCode() * 37 + version().hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if(obj instanceof RequestLine) {
			RequestLine that = (RequestLine) obj;
			 return (that.version().equals(this.version()) && that.method().equals(this.method()) && that.uri().equals(this.uri()));
		}
		return false;
	}
	
	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
}