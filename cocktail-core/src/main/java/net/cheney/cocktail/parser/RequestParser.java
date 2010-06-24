package net.cheney.cocktail.parser;

import java.nio.ByteBuffer;

import javax.annotation.Nonnull;

import net.cheney.cocktail.message.Request;

public class RequestParser {
	
	public interface State {

		@Nonnull State parse(@Nonnull ByteBuffer buffer);
		
		Request result();
	}

	private State state = new MethodState();
	
	public Request parse(ByteBuffer buffer) {
		this.state = state.parse(buffer);
		return this.state.result();
	}
	
}
