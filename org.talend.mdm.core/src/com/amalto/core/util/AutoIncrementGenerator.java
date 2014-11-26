package com.amalto.core.util;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.talend.mdm.commmon.util.webapp.XSystemObjects;

import com.amalto.core.objects.ItemPOJO;
import com.amalto.core.objects.ItemPOJOPK;
import com.amalto.core.objects.datacluster.DataClusterPOJOPK;

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

    private static Properties UNUSED_AUTOINCREMENTS = null;

    private static String[] UNUSEDIDS = new String[] { "AutoIncrement_unUsed" }; //$NON-NLS-1$

    // key: XMLRootNode hashCode; value: <key: universe.Cluster.Concept.Attribute ; value: used auto_increment id>
    static Map<Integer, Map<String, String>> USEDIDS = new HashMap<Integer, Map<String, String>>();

    static {
        init();
    }

    public static void init() {
        //first try Current path
        CONFIGURATION = new Properties();
        UNUSED_AUTOINCREMENTS = new Properties();
        try {
            ItemPOJOPK pk = new ItemPOJOPK(DC, CONCEPT, IDS);
            ItemPOJO itempojo = ItemPOJO.load(pk);
            if (itempojo == null) {
                logger.info("Could not load configuration from database, use default configuration."); //$NON-NLS-1$
            } else {
                String xml = itempojo.getProjectionAsString();
                if (xml != null && xml.trim().length() > 0) {
                    CONFIGURATION = Util.convertAutoIncrement(xml);
                }
            }
            // get unused AutoIncrement ID
            ItemPOJOPK unused_pk = new ItemPOJOPK(DC, CONCEPT, UNUSEDIDS);
            ItemPOJO unused_itempojo = ItemPOJO.load(unused_pk);
            if (unused_itempojo != null) {
                String xml = unused_itempojo.getProjectionAsString();
                if (xml != null && xml.trim().length() > 0)
                    UNUSED_AUTOINCREMENTS = Util.convertAutoIncrement(xml);
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

    /**
     * generate auto_increment id, web UI use it when save a record which includes auto_increment attributes.
     * 
     * @param rootNodeHashCode
     * @param universe
     * @param dataCluster
     * @param conceptName
     * @return
     */
    public synchronized static long generateNum(int rootNodeHashCode, String universe, String dataCluster, String conceptName) {
        String key = universe + "." + dataCluster + "." + conceptName; //$NON-NLS-1$ //$NON-NLS-2$
        // TMDM-831 GET AUTO_INCREMENT UNUSED IDS
        if (UNUSED_AUTOINCREMENTS.containsKey(key)) {
            String unusedIds = UNUSED_AUTOINCREMENTS.getProperty(key);
            String id = unusedIds.substring(0, unusedIds.indexOf(".")); //$NON-NLS-1$
            saveIdToMap(rootNodeHashCode, key, id);
            String unused = unusedIds.substring(unusedIds.indexOf(".") + 1); //$NON-NLS-1$
            if (unused != null && unused.trim().length() > 0)
                UNUSED_AUTOINCREMENTS.put(key, unused);
            else
                UNUSED_AUTOINCREMENTS.remove(key);
            // update DB
            saveUnUsedIdsToDB(false, rootNodeHashCode);
            return Long.parseLong(id);
        }
        long num;
        String n = CONFIGURATION.getProperty(key);
        if (n == null) {
            num = 0;
        } else {
            num = Long.valueOf(n);
        }
        num++;

        CONFIGURATION.setProperty(key, String.valueOf(num));
        saveIdToMap(rootNodeHashCode, key, String.valueOf(num));
        saveToDB();
        return num;
    }

    public synchronized static void saveToDB() {
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

    /**
     * when failure = true, save unused auto_increment IDS to DB; when failure = false, update unused auto_increment IDS
     * to DB.
     * 
     * @param failure
     */
    public synchronized static void saveUnUsedIdsToDB(boolean failure, int rootNodeHashCode) {
        try {
            if (failure) {
                if (USEDIDS == null || USEDIDS.size() == 0)
                    return;
                for (Integer key : USEDIDS.keySet()) {
                    Map<String, String> map = USEDIDS.get(key);
                    for (String xpath : map.keySet()) {
                        if (!UNUSED_AUTOINCREMENTS.containsKey(xpath))
                            UNUSED_AUTOINCREMENTS.put(xpath, map.get(xpath) + "."); //$NON-NLS-1$
                        else
                            UNUSED_AUTOINCREMENTS.put(xpath, UNUSED_AUTOINCREMENTS.get(xpath) + map.get(xpath) + "."); //$NON-NLS-1$
                    }
                }
                check(rootNodeHashCode);
            }
            String xmlString = Util.convertAutoIncrement(UNUSED_AUTOINCREMENTS);
            ItemPOJO pojo = new ItemPOJO(DC, // cluster
                    CONCEPT, // concept name
                    UNUSEDIDS, System.currentTimeMillis(), // insertion time
                    xmlString // actual data
            );
            pojo.setDataModelName(XSystemObjects.DM_CONF.getName());
            pojo.store(null);
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
        }
    }

    /**
     * 
     * record the used ID, when save failure, the ID need to be saved to DB
     * 
     * @param xml the current record
     * @param key the attribute xpath
     * @param id auto_increment id
     */
    public synchronized static void saveIdToMap(int rootNodeHashCode, String key, String id) {
        if (!USEDIDS.containsKey(rootNodeHashCode))
            USEDIDS.put(rootNodeHashCode, new HashMap<String, String>());
        USEDIDS.get(rootNodeHashCode).put(key, id);
    }

    /**
     * when save successfully, check USEDIDS map
     * 
     * @param rootNodeHashCode
     */
    public synchronized static void check(int rootNodeHashCode) {
        if (USEDIDS.containsKey(rootNodeHashCode))
            USEDIDS.remove(rootNodeHashCode);
    }
}
