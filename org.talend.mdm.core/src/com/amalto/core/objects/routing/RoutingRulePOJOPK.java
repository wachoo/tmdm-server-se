package com.amalto.core.objects.routing;

import com.amalto.core.objects.ObjectPOJOPK;


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
