package com.amalto.core.objects.view.ejb;

import com.amalto.core.ejb.ObjectPOJOPK;


public class ViewPOJOPK extends ObjectPOJOPK{
	
	public ViewPOJOPK(ObjectPOJOPK pk) {
		super(pk.getIds());
	}
	
	public ViewPOJOPK(String name) {
		super(name);
	}

}
