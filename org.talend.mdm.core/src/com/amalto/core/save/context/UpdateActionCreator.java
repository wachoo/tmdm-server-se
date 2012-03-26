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
import com.amalto.core.history.action.FieldUpdateAction;
import com.amalto.core.metadata.*;
import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.*;

class UpdateActionCreator extends DefaultMetadataVisitor<List<Action>> {

    private final Stack<String> path = new Stack<String>();

    private final List<Action> actions = new LinkedList<Action>();

    private Element currentOriginalElement;

    private Element currentNewElement;

    private final Date date;

    private final String source;

    private final String userName;

    public UpdateActionCreator(Document originalDocument, Document newDocument, String source, String userName) {
        currentOriginalElement = originalDocument.getDocumentElement();
        currentNewElement = newDocument.getDocumentElement();
        date = new Date(System.currentTimeMillis());
        this.source = source;
        this.userName = userName;
    }

    private String getPath(String fieldName) {
        if (path.isEmpty()) {
            return fieldName;
        } else {
            StringBuilder builder = new StringBuilder();
            Iterator<String> pathIterator = path.iterator();
            while (pathIterator.hasNext()) {
                builder.append(pathIterator.next());
                if (pathIterator.hasNext()) {
                    builder.append('/');
                }
            }
            return builder.append('/').append(fieldName).toString();
        }
    }

    @Override
    public List<Action> visit(ComplexTypeMetadata complexType) {
        super.visit(complexType);
        return actions;
    }

    @Override
    public List<Action> visit(ContainedTypeFieldMetadata containedField) {
        NodeList originalElementList = currentOriginalElement.getElementsByTagName(containedField.getName());
        NodeList newElementList = currentNewElement.getElementsByTagName(containedField.getName());

        if (originalElementList.getLength() != newElementList.getLength()) {
            // TODO This is done on purpose: we ignore updates with empty elements.
            if (newElementList.getLength() == 0) {
                return actions;
            }
        } else {
            if (originalElementList.getLength() == 0) { // And newElementList length is 0
                return actions;
            }
        }

        if (!containedField.isMany()) {
            path.add(containedField.getName());
            currentOriginalElement = (Element) originalElementList.item(0);
            currentNewElement = (Element) newElementList.item(0);

            super.visit(containedField);

            path.pop();
            currentOriginalElement = (Element) currentOriginalElement.getParentNode();
            currentNewElement = (Element) currentNewElement.getParentNode();
        } else {
            for (int i = 0; i < originalElementList.getLength(); i++) {
                // Path generation code is a bit duplicated (be careful)... and XPath indexes are 1-based (not 0-based).
                path.add(containedField.getName() + "[" + (i + 1) + "]");
                currentOriginalElement = (Element) originalElementList.item(i);
                currentNewElement = (Element) newElementList.item(i);

                super.visit(containedField);

                path.pop();
            }
            currentOriginalElement = (Element) currentOriginalElement.getParentNode();
            currentNewElement = (Element) currentNewElement.getParentNode();
        }
        return actions;
    }

    @Override
    public List<Action> visit(ReferenceFieldMetadata referenceField) {
        NodeList originalList = currentOriginalElement.getElementsByTagName(referenceField.getName());
        NodeList newList = currentNewElement.getElementsByTagName(referenceField.getName());

        if (!referenceField.isMany()) {
            handleUnaryField(referenceField, originalList, newList);
        } else {
            handleManyField(referenceField, originalList, newList);
        }
        return actions;
    }

    @Override
    public List<Action> visit(SimpleTypeFieldMetadata simpleField) {
        NodeList originalList = currentOriginalElement.getElementsByTagName(simpleField.getName());
        NodeList newList = currentNewElement.getElementsByTagName(simpleField.getName());

        if (!simpleField.isMany()) {
            handleUnaryField(simpleField, originalList, newList);
        } else {
            handleManyField(simpleField, originalList, newList);
        }
        return actions;
    }

