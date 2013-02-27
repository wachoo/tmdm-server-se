package org.talend.mdm.webapp.browserecords.client.widget.integrity;

import java.util.List;

import org.talend.mdm.webapp.base.client.SessionAwareAsyncCallback;
import org.talend.mdm.webapp.base.client.i18n.BaseMessagesFactory;
import org.talend.mdm.webapp.base.client.util.MultilanguageMessageParser;
import org.talend.mdm.webapp.base.client.widget.CallbackAction;
import org.talend.mdm.webapp.browserecords.client.BrowseRecordsServiceAsync;
import org.talend.mdm.webapp.browserecords.client.i18n.BrowseRecordsMessages;
import org.talend.mdm.webapp.browserecords.client.i18n.MessagesFactory;
import org.talend.mdm.webapp.browserecords.client.model.ItemBean;
import org.talend.mdm.webapp.browserecords.client.util.Locale;

import com.allen_sauer.gwt.log.client.Log;
import com.extjs.gxt.ui.client.widget.MessageBox;

/**
 * Wraps a physical delete operation.
 */
public class PhysicalDeleteAction implements DeleteAction {

    public void delete(final List<ItemBean> items, BrowseRecordsServiceAsync service, boolean override,
            final PostDeleteAction postDeleteAction) {
        final BrowseRecordsMessages message = MessagesFactory.getMessages();
        final MessageBox progressBar = MessageBox.wait(message.delete_item_title(), null, message.delete_item_progress());
        service.deleteItemBeans(items, override, Locale.getLanguage(), new SessionAwareAsyncCallback<List<String>>() {

            public void onSuccess(List<String> msgs) {
                progressBar.close();
                if (msgs != null) {
                    if(msgs.size() > 0) {
                        StringBuilder sb = new StringBuilder(MultilanguageMessageParser.pickOutISOMessage(msgs.get(0)));
                        for (int i = 1; i < msgs.size(); i++) {
                            String str = MultilanguageMessageParser.pickOutISOMessage(msgs.get(i));
                            if(str != null && str.trim().length() > 0) {
                                sb.append("<br/>").append(str); //$NON-NLS-1$
                            }
                        }
                        String msg = sb.toString().trim();
                        if (msg.length() > 0) {
                            MessageBox.info(message.info_title(), msg, null);
                        }
                    }
                }
                postDeleteAction.doAction();
                CallbackAction.getInstance().doAction(CallbackAction.HIERARCHY_DELETEITEM_CALLBACK, null,false);
            }

            @Override
            protected void doOnFailure(Throwable caught) {
                progressBar.close();
                String errorMsg = caught.getLocalizedMessage();
                if (errorMsg == null) {
                    if (Log.isDebugEnabled())
                        errorMsg = caught.toString(); // for debugging purpose
                    else
                        errorMsg = BaseMessagesFactory.getMessages().unknown_error();
                }
                MessageBox.alert(message.error_title(),
                        MultilanguageMessageParser.pickOutISOMessage(errorMsg), null);
            }
        });
    }
}
