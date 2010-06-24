package net.cheney.cocktail.message;

import net.cheney.cocktail.message.Response.StatusCode;

public abstract class StatusLine extends StartLine {

	public abstract StatusCode status();

	public static StatusLine.Builder builder() {
		return new StatusLine.Builder();
	}
	
	@Override
	public int hashCode() {
		return status().hashCode() * 37 + version().hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if(obj instanceof StatusLine) {
			 StatusLine that = (StatusLine) obj;
			 return (that.version().equals(this.version()) && that.status().equals(this.status()));
		}
		return false;
	}
	
	public static class Builder extends StatusLine {

		private StatusCode statusCode;
		private Version version;

		@Override
		public StatusCode status() {
			return statusCode;
		}
		
		public Builder status(StatusCode statusCode) {
			this.statusCode = statusCode;
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

	}
}