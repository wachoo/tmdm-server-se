package com.amalto.connector.logging.ra;

import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.resource.ResourceException;
import javax.resource.cci.MappedRecord;
import javax.resource.cci.MessageListener;
import javax.resource.cci.Record;
import javax.resource.spi.ActivationSpec;
import javax.resource.spi.BootstrapContext;
import javax.resource.spi.ResourceAdapter;
import javax.resource.spi.ResourceAdapterInternalException;
import javax.resource.spi.endpoint.MessageEndpoint;
import javax.resource.spi.endpoint.MessageEndpointFactory;
import javax.resource.spi.work.WorkManager;
import javax.transaction.xa.XAResource;

import org.apache.commons.lang.SystemUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.Priority;

import com.amalto.connector.interfaces.IXtentisResourceAdapter;
import com.amalto.connector.jca.MappedRecordImpl;
import com.amalto.connector.jca.RecordFactoryImpl;
import com.amalto.connector.jca.XtentisConnectorException;
import com.amalto.connector.logging.eis.LoggingListener;



/**
 * 
 * @author jfeltesse
 */
public class LoggingResourceAdapter implements ResourceAdapter,IXtentisResourceAdapter {
	
	
	private static String configurationFile="mdm.conf";

	
	private final static String JNDI_NAME="java:jca/xtentis/connector/logging";
	
	//the BootstrapContext used to get the WorkManager
	BootstrapContext bootstrapContext = null;
	
	//Cache the WorkManager
	WorkManager workManager = null;
	
	//Cache the Endpoint factory
	MessageEndpointFactory endpointFactory = null;
	
	//the active Logging listeners. the key is the port.
	private Map LoggingListeners = Collections.synchronizedMap(new HashMap());
	
	//flag that indicates that the ative configuration is in sync with the saved configuration
	private boolean  isInSyncWithSavedConfiguration = false;
	
	
	//ResourceAdapter
	public void endpointActivation(MessageEndpointFactory endpointFactory,ActivationSpec spec) throws ResourceException {
		
		Logger.getLogger(this.getClass()).debug("endpointActivation() ");
		
		this.endpointFactory = endpointFactory;
		
		try {
			
			//	Check if the server is up and running
			MappedRecord xmlServerStatus = isXMLServerRunning();
			if ("ERROR".equals(xmlServerStatus.get(RecordFactoryImpl.AS_RESPONSE_FIELD_STATUS_CODE))) {
				//Not running, hopefully we are on start-up
				Logger.getLogger(this.getClass()).info("JCA Adapter "+JNDI_NAME+" : Scheduling activation");
				//create the local stub of the endpoint
				MessageEndpoint endpoint = endpointFactory.createEndpoint(null);
				MappedRecordImpl himr = (MappedRecordImpl)
				(new RecordFactoryImpl()).createMappedRecord(RecordFactoryImpl.SCHEDULE_START);
				himr.put(RecordFactoryImpl.CONFIGURATION_FIELD_JNDI_NAME, JNDI_NAME);
				((MessageListener) endpoint).onMessage(himr);
				return;
			}
			
			startFromConfiguration(null);
		}
		catch (ResourceException e) { throw new ResourceException(e); }
		catch (Exception e) { throw new ResourceException("Endpoint Activation failed: "+e.getClass().getName()+": "+e.getLocalizedMessage()); }
	}
	
	
	//ResourceAdapter    
	public void endpointDeactivation(MessageEndpointFactory endpointFactory, ActivationSpec spec) {
		
		Logger.getLogger(this.getClass()).trace("endpointDeactivation() ");
		
		try {
			synchronized(this) {
				if (! this.isInSyncWithSavedConfiguration) saveConfiguration();
				stopListeners();
			}
		} catch (Exception e) {
			Logger.getLogger(this.getClass()).error("Unable to stop listeners: "+e.getClass().getName()+": "+e.getLocalizedMessage());
		}
	}
	
	
	//ResourceAdapter    
	public XAResource[] getXAResources(ActivationSpec[] specs) throws ResourceException {
		Logger.getLogger(this.getClass()).trace("getXAResources() ");
		return null;
	}
	
	
	//ResourceAdapter    Collections.synchronizedMap(new HashMap());
	public void start(BootstrapContext ctx) throws ResourceAdapterInternalException {
		
		Logger.getLogger(this.getClass()).trace("start() ");
		
		try {
			this.bootstrapContext = ctx;
			this.workManager = ctx.getWorkManager();
		} catch (Exception e) {
			Logger.getLogger(this.getClass()).debug("start() "+e.getLocalizedMessage());
		}
	}
	
	
	//ResourceAdapter    
	public void stop() {
		
		Logger.getLogger(this.getClass()).trace("stop() ");
		try {
			stopListeners();
		} catch (Exception e) {
			Logger.getLogger(this.getClass()).error("Unable to stop listeners: "+e.getClass().getName()+": "+e.getLocalizedMessage());
		}       
	}
	
