package org.talend.mdm.webapp.base.server.util;

import org.talend.mdm.webapp.base.server.util.DynamicLabelUtil;

import junit.framework.TestCase;

@SuppressWarnings("nls")
public class DynamicLabelUtilTest extends TestCase {

    public void testIsDynamicLabel() {
        assertFalse(DynamicLabelUtil.isDynamicLabel("D* Agent"));
    }

}
