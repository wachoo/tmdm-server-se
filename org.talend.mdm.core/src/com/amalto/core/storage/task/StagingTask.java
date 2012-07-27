package com.amalto.core.storage.task;

import com.amalto.core.metadata.ComplexTypeMetadata;
import com.amalto.core.metadata.MetadataRepository;
import com.amalto.core.save.SaverSession;
import com.amalto.core.save.context.SaverSource;
import com.amalto.core.storage.Storage;
import com.amalto.core.storage.record.DataRecord;
import com.amalto.core.storage.record.metadata.UnsupportedDataRecordMetadata;
import org.apache.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 *
 */
public class StagingTask implements Task {

    private static final Logger LOGGER = Logger.getLogger(StagingTask.class);

    private final TaskSubmitter taskSubmitter;

    private final Storage stagingStorage;

    private final String executionId;

    private boolean isCancelled = false;

    private final List<MetadataRepositoryTask> tasks;

    private final AtomicBoolean startLock = new AtomicBoolean();

    private final AtomicBoolean executionLock = new AtomicBoolean();

    private final Object currentTaskMonitor = new Object();

    private final ComplexTypeMetadata executionType;

    private final ComplexTypeMetadata definitionType;

    private final String taskDefinitionId;

    private MetadataRepositoryTask currentTask;

    private double recordCount;

    private long startTime;

    private long endTime;

    public StagingTask(TaskSubmitter taskSubmitter,
                       Storage stagingStorage,
                       MetadataRepository stagingRepository,
                       MetadataRepository userRepository,
                       SaverSource source,
                       SaverSession.Committer committer,
                       Storage destinationStorage,
                       String taskDefinitionId) {
        this.taskSubmitter = taskSubmitter;
        this.stagingStorage = stagingStorage;
        this.executionId = UUID.randomUUID().toString();
        this.taskDefinitionId = taskDefinitionId;
        this.executionType = stagingRepository.getComplexType("TALEND_TASK_EXECUTION");
        this.definitionType = stagingRepository.getComplexType("TALEND_TASK_DEFINITION");
        tasks = Arrays.asList(new ClusterTask(stagingStorage, userRepository),
                new MergeTask(stagingStorage, userRepository),
                new MDMValidationTask(stagingStorage, destinationStorage, userRepository, source, committer));
        // new DSCUpdaterTask(stagingStorage, destinationStorage, userRepository));
    }

    public String getId() {
        return executionId;
    }

    public double getRecordCount() {
        return recordCount;
    }

    public double getCurrentPerformance() {
        return 0;
    }

    public double getMinPerformance() {
        return 0;
    }

    public double getMaxPerformance() {
        return 0;
    }

    public void cancel() {
        synchronized (currentTaskMonitor) {
            isCancelled = true;
            if (currentTask != null) {
                currentTask.cancel();
            }
        }
    }

    public void waitForCompletion() throws InterruptedException {
        while (!startLock.get()) {
            synchronized (startLock) {
                startLock.wait();
            }
        }
        while (!executionLock.get()) {
            synchronized (executionLock) {
                executionLock.wait();
            }
        }
    }

    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        synchronized (startLock) {
            startLock.set(true);
            startLock.notifyAll();
        }

        try {
            if (definitionType == null) {
                throw new IllegalStateException("Can not find internal type information for task definition.");
            }
            if (executionType == null) {
                throw new IllegalStateException("Can not find internal type information for execution logging.");
            }
            // Start recording the execution
            DataRecord execution = new DataRecord(executionType, UnsupportedDataRecordMetadata.INSTANCE);
            stagingStorage.begin();
            {
                execution.set(executionType.getField("id"), executionId);
                DataRecord taskDefinition = new DataRecord(definitionType, UnsupportedDataRecordMetadata.INSTANCE);
                taskDefinition.set(definitionType.getField("id"), taskDefinitionId);
                execution.set(executionType.getField("task_id"), taskDefinition);
                startTime = System.currentTimeMillis();
                execution.set(executionType.getField("start_time"), new Timestamp(startTime));
                stagingStorage.update(execution);
            }
            stagingStorage.commit();

            recordCount = 0;
            for (MetadataRepositoryTask task : tasks) {
                synchronized (currentTaskMonitor) {
                    if (isCancelled) {
                        break;
                    }
                    currentTask = task;
                }
                LOGGER.info("--> " + task.toString());
                taskSubmitter.submitAndWait(currentTask);
                recordCount = Math.max(currentTask.getRecordCount(), recordCount);
                LOGGER.info("<-- DONE " + task.toString());
            }

            // Execution recording end.
            stagingStorage.begin();
            {
                endTime = System.currentTimeMillis();
                execution.set(executionType.getField("end_time"), new Timestamp(endTime));
                execution.set(executionType.getField("record_count"), new BigDecimal(getRecordCount()));
                stagingStorage.update(execution);
            }
            stagingStorage.commit();
        } finally {
            synchronized (executionLock) {
                executionLock.set(true);
                executionLock.notifyAll();
            }
        }
    }
}
