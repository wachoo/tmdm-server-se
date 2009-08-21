package com.amalto.core.objects.synchronization.ejb;

import com.amalto.core.ejb.ItemPOJOPK;
import com.amalto.core.ejb.ObjectPOJOPK;


public class SynchronizationItemPOJOPK extends ObjectPOJOPK{
	
	public SynchronizationItemPOJOPK() {
    }
	
	public SynchronizationItemPOJOPK(ObjectPOJOPK pk) {
		super(pk.getIds());
	}
	
	public SynchronizationItemPOJOPK(String[] ids) {
		super(ids);
	}

	
	public SynchronizationItemPOJOPK(String revisionID, ItemPOJOPK itemPOJOPK) {
		this.setIds(new String[] {
			revisionID == null ? "" : revisionID,
			itemPOJOPK.getUniqueID()
		});
	}

	
	

}
