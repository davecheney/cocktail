package net.cheney.cocktail.io.socket;

import java.io.IOException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import net.cheney.cocktail.io.Channel;
import net.cheney.cocktail.io.Channel.Registration;

public class SocketChannelRegistration implements Registration {

	private final SelectionKey key;

	public SocketChannelRegistration(Selector selector, SocketChannel sc, int interstOps, Handler handler) throws ClosedChannelException {
		key = sc.register(selector, SelectionKey.OP_READ, handler);
	}

	@Override
	public void close() {
		try {
			key.channel().close();
			key.cancel();
		} catch (IOException ignored) {
		}
	}

	private void enableInterest(int ops) {
		key.interestOps(key.interestOps() | ops);
	}

	@Override
	public void enableReadInterest() {
		enableInterest(SelectionKey.OP_READ);
	}

	@Override
	public void enableWriteInterest() {
		enableInterest(SelectionKey.OP_WRITE);
	}

	@Override
	public Channel.Reader reader() throws IOException {
		return new SocketChannelReader(channel());
	}

	private SocketChannel channel() {
		return (SocketChannel) key.channel();
	}

	@Override
	public Channel.Writer writer() {
		return Channel.Writer.forChannel(channel());
	}
	
	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
	
	@Override
	public boolean isReadable() {
		return key.isReadable();
	}

}
