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

import org.dom4j.Document;
import org.dom4j.Node;

public class DownloadUtil {

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
