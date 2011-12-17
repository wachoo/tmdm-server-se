/*
 * Copyright (C) 2006-2011 Talend Inc. - www.talend.com
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
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

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

    public void set(String value) {
        Element element = getElement();
        element.setTextContent(value);
    }

    public String get() {
        Element element = getElement();
        return element.getTextContent();
    }

    public Node getNode() {
        return getElement();
    }

    public void create() {
        parent.create();

        Document domDocument = document.asDOM();
        Element element = getElement();
        if (element == null) {
            Element newElement = domDocument.createElement(fieldName);
            Node parentNode = parent.getNode();
            parentNode.appendChild(newElement);
        }
    }

    public void delete() {
        Element element = getElement();
        element.getParentNode().removeChild(element);
    }

    public boolean exist() {
        return getElement() != null;
    }

    @Override
    public String toString() {
        return "UnaryFieldAccessor{" + "fieldName='" + fieldName + '\'' + '}'; //$NON-NLS-1$ //$NON-NLS-2$
    }
}
