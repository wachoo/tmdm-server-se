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
package org.talend.mdm.webapp.stagingareacontrol.server.actions;

import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.talend.mdm.webapp.base.client.exception.ServiceException;
import org.talend.mdm.webapp.base.server.util.CommonUtil;
import org.talend.mdm.webapp.stagingareacontrol.client.StagingAreaService;
import org.talend.mdm.webapp.stagingareacontrol.client.model.ConceptRelationshipModel;
import org.talend.mdm.webapp.stagingareacontrol.client.model.StagingAreaConfiguration;

import com.amalto.webapp.core.bean.Configuration;
import com.amalto.webapp.util.webservices.WSConceptRelationship;

/**
 * DOC suplch  class global comment. Detailled comment
 */
public class StagingAreaAction implements StagingAreaService {

    private static final Logger LOG = Logger.getLogger(StagingAreaAction.class);

    private static final String STAGING_AREA_PROPERTIES = "/stagingarea.properties"; //$NON-NLS-1$

    private static final int DEFAULT_REFRESH_INTERVALS = 1000;

    public StagingAreaConfiguration getStagingAreaConfig() {
        StagingAreaConfiguration cm = new StagingAreaConfiguration();
        try {
            InputStream is = StagingAreaAction.class.getResourceAsStream(STAGING_AREA_PROPERTIES);
            Properties prop = new Properties();
            prop.load(is);
            String refreshIntervals = prop.getProperty("refresh_intervals"); //$NON-NLS-1$
            if (refreshIntervals != null) {
                cm.setRefreshIntervals(Integer.parseInt(refreshIntervals));
            } else {
                cm.setRefreshIntervals(DEFAULT_REFRESH_INTERVALS);
            }
        } catch (Exception e) {
            LOG.error(e.getMessage());
        }
        return cm;
    }

    @Override
    public ConceptRelationshipModel getConceptRelation() throws ServiceException {
        try {
            Configuration config = Configuration.getConfiguration();
            WSConceptRelationship relationship = CommonUtil.getPort().getConceptRelation(config.getCluster());
            return new ConceptRelationshipModel(relationship.getConcepts(), relationship.getRelationShipMap());
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            throw new ServiceException(e.getLocalizedMessage());
        }
    }

}
