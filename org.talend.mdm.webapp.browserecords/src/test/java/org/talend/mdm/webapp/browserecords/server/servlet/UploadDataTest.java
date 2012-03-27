package org.talend.mdm.webapp.browserecords.server.servlet;

import junit.framework.TestCase;

public class UploadDataTest extends TestCase {

    public void testCheckBeforeSavingErrorMessages() {
        // Check error handling in English
        try {
            String msg = "<xsl:stylesheet xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\" xmlns:mdm=\"java:com.amalto.core.plugin.base.xslt.MdmExtension\" version=\"1.0\"><xsl:output method=\"xml\" indent=\"yes\" omit-xml-declaration=\"yes\"/><xsl:template match=\"/\" priority=\"1\"><report><message type=\"error\">[EN:This is beforeSaving error message][FR:This is beforeSaving french error message]</message></report></xsl:template></xsl:stylesheet>"; //$NON-NLS-1$
            UploadData.checkBeforeSavingErrorMessages(msg, "EN"); //$NON-NLS-1$
            fail();
        }
        catch (Exception e) {
            assertTrue(e.getMessage().equals("This is beforeSaving error message")); //$NON-NLS-1$
        }
        
        // Check error handling in French
        try {
            String msg = "<xsl:stylesheet xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\" xmlns:mdm=\"java:com.amalto.core.plugin.base.xslt.MdmExtension\" version=\"1.0\"><xsl:output method=\"xml\" indent=\"yes\" omit-xml-declaration=\"yes\"/><xsl:template match=\"/\" priority=\"1\"><report><message type=\"error\">[EN:This is beforeSaving error message][FR:This is beforeSaving french error message]</message></report></xsl:template></xsl:stylesheet>"; //$NON-NLS-1$
            UploadData.checkBeforeSavingErrorMessages(msg, "FR"); //$NON-NLS-1$
            fail();
        }
        catch (Exception e) {
            assertTrue(e.getMessage().equals("This is beforeSaving french error message")); //$NON-NLS-1$
        }

        // Check info handling
        try {
            String msg = "<xsl:stylesheet xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\" xmlns:mdm=\"java:com.amalto.core.plugin.base.xslt.MdmExtension\" version=\"1.0\"><xsl:output method=\"xml\" indent=\"yes\" omit-xml-declaration=\"yes\"/><xsl:template match=\"/\" priority=\"1\"><report><message type=\"info\">[EN:This is beforeSaving error message][FR:This is beforeSaving french error message]</message></report></xsl:template></xsl:stylesheet>"; //$NON-NLS-1$
            UploadData.checkBeforeSavingErrorMessages(msg, "EN"); //$NON-NLS-1$            
        }
        catch (Exception e) {
            fail();
        }
    }
}
