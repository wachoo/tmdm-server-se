/*
 * Copyright (C) 2006-2013 Talend Inc. - www.talend.com
 * 
 * This source code is available under agreement available at
 * %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
 * 
 * You should have received a copy of the agreement along with this program; if not, write to Talend SA 9 rue Pages
 * 92150 Suresnes, France
 */

package com.amalto.core.metadata.compare;

import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import org.talend.mdm.commmon.metadata.MetadataRepository;
import org.talend.mdm.commmon.metadata.compare.Change;
import org.talend.mdm.commmon.metadata.compare.Compare;
import org.talend.mdm.commmon.metadata.compare.HibernateStorageImpactAnalyzer;
import org.talend.mdm.commmon.metadata.compare.ImpactAnalyzer;

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
}
