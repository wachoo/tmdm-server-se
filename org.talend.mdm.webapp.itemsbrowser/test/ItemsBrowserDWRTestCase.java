import java.util.HashMap;

import talend.mdm.test.MDMTestCase;

import com.amalto.webapp.core.bean.UpdateReportItem;
import com.amalto.webapp.v3.itemsbrowser.bean.TreeNode;
import com.amalto.webapp.v3.itemsbrowser.dwr.ItemsBrowserDWR;

public class ItemsBrowserDWRTestCase extends MDMTestCase {

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
}
