package org.talend.mdm.webapp.browserecords.client.widget;

import org.talend.mdm.webapp.browserecords.client.BrowseRecords;
import org.talend.mdm.webapp.browserecords.client.BrowseRecordsEvents;
import org.talend.mdm.webapp.browserecords.client.BrowseRecordsServiceAsync;
import org.talend.mdm.webapp.browserecords.client.model.ItemBean;
import org.talend.mdm.webapp.browserecords.client.util.UserSession;
import org.talend.mdm.webapp.browserecords.client.widget.treedetail.TreeDetail;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.mvc.AppEvent;
import com.extjs.gxt.ui.client.widget.ContentPanel;

public class ItemPanel extends ContentPanel {

    private final TreeDetail tree = new TreeDetail();

    private ItemDetailToolBar toolBar;

    private ItemBean item;

    private String operation;

    public ItemPanel() {

    }

    public ItemPanel(ItemBean item, String operation) {

        this.item = item;
        this.toolBar = new ItemDetailToolBar(item, operation);
        this.operation = operation;
        this.initUI();
    }

    @SuppressWarnings("static-access")
    private void initUI() {
        this.setBodyBorder(false);
        this.setHeaderVisible(false);
        this.setTopComponent(toolBar);
        if (ItemDetailToolBar.CREATE_OPERATION.equals(operation)) {
            tree.initTree(null);
        } else if (ItemDetailToolBar.VIEW_OPERATION.equals(operation)) {
            tree.initTree(item);
        } else if (ItemDetailToolBar.DUPLICATE_OPERATION.equals(operation)) {
            tree.initTree(item);
        } else {
            tree.initTree(null);
        }

        tree.expand();
        this.add(tree);
        // this.setBottomComponent(new PagingToolBarEx(50));
    }

    private static BrowseRecordsServiceAsync getItemService() {

        BrowseRecordsServiceAsync service = (BrowseRecordsServiceAsync) Registry.get(BrowseRecords.BROWSERECORDS_SERVICE);
        return service;

    }

    private static UserSession getSession() {
        return Registry.get(BrowseRecords.USER_SESSION);

    }

    public void handleEvent(AppEvent event) {
        if (event.getType() == BrowseRecordsEvents.UpdatePolymorphism) {
            tree.handleEvent(event);
        }
    }

    public ItemBean getItem() {
        return item;
    }

    public TreeDetail getTree() {
        return tree;
    }

    public String getOperation() {
        return operation;
    }

}
