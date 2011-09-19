package org.talend.mdm.webapp.browserecords.client.model;

import java.io.Serializable;
import java.util.List;

import com.google.gwt.user.client.rpc.IsSerializable;

public class ItemBasePageLoadResult<Data> implements Serializable, IsSerializable {

    private List<Data> data;

    private int offset;

    private int totalLength;

    public ItemBasePageLoadResult() {
    }

    public ItemBasePageLoadResult(List<Data> data, int offset, int totalLength) {
        this.data = data;
        this.offset = offset;
        this.totalLength = totalLength;
    }

    public List<Data> getData() {
        return data;
    }

    public void setData(List<Data> data) {
        this.data = data;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public int getTotalLength() {
        return totalLength;
    }

    public void setTotalLength(int totalLength) {
        this.totalLength = totalLength;
    }
}
