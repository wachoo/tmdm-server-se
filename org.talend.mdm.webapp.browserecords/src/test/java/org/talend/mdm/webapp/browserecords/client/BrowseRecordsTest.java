package org.talend.mdm.webapp.browserecords.client;

import com.google.gwt.junit.client.GWTTestCase;

public class BrowseRecordsTest extends GWTTestCase {

    public void testOnModuleLoad() {
        // test the entry method without any exception
        new BrowseRecords().onModuleLoad();
    }

    public void testGetSessionNotNull() {
        assertNotNull(BrowseRecords.getSession());
    }

    @Override
    public String getModuleName() {
        // GWTTestCase Required
        return "org.talend.mdm.webapp.browserecords.BrowseRecords";
    }

}
