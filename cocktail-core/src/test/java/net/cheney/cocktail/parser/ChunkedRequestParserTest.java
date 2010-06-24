package net.cheney.cocktail.parser;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;

import junit.framework.Assert;

import org.junit.Ignore;
import org.junit.Test;

public class ChunkedRequestParserTest {

	// http://www.jmarshall.com/easy/http/#http1.1c2
	@Ignore
	@Test public void testChunkedRequestParser() {
		ChunkedRequestParser parser = new ChunkedRequestParser();
		String data = 
			"1a; ignore-stuff-here\r\n"+
			"abcdefghijklmnopqrstuvwxyz\r\n"+
			"10\r\n"+
			"1234567890abcdef\r\n"+
			"0\r\n";
		ByteBuffer buffer = Charset.defaultCharset().encode(data);
		ByteBuffer actual = parser.parse(buffer);
		ByteBuffer expected = Charset.defaultCharset().encode("abcdefghijklmnopqrstuvwxyz1234567890abcdef");
		Assert.assertEquals(expected, actual);
	}
}
