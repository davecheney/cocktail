package net.cheney.cocktail.channelio;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

import net.cheney.cocktail.httpsimple.ReadyOperationHandler;

public class ChannelRegistration {

	private final SelectionKey key;
	private final ChannelReader reader;
	private final ChannelWriter writer;

	public ChannelRegistration(Selector selector, SocketChannel sc, int interstOps, ReadyOperationHandler handler) throws IOException {
		key = sc.register(selector, SelectionKey.OP_READ, handler);
		reader = new ChannelReader(sc);
		writer = ChannelWriter.forChannel(sc);
	}

	public void close() {
		try {
			key.channel().close();
			key.cancel();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void enableInterest(int ops) {
		key.interestOps(key.interestOps() | ops);
	}
	
	public void enableReadInterest() {
		enableInterest(SelectionKey.OP_READ);
	}
	
	public void enableWriteInterest() {
		enableInterest(SelectionKey.OP_WRITE);
	}

	public ChannelReader reader() {
		return reader;
	}

	public ChannelWriter writer() {
		return writer;
	}

}
