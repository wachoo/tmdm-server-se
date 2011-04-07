/*
 * Copyright (C) 2006-2011 Talend Inc. - www.talend.com
 *
 * This source code is available under agreement available at
 * %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
 *
 * You should have received a copy of the agreement
 * along with this program; if not, write to Talend SA
 * 9 rue Pages 92150 Suresnes, France
 */

package com.amalto.core.load;

import java.io.ByteArrayInputStream;

import com.amalto.core.load.io.XMLStreamTokenizer;
import junit.framework.TestCase;

/**
 *
 */
public class XMLStreamTokenizerTest extends TestCase {
    public void testFail() {
        XMLStreamTokenizer tokenizer = new XMLStreamTokenizer(new ByteArrayInputStream("<root></root>".getBytes()));
        try {
            tokenizer.nextElement();
            fail("Should call hasMoreElements before next");
        } catch (Exception e) {
            // Expected
        }
    }

    public void testSingleDocument() {
        XMLStreamTokenizer tokenizer = new XMLStreamTokenizer(new ByteArrayInputStream("<root></root>".getBytes()));
        int docCount = 0;
        while (tokenizer.hasMoreElements()) {
            assertEquals("<root></root>", tokenizer.nextElement());
            docCount++;
        }

        assertEquals(1, docCount);
    }

    public void testDocuments() {
        XMLStreamTokenizer tokenizer = new XMLStreamTokenizer(new ByteArrayInputStream("<root></root><root></root>".getBytes()));
        int docCount = 0;
        while (tokenizer.hasMoreElements()) {
            assertEquals("<root></root>", tokenizer.nextElement());
            docCount++;
        }

        assertEquals(2, docCount);
    }

    public void testNestedDocuments() {
        XMLStreamTokenizer tokenizer = new XMLStreamTokenizer(new ByteArrayInputStream("<root><root></root></root>".getBytes()));
        int docCount = 0;
        while (tokenizer.hasMoreElements()) {
            assertEquals("<root><root></root></root>", tokenizer.nextElement());
            docCount++;
        }

        assertEquals(1, docCount);
    }

    public void testDocumentsWithAttributes() {
        XMLStreamTokenizer tokenizer = new XMLStreamTokenizer(new ByteArrayInputStream("<root xsi:type=\"MyType<>\"></root>".getBytes()));
        int docCount = 0;
        while (tokenizer.hasMoreElements()) {
            assertEquals("<root xsi:type=\"MyType<>\"></root>", tokenizer.nextElement());
            docCount++;
        }

        assertEquals(1, docCount);
    }

    //
    public void testDocument() {
        XMLStreamTokenizer tokenizer = new XMLStreamTokenizer(new ByteArrayInputStream("<ProductFamily><Id>2</Id><Name>Family 2</Name></ProductFamily><ProductFamily><Id>3</Id><Name>Family 3</Name></ProductFamily>".getBytes()));
        int docCount = 0;
        String[] expected = new String[]{
                "<ProductFamily><Id>2</Id><Name>Family 2</Name></ProductFamily>",
                "<ProductFamily><Id>3</Id><Name>Family 3</Name></ProductFamily>"
        };
        while (tokenizer.hasMoreElements()) {
            assertEquals(expected[docCount], tokenizer.nextElement());
            docCount++;
        }

        assertEquals(2, docCount);
    }
}
