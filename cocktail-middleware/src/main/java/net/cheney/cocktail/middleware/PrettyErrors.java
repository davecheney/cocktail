package net.cheney.cocktail.middleware;

import java.nio.charset.Charset;

import org.apache.commons.lang.StringUtils;

import net.cheney.cocktail.application.Application;
import net.cheney.cocktail.application.Environment;
import net.cheney.cocktail.message.Response;
import net.cheney.cocktail.message.Response.Builder;
import net.cheney.cocktail.message.Response.Status;

public class PrettyErrors implements Application {

	private static final Charset CHARSET_UTF8 = Charset.forName("utf-8");
	
	private final Application app;

	public PrettyErrors(Application app) {
		this.app = app;
	}
	
	@Override
	public Response call(Environment env) {
		try {
			return app.call(env);
		} catch (RuntimeException e) {
			return exception(e);
		}
	}

	private Response exception(RuntimeException e) {
		Builder builder = Response.builder(Status.SERVER_ERROR_INTERNAL);
		StringBuilder sb = new StringBuilder(1024);
		sb.append(String.format("<html><head><title>%s</title></head><body>",e.toString()));
		sb.append(String.format("<h1>%s<h1>", e.toString()));
		sb.append(String.format("%s", StringUtils.join(e.getStackTrace(),"<br/>")));
		sb.append(String.format("</body></html>"));
		builder.body(CHARSET_UTF8.encode(sb.toString()));
		return builder.build();
	}
}
