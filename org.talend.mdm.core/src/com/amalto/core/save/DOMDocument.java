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
import com.amalto.core.util.Util;
import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javax.xml.transform.TransformerException;
import java.util.StringTokenizer;

public class DOMDocument implements MutableDocument {

    private org.w3c.dom.Document domDocument;

    private String rootElementName;

    public DOMDocument(org.w3c.dom.Document domDocument) {
        init(domDocument);
    }

    public DOMDocument(Node node) {
        org.w3c.dom.Document document = SaverContextFactory.DOM_PARSER_FACTORY.newDocument();
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

    public String exportToString() {
        try {
            return Util.nodeToString(domDocument);
        } catch (TransformerException e) {
            throw new RuntimeException(e);
        }
    }

    public Document transform(DocumentTransformer transformer) {
        return transformer.transform(this);
    }

    public void restore() {
        throw new UnsupportedOperationException("Restore not supported.");
    }

    public Accessor createAccessor(String path) {
        return DOMAccessorFactory.createAccessor(rootElementName + '/' + path, this);
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
