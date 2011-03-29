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
package org.talend.mdm.webapp.itemsbrowser2.server.dwr;

import org.apache.log4j.Logger;

import com.amalto.webapp.core.util.Webapp;

/**
 * cluster
 * 
 * 
 * @author starkey
 * 
 */

public class ItemsBrowser2DWR {

    private static final Logger LOG = Logger.getLogger(ItemsBrowser2DWR.class);

    public ItemsBrowser2DWR() {
        super();
    }

    public boolean isAvailable() throws Exception {
        // The real purpose of this method is to make security filter work
        return true;
    }
    
    //TODO: customized your code here
}
