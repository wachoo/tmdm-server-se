package org.talend.mdm.webapp.browserecords.client.widget.integrity;

import org.talend.mdm.webapp.browserecords.client.widget.ItemsDetailPanel;
import org.talend.mdm.webapp.browserecords.client.widget.ItemsListPanel;
import org.talend.mdm.webapp.browserecords.client.widget.ItemsSearchContainer;

/**
 * An implementation of {@link PostDeleteAction} that performs item browser container operations (such as closing tabs
 * if needed...).
 */
public class ContainerUpdate implements PostDeleteAction {

    private final PostDeleteAction next;

    /**
     * @param next If you don't know what to pass as <code>next</code> argument, check the
     * constant {@link NoOpPostDeleteAction#INSTANCE}.
     */
    public ContainerUpdate(PostDeleteAction next) {
        this.next = next;
    }

    public void doAction() {
        ItemsListPanel.getInstance().refreshGrid();
        // After item has been deleted, close its view tab.
        ItemsDetailPanel.getInstance().closeCurrentTab();

        next.doAction();
    }
}
