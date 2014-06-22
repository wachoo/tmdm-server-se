package com.amalto.service.logging.ejb;

import java.io.Serializable;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.ejb.EJBException;
import javax.ejb.SessionBean;
import javax.resource.cci.Connection;
import javax.resource.cci.Interaction;
import javax.resource.cci.MappedRecord;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.Priority;
import org.w3c.dom.Document;

import com.amalto.connector.jca.InteractionSpecImpl;
import com.amalto.connector.jca.RecordFactoryImpl;
import com.amalto.core.ejb.ItemPOJO;
import com.amalto.core.ejb.ItemPOJOPK;
import com.amalto.core.ejb.ServiceCtrlBean;
import com.amalto.core.ejb.local.ItemCtrl2Local;
import com.amalto.core.objects.datacluster.ejb.DataClusterPOJOPK;
import com.amalto.core.util.Util;
import com.amalto.core.util.XtentisException;



/**
 * @author bgrieder, jfeltesse
 * 
 * @ejb.bean name="Logging"
 *           display-name="Name for Logging"
 *           description="Description for Logging"
 * 		  local-jndi-name = "amalto/local/service/logging"
 *           type="Stateless"
 *           view-type="local"
 * 
 * @ejb.remote-facade
 * 
 * @ejb.permission
 * 	view-type = "remote"
 * 	role-name = "administration"
 * @ejb.permission
 * 	view-type = "local"
 * 	unchecked = "true"
 * 
 * 
 * 
 */
public class LoggingServiceBean extends ServiceCtrlBean  implements SessionBean{
	
	private static final long serialVersionUID = 7146969238534906425L;
	
	private boolean configurationLoaded = false;
	
	private Integer port;
	private Integer threshold;
	private String pattern;
	
	// Default null values for this version
	private String xtentisusername;
	private String xtentispassword;
	private String logfilename;
	
		
    
    /**
     * @throws EJBException
     * 
     * @ejb.interface-method view-type = "local"
     * @ejb.facade-method 
     */
	public String getJNDIName() throws XtentisException {		
		return "amalto/local/service/logging";
	}
	
