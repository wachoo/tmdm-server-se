package com.amalto.webapp.v3.adapter.smtp.dwr;

import java.rmi.RemoteException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringEscapeUtils;
import org.w3c.dom.Document;

import com.amalto.webapp.core.util.Util;
import com.amalto.webapp.core.util.XtentisWebappException;
import com.amalto.webapp.core.util.dwr.ExtJSFormResponse;
import com.amalto.webapp.core.util.dwr.ExtJSFormSuccessResponse;
import com.amalto.webapp.util.webservices.WSServiceAction;
import com.amalto.webapp.util.webservices.WSServiceActionCode;
import com.amalto.webapp.util.webservices.WSServiceGetConfiguration;
import com.amalto.webapp.util.webservices.WSServicePutConfiguration;




/**
 * 
 * @author bgrieder
 *
 */
public class SmtpServiceDWR {

	final static String JNDI_NAME = "amalto/local/service/smtp";
    
    /**
     * DestinationModel.java
     * Constructor
     * 
     */
    public SmtpServiceDWR() {
        super();
    }

    

	/************************************************************************************
     * Ajax methods
     ************************************************************************************/
    
    public ExtJSFormResponse saveConfiguration(SmtpConfigurationBean configuration) {
      	try {
        	Util.getPort().putServiceConfiguration(
        			new WSServicePutConfiguration(
        					JNDI_NAME,
        					serializeConfiguration(configuration)
        			)
        	);
        } catch (RemoteException e) {
        	throw new RuntimeException(e.getLocalizedMessage());
        } catch (Exception e) {
        	if (e.getCause()==null)
        		throw new RuntimeException(e.getClass().getName()+": "+e.getLocalizedMessage());
        	else
        		throw new RuntimeException(e.getCause().getClass().getName()+": "+e.getCause().getLocalizedMessage());
        }    	
        return new ExtJSFormSuccessResponse("Configuration sucessfully saved. Please restart the adapter");
    	
    }
    
    public ExtJSFormResponse loadConfiguration(SmtpConfigurationBean currentConfiguration) {
    	
    	SmtpConfigurationBean configuration = new SmtpConfigurationBean();


        try {
	        String config = 
	        	Util.getPort().getServiceConfiguration(
	        			new WSServiceGetConfiguration(
	        					JNDI_NAME,
	        					null
	        			)
	        	).getValue();
			Document d = Util.parse(config);
			configuration.setSmtpServer(Util.getFirstTextNode(d.getDocumentElement(),"host"));
			String port = Util.getFirstTextNode(d.getDocumentElement(),"port");
			if (port!=null) configuration.setSmtpPort(new Integer(port).intValue());
		    configuration.setSmtpUsername(Util.getFirstTextNode(d.getDocumentElement(), "username"));
		    configuration.setSmtpPassword(Util.getFirstTextNode(d.getDocumentElement(), "password"));
		    configuration.setSmtpBCC(Util.getFirstTextNode(d.getDocumentElement(), "permanentbcc"));
	    	return new ExtJSFormSuccessResponse("",configuration);

        } catch (RemoteException e) {
        	throw new RuntimeException(e.getLocalizedMessage());
        } catch (Exception e) {
        	if (e.getCause()==null)
        		throw new RuntimeException(e.getClass().getName()+": "+e.getLocalizedMessage());
        	else
        		throw new RuntimeException(e.getCause().getClass().getName()+": "+e.getCause().getLocalizedMessage());
        }    	

    }
    
    public String getStatus() {
        try {
	        String status = 
	        	Util.getPort().serviceAction(
	        			new WSServiceAction(
	        					JNDI_NAME,
	        					WSServiceActionCode.STATUS,
	        					null,
	        					null
	        			)
	        	).getValue();
	        if ("RUNNING".equals(status) || "OK".equals(status)) {
	        	return "OK";
	        }
	        return status;
        } catch (RemoteException e) {
        	throw new RuntimeException(e.getLocalizedMessage());
        } catch (Exception e) {
        	if (e.getCause()==null)
        		throw new RuntimeException(e.getClass().getName()+": "+e.getLocalizedMessage());
        	else
        		throw new RuntimeException(e.getCause().getClass().getName()+": "+e.getCause().getLocalizedMessage());
        }    	
	
    }
    
