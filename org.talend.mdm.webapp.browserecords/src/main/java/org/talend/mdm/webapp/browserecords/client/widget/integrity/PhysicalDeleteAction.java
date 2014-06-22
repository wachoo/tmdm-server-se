package org.talend.mdm.webapp.browserecords.client.widget.integrity;

import java.util.List;

import org.talend.mdm.webapp.base.client.SessionAwareAsyncCallback;
import org.talend.mdm.webapp.base.client.util.MultilanguageMessageParser;
import org.talend.mdm.webapp.browserecords.client.BrowseRecordsServiceAsync;
import org.talend.mdm.webapp.browserecords.client.i18n.BrowseRecordsMessages;
import org.talend.mdm.webapp.browserecords.client.i18n.MessagesFactory;
import org.talend.mdm.webapp.browserecords.client.model.ItemBean;
import org.talend.mdm.webapp.browserecords.client.util.Locale;

import com.extjs.gxt.ui.client.widget.MessageBox;

/**
 * Wraps a physical delete operation.
 */
// Implementation package visibility for class is intended: no need to see this class outside of package
public class PhysicalDeleteAction implements DeleteAction {

    public void delete(final List<ItemBean> items, BrowseRecordsServiceAsync service, boolean override,
            final PostDeleteAction postDeleteAction) {
        final BrowseRecordsMessages message = MessagesFactory.getMessages();
        final MessageBox progressBar = MessageBox.wait(message.delete_item_title(), null, message.delete_item_progress());
        service.deleteItemBeans(items, override, Locale.getLanguage(), new SessionAwareAsyncCallback<List<String>>() {

            public void onSuccess(List<String> msgs) {
                progressBar.close();
                if (msgs != null) {
                    StringBuffer sb = new StringBuffer();
                    for (String msg : msgs) {
                        sb.append(MultilanguageMessageParser.pickOutISOMessage(msg) + "\n"); //$NON-NLS-1$
                    }
                    String msg = sb.toString().replaceAll("\\s", ""); //$NON-NLS-1$ //$NON-NLS-2$
                    if (msg.length() > 0) {
                        MessageBox.info(message.info_title(), msg, null);
                    }
                }
                postDeleteAction.doAction();
            }
            @Override
            protected void doOnFailure(Throwable caught) {
            	progressBar.close();
            	super.doOnFailure(caught);
            }

        });
    }
}
