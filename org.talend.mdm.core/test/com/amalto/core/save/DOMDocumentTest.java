/*
 * Copyright (C) 2006-2012 Talend Inc. - www.talend.com
 * 
 * This source code is available under agreement available at
 * %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
 * 
 * You should have received a copy of the agreement along with this program; if not, write to Talend SA 9 rue Pages
 * 92150 Suresnes, France
 */
package com.amalto.core.save;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import com.amalto.core.util.Util;

import junit.framework.TestCase;

@SuppressWarnings("nls")
public class DOMDocumentTest extends TestCase {
    
    public void testExportToString() throws Exception {
        BufferedReader in = new BufferedReader(new InputStreamReader(DOMDocumentTest.class.getResourceAsStream("test1.xml")));
        String line = null;
        String xml = "";
        while ((line = in.readLine()) != null)
            xml += line;
        DOMDocument doc = new DOMDocument(Util.parse(xml));
        assertNotNull(doc);
        assertNotNull(doc.exportToString());
        assertFalse(doc.exportToString().contains("<?xml version=\"1.0\" encoding=\"UTF-8\"?>"));
    }
}
