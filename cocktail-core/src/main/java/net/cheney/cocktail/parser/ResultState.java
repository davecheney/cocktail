package net.cheney.cocktail.parser;

import java.nio.ByteBuffer;

import net.cheney.cocktail.message.Request;
import net.cheney.cocktail.message.Request.Builder;
import net.cheney.cocktail.parser.RequestParser.State;

public class ResultState extends BaseState {

	private final Builder builder;

	public ResultState(Builder builder) {
		this.builder = builder;
	}

	@Override
	public State parse(ByteBuffer buffer) {
		return panic(buffer);
	}

	@Override
	public Request result() {
		return builder.build();
	}

}
