/*
 * Copyright (C) 2006-2014 Talend Inc. - www.talend.com
 * 
 * This source code is available under agreement available at
 * %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
 * 
 * You should have received a copy of the agreement along with this program; if not, write to Talend SA 9 rue Pages
 * 92150 Suresnes, France
 */

package com.amalto.core.metadata;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.StringTokenizer;

import javax.xml.XMLConstants;

import org.apache.commons.lang.NotImplementedException;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.talend.mdm.commmon.metadata.ComplexTypeMetadata;
import org.talend.mdm.commmon.metadata.ContainedComplexTypeMetadata;
import org.talend.mdm.commmon.metadata.ContainedTypeFieldMetadata;
import org.talend.mdm.commmon.metadata.DefaultMetadataVisitor;
import org.talend.mdm.commmon.metadata.FieldMetadata;
import org.talend.mdm.commmon.metadata.MetadataRepository;
import org.talend.mdm.commmon.metadata.ReferenceFieldMetadata;
import org.talend.mdm.commmon.metadata.TypeMetadata;
import org.talend.mdm.commmon.metadata.Types;

import com.amalto.core.integrity.ForeignKeyIntegrity;
import com.amalto.core.query.user.DateConstant;
import com.amalto.core.query.user.DateTimeConstant;
import com.amalto.core.query.user.TimeConstant;
import com.amalto.core.storage.Storage;
import com.amalto.core.storage.hibernate.TypeMapping;
import com.amalto.core.storage.record.DataRecord;
import com.amalto.core.storage.record.metadata.UnsupportedDataRecordMetadata;

public class MetadataUtils {

    protected static final Logger LOGGER = Logger.getLogger(MetadataUtils.class);

    private MetadataUtils() {
    }

    private static final double ENTITY_RANK_ADJUST = 0.9;

    /**
     * <p>
     * Computes "entity rank": entity rank score is based on a modified version of Google's Page Rank algorithm (it's
     * the inverse operation of Page Rank).
     * </p>
     * <p>
     * Entity rank is computed with this algorithm: ER(E) = N + d (ER(E1)/C(E1) + ... + ER(En)/C(En)) where:
     * <ul>
     * <li>ER(E) is the entity rank of E.</li>
     * <li>N the number of entities in <code>repository</code></li>
     * <li>d an adjustment factor (between 0 and 1)</li>
     * <li>ER(Ei) is the entity rank for entity Ei that E references via a reference field</li>
     * <li>C(Ei) the number of entities in <code>repository</code> that reference Ei in the repository.</li>
     * </ul>
     * </p>
     * <p>
     * Code is expected to run in linear time (O(n+p) where n is the number of entities and p the number of references).
     * Used memory is O(n^2) (due to a dependency ordering).
     * </p>
     * 
     * @param repository A {@link MetadataRepository} instance that contains entity types.
     * @return A {@link Map} that maps a entity to its entity rank value.
     */
    public static Map<ComplexTypeMetadata, Long> computeEntityRank(MetadataRepository repository) {
        List<ComplexTypeMetadata> sortedTypes = sortTypes(repository);
        int totalNumber = sortedTypes.size();

        Map<ComplexTypeMetadata, Long> entityRank = new HashMap<ComplexTypeMetadata, Long>();
        for (ComplexTypeMetadata currentType : sortedTypes) {
            if (currentType.isInstantiable()) {
                double rank = totalNumber;
                for (FieldMetadata currentField : currentType.getFields()) {
                    if (currentField instanceof ReferenceFieldMetadata) {
                        ComplexTypeMetadata referencedType = ((ReferenceFieldMetadata) currentField).getReferencedType();
                        if (referencedType != currentType) {
                            Long referencedEntityRank = entityRank.get(referencedType);
                            if (referencedEntityRank != null) {
                                double inboundReferencesCount = getInboundReferencesCount(repository, referencedType);
                                rank += ENTITY_RANK_ADJUST * (referencedEntityRank / inboundReferencesCount);
                            }
                        }
                    }
                }
                entityRank.put(currentType, Math.round(rank));
            }
        }
        return entityRank;
    }

    private static double getInboundReferencesCount(MetadataRepository repository, ComplexTypeMetadata referencedType) {
        return repository.accept(new ForeignKeyIntegrity(referencedType)).size();
    }

    /**
     * Similar to
     * {@link #path(org.talend.mdm.commmon.metadata.ComplexTypeMetadata, org.talend.mdm.commmon.metadata.FieldMetadata, boolean)}
     * but will remain in entity boundaries (won't follow FK to other MDM entities).
     * 
     * @param origin Point of entry in the metadata graph.
     * @param target Field to look for as end of path.
     * @return A path <b>within</b> type <code>origin</code> to field <code>target</code>. Returns empty stack if no
     * path could be found.
     * @throws IllegalArgumentException If either <code>origin</code> or <code>path</code> is null.
     */
    public static List<FieldMetadata> path(ComplexTypeMetadata origin, FieldMetadata target) {
        return path(origin, target, true);
    }

