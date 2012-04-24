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
import com.amalto.core.history.action.FieldUpdateAction;
import com.amalto.core.metadata.*;
import com.amalto.core.util.AutoIncrementGenerator;
import org.apache.commons.lang.StringUtils;
import org.talend.mdm.commmon.util.core.EUUIDCustomType;

import java.util.*;

/**
 * Generate actions on creation (like setting UUID and AUTO_INCREMENT fields that <b>are not</b> part of the saved entity
 * type).
 * @see ID for code that sets ID values.
 */
class CreateActions extends DefaultMetadataVisitor<List<Action>> {

    private final Stack<String> path = new Stack<String>();

    private final List<Action> actions = new LinkedList<Action>();

    private final Date date;

    private final String source;

    private final String userName;

    private final String universe;

    private final String dataCluster;

    CreateActions(Date date, String source, String userName, String dataCluster, String universe) {
        this.date = date;
        this.source = source;
        this.userName = userName;
        this.dataCluster = dataCluster;
        this.universe = universe;
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
            if (!simpleField.isKey()) { // Ids are handled in ID phase
                // Handle UUID and AutoIncrement elements
                if (EUUIDCustomType.AUTO_INCREMENT.getName().equalsIgnoreCase(simpleField.getType().getName())) {
                    String conceptName = getPath() + "." + simpleField.getName().replaceAll("/", "."); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                    String autoIncrementValue = String.valueOf(AutoIncrementGenerator.generateNum(universe, dataCluster, conceptName));
                    actions.add(new FieldUpdateAction(date, source, userName, getPath(), StringUtils.EMPTY, autoIncrementValue, simpleField));
                } else if (EUUIDCustomType.UUID.getName().equalsIgnoreCase(simpleField.getType().getName())) {
                    String uuidValue = UUID.randomUUID().toString();
                    actions.add(new FieldUpdateAction(date, source, userName, getPath(), StringUtils.EMPTY, uuidValue, simpleField));
                }
            }
        }
        path.pop();
        return super.visit(simpleField);
    }
}
