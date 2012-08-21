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
import com.amalto.core.metadata.ComplexTypeMetadata;
import com.amalto.core.metadata.FieldMetadata;
import com.amalto.core.metadata.MetadataRepository;
import org.apache.commons.lang.StringUtils;

import java.util.*;

class PartialUpdateActionCreator extends UpdateActionCreator {

    private final String pivot;

    private final String key;

    private final Map<FieldMetadata, Integer> originalFieldToLastIndex = new HashMap<FieldMetadata, Integer>();

    private final Stack<String> leftPath = new Stack<String>();

    private final Stack<String> rightPath = new Stack<String>();

    private final Map<String, String> keyValueToPath = new HashMap<String, String>();

    private final Closure closure;

    private String lastMatchPath;

    private boolean inPivot;

    public PartialUpdateActionCreator(MutableDocument originalDocument,
                                      MutableDocument newDocument,
                                      boolean preserveCollectionOldValues,
                                      String pivot,
                                      String key,
                                      String source,
                                      String userName,
                                      MetadataRepository repository) {
        super(originalDocument, newDocument, preserveCollectionOldValues, source, userName, repository);
        // Pivot MUST NOT end with '/' and key MUST start with '/' (see TMDM-4381).
        if (pivot.charAt(pivot.length() - 1) == '/') {
            this.pivot = pivot.substring(0, pivot.length() - 1);
        } else {
            this.pivot = pivot;
        }
        if (key.charAt(0) != '/') {
            this.key = key + '/';
        } else {
            this.key = key;
        }
        closure = new Closure() {
            public void execute(FieldMetadata field) {
                if (inPivot) {
                    compare(field);
                }
            }
        };
    }

    @Override
    public List<Action> visit(ComplexTypeMetadata complexType) {
        Accessor accessor = newDocument.createAccessor(pivot);
        if (!key.isEmpty()) {
            for (int i = 1; i <= accessor.size(); i++) {
                String path = pivot + '[' + i + ']';
                Accessor keyAccessor = newDocument.createAccessor(path + '/' + key);
                if (!keyAccessor.exist()) {
                    throw new IllegalStateException("Path '" + path + '/' + key + "' does not exist in user document.");
                }
                keyValueToPath.put(keyAccessor.get(), path);
            }
        }
        return super.visit(complexType);
    }

    @Override
    protected Closure getClosure() {
        return closure;
    }

    String getLeftPath() {
        return computePath(leftPath);
    }

    String getRightPath() {
        return computePath(rightPath);
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
        leftPath.add(field.getName());
        if (!inPivot && pivot.equals(getLeftPath())) {
            inPivot = true;
        }
        rightPath.add(field.getName());
        if (field.isMany()) {
            Accessor leftAccessor;
            Accessor rightAccessor;
            try {
                leftAccessor = originalDocument.createAccessor(getLeftPath());
                rightAccessor = newDocument.createAccessor(getRightPath());
                if (!rightAccessor.exist()) {
                    // If new list does not exist, it means element was omitted in new version (legacy behavior).
                    return;
                }
            } finally {
                leftPath.pop();
                rightPath.pop();
            }
            int max = Math.max(leftAccessor.size(), rightAccessor.size());
            if (preserveCollectionOldValues) {
                for (int i = 1; i <= max; i++) {
                    // XPath indexes are 1-based (not 0-based).
                    leftPath.add(field.getName() + '[' + (leftAccessor.size() + i) + ']');
                    doCompare(field, closure, i);
                    leftPath.pop();
                }
            } else {  // Not preserving old values in this case (overwrite=true)
                // Proceed in "reverse" order (highest index to lowest) so there won't be issues when deleting elements in
                // a sequence (if element #2 is deleted before element #3, element #3 becomes #2...).
                for (int i = max; i > 0; i--) {
                    // XPath indexes are 1-based (not 0-based).
                    leftPath.add(field.getName() + '[' + i + ']');
                    doCompare(field, closure, i);
                    leftPath.pop();
                }
            }
            leftPath.add(field.getName() + '[' + max + ']');
            rightPath.add(field.getName() + '[' + max + ']');
            {
                lastMatchPath = getLeftPath();
            }
            rightPath.pop();
            leftPath.pop();
        } else {
            closure.execute(field);
            leftPath.pop();
            rightPath.pop();
        }
    }

