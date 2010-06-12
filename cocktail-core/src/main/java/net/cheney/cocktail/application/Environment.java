package net.cheney.cocktail.application;

import static net.cheney.cocktail.application.Path.emptyPath;

import java.nio.ByteBuffer;

import javax.annotation.Nonnull;

import net.cheney.cocktail.message.Header;
import net.cheney.cocktail.message.Request;
import net.cheney.cocktail.message.Version;
import net.cheney.cocktail.message.Request.Method;

public abstract class Environment {

	public abstract Request.Method method();

	public abstract Version version();

	public abstract Header.Accessor header(Header header);
//
//	public abstract Multimap<Header<?>, String> headers();
//
	public abstract Path path();
	
	public abstract Path contextPath();
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
	
	public Depth depth() {
		return Depth.parse(header(Header.DEPTH).getOnlyElementWithDefault(""), Depth.INFINITY);
	}

	public abstract ByteBuffer body();
}
