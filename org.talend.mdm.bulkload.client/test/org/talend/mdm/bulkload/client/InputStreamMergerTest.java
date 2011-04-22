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
			new InputStreamMerger(-1);
			fail("Negative arguments are not supported");
		} catch (Exception e) {
			// Expected
		}

		try {
			InputStreamMerger bis = new InputStreamMerger(10);
			bis.push(null);
			fail("Null input streams are not supported as argument of push()");
		} catch (Exception e) {
			// Expected
		}
	}

	public void testSimpleClose() {
		InputStreamMerger bis = new InputStreamMerger(10);
		try {
			bis.close();
			assertEquals(-1, bis.read());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public void testSimpleReadReaderFirst() {
		InputStreamMerger bis = new InputStreamMerger(1);
		String testString = "test";

		// Create a reader for the stream
		ReaderRunnable reader = new ReaderRunnable(testString, bis, 1);
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
		InputStreamMerger bis = new InputStreamMerger(1);
		String testString = "test";

		// Create a reader for the stream
		ReaderRunnable reader = new ReaderRunnable(testString, bis, 1);
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

	public void testManyReadReaderFirst() {
		int times = 10;
		InputStreamMerger bis = new InputStreamMerger(10);
		String testString = "test";

		// Create a reader for the stream
		ReaderRunnable reader = new ReaderRunnable(testString, bis, times);
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

	public void testNonEmptyCloseException() {
		InputStreamMerger bis = new InputStreamMerger(1);
		String testString = "test";

		// Create a "pusher", a thread that will put a new stream
		Runnable pusher = new PusherRunnable(bis, testString, 2);

		// Start the test (but now the pusher starts first)
		Thread pusherThread = new Thread(pusher);
		pusherThread.start();
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
        try {
            bis.close();
            fail("Expected an exception (close while there's still stream to process");
        } catch (IOException e) {
            String expected = "Stream closed but 2 remained in stream";
            assertEquals(expected, e.getMessage());
        }

		// Wait for test end
		try {
			pusherThread.join();
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

	public void testBulkload() throws InterruptedException, IOException {
		String testString = "<root></root>";
		int batchSize = 10;
		int inputSize = 5000;

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
		InputStreamMerger inputStreamMerger = new InputStreamMerger(batchSize);
		ReaderRunnable reader = doSimulateBulkload(testString, batchSize,
				inputStreamMerger);
		for (int i = 0; i < inputSize; i++) {
			if (i > 0 && i % batchSize == 0) {
				// Wait for reader to finish consume of xml chunks
				inputStreamMerger.waitForExhaust();
				assertEquals(expectedTestString, reader.getRebuiltString());

				// Reset
                // Create a new stream for a new batch
				inputStreamMerger = new InputStreamMerger(batchSize);
				reader = doSimulateBulkload(testString, batchSize,
						inputStreamMerger);
			}

			inputStreamMerger.push(new ByteArrayInputStream(testString
					.getBytes()));
		}

		// Wait for remaining ones (in case (count % batchSize != 0))
		inputStreamMerger.waitForExhaust();
		if (inputSize % batchSize != 0) {
			assertEquals(remainingExpectedTestString,
					reader.getRebuiltString());
		}
	}

	private static ReaderRunnable doSimulateBulkload(String testString,
			int batchSize, InputStreamMerger bis) {
		ReaderRunnable reader = new ReaderRunnable(testString, bis, batchSize);
		Thread readerThread = new Thread(reader);
		readerThread.start(); // eq bulkload client load
		return reader;
	}

	private static class ReaderRunnable implements Runnable {
		private final InputStreamMerger bis;
		private final Object runLock = new Object();
		private String rebuiltString;
		private final char[] stringAsBytes;
		private int currentIndex;

		public ReaderRunnable(String testString, InputStreamMerger bis,
				int expectedSize) {
			this.bis = bis;
			stringAsBytes = new char[testString.length() * expectedSize];
			this.currentIndex = 0;
		}

		public void run() {
			synchronized (runLock) {
				try {
					int read;
					while ((read = bis.read()) > 0) {
						stringAsBytes[this.currentIndex++] = (char) read;
					}

					this.rebuiltString = new String(stringAsBytes);
				} catch (IOException e) {
					throw new RuntimeException(e);
				} finally {
					try {
						bis.close();
					} catch (IOException e) {
						// Ignored
						e.printStackTrace();
					}
				}
			}
		}

		public String getRebuiltString() {
			synchronized (runLock) {
                if (currentIndex == stringAsBytes.length) {
                    return rebuiltString;
                } else {
                    return new String(ArrayUtils.subarray(stringAsBytes, 0,
					currentIndex));
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
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	}
}
