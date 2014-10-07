package com.amalto.webapp.core.bean;

import java.io.Serializable;

/*
 * generic bean for Ext.2.0 datastore
 * must be used with dwrproxy
 */

public class ListRange implements Serializable {

	private static final long serialVersionUID = 6239586023660639700L;

	private Object[] data;

    private int totalSize;

    private boolean isPagingAccurate;

    public Object[] getData() {
        return data;
    }

    public void setData(Object[] data) {
        this.data = data;
    }

    public int getTotalSize() {
        return totalSize;
    }

    public void setTotalSize(int totalSize) {
        this.totalSize = totalSize;
    }

    public boolean isPagingAccurate() {
        return isPagingAccurate;
    }

    public void setPagingAccurate(boolean isPagingAccurate) {
        this.isPagingAccurate = isPagingAccurate;
    }
}