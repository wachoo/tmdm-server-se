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
package com.amalto.webapp.v3.itemsbrowser.util;

import java.io.BufferedInputStream;
import java.util.Iterator;

import junit.framework.TestCase;

import org.dom4j.Document;
import org.dom4j.io.SAXReader;

import com.sun.xml.xsom.XSElementDecl;
import com.sun.xml.xsom.XSSchema;
import com.sun.xml.xsom.XSSchemaSet;
import com.sun.xml.xsom.parser.XSOMParser;

public class DisplayRulesUtilTest extends TestCase {

    private String loadContent() throws Exception {
        byte buffer[] = new byte[1024 * 10];
        BufferedInputStream bis = new BufferedInputStream(this.getClass().getResourceAsStream("style.xslt"));
        bis.read(buffer);
        bis.close();
        return new String(buffer);
    }

    public void testgenDefaultValueStyle() throws Exception {
        SAXReader reader = new SAXReader();
        Document document = reader.read(this.getClass().getResourceAsStream("input.xml"));
        assertNotNull(document);
        XSOMParser parser = new XSOMParser();
        parser.parse(this.getClass().getResourceAsStream("xsd.xml"));
        XSSchemaSet xs = parser.getResult();
        XSElementDecl root = null;
        Iterator<XSSchema> itr = xs.iterateSchema();
        while (itr.hasNext()) {
            XSSchema s = itr.next();
            Iterator<XSElementDecl> jtr = s.iterateElementDecls();
            while (jtr.hasNext()) {
                XSElementDecl e = jtr.next();
                if (e.getName().equals("COUNTERPARTY")) {
                    root = e;
                    break;
                }
            }
        }
        assertNotNull(root);
        String result = new DisplayRulesUtil(root).genDefaultValueStyle(document);
        assertEquals(result.toString(), loadContent().trim().toString());
    }
}
