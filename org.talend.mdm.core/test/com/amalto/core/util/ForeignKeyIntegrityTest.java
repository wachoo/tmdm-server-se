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

package com.amalto.core.util;

import com.amalto.core.metadata.MetadataRepository;
import com.amalto.core.metadata.ReferenceFieldMetadata;
import com.amalto.core.metadata.TypeMetadata;
import junit.framework.TestCase;

import java.util.Set;

/**
 *
 */
@SuppressWarnings({"HardCodedStringLiteral", "nls"})
public class ForeignKeyIntegrityTest extends TestCase {

    private Set<ReferenceFieldMetadata> getReferencedFields(MetadataRepository repository, String typeName) {
        TypeMetadata type = repository.getType(typeName);
        ForeignKeyIntegrity keyIntegrity = new ForeignKeyIntegrity(type);
        return repository.accept(keyIntegrity);
    }

    private MetadataRepository getMetadataRepository(String xsdFileName) {
        MetadataRepository repository;
        try {
            repository = new MetadataRepository();
            repository.load(getClass().getResourceAsStream(xsdFileName));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return repository;
    }

    public void test1() throws Exception {
        MetadataRepository repository = getMetadataRepository("test1.xsd");

        String typeName = "language";
        Set<ReferenceFieldMetadata> references = getReferencedFields(repository, typeName);

        assertFalse(references.isEmpty());
        System.out.println("=====================");
        System.out.println("References to type:" + typeName);
        System.out.println("=====================");
        for (ReferenceFieldMetadata reference : references) {
            System.out.println("Type: " + reference.getContainingType().getName() + " / Field: " + reference.getName());
        }

    }

    public void testModel1() throws Exception {
        MetadataRepository repository = getMetadataRepository("model1.xsd");

        Set<ReferenceFieldMetadata> references = getReferencedFields(repository, "A");
        assertTrue(references.isEmpty());

        references = getReferencedFields(repository, "B");
        assertEquals(1, references.size());
    }

    public void testModel2() throws Exception {
        MetadataRepository repository = getMetadataRepository("model2.xsd");

        Set<ReferenceFieldMetadata> references = getReferencedFields(repository, "A");
        assertEquals(1, references.size());

        references = getReferencedFields(repository, "B");
        assertEquals(1, references.size());

        references = getReferencedFields(repository, "C");
        assertEquals(1, references.size());
    }

    public void testModel3() throws Exception {
        MetadataRepository repository = getMetadataRepository("model3.xsd");

        Set<ReferenceFieldMetadata> references = getReferencedFields(repository, "A");
        assertEquals(0, references.size());

        references = getReferencedFields(repository, "B");
        assertEquals(2, references.size());

        references = getReferencedFields(repository, "C");
        assertEquals(0, references.size());
    }

    public void testModel4() throws Exception {
        MetadataRepository repository = getMetadataRepository("model4.xsd");

        Set<ReferenceFieldMetadata> references = getReferencedFields(repository, "A");
        assertEquals(0, references.size());

        references = getReferencedFields(repository, "B");
        assertEquals(4, references.size());
    }

    public void testModel5() throws Exception {
        MetadataRepository repository = getMetadataRepository("model5.xsd");

        Set<ReferenceFieldMetadata> references = getReferencedFields(repository, "A");
        assertEquals(1, references.size());

        references = getReferencedFields(repository, "B");
        assertEquals(1, references.size());

    }

    public void testModel6() throws Exception {
        MetadataRepository repository = getMetadataRepository("model6.xsd");

        Set<ReferenceFieldMetadata> references = getReferencedFields(repository, "A");
        assertEquals(0, references.size());

        references = getReferencedFields(repository, "B");
        assertEquals(1, references.size());

    }

    public void testModel7() throws Exception {
        MetadataRepository repository = getMetadataRepository("model7.xsd");

        Set<ReferenceFieldMetadata> references = getReferencedFields(repository, "A");
        assertEquals(0, references.size());

        references = getReferencedFields(repository, "B");
        assertEquals(0, references.size());

    }

    public void testModel8() throws Exception {
        MetadataRepository repository = getMetadataRepository("model8.xsd");

        Set<ReferenceFieldMetadata> references = getReferencedFields(repository, "A");
        assertEquals(0, references.size());

        references = getReferencedFields(repository, "B");
        assertEquals(1, references.size());

        references = getReferencedFields(repository, "C");
        assertEquals(1, references.size());
    }

    public void testModel9() throws Exception {
        MetadataRepository repository = getMetadataRepository("model9.xsd");

        Set<ReferenceFieldMetadata> references = getReferencedFields(repository, "A");
        assertEquals(0, references.size());

        references = getReferencedFields(repository, "B");
        assertEquals(0, references.size());

        references = getReferencedFields(repository, "C");
        assertEquals(2, references.size());
    }

}
