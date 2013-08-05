/*
 * Copyright (C) 2006-2013 Talend Inc. - www.talend.com
 *
 * This source code is available under agreement available at
 * %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
 *
 * You should have received a copy of the agreement
 * along with this program; if not, write to Talend SA
 * 9 rue Pages 92150 Suresnes, France
 */

package com.amalto.core.storage.task;

import com.amalto.core.history.Action;
import com.amalto.core.metadata.MetadataUtils;
import com.amalto.core.query.user.Condition;
import com.amalto.core.query.user.Select;
import com.amalto.core.query.user.UserQueryBuilder;
import com.amalto.core.save.context.StorageDocument;
import com.amalto.core.save.context.UpdateActionCreator;
import com.amalto.core.server.ServerContext;
import com.amalto.core.storage.Storage;
import com.amalto.core.storage.StorageResults;
import com.amalto.core.storage.record.DataRecord;
import com.amalto.core.storage.transaction.Transaction;
import com.amalto.core.storage.transaction.TransactionManager;
import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.iterators.TransformIterator;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.talend.dataquality.matchmerge.*;
import org.talend.dataquality.matchmerge.mfb.MFB;
import org.talend.mdm.commmon.metadata.ComplexTypeMetadata;
import org.talend.mdm.commmon.metadata.FieldMetadata;
import org.talend.mdm.commmon.metadata.MetadataRepository;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.amalto.core.query.user.UserQueryBuilder.*;
import static com.amalto.core.query.user.UserStagingQueryBuilder.status;

public class MatchMergeTask implements Task {

    private static final Logger LOGGER = Logger.getLogger(MatchMergeTask.class);

    private final AtomicBoolean startLock = new AtomicBoolean();

    private final MetadataRepository repository;

    private final AtomicBoolean executionLock = new AtomicBoolean();

    private final String id = UUID.randomUUID().toString();

    private final Object currentTypeTaskMonitor = new Object();

    final ClosureExecutionStats stats;

    private long startTime;

    private long endTime = -1;

    private boolean isCancelled = false;

    private boolean isFinished;

    private final Storage storage;

    private final MatchMergeConfiguration configuration;

    private int recordsCount;

