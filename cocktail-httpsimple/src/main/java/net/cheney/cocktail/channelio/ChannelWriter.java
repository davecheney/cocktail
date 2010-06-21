package net.cheney.cocktail.channelio;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.GatheringByteChannel;

public abstract class ChannelWriter {
	
	private ChannelWriter next;
	protected final GatheringByteChannel channel;

	protected ChannelWriter(GatheringByteChannel channel) {
		this.channel = channel;
	}

	public static ChannelWriter forChannel(GatheringByteChannel channel) {
		return new NullChannelWriter(channel);
	}
	
	protected ChannelWriter next() {
		return next;
	}
	
	protected void setNext(ChannelWriter next) {
		this.next = next;
	}
	
	public ChannelWriter write(ByteBuffer buffer) throws IOException {
		return write(new BufferChannelWriter(channel, buffer));
	}
	
	public ChannelWriter write(ByteBuffer header, ByteBuffer body) throws IOException {
		return write(new BufferVChannelWriter(channel, header, body));
	}
		
	private ChannelWriter write(ChannelWriter next) throws IOException {
		ChannelWriter last = last();
		if(last.hasRemaning()) {
			last.setNext(next);
			return write();
		} else {
			return next.write();
		}
	}

	protected boolean hasNext() {
		return next != null;
	}

	public abstract ChannelWriter write() throws IOException;
	
	protected ChannelWriter writeMore() throws IOException {
		return hasRemaning() ? this : tryWriteNext();
	}
	
	private ChannelWriter tryWriteNext() throws IOException {
		return hasNext() ? next().write() : this;
	}

	public ChannelWriter write(FileChannel src, long count) throws IOException {
		return write(new GatheringByteChannelWriter(channel, src, count));
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
