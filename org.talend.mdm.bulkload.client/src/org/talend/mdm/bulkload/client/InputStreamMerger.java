/*
 * Copyright (C) 2006-2011 Talend Inc. - www.talend.com
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
import java.util.LinkedList;
import java.util.Queue;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 */
public class InputStreamMerger extends InputStream {
    private static final Logger log = Logger.getLogger(InputStreamMerger.class.getName());
    private final Queue<InputStream> inputStreamBuffer = new LinkedList<InputStream>();
    private final Object readLock = new Object();
    private final Object bufferLock = new Object();

    private final int count;
    private final long timeout;

    private boolean isClosed;
    private int currentCount = 0;
    private InputStream currentStream;

    public InputStreamMerger(int count) {
        this(count, -1);
    }

    public InputStreamMerger(int count, long timeout) {
        if (count < 0) {
            throw new IllegalArgumentException("Count argument can not be negative");
        }
        this.timeout = timeout;
        this.count = count;
    }

    public void push(InputStream inputStream) throws IOException {
        if (inputStream == null) {
            throw new IllegalArgumentException("Input stream can not be null.");
        }
        if (isClosed) {
            throw new IOException("Stream is closed");
        }

        synchronized (readLock) {
            inputStreamBuffer.add(inputStream);
            readLock.notifyAll();
        }
    }

    /**
     * <p>
     * Read block the current thread until data is pushed to this stream (using {@link #push(java.io.InputStream)} or
     * if {@link #close()} is called.
     * </p>
     *
     * @return the next byte of data, or <code>-1</code> if the end of the
     *             stream is reached <b>or</b> if stream is closed.
     * @throws IOException
     * @see java.io.InputStream#read()
     */
    @Override
    public int read() throws IOException {
        synchronized (readLock) {
            // Always check the isClosed flag after moveToNextInputStream()
            // since this call might close the stream.
            int read = -1;
            if (!isClosed) {
                if (currentStream == null) {
                    moveToNextInputStream();
                    if (!isClosed) {
                        read = currentStream.read();
                    }
                } else {
                    read = currentStream.read();
                }

                if (!isClosed && read < 0) {
                    moveToNextInputStream();
                    if (!isClosed) {
                        read = currentStream.read();
                    }
                }
            }
            return read;
        }
    }

    private void moveToNextInputStream() throws IOException {
        if (currentCount == count) {
            close();
        }

        // Notify any thread looking for buffer changes that state might have changed.
        synchronized (bufferLock) {
            bufferLock.notifyAll();
        }

        if (!isClosed && inputStreamBuffer.isEmpty()) {
            try {
                // Wait for a new input stream to be pushed
                synchronized (readLock) {
                    if (timeout < 0) {
                        readLock.wait();
                    } else {
                        readLock.wait(timeout);
                    }
                }
            } catch (InterruptedException e) {
                close();
                throw new RuntimeException(e);
            }
        }

        // Check the isClosed flag in case we've got waken up by a close()
        if (!isClosed) {
            currentStream = inputStreamBuffer.poll();
            currentCount++;
        }
    }

    /**
     * <p>
     * Close this stream and perform some checks:
     * <ul>
     * <li>Mark this stream as closed (no more calls to {@link #push(java.io.InputStream)} are allowed)</li>
     * <li>Closes any remaining stream pushed to this stream</li>
     * </ul>
     * </p>
     * <p>
     * Calling this method wakes up any thread blocked on {@link #read()}
     * </p>
     *
     * @throws IOException In case at least one stream in buffer hasn't been read.
     * @see java.io.InputStream#close()
     */
    @Override
    public void close() throws IOException {
        isClosed = true;
        super.close();

        synchronized (bufferLock) {
            try {
                if (!inputStreamBuffer.isEmpty()) {
                    // Clean up
                    for (InputStream inputStream : inputStreamBuffer) {
                        try {
                            inputStream.close();
                        } catch (IOException e) {
                            // Ignore this... but log error anyway
                            log.log(Level.SEVERE, "Exception during close underlying input streams", e);
                        }
                    }

                    throw new IOException("Stream closed but " + inputStreamBuffer.size() + " remained in stream");
                }
            } finally {
                bufferLock.notifyAll();

                // Wake up readers so they can detect end of stream
                synchronized (readLock) {
                    readLock.notifyAll();
                }
            }
        }
    }

    /**
     * <p>
     * Wait till all streams pushed to this stream (and stored in <code>inputStreamBuffer</code>) are
     * processed by a reader.
     * </p>
     * <p>
     * When this method exits, the buffer is empty and the last stream in buffer is fully read (i.e. until read() returns -1).
     * </p>
     */
    public void waitForExhaust() {
        while (!inputStreamBuffer.isEmpty()) {
            synchronized (bufferLock) {
                try {
                    bufferLock.wait();
                } catch (InterruptedException e) {
                    log.log(Level.SEVERE, "Error during wait for close", e);
                }
            }
        }
    }
}
