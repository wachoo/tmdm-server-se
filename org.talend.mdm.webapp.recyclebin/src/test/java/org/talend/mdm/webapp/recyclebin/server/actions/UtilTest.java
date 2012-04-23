// ============================================================================
//
// Copyright (C) 2006-2012 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.mdm.webapp.recyclebin.server.actions;

import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import junit.framework.TestCase;

import org.w3c.dom.Document;

@SuppressWarnings("nls")
public class UtilTest extends TestCase {

    private static final List<String> ROLES = Arrays.asList(new String[] { "System_Admin" });

    public void testCheckRestoreAccessHelper() throws Exception {
        String modelXSD = getXSDModel("UtilTest.xsd");
        assertFalse(Util.checkRestoreAccessHelper(modelXSD, "M26_E01", ROLES));
        assertTrue(Util.checkRestoreAccessHelper(modelXSD, "M26_E02", ROLES));
        assertFalse(Util.checkRestoreAccessHelper(modelXSD, "M26_E03", ROLES));
        assertFalse(Util.checkRestoreAccessHelper(modelXSD, "M26_E04", ROLES));
    }

    public void testCheckReadAccessHelper() throws Exception {
        String modelXSD = getXSDModel("UtilTest.xsd");
        assertTrue(Util.checkReadAccessHelper(modelXSD, "M26_E01", ROLES));
        assertTrue(Util.checkReadAccessHelper(modelXSD, "M26_E02", ROLES));
        assertFalse(Util.checkReadAccessHelper(modelXSD, "M26_E03", ROLES));
        assertFalse(Util.checkReadAccessHelper(modelXSD, "M26_E04", ROLES));
    }

    private String getXSDModel(String filename) throws Exception {
        InputStream is = getClass().getClassLoader().getResourceAsStream(filename);
        assertNotNull(is);
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(is);
        String XSDModel = com.amalto.core.util.Util.nodeToString(doc);
        return XSDModel;
    }
}
