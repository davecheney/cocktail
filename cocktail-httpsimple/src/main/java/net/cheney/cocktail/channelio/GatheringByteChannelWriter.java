package net.cheney.cocktail.channelio;

import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.channels.WritableByteChannel;

public class GatheringByteChannelWriter extends ChannelWriter {

	private final FileChannel src;
	private int position;
	private long count;

	public GatheringByteChannelWriter(WritableByteChannel dest, FileChannel src, long count) {
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
	public ChannelWriter write() throws IOException {
		long written = src.transferTo(position, count, channel);
		position += written;
		count -= written;
		return super.write();
	}

}
