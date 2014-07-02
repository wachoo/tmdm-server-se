/*
 * Copyright (C) 2006-2014 Talend Inc. - www.talend.com
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
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.talend.mdm.commmon.metadata.ComplexTypeMetadata;
import org.talend.mdm.commmon.metadata.MetadataRepository;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import com.amalto.core.storage.Storage;
import com.amalto.core.storage.record.*;
import com.amalto.core.storage.record.metadata.DataRecordMetadata;

@SuppressWarnings("nls")
public class DataRecordCreationTest extends StorageTestCase {

    public void testCreationFromXMLString() throws Exception {
        MetadataRepository repository = new MetadataRepository();
        repository.load(this.getClass().getResourceAsStream("metadata.xsd"));

        ComplexTypeMetadata product = repository.getComplexType("Product");
        assertNotNull(product);

        StringBuilder builder = new StringBuilder();
        InputStream testResource = this.getClass().getResourceAsStream("DataRecordCreationTest_1.xml");
        BufferedReader reader = new BufferedReader(new InputStreamReader(testResource));
        String current;
        while ((current = reader.readLine()) != null) {
            builder.append(current);
        }

        DataRecordReader<String> dataRecordReader = new XmlStringDataRecordReader();
        DataRecord dataRecord = dataRecordReader.read("1", repository, product, builder.toString());

        performAsserts(dataRecord);
    }

    public void testCreationFromXMLStringWithMetadata() throws Exception {
        MetadataRepository repository = new MetadataRepository();
        repository.load(this.getClass().getResourceAsStream("metadata.xsd"));

        ComplexTypeMetadata product = repository.getComplexType("Product");
        assertNotNull(product);

        StringBuilder builder = new StringBuilder();
        InputStream testResource = this.getClass().getResourceAsStream("DataRecordCreationTest_3.xml");
        BufferedReader reader = new BufferedReader(new InputStreamReader(testResource));
        String current;
        while ((current = reader.readLine()) != null) {
            builder.append(current);
        }

        DataRecordReader<String> dataRecordReader = new XmlStringDataRecordReader();
        DataRecord dataRecord = dataRecordReader.read("1", repository, product, builder.toString());

        performAsserts(dataRecord);
        performMetadataAsserts(dataRecord);
    }

    private void performMetadataAsserts(DataRecord dataRecord) {
        DataRecordMetadata recordMetadata = dataRecord.getRecordMetadata();
        assertEquals("1234", recordMetadata.getTaskId());
        Map<String, String> recordProperties = recordMetadata.getRecordProperties();
        assertEquals("My Source", recordProperties.get(Storage.METADATA_STAGING_SOURCE));
        assertEquals("My Error", recordProperties.get(Storage.METADATA_STAGING_ERROR));
        assertEquals("999", recordProperties.get(Storage.METADATA_STAGING_STATUS));
        assertEquals("5678", recordProperties.get(Storage.METADATA_STAGING_BLOCK_KEY));
    }

    public void testCreationFromXMLStringWithInheritance() throws Exception {
        MetadataRepository repository = new MetadataRepository();
        repository.load(this.getClass().getResourceAsStream("metadata.xsd"));

        ComplexTypeMetadata c = repository.getComplexType("C");
        assertNotNull(c);

        StringBuilder builder = new StringBuilder();
        InputStream testResource = this.getClass().getResourceAsStream("DataRecordCreationTest_2.xml");
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
                .getResourceAsStream("DataRecordCreationTest_1.xml")));
        DataRecord dataRecord = dataRecordReader.read("1", repository, product, input);

        performAsserts(dataRecord);
    }

    public void testCreationFromSAXWithMetadata() throws Exception {
        MetadataRepository repository = new MetadataRepository();
        repository.load(this.getClass().getResourceAsStream("metadata.xsd"));

        ComplexTypeMetadata product = repository.getComplexType("Product");
        assertNotNull(product);

        XMLReader xmlReader = XMLReaderFactory.createXMLReader();

        DataRecordReader<XmlSAXDataRecordReader.Input> dataRecordReader = new XmlSAXDataRecordReader();
        XmlSAXDataRecordReader.Input input = new XmlSAXDataRecordReader.Input(xmlReader, new InputSource(this.getClass()
                .getResourceAsStream("DataRecordCreationTest_3.xml")));
        DataRecord dataRecord = dataRecordReader.read("1", repository, product, input);

        performAsserts(dataRecord);
        performMetadataAsserts(dataRecord);
    }

    public void testCreationFromSAXWithInheritance() throws Exception {
        MetadataRepository repository = new MetadataRepository();
        repository.load(this.getClass().getResourceAsStream("metadata.xsd"));

        ComplexTypeMetadata c = repository.getComplexType("C");
        assertNotNull(c);

        XMLReader xmlReader = XMLReaderFactory.createXMLReader();

        DataRecordReader<XmlSAXDataRecordReader.Input> dataRecordReader = new XmlSAXDataRecordReader();
        XmlSAXDataRecordReader.Input input = new XmlSAXDataRecordReader.Input(xmlReader, new InputSource(this.getClass()
                .getResourceAsStream("DataRecordCreationTest_2.xml")));
        DataRecord dataRecord = dataRecordReader.read("1", repository, c, input);

        performInheritanceAsserts(dataRecord);
    }

    public void testCreationFromDOM() throws Exception {
        MetadataRepository repository = new MetadataRepository();
        repository.load(this.getClass().getResourceAsStream("metadata.xsd"));

        ComplexTypeMetadata product = repository.getComplexType("Product");
        assertNotNull(product);

        Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder()
                .parse(this.getClass().getResourceAsStream("DataRecordCreationTest_1.xml"));
        DataRecordReader<Element> dataRecordReader = new XmlDOMDataRecordReader();
        DataRecord dataRecord = dataRecordReader.read("1", repository, product, document.getDocumentElement());

        performAsserts(dataRecord);
    }

    public void testCreationFromDOMWithMetadata() throws Exception {
        MetadataRepository repository = new MetadataRepository();
        repository.load(this.getClass().getResourceAsStream("metadata.xsd"));

        ComplexTypeMetadata product = repository.getComplexType("Product");
        assertNotNull(product);

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        DocumentBuilder documentBuilder = factory.newDocumentBuilder();
        InputStream stream = this.getClass().getResourceAsStream("DataRecordCreationTest_3.xml");
        Document document = documentBuilder.parse(stream);
        DataRecordReader<Element> dataRecordReader = new XmlDOMDataRecordReader();
        DataRecord dataRecord = dataRecordReader.read("1", repository, product, document.getDocumentElement());

        performAsserts(dataRecord);
        performMetadataAsserts(dataRecord);
    }

    public void testCreationFromDOMWithInheritance() throws Exception {
        MetadataRepository repository = new MetadataRepository();
        repository.load(this.getClass().getResourceAsStream("metadata.xsd"));

        ComplexTypeMetadata c = repository.getComplexType("C");
        assertNotNull(c);

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        DocumentBuilder documentBuilder = factory.newDocumentBuilder();
        Document document = documentBuilder.parse(this.getClass().getResourceAsStream("DataRecordCreationTest_2.xml"));
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
        assertTrue(((List) o).get(0) instanceof DataRecord);
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

    public void testUserXmlData() {
        DataRecordReader<String> xmlReader = new XmlStringDataRecordReader();
        DataRecord r1 = xmlReader.read("1", repository, b, "<B><id>1</id><textB>TextB</textB></B>");
        DataRecord r2 = xmlReader.read("1", repository, d, "<D><id>2</id><textB>TextBD</textB><textD>TextDD</textD></D>");
        DataRecord r3 = xmlReader.read("1", repository, persons, "<Persons><name>person</name><age>20</age></Persons>");
        DataRecord r4 = xmlReader.read("1", repository, employee,
                "<Employee><name>employee</name><age>21</age><jobTitle>Test</jobTitle></Employee>");
        DataRecord r5 = xmlReader.read("1", repository, manager,
                "<Manager><name>manager</name><age>25</age><jobTitle>Test</jobTitle><dept>manager</dept></Manager>");

        assertNotNull(r1);
        assertEquals("1", r1.get(b.getField("id")));
        assertEquals("TextB", r1.get(b.getField("textB")));

        assertNotNull(r2);
        assertEquals("2", r2.get(d.getField("id")));
        assertEquals("TextBD", r2.get(d.getField("textB")));
        assertEquals("TextDD", r2.get(d.getField("textD")));

        assertNotNull(r3);
        assertEquals("person", r3.get(persons.getField("name")));
        assertEquals(20, r3.get(persons.getField("age")));

        assertNotNull(r4);
        assertEquals("employee", r4.get(employee.getField("name")));
        assertEquals(21, r4.get(employee.getField("age")));
        assertEquals("Test", r4.get(employee.getField("jobTitle")));

        assertNotNull(r5);
        assertEquals("manager", r5.get(manager.getField("name")));
        assertEquals(25, r5.get(manager.getField("age")));
        assertEquals("Test", r5.get(manager.getField("jobTitle")));
        assertEquals("manager", r5.get(manager.getField("dept")));

    }

    public void testUserXmlDataWithInnerProperties() {
        DataRecordReader<String> xmlReader = new XmlStringDataRecordReader();
        DataRecord r1 = xmlReader.read("1", repository, b,
                "<ii><t>1365488764093</t><taskId>123456</taskId><p><B><id>1</id><textB>TextB</textB></B></p></ii>");
        DataRecord r2 = xmlReader
                .read("1", repository, d,
                        "<ii><t>1365488764093</t><taskId>123456</taskId><p><D><id>2</id><textB>TextBD</textB><textD>TextDD</textD></D></p></ii>");
        DataRecord r3 = xmlReader.read("1", repository, persons,
                "<ii><t>1365488764093</t><taskId>123456</taskId><p><Persons><name>person</name><age>20</age></Persons></p></ii>");
        DataRecord r4 = xmlReader
                .read("1",
                        repository,
                        employee,
                        "<ii><t>1365488764093</t><taskId>123456</taskId><p><Employee><name>employee</name><age>21</age><jobTitle>Test</jobTitle></Employee></p></ii>");
        DataRecord r5 = xmlReader
                .read("1",
                        repository,
                        manager,
                        "<ii><t>1365488764093</t><taskId>123456</taskId><p><Manager><name>manager</name><age>25</age><jobTitle>Test</jobTitle><dept>manager</dept></Manager></p></ii>");

        assertNotNull(r1);
        assertEquals("1", r1.get(b.getField("id")));
        assertEquals("TextB", r1.get(b.getField("textB")));
        DataRecordMetadata metadata = r2.getRecordMetadata();
        assertEquals(1365488764093L, metadata.getLastModificationTime());
        assertEquals("123456", metadata.getTaskId());

        assertNotNull(r2);
        assertEquals("2", r2.get(d.getField("id")));
        assertEquals("TextBD", r2.get(d.getField("textB")));
        assertEquals("TextDD", r2.get(d.getField("textD")));
        metadata = r2.getRecordMetadata();
        assertEquals(1365488764093L, metadata.getLastModificationTime());
        assertEquals("123456", metadata.getTaskId());

        assertNotNull(r3);
        assertEquals("person", r3.get(persons.getField("name")));
        assertEquals(20, r3.get(persons.getField("age")));
        metadata = r2.getRecordMetadata();
        assertEquals(1365488764093L, metadata.getLastModificationTime());
        assertEquals("123456", metadata.getTaskId());

        assertNotNull(r4);
        assertEquals("employee", r4.get(employee.getField("name")));
        assertEquals(21, r4.get(employee.getField("age")));
        assertEquals("Test", r4.get(employee.getField("jobTitle")));
        metadata = r2.getRecordMetadata();
        assertEquals(1365488764093L, metadata.getLastModificationTime());
        assertEquals("123456", metadata.getTaskId());

        assertNotNull(r5);
        assertEquals("manager", r5.get(manager.getField("name")));
        assertEquals(25, r5.get(manager.getField("age")));
        assertEquals("Test", r5.get(manager.getField("jobTitle")));
        assertEquals("manager", r5.get(manager.getField("dept")));
        metadata = r2.getRecordMetadata();
        assertEquals(1365488764093L, metadata.getLastModificationTime());
        assertEquals("123456", metadata.getTaskId());

    }
}
