/*
 * Copyright (C) 2006-2013 Talend Inc. - www.talend.com
 *
 * This source code is available under agreement available at
 * %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
 *
 * You should have received a copy of the agreement
 * along with this program; if not, write to Talend SA
 * 9 rue Pages 92150 Suresnes, France
 */

package com.amalto.core.metadata;

import junit.framework.TestCase;
import org.talend.mdm.commmon.metadata.*;

import java.io.InputStream;

/**
 * Schema parsing <br>
 * <li>add Chinese and Japanese language label testCase
 */
@SuppressWarnings("nls")
public class MetadataRepositoryTest extends TestCase {

    ConsoleDumpMetadataVisitor visitor = new ConsoleDumpMetadataVisitor();

    public void test1() {
        MetadataRepository repository = new MetadataRepository();

        InputStream stream = getClass().getResourceAsStream("schema.xsd");
        repository.load(stream);
        // repository.accept(visitor);

        ComplexTypeMetadata product = repository.getComplexType("Product");
        assertNotNull(product);
        FieldMetadata field = product.getField("Features/Sizes/Size");
        assertNotNull(field);
        assertEquals("Features/Sizes/Size", field.getPath());
        assertEquals("Product", field.getEntityTypeName());
    }

    public void test2() {
        MetadataRepository repository = new MetadataRepository();

        InputStream stream = getClass().getResourceAsStream("schema2.xsd");
        repository.load(stream);
        // repository.accept(visitor);
    }

    public void test3() {
        MetadataRepository repository = new MetadataRepository();

        InputStream stream = getClass().getResourceAsStream("schema3.xsd");
        repository.load(stream);
        // repository.accept(visitor);
    }

    public void test4() {
        MetadataRepository repository = new MetadataRepository();

        InputStream stream = getClass().getResourceAsStream("schema4.xsd");
        repository.load(stream);
        // repository.accept(visitor);
    }

    public void test5() {
        MetadataRepository repository = new MetadataRepository();

        InputStream stream = getClass().getResourceAsStream("schema5.xsd");
        repository.load(stream);
        // repository.accept(visitor);
    }

    public void test6() {
        MetadataRepository repository = new MetadataRepository();

        InputStream stream = getClass().getResourceAsStream("schema6.xsd");
        repository.load(stream);
        // repository.accept(visitor);
    }


    public void test7() {
        MetadataRepository repository = new MetadataRepository();

        InputStream stream = getClass().getResourceAsStream("schema7.xsd");
        try {
            repository.load(stream); // Model has many errors to be fixed.
        } catch (Exception e) {
            // Expected
        }
        // repository.accept(visitor);
    }

    public void test8() {
        MetadataRepository repository = new MetadataRepository();

        InputStream stream = getClass().getResourceAsStream("schema8.xsd");
        repository.load(stream);
        // repository.accept(visitor);
    }

    public void test9() {
        MetadataRepository repository = new MetadataRepository();

        InputStream stream = getClass().getResourceAsStream("schema9.xsd");
        repository.load(stream);
        // repository.accept(visitor);
    }

    public void test10() {
        MetadataRepository repository = new MetadataRepository();

        InputStream stream = getClass().getResourceAsStream("schema10.xsd");
        repository.load(stream);
        // repository.accept(visitor);
    }

    public void test11() {
        MetadataRepository repository = new MetadataRepository();

        InputStream stream = getClass().getResourceAsStream("schema11.xsd");
        repository.load(stream);
        // repository.accept(visitor);
    }

    public void test12() {
        MetadataRepository repository = new MetadataRepository();
        InputStream stream = getClass().getResourceAsStream("schema12.xsd");
        repository.load(stream);
        assertTrue(repository.getTypes().size() > 0);
    }

    public void test13() {
        MetadataRepository repository = new MetadataRepository();
        InputStream stream = getClass().getResourceAsStream("schema13.xsd");
        repository.load(stream);
        assertTrue(repository.getTypes().size() > 0);
        // repository.accept(visitor);
    }

