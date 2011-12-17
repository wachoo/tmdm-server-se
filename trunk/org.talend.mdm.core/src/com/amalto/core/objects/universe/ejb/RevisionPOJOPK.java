package com.amalto.core.objects.universe.ejb;


import com.amalto.core.ejb.ObjectPOJOPK;

public class RevisionPOJOPK  extends ObjectPOJOPK {
	/**
	 * @param pk
	 */
	public RevisionPOJOPK(ObjectPOJOPK pk) {
		super(pk.getIds());
	}
	
	/**
	 * @param name
	 */
	public RevisionPOJOPK(String name) {
		super(name);
	}
}
