package net.cheney.cocktail.dav;

import java.io.IOException;

import net.cheney.cocktail.application.Environment;
import net.cheney.cocktail.application.Path;
import net.cheney.cocktail.message.Header;
import net.cheney.cocktail.message.Response;
import net.cheney.cocktail.resource.Resource;
import net.cheney.cocktail.resource.ResourceProvidor;

import org.apache.log4j.Logger;

public class Move extends BaseApplication {
	private static final Logger LOG = Logger.getLogger(Move.class);

	public Move(ResourceProvidor providor) {
		super(providor);
	}

	@Override
	public Response call(Environment env) {
		final Resource source = resolveResource(env);
		
		final Resource destination = resolveResource(Path.fromURI(destination(env)));
		
		if (destination.isLocked()) {
			return clientErrorLocked();
		}
		
		boolean overwrite = env.header(Header.OVERWRITE).getOnlyElementWithDefault("T").equals("T");
		
		LOG.debug(String.format("MOVE: src[%s], dest[%s], overwrite[%b]", source, destination, overwrite));
		try {
			return move(source, destination, overwrite);
		} catch (IOException e) {
			return serverErrorInternal(e);
		}
	}

	private Response move(Resource source, Resource destination, boolean overwrite) throws IOException {
		if (source.exists()) {
			if (source.isCollection()) { // source exists
				if (destination.exists()) { // source exists and is a collection
					if (destination.isCollection()) {
						if(overwrite) {
							source.moveTo(destination);
							return successNoContent();
						} else {
							return clientErrorPreconditionFailed();
						}
					} else {
						if(overwrite) {
							source.moveTo(destination);
							return successCreated();
						} else {
							return clientErrorPreconditionFailed();
						}
					}
				} else {
					if(destination.parent().exists()) {
						source.moveTo(destination);
						return successNoContent();
					} else {
						return clientErrorPreconditionFailed();
					}
				}
			} else {
				if (destination.exists()) { // source exists
					if (destination.isCollection()) { // source exists,
						if(overwrite) {
							source.moveTo(destination);
							return successNoContent();
						} else {
							return clientErrorPreconditionFailed();
						}
					} else {
						if(overwrite) {
							source.moveTo(destination);
							return successNoContent();
						} else {
							return clientErrorPreconditionFailed();
						}
					}
				} else {
					if (destination.parent().exists()) {
						source.moveTo(destination);
						return successCreated();
					} else {
						return clientErrorConflict();
					}
				}
			}
		} 
		return clientErrorNotFound();
	}

}
