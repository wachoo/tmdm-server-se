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
package org.talend.mdm.webapp.browserecords.client.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.talend.mdm.webapp.base.client.model.DataTypeConstants;
import org.talend.mdm.webapp.base.shared.SimpleTypeModel;
import org.talend.mdm.webapp.base.shared.TypeModel;
import org.talend.mdm.webapp.browserecords.client.model.ItemNodeModel;
import org.talend.mdm.webapp.browserecords.client.widget.treedetail.TreeDetailUtil;
import org.talend.mdm.webapp.browserecords.server.util.TestData;
import org.talend.mdm.webapp.browserecords.shared.ComplexTypeModel;

import com.amalto.webapp.core.util.Util;
import com.extjs.gxt.ui.client.data.ModelData;

@SuppressWarnings("nls")
public class CommonUtilTest extends TestCase {

    public void testValidateSearchValue() {
        Map<String, TypeModel> xpathMap = new HashMap<String, TypeModel>();
        xpathMap.put("Product/Name", new SimpleTypeModel()); //$NON-NLS-1$
        assertTrue(CommonUtil.validateSearchValue(xpathMap, "/Product/Name")); //$NON-NLS-1$
        assertTrue(CommonUtil.validateSearchValue(xpathMap, "Product/Name")); //$NON-NLS-1$
        assertFalse(CommonUtil.validateSearchValue(xpathMap, "/Product/Name/")); //$NON-NLS-1$
        assertFalse(CommonUtil.validateSearchValue(xpathMap, "/Product//Name")); //$NON-NLS-1$
        assertFalse(CommonUtil.validateSearchValue(xpathMap, "a/b")); //$NON-NLS-1$
        assertTrue(CommonUtil.validateSearchValue(xpathMap, "\"a/b\"")); //$NON-NLS-1$
        assertTrue(CommonUtil.validateSearchValue(xpathMap, "\'a/b\'")); //$NON-NLS-1$
        assertFalse(CommonUtil.validateSearchValue(xpathMap, "\'a/b\"")); //$NON-NLS-1$
        assertFalse(CommonUtil.validateSearchValue(xpathMap, "\"a/b")); //$NON-NLS-1$
        assertTrue(CommonUtil.validateSearchValue(xpathMap, "aaa")); //$NON-NLS-1$
    }

    public void testTypePathToXpath() {

        String result = CommonUtil.typePathToXpath("Eda/typeEda/typeEDA:PointSoutirageRpt/crmaEda"); //$NON-NLS-1$
        assertEquals("Eda/typeEda/typeEDA[@xsi:type='PointSoutirageRpt']/crmaEda", result); //$NON-NLS-1$

        result = CommonUtil.typePathToXpath("Eda/typeEda/typeEDA:PointSoutirageRpt/crmaEda/qqq:www"); //$NON-NLS-1$
        assertEquals("Eda/typeEda/typeEDA[@xsi:type='PointSoutirageRpt']/crmaEda/qqq[@xsi:type='www']", result); //$NON-NLS-1$

    }

    public void testGetDefaultTreeModel() {
        try {
            CommonUtil.getDefaultTreeModel(new SimpleTypeModel(), "en", true);
            fail();
        } catch (NullPointerException e) {

        }
    }

    public void transferXpath() {
        String xp = "a/b/c/d";
        Map<String, String> map = new HashMap<String, String>();
        map.put("a/b", "hello");
        map.put("a/b/c", "peili");
        map.put("a/b/c/d", "liang");
        StringBuffer sb = new StringBuffer();
        // a/b/c/d hello peili liang
        Stack<String> stack = new Stack<String>();
        do {
            String v = map.get(xp);
            stack.push(v);
            xp = xp.substring(0, xp.lastIndexOf("/"));
        } while (xp.indexOf("/") != -1);
        boolean flag = true;

        while (!stack.isEmpty()) {
            if (flag) {
                flag = false;
            } else {
                sb.append("/");
            }
            sb.append(stack.pop());
        }
        Assert.assertEquals("hello/peili/liang", sb.toString());
    }

    public void testGetRealPath() throws Exception {
        ItemNodeModel nodeModel = TestData.getModel();
        // 1. getPathWithIndex
        List<String> xpathes = TestData.getXpathes("xpathes.properties");
        Iterator<String> iter = xpathes.iterator();
        assertPathWithIndex(nodeModel, iter);
        // 2. getRealTypePath
        xpathes = TestData.getXpathes("realTypePathes.properties");
        iter = xpathes.iterator();
        assertRealTypePath(nodeModel, iter);
    }

    private void assertPathWithIndex(ItemNodeModel nodeModel, Iterator<String> iter) {
        String xpath = iter.next();
        String nodePath = CommonUtil.getRealXPath(nodeModel);
        Assert.assertEquals(xpath, nodePath);

        List<ModelData> children = nodeModel.getChildren();
        if (children != null) {
            for (ModelData child : children) {
                assertPathWithIndex((ItemNodeModel) child, iter);
            }
        }
    }

    private void assertRealTypePath(ItemNodeModel nodeModel, Iterator<String> iter) {
        String xpath = iter.next();
        String nodeRealTypePath = CommonUtil.getRealTypePath(nodeModel);
        assertEquals(xpath, nodeRealTypePath);

        List<ModelData> children = nodeModel.getChildren();
        if (children != null) {
            for (ModelData child : children) {
                assertRealTypePath((ItemNodeModel) child, iter);
            }
        }
    }

    public void test_polymorphismTypeXpathRegex() {
        String xpath = "Person:Student/Name";
        xpath = xpath.replaceAll(":\\w+", "");
        assertEquals("Person/Name", xpath);
    }

