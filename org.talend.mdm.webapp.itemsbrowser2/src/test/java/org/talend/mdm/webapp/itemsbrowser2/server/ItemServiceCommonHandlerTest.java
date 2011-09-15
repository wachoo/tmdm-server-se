// ============================================================================
//
// Copyright (C) 2006-2011 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.mdm.webapp.itemsbrowser2.server;

import junit.framework.TestCase;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;
import org.talend.mdm.webapp.itemsbrowser2.server.util.XmlUtil;

@SuppressWarnings("nls")
public class ItemServiceCommonHandlerTest extends TestCase {

    public void testParseResultDocument() throws Exception {
        ItemServiceCommonHandler handler = new ItemServiceCommonHandler();

        String expectedRootElementName = "result";
        String result = "<result><field>1</field></result>";
        Document resultDocument = handler.parseResultDocument(result, expectedRootElementName);
        Element rootElement = resultDocument.getRootElement();
        assertEquals(expectedRootElementName, rootElement.getName());
        Node node = XmlUtil.queryNode(resultDocument, expectedRootElementName + "/field");
        assertNotNull(node);
        
        result = "<field>1</field>";
        resultDocument = handler.parseResultDocument(result, expectedRootElementName);
        rootElement = resultDocument.getRootElement();
        assertEquals(expectedRootElementName, rootElement.getName());
        node = XmlUtil.queryNode(resultDocument, expectedRootElementName + "/field");
        assertNotNull(node);

        result = "<?xml version='1.0' encoding='ISO-8859-1'?><result><field>1</field></result>";
        resultDocument = handler.parseResultDocument(result, expectedRootElementName);
        rootElement = resultDocument.getRootElement();
        assertEquals(expectedRootElementName, rootElement.getName());
        node = XmlUtil.queryNode(resultDocument, expectedRootElementName + "/field");
        assertNotNull(node);
        

        result = "<?xml version='1.0' encoding='ISO-8859-1'?><field>1</field>";
        resultDocument = handler.parseResultDocument(result, expectedRootElementName);
        rootElement = resultDocument.getRootElement();
        assertEquals(expectedRootElementName, rootElement.getName());
        node = XmlUtil.queryNode(resultDocument, expectedRootElementName + "/field");
        assertNotNull(node);

        result = "<Result><field>1</field></Result>";
        resultDocument = handler.parseResultDocument(result, expectedRootElementName);
        rootElement = resultDocument.getRootElement();
        assertEquals(expectedRootElementName, rootElement.getName());
        node = XmlUtil.queryNode(resultDocument, expectedRootElementName + "/Result");
        assertNotNull(node);
    }

    public void testGetForeignKeyListWithCount() {
        // it's hard to prepare all the data here, so just test
        // com.amalto.webapp.core.util.Util.getWhereConditionFromFK(it's a new method)

        String xpathForeignKey = "Agency/Id";
        // to verify
        String xpathInfoForeignKey = "Agency/Name,Agency/City";
        String value = "google";
        String fkWhere = com.amalto.webapp.core.util.Util.getWhereConditionFromFK(null, xpathInfoForeignKey, value);
        assertTrue(fkWhere.contains("Agency/Name CONTAINS " + value + " OR " + " Agency/City CONTAINS " + value));
        fkWhere = com.amalto.webapp.core.util.Util.getWhereConditionFromFK(xpathForeignKey, xpathInfoForeignKey, value);
        assertTrue(fkWhere.contains("Agency/Id CONTAINS " + value));

    }
}
