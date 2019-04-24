/*
 * Copyright (C) 2006-2018 Talend Inc. - www.talend.com
 * 
 * This source code is available under agreement available at
 * %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
 * 
 * You should have received a copy of the agreement along with this program; if not, write to Talend SA 9 rue Pages
 * 92150 Suresnes, France
 */

package com.amalto.core.query;

import static com.amalto.core.query.user.UserQueryBuilder.alias;
import static com.amalto.core.query.user.UserQueryBuilder.and;
import static com.amalto.core.query.user.UserQueryBuilder.contains;
import static com.amalto.core.query.user.UserQueryBuilder.eq;
import static com.amalto.core.query.user.UserQueryBuilder.from;
import static com.amalto.core.query.user.UserQueryBuilder.fullText;
import static com.amalto.core.query.user.UserQueryBuilder.gt;
import static com.amalto.core.query.user.UserQueryBuilder.gte;
import static com.amalto.core.query.user.UserQueryBuilder.isEmpty;
import static com.amalto.core.query.user.UserQueryBuilder.isNull;
import static com.amalto.core.query.user.UserQueryBuilder.lt;
import static com.amalto.core.query.user.UserQueryBuilder.lte;
import static com.amalto.core.query.user.UserQueryBuilder.not;
import static com.amalto.core.query.user.UserQueryBuilder.or;
import static com.amalto.core.query.user.UserQueryBuilder.taskId;
import static com.amalto.core.query.user.UserQueryBuilder.timestamp;
import static com.amalto.core.query.user.UserStagingQueryBuilder.error;

import java.io.ByteArrayOutputStream;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.talend.mdm.commmon.metadata.ComplexTypeMetadata;
import org.talend.mdm.commmon.metadata.FieldMetadata;
import org.talend.mdm.commmon.util.core.MDMConfiguration;
import org.talend.mdm.query.QueryParser;

import com.amalto.core.query.optimization.ConfigurableContainsOptimizer;
import com.amalto.core.query.user.Alias;
import com.amalto.core.query.user.BinaryLogicOperator;
import com.amalto.core.query.user.Compare;
import com.amalto.core.query.user.Condition;
import com.amalto.core.query.user.Count;
import com.amalto.core.query.user.Distinct;
import com.amalto.core.query.user.Expression;
import com.amalto.core.query.user.FieldFullText;
import com.amalto.core.query.user.OrderBy;
import com.amalto.core.query.user.Predicate;
import com.amalto.core.query.user.Select;
import com.amalto.core.query.user.Split;
import com.amalto.core.query.user.StringConstant;
import com.amalto.core.query.user.TypedExpression;
import com.amalto.core.query.user.UserQueryBuilder;
import com.amalto.core.query.user.UserQueryHelper;
import com.amalto.core.storage.FullTextResultsWriter;
import com.amalto.core.storage.Storage;
import com.amalto.core.storage.StorageResults;
import com.amalto.core.storage.datasource.DataSource;
import com.amalto.core.storage.datasource.DataSourceDefinition;
import com.amalto.core.storage.datasource.RDBMSDataSource;
import com.amalto.core.storage.exception.FullTextQueryCompositeKeyException;
import com.amalto.core.storage.hibernate.HibernateStorage;
import com.amalto.core.storage.record.DataRecord;
import com.amalto.core.storage.record.DataRecordReader;
import com.amalto.core.storage.record.DataRecordWriter;
import com.amalto.core.storage.record.ViewSearchResultsWriter;
import com.amalto.core.storage.record.XmlStringDataRecordReader;
import com.amalto.xmlserver.interfaces.IWhereItem;
import com.amalto.xmlserver.interfaces.WhereAnd;
import com.amalto.xmlserver.interfaces.WhereCondition;

@SuppressWarnings("nls")
public class StorageFullTextTest extends StorageTestCase {

    private static Logger LOG = Logger.getLogger(StorageFullTextTest.class);
    private static ComplexTypeMetadata a1 = repository.getComplexType("a1");
    private static ComplexTypeMetadata a2 = repository.getComplexType("a2");
    private static ComplexTypeMetadata a3 = repository.getComplexType("a3");

    static {
        initStorage(DATASOURCE_FULLTEXT);
        try {
            populateData();
        } catch (Exception e) {
            LOG.error("Populate Date failed");
        }
    }

