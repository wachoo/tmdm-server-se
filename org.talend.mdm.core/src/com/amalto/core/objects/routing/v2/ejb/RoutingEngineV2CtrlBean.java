package com.amalto.core.objects.routing.v2.ejb;

import java.rmi.RemoteException;

import javax.ejb.EJBException;
import javax.ejb.SessionBean;
import javax.ejb.SessionContext;
import javax.ejb.TimedObject;
import javax.ejb.Timer;

import com.amalto.core.delegator.BeanDelegatorContainer;
import com.amalto.core.ejb.ItemPOJOPK;
import com.amalto.core.util.XtentisException;


/**
 * @author bgrieder
 * 
 * @ejb.bean name="RoutingEngineV2Ctrl"
 *           display-name="RoutingEngineV2Ctrl"
 *           description="Routing Engine"
 *           jndi-name="amalto/remote/core/routingenginev2ctrl"
 * 		  local-jndi-name = "amalto/local/core/routingenginev2ctrl"
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
public class RoutingEngineV2CtrlBean implements SessionBean, TimedObject {
  

	
    /**
     * DocumentCtrlBean.java
     * Constructor
     * 
     */
    public RoutingEngineV2CtrlBean() {
        super();
        BeanDelegatorContainer.getUniqueInstance().getRoutingEngineV2CtrlBeanDelegator().init();
    }

    

    /* (non-Javadoc)
     * @see javax.ejb.SessionBean#setSessionContext(javax.ejb.SessionContext)
     */
    public void setSessionContext(SessionContext ctx)
        throws EJBException,
        RemoteException {
    	BeanDelegatorContainer.getUniqueInstance().getRoutingEngineV2CtrlBeanDelegator().setSessionContext(ctx);
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
     * Routes a document
     * @return the list of routing rules PKs that matched
     * @throws XtentisException
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method 
     */
    public RoutingRulePOJOPK[] route(ItemPOJOPK itemPOJOPK) throws XtentisException {
    	    	
		return BeanDelegatorContainer.getUniqueInstance().getRoutingEngineV2CtrlBeanDelegator().route(itemPOJOPK);
		
    }
    
    
    
    /*****************************************************************
     *  Engine LifeCycle
    *****************************************************************/

    
    /**
     * Starts/restarts the router
     * @throws XtentisException
     * 
     * @ejb.interface-method  view-type = "both"
     * @ejb.facade-method 
     */
    public void start() throws XtentisException{       
    	BeanDelegatorContainer.getUniqueInstance().getRoutingEngineV2CtrlBeanDelegator().start();	
    }
    

    
    /**
     * Stops the routing queue
     * @throws XtentisException
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method 
     */
    public void stop() throws XtentisException{       
    	BeanDelegatorContainer.getUniqueInstance().getRoutingEngineV2CtrlBeanDelegator().stop();
    }
  
    
    /**
     * Toggle suspend a routing queue
     * @throws XtentisException
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method 
     */
    public void suspend(boolean suspend) throws XtentisException{       
    	BeanDelegatorContainer.getUniqueInstance().getRoutingEngineV2CtrlBeanDelegator().suspend(suspend);
    }
    
    /**
     * Toggle suspend a routing queue
     * @throws XtentisException
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method 
     */
    public int getStatus() throws XtentisException{       
      	return BeanDelegatorContainer.getUniqueInstance().getRoutingEngineV2CtrlBeanDelegator().getStatus();
    }

    /*****************************************************************
     *  T I M E R
    *****************************************************************/
    
	/* (non-Javadoc)
	 * @see javax.ejb.TimedObject#ejbTimeout(javax.ejb.Timer)
	 */
	public void ejbTimeout(Timer timer) {
		BeanDelegatorContainer.getUniqueInstance().getRoutingEngineV2CtrlBeanDelegator().ejbTimeout(timer);
	}

}