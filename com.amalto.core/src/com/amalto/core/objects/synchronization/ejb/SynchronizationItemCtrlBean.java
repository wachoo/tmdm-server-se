package com.amalto.core.objects.synchronization.ejb;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import javax.ejb.EJBException;
import javax.ejb.SessionBean;
import javax.ejb.SessionContext;

import com.amalto.core.ejb.ItemPOJO;
import com.amalto.core.ejb.ObjectPOJO;
import com.amalto.core.ejb.ObjectPOJOPK;
import com.amalto.core.util.XtentisException;



/**
 * @author Bruno Grieder
 * 
 * @ejb.bean name="SynchronizationItemCtrl"
 *          display-name="Name for SynchronizationItemCtrl"
 *          description="Description for SynchronizationItemCtrl"
 *          jndi-name="amalto/remote/core/synchronizationItemctrl"
 * 		  	local-jndi-name = "amalto/local/core/synchronizationItemctrl"
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
public class SynchronizationItemCtrlBean implements SessionBean{
  
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
     * Creates or updates a SynchronizationItem
     * @throws XtentisException
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method 
     */
    public SynchronizationItemPOJOPK putSynchronizationItem(SynchronizationItemPOJO synchronizationItem) throws XtentisException{  
    	org.apache.log4j.Logger.getLogger(this.getClass()).trace("putSynchronizationItem() "+synchronizationItem.getPK().getUniqueId());
    	
        try {
            
            ObjectPOJOPK pk = synchronizationItem.store();
            if (pk == null) throw new XtentisException("Check the XML Server logs");
            return new SynchronizationItemPOJOPK(synchronizationItem.getLocalRevisionID(), synchronizationItem.getItemPOJOPK());
            
	    } catch (XtentisException e) {
	    	throw(e);
	    } catch (Exception e) {
    	    String err = "Unable to create/update the Synchronization Object "+synchronizationItem.getPK().getUniqueId()
    	    		+": "+e.getClass().getName()+": "+e.getLocalizedMessage();
    	    org.apache.log4j.Logger.getLogger(this.getClass()).error(err, e);
    	    throw new XtentisException(err);
	    }

    }
    
     
    /**
     * Get SynchronizationItem
     * @throws XtentisException
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method 
     */
    public SynchronizationItemPOJO getSynchronizationItem(SynchronizationItemPOJOPK pk) throws XtentisException{

        try {
        	SynchronizationItemPOJO sp =  ObjectPOJO.load(SynchronizationItemPOJO.class,pk);
        	if (sp == null) {
        		String err= "The Synchronization Object "+pk.getUniqueId()+" does not exist.";
        		org.apache.log4j.Logger.getLogger(this.getClass()).error(err);
        		throw new XtentisException(err);
        	}
        	//read the latest XML from the live local object if the status is manual
        	if (sp.getStatus() == SynchronizationItemPOJO.STATUS_MANUAL) {
        		ItemPOJO item = ItemPOJO.load(sp.getLocalRevisionID(), sp.getItemPOJOPK());
        		if (item == null) {
            		sp.setResolvedProjection(null);
            	} else {
            		sp.setResolvedProjection(item.getProjectionAsString());
            	}
        	}
        	return sp;
	    } catch (XtentisException e) {
	    	throw(e);
	    } catch (Exception e) {
    	    String err = "Unable to get the Synchronization Object "+pk.toString()
    	    		+": "+e.getClass().getName()+": "+e.getLocalizedMessage();
    	    org.apache.log4j.Logger.getLogger(this.getClass()).error(err, e);
    	    throw new XtentisException(err);
	    }
    }
    
    /**
     * Get a SynchronizationItem - no exception is thrown: returns null if not found 
     * @throws XtentisException
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method 
     */
    public SynchronizationItemPOJO existsSynchronizationItem(SynchronizationItemPOJOPK pk)    throws XtentisException{
    	
        try {
        	return ObjectPOJO.load(SynchronizationItemPOJO.class,pk);        	
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
    public SynchronizationItemPOJOPK removeSynchronizationItem(SynchronizationItemPOJOPK pk) throws XtentisException{
    	org.apache.log4j.Logger.getLogger(this.getClass()).trace("Removing "+pk.getUniqueId());

        try {
        	ObjectPOJO.remove(SynchronizationItemPOJO.class,pk);
        	return pk;
	    } catch (XtentisException e) {
	    	throw(e);
	    } catch (Exception e) {
    	    String err = "Unable to remove the Synchronization Plan "+pk.toString()
    	    		+": "+e.getClass().getName()+": "+e.getLocalizedMessage();
    	    org.apache.log4j.Logger.getLogger(this.getClass()).error(err);
    	    throw new XtentisException(err, e);
	    }
    }    
    
    /**
	 * Retrieve all SynchronizationItem PKS 
	 * 
	 * @throws XtentisException
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method 
     */       
    public Collection<SynchronizationItemPOJOPK> getSynchronizationItemPKs(String regex) throws XtentisException {
    	Collection<ObjectPOJOPK> c = ObjectPOJO.findAllPKs(SynchronizationItemPOJO.class,regex);
    	ArrayList<SynchronizationItemPOJOPK> l = new ArrayList<SynchronizationItemPOJOPK>();
    	for (Iterator<ObjectPOJOPK> iter = c.iterator(); iter.hasNext(); ) {
			l.add(new SynchronizationItemPOJOPK(iter.next()));
		}
    	return l;
    }
    
    /**
     * Get a SynchronizationItem - no exception is thrown: returns null if not found 
     * @throws XtentisException
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method 
     */
    public SynchronizationItemPOJO resolveSynchronization(SynchronizationItemPOJOPK pk, String resolvedProjection) throws XtentisException{
    	
        try {
        	SynchronizationItemPOJO pojo = getSynchronizationItem(pk);
        	
        	//update the local item
        	try {
	            ItemPOJO item = ItemPOJO.load(pojo.getLocalRevisionID(), pojo.getItemPOJOPK());
	            if (item==null) {
	            	String err = "Unable to resolve: the local item '"+pojo.getItemPOJOPK()+"' in revision '"+pojo.getLocalRevisionID()+"'" +
                			" was deleted";
                	org.apache.log4j.Logger.getLogger(this.getClass()).error(err);
                	throw new XtentisException(err);
	            }
	            item.setPlanPK(new SynchronizationPlanPOJOPK(pojo.getLastRunPlan()));
	            item.setProjectionAsString(resolvedProjection);
	            item.store(pojo.getLocalRevisionID());
        	} catch (XtentisException e) {
    	    	throw(e);
            } catch (Exception e) {
            	String err = "Unable to resolve: the local item '"+pojo.getItemPOJOPK()+"' in revision '"+pojo.getLocalRevisionID()+"'" +
            			" could not be updated: "+e.getMessage();
            	org.apache.log4j.Logger.getLogger(this.getClass()).error(err, e);
            	throw new XtentisException(err);
            }
        	
        	//update the item
        	pojo.setResolvedProjection(resolvedProjection);
        	pojo.setStatus(SynchronizationItemPOJO.STATUS_RESOLVED);
        	pojo.store();
        	
        	return pojo;
        	
	    } catch (XtentisException e) {
	    	throw(e);
	    } catch (Exception e) {
    	    String err = "Unable to resolve the synchronization "+pk.getUniqueId()+": "+e.getClass().getName()+": "+e.getLocalizedMessage();
    	    org.apache.log4j.Logger.getLogger(this.getClass()).error(err, e);
    	    throw new XtentisException(err);
	    }
    }
	
    
		
}