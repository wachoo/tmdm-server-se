package com.amalto.core.objects.routing.v2.ejb;

import java.rmi.RemoteException;
import java.util.Collection;

import javax.ejb.EJBException;
import javax.ejb.SessionBean;
import javax.ejb.SessionContext;

import com.amalto.core.delegator.BeanDelegatorContainer;
import com.amalto.core.util.XtentisException;



/**
 * @author bgrieder
 * 
 * @ejb.bean name="RoutingRuleCtrl"
 *          display-name="Name for RoutingRuleCtrl"
 *          description="Description for RoutingRuleCtrl"
 *          jndi-name="amalto/remote/core/routingrulectrl"
 * 		  	local-jndi-name = "amalto/local/core/routingrulectrl"
 *          type="Stateless"
 *          routingRule-type="both"
 * 
 * @ejb.remote-facade
 * 
 * @ejb.permission
 * 	routingRule-type = "remote"
 * 	role-name = "administration"
 * @ejb.permission
 * 	routingRule-type = "local"
 * 	unchecked = "true"
 * 
 *
 */
public class RoutingRuleCtrlBean implements SessionBean {
     
    /**
     * DataRoutingRuleCtrlBean.java
     * Constructor
     * 
     */
    public RoutingRuleCtrlBean() {
        super();
    }

    /* (non-Javadoc)
     * @see javax.ejb.SessionBean#setSessionContext(javax.ejb.SessionContext)
     */
    public void setSessionContext(SessionContext ctx)
        throws EJBException,
        RemoteException {
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
     * @ejb.create-method  routingRule-type = "local"
     */
    public void ejbCreate() throws javax.ejb.CreateException {}
    
    /**
     * Post Create method
     */
    public void ejbPostCreate() throws javax.ejb.CreateException {}
    
    
    
    
    
    /**
     * Creates or updates a menu
     * @throwsXtentisxception
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method 
     */
    public RoutingRulePOJOPK putRoutingRule(RoutingRulePOJO routingRule) throws XtentisException{
        return BeanDelegatorContainer.getUniqueInstance().getRoutingRuleCtrlBeanDelegator().putRoutingRule(routingRule);
    }
    
 
     
    /**
     * Get menu
     * @throws XtentisException
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method 
     */
    public RoutingRulePOJO getRoutingRule(RoutingRulePOJOPK pk) 
    throws XtentisException{
    	return BeanDelegatorContainer.getUniqueInstance().getRoutingRuleCtrlBeanDelegator().getRoutingRule(pk);
    }
    
    
    /**
     * Get a RoutingRule - no exception is thrown: returns null if not found 
     * @throws XtentisException
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method 
     */
    public RoutingRulePOJO existsRoutingRule(RoutingRulePOJOPK pk) throws XtentisException{
    	
    	return BeanDelegatorContainer.getUniqueInstance().getRoutingRuleCtrlBeanDelegator().existsRoutingRule(pk);
    }
    
    /**
     * Remove a RoutingRule
     * @throws XtentisException
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method 
     */
    public RoutingRulePOJOPK removeRoutingRule(RoutingRulePOJOPK pk) 
    throws XtentisException{
    	
    	return BeanDelegatorContainer.getUniqueInstance().getRoutingRuleCtrlBeanDelegator().removeRoutingRule(pk);
    }    
    
    
    /**
	 * Retrieve all RoutingRule PKs 
	 * 
	 * @throws XtentisException
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method 
     */       
    public Collection<RoutingRulePOJOPK> getRoutingRulePKs(String regex) throws XtentisException {
    	return BeanDelegatorContainer.getUniqueInstance().getRoutingRuleCtrlBeanDelegator().getRoutingRulePKs(regex);
    }
   

}