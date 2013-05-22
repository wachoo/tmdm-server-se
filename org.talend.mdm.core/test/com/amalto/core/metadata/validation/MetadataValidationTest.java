/*
 * Copyright (C) 2006-2013 Talend Inc. - www.talend.com
 * 
 * This source code is available under agreement available at
 * %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
 * 
 * You should have received a copy of the agreement along with this program; if not, write to Talend SA 9 rue Pages
 * 92150 Suresnes, France
 */

package com.amalto.core.metadata.validation;

import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

import junit.framework.TestCase;

import org.talend.mdm.commmon.metadata.*;
import org.w3c.dom.Element;

public class MetadataValidationTest extends TestCase {

    public void testFK1() throws Exception {
        MetadataRepository repository = new MetadataRepository();
        InputStream resourceAsStream = this.getClass().getResourceAsStream("FK_0.1.xsd");
        TestValidationHandler handler = new TestValidationHandler();
        try {
            repository.load(resourceAsStream, handler);
            fail("Should fail validation.");
        } catch (Exception e) {
            // Expected
        }
        assertEquals(3, handler.getErrorCount());
        assertTrue(handler.getErrors().contains(ValidationError.TYPE_DOES_NOT_EXIST));
    }

    public void testFK2() throws Exception {
        MetadataRepository repository = new MetadataRepository();
        InputStream resourceAsStream = this.getClass().getResourceAsStream("FK2_0.1.xsd");
        TestValidationHandler handler = new TestValidationHandler();
        try {
            repository.load(resourceAsStream, handler);
            fail("Should fail validation.");
        } catch (Exception e) {
            // Expected
        }
        assertEquals(1, handler.getErrorCount());
        assertTrue(handler.getErrors().contains(ValidationError.TYPE_DOES_NOT_EXIST));
    }

    public void testFK3() throws Exception {
        MetadataRepository repository = new MetadataRepository();
        InputStream resourceAsStream = this.getClass().getResourceAsStream("FK3_0.1.xsd");
        TestValidationHandler handler = new TestValidationHandler();
        repository.load(resourceAsStream, handler);
        assertEquals(0, handler.getErrorCount());
        assertEquals(0, handler.getWarningField());
        assertEquals(0, handler.getWarningType());
    }

    public void testFKINFO() throws Exception {
        MetadataRepository repository = new MetadataRepository();
        InputStream resourceAsStream = this.getClass().getResourceAsStream("FKINFO.xsd");
        TestValidationHandler handler = new TestValidationHandler();
        repository.load(resourceAsStream, handler);
        assertEquals(1, handler.getWarningField());
        assertTrue(handler.getErrors().contains(ValidationError.FOREIGN_KEY_INFO_NOT_PRIMITIVE_XSD_TYPED));
    }

    public void testPKINFO_manyType() throws Exception {
        MetadataRepository repository = new MetadataRepository();
        InputStream resourceAsStream = this.getClass().getResourceAsStream("PKINFO_manyType_0.1.xsd");
        TestValidationHandler handler = new TestValidationHandler();
        try {
            repository.load(resourceAsStream, handler);
            fail("Should fail validation.");
        } catch (Exception e) {
            // Expected
        }
        assertEquals(2, handler.getErrorCount());
        assertTrue(handler.getErrors().contains(ValidationError.TYPE_DOES_NOT_OWN_FIELD));
    }

    public void testPK1() throws Exception {
        MetadataRepository repository = new MetadataRepository();
        InputStream resourceAsStream = this.getClass().getResourceAsStream("PK1_0.1.xsd");
        TestValidationHandler handler = new TestValidationHandler();
        try {
            repository.load(resourceAsStream, handler);
            fail("Should fail validation.");
        } catch (Exception e) {
            // Expected
        }
        assertEquals(1, handler.getErrorCount());
        assertTrue(handler.getErrors().contains(ValidationError.TYPE_DOES_NOT_OWN_FIELD));
    }

    public void testPK2() throws Exception {
        MetadataRepository repository = new MetadataRepository();
        InputStream resourceAsStream = this.getClass().getResourceAsStream("PK2_0.1.xsd");
        TestValidationHandler handler = new TestValidationHandler();
        try {
            repository.load(resourceAsStream, handler);
            fail("Should fail validation.");
        } catch (Exception e) {
            // Expected
        }
        assertEquals(1, handler.getErrorCount());
        assertTrue(handler.getErrors().contains(ValidationError.PRIMARY_KEY_INFO_CANNOT_BE_REPEATABLE));
    }

    public void testPK3() throws Exception {
        MetadataRepository repository = new MetadataRepository();
        InputStream resourceAsStream = this.getClass().getResourceAsStream("PK3_0.1.xsd");
        TestValidationHandler handler = new TestValidationHandler();
        try {
            repository.load(resourceAsStream, handler);
            fail("Should fail validation.");
        } catch (Exception e) {
            // Expected
        }
        assertEquals(1, handler.getErrorCount());
        assertTrue(handler.getErrors().contains(ValidationError.TYPE_DOES_NOT_OWN_FIELD));
    }

