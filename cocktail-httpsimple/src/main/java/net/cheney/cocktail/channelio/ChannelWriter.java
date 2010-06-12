package net.cheney.cocktail.channelio;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.WritableByteChannel;

public abstract class ChannelWriter {
	
	private ChannelWriter next;
	protected final WritableByteChannel channel;

	protected ChannelWriter(WritableByteChannel channel) {
		this.channel = channel;
	}

	public static ChannelWriter forChannel(WritableByteChannel channel) {
		return new BufferChannelWriter(channel, ByteBuffer.allocate(0));
	}
	
	protected ChannelWriter next() {
		return next;
	}
	
	protected void setNext(ChannelWriter next) {
		this.next = next;
	}
	
	public ChannelWriter write(ByteBuffer buffer) throws IOException {
		last().setNext(new BufferChannelWriter(channel, buffer));
		return write();
	}
	
	protected boolean hasNext() {
		return next != null;
	}

	public ChannelWriter write() throws IOException {
		return (hasRemaning() || !hasNext()) ? this : next().write();
	}
	
	public ChannelWriter write(FileChannel src, long count) throws IOException {
		last().setNext(new GatheringByteChannelWriter(channel, src, count));
		return write();
	}

	private ChannelWriter last() {
		ChannelWriter w = this;
		while(w.hasNext()) {
			w = w.next();
		}
		return w;
	}

	public abstract boolean hasRemaning();
	
}
