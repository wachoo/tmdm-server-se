package org.talend.mdm.webapp.browserecords.client.widget.inputfield;

import com.google.gwt.junit.client.GWTTestCase;


public class UrlFieldGWTTest extends GWTTestCase  {

    public void testUrlField() {
        UrlField url = new UrlField();
        url.setEnabled(false);
        assertFalse(url.editImage.isVisible());
    }

    public String getModuleName() {
        return "org.talend.mdm.webapp.browserecords.TestBrowseRecords"; //$NON-NLS-1$
    }
}