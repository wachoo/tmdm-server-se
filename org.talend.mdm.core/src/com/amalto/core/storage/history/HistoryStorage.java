/*
 * Copyright (C) 2006-2016 Talend Inc. - www.talend.com
 * 
 * This source code is available under agreement available at
 * %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
 * 
 * You should have received a copy of the agreement along with this program; if not, write to Talend SA 9 rue Pages
 * 92150 Suresnes, France
 */

package com.amalto.core.storage.history;

import java.util.Set;

import com.amalto.core.server.ServerContext;
import com.amalto.core.server.ServerLifecycle;
import com.amalto.core.storage.CachedResults;
import org.apache.log4j.Logger;
import org.talend.mdm.commmon.metadata.MetadataRepository;
import org.talend.mdm.commmon.metadata.compare.ImpactAnalyzer;

import com.amalto.core.history.DocumentHistory;
import com.amalto.core.query.user.At;
import com.amalto.core.query.user.Expression;
import com.amalto.core.query.user.Select;
import com.amalto.core.storage.Storage;
import com.amalto.core.storage.StorageResults;
import com.amalto.core.storage.StorageType;
import com.amalto.core.storage.datasource.DataSource;
import com.amalto.core.storage.datasource.DataSourceDefinition;
import com.amalto.core.storage.record.DataRecord;
import com.amalto.core.storage.transaction.StorageTransaction;

public class HistoryStorage implements Storage {

    protected static final Logger LOGGER = Logger.getLogger(HistoryStorage.class);

    private final Storage delegate;

    private final DocumentHistory documentHistory;

    public HistoryStorage(Storage dataStorage, DocumentHistory documentHistory) {
        this.delegate = dataStorage;
        this.documentHistory = documentHistory;
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
        if (userQuery instanceof Select) {
            Select select = (Select) userQuery;
            At selectHistory = select.getHistory();
            if (selectHistory != null) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Using history browsing extensions.");
                }
                // Build static analysis of query to understand what data is needed
                HistoryNode node = userQuery.accept(new HistoryNodeBuilder());
                // Fetch all necessary info for building history of records
                node.evaluate(delegate, documentHistory, selectHistory);
                ServerLifecycle lifecycle = ServerContext.INSTANCE.getLifecycle();
                Storage storage = lifecycle.createTemporaryStorage(delegate.getDataSource(), delegate.getType());
                storage.prepare(getMetadataRepository(), true);
                try {
                    // Fill a in-memory storage with data in nodes...
                    storage.begin();
                    node.fill(storage);
                    storage.commit();
                    // ...then perform original query on in-memory storage.
                    storage.begin();
                    StorageResults results = CachedResults.from(storage.fetch(userQuery));
                    storage.commit();
                    return results;
                } catch (Exception e) {
                    storage.rollback();
                    storage.close();
                    throw new RuntimeException("Could not fill temporary storage for query.", e);
                } finally {
                    storage.close();
                }
            }
        }
        // Fall back (in case query doesn't require any history browsing).
        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace("No history browsing extension in query, delegate to data storage.");
        }
        return delegate.fetch(userQuery);
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
    public void adapt(MetadataRepository newRepository, boolean force) {
        delegate.adapt(newRepository, force);
    }

    @Override
    public boolean isClosed() {
        return delegate.isClosed();
    }

}
