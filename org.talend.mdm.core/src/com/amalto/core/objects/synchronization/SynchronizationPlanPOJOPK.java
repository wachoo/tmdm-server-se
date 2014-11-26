package com.amalto.core.objects.synchronization;

import com.amalto.core.objects.ObjectPOJOPK;


public class SynchronizationPlanPOJOPK extends ObjectPOJOPK{
	
	/**
	 * @param pk
	 */
	public SynchronizationPlanPOJOPK(ObjectPOJOPK pk) {
		super(pk.getIds());
	}
	
	/**
	 * @param name
	 */
	public SynchronizationPlanPOJOPK(String name) {
		super(name);
	}

}
