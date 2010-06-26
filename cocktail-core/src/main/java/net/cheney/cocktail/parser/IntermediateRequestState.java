package net.cheney.cocktail.parser;

import java.nio.ByteBuffer;

import net.cheney.cocktail.message.Header;
import net.cheney.cocktail.message.Request;
import net.cheney.cocktail.message.Request.Builder;
import net.cheney.cocktail.parser.RequestParser.State;

public class IntermediateRequestState extends BaseState {

	private final Builder builder;
	
	private enum TransferEncoding { IDENTITY, CHUNKED };

	public IntermediateRequestState(Builder builder) {
		this.builder = builder;
	}

	@Override
	public State parse(ByteBuffer buffer) {
		switch(transferEncoding()) {
		case CHUNKED:
			return new ChunkedBodyState(builder).parse(buffer);

		case IDENTITY:
		default:
			if(hasContentLength()) {
				return new IdentityBodyState(builder).parse(buffer);
			} else {
				return new ResultState(builder);
			}
		}
	}

	private boolean hasContentLength() {
		return builder.header(Header.CONTENT_LENGTH).any();
	}

	private TransferEncoding transferEncoding() {
		return builder.header(Header.TRANSFER_ENCODING).getOnlyElementWithDefault("identity").equalsIgnoreCase("chunked") ? TransferEncoding.CHUNKED : TransferEncoding.IDENTITY;
	}

	@Override
	public Request result() {
		return builder.build();
	}

}