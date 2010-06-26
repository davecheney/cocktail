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
				int length = buffer.position() - offset;
				String s = new String(buffer.array(), buffer.arrayOffset() + offset, --length, US_ASCII);
				int chunksize = Integer.parseInt(s.trim(), 16);
				offset = buffer.position();
				if(chunksize > 0) {
					panic(buffer);
				} else {
					panic(buffer);
				}
				continue;
				
			case '\n':
				int l = buffer.position() - offset -1; // why ?
				String s2 = new String(buffer.array(), buffer.arrayOffset() + offset , --l, US_ASCII);
				int chunksize2 = Integer.parseInt(s2.trim(), 16);
				offset = buffer.position();
				if(chunksize2 > 0) {
//					this.chunk = allocateChunk(chunksize2);
//					state = State.CHUNK_DATA;
				} else {
					return new LastChunkEndState(builder).parse(buffer);
				}
				break;
				
			default:
				panic(buffer);
			}
		}
		buffer.position(offset);
		return this;
	}

}
