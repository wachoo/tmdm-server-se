/*
 * Copyright (C) 2006-2014 Talend Inc. - www.talend.com
 * 
 * This source code is available under agreement available at
 * %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
 * 
 * You should have received a copy of the agreement along with this program; if not, write to Talend SA 9 rue Pages
 * 92150 Suresnes, France
 */

package com.amalto.core.query;

import static com.amalto.core.query.user.UserQueryBuilder.contains;
import static com.amalto.core.query.user.UserQueryBuilder.from;

import com.amalto.core.query.user.Select;
import com.amalto.core.query.user.UserQueryBuilder;
import org.talend.mdm.storage.CacheStorage;

@SuppressWarnings("nls")
public class CacheStorageTest extends StorageTestCase {

    private CacheStorage cacheStorage;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        cacheStorage = new CacheStorage(storage);
    }

    public void testCache() throws Exception {
        UserQueryBuilder qb = from(person).selectId(person).cache();
        Select select = qb.getSelect();
        assertFalse(cacheStorage.hasCache(select));
        cacheStorage.fetch(select);
        assertTrue(cacheStorage.hasCache(select));
        cacheStorage.fetch(select);
    }

    public void testUsageEviction() throws Exception {
        UserQueryBuilder qb = from(person).selectId(person).cache();
        Select select = qb.getSelect();
        assertFalse(cacheStorage.hasCache(select));
        cacheStorage.fetch(select);
        assertTrue(cacheStorage.hasCache(select));
        for (int i = 0; i < cacheStorage.getMaxCacheEntryUsage() - 1; i++) {
            cacheStorage.fetch(select);
        }
        assertEquals(1, cacheStorage.getCacheEntryUsage(select));
        cacheStorage.fetch(select);
        assertEquals(cacheStorage.getMaxCacheEntryUsage(), cacheStorage.getCacheEntryUsage(select));
    }

    public void testTimeEviction() throws Exception {
        cacheStorage.setMaxCacheEntryLifetime(2000); // Change eviction time to avoid long running tests
        UserQueryBuilder qb = from(person).selectId(person).cache();
        Select select = qb.getSelect();
        assertFalse(cacheStorage.hasCache(select));
        cacheStorage.fetch(select);
        assertTrue(cacheStorage.hasCache(select));
        Thread.sleep(cacheStorage.getMaxCacheEntryLifetime());
        assertFalse(cacheStorage.hasCache(select)); // Cache entry is now too old
        cacheStorage.fetch(select);
        assertTrue(cacheStorage.hasCache(select)); // But redoing a fetch caches a new value
    }

    public void testCapacityEviction() {
        UserQueryBuilder qb = from(person).selectId(person).cache();
        Select initialSelect = qb.getSelect();
        assertFalse(cacheStorage.hasCache(initialSelect));
        cacheStorage.fetch(initialSelect);
        assertTrue(cacheStorage.hasCache(initialSelect));
        for (int i = 0; i < cacheStorage.getMaxCacheCapacity(); i++) {
            qb = from(person).selectId(person).where(contains(person.getField("firstname"), "value" + i)).cache();
            assertFalse(cacheStorage.hasCache(qb.getExpression()));
            cacheStorage.fetch(qb.getExpression());
            assertTrue(cacheStorage.hasCache(qb.getExpression()));
        }
        // Over-sized capacity, initial query should no longer be in cache
        assertFalse(cacheStorage.hasCache(initialSelect));
    }
}
