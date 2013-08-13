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
package org.talend.mdm.webapp.browserecords.server.util;

import java.io.InputStream;

import junit.framework.TestCase;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.talend.mdm.webapp.base.server.util.XmlUtil;

@SuppressWarnings("nls")
public class DownloadUtilTest extends TestCase {

    private static final Logger LOG = Logger.getLogger(DownloadUtilTest.class);

    public void testIsJoinField() {
        String xPath = "Product/Id";
        String concept = "Product";
        boolean result = DownloadUtil.isJoinField(xPath, concept);
        assertTrue(result);

        xPath = "Product/Name";
        concept = "ProductFamily";
        result = DownloadUtil.isJoinField(xPath, concept);
        assertFalse(result);

        xPath = "ProductFamily/Name";
        concept = "productfamily";
        result = DownloadUtil.isJoinField(xPath, concept);
        assertTrue(result);
    }

    public void testGetJoinFieldValue() {
        InputStream is = DownloadUtilTest.class.getResourceAsStream("result.xml");
        Document doc = null;
        try {
            doc = XmlUtil.parse(is);
        } catch (DocumentException e) {
            LOG.error(e);
        }

        String value = DownloadUtil.getJoinFieldValue(doc, "ProductFamily/Name", 5);
        assertEquals("Mugs", value);
    }
}
