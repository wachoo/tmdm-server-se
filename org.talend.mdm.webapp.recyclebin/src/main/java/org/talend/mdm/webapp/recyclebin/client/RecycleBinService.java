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
package org.talend.mdm.webapp.recyclebin.client;

import org.talend.mdm.webapp.recyclebin.shared.ItemsTrashItem;

import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("RecycleBinService")
public interface RecycleBinService extends RemoteService {

    PagingLoadResult<ItemsTrashItem> getTrashItems(String regex, PagingLoadConfig load) throws Exception;

    void removeDroppedItem(String itemPk, String partPath, String revisionId, String conceptName, String ids) throws Exception;

    void recoverDroppedItem(String itemPk, String partPath, String revisionId, String conceptName, String ids) throws Exception;

    String getCurrentDataModel() throws Exception;

    String getCurrentDataCluster() throws Exception;

    boolean isEntityPhysicalDeletable(String conceptName) throws Exception;
}
