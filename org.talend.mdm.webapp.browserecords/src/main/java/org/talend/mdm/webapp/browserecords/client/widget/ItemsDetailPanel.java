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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.TabItem;
import com.extjs.gxt.ui.client.widget.TabPanel;
import com.extjs.gxt.ui.client.widget.Text;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * DOC Administrator class global comment. Detailled comment
 */
public class ItemsDetailPanel extends ContentPanel {

    private static Map<String, ItemsDetailPanel> instances = new HashMap<String, ItemsDetailPanel>();

    public final static String SINGLETON = "SINGLETON"; //$NON-NLS-1$

    public final static String MULTIPLE = "MULTIPLE"; //$NON-NLS-1$

    private TabPanel tabPanel = new TabPanel();

    private SimplePanel breadCrumb = new SimplePanel();

    private ContentPanel banner = new ContentPanel();

    private Text textTitle = new Text();

    private List<Text> subTitleList = new ArrayList<Text>();

    public static ItemsDetailPanel getInstance() {
        String modelName = GWT.getModuleName();
        ItemsDetailPanel instance = instances.get(modelName);
        if (instance == null) {
            instance = new ItemsDetailPanel();
            instances.put(modelName, instance);
        }
        Window.alert(modelName);
        return instance;
    }

    protected void onDetach() {
        super.onDetach();
        instances.remove(GWT.getModuleName());
    }

    private ItemsDetailPanel() {
        super();
        this.setBodyBorder(false);
        this.setHeaderVisible(false);
        this.setLayout(new RowLayout(Orientation.VERTICAL));
        this.setHeight(500);
        this.initPanel();
    }

    private void initPanel() {
        add(breadCrumb);
        banner.setHeaderVisible(false);
        banner.setHeight("56px"); //$NON-NLS-1$
        banner.setBodyBorder(false);
        banner.setScrollMode(Scroll.AUTO);

        textTitle.setStyleName("Title"); //$NON-NLS-1$
        add(banner);
        tabPanel.setTabScroll(true);
        tabPanel.addListener(Events.Resize, new Listener<BaseEvent>() {

            public void handleEvent(BaseEvent be) {
                tabPanel.setHeight(ItemsDetailPanel.this.getOffsetHeight() - 50);
            }
        });
        add(tabPanel);
    }

    public void initBreadCrumb(BreadCrumb breadCrumb) {
        this.breadCrumb.setWidget(breadCrumb);
    }

    public void clearBreadCrumb() {
        this.breadCrumb.clear();
    }

    public void appendBreadCrumb(String concept, String ids) {
        if (this.breadCrumb.getWidget() instanceof BreadCrumb) {
            BreadCrumb curBC = (BreadCrumb) this.breadCrumb.getWidget();
            curBC.appendBreadCrumb(concept, ids);
        }
    }

    public void initBanner(List<String> xpathList, String desc) {
        if (xpathList != null && xpathList.size() > 0) {
            clearBanner();
            banner.getBody().setStyleName("banner"); //$NON-NLS-1$
            int i = 1;
            for (String str : xpathList) {
                if (i == 1) {
                    textTitle.setText(str);
                    banner.add(textTitle);
                    i++;
                    continue;
                }
                Text subTitle = new Text();
                subTitle.setStyleName("Description"); //$NON-NLS-1$
                subTitle.setText(str);
                subTitleList.add(subTitle);
                banner.add(subTitle);
            }
            banner.layout(true);
        }
        if (desc != null && !desc.equals("")) //$NON-NLS-1$
            banner.setToolTip(desc);
    }

    public void clearBanner() {
        textTitle.setText(null);
        for (Text text : subTitleList) {
            banner.remove(text);
        }
        subTitleList.clear();
        banner.layout(true);
    }

    public void clearAll() {
        clearBanner();
        clearBreadCrumb();
        clearContent();
    }

    public TabItem addTabItem(String title, ContentPanel panel, String pattern, String id) {
        TabItem newTab = null;
        if (pattern.equalsIgnoreCase(ItemsDetailPanel.MULTIPLE)) {
            newTab = new TabItem(title);
            newTab.setId(id);
            newTab.addStyleName("pad-text"); //$NON-NLS-1$
            panel.setHeight(this.getHeight() - 100);
            newTab.add(panel);
            tabPanel.add(newTab);
            if (tabPanel.getItemCount() == 1)
                tabPanel.setSelection(newTab);
        } else {
            newTab = tabPanel.getItemByItemId(id);
            if (newTab == null) {
                newTab = new TabItem(title);
                newTab.setId(id);
                newTab.setItemId(id);
                newTab.addStyleName("pad-text"); //$NON-NLS-1$
                panel.setHeight(this.getHeight() - 100);
                newTab.add(panel);
                tabPanel.add(newTab);
                tabPanel.setSelection(newTab);
            } else {
                newTab.removeAll();
                newTab.add(panel);
                newTab.layout(true);
            }
        }
        return newTab;
    }

    public ItemPanel getCurrentItemPanel() {
        TabItem tabItem = tabPanel.getSelectedItem();
        if (tabItem != null) {
            Widget w = tabItem.getWidget(0);
            if (w instanceof ItemPanel) {
                return (ItemPanel) w;
            }
        }
        return null;
    }

    public void clearContent() {
        tabPanel.removeAll();
    }

    public void clearChildrenContent() {
        List<TabItem> items = tabPanel.getItems();
        for (int i = 0; i < items.size(); i++) {
            if (i > 0)
                tabPanel.remove(items.get(i));
        }
    }

    public TabItem getTabPanelById(String itemId) {
        return tabPanel.getItemByItemId(itemId);
    }

    public void closeCurrentTab() {
        TabItem itemTab = tabPanel.getSelectedItem();
        tabPanel.remove(itemTab);
    }

    public TabPanel getTabPanel() {
        return tabPanel;
    }

    public void showBannerVisible(boolean visible) {
        banner.setVisible(visible);
    }
}
