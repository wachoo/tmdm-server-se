package com.amalto.core.objects.synchronization.ejb;

import java.rmi.RemoteException;
import java.util.Collection;

import javax.ejb.EJBException;
import javax.ejb.SessionBean;
import javax.ejb.SessionContext;

import com.amalto.core.delegator.BeanDelegatorContainer;
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
    	return BeanDelegatorContainer.getUniqueInstance().getSynchronizationItemCtrlBeanDelegator().putSynchronizationItem(synchronizationItem);
    }
    
     
    /**
     * Get SynchronizationItem
     * @throws XtentisException
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method 
     */
    public SynchronizationItemPOJO getSynchronizationItem(SynchronizationItemPOJOPK pk) throws XtentisException{
    	return BeanDelegatorContainer.getUniqueInstance().getSynchronizationItemCtrlBeanDelegator().getSynchronizationItem(pk);
    }
    
    /**
     * Get a SynchronizationItem - no exception is thrown: returns null if not found 
     * @throws XtentisException
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method 
     */
    public SynchronizationItemPOJO existsSynchronizationItem(SynchronizationItemPOJOPK pk)    throws XtentisException{
    	return BeanDelegatorContainer.getUniqueInstance().getSynchronizationItemCtrlBeanDelegator().existsSynchronizationItem(pk);
    }
    

    /**
     * Remove an item
     * @throws XtentisException
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method 
     */
    public SynchronizationItemPOJOPK removeSynchronizationItem(SynchronizationItemPOJOPK pk) throws XtentisException{
    	return BeanDelegatorContainer.getUniqueInstance().getSynchronizationItemCtrlBeanDelegator().removeSynchronizationItem(pk);
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
    	return BeanDelegatorContainer.getUniqueInstance().getSynchronizationItemCtrlBeanDelegator().getSynchronizationItemPKs(regex);
    }
    
    /**
     * Get a SynchronizationItem - no exception is thrown: returns null if not found 
     * @throws XtentisException
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method 
     */
    public SynchronizationItemPOJO resolveSynchronization(SynchronizationItemPOJOPK pk, String resolvedProjection) throws XtentisException{
    	return BeanDelegatorContainer.getUniqueInstance().getSynchronizationItemCtrlBeanDelegator().resolveSynchronization(pk,resolvedProjection);
    }
    
		
}