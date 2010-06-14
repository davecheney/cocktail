package net.cheney.cocktail.dav;

import java.io.IOException;
import java.net.URI;

import net.cheney.cocktail.application.Environment;
import net.cheney.cocktail.application.Path;
import net.cheney.cocktail.message.Header;
import net.cheney.cocktail.message.Response;
import net.cheney.cocktail.resource.Resource;
import net.cheney.cocktail.resource.ResourceProvidor;

import org.apache.log4j.Logger;

public class Copy extends BaseApplication {
	private static final Logger LOG = Logger.getLogger(Copy.class);

	public Copy(ResourceProvidor providor) {
		super(providor);
		// TODO Auto-generated constructor stub
	}

	@Override
	public Response call(Environment env) {
		final Resource source = resolveResource(env);
		final Resource destination = resolveResource(Path.fromURI(URI.create(env.header(Header.DESTINATION).getOnlyElement())));
		
		if (destination.isLocked()) {
			return clientErrorLocked();
		}
		
		boolean overwrite = env.header(Header.OVERWRITE).getOnlyElementWithDefault("T").equals("T");
		
		LOG.debug(String.format("COPY: src[%s], dest[%s], overwrite[%b]", source, destination, overwrite));
		
		try {
			return copy(source, destination, overwrite);
		} catch (IOException e) {
			return serverErrorInternal(e);
		}
	}

	private Response copy(Resource source, Resource destination, boolean overwrite) throws IOException {
		if (source.exists()) {
			if (source.isCollection()) { // source exists
				if (destination.exists()) { // source exists and is a collection
					if (destination.isCollection()) {
						if(overwrite) {
							destination.delete();
							source.copyTo(destination);
							return successNoContent();
						} else {
							return clientErrorPreconditionFailed();
						}
					} else {
						if(overwrite) {
							source.copyTo(destination.parent());
							return successCreated();
						} else {
							return clientErrorPreconditionFailed();
						}
					}
				} else {
					if(destination.parent().exists()) {
						source.copyTo(destination);
						return successNoContent();
					} else {
						return clientErrorPreconditionFailed();
					}
				}
			} else {
				if (destination.exists()) { // source exists
					if (destination.isCollection()) {
						if(overwrite) {
							source.copyTo(destination);
							return successNoContent();
						} else {
							return clientErrorPreconditionFailed();
						}
					} else {
						if(overwrite) {
							source.copyTo(destination);
							return successNoContent();
						} else {
							return clientErrorPreconditionFailed();
						}
					}
				} else {
					if (destination.parent().exists()) {
						source.copyTo(destination);
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
