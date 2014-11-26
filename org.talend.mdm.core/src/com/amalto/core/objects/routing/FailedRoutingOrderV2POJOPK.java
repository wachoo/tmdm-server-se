package com.amalto.core.objects.routing;

import com.amalto.core.objects.ObjectPOJOPK;


public class FailedRoutingOrderV2POJOPK extends AbstractRoutingOrderV2POJOPK {

	public FailedRoutingOrderV2POJOPK(ObjectPOJOPK pk) {
		super(pk.getIds()[0],AbstractRoutingOrderV2POJO.FAILED);
	}
	
	public FailedRoutingOrderV2POJOPK(String name) {
		super(name,AbstractRoutingOrderV2POJO.FAILED);
	}
	

}
