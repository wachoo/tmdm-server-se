package org.talend.mdm.webapp.itemsbrowser2.server;

import java.io.IOException;
import java.util.Properties;

import org.apache.log4j.Logger;

public class ItemsBrowserConfiguration {

    private static final Logger logger = Logger.getLogger(ItemsBrowserConfiguration.class);

    private static final String CONFIG_PROPERTIES_PATH = "../itembrowser2.properties";

    private static Properties properties;

    /**
     * refresh properties.
     * 
     * @throws IOException
     */
    public static void reload() throws IOException {
        properties = new Properties();
        properties.load(ItemsBrowserConfiguration.class.getResourceAsStream(CONFIG_PROPERTIES_PATH));
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
    private static String getPropertyValue(String strKey) {
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

        if (mode != null && mode.equals("standalone"))
            flag = true;//$NON-NLS-1$

        return flag;

    }
    

}
