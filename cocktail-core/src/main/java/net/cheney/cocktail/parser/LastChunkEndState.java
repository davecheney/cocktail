package net.cheney.cocktail.parser;

import java.nio.ByteBuffer;

public class LastChunkEndState extends ChunkState {

	private final ChunkBuilder builder;

	public LastChunkEndState(ChunkBuilder builder) {
		this.builder = builder;
	}

	@Override
	public ChunkState parse(ByteBuffer buffer) {
		int offset = buffer.position();
		while(buffer.hasRemaining()) {
			switch(buffer.get()) {
			case '\n':
				return new ChunkResultState(builder);
				
			default:
				panic(buffer);
			}
		}
		buffer.position(offset);
		return this;
	}

}
