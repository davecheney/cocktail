package net.cheney.cocktail.message;

import java.io.IOException;
import java.nio.ByteBuffer;

public abstract class Message implements Headers {
	
	public enum TransferEncoding {
		NONE,
		IDENTITY
	}
	
	public long contentLength() throws IOException {
		return hasBody() ? body().remaining() : 0;
	}
	
	public boolean closeRequested() {
		// TODO - what about HTTP/1.0
		for(String value : header(Header.CONNECTION)) {
			if(value.equalsIgnoreCase("close")) return true;
		}
		return false;
	}
	
	public abstract Version version();
	
	public TransferEncoding transferCoding() {
		return TransferEncoding.NONE;
	}
	
	public abstract ByteBuffer body();
	
	public boolean hasBody() {
		return body() != null;
	}
	
}
