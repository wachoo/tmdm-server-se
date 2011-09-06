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
package org.talend.mdm.webapp.welcomeportal.client.Util;

import com.google.gwt.user.client.Window.Location;

public class UrlUtil {

    public static String getLanguage() {
        String lang = Location.getParameter("language"); //$NON-NLS-1$
        lang = lang == null || lang.trim().length() == 0 ? "en" : lang; //$NON-NLS-1$
        return lang;
    }
}
