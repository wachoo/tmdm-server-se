package com.amalto.connector.logging.eis;

import java.io.EOFException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.resource.spi.work.Work;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggingEvent;


/**
 * 
 * @author Bruno Grieder
 */
public class LoggingEventFetcher extends Thread implements Work {
	
	
	private LoggingListener caller;
	private ObjectInputStream ois;
	
	private Thread currentThread = null;
	private volatile boolean dead = true;
	
	
	
	public LoggingEventFetcher(LoggingListener caller, Socket socket) {
		super("LoggingEventFetcher");
		this.caller = caller;
		
		try {
	        InputStream is = socket.getInputStream();
	        ois = new ObjectInputStream(is);
        } catch (Throwable t) {
	        String err = "Unable to get the Input Stream from the logging socket";
	        org.apache.log4j.Logger.getLogger(this.getClass()).error(err,t);
	        throw new IllegalArgumentException(t);
        }
		
		
	}
	

	public void run() {
		
		Logger.getLogger(this.getClass()).trace("run() : fetcher start running");
		

		Pattern p = Pattern.compile(caller.getPattern()); // Pattern.compile.(caller.getPattern(), Pattern.CASE_INSENSITIVE); ?
		currentThread = this;
		dead = false;
		
		while (! currentThread.isInterrupted()) {
			
			try {
				
				Object obj = ois.readObject();
				if ( (obj != null) && (obj instanceof LoggingEvent) ) {
					LoggingEvent le = (LoggingEvent) obj;
					Level level = le.getLevel();
					String loggerName = le.getLoggerName();
					
					if (level.toInt() < caller.getThreshold()) continue;	// Skip this one, its level is too high compared to what we want
					
					Matcher m = p.matcher(loggerName);
					if (!m.matches()) continue;								// Skip this one, it doesn't match the pattern
					
					caller.addLoggingEvent(le);				// We add this one
				}
				
			} catch (EOFException eof) {
				// End of the stream, no more things to do
				this.interrupt();
				break; 
			} catch (Exception e) {
				//likely the socket closed
				// exit
				this.interrupt();
				break;
			}
			
		}
		
		dead = true;
		Logger.getLogger(this.getClass()).trace("run() : fetcher end running");
		
	}
	
	
	
	public void release() {
		org.apache.log4j.Logger.getLogger(this.getClass()).debug("release() : sending the kill signal");
		if (currentThread != null) {
			currentThread.interrupt();
			try { ois.close(); } catch(Exception e) {}
			while (! dead) try { Thread.sleep(200); } catch (InterruptedException e) {
				org.apache.log4j.Logger.getLogger(this.getClass()).debug("release() Waiting for fetcher to die");
			}
		}
	}
	
	
}
