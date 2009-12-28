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
    	return BeanDelegatorContainer.getUniqueInstance().getSynchronizationObjectCtrlBeanDelegator().putSynchronizationObject(synchronizationObject);
    }
    
     
    /**
     * Get SynchronizationObject
     * @throws XtentisException
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method 
     */
    public SynchronizationObjectPOJO getSynchronizationObject(SynchronizationObjectPOJOPK pk) throws XtentisException{

    	return BeanDelegatorContainer.getUniqueInstance().getSynchronizationObjectCtrlBeanDelegator().getSynchronizationObject(pk);
    }
    
    /**
     * Get a SynchronizationObject - no exception is thrown: returns null if not found 
     * @throws XtentisException
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method 
     */
    public SynchronizationObjectPOJO existsSynchronizationObject(SynchronizationObjectPOJOPK pk) throws XtentisException{
    	
    	return BeanDelegatorContainer.getUniqueInstance().getSynchronizationObjectCtrlBeanDelegator().existsSynchronizationObject(pk);
    }
    

    /**
     * Remove an item
     * @throws XtentisException
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method 
     */
    public SynchronizationObjectPOJOPK removeSynchronizationObject(SynchronizationObjectPOJOPK pk) throws XtentisException{
    	return BeanDelegatorContainer.getUniqueInstance().getSynchronizationObjectCtrlBeanDelegator().removeSynchronizationObject(pk);
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
    	return BeanDelegatorContainer.getUniqueInstance().getSynchronizationObjectCtrlBeanDelegator().getSynchronizationObjectPKs(regex);
    }
	
  
    
    
		
}