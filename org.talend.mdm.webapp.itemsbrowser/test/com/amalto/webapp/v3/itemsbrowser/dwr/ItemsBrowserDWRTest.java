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
package com.amalto.webapp.v3.itemsbrowser.dwr;

import java.util.HashMap;

import junit.framework.TestCase;

import com.amalto.core.webservice.WSWhereOperator;
import com.amalto.webapp.core.bean.UpdateReportItem;
import com.amalto.webapp.core.util.Util;
import com.amalto.webapp.util.webservices.WSWhereCondition;
import com.amalto.webapp.util.webservices.WSWhereItem;
import com.amalto.webapp.v3.itemsbrowser.bean.TreeNode;

@SuppressWarnings("nls")
public class ItemsBrowserDWRTest extends TestCase {

    ItemsBrowserDWR dwr = new ItemsBrowserDWR();

    public void testParseRightValueOrPath() {
        HashMap<String, TreeNode> xpathToTreeNode = new HashMap<String, TreeNode>();
        String key = "/Agency/my_country";
        TreeNode node = new TreeNode();
        node.setName("my_country");
        node.setVisible(true);
        UpdateReportItem report = new UpdateReportItem();
        report.setNewValue("china");
        report.setPath("/Agency/my_country");

        HashMap<String, UpdateReportItem> updatedPath = new HashMap<String, UpdateReportItem>();
        updatedPath.put(key, report);

        String dataObject = "Agency";
        String rightValueOrPath = "Agency/my_country";
        String currentXpath = "/Agency/region";
        String rightValue = dwr.parseRightValueOrPath(xpathToTreeNode, updatedPath, dataObject, rightValueOrPath, currentXpath);

        assertEquals("\"china\"", rightValue);
    }

    public void testUtilBuildWhereItem() throws Exception {
        // TODO To move to com.amalto.webapp.core junit tests
        String criteria = "Agency/Name CONTAINS NYC -";
        WSWhereItem whereItem = com.amalto.webapp.core.util.Util.buildWhereItem(criteria);
        WSWhereCondition condition = whereItem.getWhereAnd().getWhereItems()[0].getWhereCondition();

        String rightValue = condition.getRightValueOrPath();
        assertTrue("NYC -".equals(rightValue));

    }

