/*
 * Copyright (C) 2006-2012 Talend Inc. - www.talend.com
 *
 * This source code is available under agreement available at
 * %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
 *
 * You should have received a copy of the agreement
 * along with this program; if not, write to Talend SA
 * 9 rue Pages 92150 Suresnes, France
 */

package com.amalto.core.save.context;

import com.amalto.core.ejb.ItemPOJO;
import com.amalto.core.objects.universe.ejb.UniversePOJO;
import com.amalto.core.save.DocumentSaverContext;
import com.amalto.core.save.SaverSession;
import com.amalto.core.util.AutoIncrementGenerator;
import com.amalto.core.util.LocalUser;
import com.amalto.core.util.Util;
import com.amalto.core.util.XtentisException;
import org.apache.log4j.Logger;
import org.talend.mdm.commmon.util.webapp.XObjectType;
import org.talend.mdm.commmon.util.webapp.XSystemObjects;

public class Checks implements DocumentSaver {

    public static final Logger LOGGER = Logger.getLogger(Checks.class);

    private final DocumentSaver next;

    Checks(DocumentSaver next) {
        this.next = next;
    }

    public void save(SaverSession session, DocumentSaverContext context) {
        SaverSource saverSource = context.getSaverSource();
        String dataClusterName = context.getDataCluster();
        String concept = context.getType().getName();
        String dataModelName = context.getDataModelName();

        // check cluster exist or not
        if (!XSystemObjects.isExist(XObjectType.DATA_CLUSTER, dataClusterName)) {
            // get the universe and revision ID
            String universe = saverSource.getUniverse();
            if (universe == null) {
                String err = "ERROR: no Universe set for user '" + saverSource.getUserName() + "'";
                LOGGER.error(err);
                throw new RuntimeException(err);
            }

            String revisionID = saverSource.getConceptRevisionID(concept);
            context.setRevisionId(revisionID);
            if (!saverSource.existCluster(revisionID, dataClusterName)) {
                throw new RuntimeException("DataContainer R-" + revisionID + "/" + dataClusterName + " doesn't exists!");
            }
        }

        // Continue save
        next.save(session, context);

        if (XSystemObjects.DC_PROVISIONING.getName().equals(dataModelName) && context.getId()[0].equals(saverSource.getUserName())) {
            saverSource.resetLocalUsers();
        }

        // reset the AutoIncrement
        if (("AutoIncrement".equals(concept) && XSystemObjects.DC_CONF.getName().equals(dataModelName))) { //$NON-NLS-1$
            saverSource.initAutoIncrement();
        }
    }

    public String[] getSavedId() {
        return next.getSavedId();
    }

    public String getSavedConceptName() {
        return next.getSavedConceptName();
    }
}
