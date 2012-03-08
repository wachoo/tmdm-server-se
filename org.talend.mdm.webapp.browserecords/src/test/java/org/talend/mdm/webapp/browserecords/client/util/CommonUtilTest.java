package org.talend.mdm.webapp.browserecords.client.util;

import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.talend.mdm.webapp.base.shared.SimpleTypeModel;
import org.talend.mdm.webapp.base.shared.TypeModel;

public class CommonUtilTest extends TestCase {

    public void testPickOutISOMessage() {
        // Sanity check
        String s = "[fr:f][en:e][zh:c]"; //$NON-NLS-1$
        assertTrue(CommonUtil.pickOutISOMessage(s, "en").equals("e")); //$NON-NLS-1$ //$NON-NLS-2$
        assertTrue(CommonUtil.pickOutISOMessage(s, "fr").equals("f")); //$NON-NLS-1$//$NON-NLS-2$
        assertTrue(CommonUtil.pickOutISOMessage(s, "zh").equals("c")); //$NON-NLS-1$ //$NON-NLS-2$

        // Test backslash escaped ] and \ characters
        s = "[fr:f\\]f][en:e\\\\e][zh:c\\]c\\]]"; //$NON-NLS-1$
        assertTrue(CommonUtil.pickOutISOMessage(s, "en").equals("e\\e")); //$NON-NLS-1$//$NON-NLS-2$
        assertTrue(CommonUtil.pickOutISOMessage(s, "fr").equals("f]f")); //$NON-NLS-1$ //$NON-NLS-2$
        assertTrue(CommonUtil.pickOutISOMessage(s, "zh").equals("c]c]")); //$NON-NLS-1$//$NON-NLS-2$

        // Test default to English if language code not present and english is
        assertTrue(CommonUtil.pickOutISOMessage(s, "sp").equals("e\\e")); //$NON-NLS-1$ //$NON-NLS-2$

        // Test default to whole string when no English
        s = "[fr:f\\]f][zh:c\\]c\\]]"; //$NON-NLS-1$
        assertTrue(CommonUtil.pickOutISOMessage(s, "sp").equals("[fr:f\\]f][zh:c\\]c\\]]")); //$NON-NLS-1$ //$NON-NLS-2$

        // Testing being able to pick out language strings
        s = "dddd[fr:f\\]f]dddd[en:e\\\\e]ddd[zh:c\\]c\\]]dddd"; //$NON-NLS-1$
        assertTrue(CommonUtil.pickOutISOMessage(s, "en").equals("e\\e")); //$NON-NLS-1$ //$NON-NLS-2$
        assertTrue(CommonUtil.pickOutISOMessage(s, "fr").equals("f]f")); //$NON-NLS-1$//$NON-NLS-2$
        assertTrue(CommonUtil.pickOutISOMessage(s, "zh").equals("c]c]")); //$NON-NLS-1$ //$NON-NLS-2$

        // Testing being able to skip malformed country codes
        s = "dddd[french:f\\]f]dddd[en:e\\\\e]ddd[zh:c\\]c\\]]dddd"; //$NON-NLS-1$
        assertTrue(CommonUtil.pickOutISOMessage(s, "en").equals("e\\e")); //$NON-NLS-1$//$NON-NLS-2$
        assertTrue(CommonUtil.pickOutISOMessage(s, "fr").equals("e\\e")); //$NON-NLS-1$ //$NON-NLS-2$
        assertTrue(CommonUtil.pickOutISOMessage(s, "zh").equals("c]c]")); //$NON-NLS-1$//$NON-NLS-2$

        // Testing special characters outside of language specific messages
        s = "dd\\\\dd[fr:f\\]f]dd\\[ddd[en:e\\\\e]dd[[d[zh:c\\]c\\]]dddd"; //$NON-NLS-1$
        assertTrue(CommonUtil.pickOutISOMessage(s, "en").equals("e\\e")); //$NON-NLS-1$ //$NON-NLS-2$
        assertTrue(CommonUtil.pickOutISOMessage(s, "fr").equals("f]f")); //$NON-NLS-1$//$NON-NLS-2$
        assertTrue(CommonUtil.pickOutISOMessage(s, "zh").equals("c]c]")); //$NON-NLS-1$ //$NON-NLS-2$
    }
    
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
}