	public boolean equals(Object obj) {
		if (obj instanceof LoggingResourceAdapter) return true;
		return false;
	}
	
	
	/****************************************************************************************
	 * Implementation of IXtentisResourceAdapter
	 ****************************************************************************************/
	
	public String getVersion() {
		return "FiXME";
	}
	
	
	public MappedRecord isXMLServerRunning() throws XtentisConnectorException {
		
		Logger.getLogger(this.getClass()).trace("isXMLServerRunning() ");
		
		try {
			//create the local stub of the endpoint
			MessageEndpoint endpoint = endpointFactory.createEndpoint(null);
			
			//Deliver the message to the endpoint
			MappedRecord result = null;
			Method targetMethod = MessageListener.class.getMethod("onMessage", new Class[] { Record.class });
			endpoint.beforeDelivery(targetMethod);
			try {
				MappedRecord request = (new RecordFactoryImpl()).createMappedRecord(RecordFactoryImpl.IS_XML_SERVER_UP);
				result = (MappedRecord) ((MessageListener) endpoint).onMessage(request);
			} catch (Exception e) {
				endpoint.afterDelivery();
				throw new XtentisConnectorException(
						"Unable to push a message to get the status of the XML server "+e.getClass().getName()+": "+e.getLocalizedMessage()
				);
			}
			endpoint.afterDelivery();
			return result;
		}
		catch (XtentisConnectorException e) { throw e; }
		catch (Exception e) { throw new XtentisConnectorException(e.getClass().getName()+": "+e.getLocalizedMessage()); }
	}
	
	
	public MappedRecord getConfiguration() throws XtentisConnectorException {
		
		Logger.getLogger(this.getClass()).trace("getConfiguration() ");
		
		try {
			//create the local stub of the endpoint
			MessageEndpoint endpoint = endpointFactory.createEndpoint(null);
			
			//Deliver the message to the endpoint
			MappedRecord result = null;
			Method targetMethod = MessageListener.class.getMethod("onMessage", new Class[] { Record.class });
			endpoint.beforeDelivery(targetMethod);
			try {
				MappedRecord request = (new RecordFactoryImpl()).createMappedRecord(RecordFactoryImpl.GET_CONFIGURATION_RECORD);
				request.put(RecordFactoryImpl.CONFIGURATION_FIELD_JNDI_NAME, JNDI_NAME);
				result = (MappedRecord) ((MessageListener) endpoint).onMessage(request);
			} catch (Exception e) {
				endpoint.afterDelivery();
				throw new XtentisConnectorException(
						"Unable to push a message to get the configuration "+e.getClass().getName()+": "+e.getLocalizedMessage()
				);
			}
			endpoint.afterDelivery();
			return result;
		}
		catch (XtentisConnectorException e) { throw e; }
		catch (Exception e) { throw new XtentisConnectorException(e.getClass().getName()+": "+e.getLocalizedMessage()); }
	}
	
	
	public void saveConfiguration() throws XtentisConnectorException {
		
		Logger.getLogger(this.getClass()).trace("saveConfiguration() ");
		
		try {
			//create the local stub of the endpoint
			MessageEndpoint endpoint = endpointFactory.createEndpoint(null);
			
			//The delivery
			Method targetMethod = MessageListener.class.getMethod("onMessage", new Class[] { Record.class });
			endpoint.beforeDelivery(targetMethod);
			try {
				MappedRecord request = (new RecordFactoryImpl()).createMappedRecord(RecordFactoryImpl.SAVE_CONFIGURATION_RECORD);
				request.put(RecordFactoryImpl.CONFIGURATION_FIELD_JNDI_NAME, JNDI_NAME);
				request.put(RecordFactoryImpl.CONFIGURATION_FIELD_DESCRIPTION, "Logging Adapter");
				request.put(RecordFactoryImpl.CONFIGURATION_FIELD_ISINBOUND, "false");
				request.put(RecordFactoryImpl.CONFIGURATION_FIELD_ISOUTBOUND, "false");
				request.put(RecordFactoryImpl.CONFIGURATION_FIELD_PORTLET_PAGE, "Logging");
				
				HashMap portsMap = new HashMap();
				Set ports = this.LoggingListeners.keySet();
				for (Iterator iter = ports.iterator(); iter.hasNext(); ) {
					String portNumber = (String) iter.next();
					LoggingListener listener = (LoggingListener)this.LoggingListeners.get(portNumber);
					HashMap config = new HashMap();
					config.put("port", new Integer(listener.getPort()));
					config.put("threshold", new Integer(listener.getThreshold()));
					config.put("pattern", listener.getPattern());
					config.put("xtentisusername", listener.getXtentisUsername());
					config.put("xtentispassword", listener.getXtentisPassword());
					config.put("logfilename", listener.getLogFileName());
					config.put("servicename", listener.getServiceName());
					
					portsMap.put("port"+portNumber, config);
				}
				request.put(RecordFactoryImpl.CONFIGURATION_FIELD_PARAMETERS, portsMap);
				
				((MessageListener) endpoint).onMessage(request);
			} catch (Exception e) {
				endpoint.afterDelivery();
				throw new XtentisConnectorException(
						"Unable to push a message to get the configuration "+e.getClass().getName()+": "+e.getLocalizedMessage()
				);
			}
			endpoint.afterDelivery();
		}
		catch (XtentisConnectorException e) { throw e; }
		catch (Exception e) { throw new XtentisConnectorException(e.getClass().getName()+": "+e.getLocalizedMessage()); }
	}
	
	
	public Object onMessage(Object connector, Object message) throws XtentisConnectorException {
		
		Logger.getLogger(this.getClass()).debug("onMessage() : ");
		
		LoggingListener listener = (LoggingListener) connector;
		HashMap params = (HashMap) message;
		
		
		try {
			//create the local stub of the endpoint
			MessageEndpoint endpoint = endpointFactory.createEndpoint(listener);
			
			//The delivery
			Record result = null;
			Method targetMethod = MessageListener.class.getMethod("onMessage", new Class[] { Record.class });
			endpoint.beforeDelivery(targetMethod);
			try {
				MappedRecordImpl himr = (MappedRecordImpl) (new RecordFactoryImpl()).createMappedRecord(RecordFactoryImpl.PUSH_MESSAGE_RECORD);
				himr.put(RecordFactoryImpl.PUSH_MESSAGE_FIELD_FUNCTION_NAME, params.get("function name"));
				himr.put(RecordFactoryImpl.PUSH_MESSAGE_FIELD_FUNCTION_TYPE, params.get("function type"));
				himr.put(RecordFactoryImpl.PUSH_MESSAGE_FIELD_IA_PARAMS_HASHMAP, params.get("query parameters"));
				himr.put(RecordFactoryImpl.PUSH_MESSAGE_FIELD_PAYLOAD_CONTENT_TYPE, params.get("content type"));
				himr.put(RecordFactoryImpl.PUSH_MESSAGE_FIELD_PAYLOAD_CHARSET, params.get("charset"));
				himr.put(RecordFactoryImpl.PUSH_MESSAGE_FIELD_PAYLOAD_BYTES, params.get("payload bytes"));
				himr.put(RecordFactoryImpl.PUSH_MESSAGE_FIELD_USERNAME, params.get("username"));
				himr.put(RecordFactoryImpl.PUSH_MESSAGE_FIELD_PASSWORD, params.get("password"));
				result = ((MessageListener) endpoint).onMessage(himr);
			} catch (Exception e) {
				endpoint.afterDelivery();
				throw new XtentisConnectorException(
						"Unable to push a message to the inbound JMS queue "+e.getClass().getName()+": "+e.getLocalizedMessage()
				);
			}
			endpoint.afterDelivery();
			Map resultParams = (Map)((MappedRecord) result).get(RecordFactoryImpl.AS_RESPONSE_FIELD_PARAMETERS);
			String msg = null;
			if (resultParams != null) {
				msg = (String)resultParams.get("message");
			}
			return (msg == null ?  "" : msg) ;
		}
		catch (XtentisConnectorException e) { throw e; }
		catch (Exception e) { throw new XtentisConnectorException(e.getClass().getName()+": "+e.getLocalizedMessage()); }
	}
	
	
	
