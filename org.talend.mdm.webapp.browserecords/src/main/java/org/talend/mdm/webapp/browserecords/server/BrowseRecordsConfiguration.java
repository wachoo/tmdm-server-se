package org.talend.mdm.webapp.browserecords.server;

import java.io.IOException;
import java.util.Properties;

import org.apache.log4j.Logger;

public class BrowseRecordsConfiguration {

    private static final Logger logger = Logger.getLogger(BrowseRecordsConfiguration.class);

    private static final String CONFIG_PROPERTIES_PATH = "../browserecords.properties"; //$NON-NLS-1$

    private static Properties properties;

    /**
     * refresh properties.
     * 
     * @throws IOException
     */
    public static void reload() throws IOException {
        properties = new Properties();
        properties.load(BrowseRecordsConfiguration.class.getResourceAsStream(CONFIG_PROPERTIES_PATH));
    }

    /**
     * get properties.
     * 
     * @return
     * @throws IOException
     */
    private static Properties getProperties() throws IOException {
        if (properties == null) {
            reload();
        }

        return properties;
    }

    /**
     * DOC HSHU Comment method "getPropertyValue".
     * 
     * @param strKey
     * @return
     */
    public static String getPropertyValue(String strKey) {
        String value = null;

        try {
            properties = getProperties();
            value = properties.getProperty(strKey);
        } catch (IOException e) {
            logger.error("Error happened when you loading properties! ", e);//$NON-NLS-1$
        }

        return value;
    }

    /**
     * DOC HSHU Comment method "getMode".
     */
    public static boolean isStandalone() {

        boolean flag = false;

        String mode = getPropertyValue("mode");//$NON-NLS-1$

        if (mode != null && mode.equals("standalone")) //$NON-NLS-1$
            flag = true;

        return flag;
    }
}
