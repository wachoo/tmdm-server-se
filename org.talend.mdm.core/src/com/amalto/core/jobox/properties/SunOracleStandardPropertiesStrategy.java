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

package com.amalto.core.jobox.properties;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.net.URL;
import java.util.Properties;

/**
 * Implementation of {@link StandardPropertiesStrategy} for Sun/Oracle JVM (tested on 1.6)
 */
class SunOracleStandardPropertiesStrategy implements StandardPropertiesStrategy {

    public static final String SUN_PROPERTIES = "sun.conf";

    public Properties getStandardProperties() {
	/*
        Properties properties = new Properties();
        try {
            URL resource = this.getClass().getResource(SUN_PROPERTIES);
            if (resource == null) {
                throw new RuntimeException("Expected file '" + SUN_PROPERTIES + "' but wasn't found.");
            }

            BufferedReader bufferedReader = new BufferedReader(new FileReader(new File(resource.toURI())));
            {
                String line = bufferedReader.readLine();
                while (line != null) {
                    if (!line.startsWith("#")) { // Ignore comments in file
                        String systemProperty = System.getProperty(line);
                        if (systemProperty != null) { // java.util.Properties throws NPE when calling put with null
                            properties.put(line, systemProperty);
                        }
                    }
                    line = bufferedReader.readLine();
                }
            }
            bufferedReader.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
	*/

        return System.getProperties();
    }
}
