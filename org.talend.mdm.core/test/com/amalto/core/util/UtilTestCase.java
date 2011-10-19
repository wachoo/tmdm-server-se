// ============================================================================
//
// Copyright (C) 2006-2011 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package com.amalto.core.util;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.ParserConfigurationException;

import junit.framework.TestCase;

import org.w3c.dom.Element;
import org.xml.sax.SAXException;

/**
 * DOC achen  class global comment. Detailled comment
 */
@SuppressWarnings("nls")
public class UtilTestCase extends TestCase {

    public void testDefaultValidate() throws IOException, ParserConfigurationException, SAXException {
        // missing mandontory field cvc-complex-type.2.4.b
        InputStream in = UtilTestCase.class.getResourceAsStream("Agency_ME02.xml");
        String xml = getStringFromInputStream(in);
        Element element = Util.parse(xml).getDocumentElement();
        InputStream inxsd = UtilTestCase.class.getResourceAsStream("DStar.xsd");
        String schema = getStringFromInputStream(inxsd);

        try {
            Util.defaultValidate(element, schema);
        } catch (Exception e) {
            String str = e.getLocalizedMessage();
            assertTrue(str
                    .contains("cvc-complex-type.2.4.b: The content of element 'Agency' is not complete. One of '{Id}' is expected"));
        }
        // invalid content cvc-complex-type.2.4.

        String invalidXml = "<Agency>aa</Agency>";
        element = Util.parse(invalidXml).getDocumentElement();
        try {
            Util.defaultValidate(element, schema);
        } catch (Exception e) {
            String str = e.getLocalizedMessage();
            assertTrue(str.contains("cvc-complex-type.2.4"));
        }

        // correct xmlstring
        String xmlString = "<Agency xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" + "<Name>Portland</Name>"
                + "<City>Portland</City>" + "<State>ME</State>" + "<Zip>04102</Zip>" + "<Region>EAST</Region>" + "<Id>ME03</Id>"
                + "</Agency>";
        element = Util.parse(xmlString).getDocumentElement();
        try {
            Util.defaultValidate(element, schema);
        } catch (Exception e) {
            throw new SAXException(e);
        }

    }

    private static String getStringFromInputStream(InputStream in) throws IOException {
        int total = in.available();
        byte[] buf = new byte[total];
        in.read(buf);
        return new String(buf);
    }
}
