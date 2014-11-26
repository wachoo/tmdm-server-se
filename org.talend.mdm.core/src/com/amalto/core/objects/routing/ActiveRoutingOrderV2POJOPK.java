package com.amalto.core.objects.routing;

import com.amalto.core.objects.ObjectPOJOPK;


public class ActiveRoutingOrderV2POJOPK extends AbstractRoutingOrderV2POJOPK {

	public ActiveRoutingOrderV2POJOPK(ObjectPOJOPK pk) {
		super(pk.getIds()[0],AbstractRoutingOrderV2POJO.ACTIVE);
	}
	
	public ActiveRoutingOrderV2POJOPK(String name) {
		super(name,AbstractRoutingOrderV2POJO.ACTIVE);
	}
	

}
