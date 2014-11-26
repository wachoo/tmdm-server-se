package com.amalto.core.objects.universe;

import com.amalto.core.objects.ObjectPOJOPK;


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
