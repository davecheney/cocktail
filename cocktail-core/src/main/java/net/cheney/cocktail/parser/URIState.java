package net.cheney.cocktail.parser;

import java.net.URI;
import java.nio.ByteBuffer;

import net.cheney.cocktail.message.RequestLine.Builder;
import net.cheney.cocktail.parser.RequestParser.State;

public class URIState extends BaseState {
	
	private final Builder builder;

	public URIState(Builder builder) {
		this.builder = builder;
	}

	@Override
	public State parse(ByteBuffer buffer) {
		int offset = buffer.position();
		while(buffer.hasRemaining()) {
			switch(buffer.get()) {
			// ALPHA
			// UPALPHA
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
			// LOALPHA 
			case 'a':
			case 'b':
			case 'c':
			case 'd':
			case 'e':
			case 'f':
			case 'g':
			case 'h':
			case 'i':
			case 'j':
			case 'k':
			case 'l':
			case 'm':
			case 'n':
			case 'o':
			case 'p':
			case 'q':
			case 'r':
			case 's':
			case 't':
			case 'u':
			case 'v':
			case 'w':
			case 'x':
			case 'y':
			case 'z':
			// DIGIT
			case '0':
			case '1':
			case '2':
			case '3':
			case '4':
			case '5':
			case '6':
			case '7':
			case '8':
			case '9':
			// unreserved
			case '-':
			case '.':
			case '_':
			case '~':
			// pct-encoded
			case '%':
			// sub-delims
			case '!': 
			case '$':
			case '&': 
			case '\'':
			case '(':
			case ')':
			case '*':
			case '+':
			case ',':
			case ';':
			case '=':
			case ':':
			case '@':
			case '/':
				continue;
			
			case ' ':
				URI uri = URI.create(stringValue(buffer, offset));
				return new VersionState(this.builder.uri(uri)).parse(buffer);
				
			default:
				panic(buffer);
			}
		}
		buffer.position(offset);
		return this;
	}

}
