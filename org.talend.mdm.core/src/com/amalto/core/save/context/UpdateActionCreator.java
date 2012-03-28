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

    private final Closure compareClosure;

    public UpdateActionCreator(MutableDocument originalDocument, MutableDocument newDocument, String source, String userName) {
        this.originalDocument = originalDocument;
        this.newDocument = newDocument;
        date = new Date(System.currentTimeMillis());
        this.source = source;
        this.userName = userName;
        compareClosure = new Closure() {
            public void execute() {
                compare();
            }
        };
    }

    @Override
    public List<Action> visit(ComplexTypeMetadata complexType) {
        super.visit(complexType);
        return actions;
    }

    @Override
    public List<Action> visit(final ContainedTypeFieldMetadata containedField) {
        handleField(containedField, new Closure() {
            public void execute() {
                containedField.getContainedType().accept(UpdateActionCreator.this);
            }
        });
        return actions;
    }

    @Override
    public List<Action> visit(ReferenceFieldMetadata referenceField) {
        handleField(referenceField, compareClosure);
        return actions;
    }

    @Override
    public List<Action> visit(SimpleTypeFieldMetadata simpleField) {
        handleField(simpleField, compareClosure);
        return actions;
    }

    @Override
    public List<Action> visit(EnumerationFieldMetadata enumField) {
        handleField(enumField, compareClosure);
        return actions;
    }

    /**
     * Interface to encapsulate action to execute on fields
     */
    interface Closure {
        void execute();
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

    private void handleField(FieldMetadata field, Closure closure) {
        if (field.isMany()) {
            Accessor leftAccessor = originalDocument.createAccessor(getPath());
            Accessor rightAccessor = newDocument.createAccessor(getPath());
            int leftLength = leftAccessor.size();
            int rightLength = rightAccessor.size();

            for (int i = 0; i < leftLength; i++) {
                // Path generation code is a bit duplicated (be careful)... and XPath indexes are 1-based (not 0-based).
                path.add(field.getName() + "[" + (i + 1) + "]");
                closure.execute();
                path.pop();
            }
            if (rightLength > leftLength) {
                for (int i = leftLength; i < rightLength; i++) {
                    // Path generation code is a bit duplicated (be careful)... and XPath indexes are 1-based (not 0-based).
                    path.add(field.getName() + "[" + (i + 1) + "]");
                    closure.execute();
                    path.pop();
                }
            }
        } else {
            path.add(field.getName());
            closure.execute();
            path.pop();
        }
    }

    private void compare() {
        String path = getPath();
        Accessor originalAccessor = originalDocument.createAccessor(path);
        Accessor newAccessor = newDocument.createAccessor(path);

        if (!originalAccessor.exist()) {
            if (!newAccessor.exist()) {
                // No op
            } else { // new accessor exist
                if (!newAccessor.get().isEmpty()) { // TODO Empty accessor means no op to ensure legacy behavior
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
