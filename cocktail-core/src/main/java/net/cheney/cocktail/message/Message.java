package net.cheney.cocktail.message;

import java.io.FileNotFoundException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public abstract class Message implements Headers {
	
	public enum TransferEncoding {
		CHUNKED,
		IDENTITY
	}
	
	public abstract long contentLength();
	
	public boolean closeRequested() {
		// TODO - what about HTTP/1.0
		for(String value : header(Header.CONNECTION)) {
			if(value.equalsIgnoreCase("close")) return true;
		}
		return false;
	}
	
	public abstract Version version();
	
	public abstract ByteBuffer body();
	
	public abstract FileChannel channel() throws FileNotFoundException;
	
	public abstract boolean hasBody();
	
}
