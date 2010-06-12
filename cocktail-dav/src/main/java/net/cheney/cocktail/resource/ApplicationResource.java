package net.cheney.cocktail.resource;

import static net.cheney.cocktail.dav.Responses.clientErrorConflict;
import static net.cheney.cocktail.dav.Responses.clientErrorLocked;
import static net.cheney.cocktail.dav.Responses.clientErrorMethodNotAllowed;
import static net.cheney.cocktail.dav.Responses.clientErrorNotFound;
import static net.cheney.cocktail.dav.Responses.clientErrorPreconditionFailed;
import static net.cheney.cocktail.dav.Responses.clientErrorUnsupportedMediaType;
import static net.cheney.cocktail.dav.Responses.serverErrorInternal;
import static net.cheney.cocktail.dav.Responses.serverErrorNotImplemented;
import static net.cheney.cocktail.dav.Responses.successCreated;
import static net.cheney.cocktail.dav.Responses.successMultiStatus;
import static net.cheney.cocktail.dav.Responses.successNoContent;
import static net.cheney.cocktail.resource.Elements.multistatus;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import net.cheney.cocktail.application.Application;
import net.cheney.cocktail.application.Environment;
import net.cheney.cocktail.application.Environment.Depth;
import net.cheney.cocktail.application.Path;
import net.cheney.cocktail.message.Header;
import net.cheney.cocktail.message.Request.Method;
import net.cheney.cocktail.message.Response;
import net.cheney.cocktail.message.Response.Status;
import net.cheney.snax.model.QName;

public abstract class ApplicationResource extends Resource implements Application {

	private static final List<QName> ALL_PROPS = Arrays.asList(new QName[] {
			Property.CREATION_DATE, Property.DISPLAY_NAME,
			Property.GET_CONTENT_LENGTH, Property.GET_LAST_MODIFIED,
			Property.RESOURCE_TYPE });
	
	@Override
	public Response call(Environment env) {
		switch(env.method()) {
		case OPTIONS:
			return options(env);
			
		case PROPFIND:
			return doPropfind(env);
			
		case MKCOL:
			return mkcol(env);
			
		case COPY:
			try {
				return copy(env);
			} catch (IOException e) {
				return serverErrorInternal(e).call(env);
			}
			
		case MOVE:
			return move(env);
			
		case PUT:
			return put(env);
			
		case DELETE:
			return delete(env);
			
		case GET:
			return get(env);
		
		default:
			return serverErrorNotImplemented().call(env);
		}
	}

	private Response copy(Environment env) throws IOException {
		final Resource source = this;
		final Resource destination = providor().resolveResource(Path.fromURI(URI.create(env.header(Header.DESTINATION).getOnlyElement())));
		
		if (destination.isLocked()) {
			return clientErrorLocked().call(env);
		}
		
		boolean overwrite = env.header(Header.OVERWRITE).getOnlyElementWithDefault("T").equals("T");
		
		if (source.exists()) {
			if (source.isCollection()) { // source exists
				if (destination.exists()) { // source exists and is a collection
					if (destination.isCollection()) {
						if(overwrite) {
							source.copyTo(destination);
							return successNoContent().call(env);
						} else {
							return clientErrorPreconditionFailed().call(env);
						}
					} else {
						if(overwrite) {
							source.copyTo(destination.parent());
							return successCreated().call(env);
						} else {
							return clientErrorPreconditionFailed().call(env);
						}
					}
				} else {
					if(destination.parent().exists()) {
						source.copyTo(destination);
						return successNoContent().call(env);
					} else {
						return clientErrorPreconditionFailed().call(env);
					}
				}
			} else {
				if (destination.exists()) { // source exists
					if (destination.isCollection()) { // source exists,
						if (destination.isCollection()) {
							if(overwrite) {
								source.copyTo(destination);
								return successNoContent().call(env);
							} else {
								return clientErrorPreconditionFailed().call(env);
							}
						} else {
							if(overwrite) {
								source.copyTo(destination.parent());
								return successNoContent().call(env);
							} else {
								return clientErrorPreconditionFailed().call(env);
							}
						}
					}
				} else {
					if (destination.parent().exists()) {
						source.copyTo(destination);
						return successCreated().call(env);
					} else {
						return clientErrorConflict().call(env);
					}
				}
			}
		} 
		return clientErrorNotFound().call(env);
	}

	protected abstract ResourceProvidor providor();

	private Response get(Environment env) {
		try {
			return exists() ? Response.builder(Status.SUCCESS_OK).body(body()).build() : clientErrorNotFound().call(env);
		} catch (IOException e) {
			return serverErrorInternal().call(env);
		}
	}

