package net.cheney.cocktail.parser;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.Arrays;

import junit.framework.Assert;

import net.cheney.cocktail.message.Header;
import net.cheney.cocktail.message.Request;
import net.cheney.cocktail.message.Version;
import net.cheney.cocktail.message.Request.Method;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.junit.Test;

import com.google.common.collect.Iterables;

public class ChunkedRequestParserTest extends BaseParserTest {

	// http://www.jmarshall.com/easy/http/#http1.1c2
	@Test public void testChunkedRequestParser() {
		String data = 
			"1a; ignore-stuff-here\r\n"+
			"abcdefghijklmnopqrstuvwxyz\r\n"+
			"10\r\n"+
			"1234567890abcdef\r\n"+
			"0\r\n";
		ByteBuffer b = request("GET /foo HTTP/1.1\r\nHost: www.example.com\r\nTransfer-Encoding: chunked\r\n\r\n"+data);
		RequestParser parser = new RequestParser();
		Request request = parser.parse(b);
		assertNotNull(ReflectionToStringBuilder.toString(parser, ToStringStyle.SHORT_PREFIX_STYLE), request);
		assertEquals(request.method(), Method.GET);
		assertEquals(request.uri(), URI.create("/foo"));
		assertEquals(request.version(), Version.HTTP_1_1);
		assertTrue(Iterables.elementsEqual(request.header(Header.HOST), Arrays.asList("www.example.com")));
		assertTrue(Iterables.elementsEqual(request.header(Header.TRANSFER_ENCODING), Arrays.asList("chunked")));
		ByteBuffer actual = request.body();
		ByteBuffer expected = Charset.defaultCharset().encode("abcdefghijklmnopqrstuvwxyz1234567890abcdef");
		Assert.assertEquals(expected, actual);
	}
}
