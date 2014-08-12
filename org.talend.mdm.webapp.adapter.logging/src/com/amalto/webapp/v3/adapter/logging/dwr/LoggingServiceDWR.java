package com.amalto.webapp.v3.adapter.logging.dwr;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.apache.log4j.Priority;
import org.w3c.dom.Document;

import com.amalto.webapp.core.util.Util;
import com.amalto.webapp.core.util.XtentisWebappException;
import com.amalto.core.webservice.WSServiceAction;
import com.amalto.core.webservice.WSServiceActionCode;
import com.amalto.core.webservice.WSServiceGetConfiguration;
import com.amalto.core.webservice.WSServicePutConfiguration;


/**
 * 
 * @author bgrieder
 *
 */
public class LoggingServiceDWR {

	final static String JNDI_NAME = "amalto/local/service/logging";
    
    /**
     * DestinationModel.java
     * Constructor
     * 
     */
    public LoggingServiceDWR() {
        super();
    }

    
    /************************************************************************************
     * Bean Implemenation for use with DWR
     ************************************************************************************/
    
    private int port;
    //private int period;
    private int threshold;
    private String pattern;
    private String xtentisusername;
    private String xtentispassword;
    private String logfilename;


	/************************************************************************************
     * Ajax methods
     ************************************************************************************/
      

    public Map getLog4gPriorities(String value){
    	LinkedHashMap map = new LinkedHashMap();
    	map.put(Priority.OFF_INT, "Off");
    	map.put(Priority.FATAL_INT, "Fatal");
    	map.put(Priority.ERROR_INT, "Error");
    	map.put(Priority.WARN_INT, "Warn");
    	return map;
    }
    
	public LoggingServiceDWR getConfiguration() throws Exception{
        try {
	        String configuration = 
	        	Util.getPort().getServiceConfiguration(
	        			new WSServiceGetConfiguration(
	        					JNDI_NAME,
	        					null
	        			)
	        	).getValue();
			Document d = Util.parse(configuration);
			port = new Integer(Util.getFirstTextNode(d.getDocumentElement(), "port")).intValue();
		//	period =new Integer(Util.getFirstTextNode(d.getDocumentElement(), "period")).intValue();
		    threshold =new Integer(Util.getFirstTextNode(d.getDocumentElement(), "threshold")).intValue();
		    pattern=Util.getFirstTextNode(d.getDocumentElement(),"pattern");
		    xtentisusername = Util.getFirstTextNode(d.getDocumentElement(), "xtentiusername");
		    xtentispassword = Util.getFirstTextNode(d.getDocumentElement(), "xtentispassword");
		    logfilename=Util.getFirstTextNode(d.getDocumentElement(), "logfilename");
		  
			return this;
        } catch (Exception e) {
        	Matcher m = Pattern.compile("(.*Exception:)(.+)",Pattern.DOTALL).matcher(e.getLocalizedMessage());
        	if (m.matches()) throw new Exception(m.group(2));
            throw new Exception(e.getClass().getName()+": "+e.getLocalizedMessage());
        }
    }
    

    
    public String start(LoggingServiceDWR configuration) throws Exception {  //or restart
    	try {
        	//save configuration
        	Util.getPort().putServiceConfiguration(
        			new WSServicePutConfiguration(
        					JNDI_NAME,
        					serializeConfiguration(configuration)
        			)
        	);
        	//perform start / restart
	        return
	        	Util.getPort().serviceAction(
	        			new WSServiceAction(
	        					JNDI_NAME,
	        					WSServiceActionCode.START,
	        					null,
	        					null
	        			)
	        	).getValue();
        } catch (Exception e) {
        	//Matcher m = Pattern.compile("(.*Exception:)(.+)",Pattern.DOTALL).matcher(e.getLocalizedMessage());
        	//if (m.matches()) throw new Exception(m.group(2));
            throw new Exception(e.getClass().getName()+": "+e.getLocalizedMessage());
        }    	
    }
    
    
	private static String serializeConfiguration(LoggingServiceDWR configuration) throws Exception{
		
		try {
			
		 
			String xml ="<configuration>";
			xml+="	<port>"+((configuration.getPort()<=0)?"":String.valueOf(configuration.getPort()))+"</port>";
			
			xml+=
				"   <threshold>"+((configuration.getThreshold()<=0)?"":String.valueOf(configuration.getThreshold()))+"</threshold>"+
				"   <pattern>"+configuration.getXtentisusername()+"</pattern>"+
				" 	<xtentisusername>"+configuration.getXtentisusername()+"</xtentisusername>"+
				" 	<xtentispassword>"+configuration.getXtentispassword()+"</xtentispassword>"+
                "   <logfilename>"+configuration.getLogfilename()+"</logfilename>";
			
		  	  xml+="</configuration>";
			return xml;

        } catch (Exception e) {
        	Matcher m = Pattern.compile("(.*Exception:)(.+)",Pattern.DOTALL).matcher(e.getLocalizedMessage());
        	if (m.matches()) throw new Exception(m.group(2));
            throw new Exception(e.getClass().getName()+": "+e.getLocalizedMessage());
        }    	
	}

	public String stop() throws Exception {  //or restart
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
        } catch (Exception e) {
        	Matcher m = Pattern.compile("(.*Exception:)(.+)",Pattern.DOTALL).matcher(e.getLocalizedMessage());
        	if (m.matches()) throw new Exception(m.group(2));
            throw new Exception(e.getClass().getName()+": "+e.getLocalizedMessage());
        }    	
    }
	
	public String getStatus() throws Exception {
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
//        	Matcher m = Pattern.compile("(.*Exception:)(.+)",Pattern.DOTALL).matcher(e.getLocalizedMessage());
//        	if (m.matches()) throw new Exception(m.group(2));
            throw new Exception(e.getClass().getName()+": "+e.getLocalizedMessage());
        }    	
    }
	
	public void tryMe() throws XtentisWebappException{
		
		String errorMsg="This is a sample error message! ";
		Logger.getLogger(this.getClass()).error(errorMsg);
		throw new XtentisWebappException(errorMsg);

	}


	public String getLogfilename() {
		return logfilename;
	}






	public void setLogfilename(String logfilename) {
		this.logfilename = logfilename;
	}






	/*public int getPeriod() {
		return period;
	}






	public void setPeriod(int period) {
		this.period = period;
	}*/






	public int getPort() {
		return port;
	}






	public void setPort(int port) {
		this.port = port;
	}



	public int getThreshold() {
		return threshold;
	}


	public void setThreshold(int threshold) {
		this.threshold = threshold;
	}


	public String getXtentispassword() {
		return xtentispassword;
	}






	public void setXtentispassword(String xtentispassword) {
		this.xtentispassword = xtentispassword;
	}






	public String getXtentisusername() {
		return xtentisusername;
	}






	public void setXtentisusername(String xtentisusername) {
		this.xtentisusername = xtentisusername;
	}


	public String getPattern() {
		return pattern;
	}


	public void setPattern(String pattern) {
		this.pattern = pattern;
	}
	
		

  
}