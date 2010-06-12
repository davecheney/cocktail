package net.cheney.cocktail.channelio;

import static junit.framework.Assert.assertEquals;

import java.io.IOException;
import java.nio.ByteBuffer;

import org.junit.Test;

public class MockWritableByteChannelTest {

	@Test public void testMockwritableByteChannel() throws IOException {
		ByteBuffer dest = ByteBuffer.allocate(100);
		MockWritableByteChannel chan = new MockWritableByteChannel(dest);
		ByteBuffer src = ByteBuffer.wrap(new byte[] { 0, 0, 0, 0, 0, 0, 0, 0} );
		assertEquals(chan.write(src), 8);
		assertEquals(dest.remaining(), 92);
	}
	
}

