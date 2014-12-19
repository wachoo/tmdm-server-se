/*
 * Copyright (C) 2006-2014 Talend Inc. - www.talend.com
 *
 * This source code is available under agreement available at
 * %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
 *
 * You should have received a copy of the agreement
 * along with this program; if not, write to Talend SA
 * 9 rue Pages 92150 Suresnes, France
 */

package com.amalto.core.save.context;

import org.apache.log4j.Logger;
import org.talend.mdm.commmon.util.webapp.XObjectType;
import org.talend.mdm.commmon.util.webapp.XSystemObjects;

import org.talend.mdm.commmon.metadata.ComplexTypeMetadata;
import com.amalto.core.save.DocumentSaverContext;
import com.amalto.core.save.SaverSession;
import com.amalto.core.util.ValidateException;

class Init implements DocumentSaver {

    private static final Logger LOGGER = Logger.getLogger(Init.class);

    private final DocumentSaver next;

    Init(DocumentSaver next) {
        this.next = next;
    }

    public void save(SaverSession session, DocumentSaverContext context) {
        SaverSource saverSource = session.getSaverSource();
        String dataClusterName = context.getDataCluster();
        String dataModelName = context.getDataModelName();
        // Get type name
        ComplexTypeMetadata type = context.getUserDocument().getType();
        // check cluster exist or not
        if (!XSystemObjects.isExist(XObjectType.DATA_CLUSTER, dataClusterName)) {
            // get the universe and revision ID
            if (!saverSource.existCluster(dataClusterName)) {
                throw new RuntimeException("Data container '" + dataClusterName + "' does not exist.");
            }
        }
        // Start a transaction on data container
        session.begin(dataClusterName);
        // Continue save
        try {
            next.save(session, context);
        } catch (Exception e) {
            // TMDM-3638: Don't log error if there's a before saving error.
            if (LOGGER.isDebugEnabled()) {
                if (getBeforeSavingMessage().isEmpty()) {
                    LOGGER.debug("Exception occurred during save.", e);
                }
            }
            if (e instanceof ValidateException) {
                // In case of validation issue, don't include a potential before saving message in exception.
                throw new com.amalto.core.save.SaveException(e);
            }
            throw new com.amalto.core.save.SaveException(getBeforeSavingMessage(), e);
        }
        // Reset user cache if save applied to a user.
        if (XSystemObjects.DC_PROVISIONING.getName().equals(dataModelName)) {
            saverSource.resetLocalUsers();
        }
    }

    public String[] getSavedId() {
        return next.getSavedId();
    }

    public String getSavedConceptName() {
        return next.getSavedConceptName();
    }

    public String getBeforeSavingMessage() {
        return next.getBeforeSavingMessage();
    }
}
