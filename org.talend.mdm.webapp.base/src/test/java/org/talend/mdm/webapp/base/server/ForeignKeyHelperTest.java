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

import com.amalto.webapp.core.util.Util;
import com.amalto.webapp.util.webservices.WSWhereCondition;
import com.amalto.webapp.util.webservices.WSWhereItem;
import com.amalto.webapp.util.webservices.WSWhereOperator;

public class ForeignKeyHelperTest extends TestCase {

    public void testGetForeignKeyHolder() throws Exception {

        TypeModel model = new SimpleTypeModel("family", DataTypeConstants.STRING); //$NON-NLS-1$
        model.setForeignkey("ProductFamily/Id"); //$NON-NLS-1$
        List<String> foreignKeyInfos = new ArrayList<String>();
        foreignKeyInfos.add("ProductFamily/Name"); //$NON-NLS-1$
        model.setForeignKeyInfo(foreignKeyInfos);
        model.setXpath("Product/Family"); //$NON-NLS-1$
        boolean ifFKFilter = false;
        String value = "Hats"; //$NON-NLS-1$

        // 1. ForeignKeyInfo = ProductFamily/Name
        MDMConfiguration.getConfiguration().setProperty("xmldb.type", EDBType.QIZX.getName()); //$NON-NLS-1$
        ForeignKeyHelper.ForeignKeyHolder result = ForeignKeyHelper.getForeignKeyHolder(model, ifFKFilter, value);
        WSWhereItem whereItem = result.whereItem;
        assertNotNull(whereItem);
        assertNotNull(whereItem.getWhereAnd());
        assertNotNull(whereItem.getWhereAnd().getWhereItems());

        WSWhereItem whereItem1 = whereItem.getWhereAnd().getWhereItems()[0];
        WSWhereCondition condition1 = whereItem1.getWhereOr().getWhereItems()[0].getWhereAnd().getWhereItems()[0].getWhereAnd()
                .getWhereItems()[0]
                .getWhereCondition();
        assertEquals("ProductFamily/Name", condition1.getLeftPath()); //$NON-NLS-1$
        assertEquals(WSWhereOperator._CONTAINS, condition1.getOperator().getValue());
        assertEquals("Hats", condition1.getRightValueOrPath()); //$NON-NLS-1$

        WSWhereItem whereItem2 = whereItem1.getWhereOr().getWhereItems()[1];
        WSWhereCondition condition2 = whereItem2.getWhereAnd().getWhereItems()[0].getWhereAnd().getWhereItems()[0]
                .getWhereCondition();
        assertEquals("ProductFamily/Id", condition2.getLeftPath()); //$NON-NLS-1$
        assertEquals(WSWhereOperator._CONTAINS, condition2.getOperator().getValue());
        assertEquals("Hats", condition2.getRightValueOrPath()); //$NON-NLS-1$

        // 2. foreignKeyInfo = ProductFamily/Name,ProductFaimly/Description
        foreignKeyInfos.add("ProductFamily/Description"); //$NON-NLS-1$
        result = ForeignKeyHelper.getForeignKeyHolder(model, ifFKFilter, value);
        whereItem = result.whereItem;
        whereItem1 = whereItem.getWhereAnd().getWhereItems()[0];
        condition1 = whereItem1.getWhereOr().getWhereItems()[0].getWhereAnd().getWhereItems()[0].getWhereAnd().getWhereItems()[0]
                .getWhereCondition();
        assertEquals("ProductFamily/Name", condition1.getLeftPath()); //$NON-NLS-1$
        assertEquals(WSWhereOperator._CONTAINS, condition1.getOperator().getValue());
        assertEquals("Hats", condition1.getRightValueOrPath()); //$NON-NLS-1$

        whereItem2 = whereItem1.getWhereOr().getWhereItems()[1];
        condition2 = whereItem2.getWhereAnd().getWhereItems()[0].getWhereAnd().getWhereItems()[0].getWhereCondition();
        assertEquals("ProductFamily/Description", condition2.getLeftPath()); //$NON-NLS-1$
        assertEquals(WSWhereOperator._CONTAINS, condition2.getOperator().getValue());
        assertEquals("Hats", condition2.getRightValueOrPath()); //$NON-NLS-1$

        WSWhereItem whereItem3 = whereItem1.getWhereOr().getWhereItems()[2];
        WSWhereCondition condition3 = whereItem3.getWhereAnd().getWhereItems()[0].getWhereAnd().getWhereItems()[0]
                .getWhereCondition();
        assertEquals("ProductFamily/Id", condition3.getLeftPath()); //$NON-NLS-1$
        assertEquals(WSWhereOperator._CONTAINS, condition3.getOperator().getValue());
        assertEquals("Hats", condition3.getRightValueOrPath()); //$NON-NLS-1$

        // 3. foreignKeyInfo is null
        foreignKeyInfos.clear();
        result = ForeignKeyHelper.getForeignKeyHolder(model, ifFKFilter, value);
        whereItem = result.whereItem;
        whereItem1 = whereItem.getWhereAnd().getWhereItems()[0];
        condition1 = whereItem1.getWhereOr().getWhereItems()[0].getWhereAnd().getWhereItems()[0].getWhereAnd().getWhereItems()[0]
                .getWhereCondition();
        assertEquals("ProductFamily/../*", condition1.getLeftPath()); //$NON-NLS-1$
        assertEquals(WSWhereOperator._CONTAINS, condition1.getOperator().getValue());
        assertEquals("Hats", condition1.getRightValueOrPath()); //$NON-NLS-1$

        // 4. foreignKey = ProductFamily, foreignKeyInfo = ProductFamily/Name
        String xpathForeignKey = "ProductFamily"; //$NON-NLS-1$
        String xpathInfoForeignKey = "ProductFamily/Name"; //$NON-NLS-1$
        String realXpathForeignKey = "ProductFamily/Id"; //$NON-NLS-1$
        whereItem = mock_SpecialForeignKeyWhereCondition(xpathForeignKey, xpathInfoForeignKey, realXpathForeignKey, value);
        
        assertNotNull(whereItem);
        assertNotNull(whereItem.getWhereOr());
        assertNotNull(whereItem.getWhereOr().getWhereItems());
        assertEquals(2, whereItem.getWhereOr().getWhereItems().length);

        whereItem1 = whereItem.getWhereOr().getWhereItems()[0];
        condition1 = whereItem1.getWhereAnd().getWhereItems()[0].getWhereAnd().getWhereItems()[0]
                .getWhereCondition();
        assertEquals("ProductFamily/Name", condition1.getLeftPath()); //$NON-NLS-1$
        assertEquals(WSWhereOperator._CONTAINS, condition1.getOperator().getValue());
        assertEquals("Hats", condition1.getRightValueOrPath()); //$NON-NLS-1$

        whereItem2 = whereItem.getWhereOr().getWhereItems()[1];
        condition2 = whereItem2.getWhereAnd().getWhereItems()[0].getWhereAnd().getWhereItems()[0]
                .getWhereCondition();
        assertEquals("ProductFamily/Id", condition2.getLeftPath()); //$NON-NLS-1$
        assertEquals(WSWhereOperator._CONTAINS, condition2.getOperator().getValue());
        assertEquals("Hats", condition2.getRightValueOrPath()); //$NON-NLS-1$
    }

