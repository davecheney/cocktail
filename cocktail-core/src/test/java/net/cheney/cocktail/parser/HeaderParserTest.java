package net.cheney.cocktail.parser;

import static junit.framework.Assert.assertNotNull;
import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Iterator;

import net.cheney.cocktail.message.Header;
import net.cheney.cocktail.parser.HeaderParser;
import net.cheney.cocktail.parser.Headers;

import org.junit.Test;

import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;

public class HeaderParserTest {

	@Test public void testHttpParser1() {
		String string = "Host: www.foo.org\r\n" +
			"Connection: close\r\n" +
			"Accept: */*\r\n" +
			"Via: 1.0 ricky, 1.1 ethel, 1.1 fred, 1.0 lucy\r\n" +
			"\r\n";
		HeaderParser parser = new HeaderParser();
		Headers headers = parser.parse(string);
		assertNotNull(headers);
		System.out.println(headers.headers());
	}
	
	@Test public void testViaHeader() {
		String string = "Via: 1.0 fred, 1.1 nowhere.com (Apache/1.1)\r\n\r\n";
		HeaderParser parser = new HeaderParser();
		Headers headers = parser.parse(string);
		assertNotNull(headers);
		assertEquals(Lists.newArrayList(headers.header(Header.VIA)), Arrays.asList("1.0 fred", "1.1 nowhere.com (Apache/1.1)"));
	}
}
