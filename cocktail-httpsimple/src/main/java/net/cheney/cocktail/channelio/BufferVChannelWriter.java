package net.cheney.cocktail.channelio;

import static java.lang.String.format;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.GatheringByteChannel;

import org.apache.log4j.Logger;

public class BufferVChannelWriter extends ChannelWriter {
	
	private static final Logger LOG = Logger.getLogger(BufferVChannelWriter.class);

	private final ByteBuffer[] buffs;

	private int offset, length;

	public BufferVChannelWriter(GatheringByteChannel channel, ByteBuffer... buffs) {
		super(channel);
		this.buffs = buffs;
		this.offset = 0;
		this.length = buffs.length;
	}

	@Override
	public ChannelWriter write() throws IOException {
		long wrote = channel.write(buffs, offset, length);
		for(int n = buffs.length; offset < n ; ++offset) {
			if(!buffs[offset].hasRemaining()) 
				break;
		}
		length -= offset;
		LOG.debug(format("Wrote: %d, remaining: %d, offset: %d, length: %d", wrote, buffs[offset].remaining(), offset, length));
		return writeMore();
	}

	@Override
	public boolean hasRemaning() {
		return buffs[offset + length - 1].hasRemaining();
	}

}
