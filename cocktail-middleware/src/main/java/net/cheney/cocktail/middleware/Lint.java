package net.cheney.cocktail.middleware;

import net.cheney.cocktail.application.Application;
import net.cheney.cocktail.application.Environment;
import net.cheney.cocktail.message.Response;

public class Lint implements Application {

	private final Application app;

	public Lint(Application app) {
		this.app = app;
	}
	
	@Override
	public Response call(Environment env) {
		return lint(app.call(env));
	}

	private Response lint(Response response) {
		// TODO Auto-generated method stub
		return null;
	}

}
