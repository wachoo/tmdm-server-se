package com.amalto.service.svn.bean;

import java.io.Serializable;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Iterator;

import org.exolab.castor.xml.Marshaller;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.amalto.core.util.Util;
import com.amalto.core.util.XtentisException;

public class SvnConfiguration implements Serializable {


	public String url;
	public String username;
	public String password;

	public SvnConfiguration() {
		super();
//		FileSystemListenerConfiguration fsl = new FileSystemListenerConfiguration();
//		fsl.setId("test");
//		fsl.setSourceDirectory("mySource");
//		fsl.setProcessedDirectory("myProcessed");
//		fsl.setTransformer("PGT");
//		listenerConfigurations.add(fsl);
	}
	/***********************************************************
	 * 
	 * The BEAN Stuff
	 * 
	***********************************************************/
	
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	
	/***********************************************************
	 * 
	 * The Marshalling Stuff
	 * 
	***********************************************************/
	
	
	/**
	 * Unmarshal the configuration from a Castor XML conf.
	 * @param marshalledConfiguration
	 * @return A fileSystemConfiguration object
	 * @throws XtentisException
	 */
	/*
	public static SvnConfiguration unmarshal(String marshalledConfiguration) throws XtentisException {	
    	try {
   			return (SvnConfiguration)Unmarshaller.unmarshal(
   										SvnConfiguration.class, 
   										new InputSource(new StringReader(marshalledConfiguration))
   							);
	    } catch (Exception e) {
    	    String err = "Unable to deserialize the File System Service configuration."
    	    		+": "+e.getClass().getName()+": "+e.getLocalizedMessage();
    	    org.apache.log4j.Category.getInstance(SvnConfiguration.class).error(err);
    	    throw new XtentisException(err);
	    }	
	}
	*/
	
	/**
	 * Marshal this configuration to a Castor XML
	 * @return the  Castor marshalled configuration 
	 * @throws XtentisException
	 */
	/*
	public String marshal() throws XtentisException{
    	try {
    		StringWriter sw = new StringWriter();
   			Marshaller.marshal(this,sw);
   			return sw.toString();
	    } catch (Exception e) {
    	    String err = "Unable to serialize the File System Service configuration."
    	    		+": "+e.getClass().getName()+": "+e.getLocalizedMessage();
    	    org.apache.log4j.Category.getInstance(SvnConfiguration.class).error(err);
    	    throw new XtentisException(err);
	    }			
	}
	*/
	
	
	
	/**
	 * Unmarshal the configuration from a serilaized configuration
	 * @param marshalledConfiguration
	 * @return A fileSystemConfiguration object
	 * @throws XtentisException
	 */
/*	public static SvnConfiguration parse(String marshalledConfiguration) throws XtentisException {	
    	try {
    		return parse(Util.parse(marshalledConfiguration).getDocumentElement());
    	} catch(XtentisException e) {
    		throw(e);
	    } catch (Exception e) {
    	    String err = "Unable to deserialize the File System Service configuration."
    	    		+": "+e.getClass().getName()+": "+e.getLocalizedMessage();
    	    org.apache.log4j.Category.getInstance(SvnConfiguration.class).error(err);
    	    throw new XtentisException(err);
	    }	
	}
	*/
	/**
	 * Unmarshal the configuration from a DOM configuration
	 * @param root - the Root Element of the DOM configuration
	 * @return A fileSystemConfiguration object
	 * @throws XtentisException
	 */
	/*public static SvnConfiguration parse(Node root) throws XtentisException {	
    	try {
    		SvnConfiguration fs = new SvnConfiguration();
			NodeList nl = Util.getNodeList(root, "FileSystemListenerConfiguration");
			if ((nl!=null) && (nl.getLength()>0)) {
				for (int i = 0; i < nl.getLength(); i++) {
					fs.getListenerConfigurations().add(
							FileSystemListenerConfiguration.parse(nl.item(i))
					);
				}
			}
    		return fs;
	    } catch (XtentisException e) {
	    	throw(e);
	    } catch (Exception e) {
    	    String err = "Unable to deserialize the File System Service configuration."
    	    		+": "+e.getClass().getName()+": "+e.getLocalizedMessage();
    	    org.apache.log4j.Category.getInstance(SvnConfiguration.class).error(err);
    	    throw new XtentisException(err);
	    }	
	}*/
	
	/**
	 * Serializes this configuration to an XML (Castor failed)
	 * @return the marshalled configuration 
	 * @throws XtentisException
	 */
	public String serialize() throws XtentisException{
    	/*try {
    		String xml = "<SvnConfiguration>";
    		for (Iterator iter = getListenerConfigurations().iterator(); iter.hasNext(); ) {
				xml+=((FileSystemListenerConfiguration)iter.next()).serialize();
			}
    		xml += "</SvnConfiguration>";
    		return xml;
	    } catch (XtentisException e) {
	    	throw(e);
	    } catch (Exception e) {
    	    String err = "Unable to serialize the File System Service configuration."
    	    		+": "+e.getClass().getName()+": "+e.getLocalizedMessage();
    	    org.apache.log4j.Category.getInstance(SvnConfiguration.class).error(err);
    	    throw new XtentisException(err);
	    }	*/
	 	StringWriter sw = new StringWriter();
    	try {
    		Marshaller.marshal(this, sw);
	    } catch (Exception e) {
    	    String err = "Unable to serialize the configuration of the Svn Service"
    	    		+": "+e.getClass().getName()+": "+e.getLocalizedMessage();
    	    org.apache.log4j.Category.getInstance(this.getClass()).error(err);
    	    throw new XtentisException(err);
	}
	    return sw.toString();
	
	}

}
