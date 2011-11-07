package org.talend.mdm.webapp.browserecords.client.widget.integrity;

import com.extjs.gxt.ui.client.widget.MessageBox;
import org.talend.mdm.webapp.base.client.SessionAwareAsyncCallback;
import org.talend.mdm.webapp.browserecords.client.BrowseRecordsServiceAsync;
import org.talend.mdm.webapp.browserecords.client.i18n.MessagesFactory;
import org.talend.mdm.webapp.browserecords.client.model.ItemBean;
import org.talend.mdm.webapp.browserecords.client.util.Locale;

/**
 * Wraps a physical delete operation.
 */
// Implementation package visibility for class is intended: no need to see this class outside of package
public class PhysicalDeleteAction implements DeleteAction {

    public void delete(final ItemBean item, BrowseRecordsServiceAsync service, boolean override,
            final PostDeleteAction postDeleteAction) {
        service.deleteItemBean(item, override, Locale.getLanguage(), new SessionAwareAsyncCallback<String>() {

            public void onSuccess(String msg) {
                if (msg != null && !msg.equals("")) { //$NON-NLS-1$
                    MessageBox.info(MessagesFactory.getMessages().info_title(), msg, null);
                }
                postDeleteAction.doAction();
            }
        });
    }
}
