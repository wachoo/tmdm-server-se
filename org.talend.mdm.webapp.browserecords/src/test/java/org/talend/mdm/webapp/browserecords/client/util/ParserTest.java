package org.talend.mdm.webapp.browserecords.client.util;

import junit.framework.TestCase;

import org.talend.mdm.webapp.browserecords.client.exception.ParserException;
import org.talend.mdm.webapp.browserecords.client.model.Criteria;

public class ParserTest extends TestCase {

    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testParseString() {
        Criteria result = null;
        String testString = "((Test/Id EQUALS *) AND (Test/Id EQUALS *) AND (Test/Id EQUALS *))";
        try {
            result = Parser.parse(testString);
        } catch (ParserException e) {
            fail(e.getMessage());
        }
        assertEquals(result.toString(), testString);
    }

}