    private void doCompare(FieldMetadata field, Closure closure, int i) {
        if (inPivot) {
            Accessor originalKeyAccessor = originalDocument.createAccessor(getLeftPath() + '/' + key);
            String newDocumentPath;
            if (originalKeyAccessor.exist()) {
                newDocumentPath = keyValueToPath.get(originalKeyAccessor.get());
            } else {
                newDocumentPath = null;
            }
            if (newDocumentPath == null) {
                rightPath.push(field.getName() + '[' + i + ']');
                if (!preserveCollectionOldValues) {  // When overwriting, append to the end of collection if key not found.
                    int newItemIndex;
                    if (originalFieldToLastIndex.containsKey(field)) {
                        newItemIndex = originalFieldToLastIndex.get(field) + 1;
                    } else {
                        leftPath.pop();
                        leftPath.push(field.getName());
                        Accessor accessor = originalDocument.createAccessor(getLeftPath());
                        newItemIndex = accessor.size() + 1;
                    }
                    originalFieldToLastIndex.put(field, newItemIndex);
                    leftPath.pop();
                    leftPath.push(field.getName() + '[' + newItemIndex + ']');
                }
            } else {
                StringTokenizer pathIterator = new StringTokenizer(newDocumentPath, "/");
                rightPath.clear();
                while (pathIterator.hasMoreTokens()) {
                    rightPath.add(pathIterator.nextToken());
                }
            }
        } else {
            rightPath.add(field.getName() + '[' + i + ']');
        }
        {
            closure.execute(field);
        }
        rightPath.pop();
    }

    protected void compare(FieldMetadata comparedField) {
        if (comparedField.isKey()) {
            // Can't update a key: don't even try to compare the field (but update lastMatchPath in case next compared
            // element is right after key field).
            lastMatchPath = getLeftPath();
            return;
        }
        if (rightPath.isEmpty()) {
            throw new IllegalStateException("Path in new document can not be empty.");
        }
        if (leftPath.isEmpty()) {
            throw new IllegalStateException("Path in database document can not be empty.");
        }
        String leftPath = getLeftPath();
        String rightPath = getRightPath();
        Accessor originalAccessor = originalDocument.createAccessor(leftPath);
        Accessor newAccessor = newDocument.createAccessor(rightPath);
        if (!originalAccessor.exist()) {
            if (!newAccessor.exist()) {
                // No op
            } else { // new accessor exist
                generateNoOp(lastMatchPath);
                actions.add(new FieldUpdateAction(date, source, userName, leftPath, StringUtils.EMPTY, newAccessor.get(), comparedField));
                generateNoOp(leftPath);
            }
        } else { // original accessor exist
            String oldValue = originalAccessor.get();
            lastMatchPath = leftPath;
            if (!newAccessor.exist()) {
                if (comparedField.isMany() && !preserveCollectionOldValues) {
                    // Null values may happen if accessor is targeting an element that contains other elements
                    actions.add(new FieldUpdateAction(date, source, userName, leftPath, oldValue == null ? StringUtils.EMPTY
                            : oldValue, null, comparedField));
                }
            } else { // new accessor exist
                if (comparedField.isMany() && preserveCollectionOldValues) {
                    // Append at the end of the collection
                    if (!originalFieldToLastIndex.containsKey(comparedField)) {
                        originalFieldToLastIndex.put(comparedField, originalAccessor.size());
                    }
                    this.leftPath.pop();
                    int newIndex = originalFieldToLastIndex.get(comparedField);
                    this.leftPath.push(comparedField.getName() + "[" + (newIndex + 1) + "]");
                    actions.add(new FieldUpdateAction(date, source, userName, getLeftPath(), StringUtils.EMPTY,
                            newAccessor.get(), comparedField));
                    originalFieldToLastIndex.put(comparedField, newIndex + 1);
                } else if (oldValue != null && !oldValue.equals(newAccessor.get())) {
                    actions.add(new FieldUpdateAction(date, source, userName, leftPath, oldValue, newAccessor.get(),
                            comparedField));
                }
            }
        }
    }
}
