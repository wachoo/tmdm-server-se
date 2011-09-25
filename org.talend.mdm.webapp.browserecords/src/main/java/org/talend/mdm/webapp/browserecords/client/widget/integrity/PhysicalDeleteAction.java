package org.talend.mdm.webapp.browserecords.client.widget.integrity;

import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.google.gwt.user.client.rpc.AsyncCallback;
import org.talend.mdm.webapp.browserecords.client.BrowseRecordsEvents;
import org.talend.mdm.webapp.browserecords.client.BrowseRecordsServiceAsync;
import org.talend.mdm.webapp.browserecords.client.model.ItemBean;
import org.talend.mdm.webapp.browserecords.client.model.ItemResult;

/**
 *
 */
class PhysicalDeleteAction implements DeleteAction {
    public void delete(ItemBean item, BrowseRecordsServiceAsync service, boolean override) {
        service.deleteItemBean(item, override, new AsyncCallback<ItemResult>() {
            public void onSuccess(ItemResult arg0) {
            }

            public void onFailure(Throwable caught) {
                Dispatcher.forwardEvent(BrowseRecordsEvents.Error, caught);
            }

        });
    }
}
