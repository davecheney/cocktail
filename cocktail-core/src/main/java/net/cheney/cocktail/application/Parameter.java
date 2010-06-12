package net.cheney.cocktail.application;

public abstract class Parameter<K> { 
	
	public abstract K decode(String string);
	
	public abstract String encode(K value);
}