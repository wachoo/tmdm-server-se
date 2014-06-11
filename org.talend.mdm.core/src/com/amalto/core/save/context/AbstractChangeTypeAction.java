/*
 * Copyright (C) 2006-2014 Talend Inc. - www.talend.com
 * 
 * This source code is available under agreement available at
 * %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
 * 
 * You should have received a copy of the agreement along with this program; if not, write to Talend SA 9 rue Pages
 * 92150 Suresnes, France
 */

package com.amalto.core.save.context;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.talend.mdm.commmon.metadata.ComplexTypeMetadata;
import org.talend.mdm.commmon.metadata.FieldMetadata;

import com.amalto.core.history.Action;
import com.amalto.core.history.FieldAction;
import com.amalto.core.history.MutableDocument;
import com.amalto.core.history.action.FieldUpdateAction;
import com.amalto.core.history.action.NoOpAction;

abstract class AbstractChangeTypeAction implements FieldAction {

    protected final String path;

    protected final ComplexTypeMetadata newType;

    protected final ComplexTypeMetadata previousType;

    protected final Date date;

    protected final String source;

    protected final String userName;

    private final FieldMetadata field;

    protected final boolean hasChangedType;

    private final List<Action> impliedActions;

    protected AbstractChangeTypeAction(MutableDocument document, Date date, String source, String userName, String path,
            ComplexTypeMetadata previousType, ComplexTypeMetadata newType, FieldMetadata field) {
        this.source = source;
        this.date = date;
        this.userName = userName;
        this.path = path;
        this.newType = newType;
        this.previousType = previousType;
        this.field = field;
        // Compute paths to fields that changed from previous type (only if type changed).
        Set<String> pathToClean = new HashSet<String>();
        hasChangedType = previousType != newType || !previousType.getName().equals(newType.getName());
        if (!hasChangedType) {
            impliedActions = Collections.emptyList();
        } else if (previousType != null) {
            boolean hasChangedType = previousType != newType || !previousType.getName().equals(newType.getName());
            if (!hasChangedType) {
                impliedActions = Collections.singletonList(NoOpAction.instance());
            } else {
                // Create field update actions
                previousType.accept(new TypeComparison(newType, pathToClean));
                impliedActions = new ArrayList<Action>(1 + pathToClean.size());
                if (!pathToClean.isEmpty()) {
                    for (String currentPathToDelete : pathToClean) {
                        String deletedPath = path + '/' + currentPathToDelete;
                        String oldValue = document.createAccessor(deletedPath).get();
                        impliedActions.add(new FieldUpdateAction(date, source, userName, deletedPath, oldValue, null, field));
                    }
                }
            }
        } else {
            impliedActions = Collections.emptyList();
        }
    }

    @Override
    public Date getDate() {
        return date;
    }

    @Override
    public String getSource() {
        return source;
    }

    @Override
    public String getUserName() {
        return userName;
    }

    @Override
    public boolean isAllowed(Set<String> roles) {
        return true;
    }

    @Override
    public MutableDocument addModificationMark(MutableDocument document) {
        throw new UnsupportedOperationException();
    }

    @Override
    public MutableDocument removeModificationMark(MutableDocument document) {
        throw new UnsupportedOperationException();
    }

    @Override
    public FieldMetadata getField() {
        return field;
    }

    /**
     * @return A list of {@link com.amalto.core.history.Action actions} this type change action implies. Changing from a
     * type to another may remove existing fields, this method returns list of value deleted actions when fields no
     * longer exists in new type.
     */
    public List<Action> getImpliedActions() {
        return impliedActions;
    }
}
