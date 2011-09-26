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
package org.talend.mdm.webapp.base.server;

import java.util.ArrayList;
import java.util.List;

import org.talend.mdm.commmon.util.core.EDBType;
import org.talend.mdm.commmon.util.core.MDMConfiguration;
import org.talend.mdm.webapp.base.client.model.DataTypeConstants;
import org.talend.mdm.webapp.base.shared.SimpleTypeModel;
import org.talend.mdm.webapp.base.shared.TypeModel;
import org.talend.mdm.webapp.base.server.ForeignKeyHelper;

import com.amalto.webapp.util.webservices.WSWhereAnd;
import com.amalto.webapp.util.webservices.WSWhereCondition;
import com.amalto.webapp.util.webservices.WSWhereItem;
import com.amalto.webapp.util.webservices.WSWhereOperator;
import com.amalto.webapp.util.webservices.WSWhereOr;

import junit.framework.TestCase;

@SuppressWarnings("nls")
public class ForeignKeyHelperTest extends TestCase {

    public void testGetForeignKeyHolder() throws Exception {

        TypeModel model = new SimpleTypeModel("family", DataTypeConstants.STRING);
        model.setForeignkey("ProductFamily/Id");
        List<String> foreignKeyInfos = new ArrayList<String>();
        foreignKeyInfos.add("ProductFamily/Name");
        model.setForeignKeyInfo(foreignKeyInfos);
        model.setXpath("Product/Family");
        boolean ifFKFilter = false;
        String value = "Hats";
        MDMConfiguration.getConfiguration().setProperty("xmldb.type", EDBType.QIZX.getName());

        ForeignKeyHelper.ForeignKeyHolder result = ForeignKeyHelper.getForeignKeyHolder(model, ifFKFilter, value);
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
        WSWhereCondition whereCond = whereItem.getWhereCondition();
        assertNotNull(whereCond);
        assertEquals("ProductFamily/../*", whereCond.getLeftPath());
        assertEquals(WSWhereOperator._CONTAINS, whereCond.getOperator().getValue());
        assertEquals("Hats", whereCond.getRightValueOrPath());

        MDMConfiguration.getConfiguration().setProperty("xmldb.type", EDBType.EXIST.getName());
        model.setRetrieveFKinfos(true);
        result = ForeignKeyHelper.getForeignKeyHolder(model, ifFKFilter, value);
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
        whereCond = whereItem.getWhereCondition();
        assertNotNull(whereCond);
        assertEquals("ProductFamily/../.", whereCond.getLeftPath());
        assertEquals(WSWhereOperator._CONTAINS, whereCond.getOperator().getValue());
        assertEquals("Hats", whereCond.getRightValueOrPath());

        model.setRetrieveFKinfos(false);
        foreignKeyInfos.add("ProductFamily/FirstName");
        result = ForeignKeyHelper.getForeignKeyHolder(model, ifFKFilter, value);
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
        whereCond = whereItem.getWhereCondition();
        assertNotNull(whereCond);
        assertEquals("ProductFamily/../.", whereCond.getLeftPath());
        assertEquals(WSWhereOperator._CONTAINS, whereCond.getOperator().getValue());
        assertEquals("Hats", whereCond.getRightValueOrPath());

    }
}
