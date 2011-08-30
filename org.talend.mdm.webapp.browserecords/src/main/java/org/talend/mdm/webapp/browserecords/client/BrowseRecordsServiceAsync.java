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
package org.talend.mdm.webapp.browserecords.client;

import java.util.List;

import org.talend.mdm.webapp.browserecords.client.model.ForeignKeyBean;
import org.talend.mdm.webapp.browserecords.client.model.ForeignKeyDrawer;
import org.talend.mdm.webapp.browserecords.client.model.ItemBaseModel;
import org.talend.mdm.webapp.browserecords.client.model.ItemBasePageLoadResult;
import org.talend.mdm.webapp.browserecords.client.model.ItemBean;
import org.talend.mdm.webapp.browserecords.client.model.ItemResult;
import org.talend.mdm.webapp.browserecords.client.model.QueryModel;
import org.talend.mdm.webapp.browserecords.client.model.Restriction;
import org.talend.mdm.webapp.browserecords.shared.AppHeader;
import org.talend.mdm.webapp.browserecords.shared.EntityModel;
import org.talend.mdm.webapp.browserecords.shared.TypeModel;
import org.talend.mdm.webapp.browserecords.shared.ViewBean;

import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The async counterpart of <code>BrowseRecordsService</code>.
 */
public interface BrowseRecordsServiceAsync {

    void getForeignKeyList(PagingLoadConfig config, TypeModel model, String dataClusterPK, boolean ifFKFilter, String value,
            AsyncCallback<ItemBasePageLoadResult<ForeignKeyBean>> callback);

    void getForeignKeyPolymTypeList(String xpathForeignKey, String language, AsyncCallback<List<Restriction>> callback);

    void switchForeignKeyType(String targetEntityType, String xpathForeignKey, String xpathInfoForeignKey, String fkFilter,
            AsyncCallback<ForeignKeyDrawer> callback);

    void queryItemBeans(QueryModel config, AsyncCallback<ItemBasePageLoadResult<ItemBean>> callback);

    void saveItemBean(ItemBean item, AsyncCallback<ItemResult> callback);

    void getItem(ItemBean itemBean, EntityModel entityModel, AsyncCallback<ItemBean> callback);

    void getView(String viewPk, String language, AsyncCallback<ViewBean> callback);

    void deleteItemBean(ItemBean item, AsyncCallback<ItemResult> callback);

    void deleteItemBeans(List<ItemBean> items, AsyncCallback<List<ItemResult>> callback);

    void logicalDeleteItem(ItemBean item, String path, AsyncCallback<ItemResult> callback);

    void logicalDeleteItems(List<ItemBean> items, String path, AsyncCallback<List<ItemResult>> callback);

    void getViewsList(String language, AsyncCallback<List<ItemBaseModel>> callback);

    void getCriteriaByBookmark(String bookmark, AsyncCallback<String> callback);

    void getUserCriterias(String view, AsyncCallback<List<ItemBaseModel>> callback);

    void getAppHeader(AsyncCallback<AppHeader> callback);

    void getCurrentDataModel(AsyncCallback<String> callback);

    void getCurrentDataCluster(AsyncCallback<String> callback);

}
