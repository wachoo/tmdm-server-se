package com.amalto.core.objects.routing;


public class CompletedRoutingOrderV2POJO extends AbstractRoutingOrderV2POJO{

	
	public CompletedRoutingOrderV2POJO() {
		this.setStatus(COMPLETED);
	}
	
	public CompletedRoutingOrderV2POJO(AbstractRoutingOrderV2POJO roPOJO) {
		super();
		setName(roPOJO.getName());
		setItemPOJOPK(roPOJO.getItemPOJOPK());
		setMessage(roPOJO.getMessage());
		setStatus(COMPLETED);
		setTimeCreated(roPOJO.getTimeCreated());
		setTimeLastRunCompleted(System.currentTimeMillis());
		setTimeLastRunStarted(roPOJO.getTimeLastRunStarted());
		setTimeScheduled(roPOJO.getTimeScheduled());
		setServiceJNDI(roPOJO.getServiceJNDI());
		setServiceParameters(roPOJO.getServiceParameters());
		setBindingUniverseName(roPOJO.getBindingUniverseName());
		setBindingUserToken(roPOJO.getBindingUserToken());
		
	}
	
}
