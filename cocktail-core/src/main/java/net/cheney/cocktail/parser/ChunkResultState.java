package net.cheney.cocktail.parser;

import java.nio.ByteBuffer;

public class ChunkResultState extends ChunkState {

	private final ChunkBuilder builder;

	public ChunkResultState(ChunkBuilder builder) {
		this.builder = builder;
	}

	@Override
	public ChunkState parse(ByteBuffer buffer) {
		return panic(buffer);
	}

	@Override
	public ByteBuffer result() {
		return builder.build();
	}
}
