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

import java.util.List;

import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.TabItem;
import com.extjs.gxt.ui.client.widget.TabPanel;
import com.extjs.gxt.ui.client.widget.Text;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * DOC Administrator  class global comment. Detailled comment
 */
public class ItemsDetailPanel extends ContentPanel {

    private static ItemsDetailPanel instance;
    public final static String SINGLETON = "SINGLETON"; //$NON-NLS-1$
    public final static String MULTIPLE = "MULTIPLE"; //$NON-NLS-1$
    private TabPanel tabPanel = new TabPanel();  

    private SimplePanel breadCrumb = new SimplePanel();
    private ContentPanel banner = new ContentPanel();
    Text textTitle = new Text();
    Text textDesc = new Text();
    
    public static ItemsDetailPanel getInstance() {
        if (instance == null) {
            instance = new ItemsDetailPanel();
        }
        return instance;
    }

    protected void onDetach() {
    	super.onDetach();
    	instance = null;
    }
    
    private ItemsDetailPanel() {
        super();
        this.setBodyBorder(false);
        this.setHeaderVisible(false);
        this.setLayout(new RowLayout(Orientation.VERTICAL));
        this.setWidth(800);
        this.setHeight(500);
        this.initPanel();
    }
    
    private void initPanel() {
        add(breadCrumb);
        // tabPanel.setWidth(450);
        banner.setHeaderVisible(false);
        banner.setHeight("60px"); //$NON-NLS-1$
        banner.setStyleName("banner"); //$NON-NLS-1$
//        banner.setBodyStyle("backgroundColor: #6888b7;"); //$NON-NLS-1$
        
        textTitle.setStyleName("Title"); //$NON-NLS-1$
        textDesc.setStyleName("Description"); //$NON-NLS-1$
        banner.add(textTitle);
        banner.add(textDesc);
        add(banner);
        
        tabPanel.setAutoHeight(true);
        add(tabPanel);        
    }

    public void initBreadCrumb(BreadCrumb breadCrumb) {
        this.breadCrumb.setWidget(breadCrumb);
    }

    public void clearBreadCrumb() {
        this.breadCrumb.clear();
    }

    public void initBanner(List<String> xpathList) {
        StringBuilder title = new StringBuilder();
        StringBuilder subTitle = new StringBuilder();
        
        if (xpathList.size() == 1) {
            title.append(xpathList.get(0));    
        }else{
            title.append(xpathList.get(0)).append("-").append(xpathList.get(1)); //$NON-NLS-1$
            if(xpathList.size() > 2){
                for(int i=2; i<xpathList.size(); i++){
                    subTitle.append(xpathList.get(i)).append("-"); //$NON-NLS-1$
                }          
            }
        }        
        
        textTitle.setText(title.toString());
        if(subTitle.length() > 0)
            textDesc.setText(subTitle.substring(0, subTitle.length() - 1));
        else
            textDesc.setText(""); //$NON-NLS-1$
    }

    public void clearBanner() {
        textTitle.setText(null);
        textDesc.setText(null);
    }

    public void clearAll() {
        clearBanner();
        clearBreadCrumb();
        clearContent();
    }

    public void addTabItem(String title, ContentPanel panel, String pattern, String id){
        if(pattern.equalsIgnoreCase(ItemsDetailPanel.MULTIPLE)){
            TabItem newTab = new TabItem(title);
            newTab.setId(id);
            newTab.setClosable(true);
            newTab.addStyleName("pad-text");   //$NON-NLS-1$
            newTab.add(panel);
            tabPanel.add(newTab);
            tabPanel.setSelection(newTab);
        }else{
            TabItem newTab = tabPanel.getItemByItemId(id);
            if(newTab == null){
                newTab = new TabItem(title); 
                newTab.setId(id);
                newTab.setItemId(id);
                newTab.setClosable(true);
                newTab.addStyleName("pad-text");   //$NON-NLS-1$
                newTab.add(panel);
                tabPanel.add(newTab);
                tabPanel.setSelection(newTab);
            }else{
                newTab.removeAll();
                newTab.add(panel);
                newTab.layout(true);
            }
        }
    }

    public ItemPanel getCurrentItemPanel(){
        TabItem tabItem = tabPanel.getSelectedItem();
        if (tabItem != null){
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

    public TabItem getTabPanelById(String itemId) {
        return tabPanel.getItemByItemId(itemId);
    }

    public void closeCurrentTab(){
        TabItem itemTab = tabPanel.getSelectedItem();
        tabPanel.remove(itemTab);
    }

    public TabPanel getTabPanel() {
        return tabPanel;
    }

}
