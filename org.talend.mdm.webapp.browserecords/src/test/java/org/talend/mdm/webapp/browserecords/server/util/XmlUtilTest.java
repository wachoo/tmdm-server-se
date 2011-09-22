package org.talend.mdm.webapp.browserecords.server.util;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import junit.framework.TestCase;

import org.dom4j.DocumentException;
import org.dom4j.Element;


public class XmlUtilTest extends TestCase {

    XmlUtil testee = new XmlUtil();

    org.w3c.dom.Document doc = null;


    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testParseText() throws DocumentException, Exception, IOException {
        InputStream is = this.getClass().getClassLoader().getResourceAsStream("temp_ColumnTreeLayout.xml");
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        org.dom4j.Document result = testee.parse(is);
        // each qualified xml should contain this element
        Element panel = (Element) result.selectSingleNode("//mdmform:Panel");
        assertTrue(panel.asXML().indexOf("mdmform:Panel") != -1);
    }

}
