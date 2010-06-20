package net.cheney.cocktail.channelio;

import static java.lang.String.format;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.ReadableByteChannel;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.apache.log4j.Logger;

public class ChannelReader {
	
	private static final Logger LOG = Logger.getLogger(ChannelReader.class);

	private final ReadableByteChannel channel;
	private final ByteBuffer buffer = (ByteBuffer) ByteBuffer.allocate(8192).flip();

	public ChannelReader(ReadableByteChannel channel) throws IOException {
		this.channel = channel;
	}
	
	private ByteBuffer readSocket() throws IOException {
		int read = channel.read(buffer);
		if(read < 0) {
			throw new ClosedChannelException();
		}
		buffer.flip();
		LOG.debug(format("Read: %d, remaining: %d [%s]", read, buffer.remaining(), buffer.toString()));
		return buffer;
	}
	
	public ByteBuffer read() throws IOException {
		if(buffer.hasRemaining()) {
			return buffer;
		} else {
			buffer.compact();
			return readSocket();
		}
	}
	
	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this, ToStringStyle.SIMPLE_STYLE);
	}

}
