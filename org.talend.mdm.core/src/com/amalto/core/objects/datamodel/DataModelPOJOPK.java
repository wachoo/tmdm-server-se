package com.amalto.core.objects.datamodel;

import com.amalto.core.objects.ObjectPOJOPK;


public class DataModelPOJOPK extends ObjectPOJOPK{
	
	/**
	 * @param pk
	 */
	public DataModelPOJOPK(ObjectPOJOPK pk) {
		super(pk.getIds());
	}
	
	/**
	 * @param name
	 */
	public DataModelPOJOPK(String name) {
		super(name);
	}

}
