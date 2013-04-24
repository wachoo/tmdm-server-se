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

import java.util.Properties;

import org.apache.commons.lang.StringEscapeUtils;
import org.talend.mdm.commmon.util.core.MDMConfiguration;
import org.talend.mdm.webapp.browserecords.server.util.DownloadUtil;

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

    private String rootElementName = "result"; //$NON-NLS-1$

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
            return DownloadUtil.convertXml2Array(StringEscapeUtils.unescapeXml(sourceXmlString), rootElementName);
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
