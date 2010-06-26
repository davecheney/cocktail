package net.cheney.cocktail.parser;

import java.nio.ByteBuffer;

public class LastChunkEndState extends ChunkState {

	private final ChunkBuilder builder;

	public LastChunkEndState(ChunkBuilder builder) {
		this.builder = builder;
	}

	@Override
	public ChunkState parse(ByteBuffer buffer) {
		// TODO Auto-generated method stub
		return null;
	}

}
