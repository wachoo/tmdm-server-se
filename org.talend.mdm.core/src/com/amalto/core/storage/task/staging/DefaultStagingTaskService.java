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

import java.util.*;

import static com.amalto.core.query.user.UserQueryBuilder.*;

public class DefaultStagingTaskService implements StagingTaskServiceDelegate {

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
        Storage storage = server.getStorageAdmin().get(dataContainer + "#STAGING");
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
            if (runningTasks.get(dataContainer + dataModel) != null) {
                throw new IllegalStateException("A validation task is already running.");
            }
            Server server = ServerContext.INSTANCE.get();
            Storage staging = server.getStorageAdmin().get(dataContainer + "#STAGING");
            if (staging == null) {
                throw new IllegalStateException("No staging storage available for container '" + dataContainer + "'.");
            }
            Storage user = server.getStorageAdmin().get(dataContainer);
            if (user == null) {
                throw new IllegalStateException("No staging storage available for container '" + dataContainer + "'.");
            }
            MetadataRepository stagingRepository = server.getMetadataRepositoryAdmin().get(dataModel + "#STAGING");
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
            TaskSubmitter.getInstance().submit(stagingTask);
            runningTasks.put(dataContainer + dataModel, stagingTask);
            return newTaskUUID;
        }
    }

    public List<String> listCompletedExecutions(String dataContainer, Date beforeDate, int start, int size) {
        Server server = ServerContext.INSTANCE.get();
        Storage staging = server.getStorageAdmin().get(dataContainer + "#STAGING");
        if (staging == null) {
            throw new IllegalStateException("No staging storage available for container '" + dataContainer + "'.");
        }
        // TODO Data container is not equals to data model name (except for staging?).
        MetadataRepository stagingRepository = server.getMetadataRepositoryAdmin().get(dataContainer + "#STAGING");
        if (stagingRepository == null) {
            throw new IllegalStateException("No staging metadata available for data model '" + dataContainer + "'.");
        }
        ComplexTypeMetadata executionType = stagingRepository.getComplexType("TALEND_TASK_EXECUTION");
        UserQueryBuilder qb = from(executionType)
                .select(executionType.getField("id"))
                .where(eq(executionType.getField("completed"), "true"));
        if (beforeDate != null) {
            qb.where(lt(executionType.getField("start_date"), beforeDate.toString()));
        }
        if (start > 0) {
            qb.start(start);
        }
        if (size > 0) {
            qb.limit(size);
        }
        List<String> taskIds;
        StorageResults results = staging.fetch(qb.getSelect());
        try {
            taskIds = new ArrayList<String>(size);
            for (DataRecord result : results) {
                taskIds.add(String.valueOf(result.get("id")));
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
                throw new IllegalArgumentException("No running task"); // TODO really (think about UI)?
            }
            ExecutionStatistics status = new ExecutionStatistics();
            status.setId(task.getId());
            status.setProcessedRecords(0); // TODO
            // TODO Current performance
            status.setTotalRecords(0); // TODO
            status.setStartDate(new Date(0)); // TODO
            status.setRunningTime("0m"); // TODO
            status.setEndDate(new Date(1000)); // TODO
            return status;
        }
    }

    public void cancelCurrentExecution(String dataContainer, String dataModel) {
        synchronized (runningTasks) {
            Task task = runningTasks.get(dataContainer + dataModel);
            if (task == null) {
                throw new IllegalArgumentException("No running task"); // TODO really? (think about UI)?
            }
            task.cancel();
            runningTasks.remove(dataContainer + dataModel);
        }
    }

    public ExecutionStatistics getExecutionStats(String dataContainer, String dataModel, String executionId) {
        Server server = ServerContext.INSTANCE.get();
        Storage staging = server.getStorageAdmin().get(dataContainer + "#STAGING");
        if (staging == null) {
            throw new IllegalStateException("No staging storage available for container '" + dataContainer + "'.");
        }
        MetadataRepository stagingRepository = server.getMetadataRepositoryAdmin().get(dataModel + "#STAGING");
        if (stagingRepository == null) {
            throw new IllegalStateException("No staging metadata available for data model '" + dataModel + "'.");
        }

        ComplexTypeMetadata executionType = stagingRepository.getComplexType("TALEND_TASK_EXECUTION");
        UserQueryBuilder qb = from(executionType)
                .where(eq(executionType.getField("id"), executionId));
        StorageResults results = staging.fetch(qb.getSelect());
        ExecutionStatistics status = new ExecutionStatistics();
        try {
            for (DataRecord result : results) {
                status.setId(String.valueOf(result.get("id")));
                // TODO Other fields.
            }
        } finally {
            results.close();
        }
        return status;
    }

    private static int countAllInstancesByStatus(Storage storage, MetadataRepository repository) {
        int totalCount = 0;
        for (ComplexTypeMetadata currentType : repository.getUserComplexTypes()) {
            UserQueryBuilder qb = from(currentType)
                    .select(alias(UserQueryBuilder.count(), "count"));
            StorageResults results = storage.fetch(qb.getSelect());
            try {
                for (DataRecord result : results) {
                    totalCount += (Integer) result.get("count");
                }
            } finally {
                results.close();
            }
        }
        return totalCount;
    }

    private static int countInstancesByStatus(Storage storage, MetadataRepository repository, String status) {
        int totalCount = 0;
        for (ComplexTypeMetadata currentType : repository.getUserComplexTypes()) {
            UserQueryBuilder qb = from(currentType)
                    .select(alias(UserQueryBuilder.count(), "count"))
                    .where(eq(UserStagingQueryBuilder.status(), status));
            StorageResults results = storage.fetch(qb.getSelect());
            try {
                for (DataRecord result : results) {
                    totalCount += (Integer) result.get("count");
                }
            } finally {
                results.close();
            }
        }
        return totalCount;
    }

    private static int countInstancesByStatus(Storage storage, MetadataRepository repository, boolean valid) {
        int totalCount = 0;
        for (ComplexTypeMetadata currentType : repository.getUserComplexTypes()) {
            UserQueryBuilder qb = from(currentType)
                    .select(alias(UserQueryBuilder.count(), "count"));
            if (valid) {
                qb.where(gte(UserStagingQueryBuilder.status(), "200"))
                        .where(lt(UserStagingQueryBuilder.status(), "400"));
            } else {
                qb.where(gte(UserStagingQueryBuilder.status(), "400"));
            }

            StorageResults results = storage.fetch(qb.getSelect());
            try {
                for (DataRecord result : results) {
                    totalCount += (Integer) result.get("count");
                }
            } finally {
                results.close();
            }
        }
        return totalCount;
    }
}
