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
package org.talend.mdm.webapp.itemsbrowser2.server;

import org.talend.mdm.webapp.base.server.mockup.FakeData;
import org.talend.mdm.webapp.itemsbrowser2.client.ItemsService;

/**
 * DOC HSHU class global comment. Detailled comment
 * 
 * Fake MDM Jboss related methods here
 */
public class ItemServiceProxyHandler extends ItemServiceCommonHandler implements ItemsService {

    public String getCurrentDataModel() throws Exception {
        return FakeData.DATA_MODEL;
    }
    
    public String getCurrentDataCluster() throws Exception {
        return FakeData.DATA_CLUSTER;
    }

}
