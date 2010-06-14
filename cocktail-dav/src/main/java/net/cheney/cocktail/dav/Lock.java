package net.cheney.cocktail.dav;

import static net.cheney.cocktail.resource.Elements.activeLock;
import static net.cheney.cocktail.resource.Elements.lockDiscovery;
import static net.cheney.cocktail.resource.Elements.prop;
import net.cheney.cocktail.application.Environment;
import net.cheney.cocktail.application.Environment.Depth;
import net.cheney.cocktail.message.Header;
import net.cheney.cocktail.message.Response;
import net.cheney.cocktail.message.Response.Status;
import net.cheney.cocktail.resource.Elements;
import net.cheney.cocktail.resource.Lock.Scope;
import net.cheney.cocktail.resource.Lock.Type;
import net.cheney.cocktail.resource.Resource;
import net.cheney.cocktail.resource.ResourceProvidor;
import net.cheney.snax.model.Document;
import net.cheney.snax.model.Element;
import net.cheney.snax.writer.XMLWriter;

public class Lock extends BaseApplication {

	public Lock(ResourceProvidor providor) {
		super(providor);
	}

	@Override
	public Response call(Environment env) {
		Document document = bodyAsXML(env);
		Resource resource = resolveResource(env);
		Depth depth = depth(env);
		
		Element lockinfo = document.childElements().first();
		Element scope = lockinfo.getChildren(Elements.LOCK_SCOPE).first();
		Element type = lockinfo.getChildren(Elements.LOCK_TYPE).first();
		Element owner = lockinfo.getChildren(Elements.OWNER).first();
		resource.lock();
		Response.Builder builder = Response.builder(Status.SUCCESS_OK);
		builder.header(Header.LOCK_TOKEN).add("<urn:uuid:e71d4fae-5dec-22d6-fea5-00a0c91e6be4>");
		Document response = new Document(prop(lockDiscovery(activeLock(lock(Type.WRITE, Scope.EXCLUSIVE, resource), depth, env.path()))));
		return builder.body(CHARSET_UTF_8.encode(XMLWriter.write(response))).build();
	}
	
	private net.cheney.cocktail.resource.Lock lock(net.cheney.cocktail.resource.Lock.Type type, net.cheney.cocktail.resource.Lock.Scope scope, Resource resource) {
		return new net.cheney.cocktail.resource.Lock(type, scope, resource);
	}

}
