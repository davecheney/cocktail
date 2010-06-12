package net.cheney.cocktail.channelio;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;

public class BufferChannelWriter extends ChannelWriter {

	private final ByteBuffer buffer;

	public BufferChannelWriter(WritableByteChannel channel, ByteBuffer buffer) {
		super(channel);
		this.buffer = buffer;
	}

	@Override
	public ChannelWriter write() throws IOException {
		channel.write(buffer);
		return super.write();
	}

	@Override
	public boolean hasRemaning() {
		return buffer.hasRemaining();
	}

}
