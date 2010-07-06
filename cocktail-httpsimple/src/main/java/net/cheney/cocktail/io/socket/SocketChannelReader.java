package net.cheney.cocktail.io.socket;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SocketChannel;

import net.cheney.cocktail.io.Channel;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

public class SocketChannelReader extends Channel.Reader {
	
	private final SocketChannel channel;
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
		return buffer;
	}
	
	@Override
	public ByteBuffer read() throws IOException {
		return buffer.hasRemaining() ? buffer : readSocket();
	}
	
	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
	
	@Override
	public void shutdown() throws IOException {
		channel.socket().shutdownInput();
	}

}
