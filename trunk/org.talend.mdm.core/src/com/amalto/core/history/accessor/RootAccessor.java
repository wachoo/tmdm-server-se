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
import org.w3c.dom.Node;

/**
 *
 */
class RootAccessor implements DOMAccessor {

    private final MutableDocument document;

    RootAccessor(MutableDocument document) {
        this.document = document;
    }

    public void set(String value) {
        throw new IllegalStateException("Cannot set value");
    }

    public String get() {
        throw new IllegalStateException("Cannot get value");
    }

    public Node getNode() {
        return document.asDOM();
    }

    public void create() {
        // Nothing to do (won't recreate the document).
    }

    public void delete() {
        // Nothing to do (won't delete the document).
    }

    public boolean exist() {
        return getNode() != null;
    }
}
