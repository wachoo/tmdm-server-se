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

package com.amalto.core.storage.task.staging;

import com.amalto.core.metadata.ComplexTypeMetadata;
import com.amalto.core.metadata.MetadataRepository;
import com.amalto.core.query.user.UserQueryBuilder;
import com.amalto.core.query.user.UserStagingQueryBuilder;
import com.amalto.core.save.DefaultCommitter;
import com.amalto.core.save.context.DefaultSaverSource;
import com.amalto.core.server.Server;
import com.amalto.core.server.ServerContext;
import com.amalto.core.storage.Storage;
import com.amalto.core.storage.StorageResults;
import com.amalto.core.storage.record.DataRecord;
import com.amalto.core.storage.task.*;

import java.math.BigDecimal;
import java.util.*;

import static com.amalto.core.query.user.UserQueryBuilder.*;

public class DefaultStagingTaskService implements StagingTaskServiceDelegate {

    public static final String EXECUTION_LOG_TYPE = "TALEND_TASK_EXECUTION"; //$NON-NLS-1$

    private final Map<String, Task> runningTasks = new HashMap<String, Task>();

    public StagingContainerSummary getContainerSummary() {
        String dataContainer = ""; // TODO
        String dataModel = ""; // TODO
        return getContainerSummary(dataContainer, dataModel);
    }

    public String startValidation() {
        String dataContainer = ""; // TODO
        String dataModel = ""; // TODO
        return startValidation(dataContainer, dataModel);
    }

    public StagingContainerSummary getContainerSummary(String dataContainer, String dataModel) {
        Server server = ServerContext.INSTANCE.get();
        Storage storage = server.getStorageAdmin().get(dataContainer + "#STAGING"); //$NON-NLS-1$
        if (storage == null) {
            throw new IllegalStateException("No staging storage available for container '" + dataContainer + "'.");
        }
        MetadataRepository repository = server.getMetadataRepositoryAdmin().get(dataModel);
        if (repository == null) {
            throw new IllegalStateException("No metadata available for data model '" + dataModel + "'.");
        }

        int allRecords = countAllInstancesByStatus(storage, repository);
        int newRecords = countInstancesByStatus(storage, repository, StagingConstants.NEW);
        int validRecords = countInstancesByStatus(storage, repository, true);
        int invalidRecords = countInstancesByStatus(storage, repository, false);

        StagingContainerSummary containerStagingSummary = new StagingContainerSummary();
        containerStagingSummary.setTotalRecord(allRecords);
        containerStagingSummary.setWaitingForValidation(newRecords);
        containerStagingSummary.setValidRecords(validRecords);
        containerStagingSummary.setInvalidRecords(invalidRecords);

        return containerStagingSummary;
    }

    public String startValidation(String dataContainer, String dataModel) {
        synchronized (runningTasks) {
            Task runningTask = runningTasks.get(dataContainer + dataModel);
            if (runningTask != null) {
                try {
                    runningTask.waitForCompletion();
                    runningTasks.remove(dataContainer + dataModel);
                } catch (InterruptedException e) {
                    throw new IllegalStateException("A validation task is already running.", e);
                }
            }
            Server server = ServerContext.INSTANCE.get();
            Storage staging = server.getStorageAdmin().get(dataContainer + "#STAGING"); //$NON-NLS-1$
            if (staging == null) {
                throw new IllegalStateException("No staging storage available for container '" + dataContainer + "'.");
            }
            Storage user = server.getStorageAdmin().get(dataContainer);
            if (user == null) {
                throw new IllegalStateException("No staging storage available for container '" + dataContainer + "'.");
            }
            MetadataRepository stagingRepository = server.getMetadataRepositoryAdmin().get(dataModel + "#STAGING"); //$NON-NLS-1$
            if (stagingRepository == null) {
                throw new IllegalStateException("No staging metadata available for data model '" + dataModel + "'.");
            }
            MetadataRepository userRepository = server.getMetadataRepositoryAdmin().get(dataModel);
            if (userRepository == null) {
                throw new IllegalStateException("No user metadata available for data model '" + dataModel + "'.");
            }
            String newTaskUUID = UUID.randomUUID().toString();
            StagingConfiguration stagingConfig = new StagingConfiguration(staging,
                    stagingRepository,
                    userRepository,
                    new DefaultSaverSource(),
                    new DefaultCommitter(),
                    user);
            Task stagingTask = TaskFactory.createStagingTask(stagingConfig);
            TaskSubmitterFactory.getSubmitter().submit(stagingTask);
            runningTasks.put(dataContainer + dataModel, stagingTask);
            return newTaskUUID;
        }
    }

