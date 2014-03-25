/*
 * Copyright (C) 2006-2014 Talend Inc. - www.talend.com
 *
 * This source code is available under agreement available at
 * %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
 *
 * You should have received a copy of the agreement
 * along with this program; if not, write to Talend SA
 * 9 rue Pages 92150 Suresnes, France
 */

package com.amalto.core.history;

import com.amalto.core.history.accessor.Accessor;
import com.amalto.core.history.accessor.DOMAccessorFactory;
import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
*
*/
// TODO Make it singleton (no need for new instances of this).
public class EmptyDocument implements MutableDocument {

    public static final MutableDocument INSTANCE = new EmptyDocument();

    public static final org.w3c.dom.Document EMPTY_DOCUMENT;

    static {
        try {
            EMPTY_DOCUMENT = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        }
    }

    private EmptyDocument() {
    }

    public String exportToString() {
        return StringUtils.EMPTY;
    }

    public Accessor createAccessor(String path) {
        return DOMAccessorFactory.createAccessor(path, this);
    }

    public org.w3c.dom.Document asDOM() {
        return EMPTY_DOCUMENT;
    }

    public Document transform(DocumentTransformer transformer) {
        if (transformer == null) {
            throw new IllegalArgumentException("Transformer argument cannot be null");
        }
        return transformer.transform(this);
    }

    public void restore() {
    }

    public MutableDocument setField(String field, String newValue) {
        return this;
    }

    public MutableDocument deleteField(String field) {
        return this;
    }

    public MutableDocument addField(String field, String value) {
        return this;
    }

    public MutableDocument create(MutableDocument content) {
        return this;
    }

    public MutableDocument setContent(MutableDocument content) {
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

    public Node getLastAccessedNode() {
        return null;
    }

    public void setLastAccessedNode(Node lastAccessedNode) {
    }

    public MutableDocument copy() {
        return this;
    }
}
