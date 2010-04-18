package net.cheney.cocktail.message;

public abstract class Header<V> {

	public enum Type {
		GENERAL,
		REQUEST,
		RESPONSE,
		ENTITY
	}
	
	public abstract Header.Type type();
	
	public abstract String name();
	
	@Override
	public boolean equals(Object that) {
		return (that instanceof Header<?>) ?
				(((Header<?>)that).name().equals(this.name()) && ((Header<?>)that).type().equals(this.type())) : false; 
	}
	
	@Override
	public int hashCode() {
		return this.name().hashCode();
	}
}