    private static void populateData() throws Exception {
        try {
            clean();
        } catch (Exception e) {
            // ignore
        }
        DataRecordReader<String> factory = new XmlStringDataRecordReader();
        List<DataRecord> allRecords = new LinkedList<DataRecord>();
        allRecords.add(factory.read(repository, productFamily, "<ProductFamily><Id>1</Id><Name>ProductFamily1</Name></ProductFamily>"));
        allRecords.add(factory.read(repository, productFamily, "<ProductFamily><Id>2</Id><Name>ProductFamily2</Name></ProductFamily>"));
        allRecords.add(factory.read(repository, productFamily, "<ProductFamily><Id>3</Id><Name>ProductFamily3</Name></ProductFamily>"));
        allRecords.add(factory.read(repository, productFamily, "<ProductFamily><Id>4</Id><Name>test_name4</Name></ProductFamily>"));
        allRecords.add(factory.read(repository, productFamily, "<ProductFamily><Id>721 345 123</Id><Name>test_name5</Name></ProductFamily>"));
        allRecords.add(factory.read(repository, productFamily, "<ProductFamily><Id>123 345</Id><Name>test_name5</Name></ProductFamily>"));
        allRecords.add(factory.read(repository, product, "<Product><Id>1</Id><Name>talend</Name><ShortDescription>Short description word</ShortDescription><LongDescription>Long description</LongDescription><Price>10</Price><Features><Sizes><Size>Small</Size><Size>Medium</Size><Size>Large</Size></Sizes><Colors><Color>Blue</Color><Color>Red</Color></Colors></Features><Status>Pending</Status><Supplier>[1]</Supplier></Product>"));
        allRecords.add(factory.read(repository, product, "<Product><Id>2</Id><Name>Renault car</Name><ShortDescription>A car</ShortDescription><LongDescription>Long description 2</LongDescription><Price>10</Price><Features><Sizes><Size>Large</Size><Size>Large</Size></Sizes><Colors><Color>Blue 2</Color><Color>Blue 1</Color><Color>Klein blue2</Color></Colors></Features><Family>[1]</Family><Status>Pending</Status><Supplier>[2]</Supplier><Supplier>[1]</Supplier></Product>"));
        allRecords.add(factory.read(repository, product, "<Product><Id>3</Id><Name>kevin cui</Name><ShortDescription>A person</ShortDescription><LongDescription>Long description 3</LongDescription><Price>100</Price><Features><Sizes><Size>Large</Size><Size>Large</Size></Sizes><Colors><Color>Blue 3</Color><Color>Blue 4</Color><Color>Kevin blue3</Color></Colors></Features><Family></Family><Status>Pending</Status></Product>"));
        allRecords.add(factory.read(repository, product, "<Product><Id>4</Id><Name>Evan Lin</Name><ShortDescription>Leader staff</ShortDescription><LongDescription>descn1</LongDescription><Price>101</Price><Features><Sizes></Sizes><Colors></Colors></Features><Family>[721 345 123]</Family><Status>Pending</Status></Product>"));
        allRecords.add(factory.read(repository, product, "<Product><Id>5</Id><Name>Evan Lin</Name><ShortDescription>Sample developer</ShortDescription><LongDescription>descn2</LongDescription><Price>101</Price><Features><Sizes></Sizes><Colors></Colors></Features><Family>[123 345]</Family><Status>Pending</Status></Product>"));
        allRecords.add(factory.read(repository, supplier, "<Supplier><Id>1</Id><SupplierName>Renault</SupplierName><Contact><Name>Jean Voiture</Name><Phone>33123456789</Phone><Email>test@test.org</Email></Contact></Supplier>"));
        allRecords.add(factory.read(repository, supplier, "<Supplier><Id>2</Id><SupplierName>Starbucks Talend</SupplierName><Contact><Name>Jean Cafe</Name><Phone>33234567890</Phone><Email>test@testfactory.org</Email></Contact></Supplier>"));
        allRecords.add(factory.read(repository, supplier, "<Supplier><Id>3</Id><SupplierName>Talend</SupplierName><Contact><Name>Jean Paul</Name><Phone>33234567890</Phone><Email>test@talend.com</Email></Contact></Supplier>"));
        allRecords.add(factory.read(repository, supplier, "<Supplier><Id>4</Id><SupplierName>IdSoftware</SupplierName><Contact><Name>John Carmack</Name><Phone>123456789</Phone><Email></Email></Contact></Supplier>"));

        allRecords
                .add(factory
                        .read(repository,
                                country,
                                "<Country><id>1</id><creationDate>2010-10-10</creationDate><creationTime>2010-10-10T00:00:01</creationTime><name>France</name></Country>"));
        allRecords
                .add(factory
                        .read(repository,
                                country,
                                "<Country><id>2</id><creationDate>2010-10-10</creationDate><creationTime>2010-10-10T00:00:01</creationTime><name>France1</name><notes><note>Country note</note><comment>repeatable comment 1</comment><comment>Repeatable comment 2</comment></notes></Country>"));

        allRecords
                .add(factory
                        .read(repository,
                                country,
                                "<Country><id>3</id><creationDate>2010-10-10</creationDate><creationTime>2010-10-10T00:00:01</creationTime><name>France2</name></Country>"));

        allRecords
                .add(factory
                        .read(repository,
                                country,
                                "<Country><id>4</id><creationDate>2010-10-10</creationDate><creationTime>2010-10-10T00:00:01</creationTime><name>France3</name></Country>"));

        allRecords
                .add(factory
                        .read(repository,
                                country,
                                "<Country><id>5</id><creationDate>2010-10-10</creationDate><creationTime>2010-10-10T00:00:01</creationTime><name>France4</name></Country>"));

        allRecords
                .add(factory
                        .read(repository,
                                country,
                                "<Country><id>6</id><creationDate>2010-10-10</creationDate><creationTime>2010-10-10T00:00:01</creationTime><name>France5</name></Country>"));

        allRecords
                .add(factory
                        .read(repository,
                                country,
                                "<Country><id>7</id><creationDate>2010-10-10</creationDate><creationTime>2010-10-10T00:00:01</creationTime><name>France6</name></Country>"));

        allRecords
                .add(factory
                        .read(repository,
                                country,
                                "<Country><id>8</id><creationDate>2010-10-10</creationDate><creationTime>2010-10-10T00:00:01</creationTime><name>France7</name></Country>"));

        allRecords
                .add(factory
                        .read(repository,
                                country,
                                "<Country><id>9</id><creationDate>2010-10-10</creationDate><creationTime>2010-10-10T00:00:01</creationTime><name>France8</name></Country>"));

        allRecords
                .add(factory
                        .read(repository,
                                country,
                                "<Country><id>10</id><creationDate>2010-10-10</creationDate><creationTime>2010-10-10T00:00:01</creationTime><name>France9</name></Country>"));

        allRecords
                .add(factory
                        .read(repository,
                                country,
                                "<Country><id>11</id><creationDate>2010-10-10</creationDate><creationTime>2010-10-10T00:00:01</creationTime><name>France10</name></Country>"));

        allRecords.add(factory.read(repository, country,
                "<Country><id>12</id><creationDate>2010-10-10</creationDate><creationTime>2010-10-10T00:00:01</creationTime><name>Foances</name></Country>"));

        allRecords
                .add(factory
                        .read(repository,
                                countryLong,
                                "<CountryLong><id>1</id><creationDate>2010-10-10</creationDate><creationTime>2010-10-10T00:00:01</creationTime><name>France1</name></CountryLong>"));

        allRecords
                .add(factory
                        .read(repository,
                                countryLong,
                                "<CountryLong><id>2</id><creationDate>2010-10-10</creationDate><creationTime>2010-10-10T00:00:01</creationTime><name>France2</name></CountryLong>"));

        allRecords
                .add(factory
                        .read(repository,
                                countryLong,
                                "<CountryLong><id>3</id><creationDate>2010-10-10</creationDate><creationTime>2010-10-10T00:00:01</creationTime><name>France3</name></CountryLong>"));

        allRecords
                .add(factory
                        .read(repository,
                                countryShort,
                                "<CountryShort><id>1</id><creationDate>2010-10-10</creationDate><creationTime>2010-10-10T00:00:01</creationTime><name>France1</name></CountryShort>"));

        allRecords
                .add(factory
                        .read(repository,
                                countryShort,
                                "<CountryShort><id>2</id><creationDate>2010-10-10</creationDate><creationTime>2010-10-10T00:00:01</creationTime><name>France2</name></CountryShort>"));

        allRecords
                .add(factory
                        .read(repository,
                                countryShort,
                                "<CountryShort><id>3</id><creationDate>2010-10-10</creationDate><creationTime>2010-10-10T00:00:01</creationTime><name>France3</name></CountryShort>"));
        allRecords
                .add(factory
                        .read(repository,
                                countryShort,
                                "<CountryShort><id>4</id><creationDate>2010-10-10</creationDate><creationTime>2010-10-10T00:00:01</creationTime><name>France3</name></CountryShort>"));

        allRecords
                .add(factory
                        .read(repository,
                                person,
                                "<Person><id>1</id><score>130000.00</score><lastname>Dupond</lastname><resume>[EN:my splendid resume, splendid isn't it][FR:mon magnifique resume, n'est ce pas ?]</resume><middlename>John</middlename><firstname>Julien</firstname><addresses></addresses><age>10</age><Status>Employee</Status><Available>true</Available></Person>"));
        allRecords
                .add(factory
                        .read(repository,
                                person,
                                "<Person><id>2</id><score>170000.00</score><lastname>Dupont</lastname><middlename>John</middlename><firstname>Robert-Julien</firstname><addresses></addresses><age>20</age><Status>Customer</Status><Available>false</Available></Person>"));
        allRecords
                .add(factory
                        .read(repository,
                                person,
                                "<Person><id>3</id><score>200000.00</score><lastname>Leblanc</lastname><middlename>John</middlename><firstname>Juste</firstname><addresses></addresses><age>30</age><Status>Friend</Status></Person>"));
        allRecords
                .add(factory
                        .read(repository,
                                person,
                                "<Person><id>4</id><score>200000.00</score><lastname>Leblanc</lastname><middlename>John</middlename><firstname>Julien</firstname><age>30</age><Status>Friend</Status></Person>"));
        allRecords
                .add(factory
                        .read(repository,
                                person,
                                "<Person><id>1234</id><firstname>quan</firstname><middlename>kevin</middlename><lastname>cui</lastname><resume>[EN:Hello [World:]][FR:bonjour :le][ZH:ni Hao]</resume><age>22</age><score>100</score><Available></Available><Status>Customer</Status></Person>"));

        allRecords
                .add(factory
                        .read(repository,
                                fullTextSearchEntityA,
                                "<FullTextSearchEntityA><Id>id1</Id><Name>name1</Name><Address><AddressName>address1</AddressName><City><CityName>city1</CityName></City></Address></FullTextSearchEntityA>"));
        allRecords.add(factory.read(repository, a2,
                "<a2><subelement>1</subelement><subelement1>10</subelement1><b3>String b3</b3><b4>String b4</b4></a2>"));
        allRecords.add(factory.read(repository, a1,
                "<a1><subelement>1</subelement><subelement1>11</subelement1><b1>String b1</b1><b2>[1][10]</b2></a1>"));
        allRecords.add(factory.read(repository, a2,
                "<a2><subelement>1</subelement><subelement1>2</subelement1><b3>String b3</b3><b4>String b4</b4></a2>"));
        allRecords.add(factory.read(repository, a3, "<a3><id>3</id><name>hamdi</name><a2>[1][2]</a2></a3>"));
        allRecords.add(factory.read(repository, store, "<Store><Id>Upper Case Id</Id><Name>name1</Name></Store>"));
        allRecords.add(factory.read(repository, store, "<Store><Id>lower case id</Id><Name>name2</Name></Store>"));
        allRecords.add(factory.read(repository, store, "<Store><Id>ab</Id><Name>name3</Name></Store>"));
        allRecords.add(factory.read(repository, store, "<Store><Id>ab&amp;cd</Id><Name>name4</Name></Store>"));
        allRecords.add(factory.read(repository, store, "<Store><Id>AB&amp;CD</Id><Name>name5</Name></Store>"));
        allRecords.add(factory.read(repository, store, "<Store><Id>One@#$Two%&amp;=Three;,.Four</Id><Name>name6</Name></Store>"));

        allRecords.add(factory.read(repository, employee, "<Employee><name>employee 1</name><age>11</age><jobTitle>jobTitle 11</jobTitle></Employee>"));
        allRecords.add(factory.read(repository, employee, "<Employee><name>employee 2</name><age>22</age><jobTitle>jobTitle 22</jobTitle></Employee>"));
        allRecords.add(factory.read(repository, employee, "<Employee><name>employee 3</name><age>33</age><jobTitle>jobTitle 33</jobTitle></Employee>"));
        allRecords.add(factory.read(repository, persons, "<Persons><name>person 1</name><age>11</age></Persons>"));
        allRecords.add(factory.read(repository, persons, "<Persons><name>person 2</name><age>22</age></Persons>"));
        allRecords.add(factory.read(repository, persons, "<Persons><name>person 3</name><age>33</age></Persons>"));
        allRecords.add(factory.read(repository, manager, "<Manager><name>manager 1</name><age>11</age><jobTitle>jobTitle 11</jobTitle><dept>dept 1</dept></Manager>"));
        allRecords.add(factory.read(repository, manager, "<Manager><name>manager 2</name><age>22</age><jobTitle>jobTitle 22</jobTitle><dept>dept 2</dept></Manager>"));
        allRecords.add(factory.read(repository, manager, "<Manager><name>manager 3</name><age>33</age><jobTitle>jobTitle 33</jobTitle><dept>dept 3</dept></Manager>"));
        allRecords.add(factory.read(repository, nn, "<NN><Id>pp</Id><name>tyu</name><sub><name>yu67</name><title>67</title></sub></NN>"));
        allRecords.add(factory.read(repository, contract, "<Contract><id>1</id><comment>1</comment><detail xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:type=\"ContractDetailType\"></detail><detail xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:type=\"ContractDetailType\"><code>1</code></detail><detail xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:type=\"ContractDetailType\"><code>1</code></detail><detail xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:type=\"ContractDetailSubType\"><code>1</code><features><actor>1</actor><vendor>1</vendor></features></detail><enumEle>pending</enumEle></Contract>"));
        try {
            storage.begin();
            storage.update(allRecords);
            storage.commit();
        } catch (Exception e) {
            storage.rollback();
            throw new RuntimeException(e);
        } finally {
            storage.end();
        }
    }

    public void tearDown() throws Exception {
        super.tearDown();
    }

    public static void clean() throws Exception {
        storage.begin();
        {
            UserQueryBuilder qb = from(productFamily);
            storage.delete(qb.getSelect());

            qb = from(supplier);
            storage.delete(qb.getSelect());

            qb = from(product);
            storage.delete(qb.getSelect());

            qb = from(country);
            storage.delete(qb.getSelect());

            qb = from(countryLong);
            storage.delete(qb.getSelect());

            qb = from(countryShort);
            storage.delete(qb.getSelect());

            qb = from(person);
            storage.delete(qb.getSelect());

            qb = from(fullTextSearchEntityA);
            storage.delete(qb.getSelect());

            qb = from(a1);
            storage.delete(qb.getSelect());

            qb = from(a2);
            storage.delete(qb.getSelect());

            qb = from(a3);
            storage.delete(qb.getSelect());

            qb = from(store);
            storage.delete(qb.getSelect());

            qb = from(address);
            storage.delete(qb.getSelect());

            qb = from(nn);
            storage.delete(qb.getSelect());

            qb = from(contract);
            storage.delete(qb.getSelect());
        }
        storage.commit();
        storage.end();
    }

    public void setUp() throws Exception {
        super.setUp();
    }

    public void testNumericQueryMix() throws Exception {
        UserQueryBuilder qb = from(product).select(prepareSelectProductFields(product)).where(fullText("2"));
        StorageResults results = storage.fetch(qb.getSelect());
        try {
            for (DataRecord result : results) {
                LOG.info("result = " + result);
            }
            assertEquals(1, results.getCount());
        } finally {
            results.close();
        }
    }

