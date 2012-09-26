package com.amalto.core.save.context;

import junit.framework.TestCase;

@SuppressWarnings("nls")
public class BeforeSavingTest extends TestCase {

    public void testValidateFormat() {
        BeforeSaving bs = new BeforeSaving(null);  
        String message = "<report type=\"info\">[EN:1111][FR:222]</report>";
        assertFalse(bs.validateFormat(message));
        
        message = "<report><message type=\"info\">[EN:1111][FR:222]</message></report>";
        assertTrue(bs.validateFormat(message));
        
        message = "<report><message>[EN:1111][FR:222]</message></report>";
        assertFalse(bs.validateFormat(message));
        
        message = "<report><message type=\"info\"></message></report>";
        assertTrue(bs.validateFormat(message));
        
        message = "<report><message type=\"info\"/></report>";
        assertTrue(bs.validateFormat(message));
        
        message = "<report><MessagE type=\"info\">[EN:1111][FR:222]</message></report>";
        assertTrue(bs.validateFormat(message));
    }
}
