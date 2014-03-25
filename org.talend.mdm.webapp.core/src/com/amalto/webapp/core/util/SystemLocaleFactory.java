// ============================================================================
//
// Copyright (C) 2006-2014 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package com.amalto.webapp.core.util;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

public class SystemLocaleFactory implements Serializable {

    /** unique instance */
    private static SystemLocaleFactory sInstance = null;

    private Map<String, SystemLocale> supportedLocales = new LinkedHashMap<String, SystemLocale>();

    /**
     * Private constuctor
     */
    private SystemLocaleFactory() {
        super();
    }

    /**
     * Get the unique instance of this class.
     */
    public static synchronized SystemLocaleFactory getInstance() {

        if (sInstance == null) {
            sInstance = new SystemLocaleFactory();
        }

        return sInstance;

    }

    public void load(SystemLocaleInitializable initializable) throws Exception {
        initializable.setSupportedLocales(supportedLocales);
        initializable.doInit();
    }

    public Map<String, SystemLocale> getSupportedLocales() {
        return supportedLocales;
    }

    public SystemLocale getLocale(String iso) {

        if (iso == null)
            return null;

        for (String myIso : supportedLocales.keySet()) {
            SystemLocale myLocale = supportedLocales.get(myIso);
            if (myLocale.getIso().equals(iso))
                return myLocale;
        }

        return null;

    }

}