    public void testMatchesInSameInstance() throws Exception {
        UserQueryBuilder qb = from(country).select(prepareSelectCountryFields(country)).where(fullText("2010"));
        StorageResults results = storage.fetch(qb.getSelect());
        try {
            for (DataRecord result : results) {
                LOG.info("result = " + result);
            }
            assertEquals(12, results.getCount());
        } finally {
            results.close();
        }

        qb = from(product).select(prepareSelectProductFields(product)).where(fullText("Large"));
        results = storage.fetch(qb.getSelect());
        try {
            for (DataRecord result : results) {
                LOG.info("result = " + result);
            }
            assertEquals(3, results.getCount());
        } finally {
            results.close();
        }
    }

    public void testSimpleSearch() throws Exception {
        UserQueryBuilder qb = from(supplier).select(prepareSelectSupplierFields(supplier)).where(fullText("Renault"));

        StorageResults results = storage.fetch(qb.getSelect());
        try {
            for (DataRecord result : results) {
                LOG.info("result = " + result);
            }
            assertEquals(1, results.getCount());
        } finally {
            results.close();
        }
    }

    public void testCountSearch() throws Exception {
        long count = 0;
        String query = "{'select': {'from': ['Product'],'fields': [{'count': {}}], 'where': {'gt': [{'field': 'Product/Price'},{'value': '1'}]}}}";
        QueryParser parser = QueryParser.newParser(storage.getMetadataRepository());
        Expression expression = parser.parse(query);
        StorageResults results = storage.fetch(expression);
        try {
            for (DataRecord result : results) {
                LOG.info("result = " + result);
                count = (Long) result.get("count");
            }
            assertEquals(5, count);
        } finally {
            results.close();
        }
    }

    public void testDistinctSearch() throws Exception {
        // there are 5 records,name are 'talend','Renault car','kevin cu'i,'Evan Lin','Evan Lin'.So the count of
        // distinct name should be 4.
        int nameCount = 0;
        String query = "{'select': {'from': ['Product'],'fields': [{'distinct': {'field': 'Product/Name'}}], 'where': {'gt': [{'field': 'Product/Price'},{'value': '1'}]}}}";
        QueryParser parser = QueryParser.newParser(storage.getMetadataRepository());
        Expression expression = parser.parse(query);
        StorageResults results = storage.fetch(expression);
        try {
            for (DataRecord result : results) {
                LOG.info("result = " + result);
                nameCount++;
            }
            assertEquals(4, nameCount);
        } finally {
            results.close();
        }
    }

    public void testDistinctCountSearch() throws Exception {
        // there are 5 records,name are 'talend','Renault car','kevin cu'i,'Evan Lin','Evan Lin' Lin.So the count of
        // distinct name should be 4.
        long count = 0;
        String query = "{'select': {'from': ['Product'],'fields': [{'count': {}},{'distinct': {'field': 'Product/Name'}}], 'where': {'gt': [{'field': 'Product/Price'},{'value': '1'}]}}}";
        QueryParser parser = QueryParser.newParser(storage.getMetadataRepository());
        Expression expression = parser.parse(query);
        StorageResults results = storage.fetch(expression);
        try {
            for (DataRecord result : results) {
                LOG.info("result = " + result);
                count = (Long) result.get("count");
            }
            assertEquals(4, count);
        } finally {
            results.close();
        }

        query = "{'select': {'from': ['Product'],'fields': [{'distinct': {'field': 'Product/Name'}},{'count': {}}], 'where': {'gt': [{'field': 'Product/Price'},{'value': '1'}]}}}";
        parser = QueryParser.newParser(storage.getMetadataRepository());
        expression = parser.parse(query);
        results = storage.fetch(expression);
        try {
            for (DataRecord result : results) {
                LOG.info("result = " + result);
                count = (Long) result.get("count");
            }
            assertEquals(4, count);
        } finally {
            results.close();
        }
    }

    public void testSimpleSearchOrderBy() throws Exception {
        UserQueryBuilder qb = from(supplier).select(supplier.getField("Id")).select(prepareSelectSupplierFields(supplier)).where(fullText("Talend"))
                .orderBy(supplier.getField("Id"), OrderBy.Direction.ASC);

        StorageResults records = storage.fetch(qb.getSelect());
        try {
            assertEquals(2, records.getCount());
            int currentId = -1;
            for (DataRecord record : records) {
                Integer id = Integer.parseInt((String) record.get("Id"));
                assertTrue(id > currentId);
                currentId = id;
            }
        } finally {
            records.close();
        }

        qb = from(supplier).select(supplier.getField("Id")).select(prepareSelectSupplierFields(supplier)).where(fullText("Talend"))
                .orderBy(supplier.getField("Id"), OrderBy.Direction.DESC);

        records = storage.fetch(qb.getSelect());
        try {
            assertEquals(2, records.getCount());
            int currentId = Integer.MAX_VALUE;
            for (DataRecord record : records) {
                Integer id = Integer.parseInt((String) record.get("Id"));
                assertTrue(id < currentId);
                currentId = id;
            }
        } finally {
            records.close();
        }
    }

    public void testSimpleSearchOrderByWithContainsCondition() throws Exception {
        // Order by "Id" field
        UserQueryBuilder qb = from(productFamily).select(prepareSelectProductFamilyFields(productFamily))
                .where(contains(productFamily.getField("Name"), "Product")).orderBy(
                productFamily.getField("Id"), OrderBy.Direction.DESC);

        StorageResults records = storage.fetch(qb.getSelect());
        try {
            assertEquals(3, records.getCount());
            int currentId = Integer.MAX_VALUE;
            for (DataRecord record : records) {
                Integer id = Integer.parseInt((String) record.get("Id"));
                assertTrue(id < currentId);
                currentId = id;
            }
        } finally {
            records.close();
        }
        // Order by "Name" field
        qb = from(productFamily).select(prepareSelectProductFamilyFields(productFamily))
                .where(contains(productFamily.getField("Name"), "Product")).orderBy(
                productFamily.getField("Name"), OrderBy.Direction.DESC);

        records = storage.fetch(qb.getSelect());
        try {
            assertEquals(3, records.getCount());
            int currentId = Integer.MAX_VALUE;
            for (DataRecord record : records) {
                Integer id = Integer.parseInt((String) record.get("Id"));
                assertTrue(id < currentId);
                currentId = id;
            }
        } finally {
            records.close();
        }
    }

    public void testMultipleTypesSearch() throws Exception {
        UserQueryBuilder qb = from(supplier).and(product)
               .select(supplier.getField("SupplierName"))
                .select(prepareSelectProductFields(product))
                .where(fullText("Renault"));

        StorageResults results = storage.fetch(qb.getSelect());
        try {
            assertEquals(2, results.getCount());
        } finally {
            results.close();
        }
    }

    public void testDateSearch() throws Exception {
        UserQueryBuilder qb = from(country).select(prepareSelectCountryFields(country)).where(fullText("2010")); // Default StandardAnalyzer will split text
                                                                     // "2010-10-12" into "2010", "10", "12"

        StorageResults results = storage.fetch(qb.getSelect());
        try {
            assertEquals(12, results.getCount());
        } finally {
            results.close();
        }
    }

    /**
     * TMDM-8970 : exception occured when launching this test before fix test for id of int type
     * 
     * @throws Exception
     */
    public void testFullSearchCountry() throws Exception {
        UserQueryBuilder qb = from(country).select(prepareSelectCountryFields(country)).where(fullText("France"));
        StorageResults results = storage.fetch(qb.getSelect());
        try {
            // debug code for unstable case
            for(DataRecord dataRecord: results){
                assertTrue(String.valueOf(dataRecord.get("name")).startsWith("France"));
            }
            assertEquals(11, results.getCount());
        } finally {
            results.close();
        }
    }

    /**
     * TMDM-8970 : exception occured when launching this test before fix test for id of long type
     * 
     * @throws Exception
     */
    public void testFullSearchCountryLong() throws Exception {
        UserQueryBuilder qb = from(countryLong).select(prepareSelectCountryLongFields(countryLong)).where(fullText("F"));
        qb.limit(2);
        StorageResults results = storage.fetch(qb.getSelect());
        try {
            assertEquals(3, results.getCount());
            assertEquals(2, results.getSize());
        } finally {
            results.close();
        }
    }

    /**
     * TMDM-8970 : exception occured when launching this test before fix test for id of short type
     * 
     * @throws Exception
     */
    public void testFullSearchCountryShort() throws Exception {
        UserQueryBuilder qb = from(countryShort).select(prepareSelectCountryShortFields(countryShort)).where(fullText("F"));
        qb.limit(2);
        StorageResults results = storage.fetch(qb.getSelect());
        try {
            assertEquals(4, results.getCount());
            assertEquals(2, results.getSize());
        } finally {
            results.close();
        }
    }

    public void testCollectionSearch() throws Exception {
        UserQueryBuilder qb = from(product).select(prepareSelectProductFields(product)).where(fullText("Blue"));

        StorageResults results = storage.fetch(qb.getSelect());
        try {
            assertEquals(3, results.getCount());
        } finally {
            results.close();
        }
    }
    
    private List<FieldMetadata> prepareSelectProductFamilyFields(ComplexTypeMetadata supplier) {
        List<FieldMetadata> results = new ArrayList<>();
        results.add(supplier.getField("Id"));
        results.add(supplier.getField("Name"));
        return results;
    }
    
    private List<FieldMetadata> prepareSelectCountryShortFields(ComplexTypeMetadata countryShort) {
        List<FieldMetadata> results = new ArrayList<>();
        results.add(countryShort.getField("id"));
        results.add(countryShort.getField("creationDate"));
        results.add(countryShort.getField("creationTime"));
        results.add(countryShort.getField("name"));
        results.add(countryShort.getField("notes/note"));
        results.add(countryShort.getField("notes/comment"));
        return results;
    }
    
    private List<FieldMetadata> prepareSelectCountryLongFields(ComplexTypeMetadata countryLong) {
        List<FieldMetadata> results = new ArrayList<>();
        results.add(countryLong.getField("id"));
        results.add(countryLong.getField("creationDate"));
        results.add(countryLong.getField("creationTime"));
        results.add(countryLong.getField("name"));
        results.add(countryLong.getField("notes/note"));
        results.add(countryLong.getField("notes/comment"));
        return results;
    }
    
