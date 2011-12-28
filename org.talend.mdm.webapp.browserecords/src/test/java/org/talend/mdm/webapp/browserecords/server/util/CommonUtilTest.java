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

public class CommonUtilTest extends TestCase {

    public void testGetDefaultTreeModel() {
        try {
            CommonUtil.getDefaultTreeModel(new SimpleTypeModel(), "en"); //$NON-NLS-1$
            fail();
        } catch (NullPointerException e) {

        }
    }

    public void transferXpath() {
        String xp = "a/b/c/d"; //$NON-NLS-1$
        Map<String, String> map = new HashMap<String, String>();
        map.put("a/b", "hello"); //$NON-NLS-1$//$NON-NLS-2$
        map.put("a/b/c", "peili"); //$NON-NLS-1$//$NON-NLS-2$
        map.put("a/b/c/d", "liang"); //$NON-NLS-1$ //$NON-NLS-2$
        StringBuffer sb = new StringBuffer();
        // a/b/c/d hello peili liang
        Stack<String> stack = new Stack<String>();
        do {
            String v = map.get(xp);
            stack.push(v);
            xp = xp.substring(0, xp.lastIndexOf("/")); //$NON-NLS-1$
        } while (xp.indexOf("/") != -1); //$NON-NLS-1$
        boolean flag = true;

        while (!stack.isEmpty()) {
            if (flag)
                flag = false;
            else
                sb.append("/"); //$NON-NLS-1$
            sb.append(stack.pop());
        }
        Assert.assertEquals("hello/peili/liang", sb.toString()); //$NON-NLS-1$
    }


    public void testGetRealPath() throws Exception {
        ItemNodeModel nodeModel = TestData.getModel();
        // 1. getPathWithIndex
        List<String> xpathes = TestData.getXpathes("xpathes.properties"); //$NON-NLS-1$
        Iterator<String> iter = xpathes.iterator();
        assertPathWithIndex(nodeModel, iter);
        // 2. getRealTypePath
        xpathes = TestData.getXpathes("realTypePathes.properties"); //$NON-NLS-1$
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
        String xpath = "Person:Student/Name"; //$NON-NLS-1$
        xpath = xpath.replaceAll(":\\w+", ""); //$NON-NLS-1$//$NON-NLS-2$
        assertEquals("Person/Name", xpath); //$NON-NLS-1$
    }

}
