package net.cheney.cocktail.channelio;

import java.io.IOException;
import java.nio.channels.WritableByteChannel;

public class NullChannelWriter extends ChannelWriter {

	protected NullChannelWriter(WritableByteChannel channel) {
		super(channel);
	}

	@Override
	public ChannelWriter write() throws IOException {
		return writeMore();
	}

	@Override
	public boolean hasRemaning() {
		return false;
	}

}
