package net.cheney.cocktail.resource;

import javax.annotation.Nonnegative;

public interface Getable {

	@Nonnegative long contentLength();
	
}