    public void testPK3_ManyPKInfo() throws Exception {
        MetadataRepository repository = new MetadataRepository();
        InputStream resourceAsStream = this.getClass().getResourceAsStream("PK3_ManyPKINFO_0.1.xsd");
        TestValidationHandler handler = new TestValidationHandler();
        try {
            repository.load(resourceAsStream, handler);
            fail("Should fail validation.");
        } catch (Exception e) {
            // Expected
        }
        assertEquals(4, handler.getErrorCount());
        assertTrue(handler.getErrors().contains(ValidationError.TYPE_DOES_NOT_OWN_FIELD));
        assertTrue(handler.getErrors().contains(ValidationError.PRIMARY_KEY_INFO_NOT_IN_ENTITY));
    }

    public void testPK4() throws Exception {
        MetadataRepository repository = new MetadataRepository();
        InputStream resourceAsStream = this.getClass().getResourceAsStream("PK4_0.1.xsd");
        TestValidationHandler handler = new TestValidationHandler();
        try {
            repository.load(resourceAsStream, handler);
            fail("Should fail validation.");
        } catch (Exception e) {
            // Expected
        }
        assertEquals(1, handler.getErrorCount());
        assertTrue(handler.getErrors().contains(ValidationError.PRIMARY_KEY_INFO_NOT_IN_ENTITY));
    }

    public void testPK5() throws Exception {
        MetadataRepository repository = new MetadataRepository();
        InputStream resourceAsStream = this.getClass().getResourceAsStream("PK5_0.1.xsd");
        TestValidationHandler handler = new TestValidationHandler();
        try {
            repository.load(resourceAsStream, handler);
            fail("Should fail validation.");
        } catch (Exception e) {
            // Expected
        }
        assertEquals(1, handler.getErrorCount());
        assertTrue(handler.getErrors().contains(ValidationError.FIELD_KEY_CANNOT_BE_FOREIGN_KEY));
    }

    public void testPK6() throws Exception {
        MetadataRepository repository = new MetadataRepository();
        InputStream resourceAsStream = this.getClass().getResourceAsStream("PK6_0.1.xsd");
        TestValidationHandler handler = new TestValidationHandler();
        try {
            repository.load(resourceAsStream, handler);
            fail("Should fail validation.");
        } catch (Exception e) {
            // Expected
        }
        assertEquals(2, handler.getErrorCount());
        assertTrue(handler.getErrors().contains(ValidationError.FIELD_KEY_MUST_BE_MANDATORY));
        assertTrue(handler.getErrors().contains(ValidationError.FIELD_KEY_CANNOT_BE_REPEATABLE));
    }

    public void testLookUpField1() throws Exception {
        MetadataRepository repository = new MetadataRepository();
        InputStream resourceAsStream = this.getClass().getResourceAsStream("LookUpField1_0.1.xsd");
        TestValidationHandler handler = new TestValidationHandler();
        try {
            repository.load(resourceAsStream, handler);
            fail("Should fail validation.");
        } catch (Exception e) {
            // Expected
        }
        assertEquals(1, handler.getErrorCount());
        assertTrue(handler.getErrors().contains(ValidationError.LOOKUP_FIELD_CANNOT_BE_KEY));
    }

    private static class TestValidationHandler implements ValidationHandler {

        private int fatalField;

        private int errorField;

        private int warningField;

        private int fatalType;

        private int errorType;

        private int warningType;

        private Set<ValidationError> errors = new HashSet<ValidationError>();

        @Override
        public void fatal(FieldMetadata field, String message, Element element, int lineNumber, int columnNumber, ValidationError error) {
            errors.add(error);
            fatalField++;
        }

        @Override
        public void error(FieldMetadata field, String message, Element element, int lineNumber, int columnNumber, ValidationError error) {
            errors.add(error);
            errorField++;
        }

        @Override
        public void warning(FieldMetadata field, String message, Element element, int lineNumber, int columnNumber, ValidationError error) {
            errors.add(error);
            warningField++;
        }

        @Override
        public void fatal(TypeMetadata type, String message, Element element, int lineNumber, int columnNumber, ValidationError error) {
            errors.add(error);
            fatalType++;
        }

        @Override
        public void error(TypeMetadata type, String message, Element element, int lineNumber, int columnNumber, ValidationError error) {
            errors.add(error);
            errorType++;
        }

        @Override
        public void warning(TypeMetadata type, String message, Element element, int lineNumber, int columnNumber, ValidationError error) {
            errors.add(error);
            warningType++;
        }

        @Override
        public void end() {
            if (getErrorCount() > 0) {
                throw new RuntimeException("Validation failed.");
            }
        }

        @Override
        public int getErrorCount() {
            return errorField + errorType;
        }

        public int getWarningField() {
            return warningField;
        }

        public int getWarningType() {
            return warningType;
        }

        private Set<ValidationError> getErrors() {
            return errors;
        }
    }
}