	public MappedRecord pushMessage(MappedRecord arg0) throws XtentisConnectorException { return null; }	
	
	public MappedRecord pull(MappedRecord recordIn) throws XtentisConnectorException { return null; }
	
	public MappedRecord push(MappedRecord recordIn) throws XtentisConnectorException { return null; }
	
	public MappedRecord receiveFromAnotherConnector(MappedRecord recordIn) throws XtentisConnectorException { return null; }
	
	private String getThresholdLevel(int threshold) {
		switch(threshold) {
			case Priority.ALL_INT: return "ALL";
			// case Priority.TRACE_INT: break;
			case Priority.DEBUG_INT: return "DEBUG";
			case Priority.ERROR_INT: return "ERROR";
			case Priority.FATAL_INT: return "FATAL";
			case Priority.INFO_INT: return "INFO";
			case Priority.OFF_INT: return "OFF";
			case Priority.WARN_INT: return "WARN";
			default : return String.valueOf(threshold);
		}
	}
	
	
	public MappedRecord startConnector(MappedRecord recordIn) throws XtentisConnectorException {
		org.apache.log4j.Logger.getLogger(this.getClass()).trace("startConnector() ");
		
		LoggingListener listener = null;
		
		try {
			
			// might throw a NullPointerExceptions so be sure to check parameters before calling here !!
			HashMap params = (HashMap)recordIn.get(RecordFactoryImpl.PARAMS_HASHMAP_IN);
			int port = ((Integer) params.get("port")).intValue();
			int threshold = ((Integer) params.get("threshold")).intValue();
			String pattern = (String) params.get("pattern");
			String xtentisUsername = (String) params.get("xtentisusername");
			String xtentisPassword = (String) params.get("xtentispassword");
			String logFilename = (String) params.get("logfilename");
			String serviceName = (String) params.get("servicename");
			
			String portAsString = String.valueOf(port);
			
			Logger.getLogger(this.getClass()).debug("startConnector() on port " + portAsString+
					", threshold='"+getThresholdLevel(threshold) + "', pattern='"+pattern+"'");
			
			
			// check if listener is already running
			listener = (LoggingListener) LoggingListeners.get(portAsString);
			if (listener != null) {
				// check if there is an error
				if (listener.getError() != null) {
					// attempt a restart
					scheduleWork(listener);
					MappedRecord response = (new RecordFactoryImpl()).createMappedRecord(RecordFactoryImpl.RECORD_OUT);
					response.put(RecordFactoryImpl.STATUS_CODE_OUT, "OK");
					HashMap reshash = new HashMap();
					reshash.put("message", "Restarted listener on port " + portAsString);
					response.put(RecordFactoryImpl.PARAMS_HASHMAP_OUT, reshash);
					return response;
				} else {
					MappedRecord response = (new RecordFactoryImpl()).createMappedRecord(RecordFactoryImpl.RECORD_OUT);
					response.put(RecordFactoryImpl.STATUS_CODE_OUT, "OK");
					HashMap reshash = new HashMap();
					reshash.put("message", "Listener already running on port "+portAsString);
					response.put(RecordFactoryImpl.PARAMS_HASHMAP_OUT, reshash);
					return response;
				}
			}
			
			// listener is not running: go ahead
			
			
			// if username is null, read default username and password from xtentis
			if(xtentisUsername == null) {
				
				Properties properties = new Properties();
				try { properties.load(new FileInputStream(configurationFile)); }
				catch (Exception e) { throw new XtentisConnectorException("Unable to load the configuration file \""+configurationFile+ "\"", e); }	
				
				xtentisUsername = properties.getProperty("connectors.inbound.default.username");
				xtentisPassword = properties.getProperty("connectors.inbound.default.password");
			}
			
			// instantiate a new listener
			listener = new LoggingListener(
					this,
					port,
					threshold,
					pattern,
					xtentisUsername,
					xtentisPassword,
					logFilename,
					serviceName
			);
			
			scheduleWork(listener);
			
			MappedRecord response = (new RecordFactoryImpl()).createMappedRecord(RecordFactoryImpl.RECORD_OUT);
			response.put(RecordFactoryImpl.STATUS_CODE_OUT, "OK");
			HashMap reshash = new HashMap();
			reshash.put("message", "Started listener on port "+portAsString);
			response.put(RecordFactoryImpl.PARAMS_HASHMAP_OUT, reshash);
			return response;
			
		}
		catch (XtentisConnectorException e) { throw e; }
		catch (Exception e) { throw new XtentisConnectorException(e.getClass().getName()+": "+e.getLocalizedMessage()); }		
	}
	
	
	
	
	private void scheduleWork(LoggingListener listener) throws XtentisConnectorException{
		
		Logger.getLogger(this.getClass()).trace("scheduleWork() ");
		
		try {
			
			// schedule the listener to start
			this.workManager.scheduleWork(listener);
			
			// wait for status
			while (
					(listener.getStatus() == LoggingListener.STATUS_INSTANTIATED) ||
					(listener.getStatus() == LoggingListener.STATUS_STARTING)
			) {
				try {
					Thread.sleep(200);
				} catch (InterruptedException e) { }
			}
			
			if (listener.getStatus() == LoggingListener.STATUS_ERROR) throw new XtentisConnectorException(
					"An error occured during startup: "+listener.getError()
			);
			if (listener.getStatus() == LoggingListener.STATUS_STOPPED) throw new XtentisConnectorException(
					"An error occured during startup: "+listener.getError()
			);
			
			LoggingListeners.put(String.valueOf(listener.getPort()), listener);
			saveConfiguration();
			
		} catch (Exception e) {
			throw new XtentisConnectorException(e.getClass().getName()+": "+e.getMessage());
		}		
	}
	
	
	
