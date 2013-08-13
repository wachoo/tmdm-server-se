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
import com.amalto.core.history.FieldAction;
import com.amalto.core.history.action.*;
import com.amalto.core.metadata.MetadataUtils;
import com.amalto.core.query.user.Select;
import com.amalto.core.query.user.UserQueryBuilder;
import com.amalto.core.save.context.StorageDocument;
import com.amalto.core.save.context.UpdateActionCreator;
import com.amalto.core.storage.Storage;
import com.amalto.core.storage.StorageResults;
import com.amalto.core.storage.record.DataRecord;
import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.iterators.TransformIterator;
import org.apache.commons.lang.NotImplementedException;
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
import static com.amalto.core.query.user.UserQueryBuilder.eq;
import static com.amalto.core.query.user.UserStagingQueryBuilder.status;

public class MatchMergeTask implements Task {

    private static final Logger LOGGER = Logger.getLogger(MatchMergeTask.class);

    private final AtomicBoolean startLock = new AtomicBoolean();

    private final MetadataRepository repository;

    private final AtomicBoolean executionLock = new AtomicBoolean();

    private final String id = UUID.randomUUID().toString();

    private final Object currentTypeTaskMonitor = new Object();

    private final ClosureExecutionStats stats;

    private final Storage storage;

    private final MatchMergeConfiguration configuration;

    private long startTime;

    private long endTime = -1;

    private boolean isCancelled = false;

    private boolean isFinished;

    private int recordsCount;

