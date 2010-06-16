package net.cheney.cocktail.message;


public interface Headers extends Iterable<Header.Accessor> {

	public abstract Header.Accessor header(Header header);

	public abstract Iterable<Header> keys();
}
