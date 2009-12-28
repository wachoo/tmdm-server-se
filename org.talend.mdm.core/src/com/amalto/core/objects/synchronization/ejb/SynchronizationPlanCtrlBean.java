package com.amalto.core.objects.synchronization.ejb;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;

import javax.ejb.EJBException;
import javax.ejb.SessionBean;
import javax.ejb.SessionContext;
import javax.ejb.TimedObject;
import javax.ejb.Timer;

import com.amalto.core.delegator.BeanDelegatorContainer;
import com.amalto.core.ejb.ItemPOJOPK;
import com.amalto.core.objects.datacluster.ejb.DataClusterPOJOPK;
import com.amalto.core.util.XtentisException;



/**
 * @author Bruno Grieder
 * 
 * @ejb.bean name="SynchronizationPlanCtrl"
 *          display-name="Name for SynchronizationPlanCtrl"
 *          description="Description for SynchronizationPlanCtrl"
 *          jndi-name="amalto/remote/core/synchronizationPlanctrl"
 * 		  	local-jndi-name = "amalto/local/core/synchronizationPlanctrl"
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
public class SynchronizationPlanCtrlBean implements SessionBean, TimedObject{
  
	public static final long serialVersionUID = 1L;
	
    /* (non-Javadoc)
     * @see javax.ejb.SessionBean#setSessionContext(javax.ejb.SessionContext)
     */
    public void setSessionContext(SessionContext ctx) throws EJBException, RemoteException {
    	BeanDelegatorContainer.getUniqueInstance().getSynchronizationPlanCtrlBeanDelegator().setSessionContext(ctx);
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
     * Creates or updates a SynchronizationPlan
     * @throws XtentisException
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method 
     */
    public SynchronizationPlanPOJOPK putSynchronizationPlan(SynchronizationPlanPOJO synchronizationPlan) throws XtentisException{  
    	
        return BeanDelegatorContainer.getUniqueInstance().getSynchronizationPlanCtrlBeanDelegator().putSynchronizationPlan(synchronizationPlan);

    }
    
    /**
     * Creates or updates a SynchronizationPlan for a particular revision ID<br/>
     * The user must have the role 'administration' to perform this task
     * @throws XtentisException
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method 
     */
    public SynchronizationPlanPOJOPK putSynchronizationPlan(String revisionID, SynchronizationPlanPOJO synchronizationPlan) throws XtentisException{  
    	
    	return BeanDelegatorContainer.getUniqueInstance().getSynchronizationPlanCtrlBeanDelegator().putSynchronizationPlan(revisionID,synchronizationPlan);

    }
    
     
    /**
     * Get SynchronizationPlan
     * @throws XtentisException
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method 
     */
    public SynchronizationPlanPOJO getSynchronizationPlan(SynchronizationPlanPOJOPK pk) throws XtentisException{

    	return BeanDelegatorContainer.getUniqueInstance().getSynchronizationPlanCtrlBeanDelegator().getSynchronizationPlan(pk);
    }
    
    /**
     * Get Synchronization Plan of a particular revision ID<br/>
     * The user must have the 'administration' role to preform this task
     * @throws XtentisException
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method 
     */
    public SynchronizationPlanPOJO getSynchronizationPlan(String revisionID, SynchronizationPlanPOJOPK pk) throws XtentisException{
    	
    	return BeanDelegatorContainer.getUniqueInstance().getSynchronizationPlanCtrlBeanDelegator().getSynchronizationPlan(revisionID,pk);
    }
    
    
    /**
     * Get a SynchronizationPlan - no exception is thrown: returns null if not found 
     * @throws XtentisException
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method 
     */
    public SynchronizationPlanPOJO existsSynchronizationPlan(SynchronizationPlanPOJOPK pk)    throws XtentisException{
    	
    	return BeanDelegatorContainer.getUniqueInstance().getSynchronizationPlanCtrlBeanDelegator().existsSynchronizationPlan(pk);
    }
    

    /**
     * Remove an item
     * @throws XtentisException
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method 
     */
    public SynchronizationPlanPOJOPK removeSynchronizationPlan(SynchronizationPlanPOJOPK pk) throws XtentisException{
    	return BeanDelegatorContainer.getUniqueInstance().getSynchronizationPlanCtrlBeanDelegator().removeSynchronizationPlan(pk);
    }    
    
    
    
    /**
	 * Retrieve all SynchronizationPlan PKS 
	 * 
	 * @throws XtentisException
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method 
     */       
    public Collection<SynchronizationPlanPOJOPK> getSynchronizationPlanPKs(String regex) throws XtentisException {
    	
    	return BeanDelegatorContainer.getUniqueInstance().getSynchronizationPlanCtrlBeanDelegator().getSynchronizationPlanPKs(regex);

    }
	
    
    /**
     * Retrieve all the Unique ID Strings of Xtentis Objects of a particular name that match a particular instance pattern
     * The user running this method must have an 'administration' role
     * 
     * @param revisionID
     * @param objectName
     * @param instancePattern
     * @param synchronizationPlanName
     * 			The synchronization plan objects are NOT synchronized with
     * 			<code>null</code> retrieves all objects
     * @return
     * 		The uniqueID of the object
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method
     */
    public ArrayList<String> synchronizationGetAllUnsynchronizedObjectsIDs(
    	String revisionID, 
    	String objectName, 
    	String instancePattern, 
    	String synchronizationPlanName) 
    throws XtentisException{
    	return BeanDelegatorContainer.getUniqueInstance().getSynchronizationPlanCtrlBeanDelegator().synchronizationGetAllUnsynchronizedObjectsIDs(
    			revisionID,
    			objectName,
    			instancePattern,
    			synchronizationPlanName);
    }
    
    
    
    /**
     * Return the marshaled Xtentis Object (e.g. its XML)
     * The user running this method must have an 'administration' role
     * 
     * @param revisionID
     * @param objectName
     * @param uniqueID
     * 		The unique ID obtain by calling {@link #synchronizationGetAllUnsynchronizedObjectsIDs(String, String, String, String)}
     * @return
     * 		The marshaled version of the object
     * @throws XtentisException
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method
     */
    public String synchronizationGetMarshaledObject(String revisionID, String objectName, String uniqueID) throws XtentisException{
    	return BeanDelegatorContainer.getUniqueInstance().getSynchronizationPlanCtrlBeanDelegator().synchronizationGetMarshaledObject(revisionID,objectName,uniqueID);
    }
    
    /**
     * Replaces or creates the xtentis object XML. This methods is called by Synchronization Plans
     * The user running this method must have an 'administration' role
     * 
     * @param revisionID
     * @param objectName
     * @param uniqueID
     * 		The unique ID obtain by calling {@link #synchronizationGetAllUnsynchronizedObjectsIDs(String, String, String, String)}
     * @param xml
     * @throws XtentisException
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method
     */
    public void synchronizationPutMarshaledObject(String revisionID, String objectName, String uniqueID, String xml) throws XtentisException{
    	BeanDelegatorContainer.getUniqueInstance().getSynchronizationPlanCtrlBeanDelegator().synchronizationPutMarshaledObject(revisionID,objectName,uniqueID,xml);
    }
    
    
    
    /**
     * Retrieve all the {@link ItemPOJOPK}s of Items in a revision 
     * that match a particular concept pattern and a particular instance pattern<br/>
     * <br/>
     * The user running this method must have an 'administration' role
     * 
     * @param revisionID
     * @param conceptPattern
     * @param instancePattern
     * @param synchronizationPlanPOJOPK
     * 			The synchronization plan objects are NOT synchronized with
     * 			<code>null</code> retrieves all objects
     * @param start
     * @param limit
     * @return
     * 		The uniqueID of the object
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method
     */
    public ArrayList<ItemPOJOPK> synchronizationGetAllUnsynchronizedItemPOJOPKs(
    	String revisionID,
    	DataClusterPOJOPK dataClusterPOJOPK,
    	String conceptPattern, 
    	String instancePattern, 
    	SynchronizationPlanPOJOPK synchronizationPlanPOJOPK,
    	long start,
    	int limit
    ) throws XtentisException{
        
	    return BeanDelegatorContainer.getUniqueInstance().getSynchronizationPlanCtrlBeanDelegator().synchronizationGetAllUnsynchronizedItemPOJOPKs(
	    		revisionID, 
        		dataClusterPOJOPK,
        		conceptPattern, 
        		instancePattern, 
        		synchronizationPlanPOJOPK,
        		start,
        		limit		
	    );
    }
    
    /**
     * Return the marshaled Xtentis Item (e.g. its XML)
     * The user running this method must have an 'administration' role
     * 
     * @param revisionID
     * @param itemPOJOPK
     * 
     * @return
     * 		The marshaled version of the object
     * @throws XtentisException
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method
     */
    public String synchronizationGetMarshaledItem(
    	String revisionID,
    	ItemPOJOPK itemPOJOPK
    ) throws XtentisException{
    	return BeanDelegatorContainer.getUniqueInstance().getSynchronizationPlanCtrlBeanDelegator().synchronizationGetMarshaledItem(revisionID, itemPOJOPK);
    }
    
    /**
     * Replaces or creates the xtentis object XML. This methods is called by Synchronization Plans
     * The user running this method must have an 'administration' role
     * 
     * @param revisionID
     * @param xml
     * 		the full Item XML
     * @throws XtentisException
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method
     */
    public ItemPOJOPK synchronizationPutMarshaledItem(
    	String revisionID, 
    	String xml
    ) throws XtentisException{
    	return BeanDelegatorContainer.getUniqueInstance().getSynchronizationPlanCtrlBeanDelegator().synchronizationPutMarshaledItem(revisionID, xml);
    }
    
    
    /**
	 * Execute the Synchronization Plan 
	 * 
	 * @throws XtentisException
	 * @return the status
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method 
     */  
    public String[] action(int action, SynchronizationPlanPOJOPK pk) throws XtentisException {
    	return BeanDelegatorContainer.getUniqueInstance().getSynchronizationPlanCtrlBeanDelegator().action(action, pk);
    }
    
    
    public void ejbTimeout(Timer timer) {
    	BeanDelegatorContainer.getUniqueInstance().getSynchronizationPlanCtrlBeanDelegator().ejbTimeout(timer);
    }
    
		
}