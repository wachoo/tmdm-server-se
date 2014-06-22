/*
 * Copyright (C) 2006-2014 Talend Inc. - www.talend.com
 *
 * This source code is available under agreement available at
 * %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
 *
 * You should have received a copy of the agreement
 * along with this program; if not, write to Talend SA
 * 9 rue Pages 92150 Suresnes, France
 */

package org.talend.mdm.bulkload.client;

import java.io.IOException;
import java.io.InputStream;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 */
public class InputStreamMerger extends InputStream {

    private static final Logger log = Logger.getLogger(InputStreamMerger.class.getName());

    private static final int DEFAULT_CAPACITY = 1000;

    private final Queue<InputStream> inputStreamBuffer;

    private final Object consumerMonitor = new Object();

    private final Object producerMonitor = new Object();

    private final Object exhaustLock = new Object();

    private final AtomicBoolean hasFinishedRead = new AtomicBoolean();

    private WarmUpStrategy warmUpStrategy;

    private boolean isClosed;

    private InputStream currentStream;

    private Throwable lastFailure;

    private ConsumeStrategy consumeStrategy;

    public InputStreamMerger() {
        this(DEFAULT_CAPACITY, NoWarmUpStrategy.INSTANCE);
    }

    public InputStreamMerger(int capacity) {
        this(capacity, NoWarmUpStrategy.INSTANCE);
    }

