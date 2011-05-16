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
package org.talend.mdm.webapp.itemsbrowser2.client.boundary;

import org.talend.mdm.webapp.itemsbrowser2.client.ItemsView;
import org.talend.mdm.webapp.itemsbrowser2.client.widget.ItemsSearchContainer;

import com.extjs.gxt.ui.client.Registry;
import com.google.gwt.user.client.Element;

/**
 * DOC HSHU class global comment. Detailled comment
 */
public class GetService {

    public static void closeViewWindow() {
        ItemsView.window.close();
    }

    public static native void regCallback()/*-{
        $wnd.callGxt = function(){
        @org.talend.mdm.webapp.itemsbrowser2.client.boundary.GetService::closeViewWindow()();
        };
    }-*/;

    /* Get languaue from outer, this is an example */
    public static native String getLanguage() /*-{
        return $wnd.parent.amalto.itemsbrowser2.ItemsBrowser2.getLanguage();
    }-*/;

    public static native void openItemBrowser(String ids, String conceptName) /*-{        
        var refreshCB = function(operation){
            if ($doc.getElementById('talend_itemsbrowser2_ItemsBrowser2')==null)return;
            if (operation == "deleteItem"){
                @org.talend.mdm.webapp.itemsbrowser2.client.boundary.GetService::refreshGrid()();
            } else {
                @org.talend.mdm.webapp.itemsbrowser2.client.boundary.GetService::refresh(ZLjava/lang/String;)(true, ids);
            }
        };
        $wnd.parent.amalto.itemsbrowser2.ItemsBrowser2.openItemBrowser(ids, conceptName, refreshCB);
    }-*/;

    static void refresh(boolean refreshItemForm,String ids){
        ItemsSearchContainer itemsSearchContainer = Registry.get(ItemsView.ITEMS_SEARCH_CONTAINER);
        itemsSearchContainer.getItemsListPanel().refresh(ids, refreshItemForm);
    }
    
    static void setEnable(boolean enabled){
        ItemsSearchContainer itemsSearchContainer = Registry.get(ItemsView.ITEMS_SEARCH_CONTAINER);
        itemsSearchContainer.getItemsListPanel().setEnabledGridSearchButton(enabled);
        
    }
    
    static void refreshGrid(){
        ItemsSearchContainer itemsSearchContainer = Registry.get(ItemsView.ITEMS_SEARCH_CONTAINER);
        itemsSearchContainer.getItemsListPanel().refreshGrid();
    }
    
    public static native void renderFormWindow(String ids, String concept, boolean isDuplicate,
            Element formWindow, boolean isDetail, boolean refreshItemForm, boolean enableQuit) /*-{
                
        var handleCallback = {
            refreshRecord : function(){
                @org.talend.mdm.webapp.itemsbrowser2.client.boundary.GetService::refresh(ZLjava/lang/String;)(refreshItemForm, ids);
            },
            enableGrid : function(){
                @org.talend.mdm.webapp.itemsbrowser2.client.boundary.GetService::setEnable(Z)(true);
            },
            refreshGrid : function(){
                @org.talend.mdm.webapp.itemsbrowser2.client.boundary.GetService::refreshGrid()();
            }
        };
        
        @org.talend.mdm.webapp.itemsbrowser2.client.boundary.GetService::setEnable(Z)(false);
        $wnd.parent.amalto.itemsbrowser2.ItemsBrowser2.renderFormWindow(ids, concept, isDuplicate, handleCallback, formWindow, isDetail, enableQuit);
    }-*/;
}
