package net.cheney.cocktail.application;

import static net.cheney.cocktail.application.Path.emptyPath;

import java.nio.ByteBuffer;

import javax.annotation.Nonnull;

import net.cheney.cocktail.message.Header;
import net.cheney.cocktail.message.Request;
import net.cheney.cocktail.message.Version;
import net.cheney.cocktail.message.Request.Method;

public abstract class Environment {

	public abstract @Nonnull Request.Method method();

	public abstract @Nonnull Version version();

	public abstract @Nonnull Header.Accessor header(Header header);
//
//	public abstract Multimap<Header<?>, String> headers();
//
	public abstract @Nonnull Path path();
	
	public abstract @Nonnull Path contextPath();
//	
//	public Parameters params() {
//		return params;
//	}
//	
//	public <K> K param(Parameter<K> key) {
//		return params.get(key);
//	}

	public static Environment fromRequest(final Request req, final ByteBuffer body) {
		return new Environment() {
			
			@Override
			public Version version() {
				return req.version();
			}
			
			@Override
			public Path path() {
				return Path.fromURI(req.uri());
			}
			
			@Override
			public Method method() {
				return req.method();
			}
			
			@Override
			public Path contextPath() {
				return emptyPath();
			}

			@Override
			public Header.Accessor header(Header header) {
				return req.header(header);
			}
			
			@Override
			public ByteBuffer body() {
				return body.asReadOnlyBuffer();
			}
			
			@Override
			public boolean hasBody() {
				return body != null;
			}
		};
	}

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
	
	public abstract @Nonnull ByteBuffer body();

	public abstract boolean hasBody();
}
