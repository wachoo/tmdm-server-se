
package com.amalto.connector.logging.eis;

import java.io.File;
import java.io.FileOutputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.Vector;

import javax.resource.spi.work.Work;
import javax.transaction.xa.XAException;
import javax.transaction.xa.XAResource;
import javax.transaction.xa.Xid;

import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggingEvent;

import com.amalto.connector.jca.XtentisConnectorException;
import com.amalto.connector.logging.ra.LoggingResourceAdapter;

/**
 * 
 * @author jfeltesse
 */
public class LoggingListener implements Work, XAResource{
	
	/* TODO
	 *
	 * Testing
	 * Heavy load behavior.
	 * Listen on multiple ports.
	 * 
	 * Functionality
	 * Should we add more fields to the output xml message ?
	 * We can get much more informations from the LoggingEvent object but they don't seem very relevant.
	 * 
	 * Compatibility
	 * Our current Jboss installation uses log4j 1.2.8.
	 * The current stable log4j version is 1.2.13.
	 * The next release (1.3) due to mid 2006 adds many things.
	 * Which one should we support ??
	 * 
	 */	
	
	private static String VERSION = "1.0";
	private final static String DEFAULT_CHARSET = "UTF-8";
	private final static String DEFAULT_CONTENT_TYPE = "text/xml";
	
	public final static int STATUS_STOPPED = 0;
	public final static int STATUS_INSTANTIATED = 1;
	public final static int STATUS_STARTING = 2;
	public final static int STATUS_RUNNING = 3;
	public final static int STATUS_ERROR = 4;
	
	
	private LoggingResourceAdapter resourceAdapter; // for workers within JCA
	private int port;
	private int threshold;
	private String pattern;
	private String logFileName;
	private String xtentisUsername;
	private String xtentisPassword;
	private String serviceName;
	
	private volatile boolean kill = false;	//request to kill the listener
	private volatile String error = null;	//contains a message when the listener stopped abnormaly
	
	private volatile int status = STATUS_INSTANTIATED;
	
	private boolean gracefulRelease = false;
	private volatile Vector<LoggingEvent> logEvents;
	private LoggingEventChecker checker;
	private LoggingEventFetcher fetcher;
//	private boolean noMoreNewEvents;
	
	
	
	public LoggingListener(
			LoggingResourceAdapter resourceAdapter,
			int port,
			int threshold,
			String pattern,
			String xtentisUsername,
			String xtentisPassword,
			String logFileName,
			String serviceName
			) {
		this.resourceAdapter = resourceAdapter;
		this.port = port;
		this.threshold = threshold;
		this.pattern = pattern;
		this.xtentisUsername = xtentisUsername;
		this.xtentisPassword = xtentisPassword;
		this.logFileName = logFileName;
		this.serviceName = serviceName;
		this.status = STATUS_INSTANTIATED;
	}
	
	
	/**
	 * @return Returns the version number
	 */
	public String getVersion() {
		return VERSION;
	}
	/**
	 * @return Returns the error
	 */
	public String getError() {
		return error;
	}
	/**
	 * @return Returns the status
	 */
	public int getStatus() {
		return status;
	}
	/**
	 * @return Returns the port.
	 */
	public int getPort() {
		return port;
	}
	/**
	 * @return Returns the threshold
	 */
	public int getThreshold() {
		return threshold;
	}
	/**
	 * @return Returns the pattern.
	 */
	public String getPattern() {
		return pattern;
	}
	/**
	 * @return Returns the logFileName.
	 */
	public String getLogFileName() {
		return logFileName;
	}
	/**
	 * @return Returns the xtentisPassword.
	 */
	public String getXtentisPassword() {
		return xtentisPassword;
	}
	/**
	 * @return Returns the xtentisUsername.
	 */
	public String getXtentisUsername() {
		return xtentisUsername;
	}
	
	/**
	 * @return Returns the serviceName.
	 */
	public String getServiceName() {
		return serviceName;
	}
	
	
	
	
	
	
	/****************************************************************************************
	 * 
	 *  WORK STUFF
	 *  
	 ****************************************************************************************/
	
