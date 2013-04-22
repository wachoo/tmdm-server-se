// ============================================================================
//
// Copyright (C) 2006-2010 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.mdm.webapp.browserecords.server.provider;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.talend.mdm.commmon.util.core.MDMConfiguration;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.amalto.commons.core.utils.XMLUtils;
import com.amalto.webapp.core.util.Util;

/**
 * DOC talend2 class global comment. Detailled comment
 */
public class DataProvider {

    private String dataCluster;

    private String viewPk;

    private String criteria;

    private Integer skip;

    private String sortDir;

    private String sortField;

    private String language;

    private String sourceXmlString;

    public DataProvider(String dataCluster, String viewPk, String criteria, Integer skip, String sortDir, String sortField,
            String language, String sourceXmlString) {
        this.dataCluster = dataCluster;
        this.viewPk = viewPk;
        this.criteria = criteria;
        this.skip = skip;
        this.sortDir = sortDir;
        this.sortField = sortField;
        this.language = language;
        this.sourceXmlString = sourceXmlString;
    }

    public String[] getDataResult() throws Exception {
        if (!"".equals(sourceXmlString)) { //$NON-NLS-1$
            List<String> resultList = new ArrayList<String>();
            // This blank reacord for excel file header
            resultList.add(""); //$NON-NLS-1$
            Document doc = Util.parse(sourceXmlString);
            NodeList ls = Util.getNodeList(doc, "/results/result"); //$NON-NLS-1$
            for (int i = 0; i < ls.getLength(); i++) {
                Node node = ls.item(i);
                resultList.add(XMLUtils.nodeToString(node));
            }
            return resultList.toArray(new String[resultList.size()]);
        } else {
            Properties mdmConfig = MDMConfiguration.getConfiguration();
            Object value = mdmConfig.get("max.export.browserecord"); //$NON-NLS-1$
            Integer maxCount = 1000;
            if (value != null) {
                maxCount = Integer.parseInt(value.toString());
            }
            return getDataResultFromDB(maxCount);
        }
    }

    private String[] getDataResultFromDB(Integer maxCount) throws Exception {
        return org.talend.mdm.webapp.base.server.util.CommonUtil.getItemBeans(dataCluster, viewPk, criteria, skip, maxCount,
                sortDir, sortField, language);
    }
}
