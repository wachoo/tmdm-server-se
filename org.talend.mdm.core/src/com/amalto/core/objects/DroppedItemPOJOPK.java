package com.amalto.core.objects;

import java.io.Serializable;

public class DroppedItemPOJOPK implements Serializable {
	
	private ItemPOJOPK refItemPOJOPK;
	
	private String partPath;
	
    public DroppedItemPOJOPK(ItemPOJOPK refItemPOJOPK, String partPath) {
        this.refItemPOJOPK = refItemPOJOPK;
        this.partPath = partPath;
    }

    public String getUniquePK() {
        return "head." + refItemPOJOPK.getUniqueID() + convertItemPartPath(partPath);  //$NON-NLS-1$
    }

    public ItemPOJOPK getRefItemPOJOPK() {
		return refItemPOJOPK;
	}
	
	public String getPartPath() {
		return partPath;
	}

    private static String convertItemPartPath(String partPath) {
        if (partPath != null && partPath.length() > 0) {
            partPath = partPath.replaceAll("/", "-");  //$NON-NLS-1$  //$NON-NLS-2$
        }
        return partPath;
    }

    @Override
	public String toString() {
		return getUniquePK();
	}

	@Override
	public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof DroppedItemPOJOPK)) {
            return false;
        }
        DroppedItemPOJOPK other = (DroppedItemPOJOPK) obj;
        return other.getUniquePK().equals(this.getUniquePK());
    }
}
