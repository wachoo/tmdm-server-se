/*
 * Copyright (C) 2006-2012 Talend Inc. - www.talend.com
 * 
 * This source code is available under agreement available at
 * %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
 * 
 * You should have received a copy of the agreement along with this program; if not, write to Talend SA 9 rue Pages
 * 92150 Suresnes, France
 */

package com.amalto.core.save.context;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import org.apache.commons.lang.StringUtils;

import com.amalto.core.history.Action;
import com.amalto.core.history.MutableDocument;
import com.amalto.core.history.accessor.Accessor;
import com.amalto.core.history.action.FieldUpdateAction;
import com.amalto.core.metadata.ComplexTypeMetadata;
import com.amalto.core.metadata.ContainedTypeFieldMetadata;
import com.amalto.core.metadata.DefaultMetadataVisitor;
import com.amalto.core.metadata.EnumerationFieldMetadata;
import com.amalto.core.metadata.FieldMetadata;
import com.amalto.core.metadata.MetadataRepository;
import com.amalto.core.metadata.ReferenceFieldMetadata;
import com.amalto.core.metadata.SimpleTypeFieldMetadata;

// TODO Clean up: preserveCollectionOldValues is dedicated to partial update only!
class UpdateActionCreator extends DefaultMetadataVisitor<List<Action>> {

    protected final Stack<String> path = new Stack<String>();

    protected final LinkedList<Action> actions = new LinkedList<Action>();

    protected final Date date;

    protected final String source;

    protected final String userName;

    protected final MutableDocument originalDocument;

    protected final MutableDocument newDocument;

    protected final MetadataRepository repository;

    private final Closure closure = new CompareClosure();

    private final Set<String> touchedPaths = new HashSet<String>();

    private final Map<FieldMetadata, Integer> originalFieldToLastIndex = new HashMap<FieldMetadata, Integer>();

    protected boolean preserveCollectionOldValues;

    private String lastMatchPath;

    private boolean isDeletingContainedElement = false;

    public UpdateActionCreator(MutableDocument originalDocument,
                               MutableDocument newDocument,
                               boolean preserveCollectionOldValues,
                               String source,
                               String userName,
                               MetadataRepository repository) {
        this.preserveCollectionOldValues = preserveCollectionOldValues;
        this.originalDocument = originalDocument;
        this.newDocument = newDocument;
        this.repository = repository;
        date = new Date(System.currentTimeMillis());
        this.source = source;
        this.userName = userName;
    }

    @Override
    public List<Action> visit(ComplexTypeMetadata complexType) {
        // This is an update, so both original and new document have a "entity root" element (TMDM-3883).
        generateNoOp("/"); //$NON-NLS-1$
        super.visit(complexType);
        return actions;
    }

    @Override
    public List<Action> visit(ContainedTypeFieldMetadata containedField) {
        handleField(containedField, new ContainedTypeClosure(containedField));
        return actions;
    }

    @Override
    public List<Action> visit(ReferenceFieldMetadata referenceField) {
        handleField(referenceField, getClosure());
        return actions;
    }

    @Override
    public List<Action> visit(SimpleTypeFieldMetadata simpleField) {
        handleField(simpleField, getClosure());
        return actions;
    }

    @Override
    public List<Action> visit(EnumerationFieldMetadata enumField) {
        handleField(enumField, getClosure());
        return actions;
    }

    protected Closure getClosure() {
        return closure;
    }

    /**
     * Interface to encapsulate action to execute on fields
     */
    interface Closure {
        void execute(FieldMetadata field);
    }

    String getLeftPath() {
        return computePath(path);
    }

    String getRightPath() {
        return computePath(path);
    }

