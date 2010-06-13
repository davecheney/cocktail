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
import static net.cheney.cocktail.resource.Elements.href;
import static net.cheney.cocktail.resource.Elements.multistatus;
import static net.cheney.cocktail.resource.Elements.prop;
import static net.cheney.cocktail.resource.Elements.propertyStatus;
import static net.cheney.cocktail.resource.Elements.response;

import java.io.IOException;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;

import net.cheney.cocktail.application.Application;
import net.cheney.cocktail.application.Environment;
import net.cheney.cocktail.application.Environment.Depth;
import net.cheney.cocktail.application.Path;
import net.cheney.cocktail.dav.Responses;
import net.cheney.cocktail.message.Header;
import net.cheney.cocktail.message.Request.Method;
import net.cheney.cocktail.message.Response;
import net.cheney.cocktail.message.Response.Status;
import net.cheney.cocktail.resource.Elements.PROPSTAT;
import net.cheney.snax.SNAX;
import net.cheney.snax.model.Document;
import net.cheney.snax.model.Element;
import net.cheney.snax.model.QName;
import net.cheney.snax.writer.XMLWriter;

public abstract class ApplicationResource extends Resource implements Application {
	
	private static final Logger LOG = Logger.getLogger(ApplicationResource.class);

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
			
		case PROPPATCH:
			return doPropPatch(env);
			
		case MKCOL:
			return mkcol(env);
			
		case COPY:
			try {
				return copy(env);
			} catch (IOException e) {
				return serverErrorInternal(e).call(env);
			}
			
		case MOVE:
			try {
				return move(env);
			} catch (IOException e) {
				return serverErrorInternal(e).call(env);
			}
			
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

	private Response doPropPatch(Environment env) {
		return doPropfind(env);
	}

	private Response copy(Environment env) throws IOException {
		final Resource source = this;
		final Resource destination = providor().resolveResource(Path.fromURI(URI.create(env.header(Header.DESTINATION).getOnlyElement())));
		
		if (destination.isLocked()) {
			return clientErrorLocked().call(env);
		}
		
		boolean overwrite = env.header(Header.OVERWRITE).getOnlyElementWithDefault("T").equals("T");
		
		LOG.debug(String.format("COPY: src[%s], dest[%s], overwrite[%b]", source, destination, overwrite));
		
		if (source.exists()) {
			if (source.isCollection()) { // source exists
				if (destination.exists()) { // source exists and is a collection
					if (destination.isCollection()) {
						if(overwrite) {
							destination.delete();
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
					if (destination.isCollection()) {
						if(overwrite) {
							source.copyTo(destination);
							return successNoContent().call(env);
						} else {
							return clientErrorPreconditionFailed().call(env);
						}
					} else {
						if(overwrite) {
							source.copyTo(destination);
							return successNoContent().call(env);
						} else {
							return clientErrorPreconditionFailed().call(env);
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
			return serverErrorInternal(e).call(env);
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
						return (delete() ? successNoContent() : serverErrorInternal(new IOException("Cannot delete"))).call(env);
					} else {
						return (delete() ? successNoContent() : serverErrorInternal(new IOException("Cannot delete"))).call(env);
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
			return serverErrorInternal(e).call(env);
		}
		
	}

	private Response move(Environment env) throws IOException {
		final Resource source = this;
		final Resource destination = providor().resolveResource(Path.fromURI(URI.create(env.header(Header.DESTINATION).getOnlyElement())));
		
		if (destination.isLocked()) {
			return clientErrorLocked().call(env);
		}
		
		boolean overwrite = env.header(Header.OVERWRITE).getOnlyElementWithDefault("T").equals("T");
		
		LOG.debug(String.format("MOVE: src[%s], dest[%s], overwrite[%b]", source, destination, overwrite));
		
		if (source.exists()) {
			if (source.isCollection()) { // source exists
				if (destination.exists()) { // source exists and is a collection
					if (destination.isCollection()) {
						if(overwrite) {
							source.moveTo(destination);
							return successNoContent().call(env);
						} else {
							return clientErrorPreconditionFailed().call(env);
						}
					} else {
						if(overwrite) {
							source.moveTo(destination);
							return successCreated().call(env);
						} else {
							return clientErrorPreconditionFailed().call(env);
						}
					}
				} else {
					if(destination.parent().exists()) {
						source.moveTo(destination);
						return successNoContent().call(env);
					} else {
						return clientErrorPreconditionFailed().call(env);
					}
				}
			} else {
				if (destination.exists()) { // source exists
					if (destination.isCollection()) { // source exists,
						if(overwrite) {
							source.moveTo(destination);
							return successNoContent().call(env);
						} else {
							return clientErrorPreconditionFailed().call(env);
						}
					} else {
						if(overwrite) {
							source.moveTo(destination);
							return successNoContent().call(env);
						} else {
							return clientErrorPreconditionFailed().call(env);
						}
					}
				} else {
					if (destination.parent().exists()) {
						source.moveTo(destination);
						return successCreated().call(env);
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
				return (parent.makeCollection(name()) ? successCreated() : serverErrorInternal(new IOException("Cannot create"))).call(env);
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
		try {
			return successMultiStatus(multistatus(propfind(getProperties((ByteBuffer) env.body().flip()), this, depth)));
		} catch (IllegalArgumentException e) {
			return Responses.clientErrorBadRequest();
		}
	}

	private List<Elements.RESPONSE> propfind(Iterable<QName> searchProps, Resource resource, Depth depth) {
		final List<Elements.RESPONSE> responses = new ArrayList<Elements.RESPONSE>();
		
		responses.add(response(href(Path.create(resource.name())), getProperties(resource, searchProps)));
		if (depth != Depth.ZERO) {
			for (final Resource child : resource.children()) {
				responses.addAll(propfind(searchProps, child, depth.decreaseDepth()));
			}
		}
		return responses;
	}

	private final List<PROPSTAT> getProperties(final Resource resource, final Iterable<QName> properties) {
		final List<PROPSTAT> propstats = new ArrayList<PROPSTAT>(2);
		final List<Element> foundProps = new ArrayList<Element>();
		final List<Element> notFoundProps = new ArrayList<Element>();
		for (final QName property : properties) {
			final Element prop = resource.property(property);
			if(prop == null) {
				notFoundProps.add(new Element(property));
			} else {
				foundProps.add(prop);
			}
		}
		if(!foundProps.isEmpty()) {
			propstats.add(propertyStatus(prop(foundProps), Status.SUCCESS_OK));
		} 
		if(!notFoundProps.isEmpty()) {
			propstats.add(propertyStatus(prop(notFoundProps), Status.CLIENT_ERROR_NOT_FOUND));
		}
		return propstats;
	}

	private final Iterable<QName> getProperties(final ByteBuffer buffer) {
		final Document doc = getPropfind(buffer);
		final Element propfind = doc.rootElement();
		if(propfind == null) {
			throw new IllegalArgumentException();
		}
		final Element props = propfind.getChildren(Elements.PROP).first();
		if (props == null || !props.hasChildren()) {
			return ALL_PROPS;
		} else {
			return Iterables.transform(props.childElements(), new Function<Element, QName>() {
				@Override
				public QName apply(Element property) {
					return property.qname();
				}
			});
		}
	}
	
	private final Document getPropfind(final ByteBuffer buffer) {
		Document document = SNAX.parse(Charset.forName("UTF-8").decode(buffer));
		LOG.debug("Request Body: "+XMLWriter.write(document));
		return document;
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
