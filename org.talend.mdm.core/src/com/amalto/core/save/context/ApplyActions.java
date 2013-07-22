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

import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.amalto.core.history.Action;
import com.amalto.core.history.MutableDocument;
import com.amalto.core.save.DocumentSaverContext;
import com.amalto.core.save.SaverSession;
import com.amalto.core.schema.validation.SkipAttributeDocumentBuilder;

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
        clean(databaseDocument.asDOM().getDocumentElement(), EmptyElementCleaner.INSTANCE, false);
        clean(validationDocument.asDOM().getDocumentElement(), EmptyElementCleaner.INSTANCE, true);
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

    private void clean(Element element, Cleaner cleaner, boolean removeTalendAttributes) {
        if (element == null) {
            return;
        }
        if (removeTalendAttributes) {
            element.removeAttributeNS(SkipAttributeDocumentBuilder.TALEND_NAMESPACE, "type"); //$NON-NLS-1$
        }
        Node current = element.getLastChild();
        while (current != null) {
            Node next = current.getPreviousSibling();
            if (current.getNodeType() == Node.ELEMENT_NODE) {
                Element currentElement = (Element) current;
                clean(currentElement, cleaner, removeTalendAttributes);
            }
            current = next;
        }
        if (cleaner.clean(element)) {
            element.getParentNode().removeChild(element);
        }
    }

    interface Cleaner {
        /**
         * Indicates to the caller whether <code>element</code> should be deleted or not.<br/>
         *
         * @param element An element to clean
         * @return <code>true</code> if element should be removed by caller, <code>false</code> otherwise.
         */
        boolean clean(Element element);
    }

    private static class EmptyElementCleaner implements Cleaner {

        static Cleaner INSTANCE = new EmptyElementCleaner();

        public boolean clean(Element element) {
            if (element == null) {
                return true;
            }
            if (element.hasChildNodes()) {
                return false;
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
            }
            return true;
        }
    }
}
