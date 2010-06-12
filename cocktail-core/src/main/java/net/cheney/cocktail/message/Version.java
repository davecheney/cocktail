package net.cheney.cocktail.message;

import static java.lang.Integer.parseInt;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class Version implements Comparable<Version> {
	
	private static final Pattern HTTP_VERSION = Pattern.compile("HTTP/(\\d)\\.(\\d)");
	
	public static final Version HTTP_0_9 = new Version() {

		public int major() { return 0; }

		public int minor() { return 9; }
		
	};
	
	public static final Version HTTP_1_0 = new Version() {
		
		public int major() { return 1; };
		
		public int minor() { return 0; };
	};
	
	public static final Version HTTP_1_1 = new Version() {
		
		public int major() { return 1; };
		
		public int minor() { return 1; };
	};
	
	public abstract int major();
	
	public abstract int minor();
	
	@Override
	public final int compareTo(Version v) {
		// TODO - crap
		return v.major() > this.major() ? 1 : v.minor() > this.minor() ? 1 : -1;
	}

	public final String toString() {
		return String.format("HTTP/%d.%d", major(), minor());
	}
	
	@Override
	public int hashCode() {
		return toString().hashCode();
	}
	
	@Override
	public boolean equals(Object o) {
		if(o instanceof Version) {
			Version that = (Version) o;
			return (that.major() == this.major() && that.minor() == this.minor());
		}
		return false;
	}

	public static Version parse(String v) {
		Matcher matcher = HTTP_VERSION.matcher(v);
		if(!matcher.matches()) throw new IllegalArgumentException(String.format("%s does not match %s", HTTP_VERSION, v));
		final int major = parseInt(matcher.group(1)), minor = parseInt(matcher.group(2));
		return new Version() {

			@Override
			public int major() {
				return major;
			}

			@Override
			public int minor() {
				return minor;
			}
			
		};
	}
	
}