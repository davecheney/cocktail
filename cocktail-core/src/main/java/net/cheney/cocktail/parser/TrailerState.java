package net.cheney.cocktail.parser;

import java.nio.ByteBuffer;

import net.cheney.cocktail.message.Request.Builder;
import net.cheney.cocktail.parser.RequestParser.State;

public class TrailerState extends BaseState {

	private final Builder builder;

	public TrailerState(Builder builder) {
		this.builder = builder;
	}

	@Override
	public State parse(ByteBuffer buffer) {
		int offset = buffer.position();
		while(buffer.hasRemaining()) {
			// peek
			switch(buffer.get(buffer.position())) {
			case '\r':
				// get in lieu of peek
				buffer.get();
				return new BodyEndState(builder).parse(buffer);
				
			default:
				new TrailerNameState(builder).parse(buffer);
			}
		}
		buffer.position(offset);
		return this;
	}

}
