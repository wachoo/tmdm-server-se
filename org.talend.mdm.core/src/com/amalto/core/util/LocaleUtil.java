// ============================================================================
//
// Copyright (C) 2006-2013 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package com.amalto.core.util;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

public class LocaleUtil {

    public static Locale getLocale(HttpServletRequest request) {
        Locale locale;
        String language = request.getParameter("language"); //$NON-NLS-1$
        if (language == null) {
            language = (String) request.getSession().getAttribute("language"); //$NON-NLS-1$
        }
        if (language == null) {
            language = request.getHeader("X-MDM-Language"); //$NON-NLS-1$
        }
        if (language == null) {
            locale = request.getLocale();
        } else {
            locale = new Locale(language.toLowerCase());
        }
        return locale;
    }
}
