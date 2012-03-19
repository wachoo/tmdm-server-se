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
import com.amalto.core.history.action.FieldUpdateAction;
import com.amalto.core.save.DocumentSaverContext;
import com.amalto.core.save.SaverSession;
import com.amalto.core.util.UpdateReportItem;
import com.amalto.core.util.Util;
import org.w3c.dom.Element;

import java.util.*;

class GenerateActions implements DocumentSaver {

    private final DocumentSaver next;

    GenerateActions(DocumentSaver next) {
        this.next = next;
    }

    public void save(SaverSession session, DocumentSaverContext context) {
        final MutableDocument userDocument = context.getUserDocument();
        Element userDocumentElement = userDocument.asDOM().getDocumentElement();
        MutableDocument databaseDocument = context.getDatabaseDocument();
        List<Action> result;
        Date date = new Date(System.currentTimeMillis());
        String source = "";
        String userName = "";

        if (databaseDocument.asDOM().getDocumentElement() == null) {
            Action action = new CreateAction(date, source, userName) {
                @Override
                public MutableDocument perform(MutableDocument document) {
                    document.create(userDocument);
                    return document;
                }
            };
            result = Collections.singletonList(action);
        } else {
            // get updated path
            Element databaseElement = databaseDocument.asDOM().getDocumentElement();
            List<Action> updateActions = new LinkedList<Action>();
            try {
                // TODO Not sure the implementation of this is really efficient (to be improved).
                Map<String, UpdateReportItem> updatedPath = Util.compareElement("/" + databaseElement.getLocalName(), userDocumentElement, databaseElement);//$NON-NLS-1$
                for (Map.Entry<String, UpdateReportItem> updatedElement : updatedPath.entrySet()) {
                    UpdateReportItem reportItem = updatedElement.getValue();
                    updateActions.add(new FieldUpdateAction(date, source, userName, reportItem.getPath(), reportItem.getOldValue(), reportItem.getNewValue()));
                }
                result = updateActions;
            } catch (Exception e) {
                throw new RuntimeException("Unable to generate update report.", e);
            }
        }
        context.setActions(result);
        
        next.save(session, context);
    }

    public String[] getSavedId() {
        return next.getSavedId();
    }
}
