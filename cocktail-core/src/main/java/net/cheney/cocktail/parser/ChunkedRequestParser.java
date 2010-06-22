package net.cheney.cocktail.parser;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class ChunkedRequestParser extends HttpParser<ByteBuffer> {
	
	private enum State {
		CHUNK_SIZE, CHUNK_EXTENSION, CHUNK_DATA, CHUNK_DATA_TRAILER, TRAILER
	}
	
	private State state = State.CHUNK_SIZE;

	private ByteBuffer chunk;
	private List<ByteBuffer> chunks = new ArrayList<ByteBuffer>();
	
	public ChunkedRequestParser() {
		reset();
	}
//	
//	@Override
//	public void reset() {
//		stateStack.addFirst(State.REQUEST_LINE_END);
//		stateStack.addFirst(State.HTTP_VERSION);
//		stateStack.addFirst(State.REQUEST_URI);
//		stateStack.addFirst(State.LAST_CHUNK);
	
	

	@Override
	public ByteBuffer parse(ByteBuffer buffer) {
		int offset = buffer.position();
		while(buffer.hasRemaining()) {
			switch (state) {
			case CHUNK_SIZE:
				byte d = buffer.get();
				switch(d) {
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
						this.chunk = allocateChunk(chunksize);
						state = State.CHUNK_EXTENSION;
					} else {
						state = State.TRAILER;
					}
					continue;
					
				case '\r':
					continue;
					
				case '\n':
					int l = buffer.position() - offset -1; // why ?
					String s2 = new String(buffer.array(), buffer.arrayOffset() + offset , --l, US_ASCII);
					int chunksize2 = Integer.parseInt(s2.trim(), 16);
					offset = buffer.position();
					if(chunksize2 > 0) {
						this.chunk = allocateChunk(chunksize2);
						state = State.CHUNK_DATA;
					} else {
						state = State.TRAILER;
					}
					break;
					
				default:
					throw new IllegalArgumentException("State: "+state+" data: ["+(char)d+"]");
				}
				break;
				
			case TRAILER:
				switch (buffer.get()) {
				case '\r':
					continue;
					
				case '\n':
					offset = buffer.position();
					return chunkData();
				}
				break;
				
			case CHUNK_EXTENSION:
				switch (buffer.get()) {
				case '\r':
					continue;
					
				case '\n':
					offset = buffer.position();
					state = State.CHUNK_DATA;
					break;
				}
				break;
				
			case CHUNK_DATA:
				ByteBuffer source = buffer.asReadOnlyBuffer();
				// TODO needs to be smarter
				source.limit(source.position() + chunk.remaining());
				chunk.put(source);
				// always consume, urgh
				offset = buffer.position(buffer.position() + chunk.position()).position();
				if(!chunk.hasRemaining()) {
					state = State.CHUNK_DATA_TRAILER;
				}
				break;
				
			case CHUNK_DATA_TRAILER:
				byte c = buffer.get();
				switch (c) {
				case '\r':
					continue;
					
				case '\n':
					offset = buffer.position();
					state = State.CHUNK_SIZE;
					break;
					
				default:
					throw new IllegalArgumentException("State: "+state+" data: ["+(char)c);
				}
				break;
				
			default:
				throw new RuntimeException(state.toString());
			}
		}
		buffer.position(offset);
		return null;
	}
	
	private ByteBuffer chunkData() {
		int size = 0;
		for(ByteBuffer chunk : chunks) {
			// tricky
			size += chunk.flip().remaining();
		}
		ByteBuffer result = ByteBuffer.allocate(size);
		for(ByteBuffer chunk : chunks) {
			result.put(chunk);
		}
		return (ByteBuffer) result.flip();
	}
	
	
	private ByteBuffer allocateChunk(int chunksize) {
		ByteBuffer chunk = ByteBuffer.allocate(chunksize);
		chunks.add(chunk);
		return chunk;
	}
	
	@Override
	public void reset() {
		// TODO Auto-generated method stub
		
	}

}
