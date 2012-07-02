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

import com.amalto.core.metadata.ComplexTypeMetadata;
import com.amalto.core.metadata.FieldMetadata;
import com.amalto.core.storage.record.DataRecord;
import org.hibernate.Session;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents type mapping between data model as specified by the user and data model as used by hibernate storage.
 */
public abstract class TypeMapping {

    private final ComplexTypeMetadata database;

    private final ComplexTypeMetadata user;

    final MappingRepository mappings;

    private Map<FieldMetadata, FieldMetadata> userToDatabase = new HashMap<FieldMetadata, FieldMetadata>();

    private Map<FieldMetadata, FieldMetadata> databaseToUser = new HashMap<FieldMetadata, FieldMetadata>();

    private boolean isFrozen;

    TypeMapping(ComplexTypeMetadata user, MappingRepository mappings) {
        this(user, (ComplexTypeMetadata) user.copyShallow(), mappings);
    }

    TypeMapping(ComplexTypeMetadata user, ComplexTypeMetadata database, MappingRepository mappings) {
        this.user = user;
        this.database = database;
        this.mappings = mappings;
    }

    void map(FieldMetadata user, FieldMetadata database) {
        if (isFrozen) {
            throw new IllegalStateException("Mapping is frozen.");
        }
        userToDatabase.put(user, database);
        databaseToUser.put(database, user);
    }

    public ComplexTypeMetadata getDatabase() {
        return database;
    }

    public ComplexTypeMetadata getUser() {
        return user;
    }

    public FieldMetadata getDatabase(FieldMetadata from) {
        return userToDatabase.get(from);
    }

    FieldMetadata getUser(FieldMetadata to) {
        return databaseToUser.get(to);
    }

    public void freeze() {
        if (!isFrozen) {
            // Ensure mapped type are frozen.
            try {
                database.freeze();
            } catch (Exception e) {
                throw new RuntimeException("Could not process internal type '" + database.getName() + "'.", e);
            }
            try {
                user.freeze();
            } catch (Exception e) {
                throw new RuntimeException("Could not process user type '" + user.getName() + "'.", e);
            }

            // Freeze field mappings.
            Map<FieldMetadata, FieldMetadata> frozen = new HashMap<FieldMetadata, FieldMetadata>();
            for (Map.Entry<FieldMetadata, FieldMetadata> entry : userToDatabase.entrySet()) {
                frozen.put(entry.getKey().freeze(), entry.getValue().freeze());
            }
            userToDatabase = frozen;
            frozen = new HashMap<FieldMetadata, FieldMetadata>();
            for (Map.Entry<FieldMetadata, FieldMetadata> entry : databaseToUser.entrySet()) {
                frozen.put(entry.getKey().freeze(), entry.getValue().freeze());
            }
            databaseToUser = frozen;

            isFrozen = true;
        }
    }

    public String getName() {
        return database.getName();
    }

    public abstract void setValues(Session session, DataRecord from, Wrapper to);

    public abstract DataRecord setValues(Wrapper from, DataRecord to);
}
