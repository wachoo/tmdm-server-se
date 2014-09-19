package com.amalto.core.objects.synchronization.ejb;

import com.amalto.core.ejb.ObjectPOJOPK;


public class SynchronizationObjectPOJOPK extends ObjectPOJOPK{
	
	public SynchronizationObjectPOJOPK(ObjectPOJOPK pk) {
		super(pk.getIds());
	}
	

	public SynchronizationObjectPOJOPK(String revisionID, String objectName, String objectUniqueID) {
		this.setIds(new String[] {
			revisionID == null ? "" : revisionID,
			objectName,
			objectUniqueID
		});
	}

}
