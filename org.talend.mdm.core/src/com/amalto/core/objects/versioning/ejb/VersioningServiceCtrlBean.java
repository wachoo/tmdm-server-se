package com.amalto.core.objects.versioning.ejb;

import javax.ejb.EJBException;

import com.amalto.core.ejb.ServiceCtrlBean;
import com.amalto.core.objects.versioning.util.HistoryInfos;
import com.amalto.core.objects.versioning.util.VersioningServiceCtrlLocalBI;
import com.amalto.core.util.XtentisException;


/**
 * @author bgrieder
 * 
 * @ejb.bean name="VersioningServiceCtrl" 
 * 		  local-jndi-name = "amalto/local/core/servicectrl"
 *           type="Stateless"
 *           view-type="local"
 *           generate = "false"
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
 * @ejb.ejb-ref 
 * 		ejb-name = "VersioningServiceBean" 
 * 		ref-name = "ejb/VersioningServiceBean" 
 * 		view-type = "local"
 * 
 */
public abstract class VersioningServiceCtrlBean extends ServiceCtrlBean implements VersioningServiceCtrlLocalBI {
  
    
    /**
     * IVersioningServiceCtrlBean.java
     * Constructor
     * 
     */
    public VersioningServiceCtrlBean() {
        super();
    }
    
    /**
     * Returns the History of an item or object
     * The path is constitued of the clustername/instancename
     * 
     * @throws EJBException
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method 
     */
    public abstract HistoryInfos getHistory(String path) throws XtentisException;
    
    /**
     * Returns the Versioning History of an item or object
     * The path is constitued of the clustername/instancename
     * 
     * @throws EJBException
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method 
     */
    public abstract HistoryInfos getVersions(String path) throws XtentisException;

    /**
     * Checkouts 
     * The path is constitued of the clustername/instancename
     * If tag is null, the checkout will be made from the head
     * 
     * @return the checked out xml
     * @throws EJBException
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method 
     */
    public abstract String[] checkOut(String path, String tag, String revision) throws XtentisException;

    /**
     * Commits to the head of the repository 
     * The path is constitued of the clustername/instancename
     * 
     * @throws EJBException
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method 
     */
    public abstract void commit(String path, String xml, String comment, String username) throws XtentisException;

    
    /**
     * Branch the head to a particular tag
     * The path is constitued of the clustername/instancename
     * If the path is a clustername only, all instances will be branched
     * 
     * @throws EJBException
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method 
     */
    public abstract void branch(String path, String tag, String comment, String username) throws XtentisException;


    /**
     * Clean the head by keeping on the listed instances of the particular cluster
     *  
     * @throws EJBException
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method 
     */
    public abstract void clean(String clustername, String instancenames[]) throws XtentisException;

    
    /**
     * Get instances name for a particular cluster and a particular tag
     * If tag null, return the instance names of the head
     *  
     * @throws EJBException
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method 
     */
    public abstract String[] getInstances(String clustername, String tag) throws XtentisException;

    
    /**
     * Sets the default/current versioning system configuration
     * 
     * @throws EJBException
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method 
     */
    public abstract void setCurrentVersioningSystemConfiguration(String name, String url, String username, String password) throws XtentisException;
    
    /**
     * checkup the svn configuration
     * @throws XtentisException
     *
     * @ejb.interface-method view-type = "local"
     * @ejb.facade-method
     */
     public boolean checkConfigure()throws com.amalto.core.util.XtentisException{
    	 return true;
     }
     
     /**
      * Returns the XML schema for the configuration<br>
      * Can be null
      *  
      * @throws XtentisException
      * 
      * @ejb.interface-method view-type = "local"
      * @ejb.facade-method 
      */
     public String getConfigurationSchema() throws XtentisException{
    	 return null;
     }
}