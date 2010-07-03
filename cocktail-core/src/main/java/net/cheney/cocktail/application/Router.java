package net.cheney.cocktail.application;

import javax.annotation.Nonnull;

public interface Router {

	/**
	 * 
	 * @param env The current {@link Environment}
	 * @return an implementation of {@link Application} which can respond to #call 
	 */
	@Nonnull Application route(@Nonnull Environment env);
}
