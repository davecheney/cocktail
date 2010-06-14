package net.cheney.cocktail.resource;

import java.util.UUID;

import net.cheney.snax.model.Element;
import net.cheney.snax.model.QName;

public final class Lock {

	public enum Scope {	
		NONE, 
		SHARED {
			@Override
			public Element toXML() {
				return new Element(QName.valueOf(Elements.DAV_NAMESPACE, "shared"));
			} 
		},
		EXCLUSIVE {
			@Override
			public Element toXML() {
				return new Element(QName.valueOf(Elements.DAV_NAMESPACE, "exclusive"));
			} 
		};
		
		public Element toXML() {
			return null;
		}	
	}
	
	public enum Type { 
		NONE, 
		READ {
			@Override
			public Element toXML() {
				return new Element(QName.valueOf(Elements.DAV_NAMESPACE, "read"));
			} 
		},
		WRITE {
			@Override
			public Element toXML() {
				return new Element(QName.valueOf(Elements.DAV_NAMESPACE, "write"));
			} 
		};

		public Element toXML() {
			return null;
		}	 
	
	}
	
	private final Type type;
	private final Scope scope;
	private final String token;
	private final Resource resource;
	
	public Lock(Type type, Scope scope, Resource resource) {
		this.type = type;
		this.scope = scope;
		this.token = generateToken();
		this.resource = resource;
	}

	private final String generateToken() {
		return "opaquelocktoken:"+UUID.randomUUID().toString();
	}
	
	public final Resource resource() {
		return resource;
	}
	
	public final Lock.Type type() {
		return type;
	}
	
	public final Lock.Scope scope() {
		return scope;
	}
	
	public final String token() {
		return token;
	}

}
