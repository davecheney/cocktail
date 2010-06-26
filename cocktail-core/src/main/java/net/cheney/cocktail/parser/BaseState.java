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

	final boolean isTokenChar(byte c) {
		return ((c >= '\u0030' && c <= '\u0039')
				|| (c >= '\u0041' && c <= '\u005A')
				|| (c >= '\u0061' && c <= '\u007a') || c == '!' || c == '#'
				|| c == '$' || c == '%' || c == '&' || c == '\'' || c == '*'
				|| c == '+' || c == '-' || c == '.' || c == '^' || c == '_'
				|| c == '`' || c == '|' || c == '~');
	}
	
	final State panic(ByteBuffer buffer) {
		throw new IllegalArgumentException("[" + (char)buffer.get(buffer.position() -1) + "]");
	}
	
	final String stringValue(ByteBuffer buffer, int offset) {
		int length = buffer.position() - offset;
		return new String(buffer.array(), buffer.arrayOffset() + offset, --length, US_ASCII);
	}
}
