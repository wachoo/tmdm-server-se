package org.talend.mdm.webapp.browserecords.server.util;

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

}
