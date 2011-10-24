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
import java.util.List;

import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.TabItem;
import com.extjs.gxt.ui.client.widget.TabPanel;
import com.extjs.gxt.ui.client.widget.Text;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * DOC Administrator class global comment. Detailled comment
 */
public class ItemsDetailPanel extends ContentPanel {

    public final static String SINGLETON = "SINGLETON"; //$NON-NLS-1$

    public final static String MULTIPLE = "MULTIPLE"; //$NON-NLS-1$

    private TabPanel tabPanel = new TabPanel();

    private SimplePanel breadCrumb = new SimplePanel();

    private LayoutContainer banner = new LayoutContainer();

    private Text textTitle = new Text();

    private List<Text> subTitleList = new ArrayList<Text>();

    public ItemsDetailPanel() {
        super();
        this.setBodyBorder(false);
        this.setHeaderVisible(false);
        this.setLayout(new RowLayout(Orientation.VERTICAL));
        this.initPanel();
    }

    private void initPanel() {
        add(breadCrumb, new RowData(1, -1));
        // banner.setHeaderVisible(false);
        banner.setHeight("56px"); //$NON-NLS-1$
        // banner.setBodyBorder(false);
        banner.setScrollMode(Scroll.AUTO);
        banner.setStyleName("banner"); //$NON-NLS-1$ 

        textTitle.setStyleName("Title"); //$NON-NLS-1$
        add(banner, new RowData(1, -1));
        tabPanel.setTabScroll(true);
        add(tabPanel, new RowData(1, 1));
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
        clearBanner();
        if (xpathList != null && xpathList.size() > 0) {
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
        TabItem newTab = tabPanel.getItemByItemId(id);
        if (pattern.equalsIgnoreCase(ItemsDetailPanel.MULTIPLE)) {
            if (newTab != null)
                tabPanel.remove(newTab);

            newTab = new TabItem(title);
            newTab.setId(id);
            newTab.setLayout(new FitLayout());
            newTab.add(panel);
            tabPanel.add(newTab);
            if (tabPanel.getItemCount() == 1)
                tabPanel.setSelection(newTab);
        } else {
            if (newTab == null) {
                newTab = new TabItem(title);
                newTab.setLayout(new FitLayout());
                newTab.setId(id);
                newTab.setItemId(id);
                newTab.setLayout(new FitLayout());
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
