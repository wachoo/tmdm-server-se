package org.talend.mdm.webapp.general.client.util;

import com.google.gwt.user.client.Window.Location;


public class UrlUtil {

    public static String getLanguage(){
        String lang = Location.getParameter("language"); //$NON-NLS-1$
        lang = lang == null || lang.trim().length() == 0 ? "en" : lang; //$NON-NLS-1$
        return lang;
    }
}
