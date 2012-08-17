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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.StringTokenizer;

import org.apache.commons.lang.StringUtils;

import com.amalto.core.history.Action;
import com.amalto.core.history.MutableDocument;
import com.amalto.core.history.accessor.Accessor;
import com.amalto.core.history.action.FieldUpdateAction;
import com.amalto.core.metadata.ComplexTypeMetadata;
import com.amalto.core.metadata.FieldMetadata;
import com.amalto.core.metadata.MetadataRepository;

class PartialUpdateActionCreator extends UpdateActionCreator {

    private final String pivot;

    private final String key;

    private final Map<FieldMetadata, Integer> originalFieldToLastIndex = new HashMap<FieldMetadata, Integer>();

    private String lastMatchPath;

    private Map<String, String> keyValueToPath = new HashMap<String, String>();

    private Set<String> toUpdatekeys = new HashSet<String>();

    private final Stack<String> leftPath = new Stack<String>();

    private final Stack<String> rightPath = new Stack<String>();

    public PartialUpdateActionCreator(MutableDocument originalDocument, MutableDocument newDocument,
            boolean preserveCollectionOldValues, String pivot, String key, String source, String userName,
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
    }

    @Override
    public List<Action> visit(ComplexTypeMetadata complexType) {
        Accessor accessor = newDocument.createAccessor(pivot);
        for (int i = 1; i <= accessor.size(); i++) {
            String path = pivot + '[' + i + ']';
            Accessor keyAccessor = newDocument.createAccessor(path + '/' + key);
            if (!keyAccessor.exist()) {
                throw new IllegalStateException("Path '" + path + '/' + key + "' does not exist in user document.");
            }
            keyValueToPath.put(keyAccessor.get(), path);
        }
        return super.visit(complexType);
    }

    @Override
    protected Closure getClosure() {
        return new Closure() {

            public void execute(FieldMetadata field) {
                String currentPath = getLeftPath();
                if (currentPath.startsWith(pivot)) {
                    compare(field);
                }
            }
        };
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

    boolean inPivot;

    protected void handleField(FieldMetadata field, Closure closure) {
        leftPath.add(field.getName());
        if (pivot.equals(getLeftPath())) {
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
            // Proceed in "reverse" order (highest index to lowest) so there won't be issues when deleting elements in
            // a sequence (if element #2 is deleted before element #3, element #3 becomes #2...).
            int max = Math.max(leftAccessor.size(), rightAccessor.size());
            for (int i = max; i > 0; i--) {
                // XPath indexes are 1-based (not 0-based).
                leftPath.add(field.getName() + '[' + i + ']');
                if (inPivot) {

                    Accessor originalKeyAccessor = originalDocument.createAccessor(getLeftPath() + '/' + key);
                    String newDocumentPath = keyValueToPath.get(originalKeyAccessor.get());
                    if (newDocumentPath == null) {
                        if (!preserveCollectionOldValues) {
                            leftPath.pop();
                            continue;
                        } else {
                            // if (i <= keyValueToPath.size())
                            rightPath.push(field.getName() + '[' + i + ']');
                        }
                    } else {
                        if (!preserveCollectionOldValues) {
                            StringTokenizer pathIterator = new StringTokenizer(newDocumentPath, "/");
                            rightPath.clear();
                            while (pathIterator.hasMoreTokens()) {
                                rightPath.add(pathIterator.nextToken());
                            }
                            toUpdatekeys.add(originalKeyAccessor.get());
                        } else {
                            // if (i <= keyValueToPath.size())
                            rightPath.push(field.getName() + '[' + i + ']');
                        }

                    }
                } else {
                    rightPath.add(field.getName() + '[' + i + ']');
                }
                {
                    closure.execute(field);
                }
                rightPath.pop();
                leftPath.pop();
            }// end for

            if (inPivot && !preserveCollectionOldValues) {
                int pos = leftAccessor.size();
                for (Iterator<String> iterator = keyValueToPath.keySet().iterator(); iterator.hasNext();) {
                    String toAppendKey = (String) iterator.next();
                    if (!toUpdatekeys.contains(toAppendKey)) {
                        pos++;
                        leftPath.add(field.getName() + '[' + pos + ']');
                        StringTokenizer pathIterator = new StringTokenizer(keyValueToPath.get(toAppendKey), "/");
                        rightPath.clear();
                        while (pathIterator.hasMoreTokens()) {
                            rightPath.add(pathIterator.nextToken());
                        }
                        {
                            closure.execute(field);
                        }
                        rightPath.pop();
                        leftPath.pop();
                    }
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

    protected void compare(FieldMetadata comparedField) {
        if (comparedField.isKey()) {
            // Can't update a key: don't even try to compare the field (but update lastMatchPath in case next compared
            // element is right after key field).
            lastMatchPath = getLeftPath();
            return;
        }

        if (rightPath.isEmpty() || leftPath.isEmpty())
            return;

        String leftPath = getLeftPath();
        String rightPath = getRightPath();
        Accessor originalAccessor = originalDocument.createAccessor(leftPath);
        Accessor newAccessor = newDocument.createAccessor(rightPath);
        if (!originalAccessor.exist()) {
            if (!newAccessor.exist()) {
                // No op
            } else { // new accessor exist
                generateNoOp(lastMatchPath);
                if (newAccessor.get() != null && !newAccessor.get().isEmpty()) { // Empty accessor means no op to ensure
                                                                                 // legacy behavior
                    actions.add(new FieldUpdateAction(date, source, userName, leftPath, StringUtils.EMPTY, newAccessor.get(),
                            comparedField));
                    generateNoOp(leftPath);
                } else {
                    // No op.
                }
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
