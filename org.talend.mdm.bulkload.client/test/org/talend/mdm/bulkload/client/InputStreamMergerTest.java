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

import java.io.ByteArrayInputStream;
import java.io.IOException;

import junit.framework.TestCase;
import org.apache.commons.lang.ArrayUtils;

/**
 *
 */
public class InputStreamMergerTest extends TestCase {

    public void testArguments() {
        try {
            InputStreamMerger bis = new InputStreamMerger();
            bis.push(null);
            fail("Null input streams are not supported as argument of push()");
        } catch (Exception e) {
            // Expected
        }
    }

    public void testSimpleClose() {
        InputStreamMerger bis = new InputStreamMerger();
        try {
            bis.close();
            assertEquals(-1, bis.read());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void testSimpleReadReaderFirst() {
        InputStreamMerger bis = new InputStreamMerger();
        String testString = "test";

        // Create a reader for the stream
        ReaderRunnable reader = new ReaderRunnable(bis);
        // Create a "pusher", a thread that will put a new stream
        Runnable pusher = new PusherRunnable(bis, testString, 1);

        // Start the test (start reader first)
        Thread readerThread = new Thread(reader);
        Thread pusherThread = new Thread(pusher);
        readerThread.start();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        pusherThread.start();

        // Wait for test end
        try {
            pusherThread.join();
            readerThread.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        assertEquals(testString, reader.getRebuiltString());
    }

    public void testSimpleReadPusherFirst() {
        InputStreamMerger bis = new InputStreamMerger();
        String testString = "test";

        // Create a reader for the stream
        ReaderRunnable reader = new ReaderRunnable(bis);
        // Create a "pusher", a thread that will put a new stream
        Runnable pusher = new PusherRunnable(bis, testString, 1);

        // Start the test (but now the pusher starts first)
        Thread readerThread = new Thread(reader);
        Thread pusherThread = new Thread(pusher);
        pusherThread.start();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        readerThread.start();

        // Wait for test end
        try {
            pusherThread.join();
            readerThread.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        assertEquals(testString, reader.getRebuiltString());
    }

    public void testSimpleReadPusherFirstWithError() {
        InputStreamMerger bis = new InputStreamMerger();
        String testString = "test";

        // Create a reader for the stream
        Runnable reader = new ReaderWithErrorsRunnable(bis);
        // Create a "pusher", a thread that will put a new stream
        Runnable pusher = new PusherRunnable(bis, testString, 1);

        // Start the test (but now the pusher starts first)
        Thread readerThread = new Thread(reader);
        Thread pusherThread = new Thread(pusher);
        pusherThread.start();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        readerThread.start();

        // Wait for test end
        try {
            readerThread.join();
            pusherThread.join();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        assertNotNull(bis.getLastReportedFailure());
        assertEquals("Expected exception text", bis.getLastReportedFailure().getCause().getMessage());
        try {
            bis.close();
            fail("Expected an exception on close.");
        } catch (IOException e) {
            assertEquals("Expected exception text", e.getCause().getCause().getMessage());
        }
    }

    public void testManyReadReaderFirst() {
        int times = 10;
        InputStreamMerger bis = new InputStreamMerger();
        String testString = "test";

        // Create a reader for the stream
        ReaderRunnable reader = new ReaderRunnable(bis);
        // Create a "pusher", a thread that will put a new stream
        Runnable pusher = new PusherRunnable(bis, testString, times);

        // Start the test (start reader first)
        Thread readerThread = new Thread(reader);
        Thread pusherThread = new Thread(pusher);
        readerThread.start();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        pusherThread.start();

        // Wait for test end
        try {
            pusherThread.join();
            readerThread.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        String expectedOutput = "";
        for (int i = 0; i < times; i++) {
            expectedOutput += testString;
        }
        assertEquals(expectedOutput, reader.getRebuiltString());
    }

    public void testBulkload() throws InterruptedException, IOException {
        String testString = "<Product><Id>99</Id><Name>BaxLlvBgKJ6NuJsdOiAAGSGbWX7F3b</Name><ShortDescription></ShortDescription><LongDescription></LongDescription><Features><Sizes><Size></Size></Sizes><Colors><Color></Color></Colors></Features><Availability></Availability><Price></Price><Family></Family><Supplier></Supplier><CreationDate></CreationDate><RemovalDate></RemovalDate><Status></Status></Product>";
        int batchSize = 10;
        int inputSize = 15000;

        //
        String expectedTestString = "";
        for (int i = 0; i < batchSize; i++) {
            expectedTestString += testString;
        }

        String remainingExpectedTestString = "";
        for (int i = 0; i < (inputSize % batchSize); i++) {
            remainingExpectedTestString += testString;
        }

        // Start the test
        InputStreamMerger inputStreamMerger = doSimulateBulkload();
        for (int i = 0; i < inputSize; i++) {
            if (i > 0 && i % batchSize == 0) {
                // Wait for reader to finish consume of xml chunks
                inputStreamMerger.close();
                // Create a new stream for a new batch
                inputStreamMerger = doSimulateBulkload();
            }

            inputStreamMerger.push(new ByteArrayInputStream(testString.getBytes()));
        }

        // Wait for remaining ones (in case (count % batchSize != 0))
        inputStreamMerger.close();
        if (inputSize % batchSize != 0) {
        }
    }

    private static InputStreamMerger doSimulateBulkload() {
        InputStreamMerger merger = new InputStreamMerger();
        ReaderRunnable reader = new ReaderRunnable(merger);
        Thread readerThread = new Thread(reader);
        readerThread.start(); // eq bulkload client load

        return merger;
    }

    private static class ReaderRunnable implements Runnable {
        private final InputStreamMerger bis;
        private final Object runLock = new Object();
        private String rebuiltString = "";

        public ReaderRunnable(InputStreamMerger bis) {
            this.bis = bis;
        }

        public void run() {
            synchronized (runLock) {
                try {
                    int readBytes;
                    byte[] buffer = new byte[1024];
                    while ((readBytes = bis.read(buffer)) > 0) {
                        rebuiltString += new String(ArrayUtils.subarray(buffer, 0, readBytes));
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        public String getRebuiltString() {
            synchronized (runLock) {
                return rebuiltString;
            }
        }
    }

    private static class ReaderWithErrorsRunnable implements Runnable {
        private final InputStreamMerger bis;
        private final Object runLock = new Object();
        private String rebuiltString = "";

        public ReaderWithErrorsRunnable(InputStreamMerger bis) {
            this.bis = bis;
        }

        public void run() {
            synchronized (runLock) {
                try {
                    int c;
                    while ((c = bis.read()) > 0) {
                        rebuiltString += c;
                        if (!rebuiltString.isEmpty()) {
                            bis.reportFailure(new RuntimeException("Expected exception text"));
                        }
                    }
                } catch (IOException e) {
                    bis.reportFailure(e);
                }
            }
        }
    }


    private static class PusherRunnable implements Runnable {
        private final InputStreamMerger bis;
        private final String testString;
        private final int count;

        public PusherRunnable(InputStreamMerger bis, String testString,
                              int count) {
            this.bis = bis;
            this.testString = testString;
            this.count = count;
        }

        public void run() {
            try {
                for (int i = 0; i < count; i++) {
                    bis.push(new ByteArrayInputStream(testString.getBytes()));
                }
                try {
                    bis.close();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
