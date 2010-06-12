package net.cheney.cocktail.parser;

import java.nio.ByteBuffer;

public interface BodyHandler {
	
	void bodyReceived(final ByteBuffer buffer);
}