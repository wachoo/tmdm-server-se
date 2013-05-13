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

import javax.xml.XMLConstants;

import com.amalto.core.schema.validation.SkipAttributeDocumentBuilder;
import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

import com.amalto.core.history.MutableDocument;

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
                    break;
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
            Node lastAccessedNode = document.getLastAccessedNode();
            if (parentNode == lastAccessedNode) {
                parentNode.insertBefore(newElement, parentNode.getFirstChild());
            } else if (lastAccessedNode != null && lastAccessedNode.getParentNode() == parentNode) {
                parentNode.insertBefore(newElement, lastAccessedNode.getNextSibling());
            } else {
                parentNode.appendChild(newElement);
            }
            element = newElement;
            document.setLastAccessedNode(element);
        }
        return element;
    }

    public void set(String value) {
        Element element = getElement();
        internalSet(value, element);
    }

    public String get() {
        Element element = getElement();
        Node textChild = element.getFirstChild();
        if (textChild != null) {
            if (textChild instanceof Text)
                return element.getTextContent();// get node value can not handle bracket well
            else
                return textChild.getNodeValue();
        } else {
            return StringUtils.EMPTY;
        }
    }

    public void touch() {
        document.setLastAccessedNode(getElement());
    }

    public Node getNode() {
        return getElement();
    }

    public void create() {
        internalCreate();
    }

    @Override
    public void insert() {
        create();
    }

    public void createAndSet(String value) {
        Element element = internalCreate();
        internalSet(value, element);
    }

    public void delete() {
        if (exist()) {
            Element element = getElement();
            element.getParentNode().removeChild(element);
        }
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
        Node node = parent.getNode();
        if (node instanceof Element) {
            Element parentElement = (Element) node;
            return parentElement.getElementsByTagName(fieldName).getLength();
        } else {
            return 1;
        }
    }

    public String getActualType() {
        Attr type = ((Element) getNode()).getAttributeNodeNS(XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI, "type"); //$NON-NLS-1$
        if (type == null) {
            type = ((Element) getNode()).getAttributeNodeNS(SkipAttributeDocumentBuilder.TALEND_NAMESPACE, "type"); //$NON-NLS-1$
        }
        if (type == null) {
            return StringUtils.EMPTY;
        } else {
            return type.getValue();
        }
    }

    @Override
    public int compareTo(Accessor accessor) {
        if (exist() != accessor.exist()) {
            return -1;
        }
        if (exist()) {
            return get().equals(accessor.get()) ? 0 : -1;
        }
        return -1;
    }
}
