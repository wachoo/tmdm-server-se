package org.talend.mdm.webapp.base.server.util;

import java.io.InputStream;

import junit.framework.TestCase;

import org.dom4j.Element;

@SuppressWarnings("nls")
public class XmlUtilTest extends TestCase {

    public void testParseText() throws Exception {
        InputStream is = this.getClass().getClassLoader().getResourceAsStream("temp_ColumnTreeLayout.xml");
        org.dom4j.Document result = XmlUtil.parse(is);
        // each qualified xml should contain this element
        Element panel = (Element) result.selectSingleNode("//mdmform:Panel");
        assertTrue(panel.asXML().indexOf("mdmform:Panel") != -1);
    }

}
