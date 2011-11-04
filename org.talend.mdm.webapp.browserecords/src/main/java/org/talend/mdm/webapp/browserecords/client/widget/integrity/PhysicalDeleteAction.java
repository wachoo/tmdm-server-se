package org.talend.mdm.webapp.browserecords.client.widget.integrity;

import org.talend.mdm.webapp.base.client.SessionAwareAsyncCallback;
import org.talend.mdm.webapp.browserecords.client.BrowseRecordsServiceAsync;
import org.talend.mdm.webapp.browserecords.client.i18n.MessagesFactory;
import org.talend.mdm.webapp.browserecords.client.model.ItemBean;
import org.talend.mdm.webapp.browserecords.client.util.Locale;
import org.talend.mdm.webapp.browserecords.client.widget.ItemDetailToolBar;

import com.extjs.gxt.ui.client.widget.MessageBox;

/**
 * Wraps a physical delete operation.
 */
// Implementation package visibility for class is intended: no need to see this class outside of package
public class PhysicalDeleteAction implements DeleteAction {
    
    private ItemDetailToolBar bar;

    /***
     * 
     * DOC Administrator Comment method "setToolBar".
     * 
     * @param bar The render toolbar(record could be opened from many places)
     */
    public void setToolBar(ItemDetailToolBar bar) {
        this.bar = bar;
    }

    public void delete(final ItemBean item, BrowseRecordsServiceAsync service, boolean override,
            final PostDeleteAction postDeleteAction) {
        service.deleteItemBean(item, override, Locale.getLanguage(), new SessionAwareAsyncCallback<String>() {

            public void onSuccess(String msg) {
                if (msg != null && !msg.equals("")) { //$NON-NLS-1$
                    MessageBox.info(MessagesFactory.getMessages().info_title(), msg, null);
                }
                if (bar != null && bar.isOutMost())
                    bar.closeOutTabPanel(item.getLabel() + " " + item.getIds()); //$NON-NLS-1$

                postDeleteAction.doAction();
            }
        });
    }
}
