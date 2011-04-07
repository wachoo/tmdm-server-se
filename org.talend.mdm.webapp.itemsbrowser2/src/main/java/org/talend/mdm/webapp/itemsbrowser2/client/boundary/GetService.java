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
        $wnd.parent.amalto.itemsbrowser2.ItemsBrowser2.openItemBrowser(ids, conceptName, function(){
            @org.talend.mdm.webapp.itemsbrowser2.client.boundary.GetService::refresh(ZLjava/lang/String;)(true, ids);
        });
    }-*/;

    static void refresh(boolean refreshItemForm,String ids){
        ItemsSearchContainer itemsSearchContainer = Registry.get(ItemsView.ITEMS_SEARCH_CONTAINER);
        itemsSearchContainer.getItemsListPanel().refresh(ids, refreshItemForm);
    }
    
    static void setEnable(boolean enabled){
        ItemsSearchContainer itemsSearchContainer = Registry.get(ItemsView.ITEMS_SEARCH_CONTAINER);
        itemsSearchContainer.getItemsListPanel().getGrid().setEnabled(enabled);
    }
    
    public static native void renderFormWindow(String ids, String concept, boolean isDuplicate,
            Element formWindow, boolean isDetail, boolean refreshItemForm) /*-{
        var refreshCB = function(){
            @org.talend.mdm.webapp.itemsbrowser2.client.boundary.GetService::refresh(ZLjava/lang/String;)(refreshItemForm, ids);
        };
        var rendered = function(){
            @org.talend.mdm.webapp.itemsbrowser2.client.boundary.GetService::setEnable(Z)(true);
        };
        @org.talend.mdm.webapp.itemsbrowser2.client.boundary.GetService::setEnable(Z)(false);
        $wnd.parent.amalto.itemsbrowser2.ItemsBrowser2.renderFormWindow(ids, concept, isDuplicate, refreshCB, formWindow, isDetail, rendered);
    }-*/;
}