    public void test_Util_ForeignKeyWhereCondition() throws Exception {
        String value = "Hats";
        String xpathForeignKey = "ProductFamily/Id";
        String xpathInfoForeignKey = "ProductFamily/Name";
        // 1. foreignKeyInfo = ProductFamily/Name
        WSWhereItem whereItem = Mock_UtilForeignKeyWhereCondition(xpathForeignKey, xpathInfoForeignKey, value, true);
        assertNotNull(whereItem);
        assertNotNull(whereItem.getWhereOr());
        assertNotNull(whereItem.getWhereOr().getWhereItems());
        assertEquals(2, whereItem.getWhereOr().getWhereItems().length);

        WSWhereItem whereItem1 = whereItem.getWhereOr().getWhereItems()[0];
        WSWhereCondition condition1 = whereItem1.getWhereAnd().getWhereItems()[0].getWhereAnd().getWhereItems()[0]
                .getWhereCondition();
        assertEquals("ProductFamily/Name", condition1.getLeftPath());
        assertEquals(WSWhereOperator._CONTAINS, condition1.getOperator().getValue());
        assertEquals("Hats", condition1.getRightValueOrPath());

        WSWhereItem whereItem2 = whereItem.getWhereOr().getWhereItems()[1];
        WSWhereCondition condition2 = whereItem2.getWhereAnd().getWhereItems()[0].getWhereAnd().getWhereItems()[0]
                .getWhereCondition();
        assertEquals("ProductFamily/Id", condition2.getLeftPath());
        assertEquals(WSWhereOperator._CONTAINS, condition2.getOperator().getValue());
        assertEquals("Hats", condition2.getRightValueOrPath());

        // 2. foreignKeyInfo = ProductFamily/Name,ProductFaimly/Description
        xpathInfoForeignKey = "ProductFamily/Name,ProductFamily/Description";
        whereItem = Mock_UtilForeignKeyWhereCondition(xpathForeignKey, xpathInfoForeignKey, value, true);
        assertEquals(3, whereItem.getWhereOr().getWhereItems().length);
        whereItem1 = whereItem.getWhereOr().getWhereItems()[0];
        condition1 = whereItem1.getWhereAnd().getWhereItems()[0].getWhereAnd().getWhereItems()[0].getWhereCondition();
        assertEquals("ProductFamily/Name", condition1.getLeftPath());
        assertEquals(WSWhereOperator._CONTAINS, condition1.getOperator().getValue());
        assertEquals("Hats", condition1.getRightValueOrPath());

        whereItem2 = whereItem.getWhereOr().getWhereItems()[1];
        condition2 = whereItem2.getWhereAnd().getWhereItems()[0].getWhereAnd().getWhereItems()[0].getWhereCondition();
        assertEquals("ProductFamily/Description", condition2.getLeftPath());
        assertEquals(WSWhereOperator._CONTAINS, condition2.getOperator().getValue());
        assertEquals("Hats", condition2.getRightValueOrPath());

        WSWhereItem whereItem3 = whereItem.getWhereOr().getWhereItems()[2];
        WSWhereCondition condition3 = whereItem3.getWhereAnd().getWhereItems()[0].getWhereAnd().getWhereItems()[0]
                .getWhereCondition();
        assertEquals("ProductFamily/Id", condition3.getLeftPath());
        assertEquals(WSWhereOperator._CONTAINS, condition3.getOperator().getValue());
        assertEquals("Hats", condition3.getRightValueOrPath());

        // 3. foreignKeyInfo is null
        xpathInfoForeignKey = "";
        whereItem = Mock_UtilForeignKeyWhereCondition(xpathForeignKey, xpathInfoForeignKey, value, true);
        assertEquals(1, whereItem.getWhereOr().getWhereItems().length);
        whereItem1 = whereItem.getWhereOr().getWhereItems()[0];
        condition1 = whereItem1.getWhereAnd().getWhereItems()[0].getWhereAnd().getWhereItems()[0].getWhereCondition();
        assertEquals("ProductFamily/../*", condition1.getLeftPath());
        assertEquals(WSWhereOperator._CONTAINS, condition1.getOperator().getValue());
        assertEquals("Hats", condition1.getRightValueOrPath());

        // 4. foreignKey = ProductFamily, foreignKeyInfo = ProductFamily/Name
        xpathForeignKey = "ProductFamily";
        xpathInfoForeignKey = "ProductFamily/Name";
        whereItem = Mock_UtilForeignKeyWhereCondition(xpathForeignKey, xpathInfoForeignKey, value, true);
        assertNotNull(whereItem);
        assertNotNull(whereItem.getWhereOr());
        assertNotNull(whereItem.getWhereOr().getWhereItems());
        assertEquals(2, whereItem.getWhereOr().getWhereItems().length);

        whereItem1 = whereItem.getWhereOr().getWhereItems()[0];
        condition1 = whereItem1.getWhereAnd().getWhereItems()[0].getWhereAnd().getWhereItems()[0]
                .getWhereCondition();
        assertEquals("ProductFamily/Name", condition1.getLeftPath());
        assertEquals(WSWhereOperator._CONTAINS, condition1.getOperator().getValue());
        assertEquals("Hats", condition1.getRightValueOrPath());

        whereItem2 = whereItem.getWhereOr().getWhereItems()[1];
        condition2 = whereItem2.getWhereAnd().getWhereItems()[0].getWhereAnd().getWhereItems()[0]
                .getWhereCondition();
        assertEquals("ProductFamily/Id", condition2.getLeftPath());
        assertEquals(WSWhereOperator._CONTAINS, condition2.getOperator().getValue());
        assertEquals("Hats", condition2.getRightValueOrPath());

        // 5. foreignKey = ProductFamily, foreignKeyInfo = ProductFamily/Name, isCount = false, xpathForeignKey =
        // ProductFamily/Id;
        whereItem = Mock_UtilForeignKeyWhereCondition(xpathForeignKey, xpathInfoForeignKey, value, false);
        assertNotNull(whereItem);
        assertNotNull(whereItem.getWhereOr());
        assertNotNull(whereItem.getWhereOr().getWhereItems());
        assertEquals(1, whereItem.getWhereOr().getWhereItems().length);

        whereItem1 = whereItem.getWhereOr().getWhereItems()[0];
        condition1 = whereItem1.getWhereAnd().getWhereItems()[0].getWhereAnd().getWhereItems()[0].getWhereCondition();
        assertEquals("ProductFamily/Id", condition1.getLeftPath());
        assertEquals(WSWhereOperator._EQUALS, condition1.getOperator().getValue());
        assertEquals("Hats", condition1.getRightValueOrPath());

    }

