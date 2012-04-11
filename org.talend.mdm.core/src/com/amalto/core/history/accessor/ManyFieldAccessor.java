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
import org.apache.commons.lang.StringUtils;
import org.w3c.dom.*;

import javax.xml.XMLConstants;

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

    private Element getCollectionItemNode() {
        Element collectionItemNode = null;
        Node node = parent.getNode();
        if (node == null) {
            throw new IllegalStateException("Could not find parent node in document.");
        }
        NodeList children = node.getChildNodes();

        if (index > children.getLength()) {
            return null;
        }

        int currentIndex = 0;
        for (int i = 0; i < children.getLength(); i++) {
            if (fieldName.equals(children.item(i).getNodeName())) {
                if (index == currentIndex) {
                    collectionItemNode = (Element) children.item(i);
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
        Element parentNode = (Element) parent.getNode();
        Node node = getCollectionItemNode();
        if (node == null) {
            NodeList children = parentNode.getElementsByTagName(fieldName);
            int currentCollectionSize = children.getLength();
            Node refNode;
            if (currentCollectionSize > 0) {
                refNode = children.item(currentCollectionSize - 1).getNextSibling();
            } else {
                refNode = parentNode.getLastChild(); // TODO Better way to way where to insert (look at XSD sequence)
            }

            while (currentCollectionSize <= index) {
                Element newChild = domDocument.createElementNS(domDocument.getNamespaceURI(), fieldName);
                parentNode.insertBefore(newChild, refNode);
                currentCollectionSize++;
            }
        }
    }

    public void createAndSet(String value) {
        create();
        set(value);
    }

    public void delete() {
        Node node = getCollectionItemNode();
        if (node == null) {
            return; // Node has already been deleted.
        }
        node.getParentNode().removeChild(node);
    }

    public void deleteContent() {
        Element collectionItemNode = getCollectionItemNode();
        NodeList children = collectionItemNode.getChildNodes();
        for (int i = children.getLength() - 1; i >= 0; i--) {
            Node item = children.item(i);
            collectionItemNode.removeChild(item);
        }
    }

    public boolean exist() {
        return parent.exist() && getCollectionItemNode() != null;
    }

    public void markModified() {
        Document domDocument = document.asDOM();
        Node collectionItemNode = getCollectionItemNode();
        if (collectionItemNode != null) {
            Attr newAttribute = domDocument.createAttribute(MODIFIED_MARKER_ATTRIBUTE);
            newAttribute.setValue(MODIFIED_MARKER_VALUE);
            collectionItemNode.getAttributes().setNamedItem(newAttribute);
        }
    }

    public void markUnmodified() {
        Node parentNode = parent.getNode();
        NamedNodeMap attributes = parentNode.getAttributes();
        if (attributes.getNamedItem(MODIFIED_MARKER_ATTRIBUTE) != null) {
            attributes.removeNamedItem(MODIFIED_MARKER_ATTRIBUTE);
        }
    }

    public int size() {
        if (!exist()) {
            return 0;
        }
        return getCollectionItemNode().getElementsByTagName("*").getLength(); //$NON-NLS-1$
    }

    public String getActualType() {
        Attr type = getCollectionItemNode().getAttributeNodeNS(XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI, "type"); //$NON-NLS-1$
        if (type == null) {
            return StringUtils.EMPTY;
        } else {
            return type.getValue();
        }
    }
}
