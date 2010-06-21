package net.cheney.cocktail.channelio;

import static java.lang.String.format;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.GatheringByteChannel;
import java.util.Arrays;

import org.apache.log4j.Logger;

public class BufferVChannelWriter extends Channel.Writer {
	
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
	public Channel.Writer write() throws IOException {
		LOG.debug(format("Writing: offset: %d, length: %d, %s", offset, length, Arrays.toString(buffs)));
		long wrote = channel.write(buffs, offset, length - offset);
		for( ; offset < length ; ++offset) {
			if(buffs[offset].hasRemaining()) {
				break;
			} 
		}
		LOG.debug(format("Wrote: %d, remaining: %d, %s", wrote, buffs[lastIndex()].remaining(), Arrays.toString(buffs)));
		return writeMore();
	}

	@Override
	public boolean hasRemaning() {
		return buffs[lastIndex()].hasRemaining();
	}
	
	private int lastIndex() {
		return Math.min(offset, length - 1);
	}
	
}
