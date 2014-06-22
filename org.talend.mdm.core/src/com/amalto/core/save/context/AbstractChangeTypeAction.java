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

package com.amalto.core.save.context;

import com.amalto.core.history.Action;
import com.amalto.core.history.MutableDocument;
import com.amalto.core.metadata.ComplexTypeMetadata;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

abstract class AbstractChangeTypeAction implements Action {

    protected final Date date;

    protected final String source;

    protected final String userName;

    protected final String path;

    protected final ComplexTypeMetadata newType;

    protected final Set<String> pathToClean;

    protected final boolean hasChangedType;

    public AbstractChangeTypeAction(Date date, String source, String userName, String path, ComplexTypeMetadata previousType, ComplexTypeMetadata newType) {
        this.source = source;
        this.date = date;
        this.userName = userName;

        this.path = path;
        this.newType = newType;

        pathToClean = new HashSet<String>();
        // Compute paths to fields that changed from previous type (only if type changed).
        hasChangedType = previousType != newType || !previousType.getName().equals(newType.getName());
        if (previousType != null && hasChangedType) {
            newType.accept(new TypeComparison(previousType, pathToClean));
            previousType.accept(new TypeComparison(newType, pathToClean));
        }
    }

    public Date getDate() {
        return date;
    }

    public String getSource() {
        return source;
    }

    public String getUserName() {
        return userName;
    }

    public boolean isAllowed(Set<String> roles) {
        return true;
    }

    public MutableDocument addModificationMark(MutableDocument document) {
        throw new UnsupportedOperationException();
    }

    public MutableDocument removeModificationMark(MutableDocument document) {
        throw new UnsupportedOperationException();
    }
}
