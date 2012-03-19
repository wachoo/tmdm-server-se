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

import com.amalto.core.history.MutableDocument;
import com.amalto.core.history.accessor.Accessor;
import com.amalto.core.metadata.ComplexTypeMetadata;
import com.amalto.core.metadata.FieldMetadata;
import com.amalto.core.save.DOMDocument;
import com.amalto.core.save.DocumentSaverContext;
import com.amalto.core.save.SaverSession;
import com.amalto.core.schema.validation.SkipAttributeDocumentBuilder;
import com.amalto.core.util.AutoIncrementGenerator;
import org.talend.mdm.commmon.util.core.EUUIDCustomType;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import java.util.LinkedList;
import java.util.List;

class ID implements DocumentSaver {

    private final DocumentSaver next;

    private final List<String> ids = new LinkedList<String>();

    ID(DocumentSaver next) {
        this.next = next;
    }

    public void save(SaverSession session, DocumentSaverContext context) {
        try {
            ComplexTypeMetadata type = context.getType();
            List<FieldMetadata> keyFields = type.getKeyFields();
            SaverSource database = context.getDatabase();
            String universe = database.getUniverse();
            String dataCluster = context.getDataCluster();

            boolean hasMetAutoIncrement = false;
            MutableDocument userDocument = context.getUserDocument();
            for (FieldMetadata keyField : keyFields) {
                String keyFieldTypeName = keyField.getType().getName();
                Accessor userAccessor = userDocument.createAccessor(keyField.getName());

                // Get (or generate) ids.
                String generatedIdValue = null;
                String currentIdValue;
                if (EUUIDCustomType.UUID.getName().equalsIgnoreCase(keyFieldTypeName)) {
                    if (userAccessor.exist()) {
                        generatedIdValue = userAccessor.get();
                    } else {
                        generatedIdValue = java.util.UUID.randomUUID().toString();
                    }
                    currentIdValue = generatedIdValue;
                } else if (EUUIDCustomType.AUTO_INCREMENT.getName().equalsIgnoreCase(keyFieldTypeName)) {
                    if (userAccessor.exist()) {
                        generatedIdValue = userAccessor.get();
                    } else {
                        generatedIdValue = String.valueOf(AutoIncrementGenerator.generateNum(universe, dataCluster, type.getName() + "." + keyField.getName().replaceAll("/", ".")));
                        hasMetAutoIncrement = true;
                    }
                    currentIdValue = generatedIdValue;
                } else {
                    // Not a value to generate, first ensure value is correctly set.
                    if (!userAccessor.exist()) {
                        throw new IllegalArgumentException("Expected id '" + keyField.getName() + "' to be set.");
                    }
                    currentIdValue = userAccessor.get();
                }

                if (generatedIdValue != null) {
                    userAccessor.createAndSet(generatedIdValue);
                }
                ids.add(currentIdValue);
            }

            // now has an id, so load database document
            String[] savedId = getSavedId();
            if (database.exist(savedId)) {
                NonCloseableInputStream nonCloseableInputStream = new NonCloseableInputStream(database.get(savedId));

                try {
                    nonCloseableInputStream.mark(-1);

                    Document databaseDomDocument = SaverContextFactory.DOM_PARSER_FACTORY.parse(nonCloseableInputStream);
                    MutableDocument databaseDocument = new DOMDocument(databaseDomDocument);

                    nonCloseableInputStream.reset();

                    SkipAttributeDocumentBuilder documentBuilder = new SkipAttributeDocumentBuilder(SaverContextFactory.DOM_PARSER_FACTORY);
                    Document databaseValidationDomDocument = documentBuilder.parse(new InputSource(nonCloseableInputStream));
                    MutableDocument databaseValidationDocument = new DOMDocument(databaseValidationDomDocument);

                    context.setDatabaseDocument(databaseDocument);
                    context.setDatabaseValidationDocument(databaseValidationDocument);
                } finally {
                    nonCloseableInputStream.forceClose();
                }
            } else {
                context.setDatabaseDocument(new DOMDocument(SaverContextFactory.DOM_PARSER_FACTORY.newDocument()));
                context.setDatabaseValidationDocument(new DOMDocument(SaverContextFactory.DOM_PARSER_FACTORY.newDocument()));
            }

            // Continue save
            context.setId(savedId);
            next.save(session, context);

            if (hasMetAutoIncrement) {
                // TODO This is somewhat bad: would be better to do it on commit.
                // Save current state of autoincrement when save is completed:
                AutoIncrementGenerator.saveToDB();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String[] getSavedId() {
        return ids.toArray(new String[ids.size()]);
    }
}
