/*
 * Copyright (C) 2006-2017 Talend Inc. - www.talend.com
 * 
 * This source code is available under agreement available at
 * %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
 * 
 * You should have received a copy of the agreement along with this program; if not, write to Talend SA 9 rue Pages
 * 92150 Suresnes, France
 */

package com.amalto.core.storage.hibernate;

import org.apache.log4j.Logger;
import org.talend.mdm.commmon.util.core.MDMConfiguration;

import com.amalto.core.objects.UpdateReportPOJO;
import com.amalto.core.storage.StorageType;
import com.amalto.core.util.Util;

@SuppressWarnings("nls")
public class EntityCountUtil {

    private static final Logger LOGGER = Logger.getLogger(EntityCountUtil.class);

    private static final EntityCounter COUNTER;

    static {
        try {
            if (Util.isEnterprise() && MDMConfiguration.isClusterEnabled()) {
                COUNTER = (EntityCounter) Class.forName("com.amalto.core.storage.hibernate.HazelcastEntityCounter").newInstance();
                LOGGER.info("Enable clustered access support for entity counter.");
            } else {
                COUNTER = new InMemoryEntityCounter();
                LOGGER.info("Clustered access support for entity counter is disabled.");
            }
        } catch (Exception e) {
            String message = "Can't initialize entity counter instance.";
            LOGGER.error(message, e);
            throw new RuntimeException(message, e);
        }
    }

    /**
     * Get entity's count data from cache
     * 
     * @param key
     * @param value
     */
    public static Integer getCount(EntityCountKey key) {
        if (isNeedToCache(key.getStorageName(), key.getStorageType())) {
            return COUNTER.get(key);
        } else {
            return null;
        }
    }

    /**
     * Put entity's count data to cache
     * 
     * @param key
     * @param value
     */
    public static void putCount(EntityCountKey key, Integer value) {
        if (isNeedToCache(key.getStorageName(), key.getStorageType())) {
            COUNTER.put(key, value);
        }
    }

    /**
     * Clear entity's count data from cache
     * 
     * @param storageName
     * @param storageType
     * @param entityName
     */
    public static void clearCounts(String storageName, StorageType storageType, String entityName) {
        if (isNeedToCache(storageName, storageType)) {
            COUNTER.clear(storageName, storageType, entityName);
        }
    }

    private static boolean isNeedToCache(String storageName, StorageType storageType) {
        return !storageType.equals(StorageType.SYSTEM) && !storageName.equals(UpdateReportPOJO.DATA_CLUSTER);
    }

    public static String getEntityKey(String storageName, StorageType storageType, String entityName) {
        return storageName + '#' + storageType + '.' + entityName + '/';
    }

}
