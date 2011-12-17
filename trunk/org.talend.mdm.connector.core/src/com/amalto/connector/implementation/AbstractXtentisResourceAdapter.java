package com.amalto.connector.implementation;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

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

import org.apache.log4j.Logger;

import com.amalto.connector.interfaces.IXtentisResourceAdapter;
import com.amalto.connector.jca.MappedRecordImpl;
import com.amalto.connector.jca.RecordFactoryImpl;
import com.amalto.connector.jca.XtentisConnectorException;
import com.amalto.connector.util.Version;

public abstract class AbstractXtentisResourceAdapter implements ResourceAdapter,
		IXtentisResourceAdapter {


	/**
	 * This is a simplified call to the full OnMessage Method.
	 * The EIS should call this method to push a message to the Application Server.
	 * The method will throw an XtentisConnectorException if the return code form the application server is not STATUS_OK.
	 * The exception message will contain the Application Server Error message.
	 * @param xaResource - can be null if you do not want transaction management
	 * @param serviceParameters - an hashMap of parameters to pass to the service
	 * @param payloadBytes - the payload itself as an Array of bytes
	 * @param payloadContentType - the content-type e.g "text/xml", "text/plain",....
	 * @param payloadCharset - the charset/encoding e.g. "utf-8", "iso-8859-1", ....
	 * @return an Object being the response of the Application Server
	 * @throws XtentisConnectorException
	 */
	public Object receiveFromEISForService(
			XAResource xaResource,
			String serviceJNDIName,
			HashMap<? extends Serializable,? extends Serializable> serviceParameters,
			byte[] payloadBytes,
			String payloadContentType,
			String payloadCharset
			) throws XtentisConnectorException {
		return onMessage(
				xaResource,
				FUNCTION_TYPE_SERVICE,
				serviceJNDIName,
				serviceParameters,
				payloadBytes,
				payloadContentType,
				payloadCharset,
				null,
				null
		);
	}
	
	@Deprecated 
	/**
	 * @deprecated
	 * Call the receiveFromEISForService method or the full onMessage method
	 */
	public Object onMessage(Object callingConnector, Object message) throws XtentisConnectorException {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * This method is invoked by the application server to pull data from the EIS.
	 * (If the EIS needs to push data to the Application Server, it should call the <code>receiveFromEIS</code> method) 
	 * If the method fails, make sure you throw an XtentisConnectorException. 
	 * The message of the exception will be passed backed as the message and the code will be set to <code>STATUS_ERROR</code>
	 *  and the status will be set to STATUS_ERROR.
	 * @param parametersIn - parameters passed by the Application server
	 * @return a Hashmap containing data to be passed back to the Application Server
	 * @throws XtentisConnectorException
	 */
	public abstract HashMap<Serializable, Serializable> pullFromEIS(HashMap<Serializable, Serializable> parametersIn) throws XtentisConnectorException;

	

	/**
	 * This method is invoked by the application server to push data to the EIS
	 * If the method fails, make sure you throw an XtentisConnectorException. 
	 * The message of the exception will be passed backed as the message and the code will be set to <code>STATUS_ERROR</code>
	 *  and the status will be set to STATUS_ERROR
	 * @param parametersIn - parameters passed by the Application server
	 * @return a Hashmap containing data to be passed back to the Application Server
	 * @throws XtentisConnectorException
	 */
	public abstract HashMap<Serializable, Serializable> pushToEIS(HashMap<Serializable, Serializable> parametersIn) throws XtentisConnectorException;

	
	
	/**
	 * This method must return the configuration in use by your running Connector.
	 * The configuration will be serialized on start and stop of the connector and used
	 * by the application server to restart your connector froms its saved configuration.
	 * On start, the configuration HashMap will be passed to the <code>startNow</code> method as parameters..
	 * @return the running configuration
	 */
	public abstract HashMap<Serializable,Serializable> getRunningConfiguration() throws XtentisConnectorException;
		

	/**
	 * This method is invoked by the application server retrieve the Connector status.
	 * The methods should returned two Strings
	 * 	-the first one being one of the values of STATUS_OK, STATUS_ERROR, STATUS_STOPPED
	 * 	-the second one, a text message  
	 * If the method fails, make sure you throw an XtentisConnectorException. 
	 * The message of the exception will be passed backed as the message and the code will be set to <code>STATUS_ERROR</code>
	 *  and the status will be set to STATUS_ERROR
	 * @param parametersIn - parameters passed by the Application server
	 * @return an Array of 2 Strings. The first one contains the status and the second one a possible message.
	 * @throws XtentisConnectorException
	 */
	public abstract String[] getStatus(HashMap<Serializable, Serializable> parametersIn) throws XtentisConnectorException;

	
	/**
	 * This method is invoked by the application server to start your Connector
	 * Use this method to start threads on your connector.
	 * The configuration is automatically saved before the call to this method.
	 * To override this automatic save, reimplement <code>public MappedRecord startConnector(MappedRecord recordIn) throws XtentisConnectorException</code>
	 * If the method fails, make sure you throw an XtentisConnectorException. 
	 * The message of the exception will be passed backed in the <code>message</code> entry  of the returned HashMap
	 * and the status will be set to STATUS_ERROR
	 * @param parametersIn - parameters passed by the Application server
	 * @return a Hasmap containing - typically - a status message
	 * @throws XtentisConnectorException
	 */
	public abstract HashMap<Serializable, Serializable> startNow(HashMap<Serializable, Serializable> parametersIn) throws XtentisConnectorException;

	
	/**
	 * This method is invoked by the application server to stop your Connector
	 * Use this method to stop threads started by your connector.
	 * The configuration is automatically saved after the call to this method.
	 * To override this automatic save, reimplement <code>public MappedRecord stopConnector(MappedRecord recordIn) throws XtentisConnectorException</code>
	 * If the method fails, make sure you throw an XtentisConnectorException. 
	 * The message of the exception will be passed backed in the <code>message</code> entry  of the returned HashMap
	 *  and the status will be set to STATUS_ERROR
	 * @param parametersIn - parameters passed by the Application server
	 * @return a Hasmap containing - typically - a status message
	 * @throws XtentisConnectorException
	 */
	public abstract HashMap<Serializable, Serializable> stopNow(HashMap<Serializable, Serializable> parametersIn) throws XtentisConnectorException;
	
	/**
	 * This method must return the JNDI name of the connector
	 *  identical to the one found in the META-INF/ds.xml
	 * Typical example:"java:jca/xtentis/connector/myconnector";
	 * @return the JNDI name
	 */
	public abstract String getJNDIName();
	
	/****************************************************************************************
	 * Pre-implemented methods of IXtentisResourceAdapter
	 * Override if needed
	 ****************************************************************************************/
	
	/**
	 * Called by another connector trying to chain this one in a processing chain
	 * @param recordIn
	 * @return a Mapped record that can be processed by the calling connector
	 * @throws XtentisConnectorException
	 */
	public MappedRecord receiveFromAnotherConnector(MappedRecord recordIn) throws XtentisConnectorException {
		//by default this method throws an Exception
		throw new XtentisConnectorException("The connector '"+getJNDIName()+"' does not know how to handle calls from another connector");
	}
	
	//flag that indicates that the active configuration is in sync with the saved configuration
	private boolean  isInSyncWithSavedConfiguration = false;
	
	
	protected final static String STATUS_ERROR="ERROR";
	protected final static String STATUS_OK="OK";
	protected final static String STATUS_STOPPED="STOPPED";
	
	protected final static String FUNCTION_TYPE_SERVICE="service";
	@Deprecated
	protected final static String FUNCTION_TYPE_INBOUND_ADAPTOR="ia";
	
	public String getVersion() {
		return Version.getVersion(this.getClass());
	}
	
	
	public MappedRecord startFromConfiguration(MappedRecord arg0) throws XtentisConnectorException {
		Logger.getLogger(this.getClass()).trace("startFromConfiguration() ");
		
		try {
			
			MappedRecord response;
			
			synchronized(this) {
				// get the configuration
				// if none exists, create a new empty one and return
				MappedRecord config = getConfiguration();
				if (STATUS_ERROR.equals(config.get(RecordFactoryImpl.AS_RESPONSE_FIELD_STATUS_CODE))) {
					this.isInSyncWithSavedConfiguration = false;
					response = (new RecordFactoryImpl()).createMappedRecord(RecordFactoryImpl.RECORD_OUT);
					response.put(RecordFactoryImpl.STATUS_CODE_OUT, STATUS_STOPPED);
					return response;
				}
				// fetch configuration parameters
				HashMap<Serializable,Serializable> params = (HashMap<Serializable,Serializable>)config.get(RecordFactoryImpl.CONFIGURATION_FIELD_PARAMETERS);
				//create a record to be passed to sstartConnector
				MappedRecord recordIn = (new RecordFactoryImpl()).createMappedRecord(RecordFactoryImpl.RECORD_IN);
				recordIn.put(RecordFactoryImpl.PARAMS_HASHMAP_IN, params);
				response = startConnector(recordIn);
				//we are now in sync
				this.isInSyncWithSavedConfiguration = true;
				return response;				
			}			
			
		}
		catch (XtentisConnectorException e) { throw e; }
		catch (Exception e) {
			org.apache.log4j.Logger.getLogger(this.getClass()).info("ERROR SYSTRACE: "+e.getClass().getName()+": "+e.getLocalizedMessage(),e);
			throw new XtentisConnectorException(e.getClass().getName()+": "+e.getMessage()); 
		}	
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
		catch (Exception e) {
			org.apache.log4j.Logger.getLogger(this.getClass()).info("ERROR SYSTRACE: "+e.getClass().getName()+": "+e.getLocalizedMessage(),e);
			throw new XtentisConnectorException(e.getClass().getName()+": "+e.getLocalizedMessage()); 
		}
	}
	
	

	

	
	
	public MappedRecord getConfiguration() throws XtentisConnectorException {
		
		Logger.getLogger(this.getClass()).trace("getConfiguration() ");
		
		try {
			//create the local stub of the endpoint
			MessageEndpoint endpoint = endpointFactory.createEndpoint(null);
			
			//Deliver the message to the endpoint
			MappedRecord resp = null;
			Method targetMethod = MessageListener.class.getMethod("onMessage", new Class[] { Record.class });
			endpoint.beforeDelivery(targetMethod);
			try {
				MappedRecord request = (new RecordFactoryImpl()).createMappedRecord(RecordFactoryImpl.GET_CONFIGURATION_RECORD);
				request.put(RecordFactoryImpl.CONFIGURATION_FIELD_JNDI_NAME, getJNDIName());
				resp = (MappedRecord) ((MessageListener) endpoint).onMessage(request);
			} catch (Exception e) {
				endpoint.afterDelivery();
				resp = (new RecordFactoryImpl()).createMappedRecord(RecordFactoryImpl.AS_RESPONSE_RECORD);
				HashMap<Serializable,Serializable> result = new HashMap<Serializable,Serializable>();
				result.put(
						"message", "Could not get the configuration for JCA Adapter "+getJNDIName()+": "+
						e.getClass().getName()+": "+e.getLocalizedMessage()
				);
				resp.put(RecordFactoryImpl.AS_RESPONSE_FIELD_PARAMETERS, result);
				resp.put(RecordFactoryImpl.AS_RESPONSE_FIELD_STATUS_CODE, STATUS_ERROR);
				return resp;
			}
			endpoint.afterDelivery();
			return resp;
		}	catch (Exception e) { 
			org.apache.log4j.Logger.getLogger(this.getClass()).info("ERROR SYSTRACE: "+e.getClass().getName()+": "+e.getLocalizedMessage(),e);
			throw new XtentisConnectorException(e.getClass().getName()+": "+e.getLocalizedMessage()); 
		}
	}
	
	/**
	 * Saves the Running configuration.
	 * Calls {@link AbstractXtentisResourceAdapter#getRunningConfiguration()}
	 */
	public void saveConfiguration() throws XtentisConnectorException {
		Logger.getLogger(this.getClass()).trace("saveRunningConfiguration() ");
		saveConfiguration(getRunningConfiguration());
	}
	

	/**
	 * Saves a new Configuration
	 * @param configuration
	 * @throws XtentisConnectorException
	 */
	public void saveConfiguration(HashMap<Serializable, Serializable> configuration) throws XtentisConnectorException {
		
		Logger.getLogger(this.getClass()).trace("saveConfiguration() ");
		
		try {
			//create the local stub of the endpoint
			MessageEndpoint endpoint = endpointFactory.createEndpoint(null);
			
			//The delivery
			Method targetMethod = MessageListener.class.getMethod("onMessage", new Class[] { Record.class });
			endpoint.beforeDelivery(targetMethod);
			try {
				MappedRecord request = (new RecordFactoryImpl()).createMappedRecord(RecordFactoryImpl.SAVE_CONFIGURATION_RECORD);
				request.put(RecordFactoryImpl.CONFIGURATION_FIELD_JNDI_NAME, getJNDIName());
				request.put(RecordFactoryImpl.CONFIGURATION_FIELD_PARAMETERS, configuration);
				((MessageListener) endpoint).onMessage(request);
			} catch (Exception e) {
				endpoint.afterDelivery();
				//throw new XtentisConnectorException(
					//	"Unable to push a message to save the configuration "+e.getClass().getName()+": "+e.getLocalizedMessage()
				//);
			}
			endpoint.afterDelivery();
		}//catch (XtentisConnectorException e) {
			//throw e; 
		//}
		catch (Exception e) {
			//org.apache.log4j.Logger.getLogger(this.getClass()).info("ERROR SYSTRACE: "+e.getClass().getName()+": "+e.getLocalizedMessage(),e);
			//throw new XtentisConnectorException(e.getClass().getName()+": "+e.getLocalizedMessage()); 
		}
	}

	
	public MappedRecord getConnectorStatus(MappedRecord recordIn) throws XtentisConnectorException {
		
		Logger.getLogger(this.getClass()).trace("getStatus() ");
		
		try {
			HashMap<Serializable,Serializable> params = (HashMap<Serializable,Serializable>)recordIn.get(RecordFactoryImpl.PARAMS_HASHMAP_IN);
			MappedRecord response = (new RecordFactoryImpl()).createMappedRecord(RecordFactoryImpl.RECORD_OUT);
			HashMap<String,Object> result = new HashMap<String, Object>();
			String[] statuses = null;
			try {
				statuses = getStatus(params);
				response.put(RecordFactoryImpl.STATUS_CODE_OUT, statuses[0]);
				result.put("message", statuses[1]);
			} catch (XtentisConnectorException e) {
				response.put(RecordFactoryImpl.STATUS_CODE_OUT, STATUS_ERROR);				
				result.put("message", e.getLocalizedMessage());
			}
			response.put(RecordFactoryImpl.PARAMS_HASHMAP_OUT, result);
			return response;	
		}	catch (Exception e) {
			org.apache.log4j.Logger.getLogger(this.getClass()).info("ERROR SYSTRACE: "+e.getClass().getName()+": "+e.getLocalizedMessage(),e);
			throw new XtentisConnectorException(e.getClass().getName()+": "+e.getMessage()); 
		}	
	}
	
	
	public MappedRecord startConnector(MappedRecord recordIn) throws XtentisConnectorException {
		org.apache.log4j.Logger.getLogger(this.getClass()).trace("startConnector() ");

		try {			
			HashMap<Serializable,Serializable> params = (HashMap<Serializable,Serializable>)recordIn.get(RecordFactoryImpl.PARAMS_HASHMAP_IN);

			MappedRecord response = (new RecordFactoryImpl()).createMappedRecord(RecordFactoryImpl.RECORD_OUT);
			
			HashMap<Serializable,Serializable> result = new HashMap<Serializable, Serializable>();
			try {
				result = startNow(params);
				response.put(RecordFactoryImpl.STATUS_CODE_OUT, STATUS_OK);
			} catch (XtentisConnectorException e) {
				response.put(RecordFactoryImpl.STATUS_CODE_OUT, STATUS_ERROR);
				result.put("message", e.getLocalizedMessage());
			}
			response.put(RecordFactoryImpl.PARAMS_HASHMAP_OUT, result);
			return response;	
		
		}
		catch (Exception e) {
			org.apache.log4j.Logger.getLogger(this.getClass()).info("ERROR SYSTRACE: "+e.getClass().getName()+": "+e.getLocalizedMessage(),e);
			throw new XtentisConnectorException(e.getClass().getName()+": "+e.getLocalizedMessage()); 
		}		
	}

	public MappedRecord stopConnector(MappedRecord recordIn){
		org.apache.log4j.Logger.getLogger(this.getClass()).trace("stopConnector() ");
		try {
			if (! this.isInSyncWithSavedConfiguration) saveConfiguration();
			HashMap<Serializable,Serializable> params = (recordIn == null ? null : (HashMap<Serializable,Serializable>)recordIn.get(RecordFactoryImpl.PARAMS_HASHMAP_IN));
			MappedRecord response = (new RecordFactoryImpl()).createMappedRecord(RecordFactoryImpl.RECORD_OUT);
			HashMap<Serializable,Serializable> result = new HashMap<Serializable, Serializable>();
			try {
				result = stopNow(params);
				response.put(RecordFactoryImpl.STATUS_CODE_OUT, STATUS_OK);
			} catch (XtentisConnectorException e) {
				//response.put(RecordFactoryImpl.STATUS_CODE_OUT, STATUS_ERROR);
				//result.put("message", e.getLocalizedMessage());
			}
			response.put(RecordFactoryImpl.PARAMS_HASHMAP_OUT, result);
			return response;			
		}	catch (Exception e) { 
			//org.apache.log4j.Logger.getLogger(this.getClass()).info("ERROR SYSTRACE: "+e.getClass().getName()+": "+e.getLocalizedMessage(),e);
			//throw new XtentisConnectorException(e.getClass().getName()+": "+e.getLocalizedMessage()); 
		}
		return null;
	}
	
	public MappedRecord push(MappedRecord recordIn) throws XtentisConnectorException {
		org.apache.log4j.Logger.getLogger(this.getClass()).trace("push() ");
		try {
			HashMap<Serializable,Serializable> params = (HashMap<Serializable,Serializable>)recordIn.get(RecordFactoryImpl.PARAMS_HASHMAP_IN);
			MappedRecord response = (new RecordFactoryImpl()).createMappedRecord(RecordFactoryImpl.RECORD_OUT);
			HashMap<Serializable,Serializable> result = new HashMap<Serializable, Serializable>();
			try {
				//pass it on
				result = pushToEIS(params);
				response.put(RecordFactoryImpl.STATUS_CODE_OUT, STATUS_OK);
			} catch (XtentisConnectorException e) {
				response.put(RecordFactoryImpl.STATUS_CODE_OUT, STATUS_ERROR);
				result.put("message", e.getLocalizedMessage());
			}
			response.put(RecordFactoryImpl.PARAMS_HASHMAP_OUT, result);
			return response;			
		}	catch (Exception e) { 
			org.apache.log4j.Logger.getLogger(this.getClass()).info("ERROR SYSTRACE: "+e.getClass().getName()+": "+e.getLocalizedMessage(),e);
			throw new XtentisConnectorException(e.getClass().getName()+": "+e.getLocalizedMessage()); 
		}
	}
	
	
	public MappedRecord pull(MappedRecord recordIn) throws XtentisConnectorException {
		org.apache.log4j.Logger.getLogger(this.getClass()).trace("pull() ");
		try {
			HashMap<Serializable,Serializable> params = (HashMap<Serializable,Serializable>)recordIn.get(RecordFactoryImpl.PARAMS_HASHMAP_IN);
			MappedRecord response = (new RecordFactoryImpl()).createMappedRecord(RecordFactoryImpl.RECORD_OUT);
			HashMap<Serializable,Serializable> result = new HashMap<Serializable, Serializable>();
			try {
				//pass it on
				result = pullFromEIS(params);
				response.put(RecordFactoryImpl.STATUS_CODE_OUT, STATUS_OK);
			} catch (XtentisConnectorException e) {
				response.put(RecordFactoryImpl.STATUS_CODE_OUT, STATUS_ERROR);
				result.put("message", e.getLocalizedMessage());
			}
			response.put(RecordFactoryImpl.PARAMS_HASHMAP_OUT, result);
			return response;			
		}	catch (Exception e) { 
			org.apache.log4j.Logger.getLogger(this.getClass()).info("ERROR SYSTRACE: "+e.getClass().getName()+": "+e.getLocalizedMessage(),e);
			throw new XtentisConnectorException(e.getClass().getName()+": "+e.getLocalizedMessage()); 
		}
	}
	
	
	public Object onMessage(
			XAResource xaResource,
			String functionType,
			String functionName,
			HashMap<? extends Serializable,? extends Serializable> functionParameters,
			byte[] payloadBytes,
			String payloadContentType,
			String payloadCharset,
			String username,
			String password
			) throws XtentisConnectorException {
				
		try {
			//create the local stub of the endpoint
	        MessageEndpoint endpoint = endpointFactory.createEndpoint(xaResource);
	        
			//The delivery
			Method targetMethod = MessageListener.class.getMethod("onMessage", new Class[] { Record.class });
			
			endpoint.beforeDelivery(targetMethod);
        	 
			MappedRecordImpl himr = (MappedRecordImpl)
        	 	(new RecordFactoryImpl()).createMappedRecord(RecordFactoryImpl.PUSH_MESSAGE_RECORD);
        	 himr.put(RecordFactoryImpl.PUSH_MESSAGE_FIELD_FUNCTION_NAME, functionName);
        	 himr.put(RecordFactoryImpl.PUSH_MESSAGE_FIELD_FUNCTION_TYPE, functionType);
        	 himr.put(RecordFactoryImpl.PUSH_MESSAGE_FIELD_IA_PARAMS_HASHMAP,functionParameters);
        	 himr.put(RecordFactoryImpl.PUSH_MESSAGE_FIELD_PAYLOAD_CONTENT_TYPE,payloadContentType);
        	 himr.put(RecordFactoryImpl.PUSH_MESSAGE_FIELD_PAYLOAD_CHARSET,payloadCharset);
        	 himr.put(RecordFactoryImpl.PUSH_MESSAGE_FIELD_PAYLOAD_BYTES,payloadBytes);
        	 himr.put(RecordFactoryImpl.PUSH_MESSAGE_FIELD_USERNAME,username);
        	 himr.put(RecordFactoryImpl.PUSH_MESSAGE_FIELD_PASSWORD,password);
        	 MappedRecord response = (MappedRecord) ((MessageListener) endpoint).onMessage(himr);
	         
        	 endpoint.afterDelivery();
	         
	         Object responseParams = response.get(RecordFactoryImpl.AS_RESPONSE_FIELD_PARAMETERS);
	         if (STATUS_ERROR.equals(response.get(RecordFactoryImpl.AS_RESPONSE_FIELD_STATUS_CODE))) {
	        	 String msg = "";
		         if (responseParams!=null)  msg = (String)((Map<Serializable,Serializable>)responseParams).get("message");
		         if (msg == null) msg ="NO ERROR MESSAGE";
		         throw new XtentisConnectorException(msg);
	         }
	         return responseParams;
	    } catch (XtentisConnectorException e) {
	    	throw(e);
	    } catch (Exception e) {
			org.apache.log4j.Logger.getLogger(this.getClass()).info("ERROR SYSTRACE: "+e.getClass().getName()+": "+e.getLocalizedMessage(),e);
	    	throw new XtentisConnectorException(e.getClass().getName()+": "+e.getLocalizedMessage());
	    }
	}

	
	/****************************************************************************************
	 * Implementation of ResourceAdapter
	 * There is usually no reason to change any of the code below
	 ****************************************************************************************/
	
	//the BootstrapContext used to get the WorkManager
	protected BootstrapContext bootstrapContext = null;
	
	//Cache the WorkManager
	protected WorkManager workManager = null;
	
	//Cache the Endpoint factory
	protected MessageEndpointFactory endpointFactory = null;
	
	//ResourceAdapter
	public void endpointActivation(MessageEndpointFactory endpointFactory,ActivationSpec spec) throws ResourceException {
		
		Logger.getLogger(this.getClass()).trace("endpointActivation() ");
		
		this.endpointFactory = endpointFactory;
		
		try {
			
			//	Check if the server is up and running
			MappedRecord xmlServerStatus = isXMLServerRunning();
			if ("ERROR".equals(xmlServerStatus.get(RecordFactoryImpl.AS_RESPONSE_FIELD_STATUS_CODE))) {
				//Not running, hopefully we are on start-up
				Logger.getLogger(this.getClass()).info("JCA Adapter "+getJNDIName()+" : Scheduling activation");
				//create the local stub of the endpoint
				MessageEndpoint endpoint = endpointFactory.createEndpoint(null);
				MappedRecordImpl himr = (MappedRecordImpl)
				(new RecordFactoryImpl()).createMappedRecord(RecordFactoryImpl.SCHEDULE_START);
				himr.put(RecordFactoryImpl.CONFIGURATION_FIELD_JNDI_NAME, getJNDIName());
				((MessageListener) endpoint).onMessage(himr);
				return;
			}
			
			startFromConfiguration(null);
		}
		catch (ResourceException e) { throw new ResourceException(e); }
		catch (Exception e) {
			org.apache.log4j.Logger.getLogger(this.getClass()).info("ERROR SYSTRACE: "+e.getClass().getName()+": "+e.getLocalizedMessage(),e);
			throw new ResourceException("Endpoint Activation failed: "+e.getClass().getName()+": "+e.getLocalizedMessage()); 
		}
	}
	
	
	//ResourceAdapter    
	public void endpointDeactivation(MessageEndpointFactory endpointFactory, ActivationSpec spec) {
		
		Logger.getLogger(this.getClass()).trace("endpointDeactivation() ");
		
		try {
			synchronized(this) {
				stopConnector(null);
			}
		} catch (Exception e) {
			//Logger.getLogger(this.getClass()).error("Unable to stop listeners: "+e.getClass().getName()+": "+e.getLocalizedMessage());
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
			stopConnector(null);
		} catch (Exception e) {
			Logger.getLogger(this.getClass()).error("Unable to stop the connector "+e.getClass().getName()+": "+e.getLocalizedMessage());
		}       
	}
	
	public boolean equals(Object obj) {
		return (
				(obj instanceof AbstractXtentisResourceAdapter) &&
				((AbstractXtentisResourceAdapter)obj).getJNDIName().equals(getJNDIName())
		);
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
