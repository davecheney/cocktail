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

public class IdentityRequestParserTest extends BaseParserTest {

	@Test public void testIdentifyBodyPropfind() {
		
		ByteBuffer buffer = request(
				"PROPFIND / HTTP/1.1\r\n"+
				"User-Agent: Transmit/4.0.5 neon/0.29.3\r\n"+
				"Keep-Alive: \r\n" +
				"Connection: TE, Keep-Alive\r\n" +
				"TE: trailers\r\n" +
				"Host: deadwood.cheney.net:8081\r\n" +
				"Depth: 0\r\n" +
				"Content-Length: 117\r\n" +
				"Content-Type: application/xml\r\n" +
				"Cookie: __utma=21207780.128671873.1276921212.1276921212.1276924188.2; __utmz=21207780.1276921212.1.1.utmcsr=(direct)|utmccn=(direct)|utmcmd=(none)\r\n" +
				"\r\n"+
				"<?xml version=\"1.0\" encoding=\"utf-8\"?>\n<propfind xmlns=\"DAV:\"><prop>\n<resourcetype xmlns=\"DAV:\"/>\n</prop></propfind>\n");
		RequestParser parser = new RequestParser();
		Request request = parser.parse(buffer);
		assertNotNull(ReflectionToStringBuilder.toString(parser, ToStringStyle.SHORT_PREFIX_STYLE), request);
		assertEquals(request.method(), Method.PROPFIND);
		assertEquals(request.uri(), URI.create("/"));
		assertEquals(request.version(), Version.HTTP_1_1);
//		assertTrue(Iterables.elementsEqual(request.header(Header.HOST), Arrays.asList("www.example.com")));
//		assertTrue(Iterables.elementsEqual(request.header(Header.TRANSFER_ENCODING), Arrays.asList("chunked")));
		
		request = parser.parse(buffer);
		ByteBuffer actual = request.body();
		ByteBuffer expected = Charset.defaultCharset().encode("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n<propfind xmlns=\"DAV:\"><prop>\n<resourcetype xmlns=\"DAV:\"/>\n</prop></propfind>\n");
		Assert.assertEquals(expected, actual);
	}
}
