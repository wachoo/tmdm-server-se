package org.talend.mdm.webapp.recyclebin.server.actions;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.ArrayUtils;

import junit.framework.TestCase;

public class UtilTest extends TestCase {

    private String modelXSD = null;
    private static final List<String> ROLES = Arrays.asList(new String[]{"System_Admin"}); //$NON-NLS-1$
    
    protected void setUp() throws Exception {
        
        InputStream in = getClass().getClassLoader().getResourceAsStream("UtilTest.xsd"); //$NON-NLS-1$
        StringBuffer fileData = new StringBuffer();
        BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF-8")); //$NON-NLS-1$
        char[] buf = new char[1024];
        int numRead=0;
        while((numRead=reader.read(buf)) != -1){
            String readData = String.valueOf(buf, 0, numRead);
            fileData.append(readData);
            buf = new char[1024];
        }
        reader.close();
        modelXSD = fileData.toString();
    }

    public void testCheckRestoreAccessHelper() {
        assertTrue(Util.checkRestoreAccessHelper(modelXSD, "M26_E01", ROLES)); //$NON-NLS-1$
        assertTrue(Util.checkRestoreAccessHelper(modelXSD, "M26_E02", ROLES)); //$NON-NLS-1$
        assertFalse(Util.checkRestoreAccessHelper(modelXSD, "M26_E03", ROLES)); //$NON-NLS-1$
        assertFalse(Util.checkRestoreAccessHelper(modelXSD, "M26_E04", ROLES)); //$NON-NLS-1$
    }

    public void testCheckReadAccessHelper() {
        assertTrue(Util.checkReadAccessHelper(modelXSD, "M26_E01", ROLES)); //$NON-NLS-1$
        assertTrue(Util.checkReadAccessHelper(modelXSD, "M26_E02", ROLES)); //$NON-NLS-1$
        assertFalse(Util.checkReadAccessHelper(modelXSD, "M26_E03", ROLES)); //$NON-NLS-1$
        assertFalse(Util.checkReadAccessHelper(modelXSD, "M26_E04", ROLES)); //$NON-NLS-1$
    }    
}
