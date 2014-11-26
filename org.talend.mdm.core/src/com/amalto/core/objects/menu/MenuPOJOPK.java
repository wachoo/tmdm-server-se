package com.amalto.core.objects.menu;

import com.amalto.core.objects.ObjectPOJOPK;


public class MenuPOJOPK extends ObjectPOJOPK{
	
	public MenuPOJOPK(ObjectPOJOPK pk) {
		super(pk.getIds());
	}
	
	public MenuPOJOPK(String name) {
		super(name);
	}

}
