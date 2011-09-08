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
import java.util.Map;

import org.talend.mdm.webapp.browserecords.client.model.ForeignKeyBean;
import org.talend.mdm.webapp.browserecords.client.model.ForeignKeyDrawer;
import org.talend.mdm.webapp.browserecords.client.model.ItemBaseModel;
import org.talend.mdm.webapp.browserecords.client.model.ItemBasePageLoadResult;
import org.talend.mdm.webapp.browserecords.client.model.ItemBean;
import org.talend.mdm.webapp.browserecords.client.model.ItemNodeModel;
import org.talend.mdm.webapp.browserecords.client.model.ItemResult;
import org.talend.mdm.webapp.browserecords.client.model.QueryModel;
import org.talend.mdm.webapp.browserecords.client.model.Restriction;
import org.talend.mdm.webapp.browserecords.shared.AppHeader;
import org.talend.mdm.webapp.browserecords.shared.EntityModel;
import org.talend.mdm.webapp.browserecords.shared.TypeModel;
import org.talend.mdm.webapp.browserecords.shared.ViewBean;

import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("BrowseRecordsService")
public interface BrowseRecordsService extends RemoteService {

    ItemBasePageLoadResult<ItemBean> queryItemBeans(final QueryModel config);

    ItemBean getItem(ItemBean itemBean, EntityModel entityModel, String language) throws Exception;

    ItemResult saveItemBean(ItemBean item);

    ItemBasePageLoadResult<ForeignKeyBean> getForeignKeyList(PagingLoadConfig config, TypeModel model, String dataClusterPK,
            boolean ifFKFilter, String value);

    List<Restriction> getForeignKeyPolymTypeList(String xpathForeignKey, String language) throws Exception;

    ForeignKeyDrawer switchForeignKeyType(String targetEntityType, String xpathForeignKey, String xpathInfoForeignKey,
            String fkFilter) throws Exception;

    ViewBean getView(String viewPk, String language);

    ItemResult deleteItemBean(ItemBean item);

    List<ItemResult> deleteItemBeans(List<ItemBean> items);

    ItemResult logicalDeleteItem(ItemBean item, String path);

    List<ItemResult> logicalDeleteItems(List<ItemBean> items, String path);

    List<ItemBaseModel> getViewsList(String language);

    String getCriteriaByBookmark(String bookmark);

    List<ItemBaseModel> getUserCriterias(String view);

    AppHeader getAppHeader() throws Exception;

    String getCurrentDataModel() throws Exception;

    String getCurrentDataCluster() throws Exception;

    PagingLoadResult<ItemBaseModel> querySearchTemplates(String view, boolean isShared, PagingLoadConfig load);

    String deleteSearchTemplate(String id);

    boolean isExistCriteria(String dataObjectLabel, String id);

    String saveCriteria(String viewPK, String templateName, boolean isShared, String criteriaString);
            
    ItemNodeModel getItemNodeModel(String concept,Map<String, TypeModel> metaDataTypes,String ids) throws Exception;
    
    List<String> getMandatoryFieldList(String tableName) throws Exception;

    ItemResult saveItem(String concept, String ids, String xml, boolean isCreate);
}
