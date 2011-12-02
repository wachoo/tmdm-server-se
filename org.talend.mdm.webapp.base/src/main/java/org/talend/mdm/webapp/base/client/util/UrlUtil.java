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
package org.talend.mdm.webapp.base.client.util;

public class UrlUtil {

    public static String getLanguage() {
        return getCurrentLanguage();
    }
    
    private static native String getCurrentLanguage() /*-{
        return $wnd.language;
    }-*/;
        
    public static native String getLocaleProperty() /*-{
        var metaArray = $doc.getElementsByTagName("meta");
        for (var i = 0; i < metaArray.length; i++) {
            if (metaArray[i].getAttribute("name") == "gwt:property") {
                var content = metaArray[i].getAttribute("content");
                var contentArray = content.split("=");
                if (contentArray[0] == "locale") {
                    var localeArray = contentArray[1].split("_");
                    return localeArray[0];
                }
            }
        }
        return "en";
    }-*/;
}
