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
import java.io.InputStream;
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

			BulkloadClient client = new BulkloadClient(serverURL, "admin",
					"talend", null, "Order", "Country", "Order");
			client.setOptions(new BulkloadOptions());
            InputStream bin = BulkloadClientTest.class.getResourceAsStream("test.xml");
            client.load(bin);
        }
    }

    public void testPerformance() throws Exception {

        String serverURL = "http://localhost:8080/datamanager/loadServlet";

        BulkloadClient client = new BulkloadClient(serverURL, "admin", "talend", null, "Order", "Country", "Order");
        client.setOptions(new BulkloadOptions());

        String xml = "<Country><isoCode>zh1</isoCode><label>china</label><Continent>Asia</Continent></Country>";
        int num = 1000;
        int gap_num = 200;
        int gap = num / gap_num;
        for (int i = 0; i < gap; i++) {
            StringBuffer sb = new StringBuffer();
            for (int j = 0; j < gap_num; j++) {
                int n = gap_num * i + j;
                xml = "<Country><isoCode>zh" + n + "</isoCode><label>china</label><Continent>Asia</Continent></Country>\n";
                sb.append(xml);
            }
            InputStreamMerger manager = client.load();

            // InputStream bin = BulkloadClientTest.class.getResourceAsStream("test.xml");
            InputStream bin = new ByteArrayInputStream(sb.toString().getBytes("utf-8"));
            manager.push(bin);
            manager.close();
            // client.load(bin);
        }
	}
}
