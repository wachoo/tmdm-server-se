package com.amalto.core.objects.backgroundjob.ejb;

import com.amalto.core.ejb.ObjectPOJOPK;


public class BackgroundJobPOJOPK extends ObjectPOJOPK{
	
	/**
	 * @param pk
	 */
	public BackgroundJobPOJOPK(ObjectPOJOPK pk) {
		super(pk.getIds());
	}
	
	/**
	 * @param id
	 */
	public BackgroundJobPOJOPK(String id) {
		super(id);
	}

}
