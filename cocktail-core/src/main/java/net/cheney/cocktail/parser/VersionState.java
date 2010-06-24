package net.cheney.cocktail.parser;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;

import net.cheney.cocktail.message.Request;
import net.cheney.cocktail.message.RequestLine.Builder;
import net.cheney.cocktail.message.Version;
import net.cheney.cocktail.parser.RequestParser.State;

public class VersionState implements State {
	
	private static final Charset US_ASCII = Charset.forName("US-ASCII");
	private final Builder builder;

	public VersionState(Builder builder) {
		this.builder = builder;
	}

	@Override
	public State parse(ByteBuffer buffer) {
		int offset = buffer.position();
		while(buffer.hasRemaining()) {
			switch(buffer.get()) {
			case 'H':
			case 'P':
			case 'T':
			case '/':
			case '.':
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
				
				
			case '\r':
				int length = buffer.position() - offset;
				String s = new String(buffer.array(), buffer.arrayOffset() + offset, --length, US_ASCII);
				Version version = Version.parse(s);
				offset = buffer.position();
				return new RequestLineState(builder.version(version)).parse(buffer);
				
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
