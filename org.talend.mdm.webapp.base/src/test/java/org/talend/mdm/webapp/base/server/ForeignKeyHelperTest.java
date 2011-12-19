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

import junit.framework.TestCase;

import org.talend.mdm.commmon.util.core.EDBType;
import org.talend.mdm.commmon.util.core.MDMConfiguration;
import org.talend.mdm.webapp.base.client.model.DataTypeConstants;
import org.talend.mdm.webapp.base.shared.SimpleTypeModel;
import org.talend.mdm.webapp.base.shared.TypeModel;

import com.amalto.webapp.util.webservices.WSWhereCondition;
import com.amalto.webapp.util.webservices.WSWhereItem;
import com.amalto.webapp.util.webservices.WSWhereOperator;

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

        // 1. ForeignKeyInfo = ProductFamily/Name
        MDMConfiguration.getConfiguration().setProperty("xmldb.type", EDBType.QIZX.getName());
        ForeignKeyHelper.ForeignKeyHolder result = ForeignKeyHelper.getForeignKeyHolder(model, ifFKFilter, value);
        WSWhereItem whereItem = result.whereItem;
        assertNotNull(whereItem);
        assertNotNull(whereItem.getWhereAnd());
        assertNotNull(whereItem.getWhereAnd().getWhereItems());

        WSWhereItem whereItem1 = whereItem.getWhereAnd().getWhereItems()[0];
        WSWhereCondition condition1 = whereItem1.getWhereOr().getWhereItems()[0].getWhereAnd().getWhereItems()[0].getWhereAnd()
                .getWhereItems()[0]
                .getWhereCondition();
        assertEquals("ProductFamily/Name", condition1.getLeftPath());
        assertEquals(WSWhereOperator._CONTAINS, condition1.getOperator().getValue());
        assertEquals("Hats", condition1.getRightValueOrPath());

        WSWhereItem whereItem2 = whereItem1.getWhereOr().getWhereItems()[1];
        WSWhereCondition condition2 = whereItem2.getWhereAnd().getWhereItems()[0].getWhereAnd().getWhereItems()[0]
                .getWhereCondition();
        assertEquals("ProductFamily/Id", condition2.getLeftPath());
        assertEquals(WSWhereOperator._CONTAINS, condition2.getOperator().getValue());
        assertEquals("Hats", condition2.getRightValueOrPath());

        // 2. foreignKeyInfo = ProductFamily/Name,ProductFaimly/Description
        foreignKeyInfos.add("ProductFamily/Description");
        result = ForeignKeyHelper.getForeignKeyHolder(model, ifFKFilter, value);
        whereItem = result.whereItem;
        whereItem1 = whereItem.getWhereAnd().getWhereItems()[0];
        condition1 = whereItem1.getWhereOr().getWhereItems()[0].getWhereAnd().getWhereItems()[0].getWhereAnd().getWhereItems()[0]
                .getWhereCondition();
        assertEquals("ProductFamily/Name", condition1.getLeftPath());
        assertEquals(WSWhereOperator._CONTAINS, condition1.getOperator().getValue());
        assertEquals("Hats", condition1.getRightValueOrPath());

        whereItem2 = whereItem1.getWhereOr().getWhereItems()[1];
        condition2 = whereItem2.getWhereAnd().getWhereItems()[0].getWhereAnd().getWhereItems()[0].getWhereCondition();
        assertEquals("ProductFamily/Description", condition2.getLeftPath());
        assertEquals(WSWhereOperator._CONTAINS, condition2.getOperator().getValue());
        assertEquals("Hats", condition2.getRightValueOrPath());

        WSWhereItem whereItem3 = whereItem1.getWhereOr().getWhereItems()[2];
        WSWhereCondition condition3 = whereItem3.getWhereAnd().getWhereItems()[0].getWhereAnd().getWhereItems()[0]
                .getWhereCondition();
        assertEquals("ProductFamily/Id", condition3.getLeftPath());
        assertEquals(WSWhereOperator._CONTAINS, condition3.getOperator().getValue());
        assertEquals("Hats", condition3.getRightValueOrPath());

        // 3. foreignKeyInfo is null
        foreignKeyInfos.clear();
        result = ForeignKeyHelper.getForeignKeyHolder(model, ifFKFilter, value);
        whereItem = result.whereItem;
        whereItem1 = whereItem.getWhereAnd().getWhereItems()[0];
        condition1 = whereItem1.getWhereOr().getWhereItems()[0].getWhereAnd().getWhereItems()[0].getWhereAnd().getWhereItems()[0]
                .getWhereCondition();
        assertEquals("ProductFamily/../*", condition1.getLeftPath());
        assertEquals(WSWhereOperator._CONTAINS, condition1.getOperator().getValue());
        assertEquals("Hats", condition1.getRightValueOrPath());
    }
}
