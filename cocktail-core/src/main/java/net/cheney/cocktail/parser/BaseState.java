package net.cheney.cocktail.parser;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.nio.charset.Charset;

import net.cheney.cocktail.message.Request;
import net.cheney.cocktail.parser.RequestParser.State;

abstract class BaseState implements State {
	
	static final Charset US_ASCII = Charset.forName("US-ASCII");
	static final Charset UTF_8 = Charset.forName("UTF-8");
	
	private final int OVERFLOW_LIMIT = 1048576; // 1Mb

	@Override
	public Request result() {
		return null;
	}
	
	final boolean isWhitespace(byte t) {
		return (t == ' ' | t == '\t');
	}

	final boolean isVisibleCharacter(byte t) {
		return (t >= '\u0021' && t <= '\u007E');
	}

	final boolean isTokenChar(byte c) {
		return ((c >= '\u0030' && c <= '\u0039')
				|| (c >= '\u0041' && c <= '\u005A')
				|| (c >= '\u0061' && c <= '\u007a') || c == '!' || c == '#'
				|| c == '$' || c == '%' || c == '&' || c == '\'' || c == '*'
				|| c == '+' || c == '-' || c == '.' || c == '^' || c == '_'
				|| c == '`' || c == '|' || c == '~');
	}
	
	final State panic(ByteBuffer buffer) {
		throw new IllegalArgumentException(String.format("%#x [%s]", buffer.get(buffer.position() -1), (char)buffer.get(buffer.position() -1)));
	}
	
	final String stringValue(ByteBuffer buffer, int offset) {
		int length = buffer.position() - offset;
		return new String(buffer.array(), buffer.arrayOffset() + offset, --length, US_ASCII);
	}
	
	final ByteBuffer createBodyBuffer(long contentLength) {
		if (contentLength < OVERFLOW_LIMIT) {
			return ByteBuffer.allocate((int)contentLength);
		} else {
			return mmapTemporaryBuffer(contentLength);
		}
	}

	private ByteBuffer mmapTemporaryBuffer(long contentLength) {
		try {
			File tmpFile = File.createTempFile("upload", null);
			RandomAccessFile raf = new RandomAccessFile(tmpFile, "rw");
			tmpFile.delete();
			FileChannel channel = raf.getChannel();
			ByteBuffer buffer = channel.map(MapMode.READ_WRITE, 0,
					contentLength);
			if (buffer.remaining() != contentLength) {
				throw new RuntimeException(String.format(
						"Buffer %s was not allocated for %d ", buffer,
						contentLength));
			}
			return buffer;
		} catch (IOException e) {
			throw new RuntimeException(String.format(
					"Could not allocate temporary file for content %d",
					contentLength), e);
		}
	}
}
