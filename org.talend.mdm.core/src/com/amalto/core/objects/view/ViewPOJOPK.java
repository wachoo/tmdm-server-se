package com.amalto.core.objects.view;

import com.amalto.core.objects.ObjectPOJOPK;


public class ViewPOJOPK extends ObjectPOJOPK{
	
	public ViewPOJOPK(ObjectPOJOPK pk) {
		super(pk.getIds());
	}
	
	public ViewPOJOPK(String name) {
		super(name);
	}

}
