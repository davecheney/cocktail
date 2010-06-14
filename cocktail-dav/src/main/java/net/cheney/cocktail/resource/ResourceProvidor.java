package net.cheney.cocktail.resource;

import java.net.URI;

import net.cheney.cocktail.application.Path;

public interface ResourceProvidor extends LockManagerProvidor {

	Resource resolveResource(Path path);

	URI relativizeResource(Resource resource);
	
}
