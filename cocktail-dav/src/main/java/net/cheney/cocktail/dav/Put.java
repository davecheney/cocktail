package net.cheney.cocktail.dav;

import java.io.IOException;
import java.nio.ByteBuffer;

import net.cheney.cocktail.application.Environment;
import net.cheney.cocktail.message.Response;
import net.cheney.cocktail.resource.Resource;
import net.cheney.cocktail.resource.ResourceProvidor;

public class Put extends BaseApplication {

	public Put(ResourceProvidor providor) {
		super(providor);
	}

	@Override
	public Response call(Environment env) {
		Resource resource = resolveResource(env);
		if (resource.exists() && resource.isCollection()) {
			return clientErrorMethodNotAllowed();
		}

		Resource parent = resource.parent();
		if (!parent.exists()) {
			return clientErrorConflict();
		}
		
		try {
			parent.create(resource.name(), (ByteBuffer) env.body().flip());
			return successCreated();
		} catch (IOException e) {
			return serverErrorInternal(e);
		}
	}

}