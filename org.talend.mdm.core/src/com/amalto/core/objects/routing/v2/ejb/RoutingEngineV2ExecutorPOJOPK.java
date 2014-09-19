package com.amalto.core.objects.routing.v2.ejb;

import com.amalto.core.ejb.ObjectPOJOPK;


public class RoutingEngineV2ExecutorPOJOPK extends ObjectPOJOPK {

	public RoutingEngineV2ExecutorPOJOPK(ObjectPOJOPK pk) {
		super(pk.getIds());
	}
	
	public RoutingEngineV2ExecutorPOJOPK(RoutingEngineV2POJOPK routingEngineV2POJOPK, String name) {
		super(new String[] {routingEngineV2POJOPK.getUniqueId(),name});
	}
	

}
