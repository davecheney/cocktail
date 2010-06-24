package net.cheney.cocktail.parser;

import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;

import net.cheney.cocktail.message.Request;
import net.cheney.cocktail.message.RequestLine.Builder;
import net.cheney.cocktail.parser.RequestParser.State;

public class URIState implements State {

	private static final Charset US_ASCII = Charset.forName("US-ASCII");
//	private static final Charset UTF_8 = Charset.forName("UTF-8");
	
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
				int length = buffer.position() - offset;
				String s = new String(buffer.array(), buffer.arrayOffset() + offset, --length, US_ASCII);
//					String s = URLDecoder.decode(s, UTF_8.displayName());
				offset = buffer.position();
				URI uri = URI.create(s);
				return new VersionState(this.builder.uri(uri)).parse(buffer);
				
			default:
				throw new IllegalArgumentException("" + (char)buffer.get(buffer.position() -1));
			}
		}
		buffer.position(offset);
		return this;
	}

	@Override
	public Request result() {
		return null;
	}

}
