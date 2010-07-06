package net.cheney.cocktail.middleware;

import static java.lang.String.format;

import net.cheney.cocktail.application.Application;
import net.cheney.cocktail.application.Environment;
import net.cheney.cocktail.message.Response;

import org.apache.commons.lang.time.FastDateFormat;
import org.apache.log4j.Logger;

public class CommonLogger implements Application {
	
	private final Application app;
	private final Logger logger;
	
	private final String NCSA_FORMAT = "%s - %s [%s] \"%s %s%s %s\" %d %s %d";
	private final FastDateFormat DATE_FORMAT = FastDateFormat.getInstance("dd/mm/yy HH:MM:ss");

	public CommonLogger(Application app, Logger logger) {
		this.app = app;
		this.logger = logger;
	}
	
	@Override
	public Response call(Environment env) {
		long begin = System.currentTimeMillis();
		Response response = app.call(env);
		log(env, response, begin);
		return response;
	}

	private void log(Environment env, Response response, long begin) {
		long finish = System.currentTimeMillis();
		logger.info(format(NCSA_FORMAT, 
				"-", // remote addr
				"-", // user
				DATE_FORMAT.format(finish),
				env.method(),
				env.path(),
				"", // query string
				env.version(),
				response.status().code(),
				getContentLength(response),
				finish - begin
			));
	}

	private long getContentLength(Response response) {
		return response.contentLength();
	}

}
