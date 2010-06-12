package net.cheney.cocktail.httpsimple;

import java.io.File;
import java.io.IOException;

import net.cheney.cocktail.application.Application;
import net.cheney.cocktail.application.Environment;
import net.cheney.cocktail.message.Response;
import net.cheney.cocktail.message.Response.Status;

public class FileServlet implements Application {

	@Override
	public Response call(Environment env) {
		File file = new File("/Users/dave/Public/HPL-2001-314.ps");
		try {
			return Response.builder(Status.SUCCESS_OK).body(file).build();
		} catch (IOException ioe) {
			return Response.builder(Status.SERVER_ERROR_INTERNAL).build();
		}
	}

}
