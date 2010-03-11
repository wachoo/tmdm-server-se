package org.talend.mdm.test;

import junit.framework.TestSuite;
import talend.mdm.test.MDMTestCase;

public class MainTestCase extends MDMTestCase {

	public static void main(String[] args) {
		TestSuite testSuite = new TestSuite();
		
		testSuite.addTestSuite(BackgroundJobWebserviceTestCase.class);
		testSuite.addTestSuite(CommonWebserviceTestCase.class);
		testSuite.addTestSuite(DataClusterWebserviceTestCase.class);
		testSuite.addTestSuite(ItemsWebserviceTestCase.class);
		testSuite.addTestSuite(MenuWebserviceTestCase.class);
		testSuite.addTestSuite(RoutingWebserviceTestCase.class);
		testSuite.addTestSuite(SearchWebserviceTestCase.class);
		testSuite.addTestSuite(SERVICESWebserviceTestCase.class);
		testSuite.addTestSuite(StoredProcedureWebserviceTestCase.class);
		testSuite.addTestSuite(UniverseWebserviceTestCase.class);
		testSuite.addTestSuite(ViewWebserviceTestCase.class);
		testSuite.addTestSuite(XtentisJCAConnectorSupportWebserviceTestCase.class);
		
		junit.textui.TestRunner.run(testSuite);
	}

}
