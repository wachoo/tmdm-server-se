package com.amalto.core.objects.synchronization;

import com.amalto.core.objects.ItemPOJOPK;
import com.amalto.core.objects.ObjectPOJOPK;


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
