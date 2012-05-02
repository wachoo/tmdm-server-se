package com.amalto.webapp.core.bean;

import java.io.Serializable;
import java.util.ArrayList;

public class FileSystemConfiguration implements Serializable {


	public ArrayList<FileSystemListenerConfiguration> listenerConfigurations = new ArrayList<FileSystemListenerConfiguration>();
	

	public FileSystemConfiguration() {
		super();
	}
	/***********************************************************
	 * 
	 * The BEAN Stuff
	 * 
	***********************************************************/
	public ArrayList<FileSystemListenerConfiguration> getListenerConfigurations() {
		return listenerConfigurations;
	}
	public void setListenerConfigurations(ArrayList<FileSystemListenerConfiguration> configuration) {
		this.listenerConfigurations = configuration;
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
	public static FileSystemConfiguration unmarshal(String marshalledConfiguration) throws XtentisException {	
    	try {
   			return (FileSystemConfiguration)Unmarshaller.unmarshal(
   										FileSystemConfiguration.class, 
   										new InputSource(new StringReader(marshalledConfiguration))
   							);
	    } catch (Exception e) {
    	    String err = "Unable to deserialize the File System Service configuration."
    	    		+": "+e.getClass().getName()+": "+e.getLocalizedMessage();
    	    org.apache.log4j.Category.getInstance(FileSystemConfiguration.class).error(err);
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
    	    org.apache.log4j.Category.getInstance(FileSystemConfiguration.class).error(err);
    	    throw new XtentisException(err);
	    }			
	}
	*/
	
	
	
	/**
	 * Unmarshal the configuration from a serilaized configuration
	 * @param marshalledConfiguration
	 * @return A fileSystemConfiguration object
	 * @throws XtentisException
	 
	public static FileSystemConfiguration parse(String marshalledConfiguration) throws XtentisException {	
    	try {
    		return parse(Util.parse(marshalledConfiguration).getDocumentElement());
    	} catch(XtentisException e) {
    		throw(e);
	    } catch (Exception e) {
    	    String err = "Unable to deserialize the File System Service configuration."
    	    		+": "+e.getClass().getName()+": "+e.getLocalizedMessage();
    	    org.apache.log4j.Category.getInstance(FileSystemConfiguration.class).error(err);
    	    throw new XtentisException(err);
	    }	
	}
	*/
	/**
	 * Unmarshal the configuration from a DOM configuration
	 * @param root - the Root Element of the DOM configuration
	 * @return A fileSystemConfiguration object
	 * @throws XtentisException
	
	public static FileSystemConfiguration parse(Node root) throws XtentisException {	
    	try {
    		FileSystemConfiguration fs = new FileSystemConfiguration();
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
    	    org.apache.log4j.Category.getInstance(FileSystemConfiguration.class).error(err);
    	    throw new XtentisException(err);
	    }	
	}
	 */
	/**
	 * Serializes this configuration to an XML (Castor failed)
	 * @return the marshalled configuration 
	 * @throws XtentisException
	 
	public String serialize() throws XtentisException{
    	try {
    		String xml = "<FileSystemConfiguration>";
    		for (Iterator iter = getListenerConfigurations().iterator(); iter.hasNext(); ) {
				xml+=((FileSystemListenerConfiguration)iter.next()).serialize();
			}
    		xml += "</FileSystemConfiguration>";
    		return xml;
	    } catch (XtentisException e) {
	    	throw(e);
	    } catch (Exception e) {
    	    String err = "Unable to serialize the File System Service configuration."
    	    		+": "+e.getClass().getName()+": "+e.getLocalizedMessage();
    	    org.apache.log4j.Category.getInstance(FileSystemConfiguration.class).error(err);
    	    throw new XtentisException(err);
	    }			
	}
	*/
	
}
