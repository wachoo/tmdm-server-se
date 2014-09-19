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
	 * For marshalling purposes only
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
		return Util.joinStrings(ids, "");
	}

	public String getUniqueId() {
		return toString();
	}

	@Override
	public boolean equals(Object obj) {
        return obj instanceof ObjectPOJOPK && this.getUniqueId().equals(((ObjectPOJOPK) obj).getUniqueId());
    }

}
