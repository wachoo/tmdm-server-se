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
package com.amalto.webapp.core.util;

import junit.framework.TestCase;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.amalto.webapp.core.json.JSONArray;
import com.amalto.webapp.core.json.JSONObject;

@SuppressWarnings("nls")
public class UtilTest extends TestCase {

    private String foreignKeyInfo = "ProductFamily/Name";

    private String foreignKey = "ProductFamily/Id";

    private String[] xpathInfos;

    private JSONArray rows = new JSONArray();

    public void testParsingFkQueryResults() throws Exception {
        xpathInfos = new String[] { "ProductFamily/Name" };
        String[] results = new String[4];
        // SQL Query(isQueryFkList = true), Test to get the FK list
        results[0] = "<totalCount>3</totalCount>";
        results[1] = "<result><i>1</i><Name>Talend Shirt</Name></result>";
        results[2] = "<result><i>2</i><Name>Talend Cup</Name></result>";
        results[3] = "<result><i>3</i><Name>Talend Car</Name></result>";
        parsingForeignKeyQueryResults(results, true);
        assertNotNull(rows);
        assertEquals(3, rows.length());
        JSONObject firstRow = (JSONObject) rows.get(0);
        assertEquals("[1]", firstRow.get("keys").toString());
        assertEquals("Talend Shirt", firstRow.get("infos").toString());
        JSONObject secondRow = (JSONObject) rows.get(1);
        assertEquals("[2]", secondRow.get("keys").toString());
        assertEquals("Talend Cup", secondRow.get("infos").toString());
        JSONObject thirdRow = (JSONObject) rows.get(2);
        assertEquals("[3]", thirdRow.get("keys").toString());
        assertEquals("Talend Car", thirdRow.get("infos").toString());
        // SQL Query(isQueryFkList = false), Test to get the FKinfo
        rows = new JSONArray();
        results = new String[2];
        results[0] = "<totalCount>1</totalCount>";
        results[1] = "<result><Name>Talend Shirt</Name><Id>1</Id></result>";
        parsingForeignKeyQueryResults(results, false);
        assertNotNull(rows);
        assertEquals(1, rows.length());
        firstRow = (JSONObject) rows.get(0);
        assertEquals("[1]", firstRow.get("keys").toString());
        assertEquals("Talend Shirt", firstRow.get("infos").toString());
        // XML Query(isQueryFkList = true), Test to get the FK list
        rows = new JSONArray();
        results = new String[4];
        results[0] = "<totalCount>3</totalCount>";
        results[1] = "<result><Name>Talend Shirt</Name><i>1</i></result>";
        results[2] = "<result><Name>Talend Cup</Name><i>2</i></result>";
        results[3] = "<result><Name>Talend Car</Name><i>3</i></result>";
        parsingForeignKeyQueryResults(results, true);
        assertNotNull(rows);
        assertEquals(3, rows.length());
        firstRow = (JSONObject) rows.get(0);
        assertEquals("[1]", firstRow.get("keys").toString());
        assertEquals("Talend Shirt", firstRow.get("infos").toString());
        secondRow = (JSONObject) rows.get(1);
        assertEquals("[2]", secondRow.get("keys").toString());
        assertEquals("Talend Cup", secondRow.get("infos").toString());
        thirdRow = (JSONObject) rows.get(2);
        assertEquals("[3]", thirdRow.get("keys").toString());
        assertEquals("Talend Car", thirdRow.get("infos").toString());
        // XML Query(isQueryFkList = false), Test to get the FKinfo
        rows = new JSONArray();
        results = new String[2];
        results[0] = "<totalCount>1</totalCount>";
        results[1] = "<result><Name>Talend Shirt</Name><i>1</i></result>";
        parsingForeignKeyQueryResults(results, false);
        assertNotNull(rows);
        assertEquals(1, rows.length());
        firstRow = (JSONObject) rows.get(0);
        assertEquals("[1]", firstRow.get("keys").toString());
        assertEquals("Talend Shirt", firstRow.get("infos").toString());

    }

    private JSONArray parsingForeignKeyQueryResults(String[] results, boolean isQueryFkList) throws Exception {
        if (results == null) {
            return null;
        }
        for (int i = 1; i < results.length; i++) {
            // process no infos case
            if (!results[i].startsWith("<result>")) {
                results[i] = "<result>" + results[i] + "</result>";
            }
            results[i] = results[i].replaceAll("\\n", "");// replace \n
            results[i] = results[i].replaceAll(">(\\s+)<", "><"); // replace spaces between elements
            Element root = Util.parse(results[i]).getDocumentElement();
            // recover keys
            String keys = "";
            NodeList nodes = Util.getNodeList(root, "//i");
            if (nodes != null) {
                // when isQueryFkList = false, SQL result:(result(<result><Name>test</Name><Id>1</Id></result>)
                if (nodes.getLength() == 0) {
                    nodes = Util.getNodeList(root, foreignKey.split("/")[1]); //$NON-NLS-1$
                }
                for (int j = 0; j < nodes.getLength(); j++) {
                    if (nodes.item(j) instanceof Element) {
                        keys += "[" + (nodes.item(j).getTextContent() == null ? "" : nodes.item(j).getTextContent()) + "]";
                    }
                }
            }
            // recover xPathInfos
            String infos = "";
            // if no xPath Infos given, use the key values
            if (xpathInfos.length == 0 || "".equals(foreignKeyInfo) || foreignKeyInfo == null) {
                infos = keys;
            } else {
                // build a dash separated string of xPath Infos
                for (String xpath : xpathInfos) {
                    String fkInfoValue = Util.getFirstTextNode(root, xpath.split("/")[1]);
                    fkInfoValue = fkInfoValue != null && fkInfoValue.trim().length() > 0 ? fkInfoValue : "";
                    if (infos.length() == 0) {
                        infos += fkInfoValue;
                    } else {
                        infos += "-" + fkInfoValue;
                    }
                }
            }

            if ((keys.equals("[]") || keys.equals("")) && (infos.equals("") || infos.equals("[]"))) {
                // empty row
            } else {
                JSONObject row = new JSONObject();
                row.put("keys", keys); //$NON-NLS-1$
                row.put("infos", infos); //$NON-NLS-1$
                rows.put(row);
            }
        }
        return rows;
    }
}