    private List<FieldMetadata> prepareSelectSupplierFields(ComplexTypeMetadata supplier) {
        List<FieldMetadata> results = new ArrayList<>();
        results.add(supplier.getField("Id"));
        results.add(supplier.getField("SupplierName"));
//        results.add(supplier.getField("Address"));
        results.add(supplier.getField("Contact/Name"));
        results.add(supplier.getField("Contact/Phone"));
        results.add(supplier.getField("Contact/Email"));
        return results;
    }
    
    
    private List<FieldMetadata> prepareSelectCountryFields(ComplexTypeMetadata country) {
        List<FieldMetadata> results = new ArrayList<>();
        results.add(country.getField("id"));
        results.add(country.getField("creationDate"));
        results.add(country.getField("creationTime"));
        results.add(country.getField("name"));
        results.add(country.getField("notes/note"));
        results.add(country.getField("notes/comment"));
        return results;
    }

    private List<FieldMetadata> prepareSelectEmployeeFields(ComplexTypeMetadata employ) {
        List<FieldMetadata> results = new ArrayList<>();
        results.add(employ.getField("name"));
        return results;
    }

    private List<FieldMetadata> prepareSelectManagerFields(ComplexTypeMetadata manager) {
        List<FieldMetadata> results = new ArrayList<>();
        results.add(manager.getField("name"));
        return results;
    }

    private List<FieldMetadata> prepareSelectPersonsFields(ComplexTypeMetadata persons) {
        List<FieldMetadata> results = new ArrayList<>();
        results.add(persons.getField("name"));
        return results;
    }

    private List<FieldMetadata> prepareContractFields(ComplexTypeMetadata contract) {
        List<FieldMetadata> results = new ArrayList<>();
        results.add(contract.getField("id"));
        results.add(contract.getField("comment"));
        results.add(contract.getField("enumEle"));
        return results;
    }

    private List<FieldMetadata> prepareSelectProductFields(ComplexTypeMetadata product) {
        List<FieldMetadata> results = new ArrayList<>();
        results.add(product.getField("Family"));
        results.add(product.getField("Stores/Store"));
        results.add(product.getField("Supplier"));
        results.add(product.getField("Id"));
        results.add(product.getField("Availability"));
        results.add(product.getField("Price"));
        results.add(product.getField("Product"));
        results.add(product.getField("ShortDescription"));
        results.add(product.getField("Name"));
        results.add(product.getField("ShortDescription"));
        results.add(product.getField("RemovalDate"));
        results.add(product.getField("CreationDate"));
        results.add(product.getField("Features/Sizes/Size"));
        results.add(product.getField("Features/Colors/Color"));
        return results;
    }

    public void testSimpleSearchWithCondition() throws Exception {
        UserQueryBuilder qb = from(supplier).select(prepareSelectSupplierFields(supplier)).where(fullText("Renault")).where(
                eq(supplier.getField("Contact/Name"), "Jean Voiture"));
        StorageResults results = storage.fetch(qb.getSelect());
        try {
            assertEquals(1, results.getCount());
        } finally {
            results.close();
        }

        qb = from(supplier).select(prepareSelectSupplierFields(supplier)).where(fullText("Renault")).where(eq(supplier.getField("Id"), "1"));
        results = storage.fetch(qb.getSelect());
        try {
            assertEquals(1, results.getCount());
        } finally {
            results.close();
        }

        qb = from(supplier).select(prepareSelectSupplierFields(supplier)).where(fullText("Renault")).where(eq(supplier.getField("Id"), "2"));
        results = storage.fetch(qb.getSelect());
        try {
            assertEquals(0, results.getCount());
        } finally {
            results.close();
        }
    }

    public void testSimpleSearchWithWildcard() throws Exception {
        UserQueryBuilder qb = from(supplier).select(prepareSelectSupplierFields(supplier)).where(fullText("*"));
        StorageResults results = storage.fetch(qb.getSelect());
        try {
            assertEquals(4, results.getCount());
            int actualCount = 0;
            for (DataRecord result : results) {
                actualCount++;
            }
            assertEquals(4, actualCount);
        } finally {
            results.close();
        }
        //
        qb = from(supplier).select(prepareSelectSupplierFields(supplier)).where(fullText("**"));
        results = storage.fetch(qb.getSelect());
        try {
            assertEquals(4, results.getCount());
            int actualCount = 0;
            for (DataRecord result : results) {
                actualCount++;
            }
            assertEquals(4, actualCount);
        } finally {
            results.close();
        }
    }

    public void testSimpleSearchWithWildcardOnTypes() throws Exception {
        UserQueryBuilder qb = from(supplier).and(product)
                .select(prepareSelectProductFields(product))
                .select(prepareSelectSupplierFields(supplier)).where(fullText("*"));
        StorageResults results = storage.fetch(qb.getSelect());
        try {
            assertEquals(9, results.getCount());
            int actualCount = 0;
            for (DataRecord result : results) {
                actualCount++;
            }
            assertEquals(9, actualCount);
        } finally {
            results.close();
        }
        //
        qb = from(supplier).and(product)
                .select(prepareSelectProductFields(product))
                .select(prepareSelectSupplierFields(supplier))
                .where(fullText("**"));
        results = storage.fetch(qb.getSelect());
        try {
            assertEquals(9, results.getCount());
            int actualCount = 0;
            for (DataRecord result : results) {
                actualCount++;
            }
            assertEquals(9, actualCount);
        } finally {
            results.close();
        }
    }

    public void testSimpleSearchWithSpace() throws Exception {
        UserQueryBuilder qb = from(supplier).select(prepareSelectSupplierFields(supplier)).where(fullText(" "));
        StorageResults results = storage.fetch(qb.getSelect());
        try {
            assertEquals(4, results.getCount());
        } finally {
            results.close();
        }

        qb = from(supplier).select(prepareSelectSupplierFields(supplier)).where(fullText("     "));
        results = storage.fetch(qb.getSelect());
        try {
            assertEquals(4, results.getCount());
        } finally {
            results.close();
        }

        qb = from(supplier).and(product)
                .select(prepareSelectProductFields(product))
                .select(prepareSelectSupplierFields(supplier)).where(fullText("     "));
        results = storage.fetch(qb.getSelect());
        try {
            assertEquals(9, results.getCount());
        } finally {
            results.close();
        }
    }

    public void testSimpleSearchWithContainsCondition() throws Exception {
        UserQueryBuilder qb = from(supplier).select(prepareSelectSupplierFields(supplier)).where(fullText("Renault"))
                .where(contains(supplier.getField("Contact/Name"), "Jean"));
        StorageResults results = storage.fetch(qb.getSelect());
        try {
            assertEquals(1, results.getCount());
        } finally {
            results.close();
        }

        qb = from(supplier).select(prepareSelectSupplierFields(supplier)).where(fullText("Talend")).where(contains(supplier.getField("Contact/Name"), "Jean"));
        results = storage.fetch(qb.getSelect());
        try {
            assertEquals(2, results.getCount());
        } finally {
            results.close();
        }
    }

    public void testSimpleSearchWithContainsConditionAndNot() throws Exception {
        UserQueryBuilder qb = from(supplier).select(prepareSelectSupplierFields(supplier)).where(fullText("Renault")).where(
                and(contains(supplier.getField("Contact/Name"), "Jean"), not(eq(supplier.getField("Id"), "0"))));
        StorageResults results = storage.fetch(qb.getSelect());
        try {
            assertEquals(1, results.getCount());
            for (DataRecord result : results) {
                assertNotSame(0, result.get("Id"));
            }
        } finally {
            results.close();
        }
    }

    public void testSimpleSearchWithGreaterThanCondition() throws Exception {
        UserQueryBuilder qb = from(supplier).select(prepareSelectSupplierFields(supplier)).where(fullText("Renault")).where(gt(supplier.getField("Id"), "1"));
        try {
            storage.fetch(qb.getSelect());
            fail("Expected: not supported.");
        } catch (Exception e) {
            // Expected.
        }

        qb = from(supplier).select(prepareSelectSupplierFields(supplier)).where(fullText("Renault")).where(gte(supplier.getField("Id"), "1"));
        try {
            storage.fetch(qb.getSelect());
            fail("Expected: not supported.");
        } catch (Exception e) {
            // Expected.
        }

        qb = from(supplier).select(prepareSelectSupplierFields(supplier)).where(fullText("Jean")).where(gte(supplier.getField("Id"), "2"));
        try {
            storage.fetch(qb.getSelect());
            fail("Expected: not supported.");
        } catch (Exception e) {
            // Expected.
        }
    }

    public void testSimpleSearchWithContainsAndIsEmptyNullCondition() throws Exception {
        UserQueryBuilder qb = from(supplier).select(prepareSelectSupplierFields(supplier)).where(
                and(contains(supplier.getField("Id"), "4"),
                        or(isEmpty(supplier.getField("Contact/Email")), isNull(supplier.getField("Contact/Email")))));
        StorageResults results = storage.fetch(qb.getSelect());
        try {
            assertEquals(1, results.getCount());
        } finally {
            results.close();
        }

        qb = from(supplier).select(prepareSelectSupplierFields(supplier)).where(
                and(contains(supplier.getField("Contact/Name"), "John"),
                        or(isNull(supplier.getField("Contact/Email")), isEmpty(supplier.getField("Contact/Email")))));
        results = storage.fetch(qb.getSelect());
        try {
            assertEquals(1, results.getCount());
        } finally {
            results.close();
        }
    }

    public void testSimpleSearchWithLessThanCondition() throws Exception {
        UserQueryBuilder qb = from(supplier).select(prepareSelectSupplierFields(supplier)).where(fullText("Renault")).where(lt(supplier.getField("Id"), "1"));
        try {
            storage.fetch(qb.getSelect());
            fail("Expected: not supported.");
        } catch (Exception e) {
            // Expected.
        }

        qb = from(supplier).select(prepareSelectSupplierFields(supplier)).where(fullText("Renault")).where(lte(supplier.getField("Id"), "1"));
        try {
            storage.fetch(qb.getSelect());
            fail("Expected: not supported.");
        } catch (Exception e) {
            // Expected.
        }
    }

    public void testSimpleSearchWithJoin() throws Exception {
        UserQueryBuilder qb = from(product).and(productFamily)
                .selectId(product).select(productFamily.getField("Name"))
                .select(product.getField("Name"))
                .where(fullText("Renault")).join(product.getField("Family"));
        StorageResults results = storage.fetch(qb.getSelect());
        try {
            assertEquals(1, results.getCount());
            for (DataRecord result : results) {
                assertNotNull(result.get("Id"));
                assertEquals("Renault car", result.get("Name"));
            }
        } finally {
            results.close();
        }

        qb = from(product).and(productFamily)
                .selectId(product).select(productFamily.getField("Name"))
                .select(product.getField("Name")).where(fullText("Renault"))
                .join(product.getField("Family")).limit(20);

        results = storage.fetch(qb.getSelect());
        try {
            assertEquals(1, results.getCount());
            for (DataRecord result : results) {
                assertNotNull(result.get("Id"));
                assertEquals("Renault car", result.get("Name"));
            }
        } finally {
            results.close();
        }
    }

