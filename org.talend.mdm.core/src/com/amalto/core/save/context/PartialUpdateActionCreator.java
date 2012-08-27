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
import com.amalto.core.metadata.MetadataUtils;
import org.apache.commons.lang.StringUtils;

import java.util.*;

class PartialUpdateActionCreator extends UpdateActionCreator {

    private final String partialUpdatePivot;

    private final String partialUpdateKey;

    private final Map<FieldMetadata, Integer> originalFieldToLastIndex = new HashMap<FieldMetadata, Integer>();

    private final Stack<String> leftPath = new Stack<String>();

    private final Stack<String> rightPath = new Stack<String>();

    private final Map<String, String> keyValueToPath = new HashMap<String, String>();

    private final Set<String> usedPaths = new HashSet<String>();

    private final Closure closure;

    private String lastMatchPath;

    private boolean inPivot;

    private ComplexTypeMetadata mainType;

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
            partialUpdatePivot = pivot.substring(0, pivot.length() - 1);
        } else {
            partialUpdatePivot = pivot;
        }
        if (!key.isEmpty() && key.charAt(0) != '/') {
            this.partialUpdateKey = key + '/';
        } else {
            this.partialUpdateKey = key;
        }
        // Special comparison closure for partial update that compares only if we are in pivot.
        closure = new Closure() {
            public void execute(FieldMetadata field) {
                if (inPivot) {
                    compare(field);
                }
            }
        };
        // Initialize key values in database document to a path in partial update document.
        Accessor accessor = newDocument.createAccessor(partialUpdatePivot);
        for (int i = 1; i <= accessor.size(); i++) {
            String path = partialUpdatePivot + '[' + i + ']';
            Accessor keyAccessor = newDocument.createAccessor(path + '/' + partialUpdateKey);
            if (!keyAccessor.exist()) {
                throw new IllegalStateException("Path '" + path + '/' + partialUpdateKey + "' does not exist in user document.");
            }
            keyValueToPath.put(keyAccessor.get(), path);
        }
    }

    @Override
    public List<Action> visit(ComplexTypeMetadata complexType) {
        if (mainType == null) {
            mainType = complexType;
        }
        List<Action> actionList = super.visit(complexType);
        if (complexType == mainType) {
            if (!preserveCollectionOldValues) {
                /*
                 * There might be elements not used for the update. In case overwrite=true, expected behavior is to append
                 * unused elements at the end. The code below removes used elements in partial update (with overwrite=true)
                 * then do a new partial update (with overwrite=false) so new elements are added at the end (this is the
                 * behavior of a overwrite=false).
                 */
                for (String usedPath : usedPaths) {
                    newDocument.createAccessor(usedPath).delete();
                }
                // Since this a costly operation do this only if there are still elements under the pivot.
                int leftElementCount = newDocument.createAccessor(StringUtils.substringBeforeLast(partialUpdatePivot, "/")).size();
                if (leftElementCount > 0) {
                    preserveCollectionOldValues = true;
                    mainType.accept(this);
                }
            }
        }
        return actionList;
    }

    private static void resetPath(String currentPath, Stack<String> path) {
        StringTokenizer pathIterator = new StringTokenizer(currentPath, "/");
        path.clear();
        while (pathIterator.hasMoreTokens()) {
            path.add(pathIterator.nextToken());
        }
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
        String currentPath = getLeftPath();
        if (!inPivot && partialUpdatePivot.equals(currentPath)) {
            inPivot = true;
        }
        rightPath.add(field.getName());
        if (field.isMany()) {
            Accessor leftAccessor;
            Accessor rightAccessor;
            try {
                rightAccessor = newDocument.createAccessor(getRightPath());
                if (!rightAccessor.exist()) {
                    // If new list does not exist, it means element was omitted in new version (legacy behavior).
                    return;
                }
                leftAccessor = originalDocument.createAccessor(getLeftPath());
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
        if (inPivot && partialUpdatePivot.equals(currentPath)) {
            inPivot = false;
        }
    }

    private void doCompare(FieldMetadata field, Closure closure, int i) {
        if (inPivot) {
            Accessor originalKeyAccessor = originalDocument.createAccessor(getLeftPath() + '/' + partialUpdateKey);
            String newDocumentPath;
            if (originalKeyAccessor.exist()) {
                newDocumentPath = keyValueToPath.get(originalKeyAccessor.get());
                usedPaths.add(newDocumentPath);
            } else {
                newDocumentPath = null;
            }
            if (newDocumentPath == null) {
                if (preserveCollectionOldValues) {
                    rightPath.push(field.getName() + '[' + i + ']');
                } else {
                    return;
                }
            } else {
                resetPath(newDocumentPath, rightPath);
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
                String newValue = newAccessor.get();
                if (newValue != null && !newValue.isEmpty()) {
                    generateNoOp(lastMatchPath);
                    actions.add(new FieldUpdateAction(date, source, userName, leftPath, StringUtils.EMPTY, newValue, comparedField));
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
                String newValue = newAccessor.get();
                if (newValue != null && !newValue.isEmpty()) {
                    if (comparedField.isMany() && preserveCollectionOldValues) {
                        // Append at the end of the collection
                        if (!originalFieldToLastIndex.containsKey(comparedField)) {
                            originalFieldToLastIndex.put(comparedField, originalAccessor.size());
                        }
                        this.leftPath.pop();
                        int newIndex = originalFieldToLastIndex.get(comparedField);
                        this.leftPath.push(comparedField.getName() + "[" + (newIndex + 1) + "]");
                        actions.add(new FieldUpdateAction(date, source, userName, getLeftPath(), StringUtils.EMPTY,
                                newValue, comparedField));
                        originalFieldToLastIndex.put(comparedField, newIndex + 1);
                    } else if (oldValue != null && !oldValue.equals(newValue)) {
                        if (!"string".equals(comparedField.getType().getName())) {
                            // Field is not string. To ensure false positive difference detection, creates a typed value.
                            Object oldObject = MetadataUtils.convert(oldValue, comparedField);
                            Object newObject = MetadataUtils.convert(newValue, comparedField);
                            if (oldObject instanceof Comparable) {
                                if (((Comparable) oldObject).compareTo(newObject) == 0) {
                                    return;
                                }
                            } else {
                                if (oldObject.equals(newObject)) {
                                    return;
                                }
                            }
                        }
                        actions.add(new FieldUpdateAction(date, source, userName, leftPath, oldValue, newValue, comparedField));
                    }
                }
            }
        }
    }
}
