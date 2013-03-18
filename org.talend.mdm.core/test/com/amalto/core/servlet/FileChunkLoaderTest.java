// ============================================================================
//
// Copyright (C) 2006-2013 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package com.amalto.core.servlet;

import static org.junit.Assert.*;

import java.io.File;
import java.net.URL;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.junit.Test;

@SuppressWarnings("nls")
public class FileChunkLoaderTest {

    @Test
    public void test() throws Exception {
        File test = getFile("com/amalto/core/servlet/test.log");

        FileChunkLoader loader = new FileChunkLoader(test);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        long position = 0;
        for (int i = 0; i < 11; i++) {
            long newPosition = loader.loadChunkTo(baos, position, 2);
            if (position == 0) {
                String result = baos.toString();
                assertTrue(result.endsWith("Service (JTA version) - JBoss Inc.\r\n"));
                assertEquals(238, newPosition);
            }
            position = newPosition;
        }
        assertEquals(2406, position);
        String result = baos.toString();
        assertTrue(result.endsWith("startup in 34 ms"));
    }

    private File getFile(String filename) {
        URL url = getClass().getClassLoader().getResource(filename);
        assertNotNull(url);
        return new File(url.getFile());
    }
}