package org.talend.mdm.webapp.browserecords.server.util;

import java.util.ArrayList;

import junit.framework.TestCase;

import com.amalto.webapp.util.webservices.WSWhereItem;

public class CommonUtilTest extends TestCase {

	public void testJoinStrings() {
		ArrayList testee=new ArrayList();
		testee.add("NJ01");
		testee.add("#@$.");
		testee.add("\\/.");
		assertEquals(CommonUtil.joinStrings(testee, "."),"NJ01.#@$..\\/.");
	}

	public void testBuildWhereItems() throws Exception {
		String inputString="Agency/Id EQUALS *";
		WSWhereItem wi=CommonUtil.buildWhereItem(inputString);
		assertEquals(wi.getWhereAnd().getWhereItems()[0].getWhereCondition().getLeftPath(),"Agency/Id");
		assertEquals(wi.getWhereAnd().getWhereItems()[0].getWhereCondition().getRightValueOrPath(),"*");
		assertEquals(wi.getWhereAnd().getWhereItems()[0].getWhereCondition().getOperator().toString(),"EQUALS");
		
	}

}
