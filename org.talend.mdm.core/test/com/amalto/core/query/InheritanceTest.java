/*
 * Copyright (C) 2006-2012 Talend Inc. - www.talend.com
 *
 * This source code is available under agreement available at
 * %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
 *
 * You should have received a copy of the agreement
 * along with this program; if not, write to Talend SA
 * 9 rue Pages 92150 Suresnes, France
 */

package com.amalto.core.query;

import com.amalto.core.metadata.ComplexTypeMetadata;
import com.amalto.core.metadata.MetadataRepository;
import com.amalto.core.metadata.MetadataUtils;
import junit.framework.TestCase;

import java.util.List;

public class InheritanceTest extends TestCase {

    public void testTypeOrdering() throws Exception {
        MetadataRepository repository = new MetadataRepository();
        repository.load(InheritanceTest.class.getResourceAsStream("inheritance.xsd"));

        List<ComplexTypeMetadata> sortedList = MetadataUtils.sortTypes(repository);
        String[] expectedOrder = {"B", "A", "C"};
        int i = 0;
        for (ComplexTypeMetadata sortedType : sortedList) {
            assertEquals(expectedOrder[i++], sortedType.getName());
        }
    }
}
