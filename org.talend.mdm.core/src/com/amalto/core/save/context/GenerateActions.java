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
import com.amalto.core.history.action.CreateAction;
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
        final MutableDocument userDocument = context.getUserDocument();
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
        String userName = session.getSaverSource().getUserName();

        List<Action> actions;
        if (databaseDocument.asDOM().getDocumentElement() == null) {
            Action action = new CreateAction(date, source, userName) {
                @Override
                public MutableDocument perform(MutableDocument document) {
                    document.create(userDocument);
                    return document;
                }
            };
            actions = Collections.singletonList(action);
        } else {
            // get updated paths
            ComplexTypeMetadata type = context.getType();
            UpdateActionCreator actionCreator = new UpdateActionCreator(databaseDocument.asDOM(), userDocument.asDOM(), source, userName);
            actions = type.accept(actionCreator);
        }
        context.setActions(actions);

        if (!actions.isEmpty()) { // Ignore rest of save chain if there's no change to perform.
            next.save(session, context);
        }
    }

    public String[] getSavedId() {
        return next.getSavedId();
    }

    public String getSavedConceptName() {
        return next.getSavedConceptName();
    }

}
