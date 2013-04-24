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
import com.amalto.core.save.UserAction;
import com.amalto.core.schema.validation.SkipAttributeDocumentBuilder;
import org.apache.log4j.Logger;
import org.talend.mdm.commmon.util.core.EUUIDCustomType;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

class ID implements DocumentSaver {

    private static final Logger LOGGER = Logger.getLogger(ID.class);

    private final DocumentSaver next;

    private final List<String> ids = new LinkedList<String>();

    private String savedTypeName;

    ID(DocumentSaver next) {
        this.next = next;
    }

    public void save(SaverSession session, DocumentSaverContext context) {
        ComplexTypeMetadata type = context.getType();
        if (type == null) {
            throw new IllegalStateException("Type of record being saved is expected to be set at this step.");
        }
        List<FieldMetadata> keyFields = type.getKeyFields();
        SaverSource database = session.getSaverSource();
        String dataCluster = context.getDataCluster();

        MutableDocument userDocument = context.getUserDocument();
        String typeName = type.getName();
        for (FieldMetadata keyField : keyFields) {
            String keyFieldTypeName = keyField.getType().getName();
            Accessor userAccessor = userDocument.createAccessor(keyField.getName());

            // Get ids.
            String currentIdValue;
            if (EUUIDCustomType.UUID.getName().equalsIgnoreCase(keyFieldTypeName) || EUUIDCustomType.AUTO_INCREMENT.getName().equalsIgnoreCase(keyFieldTypeName)) {
                if (userAccessor.exist()) { // Ignore cases where id is not there (will usually mean this is a creation).
                    String value = userAccessor.get();
                    if (!value.trim().isEmpty()) {
                        currentIdValue = value;
                    } else {
                        currentIdValue = null;  // Value is empty, don't consider this as an ID value.
                    }
                } else {
                    currentIdValue = null; // Element does not exist, don't consider this as an ID value.
                }
            } else {
                // Not a value to generate, first ensure value is correctly set.
                if (!userAccessor.exist()) {
                    throw new IllegalArgumentException("Expected id '" + keyField.getName() + "' to be set.");
                }
                currentIdValue = userAccessor.get();
            }

            if (currentIdValue != null) {
                ids.add(currentIdValue);
            }
        }

        // now has an id, so load database document
        String[] xmlDocumentId = ids.toArray(new String[ids.size()]);
        String revisionID = context.getRevisionID();
        DocumentBuilder documentBuilder;
        DocumentBuilder validationDocumentBuilder;
        try {
            documentBuilder = new SkipAttributeDocumentBuilder(SaverContextFactory.DOM_PARSER_FACTORY.newDocumentBuilder(), false);
        } catch (ParserConfigurationException e) {
            throw new RuntimeException("Could not acquire a document builder.", e);
        }
        try {
            validationDocumentBuilder = new SkipAttributeDocumentBuilder(SaverContextFactory.DOM_PARSER_FACTORY.newDocumentBuilder(), true);
        } catch (ParserConfigurationException e) {
            throw new RuntimeException("Could not acquire a document builder for validation.", e);
        }
        if (xmlDocumentId.length > 0 && database.exist(dataCluster, typeName, revisionID, xmlDocumentId)) {
            if (context.getUserAction() == UserAction.AUTO) {
                context.setUserAction(UserAction.UPDATE);
            }
            context.setId(xmlDocumentId);
            NonCloseableInputStream nonCloseableInputStream = new NonCloseableInputStream(database.get(dataCluster, typeName, revisionID, xmlDocumentId));
            try {
                nonCloseableInputStream.mark(-1);

                Document databaseDomDocument = documentBuilder.parse(nonCloseableInputStream);
                Element userXmlElement = getUserXmlElement(databaseDomDocument);
                MutableDocument databaseDocument = new DOMDocument(userXmlElement);

                nonCloseableInputStream.reset();

                Document databaseValidationDomDocument = validationDocumentBuilder.parse(new InputSource(nonCloseableInputStream));
                userXmlElement = getUserXmlElement(databaseValidationDomDocument);
                MutableDocument databaseValidationDocument = new DOMDocument(userXmlElement);

                context.setDatabaseDocument(databaseDocument);
                context.setDatabaseValidationDocument(databaseValidationDocument);
            } catch (Exception e) {
                throw new RuntimeException("Exception occurred during database document parsing", e);
            } finally {
                try {
                    nonCloseableInputStream.forceClose();
                } catch (IOException e) {
                    LOGGER.error("Exception occurred during close of stream.", e);
                }
            }
        } else {
            // Throw an exception if trying to update a document that does not exist.
            switch (context.getUserAction()) {
                case AUTO:
                case CREATE:
                case REPLACE:
                    break;
                case UPDATE:
                case PARTIAL_UPDATE:
                    StringBuilder builder = new StringBuilder();
                    for (String idElement : xmlDocumentId) {
                        builder.append('[').append(idElement).append(']');
                    }
                    throw new IllegalStateException("Can not update document '" + type.getName() + "' with id '" + builder.toString() + "' because it does not exist.");
            }
            // Creation... so mark context
            context.setUserAction(UserAction.CREATE);
            context.setDatabaseDocument(new DOMDocument(documentBuilder.newDocument()));
            context.setDatabaseValidationDocument(new DOMDocument(documentBuilder.newDocument()));
        }

        // Continue save
        savedTypeName = context.getType().getName();
        next.save(session, context);
    }

    private static Element getUserXmlElement(Document databaseDomDocument) {
        NodeList userXmlPayloadElement = databaseDomDocument.getElementsByTagName("p"); //$NON-NLS-1$
        if (userXmlPayloadElement.getLength() > 1) {
            throw new IllegalStateException("Document has multiple payload elements.");
        }
        NodeList children = userXmlPayloadElement.item(0).getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            if (children.item(i) instanceof Element) {
                return (Element) children.item(i);
            }
        }
        throw new IllegalStateException("Element 'p' is expected to have an XML element as child.");
    }

    public String[] getSavedId() {
        return next.getSavedId();
    }

    public String getSavedConceptName() {
        return savedTypeName;
    }

    public String getBeforeSavingMessage() {
        return next.getBeforeSavingMessage();
    }
}
