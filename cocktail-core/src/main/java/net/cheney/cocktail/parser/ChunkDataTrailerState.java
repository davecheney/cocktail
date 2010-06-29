package net.cheney.cocktail.parser;

import java.nio.ByteBuffer;

public class ChunkDataTrailerState extends ChunkState {

	private final ChunkBuilder builder;

	public ChunkDataTrailerState(ChunkBuilder builder) {
		this.builder = builder;
	}

	@Override
	public ChunkState parse(ByteBuffer buffer) {
		int offset = buffer.position();
		while(buffer.hasRemaining()) {
			switch(buffer.get()) {
			case '\r':
				return new ChunkDataTrailerEnd(builder).parse(buffer);
				
			default:
				panic(buffer);
			}
		}
		buffer.position(offset);
		return this;
	}

}
