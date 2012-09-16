package com.amalto.core.storage.task;

import com.amalto.core.metadata.ComplexTypeMetadata;
import com.amalto.core.metadata.MetadataRepository;
import com.amalto.core.metadata.MetadataUtils;
import com.amalto.core.storage.Storage;
import org.apache.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 *
 */
abstract class MetadataRepositoryTask implements Task {

    private static final Logger LOGGER = Logger.getLogger(MetadataRepository.class);

    private final List<Task> tasks = new LinkedList<Task>();

    private final AtomicBoolean startLock = new AtomicBoolean();

    private final MetadataRepository repository;

    private final AtomicBoolean executionLock = new AtomicBoolean();

    private final String id = UUID.randomUUID().toString();

    private final Object currentTypeTaskMonitor = new Object();

    final Storage storage;

    private double processedRecordCount;

    private long startTime;

    private long endTime = -1;

    private boolean isCancelled = false;

    private Task currentTypeTask;

    private boolean isFinished;

    MetadataRepositoryTask(Storage storage, MetadataRepository repository) {
        this.storage = storage;
        this.repository = repository;
    }

    protected abstract Task createTypeTask(ComplexTypeMetadata type);

    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        try {
            run();
        } catch (Exception e) {
            e.printStackTrace();
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
            List<ComplexTypeMetadata> types = MetadataUtils.sortTypes(repository);
            for (ComplexTypeMetadata type : types) {
                if (type.isInstantiable() && processType(type)) {
                    Task task = createTypeTask(type);
                    tasks.add(task);
                }
            }
            startTime = System.currentTimeMillis();
            for (Task task : tasks) {
                synchronized (currentTypeTaskMonitor) {
                    currentTypeTask = task;
                }
                if (!isCancelled) {
                    LOGGER.info("--> Executing " + task + "...");
                    task.run();
                    processedRecordCount += task.getProcessedRecords();
                    LOGGER.info("<-- Executed (" + task.getRecordCount() + " record validated @ " + getPerformance() + " doc/s)");
                }
            }
            endTime = System.currentTimeMillis();
            LOGGER.info("Staging migration done @" + getPerformance() + " doc/s.");
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

    public double getPerformance() {
        if (processedRecordCount > 0) {
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
            if (currentTypeTask != null) {
                currentTypeTask.cancel();
            }
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
    public int getProcessedRecords() {
        int totalProcessedRecords = 0;
        for (Task task : tasks) {
            totalProcessedRecords += task.getProcessedRecords();
        }
        return totalProcessedRecords;
    }
}
