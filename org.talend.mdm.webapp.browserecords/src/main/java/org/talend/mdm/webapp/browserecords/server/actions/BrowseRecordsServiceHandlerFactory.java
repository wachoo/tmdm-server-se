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
import org.talend.mdm.webapp.browserecords.server.BrowseRecordsConfiguration;

public class BrowseRecordsServiceHandlerFactory {

    /**
     * DOC HSHU ItemServiceHandlerFactory constructor comment.
     */
    public BrowseRecordsServiceHandlerFactory() {
    }

    /**
     * DOC HSHU Comment method "createHandler".
     * @return
     */
    public static BrowseRecordsService createHandler() {
        
        if (!BrowseRecordsConfiguration.isStandalone()) {
            return new BrowseRecordsServiceHandler();
        } else {
            return new BrowseRecordsServiceProxyHandler();
        }

    }

}