    public String start()  {  //or restart
        try {

	        return
	        	Util.getPort().serviceAction(
	        			new WSServiceAction(
	        					JNDI_NAME,
	        					WSServiceActionCode.START,
	        					null,
	        					null
	        			)
	        	).getValue();
        } catch (RemoteException e) {
        	throw new RuntimeException(e.getLocalizedMessage());
        } catch (Exception e) {
        	if (e.getCause()==null)
        		throw new RuntimeException(e.getClass().getName()+": "+e.getLocalizedMessage());
        	else
        		throw new RuntimeException(e.getCause().getClass().getName()+": "+e.getCause().getLocalizedMessage());
        }    	
    }
    
    
	private static String serializeConfiguration(SmtpConfigurationBean configuration) throws Exception{
		try {
			String xml ="<configuration>";
			xml+="  <host>"+("".equals(StringEscapeUtils.escapeXml(configuration.getSmtpServer()))?"":StringEscapeUtils.escapeXml(configuration.getSmtpServer()))+"</host>";
			xml+="	<port>"+((configuration.getSmtpPort()<=0)?"":String.valueOf(configuration.getSmtpPort()))+"</port>";

			xml+=
				" 	<username>"+(("".equals(StringEscapeUtils.escapeXml(configuration.getSmtpUsername())))?"":StringEscapeUtils.escapeXml(configuration.getSmtpUsername()))+"</username>"+
				" 	<password>"+(("".equals(StringEscapeUtils.escapeXml(configuration.getSmtpPassword())))?"":StringEscapeUtils.escapeXml(configuration.getSmtpPassword()))+"</password>"+
                "   <permanentbcc>"+(("".equals(StringEscapeUtils.escapeXml(configuration.getSmtpBCC())))?"":StringEscapeUtils.escapeXml(configuration.getSmtpBCC()).replaceAll("\n",","));
			
		    xml+="</permanentbcc></configuration>";
			return xml;

        } catch (Exception e) {
        	Matcher m = Pattern.compile("(.*Exception:)(.+)",Pattern.DOTALL).matcher(e.getLocalizedMessage());
        	if (m.matches()) throw new Exception(m.group(2));
            throw new Exception(e.getClass().getName()+": "+e.getLocalizedMessage());
        }    	
	}
	
	
    public String stop() {  //or restart
        try {
	        return
	        	Util.getPort().serviceAction(
	        			new WSServiceAction(
	        					JNDI_NAME,
	        					WSServiceActionCode.STOP,
	        					null,
	        					null
	        			)
	        	).getValue();
        } catch (RemoteException e) {
        	throw new RuntimeException(e.getLocalizedMessage());
        } catch (Exception e) {
        	if (e.getCause()==null)
        		throw new RuntimeException(e.getClass().getName()+": "+e.getLocalizedMessage());
        	else
        		throw new RuntimeException(e.getCause().getClass().getName()+": "+e.getCause().getLocalizedMessage());
        }    	
 	
    }
    
    public boolean sendSampleEmail(String from,String to,String cc,String bcc,String subject,String body) {
    	
    	String[] params={from,to,cc,bcc,subject,body};
    	String returnStatus="";
    	
    	try {
    		
    	returnStatus=Util.getPort().serviceAction(
					new WSServiceAction(
							JNDI_NAME,
							WSServiceActionCode.EXECUTE,
							"sendSimpleMail",
							params
					)
			).getValue();
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (XtentisWebappException e) {
			e.printStackTrace();
		}
    	
    	if(returnStatus!=null&&returnStatus.equals("Success")){
			return true;
		}else{
			return false;
		}
    }
  
}