package com.amalto.core.objects.routing.v2.ejb;

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
        org.apache.log4j.Logger.getLogger(this.getClass()).debug("createRoutingRule() ");
        
        try {
        	if (
        			(routingRule.getConcept() == null) ||
        			"".equals(routingRule.getConcept())
        		)
        			routingRule.setConcept("*");
            ObjectPOJOPK pk = routingRule.store();
            if (pk == null) throw new XtentisException("Unable to create the Routing Rule. Please check the XML Server logs");
            return new RoutingRulePOJOPK(pk);
            
	    } catch (Exception e) {
    	    String err = "Unable to create/update the menu "+routingRule.getName()
    	    		+": "+e.getClass().getName()+": "+e.getLocalizedMessage();
    	    org.apache.log4j.Logger.getLogger(this.getClass()).error(err, e);
    	    throw new XtentisException(err);
	    }

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
        org.apache.log4j.Logger.getLogger(this.getClass()).debug("getRoutingRule() ");
        
        try {
        	RoutingRulePOJO rule =  ObjectPOJO.load(RoutingRulePOJO.class,pk);
        	if (rule == null) {
        		String err= "The Routing Rule "+pk.getUniqueId()+" does not exist.";
        		org.apache.log4j.Logger.getLogger(this.getClass()).error(err);
        		throw new XtentisException(err);
        	}
        	return rule;
            
	    } catch (Exception e) {
    	    String err = "Unable to get the Routing Rule "+pk.toString()
    	    		+": "+e.getClass().getName()+": "+e.getLocalizedMessage();
    	    org.apache.log4j.Logger.getLogger(this.getClass()).error(err, e);
    	    throw new XtentisException(err);
	    }
    }
    
    
    /**
     * Get a RoutingRule - no exception is thrown: returns null if not found 
     * @throws XtentisException
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method 
     */
    public RoutingRulePOJO existsRoutingRule(RoutingRulePOJOPK pk) throws XtentisException{
    	
        try {
        	return ObjectPOJO.load(RoutingRulePOJO.class,pk);        	
	    } catch (XtentisException e) {
	    	return null;
	    } catch (Exception e) {
    	    String info = "Could not check whether this Routing Rule \""+pk.getUniqueId()+"\" exists:  "
    	    		+": "+e.getClass().getName()+": "+e.getLocalizedMessage();
    	    org.apache.log4j.Logger.getLogger(this.getClass()).debug(info, e);
    	   return null;
	    }
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
    	org.apache.log4j.Logger.getLogger(this.getClass()).debug("Removing "+pk.getUniqueId());
        try {
        	return new RoutingRulePOJOPK(ObjectPOJO.remove(RoutingRulePOJO.class,pk));
	    } catch (XtentisException e) {
	    	throw(e);
	    } catch (Exception e) {
    	    String err = "Unable to remove the Routing Rule "+pk.toString()
    	    		+": "+e.getClass().getName()+": "+e.getLocalizedMessage();
    	    org.apache.log4j.Logger.getLogger(this.getClass()).error(err, e);
    	    throw new XtentisException(err);
	    }
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
    	Collection<ObjectPOJOPK> c = ObjectPOJO.findAllPKs(RoutingRulePOJO.class,regex);
    	ArrayList<RoutingRulePOJOPK> l = new ArrayList<RoutingRulePOJOPK>();
    	for (Iterator<ObjectPOJOPK> iter = c.iterator(); iter.hasNext(); ) {
			l.add(new RoutingRulePOJOPK(iter.next()));
		}
    	return l;
    }
   

}