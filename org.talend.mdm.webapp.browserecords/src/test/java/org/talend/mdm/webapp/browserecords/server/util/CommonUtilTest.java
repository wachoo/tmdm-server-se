package org.talend.mdm.webapp.browserecords.server.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.talend.mdm.webapp.base.shared.SimpleTypeModel;

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
        Assert.assertEquals("hello/peili/liang", sb.toString());
    }

}