    /**
     * <p>
     * Find <b>a</b> path (<b>not necessarily the shortest</b>) from type <code>origin</code> to field
     * <code>target</code>.
     * </p>
     * <p>
     * Method is expected to run in linear time, depending on:
     * <ul>
     * <li>Number of fields in <code>origin</code>.</li>
     * <li>Number of references fields accessible from <code>origin</code>.</li>
     * </ul>
     * </p>
     * 
     * @param type Point of entry in the metadata graph.
     * @param target Field to look for as end of path.
     * @return A path from type <code>origin</code> to field <code>target</code>. Returns empty list if no path could be
     * found.
     * @throws IllegalArgumentException If either <code>origin</code> or <code>path</code> is null.
     */
    public static List<FieldMetadata> path(ComplexTypeMetadata type, FieldMetadata target, boolean includeReferences) {
        Stack<FieldMetadata> path = new Stack<FieldMetadata>();
        _path(type, target, path, new HashSet<ComplexTypeMetadata>(), includeReferences);
        return path;
    }

    private static void _path(ComplexTypeMetadata type, FieldMetadata target, Stack<FieldMetadata> path,
            Set<ComplexTypeMetadata> processedTypes, boolean includeReferences) {
        // Various optimizations for very simple cases
        if (type == null) {
            throw new IllegalArgumentException("Origin can not be null");
        }
        if (target == null) {
            throw new IllegalArgumentException("Target field can not be null");
        }
        if (Storage.PROJECTION_TYPE.equals(type.getName()) && type.hasField(target.getName())) {
            path.push(type.getField(target.getName()));
        }
        if (target.getContainingType() instanceof ContainedComplexTypeMetadata) {
            String targetPath = target.getPath();
            if (type.hasField(targetPath)) {
                StringTokenizer tokenizer = new StringTokenizer(targetPath, "/"); //$NON-NLS-1$
                StringBuilder currentPath = new StringBuilder();
                while (tokenizer.hasMoreTokens()) {
                    currentPath.append(tokenizer.nextToken()).append('/');
                    path.add(type.getField(currentPath.toString()));
                }
                return;
            }
        }
        //
        if (processedTypes.contains(type)) {
            return;
        }
        processedTypes.add(type);
        Collection<FieldMetadata> fields = type.getFields();
        for (FieldMetadata current : fields) {
            path.push(current);
            if (current == target) {
                return;
            }
            if (current instanceof ContainedTypeFieldMetadata) {
                ComplexTypeMetadata containedType = ((ContainedTypeFieldMetadata) current).getContainedType();
                _path(containedType, target, path, processedTypes, includeReferences);
                if (path.peek() == target) {
                    return;
                }
                for (ComplexTypeMetadata subType : containedType.getSubTypes()) {
                    for (FieldMetadata field : subType.getFields()) {
                        if (field.getDeclaringType() == subType) {
                            _path(subType, target, path, processedTypes, includeReferences);
                            if (path.peek() == target) {
                                return;
                            }
                        }
                    }
                }
            } else if (current instanceof ReferenceFieldMetadata) {
                if (includeReferences) {
                    ComplexTypeMetadata referencedType = ((ReferenceFieldMetadata) current).getReferencedType();
                    _path(referencedType, target, path, processedTypes, true);
                    if (path.peek() == target) {
                        return;
                    }
                    for (ComplexTypeMetadata subType : referencedType.getSubTypes()) {
                        for (FieldMetadata field : subType.getFields()) {
                            if (field.getDeclaringType() == subType) {
                                _path(subType, target, path, processedTypes, true);
                                if (path.peek() == target) {
                                    return;
                                }
                            }
                        }
                    }
                }
            }
            path.pop();
        }
    }

    /**
     * <p>
     * Find <b>all</b> paths from type <code>origin</code> to field <code>target</code>.
     * </p>
     * <p>
     * This is a rather expensive operation, so use this method only when needed. When you need only <b>a</b> path to
     * field <code>target</code>, prefer usage of
     * {@link #path(org.talend.mdm.commmon.metadata.ComplexTypeMetadata, org.talend.mdm.commmon.metadata.FieldMetadata)}
     * .
     * </p>
     * <p>
     * This method follows references to other type <b>only</b> when type is not instantiable (see
     * {@link org.talend.mdm.commmon.metadata.TypeMetadata#isInstantiable()}).
     * </p>
     * 
     * @param type Point of entry in the metadata graph.
     * @param target Field to look for as end of path.
     * @return A path from type <code>origin</code> to field <code>target</code>. Returns empty list if no path could be
     * found.
     * @throws IllegalArgumentException If either <code>origin</code> or <code>path</code> is null.
     * @see #path(org.talend.mdm.commmon.metadata.ComplexTypeMetadata, org.talend.mdm.commmon.metadata.FieldMetadata)
     */
    public static Set<List<FieldMetadata>> paths(ComplexTypeMetadata type, FieldMetadata target) {
        Stack<FieldMetadata> path = new Stack<FieldMetadata>();
        HashSet<List<FieldMetadata>> foundPaths = new HashSet<List<FieldMetadata>>();
        _paths(type, target, path, foundPaths);
        return foundPaths;
    }