	public MappedRecord startFromConfiguration(MappedRecord arg0) throws XtentisConnectorException {
		
		Logger.getLogger(this.getClass()).trace("startFromConfiguration() ");
		
		try {
			
			MappedRecord response = (new RecordFactoryImpl()).createMappedRecord(RecordFactoryImpl.RECORD_OUT);
			
			// if we are in sync, nothing to do
			if (isInSyncWithSavedConfiguration) {
				Logger.getLogger(this.getClass()).debug("startFromConfiguration() : in sync with saved configuration, nothing to do");
				response.put(RecordFactoryImpl.STATUS_CODE_OUT, "OK");
				return response;
			}
			
			synchronized(this) {
				// stop the current listeners
				try { stopListeners(); }
				catch (Exception e) {}
				
				// get the configuration
				// if none exists, create a new empty one and return
				MappedRecord config = getConfiguration();
				if ("ERROR".equals(config.get(RecordFactoryImpl.AS_RESPONSE_FIELD_STATUS_CODE))) {
					Logger.getLogger(this.getClass()).debug("startFromConfiguration() : creating a new empty config");
					saveConfiguration();
					this.isInSyncWithSavedConfiguration = true;
					response.put(RecordFactoryImpl.STATUS_CODE_OUT, "OK");
					return response;
				}
				
				// fetch configuration parameters
				HashMap params = (HashMap)config.get(RecordFactoryImpl.CONFIGURATION_FIELD_PARAMETERS);
				if (params != null) {
					//loop over listeners
					Set keys = params.keySet();
					for (Iterator iter = keys.iterator(); iter.hasNext(); ) {
						String key = (String) iter.next();
						if (key.startsWith("port")) {
							HashMap portdata = (HashMap) params.get(key);
							MappedRecord recordIn = (new RecordFactoryImpl()).createMappedRecord(RecordFactoryImpl.RECORD_IN);
							recordIn.put(RecordFactoryImpl.PARAMS_HASHMAP_IN, portdata);
							startConnector(recordIn);
							Logger.getLogger(this.getClass()).debug("startFromConfiguration() : started listener on port "+portdata.get("port"));
						}
					}
				}
				
				//we are now in sync
				this.isInSyncWithSavedConfiguration = true;
			}
			
			response.put(RecordFactoryImpl.STATUS_CODE_OUT, "OK");
			return response;
			
		}
		catch (XtentisConnectorException e) { throw e; }
		catch (Exception e) { throw new XtentisConnectorException(e.getClass().getName()+": "+e.getMessage()); }	
	}
	
	
	
