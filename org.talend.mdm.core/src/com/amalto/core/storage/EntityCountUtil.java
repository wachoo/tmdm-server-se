/*
 * Copyright (C) 2006-2017 Talend Inc. - www.talend.com
 * 
 * This source code is available under agreement available at
 * %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
 * 
 * You should have received a copy of the agreement along with this program; if not, write to Talend SA 9 rue Pages
 * 92150 Suresnes, France
 */

package com.amalto.core.storage;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.log4j.Logger;
import org.talend.mdm.commmon.util.core.MDMConfiguration;

import com.amalto.core.objects.UpdateReportPOJO;
import com.amalto.core.storage.Counter.CountKey;
import com.amalto.core.storage.Counter.CountKeyList;
import com.amalto.core.util.Util;

@SuppressWarnings("nls")
public class EntityCountUtil {

    private static final Logger LOGGER = Logger.getLogger(EntityCountUtil.class);

    private static final Set<CountKey> CLEAR_LIST = new HashSet<>();

    private static final Counter COUNTER;

    static {
        try {
            if (Util.isEnterprise() && MDMConfiguration.isClusterEnabled()) {
                COUNTER = (Counter) Class.forName("com.amalto.core.storage.HazelcastCounter").newInstance();
                LOGGER.info("Enable clustered access support for entity counter.");
            } else {
                COUNTER = new InMemoryCounter();
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
     * @param countKey
     * @param value
     */
    public static Integer getCount(CountKey countKey) {
        if (isNeedToCache(countKey.getStorageName(), countKey.getStorageType())) {
            return COUNTER.get(countKey);
        } else {
            return null;
        }
    }

    /**
     * Put entity's count data to cache
     * 
     * @param countKey
     * @param value
     */
    public static void putCount(CountKey countKey, Integer value) {
        if (isNeedToCache(countKey.getStorageName(), countKey.getStorageType())) {
            COUNTER.put(countKey, value);
        }
    }

    /**
     * Add count key to the list which will be cleared latter
     * 
     * @param countKey
     */
    public static void addToClearList(CountKey countKey) {
        if (isNeedToCache(countKey.getStorageName(), countKey.getStorageType())) {
            CountKeyList.get().add(countKey);
            CLEAR_LIST.add(countKey);
        }
    }

    /**
     * Clear entity's count data from cache (the change was made in current thread)
     * 
     * @param storage
     */
    public static void clearCounts() {
        Set<CountKey> countKeys = CountKeyList.get();
        countKeys.stream().forEach(key -> COUNTER.clear(key));
        CountKeyList.remove();
    }

    /**
     * Clear entity's count data from cache (the change was made in sub-threads)
     * 
     * @param storage
     */
    public static void clearCounts(Storage storage) {
        Iterator<CountKey> countKeys = CLEAR_LIST.iterator();
        while (countKeys.hasNext()) {
            CountKey countKey = countKeys.next();
            if (isNeedToClear(countKey, storage)) {
                COUNTER.clear(countKey);
                countKeys.remove();
            }
        }
    }

    /**
     * Clear all entity count data from cache
     */
    public static void clearAll() {
        COUNTER.clearAll();
    }

    private static boolean isNeedToCache(String storageName, StorageType storageType) {
        return !storageType.equals(StorageType.SYSTEM) && !storageName.equals(UpdateReportPOJO.DATA_CLUSTER);
    }

    private static boolean isNeedToClear(CountKey countKey, Storage storage) {
        return countKey.getStorageName().equals(storage.getName()) && countKey.getStorageType().equals(storage.getType());
    }

}