    /**
     * Create a input stream merger with custom values.
     *
     * @param capacity Input stream buffer will never exceed <code>capacity</code>. {@link Integer#MAX_VALUE} is equivalent
     *                 to infinite capacity.
     * @param warmUpStrategy Allow customization of "warm up" time. Can allow initial buffering before consumers can actually
     *                 consume data.
     * @see NoWarmUpStrategy
     * @see ThresholdWarmUpStrategy
     */
    public InputStreamMerger(int capacity, WarmUpStrategy warmUpStrategy) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("Capacity " + capacity + " is invalid.");
        }
        inputStreamBuffer = new LinkedBlockingQueue<InputStream>(capacity);
        this.warmUpStrategy = warmUpStrategy;
        this.consumeStrategy = new FinishedRead(hasFinishedRead);
    }

    /**
     * <p>
     * Indicates to this {@link org.talend.mdm.bulkload.client.InputStreamMerger} that <code>thread</code> is consuming
     * all data available in stream. Doing so ensures the {@link #close()} will also wait for consumer end before
     * actually closing the stream.
     * </p>
     *
     * @param thread The thread this input stream should wait for.
     * @see #close()
     */
    public void setConsumerThread(Thread thread) {
        this.consumeStrategy = new WaitForConsumer(hasFinishedRead, thread);
    }

    /**
     * "Push" new content to consumers: publish to this input stream readers new content to be read from <code>inputStream</code>
     * parameter. This
     * @param inputStream New content for producers
     * @throws IOException If this input stream was closed.
     * @throws IllegalArgumentException If <code>inputStream</code> is <code>null</code>.
     * @see #close()
     */
    public void push(InputStream inputStream) throws IOException {
        if (inputStream == null) {
            throw new IllegalArgumentException("Input stream can not be null.");
        }
        if (isClosed) {
            throw new IOException("Stream is closed");
        }
        while (!inputStreamBuffer.offer(inputStream)) {
            debug("[P] Waiting for a read to complete (" + inputStreamBuffer.size() + ")");
            synchronized (consumerMonitor) {
                consumerMonitor.notifyAll();
            }
            if (!inputStreamBuffer.offer(inputStream)) {
                synchronized (producerMonitor) {
                    try {
                        producerMonitor.wait();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
            debug("[P] End of wait");
        }
        debug("[P] Added a new input stream (buffer now has " + inputStreamBuffer.size() + " streams)");
    }

    public void reportFailure(Throwable e) {
        debug("Exception occurred in consumer thread: " + e.getMessage());
        lastFailure = e;
        synchronized (exhaustLock) {
            exhaustLock.notifyAll();
        }
    }

    @Override
    public int available() throws IOException {
        if (currentStream != null) {
            return currentStream.available();
        }
        moveToNextInputStream();
        if (currentStream != null) {
            return currentStream.available();
        }
        return 4096;
    }

    @Override
    public boolean markSupported() {
        return false;
    }

    /**
     * <p> Read block the current thread until data is pushed to this stream (using {@link #push(java.io.InputStream)}
     * or if {@link #close()} is called. </p>
     *
     * @return the next byte of data, or <code>-1</code> if the end of the stream is reached <b>or</b> if stream is
     *         closed.
     * @throws IOException
     * @see java.io.InputStream#read()
     */
    @Override
    public int read() throws IOException {
        int read = -1;
        if (currentStream != null) {
            read = currentStream.read();
        } else {
            moveToNextInputStream();
            if (currentStream != null) {
                read = currentStream.read();
            }
        }
        if (read < 0) {
            moveToNextInputStream();
            if (currentStream != null) {
                read = currentStream.read();
            }
        }
        if (read < 0) {
            synchronized (exhaustLock) {
                debug("[C] Notify exhaust lock");
                exhaustLock.notifyAll();
            }
        }
        // Throw any exception that might have occurred during last record processing.
        throwLastFailure();
        return read;
    }

    private void throwLastFailure() throws IOException {
        if (lastFailure != null) {
            debug("[P] Report last failure exception to producer.");
            throw new IOException("An exception occurred while processing last record.", lastFailure);
        }
    }

    private void moveToNextInputStream() throws IOException {
        if (consumeStrategy.isConsumed() && isClosed) {
            return;
        }
        // Throw any exception that might have occurred during previous records
        throwLastFailure();
        // Check the isClosed flag in case we've got waken up by a close()
        while (!warmUpStrategy.isReady(this) || (inputStreamBuffer.isEmpty() && !isClosed)) {
            synchronized (consumerMonitor) {
                try {
                    debug("[C] Wait for more input (is warm up ready: " + warmUpStrategy.isReady(this) + ")");
                    consumerMonitor.wait(1000);
                    debug("[C] Wait for more input done.");
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                if (isClosed) {
                    break;
                }
            }
        }
        if (!inputStreamBuffer.isEmpty()) {
            if (currentStream != null) {
                currentStream.close();
            }
            currentStream = inputStreamBuffer.poll();
        } else {
            currentStream = null;
            hasFinishedRead.set(true);
        }
        synchronized (producerMonitor) {
            producerMonitor.notifyAll();
        }
        debug("[C] Remaining buffers : " + inputStreamBuffer.size());
    }

    /**
     * <p> Close this stream and perform some checks: <ul> <li>Mark this stream as closed (no more calls to {@link
     * #push(java.io.InputStream)} are allowed)</li> <li>Closes any remaining stream pushed to this stream</li> </ul>
     * </p>
     * <p> Calling this method wakes up any thread blocked on {@link #read()} </p> <p> Wait till all streams pushed
     * to this stream (and stored in <code>inputStreamBuffer</code>) are processed by a reader. </p> <p> When this
     * method exits, the buffer is empty and the last stream in buffer is fully read (i.e. until read() returns -1).
     * </p>
     * <p> <b>Note:</b> if {@link #setConsumerThread(Thread)} was previously called, this method will wait for
     * the death of consumer before returning.</p>
     *
     * @throws IOException In case at least one stream in buffer hasn't been read.
     * @see java.io.InputStream#close()
     */
    @Override
    public void close() throws IOException {
        super.close();
        isClosed = true;
        warmUpStrategy = NoWarmUpStrategy.INSTANCE; // In case close() is called before end of warm up.
        synchronized (consumerMonitor) {
            consumerMonitor.notifyAll();
        }
        debug("[P] Input stream buffer size: " + inputStreamBuffer.size());
        debug("[P] Has finished read: " + consumeStrategy.isConsumed());
        while (!inputStreamBuffer.isEmpty() || !consumeStrategy.isConsumed()) {
            try {
                debug("[P] Waiting for exhaust... (" + inputStreamBuffer.size() + " buffer(s) remaining / consumer finished: " + consumeStrategy.isConsumed() + ")");
                synchronized (exhaustLock) {
                    exhaustLock.wait(200);
                }
                // In case we got woken up due to a failure
                throwLastFailure();
                debug("[P] Waiting for exhaust done.");
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        // In case failure happened on very last read.
        throwLastFailure();
        debug("[P] Close completed.");
    }

    private static void debug(String message) {
        Level debugLevel = Level.FINEST;
        if (log.isLoggable(debugLevel)) {
            log.log(debugLevel, "[" + Thread.currentThread() + "] " + message);
        }
    }

    public Throwable getLastReportedFailure() {
        return lastFailure;
    }

    public int getBufferSize() {
        return inputStreamBuffer.size();
    }

    interface WarmUpStrategy {
        /**
         * @param inputStreamMerger The input stream to be checked.
         * @return <code>true</code> if the <code>inputStreamMerger</code> is ready to be used by consumers.
         * @see org.talend.mdm.bulkload.client.InputStreamMerger#getBufferSize()
         */
        boolean isReady(InputStreamMerger inputStreamMerger);
    }

    /**
     * A "no warm up" warm up strategy: always returns <code>true</code> (<code>inputStreamMerger</code> is always
     * ready).
     */
    public static class NoWarmUpStrategy implements WarmUpStrategy {
        static WarmUpStrategy INSTANCE = new NoWarmUpStrategy();

        @Override
        public boolean isReady(InputStreamMerger inputStreamMerger) {
            return true;
        }
    }

    /**
     * A threshold based strategy: indicate input stream merger isn't ready till {@link org.talend.mdm.bulkload.client.InputStreamMerger#getBufferSize()}
     * is greater or equals to <code>threshold</code>. Once buffer size satisfies this condition, input stream is always
     * considered as ready.
     */
    public static class ThresholdWarmUpStrategy implements WarmUpStrategy {
        private final int threshold;

        private boolean isReady = false;

        public ThresholdWarmUpStrategy(int threshold) {
            this.threshold = threshold;
        }

        @Override
        public boolean isReady(InputStreamMerger inputStreamMerger) {
            if (!isReady) {
                isReady = inputStreamMerger.getBufferSize() >= threshold;
            }
            return isReady;
        }
    }

    interface ConsumeStrategy {
        boolean isConsumed();
    }

    public static class FinishedRead implements ConsumeStrategy {

        private final AtomicBoolean hasFinishedRead;

        public FinishedRead(AtomicBoolean hasFinishedRead) {
            this.hasFinishedRead = hasFinishedRead;
        }

        public boolean isConsumed() {
            return hasFinishedRead.get();
        }
    }

    public static class WaitForConsumer implements ConsumeStrategy {

        private final Thread consumerThread;

        private final AtomicBoolean hasFinishedRead;

        public WaitForConsumer(AtomicBoolean hasFinishedRead, Thread consumerThread) {
            this.consumerThread = consumerThread;
            this.hasFinishedRead = hasFinishedRead;
        }

        public boolean isConsumed() {
            return hasFinishedRead.get() && !consumerThread.isAlive();
        }
    }
}
