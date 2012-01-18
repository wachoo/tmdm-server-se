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
package org.talend.mdm.webapp.browserecords.server.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.talend.mdm.webapp.base.shared.SimpleTypeModel;
import org.talend.mdm.webapp.browserecords.client.model.ItemNodeModel;
import org.talend.mdm.webapp.browserecords.client.util.CommonUtil;

import com.extjs.gxt.ui.client.data.ModelData;

@SuppressWarnings("nls")
public class CommonUtilTest extends TestCase {

    public void testGetDefaultTreeModel() {
        try {
            CommonUtil.getDefaultTreeModel(new SimpleTypeModel(), "en");
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
            if (flag)
                flag = false;
            else
                sb.append("/");
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

    public void testPickOutISOMessage() {
        // Sanity check
        String s = "[fr:f][en:e][zh:c]";
        assertTrue(CommonUtil.pickOutISOMessage(s, "en").equals("e"));
        assertTrue(CommonUtil.pickOutISOMessage(s, "fr").equals("f"));
        assertTrue(CommonUtil.pickOutISOMessage(s, "zh").equals("c"));
        
        // Test backslash escaped ] and \ characters
        s = "[fr:f\\]f][en:e\\\\e][zh:c\\]c\\]]";
        assertTrue(CommonUtil.pickOutISOMessage(s, "en").equals("e\\e"));
        assertTrue(CommonUtil.pickOutISOMessage(s, "fr").equals("f]f"));
        assertTrue(CommonUtil.pickOutISOMessage(s, "zh").equals("c]c]"));
        
        // Test default to English if language code not present and english is
        assertTrue(CommonUtil.pickOutISOMessage(s, "sp").equals("e\\e"));
        
        // Test default to whole string when no English
        s = "[fr:f\\]f][zh:c\\]c\\]]";
        assertTrue(CommonUtil.pickOutISOMessage(s, "sp").equals("[fr:f\\]f][zh:c\\]c\\]]"));
        
        // Testing being able to pick out language strings 
        s = "dddd[fr:f\\]f]dddd[en:e\\\\e]ddd[zh:c\\]c\\]]dddd";
        assertTrue(CommonUtil.pickOutISOMessage(s, "en").equals("e\\e"));
        assertTrue(CommonUtil.pickOutISOMessage(s, "fr").equals("f]f"));
        assertTrue(CommonUtil.pickOutISOMessage(s, "zh").equals("c]c]"));
        
        // Testing being able to skip malformed country codes
        s = "dddd[french:f\\]f]dddd[en:e\\\\e]ddd[zh:c\\]c\\]]dddd";
        assertTrue(CommonUtil.pickOutISOMessage(s, "en").equals("e\\e"));
        assertTrue(CommonUtil.pickOutISOMessage(s, "fr").equals("e\\e"));
        assertTrue(CommonUtil.pickOutISOMessage(s, "zh").equals("c]c]"));
        
        // Testing special characters outside of language specific messages
        s = "dd\\\\dd[fr:f\\]f]dd\\[ddd[en:e\\\\e]dd[[d[zh:c\\]c\\]]dddd";
        assertTrue(CommonUtil.pickOutISOMessage(s, "en").equals("e\\e"));
        assertTrue(CommonUtil.pickOutISOMessage(s, "fr").equals("f]f"));
        assertTrue(CommonUtil.pickOutISOMessage(s, "zh").equals("c]c]"));        
    }
}
