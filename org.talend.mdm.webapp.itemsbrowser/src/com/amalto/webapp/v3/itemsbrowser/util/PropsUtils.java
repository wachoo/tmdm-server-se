package com.amalto.webapp.v3.itemsbrowser.util;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;



public class PropsUtils {

    private static final String CONFIG_PROPERTIES_PATH = "/../itemsbrowser.properties";

    private static Properties properties;

    /**
     * refresh properties.
     */
    public static void refreshProperties() {
        properties = new Properties();

        try {
            properties.load(PropsUtils.class.getResourceAsStream(CONFIG_PROPERTIES_PATH));
        } 
        catch (FileNotFoundException e) {
            e.printStackTrace();
        } 
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * get properties.
     * @return
     */
    public static Properties getProperties() {
        if(properties == null) {
            refreshProperties();
        }
        
        return properties;
    }
}
