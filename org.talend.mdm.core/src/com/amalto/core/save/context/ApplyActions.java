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
import com.amalto.core.save.UserAction;
import com.amalto.core.schema.validation.SkipAttributeDocumentBuilder;
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
        clean(databaseDocument.asDOM().getDocumentElement(), EmptyElementCleaner.INSTANCE);
        clean(validationDocument.asDOM().getDocumentElement(), EmptyElementCleaner.INSTANCE);

        if (context.getUserAction() == UserAction.CREATE || context.getUserAction() == UserAction.REPLACE) {
            // See TMDM-4038
            clean(validationDocument.asDOM().getDocumentElement(), TechnicalAttributeCleaner.INSTANCE);
        }
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

    private static void clean(Element element, Cleaner cleaner) {
        if (element == null) {
            return;
        }
        if (!cleaner.clean(element)) {
            NodeList children = element.getChildNodes();
            for (int i = children.getLength(); i >= 0; i--) {
                Node node = children.item(i);
                if (node instanceof Element) {
                    Element currentElement = (Element) node;
                    if (cleaner.clean(currentElement)) {
                        node.getParentNode().removeChild(node);
                    } else {
                        clean(currentElement, cleaner);
                    }
                }
            }
        }
    }

    interface Cleaner {
        /**
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
                    if (node instanceof Element && !clean((Element) node)) {
                        return false;
                    } else if (node instanceof Text) {
                        if (!node.getNodeValue().trim().isEmpty()) {
                            return false;
                        }
                    }
                }
                return true;
            }
        }
    }

    private static class TechnicalAttributeCleaner implements Cleaner {
        static Cleaner INSTANCE = new TechnicalAttributeCleaner();

        public boolean clean(Element element) {
            element.removeAttributeNS(SkipAttributeDocumentBuilder.TALEND_NAMESPACE, "type"); //$NON-NLS-1$
            return false;
        }
    }
}
