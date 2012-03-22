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
}
