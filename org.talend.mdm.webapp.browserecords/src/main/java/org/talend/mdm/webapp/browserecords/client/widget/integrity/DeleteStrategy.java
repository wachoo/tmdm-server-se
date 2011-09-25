package org.talend.mdm.webapp.browserecords.client.widget.integrity;

import org.talend.mdm.webapp.browserecords.client.model.ItemBean;
import org.talend.mdm.webapp.browserecords.shared.FKIntegrityResult;

import java.util.Map;

/**
 *
 */
public interface DeleteStrategy {
    void delete(Map<ItemBean, FKIntegrityResult> items, DeleteAction action);
}
