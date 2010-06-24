package net.cheney.cocktail.parser;

import java.nio.ByteBuffer;

import net.cheney.cocktail.message.Request;
import net.cheney.cocktail.message.RequestLine.Builder;
import net.cheney.cocktail.parser.RequestParser.State;

public class RequestLineState implements State {

	private Builder builder;

	public RequestLineState(Builder builder) {
		this.builder = builder;
	}

	@Override
	public State parse(ByteBuffer buffer) {
		int offset = buffer.position();
		while(buffer.hasRemaining()) {
			switch(buffer.get()) {
			case '\n':
				offset = buffer.position();
				return new HeaderState(Request.builder(builder.build())).parse(buffer);
				
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
