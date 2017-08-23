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
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.amalto.core.query.user.Condition;
import com.amalto.core.query.user.Select;

public interface Counter {

    /**
     * Get entity's count data from cache
     * 
     * @param countKey
     * @param value
     */
    Integer get(CountKey countKey);

    /**
     * Put entity's count data to cache
     * 
     * @param countKey
     * @param value
     */
    void put(CountKey countKey, Integer value);

    /**
     * Clear entity's count data from cache
     * 
     * @param countKey
     */
    void clear(CountKey countKey);

    /**
     * Clear all count data from cache
     * 
     */
    void clearAll();

    /**
     * Clear all count data from cache for the storage
     * 
     * @param storageName
     */
    void clearAll(String storageName);

    public static class CountKey {

        private String storageName;

        private StorageType storageType;

        private String entityName;

        private Condition condition;

        public CountKey(Storage storage, Select select) {
            this(storage.getName(), storage.getType(), select.getTypes().get(0).getName(), select.getCondition());
        }

        public CountKey(String storageName, StorageType storageType, String entityName, Condition condition) {
            this(storageName, storageType, entityName);
            this.condition = condition;
        }

        public CountKey(String storageName, StorageType storageType, String entityName) {
            this.storageName = storageName;
            this.storageType = storageType;
            this.entityName = entityName;
        }

        public Condition getCondition() {
            return condition;
        }

        public String getEntityName() {
            return entityName;
        }

        public String getStorageName() {
            return storageName;
        }

        public StorageType getStorageType() {
            return storageType;
        }

        @Override
        public String toString() {
            int conditionHash = condition != null ? condition.hashCode() : 0;
            return getEntityKey() + conditionHash;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof CountKey)) {
                return false;
            }
            CountKey anotherKey = (CountKey) o;
            return toString().equals(anotherKey.toString());
        }

        @Override
        public int hashCode() {
            return toString().hashCode();
        }

        public String getEntityKey() {
            return storageName + '#' + storageType + '.' + entityName + '/';
        }
    }

    public static class CountKeyList {

        private static ThreadLocal<Set<CountKey>> threadLocal = new ThreadLocal<Set<CountKey>>() {

            public Set<CountKey> initialValue() {
                return new HashSet<CountKey>();
            }
        };

        private CountKeyList() {
        }

        public static Set<CountKey> get() {
            return threadLocal.get();
        }

        public static void remove() {
            threadLocal.remove();
        }
    }

    public static abstract class AbstractCounter implements Counter {

        protected Map<String, Integer> cache;

        public Integer get(CountKey countKey) {
            return cache.get(countKey.toString());
        }

        public synchronized void put(CountKey countKey, Integer value) {
            cache.put(countKey.toString(), value);
        }

        public synchronized void clear(CountKey countKey) {
            String entityKey = countKey.getEntityKey();
            List<String> toClear = cache.keySet().stream().filter(key -> key.startsWith(entityKey)).collect(Collectors.toList());
            toClear.stream().forEach(key -> cache.remove(key));
        }

        public synchronized void clearAll() {
            List<String> toClear = cache.keySet().stream().collect(Collectors.toList());
            toClear.stream().forEach(key -> cache.remove(key));
        }

        public synchronized void clearAll(String storageName) {
            String masterPrefix = storageName + '#' + StorageType.MASTER;
            String stagingPrefix = storageName + '#' + StorageType.STAGING;
            List<String> toClear = cache.keySet().stream()
                    .filter(key -> key.startsWith(masterPrefix) || key.startsWith(stagingPrefix)).collect(Collectors.toList());
            toClear.stream().forEach(key -> cache.remove(key));
        }

    }

}
