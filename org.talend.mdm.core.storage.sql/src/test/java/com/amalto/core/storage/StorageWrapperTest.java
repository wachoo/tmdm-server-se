// ============================================================================
//
// Copyright (C) 2006-2014 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package com.amalto.core.storage;

import static com.amalto.core.query.user.UserQueryBuilder.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import junit.framework.TestCase;

import org.talend.mdm.commmon.metadata.ComplexTypeMetadata;
import org.talend.mdm.commmon.metadata.MetadataRepository;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.amalto.core.load.io.ResettableStringWriter;
import com.amalto.core.query.StorageTestCase;
import com.amalto.core.query.user.UserQueryBuilder;
import com.amalto.core.server.MockServerLifecycle;
import com.amalto.core.server.ServerContext;
import com.amalto.core.storage.hibernate.HibernateStorage;
import com.amalto.core.storage.record.DataRecord;
import com.amalto.core.storage.record.DataRecordReader;
import com.amalto.core.storage.record.DataRecordWriter;
import com.amalto.core.storage.record.DataRecordXmlWriter;
import com.amalto.core.storage.record.ViewSearchResultsWriter;
import com.amalto.core.storage.record.XmlStringDataRecordReader;
import com.amalto.xmlserver.interfaces.XmlServerException;

public class StorageWrapperTest extends TestCase {

    public StorageWrapperTest() {
        ServerContext.INSTANCE.get(new MockServerLifecycle());
    }

    public void testGetDocumentAsString() throws Exception {
        final MetadataRepository repository = prepareMetadata("Product.xsd"); //$NON-NLS-1$
        final Storage storage = prepareStorage(repository);

        StorageWrapper wrapper = new StorageWrapper() {

            @Override
            protected Storage getStorage(String dataClusterName) {
                return storage;
            }

            @Override
            protected Storage getStorage(String dataClusterName, String revisionId) {
                return storage;
            }
        };
        String xml = "<ii><c>Product</c><n>Product</n><dmn>Product</dmn><i>333</i><t>1372654669313</t><taskId></taskId><p> <Product><Id>333</Id><Name>333</Name><Description>333</Description><Price>333</Price></Product></p></ii>"; //$NON-NLS-1$
        wrapper.start("Product"); //$NON-NLS-1$
        {
            wrapper.putDocumentFromString(xml, "Product.Product.333", "Product", null); //$NON-NLS-1$ //$NON-NLS-2$
        }
        wrapper.commit("Product"); //$NON-NLS-1$
        String item = wrapper.getDocumentAsString(null, "Product", "Product.Product.333"); //$NON-NLS-1$ //$NON-NLS-2$
        assertNotNull(item);
        assertTrue(item.contains("<i>333</i>")); //$NON-NLS-1$

        xml = "<ii><c>Product</c><n>Product</n><dmn>Product</dmn><i>33&amp;44</i><t>1372654669313</t><taskId></taskId><p> <Product><Id>33&amp;44</Id><Name>333</Name><Description>333</Description><Price>333</Price></Product></p></ii>"; //$NON-NLS-1$
        wrapper.start("Product"); //$NON-NLS-1$
        {
            wrapper.putDocumentFromString(xml, "Product.Product.33&44", "Product", null); //$NON-NLS-1$ //$NON-NLS-2$
        }
        wrapper.commit("Product"); //$NON-NLS-1$
        item = wrapper.getDocumentAsString(null, "Product", "Product.Product.33&44"); //$NON-NLS-1$ //$NON-NLS-2$
        assertNotNull(item);
        assertTrue(item.contains("<i>33&amp;44</i>")); //$NON-NLS-1$

        xml = "<ii><c>Product</c><n>Product</n><dmn>Product</dmn><i>&quot;555&lt;666&gt;444&quot;</i><t>1372654669313</t><taskId></taskId><p> <Product><Id>&quot;555&lt;666&gt;444&quot;</Id><Name>333</Name><Description>333</Description><Price>333</Price></Product></p></ii>"; //$NON-NLS-1$
        wrapper.start("Product"); //$NON-NLS-1$
        {
            wrapper.putDocumentFromString(xml, "Product.Product.\"555<666>444\"", "Product", null); //$NON-NLS-1$ //$NON-NLS-2$
        }
        wrapper.commit("Product"); //$NON-NLS-1$
        item = wrapper.getDocumentAsString(null, "Product", "Product.Product.\"555<666>444\""); //$NON-NLS-1$ //$NON-NLS-2$
        assertNotNull(item);
        assertTrue(item.contains("<i>&quot;555&lt;666&gt;444&quot;</i>")); //$NON-NLS-1$
    }

