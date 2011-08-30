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

import java.util.List;

import org.apache.log4j.Logger;
import org.talend.mdm.webapp.browserecords.client.BrowseRecordsService;
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
import com.extjs.gxt.ui.client.data.PagingLoadResult;

/**
 * DOC Administrator class global comment. Detailled comment
 */
public class BrowseRecordsAction implements BrowseRecordsService {
    private static final Logger LOG = Logger.getLogger(BrowseRecordsAction.class);

    private static BrowseRecordsService browserecordsServiceHandler = BrowseRecordsServiceHandlerFactory.createHandler();

    public ItemResult deleteItemBean(ItemBean item) {
        return browserecordsServiceHandler.deleteItemBean(item);
    }

    public List<ItemResult> deleteItemBeans(List<ItemBean> items) {
        return browserecordsServiceHandler.deleteItemBeans(items);
    }

    public ItemBasePageLoadResult<ForeignKeyBean> getForeignKeyList(PagingLoadConfig config, TypeModel model,
            String dataClusterPK, boolean ifFKFilter, String value) {
        return browserecordsServiceHandler.getForeignKeyList(config, model, dataClusterPK, ifFKFilter, value);
    }

    public List<Restriction> getForeignKeyPolymTypeList(String xpathForeignKey, String language) throws Exception {
        return browserecordsServiceHandler.getForeignKeyPolymTypeList(xpathForeignKey, language);
    }

    public ItemBean getItem(ItemBean itemBean, EntityModel entityModel) throws Exception {
        return browserecordsServiceHandler.getItem(itemBean, entityModel);
    }

    public ViewBean getView(String viewPk, String language) {
        return browserecordsServiceHandler.getView(viewPk, language);
    }

    public ItemResult logicalDeleteItem(ItemBean item, String path) {
        return browserecordsServiceHandler.logicalDeleteItem(item, path);
    }

    public List<ItemResult> logicalDeleteItems(List<ItemBean> items, String path) {
        return browserecordsServiceHandler.logicalDeleteItems(items, path);
    }

    public ItemBasePageLoadResult<ItemBean> queryItemBeans(QueryModel config) {
        return browserecordsServiceHandler.queryItemBeans(config);
    }

    public ItemResult saveItemBean(ItemBean item) {
        return browserecordsServiceHandler.saveItemBean(item);
    }

    public ForeignKeyDrawer switchForeignKeyType(String targetEntityType, String xpathForeignKey, String xpathInfoForeignKey,
            String fkFilter) throws Exception {
        return browserecordsServiceHandler.switchForeignKeyType(targetEntityType, xpathForeignKey, xpathInfoForeignKey, fkFilter);
    }

    public String getCriteriaByBookmark(String bookmark) {
        return browserecordsServiceHandler.getCriteriaByBookmark(bookmark);
    }

    public List<ItemBaseModel> getUserCriterias(String view) {
        return browserecordsServiceHandler.getUserCriterias(view);
    }

    public List<ItemBaseModel> getViewsList(String language) {
        return browserecordsServiceHandler.getViewsList(language);
    }

    public AppHeader getAppHeader() throws Exception {
        return browserecordsServiceHandler.getAppHeader();
    }

    public String getCurrentDataCluster() throws Exception {
        return browserecordsServiceHandler.getCurrentDataCluster();
    }

    public String getCurrentDataModel() throws Exception {
        return browserecordsServiceHandler.getCurrentDataModel();
    }

    public PagingLoadResult<ItemBaseModel> querySearchTemplates(String view, boolean isShared, PagingLoadConfig load) {
        return browserecordsServiceHandler.querySearchTemplates(view, isShared, load);
    }

    public String deleteSearchTemplate(String id) {
        return browserecordsServiceHandler.deleteSearchTemplate(id);
    }

    public boolean isExistCriteria(String dataObjectLabel, String id) {
        return browserecordsServiceHandler.isExistCriteria(dataObjectLabel, id);
    }

    public String saveCriteria(String viewPK, String templateName, boolean isShared, String criteriaString) {
        return browserecordsServiceHandler.saveCriteria(viewPK, templateName, isShared, criteriaString);
    }

}
