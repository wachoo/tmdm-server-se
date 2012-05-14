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

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.amalto.core.history.Action;
import com.amalto.core.history.MutableDocument;
import com.amalto.core.metadata.ComplexTypeMetadata;
import com.amalto.core.metadata.MetadataRepository;
import com.amalto.core.save.DocumentSaverContext;
import com.amalto.core.save.ReportDocumentSaverContext;
import com.amalto.core.save.SaverSession;

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
        String universe = saverSource.getUniverse();
        List<Action> actions;
        MetadataRepository metadataRepository = saverSource.getMetadataRepository(context.getDataModelName());
        if (context.isCreate()) {
            Action createAction = new OverrideCreateAction(date, source, userName, userDocument, context.getType());
            // Generate field update actions for UUID and AutoIncrement elements.
            CreateActions createActions = new CreateActions(date, source, userName, context.getDataCluster(), universe, saverSource);
            UpdateActionCreator updateActions = new UpdateActionCreator(databaseDocument, userDocument, source, userName, metadataRepository);

            // Builds action list (be sure to include actual creation as first action).
            actions = new LinkedList<Action>();
            actions.add(createAction);
            actions.addAll(type.accept(createActions));
            actions.addAll(type.accept(updateActions));

            context.setHasMetAutoIncrement(createActions.hasMetAutoIncrement());
            List<String> idValues = createActions.getIdValues();
            // TODO This does not guarantee key values are in correct order with key fields (for cases where ID is composed of mixed AUTO_INCREMENT and user fields).
            // Join ids read from XML document and generated ID values.
            String[] joinIds = new String[context.getId().length + idValues.size()];
            System.arraycopy(context.getId(), 0, joinIds, 0, context.getId().length);
            System.arraycopy(idValues.toArray(new String[idValues.size()]), 0, joinIds, context.getId().length, idValues.size());
            context.setId(joinIds);
        } else {
            if (!context.isReplace()) { // "Is update"
                // get updated paths
                UpdateActionCreator actionCreator = new UpdateActionCreator(databaseDocument, userDocument, source, userName, metadataRepository);
                actions = type.accept(actionCreator);
            } else { // "Is replace" (similar to creation but without clean up of empty elements).
                UpdateActionCreator updateActions = new UpdateActionCreator(databaseDocument, userDocument, source, userName, metadataRepository);
                // Builds action list (be sure to include actual creation as first action).
                actions = new LinkedList<Action>();
                Action createAction = new OverrideReplaceAction(date, source, userName, userDocument, context.getType());
                CreateActions createActions = new CreateActions(date, source, userName, context.getDataCluster(), universe, saverSource);
                actions.add(createAction);
                actions.addAll(type.accept(createActions));
                actions.addAll(type.accept(updateActions));
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
