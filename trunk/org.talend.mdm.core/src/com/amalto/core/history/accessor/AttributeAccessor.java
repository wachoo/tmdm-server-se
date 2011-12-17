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
import org.apache.log4j.Logger;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 *
 */
class AttributeAccessor implements DOMAccessor {

    private static final Logger logger = Logger.getLogger(AttributeAccessor.class);

    private final DOMAccessor parent;

    private final String attributeName;

    private final MutableDocument document;

    public AttributeAccessor(DOMAccessor parent, String attributeName, MutableDocument document) {
        this.parent = parent;
        this.attributeName = attributeName;
        this.document = document;
    }

    private Node getAttribute() {
        Node parentNode = parent.getNode();
        NamedNodeMap attributes = parentNode.getAttributes();
        if (attributes == null) {
            throw new IllegalStateException("Could not find a parent node in document (check if document has a root element).");
        }

        return attributes.getNamedItem(attributeName);
    }

    public void set(String value) {
        Node namedItem = getAttribute();
        if (!(namedItem instanceof Attr)) {
            throw new IllegalStateException("Expected a " + Attr.class.getName() + " instance but got a " + namedItem.getClass().getName());
        }

        Attr attribute = (Attr) namedItem;
        attribute.setValue(value);
    }

    public String get() {
        Node namedItem = getAttribute();
        if (!(namedItem instanceof Attr)) {
            throw new IllegalStateException("Expected a " + Attr.class.getName() + " instance but got a " + namedItem.getClass().getName());
        }

        Attr attribute = (Attr) namedItem;
        return attribute.getValue();
    }

    public Node getNode() {
        return getAttribute();
    }

    public void create() {
        // Ensure everything is created in parent nodes.
        parent.create();

        // Create the attribute if it does not exist
        Document domDocument = document.asDOM();
        Node parentNode = parent.getNode();
        Node attribute = getAttribute();
        if (attribute == null) {
            Attr newAttribute = domDocument.createAttribute(attributeName);
            parentNode.getAttributes().setNamedItem(newAttribute);
        }
    }

    public void delete() {
        Node parentNode = parent.getNode();
        Node attribute = getAttribute();
        if (attribute != null) {
            parentNode.getAttributes().removeNamedItemNS(attribute.getNamespaceURI(), attribute.getLocalName());
        } else {
            logger.warn("Attempt to delete the attribute '" + attributeName + "'that does not exist.");
        }
    }

    public boolean exist() {
        return getAttribute() != null;
    }
}