    public void testSimpleSearchWithProjection() throws Exception {
        UserQueryBuilder qb = from(supplier).select(supplier.getField("SupplierName")).select(supplier.getField("Contact/Name")).where(fullText("Renault"));

        StorageResults results = storage.fetch(qb.getSelect());
        try {
            assertEquals(1, results.getCount());
            for (DataRecord result : results) {
                assertEquals(2, result.getSetFields().size());
                assertNotNull(result.get("Name"));
            }
        } finally {
            results.close();
        }
    }

    public void testSimpleSearchWithProjectionAlias() throws Exception {
        UserQueryBuilder qb = from(supplier).select(supplier.getField("SupplierName")).select(alias(supplier.getField("Contact/Name"), "element")).where(
                fullText("Renault"));
        StorageResults results = storage.fetch(qb.getSelect());
        try {
            assertEquals(1, results.getCount());
            for (DataRecord result : results) {
                assertEquals(2, result.getSetFields().size());
                assertNotNull(result.get("element"));
            }
        } finally {
            results.close();
        }

        qb = from(supplier).select(supplier.getField("SupplierName")).select(alias(supplier.getField("Contact/Name"), "element")).where(fullText("Renault")).start(0)
                .limit(20);
        results = storage.fetch(qb.getSelect());
        try {
            assertEquals(1, results.getCount());
            for (DataRecord result : results) {
                assertEquals(2, result.getSetFields().size());
                assertNotNull(result.get("element"));
            }
        } finally {
            results.close();
        }
    }

    public void testFKSearchWithProjection() throws Exception {
        UserQueryBuilder qb = from(product).select(product.getField("Name")).select(product.getField("Family")).where(fullText("car"));

        StorageResults results = storage.fetch(qb.getSelect());
        try {
            assertEquals(1, results.getCount());
            ViewSearchResultsWriter writer = new ViewSearchResultsWriter();
            StringWriter resultWriter = new StringWriter();
            for (DataRecord result : results) {
                writer.write(result, resultWriter);
            }
            assertEquals("<result xmlns:metadata=\"http://www.talend.com/mdm/metadata\" "
                    + "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n" + "\t<Name>Renault car</Name>\n" + "\t<Family>[1]</Family>\n" + "</result>",
                    resultWriter.toString());
        } finally {
            results.close();
        }
    }

    public void testMultipleTypesSearchWithCondition() throws Exception {
        UserQueryBuilder qb = from(supplier).select(prepareSelectSupplierFields(supplier)).where(fullText("Renault")).where(
                eq(supplier.getField("Contact/Name"), "Jean Voiture"));

        StorageResults results = storage.fetch(qb.getSelect());
        try {
            assertEquals(1, results.getCount());
        } finally {
            results.close();
        }
    }

    public void testFullTextSuggestion() throws Exception {
        try {
            storage.getFullTextSuggestion("Ren", Storage.FullTextSuggestion.START, 3);
            fail("Expected due to Lucene version being used.");
        } catch (Exception e) {
            // Expected.
        }
    }

    public void testFullTextAlternative() throws Exception {
        try {
            storage.getFullTextSuggestion("strabuks", Storage.FullTextSuggestion.ALTERNATE, 3);
            fail("Expected due to Lucene version being used");
        } catch (Exception e) {
            // Expected
        }

    }

    public void testFullTextResultsFormat() throws Exception {
        UserQueryBuilder qb = from(product).select(prepareSelectProductFields(product)).where(fullText("Renault"));

        StorageResults results = null;
        try {
            results = storage.fetch(qb.getSelect());
            assertEquals(1, results.getCount());

            DataRecordWriter writer = new FullTextResultsWriter("Renault");
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            for (DataRecord result : results) {
                writer.write(result, output);
            }
            assertTrue(output.toString().contains("<b>Renault car</b>"));
        } finally {
            if (results != null) {
                results.close();
            }
        }
    }

    public void testFullTextResultsInNestedFormat() throws Exception {
        UserQueryBuilder qb = from(product).select(prepareSelectProductFields(product)).where(fullText("Klein"));

        StorageResults results = null;
        try {
            results = storage.fetch(qb.getSelect());
            assertEquals(1, results.getCount());

            DataRecordWriter writer = new FullTextResultsWriter("Klein");
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            for (DataRecord result : results) {
                writer.write(result, output);
            }
            assertTrue(output.toString().contains("<b>Klein blue2</b>"));
        } finally {
            if (results != null) {
                results.close();
            }
        }
    }

    public void testNoFullText() throws Exception {
        Storage storage = new HibernateStorage("noFullText");
        try {
            storage.init(getDatasource("RDBMS-1-NO-FT"));
            storage.prepare(repository, Collections.<Expression> emptySet(), false, false);
            UserQueryBuilder qb = from(product).select(prepareSelectProductFields(product)).where(fullText("Test"));

            try {
                storage.fetch(qb.getSelect());
                fail("Full text is not enabled");
            } catch (Exception e) {
                assertEquals("Storage 'noFullText(MASTER)' is not configured to support full text queries.", e.getCause().getMessage());
            }
        } finally {
            storage.close();
        }
    }

    public void testSimpleWithoutFkValueSearch() throws Exception {
        UserQueryBuilder qb = from(product).select(prepareSelectProductFields(product)).select(product.getField("Family")).where(fullText("talend")).limit(20);
        StorageResults results = storage.fetch(qb.getSelect());
        try {
            assertEquals(1, results.getCount());
            for (DataRecord result : results) {
                assertEquals(null, result.get("Family"));
            }
        } finally {
            results.close();
        }
    }

    public void testFullTestWithCompositeKeySearch() throws Exception {

        try {
            UserQueryBuilder qb = from(a1).selectId(a1).select(a1.getField("b1")).select(a1.getField("b2"))
                    .where(fullText("String")).limit(20);
            storage.fetch(qb.getSelect());
            fail();
        } catch (RuntimeException runtimeException) {
            if (FullTextQueryCompositeKeyException.class.isInstance(runtimeException.getCause())) {
                assertEquals("a1", runtimeException.getCause().getMessage());
            } else {
                throw runtimeException;
            }
        }
    }

    public void testFieldFullText() throws Exception {
        UserQueryBuilder qb = from(product).select(prepareSelectProductFields(product)).where(fullText(product.getField("ShortDescription"), "description")).limit(20);
        StorageResults results = storage.fetch(qb.getSelect());
        try {
            assertEquals(1, results.getCount());
        } finally {
            results.close();
        }

        qb = from(product).select(prepareSelectProductFields(product)).where(fullText(product.getField("ShortDescription"), "long")).limit(20);
        results = storage.fetch(qb.getSelect());
        try {
            assertEquals(0, results.getCount());
        } finally {
            results.close();
        }
    }

    public void testFullTextAndRangeTimeQuery() throws Exception {
        // Original case not working due to a issue in Lucene, where some collectors cannot accept out-of-order scoring.
        // should be fixed in Lucene 5.0, https://issues.apache.org/jira/browse/LUCENE-6179
        UserQueryBuilder qb = from(product).select(prepareSelectProductFields(product)).where(
                or(fullText(product.getField("ShortDescription"), "description"),
                        and(gte(timestamp(), "0"), lte(timestamp(), String.valueOf(System.currentTimeMillis()))))).limit(20);
        StorageResults results = storage.fetch(qb.getSelect());
        try {
            assertEquals(1, results.getCount());
        } finally {
            results.close();
        }
    }

    public void testTaskIdProjection() throws Exception {
        UserQueryBuilder qb = from(product).select(prepareSelectProductFields(product)).select(alias(taskId(), "taskId")).select(alias(error(), "error"))
                .where(fullText(product.getField("ShortDescription"), "description"));
        StorageResults results = storage.fetch(qb.getSelect());
        try {
            assertEquals(1, results.getCount());
            for (DataRecord result : results) {
                assertNull(result.get("taskId"));
                assertNull(result.get("error"));
            }
        } finally {
            results.close();
        }

        qb = from(product).select(prepareSelectProductFields(product)).select(alias(taskId(), "taskId")).select(alias(error(), "error"))
                .where(fullText(product.getField("ShortDescription"), "description")).limit(20);
        results = storage.fetch(qb.getSelect());
        try {
            assertEquals(1, results.getCount());
            for (DataRecord result : results) {
                assertNull(result.get("taskId"));
                assertNull(result.get("error"));
            }
        } finally {
            results.close();
        }
    }

    public void testSearchOnContainedType() throws Exception {
        UserQueryBuilder qb = from(product).where(fullText(product.getField("Features"), "klein"));
        StorageResults results = storage.fetch(qb.getSelect());
        try {
            assertEquals(1, results.getCount());
            for (DataRecord result : results) {
                Object object = result.get("Features/Colors/Color");
                assertTrue(object instanceof List);
                assertEquals(3, ((List) object).size());
                assertEquals("Klein blue2", ((List) object).get(2));
            }
        } finally {
            results.close();
        }

        qb = UserQueryBuilder.from(product);
        String fieldName = "Product/Features";
        IWhereItem item = new WhereAnd(Arrays.<IWhereItem> asList(new WhereCondition(fieldName, WhereCondition.FULLTEXTSEARCH,
                "klein", WhereCondition.NO_OPERATOR)));
        Condition condition = UserQueryHelper.buildCondition(qb, item, repository);
        Expression normalizedCondition = condition.normalize();
        assertTrue(normalizedCondition instanceof BinaryLogicOperator);
        assertTrue(((BinaryLogicOperator) normalizedCondition).getLeft() instanceof FieldFullText);
        qb = qb.where(condition);
        results = storage.fetch(qb.getSelect());
        try {
            assertEquals(1, results.getCount());
            for (DataRecord result : results) {
                Object object = result.get("Features/Colors/Color");
                assertTrue(object instanceof List);
                assertEquals(3, ((List) object).size());
                assertEquals("Klein blue2", ((List) object).get(2));
            }
        } finally {
            results.close();
        }
    }

