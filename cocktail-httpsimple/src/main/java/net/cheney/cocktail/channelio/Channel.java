package net.cheney.cocktail.channelio;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.GatheringByteChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;


public class Channel {
	
	public static Channel.Registration register(Selector selector, SocketChannel sc, int interstOps, ReadyOperationHandler handler) throws IOException {
		return new Registration(selector, sc, interstOps, handler);
	}

	public static class Registration {

		private final SelectionKey key;

		private Registration(Selector selector, SocketChannel sc, int interstOps, ReadyOperationHandler handler) throws IOException {
			key = sc.register(selector, SelectionKey.OP_READ, handler);
		}

		public void close() {
			try {
				key.channel().close();
				key.cancel();
			} catch (IOException ignored) { }
		}

		private void enableInterest(int ops) {
			key.interestOps(key.interestOps() | ops);
		}
		
		public void enableReadInterest() {
			enableInterest(SelectionKey.OP_READ);
		}
		
		public void enableWriteInterest() {
			enableInterest(SelectionKey.OP_WRITE);
		}

		public Channel.Reader reader() throws IOException {
			return new ChannelReader(channel());
		}

		private SocketChannel channel() {
			return (SocketChannel) key.channel();
		}

		public Channel.Writer writer() {
			return 	Channel.Writer.forChannel(channel());
		}

	}
	
	public abstract static class Reader {

		public abstract ByteBuffer read() throws IOException;

	}
	
	public abstract static class Writer {

		private Channel.Writer next;
		protected final GatheringByteChannel channel;

		protected Writer(GatheringByteChannel channel) {
			this.channel = channel;
		}

		public static Channel.Writer forChannel(GatheringByteChannel channel) {
			return new NullChannelWriter(channel);
		}
		
		protected Channel.Writer next() {
			return next;
		}
		
		protected void setNext(Channel.Writer next) {
			this.next = next;
		}
		
		public Channel.Writer write(ByteBuffer buffer) throws IOException {
			return write(new BufferChannelWriter(channel, buffer));
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

		public Channel.Writer write(FileChannel src, long count) throws IOException {
			return write(new GatheringByteChannelWriter(channel, src, count));
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
