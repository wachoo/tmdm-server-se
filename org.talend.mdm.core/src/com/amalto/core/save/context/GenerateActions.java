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
import com.amalto.core.metadata.ComplexTypeMetadata;
import com.amalto.core.metadata.MetadataRepository;
import com.amalto.core.save.DocumentSaverContext;
import com.amalto.core.save.ReportDocumentSaverContext;
import com.amalto.core.save.SaverSession;
import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

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
            // Remove empty elements -> web ui sends empty elements (but do this only for creation).
            clean(userDocument.asDOM().getDocumentElement());
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

    private static void clean(Element element) {
        if (element == null) {
            return;
        }
        if (!isEmpty(element)) {
            NodeList children = element.getChildNodes();
            for (int i = children.getLength(); i >= 0; i--) {
                Node node = children.item(i);
                if (node instanceof Element) {
                    Element currentElement = (Element) node;
                    if (isEmpty(currentElement)) {
                        node.getParentNode().removeChild(node);
                    } else {
                        clean(currentElement);
                    }
                }
            }
        }
    }

    private static boolean isEmpty(Element element) {
        if (element == null) {
            return true;
        }

        NodeList children = element.getChildNodes();
        if (children.getLength() == 0) {
            return true;
        } else {
            for (int i = 0; i < children.getLength(); i++) {
                Node node = children.item(i);
                if (node instanceof Element && !isEmpty((Element) node)) {
                    return false;
                } else if (node instanceof Text) {
                    if (!node.getTextContent().trim().isEmpty()) {
                        return false;
                    }
                }
            }
            return true;
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
