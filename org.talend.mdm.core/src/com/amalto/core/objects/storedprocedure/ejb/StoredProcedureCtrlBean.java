package com.amalto.core.objects.storedprocedure.ejb;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import javax.ejb.EJBException;
import javax.ejb.SessionBean;
import javax.ejb.SessionContext;

import com.amalto.core.ejb.ObjectPOJO;
import com.amalto.core.ejb.ObjectPOJOPK;
import com.amalto.core.objects.datacluster.ejb.DataClusterPOJOPK;
import com.amalto.core.util.XtentisException;



/**
 * @author bgrieder
 * 
 * @ejb.bean name="StoredProcedureCtrl"
 *          display-name="Name for StoredProcedureCtrl"
 *          description="Description for StoredProcedureCtrl"
 *          jndi-name="amalto/remote/core/storedprocedurectrl"
 * 		  	local-jndi-name = "amalto/local/core/storedprocedurectrl"
 *          type="Stateless"
 *          view-type="both"
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
 */
public class StoredProcedureCtrlBean implements SessionBean{
  
	public static final long serialVersionUID = 1987987232;
	
	
    /**
     * StoredProcedureCtrlBean.java
     * Constructor
     * 
     */
    public StoredProcedureCtrlBean() {
        super();
    }

    /* (non-Javadoc)
     * @see javax.ejb.SessionBean#setSessionContext(javax.ejb.SessionContext)
     */
    public void setSessionContext(SessionContext ctx)
        throws EJBException,
        RemoteException {
    	//context=ctx;
    }

    /* (non-Javadoc)
     * @see javax.ejb.SessionBean#ejbRemove()
     */
    public void ejbRemove() throws EJBException, RemoteException {
    }

    /* (non-Javadoc)
     * @see javax.ejb.SessionBean#ejbActivate()
     */
    public void ejbActivate() throws EJBException, RemoteException {
    }

    /* (non-Javadoc)
     * @see javax.ejb.SessionBean#ejbPassivate()
     */
    public void ejbPassivate() throws EJBException, RemoteException {
    }
    
    /**
     * Create method
     * @ejb.create-method  view-type = "local"
     */
    public void ejbCreate() throws javax.ejb.CreateException {}
    
    /**
     * Post Create method
     */
    public void ejbPostCreate() throws javax.ejb.CreateException {}
    
    
    
    /**
     * Creates or updates a Stored Procedure
     * @throws XtentisException
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method 
     */
    public StoredProcedurePOJOPK putStoredProcedure(StoredProcedurePOJO storedProcedure) throws XtentisException{  
    	
    	org.apache.log4j.Logger.getLogger(this.getClass()).debug("putStoredProcedure() ");
    	
        try {
            return new StoredProcedurePOJOPK(storedProcedure.store());
	    } catch (XtentisException e) {
	    	throw(e);
	    } catch (Exception e) {
    	    String err = "Unable to create/update the Stored Procedure "+storedProcedure.getName()
    	    		+": "+e.getClass().getName()+": "+e.getLocalizedMessage();
    	    org.apache.log4j.Logger.getLogger(this.getClass()).error(err);
    	    throw new XtentisException(err, e);
	    }

    }
    
     
    /**
     * Get item
     * @throws XtentisException
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method 
     */
    public StoredProcedurePOJO getStoredProcedure(StoredProcedurePOJOPK pk) throws XtentisException{

        try {
        	StoredProcedurePOJO sp = ObjectPOJO.load(StoredProcedurePOJO.class, pk);
        	if (sp == null) {
        		String err= "The Stored Procedure "+pk.getUniqueId()+" does not exist.";
        		org.apache.log4j.Logger.getLogger(this.getClass()).error(err);
        		throw new XtentisException(err);
        	}
        	return sp;
	    } catch (XtentisException e) {
	    	throw(e);
	    } catch (Exception e) {
    	    String err = "Unable to get the StoredProcedure "+pk.toString()
    	    		+": "+e.getClass().getName()+": "+e.getLocalizedMessage();
    	    org.apache.log4j.Logger.getLogger(this.getClass()).error(err);
    	    throw new XtentisException(err, e);
	    }
    }
    
    /**
     * Get a Stored Procedure - no exception is thrown: returns null if not found 
     * @throws XtentisException
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method 
     */
    public StoredProcedurePOJO existsStoredProcedure(StoredProcedurePOJOPK pk)    throws XtentisException{
    	
        try {
        	return ObjectPOJO.load(StoredProcedurePOJO.class, pk);
	    } catch (XtentisException e) {
	    	return null;
	    } catch (Exception e) {
    	    String info = "Could not check whether this Stored Procedure exists:  "+pk.toString()
    	    		+": "+e.getClass().getName()+": "+e.getLocalizedMessage();
    	    org.apache.log4j.Logger.getLogger(this.getClass()).debug(info, e);
    	   return null;
	    }
    }
    

    /**
     * Remove an item
     * @throws XtentisException
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method 
     */
    public StoredProcedurePOJOPK removeStoredProcedure(StoredProcedurePOJOPK pk) 
    throws XtentisException{
    	org.apache.log4j.Logger.getLogger(this.getClass()).debug("Removing "+pk.getUniqueId());

        try {
        	return new StoredProcedurePOJOPK(ObjectPOJO.remove(StoredProcedurePOJO.class,pk));
	    } catch (XtentisException e) {
	    	throw(e);
	    } catch (Exception e) {
    	    String err = "Unable to remove the Stored Procedure "+pk.toString()
    	    		+": "+e.getClass().getName()+": "+e.getLocalizedMessage();
    	    org.apache.log4j.Logger.getLogger(this.getClass()).error(err);
    	    throw new XtentisException(err, e);
	    }
    }    
    

    
    /**
	 * Executes the stored procedure and return the result as a Collection 
	 * 
	 * @throws XtentisException
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method 
     */   
    public Collection<String> execute(
    		StoredProcedurePOJOPK sppk,
    		String revisionID,
    		DataClusterPOJOPK dcpk,
    		String[] parameters
		)throws XtentisException{
    	
        try {            
       		return getStoredProcedure(sppk).execute(revisionID, dcpk, parameters);
	    } catch (XtentisException e) {
	    	throw(e);
	    } catch (Exception e) {
    	    String err = e.getLocalizedMessage();
    	    org.apache.log4j.Logger.getLogger(this.getClass()).error(err);
    	    throw new XtentisException(err, e);
	    }
    }    
    
    
    
    /**
	 * Retrieve all Stored Procedure PKS 
	 * 
	 * @throws XtentisException
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method 
     */       
    public Collection<StoredProcedurePOJOPK> getStoredProcedurePKs(String regex) throws XtentisException {
    	Collection<ObjectPOJOPK> c = ObjectPOJO.findAllPKs(StoredProcedurePOJO.class,regex);
    	ArrayList<StoredProcedurePOJOPK> l = new ArrayList<StoredProcedurePOJOPK>();
    	for (Iterator<ObjectPOJOPK> iter = c.iterator(); iter.hasNext(); ) {
			l.add(new StoredProcedurePOJOPK(iter.next()));
		}
    	return l;
    }
	
		
}