    public void testTimeStampProjectionNoAlias() throws Exception {
        // TMDM-7737: Test metadata field projection *with* paging in query.
        UserQueryBuilder qb = from(product).select(timestamp()).where(fullText(product.getField("Features"), "klein")).start(0)
                .limit(20);
        StorageResults results = storage.fetch(qb.getSelect());
        try {
            assertEquals(1, results.getCount());
        } finally {
            results.close();
        }
    }

    public void testIdFieldContainUpperCaseKeyWordSearch() throws Exception {
        UserQueryBuilder qb = from(store).selectId(store).where(contains(store.getField("Id"), "case"));
        storage.begin();
        StorageResults results = storage.fetch(qb.getSelect());
        try {
            assertEquals(2, results.getCount());
        } finally {
            results.close();
        }

        qb = from(store).selectId(store).where(fullText("case"));
        storage.begin();
        results = storage.fetch(qb.getSelect());
        try {
            assertEquals(2, results.getCount());
        } finally {
            results.close();
        }
    }

    // TMDM-8798 FK constraint warning when creating a fk on the fly with special character
    public void testIdFieldContainSpecialCharacter() throws Exception {
        UserQueryBuilder qb = from(store).selectId(store).where(contains(store.getField("Id"), "ab&cd"));
        storage.begin();
        StorageResults results = storage.fetch(qb.getSelect());
        try {
            assertEquals(3, results.getCount());
        } finally {
            results.close();
        }

        qb = from(store).selectId(store).where(contains(store.getField("Id"), "ab"));
        storage.begin();
        results = storage.fetch(qb.getSelect());
        try {
            assertEquals(3, results.getCount());
        } finally {
            results.close();
        }

        qb = from(store).selectId(store).where(contains(store.getField("Id"), "AB&CD"));
        storage.begin();
        results = storage.fetch(qb.getSelect());
        try {
            assertEquals(3, results.getCount());
        } finally {
            results.close();
        }
        
        qb = from(store).selectId(store).where(contains(store.getField("Id"), "one"));
        storage.begin();
        results = storage.fetch(qb.getSelect());
        try {
            assertEquals(1, results.getCount());
        } finally {
            results.close();
        }
        
        qb = from(store).selectId(store).where(contains(store.getField("Id"), "two"));
        storage.begin();
        results = storage.fetch(qb.getSelect());
        try {
            assertEquals(1, results.getCount());
        } finally {
            results.close();
        }
        
        qb = from(store).selectId(store).where(contains(store.getField("Id"), "three"));
        storage.begin();
        results = storage.fetch(qb.getSelect());
        try {
            assertEquals(1, results.getCount());
        } finally {
            results.close();
        }
        
        qb = from(store).selectId(store).where(contains(store.getField("Id"), "four"));
        storage.begin();
        results = storage.fetch(qb.getSelect());
        try {
            assertEquals(1, results.getCount());
        } finally {
            results.close();
        }

        qb = from(store).selectId(store).where(fullText("ab&cd"));
        storage.begin();
        results = storage.fetch(qb.getSelect());
        try {
            assertEquals(3, results.getCount());
        } finally {
            results.close();
        }

        qb = from(store).selectId(store).where(fullText("ab"));
        storage.begin();
        results = storage.fetch(qb.getSelect());
        try {
            assertEquals(3, results.getCount());
        } finally {
            results.close();
        }

        qb = from(store).selectId(store).where(fullText("AB&CD"));
        storage.begin();
        results = storage.fetch(qb.getSelect());
        try {
            assertEquals(3, results.getCount());
        } finally {
            results.close();
        }
        
        qb = from(store).selectId(store).where(fullText("one"));
        storage.begin();
        results = storage.fetch(qb.getSelect());
        try {
            assertEquals(1, results.getCount());
        } finally {
            results.close();
        }
        
        qb = from(store).selectId(store).where(fullText("two"));
        storage.begin();
        results = storage.fetch(qb.getSelect());
        try {
            assertEquals(1, results.getCount());
        } finally {
            results.close();
        }
        
        qb = from(store).selectId(store).where(fullText("three"));
        storage.begin();
        results = storage.fetch(qb.getSelect());
        try {
            assertEquals(1, results.getCount());
        } finally {
            results.close();
        }
        
        qb = from(store).selectId(store).where(fullText("four"));
        storage.begin();
        results = storage.fetch(qb.getSelect());
        try {
            assertEquals(1, results.getCount());
        } finally {
            results.close();
        }
    }

    public void testGenerateIdFetchSize() throws Exception {
        // Test "stream resultset"
        RDBMSDataSource dataSource = new RDBMSDataSource("TestDataSource", "MySQL", "", "", "", 0, 0, "", "", false, "update",
                false, new HashMap(), "", "", null, "", "", "", false);
        Configuration configuration = new Configuration();
        configuration.setProperty(Environment.STATEMENT_FETCH_SIZE, "1000");
        HibernateStorage storage = new HibernateStorage("HibernateStorage");
        storage.init(getDatasource("RDBMS-1-NO-FT"));
        storage.prepare(repository, Collections.<Expression> emptySet(), false, false);

        Class storageClass = storage.getClass();
        Field dataSourceField = storageClass.getDeclaredField("dataSource");
        dataSourceField.setAccessible(true);
        dataSourceField.set(storage, dataSource);

        Field configurationField = storageClass.getDeclaredField("configuration");
        configurationField.setAccessible(true);
        configurationField.set(storage, configuration);

        Method generateIdFetchSizeMethod = storageClass.getDeclaredMethod("generateIdFetchSize", null);
        generateIdFetchSizeMethod.setAccessible(true);
        assertEquals(Integer.MIN_VALUE, generateIdFetchSizeMethod.invoke(storage, null));

        // Test config batch size
        dataSource = new RDBMSDataSource("TestDataSource", "H2", "", "", "", 0, 0, "", "", false, "update", false, new HashMap(),
                "", "", null, "", "", "", false);
        storage = new HibernateStorage("HibernateStorage");
        storage.init(getDatasource("RDBMS-1-NO-FT"));
        storage.prepare(repository, Collections.<Expression> emptySet(), false, false);
        storageClass = storage.getClass();

        dataSourceField = storageClass.getDeclaredField("dataSource");
        dataSourceField.setAccessible(true);
        dataSourceField.set(storage, dataSource);

        configurationField = storageClass.getDeclaredField("configuration");
        configurationField.setAccessible(true);
        configurationField.set(storage, configuration);
        assertEquals(1000, generateIdFetchSizeMethod.invoke(storage, null));

        // Test default batch size
        configuration = new Configuration();
        configurationField = storageClass.getDeclaredField("configuration");
        configurationField.setAccessible(true);
        configurationField.set(storage, configuration);
        assertEquals(500, generateIdFetchSizeMethod.invoke(storage, null));
    }

    public void testFieldQueryWhenHavingCompositeFK() throws Exception {

        UserQueryBuilder qb = from(a3).select(a3.getField("id")).select(a3.getField("a2"))
                .where(fullText(a3.getField("name"), "hamdi")).limit(20);
        StorageResults results = storage.fetch(qb.getSelect());
        Exception exception = null;
        try {
            assertEquals(1, results.getCount());
            DataRecord result = results.iterator().next();
            Object b2 = result.get(a3.getField("a2"));
            assertTrue(b2 instanceof Object[]);
            assertEquals("1", ((Object[]) b2)[0]);
            assertEquals("2", ((Object[]) b2)[1]);
        } catch (Exception e) {
            exception = e;
        } finally {
            results.close();
        }
        assertNull(exception);
    }

    public void testSearchOnMultiLingualType() throws Exception {
        UserQueryBuilder qb = from(person).select(person.getField("id")).where(contains(person.getField("resume"), " world "));
        storage.begin();
        StorageResults results = storage.fetch(qb.getSelect());
        try {
            assertEquals(1, results.getCount());
            for (DataRecord result : results) {
                assertNotNull(result.get("id"));
                assertEquals("1234", result.get("id"));
            }
        } finally {
            results.close();
        }

        qb = from(person).selectId(person).where(contains(person.getField("resume"), " bon "));
        storage.begin();
        results = storage.fetch(qb.getSelect());
        try {
            assertEquals(1, results.getCount());
            for (DataRecord result : results) {
                assertNotNull(result.get("id"));
                assertEquals("1234", result.get("id"));
            }
        } finally {
            results.close();
        }

        qb = from(person).selectId(person).where(contains(person.getField("resume"), " le "));
        storage.begin();
        results = storage.fetch(qb.getSelect());
        try {
            assertEquals(1, results.getCount());
            for (DataRecord result : results) {
                assertNotNull(result.get("id"));
                assertEquals("1234", result.get("id"));
            }
        } finally {
            results.close();
        }

        qb = from(person).selectId(person).where(contains(person.getField("resume"), "hao"));
        storage.begin();
        results = storage.fetch(qb.getSelect());
        try {
            assertEquals(1, results.getCount());
            for (DataRecord result : results) {
                assertNotNull(result.get("id"));
                assertEquals("1234", result.get("id"));
            }
        } finally {
            results.close();
        }
    }

    public void testFullTextOnRepeatable() throws Exception {
        UserQueryBuilder qb = UserQueryBuilder.from(country).selectId(country)
                .select(country.getField("notes/comment")).where(fullText("repeatable"));
        StorageResults results = storage.fetch(qb.getSelect());
        for (DataRecord result : results) {
            assertNotNull(result.getType());
            assertEquals("Country", result.getType().getField("comment").getEntityTypeName());
            assertEquals(2, result.get("id"));
        }
    }

    public void testFullText() throws Exception {
        UserQueryBuilder qb = UserQueryBuilder.from(country).selectId(country)
                .select(prepareSelectCountryFields(country)).where(fullText("note"));
        StorageResults results = storage.fetch(qb.getSelect());
        for (DataRecord result : results) {
            assertNotNull(result.getType());
            assertEquals("Country", result.getType().getField("comment").getEntityTypeName());
            assertEquals(2, result.get("id"));
        }

        qb = UserQueryBuilder.from(fullTextSearchEntityA)
                .selectId(fullTextSearchEntityA)
                .select(fullTextSearchEntityA.getField("Id"))
                .select(fullTextSearchEntityA.getField("Address"))
                .select(fullTextSearchEntityA.getField("Name"))
                .where(fullText("id1 city"));
        results = storage.fetch(qb.getSelect());
        assertEquals(1, results.getCount());
        for (DataRecord result : results) {
            assertEquals("id1", result.get("Id"));
        }
    }

