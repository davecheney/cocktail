package net.cheney.cocktail.io.socket;

import static java.lang.String.format;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.SocketChannel;

import net.cheney.cocktail.io.Channel;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.apache.log4j.Logger;

public class SocketChannelReader extends Channel.Reader {
	
	private static final Logger LOG = Logger.getLogger(SocketChannelReader.class);

	private final ReadableByteChannel channel;
	private final ByteBuffer buffer = (ByteBuffer) ByteBuffer.allocate(8192).flip();

	public SocketChannelReader(SocketChannel channel) throws IOException {
		this.channel = channel;
	}
	
	private ByteBuffer readSocket() throws IOException {
		int read = channel.read(buffer.compact());
		if(read < 0) {
			throw new ClosedChannelException();
		}
		buffer.flip();
		LOG.debug(format("Read: %d, remaining: %d [%s]", read, buffer.remaining(), buffer.toString()));
		return buffer;
	}
	
	@Override
	public ByteBuffer read() throws IOException {
		return buffer.hasRemaining() ? buffer : readSocket();
	}
	
	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this, ToStringStyle.SIMPLE_STYLE);
	}

}
