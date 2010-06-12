package net.cheney.cocktail.message;

import junit.framework.Assert;

import org.junit.Test;


public class VersionTest {

	@Test public void testVersions() {
		Assert.assertEquals(Version.HTTP_0_9, Version.parse("HTTP/0.9"));
		Assert.assertEquals(Version.HTTP_1_0, Version.parse("HTTP/1.0"));
		Assert.assertEquals(Version.HTTP_1_1, Version.parse("HTTP/1.1"));
	}
}
