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

package com.amalto.core.history;

import org.apache.commons.lang.StringUtils;

/**
*
*/
public class EmptyDocument implements MutableDocument {
    public String getAsString() {
        return StringUtils.EMPTY;
    }

    public boolean isCreated() {
        return false;
    }

    public boolean isDeleted() {
        return false;
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

    public MutableDocument create() {
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
