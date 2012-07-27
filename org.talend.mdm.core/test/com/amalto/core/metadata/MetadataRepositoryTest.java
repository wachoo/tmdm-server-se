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

package com.amalto.core.metadata;

import junit.framework.TestCase;

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
        repository.load(stream);
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
    
    public void test12() {
        MetadataRepository repository = new MetadataRepository();
        InputStream stream = getClass().getResourceAsStream("schema12.xsd");
        repository.load(stream);
        assertTrue(repository.getTypes().size() > 0);
    }

}
