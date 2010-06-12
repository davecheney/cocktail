package net.cheney.cocktail.dav;

import java.io.File;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;

import net.cheney.cocktail.application.Application;
import net.cheney.cocktail.application.Environment;
import net.cheney.cocktail.message.Response;
import net.cheney.cocktail.resource.Property;
import net.cheney.cocktail.resource.ResourceProvidor;
import net.cheney.cocktail.resource.file.FileResourceProvidor;
import net.cheney.snax.model.QName;

public class DavApplication implements Application {

	private static final List<QName> ALL_PROPS = Arrays.asList(new QName[] {
			Property.CREATION_DATE, Property.DISPLAY_NAME,
			Property.GET_CONTENT_LENGTH, Property.GET_LAST_MODIFIED,
			Property.RESOURCE_TYPE });
	
	private static final Charset CHARSET_UTF_8 = Charset.forName("UTF-8");
	
	private final ResourceProvidor providor;

	public DavApplication(File root) {
		this.providor = new FileResourceProvidor(root);
	}
	
	@Override
	public Response call(Environment env) {
		Application app = providor.resolveResource(env.path());
		return app.call(env);
	}

}
