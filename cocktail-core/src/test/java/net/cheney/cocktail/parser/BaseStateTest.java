package net.cheney.cocktail.parser;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;


public abstract class BaseStateTest  {

	private static final Charset CHARSET_US_ASCII = Charset.forName("US-ASCII");
	
	protected ByteBuffer buffer(String string) {
		return CHARSET_US_ASCII.encode(string);
	}
}
