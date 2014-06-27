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

import junit.framework.TestCase;
import org.talend.mdm.commmon.metadata.*;
import org.w3c.dom.Element;

import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

public class MetadataValidationTest extends TestCase {

    // See TMDMQA-6270 / 6330
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
        assertTrue(handler.getMessages().contains(ValidationError.TYPE_DOES_NOT_EXIST));
        assertTrue(handler.getLineNumbers().contains(31));
        assertTrue(handler.getLineNumbers().contains(47));
        assertFalse(handler.getLineNumbers().contains(null));
    }

    // See TMDMQA-6363
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
        assertTrue(handler.getMessages().contains(ValidationError.TYPE_DOES_NOT_EXIST));
        assertTrue(handler.getLineNumbers().contains(30));
        assertFalse(handler.getLineNumbers().contains(null));
    }

    public void testFK3() throws Exception {
        MetadataRepository repository = new MetadataRepository();
        InputStream resourceAsStream = this.getClass().getResourceAsStream("FK3_0.1.xsd");
        TestValidationHandler handler = new TestValidationHandler();
        repository.load(resourceAsStream, handler);
        assertEquals(0, handler.getErrorCount());
        assertEquals(0, handler.getWarningCount());
        assertTrue(handler.getLineNumbers().isEmpty());
    }

    public void testFK4() throws Exception {
        MetadataRepository repository = new MetadataRepository();
        InputStream resourceAsStream = this.getClass().getResourceAsStream("FK4_0.1.xsd");
        TestValidationHandler handler = new TestValidationHandler();
        repository.load(resourceAsStream, handler);
        assertEquals(0, handler.getErrorCount());
        assertEquals(1, handler.getWarningCount());
        assertTrue(handler.getMessages().contains(ValidationError.FOREIGN_KEY_USES_MAX_LENGTH));
        assertTrue(handler.getLineNumbers().contains(18));
        assertFalse(handler.getLineNumbers().contains(null));
    }

    // See TMDMQA-6364
    public void testFK5() throws Exception {
        MetadataRepository repository = new MetadataRepository();
        InputStream resourceAsStream = this.getClass().getResourceAsStream("FK5_0.1.xsd");
        TestValidationHandler handler = new TestValidationHandler();
        try {
            repository.load(resourceAsStream, handler);
            fail("Should fail validation.");
        } catch (Exception e) {
            // Expected
        }
        assertEquals(1, handler.getErrorCount());
        assertTrue(handler.getMessages().contains(ValidationError.TYPE_DOES_NOT_OWN_FIELD));
        assertTrue(handler.getLineNumbers().contains(39));
        assertFalse(handler.getLineNumbers().contains(null));
    }

    public void testFK6() throws Exception {
        MetadataRepository repository = new MetadataRepository();
        InputStream resourceAsStream = this.getClass().getResourceAsStream("FK6_0.1.xsd");
        TestValidationHandler handler = new TestValidationHandler();
        try {
            repository.load(resourceAsStream, handler);
            fail("Should fail validation.");
        } catch (Exception e) {
            // Expected
        }
        assertEquals(1, handler.getErrorCount());
        assertTrue(handler.getMessages().contains(ValidationError.TYPE_DOES_NOT_EXIST));
        assertTrue(handler.getLineNumbers().contains(39));
        assertFalse(handler.getLineNumbers().contains(null));
    }

    public void testFK7() throws Exception {
        MetadataRepository repository = new MetadataRepository();
        InputStream resourceAsStream = this.getClass().getResourceAsStream("FK7_0.1.xsd");
        TestValidationHandler handler = new TestValidationHandler();
        try {
            repository.load(resourceAsStream, handler);
            fail("Should fail validation.");
        } catch (Exception e) {
            // Expected
        }
        assertEquals(1, handler.getErrorCount());
        assertTrue(handler.getMessages().contains(ValidationError.TYPE_DOES_NOT_EXIST));
        assertTrue(handler.getLineNumbers().contains(193));
        assertFalse(handler.getLineNumbers().contains(null));
    }

    public void testFKPointToNonPK() throws Exception {
        MetadataRepository repository = new MetadataRepository();
        InputStream resourceAsStream = this.getClass().getResourceAsStream("FKCheck.xsd");
        TestValidationHandler handler = new TestValidationHandler();
        repository.load(resourceAsStream, handler);
        assertEquals(1, handler.getWarningCount());
        assertTrue(handler.getMessages().contains(ValidationError.FOREIGN_KEY_SHOULD_POINT_TO_PRIMARY_KEY));
        assertTrue(handler.getLineNumbers().contains(30));
        assertFalse(handler.getLineNumbers().contains(null));
    }

    // See TMDMQA-6366
    public void testFKINFO() throws Exception {
        MetadataRepository repository = new MetadataRepository();
        InputStream resourceAsStream = this.getClass().getResourceAsStream("FKINFO.xsd");
        TestValidationHandler handler = new TestValidationHandler();
        try {
            repository.load(resourceAsStream, handler);
            fail("Should fail validation.");
        } catch (Exception e) {
            // Expected
        }
        assertEquals(1, handler.getWarningCount());
        assertTrue(handler.getMessages().contains(ValidationError.FOREIGN_KEY_INFO_NOT_PRIMITIVE_XSD_TYPED));
        assertEquals(2, handler.getErrorCount());
        assertTrue(handler.getMessages().contains(ValidationError.TYPE_DOES_NOT_OWN_FIELD));
        assertTrue(handler.getMessages().contains(ValidationError.TYPE_DOES_NOT_EXIST));
        assertTrue(handler.getLineNumbers().contains(23));
        assertTrue(handler.getLineNumbers().contains(24));
        assertTrue(handler.getLineNumbers().contains(26));
        assertFalse(handler.getLineNumbers().contains(null));
    }

    public void testFKInfo2() throws Exception {
        MetadataRepository repository = new MetadataRepository();
        InputStream resourceAsStream = this.getClass().getResourceAsStream("FKINFO2.xsd");
        TestValidationHandler handler = new TestValidationHandler();
        repository.load(resourceAsStream, handler);
        assertEquals(1, handler.getWarningCount());
        assertTrue(handler.getMessages().contains(ValidationError.FOREIGN_KEY_INFO_NOT_PRIMITIVE_XSD_TYPED));
        assertTrue(handler.getLineNumbers().contains(10));
        assertFalse(handler.getLineNumbers().contains(null));
    }

    // See TMDMQA-6365
    public void testFKInfo3() throws Exception {
        MetadataRepository repository = new MetadataRepository();
        InputStream resourceAsStream = this.getClass().getResourceAsStream("FKINFO3.xsd");
        TestValidationHandler handler = new TestValidationHandler();
        try {
            repository.load(resourceAsStream, handler);
            fail("Should fail validation.");
        } catch (Exception e) {
            // Expected
        }
        assertEquals(1, handler.getErrorCount());
        assertTrue(handler.getMessages().contains(ValidationError.FOREIGN_KEY_INFO_NOT_REFERENCING_FK_TYPE));
        assertTrue(handler.getLineNumbers().contains(150));
        assertFalse(handler.getLineNumbers().contains(null));
    }

    // See TMDMQA-6332
    public void testFKInfo4() throws Exception {
        MetadataRepository repository = new MetadataRepository();
        InputStream resourceAsStream = this.getClass().getResourceAsStream("FKINFO4.xsd");
        TestValidationHandler handler = new TestValidationHandler();
        repository.load(resourceAsStream, handler);
        assertEquals(1, handler.getWarningCount());
        assertTrue(handler.getMessages().contains(ValidationError.FOREIGN_KEY_INFO_REPEATABLE));
        assertTrue(handler.getLineNumbers().contains(21));
        assertFalse(handler.getLineNumbers().contains(null));
    }

    public void testFKInfo5() throws Exception {
        MetadataRepository repository = new MetadataRepository();
        InputStream resourceAsStream = this.getClass().getResourceAsStream("FKINFO5.xsd");
        TestValidationHandler handler = new TestValidationHandler();
        try {
            repository.load(resourceAsStream, handler);
            fail("Should fail validation.");
        } catch (Exception e) {
            // Expected
        }
        assertEquals(1, handler.getErrorCount());
        assertEquals(0, handler.getWarningCount());
        assertTrue(handler.getMessages().contains(ValidationError.TYPE_DOES_NOT_EXIST));
        assertTrue(handler.getLineNumbers().contains(161));
        assertFalse(handler.getLineNumbers().contains(null));
    }

    public void testFKInfo6() throws Exception {
        MetadataRepository repository = new MetadataRepository();
        InputStream resourceAsStream = this.getClass().getResourceAsStream("FKINFO6.xsd");
        TestValidationHandler handler = new TestValidationHandler();
        try {
            repository.load(resourceAsStream, handler);
            fail("Should fail validation.");
        } catch (Exception e) {
            // Expected
        }
        assertEquals(1, handler.getErrorCount());
        assertEquals(0, handler.getWarningCount());
        assertTrue(handler.getMessages().contains(ValidationError.TYPE_DOES_NOT_OWN_FIELD));
        assertTrue(handler.getLineNumbers().contains(228));
        assertFalse(handler.getLineNumbers().contains(null));
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
        assertTrue(handler.getMessages().contains(ValidationError.TYPE_DOES_NOT_OWN_FIELD));
        assertTrue(handler.getLineNumbers().contains(21));
        assertTrue(handler.getLineNumbers().contains(36));
        assertFalse(handler.getLineNumbers().contains(null));
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
        assertTrue(handler.getMessages().contains(ValidationError.TYPE_DOES_NOT_OWN_FIELD));
        assertTrue(handler.getLineNumbers().contains(15));
        assertFalse(handler.getLineNumbers().contains(null));
    }

    // See TMDMQA-6327 / 6328
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
        assertTrue(handler.getMessages().contains(ValidationError.PRIMARY_KEY_INFO_CANNOT_BE_REPEATABLE));
        assertTrue(handler.getLineNumbers().contains(16));
        assertFalse(handler.getLineNumbers().contains(null));
    }

    // See TMDMQA-6329
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
        assertTrue(handler.getMessages().contains(ValidationError.TYPE_DOES_NOT_OWN_FIELD));
        assertTrue(handler.getLineNumbers().contains(15));
        assertFalse(handler.getLineNumbers().contains(null));
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
        assertTrue(handler.getMessages().contains(ValidationError.TYPE_DOES_NOT_OWN_FIELD));
        assertTrue(handler.getMessages().contains(ValidationError.PRIMARY_KEY_INFO_NOT_IN_ENTITY));
        assertTrue(handler.getLineNumbers().contains(15));
        assertTrue(handler.getLineNumbers().contains(16));
        assertTrue(handler.getLineNumbers().contains(17));
        assertTrue(handler.getLineNumbers().contains(48));
        assertFalse(handler.getLineNumbers().contains(null));
    }

    // See TMDMQA-6326
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
        assertTrue(handler.getMessages().contains(ValidationError.PRIMARY_KEY_INFO_NOT_IN_ENTITY));
        assertTrue(handler.getLineNumbers().contains(15));
        assertFalse(handler.getLineNumbers().contains(null));
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
        assertTrue(handler.getMessages().contains(ValidationError.FIELD_KEY_CANNOT_BE_FOREIGN_KEY));
        assertTrue(handler.getLineNumbers().contains(28));
        assertFalse(handler.getLineNumbers().contains(null));
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
        assertEquals(3, handler.getErrorCount());
        assertTrue(handler.getMessages().contains(ValidationError.FIELD_KEY_MUST_BE_MANDATORY));
        assertTrue(handler.getMessages().contains(ValidationError.FIELD_KEY_CANNOT_BE_REPEATABLE));
        assertTrue(handler.getLineNumbers().contains(26));
        assertFalse(handler.getLineNumbers().contains(null));
    }

    public void testPK7() throws Exception {
        MetadataRepository repository = new MetadataRepository();
        InputStream resourceAsStream = this.getClass().getResourceAsStream("PK7_0.1.xsd");
        TestValidationHandler handler = new TestValidationHandler();
        try {
            repository.load(resourceAsStream, handler);
            fail("Should fail validation.");
        } catch (Exception e) {
            // Expected
        }
        assertEquals(2, handler.getErrorCount());
        assertTrue(handler.getMessages().contains(ValidationError.TYPE_DOES_NOT_OWN_FIELD));
        assertTrue(handler.getLineNumbers().contains(193));
        assertTrue(handler.getLineNumbers().contains(248));
        assertTrue(handler.getLineNumbers().contains(398));
        assertTrue(handler.getLineNumbers().contains(411));
        assertFalse(handler.getLineNumbers().contains(null));
    }

    public void testPKInfo() throws Exception {
        MetadataRepository repository = new MetadataRepository();
        InputStream resourceAsStream = this.getClass().getResourceAsStream("PKINFO_0.1.xsd");
        TestValidationHandler handler = new TestValidationHandler();
        try {
            repository.load(resourceAsStream, handler);
            fail("Should fail validation.");
        } catch (Exception e) {
            // Expected
        }
        assertEquals(3, handler.getErrorCount());
        assertTrue(handler.getMessages().contains(ValidationError.TYPE_DOES_NOT_OWN_FIELD));
        assertTrue(handler.getLineNumbers().contains(22));
        assertTrue(handler.getLineNumbers().contains(203));
        assertTrue(handler.getLineNumbers().contains(338));
        assertFalse(handler.getLineNumbers().contains(null));
    }

    // See TMDMQA-6333
    public void testPKInfo2() throws Exception {
        MetadataRepository repository = new MetadataRepository();
        InputStream resourceAsStream = this.getClass().getResourceAsStream("PKINFO2_0.1.xsd");
        TestValidationHandler handler = new TestValidationHandler();
        repository.load(resourceAsStream, handler);
        assertEquals(1, handler.getWarningCount());
        assertTrue(handler.getMessages().contains(ValidationError.PRIMARY_KEY_INFO_TYPE_NOT_PRIMITIVE));
        assertTrue(handler.getLineNumbers().contains(15));
        assertFalse(handler.getLineNumbers().contains(null));
    }
    
    // See TMDM-6554
    public void testSecondLevelPkInfo() throws Exception {
        MetadataRepository repository = new MetadataRepository();
        InputStream resourceAsStream = this.getClass().getResourceAsStream("PKINFO_2LevelPKInfo.xsd"); //$NON-NLS-1$
        TestValidationHandler handler = new TestValidationHandler();
        repository.load(resourceAsStream, handler);
        assertEquals(0, handler.getWarningCount());
        assertEquals(0, handler.getErrorCount());
    }

    // See TMDMQA-6317
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
        assertTrue(handler.getMessages().contains(ValidationError.LOOKUP_FIELD_CANNOT_BE_KEY));
        assertTrue(handler.getLineNumbers().contains(5));
        assertFalse(handler.getLineNumbers().contains(null));
    }

    // See TMDMQA-6313
    public void testLookUpField2() throws Exception {
        MetadataRepository repository = new MetadataRepository();
        InputStream resourceAsStream = this.getClass().getResourceAsStream("LookUpField2_0.1.xsd");
        TestValidationHandler handler = new TestValidationHandler();
        try {
            repository.load(resourceAsStream, handler);
            fail("Should fail validation.");
        } catch (Exception e) {
            // Expected
        }
        assertEquals(2, handler.getErrorCount()); // TODO Should be 1?
        assertTrue(handler.getMessages().contains(ValidationError.LOOKUP_FIELD_NOT_IN_ENTITY));
        assertTrue(handler.getLineNumbers().contains(18));
        assertFalse(handler.getLineNumbers().contains(null));
    }

    public void testLookUpField3() throws Exception {
        MetadataRepository repository = new MetadataRepository();
        InputStream resourceAsStream = this.getClass().getResourceAsStream("LookUpField3_0.1.xsd");
        TestValidationHandler handler = new TestValidationHandler();
        repository.load(resourceAsStream, handler);
        assertEquals(0, handler.getErrorCount());
        assertEquals(0, handler.getWarningCount());
        assertTrue(handler.getLineNumbers().isEmpty());
    }

    // See TMDMQA-6357 / 6367
    public void testInheritance() throws Exception {
        MetadataRepository repository = new MetadataRepository();
        InputStream resourceAsStream = this.getClass().getResourceAsStream("Inheritance.xsd");
        TestValidationHandler handler = new TestValidationHandler();
        try {
            repository.load(resourceAsStream, handler);
            fail("Should fail validation.");
        } catch (Exception e) {
            // Expected
        }
        assertEquals(3, handler.getErrorCount()); // TODO Check other errors in data model in assert
        assertTrue(handler.getMessages().contains(ValidationError.TYPE_CANNOT_OVERRIDE_SUPER_TYPE_KEY));
        assertTrue(handler.getMessages().contains(ValidationError.TYPE_DOES_NOT_EXIST));
        assertTrue(handler.getLineNumbers().contains(18));
        assertTrue(handler.getLineNumbers().contains(160));
        assertTrue(handler.getLineNumbers().contains(393));
        assertFalse(handler.getLineNumbers().contains(null));
    }

    public void testXSDAttributeWarning() throws Exception {
        MetadataRepository repository = new MetadataRepository();
        InputStream resourceAsStream = this.getClass().getResourceAsStream("xsd_attributes.xsd");
        TestValidationHandler handler = new TestValidationHandler();
        repository.load(resourceAsStream, handler);
        assertEquals(6, handler.getWarningCount());
        assertTrue(handler.getMessages().contains(ValidationError.TYPE_USE_XSD_ATTRIBUTES));
        assertTrue(handler.getLineNumbers().contains(63));
        assertTrue(handler.getLineNumbers().contains(109));
        assertFalse(handler.getLineNumbers().contains(null));
    }

    private static class TestValidationHandler implements ValidationHandler {

        private int errorField;

        private int warningField;

        private int errorType;

        private int warningType;

        private Set<ValidationError> errors = new HashSet<ValidationError>();

        private Set<Integer> lineNumbers = new HashSet<Integer>();

        @Override
        public void fatal(FieldMetadata field, String message, Element element, Integer lineNumber, Integer columnNumber, ValidationError error) {
            errors.add(error);
        }

        @Override
        public void error(FieldMetadata field, String message, Element element, Integer lineNumber, Integer columnNumber, ValidationError error) {
            errors.add(error);
            lineNumbers.add(lineNumber);
            errorField++;
        }

        @Override
        public void warning(FieldMetadata field, String message, Element element, Integer lineNumber, Integer columnNumber, ValidationError error) {
            errors.add(error);
            lineNumbers.add(lineNumber);
            warningField++;
        }

        @Override
        public void fatal(TypeMetadata type, String message, Element element, Integer lineNumber, Integer columnNumber, ValidationError error) {
            errors.add(error);
        }

        @Override
        public void error(TypeMetadata type, String message, Element element, Integer lineNumber, Integer columnNumber, ValidationError error) {
            errors.add(error);
            lineNumbers.add(lineNumber);
            errorType++;
        }

        @Override
        public void warning(TypeMetadata type, String message, Element element, Integer lineNumber, Integer columnNumber, ValidationError error) {
            errors.add(error);
            lineNumbers.add(lineNumber);
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

        public int getWarningCount() {
            return warningField + warningType;
        }

        private Set<ValidationError> getMessages() {
            return errors;
        }

        private Set<Integer> getLineNumbers() {
            return lineNumbers;
        }
    }
}
