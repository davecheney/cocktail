package net.cheney.cocktail.parser;

import java.nio.ByteBuffer;

import net.cheney.cocktail.message.Request;
import net.cheney.cocktail.message.Request.Builder;
import net.cheney.cocktail.parser.RequestParser.State;

public class IntermediateRequestState implements State {

	private final Builder builder;

	public IntermediateRequestState(Builder builder) {
		this.builder = builder;
	}

	@Override
	public State parse(ByteBuffer buffer) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Request result() {
		return builder.build();
	}

}