    public List<String> listCompletedExecutions(String dataContainer, Date beforeDate, int start, int size) {
        Server server = ServerContext.INSTANCE.get();
        Storage staging = server.getStorageAdmin().get(dataContainer + "#STAGING"); //$NON-NLS-1$
        if (staging == null) {
            throw new IllegalStateException("No staging storage available for container '" + dataContainer + "'.");
        }
        // TODO Data container is not equals to data model name (except for staging?).
        MetadataRepository stagingRepository = server.getMetadataRepositoryAdmin().get(dataContainer + "#STAGING"); //$NON-NLS-1$
        if (stagingRepository == null) {
            throw new IllegalStateException("No staging metadata available for data model '" + dataContainer + "'.");
        }
        ComplexTypeMetadata executionType = stagingRepository.getComplexType(EXECUTION_LOG_TYPE);
        UserQueryBuilder qb = from(executionType)
                .select(executionType.getField("id")) //$NON-NLS-1$
                .where(eq(executionType.getField("completed"), "true")); //$NON-NLS-1$ //$NON-NLS-2$
        if (beforeDate != null) {
            qb.where(lt(executionType.getField("start_date"), beforeDate.toString())); //$NON-NLS-1$
        }
        if (start > 0) {
            qb.start(start);
        }
        if (size > 1) {
            qb.limit(size);
        }
        List<String> taskIds;
        StorageResults results = staging.fetch(qb.getSelect());
        try {
            taskIds = new ArrayList<String>(size);
            for (DataRecord result : results) {
                taskIds.add(String.valueOf(result.get("id"))); //$NON-NLS-1$
            }
        } finally {
            results.close();
        }
        return taskIds;
    }

    public ExecutionStatistics getCurrentExecutionStats(String dataContainer, String dataModel) {
        synchronized (runningTasks) {
            Task task = runningTasks.get(dataContainer + dataModel);
            if (task == null) {
                return null; // This is a way to say "no current running validation task".
            }
            ExecutionStatistics status = new ExecutionStatistics();
            status.setId(task.getId());
            status.setProcessedRecords(task.getProcessedRecords());
            status.setTotalRecords(task.getRecordCount());
            status.setStartDate(new Date(task.getStartDate()));
            long elapsedTime = System.currentTimeMillis() - task.getStartDate();
            String formattedElapsedTime = String.format("%d:%02d:%02d", elapsedTime / 3600, (elapsedTime % 3600) / 60, (elapsedTime % 60)); //$NON-NLS-1$
            status.setRunningTime(formattedElapsedTime);
            long timeLeft = (long) ((task.getRecordCount() - task.getProcessedRecords()) / task.getPerformance());
            long endTime = (System.currentTimeMillis() + timeLeft);
            status.setEndDate(new Date(endTime));
            return status;
        }
    }

    public void cancelCurrentExecution(String dataContainer, String dataModel) {
        synchronized (runningTasks) {
            Task task = runningTasks.get(dataContainer + dataModel);
            if (task == null) {
                return; // No running task, simply ignore call.
            }
            task.cancel();
            runningTasks.remove(dataContainer + dataModel);
        }
    }

