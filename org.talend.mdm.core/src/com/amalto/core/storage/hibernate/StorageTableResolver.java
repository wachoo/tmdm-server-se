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

package com.amalto.core.storage.hibernate;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.talend.mdm.commmon.metadata.ComplexTypeMetadata;
import org.talend.mdm.commmon.metadata.FieldMetadata;
import org.talend.mdm.commmon.metadata.ReferenceFieldMetadata;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

class StorageTableResolver implements TableResolver {

    private static final String RESERVED_SQL_KEYWORDS = "reservedSQLKeywords.txt"; //$NON-NLS-1$

    private static final Logger LOGGER = Logger.getLogger(StorageTableResolver.class);

    protected static final String STANDARD_PREFIX = "X_"; //$NON-NLS-1$

    protected static final String RESERVED_KEYWORD_PREFIX = "X_"; //$NON-NLS-1$

    private static Set<String> reservedKeyWords;

    private Set<FieldMetadata> indexedFields;

    private final int maxLength;

    public StorageTableResolver(Set<FieldMetadata> indexedFields) {
        this(indexedFields, Integer.MAX_VALUE);
    }

    public StorageTableResolver(Set<FieldMetadata> indexedFields, int maxLength) {
        this.indexedFields = indexedFields;
        this.maxLength = maxLength;
        // Loads reserved SQL keywords.
        synchronized (MappingGenerator.class) {
            if (reservedKeyWords == null) {
                reservedKeyWords = new TreeSet<String>();
                InputStream reservedKeyWordsList = this.getClass().getResourceAsStream(RESERVED_SQL_KEYWORDS);
                try {
                    if (reservedKeyWordsList == null) {
                        throw new IllegalStateException("File '" + RESERVED_SQL_KEYWORDS + "' was not found.");
                    }
                    List list = IOUtils.readLines(reservedKeyWordsList);
                    for (Object o : list) {
                        reservedKeyWords.add(String.valueOf(o));
                    }
                    if (LOGGER.isDebugEnabled()) {
                        LOGGER.debug("Loaded " + reservedKeyWords.size() + " reserved SQL key words.");
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                } finally {
                    try {
                        if (reservedKeyWordsList != null) {
                            reservedKeyWordsList.close();
                        }
                    } catch (IOException e) {
                        LOGGER.error("Error occurred when closing reserved keyword list.", e);
                    }
                }
            }
        }
    }

    public String get(ComplexTypeMetadata type) {
        String tableName = formatSQLName(type.getName().replace('-', '_')).toUpperCase();
        if (!type.isInstantiable() && !tableName.startsWith(STANDARD_PREFIX)) {
            tableName = STANDARD_PREFIX + tableName;
        }
        return formatSQLName(tableName.replace('-', '_')).toUpperCase();
    }

    public String get(FieldMetadata field) {
        return get(field, StringUtils.EMPTY);
    }

    @Override
    public String get(FieldMetadata field, String prefix) {
        String name;
        if (StringUtils.isEmpty(prefix)) {
            name = field.getName();
        } else {
            name = prefix + '_' + field.getName();
        }
        String formattedName = formatSQLName(name.replace('-', '_'));
        if (!formattedName.startsWith(STANDARD_PREFIX) && !formattedName.startsWith(STANDARD_PREFIX.toLowerCase())) {
            return (STANDARD_PREFIX + formattedName).toLowerCase();
        }
        return formattedName.toLowerCase();
    }

    public boolean isIndexed(FieldMetadata field) {
        return indexedFields.contains(field);
    }

    @Override
    public String getIndex(String fieldName, String prefix) {
        return formatSQLName(prefix + '_' + fieldName + "_index"); //$NON-NLS-1$
    }

    @Override
    public String getCollectionTable(FieldMetadata field) {
        if (field instanceof ReferenceFieldMetadata) {
            ReferenceFieldMetadata referenceField = (ReferenceFieldMetadata) field;
            return formatSQLName(referenceField.getContainingType().getName() + '_'
                    + referenceField.getName() + '_'
                    + referenceField.getReferencedType().getName()).toUpperCase();
        }
        return formatSQLName(get(field.getContainingType()) + '_' + field.getName()).toUpperCase();
    }

    /**
     * <p>
     * Short a string so it doesn't exceed <code>maxLength</code> length. Consecutive calls to this method with same input
     * always return the same value.
     * </p>
     * <p>
     * This method also makes sure the SQL name is not a reserved SQL key word.
     * </p>
     * <p>
     * Additionally, this method will replace all '-' characters by '_' in the returned string.
     * </p>
     *
     * @param s A non null string.
     * @return <code>null</code> if <code>s</code> is null, a shorten string so it doesn't exceed <code>maxLength</code>.
     */
    protected String formatSQLName(String s) {
        if (maxLength < 1) {
            throw new IllegalArgumentException("Max length must be greater than 0 (was " + maxLength + ").");
        }
        if (s == null) {
            return null;
        }
        // Adds a prefix until 's' is no longer a SQL reserved key word.
        String backup = s;
        while (reservedKeyWords.contains(s.toUpperCase())) {
            s = RESERVED_KEYWORD_PREFIX + s;
        }
        if (LOGGER.isDebugEnabled()) {
            if (!s.equals(backup)) {
                LOGGER.debug("Replaced '" + backup + "' with '" + s + "' because it is a reserved SQL keyword.");
            }
        }
        if (s.length() < maxLength) {
            return s;
        }
        char[] chars = s.toCharArray();
        return __shortString(chars, maxLength);
    }

    // Internal method for recursion.
    protected static String __shortString(char[] chars, int threshold) {
        if (chars.length < threshold) {
            return new String(chars).replace('-', '_');
        } else {
            String s = new String(ArrayUtils.subarray(chars, 0, threshold / 2)) + new String(ArrayUtils.subarray(chars, threshold / 2, chars.length)).hashCode();
            return __shortString(s.toCharArray(), threshold);
        }
    }
}
