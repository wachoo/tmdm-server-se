package org.talend.mdm.webapp.browserecords.client.widget.integrity;

import org.talend.mdm.webapp.browserecords.client.widget.ItemsListPanel;

/**
 *
 */
public class ListRefresh implements PostDeleteAction {

    private final ItemsListPanel list;

    private final PostDeleteAction next;

    public ListRefresh(ItemsListPanel list, PostDeleteAction next) {
        this.list = list;
        this.next = next;
    }

    public void doAction() {
        // Reload
        list.getStore().getLoader().load();
        next.doAction();
    }
}
