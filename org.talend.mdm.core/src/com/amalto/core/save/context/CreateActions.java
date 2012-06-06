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
import com.amalto.core.history.accessor.Accessor;
import com.amalto.core.history.action.FieldUpdateAction;
import com.amalto.core.metadata.*;
import com.amalto.core.history.Action;
import com.amalto.core.history.MutableDocument;
import com.amalto.core.history.accessor.Accessor;
import com.amalto.core.history.action.FieldUpdateAction;
import com.amalto.core.metadata.*;
import org.apache.commons.lang.StringUtils;
import org.talend.mdm.commmon.util.core.EUUIDCustomType;

import java.util.*;

/**
 * Generate actions on creation (like setting UUID and AUTO_INCREMENT fields that <b>are not</b> part of the saved entity
 * type).
 *
 * @see ID for code that sets ID values.
 */
class CreateActions extends DefaultMetadataVisitor<List<Action>> {

    private final Stack<String> path = new Stack<String>();

    private final List<Action> actions = new LinkedList<Action>();

    private final Date date;

    private final String source;

    private final String userName;

    private final String universe;

    private final SaverSource saverSource;

    private final String dataCluster;

    private final List<String> idValues = new LinkedList<String>();

    private final MutableDocument document;

    private boolean hasMetAutoIncrement;

    private String rootTypeName = null;

    CreateActions(MutableDocument document, Date date, String source, String userName, String dataCluster, String universe, SaverSource saverSource) {
        this.document = document;
        this.date = date;
        this.source = source;
        this.userName = userName;
        this.dataCluster = dataCluster;
        this.universe = universe;
        this.saverSource = saverSource;
    }

    private String getPath() {
        if (path.isEmpty()) {
            throw new IllegalStateException();
        } else {
            StringBuilder builder = new StringBuilder();
            Iterator<String> pathIterator = path.iterator();
            while (pathIterator.hasNext()) {
                builder.append(pathIterator.next());
                if (pathIterator.hasNext()) {
                    builder.append('/');
                }
            }
            return builder.toString();
        }
    }

    @Override
    public List<Action> visit(ComplexTypeMetadata complexType) {
        if (rootTypeName == null) {
            rootTypeName = complexType.getName();
        }
        super.visit(complexType);
        return actions;
    }

    @Override
    public List<Action> visit(ReferenceFieldMetadata referenceField) {
        path.push(referenceField.getName());
        {
            super.visit(referenceField);
        }
        path.pop();
        return actions;
    }

    @Override
    public List<Action> visit(ContainedTypeFieldMetadata containedField) {
        path.push(containedField.getName());
        {
            super.visit(containedField);
        }
        path.pop();
        return actions;
    }

    @Override
    public List<Action> visit(EnumerationFieldMetadata enumField) {
        path.push(enumField.getName());
        {
            super.visit(enumField);
        }
        path.pop();
        return actions;
    }

    @Override
    public List<Action> visit(SimpleTypeFieldMetadata simpleField) {
        path.push(simpleField.getName());
        {
            // Handle UUID and AutoIncrement elements (this code also ensures any previous value is overwritten, see TMDM-3900).
            // Note #2: This code generate values even for non-mandatory fields (but this is expected behavior).
            String currentPath = getPath();
            if (EUUIDCustomType.AUTO_INCREMENT.getName().equalsIgnoreCase(simpleField.getType().getName())) {
                String conceptName = rootTypeName + "." + simpleField.getName().replaceAll("/", "."); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                String autoIncrementValue = saverSource.nextAutoIncrementId(universe, dataCluster, conceptName);
                actions.add(new FieldUpdateAction(date, source, userName, currentPath, StringUtils.EMPTY, autoIncrementValue, simpleField));
                if (simpleField.isKey()) {
                    idValues.add(autoIncrementValue);
                }
                hasMetAutoIncrement = true; // Remembers we've just met an auto increment value
            } else if (EUUIDCustomType.UUID.getName().equalsIgnoreCase(simpleField.getType().getName())) {
                String uuidValue = UUID.randomUUID().toString();
                if (simpleField.isKey()) {
                    idValues.add(uuidValue);
                }
                actions.add(new FieldUpdateAction(date, source, userName, currentPath, StringUtils.EMPTY, uuidValue, simpleField));
            } else {
                if (simpleField.isKey()) {
                    Accessor accessor = document.createAccessor(currentPath);
                    if (!accessor.exist()) {
                        throw new IllegalStateException("Document was expected to contain id information at '" + currentPath + "'");
                    }
                    idValues.add(accessor.get());
                }
            }
        }
        path.pop();
        return super.visit(simpleField);
    }

    public boolean hasMetAutoIncrement() {
        return hasMetAutoIncrement;
    }

    public List<String> getIdValues() {
        return idValues;
    }
}
