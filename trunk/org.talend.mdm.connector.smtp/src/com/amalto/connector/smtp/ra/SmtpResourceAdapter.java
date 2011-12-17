package com.amalto.connector.smtp.ra;

import java.io.File;
import java.io.FileInputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Properties;

import javax.resource.ResourceException;
import javax.resource.cci.MappedRecord;
import javax.resource.spi.ActivationSpec;
import javax.resource.spi.BootstrapContext;
import javax.resource.spi.ResourceAdapter;
import javax.resource.spi.ResourceAdapterInternalException;
import javax.resource.spi.endpoint.MessageEndpointFactory;
import javax.transaction.xa.XAResource;

import org.apache.commons.lang.SystemUtils;
import org.apache.log4j.Logger;

import com.amalto.connector.interfaces.IXtentisResourceAdapter;
import com.amalto.connector.jca.RecordFactoryImpl;
import com.amalto.connector.jca.XtentisConnectorException;
import com.amalto.connector.smtp.eis.SmtpSender;

/**
 * 
 * @author jfeltesse
 */
public class SmtpResourceAdapter implements IXtentisResourceAdapter, ResourceAdapter {

	
	
	public static String VERSION = "1.0";
	
	private static String configurationFile="mdm.conf";

	
	
	
	
	/****************************************************************************************
	 * Useful methods
	 ****************************************************************************************/
	
	public MappedRecord pull(MappedRecord recordIn) throws XtentisConnectorException { return null; }
	
	
	public MappedRecord push(MappedRecord recordIn) throws XtentisConnectorException {
		
		try {
			
			HashMap<String,Serializable> params = (HashMap<String,Serializable>)recordIn.get(RecordFactoryImpl.PARAMS_HASHMAP_IN);
			
			// might throw a NullPointerExceptions so be sure to check parameters before calling here !!
    		String host = (String) params.get("host");
    		int port = 0;	
    		Object getPort=params.get("port");
    		if(getPort instanceof String){
    			port=Integer.parseInt((String)getPort);
    		}else if(getPort instanceof Integer){
    			port=((Integer)getPort).intValue();
    		}
			String username = (String) params.get("username");
			String password = (String) params.get("password");
			String mails = (String) params.get("mails");
			Boolean auth = (Boolean) params.get("auth");
			String logFileName = (String) params.get("logfilename");
			
			boolean authentication = true;
			if (auth==null) { //the guy forgot to specify authorisation
				if (username==null)
					authentication = false;
				else
					authentication = true;
			} else {
				authentication = auth.booleanValue();
			}
			
			// If the authentication is needed and the username is null, read default username and password from xtentis
			if (authentication && username == null) {
				
				Properties properties = new Properties();
				try { properties.load(new FileInputStream(configurationFile)); }
				catch (Exception e) { throw new XtentisConnectorException("Unable to load the configuration file \""+configurationFile+ "\"", e); }
				
				username = properties.getProperty("connectors.outbound.default.username");
				password = properties.getProperty("connectors.outbound.default.password");			
			}
			
				
			SmtpSender smtp = new SmtpSender(host, port, username, password, authentication, logFileName);
			Logger.getLogger(this.getClass()).debug("push() smtp mail through host " + host);
			smtp.processMails(mails);
			
			MappedRecord response = (new RecordFactoryImpl()).createMappedRecord(RecordFactoryImpl.RECORD_OUT);
			response.put(RecordFactoryImpl.STATUS_CODE_OUT, smtp.getStatusOutCode());
			HashMap<String,Serializable> reshash = new HashMap<String,Serializable>();
			reshash.put("message", smtp.getStatusOutText());
			response.put(RecordFactoryImpl.PARAMS_HASHMAP_OUT, reshash);
			return response;
			
		}
		catch (Exception e) {
			e.printStackTrace();
			throw new XtentisConnectorException(e);
		}
	}
	
	
	
	
	/****************************************************************************************
	 * Implementation of ResourceAdapter
	 ****************************************************************************************/
	
	public String getVersion() { return null; }
	
	public MappedRecord startConnector(MappedRecord recordIn) throws XtentisConnectorException { return null; }
	
	public MappedRecord startFromConfiguration(MappedRecord recordIn) throws XtentisConnectorException { return null; }
	
	public MappedRecord stopConnector(MappedRecord recordIn) throws XtentisConnectorException { return null; }
	
	public MappedRecord getConnectorStatus(MappedRecord recordIn) throws XtentisConnectorException { return null; }
	
	public MappedRecord isXMLServerRunning() throws XtentisConnectorException { return null; }
	
	public MappedRecord getConfiguration() throws XtentisConnectorException { return null; }
	
	public void saveConfiguration() throws XtentisConnectorException { }
	
	public Object onMessage(Object callingConnector, Object message) throws XtentisConnectorException { return null; }
	
	public MappedRecord receiveFromAnotherConnector(MappedRecord recordIn) throws XtentisConnectorException {
	    return push(recordIn);
	}
	
	
	/****************************************************************************************
	 * Implementation of ResourceAdapter
	 ****************************************************************************************/
	
	public void start(BootstrapContext ctx) throws ResourceAdapterInternalException { }
	
	public void stop() { }
	
	public void endpointActivation(MessageEndpointFactory endpointFactory, ActivationSpec spec) throws ResourceException { }
	
	public void endpointDeactivation(MessageEndpointFactory endpointFactory, ActivationSpec spec) { }
	
	public XAResource[] getXAResources(ActivationSpec[] specs) throws ResourceException { return null; }
	
}