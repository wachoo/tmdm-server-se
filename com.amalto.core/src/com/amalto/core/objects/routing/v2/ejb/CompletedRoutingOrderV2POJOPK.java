package com.amalto.core.objects.routing.v2.ejb;

import com.amalto.core.ejb.ObjectPOJOPK;


public class CompletedRoutingOrderV2POJOPK extends AbstractRoutingOrderV2POJOPK {

	public CompletedRoutingOrderV2POJOPK(ObjectPOJOPK pk) {
		super(pk.getIds()[0],AbstractRoutingOrderV2POJO.COMPLETED);
	}
	
	public CompletedRoutingOrderV2POJOPK(String name) {
		super(name,AbstractRoutingOrderV2POJO.COMPLETED);
	}
	

}
