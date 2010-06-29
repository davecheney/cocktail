package net.cheney.cocktail.parser;

import java.nio.ByteBuffer;

import net.cheney.cocktail.message.RequestLine.Builder;
import net.cheney.cocktail.message.Version;
import net.cheney.cocktail.parser.RequestParser.State;

public class VersionState extends BaseState {
	
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
				Version version = Version.parse(stringValue(buffer, offset));
				return new RequestLineState(builder.version(version)).parse(buffer);
				
			default:
				panic(buffer);
			}
		}
		buffer.position(offset);
		return this;
	}

}
