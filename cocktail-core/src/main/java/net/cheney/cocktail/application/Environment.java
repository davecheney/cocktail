package net.cheney.cocktail.application;

import net.cheney.cocktail.message.Header;
import net.cheney.cocktail.message.Message.Version;
import net.cheney.cocktail.message.Request;

public abstract class Environment {

	public abstract Request.Method method();

	public abstract Version version();

	public abstract HeaderAccessor<Request> header(Header<?> header);

	public abstract Multimap<Header<?>, String> headers();

	public abstract Path pathInfo();
	
	public Parameters params() {
		return params;
	}
	
	public <K> K param(Parameter<K> key) {
		return params.get(key);
	}
}
