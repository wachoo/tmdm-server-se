package com.amalto.core.objects.routing;

import com.amalto.core.objects.ObjectPOJOPK;


public class CompletedRoutingOrderV2POJOPK extends AbstractRoutingOrderV2POJOPK {

	public CompletedRoutingOrderV2POJOPK(ObjectPOJOPK pk) {
		super(pk.getIds()[0],AbstractRoutingOrderV2POJO.COMPLETED);
	}
	
	public CompletedRoutingOrderV2POJOPK(String name) {
		super(name,AbstractRoutingOrderV2POJO.COMPLETED);
	}
	

}
