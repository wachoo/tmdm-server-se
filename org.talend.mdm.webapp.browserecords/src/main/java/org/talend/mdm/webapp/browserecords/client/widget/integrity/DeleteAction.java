package org.talend.mdm.webapp.browserecords.client.widget.integrity;

import org.talend.mdm.webapp.browserecords.client.BrowseRecordsServiceAsync;
import org.talend.mdm.webapp.browserecords.client.model.ItemBean;

/**
 *
 */
public interface DeleteAction {

    DeleteAction PHYSICAL = new PhysicalDeleteAction();

    void delete(ItemBean item, BrowseRecordsServiceAsync service, boolean override);
}

