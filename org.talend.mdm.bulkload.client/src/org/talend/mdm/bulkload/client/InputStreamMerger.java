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

package org.talend.mdm.bulkload.client;

import java.io.IOException;
import java.io.InputStream;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 */
public class InputStreamMerger extends InputStream {

    private static final Logger log = Logger.getLogger(InputStreamMerger.class.getName());

    private final Queue<InputStream> inputStreamBuffer = new ConcurrentLinkedQueue<InputStream>();

    private final Object readLock = new Object();

    private final Object exhaustLock = new Object();

    private boolean isClosed;

    private InputStream currentStream;

    private boolean hasFinishedRead;

    private Throwable lastFailure;

    public void push(InputStream inputStream) throws IOException {
        if (inputStream == null) {
            throw new IllegalArgumentException("Input stream can not be null.");
        }
        if (isClosed) {
            throw new IOException("Stream is closed");
        }
        inputStreamBuffer.add(inputStream);
        debug("Added a new input stream (buffer now has " + inputStreamBuffer.size() + " streams)");
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
                debug("Notify exhaust lock");
                exhaustLock.notifyAll();
            }
        }
        // Throw any exception that might have occurred during last record processing.
        throwLastFailure();
        return read;
    }

    private void throwLastFailure() throws IOException {
        if (lastFailure != null) {
            debug("Report last failure exception to producer.");
            throw new IOException("An exception occurred while processing last record.", lastFailure);
        }
    }

    private void moveToNextInputStream() throws IOException {
        // Throw any exception that might have occurred during previous records
        throwLastFailure();
        // Check the isClosed flag in case we've got waken up by a close()
        while (inputStreamBuffer.isEmpty() && !isClosed) {
            synchronized (readLock) {
                try {
                    debug("Wait for more input...");
                    readLock.wait();
                    debug("Wait for more input done.");
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
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
            hasFinishedRead = true;
        }
        debug("Remaining buffers : " + inputStreamBuffer.size());
    }

    /**
     * <p> Close this stream and perform some checks: <ul> <li>Mark this stream as closed (no more calls to {@link
     * #push(java.io.InputStream)} are allowed)</li> <li>Closes any remaining stream pushed to this stream</li> </ul>
     * </p> <p> Calling this method wakes up any thread blocked on {@link #read()} </p> <p> Wait till all streams pushed
     * to this stream (and stored in <code>inputStreamBuffer</code>) are processed by a reader. </p> <p> When this
     * method exits, the buffer is empty and the last stream in buffer is fully read (i.e. until read() returns -1).
     * </p>
     *
     * @throws IOException In case at least one stream in buffer hasn't been read.
     * @see java.io.InputStream#close()
     */
    @Override
    public void close() throws IOException {
        super.close();
        isClosed = true;
        synchronized (readLock) {
            readLock.notifyAll();
        }
        debug("Input stream buffer size: " + +inputStreamBuffer.size());
        debug("Has finished read: " + hasFinishedRead);
        debug("Stop condition: " + (!inputStreamBuffer.isEmpty() && !hasFinishedRead));
        while (!inputStreamBuffer.isEmpty() && !hasFinishedRead) {
            try {
                debug("Waiting for exhaust... (" + inputStreamBuffer.size() + " remaining)");
                synchronized (exhaustLock) {
                    exhaustLock.wait();
                }
                // In case we got woken up due to a failure
                throwLastFailure();
                debug("Waiting for exhaust done.");
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        // In case failure happened on very last read.
        throwLastFailure();
        debug("Close completed.");
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
}
