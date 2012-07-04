/*
 * Copyright (C) 2006-2012 Talend Inc. - www.talend.com
 *
 * This source code is available under agreement available at
 * %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
 *
 * You should have received a copy of the agreement
 * along with this program; if not, write to Talend SA
 * 9 rue Pages 92150 Suresnes, France
 */

package com.amalto.core.storage;

import com.amalto.core.metadata.FieldMetadata;
import com.amalto.core.metadata.MetadataRepository;
import com.amalto.core.query.user.Expression;
import com.amalto.core.storage.datasource.DataSource;
import com.amalto.core.storage.record.DataRecord;

import java.util.Set;

/**
 *
 */
public interface Storage {

    /**
     * TODO This is temporary value!
     * This value is used to limit current state of implementation: there's no current support for multiple Storage instances
     * in MDM (but unit tests test this behavior).
     */
    String DEFAULT_DATA_SOURCE_NAME = "RDBMS-1";  //$NON-NLS-1$

    String METADATA_TIMESTAMP = "x_talend_timestamp"; //$NON-NLS-1$

    String METADATA_TASK_ID = "x_talend_task_id"; //$NON-NLS-1$

    String METADATA_REVISION_ID = "x_talend_revision_id"; //$NON-NLS-1$

    String METADATA_STAGING_STATUS = "x_talend_staging_status"; //$NON-NLS-1$

    String METADATA_STAGING_ERROR = "x_talend_staging_error"; //$NON-NLS-1$

    /**
     * Early initialization (i.e. might create pools): performs all actions that do not need to know what kind of types
     * this storage should take care of (usually stateless components).
     *
     * @param dataSourceName The name of the <i>data source</i> to be used by the storage.
     */
    void init(String dataSourceName);

    /**
     * Prepare storage to handle types located in {@link MetadataRepository}.
     *
     * @param repository       A initialized {@link com.amalto.core.metadata.MetadataRepository} instance.
     * @param indexedFields    A {@link Set} of {@link FieldMetadata} that need to be indexed. It is up to the implementation
     *                         to decide whether this information should be used or not. Callers of this method expects
     *                         implementation to take all necessary actions to allow quick search on the fields in <code>indexedFields</code>.
     * @param force            <code>true</code> will force the storage to prepare event if {@link #prepare(com.amalto.core.metadata.MetadataRepository, boolean)}
     *                         has already been called. <code>false</code> will be a "no op" operation if storage is already
     *                         prepared.
     * @param dropExistingData if <code>true</code>, storage preparation will drop all data that may previously exist.
     *                         Use this parameter with caution since recovery is not supported.   @see {@link MetadataRepository#load(java.io.InputStream)}
     * @see #prepare(com.amalto.core.metadata.MetadataRepository, boolean)
     */
    void prepare(MetadataRepository repository, Set<FieldMetadata> indexedFields, boolean force, boolean dropExistingData);

    /**
     * Prepare storage to handle types located in {@link MetadataRepository}.
     *
     * @param repository       A initialized {@link com.amalto.core.metadata.MetadataRepository} instance.
     * @param dropExistingData if <code>true</code>, storage preparation will drop all data that may previously exist.
     *                         Use this parameter with caution since recovery is not supported.
     * @see {@link MetadataRepository#load(java.io.InputStream)}
     */
    void prepare(MetadataRepository repository, boolean dropExistingData);

    /**
     * Returns all records that match the {@link Expression}. The <code>expression</code> should be a valid {@link com.amalto.core.query.user.Select}.
     *
     * @param userQuery A {@link com.amalto.core.query.user.Select} instance.
     * @return A {@link Iterable} instance to navigate through query results. This iterable class also provides ways to get
     *         how many records are returned and how many matched query in database.
     * @see com.amalto.core.query.user.UserQueryBuilder
     */
    StorageResults fetch(Expression userQuery);

    /**
     * Updates storage with a new or existing record. Record might already exist, storage implementation (or underlying
     * storage framework) will decide whether this is new record or old one.
     *
     * @param record Record to be created or updated.
     */
    void update(DataRecord record);

    /**
     * Updates storage with new or existing records. Records might already exist, storage implementation (or underlying
     * storage framework) will decide whether this is all new records or old ones.
     *
     * @param records Records to be created or updated.
     */
    void update(Iterable<DataRecord> records);

    /**
     * Deletes all records that match the {@link Expression}. The <code>userQuery</code> should be a valid {@link com.amalto.core.query.user.Select}.
     *
     * @param userQuery A {@link com.amalto.core.query.user.Select} instance.
     * @see com.amalto.core.query.user.UserQueryBuilder
     */
    void delete(Expression userQuery);

    /**
     * Performs shutdown actions and clean up.
     */
    void close();

    /**
     * <p>
     * Starts a transaction for current thread. If a previous call to this method has been made without calling any end
     * of transaction method (e.g. {@link #commit()}), calling this method has no effect.
     * </p>
     *
     * @throws IllegalStateException If a transaction was already started for the current thread.
     * @see #commit()
     * @see #rollback()
     * @see #end()
     */
    void begin();

    /**
     * Commit changes done during transaction for current thread. {@link #begin()} must have previously been called.
     */
    void commit();

    /**
     * Rollback changes done during transaction for current thread. {@link #begin()} must have previously been called.
     */
    void rollback();

    /**
     * Free any resource allocated for a transaction. {@link #begin()} must have previously been called.
     */
    void end();

    /**
     * Re-index the whole database for full text indexing. This can be quite a high-cost operation (depending on the
     * database size). This is a blocking operation.
     */
    void reindex();

    /**
     * Returns suggested keywords (words that match result in full text index) for the <code>keyword</code>. Returned
     * results depend on {@link FullTextSuggestion}.
     *
     * @param keyword        A word to be used as input for this method (only one word).
     * @param mode           {@link FullTextSuggestion} suggestion mode.
     * @param suggestionSize Number of suggestions this method should return.
     * @return A {@link Set} of <code>suggestionSize</code> keywords that matches results in full text index.
     */
    Set<String> getFullTextSuggestion(String keyword, FullTextSuggestion mode, int suggestionSize);

    String getName();

    DataSource getDataSource();

    enum FullTextSuggestion {
        /**
         * Returns keyword suggestion that start with a given set of characters.
         */
        START,
        /**
         * Returns keyword suggestion that
         */
        ALTERNATE
    }
}
