package com.amalto.core.objects.routing.v2.ejb;

import java.io.Serializable;

import com.amalto.core.objects.universe.ejb.UniversePOJO;

public class AsynchronousOrderData implements Serializable{
    /**
		 * 
		 */
	private static final long serialVersionUID = 7795390018723403569L;

	protected UniversePOJO currentUniversePOJO;

    protected AbstractRoutingOrderV2POJO routingOrderV2POJO;

    public AsynchronousOrderData(UniversePOJO currentUniversePOJO, AbstractRoutingOrderV2POJO routingOrderV2POJO) {

            super();

            this.currentUniversePOJO = currentUniversePOJO;

            this.routingOrderV2POJO = routingOrderV2POJO;

     }

 }
