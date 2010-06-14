package net.cheney.cocktail.dav;

import net.cheney.cocktail.application.Environment;
import net.cheney.cocktail.message.Response;
import net.cheney.cocktail.message.Response.Status;
import net.cheney.cocktail.resource.Elements;
import net.cheney.cocktail.resource.Elements.MULTISTATUS;
import net.cheney.cocktail.resource.Elements.PROPSTAT;
import net.cheney.cocktail.resource.Elements.RESPONSE;
import net.cheney.cocktail.resource.ResourceProvidor;
import net.cheney.snax.model.Document;
import net.cheney.snax.model.Element;

import org.apache.log4j.Logger;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;

public class Proppatch extends BaseApplication {
	private static final Logger LOG = Logger.getLogger(Proppatch.class);

	public Proppatch(ResourceProvidor providor) {
		super(providor);
	}

	@Override
	public Response call(Environment env) {
		Document document = bodyAsXML(env);
		if(document == null) {
			return clientErrorBadRequest();
		}
		Element propertyUpdate = document.rootElement();
		Iterable<Element> sets = propertyUpdate.getChildren(Elements.SET);
		Iterable<Element> removes = propertyUpdate.getChildren(Elements.REMOVE);
		RESPONSE response = Elements.response(Elements.href(env.path()), Iterables.concat(set(sets), remove(removes)));
		MULTISTATUS multistatus = Elements.multistatus(response);
		return successMultiStatus(multistatus);
	}

	private Iterable<PROPSTAT> remove(Iterable<Element> removes) {
		LOG.info(Iterables.toString(removes));
		return Iterables.transform(removes, new Function<Element, PROPSTAT>() {

			@Override
			public PROPSTAT apply(Element remove) {
				return remove(remove);
			}
			
		});
	}
	
	private Iterable<PROPSTAT> set(Iterable<Element> sets) {
		LOG.info(Iterables.toString(sets));
		return Iterables.transform(sets, new Function<Element, PROPSTAT>() {

			@Override
			public PROPSTAT apply(Element set) {
				return set(set);
			}
			
		});
	}

	private PROPSTAT set(Element set) {
		Element prop = set.getChildren(Elements.PROP).first().childElements().first();
		return Elements.propertyStatus(Elements.prop(new Element(prop.qname())), Status.SUCCESS_OK);
	}


	private PROPSTAT remove(Element remove) {
		Element prop = remove.getChildren(Elements.PROP).first().childElements().first();
		return Elements.propertyStatus(Elements.prop(new Element(prop.qname())), Status.SUCCESS_OK);
	}

}
