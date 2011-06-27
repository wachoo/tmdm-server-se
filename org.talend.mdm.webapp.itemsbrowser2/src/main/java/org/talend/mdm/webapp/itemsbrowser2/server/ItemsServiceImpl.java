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

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.ServletException;

import org.talend.mdm.webapp.itemsbrowser2.client.ItemsService;
import org.talend.mdm.webapp.itemsbrowser2.client.i18n.MessagesFactory;
import org.talend.mdm.webapp.itemsbrowser2.client.model.ForeignKeyBean;
import org.talend.mdm.webapp.itemsbrowser2.client.model.ItemBaseModel;
import org.talend.mdm.webapp.itemsbrowser2.client.model.ItemBasePageLoadResult;
import org.talend.mdm.webapp.itemsbrowser2.client.model.ItemBean;
import org.talend.mdm.webapp.itemsbrowser2.client.model.ItemResult;
import org.talend.mdm.webapp.itemsbrowser2.client.model.QueryModel;
import org.talend.mdm.webapp.itemsbrowser2.server.i18n.ItemsbrowserMessagesImpl;
import org.talend.mdm.webapp.itemsbrowser2.shared.AppHeader;
import org.talend.mdm.webapp.itemsbrowser2.shared.EntityModel;
import org.talend.mdm.webapp.itemsbrowser2.shared.TypeModel;
import org.talend.mdm.webapp.itemsbrowser2.shared.ViewBean;