    /**
     * @throws EJBException
     * 
     * @ejb.interface-method view-type = "local"
     * @ejb.facade-method 
     */
	public String getDescription(String twoLetterLanguageCode) throws XtentisException {
		if ("fr".matches(twoLetterLanguageCode.toLowerCase()))
			return "Le service de logging";
		return "The logging service";
	}
    /**
     * @author achen
     * @throws XtentisException
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method 
     */
    public  String getDocumentation(String twoLettersLanguageCode) throws XtentisException{
    	return "This service main role is to start, stop and confgure the logging connector in webapp. \n"+
    	"It is not meant to be called from a Routing Rule.";
    }
    /**
     * @throws EJBException
     * 
     * @ejb.interface-method view-type = "local"
     * @ejb.facade-method 
     */
	public String getStatus() throws XtentisException {
		
		Connection conx = null;
		
		try {
			
			//Check that listener is started
			conx = getConnection("java:jca/xtentis/connector/logging");
			Interaction interaction = conx.createInteraction();
	    	InteractionSpecImpl interactionSpec = new InteractionSpecImpl();
	    	
			MappedRecord recordIn = new RecordFactoryImpl().createMappedRecord(RecordFactoryImpl.RECORD_IN);
	    	HashMap<String,Serializable> params = new HashMap<String,Serializable>();
	    	params.put("port", port);
	    	recordIn.put(RecordFactoryImpl.PARAMS_HASHMAP_IN, params);
	    	
			interactionSpec.setFunctionName(InteractionSpecImpl.FUNCTION_GET_STATUS);
			MappedRecord recordOut = (MappedRecord)interaction.execute(interactionSpec, recordIn);
			String code = (String)recordOut.get(RecordFactoryImpl.STATUS_CODE_OUT);
			
			Logger.getLogger(this.getClass()).debug("getStatus(): code="+code);
			
			if (! "OK".equals(code)) return "STOPPED";
			else return "OK";

		} catch (Exception e) {
			String err = "Could not get the status of the Logging Service: "+e.getClass().getName()+": "+e.getLocalizedMessage();
			org.apache.log4j.Logger.getLogger(this.getClass()).error("getStatus(): "+err);
			throw new XtentisException(e);
		} finally {
			try { conx.close();} catch (Exception e) {};
		}

	}

	
    /**
     * @throws EJBException
     * 
     * @ejb.interface-method view-type = "local"
     * @ejb.facade-method 
     */
	public void start() throws XtentisException {
		
		Connection conx = null;
		try {
		
			if (!configurationLoaded) getConfiguration(null);
			
			//DEPRECATED: the connector supplies a logging_event in the appropriate format
			//check that the inbound adaptor exists
			/*
			InboundAdaptorLocalHome iaHome = getInboundAdaptorLocalHome();
			InboundAdaptorLocal ia = iaHome.findIfExists(new InboundAdaptorPK("Logging"));
			if (ia == null) {
				InputStream is = LoggingServiceBean.class.getResourceAsStream("Logging-Inbound.xslt");
			    BufferedReader br = new BufferedReader(new InputStreamReader(is, "utf-8"));
			    String xsl="";
		        String line;
		        while ((line=br.readLine())!=null) xsl+=line+"\n";

		        //create Source
		        SourceValue sv = getSourceLocalHome().create(new SourceValue("JBoss Logger", "Jboss server logs")).getSourceValue();
		        
		        
		        InboundAdaptorValue iavo = new InboundAdaptorValue(
        				"Logging",
        				"Logging Inbound",
        				xsl,
        				null
        		);
		        //get Data Model
		        iavo.setDataModel(getDataModelLocalHome().findByPrimaryKey(new DataModelPK("B2BOX")).getDataModelValue());
		        iavo.setSource(sv);
		        
		        iaHome.create(iavo);
			} // inbound adaptor
			*/
			
			//Restart the listener
			conx  = getConnection("java:jca/xtentis/connector/logging");
			Interaction interaction = conx.createInteraction();
	    	InteractionSpecImpl interactionSpec = new InteractionSpecImpl();
			MappedRecord recordIn = new RecordFactoryImpl().createMappedRecord(RecordFactoryImpl.RECORD_IN);
			HashMap<String,Serializable> params = new HashMap<String,Serializable>();
	    	recordIn.put(RecordFactoryImpl.PARAMS_HASHMAP_IN, params);
			
			//stop the listener
			interactionSpec.setFunctionName(InteractionSpecImpl.FUNCTION_STOP);
			try {interaction.execute(interactionSpec, recordIn); } catch (Exception e) {}
			
			//start the listener
	    	params.put("port", port);
	    	params.put("threshold", threshold);
	    	params.put("pattern", pattern);
	    	params.put("xtentisusername", xtentisusername);
	    	params.put("xtentispassword", xtentispassword);
	    	params.put("logfilename", logfilename);
	    	params.put("servicename", "logging");
	    	recordIn.put(RecordFactoryImpl.PARAMS_HASHMAP_IN, params);
			interactionSpec.setFunctionName(InteractionSpecImpl.FUNCTION_START);
			MappedRecord result = (MappedRecord)interaction.execute(interactionSpec, recordIn);
			
			//check the result
			if (!"OK".equals(result.get(RecordFactoryImpl.STATUS_CODE_OUT))) {
				String message = (String)((HashMap<String,Serializable>)result.get(RecordFactoryImpl.PARAMS_HASHMAP_OUT)).get("message");
				String err = "Logging Service: could not start the listener on port: "+ (port != null ? port.toString() : "null") +": "+message;
				org.apache.log4j.Logger.getLogger(this.getClass()).error("start() "+err);
				throw new XtentisException(err);
			}
			return;
		} catch (XtentisException xe) {
			throw (xe);
		} catch (Exception e) {
			e.printStackTrace();
			String err = "Could not start the Logging service:"+
				e.getClass().getName()+": "+e.getLocalizedMessage();
			org.apache.log4j.Logger.getLogger(this.getClass()).error("start() "+err);
			throw new XtentisException(e);
		} finally {
			try {conx.close();} catch (Exception e){}
		}
	}


	
    /**
     * @throws EJBException
     * 
     * @ejb.interface-method view-type = "local"
     * @ejb.facade-method 
     */
	public void stop() throws XtentisException {
		
		Logger.getLogger(this.getClass()).debug("stop() : SERVICE: STOP");
		
		Connection conx = null;
		
		try {
			
			if (!configurationLoaded) getConfiguration(null);
			
			//Try to stop the  port
			conx  = getConnection("java:jca/xtentis/connector/logging");
			Interaction interaction = conx.createInteraction();
	    	InteractionSpecImpl interactionSpec = new InteractionSpecImpl();
	    	
			MappedRecord recordIn = new RecordFactoryImpl().createMappedRecord(RecordFactoryImpl.RECORD_IN);
	    	HashMap<String,Serializable> params = new HashMap<String,Serializable>();
	    	params.put("port", port);
	    	recordIn.put(RecordFactoryImpl.PARAMS_HASHMAP_IN, params);
			interactionSpec.setFunctionName(InteractionSpecImpl.FUNCTION_STOP);
			interaction.execute(interactionSpec, recordIn);
			return;
			
		}
		catch (XtentisException e) { throw (e); }
		catch (Exception e) {
			String err = "Could not stop the Logging service:"+e.getClass().getName()+": "+e.getLocalizedMessage();
			org.apache.log4j.Logger.getLogger(this.getClass()).error("stop() "+err);
			throw new XtentisException(e);
		} finally {
			try {conx.close();} catch (Exception e){}
		}


	}


