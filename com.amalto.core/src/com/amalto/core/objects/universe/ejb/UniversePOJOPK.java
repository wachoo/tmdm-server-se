package com.amalto.core.objects.universe.ejb;

import com.amalto.core.ejb.ObjectPOJOPK;


public class UniversePOJOPK extends ObjectPOJOPK{
	
	/**
	 * @param pk
	 */
	public UniversePOJOPK(ObjectPOJOPK pk) {
		super(pk.getIds());
	}
	
	/**
	 * @param name
	 */
	public UniversePOJOPK(String name) {
		super(name);
	}

}
