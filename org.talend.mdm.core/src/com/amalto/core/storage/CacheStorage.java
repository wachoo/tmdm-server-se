/*
 * Copyright (C) 2006-2013 Talend Inc. - www.talend.com
 * 
 * This source code is available under agreement available at
 * %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
 * 
 * You should have received a copy of the agreement along with this program; if not, write to Talend SA 9 rue Pages
 * 92150 Suresnes, France
 */

package com.amalto.core.storage;

import com.amalto.core.query.user.Expression;
import com.amalto.core.storage.datasource.DataSource;
import com.amalto.core.storage.datasource.DataSourceDefinition;
import com.amalto.core.storage.record.DataRecord;
import com.amalto.core.storage.transaction.StorageTransaction;
import org.apache.log4j.Logger;
import org.talend.mdm.commmon.metadata.MetadataRepository;
import org.talend.mdm.commmon.metadata.compare.Compare;
import org.talend.mdm.commmon.metadata.compare.ImpactAnalyzer;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * <p>
 * An implementation of {@link com.amalto.core.storage.Storage} that caches
 * {@link com.amalto.core.storage.Storage#fetch(com.amalto.core.query.user.Expression) fetch} results. Cache key is the
 * hashCode of the {@link com.amalto.core.query.user.Expression} used to read records.
 * </p>
 * <p>
 * Cache results are evicted on the following conditions:
 * <ul>
 * <li>Cache result hasn't been used for a long time.</li>
 * <li>Cache result was used too many times.</li>
 * </ul>
 * When at least one of the condition is met, a new result is computed.
 * </p>
 * TODO Evict from cache on update/delete query.
 */
public class CacheStorage implements Storage {

    private static final int MAX_CACHE_LIFETIME = 60 * 1000;

    private static final Logger LOGGER = Logger.getLogger(CacheStorage.class);

    private static final int MAX_TOKEN = 10;

    private final Storage delegate;

    private final Map<Expression, CacheValue> cache = new HashMap<Expression, CacheValue>();

    public CacheStorage(Storage delegate) {
        this.delegate = delegate;
    }

    static class CacheValue {

        AtomicInteger tokenCount = new AtomicInteger(MAX_TOKEN);

        long lastAccessTime = System.currentTimeMillis();

        List<DataRecord> results = Collections.emptyList();
    }

    @Override
    public Storage asInternal() {
        return delegate.asInternal();
    }

    @Override
    public int getCapabilities() {
        return delegate.getCapabilities();
    }

    @Override
    public StorageTransaction newStorageTransaction() {
        return delegate.newStorageTransaction();
    }

    @Override
    public void init(DataSourceDefinition dataSource) {
        delegate.init(dataSource);
    }

    @Override
    public void prepare(MetadataRepository repository, Set<Expression> optimizedExpressions, boolean force,
            boolean dropExistingData) {
        delegate.prepare(repository, optimizedExpressions, force, dropExistingData);
    }

    @Override
    public void prepare(MetadataRepository repository, boolean dropExistingData) {
        delegate.prepare(repository, dropExistingData);
    }

    @Override
    public MetadataRepository getMetadataRepository() {
        return delegate.getMetadataRepository();
    }

    @Override
    public StorageResults fetch(Expression userQuery) {
        if (userQuery.cache()) {
            CacheValue cacheValue = cache.get(userQuery);
            if (cacheValue != null) {
                cacheValue.tokenCount.decrementAndGet();
                long timeSpentInCache = System.currentTimeMillis() - cacheValue.lastAccessTime;
                if (cacheValue.tokenCount.get() > 0 || timeSpentInCache < MAX_CACHE_LIFETIME) {
                    // Cache hit!
                    if (LOGGER.isDebugEnabled()) {
                        LOGGER.debug("Cache hit! Using value for '" + userQuery + "' (token left: " + cacheValue.tokenCount.get()
                                + ").");
                    }
                    cacheValue.lastAccessTime = System.currentTimeMillis();
                    return new CacheResults(cacheValue.results);
                } else {
                    // Cache hit! (but value expired in cache)
                    if (LOGGER.isDebugEnabled()) {
                        if (cacheValue.tokenCount.get() < 0) {
                            LOGGER.debug("Cache hit! Expired value for '" + userQuery + "' (results used too many times).");
                        } else {
                            LOGGER.debug("Cache hit! Expired value for '" + userQuery + "' (results too old).");
                        }
                    }
                }
            } else {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Cache miss! for query '" + userQuery + "'.");
                }
            }
            // New value in cache
            StorageResults results = delegate.fetch(userQuery);
            cacheValue = new CacheValue();
            final List<DataRecord> records = new ArrayList<DataRecord>(results.getCount());
            for (DataRecord result : results) {
                records.add(result);
            }
            cacheValue.results = records;
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("New cache value for '" + userQuery + "'.");
            }
            cacheValue.lastAccessTime = System.currentTimeMillis();
            cache.put(userQuery, cacheValue);
            return new CacheResults(records);
        } else {
            return delegate.fetch(userQuery);
        }
    }

    @Override
    public void update(DataRecord record) {
        delegate.update(record);
    }

    @Override
    public void update(Iterable<DataRecord> records) {
        delegate.update(records);
    }

    @Override
    public void delete(Expression userQuery) {
        delegate.delete(userQuery);
    }

    @Override
    public void delete(DataRecord record) {
        delegate.delete(record);
    }

    @Override
    public void close() {
        delegate.close();
    }

    @Override
    public void close(boolean dropExistingData) {
        delegate.close(dropExistingData);
    }

    @Override
    public void begin() {
        delegate.begin();
    }

    @Override
    public void commit() {
        delegate.commit();
    }

    @Override
    public void rollback() {
        delegate.rollback();
    }

    @Override
    public void end() {
        delegate.end();
    }

    @Override
    public void reindex() {
        delegate.reindex();
    }

    @Override
    public Set<String> getFullTextSuggestion(String keyword, FullTextSuggestion mode, int suggestionSize) {
        return delegate.getFullTextSuggestion(keyword, mode, suggestionSize);
    }

    @Override
    public String getName() {
        return delegate.getName();
    }

    @Override
    public DataSource getDataSource() {
        return delegate.getDataSource();
    }

    @Override
    public StorageType getType() {
        return delegate.getType();
    }

    @Override
    public ImpactAnalyzer getImpactAnalyzer() {
        return delegate.getImpactAnalyzer();
    }

    @Override
    public void adapt(Compare.DiffResults diffResults, boolean force) {
        delegate.adapt(diffResults, force);
    }

    private static class CacheResults implements StorageResults {

        private final List<DataRecord> records;

        public CacheResults(List<DataRecord> records) {
            this.records = records;
        }

        @Override
        public int getSize() {
            return records.size();
        }

        @Override
        public int getCount() {
            return records.size();
        }

        @Override
        public void close() {
        }

        @Override
        public Iterator<DataRecord> iterator() {
            return records.iterator();
        }
    }
}
