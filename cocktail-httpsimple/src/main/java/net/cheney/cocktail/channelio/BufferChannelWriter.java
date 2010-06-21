package net.cheney.cocktail.channelio;

import static java.lang.String.format;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.GatheringByteChannel;

import org.apache.log4j.Logger;

public class BufferChannelWriter extends Channel.Writer {
	
	private static final Logger LOG = Logger.getLogger(BufferChannelWriter.class);

	private final ByteBuffer buffer;

	public BufferChannelWriter(GatheringByteChannel channel, ByteBuffer buffer) {
		super(channel);
		this.buffer = buffer;
	}

	@Override
	public Channel.Writer write() throws IOException {
		int wrote = channel.write(buffer);
		LOG.debug(format("Wrote: %d, remaining: %d [%s]", wrote, buffer.remaining(), buffer.toString()));
		return writeMore();
	}

	@Override
	public boolean hasRemaning() {
		return buffer.hasRemaining();
	}

}