	private final static Pattern timePattern = Pattern.compile(".*?<time>(.*?)</time>.*",Pattern.DOTALL);
    /**
     * @throws EJBException
     * 
     * @ejb.interface-method view-type = "local"
     * @ejb.facade-method 
     */
	public Serializable receiveFromOutbound(HashMap<String, Serializable> map) throws XtentisException {
		
		try {
			
			org.apache.log4j.Logger.getLogger(this.getClass()).debug("receiveFromOutbound() ");
			
			String charset = (String) map.get("charset");
			String cluster = (String) map.get("cluster");
			byte[] bytes = (byte[])map.get("bytes");
			String logging_event = new String(bytes, charset);
			
			//gréb time
			String id = ""+System.currentTimeMillis();
			Matcher m = timePattern.matcher(logging_event);
			if (m.matches()) {
				id = m.group(1);
			}
			
			//build the ItemPOJO
			ItemPOJO pojo =
				new ItemPOJO(
						new DataClusterPOJOPK(cluster),
						"logging_event",
						new String[]{id},
						System.currentTimeMillis(),
						logging_event
				);
			
			// project to repository
			ItemCtrl2Local ictrl = Util.getItemCtrl2Local();
			
			ictrl.putItem(
					pojo,
					null //no data model - we know what we are doing right?  
			);
			
			/*
			ItemPOJO pojo = ictrl.projectItem(
					new String(bytes, charset),
					null,	//Document PK
					new InboundAdaptorPK("Logging"),
					new DataClusterPK(cluster),
					create,
					update,
					username
			);
			*/
			
			
			//send to router
			Util.getRoutingEngineV2CtrlLocal().route(pojo.getItemPOJOPK());
			
			return null;
			
		} catch (XtentisException e) {
			throw(e);
		} catch (Exception e) {
			//cannot trigger an error in the log --> infinite loop
			String err = "Logging Event Processing Service ERROR: unable to process the logging event "+e.getClass().getName()+" : "+e.getMessage();
			org.apache.log4j.Logger.getLogger(this.getClass()).info("receiveFromOutbound() "+err);
			e.printStackTrace();
			throw new XtentisException(err);
		}
	}



	
    /**
     * @throws EJBException
     * 
     * @ejb.interface-method view-type = "local"
     * @ejb.facade-method 
     */
	public String receiveFromInbound(ItemPOJOPK itemPK, String routingOrderID, String parameters) throws com.amalto.core.util.XtentisException {
		// Not needed for the logging service
		return null;
	}

