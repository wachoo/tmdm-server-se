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

import org.talend.mdm.webapp.itemsbrowser2.client.Itemsbrowser2;


/**
 * DOC HSHU class global comment. Detailled comment
 */
public class PubService {

    /**
     * DOC HSHU Comment method "displayItemsForm".
     */
    public static void renderUIImpl() {
        
        Itemsbrowser2.onModuleRender();

    }

    public static native void renderUI() /*-{
        
        $wnd.top.org_talend_mdm_webapp_itemsbrowser2_InBoundService_renderUI = function () { 
          @org.talend.mdm.webapp.itemsbrowser2.client.boundary.PubService::renderUIImpl()();
        };
         
    }-*/;
    
    public static native void regRefresh()/*-{
        $wnd.top.org_talend_mdm_webapp_itemsbrowser2_InBoundService_refreshGrid = function(){
            @org.talend.mdm.webapp.itemsbrowser2.client.boundary.GetService::refreshGrid()();
        };
    }-*/;
    
}
