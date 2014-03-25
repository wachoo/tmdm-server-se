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

import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;

/**
 * DOC HSHU class global comment. Detailled comment
 */
public class GxtFactory {


    /** the log used by this class */
    private static Logger log = Logger.getLogger(GxtFactory.class);

    /** the collection of actions that we know about */
    private Map<String, String> entries = new HashMap<String, String>();

    /** the name of the action mapping file */
    private String gxtRegisterFileName;

    private Map<String, Boolean> excludingMapping = new HashMap<String, Boolean>();

    private String excludingFileName;
    /**
     * Creates a new instance, using the given configuration file.
     */
    public GxtFactory(String gxtRegisterFileName, String excludingFileName) {
        this.gxtRegisterFileName = gxtRegisterFileName;
        this.excludingFileName = excludingFileName;
        init();
    }

    /**
     * Initialises this component, reading in and creating the map of action names to action classes.
     */
    private void init() {
        try {
            // load the properties file containing the name -> class name mapping
            InputStream in = getClass().getClassLoader().getResourceAsStream(gxtRegisterFileName);
            Properties props = new Properties();
            props.load(in);

            // and store them in a HashMap
            Enumeration<?> e = props.propertyNames();
            String actionName;
            String className;

            while (e.hasMoreElements()) {
                actionName = (String) e.nextElement();
                className = props.getProperty(actionName);

                entries.put(actionName, className);
            }

            InputStream excludingIn = getClass().getClassLoader().getResourceAsStream(excludingFileName);
            Properties excludeProps = new Properties();
            excludeProps.load(excludingIn);

            Enumeration<?> excludeEnum = excludeProps.propertyNames();
            String appName;
            Boolean isExcluding;

            while (excludeEnum.hasMoreElements()) {
                appName = (String) excludeEnum.nextElement();
                isExcluding = Boolean.valueOf(excludeProps.getProperty(appName));
                excludingMapping.put(appName, isExcluding);
            }

        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    public String getGxtEntryModule(String context, String application) {
        String key = context + '.' + application;
        if (entries != null && entries.containsKey(key)) {
            return entries.get(key) == null ? null : (String) entries.get(key);
        }
        return null;
    }

    public Boolean isExcluded(String context, String application) {
        String key = context + '.' + application;
        Boolean isExcluded = excludingMapping.get(key);
        if (isExcluded != null) {
            return isExcluded;
        }
        return false;
    }

}
