package com.amalto.core.objects.storedprocedure;

import com.amalto.core.objects.ObjectPOJOPK;


public class StoredProcedurePOJOPK extends ObjectPOJOPK{
	
	public StoredProcedurePOJOPK(String name) {
		super(name);
	}
	
	public StoredProcedurePOJOPK(ObjectPOJOPK pk) {
		super(pk.getIds());
	}
	

}
