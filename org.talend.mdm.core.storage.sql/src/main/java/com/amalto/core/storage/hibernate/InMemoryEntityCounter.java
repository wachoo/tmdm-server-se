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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.amalto.core.storage.StorageType;

public class InMemoryEntityCounter implements EntityCounter {

    private final Map<String, Integer> COUNT_CACHE;

    public InMemoryEntityCounter() {
        COUNT_CACHE = new HashMap<>();
    }

    @Override
    public Integer get(EntityCountKey key) {
        return COUNT_CACHE.get(key.toString());
    }

    @Override
    public void put(EntityCountKey key, Integer value) {
        synchronized (COUNT_CACHE) {
            COUNT_CACHE.put(key.toString(), value);
        }
    }

    @Override
    public void clear(String storageName, StorageType storageType, String entityName) {
        String entityKey = EntityCountUtil.getEntityKey(storageName, storageType, entityName);
        synchronized (COUNT_CACHE) {
            List<String> toRemove = COUNT_CACHE.keySet().stream().filter(key -> key.startsWith(entityKey))
                    .collect(Collectors.toList());
            toRemove.stream().forEach(key -> COUNT_CACHE.remove(key));
        }
    }

}
