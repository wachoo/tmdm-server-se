package org.talend.mdm.webapp.browserecords.client.util;

import junit.framework.TestCase;

import org.talend.mdm.webapp.browserecords.client.model.Criteria;

@SuppressWarnings("nls")
public class ParserTest extends TestCase {

    public void testParseString() throws Exception {
        Criteria result = null;
        String testString = "((Test/Id EQUALS *) AND (Test/Id EQUALS *) AND (Test/Id EQUALS *))";

        result = Parser.parse(testString);
        assertEquals(testString, result.toString());
    }

}
