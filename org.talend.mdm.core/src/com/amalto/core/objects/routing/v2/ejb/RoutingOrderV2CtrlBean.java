package com.amalto.core.objects.routing.v2.ejb;

import java.rmi.RemoteException;
import java.util.Collection;

import javax.ejb.EJBException;
import javax.ejb.SessionBean;
import javax.ejb.SessionContext;
import javax.ejb.TimedObject;
import javax.ejb.Timer;

import com.amalto.core.delegator.BeanDelegatorContainer;
import com.amalto.core.objects.universe.ejb.UniversePOJO;
import com.amalto.core.util.XtentisException;


/**
 * @author bgrieder
 * 
 * @ejb.bean name="RoutingOrderV2Ctrl"
 *           display-name="RoutingOrderV2Ctrl"
 *           description="Routing Engine"
 *           jndi-name="amalto/remote/core/RoutingOrderCtrlV2"
 * 		  local-jndi-name = "amalto/local/core/RoutingOrderCtrlV2"
 *           type="Stateless"
 *           view-type="both"
 * 
 * @ejb.remote-facade
 * 
 * @ejb.permission
 * 	view-type = "remote"
 * 	role-name = "administration"
 * @ejb.permission
 * 	view-type = "local"
 * 	unchecked = "true"
 */
public class RoutingOrderV2CtrlBean implements SessionBean, TimedObject {
  
		
    
    /**
     * DocumentCtrlBean.java
     * Constructor
     * 
     */
    public RoutingOrderV2CtrlBean() {
        super();
    }