    MatchMergeTask(Storage storage,
                   MetadataRepository repository,
                   ClosureExecutionStats stats) {
        this.storage = storage;
        this.repository = repository;
        this.stats = stats;
        this.configuration = new DefaultMatchMergeConfiguration();
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
        try {
            List<ComplexTypeMetadata> types = MetadataUtils.sortTypes(repository);
            startTime = System.currentTimeMillis();
            for (ComplexTypeMetadata type : types) {
                storage.begin();
                // TODO Comment
                UserQueryBuilder qb = from(type);
                StorageResults countResult = storage.fetch(qb.getSelect());
                long typeCount;
                try {
                    typeCount = countResult.getCount();
                } finally {
                    countResult.close();
                }
                recordsCount += typeCount;
                // TODO Comment
                if (typeCount > 0 && type.isInstantiable() && processType(type)) {
                    qb = from(type).where(
                            or(
                                    or(
                                            eq(status(), StagingConstants.NEW),
                                            isNull(status())
                                    ),
                                    eq(status(), StagingConstants.SUCCESS_MERGED_RECORD)
                            )
                    ).forUpdate();
                    if (configuration.include(type)) {
                        configuration.check(type); // performs some asserts on the type.
                        Collection<Select> blockQueries = configuration.getBlocks(type, qb.getSelect().copy());
                        int typeRecordCount = 0;
                        List<Record> matchMergeResult = Collections.emptyList();
                        final List<FieldMetadata> matchFields = configuration.getMatchFields(type);
                        if (LOGGER.isDebugEnabled()) {
                            LOGGER.debug("---- " + type.getName() + " ----");
                            LOGGER.debug("Configuration:");
                            for (FieldMetadata matchField : matchFields) {
                                LOGGER.debug("    Match field: " + matchField.getName());
                            }
                        }
                        for (Select blockQuery : blockQueries) { // TODO Implement block detection
                            final StorageResults records = storage.fetch(blockQuery); // Expects an active transaction here
                            try {
                                int count = records.getCount();
                                typeRecordCount += count;
                                final Iterator<DataRecord> iterator = records.iterator();
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
                                        String keyValue = String.valueOf(dataRecord.get(dataRecord.getType().getKeyFields().iterator().next()));
                                        Record transformed = new Record(keyValue);
                                        for (FieldMetadata matchField : matchFields) {
                                            transformed.getAttributes().add(new Attribute(matchField.getName(), String.valueOf(dataRecord.get(matchField))));
                                        }
                                        return transformed;
                                    }
                                });
                                // Run the match&merge algorithm
                                MatchMergeAlgorithm algorithm = new MFB(matchAlgorithm, thresholds, merges);
                                matchMergeResult = algorithm.execute(matchMergeInput);
                            } finally {
                                records.close();
                            }
                            if (LOGGER.isDebugEnabled()) {
                                LOGGER.debug("Matched & merge: " + typeRecordCount + " record(s) -> " + matchMergeResult.size() + " record(s) after processing.");
                            }
                            // Update related record (i.e. from groups in staging area based on merge result).
                            if (LOGGER.isDebugEnabled() && !matchMergeResult.isEmpty()) {
                                LOGGER.debug("Match details:");
                            }
                            for (Record record : matchMergeResult) {
                                String groupId = record.getGroupId();
                                if (groupId == null) {
                                    throw new IllegalStateException("Group id cannot be null.");
                                }
                                if (LOGGER.isDebugEnabled()) {
                                    LOGGER.debug("Group id: " + groupId + " (" + record.getRelatedIds().size() + " records)");
                                }
                                // Select related records
                                // Build golden record
                                int i = 0;
                                DataRecord goldenRecord = null;
                                if (record.getRelatedIds().size() > 1) {
                                    for (String currentId : record.getRelatedIds()) {
                                        qb = from(type).where(eq(type.getKeyFields().iterator().next(), currentId)).forUpdate();
                                        StorageResults relatedRecordResults = storage.fetch(qb.getSelect());
                                        DataRecord relatedRecord = relatedRecordResults.iterator().next();
                                        if (LOGGER.isDebugEnabled()) {
                                            LOGGER.debug("Record #" + i + " (id: " + relatedRecord.get(relatedRecord.getType().getKeyFields().iterator().next()) + ")");
                                        }
                                        // Merge document with current golden record (if group bigger than 1).
                                        if (goldenRecord == null) {
                                            goldenRecord = DataRecord.copy(relatedRecord);
                                            for (Attribute attribute : record.getAttributes()) {
                                                FieldMetadata field = goldenRecord.getType().getField(attribute.getLabel());
                                                goldenRecord.set(field, MetadataUtils.convert(attribute.getValue(), field));
                                            }
                                        } else {
                                            StorageDocument storageDocument = new StorageDocument(StringUtils.EMPTY, repository, goldenRecord);
                                            StorageDocument newDocument = new StorageDocument(StringUtils.EMPTY, repository, relatedRecord);
                                            UpdateActionCreator updateActionCreator = new UpdateActionCreator(storageDocument,
                                                    newDocument,
                                                    new Date(newDocument.getDataRecord().getRecordMetadata().getLastModificationTime()),
                                                    true,
                                                    StringUtils.EMPTY,
                                                    StringUtils.EMPTY,
                                                    false, // No need for 'touch' action in this case (data record comparison does not need this).
                                                    storage.getMetadataRepository()); // Important! Use staging metadata repository!
                                            List<Action> actions = goldenRecord.getType().accept(updateActionCreator);
                                            for (Action action : actions) {
                                                if (action instanceof FieldUpdateAction) {
                                                    FieldAction fieldAction = (FieldAction) action;
                                                    FieldMetadata field = fieldAction.getField();
                                                    if (!matchFields.contains(field)) { // Skip actions performed on the merge fields
                                                        MergeAlgorithm defaultMerge = configuration.getDefaultMergeAlgorithm(field);
                                                        switch (defaultMerge) {
                                                            case CONCAT:
                                                                if (!field.isMany()) {
                                                                    ConcatAction.concat((FieldUpdateAction) action).perform(storageDocument);
                                                                } else {
                                                                    action.perform(storageDocument);
                                                                }
                                                                break;
                                                            case MOST_RECENT:
                                                                long time = storageDocument.getDataRecord().getRecordMetadata().getLastModificationTime();
                                                                DateBasedAction.mostRecent(NoOpAction.instance(time), fieldAction).perform(storageDocument);
                                                                break;
                                                            case MOST_COMMON:
                                                                throw new NotImplementedException("Not (yet) supported merge: " + defaultMerge);
                                                            case MAX:
                                                                NumberActions.max((FieldUpdateAction) action).perform(storageDocument);
                                                                break;
                                                            case MIN:
                                                                NumberActions.min((FieldUpdateAction) action).perform(storageDocument);
                                                                break;
                                                            case MEAN:
                                                                NumberActions.mean((FieldUpdateAction) action).perform(storageDocument);
                                                                break;
                                                            case SUM:
                                                                NumberActions.sum((FieldUpdateAction) action).perform(storageDocument);
                                                                break;
                                                            case UNIFY:
                                                            case OLDEST_DATE:
                                                            case REPEATED_VALUES:
                                                                throw new NotImplementedException("Not supported default merge: " + defaultMerge);
                                                        }
                                                    }
                                                } else {
                                                    action.perform(storageDocument);
                                                }
                                            }
                                        }
                                        // Record status change
                                        relatedRecord.getRecordMetadata().setTaskId(groupId);
                                        relatedRecord.getRecordMetadata().getRecordProperties().put(Storage.METADATA_STAGING_STATUS, StagingConstants.SUCCESS_MERGE_CLUSTERS);
                                        storage.update(relatedRecord);
                                        // Logger merged record content
                                        if (LOGGER.isDebugEnabled()) {
                                            StringBuilder builder = new StringBuilder();
                                            for (FieldMetadata field : matchFields) {
                                                builder.append('\t').append(field.getName()).append('=').append(relatedRecord.get(field)).append(' ');
                                            }
                                            LOGGER.debug(builder.toString());
                                        }
                                        i++;
                                        if (i < record.getRelatedIds().size()) {
                                            stats.reportSuccess();
                                        }
                                    }
                                    if (LOGGER.isDebugEnabled()) {
                                        LOGGER.debug("-----------------");
                                    }
                                } else {
                                    // For group of one, only related record is the golden one.
                                    qb = from(type).where(eq(type.getKeyFields().iterator().next(), record.getId()));
                                    StorageResults relatedRecordResults = storage.fetch(qb.getSelect());
                                    goldenRecord = DataRecord.copy(relatedRecordResults.iterator().next());
                                }
                                // Mark the merged golden record.
                                if (goldenRecord != null) {
                                    goldenRecord.getRecordMetadata().setTaskId(groupId);
                                    goldenRecord.getRecordMetadata().getRecordProperties().put(Storage.METADATA_STAGING_STATUS, StagingConstants.SUCCESS_MERGED_RECORD);
                                    // Type for golden record is expected to have a single key field that can accept UUID values
                                    // (it is an expected error to have failure here in case this isn't true).
                                    FieldMetadata keyField = goldenRecord.getType().getKeyFields().iterator().next();
                                    goldenRecord.set(keyField, groupId);
                                    storage.update(goldenRecord); // Golden record will go to master database in MDMValidationTask
                                } else {
                                    throw new IllegalStateException("Expected a golden record to be built.");
                                }
                            }
                        }
                    }
                }
                storage.commit();
            }
            endTime = System.currentTimeMillis();
        } finally {
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
