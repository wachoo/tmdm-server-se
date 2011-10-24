// ============================================================================
//
// Copyright (C) 2006-2011 Talend Inc. - www.talend.com
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

import org.talend.mdm.webapp.browserecords.client.mvc.BrowseRecordsView;

import com.extjs.gxt.ui.client.widget.ContentPanel;
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

    public ItemsDetailPanel getCurrentViewTabItem(){
        if (this.getSelectedItem().getWidget(0) instanceof ItemsDetailPanel)
            return (ItemsDetailPanel) this.getSelectedItem().getWidget(0);
        return null;
    }
}
