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

    private MetadataRepositoryTask currentTask;

    private int recordCount;

    private int processedRecordCount;

    private long startTime;

    private long endTime;

    public StagingTask(TaskSubmitter taskSubmitter,
                       Storage stagingStorage,
                       MetadataRepository stagingRepository,
                       MetadataRepository userRepository,
                       SaverSource source,
                       SaverSession.Committer committer,
                       Storage destinationStorage) {
        this.taskSubmitter = taskSubmitter;
        this.stagingStorage = stagingStorage;
        this.executionId = UUID.randomUUID().toString();
        this.executionType = stagingRepository.getComplexType("TALEND_TASK_EXECUTION");
        tasks = Arrays.asList(new ClusterTask(stagingStorage, userRepository),
                new MergeTask(stagingStorage, userRepository),
                new MDMValidationTask(stagingStorage, destinationStorage, userRepository, source, committer));
        // new DSCUpdaterTask(stagingStorage, destinationStorage, userRepository));
    }

    public String getId() {
        return executionId;
    }

    public int getRecordCount() {
        return recordCount;
    }

    public double getPerformance() {
        return (processedRecordCount * tasks.size()) / ((System.currentTimeMillis() - startTime) / 1000f);
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

    @Override
    public long getStartDate() {
        return startTime;
    }

    @Override
    public int getProcessedRecords() {
        return processedRecordCount;
    }

    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        try {
            run();
        } catch (Exception e) {
            throw new JobExecutionException(e);
        }
    }

    @Override
    public void run() {
        synchronized (startLock) {
            startLock.set(true);
            startLock.notifyAll();
        }

        try {
            if (executionType == null) {
                throw new IllegalStateException("Can not find internal type information for execution logging.");
            }
            // Start recording the execution
            DataRecord execution = new DataRecord(executionType, UnsupportedDataRecordMetadata.INSTANCE);
            stagingStorage.begin();
            {
                execution.set(executionType.getField("id"), executionId);
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
                processedRecordCount = Math.max(currentTask.getProcessedRecords(), processedRecordCount);
                LOGGER.info("<-- DONE " + task.toString());
            }

            // Execution recording end.
            stagingStorage.begin();
            {
                endTime = System.currentTimeMillis();
                execution.set(executionType.getField("end_time"), new Timestamp(endTime));
                execution.set(executionType.getField("record_count"), new BigDecimal(getRecordCount()));
                execution.set(executionType.getField("completed"), Boolean.TRUE);
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