    private static void _paths(ComplexTypeMetadata type, FieldMetadata target, Stack<FieldMetadata> currentPath,
            Set<List<FieldMetadata>> foundPaths) {
        // Various optimizations for very simple cases
        if (type == null) {
            throw new IllegalArgumentException("Origin can not be null");
        }
        if (target == null) {
            throw new IllegalArgumentException("Target field can not be null");
        }
        if (Storage.PROJECTION_TYPE.equals(type.getName()) && type.hasField(target.getName())) {
            currentPath.push(type.getField(target.getName()));
        }
        //
        Collection<FieldMetadata> fields = type.getFields();
        for (FieldMetadata current : fields) {
            currentPath.push(current);
            if (current == target) {
                foundPaths.add(new ArrayList<FieldMetadata>(currentPath));
            }
            if (current instanceof ContainedTypeFieldMetadata) {
                ComplexTypeMetadata containedType = ((ContainedTypeFieldMetadata) current).getContainedType();
                _paths(containedType, target, currentPath, foundPaths);
                for (ComplexTypeMetadata subType : containedType.getSubTypes()) {
                    for (FieldMetadata field : subType.getFields()) {
                        if (field.getDeclaringType() == subType) {
                            _paths(subType, target, currentPath, foundPaths);
                        }
                    }
                }
            } else if (current instanceof ReferenceFieldMetadata) {
                ComplexTypeMetadata referencedType = ((ReferenceFieldMetadata) current).getReferencedType();
                if (!referencedType.isInstantiable()) {
                    _paths(referencedType, target, currentPath, foundPaths);
                    for (ComplexTypeMetadata subType : referencedType.getSubTypes()) {
                        for (FieldMetadata field : subType.getFields()) {
                            if (field.getDeclaringType() == subType) {
                                _paths(subType, target, currentPath, foundPaths);
                            }
                        }
                    }
                }
            }
            currentPath.pop();
        }
    }

    /**
     * Creates a value from <code>dataAsString</code>. Type and/or format of the returned value depends on
     * <code>field</code>. For instance, calling this method with {@link String} with value "0" and a field typed as
     * integer returns {@link Integer} instance with value 0.
     * 
     * @param dataAsString A {@link String} containing content to initialize a value.
     * @param field A {@link FieldMetadata} that describes type information about the field.
     * @return A {@link Object} value that has correct type according to <code>field</code>. Returns <code>null</code>
     * if field is instance of {@link ContainedTypeFieldMetadata} (this type of field isn't expected to have values).
     * Also returns <code>null</code> is parameter <code>dataAsString</code> is null <b>OR</b> if
     * <code>dataAsString</code> is empty string.
     * @throws RuntimeException Throws sub classes of {@link RuntimeException} if <code>dataAsString</code> format does
     * not match field's type.
     */
    public static Object convert(String dataAsString, FieldMetadata field) {
        return convert(dataAsString, field.getType());
    }

