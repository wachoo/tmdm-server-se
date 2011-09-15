package com.amalto.webapp.v3.itemsbrowser.dwr;

import java.util.HashMap;

import junit.framework.TestCase;

import com.amalto.webapp.core.bean.UpdateReportItem;
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
