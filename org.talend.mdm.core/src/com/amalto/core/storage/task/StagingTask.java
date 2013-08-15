package com.amalto.core.storage.task;

import com.amalto.core.storage.Storage;
import com.amalto.core.storage.record.DataRecord;
import com.amalto.core.storage.record.metadata.UnsupportedDataRecordMetadata;
import org.apache.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.talend.mdm.commmon.metadata.ComplexTypeMetadata;
import org.talend.mdm.commmon.metadata.MetadataRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 */
public class StagingTask implements Task {

    private static final Logger LOGGER = Logger.getLogger(StagingTask.class);

    private final TaskSubmitter taskSubmitter;

    private final Storage stagingStorage;

    private final String executionId;

    private boolean isCancelled = false;

    private final List<Task> tasks;

    private final AtomicBoolean startLock = new AtomicBoolean();

    private final AtomicBoolean executionLock = new AtomicBoolean();

    private final Object currentTaskMonitor = new Object();

    private final ComplexTypeMetadata executionType;

    private final AtomicInteger recordCount = new AtomicInteger();

    private final ClosureExecutionStats stats = new ClosureExecutionStats();

    private Task currentTask;

    private long startTime;

    private boolean isFinished;

    public StagingTask(TaskSubmitter taskSubmitter,
                       Storage stagingStorage,
                       MetadataRepository stagingRepository,
                       List<Task> tasks) {
        this.taskSubmitter = taskSubmitter;
        this.stagingStorage = stagingStorage;
        this.executionId = UUID.randomUUID().toString();
        this.executionType = stagingRepository.getComplexType("TALEND_TASK_EXECUTION"); //$NON-NLS-1$
        this.tasks = tasks;
    }

    public String getId() {
        return executionId;
    }

    public int getRecordCount() {
        synchronized (currentTaskMonitor) {
            if (currentTask != null) {
                return currentTask.getRecordCount();
            } else {
                return tasks.get(0).getRecordCount();
            }
        }
    }

    @Override
    public int getErrorCount() {
        return stats.getErrorCount();
    }

    public double getPerformance() {
        return (getRecordCount()) / ((System.currentTimeMillis() - startTime) / 1000f);
    }

    public void cancel() {
        synchronized (currentTaskMonitor) {
            isCancelled = true;
            if (currentTask != null) {
                currentTask.cancel();
            }
            synchronized (startLock) {
                startLock.notifyAll();
            }
            synchronized (executionLock) {
                executionLock.notifyAll();
            }
            // Ensure cancel execution stats are stored to database.
            recordExecutionEnd();
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
    public boolean hasFinished() {
        return isCancelled || isFinished;
    }

    @Override
    public int getProcessedRecords() {
        return stats.getErrorCount() + stats.getSuccessCount();
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
            recordExecutionStart();
            recordCount.set(0);
            for (Task task : tasks) {
                synchronized (currentTaskMonitor) {
                    if (isCancelled) {
                        break;
                    }
                    currentTask = task;
                }
                LOGGER.info("--> " + task.toString());
                taskSubmitter.submitAndWait(currentTask);
                LOGGER.info("<-- DONE " + task.toString());
            }
            recordExecutionEnd();
        } finally {
            synchronized (executionLock) {
                executionLock.set(true);
                executionLock.notifyAll();
            }
            isFinished = true;
            synchronized (currentTaskMonitor) {
                currentTask = null;
            }
        }
    }

    private void recordExecutionStart() {
        DataRecord execution = new DataRecord(executionType, UnsupportedDataRecordMetadata.INSTANCE);
        execution.set(executionType.getField("id"), executionId); //$NON-NLS-1$
        startTime = System.currentTimeMillis();
        execution.set(executionType.getField("start_time"), startTime); //$NON-NLS-1$
        try {
            stagingStorage.begin();
            stagingStorage.update(execution);
            stagingStorage.commit();
        } catch (Exception e) {
            stagingStorage.rollback();
            throw new RuntimeException(e);
        }
    }

    private void recordExecutionEnd() {
        DataRecord execution = new DataRecord(executionType, UnsupportedDataRecordMetadata.INSTANCE);
        execution.set(executionType.getField("id"), executionId); //$NON-NLS-1$
        execution.set(executionType.getField("start_time"), startTime); //$NON-NLS-1$
        execution.set(executionType.getField("end_time"), System.currentTimeMillis()); //$NON-NLS-1$
        execution.set(executionType.getField("error_count"), new BigDecimal(getErrorCount())); //$NON-NLS-1$
        execution.set(executionType.getField("record_count"), new BigDecimal(getProcessedRecords())); //$NON-NLS-1$
        execution.set(executionType.getField("completed"), Boolean.TRUE); //$NON-NLS-1$
        try {
            stagingStorage.begin();
            stagingStorage.update(execution);
            stagingStorage.commit();
        } catch (Exception e) {
            stagingStorage.rollback();
            throw new RuntimeException(e);

        }
    }
}
