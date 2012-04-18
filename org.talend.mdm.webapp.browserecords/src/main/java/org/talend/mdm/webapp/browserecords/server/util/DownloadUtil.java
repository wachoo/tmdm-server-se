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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DownloadUtil {

    public static void assembleFkMap(Map<String, String> colFkMap, Map<String, List<String>> fkMap, String fkColXPath, String fkInfo) {
        if (!fkColXPath.equalsIgnoreCase("")) { //$NON-NLS-1$
            String[] fkColXPathArr = fkColXPath.split("@"); //$NON-NLS-1$
            String[] fkInfoArr = fkInfo.split("@"); //$NON-NLS-1$
            for (int i = 0; i < fkColXPathArr.length; i++) {
                String[] fkStr = fkInfoArr[i].split(","); //$NON-NLS-1$
                List<String> fkList = new ArrayList<String>();
                for (String str : fkStr) {
                    fkList.add(str);
                }
                String[] arr = fkColXPathArr[i].split(","); //$NON-NLS-1$
                fkMap.put(arr[0], fkList);
                colFkMap.put(arr[0], arr[1]);
            }
        }
    }
}
