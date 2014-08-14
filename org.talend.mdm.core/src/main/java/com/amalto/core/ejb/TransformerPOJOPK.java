package com.amalto.core.ejb;


/**
 * @deprecated - use TransformerV2 package
 */


public class TransformerPOJOPK extends ObjectPOJOPK {

	public TransformerPOJOPK(ObjectPOJOPK pk) {
		super(pk.getIds());
	}
	
	public TransformerPOJOPK(String name) {
		super(new String[] {name});
	}
	

}
