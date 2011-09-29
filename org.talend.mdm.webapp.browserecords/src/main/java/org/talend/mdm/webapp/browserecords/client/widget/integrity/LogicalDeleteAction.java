package org.talend.mdm.webapp.browserecords.client.widget.integrity;

import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.google.gwt.user.client.rpc.AsyncCallback;
import org.talend.mdm.webapp.browserecords.client.BrowseRecordsEvents;
import org.talend.mdm.webapp.browserecords.client.BrowseRecordsServiceAsync;
import org.talend.mdm.webapp.browserecords.client.model.ItemBean;
import org.talend.mdm.webapp.browserecords.client.model.ItemResult;

/**
 * Wraps a logical delete operation.
 */
public class LogicalDeleteAction implements DeleteAction {

    private String url;

    public LogicalDeleteAction(String url) {
        this.url = url;
    }

    public void delete(ItemBean item, BrowseRecordsServiceAsync service, boolean override) {
        service.logicalDeleteItem(item, url, override, new AsyncCallback<ItemResult>() {
            public void onSuccess(ItemResult arg0) {
            }

            public void onFailure(Throwable caught) {
                Dispatcher.forwardEvent(BrowseRecordsEvents.Error, caught);
            }
        });
    }

}
