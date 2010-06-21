package net.cheney.cocktail.channelio;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.nio.ByteBuffer;

import net.cheney.cocktail.io.Channel;

import org.junit.Test;

public class ChainedChannelWriterTest {

	@Test public void testChannelWriter() throws IOException {
		ByteBuffer dest = ByteBuffer.allocate(8192);
		MockWritableByteChannel channel = new MockWritableByteChannel(dest);
		Channel.Writer w = Channel.Writer.forChannel(channel);
		ByteBuffer a = ByteBuffer.wrap(new byte[] { (byte)1, (byte)2,(byte) 3,(byte) 4 });
		w = w.write(a);
		assertEquals(dest.remaining(), 8192 - a.capacity());
	}
}
