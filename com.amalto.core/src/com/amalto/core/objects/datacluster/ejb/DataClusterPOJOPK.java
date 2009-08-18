package com.amalto.core.objects.datacluster.ejb;

import com.amalto.core.ejb.ObjectPOJOPK;


public class DataClusterPOJOPK extends ObjectPOJOPK{
	
	/**
	 * @param pk
	 */
	public DataClusterPOJOPK(ObjectPOJOPK pk) {
		super(pk.getIds());
	}
	
	/**
	 * For Marshaling purposes only
	 */
	public DataClusterPOJOPK() {
		super();
	}
	
	/**
	 * @param name
	 */
	public DataClusterPOJOPK(String name) {
		super(new String[] {name});
	}

}
