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
import com.amalto.core.metadata.MetadataUtils;
import org.apache.commons.lang.StringUtils;
import org.talend.mdm.commmon.metadata.ComplexTypeMetadata;
import org.talend.mdm.commmon.metadata.FieldMetadata;
import org.talend.mdm.commmon.metadata.MetadataRepository;
import org.talend.mdm.commmon.metadata.ReferenceFieldMetadata;

import java.util.*;

class PartialUpdateActionCreator extends UpdateActionCreator {

    private final String partialUpdatePivot;

    private final String partialUpdateKey;

    private final Map<FieldMetadata, Integer> originalFieldToLastIndex = new HashMap<FieldMetadata, Integer>();

    private final Stack<String> leftPath = new Stack<String>();

    private final Stack<String> rightPath = new Stack<String>();

    private final Map<String, String> keyValueToPath = new HashMap<String, String>();

    private final LinkedList<String> usedPaths = new LinkedList<String>();

    private final Closure closure;

    private String lastMatchPath;

    private boolean inPivot;

    private ComplexTypeMetadata mainType;

    public PartialUpdateActionCreator(MutableDocument originalDocument,
                                      MutableDocument newDocument,
                                      Date date,
                                      boolean preserveCollectionOldValues,
                                      String pivot,
                                      String key,
                                      String source,
                                      String userName,
                                      MetadataRepository repository) {
        super(originalDocument, newDocument, date, preserveCollectionOldValues, source, userName, repository);
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
                throw new IllegalStateException("Path '" + path + '/' + partialUpdateKey + "' does not exist in document sent for partial update.");
            }
            String keyValue = keyAccessor.get();
            if (keyValue != null) {
                keyValueToPath.put(keyValue, path);
            } else {
                // TODO Warning?
            }
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
                int leftElementCount = newDocument.createAccessor(StringUtils.substringBeforeLast(partialUpdatePivot, "/")).size(); //$NON-NLS-1$
                if (leftElementCount > 0) {
                    preserveCollectionOldValues = true;
                    mainType.accept(this);
                }
            }
        }
        return actionList;
    }

    private static void resetPath(String currentPath, Stack<String> path) {
        StringTokenizer pathIterator = new StringTokenizer(currentPath, "/"); //$NON-NLS-1$
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
            return StringUtils.EMPTY;
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
        enterLeft(field);
        enterRight(field);
        try {
            boolean isPivot = partialUpdatePivot.equals(getLeftPath());
            if (!inPivot && isPivot) {
                inPivot = true;
            }
            if (inPivot && field.isMany()) {
                Accessor leftAccessor;
                Accessor rightAccessor;
                rightAccessor = newDocument.createAccessor(getRightPath());
                if (!rightAccessor.exist()) {
                    // If new list does not exist, it means element was omitted in new version (legacy behavior).
                    return;
                }
                leftAccessor = originalDocument.createAccessor(getLeftPath());
                leaveLeft();
                leaveRight();
                int max = Math.max(leftAccessor.size(), rightAccessor.size());
                for (int i = 1; i <= max; i++) {
                    // XPath indexes are 1-based (not 0-based).
                    if (preserveCollectionOldValues) {
                        enterLeft(field, (leftAccessor.size() + i));
                    } else {
                        enterLeft(field, i);
                    }
                    doCompare(field, closure, i);
                    leaveLeft();
                }
                if (preserveCollectionOldValues) {
                    enterLeft(field, leftAccessor.size() + rightAccessor.size());
                } else {
                    enterLeft(field, max);
                }
                lastMatchPath = getLeftPath();
                leaveLeft();
            } else {
                closure.execute(field);
            }
            if (inPivot && isPivot) {
                inPivot = false;
            }
        } finally {
            leaveLeft();
            leaveRight();
        }
    }

    private void enterLeft(FieldMetadata field) {
        leftPath.add(field.getName());
    }

    private void enterLeft(FieldMetadata field, int position) {
        leftPath.add(field.getName() + '[' + position + ']');
    }

    private void leaveLeft() {
        if (!leftPath.isEmpty()) {
            leftPath.pop();
        }
    }

    private void enterRight(FieldMetadata field) {
        rightPath.add(field.getName());
    }

    private void enterRight(FieldMetadata field, int position) {
        rightPath.add(field.getName() + '[' + position + ']');
    }

    private void leaveRight() {
        if (!rightPath.isEmpty()) {
            rightPath.pop();
        }
    }

    private void doCompare(FieldMetadata field, Closure closure, int i) {
        if (inPivot) {
            String left = getLeftPath();
            Accessor originalKeyAccessor = originalDocument.createAccessor(left + '/' + partialUpdateKey);
            String newDocumentPath;
            if (originalKeyAccessor.exist()) {
                newDocumentPath = keyValueToPath.get(originalKeyAccessor.get());
                usedPaths.add(newDocumentPath);
            } else {
                newDocumentPath = null;
            }
            if (newDocumentPath == null) {
                if (preserveCollectionOldValues) {
                    enterRight(field, i);
                } else if(usedPaths.getLast() != null && left.startsWith(usedPaths.getLast())) { // Implicit !preserveCollectionOldValues
                    enterRight(field, i);
                } else {
                    return;
                }
            } else {
                resetPath(newDocumentPath, rightPath);
            }
        } else {
            enterRight(field, i);
        }
        closure.execute(field);
        leaveRight();
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
            if (newAccessor.exist()) {
                String newValue = newAccessor.get();
                if (newValue != null) {
                    if (comparedField.isMany()) {
                        // Append at the end of the collection
                        if (!originalFieldToLastIndex.containsKey(comparedField)) {
                            originalFieldToLastIndex.put(comparedField, originalAccessor.size());
                        }
                        leaveLeft();
                        int newIndex = originalFieldToLastIndex.get(comparedField);
                        enterLeft(comparedField, (newIndex + 1));
                        actions.add(new FieldUpdateAction(date, source, userName, getLeftPath(), StringUtils.EMPTY,
                                newValue, comparedField));
                        originalFieldToLastIndex.put(comparedField, newIndex + 1);
                    } else if (oldValue != null && !oldValue.equals(newValue)) {
                        if (!"string".equals(comparedField.getType().getName()) && !(comparedField instanceof ReferenceFieldMetadata)) { // TODO Type constant
                            // Field is not string. To ensure false positive difference detection, creates a typed value.
                            Object oldObject = MetadataUtils.convert(oldValue, comparedField);
                            Object newObject = MetadataUtils.convert(newValue, comparedField);
                            if (oldObject != null && newObject != null && oldObject instanceof Comparable) {
                                if (((Comparable) oldObject).compareTo(newObject) == 0) {
                                    // Objects are the 'same' (e.g. 10.0 is same as 10).
                                    return;
                                }
                            } else {
                                if (oldObject != null && oldObject.equals(newObject)) {
                                    return;
                                } else if (newObject != null && newObject.equals(oldObject)) {
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
