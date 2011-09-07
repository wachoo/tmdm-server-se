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
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The async counterpart of <code>RecycleBinService</code>.
 */
public interface RecycleBinServiceAsync {

    void getTrashItems(String regex, PagingLoadConfig load, AsyncCallback<PagingLoadResult<ItemsTrashItem>> callback);

    void removeDroppedItem(String itemPk, String partPath, String revisionId, String conceptName, String ids,
            AsyncCallback<Void> callback);

    void recoverDroppedItem(String itemPk, String partPath, String revisionId, String conceptName, String ids,
            AsyncCallback<Void> callback);

    void getCurrentDataModel(AsyncCallback<String> callback);

    void getCurrentDataCluster(AsyncCallback<String> callback);

    void isEntityPhysicalDeletable(String conceptName, AsyncCallback<Boolean> callback);
}
