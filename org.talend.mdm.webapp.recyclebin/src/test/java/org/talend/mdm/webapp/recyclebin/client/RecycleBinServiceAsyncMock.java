package org.talend.mdm.webapp.recyclebin.client;

import java.util.List;

import org.talend.mdm.webapp.recyclebin.shared.ItemsTrashItem;

import com.extjs.gxt.ui.client.data.BasePagingLoadResult;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class RecycleBinServiceAsyncMock implements RecycleBinServiceAsync {

    public void checkConflict(String itemPk, String conceptName, String id, AsyncCallback<Boolean> callback) {
        callback.onSuccess(true);
    }

    public void getCurrentDataCluster(AsyncCallback<String> callback) {
        callback.onSuccess("Product");
    }

    public void getCurrentDataModel(AsyncCallback<String> callback) {
        callback.onSuccess("Product");
    }

    public void getTrashItems(String regex, PagingLoadConfig load, AsyncCallback<PagingLoadResult<ItemsTrashItem>> callback) {
        List<ItemsTrashItem> items = FakeData.getItems();
        PagingLoadResult<ItemsTrashItem> result = new BasePagingLoadResult<ItemsTrashItem>(items);
        callback.onSuccess(result);
    }

    public void isEntityPhysicalDeletable(String conceptName, AsyncCallback<Boolean> callback) {
        callback.onSuccess(true);
    }

    public void recoverDroppedItem(String itemPk, String partPath, String revisionId, String conceptName, String modelName,
            String ids, AsyncCallback<Void> callback) {

    }

    @Override
    public void removeDroppedItem(String itemPk, String partPath, String revisionId, String conceptName, String ids,
            String language, AsyncCallback<String> callback) {
        FakeData.remvoeItem(itemPk, conceptName, ids);
        callback.onSuccess("[EN:del succ][FR:del succ]");
    }

}
