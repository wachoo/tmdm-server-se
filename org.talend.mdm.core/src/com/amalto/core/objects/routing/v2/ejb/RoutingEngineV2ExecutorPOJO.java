package com.amalto.core.objects.routing.v2.ejb;

import com.amalto.core.ejb.ObjectPOJO;
import com.amalto.core.ejb.ObjectPOJOPK;



/**
 * @author bgrieder
 * 
 */
public class RoutingEngineV2ExecutorPOJO extends ObjectPOJO{
   

	//check if cluster exists - if not create - we have a singleton for the moment.
//	{
//		ObjectPOJO.createCluster(RoutingEngineV2ExecutorPOJO.class);
//	}
//	
	
    private String name;
    private RoutingEngineV2POJOPK routingEnginePK;
    private AbstractRoutingOrderV2POJOPK executingRoutingOrderPK;
    
        
	public RoutingEngineV2ExecutorPOJO(
		String name,
		RoutingEngineV2POJOPK routingEnginePK,
		AbstractRoutingOrderV2POJOPK executingRoutingOrderPK) {
		super();
		this.name = name;
		this.routingEnginePK = routingEnginePK;
		this.executingRoutingOrderPK = executingRoutingOrderPK;
	}




	public String getName() {
		return name;
	}

	public void setName(
		String name) {
		this.name = name;
	}

	public RoutingEngineV2POJOPK getRoutingEnginePK() {
		return routingEnginePK;
	}

	public void setRoutingEnginePK(
		RoutingEngineV2POJOPK routingEnginePK) {
		this.routingEnginePK = routingEnginePK;
	}

	public AbstractRoutingOrderV2POJOPK getExecutingRoutingOrderPK() {
		return executingRoutingOrderPK;
	}

	public void setExecutingRoutingOrderPK(
		AbstractRoutingOrderV2POJOPK executingRoutingOrderPK) {
		this.executingRoutingOrderPK = executingRoutingOrderPK;
	}

	@Override
    public ObjectPOJOPK getPK() {
    	return new ObjectPOJOPK(new String[]{routingEnginePK.getUniqueId(), name});
    }
}
