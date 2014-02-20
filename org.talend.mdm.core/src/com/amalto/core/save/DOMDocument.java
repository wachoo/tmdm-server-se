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

import com.amalto.core.history.DeleteType;
import com.amalto.core.history.Document;
import com.amalto.core.history.DocumentTransformer;
import com.amalto.core.history.MutableDocument;
import com.amalto.core.history.accessor.Accessor;
import com.amalto.core.history.accessor.DOMAccessorFactory;
import com.amalto.core.save.context.SaverContextFactory;
import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import java.io.StringWriter;

public class DOMDocument implements MutableDocument {

    private org.w3c.dom.Document domDocument;

    private Node lastAccessedNode;

    private String rootElementName;

    public DOMDocument(org.w3c.dom.Document domDocument) {
        init(domDocument);
    }

    public DOMDocument(Node node) {
        DocumentBuilder documentBuilder;
        documentBuilder = SaverContextFactory.DOCUMENT_BUILDER;
        org.w3c.dom.Document document = documentBuilder.newDocument();
        document.adoptNode(node);
        document.appendChild(node);
        init(document);
    }

    private void init(org.w3c.dom.Document domDocument) {
        this.domDocument = domDocument;
        Element documentElement = domDocument.getDocumentElement();

        if (documentElement != null) {
            rootElementName = documentElement.getTagName();
        } else {
            rootElementName = null;
        }
    }

    public Node getLastAccessedNode() {
        return lastAccessedNode;
    }

    public void setLastAccessedNode(Node lastAccessedNode) {
        this.lastAccessedNode = lastAccessedNode;
    }

    public MutableDocument copy() {
        return new DOMDocument(domDocument.getDocumentElement().cloneNode(true));
    }

    public String exportToString() {
        try {
            OutputFormat format = new OutputFormat(domDocument);
            // TMDM-6900 Ensure the xsi prefix is declared in exported document when save uses a DOM document.
            domDocument.getDocumentElement().setAttributeNS(XMLConstants.XMLNS_ATTRIBUTE_NS_URI,
                    "xmlns:xsi", XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI); //$NON-NLS-1$
            format.setOmitXMLDeclaration(true);
            StringWriter stringOut = new StringWriter();
            XMLSerializer serial = new XMLSerializer(stringOut, format);
            serial.serialize(domDocument);
            return stringOut.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Document transform(DocumentTransformer transformer) {
        if (transformer == null) {
            throw new IllegalArgumentException("Transformer argument cannot be null");
        }
        return transformer.transform(this);
    }

    public void restore() {
        throw new UnsupportedOperationException("Restore not supported.");
    }

    public Accessor createAccessor(String path) {
        if (rootElementName != null) {
            return DOMAccessorFactory.createAccessor(rootElementName + '/' + path, this);
        } else {
            return DOMAccessorFactory.createAccessor(path, this);
        }
    }

    public org.w3c.dom.Document asDOM() {
        return domDocument;
    }

    public MutableDocument setField(String field, String newValue) {
        createAccessor(field).set(newValue);
        return this;
    }

    public MutableDocument deleteField(String field) {
        createAccessor(field).delete();
        return this;
    }

    public MutableDocument addField(String field, String value) {
        Accessor accessor = createAccessor(field);
        accessor.createAndSet(value);
        return this;
    }

    public MutableDocument create(MutableDocument content) {
        init(content.asDOM());
        return this;
    }

    public MutableDocument setContent(MutableDocument content) {
        init(content.asDOM());
        return this;
    }

    public MutableDocument delete(DeleteType deleteType) {
        return this;
    }

    public MutableDocument recover(DeleteType deleteType) {
        return this;
    }

    public Document applyChanges() {
        return this;
    }

}
