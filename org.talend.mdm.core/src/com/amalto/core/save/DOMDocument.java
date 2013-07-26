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

import com.amalto.core.history.*;
import com.amalto.core.history.Document;
import com.amalto.core.history.accessor.Accessor;
import com.amalto.core.history.accessor.DOMAccessorFactory;
import com.amalto.core.save.context.SaverContextFactory;
import com.amalto.core.schema.validation.SkipAttributeDocumentBuilder;
import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.talend.mdm.commmon.metadata.ComplexTypeMetadata;
import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilder;
import java.io.StringWriter;

public class DOMDocument implements DOMMutableDocument {

    private org.w3c.dom.Document domDocument;

    private Node lastAccessedNode;

    private String rootElementName;

    private ComplexTypeMetadata type;

    private final String revisionId;

    private final String dataModelName;

    private final String dataCluster;

    public DOMDocument(org.w3c.dom.Document domDocument, ComplexTypeMetadata type, String revisionId, String dataCluster, String dataModelName) {
        this.type = type;
        this.revisionId = revisionId;
        this.dataCluster = dataCluster;
        this.dataModelName = dataModelName;
        init(domDocument);
    }

    public DOMDocument(Node node, ComplexTypeMetadata type, String revisionId, String dataCluster, String dataModelName) {
        this.type = type;
        this.revisionId = revisionId;
        this.dataCluster = dataCluster;
        this.dataModelName = dataModelName;
        DocumentBuilder documentBuilder = SaverContextFactory.DOCUMENT_BUILDER;
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

    @Override
    public Node getLastAccessedNode() {
        return lastAccessedNode;
    }

    @Override
    public void setLastAccessedNode(Node lastAccessedNode) {
        this.lastAccessedNode = lastAccessedNode;
    }

    public MutableDocument copy() {
        return new DOMDocument((org.w3c.dom.Document) domDocument.cloneNode(true), type, revisionId, dataCluster, dataModelName);
    }

    @Override
    public void clean() {
        clean(domDocument.getDocumentElement(), EmptyElementCleaner.INSTANCE, false);
    }

    private void clean(Element element, Cleaner cleaner, boolean removeTalendAttributes) {
        if (element == null) {
            return;
        }
        if (removeTalendAttributes) {
            element.removeAttributeNS(SkipAttributeDocumentBuilder.TALEND_NAMESPACE, "type"); //$NON-NLS-1$
        }
        Node current = element.getLastChild();
        while (current != null) {
            Node next = current.getPreviousSibling();
            if (current.getNodeType() == Node.ELEMENT_NODE) {
                Element currentElement = (Element) current;
                clean(currentElement, cleaner, removeTalendAttributes);
            }
            current = next;
        }
        if (cleaner.clean(element)) {
            element.getParentNode().removeChild(element);
        }
    }

    interface Cleaner {
        /**
         * Indicates to the caller whether <code>element</code> should be deleted or not.<br/>
         *
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
            if (element.hasChildNodes()) {
                return false;
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
            }
            return true;
        }
    }

    public String exportToString() {
        try {
            OutputFormat format = new OutputFormat(domDocument);
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

    @Override
    public ComplexTypeMetadata getType() {
        return type;
    }

    @Override
    public String getDataModel() {
        return dataModelName;
    }

    @Override
    public String getRevision() {
        return revisionId;
    }

    @Override
    public String getDataCluster() {
        return dataCluster;
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

    @Override
    public org.w3c.dom.Document asValidationDOM() {
        org.w3c.dom.Document validationDOM = (org.w3c.dom.Document) domDocument.cloneNode(true);
        clean(validationDOM.getDocumentElement(), EmptyElementCleaner.INSTANCE, true);
        return validationDOM;
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
