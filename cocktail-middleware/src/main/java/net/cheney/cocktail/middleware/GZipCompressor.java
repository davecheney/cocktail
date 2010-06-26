package net.cheney.cocktail.middleware;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.zip.GZIPOutputStream;

import net.cheney.cocktail.application.Application;
import net.cheney.cocktail.application.Environment;
import net.cheney.cocktail.message.Header;
import net.cheney.cocktail.message.Response;
import net.cheney.cocktail.message.Response.Status;

public class GZipCompressor implements Application {

	private final Application application;

	public GZipCompressor(Application application) {
		this.application = application;
	}
	
	@Override
	public Response call(Environment env) {
		Response response = application.call(env);
		if(response.hasBody()) {
			ByteBuffer body = response.body();
			Response.Builder builder = Response.builder(response.status());
			for(Header.Accessor accessor : response) {
				builder.header(accessor.header()).set(accessor);
			}
			try {
				return builder.body(compressBody(body)).build();
			} catch (IOException e) {
				return Response.builder(Status.SERVER_ERROR_INTERNAL).body(Charset.defaultCharset().encode(e.toString())).build();
			}
		} else {
			return response;
		}
	}

	private ByteBuffer compressBody(ByteBuffer body) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream(2^14);
		GZIPOutputStream gzip = new GZIPOutputStream(baos);
		gzip.write(body.array(), body.arrayOffset(), body.limit());
		return ByteBuffer.wrap(baos.toByteArray());
	}

}
