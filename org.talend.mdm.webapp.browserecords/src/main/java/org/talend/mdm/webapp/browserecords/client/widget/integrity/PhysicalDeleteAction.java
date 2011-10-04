package org.talend.mdm.webapp.browserecords.client.widget.integrity;

import org.talend.mdm.webapp.base.client.SessionAwareAsyncCallback;
import org.talend.mdm.webapp.browserecords.client.BrowseRecordsServiceAsync;
import org.talend.mdm.webapp.browserecords.client.model.ItemBean;

/**
 * Wraps a physical delete operation.
 */
// Implementation package visibility for class is intended: no need to see this class outside of package
class PhysicalDeleteAction implements DeleteAction {

    public void delete(ItemBean item, BrowseRecordsServiceAsync service, boolean override) {
        service.deleteItemBean(item, override, new SessionAwareAsyncCallback<String>() {

            public void onSuccess(String arg0) {
            }
        });
    }
}
