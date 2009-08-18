package com.amalto.service.dumptoconsole.ejb;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.HashMap;

import javax.ejb.EJBException;
import javax.ejb.SessionBean;

import com.amalto.core.ejb.ItemPOJOPK;
import com.amalto.core.ejb.ServiceCtrlBean;
import com.amalto.core.util.Util;
import com.amalto.core.util.XtentisException;

/**
 * @author Bruno Grieder
 * 
 * @ejb.bean 	name="DumpToConsole"
 *           	display-name="Name for DumpToConsole"
 *           	description="Description for DumpToConsole"
 * 		  		local-jndi-name = "amalto/local/service/dumptoconsole"
 *           	type="Stateless"
 *           	view-type="local"
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
public class DumpToConsoleBean extends ServiceCtrlBean  implements SessionBean{
  
	private static final long serialVersionUID = 290873987;

	

	/* (non-Javadoc)
	 * @see com.amalto.core.ejb.ServiceCtrlBean#getJNDIName()
	 */
    /**
     * @throws EJBException
     * 
     * @ejb.interface-method view-type = "local"
     * @ejb.facade-method 
     */
	public String getJNDIName() throws XtentisException {
		return "amalto/local/service/dumptoconsole";
	}
	
    /**
     * 
     * @see com.amalto.core.ejb.ServiceCtrlBean#getConfiguration(java.lang.String)
     * @throws EJBException
     * 
     * @ejb.interface-method view-type = "local"
     * @ejb.facade-method 
     */
    public String getConfiguration(String optionalParameters) throws XtentisException{
    	return "";
    }
	/* (non-Javadoc)
	 * @see com.amalto.core.ejb.ServiceCtrlBean#getDescription()
	 */
    /**
     * @throws EJBException
     * 
     * @ejb.interface-method view-type = "local"
     * @ejb.facade-method 
     */
	public String getDescription(String twoLetterLanguageCode) throws XtentisException {
		if ("fr".matches(twoLetterLanguageCode.toLowerCase()))
			return "This service dumps the content of the Item to the console and logs it as INFO";
		return "This service dumps the content of the Item to the console and logs it as INFO";
	}

    /**
     * @author achen
     * @throws XtentisException
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method 
     */
    public  String getDocumentation(String twoLettersLanguageCode) throws XtentisException{
    	return "N/A";
    }
	/* (non-Javadoc)
	 * @see com.amalto.core.ejb.ServiceCtrlBean#getStatus()
	 */
    /**
     * @throws EJBException
     * 
     * @ejb.interface-method view-type = "local"
     * @ejb.facade-method 
     */
	public String getStatus() throws XtentisException {
		return "OK"; 
	}


	/* (non-Javadoc)
	 * @see com.amalto.core.ejb.ServiceCtrlBean#start()
	 */
    /**
     * @throws EJBException
     * 
     * @ejb.interface-method view-type = "local"
     * @ejb.facade-method 
     */
	public void start() throws XtentisException {
		return;
	}


	/* (non-Javadoc)
	 * @see com.amalto.core.ejb.ServiceCtrlBean#stop()
	 */
    /**
     * @throws EJBException
     * 
     * @ejb.interface-method view-type = "local"
     * @ejb.facade-method 
     */
	public void stop() throws XtentisException {
		return;
	}


	/* (non-Javadoc)
	 * @see com.amalto.core.ejb.ServiceCtrlBean#receiveFromOutbound(java.util.HashMap, java.lang.String)
	 */
    /**
     * @throws EJBException
     * 
     * @ejb.interface-method view-type = "local"
     * @ejb.facade-method 
     */
	public Serializable receiveFromOutbound(HashMap<String, Serializable> map) throws XtentisException {
		try {
			String contentType = (String)map.get("contentType");
			String charset = (String)map.get("charset");
			byte[] bytes = (byte[])map.get("bytes");
			if (	
					(contentType!=null) && 
					(contentType.toLowerCase().startsWith("text"))
				)
			{
				dump(new String(bytes,charset));
			} else {
				ObjectInputStream ois  = new ObjectInputStream(new ByteArrayInputStream(bytes));
				dump(ois.readObject().toString());
			}
		} catch (Exception e) {
			String err = "ERROR: unable to dump the message to console"
				+e.getClass().getName()+": "+e.getLocalizedMessage();
			org.apache.log4j.Logger.getLogger(this.getClass()).error("Dump to console: "+err);
			throw new XtentisException(err);
		}
		return null;
	}




	/* (non-Javadoc)
	 * @see com.amalto.core.ejb.IServiceBean#run(java.lang.String)
	 */
    /**
     * @throws EJBException
     * 
     * @ejb.interface-method view-type = "local"
     * @ejb.facade-method 
     */
	public String receiveFromInbound(ItemPOJOPK itemPK, String routingOrderID, String parameters) throws XtentisException {
		try {
			String xml = Util.getItemCtrl2Local().getItem(itemPK).getProjectionAsString();
			dump(xml);
    	} catch (XtentisException e) {
    		throw (e);
	    } catch (Exception e) {
    	    String err = "Unable to dump to console"
    	    		+": "+e.getClass().getName()+": "+e.getLocalizedMessage();
    	    org.apache.log4j.Logger.getLogger(this.getClass()).error(err, e);
    	    throw new XtentisException(err);
	    }
	    return "Document successfully dumped to console";
		
	}
	
	

	private void dump(String string) {
		System.out.println("DUMP TO CONSOLE<<<<<<<<<<<<");
		System.out.println(string);
		System.out.println(">>>>>>>>>>>>DUMP TO CONSOLE");		
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