	private Response delete(Environment env) {
//		if (fragment != null) {
//			return clientErrorMethodNotAllowed();
//		} else {
			if (isLocked()) {
				return clientErrorLocked().call(env);
			}
			if (exists()) {
				if (isLocked()) {
					return clientErrorLocked().call(env);
				} else {
					if (isCollection()) {
						return (delete() ? successNoContent() : serverErrorInternal()).call(env);
					} else {
						return (delete() ? successNoContent() : serverErrorInternal()).call(env);
					}
				}
			} else {
				return clientErrorNotFound().call(env);
			}
//		}
	}

	private Response put(Environment env) {
		if (!parent().exists()) {
			return clientErrorConflict().call(env);
		}

		if (exists() && isCollection()) {
			return clientErrorMethodNotAllowed().call(env);
		}

		try {
			parent().create(name(), env.body());
			return successCreated().call(env);
		} catch (IOException e) {
			return serverErrorInternal().call(env);
		}
		
	}

	private Response move(Environment env) {
		final Resource source = this;
		final Resource destination = providor().resolveResource(Path.fromURI(URI.create(env.header(Header.DESTINATION).getOnlyElement())));
		
		if (destination.isLocked()) {
			return clientErrorLocked().call(env);
		}
		
		boolean overwrite = env.header(Header.OVERWRITE).getOnlyElementWithDefault("T").equals("T");
		
		if (source.exists()) {
			if (source.isCollection()) { // source exists
				if (destination.exists()) { // source exists and is a collection
					if (destination.isCollection()) {
						if(overwrite) {
							source.moveTo(destination);
						} else {
							return clientErrorPreconditionFailed().call(env);
						}
					} else {
						if(overwrite) {
							source.moveTo(destination.parent());
						} else {
							return clientErrorPreconditionFailed().call(env);
						}
					}
				} else {
					if(destination.parent().exists()) {
						source.moveTo(destination);
					} else {
						return clientErrorPreconditionFailed().call(env);
					}
				}
			} else {
				if (destination.exists()) { // source exists
					if (destination.isCollection()) { // source exists,
						if (destination.isCollection()) {
							if(overwrite) {
								source.moveTo(destination);
							} else {
								return clientErrorPreconditionFailed().call(env);
							}
						} else {
							if(overwrite) {
								source.moveTo(destination.parent());
							} else {
								return clientErrorPreconditionFailed().call(env);
							}
						}
					}
				} else {
					if (destination.parent().exists()) {
						if(destination.parent().exists()) {
							source.moveTo(destination);
						} else {
							return clientErrorPreconditionFailed().call(env);
						}
					} else {
						return clientErrorConflict().call(env);
					}
				}
			}
		} 
		return clientErrorNotFound().call(env);
	}

	private Response mkcol(Environment env) {
		if (env.hasBody()) {
			return clientErrorUnsupportedMediaType().call(env);
		}
		if(exists()) {
			return clientErrorMethodNotAllowed().call(env);
		} else {
			Resource parent = parent();
			if (parent.exists()) {
				return (parent.makeCollection(name()) ? successCreated() : serverErrorInternal()).call(env);
			} else {
				return clientErrorConflict().call(env);
			}
		}
	}

	protected Response doPropfind(Environment env) {
		return (exists() ? propfind(env) : clientErrorNotFound()).call(env);
	}

	private Application propfind(Environment env) {
		Depth depth = Depth.parse(env.header(Header.DEPTH).getOnlyElementWithDefault(""), Depth.INFINITY);
		return successMultiStatus(multistatus(propfind(ALL_PROPS, this, depth)));
	}

	private List<Elements.RESPONSE> propfind(List<QName> searchProps, Resource resource, Depth depth) {
		final List<Elements.RESPONSE> responses = new ArrayList<Elements.RESPONSE>();
//		
//		responses.add(response(href(relativizeResource(resource)), getProperties(resource, properties)));
//		if (depth != Depth.ZERO) {
//			for (final Resource child : resource.members()) {
//				responses.addAll(propfind(properties, child, depth.decreaseDepth()));
//			}
//		}
		return responses;
	}

	protected Response options(Environment env) {
		Response.Builder builder = Response.builder(Status.SUCCESS_NO_CONTENT);
		
		for(Method method : supportedMethods()) {
			builder.header(Header.ALLOW).add(method.name());
		}
		
		builder.header(Header.DAV).add("1").add("2");
		
		return builder.build();
	}
}
