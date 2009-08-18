package com.amalto.core.objects.synchronization.ejb;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import javax.ejb.EJBException;
import javax.ejb.SessionBean;
import javax.ejb.SessionContext;

import com.amalto.core.ejb.ObjectPOJO;
import com.amalto.core.ejb.ObjectPOJOPK;
import com.amalto.core.util.XtentisException;



/**
 * @author Bruno Grieder
 * 
 * @ejb.bean name="SynchronizationObjectCtrl"
 *          display-name="Name for SynchronizationObjectCtrl"
 *          description="Description for SynchronizationObjectCtrl"
 *          jndi-name="amalto/remote/core/synchronizationObjectctrl"
 * 		  	local-jndi-name = "amalto/local/core/synchronizationObjectctrl"
 *          type="Stateless"
 *          view-type="both"
 * 
 * @ejb.remote-facade
 * 
 * @ejb.permission
 * 	view-type = "remote"
 * 	role-name = "administration"
 * 
 * @ejb.permission
 * 	view-type = "local"
 * 	unchecked = "true"
 *  
 */
public class SynchronizationObjectCtrlBean implements SessionBean{
  
	public static final long serialVersionUID = 1L;
	
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
     * Creates or updates a SynchronizationObject
     * @throws XtentisException
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method 
     */
    public SynchronizationObjectPOJOPK putSynchronizationObject(SynchronizationObjectPOJO synchronizationObject) throws XtentisException{  
    	org.apache.log4j.Logger.getLogger(this.getClass()).trace("putSynchronizationObject() "+synchronizationObject.getPK().getUniqueId());
    	
        try {
            
            ObjectPOJOPK pk = synchronizationObject.store();
            if (pk == null) throw new XtentisException("Check the XML Server logs");
            return new SynchronizationObjectPOJOPK(pk);
            
	    } catch (XtentisException e) {
	    	throw(e);
	    } catch (Exception e) {
    	    String err = "Unable to create/update the Synchronization Object "+synchronizationObject.getPK().getUniqueId()
    	    		+": "+e.getClass().getName()+": "+e.getLocalizedMessage();
    	    org.apache.log4j.Logger.getLogger(this.getClass()).error(err);
    	    throw new XtentisException(err);
	    }

    }
    
     
    /**
     * Get SynchronizationObject
     * @throws XtentisException
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method 
     */
    public SynchronizationObjectPOJO getSynchronizationObject(SynchronizationObjectPOJOPK pk) throws XtentisException{

        try {
        	SynchronizationObjectPOJO sp =  ObjectPOJO.load(SynchronizationObjectPOJO.class,pk);
        	if (sp == null) {
        		String err= "The Synchronization Object "+pk.getUniqueId()+" does not exist.";
        		org.apache.log4j.Logger.getLogger(this.getClass()).error(err);
        		throw new XtentisException(err);
        	}
        	return sp;
	    } catch (XtentisException e) {
	    	throw(e);
	    } catch (Exception e) {
    	    String err = "Unable to get the Synchronization Object "+pk.toString()
    	    		+": "+e.getClass().getName()+": "+e.getLocalizedMessage();
    	    org.apache.log4j.Logger.getLogger(this.getClass()).error(err);
    	    throw new XtentisException(err);
	    }
    }
    
    /**
     * Get a SynchronizationObject - no exception is thrown: returns null if not found 
     * @throws XtentisException
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method 
     */
    public SynchronizationObjectPOJO existsSynchronizationObject(SynchronizationObjectPOJOPK pk)    throws XtentisException{
    	
        try {
        	return ObjectPOJO.load(SynchronizationObjectPOJO.class,pk);        	
	    } catch (XtentisException e) {
	    	return null;
	    } catch (Exception e) {
    	    String info = "Could not check whether this Synchronization Object exists:  "+pk.getUniqueId()
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
    public SynchronizationObjectPOJOPK removeSynchronizationObject(SynchronizationObjectPOJOPK pk) throws XtentisException{
    	org.apache.log4j.Logger.getLogger(this.getClass()).trace("Removing "+pk.getUniqueId());

        try {
        	return new SynchronizationObjectPOJOPK(ObjectPOJO.remove(SynchronizationObjectPOJO.class,pk));
	    } catch (XtentisException e) {
	    	throw(e);
	    } catch (Exception e) {
    	    String err = "Unable to remove the Synchronization Plan "+pk.toString()
    	    		+": "+e.getClass().getName()+": "+e.getLocalizedMessage();
    	    org.apache.log4j.Logger.getLogger(this.getClass()).error(err);
    	    throw new XtentisException(err);
	    }
    }    
    
    
    
    /**
	 * Retrieve all SynchronizationObject PKS 
	 * 
	 * @throws XtentisException
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method 
     */       
    public Collection<SynchronizationObjectPOJOPK> getSynchronizationObjectPKs(String regex) throws XtentisException {
    	Collection<ObjectPOJOPK> c = ObjectPOJO.findAllPKs(SynchronizationObjectPOJO.class,regex);
    	ArrayList<SynchronizationObjectPOJOPK> l = new ArrayList<SynchronizationObjectPOJOPK>();
    	for (Iterator<ObjectPOJOPK> iter = c.iterator(); iter.hasNext(); ) {
			l.add(new SynchronizationObjectPOJOPK(iter.next()));
		}
    	return l;
    }
	
  
    
    
		
}