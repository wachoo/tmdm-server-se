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

import com.amalto.core.query.user.DateConstant;
import com.amalto.core.query.user.DateTimeConstant;
import com.amalto.core.query.user.TimeConstant;
import org.apache.commons.lang.NotImplementedException;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.*;

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
    public static List<FieldMetadata> path(ComplexTypeMetadata origin, FieldMetadata target) {
        Stack<FieldMetadata> stack = new Stack<FieldMetadata>();
        Set<TypeMetadata> processedTypes = new HashSet<TypeMetadata>();
        for (FieldMetadata fieldMetadata : origin.getFields()) {
            List<FieldMetadata> path = path(fieldMetadata, target, stack, processedTypes);
            if (path != null) {
                return path;
            } else {
                stack.clear();
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
            if (metadata instanceof ReferenceFieldMetadata || metadata instanceof ContainedTypeFieldMetadata) {
                ComplexTypeMetadata referencedType;
                if (metadata instanceof ReferenceFieldMetadata) {
                    referencedType = (ComplexTypeMetadata) ((ReferenceFieldMetadata) metadata).getReferencedType();
                } else {
                    referencedType = ((ContainedTypeFieldMetadata) metadata).getContainedType();
                }

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

    /**
     * Creates a value from <code>dataAsString</code>. Type and/or format of the returned value depends on <code>field</code>.
     * For instance, calling this method with {@link String} with value "0" and a field typed as integer returns {@link Integer}
     * instance with value 0.
     *
     * @param dataAsString A {@link String} containing content to initialize a value.
     * @param field        A {@link FieldMetadata} that describes type information about the field.
     * @return A {@link Object} value that has correct type according to <code>field</code>. Returns <code>null</code> if
     *         field is instance of {@link ContainedTypeFieldMetadata} (this type of field isn't expected to have values).
     * @throws RuntimeException Throws sub classes of {@link RuntimeException} if <code>dataAsString</code>  format does
     *                          not match field's type.
     */
    public static Object convert(String dataAsString, FieldMetadata field) {
        if (field instanceof ReferenceFieldMetadata) {
            char[] chars = dataAsString.toCharArray();
            List<String> ids = new LinkedList<String>();
            StringBuilder builder = null;
            for (char currentChar : chars) {
                switch (currentChar) {
                    case '[':
                        builder = new StringBuilder();
                        break;
                    case ']':
                        if (builder != null) {
                            ids.add(builder.toString());
                        }
                        break;
                    default:
                        if (builder != null) {
                            builder.append(currentChar);
                        }
                        break;
                }
            }
            if (ids.isEmpty()) {
                throw new IllegalArgumentException("Id '" + dataAsString + "' does not match expected format (no id found).");
            }

            FieldMetadata referencedField = ((ReferenceFieldMetadata) field).getReferencedField();
            if (referencedField instanceof SoftIdFieldRef) {
                referencedField = ((SoftIdFieldRef) referencedField).getField();
            }

            if (referencedField instanceof CompoundFieldMetadata) {
                FieldMetadata[] fields = ((CompoundFieldMetadata) referencedField).getFields();

                if (fields.length != ids.size()) {
                    throw new IllegalArgumentException("Id '" + dataAsString + "' does not match expected format (expected " + fields.length + " values but got " + ids.size() + ").");
                }

                List<Object> fkValues = new LinkedList<Object>();
                int i = 0;
                for (FieldMetadata currentCompoundField : fields) {
                    fkValues.add(createSimpleValue(ids.get(i++), currentCompoundField));
                }
                return fkValues;
            } else {
                return createSimpleValue(ids.get(0), field);
            }
        } else {
            return createSimpleValue(dataAsString, field);
        }
    }

    // Internal method for type instantiation.
    private static Object createSimpleValue(String xmlData, FieldMetadata field) {
        TypeMetadata type = field.getType();
        // Move up the inheritance tree to find the "most generic" type (used when simple types inherits from XSD types,
        // in this case, the XSD type is interesting, not the custom one).
        while (!type.getSuperTypes().isEmpty()) {
            type = type.getSuperTypes().iterator().next();
        }

        if (!(field instanceof ContainedTypeFieldMetadata)) { // Don't set contained (anonymous types) values
            if ("string".equals(type.getName())) {
                if (xmlData.trim().isEmpty()) {  // Empty string is considered as null value
                    return null;
                }
                return xmlData.trim().isEmpty() ? null : xmlData; // Empty string is considered as null value
            } else if ("integer".equals(type.getName())
                    || "positiveInteger".equals(type.getName())
                    || "negativeInteger".equals(type.getName())
                    || "nonNegativeInteger".equals(type.getName())
                    || "nonPositiveInteger".equals(type.getName())
                    || "int".equals(type.getName())
                    || "unsignedInt".equals(type.getName())) {
                return Integer.parseInt(xmlData);
            } else if ("date".equals(type.getName())) {
                try {
                    DateFormat dateFormat = DateConstant.DATE_FORMAT;
                    Date date = dateFormat.parse(xmlData);
                    return new Timestamp(date.getTime());
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
            } else if ("dateTime".equals(type.getName())) {
                try {
                    DateFormat dateFormat = DateTimeConstant.DATE_FORMAT;
                    Date date = dateFormat.parse(xmlData);
                    return new Timestamp(date.getTime());
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
            } else if ("boolean".equals(type.getName())) {
                return Boolean.parseBoolean(xmlData);
            } else if ("decimal".equals(type.getName())) {
                return new BigDecimal(xmlData);
            } else if ("float".equals(type.getName())) {
                return Float.parseFloat(xmlData);
            } else if ("long".equals(type.getName()) || "unsignedLong".equals(type.getName())) {
                return Long.parseLong(xmlData);
            } else if ("anyURI".equals(type.getName())) {
                return xmlData;
            } else if ("short".equals(type.getName()) || "unsignedShort".equals(type.getName())) {
                return Short.parseShort(xmlData);
            } else if ("QName".equals(type.getName())) {
                return xmlData;
            } else if ("base64Binary".equals(type.getName())) {
                return xmlData;
            } else if ("hexBinary".equals(type.getName())) {
                return xmlData;
            } else if ("byte".equals(type.getName()) || "unsignedByte".equals(type.getName())) {
                return Byte.parseByte(xmlData);
            } else if ("double".equals(type.getName()) || "unsignedDouble".equals(type.getName())) {
                return Double.parseDouble(xmlData);
            } else if ("duration".equals(type.getName()) || "time".equals(type.getName())) {
                try {
                    DateFormat dateFormat = TimeConstant.TIME_FORMAT;
                    Date date = dateFormat.parse(xmlData);
                    return new Timestamp(date.getTime());
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
            } else {
                throw new NotImplementedException("No support for type '" + type.getName() + "'");
            }
        } else {
            return null;
        }
    }
}
