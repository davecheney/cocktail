package net.cheney.cocktail.resource;

import java.util.UUID;

public final class Lock {

	public enum Scope {	NONE, SHARED, EXCLUSIVE }
	
	public enum Type { NONE, READ, WRITE }
	
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
