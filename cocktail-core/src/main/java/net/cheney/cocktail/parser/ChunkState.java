package net.cheney.cocktail.parser;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;

public abstract class ChunkState {
	
	static final Charset US_ASCII = Charset.forName("US-ASCII");

	public abstract ChunkState parse(ByteBuffer buffer);
	
	public ByteBuffer result() {
		return null;
	}
	
	final ChunkState panic(ByteBuffer buffer) {
		throw new IllegalArgumentException(String.format("%x[%s]", buffer.get(buffer.position() -1), (char)buffer.get(buffer.position() -1)));
	}
	
	final String stringValue(ByteBuffer buffer, int offset) {
		int length = buffer.position() - offset;
		return new String(buffer.array(), buffer.arrayOffset() + offset, --length, US_ASCII);
	}

}