    public void testFullTextWithMultiKeywords() throws Exception {
        UserQueryBuilder qb = UserQueryBuilder.from(supplier).where(contains(supplier.getField("SupplierName"), "Star Id"));
        StorageResults results = storage.fetch(qb.getSelect());
        assertEquals(2, results.getCount());
        for (DataRecord result : results) {
            if ("2".equals(result.get("Id"))) {
                assertEquals("Starbucks Talend", result.get("SupplierName"));
            }
            if ("4".equals(result.get("Id"))) {
                assertEquals("IdSoftware", result.get("SupplierName"));
            }
        }
    }

    public void testTypeSplitWithPaging() throws Exception {
        // Build expected results
        UserQueryBuilder qb = UserQueryBuilder.from(person)
                .selectId(person)
                .select(person.getField("firstname"))
                .where(fullText("Julien"));
        List<String> expected = new LinkedList<String>();
        StorageResults records = storage.fetch(qb.getSelect());
        int count = records.getCount();
        int size = records.getSize();
        for (DataRecord record : records) {
            expected.add(String.valueOf(record.get("id")));
        }
        // Ensures split behavior is same as no split
        StorageResults split = Split.fetchAndMerge(storage, qb.getSelect());
        for (DataRecord record : split) {
            assertTrue(expected.remove(String.valueOf(record.get("id"))));
        }
        assertEquals(count, split.getCount());
        assertEquals(size, split.getSize());

    }

    public void testTypeSplit() throws Exception {
        // Build expected results
        UserQueryBuilder qb = UserQueryBuilder.from(person)
                .selectId(person).select(person.getField("firstname")).where(fullText("Julien"));
        List<DataRecord> expected = new LinkedList<DataRecord>();
        StorageResults records = storage.fetch(qb.getSelect());
        int count = records.getCount();
        int size = records.getSize();
        for (DataRecord record : records) {
            expected.add(record);
        }
        // Ensures split behavior is same as no split
        StorageResults split = Split.fetchAndMerge(storage, qb.getSelect());
        int i = 0;
        for (DataRecord record : split) {
            assertEquals(record, expected.get(i++));
        }
        assertEquals(count, split.getCount());
        assertEquals(size, split.getSize());
    }

    public void testContainsWithUnderscoreInSearchWords3() throws Exception {
        UserQueryBuilder qb = from(productFamily).where(contains(productFamily.getField("Name"), "te*t_nam*"));

        Select select = qb.getSelect();
        assertNotNull(select);
        Condition condition = select.getCondition();
        assertNotNull(condition);
        assertTrue(condition instanceof Compare);
        Compare compareCondition = (Compare) condition;
        Expression right = compareCondition.getRight();
        assertTrue(right instanceof StringConstant);
        assertEquals("te*t_nam*", ((StringConstant) right).getValue());

        StorageResults results = storage.fetch(qb.getSelect());
        try {
            assertEquals(3, results.getCount());
        } finally {
            results.close();
        }
    }

    public void testManyFieldUsingAndContainsCondition() throws Exception {
        UserQueryBuilder qb = from(product).where(contains(product.getField("Features/Sizes/Size"), "ValueDoesNotExist"));
        StorageResults results = storage.fetch(qb.getSelect());
        assertEquals(0, results.getCount());

        qb = from(product).where(
                and(eq(product.getField("Id"), "1"), contains(product.getField("Features/Sizes/Size"), "ValueDoesNotExist")));
        results = storage.fetch(qb.getSelect());
        assertEquals(0, results.getCount());

        qb = from(product).where(contains(product.getField("Features/Sizes/Size"), "large"));
        results = storage.fetch(qb.getSelect());
        assertEquals(3, results.getCount());
    }

    public void testContainsWithReservedCharacters() throws Exception {
        DataSourceDefinition definition = getDatasource(DATASOURCE_FULLTEXT);
        DataSource datasource = definition.getMaster();
        assertTrue(datasource instanceof RDBMSDataSource);
        RDBMSDataSource rdbmsDataSource = (RDBMSDataSource) datasource;
        TestRDBMSDataSource testDataSource = new TestRDBMSDataSource(rdbmsDataSource);
        testDataSource.setCaseSensitiveSearch(false);
        testDataSource.setSupportFullText(true);
        testDataSource.setOptimization(RDBMSDataSource.ContainsOptimization.FULL_TEXT);
        ConfigurableContainsOptimizer optimizer = new ConfigurableContainsOptimizer(testDataSource);
        // Only '-' should disable contains optimization
        UserQueryBuilder qb = UserQueryBuilder.from(person).where(contains(person.getField("id"), "-"));
        Select copy = qb.getSelect().copy();
        optimizer.optimize(copy);
        assertFalse(copy.getCondition() instanceof FieldFullText);
        StorageResults records = storage.fetch(qb.getSelect());
        try {
            assertEquals(0, records.getCount());
        } finally {
            records.close();
        }
        // Only '/' should disable contains optimization
        qb = UserQueryBuilder.from(person).where(contains(person.getField("id"), "/"));
        copy = qb.getSelect().copy();
        optimizer.optimize(copy);
        assertFalse(copy.getCondition() instanceof FieldFullText);
        records = storage.fetch(qb.getSelect());
        try {
            assertEquals(0, records.getCount());
        } finally {
            records.close();
        }
        // Contains optimization should be disabled for next query
        qb = UserQueryBuilder.from(person).where(contains(person.getField("id"), "1-1"));
        copy = qb.getSelect().copy();
        optimizer.optimize(copy);
        assertFalse(copy.getCondition() instanceof FieldFullText);
        records = storage.fetch(qb.getSelect());
        try {
            assertEquals(0, records.getCount());
        } finally {
            records.close();
        }
        // Contains optimization should be enabled
        qb = UserQueryBuilder.from(person).where(contains(person.getField("id"), "_"));
        copy = qb.getSelect().copy();
        optimizer.optimize(copy);
        assertTrue(copy.getCondition() instanceof FieldFullText);
        records = storage.fetch(qb.getSelect());
        try {
            assertEquals(0, records.getCount());
        } finally {
            records.close();
        }
        // Contains optimization should also be enabled
        qb = UserQueryBuilder.from(person).where(contains(person.getField("id"), "1_1"));
        copy = qb.getSelect().copy();
        optimizer.optimize(copy);
        assertTrue(copy.getCondition() instanceof FieldFullText);
        records = storage.fetch(qb.getSelect());
        try {
            assertEquals(0, records.getCount());
        } finally {
            records.close();
        }
    }

    public void testContainsOptimization() throws Exception {
        DataSourceDefinition definition = getDatasource(DATASOURCE_FULLTEXT);
        DataSource datasource = definition.getMaster();
        assertTrue(datasource instanceof RDBMSDataSource);
        RDBMSDataSource rdbmsDataSource = (RDBMSDataSource) datasource;
        TestRDBMSDataSource testDataSource = new TestRDBMSDataSource(rdbmsDataSource);
        testDataSource.setCaseSensitiveSearch(false);
        testDataSource.setSupportFullText(true);
        ConfigurableContainsOptimizer optimizer = new ConfigurableContainsOptimizer(testDataSource);
        // Default optimization
        UserQueryBuilder qb = UserQueryBuilder.from(person).where(
                contains(person.getField("knownAddresses/knownAddress/Notes/Note"), "test note"));
        Select select = qb.getSelect();
        assertTrue(select.getCondition() instanceof Compare);
        assertTrue(((Compare) select.getCondition()).getPredicate() == Predicate.CONTAINS);
        // LIKE optimization
        testDataSource.setOptimization(RDBMSDataSource.ContainsOptimization.LIKE);
        qb = UserQueryBuilder.from(person)
                .where(contains(person.getField("knownAddresses/knownAddress/Notes/Note"), "test note"));
        select = qb.getSelect();
        optimizer.optimize(select);
        assertTrue(select.getCondition() instanceof Compare);
        assertTrue(((Compare) select.getCondition()).getPredicate() == Predicate.CONTAINS);
        // DISABLED optimization
        testDataSource.setOptimization(RDBMSDataSource.ContainsOptimization.DISABLED);
        qb = UserQueryBuilder.from(person)
                .where(contains(person.getField("knownAddresses/knownAddress/Notes/Note"), "test note"));
        select = qb.getSelect();
        try {
            optimizer.optimize(select);
            fail("Contains use is disabled.");
        } catch (Exception e) {
            // Expected
        }
        // FULL TEXT optimization
        testDataSource.setOptimization(RDBMSDataSource.ContainsOptimization.FULL_TEXT);
        qb = UserQueryBuilder.from(person)
                .where(contains(person.getField("knownAddresses/knownAddress/Notes/Note"), "test note"));
        select = qb.getSelect();
        optimizer.optimize(select);
        assertTrue(select.getCondition() instanceof FieldFullText);
        assertEquals("test note", ((FieldFullText) select.getCondition()).getValue());
    }

    public void testContainsOptimizationOnReusableTypes() throws Exception {
        DataSourceDefinition definition = getDatasource(DATASOURCE_FULLTEXT);
        DataSource datasource = definition.getMaster();
        assertTrue(datasource instanceof RDBMSDataSource);
        RDBMSDataSource rdbmsDataSource = (RDBMSDataSource) datasource;
        TestRDBMSDataSource testDataSource = new TestRDBMSDataSource(rdbmsDataSource);
        testDataSource.setCaseSensitiveSearch(false);
        testDataSource.setSupportFullText(true);
        ConfigurableContainsOptimizer optimizer = new ConfigurableContainsOptimizer(testDataSource);
        // Default optimization
        UserQueryBuilder qb = UserQueryBuilder.from(customer).where(contains(customer.getField("address1/Street"), "test note"));
        Select select = qb.getSelect();
        assertTrue(select.getCondition() instanceof Compare);
        assertTrue(((Compare) select.getCondition()).getPredicate() == Predicate.CONTAINS);
        // LIKE optimization
        testDataSource.setOptimization(RDBMSDataSource.ContainsOptimization.LIKE);
        qb = UserQueryBuilder.from(customer).where(contains(customer.getField("address1/Street"), "test note"));
        select = qb.getSelect();
        optimizer.optimize(select);
        assertTrue(select.getCondition() instanceof Compare);
        assertTrue(((Compare) select.getCondition()).getPredicate() == Predicate.CONTAINS);
        // DISABLED optimization
        testDataSource.setOptimization(RDBMSDataSource.ContainsOptimization.DISABLED);
        qb = UserQueryBuilder.from(customer).where(contains(customer.getField("address1/Street"), "test note"));
        select = qb.getSelect();
        try {
            optimizer.optimize(select);
            fail("Contains use is disabled.");
        } catch (Exception e) {
            // Expected
        }
        // FULL TEXT optimization
        testDataSource.setOptimization(RDBMSDataSource.ContainsOptimization.FULL_TEXT);
        qb = UserQueryBuilder.from(customer).where(contains(customer.getField("address1/Street"), "test note"));
        select = qb.getSelect();
        optimizer.optimize(select);
        assertTrue(select.getCondition() instanceof Compare);
        assertTrue(((Compare) select.getCondition()).getPredicate() == Predicate.CONTAINS);
    }

