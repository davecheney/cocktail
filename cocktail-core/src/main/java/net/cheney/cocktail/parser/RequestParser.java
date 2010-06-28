package net.cheney.cocktail.parser;

import java.nio.ByteBuffer;

import javax.annotation.Nonnull;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.apache.log4j.Logger;

import net.cheney.cocktail.message.Request;

public class RequestParser {
	
	private static final Logger LOG = Logger.getLogger(RequestParser.class);
	
	public interface State {

		@Nonnull State parse(@Nonnull ByteBuffer buffer);
		
		Request result();
	}

	private State state = new MethodState();
	
	public Request parse(ByteBuffer buffer) {
		this.state = state.parse(buffer);
		LOG.debug(String.format("Returning from parse(), state %s", state));
		return this.state.result();
	}
	
	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
}
