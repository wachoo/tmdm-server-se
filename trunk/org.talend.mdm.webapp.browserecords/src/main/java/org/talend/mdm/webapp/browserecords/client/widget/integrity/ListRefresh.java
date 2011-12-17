package org.talend.mdm.webapp.browserecords.client.widget.integrity;

import org.talend.mdm.webapp.browserecords.client.widget.ItemsListPanel;

/**
 * This implementation of {@link PostDeleteAction} refreshes the content of the item browser list (can be used
 * to remove the freshly deleted items from the view).
 */
public class ListRefresh implements PostDeleteAction {

    private final PostDeleteAction next;

    /**
     * If you don't know what to pass as <code>next</code> argument, check the constant {@link NoOpPostDeleteAction#INSTANCE}.
     * @param next The next action to be called once this action has just been performed.
     */
    public ListRefresh(PostDeleteAction next) {
        this.next = next;
    }

    public void doAction() {
        // Reload
        ItemsListPanel.getInstance().getStore().getLoader().load();
        next.doAction();
    }
}