    private String computePath(Stack<String> path) {
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

    protected void handleField(FieldMetadata field, Closure closure) {
        path.add(field.getName());
        if (field.isMany()) {
            String currentPath = getLeftPath();
            Accessor leftAccessor;
            Accessor rightAccessor;
            try {
                rightAccessor = newDocument.createAccessor(currentPath);
                if (!rightAccessor.exist() && !isDeletingContainedElement) {
                    // If new list does not exist, it means element was omitted in new version (legacy behavior).
                    return;
                }
                leftAccessor = originalDocument.createAccessor(currentPath);
            } finally {
                path.pop();
            }
            // Proceed in "reverse" order (highest index to lowest) so there won't be issues when deleting elements in
            // a sequence (if element #2 is deleted before element #3, element #3 becomes #2...).
            int max = Math.max(leftAccessor.size(), rightAccessor.size());
            for (int i = 1; i <= max; i++) {
                // XPath indexes are 1-based (not 0-based).
                path.add(field.getName() + '[' + i + ']');
                closure.execute(field);
                path.pop();
            }
            path.add(field.getName() + '[' + max + ']');
            path.pop();
        } else {
            closure.execute(field);
            path.pop();
        }
    }

    protected void compare(FieldMetadata comparedField) {
        if (comparedField.isKey()) {
            // Can't update a key: don't even try to compare the field (but update lastMatchPath in case next compared
            // element is right after key field).
            lastMatchPath = getLeftPath();
            return;
        }
        if (path.isEmpty()) {
            throw new IllegalStateException("Path for compare can not be empty.");
        }
        String path = getLeftPath();
        Accessor originalAccessor = originalDocument.createAccessor(path);
        Accessor newAccessor = newDocument.createAccessor(path);
        if (!originalAccessor.exist()) {
            if (!newAccessor.exist()) {
                // No op
            } else { // new accessor exist
                if (newAccessor.get() != null && !newAccessor.get().isEmpty()) { // Empty accessor means no op to ensure legacy behavior
                    generateNoOp(lastMatchPath);
                    actions.add(new FieldUpdateAction(date, source, userName, path, StringUtils.EMPTY, newAccessor.get(), comparedField));
                    generateNoOp(path);
                } else {
                    // No op.
                }
            }
        } else { // original accessor exist
            String oldValue = originalAccessor.get();
            lastMatchPath = path;
            if (!newAccessor.exist()) {
                if (comparedField.isMany() && !preserveCollectionOldValues) {
                    // TMDM-5216: Visit sub fields include old/new values for sub elements.
                    if (comparedField instanceof ContainedTypeFieldMetadata) {
                        isDeletingContainedElement = true;
                        ((ContainedTypeFieldMetadata) comparedField).getContainedType().accept(this);
                        isDeletingContainedElement = false;
                    } else if (!isDeletingContainedElement) {
                        // TMDM-5257: RemoveSimpleTypeNodeWithOccurrence
                        // Null values may happen if accessor is targeting an element that contains other elements
                        actions.add(new FieldUpdateAction(date, source, userName, path, oldValue == null ? StringUtils.EMPTY
                                : oldValue, null, comparedField));
                    }
                }
                if (isDeletingContainedElement) {
                    // Null values may happen if accessor is targeting an element that contains other elements
                    actions.add(new FieldUpdateAction(date, source, userName, path, oldValue == null ? StringUtils.EMPTY
                            : oldValue, null, comparedField));
                }
            } else { // new accessor exist
                String newValue = newAccessor.get();
                if (newAccessor.get() != null && !(comparedField instanceof ContainedTypeFieldMetadata)) {
                    if (comparedField.isMany() && preserveCollectionOldValues) {
                        // Append at the end of the collection
                        if (!originalFieldToLastIndex.containsKey(comparedField)) {
                            originalFieldToLastIndex.put(comparedField, originalAccessor.size());
                        }
                        String previousPathElement = this.path.pop();
                        int newIndex = originalFieldToLastIndex.get(comparedField);
                        this.path.push(comparedField.getName() + "[" + (newIndex + 1) + "]");
                        actions.add(new FieldUpdateAction(date, source, userName, getLeftPath(), StringUtils.EMPTY,
                                newAccessor.get(), comparedField));
                        this.path.pop();
                        this.path.push(previousPathElement);
                        originalFieldToLastIndex.put(comparedField, newIndex + 1);
                    } else if (oldValue != null && !oldValue.equals(newAccessor.get())) {
                        actions.add(new FieldUpdateAction(date, source, userName, path, oldValue, newAccessor.get(), comparedField));
                    }
                }
            }
        }
    }

    protected void generateNoOp(String path) {
        // TODO Do only this if type is a sequence (useless if type isn't ordered).
        if (!touchedPaths.contains(path) && path != null) {
            touchedPaths.add(path);
            actions.add(new TouchAction(path, date, source, userName));
        }
    }

    private class ContainedTypeClosure implements Closure {

        private final ContainedTypeFieldMetadata containedField;

        public ContainedTypeClosure(ContainedTypeFieldMetadata containedField) {
            this.containedField = containedField;
        }

        public void execute(FieldMetadata field) {
            ComplexTypeMetadata type = containedField.getContainedType();

            compare(field);

            Accessor leftAccessor = originalDocument.createAccessor(getLeftPath());
            Accessor rightAccessor = newDocument.createAccessor(getRightPath());
            if (rightAccessor.exist()) {
                String newType = rightAccessor.getActualType();
                String previousType = StringUtils.EMPTY;
                if (leftAccessor.exist()) {
                    previousType = leftAccessor.getActualType();
                }

                if (!newType.isEmpty()) {
                    ComplexTypeMetadata newTypeMetadata = (ComplexTypeMetadata) repository.getNonInstantiableType(StringUtils.EMPTY, newType);
                    ComplexTypeMetadata previousTypeMetadata = (ComplexTypeMetadata) repository.getNonInstantiableType(StringUtils.EMPTY, previousType);
                    // Perform some checks about the xsi:type value (valid or not?).
                    if (newTypeMetadata == null) {
                        throw new IllegalArgumentException("Type '" + newType + "' was not found.");
                    }
                    // Check if type of element isn't a subclass of declared type (use of xsi:type).
                    if (!newTypeMetadata.isAssignableFrom(field.getType())) {
                        throw new IllegalArgumentException("Type '" + field.getType().getName()
                                + "' is not assignable from type '" + newTypeMetadata.getName() + "'");
                    }

                    actions.add(new ChangeTypeAction(date, source, userName, getLeftPath(), previousTypeMetadata, newTypeMetadata));
                    type = newTypeMetadata;
                }
            }

            Action before = actions.getLast();
            type.accept(UpdateActionCreator.this);
            // Way to detect if there is a change in elements below: check if last action in list changed.
            boolean hasActions = actions.getLast() != before;
            if (leftAccessor.exist() || (rightAccessor.exist() && hasActions)) {
                lastMatchPath = getLeftPath();
            }
        }
    }

    private class CompareClosure implements Closure {

        public void execute(FieldMetadata field) {
            compare(field);
            if (field instanceof ReferenceFieldMetadata) {
                Accessor leftAccessor = originalDocument.createAccessor(getLeftPath());
                Accessor rightAccessor = newDocument.createAccessor(getRightPath());
                if (rightAccessor.exist()) {
                    String newType = rightAccessor.getActualType();
                    String previousType = StringUtils.EMPTY;
                    if (leftAccessor.exist()) {
                        previousType = leftAccessor.getActualType();
                    }

                    if (!newType.isEmpty()) {
                        ComplexTypeMetadata newTypeMetadata = (ComplexTypeMetadata) repository.getNonInstantiableType(StringUtils.EMPTY, newType);
                        ComplexTypeMetadata previousTypeMetadata = (ComplexTypeMetadata) repository.getNonInstantiableType(StringUtils.EMPTY, previousType);
                        // Perform some checks about the xsi:type value (valid or not?).
                        if (newTypeMetadata == null) {
                            throw new IllegalArgumentException("Type '" + newType + "' was not found.");
                        }
                        // TODO Check if type of element isn't a subclass of declared type (use of xsi:type).
                        actions.add(new ChangeReferenceTypeAction(date, source, userName, getLeftPath(), previousTypeMetadata, newTypeMetadata));
                    }
                }
                Action before = actions.getLast();
                // Way to detect if there is a change in elements below: check if last action in list changed.
                boolean hasActions = actions.getLast() != before;
                if (leftAccessor.exist() || (rightAccessor.exist() && hasActions)) {
                    lastMatchPath = getLeftPath();
                }
            }
        }
    }
}
