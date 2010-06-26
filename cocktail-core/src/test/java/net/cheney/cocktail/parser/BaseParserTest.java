package net.cheney.cocktail.parser;

import static junit.framework.Assert.assertTrue;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;

import com.google.common.collect.Iterables;

public abstract class BaseParserTest {

	protected ByteBuffer request(String string) {
		return Charset.forName("US-ASCII").encode(string);
	}
	
	protected static void assertElementsEqual(Iterable<?> expected, Iterable<?> actual) {
		assertTrue(String.format("Expected: %s, Actual: %s", expected, actual), Iterables.elementsEqual(expected, actual));
	}
}
