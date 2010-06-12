package net.cheney.cocktail.resource;

import java.net.URI;

import net.cheney.cocktail.application.Path;

public interface ResourceProvidor extends LockManagerProvidor {

	ApplicationResource resolveResource(Path path);

	URI relativizeResource(Resource resource);
	
}
