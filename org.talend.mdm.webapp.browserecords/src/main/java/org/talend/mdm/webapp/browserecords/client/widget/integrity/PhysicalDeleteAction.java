package org.talend.mdm.webapp.browserecords.client.widget.integrity;

import org.talend.mdm.webapp.base.client.SessionAwareAsyncCallback;
import org.talend.mdm.webapp.base.client.i18n.BaseMessagesFactory;
import org.talend.mdm.webapp.browserecords.client.BrowseRecordsServiceAsync;
import org.talend.mdm.webapp.browserecords.client.i18n.MessagesFactory;
import org.talend.mdm.webapp.browserecords.client.model.ItemBean;
import org.talend.mdm.webapp.browserecords.client.util.CommonUtil;
import org.talend.mdm.webapp.browserecords.client.util.Locale;

import com.allen_sauer.gwt.log.client.Log;
import com.extjs.gxt.ui.client.widget.MessageBox;

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

            @Override
            protected void doOnFailure(Throwable caught) {
                String errorMsg = caught.getLocalizedMessage();
                if (errorMsg == null) {
                    if (Log.isDebugEnabled())
                        errorMsg = caught.toString(); // for debugging purpose
                    else
                        errorMsg = BaseMessagesFactory.getMessages().unknown_error();
                }
                MessageBox.alert(BaseMessagesFactory.getMessages().error_title(), CommonUtil.pickOutISOMessage(errorMsg), null);
            }
        });
    }
}
