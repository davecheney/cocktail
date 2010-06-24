package net.cheney.cocktail.message;

abstract class StartLine {

	public abstract Version version();
	
	@Override
	public abstract int hashCode();
	
	@Override
	public abstract boolean equals(Object obj);

}