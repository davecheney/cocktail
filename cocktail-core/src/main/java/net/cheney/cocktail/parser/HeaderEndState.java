package net.cheney.cocktail.parser;

import java.nio.ByteBuffer;

import net.cheney.cocktail.message.Request;
import net.cheney.cocktail.message.Request.Builder;
import net.cheney.cocktail.parser.RequestParser.State;

public class HeaderEndState implements State {

	private final Builder builder;

	public HeaderEndState(Builder builder) {
		this.builder = builder;
	}

	@Override
	public State parse(ByteBuffer buffer) {
		int offset = buffer.position();
		while(buffer.hasRemaining()) {
			switch(buffer.get()) {
			case '\n':
				offset = buffer.position();
				return new IntermediateRequestState(builder);
				
			default:
				throw new IllegalArgumentException();
			}
		}
		buffer.position(offset);
		return this;
	}

	@Override
	public Request result() {
		// TODO Auto-generated method stub
		return null;
	}

}
