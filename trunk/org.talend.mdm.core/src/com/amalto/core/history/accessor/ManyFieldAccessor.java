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
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 */
class ManyFieldAccessor implements DOMAccessor {

    private final DOMAccessor parent;

    private final String fieldName;

    private final int index;

    private final MutableDocument document;

    public ManyFieldAccessor(DOMAccessor parent, String fieldName, int index, MutableDocument document) {
        this.parent = parent;
        this.fieldName = fieldName;
        this.index = index;
        this.document = document;
    }

    private Node getCollectionItemNode() {
        Node collectionItemNode = null;
        Node node = parent.getNode();
        NodeList children = node.getChildNodes();

        if (index > children.getLength()) {
            return null;
        }

        int currentIndex = 0;
        for (int i = 0; i < children.getLength(); i++) {
            if (fieldName.equals(children.item(i).getNodeName())) {
                if (index == currentIndex) {
                    collectionItemNode = children.item(i);
                    break;
                } else {
                    currentIndex++;
                }
            }
        }

        return collectionItemNode;
    }

    public void set(String value) {
        Node collectionItemNode = getCollectionItemNode();
        collectionItemNode.setTextContent(value);
    }

    public String get() {
        Node collectionItemNode = getCollectionItemNode();
        return collectionItemNode.getTextContent();
    }

    public Node getNode() {
        return getCollectionItemNode();
    }

    public void create() {
        parent.create();

        Document domDocument = document.asDOM();
        Node parentNode = parent.getNode();
        Node node = getCollectionItemNode();
        if (node == null) {
            int currentCollectionSize = 0;
            NodeList children = parentNode.getChildNodes();
            for (int i = 0; i < children.getLength(); i++) {
                if (fieldName.equals(children.item(i).getNodeName())) {
                    currentCollectionSize++;
                }
            }

            while (currentCollectionSize <= index) {
                parentNode.appendChild(domDocument.createElement(fieldName));
                currentCollectionSize++;
            }
        }
    }

    public void delete() {
        Node node = getCollectionItemNode();
        if (node == null) {
            return; // Node has already been deleted.
        }
        node.getParentNode().removeChild(node);
    }

    public boolean exist() {
        return getCollectionItemNode() != null;
    }
}
