package com.amalto.core.objects.routing.v2.ejb;

import com.amalto.core.ejb.ObjectPOJOPK;


public class RoutingEngineV2POJOPK extends ObjectPOJOPK {

	public RoutingEngineV2POJOPK(ObjectPOJOPK pk) {
		super(pk.getIds());
	}
	
	public RoutingEngineV2POJOPK(String name) {
		super(new String[] {name});
	}
	

}
