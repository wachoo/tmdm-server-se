// ============================================================================
//
// Copyright (C) 2006-2012 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.mdm.webapp.browserecords.client.widget;

import java.util.HashMap;
import java.util.Map;

import org.talend.mdm.webapp.browserecords.client.i18n.MessagesFactory;
import org.talend.mdm.webapp.browserecords.client.model.ItemNodeModel;
import org.talend.mdm.webapp.browserecords.client.mvc.BrowseRecordsView;
import org.talend.mdm.webapp.browserecords.client.widget.treedetail.TreeDetailUtil;

import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.event.TabPanelEvent;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.TabItem;
import com.extjs.gxt.ui.client.widget.TabPanel;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.core.client.GWT;


/**
 * DOC Administrator  class global comment. Detailled comment
 */
public class ItemsMainTabPanel extends TabPanel {

    private static Map<String, ItemsMainTabPanel> instances = new HashMap<String, ItemsMainTabPanel>();

    private ItemsMainTabPanel() {
        // this.setLayout(new FitLayout());
        setResizeTabs(true);
        setAnimScroll(true);
        this.addListener(Events.BeforeAdd, new Listener<BaseEvent>() {

            public void handleEvent(BaseEvent be) {
                if (!ItemsMainTabPanel.this.isVisible()) {
                    ItemsMainTabPanel.this.setVisible(true);
                }
            }
        });

        this.addListener(Events.Remove, new Listener<BaseEvent>() {

            public void handleEvent(BaseEvent be) {
                if (ItemsMainTabPanel.this.getItemCount() == 0) {
                    ItemsMainTabPanel.this.setVisible(false);
                }
            }
        });
    }

    public static ItemsMainTabPanel getInstance() {
        String modelName = GWT.getModuleName();
        ItemsMainTabPanel instance = instances.get(modelName);
        if (instance == null) {
            instance = new ItemsMainTabPanel();
            instances.put(modelName, instance);
        }
        return instance;
    }

    protected void onAttach() {
        super.onAttach();
        this.setVisible(this.getItemCount() > 0);
    }

    protected void onDetach() {
        super.onDetach();
        instances.remove(GWT.getModuleName());
    }

    public TabItem addMainTabItem(String title, ContentPanel panel, String id) {
        if (this.getItemByItemId(id) != null)
            this.remove(this.getItemByItemId(id));

        TabItem item = new TabItem(title);
        item.setId(id);
        item.setLayout(new FitLayout());
        item.add(panel);
        item.setClosable(true);
        this.add(item);
        this.setSelection(item);
        return item;
    }

    public ItemsDetailPanel getDefaultViewTabItem() {
        if (this.getItemByItemId(BrowseRecordsView.DEFAULT_ITEMVIEW) != null)
            return (ItemsDetailPanel) this.getItemByItemId(BrowseRecordsView.DEFAULT_ITEMVIEW).getWidget(0);
        return null;
    }

    /**
     * Get current ItemsDetailPanel.
     * 
     * @return current ItemsDetailPanel.
     * @deprecated This method is deprecated because the singleton pattern should not be used in detailed panel component.
     */    
    @Deprecated
    public ItemsDetailPanel getCurrentViewTabItem(){
        if (this.getSelectedItem().getWidget(0) instanceof ItemsDetailPanel)
            return (ItemsDetailPanel) this.getSelectedItem().getWidget(0);
        return null;
    }
    
    public boolean insert(TabItem item, int index) {
        addItemListener(item);
        return super.insert(item, index);
    }

    public boolean add(TabItem item) {
        addItemListener(item);
        return super.add(item);
    }

    private boolean isConfirmedTabClose = false;
    private void addItemListener(final TabItem item) {
        item.addListener(Events.BeforeClose, new Listener<TabPanelEvent>() {

            public void handleEvent(TabPanelEvent be) {
                if (isConfirmedTabClose)
                    isConfirmedTabClose = false;
                else {
                    be.setCancelled(true);
                    closeTabItem(item);
                }
            }
        });
    }


    public void closeTabItem(final TabItem item) {
        ItemsDetailPanel itemsDetailPanel = (ItemsDetailPanel) item.getWidget(0);
        boolean isChangeCurrentRecord;
        if (itemsDetailPanel != null && itemsDetailPanel.getCurrentItemPanel() != null) {
            ItemPanel itemPanel = itemsDetailPanel.getCurrentItemPanel();
            if(itemPanel.getOperation().equals(ItemDetailToolBar.VIEW_OPERATION)){
                final ItemDetailToolBar toolBar = itemPanel.getToolBar();
                ItemNodeModel root = (ItemNodeModel) itemPanel.getTree().getTree().getItem(0).getUserObject();
                isChangeCurrentRecord = root != null ? TreeDetailUtil.isChangeValue(root) : false;
                if (isChangeCurrentRecord) {
                    MessageBox msgBox = MessageBox.confirm(MessagesFactory.getMessages().confirm_title(), MessagesFactory
                            .getMessages().msg_confirm_save_tree_detail(root.getLabel()), new Listener<MessageBoxEvent>() {

                        public void handleEvent(MessageBoxEvent be) {
                            if (Dialog.YES.equals(be.getButtonClicked().getItemId())) {
                                if (!toolBar.isHierarchyCall() && !toolBar.isFkToolBar()) {
                                    ItemsListPanel.getInstance().setChangedRecordId(toolBar.getItemBean().getIds());
                                    ItemsListPanel.getInstance().setSaveCurrentChangeBeforeSwitching(true);
                                }
                                toolBar.saveItemAndClose(true);

                            }
                            isConfirmedTabClose = true;
                            item.close();
                        }
                    });
                    msgBox.getDialog().setWidth(550);
                } else {
                    isConfirmedTabClose = true;
                    item.close();
                }
                if (!toolBar.isHierarchyCall() && !toolBar.isFkToolBar()) {
                    ItemsListPanel.getInstance().deSelectCurrentItem();
                }
            } else {
                isConfirmedTabClose = true;
                item.removeAllListeners();
                item.close();
                isConfirmedTabClose = false;
            }
        }
    }
}
