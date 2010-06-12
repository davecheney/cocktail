package net.cheney.cocktail.parser;

import static junit.framework.Assert.assertNotNull;

import java.util.Arrays;

import net.cheney.cocktail.message.Header;

import org.junit.Assert;
import org.junit.Test;

import com.google.common.collect.Iterables;

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
		Iterable<String> expected = Arrays.asList("1.0 ricky", "1.1 ethel", "1.1 fred", "1.0 lucy");
		Iterable<String> actual = headers.header(Header.VIA);
		Assert.assertTrue(Iterables.elementsEqual(expected, actual));
	}
	
	@Test public void testViaHeader() {
		String string = "Via: 1.0 fred, 1.1 nowhere.com (Apache/1.1)\r\n\r\n";
		HeaderParser parser = new HeaderParser();
		Headers headers = parser.parse(string);
		assertNotNull(headers);
		Iterable<String> expected = Arrays.asList("1.0 fred", "1.1 nowhere.com (Apache/1.1)");
		Iterable<String> actual = headers.header(Header.VIA);
		Assert.assertTrue(Iterables.elementsEqual(expected, actual));
	}
}
