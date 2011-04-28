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

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

import junit.framework.TestCase;

/**
 *
 */
public class BulkloadClientTest extends TestCase {

	public void testClient() throws Exception {
        String serverURL = "http://localhost:8080/datamanager/loadServlet";

        boolean isServerRunning = false;
		try {
			URL testURL = new URL(serverURL);
			URLConnection urlConnection = testURL.openConnection();
			urlConnection.connect();
			isServerRunning = true;
		} catch (IOException e) {
			System.out.println("Server is not running on '" + serverURL
					+ "', skip this test.");
		}

		if (isServerRunning) {
			URL url = BulkloadClient.class.getResource("test.xml");

			BufferedInputStream in = ((BufferedInputStream) url.getContent());
			byte[] buf = new byte[in.available()];
			int read = in.read(buf);
			assertTrue(read >= 0);

			String xml = new String(buf);
			BulkloadClient client = new BulkloadClient(serverURL, "admin",
					"talend", null, "Order", "Country", "Order");
			client.setOptions(new BulkloadOptions());
			client.load(xml);
		}
	}
}
