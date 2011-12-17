package com.amalto.core.objects.role.ejb;

import com.amalto.core.ejb.ObjectPOJOPK;


public class RolePOJOPK extends ObjectPOJOPK{
	
	public RolePOJOPK(ObjectPOJOPK pk) {
		super(pk.getIds());
	}
	
	public RolePOJOPK(String name) {
		super(name);
	}

}
