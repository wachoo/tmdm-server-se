package com.amalto.webapp.core.dwr;

import java.io.StringReader;
import java.io.StringWriter;
import java.rmi.RemoteException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.Marshaller;
import org.exolab.castor.xml.Unmarshaller;
import org.exolab.castor.xml.ValidationException;
import org.xml.sax.InputSource;

import com.amalto.webapp.core.util.Util;
import com.amalto.webapp.core.util.XtentisWebappException;
import com.amalto.webapp.util.webservices.WSServiceAction;
import com.amalto.webapp.util.webservices.WSServiceActionCode;
import com.amalto.webapp.util.webservices.WSServicePutConfiguration;



public abstract class ServiceDWR {
	
	private String JNDI_NAME = "";
	private String serviceName = "";


	
	public ServiceDWR(String JNDI_NAME, String serviceName) {
		this.JNDI_NAME = JNDI_NAME;
		this.serviceName = serviceName;

	}

	//TODO remove RemoteException in Util
	public String start() throws RemoteException, XtentisWebappException{
    	return Util.getPort().serviceAction(
    			new WSServiceAction(
    					JNDI_NAME,
    					WSServiceActionCode.STOP,
    					null,
    					null
    			)
    	).getValue();
	}
	
	public String stop() throws RemoteException, XtentisWebappException{
    	return Util.getPort().serviceAction(
    			new WSServiceAction(
    					JNDI_NAME,
    					WSServiceActionCode.STOP,
    					null,
    					null
    			)
    	).getValue();
	}
	
	public String getStatus() throws Exception{
        try {
	        return  
	        	Util.getPort().serviceAction(
	        			new WSServiceAction(
	        					JNDI_NAME,
	        					WSServiceActionCode.STATUS,
	        					null,
	        					null
	        			)
	        	).getValue();
        } catch (Exception e) {
        	Matcher m = Pattern.compile("(.*Exception:)(.+)",Pattern.DOTALL).matcher(e.getLocalizedMessage());
        	if ((m!=null) &&  (m.matches())) throw new Exception(m.group(2));
            throw new Exception(e.getClass().getName()+": "+e.getLocalizedMessage());
        }    	
	}
	
	public String putConfiguration(Object configuration ) throws XtentisWebappException, MarshalException, ValidationException {
    	
    	StringWriter sw = new StringWriter();
    	try {
    		Marshaller.marshal(configuration, sw);
	    } catch (Exception e) {
    	    String err = "Unable to serialize the configuration of the "+serviceName+" Service"
    	    		+": "+e.getClass().getName()+": "+e.getLocalizedMessage();
    	    org.apache.log4j.Category.getInstance(this.getClass()).error(err);
    	    throw new XtentisWebappException(err);
	    }		    
	    
	   	try {    	
			Util.getPort().putServiceConfiguration(
					new WSServicePutConfiguration(
							JNDI_NAME,
							sw.toString()
					)
			);	
			start();	
		} catch (Exception e) {
			throw new XtentisWebappException("Unable to put the configuration: "+e.getClass().getName()+": "+e.getLocalizedMessage());
		}
		return 	"OK";    		
	}
	
	public Object getConfiguration(Class clazz) throws Exception{
		//if (configuration == null) 
			return fetchConfiguration(clazz);
	}
	
	private Object fetchConfiguration(Class clazz) throws Exception{

	        String marshalledConfiguration =
	    //"<file-system-configuration><listener-configurations xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" direct-access=\"true\" period=\"10\" xsi:type=\"java:com.amalto.service.filesystem.bean.FileSystemListenerConfiguration\">+
	    //    	"<source-directory>/home/asaintguilhem/test</source-directory><id>PO FileSystem</id><action>STOP</action><status>STOPPED</status><error-postfix>error</error-postfix><processing-postfix>processing</processing-postfix><processed-directory>/home/asaintguilhem/processed</processed-directory><pattern>.*</pattern><transformer>FileSystem PO to SMTP HTML</transformer><log-filename>processing.log</log-filename><content-type>text/xml</content-type><files-encoding>UTF-8</files-encoding></listener-configurations></file-system-configuration>";
	        	Util.getPort().serviceAction(
	        			new WSServiceAction(
	        					JNDI_NAME,
	        					WSServiceActionCode.EXECUTE,
	        					"getConfiguration",
	        					null
	        			)
	        	).getValue();
	        return  
				Unmarshaller.unmarshal(
						clazz, 
						new InputSource(new StringReader(marshalledConfiguration))
						);
	        
 
	}
	

	
}
