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

package com.amalto.core.history.action;

import com.amalto.core.history.Action;
import com.amalto.core.history.MutableDocument;
import org.apache.commons.lang.StringUtils;

import java.util.Date;
import java.util.Set;

/**
 *
 */
public class NoOpAction implements Action {

    private final Date date = new Date(Long.MIN_VALUE);

    public MutableDocument perform(MutableDocument document) {
        return document;
    }

    public MutableDocument undo(MutableDocument document) {
        return document;
    }

    public MutableDocument addModificationMark(MutableDocument document) {
        return document;
    }

    public MutableDocument removeModificationMark(MutableDocument document) {
        return document;
    }

    public Date getDate() {
        return date;
    }

    public String getSource() {
        return StringUtils.EMPTY;
    }

    public String getUserName() {
        return StringUtils.EMPTY;
    }

    public boolean isAllowed(Set<String> roles) {
        return true;
    }

    public String getDetails() {
        return StringUtils.EMPTY;
    }
}
