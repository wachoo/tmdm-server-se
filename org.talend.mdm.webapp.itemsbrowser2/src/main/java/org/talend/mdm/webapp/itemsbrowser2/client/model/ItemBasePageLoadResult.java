package org.talend.mdm.webapp.itemsbrowser2.client.model;

import java.util.List;

import com.extjs.gxt.ui.client.data.BasePagingLoadResult;
import com.google.gwt.user.client.rpc.IsSerializable;


public class ItemBasePageLoadResult<Data> extends BasePagingLoadResult<Data> implements IsSerializable {

    public ItemBasePageLoadResult(List<Data> data, int offset, int totalLength) {
        super(data, offset, totalLength);
    }

    public ItemBasePageLoadResult(List<Data> data) {
        super(data);
    }


}
