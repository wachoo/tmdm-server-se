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
import com.amalto.core.metadata.ReferenceUnaryFieldMetadata;
import junit.framework.TestCase;

import java.util.Set;

/**
 *
 */
@SuppressWarnings({"HardCodedStringLiteral", "nls"})
public class ForeignKeyIntegrityTest extends TestCase {

    private MetadataRepository repository;

    @Override
    public void setUp() throws Exception {
        try {
            repository = new MetadataRepository();
            repository.load(getClass().getResourceAsStream("test1.xsd"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void test1() throws Exception {
        String typeName = "language";
        ForeignKeyIntegrity keyIntegrity = new ForeignKeyIntegrity(typeName);
        Set<ReferenceFieldMetadata> references = repository.accept(keyIntegrity);

        assertFalse(references.isEmpty());
        System.out.println("=====================");
        System.out.println("References to type:" + typeName);
        System.out.println("=====================");
        for (ReferenceFieldMetadata reference : references) {
            System.out.println("Type: " + reference.getContainingType().getName() + " / Field: " + reference.getName());
        }

    }
}
