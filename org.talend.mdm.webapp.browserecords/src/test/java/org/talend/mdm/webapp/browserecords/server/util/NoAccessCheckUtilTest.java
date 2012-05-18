package org.talend.mdm.webapp.browserecords.server.util;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;

import junit.framework.TestCase;


public class NoAccessCheckUtilTest extends TestCase {
    
    
    private static final List<String> ROLES = Arrays.asList(new String[] { "System_Admin" }); //$NON-NLS-1$
    private static final String XSD = "<xsd:schema xmlns:xsd='http://www.w3.org/2001/XMLSchema'><xsd:import namespace='http://www.w3.org/2001/XMLSchema' /><xsd:element name='M28_E01'><xsd:complexType><xsd:all><xsd:element name='subelement' type='xsd:string' /></xsd:all></xsd:complexType><xsd:unique name='M28_E01'><xsd:selector xpath='.' /><xsd:field xpath='subelement' /></xsd:unique></xsd:element><xsd:element name='M28_E02'><xsd:annotation><xsd:appinfo source='X_Hide'>System_Admin</xsd:appinfo></xsd:annotation><xsd:complexType><xsd:all><xsd:element name='subelement' type='xsd:string'><xsd:annotation><xsd:appinfo source='X_Write'>System_Admin</xsd:appinfo></xsd:annotation></xsd:element></xsd:all></xsd:complexType><xsd:unique name='M28_E02'><xsd:selector xpath='.' /><xsd:field xpath='subelement' /></xsd:unique></xsd:element></xsd:schema>"; //$NON-NLS-1$

    public void testCheckNoAccessHelper() throws Exception {
        String modelXSD = getXSDModel("BrowseRecordsActionTest.xsd"); //$NON-NLS-1$
        assertFalse(NoAccessCheckUtil.checkNoAccessHelper(modelXSD, "M28_E01", ROLES)); //$NON-NLS-1$
        assertTrue(NoAccessCheckUtil.checkNoAccessHelper(modelXSD, "M28_E02", ROLES)); //$NON-NLS-1$
    }
    
    private String getXSDModel(String filename) throws Exception {
        InputStream is = new ByteArrayInputStream(XSD.getBytes("UTF-8")); //$NON-NLS-1$
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(is);
        String XSDModel = com.amalto.core.util.Util.nodeToString(doc);
        return XSDModel;
    }
}
