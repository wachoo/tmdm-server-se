package org.talend.mdm.test.transformer;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllPluginTests {

	public static Test suite() {
		TestSuite suite = new TestSuite(
				"Test for org.talend.mdm.test.transformer");
		//$JUnit-BEGIN$
		suite.addTestSuite(DumpPluginWebserviceTestCase.class);
		suite.addTestSuite(GroovyPluginWebserviceTestCase.class);
		//$JUnit-END$
		return suite;
	}

}
