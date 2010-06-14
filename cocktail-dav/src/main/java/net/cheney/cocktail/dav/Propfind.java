package net.cheney.cocktail.dav;

import static net.cheney.cocktail.resource.Elements.href;
import static net.cheney.cocktail.resource.Elements.multistatus;
import static net.cheney.cocktail.resource.Elements.prop;
import static net.cheney.cocktail.resource.Elements.propertyStatus;
import static net.cheney.cocktail.resource.Elements.response;

import java.util.ArrayList;
import java.util.List;

import net.cheney.cocktail.application.Environment;
import net.cheney.cocktail.application.Environment.Depth;
import net.cheney.cocktail.application.Path;
import net.cheney.cocktail.message.Response;
import net.cheney.cocktail.message.Response.Status;
import net.cheney.cocktail.resource.Elements;
import net.cheney.cocktail.resource.Elements.PROPSTAT;
import net.cheney.cocktail.resource.Elements.RESPONSE;
import net.cheney.cocktail.resource.Property;
import net.cheney.cocktail.resource.Resource;
import net.cheney.cocktail.resource.ResourceProvidor;
import net.cheney.snax.model.Document;
import net.cheney.snax.model.Element;
import net.cheney.snax.model.QName;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

public class Propfind extends BaseApplication {
	
	private static final Iterable<QName> ALL_PROPS = Lists.newArrayList(
			Property.CREATION_DATE, Property.DISPLAY_NAME,
			Property.GET_CONTENT_LENGTH, Property.GET_LAST_MODIFIED,
			Property.RESOURCE_TYPE);

	public Propfind(ResourceProvidor providor) {
		super(providor);
	}

	@Override
	public Response call(Environment env) {
		Depth depth = depth(env);
		Resource resource = resolveResource(env);
		try {
			Iterable<QName> properties = getProperties(env);
			List<RESPONSE> propfind = propfind(properties, resource, depth);
			return successMultiStatus(multistatus(propfind));
		} catch (IllegalArgumentException e) {
			return clientErrorBadRequest();
		}
	}
	
	private final Iterable<QName> getProperties(final Environment env) {
		final Document doc = bodyAsXML(env);
		if(doc == null) {
			throw new IllegalArgumentException();
		}
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
	
	private List<Elements.RESPONSE> propfind(Iterable<QName> searchProps, Resource resource, Depth depth) {
		List<Elements.RESPONSE> responses = new ArrayList<Elements.RESPONSE>();
		
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
	
}
