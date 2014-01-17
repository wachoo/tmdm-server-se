// ============================================================================
//
// Copyright (C) 2006-2013 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package com.amalto.core.ejb;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.talend.mdm.commmon.util.datamodel.management.BusinessConcept;
import org.talend.mdm.commmon.util.datamodel.management.DataModelID;

import com.amalto.core.schema.manage.SchemaCoreAgent;

@RunWith(PowerMockRunner.class)
public class DroppedItemPOJOPKTest extends TestCase {

    @Test
    @PrepareForTest({ SchemaCoreAgent.class })
    public void testbuildUid2POJOPK() throws Exception {
        String input = "input head.Product.Product.id3-"; //$NON-NLS-1$
        Map<String, BusinessConcept> conceptMap = new HashMap<String, BusinessConcept>();
        List<String> list = new ArrayList<String>();
        list.add("test"); //$NON-NLS-1$

        BusinessConcept businessConcept = new BusinessConcept();
        businessConcept.setKeyFieldPaths(list);

        SchemaCoreAgent agent = PowerMockito.mock(SchemaCoreAgent.class);
        PowerMockito.mockStatic(SchemaCoreAgent.class);
        PowerMockito.when(SchemaCoreAgent.getInstance()).thenReturn(agent);
        PowerMockito.when(agent.getBusinessConcept(Mockito.any(String.class), new DataModelID(Mockito.any(String.class), null)))
                .thenReturn(businessConcept);

        assertEquals("id3", DroppedItemPOJOPK.buildUid2POJOPK(input, conceptMap).getRefItemPOJOPK().getIds()[0]); //$NON-NLS-1$
    }

}
