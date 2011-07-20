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

    //
    public void testDocument2() {
        XMLStreamTokenizer tokenizer = new XMLStreamTokenizer(new ByteArrayInputStream("<Product><Name>Talend Golf Shirt</Name><Description>Golf-style, collared t-shirt</Description><Availability>false</Availability><Price>16.99</Price><Family/><Id>231035935</Id><Picture>/imageserver/upload/TalendShop/golf_shirt.jpg?width=150&amp;amp;height=90&amp;amp;preserveAspectRatio=true</Picture><OnlineStore>Talend Shop@@http://www.cafepress.com/Talend.231035935</OnlineStore></Product><Product><Name>Talend Fitted T-Shirt</Name><Description>Fitted T. ultra-fine combed ring spun cotton</Description><Availability>false</Availability><Price>15.99</Price><Family/><Id>231035936</Id><Picture>/imageserver/upload/TalendShop/tshirt.jpg?width=150&amp;amp;height=90&amp;amp;preserveAspectRatio=true</Picture><OnlineStore>Talend Shop@@http://www.cafepress.com/Talend.231035936</OnlineStore></Product>".getBytes()));
        int docCount = 0;
        String[] expected = new String[]{
                "<Product><Name>Talend Golf Shirt</Name><Description>Golf-style, collared t-shirt</Description><Availability>false</Availability><Price>16.99</Price><Family/><Id>231035935</Id><Picture>/imageserver/upload/TalendShop/golf_shirt.jpg?width=150&amp;amp;height=90&amp;amp;preserveAspectRatio=true</Picture><OnlineStore>Talend Shop@@http://www.cafepress.com/Talend.231035935</OnlineStore></Product>",
                "<Product><Name>Talend Fitted T-Shirt</Name><Description>Fitted T. ultra-fine combed ring spun cotton</Description><Availability>false</Availability><Price>15.99</Price><Family/><Id>231035936</Id><Picture>/imageserver/upload/TalendShop/tshirt.jpg?width=150&amp;amp;height=90&amp;amp;preserveAspectRatio=true</Picture><OnlineStore>Talend Shop@@http://www.cafepress.com/Talend.231035936</OnlineStore></Product>"
        };
        while (tokenizer.hasMoreElements()) {
            assertEquals(expected[docCount], tokenizer.nextElement());
            docCount++;
        }

        assertEquals(2, docCount);
    }

    public void testProcessingInstructions() {
        XMLStreamTokenizer tokenizer = new XMLStreamTokenizer(new ByteArrayInputStream("<?xml version=\"1.0\"?><root></root><?xml version=\"1.0\"?><root></root>".getBytes()));
        int docCount = 0;
        String[] expected = new String[]{
                "<root></root>",
                "<root></root>"
        };
        while (tokenizer.hasMoreElements()) {
            assertEquals(expected[docCount], tokenizer.nextElement());
            docCount++;
        }

        assertEquals(2, docCount);
    }

    public void testMixedRootElements() {
        XMLStreamTokenizer tokenizer = new XMLStreamTokenizer(new ByteArrayInputStream("<root></root><alternative></alternative><root></root><alternative></alternative>".getBytes()));
        int docCount = 0;
        String[] expected = new String[]{
                "<root></root>",
                "<alternative></alternative>",
                "<root></root>",
                "<alternative></alternative>"
        };
        while (tokenizer.hasMoreElements()) {
            assertEquals(expected[docCount], tokenizer.nextElement());
            docCount++;
        }

        assertEquals(4, docCount);
    }
}
