package net.cheney.cocktail.resource;

import java.io.IOException;

public interface Moveable {

	void moveTo(CollectionResource destination) throws IOException;
}
