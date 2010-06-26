package net.cheney.cocktail.parser;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class ChunkBuilder {
	
	private final List<ByteBuffer> chunks = new ArrayList<ByteBuffer>();
	private ByteBuffer chunk = null;

	public ChunkBuilder allocateChunk(int size) {
		this.chunk = ByteBuffer.allocate(size);
		this.chunks.add(chunk);
		return this;
	}
	
	public ByteBuffer chunk() {
		return this.chunk;
	}
	
	public ByteBuffer build() {
		int size = 0;
		for(ByteBuffer chunk : chunks) {
			size += chunk.flip().remaining();
		}
		ByteBuffer result = ByteBuffer.allocate(size);
		for(ByteBuffer chunk : chunks) {
			result.put(chunk);
		}
		return (ByteBuffer) result.flip();
	}
}
