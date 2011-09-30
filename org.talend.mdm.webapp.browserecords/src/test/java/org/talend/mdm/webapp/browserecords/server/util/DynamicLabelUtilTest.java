package org.talend.mdm.webapp.browserecords.server.util;

import junit.framework.TestCase;

public class DynamicLabelUtilTest extends TestCase {

	public void testIsDynamicLabel() {
		assertFalse(DynamicLabelUtil.isDynamicLabel("D* Agent"));
	}

}
