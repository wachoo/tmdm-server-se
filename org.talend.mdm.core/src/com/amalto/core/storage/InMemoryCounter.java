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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class InMemoryCounter implements Counter {

    private final Map<String, Integer> COUNT_CACHE;

    public InMemoryCounter() {
        COUNT_CACHE = new HashMap<>();
    }

    @Override
    public Integer get(CountKey countKey) {
        return COUNT_CACHE.get(countKey.toString());
    }

    @Override
    public void put(CountKey countKey, Integer value) {
        synchronized (COUNT_CACHE) {
            COUNT_CACHE.put(countKey.toString(), value);
        }
    }

    @Override
    public void clear(CountKey countKey) {
        String entityKey = countKey.getEntityKey();
        synchronized (COUNT_CACHE) {
            List<String> toClear = COUNT_CACHE.keySet().stream().filter(key -> key.startsWith(entityKey))
                    .collect(Collectors.toList());
            toClear.stream().forEach(key -> COUNT_CACHE.remove(key));
        }
    }

    @Override
    public void clearAll() {
        List<String> toClear = COUNT_CACHE.keySet().stream().collect(Collectors.toList());
        toClear.stream().forEach(key -> COUNT_CACHE.remove(key));
    }

}
