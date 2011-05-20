package com.amalto.core.util;

import java.io.File;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.talend.mdm.commmon.util.webapp.XSystemObjects;

import com.amalto.core.ejb.ItemPOJO;
import com.amalto.core.ejb.ItemPOJOPK;
import com.amalto.core.objects.datacluster.ejb.DataClusterPOJOPK;

/**
 * 
 * AutoIncrement to generate a num
 * the autoincrement num is saved in auto_increment.conf file
 * @author achen
 *
 */
public class AutoIncrementGenerator {
    //static volatile long  num=-1;

    private static final Logger logger = Logger.getLogger(AutoIncrementGenerator.class);
    static File file = new File("auto_increment.conf"); // $NON-NLS-1$
    public static String AUTO_INCREMENT = "Auto_Increment"; // $NON-NLS-1$
    private static Properties CONFIGURATION = null;
    static DataClusterPOJOPK DC = new DataClusterPOJOPK(XSystemObjects.DC_CONF.getName());
    static String[] IDS = new String[]{"AutoIncrement"}; // $NON-NLS-1$
    static String CONCEPT = "AutoIncrement"; // $NON-NLS-1$

    static {
        init();
    }

    public static void init() {
        //first try Current path
        CONFIGURATION = new Properties();
        try {
            ItemPOJOPK pk = new ItemPOJOPK(DC, CONCEPT, IDS);
            ItemPOJO itempojo = ItemPOJO.load(pk);
            if (itempojo == null) {
                logger.info("Could not load configuration from database, use default configuration.");
            } else {
                String xml = itempojo.getProjectionAsString();
                if (xml != null && xml.trim().length() > 0) {
                    CONFIGURATION = Util.convertAutoIncrement(xml);
                }
            }
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
        }
    }

    /**
     * this is not a good algorithm, need to find a better way
     *
     * @param universe
     * @param dataCluster
     * @param conceptName
     * @return
     */
    public synchronized static long generateNum(String universe, String dataCluster, String conceptName) {
        String key = universe + "." + dataCluster + "." + conceptName;
        long num;
        String n = CONFIGURATION.getProperty(key);
        if (n == null) {
            num = 0;
        } else {
            num = Long.valueOf(n);
        }
        num++;

        CONFIGURATION.setProperty(key, String.valueOf(num));
        return num;
    }

    public static void saveToDB() {
        try {
            String xmlString = Util.convertAutoIncrement(CONFIGURATION);
            ItemPOJO pojo = new ItemPOJO(
                    DC,    //cluster
                    CONCEPT,                                //concept name
                    IDS,
                    System.currentTimeMillis(),            //insertion time
                    xmlString                                                //actual data
            );
            pojo.setDataModelName(XSystemObjects.DM_CONF.getName());
            pojo.store(null);
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
        }
    }
}
