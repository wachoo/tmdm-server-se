/*
 * Copyright (C) 2006-2012 Talend Inc. - www.talend.com
 *
 * This source code is available under agreement available at
 * %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
 *
 * You should have received a copy of the agreement
 * along with this program; if not, write to Talend SA
 * 9 rue Pages 92150 Suresnes, France
 */

package com.amalto.core.query;

import com.amalto.core.metadata.ComplexTypeMetadata;
import com.amalto.core.metadata.MetadataRepository;
import com.amalto.core.storage.record.*;
import com.amalto.core.storage.record.metadata.DataRecordMetadata;
import junit.framework.TestCase;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

public class DataRecordCreationTest extends TestCase {

    public void testCreationFromXMLString() throws Exception {
        MetadataRepository repository = new MetadataRepository();
        repository.load(this.getClass().getResourceAsStream("metadata.xsd"));

        ComplexTypeMetadata product = repository.getComplexType("Product");
        assertNotNull(product);

        StringBuilder builder = new StringBuilder();
        InputStream testResource = this.getClass().getResourceAsStream("DataRecordCreationTest.xml");
        BufferedReader reader = new BufferedReader(new InputStreamReader(testResource));
        String current;
        while ((current = reader.readLine()) != null) {
            builder.append(current);
        }

        DataRecordReader<String> dataRecordReader = new XmlStringDataRecordReader();
        DataRecord dataRecord = dataRecordReader.read(1, repository, product, builder.toString());

        performAsserts(product, dataRecord);
    }

    public void testCreationFromSAX() throws Exception {
        MetadataRepository repository = new MetadataRepository();
        repository.load(this.getClass().getResourceAsStream("metadata.xsd"));

        ComplexTypeMetadata product = repository.getComplexType("Product");
        assertNotNull(product);

        XMLReader xmlReader = XMLReaderFactory.createXMLReader();

        DataRecordReader<XmlSAXDataRecordReader.Input> dataRecordReader = new XmlSAXDataRecordReader();
        XmlSAXDataRecordReader.Input input = new XmlSAXDataRecordReader.Input(xmlReader, new InputSource(this.getClass().getResourceAsStream("DataRecordCreationTest.xml")));
        DataRecord dataRecord = dataRecordReader.read(1, repository, product, input);

        performAsserts(product, dataRecord);
    }

    public void testCreationFromDOM() throws Exception {
        MetadataRepository repository = new MetadataRepository();
        repository.load(this.getClass().getResourceAsStream("metadata.xsd"));

        ComplexTypeMetadata product = repository.getComplexType("Product");
        assertNotNull(product);

        Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(this.getClass().getResourceAsStream("DataRecordCreationTest.xml"));
        DataRecordReader<Element> dataRecordReader = new XmlDOMDataRecordReader();
        DataRecord dataRecord = dataRecordReader.read(1, repository, product, document.getDocumentElement());

        performAsserts(product, dataRecord);
    }

    private void performAsserts(ComplexTypeMetadata product, DataRecord dataRecord) {
        assertNotNull(dataRecord);
        assertEquals("Product", dataRecord.getType().getName());

        assertEquals("1", dataRecord.get("Id"));
        assertEquals("1", dataRecord.get(product.getField("Id")));
        assertEquals("Test23", dataRecord.get("Name"));
        assertEquals("Test23", dataRecord.get(product.getField("Name")));
        assertNull(dataRecord.get("LongDescription"));
        assertNull(dataRecord.get(product.getField("LongDescription")));
        assertEquals("Pending", dataRecord.get("Status"));
        assertEquals("Pending", dataRecord.get(product.getField("Status")));

        Object o = dataRecord.get("Features/Sizes/Size");
        assertTrue(o instanceof List);
        List list = (List) o;
        assertEquals(2, list.size());
        assertEquals("Small", list.get(0));
        assertEquals("Large", list.get(1));

        o = dataRecord.get("Features/Colors/Color");
        assertTrue(o instanceof List);
        list = (List) o;
        assertEquals(1, list.size());
        assertEquals("Blue", list.get(0));

        // Metadata asserts
        DataRecordMetadata metadata = dataRecord.getRecordMetadata();
        assertNotNull(metadata);

        assertEquals(1328544306381l, metadata.getLastModificationTime());
        assertEquals(null, metadata.getTaskId());
        assertNotNull(metadata.getRecordProperties());
    }


}
