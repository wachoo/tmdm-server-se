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
package org.talend.mdm.webapp.stagingarea.server.actions;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.talend.mdm.commmon.util.webapp.XObjectType;
import org.talend.mdm.commmon.util.webapp.XSystemObjects;
import org.talend.mdm.webapp.stagingarea.client.StagingAreaService;
import org.talend.mdm.webapp.stagingarea.client.model.ContextModel;

import com.amalto.webapp.core.util.Util;
import com.amalto.webapp.util.webservices.WSDataClusterPK;
import com.amalto.webapp.util.webservices.WSDataModelPK;
import com.amalto.webapp.util.webservices.WSRegexDataClusterPKs;
import com.amalto.webapp.util.webservices.WSRegexDataModelPKs;

/**
 * DOC suplch  class global comment. Detailled comment
 */
public class StagingAreaAction implements StagingAreaService {

    private static final Logger LOG = Logger.getLogger(StagingAreaAction.class);

    private static final String STAGING_AREA_PROPERTIES = "/stagingarea.properties"; //$NON-NLS-1$

    public ContextModel getContextModel() {
        ContextModel cm = new ContextModel();
        try {
            cm.setDataContainer(getClusters());
            cm.setDataModels(getModels());

            InputStream is = StagingAreaAction.class.getResourceAsStream(STAGING_AREA_PROPERTIES);
            Properties prop = new Properties();
            prop.load(is);
            String refreshIntervals = prop.getProperty("refresh_intervals"); //$NON-NLS-1$
            if (refreshIntervals != null) {
                cm.setRefreshIntervals(Integer.parseInt(refreshIntervals));
            }

        } catch (Exception e) {
            LOG.error(e.getMessage());
        }
        return cm;
    }

    private List<String> getClusters() throws Exception {
        List<String> clusters = new ArrayList<String>();

        WSDataClusterPK[] wsDataClustersPKs = Util.getPort().getDataClusterPKs(new WSRegexDataClusterPKs("*") //$NON-NLS-1$
                ).getWsDataClusterPKs();
        Map<String, XSystemObjects> xDataClustersMap = XSystemObjects.getXSystemObjects(XObjectType.DATA_CLUSTER);
        for (int i = 0; i < wsDataClustersPKs.length; i++) {
            if (!XSystemObjects.isXSystemObject(xDataClustersMap, XObjectType.DATA_CLUSTER, wsDataClustersPKs[i].getPk())) {
                clusters.add(wsDataClustersPKs[i].getPk());
            }
        }

        return clusters;
    }

    private List<String> getModels() throws Exception {
        List<String> models = new ArrayList<String>();
        WSDataModelPK[] wsDataModelsPKs = Util.getPort().getDataModelPKs(new WSRegexDataModelPKs("*") //$NON-NLS-1$
                ).getWsDataModelPKs();

        Map<String, XSystemObjects> xDataModelsMap = XSystemObjects.getXSystemObjects(XObjectType.DATA_MODEL);
        for (int i = 0; i < wsDataModelsPKs.length; i++) {
            if (!XSystemObjects.isXSystemObject(xDataModelsMap, XObjectType.DATA_MODEL, wsDataModelsPKs[i].getPk())) {
                models.add(wsDataModelsPKs[i].getPk());
            }
        }

        return models;
    }
}
