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
package org.talend.mdm.webapp.browserecords.server.actions;

import org.talend.mdm.webapp.browserecords.client.BrowseRecordsService;

import com.amalto.webapp.core.bean.Configuration;


/**
 * DOC HSHU  class global comment. Detailled comment
 * 
 * Customize MDM Jboss related methods here
 */
public class BrowseRecordsServiceHandler extends BrowseRecordsServiceCommonHandler implements BrowseRecordsService {

    public String getCurrentDataModel() throws Exception {
        Configuration config = Configuration.getConfiguration();
        return config.getModel();
    }
    

    public String getCurrentDataCluster() throws Exception {
        Configuration config = Configuration.getConfiguration();
        return config.getCluster();
    }
}
