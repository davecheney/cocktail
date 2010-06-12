package net.cheney.cocktail.channelio;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

public class ChannelReader {

	private final ReadableByteChannel channel;
	private final ByteBuffer buffer = (ByteBuffer) ByteBuffer.allocate(8192).flip();

	public ChannelReader(ReadableByteChannel channel) throws IOException {
		this.channel = channel;
	}
	
	private ByteBuffer readSocket() throws IOException {
		channel.read(buffer);
		return (ByteBuffer) buffer.flip();
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
