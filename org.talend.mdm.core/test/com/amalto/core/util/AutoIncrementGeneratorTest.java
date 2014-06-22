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
package com.amalto.core.util;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Properties;

import junit.framework.TestCase;

import org.apache.commons.io.FileUtils;

/**
 * Mock test com.amalto.core.util.AutoIncrementGenerator
 */
@SuppressWarnings("nls")
public class AutoIncrementGeneratorTest extends TestCase {

    String key = "[HEAD].Product.ProductFamily.Id";

    int rootNodeHashCode = 1;

    String universe = "[HEAD]";

    String dataCluster = "Product";

    String conceptName = "ProductFamily.Id";

    @SuppressWarnings("static-access")
    public void testGenerateNum() {
        MockAutoIncrementGenerator mockObject = new MockAutoIncrementGenerator();
        long auto_incrementid = mockObject.generateNum(rootNodeHashCode, universe, dataCluster, conceptName);
        assertEquals("18", String.valueOf(auto_incrementid));
        auto_incrementid = mockObject.generateNum(rootNodeHashCode, universe, dataCluster, conceptName);
        assertEquals("20", String.valueOf(auto_incrementid));
        auto_incrementid = mockObject.generateNum(rootNodeHashCode, universe, dataCluster, conceptName);
        assertEquals("21", String.valueOf(auto_incrementid));
    }
    
    @SuppressWarnings("static-access")
    public void testSaveUnUsedIdsToDB() throws Exception {
        MockAutoIncrementGenerator mockObject = new MockAutoIncrementGenerator();
        mockObject.init();
        mockObject.USEDIDS.clear();
        mockObject.saveIdToMap(rootNodeHashCode, key, "19");
        mockObject.saveUnUsedIdsToFile(true, rootNodeHashCode);
        URL url = AutoIncrementGeneratorTest.class.getResource("CONF.AutoIncrement.AutoIncrement_unUsed.xml");
        String fileName = url.getPath();
        String xml = FileUtils.readFileToString(new File(fileName), "UTF-8");
        Properties p = Util.convertAutoIncrement(xml);
        assertEquals("18.19.", p.get(key));
    }

    @SuppressWarnings("static-access")
    public void testSaveIdToMap() {
        MockAutoIncrementGenerator mockObject = new MockAutoIncrementGenerator();
        mockObject.USEDIDS.clear();
        mockObject.saveIdToMap(rootNodeHashCode, key, "19");
        assertEquals("19", mockObject.USEDIDS.get(rootNodeHashCode).get(key));
    }

    @SuppressWarnings("static-access")
    public void testCheck() {
        MockAutoIncrementGenerator mockObject = new MockAutoIncrementGenerator();
        mockObject.USEDIDS.clear();
        mockObject.saveIdToMap(rootNodeHashCode, key, "19");
        mockObject.check(rootNodeHashCode);
        assertEquals(true, mockObject.USEDIDS.size() == 0);
    }


    @SuppressWarnings("static-access")
    public void testWriteXMLToFile() throws IOException {
        String xml = "<AutoIncrement><id>AutoIncrement</id></AutoIncrement>";
        URL url = AutoIncrementGeneratorTest.class.getResource("CONF.AutoIncrement.AutoIncrement.xml");
        String fileName = url.getPath();
        MockAutoIncrementGenerator mockObject = new MockAutoIncrementGenerator();
        mockObject.writeXMLToFile(xml, fileName);
        assertEquals(xml, FileUtils.readFileToString(new File(fileName)));
    }

    public void testSubString() {
        String unusedIds = "1.2.3.";
        String id = unusedIds.substring(0, unusedIds.indexOf("."));
        String unused = unusedIds.substring(unusedIds.indexOf(".") + 1);
        assertEquals("1", id);
        assertEquals("2.3.", unused);
    }

    @SuppressWarnings("static-access")
    public void test_setUpValueWhenThrowException() {
        boolean saveSuccess = true;
        try {
            String xml = "<AutoIncrement><id>AutoIncrement</id></AutoIncrement>";
            URL url = AutoIncrementGeneratorTest.class.getResource("CONF.AutoIncrement.AutoIncrementTest.xml");
            if (url == null)
                throw new NullPointerException("File is not found");
            String fileName = url.getPath();
            MockAutoIncrementGenerator mockObject = new MockAutoIncrementGenerator();
            mockObject.writeXMLToFile(xml, fileName);
        } catch (Exception e) {
            saveSuccess = false;
            assertSame("File is not found", e.getMessage());
        } finally {
            assertFalse(saveSuccess);
        }
    }

}