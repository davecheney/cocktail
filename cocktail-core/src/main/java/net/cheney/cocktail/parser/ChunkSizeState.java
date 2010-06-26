package net.cheney.cocktail.parser;

import java.nio.ByteBuffer;

public class ChunkSizeState extends ChunkState {

	private ChunkBuilder builder;

	public ChunkSizeState(ChunkBuilder builder) {
		this.builder = builder;
	}
	
	@Override
	public ChunkState parse(ByteBuffer buffer) {
		int offset = buffer.position();
		while(buffer.hasRemaining()) {
			switch(buffer.get()) {
			case 'A':
			case 'B':
			case 'C':
			case 'D':
			case 'E':
			case 'F':
			case 'a':
			case 'b':
			case 'c':
			case 'd':
			case 'e':
			case 'f':
			case '0':
			case '1':
			case '2':
			case '3':
			case '4':
			case '5':
			case '6':
			case '7':
			case '8':
			case '9':
				// valid char for CHUNK method
				continue;
				
			case ' ':
				// some implementations pad 0x20 between the chunk_size and the \r\n
				continue;
				
			case ';':
				int chunksize = Integer.parseInt(stringValue(buffer, offset).trim(), 16);
				offset = buffer.position();
				if(chunksize > 0) {
					return new ChunkExtensionState(builder.allocateChunk(chunksize)).parse(buffer);
				} else {
					panic(buffer);
				}
				continue;
				
			case '\r':
				int chunksize2 = Integer.parseInt(stringValue(buffer, offset).trim(), 16);
				offset = buffer.position();
				if(chunksize2 > 0) {
					return new ChunkSizeEndState(builder.allocateChunk(chunksize2)).parse(buffer);
				} else {
					return new LastChunkEndState(builder).parse(buffer);
				}
				
			default:
				panic(buffer);
			}
		}
		buffer.position(offset);
		return this;
	}

}
