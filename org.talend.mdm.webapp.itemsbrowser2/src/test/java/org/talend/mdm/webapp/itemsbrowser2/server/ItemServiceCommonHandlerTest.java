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

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;
import org.talend.mdm.commmon.util.core.EDBType;
import org.talend.mdm.commmon.util.core.MDMConfiguration;
import org.talend.mdm.webapp.itemsbrowser2.client.model.DataTypeConstants;
import org.talend.mdm.webapp.itemsbrowser2.server.util.XmlUtil;
import org.talend.mdm.webapp.itemsbrowser2.shared.SimpleTypeModel;
import org.talend.mdm.webapp.itemsbrowser2.shared.TypeModel;

import com.amalto.webapp.core.util.Util;
import com.amalto.webapp.util.webservices.WSWhereAnd;
import com.amalto.webapp.util.webservices.WSWhereCondition;
import com.amalto.webapp.util.webservices.WSWhereItem;
import com.amalto.webapp.util.webservices.WSWhereOperator;
import com.amalto.webapp.util.webservices.WSWhereOr;

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

    public void testGetForeignKeyHolder() throws Exception {       
        ItemServiceCommonHandler handler = new ItemServiceCommonHandler();

        TypeModel model = new SimpleTypeModel("family", DataTypeConstants.STRING);
        model.setForeignkey("ProductFamily/Id");
        List<String> foreignKeyInfos = new ArrayList<String>();
        foreignKeyInfos.add("ProductFamily/Name");
        model.setForeignKeyInfo(foreignKeyInfos);
        model.setXpath("Product/Family");
        boolean ifFKFilter = false;
        String value = "Hats";
        MDMConfiguration.getConfiguration().setProperty("xmldb.type", EDBType.QIZX.getName());

        ItemServiceCommonHandler.ForeignKeyHolder result = handler.getForeignKeyHolder(model, ifFKFilter, value);
        assertNotNull(result);
        assertEquals("ProductFamily", result.conceptName);
        assertEquals("", result.fkFilter);
        assertNull(result.orderbyPath);
        assertEquals("[ProductFamily/../../i]", result.xpaths.toString());
        WSWhereAnd whereAnd = result.whereItem.getWhereAnd();
        assertNotNull(whereAnd);
        WSWhereItem[] whereItems = whereAnd.getWhereItems();
        assertNotNull(whereItems);
        assertEquals(1, whereItems.length);
        WSWhereItem whereItem = whereItems[0];
        whereAnd = whereItem.getWhereAnd();
        assertNull(whereAnd);
        WSWhereOr whereOr = whereItem.getWhereOr();
        assertNotNull(whereOr);
        whereItems = whereOr.getWhereItems();
        assertNotNull(whereItems);
        assertEquals(1, whereItems.length);
        whereItem = whereItems[0];
        whereAnd = whereItem.getWhereAnd();
        assertNotNull(whereAnd);
        whereItems = whereAnd.getWhereItems();
        assertNotNull(whereItems);
        assertEquals(1, whereItems.length);
        whereItem = whereItems[0];
        whereAnd = whereItem.getWhereAnd();
        assertNotNull(whereAnd);
        whereItems = whereAnd.getWhereItems();
        assertNotNull(whereItems);
        assertEquals(1, whereItems.length);
        whereItem = whereItems[0];
        WSWhereCondition whereCond =whereItem.getWhereCondition();
        assertNotNull(whereCond);
        assertEquals("ProductFamily/../*", whereCond.getLeftPath());
        assertEquals(WSWhereOperator._CONTAINS, whereCond.getOperator().getValue());
        assertEquals("Hats", whereCond.getRightValueOrPath());

        MDMConfiguration.getConfiguration().setProperty("xmldb.type", EDBType.EXIST.getName());
        model.setRetrieveFKinfos(true);
        result = handler.getForeignKeyHolder(model, ifFKFilter, value);
        assertNotNull(result);
        assertEquals("ProductFamily", result.conceptName);
        assertEquals("", result.fkFilter);
        assertEquals("ProductFamily/Name", result.orderbyPath);
        assertEquals("[ProductFamily/Name, ProductFamily/../../i]", result.xpaths.toString());
        whereAnd = result.whereItem.getWhereAnd();
        assertNotNull(whereAnd);
        whereItems = whereAnd.getWhereItems();
        assertNotNull(whereItems);
        assertEquals(1, whereItems.length);
        whereItem = whereItems[0];
        whereAnd = whereItem.getWhereAnd();
        assertNull(whereAnd);
        whereOr = whereItem.getWhereOr();
        assertNotNull(whereOr);
        whereItems = whereOr.getWhereItems();
        assertNotNull(whereItems);
        assertEquals(1, whereItems.length);
        whereItem = whereItems[0];
        whereAnd = whereItem.getWhereAnd();
        assertNotNull(whereAnd);
        whereItems = whereAnd.getWhereItems();
        assertNotNull(whereItems);
        assertEquals(1, whereItems.length);
        whereItem = whereItems[0];
        whereAnd = whereItem.getWhereAnd();
        assertNotNull(whereAnd);
        whereItems = whereAnd.getWhereItems();
        assertNotNull(whereItems);
        assertEquals(1, whereItems.length);
        whereItem = whereItems[0];
        whereCond =whereItem.getWhereCondition();
        assertNotNull(whereCond);
        assertEquals("ProductFamily/../.", whereCond.getLeftPath());
        assertEquals(WSWhereOperator._CONTAINS, whereCond.getOperator().getValue());
        assertEquals("Hats", whereCond.getRightValueOrPath());

        model.setRetrieveFKinfos(false);
        foreignKeyInfos.add("ProductFamily/FirstName");
        result = handler.getForeignKeyHolder(model, ifFKFilter, value);
        assertNotNull(result);
        assertEquals("[ProductFamily/../../i]", result.xpaths.toString());
        whereAnd = result.whereItem.getWhereAnd();
        assertNotNull(whereAnd);
        whereItems = whereAnd.getWhereItems();
        assertNotNull(whereItems);
        assertEquals(1, whereItems.length);
        whereItem = whereItems[0];
        whereAnd = whereItem.getWhereAnd();
        assertNull(whereAnd);
        whereOr = whereItem.getWhereOr();
        assertNotNull(whereOr);
        whereItems = whereOr.getWhereItems();
        assertNotNull(whereItems);
        assertEquals(1, whereItems.length);
        whereItem = whereItems[0];
        whereAnd = whereItem.getWhereAnd();
        assertNotNull(whereAnd);
        whereItems = whereAnd.getWhereItems();
        assertNotNull(whereItems);
        assertEquals(1, whereItems.length);
        whereItem = whereItems[0];
        whereAnd = whereItem.getWhereAnd();
        assertNotNull(whereAnd);
        whereItems = whereAnd.getWhereItems();
        assertNotNull(whereItems);
        assertEquals(1, whereItems.length);
        whereItem = whereItems[0];
        whereCond =whereItem.getWhereCondition();
        assertNotNull(whereCond);
        assertEquals("ProductFamily/../.", whereCond.getLeftPath());
        assertEquals(WSWhereOperator._CONTAINS, whereCond.getOperator().getValue());
        assertEquals("Hats", whereCond.getRightValueOrPath());

    }
}
