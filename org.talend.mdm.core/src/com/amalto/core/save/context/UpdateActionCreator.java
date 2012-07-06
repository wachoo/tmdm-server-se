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

import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
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

class UpdateActionCreator extends DefaultMetadataVisitor<List<Action>> {

    private final Stack<String> path = new Stack<String>();

    private final List<Action> actions = new LinkedList<Action>();

    private final Date date;

    private final String source;

    private final String userName;

    private final MutableDocument originalDocument;

    private final MutableDocument newDocument;

    private final MetadataRepository repository;

    private final Closure compareClosure;

    private final boolean preserveCollectionOldValues;

    private final Set<String> touchedPaths = new HashSet<String>();

    private final Map<FieldMetadata, Integer> originalFieldToLastIndex = new HashMap<FieldMetadata, Integer>();

    private String lastMatchPath;

    public UpdateActionCreator(MutableDocument originalDocument, MutableDocument newDocument, boolean preserveCollectionOldValues, String source, String userName, MetadataRepository repository) {
        this.preserveCollectionOldValues = preserveCollectionOldValues;
        this.originalDocument = originalDocument;
        this.newDocument = newDocument;
        this.repository = repository;
        date = new Date(System.currentTimeMillis());
        this.source = source;
        this.userName = userName;
        compareClosure = new Closure() {
            public void execute(FieldMetadata field) {
                compare(field);
            }
        };
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
        void execute(FieldMetadata field);
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
        path.add(field.getName());
        if (field.isMany()) {
            String currentPath = getPath();
            Accessor leftAccessor;
            Accessor rightAccessor;
            try {
                leftAccessor = originalDocument.createAccessor(currentPath);
                rightAccessor = newDocument.createAccessor(currentPath);
                if (!rightAccessor.exist()) {
                    // If new list does not exist, it means element was omitted in new version (legacy behavior).
                    return;
                }
            } finally {
                path.pop();
            }

            // Proceed in "reverse" order (highest index to lowest) so there won't be issues when deleting elements in
            // a sequence (if element #2 is deleted before element #3, element #3 becomes #2...).
            int max = Math.max(leftAccessor.size(), rightAccessor.size());
            for (int i = max; i > 0; i--) {
                // XPath indexes are 1-based (not 0-based).
                path.add(field.getName() + '[' + i + ']');
                closure.execute(field);
                path.pop();
            }
            path.add(field.getName() + '[' + max + ']');
            lastMatchPath = getPath();
            path.pop();
        } else {
            closure.execute(field);
            path.pop();
        }
    }

    private void compare(FieldMetadata comparedField) {
        if (comparedField.isKey()) {
            // Can't update a key: don't even try to compare the field (but update lastMatchPath in case next compared
            // element is right after key field).
            lastMatchPath = getPath();
            return;
        }
        String path = getPath();
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
            if (!newAccessor.exist()) {
                if (comparedField.isMany()) {
                    // Null values may happen if accessor is targeting an element that contains other elements
                    actions.add(new FieldUpdateAction(date, source, userName, path, oldValue == null ? StringUtils.EMPTY : oldValue, null, comparedField));
                }
                lastMatchPath = path;
            } else { // new accessor exist
                lastMatchPath = path;
                if (oldValue != null && !oldValue.equals(newAccessor.get())) {
                    if (comparedField.isMany() && preserveCollectionOldValues) {
                        // Append at the end of the collection
                        if (!originalFieldToLastIndex.containsKey(comparedField)) {
                            originalFieldToLastIndex.put(comparedField, originalAccessor.size() + 1);
                        }
                        String previousPathElement = this.path.pop();
                        int newIndex = originalFieldToLastIndex.get(comparedField);
                        this.path.push(comparedField.getName() + "[" + (newIndex + 1) + "]");
                        actions.add(new FieldUpdateAction(date, source, userName, getPath(), StringUtils.EMPTY, newAccessor.get(), comparedField));
                        this.path.pop();
                        this.path.push(previousPathElement);
                        originalFieldToLastIndex.put(comparedField, newIndex + 1);
                    } else {
                        actions.add(new FieldUpdateAction(date, source, userName, path, oldValue, newAccessor.get(), comparedField));
                    }
                } else if (oldValue == null && newAccessor.get() != null) {
                    actions.add(new FieldUpdateAction(date, source, userName, path, oldValue, newAccessor.get(), comparedField));
                }
            }
        }
    }

    private void generateNoOp(String path) {
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

            String currentPath = getPath();
            Accessor leftAccessor = originalDocument.createAccessor(currentPath);
            Accessor rightAccessor = newDocument.createAccessor(currentPath);
            if (rightAccessor.exist()) {
                String newType = rightAccessor.getActualType();
                String previousType = StringUtils.EMPTY;
                if (leftAccessor.exist()) {
                    previousType = leftAccessor.getActualType();
                }

                if (!newType.isEmpty()) {
                    ComplexTypeMetadata newTypeMetadata = (ComplexTypeMetadata) repository.getNonInstantiableType(newType);
                    ComplexTypeMetadata previousTypeMetadata = (ComplexTypeMetadata) repository.getNonInstantiableType(previousType);
                    // Perform some checks about the xsi:type value (valid or not?).
                    if (newTypeMetadata == null) {
                        throw new IllegalArgumentException("Type '" + newType + "' was not found.");
                    }
                    // Check if type of element isn't a subclass of declared type (use of xsi:type).
                    if (!field.getType().isAssignableFrom(newTypeMetadata)) {
                        throw new IllegalArgumentException("Type '" + field.getType().getName() + "' is not assignable from type '" + newTypeMetadata.getName() + "'");
                    }

                    // if (!newType.equals(previousType)) {
                    generateNoOp(lastMatchPath);
                    actions.add(new ChangeTypeAction(date, source, userName, currentPath, previousTypeMetadata, newTypeMetadata));
                    //}
                    type = newTypeMetadata;
                }
            }

            type.accept(UpdateActionCreator.this);
            lastMatchPath = currentPath;
        }
    }
}
