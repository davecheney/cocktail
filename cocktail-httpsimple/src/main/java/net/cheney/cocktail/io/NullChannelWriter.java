package net.cheney.cocktail.io;

import java.io.IOException;
import java.nio.channels.GatheringByteChannel;


public class NullChannelWriter extends Channel.Writer {

	protected NullChannelWriter(GatheringByteChannel channel) {
		super(channel);
	}

	@Override
	public Channel.Writer write() throws IOException {
		return writeMore();
	}

	@Override
	public boolean hasRemaning() {
		return false;
	}

}
