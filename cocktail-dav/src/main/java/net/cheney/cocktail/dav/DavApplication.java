package net.cheney.cocktail.dav;

import static net.cheney.cocktail.dav.Responses.serverErrorNotImplemented;

import java.io.File;

import net.cheney.cocktail.application.Application;
import net.cheney.cocktail.application.Environment;
import net.cheney.cocktail.message.Request.Method;
import net.cheney.cocktail.message.Response;
import net.cheney.cocktail.resource.ResourceProvidor;
import net.cheney.cocktail.resource.file.FileResourceProvidor;

public class DavApplication implements Application {

	private final ResourceProvidor providor;

	public DavApplication(File root) {
		this.providor = new FileResourceProvidor(root);
	}
	
	public Response call(Environment env) {
		return forMethod(env.method()).call(env);
	}

	private Application forMethod(Method method) {
		switch(method) {
		case OPTIONS:
			return new Options(providor);
			
		case PROPFIND:
			return new Propfind(providor);
			
		case PROPPATCH:
			return new Proppatch(providor);
			
		case MKCOL:
			return new Mkcol(providor);
			
		case COPY:
			return new Copy(providor);
			
		case MOVE:
			return new Move(providor);
			
		case PUT:
			return new Put(providor);
			
		case DELETE:
			return new Delete(providor);
			
		case GET:
			return new Get(providor);
			
		case LOCK:
			return new Lock(providor);
		
		default:
			return serverErrorNotImplemented();
		}
	}


}
