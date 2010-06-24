package net.cheney.cocktail.parser;

import java.nio.ByteBuffer;

import net.cheney.cocktail.message.Request;
import net.cheney.cocktail.message.Request.Builder;
import net.cheney.cocktail.parser.RequestParser.State;

public class HeaderState implements State {

	private final Builder builder;

	public HeaderState(net.cheney.cocktail.message.Request.Builder builder) {
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
				return new HeaderEndState(builder).parse(buffer);
				
			default:
				new HeaderNameState(builder).parse(buffer);
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
