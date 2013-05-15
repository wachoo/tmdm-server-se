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

package com.amalto.core.metadata.validation;

import junit.framework.TestCase;
import org.talend.mdm.commmon.metadata.DefaultValidationHandler;
import org.talend.mdm.commmon.metadata.MetadataRepository;

import java.io.InputStream;

public class MetadataValidationTest extends TestCase {

    public void testFK1() throws Exception {
        MetadataRepository repository = new MetadataRepository();
        InputStream resourceAsStream = this.getClass().getResourceAsStream("FK_0.1.xsd");
        DefaultValidationHandler handler = new DefaultValidationHandler();
        try {
            repository.load(resourceAsStream, handler);
            fail("Should fail validation.");
        } catch (Exception e) {
            // Expected
        }
        assertEquals(2, handler.getMessages().size());
    }

    public void testFK2() throws Exception {
        MetadataRepository repository = new MetadataRepository();
        InputStream resourceAsStream = this.getClass().getResourceAsStream("FK2_0.1.xsd");
        DefaultValidationHandler handler = new DefaultValidationHandler();
        try {
            repository.load(resourceAsStream, handler);
            fail("Should fail validation.");
        } catch (Exception e) {
            // Expected
        }
        assertEquals(1, handler.getMessages().size());
    }

    public void testFK3() throws Exception {
        MetadataRepository repository = new MetadataRepository();
        InputStream resourceAsStream = this.getClass().getResourceAsStream("FK3_0.1.xsd");
        DefaultValidationHandler handler = new DefaultValidationHandler();
        repository.load(resourceAsStream, handler);
        assertEquals(0, handler.getMessages().size());
    }

    public void testPKINFO_manyType() throws Exception {
        MetadataRepository repository = new MetadataRepository();
        InputStream resourceAsStream = this.getClass().getResourceAsStream("PKINFO_manyType_0.1.xsd");
        DefaultValidationHandler handler = new DefaultValidationHandler();
        try {
            repository.load(resourceAsStream, handler);
            fail("Should fail validation.");
        } catch (Exception e) {
            // Expected
        }
        assertEquals(2, handler.getMessages().size());
    }

    public void testPK1() throws Exception {
        MetadataRepository repository = new MetadataRepository();
        InputStream resourceAsStream = this.getClass().getResourceAsStream("PK1_0.1.xsd");
        DefaultValidationHandler handler = new DefaultValidationHandler();
        try {
            repository.load(resourceAsStream, handler);
            fail("Should fail validation.");
        } catch (Exception e) {
            // Expected
        }
        assertEquals(1, handler.getMessages().size());
    }

    public void testPK2() throws Exception {
        MetadataRepository repository = new MetadataRepository();
        InputStream resourceAsStream = this.getClass().getResourceAsStream("PK2_0.1.xsd");
        DefaultValidationHandler handler = new DefaultValidationHandler();
        try {
            repository.load(resourceAsStream, handler);
            fail("Should fail validation.");
        } catch (Exception e) {
            // Expected
        }
        assertEquals(1, handler.getMessages().size());
    }

    public void testPK3() throws Exception {
        MetadataRepository repository = new MetadataRepository();
        InputStream resourceAsStream = this.getClass().getResourceAsStream("PK3_0.1.xsd");
        DefaultValidationHandler handler = new DefaultValidationHandler();
        try {
            repository.load(resourceAsStream, handler);
            fail("Should fail validation.");
        } catch (Exception e) {
            // Expected
        }
        assertEquals(1, handler.getMessages().size());
    }

    public void testPK3_ManyPKInfo() throws Exception {
        MetadataRepository repository = new MetadataRepository();
        InputStream resourceAsStream = this.getClass().getResourceAsStream("PK3_ManyPKINFO_0.1.xsd");
        DefaultValidationHandler handler = new DefaultValidationHandler();
        try {
            repository.load(resourceAsStream, handler);
            fail("Should fail validation.");
        } catch (Exception e) {
            // Expected
        }
        assertEquals(4, handler.getMessages().size());
    }

    public void testPK4() throws Exception {
        MetadataRepository repository = new MetadataRepository();
        InputStream resourceAsStream = this.getClass().getResourceAsStream("PK4_0.1.xsd");
        DefaultValidationHandler handler = new DefaultValidationHandler();
        try {
            repository.load(resourceAsStream, handler);
            fail("Should fail validation.");
        } catch (Exception e) {
            // Expected
        }
        assertEquals(1, handler.getMessages().size());
    }

    public void testPK5() throws Exception {
        MetadataRepository repository = new MetadataRepository();
        InputStream resourceAsStream = this.getClass().getResourceAsStream("PK5_0.1.xsd");
        DefaultValidationHandler handler = new DefaultValidationHandler();
        try {
            repository.load(resourceAsStream, handler);
            fail("Should fail validation.");
        } catch (Exception e) {
            // Expected
        }
        assertEquals(1, handler.getMessages().size());
    }

    public void testPK6() throws Exception {
        MetadataRepository repository = new MetadataRepository();
        InputStream resourceAsStream = this.getClass().getResourceAsStream("PK6_0.1.xsd");
        DefaultValidationHandler handler = new DefaultValidationHandler();
        try {
            repository.load(resourceAsStream, handler);
            fail("Should fail validation.");
        } catch (Exception e) {
            // Expected
        }
        assertEquals(2, handler.getMessages().size());
    }

    public void testLookUpField1() throws Exception {
        MetadataRepository repository = new MetadataRepository();
        InputStream resourceAsStream = this.getClass().getResourceAsStream("LookUpField1_0.1.xsd");
        DefaultValidationHandler handler = new DefaultValidationHandler();
        try {
            repository.load(resourceAsStream, handler);
            fail("Should fail validation.");
        } catch (Exception e) {
            // Expected
        }
        assertEquals(1, handler.getMessages().size());

    }
}