    public void testContainsSentenceSearch() throws Exception {
        UserQueryBuilder qb = from(supplier).where(contains(supplier.getField("Contact/Name"), "'jean cafe'"));
        StorageResults results = storage.fetch(qb.getSelect());
        try {
            assertTrue(results.getSize() == 1);
            for (DataRecord result : results) {
                assertEquals("2", result.get("Id"));
            }
        } finally {
            results.close();
        }

        qb = from(product).select(prepareSelectProductFields(product)).where(and(eq(product.getField("Id"), "2"), contains(product.getField("Name"), "'Renault car'"))).where(fullText("car"));
        results = storage.fetch(qb.getSelect());
        try {
            assertTrue(results.getSize() == 1);
            for (DataRecord result : results) {
                assertEquals("A car", result.get("ShortDescription"));
            }
        } finally {
            results.close();
        }
    }

    public void testFullTextSearchResultContainsMultiOccurReferenceFields() throws Exception {
        UserQueryBuilder qb = from(product).select(product.getField("Name")).select(product.getField("Supplier")).where(fullText("car"));
        StorageResults results = storage.fetch(qb.getSelect());
        try {
            assertEquals(1, results.getCount());
            for (DataRecord result : results) {
                assertEquals("Renault car", (String)result.get("Name"));
                assertEquals("2", ((List)result.get("Supplier")).get(0));
            }
        } finally {
            results.close();
        }
        
        qb = from(product).select(product.getField("Name")).select(product.getField("Supplier")).where(fullText("kevin"));
        results = storage.fetch(qb.getSelect());
        try {
            assertEquals(1, results.getCount());
            for (DataRecord result : results) {
                assertEquals("kevin cui", (String)result.get("Name"));
                assertNull(result.get("Supplier"));
            }
        } finally {
            results.close();
        }
    }
    
    public void testFullTextSearchOnComplexType() throws Exception {
        UserQueryBuilder qb = from(persons).select(prepareSelectPersonsFields(persons)).where(fullText("1"));
        qb.limit(5);
        StorageResults results = storage.fetch(qb.getSelect());
        try {
            int recordCount = 0;
            assertEquals(3, results.getCount());
            for (DataRecord result : results) {
                if (result != null) {
                    if ("person 1".equals(result.get("name")) || "employee 1".equals(result.get("name")) || "manager 1".equals(result.get("name"))) {
                        recordCount++;
                    }
                }
            }
            assertEquals(3, recordCount);
        } finally {
            results.close();
        }
        
        qb = from(employee).select(prepareSelectEmployeeFields(employee)).where(fullText("1"));
        results = storage.fetch(qb.getSelect());
        try {
            int recordCount = 0;
            assertEquals(2, results.getCount());
            for (DataRecord result : results) {
                if (result != null) {
                    if ("employee 1".equals(result.get("name")) || "manager 1".equals(result.get("name"))) {
                        recordCount++;
                    }
                }
            }
            assertEquals(2, recordCount);
        } finally {
            results.close();
        }
        
        qb = from(manager).select(prepareSelectManagerFields(manager)).where(fullText("1"));
        results = storage.fetch(qb.getSelect());
        try {
            int recordCount = 0;
            assertEquals(1, results.getCount());
            for (DataRecord result : results) {
                if (result != null) {
                    if ("manager 1".equals(result.get("name"))) {
                        recordCount++;
                    }
                }
            }
            assertEquals(1, recordCount);
        } finally {
            results.close();
        }
    }

    public void testComplexTypeContains() throws Exception {
        Condition condition = or(
                contains(nn.getField("Id"), "pp"),
                or(contains(nn.getField("name"), "pp"),
                        or(contains(nn.getField("sub/title"), "pp"), contains(nn.getField("sub/name"), "pp"))));
        UserQueryBuilder qb = from(nn).where(condition);
        qb.limit(5);
        StorageResults results = storage.fetch(qb.getSelect());
        try {
            assertEquals(1, results.getCount());
            for (DataRecord result : results) {
                if (result != null) {
                    if ("tyu".equals(result.get("name"))) {
                    }
                }
            }
        } finally {
            results.close();
        }
    }

    public void testFKContainsAndContainsSentenceSearch() {
        UserQueryBuilder qb = from(product).where(contains(product.getField("Family"), "345"));
        StorageResults results = storage.fetch(qb.getSelect());
        try {
            assertTrue(results.getSize() == 2);
            for (DataRecord result : results) {
                assertTrue("4".equals(result.get("Id")) || "5".equals(result.get("Id")));
            }
        } finally {
            results.close();
        }

        qb = from(product).where(contains(product.getField("Family"), "'345 123'"));
        results = storage.fetch(qb.getSelect());
        try {
            assertTrue(results.getSize() == 1);
            for (DataRecord result : results) {
                assertEquals("4", result.get("Id"));
            }
        } finally {
            results.close();
        }
    }

    public void testInheritSearch() {
        List<TypedExpression> fields = UserQueryHelper.getFields(contract, "detail/@xsi:type");
        UserQueryBuilder qb = from(contract).select(prepareContractFields(contract)).where(fullText("1"));
        for (TypedExpression field : fields) {
            qb.select(field);
        }
        StorageResults results = storage.fetch(qb.getSelect());
        try {
            assertTrue(results.getSize() == 1);
            for (DataRecord result : results) {
                assertEquals("1", result.get("id"));
            }
        } finally {
            results.close();
        }
    }

    public void testFuzzySearchCountry() throws Exception {
        String luceneFuzzySearch = "lucene.fuzzy.search";
        MDMConfiguration.getConfiguration().setProperty(luceneFuzzySearch, "false");
        UserQueryBuilder qb = from(country);
        StorageResults results = storage.fetch(qb.getSelect());
        try {
            assertEquals(12, results.getCount());
        } finally {
            results.close();
        }

        qb = from(country).select(prepareSelectCountryFields(country)).where(fullText("France~"));
        results = storage.fetch(qb.getSelect());
        try {
            assertEquals(11, results.getCount());
        } finally {
            results.close();
        }

        qb = from(country).select(prepareSelectCountryFields(country)).where(fullText("F~c#e~"));
        results = storage.fetch(qb.getSelect());
        try {
            assertEquals(12, results.getCount());
        } finally {
            results.close();
        }

        MDMConfiguration.getConfiguration().setProperty(luceneFuzzySearch, "true");
        qb = from(country).select(prepareSelectCountryFields(country)).where(fullText("France~"));
        results = storage.fetch(qb.getSelect());
        try {
            assertEquals(12, results.getCount());
        } finally {
            results.close();
        }

        qb = from(country).select(prepareSelectCountryFields(country)).where(fullText("France~0.5"));
        results = storage.fetch(qb.getSelect());
        try {
            assertEquals(12, results.getCount());
        } finally {
            results.close();
        }

        qb = from(country).select(prepareSelectCountryFields(country)).where(fullText("France~1"));
        results = storage.fetch(qb.getSelect());
        try {
            assertEquals(10, results.getCount());
        } finally {
            results.close();
        }

        qb = from(country).select(prepareSelectCountryFields(country)).where(fullText("France~0"));
        results = storage.fetch(qb.getSelect());
        try {
            assertEquals(1, results.getCount());
        } finally {
            results.close();
        }

        qb = from(country).select(prepareSelectCountryFields(country)).where(fullText("France~1.2"));
        results = storage.fetch(qb.getSelect());
        try {
            assertEquals(12, results.getCount());
        } finally {
            results.close();
        }

        qb = from(country).select(prepareSelectCountryFields(country)).where(fullText("France~~~~~"));
        results = storage.fetch(qb.getSelect());
        try {
            assertEquals(11, results.getCount());
        } finally {
            results.close();
        }

        qb = from(country).select(prepareSelectCountryFields(country)).where(fullText("France!^~"));
        results = storage.fetch(qb.getSelect());
        try {
            assertEquals(11, results.getCount());
        } finally {
            results.close();
        }

        qb = from(country).select(prepareSelectCountryFields(country)).where(fullText("Fr~e"));
        results = storage.fetch(qb.getSelect());
        try {
            assertEquals(11, results.getCount());
        } finally {
            results.close();
        }

        qb = from(country).select(prepareSelectCountryFields(country)).where(fullText("F~ce~"));
        results = storage.fetch(qb.getSelect());
        try {
            assertEquals(12, results.getSize());
        } finally {
            results.close();
        }

        qb = from(country).select(prepareSelectCountryFields(country)).where(fullText("F~c#e~"));
        results = storage.fetch(qb.getSelect());
        try {
            assertEquals(12, results.getSize());
        } finally {
            results.close();
        }

        MDMConfiguration.getConfiguration().setProperty(luceneFuzzySearch, "false");
    }

    private static class TestRDBMSDataSource extends RDBMSDataSource {

        private ContainsOptimization optimization;

        private boolean supportFullText;

        private boolean isCaseSensitiveSearch;

        public TestRDBMSDataSource(RDBMSDataSource rdbmsDataSource) {
            super(rdbmsDataSource);
        }

        @Override
        public boolean supportFullText() {
            return supportFullText;
        }

        private void setSupportFullText(boolean supportFullText) {
            this.supportFullText = supportFullText;
        }

        @Override
        public boolean isCaseSensitiveSearch() {
            return isCaseSensitiveSearch;
        }

        public void setCaseSensitiveSearch(boolean caseSensitiveSearch) {
            isCaseSensitiveSearch = caseSensitiveSearch;
        }

        @Override
        public ContainsOptimization getContainsOptimization() {
            return optimization;
        }

        public void setOptimization(ContainsOptimization optimization) {
            this.optimization = optimization;
        }
    }
}