	public void run() {
		
		Socket socket = null;
		
		try {
			
			this.status = STATUS_STARTING;
			
			//delete the current logFileName
			if (this.logFileName != null)  {
				try {
					File f = new File(this.logFileName);
					if (f.isFile()) f.delete();
				} catch (Exception e) {}
			}	
			
			
			ServerSocket serverSocket = new ServerSocket(this.port);
			checker = new LoggingEventChecker(this);
			checker.setDaemon(true);
			logEvents = new Vector<LoggingEvent>();
			
			Logger.getLogger(this.getClass()).info("Logging Listener started and waiting for connections on port " + this.port + "...\n");
			
			while (true) {
				
				this.status = STATUS_RUNNING;
				serverSocket.setSoTimeout(1000);
				
				while (!this.kill) {
					try {
						socket = serverSocket.accept(); // waiting
						break; //process the connect
					} catch (SocketTimeoutException ste) {}
				}
				if (this.kill) {
					
					if (fetcher != null) {
						fetcher.release();
					}
					if (socket != null) socket.close();
					if (serverSocket != null) serverSocket.close();
					
					if (gracefulRelease) {
						Logger.getLogger(this.getClass()).debug("run() : graceful release : processing "+logEvents.size()+" logging events before releasing.");
						while(logEvents.size() > 0) {	// Wait until the processing of the logging events is done
							try { Thread.sleep(500); }
							catch (InterruptedException ste) {}
						}
					}
					else {
						Logger.getLogger(this.getClass()).debug("run() : release : "+logEvents.size()+" logging events go into oblivion.");
					}
					if (checker != null) checker.release();
					
					Logger.getLogger(this.getClass()).debug("run() Killed Listener on port "+ this.port);
					this.error = "Listener killed on request";
					this.status = STATUS_STOPPED;
					
					return;
				}
				
				
				Logger.getLogger(this.getClass()).debug("run () : accepted a connection");
				
				fetcher = new LoggingEventFetcher(this, socket);
				fetcher.setDaemon(true);
				
				if (this.resourceAdapter == null) {	// Standalone
					fetcher.start();
					if ( !checker.isAlive()) checker.start();
				}
				else {
					this.resourceAdapter.getWorkManager().scheduleWork(fetcher);
					if ( !checker.isAlive()) this.resourceAdapter.getWorkManager().scheduleWork(checker);
				}
				
				
			}
			
		}
		catch (Exception e) {
			Logger.getLogger(this.getClass()).error("Logging error", e);
			this.error = e.getClass().getName()+": "+e.getLocalizedMessage();
			this.status = STATUS_ERROR;
		}
		finally {
			try { if (socket != null) socket.close(); } catch (Exception e) { }
			if (this.status != STATUS_ERROR) this.status = STATUS_STOPPED;
		}
		
	}
	
	
	
	public LoggingEvent getPendingEvent() {
		if (logEvents.size() > 0) {
			LoggingEvent le = logEvents.elementAt(0);
			logEvents.remove(0);
			return le;
		}
		else {
			return null;
		}
	}
	
	
	public void addLoggingEvent(LoggingEvent le) {
		logEvents.add(le);
		synchronized (checker) { checker.notify(); }
	}
	
	
	
	
	public void releaseGracefully() {
		gracefulRelease = true;
		release();
	}
	
	
	public void release() {
		Logger.getLogger(this.getClass()).debug("release() : releasing port "+this.port);
		this.kill = true;
		while (this.status != STATUS_ERROR && this.status != STATUS_STOPPED) {
			try {
				Logger.getLogger(this.getClass()).debug("release() : waiting for thread to be dead (status="+status+")");
				Thread.sleep(1000);
			} catch (InterruptedException ste) {}
		}
		Logger.getLogger(this.getClass()).debug("release() : thread is dead");
	}
	
	
	
	
	
	/****************************************************************************************
	 * 
	 *  SOCKET Threads Handling
	 *  
	 ****************************************************************************************/
	
	public void handlingDone(String xml) throws XtentisConnectorException {
		
		if (this.resourceAdapter == null) {	// Standalone
			Logger.getLogger(this.getClass()).debug("handlingDone() : \n\n"+xml);
		}
		else {
			
			Logger.getLogger(this.getClass()).debug("handlingDone() : ");
			
			// Convert the xml String to bytes
			byte[] payload = null;
			try { payload = xml.getBytes(DEFAULT_CHARSET);	}
			catch (UnsupportedEncodingException ue) {
				Logger.getLogger(this.getClass()).error("Couldn't convert xml logging event to UTF-8 encoded bytes", ue);
			}
			
			// Prepare the message
			HashMap<String,Serializable> params = new HashMap<String,Serializable>();
			params.put("function name", serviceName);
			params.put("function type", "service");
			params.put("query parameters", null);
			params.put("content type", DEFAULT_CONTENT_TYPE);
			params.put("charset", DEFAULT_CHARSET);
			params.put("payload bytes", payload);
			params.put("username", this.xtentisUsername);
			params.put("password", this.xtentisPassword);
			
			
			// Push the message to the resource adapter
			try {
				this.resourceAdapter.onMessage(this,params);
			}
			catch (XtentisConnectorException e) { throw e; }
			catch (Exception e) { throw new XtentisConnectorException(e.getClass().getName()+": "+e.getLocalizedMessage()); }
			
		}
		
	}
	
	
	
	public synchronized void writeToLog(String message) {
		try {
			FileOutputStream fos = null;
			if (this.logFileName!=null) { 
				fos= new FileOutputStream(new File(this.logFileName),true);
				fos.write(message.getBytes("UTF-8"));
			}
		} catch (Exception e) {
			org.apache.log4j.Logger.getLogger(this.getClass()).debug("writeToLog() Could not write to Log "+this.logFileName+": "+e.getLocalizedMessage());
		}
	}
	
	
	
	
	/****************************************************************************************
	 * 
	 *  XA Stuff
	 *  
	 ****************************************************************************************/
	
	public void commit(Xid arg0, boolean arg1) throws XAException { }
	
	public void end(Xid arg0, int arg1) throws XAException { }
	
	public void forget(Xid arg0) throws XAException { }
	
	public int getTransactionTimeout() throws XAException { return 0; }
	
	public boolean isSameRM(XAResource xares) throws XAException { return (xares == this); }
	
	public int prepare(Xid arg0) throws XAException { return XAResource.XA_OK; }
	
	public Xid[] recover(int arg0) throws XAException { return new Xid[0]; }
	
	public void rollback(Xid arg0) throws XAException { }
	
	public boolean setTransactionTimeout(int arg0) throws XAException { return false; }
	
	public void start(Xid arg0, int arg1) throws XAException { }



	
	
}