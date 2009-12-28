package com.amalto.core.delegator;

import java.util.Collection;

import com.amalto.core.objects.routing.v2.ejb.RoutingRulePOJO;
import com.amalto.core.objects.routing.v2.ejb.RoutingRulePOJOPK;
import com.amalto.core.util.XtentisException;


public interface IRoutingRuleCtrlBeanDelegator extends BeanDelegator {
	
	public RoutingRulePOJOPK putRoutingRule(RoutingRulePOJO routingRule) throws XtentisException;
	
	public RoutingRulePOJO getRoutingRule(RoutingRulePOJOPK pk) throws XtentisException;
	
	public RoutingRulePOJO existsRoutingRule(RoutingRulePOJOPK pk) throws XtentisException;
	
	public RoutingRulePOJOPK removeRoutingRule(RoutingRulePOJOPK pk) 
	   throws XtentisException;
	
	public Collection<RoutingRulePOJOPK> getRoutingRulePKs(String regex) throws XtentisException;
	
}
