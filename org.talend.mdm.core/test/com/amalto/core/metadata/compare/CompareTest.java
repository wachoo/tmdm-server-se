/*
 * Copyright (C) 2006-2014 Talend Inc. - www.talend.com
 * 
 * This source code is available under agreement available at
 * %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
 * 
 * You should have received a copy of the agreement along with this program; if not, write to Talend SA 9 rue Pages
 * 92150 Suresnes, France
 */

package com.amalto.core.metadata.compare;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import org.apache.commons.io.IOUtils;
import org.talend.mdm.commmon.metadata.MetadataRepository;
import org.talend.mdm.commmon.metadata.compare.Change;
import org.talend.mdm.commmon.metadata.compare.Compare;
import org.talend.mdm.commmon.metadata.compare.HibernateStorageImpactAnalyzer;
import org.talend.mdm.commmon.metadata.compare.ImpactAnalyzer;

import com.amalto.core.server.MockServerLifecycle;
import com.amalto.core.server.Server;
import com.amalto.core.server.ServerContext;
import com.amalto.core.storage.StorageType;
import com.amalto.core.storage.hibernate.HibernateStorage;

public class CompareTest extends TestCase {

    public void test1() throws Exception {
        MetadataRepository original = new MetadataRepository();
        original.load(CompareTest.class.getResourceAsStream("schema1_1.xsd"));
        MetadataRepository updated = new MetadataRepository();
        updated.load(CompareTest.class.getResourceAsStream("schema1_2.xsd"));
        Compare.DiffResults diffResults = Compare.compare(original, updated);
        assertEquals(4, diffResults.getActions().size());
        assertEquals(1, diffResults.getModifyChanges().size());
        assertEquals(3, diffResults.getRemoveChanges().size());

        ImpactAnalyzer analyzer = new HibernateStorageImpactAnalyzer();
        Map<ImpactAnalyzer.Impact, List<Change>> sort = analyzer.analyzeImpacts(diffResults);
        assertEquals(2, sort.get(ImpactAnalyzer.Impact.HIGH).size());
        assertEquals(2, sort.get(ImpactAnalyzer.Impact.MEDIUM).size());
    }

    public void test2() throws Exception {
        MetadataRepository original = new MetadataRepository();
        original.load(CompareTest.class.getResourceAsStream("schema2_1.xsd"));
        MetadataRepository updated = new MetadataRepository();
        updated.load(CompareTest.class.getResourceAsStream("schema2_2.xsd"));
        Compare.DiffResults diffResults = Compare.compare(original, updated);
        assertEquals(1, diffResults.getActions().size());
        assertEquals(0, diffResults.getModifyChanges().size());
        assertEquals(0, diffResults.getRemoveChanges().size());
        assertEquals(1, diffResults.getAddChanges().size());

        ImpactAnalyzer analyzer = new HibernateStorageImpactAnalyzer();
        Map<ImpactAnalyzer.Impact, List<Change>> sort = analyzer.analyzeImpacts(diffResults);
        assertEquals(0, sort.get(ImpactAnalyzer.Impact.HIGH).size());
        assertEquals(0, sort.get(ImpactAnalyzer.Impact.MEDIUM).size());
        assertEquals(1, sort.get(ImpactAnalyzer.Impact.LOW).size());
    }

    public void test3() throws Exception {
        MetadataRepository original = new MetadataRepository();
        original.load(CompareTest.class.getResourceAsStream("schema3_1.xsd"));
        MetadataRepository updated = new MetadataRepository();
        updated.load(CompareTest.class.getResourceAsStream("schema3_2.xsd"));
        Compare.DiffResults diffResults = Compare.compare(original, updated);
        assertEquals(1, diffResults.getActions().size());
        assertEquals(1, diffResults.getModifyChanges().size());
        assertEquals(0, diffResults.getRemoveChanges().size());
        assertEquals(0, diffResults.getAddChanges().size());

        ImpactAnalyzer analyzer = new HibernateStorageImpactAnalyzer();
        Map<ImpactAnalyzer.Impact, List<Change>> sort = analyzer.analyzeImpacts(diffResults);
        assertEquals(1, sort.get(ImpactAnalyzer.Impact.HIGH).size());
        assertEquals(0, sort.get(ImpactAnalyzer.Impact.MEDIUM).size());
        assertEquals(0, sort.get(ImpactAnalyzer.Impact.LOW).size());
    }

    public void test4() throws Exception {
        MetadataRepository original = new MetadataRepository();
        original.load(CompareTest.class.getResourceAsStream("schema4_1.xsd"));
        MetadataRepository updated = new MetadataRepository();
        updated.load(CompareTest.class.getResourceAsStream("schema4_2.xsd"));
        Compare.DiffResults diffResults = Compare.compare(original, updated);
        assertEquals(1, diffResults.getActions().size());
        assertEquals(1, diffResults.getModifyChanges().size());
        assertEquals(0, diffResults.getRemoveChanges().size());
        assertEquals(0, diffResults.getAddChanges().size());

        ImpactAnalyzer analyzer = new HibernateStorageImpactAnalyzer();
        Map<ImpactAnalyzer.Impact, List<Change>> sort = analyzer.analyzeImpacts(diffResults);
        assertEquals(1, sort.get(ImpactAnalyzer.Impact.HIGH).size());
        assertEquals(0, sort.get(ImpactAnalyzer.Impact.MEDIUM).size());
        assertEquals(0, sort.get(ImpactAnalyzer.Impact.LOW).size());
    }

    public void test5() throws Exception {
        MetadataRepository original = new MetadataRepository();
        original.load(CompareTest.class.getResourceAsStream("schema5_1.xsd"));
        original = original.copy();
        MetadataRepository updated = new MetadataRepository();
        updated.load(CompareTest.class.getResourceAsStream("schema5_2.xsd")); //$NON-NLS-1$
        Compare.DiffResults diffResults = Compare.compare(original, updated);
        for (Change change : diffResults.getActions()) {
            System.out.println("change = " + change.getMessage());
        }
        assertEquals(0, diffResults.getActions().size());
        assertEquals(0, diffResults.getModifyChanges().size());
        assertEquals(0, diffResults.getRemoveChanges().size());
        assertEquals(0, diffResults.getAddChanges().size());

        ImpactAnalyzer analyzer = new HibernateStorageImpactAnalyzer();
        Map<ImpactAnalyzer.Impact, List<Change>> sort = analyzer.analyzeImpacts(diffResults);
        assertEquals(0, sort.get(ImpactAnalyzer.Impact.HIGH).size());
        assertEquals(0, sort.get(ImpactAnalyzer.Impact.MEDIUM).size());
        assertEquals(0, sort.get(ImpactAnalyzer.Impact.LOW).size());
    }
}
