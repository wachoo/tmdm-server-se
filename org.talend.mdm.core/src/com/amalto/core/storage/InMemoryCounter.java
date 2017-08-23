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

import com.amalto.core.storage.Counter.AbstractCounter;

public class InMemoryCounter extends AbstractCounter implements Counter {

    public InMemoryCounter() {
        cache = new HashMap<>();
    }

    @Override
    public Integer get(CountKey countKey) {
        return super.get(countKey);
    }

    @Override
    public void put(CountKey countKey, Integer value) {
        super.put(countKey, value);
    }

    @Override
    public void clear(CountKey countKey) {
        super.clear(countKey);
    }

    @Override
    public void clearAll() {
        super.clearAll();
    }

    @Override
    public void clearAll(String storageName) {
        super.clearAll(storageName);
    }

}
