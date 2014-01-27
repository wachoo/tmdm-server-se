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

import com.amalto.core.history.Action;
import com.amalto.core.history.MutableDocument;
import com.amalto.core.save.DocumentSaverContext;
import com.amalto.core.save.SaverSession;
import org.apache.log4j.Logger;

class ApplyActions implements DocumentSaver {

    private static final Logger LOGGER = Logger.getLogger(ApplyActions.class);

    private final DocumentSaver next;

    ApplyActions(DocumentSaver next) {
        this.next = next;
    }

    public void save(SaverSession session, DocumentSaverContext context) {
        MutableDocument databaseDocument = context.getDatabaseDocument();
        for (Action action : context.getActions()) {
            if (LOGGER.isTraceEnabled()) {
                StringBuilder builder = new StringBuilder();
                String[] ids = context.getId();
                for (String id : ids) {
                    builder.append(' ').append(id);
                }
                LOGGER.trace("Actions for record of type '" + context.getDatabaseDocument().getType() + "' (id:" + builder + ")");
                LOGGER.trace("   " + action);
            }
            action.perform(databaseDocument);
        }
        databaseDocument.clean();
        next.save(session, context);
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
