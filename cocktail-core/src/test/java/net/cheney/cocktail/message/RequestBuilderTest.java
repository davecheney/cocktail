package net.cheney.cocktail.message;

import java.net.URI;

import junit.framework.Assert;

import net.cheney.cocktail.message.Request.Method;

import org.junit.Test;

import com.google.common.collect.Iterables;


public class RequestBuilderTest {

	@Test public void testRemoveHeader() {
		RequestLine requestLine = RequestLine.builder().method(Method.GET).uri(URI.create("/")).version(Version.HTTP_1_1).build();
		Request.Builder builder = Request.builder(requestLine);
		builder.header(Header.TRANSFER_ENCODING).add("chunked");
		builder.header(Header.TRANSFER_ENCODING).delete();
		Request request = builder.build();
		Assert.assertFalse(request.header(Header.TRANSFER_ENCODING).any());
		Assert.assertFalse(Iterables.contains(request.keys(), Header.TRANSFER_ENCODING));
	}
}