    public ExecutionStatistics getExecutionStats(String dataContainer, String dataModel, String executionId) {
        Server server = ServerContext.INSTANCE.get();
        Storage staging = server.getStorageAdmin().get(dataContainer + "#STAGING"); //$NON-NLS-1$
        if (staging == null) {
            throw new IllegalStateException("No staging storage available for container '" + dataContainer + "'.");
        }
        // TODO Data container is not equals to data model name (except for staging?) -> this is an issue from web ui.
        MetadataRepository stagingRepository = server.getMetadataRepositoryAdmin().get(dataContainer + "#STAGING"); //$NON-NLS-1$
        if (stagingRepository == null) {
            throw new IllegalStateException("No staging metadata available for data model '" + dataModel + "'.");
        }

        ComplexTypeMetadata executionType = stagingRepository.getComplexType(EXECUTION_LOG_TYPE);
        UserQueryBuilder qb = from(executionType)
                .where(eq(executionType.getField("id"), executionId)); //$NON-NLS-1$
        StorageResults results = staging.fetch(qb.getSelect());
        ExecutionStatistics status = new ExecutionStatistics();
        try {

            for (DataRecord result : results) {
                status.setId(String.valueOf(result.get("id"))); //$NON-NLS-1$
                status.setStartDate(((Date) result.get("start_time")));
                status.setEndDate(((Date) result.get("end_time")));
                status.setInvalidRecords(1);
                status.setProcessedRecords(1);
                status.setRunningTime("1h");
                status.setTotalRecords(((BigDecimal) result.get("record_count")).doubleValue());
            }
        } finally {
            results.close();
        }
        return status;
    }

    private static int countAllInstancesByStatus(Storage storage, MetadataRepository repository) {
        int totalCount = 0;
        for (ComplexTypeMetadata currentType : repository.getUserComplexTypes()) {
            if (currentType.isInstantiable()) {
                UserQueryBuilder qb = from(currentType)
                        .select(alias(UserQueryBuilder.count(), "count")); //$NON-NLS-1$
                StorageResults results = storage.fetch(qb.getSelect());
                try {
                    for (DataRecord result : results) {
                        totalCount += (Long) result.get("count"); //$NON-NLS-1$
                    }
                } finally {
                    results.close();
                }
            }
        }
        return totalCount;
    }

    private static int countInstancesByStatus(Storage storage, MetadataRepository repository, String status) {
        int totalCount = 0;
        for (ComplexTypeMetadata currentType : repository.getUserComplexTypes()) {
            if (currentType.isInstantiable()) {
                UserQueryBuilder qb = from(currentType)
                        .select(alias(UserQueryBuilder.count(), "count")); //$NON-NLS-1$
                if (StagingConstants.NEW.equals(status)) {
                    qb.where(or(eq(UserStagingQueryBuilder.status(), status), isNull(UserStagingQueryBuilder.status())));
                } else {
                    qb.where(eq(UserStagingQueryBuilder.status(), status));
                }
                StorageResults results = storage.fetch(qb.getSelect());
                try {
                    for (DataRecord result : results) {
                        totalCount += (Long) result.get("count"); //$NON-NLS-1$
                    }
                } finally {
                    results.close();
                }
            }
        }
        return totalCount;
    }

    private static int countInstancesByStatus(Storage storage, MetadataRepository repository, boolean valid) {
        int totalCount = 0;
        for (ComplexTypeMetadata currentType : repository.getUserComplexTypes()) {
            if (currentType.isInstantiable()) {
                UserQueryBuilder qb = from(currentType)
                        .select(alias(UserQueryBuilder.count(), "count")); //$NON-NLS-1$
                if (valid) {
                    qb.where(eq(UserStagingQueryBuilder.status(), StagingConstants.SUCCESS_VALIDATE));
                } else {
                    qb.where(gte(UserStagingQueryBuilder.status(), StagingConstants.FAIL));
                }

                StorageResults results = storage.fetch(qb.getSelect());
                try {
                    for (DataRecord result : results) {
                        totalCount += (Long) result.get("count"); //$NON-NLS-1$
                    }
                } finally {
                    results.close();
                }
            }
        }
        return totalCount;
    }
}
