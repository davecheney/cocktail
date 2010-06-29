package net.cheney.cocktail.parser;

import java.nio.ByteBuffer;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import net.cheney.cocktail.message.Header;
import net.cheney.cocktail.message.Request.Builder;
import net.cheney.cocktail.parser.RequestParser.State;

public class ChunkedBodyState extends BaseState {

	private final Builder builder;
	private ChunkState state = new ChunkSizeState(new ChunkBuilder());

	public ChunkedBodyState(Builder builder) {
		this.builder = builder;
	}

	@Override
	public State parse(ByteBuffer buffer) {
		this.state = state.parse(buffer);
		ByteBuffer body = state.result();
		if(body == null) {
			return this;
		} else {
			builder.header(Header.TRANSFER_ENCODING).delete();
			return new TrailerNameState(builder.body(body)).parse(buffer);
		}
	}

	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
}
