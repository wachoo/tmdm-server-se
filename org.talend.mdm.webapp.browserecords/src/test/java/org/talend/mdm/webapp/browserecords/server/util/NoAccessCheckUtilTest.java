package org.talend.mdm.webapp.browserecords.server.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;

import junit.framework.TestCase;


public class NoAccessCheckUtilTest extends TestCase {
    
    
    private static final List<String> ROLES = Arrays.asList(new String[] { "System_Admin" }); //$NON-NLS-1$


    public void testCheckNoAccessHelper() throws Exception {
        String modelXSD = getXSDModel("BrowseRecordsActionTest.xsd"); //$NON-NLS-1$
        assertFalse(NoAccessCheckUtil.checkNoAccessHelper(modelXSD, "M28_E01", ROLES)); //$NON-NLS-1$
        assertTrue(NoAccessCheckUtil.checkNoAccessHelper(modelXSD, "M28_E02", ROLES)); //$NON-NLS-1$
    }
    
    private String getXSDModel(String filename) throws IOException {

        InputStream stream = getClass().getResourceAsStream(filename);

        BufferedReader in = new BufferedReader(new InputStreamReader(stream));
        StringBuffer buffer = new StringBuffer();
        String line = ""; //$NON-NLS-1$
        while ((line = in.readLine()) != null) {
            buffer.append(line);
        }
        return buffer.toString();

    }
}
