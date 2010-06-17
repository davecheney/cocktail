package net.cheney.cocktail.channelio;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;

import org.apache.log4j.Logger;

public class BufferChannelWriter extends ChannelWriter {
	
	private static final Logger LOG = Logger.getLogger(BufferChannelWriter.class);

	private final ByteBuffer buffer;

	public BufferChannelWriter(WritableByteChannel channel, ByteBuffer buffer) {
		super(channel);
		this.buffer = buffer;
	}

	@Override
	public ChannelWriter write() throws IOException {
		int wrote = channel.write(buffer);
		LOG.debug("Wrote "+wrote+": "+buffer.toString());
		return writeMore();
	}

	@Override
	public boolean hasRemaning() {
		return buffer.hasRemaining();
	}

}
