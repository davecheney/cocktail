package net.cheney.cocktail.application;

import java.net.URI;

import junit.framework.Assert;

import org.junit.Test;


public class PathTest {

	@Test public void testRootPath() {
		Path expected = Path.emptyPath();
		Path actual = Path.fromURI(URI.create("http://localhost/"));
		Assert.assertEquals(expected, actual);
	}
	
	@Test public void testSingleElement() {
		Path expected = Path.create("child");
		Path actual = Path.fromURI(URI.create("http://localhost/child"));
		Assert.assertEquals(expected, actual);
	}
	
	@Test public void testMultipleElements() {
		Path expected = Path.create("child", "child");
		Path actual = Path.fromURI(URI.create("http://localhost/child/child"));
		Assert.assertEquals(expected, actual);
	}
	
	@Test public void testTrailingSlash() {
		Path expected = Path.create("child", "child");
		Path actual = Path.fromURI(URI.create("http://localhost/child/child/"));
		Assert.assertEquals(expected, actual);
	}
}
