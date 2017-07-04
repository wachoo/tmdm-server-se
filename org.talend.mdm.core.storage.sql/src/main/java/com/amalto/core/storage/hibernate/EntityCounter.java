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

import com.amalto.core.storage.StorageType;

public interface EntityCounter {

    /**
     * Get entity's count data from cache
     * 
     * @param key
     * @param value
     */
    Integer get(EntityCountKey key);

    /**
     * Put entity's count data to cache
     * 
     * @param key
     * @param value
     */
    void put(EntityCountKey key, Integer value);

    /**
     * Clear entity's count data from cache
     * 
     * @param storageName
     * @param storageType
     * @param entityName
     */
    void clear(String storageName, StorageType storageType, String entityName);

}
