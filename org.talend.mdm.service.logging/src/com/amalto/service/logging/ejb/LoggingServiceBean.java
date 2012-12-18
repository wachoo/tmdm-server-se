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
	
    private final static Pattern timePattern = Pattern.compile(".*?<time>(.*?)</time>.*",Pattern.DOTALL); //$NON-NLS-1$

    private static final Logger LOGGER = Logger.getLogger(LoggingServiceBean.class);
    
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
		return "amalto/local/service/logging"; //$NON-NLS-1$
	}
	
    /**
     * @throws EJBException
     * 
     * @ejb.interface-method view-type = "local"
     * @ejb.facade-method 
     */
	public String getDescription(String twoLetterLanguageCode) throws XtentisException {
		if ("fr".matches(twoLetterLanguageCode.toLowerCase())) { //$NON-NLS-1$
            return "Le service de logging";
        }
        return "The logging service";
	}
    /**
     * @throws XtentisException
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method 
     */
    public  String getDocumentation(String twoLettersLanguageCode) throws XtentisException{
    	return "This service main role is to start, stop and configure the logging connector in webapp. \n"+
    	"It is not meant to be called from a Routing Rule.";
    }
    /**
     * @throws EJBException
     * 
     * @ejb.interface-method view-type = "local"
     * @ejb.facade-method 
     */
	public String getStatus() throws XtentisException {
		Connection connection = null;
		try {
			//Check that listener is started
			connection = getConnection("java:jca/xtentis/connector/logging"); //$NON-NLS-1$
			Interaction interaction = connection.createInteraction();
	    	InteractionSpecImpl interactionSpec = new InteractionSpecImpl();
			MappedRecord recordIn = new RecordFactoryImpl().createMappedRecord(RecordFactoryImpl.RECORD_IN);
	    	HashMap<String,Serializable> params = new HashMap<String,Serializable>();
	    	params.put("port", port); //$NON-NLS-1$
	    	recordIn.put(RecordFactoryImpl.PARAMS_HASHMAP_IN, params);
			interactionSpec.setFunctionName(InteractionSpecImpl.FUNCTION_GET_STATUS);
			MappedRecord recordOut = (MappedRecord)interaction.execute(interactionSpec, recordIn);
			String code = (String)recordOut.get(RecordFactoryImpl.STATUS_CODE_OUT);
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("getStatus(): code="+code);
            }
            if (!"OK".equals(code)) {
                return "STOPPED"; //$NON-NLS-1$
            } else {
                return "OK"; //$NON-NLS-1$
            }
        } catch (Exception e) {
			LOGGER.error("Could not get the status of the Logging Service.", e);
			throw new XtentisException(e);
		} finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (Exception e) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Could not close connection.", e);
                }
            }
            
        }

	}

	
    /**
     * @throws EJBException
     * 
     * @ejb.interface-method view-type = "local"
     * @ejb.facade-method 
     */
	public void start() throws XtentisException {
		Connection connection = null;
		try {
			if (!configurationLoaded) {
                getConfiguration(null);
            }
            //Restart the listener
			connection  = getConnection("java:jca/xtentis/connector/logging"); //$NON-NLS-1$
			Interaction interaction = connection.createInteraction();
	    	InteractionSpecImpl interactionSpec = new InteractionSpecImpl();
			MappedRecord recordIn = new RecordFactoryImpl().createMappedRecord(RecordFactoryImpl.RECORD_IN);
			HashMap<String,Serializable> params = new HashMap<String,Serializable>();
	    	recordIn.put(RecordFactoryImpl.PARAMS_HASHMAP_IN, params);
			//stop the listener
			interactionSpec.setFunctionName(InteractionSpecImpl.FUNCTION_STOP);
            try {
                interaction.execute(interactionSpec, recordIn);
            } catch (Exception e) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Interaction execution error.", e);
                }
            }
            //start the listener
	    	params.put("port", port); //$NON-NLS-1$
	    	params.put("threshold", threshold); //$NON-NLS-1$
	    	params.put("pattern", pattern); //$NON-NLS-1$
	    	params.put("xtentisusername", xtentisusername); //$NON-NLS-1$
	    	params.put("xtentispassword", xtentispassword); //$NON-NLS-1$
	    	params.put("logfilename", logfilename); //$NON-NLS-1$
	    	params.put("servicename", "logging"); //$NON-NLS-1$ //$NON-NLS-2$
	    	recordIn.put(RecordFactoryImpl.PARAMS_HASHMAP_IN, params);
			interactionSpec.setFunctionName(InteractionSpecImpl.FUNCTION_START);
			MappedRecord result = (MappedRecord)interaction.execute(interactionSpec, recordIn);
			
			//check the result
			if (!"OK".equals(result.get(RecordFactoryImpl.STATUS_CODE_OUT))) { //$NON-NLS-1$
				String message = (String)((HashMap<String,Serializable>)result.get(RecordFactoryImpl.PARAMS_HASHMAP_OUT)).get("message"); //$NON-NLS-1$
				String err = "Logging Service: could not start the listener on port: "+ (port != null ? port.toString() : "null") +": "+message;
				LOGGER.error("start() "+err);
				throw new XtentisException(err);
			}
		} catch (XtentisException xe) {
			throw (xe);
		} catch (Exception e) {
			LOGGER.error("Could not start the Logging service: ", e);
			throw new XtentisException(e);
		} finally {
            try {
                connection.close();
            } catch (Exception e) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Could not close connection.", e);
                }
            }
        }
	}

    /**
     * @throws EJBException
     * 
     * @ejb.interface-method view-type = "local"
     * @ejb.facade-method 
     */
	public void stop() throws XtentisException {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("stop() : SERVICE: STOP");
        }
        Connection connection = null;
        try {
            if (!configurationLoaded) {
                getConfiguration(null);
            }
            //Try to stop the  port
            connection = getConnection("java:jca/xtentis/connector/logging"); //$NON-NLS-1$
            Interaction interaction = connection.createInteraction();
            InteractionSpecImpl interactionSpec = new InteractionSpecImpl();

            MappedRecord recordIn = new RecordFactoryImpl().createMappedRecord(RecordFactoryImpl.RECORD_IN);
            HashMap<String, Serializable> params = new HashMap<String, Serializable>();
            params.put("port", port); //$NON-NLS-1$
            recordIn.put(RecordFactoryImpl.PARAMS_HASHMAP_IN, params);
            interactionSpec.setFunctionName(InteractionSpecImpl.FUNCTION_STOP);
            interaction.execute(interactionSpec, recordIn);
        } catch (XtentisException e) {
            throw (e);
        } catch (Exception e) {
            LOGGER.error("Could not stop the Logging service.", e);
            throw new XtentisException(e);
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (Exception e) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Could not close connection.", e);
                }
            }
        }
    }

    /**
     * @throws EJBException
     * 
     * @ejb.interface-method view-type = "local"
     * @ejb.facade-method 
     */
	public Serializable receiveFromOutbound(HashMap<String, Serializable> map) throws XtentisException {
		
		try {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("receiveFromOutbound() ");
            }
            String charset = (String) map.get("charset"); //$NON-NLS-1$
            String cluster = (String) map.get("cluster"); //$NON-NLS-1$
            byte[] bytes = (byte[]) map.get("bytes"); //$NON-NLS-1$
            String logging_event = new String(bytes, charset);
            //grab time
            String id = "" + System.currentTimeMillis(); //$NON-NLS-1$
            Matcher m = timePattern.matcher(logging_event);
            if (m.matches()) {
                id = m.group(1);
            }
            //build the ItemPOJO
            ItemPOJO pojo = new ItemPOJO(
                            new DataClusterPOJOPK(cluster),
                            "logging_event", //$NON-NLS-1$
                            new String[]{id},
                            System.currentTimeMillis(),
                            logging_event
                    );

            // project to repository
            ItemCtrl2Local itemCtrlLocal = Util.getItemCtrl2Local();
            itemCtrlLocal.putItem(
                    pojo,
                    null //no data model - we know what we are doing right?
            );
            //send to router
            Util.getRoutingEngineV2CtrlLocal().route(pojo.getItemPOJOPK());
            return null;
        } catch (XtentisException e) {
			throw(e);
		} catch (Exception e) {
			//cannot trigger an error in the log --> infinite loop
			String err = "Logging Event Processing Service ERROR: unable to process the logging event "+e.getClass().getName()+" : "+e.getMessage();
			LOGGER.info("receiveFromOutbound() ", e);
			throw new XtentisException(err, e);
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
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("getConfiguration() : ");
            }
            String configuration = loadConfiguration();
            if (configuration == null) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("getConfiguration() : configuration is null, falling back to default one");
                }
                configuration = getDefaultConfiguration();
            }
            Document d = Util.parse(configuration);
            // Parsing & checking of mandatory parameters
            String tmpPort = Util.getFirstTextNode(d.getDocumentElement(), "port"); //$NON-NLS-1$
            if (tmpPort == null) {
                throw new XtentisException("Port number required");
            } else this.port = new Integer(tmpPort);
            if (this.port < 1) {
                throw new XtentisException("Invalid port number");
            }
            String tmpThreshold = Util.getFirstTextNode(d.getDocumentElement(), "threshold"); //$NON-NLS-1$
            if (tmpThreshold == null) {
                throw new XtentisException("Threshold required");
            } else {
                this.threshold = new Integer(tmpThreshold);
            }
            // Parsing of other parameters
            String tmpPattern = StringEscapeUtils.unescapeXml(Util.getFirstTextNode(d.getDocumentElement(), "pattern")); //$NON-NLS-1$
            if (tmpPattern == null) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("getConfiguration() : Pattern is null, using default one");
                }
                this.pattern = "com\\.amalto\\..*"; //$NON-NLS-1$
            } else {
                this.pattern = tmpPattern;
            }
            xtentisusername = StringEscapeUtils.unescapeXml(Util.getFirstTextNode(d.getDocumentElement(), "xtentisusername")); //$NON-NLS-1$
            xtentispassword = StringEscapeUtils.unescapeXml(Util.getFirstTextNode(d.getDocumentElement(), "xtentispassword")); //$NON-NLS-1$
            logfilename = StringEscapeUtils.unescapeXml(Util.getFirstTextNode(d.getDocumentElement(), "logfilename")); //$NON-NLS-1$
            configurationLoaded = true;
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("getConfiguration() : Configuration String: " + configuration);
                LOGGER.debug("getConfiguration() : Variables: port=" + port + ", threshold=" + threshold + ", " +
                        "pattern=" + pattern + ", xtentisusername=" + xtentisusername + ", xtentispassword=" + (xtentispassword == null ? "null" : "(hidden)") + ", logfilename=" + logfilename);
            }
            return configuration;
        } catch (XtentisException e) {
            e.printStackTrace();
            throw (e);
        } catch (Exception e) {
            String err = "Unable to deserialize the configuration of the Logging Service: " + e.getClass().getName() + ": " + e.getLocalizedMessage();
            LOGGER.error(err, e);
            throw new XtentisException(err, e);
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
     * @ejb.interface-method view-type = "local"
     * @ejb.facade-method
     */
    public Serializable fetchFromOutbound(String command, String parameters, String schedulePlanID) throws XtentisException {
        // N/A
        return null;
    }


}