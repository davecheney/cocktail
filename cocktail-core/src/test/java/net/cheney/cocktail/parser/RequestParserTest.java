package net.cheney.cocktail.parser;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

import java.net.URI;
import java.nio.ByteBuffer;
import java.util.Arrays;

import junit.framework.Assert;

import net.cheney.cocktail.message.Header;
import net.cheney.cocktail.message.Request;
import net.cheney.cocktail.message.Request.Method;
import net.cheney.cocktail.message.Version;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.junit.Test;

import com.google.common.collect.Iterables;

public class RequestParserTest extends BaseParserTest {

	@Test public void testSimpleHTTP10Request() {
		ByteBuffer b = request("GET /foo HTTP/1.0\r\n\r\n");
		RequestParser parser = new RequestParser();
		Request request = parser.parse(b);
		assertNotNull(ReflectionToStringBuilder.toString(parser), request);
		assertEquals(request.method(), Method.GET);
		assertEquals(request.uri(), URI.create("/foo"));
		assertEquals(request.version(), Version.HTTP_1_0);
	}
	
	@Test public void testSimpleHttp11Request() {
		ByteBuffer b = request("GET /foo HTTP/1.1\r\nHost: www.example.com\r\n\r\n");
		RequestParser parser = new RequestParser();
		Request request = parser.parse(b);
		assertNotNull(ReflectionToStringBuilder.toString(parser), request);
		assertEquals(request.method(), Method.GET);
		assertEquals(request.uri(), URI.create("/foo"));
		assertEquals(request.version(), Version.HTTP_1_1);
		assertTrue(Iterables.elementsEqual(request.header(Header.HOST), Arrays.asList("www.example.com")));
	}
	
	@Test public void testCookieHttp11Request() {
		ByteBuffer b = request("GET /foo HTTP/1.1\r\nHost: www.example.com\r\nCookie: foo=1\r\nCookie: bar=2\r\n\r\n");
		RequestParser parser = new RequestParser();
		Request request = parser.parse(b);
		assertNotNull(ReflectionToStringBuilder.toString(parser), request);
		assertEquals(request.method(), Method.GET);
		assertEquals(request.uri(), URI.create("/foo"));
		assertEquals(request.version(), Version.HTTP_1_1);
		assertTrue(Iterables.elementsEqual(request.header(Header.HOST), Arrays.asList("www.example.com")));
		assertTrue(Iterables.elementsEqual(request.header(Header.COOKIE), Arrays.asList("foo=1", "bar=2")));
	}
}
