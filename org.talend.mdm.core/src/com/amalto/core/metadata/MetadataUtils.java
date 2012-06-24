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
import com.amalto.core.storage.record.DataRecord;
import com.amalto.core.storage.record.metadata.UnsupportedDataRecordMetadata;
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
     * @throws IllegalArgumentException If either <code>origin</code> or <code>path</code> is null.
     */
    public static List<FieldMetadata> path(ComplexTypeMetadata origin, FieldMetadata target) {
        if (origin == null) {
            throw new IllegalArgumentException("Origin can not be null");
        }
        if (target == null) {
            throw new IllegalArgumentException("Target field can not be null");
        }

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
                    referencedType = ((ReferenceFieldMetadata) metadata).getReferencedType();
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
            }
            currentPath.pop();
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
     *         Also returns <code>null</code> is parameter <code>dataAsString</code> is null.
     * @throws RuntimeException         Throws sub classes of {@link RuntimeException} if <code>dataAsString</code>  format does
     *                                  not match field's type.
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

            ReferenceFieldMetadata referenceField = (ReferenceFieldMetadata) field;
            DataRecord referencedRecord = new DataRecord(referenceField.getReferencedType(), UnsupportedDataRecordMetadata.INSTANCE);
            Iterator<FieldMetadata> keyIterator = referenceField.getReferencedType().getKeyFields().iterator();
            for (String id : ids) {
                FieldMetadata nextKey = keyIterator.next();
                referencedRecord.set(nextKey, convert(id, nextKey));
            }
            return referencedRecord;
        } else {
            return createSimpleValue(dataAsString, field);
        }
    }

    // Internal method for type instantiation.
    private static Object createSimpleValue(String xmlData, FieldMetadata field) {
        if (xmlData == null) {
            return null;
        }
        xmlData = xmlData.trim();
        if (xmlData.trim().isEmpty()) { // Empty string is considered as null value
            return null;
        }

        TypeMetadata type = field.getType();
        // Move up the inheritance tree to find the "most generic" type (used when simple types inherits from XSD types,
        // in this case, the XSD type is interesting, not the custom one).
        while (!type.getSuperTypes().isEmpty()) {
            type = type.getSuperTypes().iterator().next();
        }

        if (!(field instanceof ContainedTypeFieldMetadata)) { // Don't set contained (anonymous types) values
            if ("string".equals(type.getName())) { //$NON-NLS-1$
                return xmlData;
            } else if ("integer".equals(type.getName()) //$NON-NLS-1$
                    || "positiveInteger".equals(type.getName()) //$NON-NLS-1$
                    || "negativeInteger".equals(type.getName()) //$NON-NLS-1$
                    || "nonNegativeInteger".equals(type.getName()) //$NON-NLS-1$
                    || "nonPositiveInteger".equals(type.getName()) //$NON-NLS-1$
                    || "int".equals(type.getName()) //$NON-NLS-1$
                    || "unsignedInt".equals(type.getName())) { //$NON-NLS-1$
                return Integer.parseInt(xmlData);
            } else if ("date".equals(type.getName())) { //$NON-NLS-1$
                // Be careful here: DateFormat is not thread safe
                synchronized (DateConstant.DATE_FORMAT) {
                    try {
                        DateFormat dateFormat = DateConstant.DATE_FORMAT;
                        Date date = dateFormat.parse(xmlData);
                        return new Timestamp(date.getTime());
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            } else if ("dateTime".equals(type.getName())) { //$NON-NLS-1$
                // Be careful here: DateFormat is not thread safe
                synchronized (DateTimeConstant.DATE_FORMAT) {
                    try {
                        DateFormat dateFormat = DateTimeConstant.DATE_FORMAT;
                        Date date = dateFormat.parse(xmlData);
                        return new Timestamp(date.getTime());
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            } else if ("boolean".equals(type.getName())) { //$NON-NLS-1$
                return Boolean.parseBoolean(xmlData);
            } else if ("decimal".equals(type.getName())) { //$NON-NLS-1$
                return new BigDecimal(xmlData);
            } else if ("float".equals(type.getName())) { //$NON-NLS-1$
                return Float.parseFloat(xmlData);
            } else if ("long".equals(type.getName()) || "unsignedLong".equals(type.getName())) { //$NON-NLS-1$ //$NON-NLS-2$
                return Long.parseLong(xmlData);
            } else if ("anyURI".equals(type.getName())) { //$NON-NLS-1$
                return xmlData;
            } else if ("short".equals(type.getName()) || "unsignedShort".equals(type.getName())) { //$NON-NLS-1$ //$NON-NLS-2$
                return Short.parseShort(xmlData);
            } else if ("QName".equals(type.getName())) { //$NON-NLS-1$
                return xmlData;
            } else if ("base64Binary".equals(type.getName())) { //$NON-NLS-1$
                return xmlData;
            } else if ("hexBinary".equals(type.getName())) { //$NON-NLS-1$
                return xmlData;
            } else if ("byte".equals(type.getName()) || "unsignedByte".equals(type.getName())) { //$NON-NLS-1$ //$NON-NLS-2$
                return Byte.parseByte(xmlData);
            } else if ("double".equals(type.getName()) || "unsignedDouble".equals(type.getName())) { //$NON-NLS-1$ //$NON-NLS-2$
                return Double.parseDouble(xmlData);
            } else if ("duration".equals(type.getName()) || "time".equals(type.getName())) { //$NON-NLS-1$ //$NON-NLS-2$
                // Be careful here: DateFormat is not thread safe
                synchronized (TimeConstant.TIME_FORMAT) {
                    try {
                        DateFormat dateFormat = TimeConstant.TIME_FORMAT;
                        Date date = dateFormat.parse(xmlData);
                        return new Timestamp(date.getTime());
                    } catch (ParseException e) {
                        throw new RuntimeException(e);
                    }
                }
            } else {
                throw new NotImplementedException("No support for type '" + type.getName() + "'");
            }
        } else {
            return null;
        }
    }

    /**
     * Returns the corresponding Java type for the {@link TypeMetadata} type.
     *
     * @param metadata A {@link TypeMetadata} instance.
     * @return The name of Java class for the <code>metadata</code> argument. Returned string might directly be used for
     *         a {@link Class#forName(String)} call.
     */
    public static String getJavaType(TypeMetadata metadata) {
        // Move up the inheritance tree to find the "most generic" type (used when simple types inherits from XSD types,
        // in this case, the XSD type is interesting, not the custom one).
        while (!metadata.getSuperTypes().isEmpty()) {
            metadata = metadata.getSuperTypes().iterator().next();
        }
        String type = metadata.getName();
        if ("string".equals(type)) { //$NON-NLS-1$
            return "java.lang.String"; //$NON-NLS-1$
        } else if ("anyURI".equals(type)) {
            return "java.lang.String"; //$NON-NLS-1$
        } else if ("int".equals(type) //$NON-NLS-1$
                || "integer".equals(type) //$NON-NLS-1$
                || "positiveInteger".equals(type) //$NON-NLS-1$
                || "nonPositiveInteger".equals(type) //$NON-NLS-1$
                || "nonNegativeInteger".equals(type) //$NON-NLS-1$
                || "negativeInteger".equals(type) //$NON-NLS-1$
                || "unsignedInt".equals(type)) { //$NON-NLS-1$
            return "java.lang.Integer"; //$NON-NLS-1$
        } else if ("boolean".equals(type)) { //$NON-NLS-1$
            return "java.lang.Boolean"; //$NON-NLS-1$
        } else if ("decimal".equals(type)) { //$NON-NLS-1$
            return "java.math.BigDecimal"; //$NON-NLS-1$
        } else if ("date".equals(type) //$NON-NLS-1$
                || "dateTime".equals(type) //$NON-NLS-1$
                || "time".equals(type) //$NON-NLS-1$
                || "duration".equals(type)) { //$NON-NLS-1$
            return "java.sql.Timestamp"; //$NON-NLS-1$
        } else if ("unsignedShort".equals(type) || "short".equals(type)) { //$NON-NLS-1$ //$NON-NLS-2$
            return "java.lang.Short"; //$NON-NLS-1$
        } else if ("unsignedLong".equals(type) || "long".equals(type)) { //$NON-NLS-1$ //$NON-NLS-2$
            return "java.lang.Long"; //$NON-NLS-1$
        } else if ("float".equals(type)) { //$NON-NLS-1$
            return "java.lang.Float"; //$NON-NLS-1$
        } else if ("base64Binary".equals(type)) { //$NON-NLS-1$
            return "java.lang.String"; //$NON-NLS-1$
        } else if ("byte".equals(type) || "unsignedByte".equals(type)) { //$NON-NLS-1$ //$NON-NLS-2$
            return "java.lang.Byte"; //$NON-NLS-1$
        } else if ("QName".equals(type)) { //$NON-NLS-1$
            return "java.lang.String"; //$NON-NLS-1$
        } else if ("hexBinary".equals(type)) { //$NON-NLS-1$
            return "java.lang.String"; //$NON-NLS-1$
        } else if ("double".equals(type)) { //$NON-NLS-1$
            return "java.lang.Double"; //$NON-NLS-1$
        } else {
            throw new NotImplementedException("No support for field typed as '" + type + "'");
        }
    }
}
