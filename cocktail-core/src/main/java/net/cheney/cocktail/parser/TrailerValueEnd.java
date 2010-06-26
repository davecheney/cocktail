package net.cheney.cocktail.parser;

import java.nio.ByteBuffer;

import net.cheney.cocktail.message.Request.Builder;
import net.cheney.cocktail.parser.RequestParser.State;

public class TrailerValueEnd extends BaseState {

	private final Builder builder;

	public TrailerValueEnd(Builder builder) {
		this.builder = builder;
	}

	@Override
	public State parse(ByteBuffer buffer) {
		int offset = buffer.position();
		while(buffer.hasRemaining()) {
			switch(buffer.get()) {
			case '\n':
				offset = buffer.position();
				return new TrailerNameState(builder);
				
			default:
				panic(buffer);
			}
		}
		buffer.position(offset);
		return this;
	}

}
