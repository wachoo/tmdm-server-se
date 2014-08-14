package com.amalto.core.objects.storedprocedure.ejb;

import com.amalto.core.ejb.ObjectPOJOPK;


public class StoredProcedurePOJOPK extends ObjectPOJOPK{
	
	public StoredProcedurePOJOPK(String name) {
		super(name);
	}
	
	public StoredProcedurePOJOPK(ObjectPOJOPK pk) {
		super(pk.getIds());
	}
	

}
