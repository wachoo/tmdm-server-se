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

package com.amalto.core.save;

import com.amalto.core.history.accessor.Accessor;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

class XPathAccessor implements Accessor {

    private final XPath xPath;

    private final org.w3c.dom.Document domDocument;

    private final String rootElementName;

    private final String path;

    public XPathAccessor(org.w3c.dom.Document domDocument, String rootElementName, String path) {
        this.domDocument = domDocument;
        this.rootElementName = rootElementName;
        this.path = path;
        xPath = XPathFactory.newInstance().newXPath();
    }

    private Node evaluateNode() throws XPathExpressionException {
        return (Node) xPath.evaluate('/' + rootElementName + '/' + path, domDocument, XPathConstants.NODE);
    }

    private Element createElement() {
        // TODO Support nested paths
        Element element = domDocument.createElementNS(domDocument.getNamespaceURI(), path);
        domDocument.getDocumentElement().appendChild(element);
        return element;
    }

    private Text createText(String value) {
        return domDocument.createTextNode(value);
    }

    public void set(String value) {
        try {
            Node node = evaluateNode();
            node.setTextContent(value);
        } catch (XPathExpressionException e) {
            throw new RuntimeException(e);
        }
    }

    public String get() {
        try {
            Node node = evaluateNode();
            if (node == null) {
                return null;
            }

            Text text = (Text) node.getFirstChild();
            return text == null ? null : text.getWholeText();
        } catch (XPathExpressionException e) {
            throw new RuntimeException(e);
        }
    }

    public void create() {
        createElement();
    }

    public void createAndSet(String value) {
        Text text = createText(value);
        createElement().appendChild(text);
    }

    public void delete() {
        throw new UnsupportedOperationException();
    }

    public boolean exist() {
        return get() != null;
    }

    public void markModified() {
    }

    public void markUnmodified() {
    }
}