	public MappedRecord getConnectorStatus(MappedRecord recordIn) throws XtentisConnectorException {
		
		Logger.getLogger(this.getClass()).debug("getStatus() ");
		
		try {
			
			HashMap params = (HashMap)recordIn.get(RecordFactoryImpl.PARAMS_HASHMAP_IN);
			String port = (params.get("port") == null ? null : String.valueOf((Integer)params.get("port")));
			
			if ((port == null) || "*".equals(port) || "".equals(port)) {
				MappedRecord response = (new RecordFactoryImpl()).createMappedRecord(RecordFactoryImpl.RECORD_OUT);
				response.put(RecordFactoryImpl.STATUS_CODE_OUT, "STOPPED");
				HashMap reshash = new HashMap();
				Set ports = this.LoggingListeners.keySet();
				synchronized(this.LoggingListeners) {
					for (Iterator iter = ports.iterator(); iter.hasNext();) {
						String thisPort = (String) iter.next();
						String[] res = getConnectorStatus(thisPort);
						reshash.put(thisPort, "Port "+thisPort+": "+res[0]+("".equals(res[1]) ? "" : ":  "+res[1]));
						response.put(RecordFactoryImpl.PARAMS_HASHMAP_OUT, reshash);
					}
				}
				return response;
			} else {
				String[] res = getConnectorStatus(port);
				MappedRecord response = (new RecordFactoryImpl()).createMappedRecord(RecordFactoryImpl.RECORD_OUT);
				response.put(RecordFactoryImpl.STATUS_CODE_OUT, res[0]);
				HashMap reshash = new HashMap();
				reshash.put(port, res[1]);
				response.put(RecordFactoryImpl.PARAMS_HASHMAP_OUT, reshash);					
				return response;
			}
		}
		catch (Exception e) { throw new XtentisConnectorException(e.getClass().getName()+": "+e.getMessage()); }	
		
	}
	
	
	private String[] getConnectorStatus(String port) {
		
		Logger.getLogger(this.getClass()).trace("getConnectorStatus() ");
		
		LoggingListener listener = (LoggingListener) LoggingListeners.get(port);
		
		if (listener == null) return new String[] {"STOPPED",""};
		
		switch (listener.getStatus()) {
		case LoggingListener.STATUS_RUNNING:
		case LoggingListener.STATUS_INSTANTIATED:
		case LoggingListener.STATUS_STARTING:
			return new String[] {"OK",""};
			
		case LoggingListener.STATUS_STOPPED:
			return new String[] {"STOPPED",listener.getError()==null ? "" : listener.getError()};
			
		default:
			return new String[] {"ERROR",listener.getError()==null ? "" : listener.getError()};
		}
	}
	
	
	
