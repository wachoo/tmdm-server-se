package com.amalto.core.objects.versioning.ejb;

import com.amalto.core.ejb.ObjectPOJOPK;


public class VersioningSystemPOJOPK extends ObjectPOJOPK {

	public VersioningSystemPOJOPK(ObjectPOJOPK pk) {
		super(pk.getIds());
	}
	
	public VersioningSystemPOJOPK(String name) {
		super(new String[] {name});
	}
	

}