    public void testMultipleOccurrenceComplex() throws IOException {
        final MetadataRepository repository = prepareMetadata("MultipleOccurrenceComplex.xsd"); //$NON-NLS-1$
        final Storage storage = prepareStorage(repository);
        DataRecordReader<String> factory = new XmlStringDataRecordReader();
        String recordXml = "<C><subelement>1</subelement><A xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:type=\"B\"><a>7</a><B>6</B></A><A xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:type=\"A\"><a>5</a></A><A xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:type=\"B\"><a>4</a><B>3</B></A><Aa xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:type=\"B\"><a>2</a><B>1</B></Aa></C>"; //$NON-NLS-1$
        List<DataRecord> records = new LinkedList<DataRecord>();
        records.add(factory.read("1", repository, repository.getComplexType("C"), recordXml)); //$NON-NLS-1$ //$NON-NLS-2$
        storage.begin();
        storage.update(records);
        storage.commit();

        storage.begin();
        ComplexTypeMetadata c = repository.getComplexType("C"); //$NON-NLS-1$
        UserQueryBuilder qb = from(c);
        qb.start(0);
        qb.limit(1);
        StorageResults results = storage.fetch(qb.getSelect());
        DataRecord result = results.iterator().next();
        DataRecordXmlWriter writer = new DataRecordXmlWriter(c);
        ResettableStringWriter stringWriter = new ResettableStringWriter();
        writer.write(result, stringWriter);
        assertEquals(recordXml, stringWriter.toString());
    }

