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
package org.talend.mdm.webapp.base.server;

import java.io.IOException;
import java.util.Properties;

import org.apache.log4j.Logger;

public class BaseConfiguration {

    private static final Logger logger = Logger.getLogger(BaseConfiguration.class);

    private static final String CONFIG_PROPERTIES_PATH = "org/talend/mdm/webapp/base/base.properties"; //$NON-NLS-1$

    private static Properties properties;

    public static void reload() throws IOException {
        properties = new Properties();
        properties.load(BaseConfiguration.class.getClassLoader().getResourceAsStream(CONFIG_PROPERTIES_PATH));
    }

    private static Properties getProperties() throws IOException {
        if (properties == null) {
            reload();
        }

        return properties;
    }

    public static String getPropertyValue(String strKey) {
        String value = null;

        try {
            properties = getProperties();
            value = properties.getProperty(strKey);
        } catch (IOException e) {
            logger.error("Error happened when you loading properties! ", e);
        }

        return value;
    }

    public static boolean isStandalone() {
        boolean flag = false;
        String mode = getPropertyValue("mode");//$NON-NLS-1$
        if (mode != null && mode.equals("standalone")) //$NON-NLS-1$
            flag = true;
        return flag;
    }

    public static boolean isUsingDefaultForm() {
        boolean flag = false;
        String formHook = getPropertyValue("formHook");//$NON-NLS-1$
        if (formHook != null && formHook.equals("default")) //$NON-NLS-1$
            flag = true;
        return flag;
    }
}
