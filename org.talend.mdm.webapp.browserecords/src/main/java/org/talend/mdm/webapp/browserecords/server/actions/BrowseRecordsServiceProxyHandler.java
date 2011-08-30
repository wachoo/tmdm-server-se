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
import org.talend.mdm.webapp.browserecords.server.mockup.FakeData;

/**
 * DOC HSHU class global comment. Detailled comment
 * 
 * Fake MDM Jboss related methods here
 */
public class BrowseRecordsServiceProxyHandler extends BrowseRecordsServiceCommonHandler implements BrowseRecordsService {

    public String getCurrentDataModel() throws Exception {
        return FakeData.DATA_MODEL;
    }
    
    public String getCurrentDataCluster() throws Exception {
        return FakeData.DATA_CLUSTER;
    }

}
