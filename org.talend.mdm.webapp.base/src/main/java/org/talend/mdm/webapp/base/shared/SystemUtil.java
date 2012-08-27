// ============================================================================
//
// Copyright (C) 2006-2012 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.mdm.webapp.base.shared;

public class SystemUtil {

    private static final String DEFAULT_DATETIME_FORMAT = "MM/dd/yyyy HH:mm:ss";//$NON-NLS-1$

    public static String getDateTimeFormat(String language) {
        SystemLocale defaultLocale = SystemLocaleFactory.getInstance().getLocale(language);
        String format=defaultLocale == null ? null : defaultLocale.getDateTimeFormat();
        if(format==null)
            format = DEFAULT_DATETIME_FORMAT;
        return format;
    }

}
