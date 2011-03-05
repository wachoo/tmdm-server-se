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

import org.talend.mdm.webapp.itemsbrowser2.client.ItemsService;

public class ItemServiceHandlerFactory {

    /**
     * DOC HSHU ItemServiceHandlerFactory constructor comment.
     */
    public ItemServiceHandlerFactory() {
    }

    /**
     * DOC HSHU Comment method "createHandler".
     * @return
     */
    public static ItemsService createHandler() {
        
        if (!ItemsBrowserConfiguration.isStandalone()) {
            return new ItemServiceHandler();
        } else {
            return new ItemServiceProxyHandler();
        }

    }

}
