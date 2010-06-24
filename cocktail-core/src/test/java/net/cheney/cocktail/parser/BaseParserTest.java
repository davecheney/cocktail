package net.cheney.cocktail.parser;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;

public abstract class BaseParserTest {

	protected ByteBuffer request(String string) {
		return Charset.forName("US-ASCII").encode(string);
	}
}