    /* (non-Javadoc)
     * @see javax.ejb.SessionBean#setSessionContext(javax.ejb.SessionContext)
     */
    public void setSessionContext(SessionContext ctx)
        throws EJBException,
        RemoteException {
    	BeanDelegatorContainer.getUniqueInstance().getRoutingOrderCtrlBeanDelegator().setSessionContext(ctx);
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
     * Executes a Routing Order now
     * @throws XtentisException
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method 
     */
    public String executeSynchronously(AbstractRoutingOrderV2POJO routingOrderPOJO) throws XtentisException {
    	return BeanDelegatorContainer.getUniqueInstance().getRoutingOrderCtrlBeanDelegator().executeSynchronously(routingOrderPOJO);
    }
    
    /**
     * Executes a Routing Order now
     * @throws XtentisException
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method 
     */
    public String executeSynchronously(AbstractRoutingOrderV2POJO routingOrderPOJO, boolean cleanUpRoutingOrder) throws XtentisException {    	
    	return BeanDelegatorContainer.getUniqueInstance().getRoutingOrderCtrlBeanDelegator().executeSynchronously(routingOrderPOJO,cleanUpRoutingOrder);
    }
    
    /**
	 * 
	 * Executes a Routing Order now in a particular universe
	 * @throws XtentisException
	 * 
	 * 
	 * @ejb.interface-method view-type = "both"
	 * @ejb.facade-method
	 */
	public String executeSynchronously(
			AbstractRoutingOrderV2POJO routingOrderPOJO,
			boolean cleanUpRoutingOrder, UniversePOJO universePOJO)
			throws XtentisException {

		return BeanDelegatorContainer.getUniqueInstance().getRoutingOrderCtrlBeanDelegator().executeSynchronously(routingOrderPOJO, cleanUpRoutingOrder,universePOJO);

	}
    
    
    /**
     * Executes a Routing Order in delay milliseconds
     * @throws XtentisException
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method 
     */
    public void executeAsynchronously(AbstractRoutingOrderV2POJO routingOrderPOJO, long delayInMillis) throws XtentisException {
    	BeanDelegatorContainer.getUniqueInstance().getRoutingOrderCtrlBeanDelegator().executeAsynchronously(routingOrderPOJO, delayInMillis);
    }

    /**
     * Executes a Routing Order in default DELAY milliseconds
     * @throws XtentisException
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method 
     */
    public void executeAsynchronously(AbstractRoutingOrderV2POJO routingOrderPOJO) throws XtentisException {
    	BeanDelegatorContainer.getUniqueInstance().getRoutingOrderCtrlBeanDelegator().executeAsynchronously(routingOrderPOJO);
    }
    
    /**
     * Remove an item
     * @throws XtentisException
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method 
     */
    public AbstractRoutingOrderV2POJOPK removeRoutingOrder(AbstractRoutingOrderV2POJOPK pk) 
    throws XtentisException{
    	return BeanDelegatorContainer.getUniqueInstance().getRoutingOrderCtrlBeanDelegator().removeRoutingOrder(pk);
    }    
    
    
    /**
     * Creates or updates a Transformer
     * @throws XtentisException
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method 
     */
    public AbstractRoutingOrderV2POJOPK putRoutingOrder(AbstractRoutingOrderV2POJO routingOrderPOJO) throws XtentisException{  
    	
    	return BeanDelegatorContainer.getUniqueInstance().getRoutingOrderCtrlBeanDelegator().putRoutingOrder(routingOrderPOJO);

    }
    
    
    /**
     * Get Routing Order
     * @throws XtentisException
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method 
     */
    public AbstractRoutingOrderV2POJO getRoutingOrder(AbstractRoutingOrderV2POJOPK pk) throws XtentisException{

    	return BeanDelegatorContainer.getUniqueInstance().getRoutingOrderCtrlBeanDelegator().getRoutingOrder(pk);
    }
    
    
    
    /**
     * Get a RoutingOrder knowing its class - no exception is thrown: returns null if not found 
     * @throws XtentisException
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method 
     */
    public AbstractRoutingOrderV2POJO existsRoutingOrder(AbstractRoutingOrderV2POJOPK pk) throws XtentisException{
    	
    	return BeanDelegatorContainer.getUniqueInstance().getRoutingOrderCtrlBeanDelegator().existsRoutingOrder(pk);
    }
    
    /**
     * Find Active Routing Orders
     * @throws XtentisException
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method 
     */
    public ActiveRoutingOrderV2POJO[] findActiveRoutingOrders(long lastScheduledTime, int maxRoutingOrders) throws XtentisException{
    	
    	return BeanDelegatorContainer.getUniqueInstance().getRoutingOrderCtrlBeanDelegator().findActiveRoutingOrders(lastScheduledTime, maxRoutingOrders);
    }
    	
    
    /**
     * Find Dead Routing Orders
     * @throws XtentisException
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method 
     */
    public ActiveRoutingOrderV2POJO[] findDeadRoutingOrders(long maxLastRunStartedTime, int maxRoutingOrders) throws XtentisException{
    	
    	return BeanDelegatorContainer.getUniqueInstance().getRoutingOrderCtrlBeanDelegator().findDeadRoutingOrders(maxLastRunStartedTime, maxRoutingOrders);
    }
    

    /**
	 * Retrieve all Active Routing Order PKs 
	 * 
	 * @throws XtentisException
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method 
     */       
    public Collection<ActiveRoutingOrderV2POJOPK> getActiveRoutingOrderPKs(String regex) throws XtentisException {
    	return BeanDelegatorContainer.getUniqueInstance().getRoutingOrderCtrlBeanDelegator().getActiveRoutingOrderPKs(regex);
    }

    /**
	 * Retrieve all Completed Routing Order PKs 
	 * 
	 * @throws XtentisException
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method 
     */       
    public Collection<CompletedRoutingOrderV2POJOPK> getCompletedRoutingOrderPKs(String regex) throws XtentisException {
    	return BeanDelegatorContainer.getUniqueInstance().getRoutingOrderCtrlBeanDelegator().getCompletedRoutingOrderPKs(regex);
    }

    /**
	 * Retrieve all Failed Routing Order PKs 
	 * 
	 * @throws XtentisException
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method 
     */       
    public Collection<FailedRoutingOrderV2POJOPK> getFailedRoutingOrderPKs(String regex) throws XtentisException {
    	return BeanDelegatorContainer.getUniqueInstance().getRoutingOrderCtrlBeanDelegator().getFailedRoutingOrderPKs(regex);
    }

    
    /**
	 * Retrieve all RoutingOrder PKs whatever the class
	 * 
	 * @throws XtentisException
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method 
     */       
    public Collection<AbstractRoutingOrderV2POJOPK> getAllRoutingOrderPKs(String regex) throws XtentisException {
    	return BeanDelegatorContainer.getUniqueInstance().getRoutingOrderCtrlBeanDelegator().getAllRoutingOrderPKs(regex);

    }
    
    
    /**
	 * Retrieve all RoutingOrder PKs by Criteria
	 * 
	 * @throws XtentisException
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method 
     */
    public Collection<AbstractRoutingOrderV2POJOPK> getRoutingOrderPKsByCriteria(
    	Class<? extends AbstractRoutingOrderV2POJO> routingOrderV2POJOClass,
    	String anyFieldContains,
        String name,
        long timeCreatedMin, long timeCreatedMax,
        long timeScheduledMin, long timeScheduledMax,
        long timeLastRunStartedMin, long timeLastRunStartedMax,
        long timeLastRunCompletedMin, long timeLastRunCompletedMax,
        String itemConceptContains,
        String itemIDsContain,
        String serviceJNDIContains,
        String serviceParametersContains,
        String messageContains
    ) throws XtentisException {
    	
    	return BeanDelegatorContainer.getUniqueInstance().getRoutingOrderCtrlBeanDelegator().getRoutingOrderPKsByCriteria(
    			routingOrderV2POJOClass,
    			anyFieldContains,
    			name,
    			timeCreatedMin,timeCreatedMax,
    			timeScheduledMin,timeScheduledMax,
    			timeLastRunStartedMin,timeLastRunStartedMax,
    			timeLastRunCompletedMin,timeLastRunCompletedMax,
    			itemConceptContains,
    			itemIDsContain,
    			serviceJNDIContains,
    			serviceParametersContains,
    			messageContains);

         
     }
    

            
	/* (non-Javadoc)
	 * @see javax.ejb.TimedObject#ejbTimeout(javax.ejb.Timer)
	 */
	public void ejbTimeout(Timer timer) {		
		
		 BeanDelegatorContainer.getUniqueInstance().getRoutingOrderCtrlBeanDelegator().ejbTimeout(timer);


	}
    
	
}