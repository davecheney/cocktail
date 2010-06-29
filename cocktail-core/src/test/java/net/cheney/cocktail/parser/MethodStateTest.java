package net.cheney.cocktail.parser;

import net.cheney.cocktail.parser.RequestParser.State;

import org.junit.Assert;
import org.junit.Test;

public class MethodStateTest extends BaseStateTest {
	
	@Test public void testMethodParser() {
		State result = new MethodState().parse(buffer("GET "));
		Assert.assertTrue(result.toString(), result instanceof URIState);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testMethodParserInvalid() {
		State result = new MethodState().parse(buffer("get "));
		Assert.assertTrue(result.toString(), result instanceof URIState);
	}
}
