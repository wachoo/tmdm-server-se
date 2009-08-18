package com.amalto.webapp.v3.adapter.logging.dwr;

import java.rmi.RemoteException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringEscapeUtils;
import org.w3c.dom.Document;

import com.amalto.webapp.core.util.Util;
import com.amalto.webapp.core.util.dwr.ExtJSFormResponse;
import com.amalto.webapp.core.util.dwr.ExtJSFormSuccessResponse;
import com.amalto.webapp.util.webservices.WSServiceGetConfiguration;
import com.amalto.webapp.util.webservices.WSServicePutConfiguration;




/**
 * 
 * @author bgrieder
 *
 */
public class LoggingSmtpDWR {

	final static String JNDI_NAME = "amalto/local/service/loggingsmtp";
    
    /**
     * DestinationModel.java
     * Constructor
     * 
     */
    public LoggingSmtpDWR() {
        super();
    }

    
   public ExtJSFormResponse loadConfiguration(LoggingSmtpConfigurationBean currentConfiguration) {
    	
    	LoggingSmtpConfigurationBean configuration = new LoggingSmtpConfigurationBean();
        try {
	        String config = 
	        	Util.getPort().getServiceConfiguration(
	        			new WSServiceGetConfiguration(
	        					JNDI_NAME,
	        					null
	        			)
	        	).getValue();
			Document d = Util.parse(config);
			configuration.setLoggingsmtpServer(Util.getFirstTextNode(d.getDocumentElement(),"host"));
			String port = Util.getFirstTextNode(d.getDocumentElement(),"port");
			if (port!=null) configuration.setLoggingsmtpPort(new Integer(port).intValue());
		    configuration.setLoggingsmtpUsername(Util.getFirstTextNode(d.getDocumentElement(), "username"));
		    configuration.setLoggingsmtpPassword(Util.getFirstTextNode(d.getDocumentElement(), "password"));
		    configuration.setLoggingsmtpBCC(Util.getFirstTextNode(d.getDocumentElement(), "permanentbcc"));
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
    

    public ExtJSFormResponse saveConfiguration(LoggingSmtpConfigurationBean configuration) {
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


    
	private static String serializeConfiguration(LoggingSmtpConfigurationBean configuration) throws Exception{
		try {
			String xml ="<configuration>";
			xml+="  <host>"+("".equals(StringEscapeUtils.escapeXml(configuration.getLoggingsmtpServer()))?"":StringEscapeUtils.escapeXml(configuration.getLoggingsmtpServer()))+"</host>";
			xml+="	<port>"+((configuration.getLoggingsmtpPort()<=0)?"":String.valueOf(configuration.getLoggingsmtpPort()))+"</port>";

			xml+=
				" 	<username>"+(("".equals(StringEscapeUtils.escapeXml(configuration.getLoggingsmtpUsername())))?"":StringEscapeUtils.escapeXml(configuration.getLoggingsmtpUsername()))+"</username>"+
				" 	<password>"+(("".equals(StringEscapeUtils.escapeXml(configuration.getLoggingsmtpPassword())))?"":StringEscapeUtils.escapeXml(configuration.getLoggingsmtpPassword()))+"</password>"+
                "   <permanentbcc>"+(("".equals(StringEscapeUtils.escapeXml(configuration.getLoggingsmtpBCC())))?"":StringEscapeUtils.escapeXml(configuration.getLoggingsmtpBCC()).replaceAll("\n",","));
			
		    xml+="</permanentbcc></configuration>";
			return xml;

        } catch (Exception e) {
        	Matcher m = Pattern.compile("(.*Exception:)(.+)",Pattern.DOTALL).matcher(e.getLocalizedMessage());
        	if (m.matches()) throw new Exception(m.group(2));
            throw new Exception(e.getClass().getName()+": "+e.getLocalizedMessage());
        }    	
	}
	
	//
//	public LoggingSmtpDWR getConfiguration() throws Exception{
//        try {
//	        String configuration = 
//	        	Util.getPort().getServiceConfiguration(
//	        			new WSServiceGetConfiguration(
//	        					JNDI_NAME,
//	        					null
//	        			)
//	        	).getValue();
//			Document d = Util.parse(configuration);
//			port = new Integer(Util.getFirstTextNode(d.getDocumentElement(),"port")).intValue();
//			host =Util.getFirstTextNode(d.getDocumentElement(),"host");
//		    username = Util.getFirstTextNode(d.getDocumentElement(), "username");
//		    password = Util.getFirstTextNode(d.getDocumentElement(), "password");
//		    permanentbcc=Util.getFirstTextNode(d.getDocumentElement(), "permanentbcc");
//			return this;
//        } catch (Exception e) {
//        	Matcher m = Pattern.compile("(.*Exception:)(.+)",Pattern.DOTALL).matcher(e.getLocalizedMessage());
//        	if (m.matches()) throw new Exception(m.group(2));
//            throw new Exception(e.getClass().getName()+": "+e.getLocalizedMessage());
//        }
//    }
	
    
//  public String start(LoggingSmtpDWR configuration) throws Exception {  //or restart
//      try {
//      	//save configuration
//      	Util.getPort().putServiceConfiguration(
//      			new WSServicePutConfiguration(
//      					JNDI_NAME,
//      					serializeConfiguration(configuration)
//      			)
//      	);
//      	//perform start / restart
//	        return
//	        	Util.getPort().serviceAction(
//	        			new WSServiceAction(
//	        					JNDI_NAME,
//	        					WSServiceActionCode.START,
//	        					null,null
//	        			)
//	        	).getValue();
//      } catch (Exception e) {
//      	// m = Pattern.compile("(.*Exception:)(.+)",Pattern.DOTALL).matcher(e.getLocalizedMessage());
//      	//if (m.matches()) throw new Exception(m.group(2));
//          throw new Exception(e.getClass().getName()+": "+e.getLocalizedMessage());
//      }    	
//  }
  

}