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

package com.amalto.core.save.context;

import com.amalto.core.history.FieldAction;
import com.amalto.core.history.MutableDocument;
import org.talend.mdm.commmon.metadata.ComplexTypeMetadata;
import org.talend.mdm.commmon.metadata.FieldMetadata;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

abstract class AbstractChangeTypeAction implements FieldAction {

    private final Date date;

    private final String source;

    private final String userName;

    protected final String path;

    protected final ComplexTypeMetadata newType;

    private final FieldMetadata field;

    protected final Set<String> pathToClean;

    protected final boolean hasChangedType;

    public AbstractChangeTypeAction(Date date,
                                    String source,
                                    String userName,
                                    String path,
                                    ComplexTypeMetadata previousType,
                                    ComplexTypeMetadata newType,
                                    FieldMetadata field) {
        this.source = source;
        this.date = date;
        this.userName = userName;
        this.path = path;
        this.newType = newType;
        this.field = field;
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

    public FieldMetadata getField() {
        return field;
    }
}
