package net.cheney.cocktail.io.socket;

import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.channels.GatheringByteChannel;

import net.cheney.cocktail.io.Channel;

public class GatheringByteChannelWriter extends Channel.Writer {

	private final FileChannel src;
	private int position;
	private long count;

	public GatheringByteChannelWriter(GatheringByteChannel dest, FileChannel src, long count) {
		super(dest);
		this.src = src;
		this.position = 0;
		this.count = count;
	}

	@Override
	public boolean hasRemaning() {
		return count > 0;
	}
	
	@Override
	public Channel.Writer write() throws IOException {
		long written = src.transferTo(position, count, channel);
		position += written;
		count -= written;
		return writeMore();
	}

}
