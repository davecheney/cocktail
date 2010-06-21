package net.cheney.cocktail.channelio;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.GatheringByteChannel;
import java.nio.channels.WritableByteChannel;

import junit.framework.Assert;

import org.junit.Test;

public class MockWritableByteChannel implements GatheringByteChannel {

	private final ByteBuffer buffer;

	public MockWritableByteChannel(ByteBuffer buffer) {
		this.buffer = buffer;
	}
	
	@Override
	public boolean isOpen() {
		return true;
	}

	@Override
	public void close() throws IOException {
		// TODO Auto-generated method stub

	}
	
	public ByteBuffer contents() {
		return buffer;
	}

	@Override
	public int write(ByteBuffer src) throws IOException {
		int remaining = buffer.remaining();
		buffer.put(src);
		return remaining - buffer.remaining();
	}

	@Override
	public long write(ByteBuffer[] srcs, int offset, int length)
			throws IOException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long write(ByteBuffer[] srcs) throws IOException {
		// TODO Auto-generated method stub
		return 0;
	}

}
