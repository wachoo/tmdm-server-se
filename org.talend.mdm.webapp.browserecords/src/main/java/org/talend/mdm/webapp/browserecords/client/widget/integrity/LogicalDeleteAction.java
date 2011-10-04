package org.talend.mdm.webapp.browserecords.client.widget.integrity;

import org.talend.mdm.webapp.base.client.SessionAwareAsyncCallback;
import org.talend.mdm.webapp.browserecords.client.BrowseRecordsServiceAsync;
import org.talend.mdm.webapp.browserecords.client.model.ItemBean;

/**
 * Wraps a logical delete operation.
 */
public class LogicalDeleteAction implements DeleteAction {

    private String url;

    public LogicalDeleteAction(String url) {
        this.url = url;
    }

    public void delete(ItemBean item, BrowseRecordsServiceAsync service, boolean override) {
        service.logicalDeleteItem(item, url, override, new SessionAwareAsyncCallback<Void>() {

            public void onSuccess(Void arg0) {
            }
        });
    }

}
