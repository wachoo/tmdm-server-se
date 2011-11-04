package org.talend.mdm.webapp.browserecords.client.widget.integrity;

import org.talend.mdm.webapp.base.client.SessionAwareAsyncCallback;
import org.talend.mdm.webapp.browserecords.client.BrowseRecordsServiceAsync;
import org.talend.mdm.webapp.browserecords.client.model.ItemBean;
import org.talend.mdm.webapp.browserecords.client.widget.ItemDetailToolBar;

/**
 * Wraps a logical delete operation.
 */
public class LogicalDeleteAction implements DeleteAction {

    private String url;
    
    private ItemDetailToolBar bar;

    /**
     * 
     * DOC Administrator LogicalDeleteAction constructor comment.
     * 
     * @param url
     * @param bar The render toolbar(record could be opened from many places)
     */
    public LogicalDeleteAction(String url, ItemDetailToolBar bar) {
        this.url = url;
        this.bar = bar;
    }

    public void delete(final ItemBean item, BrowseRecordsServiceAsync service, boolean override, final PostDeleteAction postDeleteAction) {
        service.logicalDeleteItem(item, url, override, new SessionAwareAsyncCallback<Void>() {

            public void onSuccess(Void arg0) {
                if (bar != null && bar.isOutMost())
                    bar.closeOutTabPanel(item.getLabel() + " " + item.getIds()); //$NON-NLS-1$

                postDeleteAction.doAction();
            }
        });
    }

}
