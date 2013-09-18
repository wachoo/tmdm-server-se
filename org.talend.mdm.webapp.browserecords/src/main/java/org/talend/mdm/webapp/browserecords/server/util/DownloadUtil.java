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

import org.dom4j.Document;
import org.dom4j.Node;

import com.amalto.commons.core.utils.XMLUtils;
import com.amalto.webapp.core.util.Util;

public class DownloadUtil {

    public static void assembleFkMap(Map<String, String> colFkMap, Map<String, List<String>> fkMap, String fkColXPath,
            String fkInfo) throws Exception {
        if (!fkColXPath.equalsIgnoreCase("")) { //$NON-NLS-1$
            String[] fkColXPathArr = convertXml2Array(fkColXPath, "fkColXPath"); //$NON-NLS-1$
            String[] fkInfoArr = convertXml2Array(fkInfo, "fkInfo"); //$NON-NLS-1$
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

    public static String[] convertXml2Array(String xml, String rootName) throws Exception {
        List<String> resultList = new ArrayList<String>();
        org.w3c.dom.Document doc = Util.parse(xml);
        org.w3c.dom.NodeList nodeList = Util.getNodeList(doc, "/" + rootName + "/item"); //$NON-NLS-1$ //$NON-NLS-2$
        for (int i = 0; i < nodeList.getLength(); i++) {
            org.w3c.dom.Node node = nodeList.item(i).getFirstChild();
            resultList.add(XMLUtils.nodeToString(node));
        }
        return resultList.toArray(new String[resultList.size()]);
    }

    public static boolean isJoinField(String xPath, String concept) {
        String str = xPath.substring(0, xPath.indexOf("/")); //$NON-NLS-1$
        return str.equalsIgnoreCase(concept);
    }

    public static String getJoinFieldValue(Document doc, String xPath, int count) {
        if (doc.getRootElement().elements().size() > count) {
            Node node = (Node) doc.getRootElement().elements().get(count);
            String joinConcept = xPath.substring(0, xPath.indexOf("/")); //$NON-NLS-1$
            String joinFieldXpath = xPath.replaceFirst(joinConcept + "/", "/result/"); //$NON-NLS-1$//$NON-NLS-2$
            if (node.getPath().equalsIgnoreCase(joinFieldXpath)) {
                return node.getText();
            }
        }
        return ""; //$NON-NLS-1$
    }
}
