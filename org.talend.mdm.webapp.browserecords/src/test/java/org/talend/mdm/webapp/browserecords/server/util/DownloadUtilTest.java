// ============================================================================
//
// Copyright (C) 2006-2012 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.mdm.webapp.browserecords.server.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

@SuppressWarnings("nls")
public class DownloadUtilTest extends TestCase {
    
    public void testAssembleFkMap(){
        Map<String, String> colFkMap = new HashMap<String, String>();
        Map<String, List<String>> fkMap = new HashMap<String, List<String>>();
        String fkColXPath = "Person/Shop,Store/Id"; 
        String fkInfo = "Store/Name";
        DownloadUtil.assembleFkMap(colFkMap, fkMap, fkColXPath, fkInfo);
        
        assertEquals(1, colFkMap.size());
        assertEquals(1, fkMap.size());
        assertEquals("Person/Shop", colFkMap.keySet().iterator().next());
        assertEquals("Person/Shop", fkMap.keySet().iterator().next());
        assertEquals("Store/Id", colFkMap.get(colFkMap.keySet().iterator().next()));
        assertEquals("Store/Name", fkMap.get(fkMap.keySet().iterator().next()).get(0));
        
        colFkMap.clear();
        fkMap.clear();
        String fk1 = "Person/Shop";
        String fk2 = "Product/Famliy";
        fkColXPath = fk1 + ",Store/Id@" + fk2 + ",ProductFamliy/Id"; 
        fkInfo = "Store/Name@ProductFamliy/name,ProductFamliy/Code";
        DownloadUtil.assembleFkMap(colFkMap, fkMap, fkColXPath, fkInfo);
        
        assertEquals(2, colFkMap.size());
        assertEquals(2, fkMap.size());
        assertEquals("Store/Id", colFkMap.get(fk1));
        assertEquals("ProductFamliy/Id", colFkMap.get(fk2));
        assertEquals("Store/Name", fkMap.get(fk1).get(0));
        assertEquals(2, fkMap.get(fk2).size());
        assertEquals("ProductFamliy/name", fkMap.get(fk2).get(0));
        assertEquals("ProductFamliy/Code", fkMap.get(fk2).get(1));
    }
}
