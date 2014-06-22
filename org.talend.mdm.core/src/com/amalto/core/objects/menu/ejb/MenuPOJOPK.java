package com.amalto.core.objects.menu.ejb;

import com.amalto.core.ejb.ObjectPOJOPK;


public class MenuPOJOPK extends ObjectPOJOPK{
	
	public MenuPOJOPK(ObjectPOJOPK pk) {
		super(pk.getIds());
	}
	
	public MenuPOJOPK(String name) {
		super(name);
	}

}
