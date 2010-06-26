package net.cheney.cocktail.parser;

import java.nio.ByteBuffer;

public class ChunkDataState extends ChunkState {

	private final ChunkBuilder builder;

	public ChunkDataState(ChunkBuilder builder) {
		this.builder = builder;
	}

	@Override
	public ChunkState parse(ByteBuffer buffer) {
		ByteBuffer source = buffer.asReadOnlyBuffer();
		// TODO needs to be smarter
		source.limit(source.position() + builder.chunk().remaining());
		builder.chunk().put(source);
		// always consume, urgh
		buffer.position(buffer.position() + builder.chunk().position()).position();
		if(!builder.chunk().hasRemaining()) {
			return new ChunkDataTrailerState(builder).parse(buffer);
		} else {
			return this;
		}
		
	}

}
