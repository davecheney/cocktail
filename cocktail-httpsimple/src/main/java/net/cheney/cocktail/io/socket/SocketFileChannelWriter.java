package net.cheney.cocktail.io.socket;

import static java.lang.String.format;

import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.channels.GatheringByteChannel;

import net.cheney.cocktail.io.Channel;
import net.cheney.cocktail.io.Channel.Writer;

import org.apache.log4j.Logger;

public class SocketFileChannelWriter extends Channel.Writer {
	
	private static final Logger LOG = Logger.getLogger(SocketChannelWriter.class);
	private final FileChannel source;
	private long offset;
	private long length;

	public SocketFileChannelWriter(GatheringByteChannel target, FileChannel source) throws IOException {
		super(target);
		this.source = source;
		this.offset = 0;
		this.length = source.size();
	}

	@Override
	public Channel.Writer write() throws IOException {
		long written = source.transferTo(offset, length, channel);
		offset += written;
		length -= written;
		LOG.debug(format("Wrote: %d, remaining: %d [%s]", written, length, source.toString()));
		return writeMore();
	}

	@Override
	public boolean hasRemaning() {
		return length > 0;
	}
	
	@Override
	protected Writer tryWriteNext() throws IOException {
		LOG.debug(String.format("Closing %s", source));
		source.close();
		return super.tryWriteNext();
	}

}