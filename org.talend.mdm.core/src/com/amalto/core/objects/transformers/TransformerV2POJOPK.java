package com.amalto.core.objects.transformers;

import com.amalto.core.objects.ObjectPOJOPK;


public class TransformerV2POJOPK extends ObjectPOJOPK {

	public TransformerV2POJOPK(ObjectPOJOPK pk) {
		super(pk.getIds());
	}
	
	public TransformerV2POJOPK(String name) {
		super(new String[] {name});
	}
	

}