    public void test14() {
        MetadataRepository repository = new MetadataRepository();
        InputStream stream = getClass().getResourceAsStream("schema14.xsd");
        try {
            repository.load(stream);
            fail("Expected exception due to invalid key definition in inheritance tree.");
        } catch (Exception e) {
            // Expected.
        }
    }

    public void test15() {
        MetadataRepository repository = new MetadataRepository();
        InputStream stream = getClass().getResourceAsStream("schema15.xsd");
        repository.load(stream);

        ComplexTypeMetadata company = repository.getComplexType("Company");
        assertNotNull(company);
        assertEquals(1, company.getKeyFields().size());
    }

    public void test16() throws Exception {
        MetadataRepository repository = new MetadataRepository();
        InputStream stream = getClass().getResourceAsStream("schema16.xsd");
        repository.load(stream);

        ComplexTypeMetadata person = repository.getComplexType("Person");
        assertNotNull(person);
        assertEquals(2, person.getKeyFields().size());
        String[] expectedOrder = {"lastname", "firstname"};
        int i = 0;
        for (FieldMetadata keyField : person.getKeyFields()) {
            assertEquals(expectedOrder[i++], keyField.getName());
        }
    }

    public void test17() throws Exception {
        MetadataRepository repository = new MetadataRepository();
        InputStream stream = getClass().getResourceAsStream("schema17.xsd");
        repository.load(stream);

        ComplexTypeMetadata entityType = repository.getComplexType("A");
        assertNotNull(entityType);
        TypeMetadata simpleType = repository.getNonInstantiableType("", "A");
        assertNotNull(simpleType);
        assertNotSame(entityType, simpleType);
    }

    public void test18() throws Exception {
        MetadataRepository repository = new MetadataRepository();
        InputStream stream = getClass().getResourceAsStream("schema18.xsd");
        repository.load(stream);

        ComplexTypeMetadata entityType = repository.getComplexType("A");
        assertNotNull(entityType);
        assertTrue(entityType.hasField("string20"));
        assertTrue(entityType.hasField("string30"));

        TypeMetadata string20Field = entityType.getField("string20").getType();
        assertEquals("20", string20Field.getData(MetadataRepository.DATA_MAX_LENGTH));
        TypeMetadata string30Field = entityType.getField("string30").getType();
        assertEquals("30", string30Field.getData(MetadataRepository.DATA_MAX_LENGTH));
    }

    public void test19() throws Exception {
        MetadataRepository repository = new MetadataRepository();
        InputStream stream = getClass().getResourceAsStream("schema19.xsd");
        repository.load(stream);
        try {
            MetadataUtils.sortTypes(repository);
            fail("Expected exception (cycles in model).");
        } catch (Exception e) {
            // Expected
        }
    }

    public void test20() throws Exception {
        MetadataRepository repository = new MetadataRepository();
        InputStream stream = getClass().getResourceAsStream("schema20.xsd");
        repository.load(stream);
        ComplexTypeMetadata test = repository.getComplexType("Test");
        assertNotNull(test);
        assertTrue(test.hasField("text"));
        assertEquals("888", test.getField("text").getType().getData(MetadataRepository.DATA_MAX_LENGTH));
    }

    public void test21() throws Exception {
        MetadataRepository repository = new MetadataRepository();
        InputStream stream = getClass().getResourceAsStream("schema21.xsd");
        repository.load(stream);
        ComplexTypeMetadata test = repository.getComplexType("shiporder");
        assertNotNull(test);
        assertTrue(test.hasField("shipto/name"));
    }

    public void testCopy() throws Exception {
        MetadataRepository repository = new MetadataRepository();
        InputStream stream = getClass().getResourceAsStream("schema21.xsd");
        repository.load(stream);
        ComplexTypeMetadata test1 = repository.getComplexType("shiporder");

        MetadataRepository copy = repository.copy();
        ComplexTypeMetadata test2 = copy.getComplexType("shiporder");

        assertEquals(test1.getName(), test2.getName());
        assertTrue(test1 != test2);
    }
}