    public void test_getFKTabLabel() {
        // 1
        String label = "Agency{position()}";
        assertEquals("Agency", LabelUtil.getFKTabLabel(label));
        // 2
        label = "Agency:{position()}";
        assertEquals("Agency", LabelUtil.getFKTabLabel(label));
        // 3
        label = "Agent:Agency{position()}";
        assertEquals("Agent:Agency", LabelUtil.getFKTabLabel(label));
        // 4
        label = "Agency: {position()}";
        assertEquals("Agency", LabelUtil.getFKTabLabel(label));
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
            if (message.indexOf("<msg>") == -1) {
                message = "<msg>" + message + "</msg>";
            }
        }
        language = "en";
        actualMsg = Util.getExceptionMessage(message, language);
        assertEquals("price must > 10", actualMsg);
    }

    public void testGetFKFormat() {
        int result = -1;
        result = org.talend.mdm.webapp.browserecords.server.util.CommonUtil.getFKFormatType(null);
        assertEquals(0, result);

        result = org.talend.mdm.webapp.browserecords.server.util.CommonUtil.getFKFormatType("   ");
        assertEquals(0, result);

        result = org.talend.mdm.webapp.browserecords.server.util.CommonUtil.getFKFormatType("[1]");
        assertEquals(1, result);

        result = org.talend.mdm.webapp.browserecords.server.util.CommonUtil.getFKFormatType("a[1]b");
        assertEquals(0, result);

        result = org.talend.mdm.webapp.browserecords.server.util.CommonUtil.getFKFormatType("[1]-aaa");
        assertEquals(2, result);

        result = org.talend.mdm.webapp.browserecords.server.util.CommonUtil.getFKFormatType("aaa-[1]-aaa");
        assertEquals(0, result);

    }

    public void testGetForeignKeyId() {
        String fk = null;
        fk = org.talend.mdm.webapp.browserecords.server.util.CommonUtil.getForeignKeyId("test", 0);
        assertNull(fk);

        fk = org.talend.mdm.webapp.browserecords.server.util.CommonUtil.getForeignKeyId("[1]", 1);
        assertEquals("[1]", fk);

        fk = org.talend.mdm.webapp.browserecords.server.util.CommonUtil.getForeignKeyId("test", 2);
        assertNull(fk);

        fk = org.talend.mdm.webapp.browserecords.server.util.CommonUtil.getForeignKeyId("[2]-test", 2);
        assertEquals("[2]", fk);
    }

    public void test_isChangeValue() {
        String language = "en";
        boolean isCreate = true;
        // Build a root
        ComplexTypeModel root = new ComplexTypeModel("root", DataTypeConstants.STRING);
        root.addDescription(language, "root");
        // add id to root
        TypeModel idModel = new SimpleTypeModel("id", DataTypeConstants.LONG);
        idModel.addDescription(language, "id");
        root.addSubType(idModel);
        // add name to root
        TypeModel nameModel = new SimpleTypeModel("name", DataTypeConstants.STRING);
        nameModel.addDescription(language, "name");
        nameModel.setDefaultValueExpression("Hello");
        nameModel.setDefaultValue("Hello");
        root.addSubType(nameModel);
        // when create a tree model
        List<ItemNodeModel> list = org.talend.mdm.webapp.browserecords.server.util.CommonUtil.getDefaultTreeModel(root, isCreate,
                language);
        ItemNodeModel rootNode = list.get(0);
        assertTrue(TreeDetailUtil.isChangeValue(rootNode));
        // when display a tree model
        isCreate = false;
        list = org.talend.mdm.webapp.browserecords.server.util.CommonUtil.getDefaultTreeModel(root, isCreate, language);
        rootNode = list.get(0);
        assertFalse(TreeDetailUtil.isChangeValue(rootNode));
    }

    public void testDefaultItemNodeValue() {

        String language = "en";
        boolean isCreate = true;

        ComplexTypeModel typeModel = new ComplexTypeModel("root", DataTypeConstants.STRING);
        typeModel.addDescription(language, "root");

        TypeModel idModel = new SimpleTypeModel("id", DataTypeConstants.LONG);
        typeModel.addSubType(idModel);

        TypeModel nameModel = new SimpleTypeModel("name", DataTypeConstants.STRING);
        nameModel.addDescription(language, "name");
        typeModel.addSubType(nameModel);

        TypeModel ageModel = new SimpleTypeModel("age", DataTypeConstants.INTEGER);
        typeModel.addSubType(ageModel);

        TypeModel dobModel = new SimpleTypeModel("DOB", DataTypeConstants.DATETIME);
        typeModel.addSubType(dobModel);

        TypeModel isActiveModel = new SimpleTypeModel("isActive", DataTypeConstants.BOOLEAN);
        typeModel.addSubType(isActiveModel);

        List<ItemNodeModel> list = org.talend.mdm.webapp.browserecords.server.util.CommonUtil.getDefaultTreeModel(typeModel,
                isCreate, language);

        assertNotNull(list);
        assertTrue(list.size() > 0);

        List<ModelData> children = list.get(0).getChildren();
        for (ModelData modelData : children) {
            ItemNodeModel itemNodeModel = (ItemNodeModel) modelData;
            assertNull(itemNodeModel.getObjectValue());
        }
    }

    public void testGetDownloadFileHeadName() {
        TypeModel typeModel = new SimpleTypeModel("downloadFileHeadName", null);
        assertEquals("downloadFileHeadName", CommonUtil.getDownloadFileHeadName(typeModel));
    }
}
