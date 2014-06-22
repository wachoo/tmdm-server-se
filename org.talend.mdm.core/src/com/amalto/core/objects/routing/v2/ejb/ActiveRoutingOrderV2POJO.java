package com.amalto.core.objects.routing.v2.ejb;

import java.util.Date;

import com.amalto.core.ejb.ItemPOJOPK;

public class ActiveRoutingOrderV2POJO extends AbstractRoutingOrderV2POJO implements Comparable<ActiveRoutingOrderV2POJO>{
	
	public ActiveRoutingOrderV2POJO() {
		super();
		this.setStatus(AbstractRoutingOrderV2POJO.ACTIVE);
	}

    public ActiveRoutingOrderV2POJO(
		String name,
		long timeScheduled,
		ItemPOJOPK itemPOJOPK,
		String message,
		String serviceJNDI,
		String serviceParameters,
		String bindingUniverseName,
		String bindingUserToken
	) {
		super();
		if (message==null) message = "";
		if (! "".equals(message)) message+="\n";
		message+="---> CREATED "+sdf.format(new Date());
		this.setName(name);
		this.setItemPOJOPK(itemPOJOPK);
		this.setMessage(message);
		this.setStatus(AbstractRoutingOrderV2POJO.ACTIVE);
		this.setTimeCreated(System.currentTimeMillis());
		this.setTimeLastRunCompleted(-1);
		this.setTimeLastRunStarted(-1);
		this.setTimeScheduled(timeScheduled);
		this.setServiceJNDI(serviceJNDI);
		this.setServiceParameters(serviceParameters);
		this.setRoutingEnginePOJOPK(null);
		this.setRoutingEngineToken(null);
		this.setBindingUniverseName(bindingUniverseName);
		this.setBindingUserToken(bindingUserToken);
	}
    
    /**
     * Sorts the Routing Orders in Scheduled Time Order
     */
    public int compareTo(ActiveRoutingOrderV2POJO o) {
    	return new Long(this.getTimeScheduled()).compareTo(o.getTimeScheduled());
 	}    
	
}
