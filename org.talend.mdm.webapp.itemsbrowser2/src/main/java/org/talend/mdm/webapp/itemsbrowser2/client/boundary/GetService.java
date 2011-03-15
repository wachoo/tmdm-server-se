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

import com.google.gwt.user.client.Element;

/**
 * DOC HSHU class global comment. Detailled comment
 */
public class GetService {

    /* Get languaue from outer, this is an example */
    public static native String getLanguage() /*-{
        return $wnd.parent.amalto.itemsbrowser2.ItemsBrowser2.getLanguage();
    }-*/;

    public static native void openItemBrowser(String ids, String conceptName) /*-{
        var idsArray = ids.split(".");
        $wnd.parent.amalto.itemsbrowser2.ItemsBrowser2.openItemBrowser(idsArray, conceptName);
    }-*/;

}
