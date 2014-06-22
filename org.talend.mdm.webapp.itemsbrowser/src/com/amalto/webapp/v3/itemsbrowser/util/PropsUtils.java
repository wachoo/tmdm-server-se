package com.amalto.webapp.v3.itemsbrowser.util;

import java.io.IOException;
import java.util.Properties;



public class PropsUtils {

    private static final String CONFIG_PROPERTIES_PATH = "/../itemsbrowser.properties";

    private static Properties properties;

    /**
     * refresh properties.
     * @throws IOException 
     */
    public static void refreshProperties() throws IOException {
        properties = new Properties();
        properties.load(PropsUtils.class.getResourceAsStream(CONFIG_PROPERTIES_PATH));
    }

    /**
     * get properties.
     * @return
     * @throws IOException 
     */
    public static Properties getProperties() throws IOException {
        if(properties == null) {
            refreshProperties();
        }
        
        return properties;
    }
}
