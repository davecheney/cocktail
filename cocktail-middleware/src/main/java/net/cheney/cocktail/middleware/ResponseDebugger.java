package net.cheney.cocktail.middleware;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Writer;

import net.cheney.cocktail.application.Application;
import net.cheney.cocktail.application.Environment;
import net.cheney.cocktail.message.Response;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

public class ResponseDebugger implements Application {

	private final Application app;
	private final Writer logger;

	public ResponseDebugger(Application app, OutputStream out) {
		this(app, new PrintWriter(out));
	}

	public ResponseDebugger(Application app, Writer logger) {
		this.app = app;
		this.logger = logger;
	}

	@Override
	public Response call(Environment env) {
		return log(env, app.call(env));
	}

	private Response log(Environment env, Response response) {
		log(ReflectionToStringBuilder.toString(response, ToStringStyle.SIMPLE_STYLE));
		return response;
	}

	private void log(String line) {
		try {
			logger.append(line).flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
