package org.talend.mdm.webapp.browserecords.client.widget.integrity;

import org.talend.mdm.webapp.browserecords.client.widget.ItemsDetailPanel;
import org.talend.mdm.webapp.browserecords.client.widget.ItemsListPanel;
import org.talend.mdm.webapp.browserecords.client.widget.ItemsSearchContainer;

/**
*
*/
public class ContainerUpdate implements PostDeleteAction {

    private final PostDeleteAction next;

    public ContainerUpdate(PostDeleteAction next) {
        this.next = next;
    }

    public void doAction() {
        ItemsListPanel listPanel = ItemsListPanel.getInstance();
        listPanel.refreshGrid();
        ItemsDetailPanel.getInstance().closeCurrentTab();

        next.doAction();
    }
}
