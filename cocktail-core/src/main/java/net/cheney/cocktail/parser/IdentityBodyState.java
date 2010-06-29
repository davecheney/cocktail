package net.cheney.cocktail.parser;

import java.nio.ByteBuffer;

import net.cheney.cocktail.message.Request.Builder;
import net.cheney.cocktail.parser.RequestParser.State;

public class IdentityBodyState extends BaseState {

	private final Builder builder;
	private final ByteBuffer body;

	public IdentityBodyState(Builder builder) {
		this.builder = builder;
		this.body = createBodyBuffer(builder.contentLength());
		this.builder.body(body);
	}

	@Override
	public State parse(ByteBuffer buffer) {
		if (body.hasRemaining()) {
			appendToBody(buffer);
			if (body.hasRemaining()) {
				return this;
			}
		}
		builder.body().flip(); // lame
		return new ResultState(builder);
	}

	private void appendToBody(ByteBuffer buffer) {
		if (buffer.remaining() > body.remaining()) {
			panic(buffer);
		} else {
			body.put(buffer);
		}
	}

}
