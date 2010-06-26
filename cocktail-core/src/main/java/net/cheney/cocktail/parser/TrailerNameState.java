package net.cheney.cocktail.parser;

import java.nio.ByteBuffer;

import net.cheney.cocktail.message.Header;
import net.cheney.cocktail.message.Request.Builder;
import net.cheney.cocktail.parser.RequestParser.State;

public class TrailerNameState extends BaseState {

	private final Builder builder;

	public TrailerNameState(Builder builder) {
		this.builder = builder;
	}

	@Override
	public State parse(ByteBuffer buffer) {
		int offset = buffer.position();
		while(buffer.hasRemaining()) {
			switch(buffer.get()) {
			// token
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
				continue;
				
			case ':':
				Header header = parseHeader(stringValue(buffer, offset));
				offset = buffer.position();
				return new TrailerValueState(builder, builder.header(header)).parse(buffer);
				
			default:
				panic(buffer);
			}
		}
		buffer.position(offset);
		return this;
	}
	
	private Header parseHeader(final String s) {
		return new Header() {

			@Override
			public net.cheney.cocktail.message.Header.Type type() {
				return Type.REQUEST;
			}

			@Override
			public String name() {
				return s;
			}
			
			@Override
			public String toString() {
				return name();
			}

		};	
	}

}