    /**
    *
    * @get the default configuration
    * @throws EJBException
    *
    * @ejb.interface-method view-type = "local"
    * @ejb.facade-method
    */
    
    public String getDefaultConfiguration() {
    	return
    		"<configuration>"+
    		"	<port>4561</port>"+
    		"	<threshold>"+Priority.ERROR_INT+"</threshold>"+
    		"	<pattern>com\\.amalto\\..*</pattern>"+
    		"	<xtentisusername>admin</xtentisusername>"+
    		"	<xtentispassword>talend</xtentispassword>"+
    		"	<logfilename></logfilename>"+
			"</configuration>";
    }
    

    
    /**
     * @throws EJBException
     * 
     * @ejb.interface-method view-type = "local"
     * @ejb.facade-method 
     */
    public String getConfiguration(String optionalParameters) throws XtentisException {
    	
    	try {
    		
    		Logger.getLogger(this.getClass()).debug("getConfiguration() : ");
    		
    		String configuration = loadConfiguration();
    		if (configuration == null) {
    			Logger.getLogger(this.getClass()).debug("getConfiguration() : configuration is null, falling back to default one");
    			configuration = getDefaultConfiguration();
    		}
    		
    		Document d = Util.parse(configuration);
    		
    		// Parsing & checking of mandatory parameters
    		String tmpport = Util.getFirstTextNode(d.getDocumentElement(), "port");
    		if  (tmpport == null) throw new XtentisException("Port number required");
    		else this.port = new Integer(tmpport);
    		if (this.port.intValue() < 1) throw new XtentisException("Invalid port number");
    		
    		String tmpthreshold = Util.getFirstTextNode(d.getDocumentElement(), "threshold");
    		if  (tmpthreshold == null) throw new XtentisException("Threshold required");
    		else this.threshold = new Integer(tmpthreshold);    		
    		   		
    		// Parsing of other parameters
    		String tmppattern = StringEscapeUtils.unescapeXml(Util.getFirstTextNode(d.getDocumentElement(), "pattern"));
    		if  (tmppattern == null) {
    			Logger.getLogger(this.getClass()).debug("getConfiguration() : Pattern is null, using default one");
    			this.pattern = "com\\.amalto\\..*";
    		}
    		else this.pattern = tmppattern;
    		
    		xtentisusername = StringEscapeUtils.unescapeXml(Util.getFirstTextNode(d.getDocumentElement(), "xtentisusername"));
    		xtentispassword = StringEscapeUtils.unescapeXml(Util.getFirstTextNode(d.getDocumentElement(), "xtentispassword"));
		    logfilename = StringEscapeUtils.unescapeXml(Util.getFirstTextNode(d.getDocumentElement(), "logfilename"));
		    
    		configurationLoaded = true;
    		
    		Logger.getLogger(this.getClass()).debug("getConfiguration() : Configuration String: "+configuration);
    		Logger.getLogger(this.getClass()).debug("getConfiguration() : Variables: port="+port+", threshold="+threshold+", " + 
    				"pattern="+pattern+", xtentisusername="+xtentisusername+", xtentispassword="+(xtentispassword == null ? "null" : "(hidden)")+", logfilename="+logfilename);
    		
    		return configuration;
        }
    	catch (XtentisException e) { 
    		e.printStackTrace();
    		throw (e); }
        catch (Exception e) {
    	    String err = "Unable to deserialize the configuration of the Logging Service: "+e.getClass().getName()+": "+e.getLocalizedMessage();
    	    org.apache.log4j.Logger.getLogger(this.getClass()).error(err,e);
    	    throw new XtentisException(err);
	    }
    }
    

    
    /**
     * @throws EJBException
     * 
     * @ejb.interface-method view-type = "local"
     * @ejb.facade-method 
     */
	public void putConfiguration(String configuration) throws XtentisException {
		configurationLoaded = false;
		super.putConfiguration(configuration);
	}
	
	/**
	 * @throws EJBException
	 *
	 * @ejb.interface-method view-type = "local"
	 * @ejb.facade-method
	 */
	 public Serializable fetchFromOutbound(String command, String parameters,String schedulePlanID) throws XtentisException {
			// N/A
			return null;
	 }

    
}