    MatchMergeTask(Storage storage,
                   MetadataRepository repository,
                   ClosureExecutionStats stats) {
        this.storage = storage;
        this.repository = repository;
        this.stats = stats;
        configuration = new DefaultMatchMergeConfiguration();
    }


    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        try {
            run();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void run() {
        synchronized (startLock) {
            startLock.set(true);
            startLock.notifyAll();
        }
        TransactionManager transactionManager = ServerContext.INSTANCE.get().getTransactionManager();
        Transaction transaction = transactionManager.create(Transaction.Lifetime.LONG);
        try {
            List<ComplexTypeMetadata> types = MetadataUtils.sortTypes(repository);
            startTime = System.currentTimeMillis();
            for (ComplexTypeMetadata type : types) {
                if (type.isInstantiable() && processType(type) && configuration.include(type)) {
                    configuration.check(type); // performs some asserts on the type.
                    UserQueryBuilder typeQueryBuilder = from(type).where(or(or(eq(status(), "0"), eq(status(), StagingConstants.SUCCESS_MERGED_RECORD)), isNull(status())));
                    Collection<Select> blockQueries = configuration.getBlocks(type, typeQueryBuilder.getSelect().copy());
                    int typeRecordCount = 0;
                    List<Record> matchMergeResult = Collections.emptyList();
                    final List<FieldMetadata> matchFields = configuration.getMatchFields(type);
                    for (Select blockQuery : blockQueries) {
                        storage.begin();
                        StorageResults records = storage.fetch(blockQuery); // Expects an active transaction here
                        try {
                            int count = records.getCount();
                            recordsCount += count;
                            typeRecordCount += count;
                            Iterator<DataRecord> iterator = records.iterator();
                            // Read configuration
                            for (FieldMetadata matchField : matchFields) {
                                if (!matchField.getContainingType().equals(type)) {
                                    throw new IllegalArgumentException("Field '" + matchField.getName() + "' is not a direct element of type '" + type.getName() + "'.");
                                }
                                if (matchField.isMany()) {
                                    throw new IllegalArgumentException("Field '" + matchField.getName() + "' is a repeatable element.");
                                }
                            }
                            MatchAlgorithm[] matchAlgorithm = configuration.getMatchAlgorithms(matchFields);
                            float[] thresholds = configuration.getThresholds(matchFields);
                            MergeAlgorithm[] merges = configuration.getMergeAlgorithms(matchFields);
                            // Adapt results for the match&merge algorithm
                            TransformIterator matchMergeInput = new TransformIterator(iterator, new Transformer() {
                                @Override
                                public Object transform(Object input) {
                                    DataRecord dataRecord = (DataRecord) input;
                                    String keyValue = String.valueOf(dataRecord.get(dataRecord.getType().getKeyFields().iterator().next())); // TODO Compound key
                                    Record transformed = new Record(keyValue);
                                    for (FieldMetadata matchField : matchFields) {
                                        transformed.getAttributes().add(new Attribute(matchField.getName(), String.valueOf(dataRecord.get(matchField))));
                                    }
                                    return transformed;
                                }
                            });
                            // Dump configuration (debug mode)
                            if (LOGGER.isInfoEnabled()) {
                                LOGGER.info("-----------------");
                                LOGGER.info("Match & merge configuration for type '" + type.getName() + "':");
                                for (FieldMetadata matchField : matchFields) {
                                    LOGGER.info("\tField: " + matchField.getName());
                                }
                                LOGGER.info("-----------------");
                            }
                            // Run the match&merge algorithm
                            MatchMergeAlgorithm algorithm = new MFB(matchAlgorithm, thresholds, merges);
                            matchMergeResult = algorithm.execute(matchMergeInput);
                        } finally {
                            records.close();
                        }
                        LOGGER.info("Matched & merge: " + typeRecordCount + " record(s) -> " + matchMergeResult.size() + " record(s) after processing.");
                        // Update related record (i.e. from groups in staging area based on merge result).
                        if (LOGGER.isInfoEnabled()) {
                            LOGGER.info("Match details:");
                        }
                        for (Record record : matchMergeResult) {
                            if (LOGGER.isInfoEnabled()) {
                                LOGGER.info("-----------------");
                                LOGGER.info("Group id: " + record.getGroupId());
                            }
                            UserQueryBuilder qb = from(type);
                            int groupSize = record.getRelatedIds().size();
                            if (groupSize > 1) {
                                Condition condition = null;
                                for (String relatedIds : record.getRelatedIds()) {
                                    Condition currentEquals = eq(type.getKeyFields().iterator().next(), relatedIds);
                                    if (condition == null) {
                                        condition = currentEquals;
                                    } else {
                                        condition = or(condition, currentEquals);
                                    }
                                }
                                qb.where(condition);
                            } else { // Group of 1
                                qb.where(eq(type.getKeyFields().iterator().next(), record.getId()));
                            }
                            StorageResults relatedRecords = storage.fetch(qb.getSelect());
                            int i = 0;
                            DataRecord goldenRecord = null;
                            for (DataRecord relatedRecord : relatedRecords) {
                                if (LOGGER.isInfoEnabled()) {
                                    LOGGER.info("Record #" + i);
                                }
                                // Merge document with current golden record (if group bigger than 1).
                                if (groupSize > 1) {
                                    if(goldenRecord == null) {
                                        goldenRecord = relatedRecord;
                                        for (Attribute attribute : record.getAttributes()) {
                                            FieldMetadata field = goldenRecord.getType().getField(attribute.getLabel());
                                            goldenRecord.set(field, MetadataUtils.convert(attribute.getValue(), field));
                                        }
                                    } else {
                                        StorageDocument storageDocument = new StorageDocument(StringUtils.EMPTY, repository, goldenRecord);
                                        StorageDocument newDocument = new StorageDocument(StringUtils.EMPTY, repository, relatedRecord);
                                        UpdateActionCreator updateActionCreator = new UpdateActionCreator(storageDocument,
                                                newDocument,
                                                new Date(System.currentTimeMillis()),
                                                StringUtils.EMPTY,
                                                StringUtils.EMPTY,
                                                false, // No need for 'touch' action in this case (data record comparison does not need this).
                                                repository);
                                        List<Action> actions = goldenRecord.getType().accept(updateActionCreator);
                                        for (Action action : actions) {
                                            // TODO Skip actions performed on the merge fields
                                            action.perform(storageDocument);
                                        }
                                    }
                                } else {
                                    goldenRecord = relatedRecord; // For group of one, only related record is the golden one.
                                }
                                // Record status change
                                relatedRecord.getRecordMetadata().setTaskId(record.getGroupId());
                                relatedRecord.getRecordMetadata().getRecordProperties().put(Storage.METADATA_STAGING_STATUS, StagingConstants.SUCCESS_MERGE_CLUSTERS);
                                storage.update(relatedRecord);
                                // Logger merged record content
                                if (LOGGER.isInfoEnabled()) {
                                    StringBuilder builder = new StringBuilder();
                                    for (FieldMetadata field : matchFields) {
                                        builder.append('\t').append(field.getName()).append('=').append(relatedRecord.get(field)).append(' ');
                                    }
                                    LOGGER.info(builder.toString());
                                }
                                i++;
                            }
                            if (LOGGER.isInfoEnabled()) {
                                LOGGER.info("-----------------");
                            }
                            // Mark the merged golden record.
                            if (goldenRecord != null) {
                                goldenRecord.getRecordMetadata().setTaskId(record.getGroupId());
                                goldenRecord.getRecordMetadata().getRecordProperties().put(STAGING_STATUS_FIELD, StagingConstants.SUCCESS_MERGED_RECORD);
                                // Type for golden record is expected to have a single key field that can accept UUID values
                                // (it is an expected error to have failure here in case this isn't true).
                                FieldMetadata keyField = goldenRecord.getType().getKeyFields().iterator().next();
                                goldenRecord.set(keyField, UUID.randomUUID().toString());
                                storage.update(goldenRecord); // Golden record will go to master database in MDMValidationTask
                            } else {
                                throw new IllegalStateException("Expected a golden record to be built.");
                            }
                        }
                        storage.commit();
                    }
                }
            }
            endTime = System.currentTimeMillis();
        } finally {
            transaction.commit();
            synchronized (executionLock) {
                executionLock.set(true);
                executionLock.notifyAll();
            }
            isFinished = true;
        }
    }

    private static boolean processType(ComplexTypeMetadata type) {
        // Do not process UpdateReport type
        return !"Update".equals(type.getName()); //$NON-NLS-1$
    }

    public String getId() {
        return id;
    }

    @Override
    public int getRecordCount() {
        return recordsCount;
    }

    @Override
    public int getErrorCount() {
        return stats.getErrorCount();
    }

    @Override
    public int getProcessedRecords() {
        return stats.getErrorCount() + stats.getSuccessCount();
    }

    public double getPerformance() {
        if (getProcessedRecords() > 0) {
            float time;
            if (endTime > 0) {
                time = (endTime - startTime) / 1000f;
            } else {
                time = (System.currentTimeMillis() - startTime) / 1000f;
            }
            return getProcessedRecords() / time;
        } else {
            return 0;
        }
    }

    public void cancel() {
        synchronized (currentTypeTaskMonitor) {
            isCancelled = true;
        }
    }

    public void waitForCompletion() throws InterruptedException {
        synchronized (startLock) {
            while (!startLock.get()) {
                startLock.wait();
            }
        }
        synchronized (executionLock) {
            while (!executionLock.get()) {
                executionLock.wait();
            }
        }
    }

    @Override
    public long getStartDate() {
        return startTime;
    }

    @Override
    public boolean hasFinished() {
        return isCancelled || isFinished;
    }

    @Override
    public String toString() {
        return "MATCH & MERGE";
    }
}
