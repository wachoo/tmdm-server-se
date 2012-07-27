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

    private Map<String, FieldMetadata> userToDatabase = new HashMap<String, FieldMetadata>();

    private Map<String, FieldMetadata> databaseToUser = new HashMap<String, FieldMetadata>();

    private boolean isFrozen;

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

    void map(FieldMetadata user, FieldMetadata database) {
        if (isFrozen) {
            throw new IllegalStateException("Mapping is frozen.");
        }
        userToDatabase.put(user.getName(), database);
        databaseToUser.put(database.getName(), user);
    }

    public ComplexTypeMetadata getDatabase() {
        return database;
    }

    public ComplexTypeMetadata getUser() {
        return user;
    }

    public FieldMetadata getDatabase(FieldMetadata from) {
        return userToDatabase.get(from.getName());
    }

    FieldMetadata getUser(FieldMetadata to) {
        return databaseToUser.get(to.getName());
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
            Map<String, FieldMetadata> frozen = new HashMap<String, FieldMetadata>();
            for (Map.Entry<String, FieldMetadata> entry : userToDatabase.entrySet()) {
                frozen.put(entry.getKey(), entry.getValue().freeze());
            }
            userToDatabase = frozen;
            frozen = new HashMap<String, FieldMetadata>();
            for (Map.Entry<String, FieldMetadata> entry : databaseToUser.entrySet()) {
                frozen.put(entry.getKey(), entry.getValue().freeze());
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
