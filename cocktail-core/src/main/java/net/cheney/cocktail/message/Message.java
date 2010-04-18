package net.cheney.cocktail.message;

import javax.annotation.Nonnull;

public abstract class Message {

	public enum Version {

		HTTP_0_9("HTTP/0.9"), HTTP_1_0("HTTP/1.0"), HTTP_1_1("HTTP/1.1");

		private final String value;

		private Version(@Nonnull String value) {
			this.value = value;
		}

		public static final Version parse(@Nonnull CharSequence version) {
			return valueOf(version.toString().replace('/', '_').replace('.', '_'));
		}

		@Override
		public final String toString() {
			return value;
		}

	}
	
	abstract static class StartLine {

		private final Version version;

		StartLine(@Nonnull Version version) {
			this.version = version;
		}
		
		public final Version version() {
			return this.version;
		}
		
		@Override
		public abstract int hashCode();
		
		@Override
		public abstract boolean equals(Object obj);

	}
}
