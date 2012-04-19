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
import com.amalto.core.metadata.*;
import com.amalto.core.save.DocumentSaverContext;
import com.amalto.core.save.ReportDocumentSaverContext;
import com.amalto.core.save.SaverSession;
import org.apache.commons.lang.StringUtils;

import java.util.*;

class GenerateActions implements DocumentSaver {

    private final DocumentSaver next;

    GenerateActions(DocumentSaver next) {
        this.next = next;
    }

    public void save(SaverSession session, DocumentSaverContext context) {
        MutableDocument userDocument = context.getUserDocument();
        MutableDocument databaseDocument = context.getDatabaseDocument();
        if (databaseDocument == null) {
            throw new IllegalStateException("Database document is expected to be set.");
        }
        // Get source of modification (only if we're in the context of an update report).
        String source;
        if (context instanceof ReportDocumentSaverContext) {
            source = ((ReportDocumentSaverContext) context).getChangeSource();
        } else {
            source = StringUtils.EMPTY;
        }
        Date date = new Date(System.currentTimeMillis());
        SaverSource saverSource = session.getSaverSource();
        String userName = saverSource.getUserName();

        ComplexTypeMetadata type = context.getType();
        List<Action> actions;
        if (databaseDocument.asDOM().getDocumentElement() == null) {
            // This is a creation (database document is empty).
            Action createAction = new OverrideCreateAction(date, source, userName, userDocument, context.getType());
            // Generate field update actions for UUID and AutoIncrement elements.
            String universe = saverSource.getUniverse();
            CreateActions createActions = new CreateActions(date, source, userName, context.getDataCluster(), universe);
            List<Action> fieldActions = type.accept(createActions);
            // Builds action list (be sure to include actual creation as first action).
            actions = new LinkedList<Action>();
            actions.add(createAction);
            actions.addAll(fieldActions);
        } else {
            // get updated paths
            MetadataRepository metadataRepository = saverSource.getMetadataRepository(context.getDataModelName());
            UpdateActionCreator actionCreator = new UpdateActionCreator(databaseDocument, userDocument, source, userName, metadataRepository);
            try {
                actions = type.accept(actionCreator);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        context.setActions(actions);

        boolean hasModificationActions = hasModificationActions(actions);
        if (hasModificationActions) { // Ignore rest of save chain if there's no change to perform.
            next.save(session, context);
        }
    }

    private boolean hasModificationActions(List<Action> actions) {
        if (actions.isEmpty()) {
            return false;
        }
        for (Action action : actions) {
            if (!(action instanceof UpdateActionCreator.TouchAction)) {
                return true;
            }
        }
        return false;
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
