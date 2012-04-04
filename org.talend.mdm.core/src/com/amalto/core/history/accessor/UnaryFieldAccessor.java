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

package com.amalto.core.history.accessor;

import com.amalto.core.history.MutableDocument;
import org.w3c.dom.*;

/**
 *
 */
class UnaryFieldAccessor implements DOMAccessor {

    private final DOMAccessor parent;

    private final String fieldName;

    private final MutableDocument document;

    public UnaryFieldAccessor(DOMAccessor parent, String fieldName, MutableDocument document) {
        this.parent = parent;
        this.fieldName = fieldName;
        this.document = document;
    }

    private Element getElement() {
        Element element = null;
        Node parentNode = parent.getNode();
        if (parentNode != null) {
            NodeList children = parentNode.getChildNodes();
            for (int i = 0; i < children.getLength(); i++) {
                Node currentChild = children.item(i);
                if (fieldName.equals(currentChild.getNodeName())) {
                    element = (Element) currentChild;
                }
            }
        }

        return element;
    }

    private void internalSet(String value, Element element) {
        element.setTextContent(value);
    }

    private Element internalCreate() {
        parent.create();

        Document domDocument = document.asDOM();
        Element element = getElement();
        if (element == null) {
            Element newElement = domDocument.createElementNS(domDocument.getNamespaceURI(), fieldName);
            Node parentNode = parent.getNode();
            parentNode.appendChild(newElement);
            element = newElement;
        }
        return element;
    }

    public void set(String value) {
        Element element = getElement();
        internalSet(value, element);
    }

    public String get() {
        Element element = getElement();
        return element.getTextContent();
    }

    public Node getNode() {
        return getElement();
    }

    public void create() {
        internalCreate();
    }

    public void createAndSet(String value) {
        Element element = internalCreate();
        internalSet(value, element);
    }

    public void delete() {
        Element element = getElement();
        element.getParentNode().removeChild(element);
    }

    public boolean exist() {
        return parent.exist() && getElement() != null;
    }

    public void markModified() {
        Document domDocument = document.asDOM();
        Element element = getElement();
        if (element != null) {
            Attr newAttribute = domDocument.createAttribute(MODIFIED_MARKER_ATTRIBUTE);
            newAttribute.setValue(MODIFIED_MARKER_VALUE);
            element.getAttributes().setNamedItem(newAttribute);
        }
    }

    public void markUnmodified() {
        Node parentNode = getElement();
        NamedNodeMap attributes = parentNode.getAttributes();
        if (attributes.getNamedItem(MODIFIED_MARKER_ATTRIBUTE) != null) {
            attributes.removeNamedItem(MODIFIED_MARKER_ATTRIBUTE);
        }
    }

    public int size() {
        if (!exist()) {
            return 0;
        }
        return getElement().getChildNodes().getLength();
    }
}
