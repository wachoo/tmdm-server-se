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

package com.amalto.core.metadata;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;

public class MetadataUtils {

    private MetadataUtils() {
    }

    /**
     * <p>
     * Find a path (not necessarily the shortest) from type <code>origin</code> to field <code>target</code>.
     * </p>
     * <p>
     * Method is expected to run in linear time (but uses recursion, so not-so-good performance is to expect), depending on:
     * <ul>
     * <li>Number of fields in <code>origin</code>.</li>
     * <li>Number of references fields accessible from <code>origin</code>.</li>
     * </ul>
     * </p>
     *
     * @param origin Point of entry in the metadata graph.
     * @param target Field to look for as end of path.
     * @return A path from type <code>origin</code> to field <code>target</code>. Returns empty stack if no path could be found.
     */
    public static List<FieldMetadata> path(TypeMetadata origin, FieldMetadata target) {
        Stack<FieldMetadata> stack = new Stack<FieldMetadata>();
        Set<TypeMetadata> processedTypes = new HashSet<TypeMetadata>();
        for (FieldMetadata fieldMetadata : origin.getFields()) {
            List<FieldMetadata> path = path(fieldMetadata, target, stack, processedTypes);
            if (path != null) {
                return path;
            }
        }

        return stack;
    }

    // Internal method for recursion
    private static List<FieldMetadata> path(FieldMetadata field, FieldMetadata target, Stack<FieldMetadata> currentPath, Set<TypeMetadata> processedTypes) {
        currentPath.push(field);
        if (field.equals(target)) {
            return currentPath;
        } else {
            FieldMetadata metadata = currentPath.peek();
            if (metadata instanceof ReferenceFieldMetadata) {
                TypeMetadata referencedType = ((ReferenceFieldMetadata) metadata).getReferencedType();
                List<FieldMetadata> fields = referencedType.getFields();
                if (!processedTypes.contains(referencedType)) {
                    processedTypes.add(referencedType);
                    for (FieldMetadata fieldMetadata : fields) {
                        List<FieldMetadata> subPath = path(fieldMetadata, target, currentPath, processedTypes);
                        if (subPath != null) {
                            return currentPath;
                        }
                    }
                }
            } else {
                currentPath.pop();
            }

        }
        return null;
    }
}
