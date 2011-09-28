package org.talend.mdm.webapp.browserecords.client.widget.integrity;

import com.extjs.gxt.ui.client.widget.MessageBox;
import org.talend.mdm.webapp.browserecords.client.BrowseRecordsServiceAsync;
import org.talend.mdm.webapp.browserecords.client.i18n.MessagesFactory;
import org.talend.mdm.webapp.browserecords.client.model.ItemBean;
import org.talend.mdm.webapp.browserecords.shared.FKIntegrityResult;

import java.util.Map;
import java.util.Set;

/**
 *
 */
class ListDeleteStrategy implements DeleteStrategy {

    private BrowseRecordsServiceAsync service;

    ListDeleteStrategy(BrowseRecordsServiceAsync service) {
        this.service = service;
    }

    public void delete(Map<ItemBean, FKIntegrityResult> items, DeleteAction action) {
        Set<Map.Entry<ItemBean, FKIntegrityResult>> itemsToDelete = items.entrySet();
        boolean hasMetForbiddenDeletes = false;
        for (Map.Entry<ItemBean, FKIntegrityResult> currentItem : itemsToDelete) {
            FKIntegrityResult integrityCheckResult = currentItem.getValue();
            switch (integrityCheckResult) {
                case FORBIDDEN_OVERRIDE_ALLOWED:
                case FORBIDDEN:
                    hasMetForbiddenDeletes = true;
                    break;
                case ALLOWED:
                    action.delete(currentItem.getKey(), service, false);
                    break;
            }
        }

        if (hasMetForbiddenDeletes) {
            MessageBox.info(MessagesFactory.getMessages().info_title(), MessagesFactory.getMessages()
                    .fk_integrity_list_partial_delete(), null);
        }
    }
}
