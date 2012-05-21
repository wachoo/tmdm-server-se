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
import org.w3c.dom.*;

class ApplyActions implements DocumentSaver {

    private final DocumentSaver next;

    ApplyActions(DocumentSaver next) {
        this.next = next;
    }

    public void save(SaverSession session, DocumentSaverContext context) {
        MutableDocument databaseDocument = context.getDatabaseDocument();
        MutableDocument validationDocument = context.getDatabaseValidationDocument();
        for (Action action : context.getActions()) {
            action.perform(databaseDocument);
            action.perform(validationDocument);
        }

        // Never store empty elements in database
        clean(databaseDocument.asDOM().getDocumentElement());
        clean(validationDocument.asDOM().getDocumentElement());

        next.save(session, context);
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
        if (element.hasAttributes()) {
            // Returns true (isEmpty) if all attributes are empty.
            NamedNodeMap attributes = element.getAttributes();
            for (int i = 0; i < attributes.getLength(); i++) {
                String attributeValue = attributes.item(i).getNodeValue();
                if (attributeValue != null && !attributeValue.trim().isEmpty()) {
                    return false;
                }
            }
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
