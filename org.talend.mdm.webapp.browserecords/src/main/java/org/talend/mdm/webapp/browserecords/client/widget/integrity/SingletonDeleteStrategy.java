package org.talend.mdm.webapp.browserecords.client.widget.integrity;

import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.google.gwt.user.client.rpc.AsyncCallback;
import org.talend.mdm.webapp.browserecords.client.BrowseRecordsEvents;
import org.talend.mdm.webapp.browserecords.client.BrowseRecordsServiceAsync;
import org.talend.mdm.webapp.browserecords.client.i18n.MessagesFactory;
import org.talend.mdm.webapp.browserecords.client.model.ItemBean;
import org.talend.mdm.webapp.browserecords.shared.FKIntegrityResult;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 */
class SingletonDeleteStrategy implements DeleteStrategy {

    private BrowseRecordsServiceAsync service;

    /**
     * @param service Need a {@link BrowseRecordsServiceAsync} instance to display 'Relations' window if needed.
     */
    SingletonDeleteStrategy(BrowseRecordsServiceAsync service) {
        this.service = service;
    }

    public void delete(final Map<ItemBean, FKIntegrityResult> items, final DeleteAction action) {
        Set<Map.Entry<ItemBean, FKIntegrityResult>> entries = items.entrySet();
        Iterator<Map.Entry<ItemBean, FKIntegrityResult>> iterator = entries.iterator();

        Map.Entry<ItemBean, FKIntegrityResult> entry = iterator.next();
        if (iterator.hasNext()) {
            throw new IllegalArgumentException("Expected only one argument for delete.");
        }

        FKIntegrityResult result = entry.getValue();
        final ItemBean item = entry.getKey();

        switch (result) {
            case FORBIDDEN_OVERRIDE_ALLOWED:
                MessageBox.confirm(MessagesFactory.getMessages().error_title(),
                        MessagesFactory.getMessages().fk_integrity_fail_override(),
                        new Listener<MessageBoxEvent>() {
                            public void handleEvent(MessageBoxEvent be) {
                                if (Dialog.YES.equals(be.getButtonClicked().getItemId())) {
                                    action.delete(item, service, true);
                                }
                            }
                        });
                break;
            case FORBIDDEN:
                MessageBox.confirm(MessagesFactory.getMessages().error_title(),
                        MessagesFactory.getMessages().fk_integrity_fail_open_relations(),
                        new Listener<MessageBoxEvent>() {

                            public void handleEvent(MessageBoxEvent be) {
                                if (Dialog.YES.equals(be.getButtonClicked().getItemId())) {
                                    service.getLineageEntity(item.getConcept(), new AsyncCallback<List<String>>() {
                                        public void onSuccess(List<String> list) {
                                            StringBuilder entityStr = new StringBuilder();
                                            if (list != null) {
                                                for (String str : list)
                                                    entityStr.append(str).append(","); //$NON-NLS-1$
                                                String arrStr = entityStr.toString().substring(0, entityStr.length() - 1);
                                                String ids = item.getIds();
                                                if (ids == null || "".equals(ids.trim()))
                                                    ids = "";
                                                initSearchEntityPanel(arrStr, ids, item.getConcept());
                                            }
                                        }

                                        public void onFailure(Throwable caught) {
                                            Dispatcher.forwardEvent(BrowseRecordsEvents.Error, caught);
                                        }
                                    });

                                }
                            }
                        });
                break;
            case ALLOWED:
                action.delete(item, service, false);
                break;
        }

    }

    private native boolean initSearchEntityPanel(String arrStr, String ids, String dataObject)/*-{
        var lineageEntities = arrStr.split(",");
        $wnd.amalto.itemsbrowser.ItemsBrowser.lineageItem(lineageEntities, ids, dataObject);
        return true;
    }-*/;
}
