package com.amalto.core.objects.routing.v2.ejb;

import com.amalto.core.ejb.ObjectPOJOPK;


public class RoutingRulePOJOPK extends ObjectPOJOPK{
	
	/**
	 * @param pk
	 */
	public RoutingRulePOJOPK(ObjectPOJOPK pk) {
		super(pk.getIds());
	}
	
	/**
	 * @param name
	 */
	public RoutingRulePOJOPK(String name) {
		super(name);
	}

}
