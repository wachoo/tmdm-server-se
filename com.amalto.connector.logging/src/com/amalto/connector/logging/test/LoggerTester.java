
package com.amalto.connector.logging.test;

import org.apache.log4j.Layout;
import org.apache.log4j.Logger;
import org.apache.log4j.Priority;
import org.apache.log4j.PropertyConfigurator;

import com.amalto.connector.jca.XtentisConnectorException;
import com.amalto.connector.logging.eis.LoggingListener;


/**
 * 
 * @author jfeltesse
 */
public class LoggerTester {
	
	
	
	public static void main(String args[]) throws Exception {
		
		if (args.length != 1) {
			System.out.println("Usage : java LoggerTester [port]");
			System.exit(1);
		}
		
		
		int port = Integer.parseInt(args[0]);
		
		PropertyConfigurator.configure("/opt/workspace/com.amalto.connector.logging/log4j.properties");
		
		Package p = Layout.class.getPackage(); 
		System.out.println(p); 
		Logger.getLogger("LoggerTester.class").info("Implementation title:   "+p.getImplementationTitle()); 
		Logger.getLogger("LoggerTester.class").info("Implementation vendor:  "+p.getImplementationVendor()); 
		Logger.getLogger("LoggerTester.class").info("Implementation version: "+p.getImplementationVersion()); 
		
		/*
		 * Log4j Levels
		 * OFF > FATAL > ERROR > WARN > INFO > DEBUG > TRACE > ALL
		 * 
		 * OFF_INT = Integer.MAX_VALUE;
		 * FATAL_INT = 50000;
		 * ERROR_INT = 40000;
		 * WARN_INT = 30000;
		 * INFO_INT = 20000;
		 * DEBUG_INT = 10000;
		 * TRACE_INT = 5000;	// Since 1.2.12
		 * ALL_INT = Integer.MIN_VALUE;
		 */
		
		// com\\.amalto\\..*
		
		LoggingListener listener = new LoggingListener(
				null, 
				port, 
				Priority.ALL_INT, 
				"com\\.amalto\\..*", 
				null, 
				null, 
				null,
				null
		);
		Thread t = new Thread(listener, "listener");
		Logger.getLogger("LoggerTester.class").debug("Starting the listener");
		t.start();		
		if (listener.getStatus() == LoggingListener.STATUS_ERROR) throw new XtentisConnectorException(
				"An error occured during startup: "+listener.getError()
		);
		
		
	}
	
}