import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class ItemsServiceImpl extends RemoteServiceServlet implements ItemsService {

    @Override
    public void init() throws ServletException {
        super.init();
        MessagesFactory.setMessages(new ItemsbrowserMessagesImpl());
    }

    private static ItemsService itemsServiceHandler = ItemServiceHandlerFactory.createHandler();

    public ItemBasePageLoadResult<ItemBean> queryItemBeans(QueryModel config) {
        return itemsServiceHandler.queryItemBeans(config);
    }

    public ViewBean getView(String viewPk, String language) {
        return itemsServiceHandler.getView(viewPk, language);
    }

    public List<ItemBaseModel> getViewsList(String language) {
        return itemsServiceHandler.getViewsList(language);
    }

    /*
     * (non-Jsdoc)
     * 
     * @see org.talend.mdm.webapp.itemsbrowser2.client.ItemsService#isExistCriteria(java.lang.String, java.lang.String)
     */
    public boolean isExistCriteria(String dataObjectLabel, String id) {
        return itemsServiceHandler.isExistCriteria(dataObjectLabel, id);
    }

    /*
     * (non-Jsdoc)
     * 
     * @see org.talend.mdm.webapp.itemsbrowser2.client.ItemsService#saveCriteria(java.lang.String, java.lang.String,
     * boolean, java.lang.String)
     */
    public String saveCriteria(String viewPK, String templateName, boolean isShared, String criteriaString) {
        return itemsServiceHandler.saveCriteria(viewPK, templateName, isShared, criteriaString);
    }

    /*
     * (non-Jsdoc)
     * 
     * @see org.talend.mdm.webapp.itemsbrowser2.client.ItemsService#getCriteriaByBookmark(java.lang.String)
     */
    public String getCriteriaByBookmark(String bookmark) {
        return itemsServiceHandler.getCriteriaByBookmark(bookmark);
    }

    /*
     * (non-Jsdoc)
     * 
     * @see org.talend.mdm.webapp.itemsbrowser2.client.ItemsService#getviewItemsCriterias(java.lang.String)
     */
    public List<ItemBaseModel> getUserCriterias(String view) {
        return itemsServiceHandler.getUserCriterias(view);
    }

    /*
     * (non-Jsdoc)
     * 
     * @see org.talend.mdm.webapp.itemsbrowser2.client.ItemsService#querySearchTemplates(java.lang.String, boolean,
     * com.extjs.gxt.ui.client.data.PagingLoadConfig)
     */
    public PagingLoadResult<ItemBaseModel> querySearchTemplates(String view, boolean isShared, PagingLoadConfig load) {
        return itemsServiceHandler.querySearchTemplates(view, isShared, load);
    }

    /*
     * (non-Jsdoc)
     * 
     * @see org.talend.mdm.webapp.itemsbrowser2.client.ItemsService#deleteSearchTemplate(java.lang.String)
     */
    public String deleteSearchTemplate(String id) {
        return itemsServiceHandler.deleteSearchTemplate(id);
    }

    /*
     * (non-Jsdoc)
     * 
     * @see org.talend.mdm.webapp.itemsbrowser2.client.ItemsService#getCurrentDataModel()
     */
    public String getCurrentDataModel() throws Exception {
        return itemsServiceHandler.getCurrentDataModel();
    }

    /*
     * (non-Jsdoc)
     * 
     * @see org.talend.mdm.webapp.itemsbrowser2.client.ItemsService#getCurrentDataCluster()
     */
    public String getCurrentDataCluster() throws Exception {
        return itemsServiceHandler.getCurrentDataCluster();
    }

    /*
     * (non-Jsdoc)
     * 
     * @see
     * org.talend.mdm.webapp.itemsbrowser2.client.ItemsService#saveItemBean(org.talend.mdm.webapp.itemsbrowser2.client
     * .model.ItemBean)
     */
    public ItemResult saveItemBean(ItemBean item) {
        return itemsServiceHandler.saveItemBean(item);
    }

    /*
     * (non-Jsdoc)
     * 
     * @see
     * org.talend.mdm.webapp.itemsbrowser2.client.ItemsService#logicalDeleteItem(org.talend.mdm.webapp.itemsbrowser2
     * .client .model.ItemBean, java.lang.String)
     */
    public ItemResult logicalDeleteItem(ItemBean item, String path) {
        return itemsServiceHandler.logicalDeleteItem(item, path);
    }

    public List<ItemResult> logicalDeleteItems(List<ItemBean> items, String path) {
        return itemsServiceHandler.logicalDeleteItems(items, path);
    }

    /*
     * (non-Jsdoc)
     * 
     * @see
     * org.talend.mdm.webapp.itemsbrowser2.client.ItemsService#deleteItemBean(org.talend.mdm.webapp.itemsbrowser2.client
     * .model.ItemBean)
     */
    public ItemResult deleteItemBean(ItemBean item) {
        return itemsServiceHandler.deleteItemBean(item);
    }

    public List<ItemResult> deleteItemBeans(List<ItemBean> items) {
        return itemsServiceHandler.deleteItemBeans(items);
    }

    /**
     * DOC HSHU Comment method "getAppHeader".
     */
    public AppHeader getAppHeader() throws Exception {
        return itemsServiceHandler.getAppHeader();
    }

    /**
     * DOC HSHU Comment method "getItem".
     * 
     * @throws Exception
     */
    public ItemBean getItem(ItemBean itemBean, EntityModel entityModel) throws Exception {
        return itemsServiceHandler.getItem(itemBean, entityModel);
    }

    public ItemBasePageLoadResult<ForeignKeyBean> getForeignKeyList(PagingLoadConfig config, TypeModel model,
            String dataClusterPK, boolean ifFKFilter, String value) {
        return itemsServiceHandler.getForeignKeyList(config, model, dataClusterPK, ifFKFilter, value);
    }

	@Override
	public List<ItemBaseModel> getUploadTableNames(String datacluster, String value) {
		return itemsServiceHandler.getUploadTableNames(datacluster, value);
	}

	@Override
	public Map<String, List<String>> getUploadTableDescription(String datacluster, String tableName) {
		return itemsServiceHandler.getUploadTableDescription(datacluster, tableName);
	}

	@Override
	public List<ItemBaseModel> deleteItemsBrowserTable(String datacluster, String tableName) {
		return itemsServiceHandler.deleteItemsBrowserTable(datacluster, tableName);
	}


}