    public void testDateTimeAsBaseType() throws XmlServerException, ParserConfigurationException, SAXException, IOException,
            XPathExpressionException {
        final MetadataRepository repository = prepareMetadata("CustomDateTime.xsd"); //$NON-NLS-1$
        final Storage storage = prepareStorage(repository);
        DataRecordReader<String> factory = new XmlStringDataRecordReader();

        List<DataRecord> records = new LinkedList<DataRecord>();
        records.add(factory.read("1", repository, repository.getComplexType("Employee"), //$NON-NLS-1$ //$NON-NLS-2$
                "<Employee><Id>22</Id><Holiday>2014-04-17T12:00:00</Holiday><birthday>2014-04-16T12:00:00</birthday></Employee>")); //$NON-NLS-1$
        storage.begin();
        storage.update(records);
        storage.commit();

        // Query saved data
        storage.begin();
        ComplexTypeMetadata employee = repository.getComplexType("Employee"); //$NON-NLS-1$
        UserQueryBuilder qb = from(employee);
        qb.start(0);
        qb.limit(1);
        StorageResults results = storage.fetch(qb.getSelect());
        assertEquals(1, results.getCount());
        DataRecord result = results.iterator().next();

        DataRecordXmlWriter writer = new DataRecordXmlWriter(employee);
        ResettableStringWriter stringWriter = new ResettableStringWriter();
        try {
            writer.write(result, stringWriter);
        } catch (IOException e) {
            throw new XmlServerException(e);
        }
        String recordStringValue = stringWriter.toString();
        XPath xpath = XPathFactory.newInstance().newXPath();
        DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Element r = documentBuilder.parse(new InputSource(new StringReader(recordStringValue))).getDocumentElement();
        NodeList holiday = (NodeList) xpath.evaluate("./Holiday", r, XPathConstants.NODESET); //$NON-NLS-1$
        List<String> datetimeStrValues = new ArrayList<String>();
        for (int j = 0; j < holiday.getLength(); j++) {
            datetimeStrValues.add(holiday.item(j).getFirstChild() == null ? "" : holiday.item(j).getFirstChild().getNodeValue()); //$NON-NLS-1$
        }
        assertTrue(datetimeStrValues.contains("2014-04-17T12:00:00")); //$NON-NLS-1$
        stringWriter.reset();

        DataRecordWriter viewWriter = new ViewSearchResultsWriter();
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try {
            viewWriter.write(result, output);
        } catch (IOException e) {
            throw new XmlServerException(e);
        }
        String resultsAsString = new String(output.toByteArray(), Charset.forName("UTF-8")); //$NON-NLS-1$
        output.reset();

        xpath = XPathFactory.newInstance().newXPath();
        r = documentBuilder.parse(new InputSource(new StringReader(resultsAsString))).getDocumentElement();
        NodeList birthday = (NodeList) xpath.evaluate("./birthday", r, XPathConstants.NODESET); //$NON-NLS-1$
        datetimeStrValues = new ArrayList<String>();
        for (int j = 0; j < birthday.getLength(); j++) {
            datetimeStrValues
                    .add(birthday.item(j).getFirstChild() == null ? "" : birthday.item(j).getFirstChild().getNodeValue()); //$NON-NLS-1$
        }
        assertTrue(datetimeStrValues.contains("2014-04-16T12:00:00")); //$NON-NLS-1$

        storage.commit();

        final MetadataRepository repository2 = prepareMetadata("CustomDate.xsd"); //$NON-NLS-1$
        final Storage storage2 = prepareStorage(repository2);

        factory = new XmlStringDataRecordReader();

        records.clear();
        records.add(factory.read("1", repository2, repository2.getComplexType("CustomDate"), //$NON-NLS-1$ //$NON-NLS-2$
                "<CustomDate><Id>22</Id><MyDate>2014-04-17</MyDate></CustomDate>")); //$NON-NLS-1$
        storage2.begin();
        storage2.update(records);
        storage2.commit();

        // Query saved data
        storage2.begin();
        ComplexTypeMetadata mydate = repository2.getComplexType("CustomDate"); //$NON-NLS-1$
        qb = from(mydate);
        qb.start(0);
        qb.limit(1);
        results = storage2.fetch(qb.getSelect());
        assertEquals(1, results.getCount());
        result = results.iterator().next();
        writer = new DataRecordXmlWriter(mydate);
        try {
            writer.write(result, stringWriter);
        } catch (IOException e) {
            throw new XmlServerException(e);
        }
        recordStringValue = stringWriter.toString();
        xpath = XPathFactory.newInstance().newXPath();

        r = documentBuilder.parse(new InputSource(new StringReader(recordStringValue))).getDocumentElement();
        holiday = (NodeList) xpath.evaluate("./MyDate", r, XPathConstants.NODESET); //$NON-NLS-1$
        List<String> dateStrValues = new ArrayList<String>();
        for (int j = 0; j < holiday.getLength(); j++) {
            dateStrValues.add(holiday.item(j).getFirstChild() == null ? "" : holiday.item(j).getFirstChild().getNodeValue()); //$NON-NLS-1$
        }
        assertTrue(dateStrValues.contains("2014-04-17")); //$NON-NLS-1$
        stringWriter.reset();

        storage2.commit();

    }

    private MetadataRepository prepareMetadata(String dataModelFile) {
        MetadataRepository repository = new MetadataRepository();
        repository.load(StorageWrapperTest.class.getResourceAsStream(dataModelFile));
        return repository;
    }

    private Storage prepareStorage(MetadataRepository repository) {
        Storage storage = new HibernateStorage("Product"); //$NON-NLS-1$
        storage.init(ServerContext.INSTANCE.get().getDefinition(StorageTestCase.DATABASE + "-Default", "MDM")); //$NON-NLS-1$//$NON-NLS-2$
        storage.prepare(repository, true);
        return storage;
    }
}
