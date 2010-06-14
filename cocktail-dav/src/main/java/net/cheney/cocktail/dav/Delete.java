package net.cheney.cocktail.dav;

import java.io.IOException;

import net.cheney.cocktail.application.Environment;
import net.cheney.cocktail.message.Response;
import net.cheney.cocktail.resource.Resource;
import net.cheney.cocktail.resource.ResourceProvidor;

public class Delete extends BaseApplication {

	public Delete(ResourceProvidor providor) {
		super(providor);
	}

	@Override
	public Response call(Environment env) {
		Resource resource = resolveResource(env);
		if (resource.isLocked()) {
			return clientErrorLocked();
		}
		if (resource.exists()) {
			if (resource.isLocked()) {
				return clientErrorLocked();
			} else {
				return delete(resource);
			}
		} else {
			return clientErrorNotFound();
		}
	}

	private Response delete(Resource resource) {
		try {
			resource.delete();
			return successNoContent();
		} catch (IOException e) {
			return serverErrorInternal(e);
		}
	}

}
