package net.cheney.cocktail.resource;

import net.cheney.cocktail.resource.Lock.Scope;
import net.cheney.cocktail.resource.Lock.Type;

public interface LockManager {

	Lock lock(Resource resource, Type type, Scope scope);
	
	Lock unlock(Resource resource);

	boolean isLocked(Resource resource);
}