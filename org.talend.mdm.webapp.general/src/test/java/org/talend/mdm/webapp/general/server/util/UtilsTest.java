// ============================================================================
//
// Copyright (C) 2006-2012 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.mdm.webapp.general.server.util;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import junit.framework.TestCase;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import com.amalto.commons.core.utils.XMLUtils;

@SuppressWarnings("nls")
public class UtilsTest extends TestCase {

    public void testSetLanguages() throws Exception {
        String xml = "<User><username>administrator</username><password>200ceb26807d6bf99fd6f4f0d1ca54d4</password><givenname>Default</givenname><familyname>Administrator</familyname><company>Company</company><id>null</id><realemail>admin@company.com</realemail><viewrealemail>no</viewrealemail><registrationdate>1393637417135</registrationdate><lastvisitdate>0</lastvisitdate><enabled>yes</enabled><homepage>Home</homepage><language>en</language><roles><role>System_Admin</role><role>administration</role></roles></User>";
        assertEquals("en", xml.substring(xml.indexOf("<language>") + 10, xml.indexOf("</language>")));
        xml = Utils.setLanguage(xml, "fr");
        assertEquals("fr", xml.substring(xml.indexOf("<language>") + 10, xml.indexOf("</language>")));
    }

    public void testAddLanguages() throws DOMException, TransformerException, ParserConfigurationException, IOException,
            SAXException {
        String xml = "<User></User>";
        Document doc = XMLUtils.parse(xml);
        assertNull(doc.getElementsByTagName("language").item(0));
        Element node = doc.createElement("language");
        node.setTextContent("en");
        doc.getDocumentElement().appendChild(node);
        String newXml = XMLUtils.nodeToString(doc);
        assertEquals("en", XMLUtils.parse(newXml).getElementsByTagName("language").item(0).getTextContent());
    }
}