    /**
     * DOC Administrator Comment method "Mock_UtilForeignKeyWhereCondition". Mock buildWhereItems in
     * com.amalto.webapp.core.util.Util.getForeignKeyList method<br>
     * isCount : it is true when you use the ForeignKeyWindow to search FK results, it is false when you display a FK
     * item on UI
     * 
     * @return
     * @throws Exception
     */
    private WSWhereItem Mock_UtilForeignKeyWhereCondition(String xpathForeignKey, String xpathInfoForeignKey, String value,
            boolean isCount)
            throws Exception {
        String initXpathForeignKey = Util.getForeignPathFromPath(xpathForeignKey);
        initXpathForeignKey = initXpathForeignKey.split("/")[0];
        String[] fkInfos = xpathInfoForeignKey.split(",");
        String queryKeyWord = isCount ? " CONTAINS " : " EQUALS "; //$NON-NLS-1$ //$NON-NLS-2$
        String fkWhere = initXpathForeignKey + "/../*" + queryKeyWord + value;
        if (xpathInfoForeignKey.trim().length() > 0) {
            StringBuffer ids = new StringBuffer();
            String realForeignKey = null;
            if (xpathForeignKey.indexOf("/") == -1) {
                String[] fks = new String[] { "ProductFamily/Id" };
                if (fks != null && fks.length > 0) {
                    realForeignKey = fks[0];
                    for (int i = 0; i < fks.length; i++) {
                        ids.append(fks[i] + queryKeyWord + value);
                        if (i != fks.length - 1)
                            ids.append(" OR ");
                    }
                }
            }
            StringBuffer sb = new StringBuffer();
            if (isCount)
                for (String fkInfo : fkInfos) {
                    sb.append((fkInfo.startsWith(".") ? Util.convertAbsolutePath((realForeignKey != null && realForeignKey.trim()
                            .length() > 0) ? realForeignKey : xpathForeignKey, fkInfo) : fkInfo)
                            + " CONTAINS " + value);
                    sb.append(" OR ");
                }
            if (realForeignKey != null)
                sb.append(ids.toString());
            else
                sb.append(xpathForeignKey + queryKeyWord + value);
            fkWhere = sb.toString();
        }

        return Util.buildWhereItems(fkWhere);
    }

    public void test_Util_getExceptionMessage() {
        String message = "<msg>[EN:validate error][FR:validate error]</msg>";
        String language = "en";
        // 1
        String actualMsg = Util.getExceptionMessage(message, language);
        assertEquals("validate error", actualMsg);
        // 2
        message = "<msg/>";
        actualMsg = Util.getExceptionMessage(message, language);
        assertEquals("", actualMsg);
        // 3
        message = "<msg>[EN:validate error]</msg>";
        language = "fr";
        actualMsg = Util.getExceptionMessage(message, language);
        assertEquals(message, actualMsg);
        // 4
        message = "<msg>[CHINESE:validate error]</msg>";
        language = "chinese";
        actualMsg = Util.getExceptionMessage(message, language);
        assertEquals("validate error", actualMsg);
        // 5
        message = "<msg>[EN:validate error][FR:fr validate error]</msg>";
        language = "fr";
        actualMsg = Util.getExceptionMessage(message, language);
        assertEquals("fr validate error", actualMsg);
        // 6
        message = "<msg>[EN:validate error][CHINESE:validate error][FR:fr validate error]</msg>";
        language = "fr";
        actualMsg = Util.getExceptionMessage(message, language);
        assertEquals("fr validate error", actualMsg);
        // 7
        message = "[EN:price must > 10][FR:price must > 10]";
        if (message.length() > 0) {
            if (message.indexOf("<msg>") == -1)
                message = "<msg>" + message + "</msg>";
        }
        language = "en";
        actualMsg = Util.getExceptionMessage(message, language);
        assertEquals("price must > 10", actualMsg);

    }
}