    @Override
    public List<Action> visit(EnumerationFieldMetadata enumField) {
        NodeList originalList = currentOriginalElement.getElementsByTagName(enumField.getName());
        NodeList newList = currentNewElement.getElementsByTagName(enumField.getName());

        if (!enumField.isMany()) {
            handleUnaryField(enumField, originalList, newList);
        } else {
            handleManyField(enumField, originalList, newList);
        }
        return actions;
    }

    private void handleUnaryField(FieldMetadata simpleField, NodeList originalList, NodeList newList) {
        if (originalList.getLength() == newList.getLength()) {
            if (originalList.getLength() == 1) {
                String originalTextContent = originalList.item(0).getTextContent();
                String newTextContent = newList.item(0).getTextContent();

                if (!newTextContent.isEmpty()) {
                    if (!originalTextContent.equals(newTextContent)) {
                        actions.add(new FieldUpdateAction(date, source, userName, getPath(simpleField.getName()), originalTextContent, newTextContent));
                    }
                } else {
                    // TODO This is done on purpose: we ignore updates with empty elements.
                }
            }
        } else if (originalList.getLength() == 0) {
            String newTextContent = newList.item(0).getTextContent();
            actions.add(new FieldUpdateAction(date, source, userName, getPath(simpleField.getName()), StringUtils.EMPTY, newTextContent));
        } // TODO if (newList.getLength() == 0) --> user document may omit fields do not generate updates for this.
    }

    private void handleManyField(FieldMetadata manyField, NodeList originalList, NodeList newList) {
        // Path generation code is a bit duplicated (be careful)... and XPath indexes are 1-based (not 0-based).
        if (originalList.getLength() == newList.getLength()) {
            for (int i = 0; i < originalList.getLength(); i++) {
                String originalTextContent = originalList.item(i).getTextContent();
                String newTextContent = newList.item(i).getTextContent();

                if (!originalTextContent.equals(newTextContent)) {
                    String itemPath = getPath(manyField.getName()) + "[" + (i + 1) + "]";
                    actions.add(new FieldUpdateAction(date, source, userName, itemPath, originalTextContent, newTextContent));
                }
            }
        } else if (originalList.getLength() > newList.getLength()) {
            int i = 0;
            for (; i < newList.getLength(); i++) {
                String originalTextContent = originalList.item(i).getTextContent();
                String newTextContent = newList.item(i).getTextContent();

                if (!originalTextContent.equals(newTextContent)) {
                    String itemPath = getPath(manyField.getName()) + "[" + (i + 1) + "]";
                    actions.add(new FieldUpdateAction(date, source, userName, itemPath, originalTextContent, newTextContent));
                }
            }
            for (; i < originalList.getLength(); i++) {
                String originalTextContent = originalList.item(i).getTextContent();
                String itemPath = getPath(manyField.getName()) + "[" + (i + 1) + "]";
                actions.add(new FieldUpdateAction(date, source, userName, itemPath, originalTextContent, StringUtils.EMPTY));
            }
        } else { // originalList.getLength() < newList.getLength() 
            int i = 0;
            for (; i < originalList.getLength(); i++) {
                String originalTextContent = originalList.item(i).getTextContent();
                String newTextContent = newList.item(i).getTextContent();

                if (!originalTextContent.equals(newTextContent)) {
                    String itemPath = getPath(manyField.getName()) + "[" + (i + 1) + "]";
                    actions.add(new FieldUpdateAction(date, source, userName, itemPath, originalTextContent, newTextContent));
                }
            }
            for (; i < newList.getLength(); i++) {
                String newListContent = newList.item(i).getTextContent();
                String itemPath = getPath(manyField.getName()) + "[" + (i + 1) + "]";
                actions.add(new FieldUpdateAction(date, source, userName, itemPath, StringUtils.EMPTY, newListContent));
            }
        }
    }

}
