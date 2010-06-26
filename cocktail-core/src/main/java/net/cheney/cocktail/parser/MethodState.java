package net.cheney.cocktail.parser;

import java.nio.ByteBuffer;

import net.cheney.cocktail.message.Request.Method;
import net.cheney.cocktail.message.RequestLine;
import net.cheney.cocktail.parser.RequestParser.State;

public class MethodState extends BaseState {
	
	@Override
	public State parse(ByteBuffer buffer) {
		int offset = buffer.position();
		while(buffer.hasRemaining()) {
			switch(buffer.get()) {
			case 'A':
			case 'B':
			case 'C':
			case 'D':
			case 'E':
			case 'F':
			case 'G':
			case 'H':
			case 'I':
			case 'J':
			case 'K':
			case 'L':
			case 'M':
			case 'N':
			case 'O':
			case 'P':
			case 'Q':
			case 'R':
			case 'S':
			case 'T':
			case 'U':
			case 'V':
			case 'W':
			case 'X':
			case 'Y':
			case 'Z':
				continue;
				
				
			case ' ':
				int length = buffer.position() - offset;
				String s = new String(buffer.array(), buffer.arrayOffset() + offset, --length, US_ASCII);
				Method method = Method.parse(s);
				offset = buffer.position();
				return new URIState(RequestLine.builder().method(method)).parse(buffer);
				
			default:
				throw new IllegalArgumentException();
			}
		}
		buffer.position(offset);
		return this;
	}

}
