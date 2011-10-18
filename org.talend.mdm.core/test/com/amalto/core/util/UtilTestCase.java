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
public class UtilTestCase extends TestCase {

    public void testDefaultValidate() throws IOException, ParserConfigurationException, SAXException {
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
    }

    private static String getStringFromInputStream(InputStream in) throws IOException {
        int total = in.available();
        byte[] buf = new byte[total];
        in.read(buf);
        return new String(buf);
    }
}
