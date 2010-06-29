package net.cheney.cocktail.io;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.GatheringByteChannel;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

import net.cheney.cocktail.io.socket.SocketChannelWriter;
import net.cheney.cocktail.io.socket.BufferVChannelWriter;
import net.cheney.cocktail.io.socket.SocketChannelRegistration;


public class Channel {
	
	public static Channel.Registration register(Selector selector, SocketChannel sc, int ops, Registration.Handler handler) throws IOException {
		return new SocketChannelRegistration(sc.register(selector, ops, handler));
	}

	public interface Registration {
		
		public interface Handler {

			void onReadyOps(int readyOps);

		}

		void close();

		void enableReadInterest();

		void enableWriteInterest();

		Reader reader() throws IOException;

		Writer writer();
	}
	
	public abstract static class Reader {

		public abstract ByteBuffer read() throws IOException;

		public abstract void shutdown() throws IOException;

	}
	
	public abstract static class Writer {

		private Channel.Writer next;
		protected final GatheringByteChannel channel;

		protected Writer(GatheringByteChannel channel) {
			this.channel = channel;
		}

		public static Channel.Writer forChannel(SocketChannel channel) {
			return new SocketChannelWriter(channel, ByteBuffer.allocate(0));
		}
		
		protected Channel.Writer next() {
			return next;
		}
		
		protected void setNext(Channel.Writer next) {
			this.next = next;
		}
		
		public Channel.Writer write(ByteBuffer buffer) throws IOException {
			return write(new SocketChannelWriter(channel, buffer));
		}
		
		public Channel.Writer write(ByteBuffer header, ByteBuffer body) throws IOException {
			return write(new BufferVChannelWriter(channel, header, body));
		}
			
		private Channel.Writer write(Channel.Writer next) throws IOException {
			Channel.Writer last = last();
			if(last.hasRemaning()) {
				last.setNext(next);
				return write();
			} else {
				return next.write();
			}
		}

		protected boolean hasNext() {
			return next != null;
		}

		public abstract Channel.Writer write() throws IOException;
		
		protected Channel.Writer writeMore() throws IOException {
			return hasRemaning() ? this : tryWriteNext();
		}
		
		private Channel.Writer tryWriteNext() throws IOException {
			return hasNext() ? next().write() : this;
		}

		private Channel.Writer last() {
			Channel.Writer w = this;
			while(w.hasNext()) {
				w = w.next();
			}
			return w;
		}

		public abstract boolean hasRemaning();
		
	}
}
