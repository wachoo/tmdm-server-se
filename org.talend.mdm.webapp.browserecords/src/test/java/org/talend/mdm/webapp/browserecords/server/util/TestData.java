// ============================================================================
//
// Copyright (C) 2006-2011 Talend Inc. - www.talend.com
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

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.talend.mdm.webapp.browserecords.client.model.ItemNodeModel;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


public class TestData {

    private static ItemNodeModel builder(Node node) {
        ItemNodeModel nodeModel = new ItemNodeModel(node.getNodeName());
        String realType = ((Element) node).getAttribute("xsi:type"); //$NON-NLS-1$
        if (realType != null && realType.trim().length() > 0)
            nodeModel.setRealType(realType);
        NodeList children = node.getChildNodes();
        if (children != null) {
            for (int i = 0; i < children.getLength(); i++) {
                Node child = children.item(i);
                if (child.getNodeType() == Node.ELEMENT_NODE) {
                    nodeModel.add(builder(child));
                }
            }
        }
        return nodeModel;
    }

    public static ItemNodeModel getModel() throws Exception {
        Document doc = getDocument();
        ItemNodeModel nodeModel = builder(doc.getDocumentElement());
        return nodeModel;
    }

    public static List<String> getXpathes(String fileName) throws Exception {
        List<String> xpathes = new ArrayList<String>();
        InputStream is = TestData.class.getResourceAsStream("../../" + fileName); //$NON-NLS-1$
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        String ln = br.readLine();
        while (ln != null) {
            xpathes.add(ln);
            ln = br.readLine();
        }
        return xpathes;
    }

    public static Document getDocument() throws Exception {
        InputStream is = CommonUtilTest.class.getResourceAsStream("../../data.xml"); //$NON-NLS-1$
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(is);
        return doc;

    }
}