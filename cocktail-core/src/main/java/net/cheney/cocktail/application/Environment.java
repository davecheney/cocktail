package net.cheney.cocktail.application;

import java.nio.ByteBuffer;

import javax.annotation.Nonnull;

import net.cheney.cocktail.message.Header;
import net.cheney.cocktail.message.Request;
import net.cheney.cocktail.message.Version;

public interface Environment {

	public enum Depth { 
		
		ZERO {
			public String toString() {
				return "0";
			}
		},
		
		ONE {
			public String toString() {
				return "1";
			}
		},
		
		INFINITY {
			public String toString() {
				return "infinity";
			}
		};
		
		public Depth decreaseDepth() {
			return (this == INFINITY ? INFINITY : ZERO);
		}
		
		public static final Depth parse(@Nonnull String depth, @Nonnull Depth defaultDepth) {
			try {
				if("infinity".equalsIgnoreCase(depth)) {
					return defaultDepth;
				} else {
					return (Integer.parseInt(depth) == 0 ? Depth.ZERO : Depth.ONE);
				}
			} catch (NumberFormatException e) {
				return defaultDepth;
			}
		}
		
	}
	@Nonnull Request.Method method();

	@Nonnull Version version();

	@Nonnull Header.Accessor header(Header header);

	@Nonnull Path path();
	
	@Nonnull Path contextPath();
//	
//	public Parameters params() {
//		return params;
//	}
//	
//	public <K> K param(Parameter<K> key) {
//		return params.get(key);
//	}
	
	@Nonnull ByteBuffer body();

	boolean hasBody();
}
