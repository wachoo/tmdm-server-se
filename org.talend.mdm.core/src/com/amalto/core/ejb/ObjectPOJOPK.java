package com.amalto.core.ejb;

import java.io.Serializable;

import com.amalto.core.util.Util;


public class ObjectPOJOPK implements Serializable{

	String[] ids = null;

	public ObjectPOJOPK (String[] itemIds) {
		this.ids = itemIds;
	}

	public ObjectPOJOPK (String uniqueid) {
		this(uniqueid == null ? null : uniqueid.split("\\.\\."));
	}

	/**
	 * For Marshalling puproses only
	 */
	public ObjectPOJOPK () {
	}


	public String[] getIds() {
		return ids;
	}

	public void setIds(String[] ids) {
		this.ids = ids;
	}

	public String toString() {
		return Util.joinStrings(ids, "..");
	}

	public String getUniqueId() {
		return toString();
	}

	@Override
	public boolean equals(Object obj) {
        if (obj instanceof ObjectPOJOPK) {
        	return this.getUniqueId().equals(((ObjectPOJOPK)obj).getUniqueId());
        }
        return false;
	}

}
