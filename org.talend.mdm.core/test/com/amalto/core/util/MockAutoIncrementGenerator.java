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
package com.amalto.core.util;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

/**
 * Mock com.amalto.core.util.AutoIncrementGenerator
 */
@SuppressWarnings("nls")
public class MockAutoIncrementGenerator {

    private static final Logger logger = Logger.getLogger(MockAutoIncrementGenerator.class);

    private static Properties CONFIGURATION = null;

    private static Properties UNUSED_AUTOINCREMENTS = null;

    private static String encoding = "UTF-8";

    // key: XMLRootNode hashCode; value: <key: universe.Cluster.Concept.Attribute ; value: used auto_increment id>
    static Map<Integer, Map<String, String>> USEDIDS = new HashMap<Integer, Map<String, String>>();

    static {
        init();
    }

    public static void init() {
        // first try Current path
        CONFIGURATION = new Properties();
        UNUSED_AUTOINCREMENTS = new Properties();
        try {
            // setup CONF.AutoIncrement.AutoIncrement.xml
            URL url = MockAutoIncrementGenerator.class.getResource("CONF.AutoIncrement.AutoIncrement_bak.xml");
            String srcFileName = url.getPath();
            URL destUrl = MockAutoIncrementGenerator.class.getResource("CONF.AutoIncrement.AutoIncrement.xml");
            String destFileName = destUrl.getPath();
            File src_AutoIncrementFile = new File(srcFileName);
            File dest_AutoIncrementFile = new File(destFileName);
            FileUtils.copyFile(src_AutoIncrementFile, dest_AutoIncrementFile);
            String xml = FileUtils.readFileToString(dest_AutoIncrementFile, encoding);
            if (xml != null && xml.trim().length() > 0)
                CONFIGURATION = Util.convertAutoIncrement(xml);

            // setup CONF.AutoIncrement.AutoIncrement_unUsed.xml and get unused AutoIncrement ID
            url = MockAutoIncrementGenerator.class.getResource("CONF.AutoIncrement.AutoIncrement_bakUnUsed.xml");
            srcFileName = url.getPath();
            destUrl = MockAutoIncrementGenerator.class.getResource("CONF.AutoIncrement.AutoIncrement_unUsed.xml");
            destFileName = destUrl.getPath();
            src_AutoIncrementFile = new File(srcFileName);
            dest_AutoIncrementFile = new File(destFileName);
            FileUtils.copyFile(src_AutoIncrementFile, dest_AutoIncrementFile);
            String unused_xml = FileUtils.readFileToString(dest_AutoIncrementFile, encoding);
            if (unused_xml != null && unused_xml.trim().length() > 0)
                UNUSED_AUTOINCREMENTS = Util.convertAutoIncrement(unused_xml);
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
        String key = universe + "." + dataCluster + "." + conceptName; //$NON-NLS-1$ //$NON-NLS-2$
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
            // update File
            saveUnUsedIdsToFile(false, rootNodeHashCode);
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
        saveToFile();
        return num;
    }

    public synchronized static void saveToFile() {
        try {
            String xmlString = Util.convertAutoIncrement(CONFIGURATION);
            URL url = AutoIncrementGeneratorTest.class.getResource("CONF.AutoIncrement.AutoIncrement.xml");
            String fileName = url.getPath();
            writeXMLToFile(xmlString, fileName);
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
        }
    }

    /**
     * when failure = true, save unused auto_increment IDS to File; when failure = false, update unused auto_increment
     * IDS to File.
     * 
     * @param failure
     */
    public static void saveUnUsedIdsToFile(boolean failure, int rootNodeHashCode) {
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
            URL url = AutoIncrementGeneratorTest.class.getResource("CONF.AutoIncrement.AutoIncrement_unUsed.xml");
            String fileName = url.getPath();
            writeXMLToFile(xmlString, fileName);
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
        }
    }

    /**
     * 
     * record the used ID, when save failure, the ID need to be saved to File
     * 
     * @param xml the current record
     * @param key the attribute xpath
     * @param id auto_increment id
     */
    public static void saveIdToMap(int rootNodeHashCode, String key, String id) {
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

    public static void writeXMLToFile(String xml, String fileName) throws IOException {
        File file = new File(fileName);
        FileUtils.writeStringToFile(file, xml, encoding);
    }
}