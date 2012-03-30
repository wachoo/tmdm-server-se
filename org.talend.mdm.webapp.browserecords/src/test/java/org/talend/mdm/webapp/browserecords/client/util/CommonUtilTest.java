package org.talend.mdm.webapp.browserecords.client.util;

import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.talend.mdm.webapp.base.shared.SimpleTypeModel;
import org.talend.mdm.webapp.base.shared.TypeModel;

public class CommonUtilTest extends TestCase {

    
    public void testValidateSearchValue(){
        Map<String, TypeModel> xpathMap = new HashMap<String,TypeModel>();        
        xpathMap.put("Product/Name", new SimpleTypeModel()); //$NON-NLS-1$
        assertTrue(CommonUtil.validateSearchValue(xpathMap, "/Product/Name")); //$NON-NLS-1$
        assertTrue(CommonUtil.validateSearchValue(xpathMap, "Product/Name")); //$NON-NLS-1$
        assertFalse(CommonUtil.validateSearchValue(xpathMap, "/Product/Name/")); //$NON-NLS-1$
        assertFalse(CommonUtil.validateSearchValue(xpathMap, "/Product//Name")); //$NON-NLS-1$
        assertFalse(CommonUtil.validateSearchValue(xpathMap, "a/b")); //$NON-NLS-1$
        assertTrue(CommonUtil.validateSearchValue(xpathMap, "\"a/b\"")); //$NON-NLS-1$
        assertTrue(CommonUtil.validateSearchValue(xpathMap, "\'a/b\'")); //$NON-NLS-1$
        assertFalse(CommonUtil.validateSearchValue(xpathMap, "\'a/b\""));   //$NON-NLS-1$
        assertFalse(CommonUtil.validateSearchValue(xpathMap, "\"a/b"));    //$NON-NLS-1$
        assertTrue(CommonUtil.validateSearchValue(xpathMap, "aaa")); //$NON-NLS-1$
    }

    /**
     * DOC Starkey Comment method "testParseFileName".
     */
    public void testParseFileName() {

        // test filename
        String path = "hshu.jpg"; //$NON-NLS-1$
        assertEquals(CommonUtil.parseFileName(path)[0], "hshu"); //$NON-NLS-1$
        assertEquals(CommonUtil.parseFileName(path)[1], "jpg"); //$NON-NLS-1$

        // test for case "\"
        String path2 = "C:\\fakepath\\hshu.jpg"; //$NON-NLS-1$
        assertEquals(CommonUtil.parseFileName(path2)[0], "hshu"); //$NON-NLS-1$
        assertEquals(CommonUtil.parseFileName(path2)[1], "jpg"); //$NON-NLS-1$

        // test for case "/"
        String path3 = "C:/fakepath/hshu.jpg"; //$NON-NLS-1$
        assertEquals(CommonUtil.parseFileName(path3)[0], "hshu"); //$NON-NLS-1$
        assertEquals(CommonUtil.parseFileName(path3)[1], "jpg"); //$NON-NLS-1$

    }
}
