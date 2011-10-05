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

import org.talend.mdm.webapp.base.client.exception.ServiceException;
import org.talend.mdm.webapp.base.client.model.ForeignKeyBean;
import org.talend.mdm.webapp.base.client.model.ItemBaseModel;
import org.talend.mdm.webapp.base.client.model.ItemBasePageLoadResult;
import org.talend.mdm.webapp.base.shared.TypeModel;
import org.talend.mdm.webapp.browserecords.client.model.ColumnTreeLayoutModel;
import org.talend.mdm.webapp.browserecords.client.model.ForeignKeyDrawer;
import org.talend.mdm.webapp.browserecords.client.model.ForeignKeyModel;
import org.talend.mdm.webapp.browserecords.client.model.ItemBean;
import org.talend.mdm.webapp.browserecords.client.model.ItemNodeModel;
import org.talend.mdm.webapp.browserecords.client.model.QueryModel;
import org.talend.mdm.webapp.browserecords.client.model.Restriction;
import org.talend.mdm.webapp.browserecords.shared.AppHeader;
import org.talend.mdm.webapp.browserecords.shared.EntityModel;
import org.talend.mdm.webapp.browserecords.shared.FKIntegrityResult;
import org.talend.mdm.webapp.browserecords.shared.ViewBean;
import org.talend.mdm.webapp.browserecords.shared.VisibleRuleResult;

import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("BrowseRecordsService")
public interface BrowseRecordsService extends RemoteService {

    ItemBasePageLoadResult<ItemBean> queryItemBeans(final QueryModel config) throws ServiceException;

    ItemBean getItem(ItemBean itemBean, EntityModel entityModel, String language) throws ServiceException;

    String saveItemBean(ItemBean item) throws ServiceException;

    ItemBasePageLoadResult<ForeignKeyBean> getForeignKeyList(PagingLoadConfig config, TypeModel model, String dataClusterPK,
            boolean ifFKFilter, String value) throws ServiceException;

    List<Restriction> getForeignKeyPolymTypeList(String xpathForeignKey, String language) throws ServiceException;

    ForeignKeyDrawer switchForeignKeyType(String targetEntityType, String xpathForeignKey, String xpathInfoForeignKey,
            String fkFilter) throws ServiceException;

    ViewBean getView(String viewPk, String language) throws ServiceException;

    String deleteItemBean(ItemBean item, boolean override) throws ServiceException;

    List<String> deleteItemBeans(List<ItemBean> items, boolean override) throws ServiceException;

    Map<ItemBean, FKIntegrityResult> checkFKIntegrity(List<ItemBean> selectedItems) throws ServiceException;

    void logicalDeleteItem(ItemBean item, String path, boolean override) throws ServiceException;

    void logicalDeleteItems(List<ItemBean> items, String path, boolean override) throws ServiceException;

    List<ItemBaseModel> getViewsList(String language) throws ServiceException;

    String getCriteriaByBookmark(String bookmark) throws ServiceException;

    List<ItemBaseModel> getUserCriterias(String view) throws ServiceException;

    AppHeader getAppHeader() throws ServiceException;

    String getCurrentDataModel() throws ServiceException;

    String getCurrentDataCluster() throws ServiceException;

    PagingLoadResult<ItemBaseModel> querySearchTemplates(String view, boolean isShared, PagingLoadConfig load)
            throws ServiceException;

    void deleteSearchTemplate(String id) throws ServiceException;

    boolean isExistCriteria(String dataObjectLabel, String id) throws ServiceException;

    void saveCriteria(String viewPK, String templateName, boolean isShared, String criteriaString) throws ServiceException;

    ItemNodeModel getItemNodeModel(ItemBean item, EntityModel entity, String language) throws ServiceException;

    List<String> getMandatoryFieldList(String tableName) throws ServiceException;

    String saveItem(String concept, String ids, String xml, boolean isCreate) throws ServiceException;

    ColumnTreeLayoutModel getColumnTreeLayout(String concept) throws ServiceException;

    ForeignKeyModel getForeignKeyModel(String concept, String ids, String language) throws ServiceException;

    List<ItemBaseModel> getRunnableProcessList(String concept, String language) throws ServiceException;

    String processItem(String concept, String[] ids, String transformerPK) throws ServiceException;

    List<String> getLineageEntity(String concept) throws ServiceException;

    List<ItemBaseModel> getSmartViewList(String regex) throws ServiceException;

    ItemBean getItemBeanById(String concept, String[] ids, String language) throws ServiceException;

    List<VisibleRuleResult> executeVisibleRule(String xml) throws ServiceException;

    boolean isItemModifiedByOthers(ItemBean itemBean) throws ServiceException;
}
