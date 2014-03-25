/*
 * Copyright (C) 2006-2014 Talend Inc. - www.talend.com
 *
 * This source code is available under agreement available at
 * %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
 *
 * You should have received a copy of the agreement
 * along with this program; if not, write to Talend SA
 * 9 rue Pages 92150 Suresnes, France
 */

package com.amalto.core.storage.hibernate;

import com.amalto.core.storage.record.DataRecord;
import org.apache.commons.codec.binary.Base64InputStream;
import org.apache.commons.codec.binary.Base64OutputStream;
import org.apache.commons.io.IOUtils;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.talend.mdm.commmon.metadata.ComplexTypeMetadata;
import org.talend.mdm.commmon.metadata.FieldMetadata;
import org.talend.mdm.commmon.metadata.MetadataRepository;

import java.io.*;
import java.sql.Clob;
import java.util.Iterator;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * Represents type mapping between data model as specified by the user and data model as used by hibernate storage.
 */
public abstract class TypeMapping {

    public static final String SQL_TYPE = "SQL_TYPE"; //$NON-NLS-1$

    protected final ComplexTypeMetadata user;

    protected final ComplexTypeMetadata database;

    protected final MappingRepository mappings;

    protected boolean isFrozen;

    TypeMapping(ComplexTypeMetadata user, MappingRepository mappings) {
        this(user, (ComplexTypeMetadata) user.copyShallow(), mappings);
        // Make sure internal type (database) does not have any '-' that blocks Java classes generation.
        database.setName(database.getName().replace('-', '_'));
    }

    TypeMapping(ComplexTypeMetadata user, ComplexTypeMetadata database, MappingRepository mappings) {
        this.user = user;
        this.database = database;
        this.mappings = mappings;
    }

    protected static <T> void resetList(List<T> oldValues, List<T> newValues) {
        if (newValues == null) {
            if (oldValues != null) {
                oldValues.clear();
            }
            return;
        }
        Iterator<T> iterator = newValues.iterator();
        for (int i = 0; iterator.hasNext(); i++) {
            T nextNew = iterator.next();
            if (nextNew != null) {
                if (i < oldValues.size() && !nextNew.equals(oldValues.get(i))) {
                    oldValues.set(i, nextNew);
                } else if (i >= oldValues.size()) {
                    oldValues.add(i, nextNew);
                }
            }
        }
        while (oldValues.size() > newValues.size()) {
            oldValues.remove(oldValues.size() - 1);
        }
    }

    protected static Object readValue(DataRecord from, FieldMetadata sourceField, FieldMetadata targetField, Session session) {
        Object value = from.get(sourceField);
        return _serializeValue(value, sourceField, targetField, session);
    }

    protected static Object readValue(Wrapper from, FieldMetadata sourceField, FieldMetadata targetField) {
        Object value = from.get(sourceField.getName());
        return _deserializeValue(value, sourceField, targetField);
    }

    private static Object _serializeValue(Object value, FieldMetadata sourceField, FieldMetadata targetField, Session session) {
            if (targetField == null) {
                return value;
            }
            if (!targetField.isMany()) {
                Boolean targetZipped = targetField.getData(MetadataRepository.DATA_ZIPPED);
                Boolean sourceZipped = sourceField.getData(MetadataRepository.DATA_ZIPPED);
                if (sourceZipped == null && Boolean.TRUE.equals(targetZipped)) {
                    try {
                        ByteArrayOutputStream characters = new ByteArrayOutputStream();
                        OutputStream bos = new Base64OutputStream(characters);
                        ZipOutputStream zos = new ZipOutputStream(bos);
                        ZipEntry zipEntry = new ZipEntry("content"); //$NON-NLS-1$
                        zos.putNextEntry(zipEntry);
                        zos.write(String.valueOf(value).getBytes("UTF-8")); //$NON-NLS-1$
                        zos.closeEntry();
                        zos.flush();
                        zos.close();
                        return new String(characters.toByteArray());
                    } catch (IOException e) {
                        throw new RuntimeException("Unexpected compression exception", e);
                    }
                }
                String targetSQLType = targetField.getType().getData(TypeMapping.SQL_TYPE);
                if (targetSQLType != null && "clob".equalsIgnoreCase(targetSQLType)) { //$NON-NLS-1$
                    if (value != null) {
                        return Hibernate.createClob(String.valueOf(value), session);
                    } else {
                        return null;
                    }
                }
            }
            return value;
        }