	public MappedRecord stopConnector(MappedRecord recordIn) throws XtentisConnectorException {
		
		Logger.getLogger(this.getClass()).debug("stopConnector() ");
		
		try {
			
			HashMap params = (HashMap)recordIn.get(RecordFactoryImpl.PARAMS_HASHMAP_IN);
			String port = (params.get("port") == null ? null : String.valueOf(params.get("port")));
			
			if ((port == null) || "*".equals(port) || "".equals(port)) {
				MappedRecord response = (new RecordFactoryImpl()).createMappedRecord(RecordFactoryImpl.RECORD_OUT);
				response.put(RecordFactoryImpl.STATUS_CODE_OUT, "OK");
				HashMap reshash = new HashMap();
				Set ports = this.LoggingListeners.keySet();
				synchronized(this.LoggingListeners) {
					for (Iterator iter = ports.iterator(); iter.hasNext(); ) {
						String thisPort = (String) iter.next();
						LoggingListener listener = (LoggingListener)this.LoggingListeners.get(thisPort);
						listener.release();
						reshash.put(thisPort, "Stopped listener on port "+thisPort);
						response.put(RecordFactoryImpl.PARAMS_HASHMAP_OUT, reshash);					
					}
					this.LoggingListeners = new HashMap();
				}
				return response;
			} else {
				LoggingListener listener = (LoggingListener)this.LoggingListeners.get(port);
				if (listener!=null) {
					listener.release();
					this.LoggingListeners.remove(port);
					saveConfiguration();
				}
				MappedRecord response = (new RecordFactoryImpl()).createMappedRecord(RecordFactoryImpl.RECORD_OUT);
				response.put(RecordFactoryImpl.STATUS_CODE_OUT, "STOPPED");
				HashMap reshash = new HashMap();
				reshash.put(port, "Stopped listener on port "+port);
				response.put(RecordFactoryImpl.PARAMS_HASHMAP_OUT, reshash);					
				return response;
			}
		}
		catch (Exception e) { throw new XtentisConnectorException(e.getClass().getName()+": "+e.getMessage()); }	
	}
	
	
	
	private void stopListeners() throws XtentisConnectorException {
		
		Logger.getLogger(this.getClass()).debug("stopListeners() STOPPING "+LoggingListeners.size()+" port listeners");
		
		Set<String> ports = LoggingListeners.keySet();
		synchronized(this.LoggingListeners) {
			for (Iterator<String> iter = ports.iterator(); iter.hasNext(); ) {
				String port = iter.next();
				LoggingListener listener = (LoggingListener)this.LoggingListeners.get(port);
				listener.release();
			}
			this.LoggingListeners = new HashMap<String, LoggingListener>();
		}
	}
	
	
	
	/********************************************************************
	 * Access to Cached Values
	 ********************************************************************/
	
	/**
	 * @return Returns the bootstrapContext. 
	 */
	public BootstrapContext getBootstrapContext() {
		return this.bootstrapContext;
	}
	/**
	 * @return Returns the workManager.
	 */
	public WorkManager getWorkManager() {
		return workManager;
	}
	/**
	 * @return Returns the endpointFactory.
	 */
	public MessageEndpointFactory getEndpointFactory() {
		return endpointFactory;
	}
	
	
	
	
	
}
