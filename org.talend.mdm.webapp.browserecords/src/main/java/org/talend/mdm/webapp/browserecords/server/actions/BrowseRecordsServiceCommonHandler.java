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

import org.talend.mdm.webapp.browserecords.client.model.ForeignKeyBean;
import org.talend.mdm.webapp.browserecords.client.model.ForeignKeyDrawer;
import org.talend.mdm.webapp.browserecords.client.model.ItemBaseModel;
import org.talend.mdm.webapp.browserecords.client.model.ItemBasePageLoadResult;
import org.talend.mdm.webapp.browserecords.client.model.ItemBean;
import org.talend.mdm.webapp.browserecords.client.model.ItemResult;
import org.talend.mdm.webapp.browserecords.client.model.QueryModel;
import org.talend.mdm.webapp.browserecords.client.model.Restriction;
import org.talend.mdm.webapp.browserecords.server.BrowseRecordsConfiguration;
import org.talend.mdm.webapp.browserecords.shared.AppHeader;
import org.talend.mdm.webapp.browserecords.shared.EntityModel;
import org.talend.mdm.webapp.browserecords.shared.TypeModel;
import org.talend.mdm.webapp.browserecords.shared.ViewBean;

import com.extjs.gxt.ui.client.data.PagingLoadConfig;

/**
 * DOC Administrator  class global comment. Detailled comment
 */
public class BrowseRecordsServiceCommonHandler extends BrowseRecordsAction {

    public ItemResult deleteItemBean(ItemBean item) {
        return null;
    }

    public List<ItemResult> deleteItemBeans(List<ItemBean> items) {
        return null;
    }

    public ItemBasePageLoadResult<ForeignKeyBean> getForeignKeyList(PagingLoadConfig config, TypeModel model,
            String dataClusterPK, boolean ifFKFilter, String value) {
        return null;
    }

    public List<Restriction> getForeignKeyPolymTypeList(String xpathForeignKey, String language) throws Exception {
        return null;
    }

    public ItemBean getItem(ItemBean itemBean, EntityModel entityModel) throws Exception {
        return null;
    }

    public ViewBean getView(String viewPk, String language) {
        return null;
    }

    public ItemResult logicalDeleteItem(ItemBean item, String path) {
        return null;
    }

    public List<ItemResult> logicalDeleteItems(List<ItemBean> items, String path) {
        return null;
    }

    public ItemBasePageLoadResult<ItemBean> queryItemBeans(QueryModel config) {
        return null;
    }

    public ItemResult saveItemBean(ItemBean item) {
        return null;
    }

    public ForeignKeyDrawer switchForeignKeyType(String targetEntityType, String xpathForeignKey, String xpathInfoForeignKey,
            String fkFilter) throws Exception {
        return null;
    }

    public String getCriteriaByBookmark(String bookmark) {
        return null;
    }

    public List<ItemBaseModel> getUserCriterias(String view) {
        return null;
    }

    public List<ItemBaseModel> getViewsList(String language) {
        return null;
    }

    public AppHeader getAppHeader() throws Exception {

        AppHeader header = new AppHeader();
        header.setDatacluster(getCurrentDataCluster());
        header.setDatamodel(getCurrentDataModel());
        header.setStandAloneMode(BrowseRecordsConfiguration.isStandalone());
        return header;

    }

}
