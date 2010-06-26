package net.cheney.cocktail.parser;

import java.nio.ByteBuffer;

public class ChunkDataState extends ChunkState {

	private final ChunkBuilder builder;

	public ChunkDataState(ChunkBuilder builder) {
		this.builder = builder;
	}

	@Override
	public ChunkState parse(ByteBuffer buffer) {
		ByteBuffer target = builder.chunk();
		int limit = buffer.limit();
		buffer.limit(buffer.position()+target.remaining());
		target.put(buffer);
		buffer.limit(limit);
		if(target.hasRemaining()) {
			return this;
		} else {
			return new ChunkDataTrailerState(builder).parse(buffer);
		}
		
	}

}
