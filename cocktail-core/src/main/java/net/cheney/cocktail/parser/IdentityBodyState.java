package net.cheney.cocktail.parser;

import java.nio.ByteBuffer;

import net.cheney.cocktail.message.Header;
import net.cheney.cocktail.message.Request.Builder;
import net.cheney.cocktail.parser.RequestParser.State;

public class IdentityBodyState extends BaseState {

	private final Builder builder;
	private final ByteBuffer body;

	public IdentityBodyState(Builder builder) {
		this.builder = builder;
		this.body = createBodyBuffer(contentLength());
		this.builder.body(body);
	}

	private ByteBuffer createBodyBuffer(int contentLength) {
		return ByteBuffer.allocate(contentLength);
	}

	private int contentLength() {
		return Integer.parseInt(builder.header(Header.CONTENT_LENGTH).getOnlyElementWithDefault("0"));
	}

	@Override
	public State parse(ByteBuffer buffer) {
		if(body.hasRemaining()) {
			appendToBody(buffer);
			if(body.hasRemaining()) {
				return this;
			}
		}
		return new ResultState(builder);
	}

	private void appendToBody(ByteBuffer buffer) {
		if(buffer.remaining() > body.remaining()) {
			panic(buffer);
		} else {
			buffer.put(body);
		}
	}

}
