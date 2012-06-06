package com.amalto.core.storage.task;

import com.amalto.core.metadata.ComplexTypeMetadata;
import com.amalto.core.metadata.DefaultMetadataVisitor;
import com.amalto.core.metadata.MetadataRepository;
import com.amalto.core.metadata.ReferenceFieldMetadata;
import com.amalto.core.storage.Storage;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 *
 */
abstract class MetadataRepositoryTask implements Task {

    private final List<ComplexTypeMetadata> types = new LinkedList<ComplexTypeMetadata>();

    protected final Storage storage;

    private final List<Task> tasks = new LinkedList<Task>();

    protected final MetadataRepository repository;

    protected double recordCount;

    protected long startTime;

    protected long endTime = -1;

    protected final AtomicBoolean startLock = new AtomicBoolean();

    protected final AtomicBoolean executionLock = new AtomicBoolean();

    protected final String id = UUID.randomUUID().toString();

    private double maxPerformance = Double.MIN_VALUE;

    private double minPerformance = Double.MAX_VALUE;

    private boolean isCancelled = false;

    private Task currentTypeTask;

    private final Object currentTypeTaskMonitor = new Object();

    MetadataRepositoryTask(Storage storage, MetadataRepository repository) {
        this.storage = storage;
        this.repository = repository;
    }

    private static boolean hasIncomingEdges(byte[] line) {
        boolean hasIncomingEdge = false;
        for (byte column : line) {
            if (column > 0) {
                hasIncomingEdge = true;
                break;
            }
        }
        return hasIncomingEdge;
    }

    private int getId(ComplexTypeMetadata type) {
        if (!types.contains(type)) {
            types.add(type);
        }
        return types.indexOf(type);
    }

    private ComplexTypeMetadata getType(int id) {
        return types.get(id);
    }

    protected abstract Task createTypeTask(ComplexTypeMetadata type);

    /**
     * Sorts type in inverse order of dependency (topological sort).
     *
     * @param repository The repository that contains types to sort.
     * @return A sorted list of {@link com.amalto.core.metadata.ComplexTypeMetadata} types.
     */
    protected List<ComplexTypeMetadata> sortTypes(MetadataRepository repository) {
        Collection<ComplexTypeMetadata> userDefinedTypes = repository.getUserComplexTypes();

        /*
         * Compute additional data for topological sorting
         */
        // TODO This use n² in memory... which isn't so good
        final byte[][] dependencyGraph = new byte[userDefinedTypes.size()][userDefinedTypes.size()];
        for (final ComplexTypeMetadata type : userDefinedTypes) {
            byte[] lineValue = new byte[userDefinedTypes.size()];
            dependencyGraph[getId(type)] = lineValue;
            type.accept(new DefaultMetadataVisitor<Void>() {
                @Override
                public Void visit(ReferenceFieldMetadata referenceField) {
                    if (!type.equals(referenceField.getReferencedType())) { // Don't count a dependency to itself as a dependency.
                        dependencyGraph[getId(type)][getId(referenceField.getReferencedType())]++;
                    }
                    return null;
                }
            });
        }

        /*
         * TOPOLOGICAL SORTING
         * See "Kahn, A. B. (1962), "Topological sorting of large networks", Communications of the ACM"
         */
        List<ComplexTypeMetadata> sortedTypes = new LinkedList<ComplexTypeMetadata>();
        Set<ComplexTypeMetadata> noIncomingEdges = new HashSet<ComplexTypeMetadata>();
        int lineNumber = 0;
        for (byte[] line : dependencyGraph) {
            if (!hasIncomingEdges(line)) {
                noIncomingEdges.add(getType(lineNumber));
            }
            lineNumber++;
        }

        while (!noIncomingEdges.isEmpty()) {
            Iterator<ComplexTypeMetadata> iterator = noIncomingEdges.iterator();
            ComplexTypeMetadata type = iterator.next();
            iterator.remove();

            sortedTypes.add(type);
            int columnNumber = getId(type);
            for (int i = 0; i < types.size(); i++) {
                int edge = dependencyGraph[i][columnNumber];
                if (edge > 0) {
                    dependencyGraph[i][columnNumber] -= edge;

                    if (!hasIncomingEdges(dependencyGraph[i])) {
                        noIncomingEdges.add(getType(i));
                    }
                }
            }
        }

        lineNumber = 0;
        for (byte[] line : dependencyGraph) {
            for (int i : line) {
                if (i != 0) {
                    throw new IllegalArgumentException("Data model has at least one circular dependency (Hint: " + getType(lineNumber).getName() + " type)");
                }
            }
            lineNumber++;
        }

        return sortedTypes;
    }

    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        synchronized (startLock) {
            startLock.set(true);
            startLock.notifyAll();
        }

        List<ComplexTypeMetadata> types = sortTypes(repository);

        for (ComplexTypeMetadata type : types) {
            Task task = createTypeTask(type);
            tasks.add(task);
        }

        startTime = System.currentTimeMillis();
        for (Task task : tasks) {
            synchronized (currentTypeTaskMonitor) {
                currentTypeTask = task;
            }
            if (!isCancelled) {
                System.out.println("--> Executing " + task + "...");
                task.execute(jobExecutionContext);
                recordCount += task.getRecordCount();
                System.out.println("<-- Executed (" + task.getRecordCount() + " record validated @ " + getCurrentPerformance() + " doc/s)");
            }
        }
        endTime = System.currentTimeMillis();
        System.out.println("Staging migration done @" + getCurrentPerformance() + " doc/s.");

        synchronized (executionLock) {
            executionLock.set(true);
            executionLock.notifyAll();
        }
    }

    public String getId() {
        return id;
    }

    public double getRecordCount() {
        return recordCount;
    }

    public double getCurrentPerformance() {
        if (recordCount > 0) {
            float time;
            if (endTime > 0) {
                time = (endTime - startTime) / 1000f;
            } else {
                time = (System.currentTimeMillis() - startTime) / 1000f;
            }
            double currentPerformance = recordCount / time;
            minPerformance = Math.min(minPerformance, currentPerformance);
            maxPerformance = Math.max(maxPerformance, currentPerformance);
            return currentPerformance;
        } else {
            return 0;
        }
    }

    public double getMinPerformance() {
        return minPerformance;
    }

    public double getMaxPerformance() {
        return maxPerformance;
    }

    public void cancel() {
        synchronized (currentTypeTaskMonitor) {
            currentTypeTask.cancel();
            isCancelled = true;
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
}
