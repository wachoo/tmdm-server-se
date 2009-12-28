package com.amalto.core.delegator.enterprise;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import com.amalto.core.delegator.RoutingRuleCtrlBeanDefaultDelegator;
import com.amalto.core.ejb.ObjectPOJO;
import com.amalto.core.ejb.ObjectPOJOPK;
import com.amalto.core.objects.routing.v2.ejb.RoutingRulePOJO;
import com.amalto.core.objects.routing.v2.ejb.RoutingRulePOJOPK;
import com.amalto.core.util.XtentisException;

public class RoutingRuleCtrlBeanEnterpriseDelegator extends
		RoutingRuleCtrlBeanDefaultDelegator {
	
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
   
   public Collection<RoutingRulePOJOPK> getRoutingRulePKs(String regex) throws XtentisException {
   	Collection<ObjectPOJOPK> c = ObjectPOJO.findAllPKs(RoutingRulePOJO.class,regex);
   	ArrayList<RoutingRulePOJOPK> l = new ArrayList<RoutingRulePOJOPK>();
   	for (Iterator<ObjectPOJOPK> iter = c.iterator(); iter.hasNext(); ) {
			l.add(new RoutingRulePOJOPK(iter.next()));
		}
   	return l;
   }

}
