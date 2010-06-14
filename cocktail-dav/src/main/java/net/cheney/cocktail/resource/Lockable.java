package net.cheney.cocktail.resource;

import javax.annotation.Nonnull;

import net.cheney.cocktail.resource.Lock.Scope;
import net.cheney.cocktail.resource.Lock.Type;

public interface Lockable {

	@Nonnull Lock lock(Type type, Scope scope);
	
	@Nonnull Lock unlock();

	boolean isLocked();
}
