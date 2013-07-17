// ============================================================================
//
// Copyright (C) 2006-2013 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.mdm.webapp.browserecordsinstaging.server.actions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.talend.mdm.webapp.base.client.exception.ServiceException;
import org.talend.mdm.webapp.base.client.model.ItemBasePageLoadResult;
import org.talend.mdm.webapp.browserecords.client.model.ItemBean;
import org.talend.mdm.webapp.browserecords.client.model.QueryModel;
import org.talend.mdm.webapp.browserecords.server.actions.BrowseRecordsAction;
import org.talend.mdm.webapp.browserecords.shared.FKIntegrityResult;
import org.talend.mdm.webapp.browserecordsinstaging.client.BrowseRecordsInStagingService;

import com.amalto.core.server.StorageAdmin;

public class BrowseRecordsInStagingAction extends BrowseRecordsAction implements BrowseRecordsInStagingService {

    private final Logger LOG = Logger.getLogger(BrowseRecordsInStagingAction.class);

    @Override
    public ItemBasePageLoadResult<ItemBean> queryItemBeans(QueryModel config, String language) throws ServiceException {
        if (!config.getDataClusterPK().endsWith(StorageAdmin.STAGING_SUFFIX)) {
            config.setDataClusterPK(config.getDataClusterPK() + StorageAdmin.STAGING_SUFFIX);
        }
        return super.queryItemBeans(config, language);
    }

    @Override
    public String getCurrentDataCluster() throws ServiceException {
        String cluster = super.getCurrentDataCluster();
        if (!cluster.endsWith(StorageAdmin.STAGING_SUFFIX)) {
            cluster += StorageAdmin.STAGING_SUFFIX;
        }
        return cluster;
    }

    @Override
    public Map<ItemBean, FKIntegrityResult> checkFKIntegrity(List<ItemBean> selectedItems) throws ServiceException {
        try {
            Map<ItemBean, FKIntegrityResult> itemBeanToResult = new HashMap<ItemBean, FKIntegrityResult>(selectedItems.size());
            for (ItemBean selectedItem : selectedItems) {
                itemBeanToResult.put(selectedItem, FKIntegrityResult.ALLOWED);
            }
            return itemBeanToResult;
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            throw new ServiceException(e.getLocalizedMessage());
        }
    }

}
