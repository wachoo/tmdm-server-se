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

import java.util.*;

class UpdateActionCreator extends DefaultMetadataVisitor<List<Action>> {

    private final Stack<String> path = new Stack<String>();

    private final List<Action> actions = new LinkedList<Action>();

    private final Date date;

    private final String source;

    private final String userName;

    private final MutableDocument originalDocument;

    private final MutableDocument newDocument;

    public UpdateActionCreator(MutableDocument originalDocument, MutableDocument newDocument, String source, String userName) {
        this.originalDocument = originalDocument;
        this.newDocument = newDocument;
        date = new Date(System.currentTimeMillis());
        this.source = source;
        this.userName = userName;
    }

    private String getPath(String fieldName) {
        if (path.isEmpty()) {
            return fieldName;
        } else {
            StringBuilder builder = new StringBuilder();
            Iterator<String> pathIterator = path.iterator();
            while (pathIterator.hasNext()) {
                builder.append(pathIterator.next());
                if (pathIterator.hasNext()) {
                    builder.append('/');
                }
            }
            return builder.append('/').append(fieldName).toString();
        }
    }

    @Override
    public List<Action> visit(ComplexTypeMetadata complexType) {
        super.visit(complexType);
        return actions;
    }

    @Override
    public List<Action> visit(ContainedTypeFieldMetadata containedField) {
        if (!containedField.isMany()) {
            path.add(containedField.getName());
            super.visit(containedField);
            path.pop();
        } else {
            Accessor accessor = originalDocument.createAccessor(getPath(containedField.getName()));
            int leftLength = accessor.size();
            int rightLength = accessor.size();

            for (int i = 0; i < leftLength; i++) {
                // Path generation code is a bit duplicated (be careful)... and XPath indexes are 1-based (not 0-based).
                path.add(containedField.getName() + "[" + (i + 1) + "]");
                super.visit(containedField);
                path.pop();
            }
            if (rightLength > leftLength) {
                for (int i = leftLength; i < rightLength; i++) {
                    // Path generation code is a bit duplicated (be careful)... and XPath indexes are 1-based (not 0-based).
                    path.add(containedField.getName() + "[" + (i + 1) + "]");
                    super.visit(containedField);
                    path.pop();
                }
            }
        }
        return actions;
    }

    @Override
    public List<Action> visit(ReferenceFieldMetadata referenceField) {
        handleField(referenceField);
        return actions;
    }

    @Override
    public List<Action> visit(SimpleTypeFieldMetadata simpleField) {
        handleField(simpleField);
        return actions;
    }

    @Override
    public List<Action> visit(EnumerationFieldMetadata enumField) {
        handleField(enumField);
        return actions;
    }

    private void handleField(FieldMetadata simpleField) {
        String path = getPath(simpleField.getName());
        Accessor originalAccessor = originalDocument.createAccessor(path);
        Accessor newAccessor = newDocument.createAccessor(path);

        if (!originalAccessor.exist()) {
            if (!newAccessor.exist()) {
                // No op
            } else { // new accessor exist
                if (!newAccessor.get().isEmpty()) {
                    actions.add(new FieldUpdateAction(date, source, userName, path, "", newAccessor.get()));
                }
            }
        } else { // original accessor exist
            if (!newAccessor.exist()) {
                // No op
            } else { // new accessor exist
                if (!originalAccessor.get().equals(newAccessor.get())) {
                    actions.add(new FieldUpdateAction(date, source, userName, path, originalAccessor.get(), newAccessor.get()));
                }
            }
        }
    }
}