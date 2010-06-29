package net.cheney.cocktail.parser;

import net.cheney.cocktail.parser.RequestParser.State;

import org.junit.Assert;
import org.junit.Test;

public class VersionStateTest extends BaseStateTest {
	
	@Test public void testVersionParser() {
		State result = new MethodState().parse(buffer("GET /foo HTTP/1.1"));
		Assert.assertTrue(result.toString(), result instanceof VersionState);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testMethodParserInvalid() {
		State result = new MethodState().parse(buffer("GET /foo VERSION/1.1"));
		Assert.assertTrue(result.toString(), result instanceof VersionState);
	}
}