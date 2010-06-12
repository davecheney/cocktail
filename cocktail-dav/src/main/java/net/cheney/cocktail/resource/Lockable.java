package net.cheney.cocktail.resource;

public interface Lockable {

	boolean isLocked();
	
	void lock();
	
	void unlock();
}
