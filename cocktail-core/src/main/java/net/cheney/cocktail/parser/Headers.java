package net.cheney.cocktail.parser;

import java.util.Iterator;

import net.cheney.cocktail.message.Header;

public abstract class Headers implements Iterable<Header.Accessor> {

	public abstract Header.Accessor header(Header header);

	public abstract Iterable<Header.Accessor> headers();
	
	@Override
	public Iterator<Header.Accessor> iterator() {
		return headers().iterator();
	}
	
	public abstract Iterable<Header> keys();
}
