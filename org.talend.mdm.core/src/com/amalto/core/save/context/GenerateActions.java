/*
 * Copyright (C) 2006-2012 Talend Inc. - www.talend.com
 * 
 * This source code is available under agreement available at
 * %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
 * 
 * You should have received a copy of the agreement along with this program; if not, write to Talend SA 9 rue Pages
 * 92150 Suresnes, France
 */

package com.amalto.core.save.context;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.NotImplementedException;
import org.apache.commons.lang.StringUtils;

import com.amalto.core.history.Action;
import com.amalto.core.history.MutableDocument;
import com.amalto.core.metadata.ComplexTypeMetadata;
import com.amalto.core.metadata.MetadataRepository;
import com.amalto.core.save.DocumentSaverContext;
import com.amalto.core.save.ReportDocumentSaverContext;
import com.amalto.core.save.SaverSession;
import com.amalto.core.save.UserAction;

class GenerateActions implements DocumentSaver {

    private final DocumentSaver next;

    GenerateActions(DocumentSaver next) {
        this.next = next;
    }

    @Override
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
        // Generate field update actions for UUID and AutoIncrement elements.
        UpdateActionCreator updateActions = new UpdateActionCreator(databaseDocument, userDocument,
                context.preserveOldCollectionValues(), source, userName, metadataRepository);
        UserAction userAction = context.getUserAction();
        switch (userAction) {
        case CREATE:
            CreateActions createActions = new CreateActions(userDocument, date, source, userName, context.getDataCluster(),
                    universe, saverSource, context.getAutoIncrementFieldMap());
            Action createAction = new OverrideCreateAction(date, source, userName, userDocument, context.getType());
            // Builds action list (be sure to include actual creation as first action).
            actions = new LinkedList<Action>();
            actions.add(createAction);
            actions.addAll(type.accept(createActions));
            actions.addAll(type.accept(updateActions));
            context.setHasMetAutoIncrement(createActions.hasMetAutoIncrement());
            List<String> idValues = createActions.getIdValues();
            // TODO This does not guarantee key values are in correct order with key fields (for cases where ID is
            // composed of mixed AUTO_INCREMENT and user fields).
            // Join ids read from XML document and generated ID values.
            String[] joinIds = new String[context.getId().length + idValues.size()];
            System.arraycopy(context.getId(), 0, joinIds, 0, context.getId().length);
            System.arraycopy(idValues.toArray(new String[idValues.size()]), 0, joinIds, context.getId().length, idValues.size());
            context.setId(joinIds);
            break;
        case UPDATE:
            // get updated paths
            actions = type.accept(updateActions);
            break;
        case REPLACE:
            // "Is replace" (similar to creation but without clean up of empty elements).
            // Builds action list (be sure to include actual creation as first action).
            actions = new LinkedList<Action>();
            Action replaceAction = new OverrideReplaceAction(date, source, userName, userDocument, context.getType());
            actions.add(replaceAction);
            actions.addAll(type.accept(updateActions));
            break;
        case PARTIAL_UPDATE:
            PartialUpdateActionCreator partialUpdateActionCreator = new PartialUpdateActionCreator(databaseDocument,
                    userDocument, context.preserveOldCollectionValues(), context.getPartialUpdatePivot(),
                    context.getPartialUpdateKey(), source, userName, metadataRepository);
            actions = type.accept(partialUpdateActionCreator);
            break;
        default:
            throw new NotImplementedException("Support for '" + userAction + "'.");
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
            if (!(action instanceof TouchAction)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String[] getSavedId() {
        return next.getSavedId();
    }

    @Override
    public String getSavedConceptName() {
        return next.getSavedConceptName();
    }

    @Override
    public String getBeforeSavingMessage() {
        return next.getBeforeSavingMessage();
    }

}
