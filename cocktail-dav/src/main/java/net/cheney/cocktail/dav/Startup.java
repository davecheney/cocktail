package net.cheney.cocktail.dav;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

import net.cheney.cocktail.application.Application;
import net.cheney.cocktail.httpsimple.HttpServer;
import net.cheney.cocktail.middleware.CommonLogger;
import net.cheney.cocktail.middleware.ResponseDebugger;

public class Startup {
	
	public static void main(String[] args) throws InterruptedException, IOException {
		BasicConfigurator.configure();
		
		File root = new File("/tmp/dav");
		root.mkdir();
		Application dav = new DavApplication(root);
		dav = new CommonLogger(dav, Logger.getRootLogger());
		dav = new ResponseDebugger(dav, System.out);
		
		HttpServer.builder(dav).bind(new InetSocketAddress(InetAddress.getLocalHost(), 8080)).build().start(4);
	}
}