    private static Object _deserializeValue(Object value, FieldMetadata sourceField, FieldMetadata targetField) {
        if (targetField == null) {
            return value;
        }
        if (!targetField.isMany()) {
            Boolean targetZipped = targetField.getData(MetadataRepository.DATA_ZIPPED);
            Boolean sourceZipped = sourceField.getData(MetadataRepository.DATA_ZIPPED);
            if (Boolean.TRUE.equals(sourceZipped) && targetZipped == null) {
                try {
                    ByteArrayInputStream bis = new ByteArrayInputStream(String.valueOf(value).getBytes("UTF-8")); //$NON-NLS-1$
                    ZipInputStream zis = new ZipInputStream(new Base64InputStream(bis));
                    byte[] buffer = new byte[1024];
                    int read;
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    while (zis.getNextEntry() != null) {
                        while ((read = zis.read(buffer, 0, buffer.length)) > -1) {
                            bos.write(buffer, 0, read);
                        }
                    }
                    return new String(bos.toByteArray(), "UTF-8"); //$NON-NLS-1$
                } catch (IOException e) {
                    throw new RuntimeException("Unexpected deflate exception", e);
                }
            }
            String targetSQLType = sourceField.getType().getData(TypeMapping.SQL_TYPE);
            if (value != null && targetSQLType != null && "clob".equalsIgnoreCase(targetSQLType)) { //$NON-NLS-1$
                try {
                    Reader characterStream = ((Clob) value).getCharacterStream();
                    return new String(IOUtils.toCharArray(characterStream)); // No need to close (Hibernate seems to handle this).
                } catch (Exception e) {
                    throw new RuntimeException("Unexpected read from clob exception", e);
                }
            }
        }
        return value;
    }

    public ComplexTypeMetadata getDatabase() {
        return database;
    }

    public ComplexTypeMetadata getUser() {
        return user;
    }

    protected abstract void map(FieldMetadata user, FieldMetadata database);

    protected abstract void freeze();

    public abstract FieldMetadata getDatabase(FieldMetadata from);

    public abstract FieldMetadata getUser(FieldMetadata to);

    public String getName() {
        return database.getName();
    }

    /**
     * Set values <b>from</b> MDM representation <b>to</b> a Hibernate representation (i.e. POJOs).
     *
     * @param session A valid (opened) Hibernate session that might be used to resolve FK values.
     * @param from    A value from MDM (usually got with {@link com.amalto.core.storage.Storage#fetch(com.amalto.core.query.user.Expression)}
     * @param to      The Hibernate POJO where values should be set. Object is expected to implement {@link Wrapper} interface.
     */
    public abstract void setValues(Session session, DataRecord from, Wrapper to);

    /**
     * Set values <b>from</b> Hibernate representation <b>to</b> a MDM representation.
     *
     * @param from A Hibernate object that represents a MDM entity instance.
     * @param to   A MDM internal representation of the MDM record.
     * @return The modified version of <code>to</code>. In fact, return can be ignored for callers, this is a convenience
     *         for recursion.
     */
    public abstract DataRecord setValues(Wrapper from, DataRecord to);

    /**
     * @return A name for the timestamp in storage. The name is database dependent and represent name of a field in
     * underlying storage (column name, XPath...). Returns <code>null</code> if mapping has no database field for this.
     */
    public abstract String getDatabaseTimestamp();

    /**
     * @return A name for the task id in storage. The name is database dependent and represent name of a field in
     *         underlying storage (column name, XPath...). Returns <code>null</code> if mapping has no database field
     *         for this.
     */
    public abstract String getDatabaseTaskId();
}
