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
import java.util.Map;

import com.amalto.core.storage.Counter.AbstractCounter;

public class InMemoryCounter extends AbstractCounter implements Counter {

    private final Map<String, Integer> COUNT_CACHE;

    public InMemoryCounter() {
        COUNT_CACHE = new HashMap<>();
    }

    @Override
    public Integer get(CountKey countKey) {
        return get(COUNT_CACHE, countKey);
    }

    @Override
    public void put(CountKey countKey, Integer value) {
        put(COUNT_CACHE, countKey, value);
    }

    @Override
    public void clear(CountKey countKey) {
        clear(COUNT_CACHE, countKey);
    }

    @Override
    public void clearAll() {
        clearAll(COUNT_CACHE);
    }

    @Override
    public void clearAll(String storageName) {
        clearAll(COUNT_CACHE, storageName);
    }

}
