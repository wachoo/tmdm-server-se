/*
 * Copyright (C) 2006-2012 Talend Inc. - www.talend.com
 * 
 * This source code is available under agreement available at
 * %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
 * 
 * You should have received a copy of the agreement along with this program; if not, write to Talend SA 9 rue Pages
 * 92150 Suresnes, France
 */

package com.amalto.core.query;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import junit.framework.TestCase;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import com.amalto.core.metadata.ComplexTypeMetadata;
import com.amalto.core.metadata.MetadataRepository;
import com.amalto.core.storage.record.DataRecord;
import com.amalto.core.storage.record.DataRecordReader;
import com.amalto.core.storage.record.XmlDOMDataRecordReader;
import com.amalto.core.storage.record.XmlSAXDataRecordReader;
import com.amalto.core.storage.record.XmlStringDataRecordReader;
import com.amalto.core.storage.record.metadata.DataRecordMetadata;

@SuppressWarnings("nls")
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
        DataRecord dataRecord = dataRecordReader.read("1", repository, product, builder.toString());

        performAsserts(dataRecord);
    }

    public void testCreationFromXMLStringWithInheritance() throws Exception {
        MetadataRepository repository = new MetadataRepository();
        repository.load(this.getClass().getResourceAsStream("metadata.xsd"));

        ComplexTypeMetadata c = repository.getComplexType("C");
        assertNotNull(c);

        StringBuilder builder = new StringBuilder();
        InputStream testResource = this.getClass().getResourceAsStream("DataRecordCreationTest2.xml");
        BufferedReader reader = new BufferedReader(new InputStreamReader(testResource));
        String current;
        while ((current = reader.readLine()) != null) {
            builder.append(current);
        }

        DataRecordReader<String> dataRecordReader = new XmlStringDataRecordReader();
        DataRecord dataRecord = dataRecordReader.read("1", repository, c, builder.toString());

        performInheritanceAsserts(dataRecord);
    }

    public void testCreationFromSAX() throws Exception {
        MetadataRepository repository = new MetadataRepository();
        repository.load(this.getClass().getResourceAsStream("metadata.xsd"));

        ComplexTypeMetadata product = repository.getComplexType("Product");
        assertNotNull(product);

        XMLReader xmlReader = XMLReaderFactory.createXMLReader();

        DataRecordReader<XmlSAXDataRecordReader.Input> dataRecordReader = new XmlSAXDataRecordReader();
        XmlSAXDataRecordReader.Input input = new XmlSAXDataRecordReader.Input(xmlReader, new InputSource(this.getClass()
                .getResourceAsStream("DataRecordCreationTest.xml")));
        DataRecord dataRecord = dataRecordReader.read("1", repository, product, input);

        performAsserts(dataRecord);
    }

    public void testCreationFromSAXWithInheritance() throws Exception {
        MetadataRepository repository = new MetadataRepository();
        repository.load(this.getClass().getResourceAsStream("metadata.xsd"));

        ComplexTypeMetadata c = repository.getComplexType("C");
        assertNotNull(c);

        XMLReader xmlReader = XMLReaderFactory.createXMLReader();

        DataRecordReader<XmlSAXDataRecordReader.Input> dataRecordReader = new XmlSAXDataRecordReader();
        XmlSAXDataRecordReader.Input input = new XmlSAXDataRecordReader.Input(xmlReader, new InputSource(this.getClass()
                .getResourceAsStream("DataRecordCreationTest2.xml")));
        DataRecord dataRecord = dataRecordReader.read("1", repository, c, input);

        performInheritanceAsserts(dataRecord);
    }

    public void testCreationFromDOM() throws Exception {
        MetadataRepository repository = new MetadataRepository();
        repository.load(this.getClass().getResourceAsStream("metadata.xsd"));

        ComplexTypeMetadata product = repository.getComplexType("Product");
        assertNotNull(product);

        Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder()
                .parse(this.getClass().getResourceAsStream("DataRecordCreationTest.xml"));
        DataRecordReader<Element> dataRecordReader = new XmlDOMDataRecordReader();
        DataRecord dataRecord = dataRecordReader.read("1", repository, product, document.getDocumentElement());

        performAsserts(dataRecord);
    }

    public void testCreationFromDOMWithInheritance() throws Exception {
        MetadataRepository repository = new MetadataRepository();
        repository.load(this.getClass().getResourceAsStream("metadata.xsd"));

        ComplexTypeMetadata c = repository.getComplexType("C");
        assertNotNull(c);

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        DocumentBuilder documentBuilder = factory.newDocumentBuilder();
        Document document = documentBuilder.parse(this.getClass().getResourceAsStream("DataRecordCreationTest2.xml"));
        DataRecordReader<Element> dataRecordReader = new XmlDOMDataRecordReader();
        DataRecord dataRecord = dataRecordReader.read("1", repository, c, document.getDocumentElement());

        performInheritanceAsserts(dataRecord);
    }

    private void performAsserts(DataRecord dataRecord) {
        assertNotNull(dataRecord);
        assertEquals("Product", dataRecord.getType().getName());

        ComplexTypeMetadata product = dataRecord.getType();
        assertEquals("1", dataRecord.get("Id"));
        assertEquals("1", dataRecord.get(product.getField("Id")));
        assertEquals("Test23", dataRecord.get("Name"));
        assertEquals("Test23", dataRecord.get(product.getField("Name")));
        assertNull(dataRecord.get("LongDescription"));
        assertNull(dataRecord.get(product.getField("LongDescription")));
        assertEquals("Pending", dataRecord.get("Status"));
        assertEquals("Pending", dataRecord.get(product.getField("Status")));

        Object o = dataRecord.get("Features/Sizes/Size");
        assertNotNull(o);
        assertTrue(o instanceof List);
        List list = (List) o;
        assertEquals(2, list.size());
        assertEquals("Small", list.get(0));
        assertEquals("Large", list.get(1));

        o = dataRecord.get("Features/Colors/Color");
        assertNotNull(o);
        assertTrue(o instanceof List);
        list = (List) o;
        assertEquals(1, list.size());
        assertEquals("Blue", list.get(0));

        assertNotNull(dataRecord.get("Product"));

        o = dataRecord.get("Supplier");
        assertNotNull(o);
        assertTrue(o instanceof List);
        assertEquals(1, ((List) o).size());
        assertTrue(((DataRecord) ((List) o).get(0)) instanceof DataRecord);
        assertEquals("Supplier", ((DataRecord) ((List) o).get(0)).getType().getName());
        assertEquals("1", ((DataRecord) ((List) o).get(0)).get("Id"));

        o = dataRecord.get("Family");
        assertNotNull(o);
        assertTrue(o instanceof DataRecord);
        assertEquals("ProductFamily", ((DataRecord) o).getType().getName());
        assertEquals("2", ((DataRecord) o).get("Id"));

        // Metadata asserts
        DataRecordMetadata metadata = dataRecord.getRecordMetadata();
        assertNotNull(metadata);

        assertEquals(1328544306381l, metadata.getLastModificationTime());
        assertEquals(null, metadata.getTaskId());
        assertNotNull(metadata.getRecordProperties());
    }

    private void performInheritanceAsserts(DataRecord dataRecord) {
        assertNotNull(dataRecord);
        assertEquals("C", dataRecord.getType().getName());

        assertEquals("2", dataRecord.get("id"));
        assertEquals("TextAC", dataRecord.get("textA"));
        assertEquals("TextCC", dataRecord.get("textC"));

        Object o = dataRecord.get("refB");
        assertNotNull(o);
        assertTrue(o instanceof DataRecord);
        assertEquals("D", ((DataRecord) o).getType().getName());

        o = dataRecord.get("nestedB");
        assertNotNull(o);
        assertTrue(o instanceof DataRecord);
        assertEquals("SubNested", ((DataRecord) o).getType().getName());

        assertEquals("Text", dataRecord.get("nestedB/text"));
        assertEquals("SubText", dataRecord.get("nestedB/subText"));
    }
}
