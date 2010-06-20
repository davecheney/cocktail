package net.cheney.cocktail.httpsimple;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;

import net.cheney.cocktail.application.Application;

public class HttpServer {
	
	private static final Logger LOG = Logger.getLogger(HttpServer.class);

	private final List<ServerSocketChannel> channels;
	private final Application application;

	private HttpServer(List<ServerSocketChannel> channels, Application application) {
		this.channels = channels;
		this.application = application;
	}
	
	public void start() throws InterruptedException, IOException {
		start(1);
	}

	public void start(int nWorkers) throws InterruptedException, IOException {
		ExecutorService executorService = Executors.newFixedThreadPool(nWorkers);
		LOG.info("Started: "+executorService.invokeAll(createWorkerTasks(nWorkers)));
	}

	private Collection<? extends Callable<Void>> createWorkerTasks(int nWorkers) throws IOException {
		List<Callable<Void>> workers = new ArrayList<Callable<Void>>();
		for(int i = 0 ; i < nWorkers ; ++i) {
			workers.add(new HttpServer.HttpWorkerCallable());
		}
		return workers;
	}

	public static HttpServer.Builder builder(Application application) {
		return new HttpServer.Builder(application);
	}
	
	public static class Builder {
		
		private final List<ServerSocketChannel> addresses = new ArrayList<ServerSocketChannel>();
		private final Application application;

		public Builder(Application application) {
			this.application = application;
		}

		public Builder bind(SocketAddress address) throws IOException {
			ServerSocketChannel ssc = SelectorProvider.provider().openServerSocketChannel();
			ssc.socket().bind(address);
			ssc.configureBlocking(false);
			this.addresses.add(ssc);
			return this;
		}
		
		public HttpServer build() {
			return new HttpServer(addresses, application);
		}
	}
	
	public class HttpWorkerCallable implements Callable<Void> {

		private final Selector selector;

		public HttpWorkerCallable() throws IOException {
			this.selector = SelectorProvider.provider().openSelector();
			for(SelectableChannel channel : channels) {
				channel.register(selector, SelectionKey.OP_ACCEPT);
			}
		}
		
		@Override
		public Void call() throws Exception {
			try {
				call0();
				return null;
			} catch (Exception e) {
				e.printStackTrace();
				throw e;
			}
		}

		private void call0() throws Exception {
			for(;;) {
				Set<SelectionKey> keys = selectNow();
				for(SelectionKey key : keys) {
					try {
						handleKey(key);
					} catch (IllegalArgumentException e) {
						key.channel().close();
						key.cancel();
					}
				}
				keys.clear();
			}			
		}

		private void handleKey(SelectionKey key) throws IOException {
			if(key.isValid()) {
				switch(key.readyOps()) {
				case SelectionKey.OP_ACCEPT:
					SocketChannel sc = ((ServerSocketChannel)key.channel()).accept();
					if(sc != null) {
						sc.configureBlocking(false);
						new HttpConnection(sc, selector, application);
					}
					break;
				
				case SelectionKey.OP_READ:
				case SelectionKey.OP_WRITE:
				case SelectionKey.OP_READ|SelectionKey.OP_WRITE:
					key.interestOps(key.interestOps() & (~key.readyOps()));
					((HttpConnection)key.attachment()).onReadyOps(key.readyOps());
					break;
				
				default:
					throw new IllegalStateException();
				}
			} else {
				LOG.warn(key);
			}			
		}

		private Set<SelectionKey> selectNow() throws IOException {
			return selector.select() > 0 ? selector.selectedKeys() : Collections.<SelectionKey>emptySet();
		}

	}

}