    public static Object convert(String dataAsString, FieldMetadata field, TypeMetadata actualType) {
        if (actualType == null) {
            // Use field's declared type if no actual type (TMDM-6898)
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Type is null, replacing type with field's declared type");
            }
            actualType = field.getType();
            if (actualType == null) {
                throw new IllegalArgumentException("Actual type for field '" + field.getName() + "' cannot be null.");
            }
        }
        if (field instanceof ReferenceFieldMetadata) {
            if (dataAsString == null || dataAsString.trim().isEmpty()) {
                return null;
            }
            List<String> ids = new LinkedList<String>();
            if (dataAsString.startsWith("[")) { //$NON-NLS-1$
                char[] chars = dataAsString.toCharArray();
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
            } else {
                ids.add(dataAsString);
            }
            if (ids.isEmpty()) {
                throw new IllegalArgumentException("Id '" + dataAsString + "' does not match expected format (no id found).");
            }
            if (!(actualType instanceof ComplexTypeMetadata)) {
                throw new IllegalArgumentException("Type '" + actualType.getName() + "' was expected to be an entity type.");
            }
            ComplexTypeMetadata actualComplexType = (ComplexTypeMetadata) actualType;
            DataRecord referencedRecord = new DataRecord(actualComplexType, UnsupportedDataRecordMetadata.INSTANCE);
            Collection<FieldMetadata> keyFields = actualComplexType.getKeyFields();
            if (ids.size() != keyFields.size()) {
                throw new IllegalStateException("Type '" + actualType.getName() + "' expects " + keyFields.size()
                        + " keys values, but got " + ids.size() + ".");
            }
            Iterator<FieldMetadata> keyIterator = keyFields.iterator();
            for (String id : ids) {
                FieldMetadata nextKey = keyIterator.next();
                referencedRecord.set(nextKey, convert(id, nextKey));
            }
            return referencedRecord;
        } else {
            if (dataAsString == null) {
                return null;
            }
            if (dataAsString.trim().isEmpty()) { // Empty string is considered as null value
                return null;
            }
            TypeMetadata type = field.getType();
            if (!(field instanceof ContainedTypeFieldMetadata)) { // Contained (anonymous types) values can't have
                                                                  // values
                try {
                    return convert(dataAsString, type);
                } catch (Exception e) {
                    throw new RuntimeException("Could not convert value for field '" + field.getName() + "'", e);
                }
            } else {
                return null;
            }
        }
    }

    /**
     * Returns the top level type for <code>type</code> parameter: this method returns the type before <i>anyType</i> in
     * type hierarchy. This does not apply to types declared in {@link XMLConstants#W3C_XML_SCHEMA_NS_URI}.
     * <ul>
     * <li>In an MDM entity B inherits from A, getSuperConcreteType(B) returns A.</li>
     * <li>If a simple type LimitedString extends xsd:string, getSuperConcreteType(LimitedString) returns xsd:string.</li>
     * <li>getSuperConcreteType(xsd:long) returns xsd:long (even if xsd:long extends xsd:decimal).</li>
     * <li>If the type does not have any super type, this method returns the <code>type</code> parameter.</li>
     * </ul>
     * 
     * @param type A non null type that may have super types.
     * @return The higher type in inheritance tree before <i>anyType</i>.
     */
    public static TypeMetadata getSuperConcreteType(TypeMetadata type) {
        if (type == null) {
            return null;
        }
        // Move up the inheritance tree to find the "most generic" type (used when simple types inherits from XSD types,
        // in this case, the XSD type is interesting, not the custom one).
        while (!XMLConstants.W3C_XML_SCHEMA_NS_URI.equals(type.getNamespace()) && !type.getSuperTypes().isEmpty()) {
            type = type.getSuperTypes().iterator().next();
        }
        return type;
    }

    public static Object convert(String dataAsString, TypeMetadata type) {
        return convert(dataAsString, getSuperConcreteType(type).getName());
    }

    public static Object convert(String dataAsString, String type) {
        if (dataAsString == null || dataAsString.isEmpty()) {
            return null;
        }
        if (Types.STRING.equals(type)) {
            return dataAsString;
        } else if (Types.INTEGER.equals(type) || Types.POSITIVE_INTEGER.equals(type) || Types.NEGATIVE_INTEGER.equals(type)
                || Types.NON_NEGATIVE_INTEGER.equals(type) || Types.NON_POSITIVE_INTEGER.equals(type) || Types.INT.equals(type)
                || Types.UNSIGNED_INT.equals(type)) {
            return Integer.parseInt(dataAsString);
        } else if (Types.DATE.equals(type)) {
            // Be careful here: DateFormat is not thread safe
            synchronized (DateConstant.DATE_FORMAT) {
                try {
                    DateFormat dateFormat = DateConstant.DATE_FORMAT;
                    Date date = dateFormat.parse(dataAsString);
                    return new Timestamp(date.getTime());
                } catch (Exception e) {
                    throw new RuntimeException("Could not parse date string", e);
                }
            }
        } else if (Types.DATETIME.equals(type)) {
            // Be careful here: DateFormat is not thread safe
            synchronized (DateTimeConstant.DATE_FORMAT) {
                try {
                    DateFormat dateFormat = DateTimeConstant.DATE_FORMAT;
                    Date date = dateFormat.parse(dataAsString);
                    return new Timestamp(date.getTime());
                } catch (Exception e) {
                    throw new RuntimeException("Could not parse date time string", e);
                }
            }
        } else if (Types.BOOLEAN.equals(type)) {
            // Boolean.parseBoolean returns "false" if content isn't a boolean string value. Callers of this method
            // expect call to fail if data is malformed.
            if ("0".equals(dataAsString)) { //$NON-NLS-1$
                return false;
            } else if ("1".equals(dataAsString)) { //$NON-NLS-1$
                return true;
            }
            if (!"false".equalsIgnoreCase(dataAsString) && !"true".equalsIgnoreCase(dataAsString)) { //$NON-NLS-1$ //$NON-NLS-2$
                throw new IllegalArgumentException("Value '" + dataAsString + "' is not valid for boolean");
            }
            return Boolean.parseBoolean(dataAsString);
        } else if (Types.DECIMAL.equals(type)) {
            try {
                return new BigDecimal(dataAsString);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("'" + dataAsString + "' is not a number.", e);
            }
        } else if (Types.FLOAT.equals(type)) {
            return Float.parseFloat(dataAsString);
        } else if (Types.LONG.equals(type) || Types.UNSIGNED_LONG.equals(type)) {
            return Long.parseLong(dataAsString);
        } else if (Types.ANY_URI.equals(type)) {
            return dataAsString;
        } else if (Types.SHORT.equals(type) || Types.UNSIGNED_SHORT.equals(type)) {
            return Short.parseShort(dataAsString);
        } else if (Types.QNAME.equals(type)) {
            return dataAsString;
        } else if (Types.BASE64_BINARY.equals(type)) {
            return dataAsString;
        } else if (Types.HEX_BINARY.equals(type)) {
            return dataAsString;
        } else if (Types.BYTE.equals(type) || Types.UNSIGNED_BYTE.equals(type)) {
            return Byte.parseByte(dataAsString);
        } else if (Types.DOUBLE.equals(type) || Types.UNSIGNED_DOUBLE.equals(type)) {
            return Double.parseDouble(dataAsString);
        } else if (Types.DURATION.equals(type) || Types.TIME.equals(type)) {
            // Be careful here: DateFormat is not thread safe
            synchronized (TimeConstant.TIME_FORMAT) {
                try {
                    DateFormat dateFormat = TimeConstant.TIME_FORMAT;
                    Date date = dateFormat.parse(dataAsString);
                    return new Timestamp(date.getTime());
                } catch (ParseException e) {
                    throw new RuntimeException("Could not parse time string", e);
                }
            }
        } else {
            throw new NotImplementedException("No support for type '" + type + "'");
        }
    }

    /**
     * Returns the corresponding Java type for the {@link TypeMetadata} type.
     * 
     * @param metadata A {@link TypeMetadata} instance.
     * @return The name of Java class for the <code>metadata</code> argument. Returned string might directly be used for
     * a {@link Class#forName(String)} call.
     */
    public static String getJavaType(TypeMetadata metadata) {
        String sqlType = metadata.getData(TypeMapping.SQL_TYPE);
        if (sqlType != null) {
            if ("clob".equals(sqlType)) { //$NON-NLS-1$
                return "java.sql.Clob"; //$NON-NLS-1$
            }
        }
        String type = getSuperConcreteType(metadata).getName();
        if (Types.STRING.equals(type)) {
            return "java.lang.String"; //$NON-NLS-1$
        } else if (Types.ANY_URI.equals(type)) {
            return "java.lang.String"; //$NON-NLS-1$
        } else if (Types.INT.equals(type) || Types.INTEGER.equals(type) || Types.POSITIVE_INTEGER.equals(type)
                || Types.NON_POSITIVE_INTEGER.equals(type) || Types.NON_NEGATIVE_INTEGER.equals(type)
                || Types.NEGATIVE_INTEGER.equals(type) || Types.UNSIGNED_INT.equals(type)) {
            return "java.lang.Integer"; //$NON-NLS-1$
        } else if (Types.BOOLEAN.equals(type)) {
            return "java.lang.Boolean"; //$NON-NLS-1$
        } else if (Types.DECIMAL.equals(type)) {
            return "java.math.BigDecimal"; //$NON-NLS-1$
        } else if (Types.DATE.equals(type) || Types.DATETIME.equals(type) || Types.TIME.equals(type)
                || Types.DURATION.equals(type)) {
            return "java.sql.Timestamp"; //$NON-NLS-1$
        } else if (Types.UNSIGNED_SHORT.equals(type) || Types.SHORT.equals(type)) {
            return "java.lang.Short"; //$NON-NLS-1$
        } else if (Types.UNSIGNED_LONG.equals(type) || Types.LONG.equals(type)) {
            return "java.lang.Long"; //$NON-NLS-1$
        } else if (Types.FLOAT.equals(type)) {
            return "java.lang.Float"; //$NON-NLS-1$
        } else if (Types.BASE64_BINARY.equals(type)) {
            return "java.lang.String"; //$NON-NLS-1$
        } else if (Types.BYTE.equals(type) || Types.UNSIGNED_BYTE.equals(type)) {
            return "java.lang.Byte"; //$NON-NLS-1$
        } else if (Types.HEX_BINARY.equals(type)) {
            return "java.lang.String"; //$NON-NLS-1$
        } else if (Types.DOUBLE.equals(type)) {
            return "java.lang.Double"; //$NON-NLS-1$
        } else {
            throw new UnsupportedOperationException("No support for field typed as '" + type + "'");
        }
    }

    /**
     * @param javaClassName A java class name.
     * @return The XSD type that can be used to store a value typed as <code>javaClassName</code>.
     * @throws UnsupportedOperationException If there's no known mapping from this java class to a XSD primitive type.
     */
    public static String getType(String javaClassName) {
        if ("java.lang.String".equals(javaClassName)) { //$NON-NLS-1$
            return Types.STRING;
        } else if ("java.lang.Integer".equals(javaClassName) //$NON-NLS-1$
                || "java.math.BigInteger".equals(javaClassName)) { //$NON-NLS-1$
            return Types.INT;
        } else if ("java.lang.Boolean".equals(javaClassName)) { //$NON-NLS-1$
            return Types.BOOLEAN;
        } else if ("java.math.BigDecimal".equals(javaClassName)) { //$NON-NLS-1$
            return Types.DECIMAL;
        } else if ("java.sql.Timestamp".equals(javaClassName)) { //$NON-NLS-1$
            return Types.DATETIME;
        } else if ("java.lang.Short".equals(javaClassName)) { //$NON-NLS-1$
            return Types.SHORT;
        } else if ("java.lang.Long".equals(javaClassName)) { //$NON-NLS-1$
            return Types.LONG;
        } else if ("java.lang.Float".equals(javaClassName)) { //$NON-NLS-1$
            return Types.FLOAT;
        } else if ("java.lang.Byte".equals(javaClassName)) { //$NON-NLS-1$
            return Types.BYTE;
        } else if ("java.lang.Double".equals(javaClassName)) { //$NON-NLS-1$
            return Types.DOUBLE;
        } else {
            throw new UnsupportedOperationException("No support for java class '" + javaClassName + "'");
        }
    }

    /**
     * <p>
     * Sorts type in inverse order of dependency (topological sort). A dependency to <i>type</i> might be:
     * <ul>
     * <li>FK reference to <i>type</i> (sub types of <i>type</i> are all included as a dependency).</li>
     * <li>Use of <i>type</i> as a super type.</li>
     * </ul>
     * This method runs in linear time <i>O(n+p)</i> (<i>n</i> number of types and <i>p</i> number of dependencies
     * between types). This method uses <i>n²</i> bytes in memory for processing.
     * </p>
     * <p>
     * This method is thread safe.
     * </p>
     * 
     * @param repository The repository that contains entity types to sort.
     * @return A sorted list of {@link ComplexTypeMetadata} types. First type of list is a type that has no dependency
     * on any other type of the list.
     * @throws IllegalArgumentException If repository contains types that creates a cyclic dependency. Error message
     * contains information on where the cycle is.
     */
    public static List<ComplexTypeMetadata> sortTypes(MetadataRepository repository) {
        ArrayList<ComplexTypeMetadata> types = new ArrayList<ComplexTypeMetadata>(repository.getUserComplexTypes());
        return _sortTypes(repository, false, types);
    }

    /**
     * <p>
     * Sorts types (usually a sub set of types in a {@link MetadataRepository}) in inverse order of dependency
     * (topological sort). A dependency to <i>type</i> might be:
     * <ul>
     * <li>FK reference to <i>type</i> (sub types of <i>type</i> are all included as a dependency).</li>
     * <li>Use of <i>type</i> as a super type.</li>
     * </ul>
     * This method runs in linear time <i>O(n+p)</i> (<i>n</i> number of types and <i>p</i> number of dependencies
     * between types). This method uses <i>n²</i> bytes in memory for processing.
     * </p>
     * <p>
     * This method is thread safe.
     * </p>
     * 
     * @param repository This is used to display information in case of cycle.
     * @param types The list of types to be sorted. This list should provide a transitive closure of types (all
     * references to other types must be satisfied in this list), if it isn't the unresolved FK will be ignored.
     * @return A sorted list of {@link ComplexTypeMetadata} types. First type of list is a type that has no dependency
     * on any other type of the list.
     * @throws IllegalArgumentException If repository contains types that creates a cyclic dependency. Error message
     * contains information on where the cycle is.
     */
    public static List<ComplexTypeMetadata> sortTypes(MetadataRepository repository, List<ComplexTypeMetadata> types) {
        return _sortTypes(repository, true, types);
    }

    private static List<ComplexTypeMetadata> _sortTypes(MetadataRepository repository, final boolean sortAllTypes,
            final List<ComplexTypeMetadata> types) {
        /*
         * Compute additional data for topological sorting
         */
        final int typeNumber = types.size();
        byte[][] dependencyGraph = new byte[typeNumber][typeNumber];
        for (final ComplexTypeMetadata type : types) {
            dependencyGraph[getId(type, types)] = type.accept(new DefaultMetadataVisitor<byte[]>() {

                Set<TypeMetadata> processedTypes = new HashSet<TypeMetadata>();

                Set<TypeMetadata> processedReferences = new HashSet<TypeMetadata>();

                byte[] lineContent = new byte[typeNumber]; // Stores dependencies of current type

                @Override
                public byte[] visit(ComplexTypeMetadata complexType) {
                    if (processedTypes.contains(complexType)) {
                        return lineContent;
                    } else {
                        processedTypes.add(complexType);
                    }
                    if (sortAllTypes || complexType.isInstantiable()) {
                        Collection<TypeMetadata> superTypes = complexType.getSuperTypes();
                        for (TypeMetadata superType : superTypes) {
                            if (superType instanceof ComplexTypeMetadata) {
                                lineContent[getId(((ComplexTypeMetadata) superType), types)]++;
                            }
                        }
                        super.visit(complexType);
                    }
                    if (complexType.isInstantiable()) {
                        processedTypes.clear();
                    }
                    return lineContent;
                }

                @Override
                public byte[] visit(ContainedTypeFieldMetadata containedField) {
                    ComplexTypeMetadata containedType = containedField.getContainedType();
                    if (processedTypes.contains(containedType)) {
                        return lineContent;
                    } else {
                        processedTypes.add(containedType);
                    }
                    containedType.accept(this);
                    for (ComplexTypeMetadata subType : containedType.getSubTypes()) {
                        if (processedTypes.contains(subType)) {
                            return lineContent;
                        } else {
                            processedTypes.add(subType);
                            subType.accept(this);
                        }
                    }
                    return lineContent;
                }

                @Override
                public byte[] visit(ReferenceFieldMetadata referenceField) {
                    boolean isInherited = !referenceField.getDeclaringType().equals(referenceField.getContainingType());
                    // Only handle FK declared IN the type (inherited FKs are already processed).
                    if (isInherited) {
                        return lineContent;
                    }
                    // Within entity count only once references to other type
                    ComplexTypeMetadata referencedType = referenceField.getReferencedType();
                    if (!processedReferences.add(referencedType)) {
                        return lineContent;
                    }
                    // Don't count a dependency to itself as a dependency and only takes into account FK
                    // integrity-enabled FKs.
                    if (!type.equals(referencedType) && referenceField.isFKIntegrity() && referenceField.isMandatory()) {
                        if (sortAllTypes || referencedType.isInstantiable()) {
                            if (types.contains(referencedType)) {
                                lineContent[getId(referencedType, types)]++;
                                // Implicitly include reference to sub types of referenced type.
                                for (ComplexTypeMetadata subType : referencedType.getSubTypes()) {
                                    lineContent[getId(subType, types)]++;
                                }
                            }
                        }
                    }
                    return lineContent;
                }
            });
        }
        // Log dependency matrix (before sort)
        if (LOGGER.isTraceEnabled()) {
            StringBuilder builder = logDependencyMatrix(dependencyGraph);
            LOGGER.trace(builder.toString());
        }
        /*
         * TOPOLOGICAL SORTING See "Kahn, A. B. (1962), "Topological sorting of large
         * networks", Communications of the ACM"
         */
        List<ComplexTypeMetadata> sortedTypes = new LinkedList<ComplexTypeMetadata>();
        Set<ComplexTypeMetadata> noIncomingEdges = new HashSet<ComplexTypeMetadata>();
        int lineNumber = 0;
        for (byte[] line : dependencyGraph) {
            if (!hasIncomingEdges(line)) {
                noIncomingEdges.add(getType(types, lineNumber));
            }
            lineNumber++;
        }
        while (!noIncomingEdges.isEmpty()) {
            Iterator<ComplexTypeMetadata> iterator = noIncomingEdges.iterator();
            ComplexTypeMetadata type = iterator.next();
            iterator.remove();
            sortedTypes.add(type);
            int columnNumber = getId(type, types);
            for (int i = 0; i < typeNumber; i++) {
                int edge = dependencyGraph[i][columnNumber];
                if (edge > 0) {
                    dependencyGraph[i][columnNumber] -= edge;
                    if (!hasIncomingEdges(dependencyGraph[i])) {
                        noIncomingEdges.add(getType(types, i));
                    }
                }
            }
        }
        // Log dependency matrix (after sort)
        if (LOGGER.isTraceEnabled()) {
            StringBuilder builder = logDependencyMatrix(dependencyGraph);
            LOGGER.trace(builder.toString());
        }
        // Check for cycles
        if (sortedTypes.size() < dependencyGraph.length) {
            lineNumber = 0;
            List<List<ComplexTypeMetadata>> cycles = new LinkedList<List<ComplexTypeMetadata>>();
            // use dependency graph matrix to get cyclic dependencies (if any).
            for (byte[] line : dependencyGraph) {
                if (hasIncomingEdges(line)) { // unresolved dependency (means this is a cycle start).
                    List<ComplexTypeMetadata> dependencyPath = new LinkedList<ComplexTypeMetadata>();
                    int currentLineNumber = lineNumber;
                    do {
                        ComplexTypeMetadata type = getType(types, currentLineNumber);
                        dependencyPath.add(type);
                        ForeignKeyIntegrity incomingReferences = new ForeignKeyIntegrity(type);
                        Set<ReferenceFieldMetadata> incomingFields = repository.accept(incomingReferences);
                        boolean hasMetDependency = false;
                        for (ReferenceFieldMetadata incomingField : incomingFields) {
                            ComplexTypeMetadata containingType = repository.getComplexType(incomingField.getEntityTypeName());
                            // Containing type might be null if incoming reference is in the reusable type definition
                            // (but we only care about the entity relations, so use of the reusable types
                            // in entities).
                            if (containingType != null) {
                                int currentDependency = getId(containingType, types);
                                if (hasIncomingEdges(dependencyGraph[currentDependency])) {
                                    dependencyGraph[currentLineNumber][currentDependency]--;
                                    currentLineNumber = currentDependency;
                                    hasMetDependency = true;
                                    break;
                                }
                            }
                        }
                        if (!hasMetDependency) {
                            break;
                        }
                    } while (currentLineNumber != lineNumber);
                    if (dependencyPath.size() > 1) {
                        dependencyPath.add(getType(types, lineNumber)); // Include cycle start to get a better exception
                                                                        // message.
                        cycles.add(dependencyPath);
                    }
                }
                lineNumber++;
            }
            if (!cycles.isEmpty()) { // Found cycle(s): report it/them as exception
                StringBuilder cyclesAsString = new StringBuilder();
                int i = 1;
                Iterator<List<ComplexTypeMetadata>> cyclesIterator = cycles.iterator();
                while (cyclesIterator.hasNext()) {
                    cyclesAsString.append(i++).append(") "); //$NON-NLS-1$
                    Iterator<ComplexTypeMetadata> dependencyPathIterator = cyclesIterator.next().iterator();
                    ComplexTypeMetadata previous = null;
                    while (dependencyPathIterator.hasNext()) {
                        ComplexTypeMetadata currentType = dependencyPathIterator.next();
                        cyclesAsString.append(currentType.getName());
                        if (dependencyPathIterator.hasNext()) {
                            cyclesAsString.append(" -> "); //$NON-NLS-1$
                        } else if (previous != null) {
                            Set<ReferenceFieldMetadata> inboundReferences = repository
                                    .accept(new ForeignKeyIntegrity(currentType));
                            cyclesAsString.append(" ( possible fields: ");
                            for (ReferenceFieldMetadata inboundReference : inboundReferences) {
                                ComplexTypeMetadata entity = repository.getComplexType(inboundReference.getEntityTypeName());
                                if (entity != null) {
                                    String xPath = inboundReference.getEntityTypeName() + '/' + inboundReference.getPath();
                                    cyclesAsString.append(xPath).append(' ');
                                }
                            }
                            cyclesAsString.append(')');
                        }
                        previous = currentType;
                    }
                    if (cyclesIterator.hasNext()) {
                        cyclesAsString.append('\n');
                    }
                }
                throw new IllegalArgumentException("Data model has circular dependencies:\n" + cyclesAsString);
            }
        }
        return sortedTypes;
    }

    private static StringBuilder logDependencyMatrix(byte[][] dependencyGraph) {
        StringBuilder builder = new StringBuilder();
        builder.append("Dependency matrix").append('\n');
        int maxSpace = getNumberLength(dependencyGraph.length);
        for (int i = 0; i <= maxSpace; i++) {
            builder.append(' ');
        }
        for (int i = 0; i < dependencyGraph.length; i++) {
            builder.append(i);
            for (int j = 0; j <= maxSpace - getNumberLength(i); j++) {
                builder.append(' ');
            }
        }
        builder.append('\n');
        int line = 0;
        for (byte[] lineContent : dependencyGraph) {
            builder.append(line);
            for (int j = 0; j <= maxSpace - getNumberLength(line); j++) {
                builder.append(' ');
            }
            for (byte b : lineContent) {
                builder.append(b);
                for (int j = 0; j <= maxSpace - getNumberLength(b); j++) {
                    builder.append(' ');
                }
            }
            builder.append('\n');
            line++;
        }
        return builder;
    }

    private static int getNumberLength(int number) {
        int length = 1;
        while (number >= 10) {
            number %= 10;
            length++;
        }
        return length;
    }

    private static ComplexTypeMetadata getType(List<ComplexTypeMetadata> types, int lineNumber) {
        return types.get(lineNumber);
    }

    // internal method for sortTypes
    private static boolean hasIncomingEdges(byte[] line) {
        for (byte column : line) {
            if (column > 0) {
                return true;
            }
        }
        return false;
    }

    // internal method for sortTypes
    private static int getId(ComplexTypeMetadata type, List<ComplexTypeMetadata> types) {
        if (!types.contains(type)) {
            types.add(type);
        }
        return types.indexOf(type);
    }

    public static boolean isValueAssignable(String value, String typeName) {
        try {
            convert(value, typeName);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public static String toString(Object o, FieldMetadata field) {
        if (o == null) {
            return null;
        }
        if (field instanceof ReferenceFieldMetadata) {
            return toString(o);
        }
        String typeName = field.getType().getName();
        if (Types.DATE.equals(typeName)) {
            synchronized (DateConstant.DATE_FORMAT) {
                try {
                    DateFormat dateFormat = DateConstant.DATE_FORMAT;
                    return dateFormat.format(o);
                } catch (Exception e) {
                    throw new RuntimeException("Could not parse date '" + o + "'.", e);
                }
            }
        } else if (Types.DATETIME.equals(typeName)) {
            synchronized (DateTimeConstant.DATE_FORMAT) {
                try {
                    DateFormat dateFormat = DateTimeConstant.DATE_FORMAT;
                    return dateFormat.format(o);
                } catch (Exception e) {
                    throw new RuntimeException("Could not parse date time '" + o + "'.", e);
                }
            }
        } else if (Types.TIME.equals(typeName)) {
            synchronized (TimeConstant.TIME_FORMAT) {
                try {
                    DateFormat dateFormat = TimeConstant.TIME_FORMAT;
                    return dateFormat.format(o);
                } catch (Exception e) {
                    throw new RuntimeException("Could not parse time '" + o + "'.", e);
                }
            }
        } else {
            return String.valueOf(o);
        }
    }

    public static String toString(Object value) {
        if (value == null) {
            return StringUtils.EMPTY;
        }
        if (value instanceof DataRecord) {
            DataRecord record = (DataRecord) value;
            StringBuilder builder = new StringBuilder();
            Collection<FieldMetadata> keyFields = record.getType().getKeyFields();
            for (FieldMetadata keyField : keyFields) {
                String keyFieldValue = MetadataUtils.toString(record.get(keyField), keyField);
                if (Types.STRING.equals(MetadataUtils.getSuperConcreteType(keyField.getType()).getName())) {
                    keyFieldValue = StringEscapeUtils.escapeXml(keyFieldValue);
                }
                builder.append('[').append(keyFieldValue).append(']');
            }
            return builder.toString();
        }
        return StringEscapeUtils.escapeXml(String.valueOf(value));
    }
}