    /**
     * Mock org.talend.mdm.webapp.base.server.ForeignKeyHelper.getForeignKeyHolder(TypeModel, boolean, String) <li>when
     * the foreignKey = conceptName, using the method to test where condition.
     * @return
     * @throws Exception
     */
    private WSWhereItem mock_SpecialForeignKeyWhereCondition(String xpathForeignKey, String xpathInfoForeignKey,
            String realXpathForeignKey, String value)
            throws Exception {
        String initXpathForeignKey = Util.getForeignPathFromPath(xpathForeignKey);
        initXpathForeignKey = initXpathForeignKey.split("/")[0]; //$NON-NLS-1$
        String[] fkInfos = xpathInfoForeignKey.split(","); //$NON-NLS-1$
        String fkWhere = initXpathForeignKey + "/../* CONTAINS " + value; //$NON-NLS-1$
        if (xpathInfoForeignKey.trim().length() > 0) {
            StringBuffer ids = new StringBuffer();
            String realForeignKey = null;
            if (xpathForeignKey.indexOf("/") == -1) { //$NON-NLS-1$
                String[] fks = new String[] { realXpathForeignKey };
                if (fks != null && fks.length > 0) {
                    realForeignKey = fks[0];
                    for (int i = 0; i < fks.length; i++) {
                        ids.append(fks[i] + " CONTAINS " + value); //$NON-NLS-1$
                        if (i != fks.length - 1)
                            ids.append(" OR "); //$NON-NLS-1$
                    }
                }
            }
            StringBuffer sb = new StringBuffer();
            for (String fkInfo : fkInfos) {
                sb.append((fkInfo.startsWith(".") ? Util.convertAbsolutePath((realForeignKey != null && realForeignKey.trim() //$NON-NLS-1$
                        .length() > 0) ? realForeignKey : xpathForeignKey, fkInfo) : fkInfo) + " CONTAINS " + value); //$NON-NLS-1$
                sb.append(" OR "); //$NON-NLS-1$
            }
            if (realForeignKey != null)
                sb.append(ids.toString());
            else
                sb.append(xpathForeignKey + " CONTAINS " + value); //$NON-NLS-1$
            fkWhere = sb.toString();
        }

        return Util.buildWhereItems(fkWhere);
    }
}