package com.amalto.core.delegator;

import java.util.Collection;

import com.amalto.core.objects.routing.v2.ejb.RoutingRulePOJO;
import com.amalto.core.objects.routing.v2.ejb.RoutingRulePOJOPK;
import com.amalto.core.util.XtentisException;


public class RoutingRuleCtrlBeanDefaultDelegator implements
		IRoutingRuleCtrlBeanDelegator {

	public RoutingRulePOJOPK putRoutingRule(RoutingRulePOJO routingRule)
			throws XtentisException {
		throw new XtentisException("Not Support!");
	}

	public RoutingRulePOJO getRoutingRule(RoutingRulePOJOPK pk)
			throws XtentisException {
		throw new XtentisException("Not Support!");
	}

	public RoutingRulePOJO existsRoutingRule(RoutingRulePOJOPK pk)
			throws XtentisException {
		throw new XtentisException("Not Support!");
	}

	public RoutingRulePOJOPK removeRoutingRule(RoutingRulePOJOPK pk)
			throws XtentisException {
		throw new XtentisException("Not Support!");
	}

	public Collection<RoutingRulePOJOPK> getRoutingRulePKs(String regex)
			throws XtentisException {
		throw new XtentisException("Not Support!");
	}

	

}
