package com.amalto.core.objects.role;

import com.amalto.core.objects.ObjectPOJOPK;


public class RolePOJOPK extends ObjectPOJOPK{
	
	public RolePOJOPK(ObjectPOJOPK pk) {
		super(pk.getIds());
	}
	
	public RolePOJOPK(String name) {
		super(name);
	}

}
