package net.cheney.cocktail.parser;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;

import net.cheney.cocktail.message.Request;
import net.cheney.cocktail.parser.RequestParser.State;

abstract class BaseState implements State {
	
	static final Charset US_ASCII = Charset.forName("US-ASCII");
	static final Charset UTF_8 = Charset.forName("UTF-8");

	@Override
	public Request result() {
		return null;
	}
	
	final boolean isWhitespace(byte t) {
		return (t == ' ' | t == '\t');
	}

	final boolean isVisibleCharacter(byte t) {
		return (t >= '\u0021' && t <= '\u007E');
	}

	final boolean isTokenChar(byte b) {
		return ((b >= '\u0030' && b <= '\u0039')
				|| (b >= '\u0041' && b <= '\u005A')
				|| (b >= '\u0061' && b <= '\u007a') || b == '!' || b == '#'
				|| b == '$' || b == '%' || b == '&' || b == '\'' || b == '*'
				|| b == '+' || b == '-' || b == '.' || b == '^' || b == '_'
				|| b == '`' || b == '|' || b == '~');
	}
	
	final State panic(ByteBuffer buffer) {
		throw new IllegalArgumentException("" + (char)buffer.get(buffer.position() -1));
	}
	
	final String stringValue(ByteBuffer buffer, int offset) {
		int length = buffer.position() - offset;
		return new String(buffer.array(), buffer.arrayOffset() + offset, --length, US_ASCII);
	}
}
