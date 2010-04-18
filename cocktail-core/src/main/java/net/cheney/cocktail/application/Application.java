package net.cheney.cocktail.application;

import javax.annotation.Nonnull;

import net.cheney.cocktail.message.Response;

public interface Application {

	/**
	 * 
	 * @param env The environment for this request
	 * @return a @Response object, this cannot be null
	 */
	@Nonnull Response call(@Nonnull Environment env);
}
