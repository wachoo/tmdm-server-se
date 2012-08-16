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
package org.talend.mdm.webapp.stagingarea.client;

import org.talend.mdm.webapp.stagingarea.client.model.StagingAreaConfiguration;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;


/**
 * DOC suplch  class global comment. Detailled comment
 */
@RemoteServiceRelativePath("StagingAreaService")
public interface StagingAreaService extends RemoteService {

    public StagingAreaConfiguration getStagingAreaConfig();
}
