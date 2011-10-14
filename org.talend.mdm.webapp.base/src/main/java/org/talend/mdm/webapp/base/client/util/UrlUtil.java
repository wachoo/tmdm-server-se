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

import com.google.gwt.i18n.client.LocaleInfo;

public class UrlUtil {

    public static String getLanguage() {
        String localeName = LocaleInfo.getCurrentLocale().getLocaleName();
        String language;
        if (localeName.equals("default")) { //$NON-NLS-1$
            language = getLocaleProperty();
        } else
            language = localeName.split("_")[0]; //$NON-NLS-1$
        return language;
    }

    private static native String getLocaleProperty() /*-{
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
