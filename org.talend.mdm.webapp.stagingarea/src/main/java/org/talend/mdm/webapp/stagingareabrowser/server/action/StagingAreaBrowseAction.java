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
package org.talend.mdm.webapp.stagingareabrowser.server.action;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.talend.mdm.webapp.base.client.exception.ServiceException;
import org.talend.mdm.webapp.base.server.util.CommonUtil;
import org.talend.mdm.webapp.stagingareabrowser.client.StagingAreaBrowseService;
import org.talend.mdm.webapp.stagingareabrowser.client.model.FakeData;
import org.talend.mdm.webapp.stagingareabrowser.client.model.ResultItem;
import org.talend.mdm.webapp.stagingareabrowser.client.model.SearchModel;

import com.amalto.webapp.core.bean.Configuration;
import com.amalto.webapp.util.webservices.WSDataModelPK;
import com.amalto.webapp.util.webservices.WSGetBusinessConcepts;
import com.extjs.gxt.ui.client.data.BaseModel;
import com.extjs.gxt.ui.client.data.BasePagingLoadResult;
import com.extjs.gxt.ui.client.data.PagingLoadResult;

public class StagingAreaBrowseAction implements StagingAreaBrowseService {

    private static final Logger LOG = Logger.getLogger(StagingAreaBrowseAction.class);

    public List<BaseModel> getConcepts(String language) throws ServiceException {
        try {
            String model = getCurrentDataModel();
            String[] businessConcept = CommonUtil.getPort().getBusinessConcepts(
                    new WSGetBusinessConcepts(new WSDataModelPK(model))).getStrings();
            List<BaseModel> conceptModels = new ArrayList<BaseModel>();
            for (String concept : businessConcept) {
                BaseModel conceptModel = new BaseModel();
                conceptModel.set("name", concept); //$NON-NLS-1$
                conceptModel.set("value", concept); //$NON-NLS-1$
                conceptModels.add(conceptModel);
            }
            return conceptModels;
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            throw new ServiceException(e.getLocalizedMessage());
        }
    }

    public PagingLoadResult<ResultItem> searchStaging(SearchModel searchModel) {

        // TODO wait for confirmation logic

        // WSStringArray results = null;
        // try {
        // WSDataClusterPK wsDataClusterPK = new WSDataClusterPK(getCurrentDataCluster());
        // WSStringArray viewablePaths = new WSStringArray(new String[] { "Pro/subelement" });
        //
        // WSWhereItem whereItem = null;
        // // whereItem.setWhereCondition(new WSWhereCondition(searchModel.getEntity() + "/$staging_status$",
        // // WSWhereOperator.GREATER_THAN_OR_EQUAL, "400", WSStringPredicate.NONE, false));
        //
        // WSXPathsSearch wsXPathsSearch = new WSXPathsSearch(wsDataClusterPK, null, viewablePaths, whereItem, -1,
        // searchModel
        // .getOffset(), searchModel.getLimit(), searchModel.getSortField(), searchModel.getSortDir(), true);
        //
        //
        // results = CommonUtil.getPort().xPathsSearch(wsXPathsSearch);
        //
        // } catch (Exception e) {
        // LOG.error(e.getMessage());
        // }

        int offset = searchModel.getOffset();
        PagingLoadResult<ResultItem> result = new BasePagingLoadResult<ResultItem>(FakeData.getResults(offset, offset
                + searchModel.getLimit()), offset, FakeData.getTotal());
        return result;
    }

    public String getCurrentDataModel() throws ServiceException {
        try {
            Configuration config = Configuration.getConfiguration();
            return config.getModel();
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            throw new ServiceException(e.getLocalizedMessage());
        }
    }

    public String getCurrentDataCluster() throws ServiceException {
        try {
            Configuration config = Configuration.getConfiguration();
            return config.getCluster() + "#STAGING"; //$NON-NLS-1$
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            throw new ServiceException(e.getLocalizedMessage());
        }
    }
}
