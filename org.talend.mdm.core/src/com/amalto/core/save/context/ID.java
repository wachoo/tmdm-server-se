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
import org.apache.log4j.Logger;
import org.talend.mdm.commmon.util.core.EUUIDCustomType;
import org.w3c.dom.*;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

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
        String universe = database.getUniverse();
        String dataCluster = context.getDataCluster();

        boolean hasMetAutoIncrement = false;
        MutableDocument userDocument = context.getUserDocument();
        String typeName = type.getName();
        for (FieldMetadata keyField : keyFields) {
            String keyFieldTypeName = keyField.getType().getName();
            Accessor userAccessor = userDocument.createAccessor(keyField.getName());

            // Get (or generate) ids.
            String generatedIdValue = null;
            String currentIdValue;
            if (EUUIDCustomType.UUID.getName().equalsIgnoreCase(keyFieldTypeName)) {
                if (userAccessor.exist()) {
                    generatedIdValue = userAccessor.get();
                }
                if (generatedIdValue == null || generatedIdValue.isEmpty()) { // Web UI generates empty elements!
                    generatedIdValue = java.util.UUID.randomUUID().toString();
                }
                if (userAccessor.exist()) {
                    userAccessor.set(generatedIdValue);
                } else {
                    userAccessor.createAndSet(generatedIdValue);
                }
                currentIdValue = generatedIdValue;
            } else if (EUUIDCustomType.AUTO_INCREMENT.getName().equalsIgnoreCase(keyFieldTypeName)) {
                if (userAccessor.exist()) {
                    generatedIdValue = userAccessor.get();
                }
                if (generatedIdValue == null || generatedIdValue.isEmpty()) { // Web UI generates empty elements!
                    generatedIdValue = String.valueOf(AutoIncrementGenerator.generateNum(universe, dataCluster, typeName + "." + keyField.getName().replaceAll("/", ".")));   //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                }
                if (userAccessor.exist()) {
                    userAccessor.set(generatedIdValue);
                } else {
                    userAccessor.createAndSet(generatedIdValue);
                }
                currentIdValue = generatedIdValue;
                hasMetAutoIncrement = true;
            } else {
                // Not a value to generate, first ensure value is correctly set.
                if (!userAccessor.exist()) {
                    throw new IllegalArgumentException("Expected id '" + keyField.getName() + "' to be set.");
                }
                currentIdValue = userAccessor.get();
            }

            ids.add(currentIdValue);
        }

        // TODO This is temporary! This is due to legacy behavior (consider empty elements as no op)!
        clean(userDocument.asDOM().getDocumentElement());

        // now has an id, so load database document
        String[] savedId = getSavedId();
        String revisionID = context.getRevisionID();
        DocumentBuilder documentBuilder;
        try {
            documentBuilder = SaverContextFactory.DOM_PARSER_FACTORY.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            throw new RuntimeException("Could not acquire a document builder.", e);
        }
        if (database.exist(dataCluster, typeName, revisionID, savedId)) {
            NonCloseableInputStream nonCloseableInputStream = new NonCloseableInputStream(database.get(dataCluster, typeName, revisionID, savedId));

            try {
                nonCloseableInputStream.mark(-1);

                Document databaseDomDocument = documentBuilder.parse(nonCloseableInputStream);
                Element userXmlElement = getUserXmlElement(databaseDomDocument);
                // TODO This is temporary! This is due to the Web UI that saves invalid documents!
                clean(userXmlElement);
                MutableDocument databaseDocument = new DOMDocument(userXmlElement);

                nonCloseableInputStream.reset();

                SkipAttributeDocumentBuilder validationDocumentBuilder = new SkipAttributeDocumentBuilder(documentBuilder);
                Document databaseValidationDomDocument = validationDocumentBuilder.parse(new InputSource(nonCloseableInputStream));
                userXmlElement = getUserXmlElement(databaseValidationDomDocument);
                // TODO This is temporary! This is due to the Web UI that saves invalid documents!
                clean(userXmlElement);
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
            context.setDatabaseDocument(new DOMDocument(documentBuilder.newDocument()));
            context.setDatabaseValidationDocument(new DOMDocument(documentBuilder.newDocument()));
        }

        // Continue save
        savedTypeName = context.getType().getName();
        context.setId(savedId);
        next.save(session, context);

        if (hasMetAutoIncrement) {
            // TODO This is somewhat bad: would be better to do it on commit.
            // Save current state of autoincrement when save is completed:
            AutoIncrementGenerator.saveToDB();
        }

    }

    private static void clean(Element element) {
        if (element == null) {
            return;
        }
        if (!isEmpty(element)) {
            NodeList children = element.getChildNodes();
            Set<Node> nodeToDelete = new HashSet<Node>();
            for (int i = 0; i < children.getLength(); i++) {
                Node node = children.item(i);
                if (node instanceof Element) {
                    Element currentElement = (Element) node;
                    if (isEmpty(currentElement)) {
                        // Instead of immediately deleting element... mark it (so children indexes don't change).
                        nodeToDelete.add(currentElement);
                    } else {
                        clean(currentElement);
                    }
                }
            }
            for (Node node : nodeToDelete) {
                node.getParentNode().removeChild(node);
            }
        }
    }

    private static boolean isEmpty(Element element) {
        if (element == null) {
            return true;
        }

        NodeList children = element.getChildNodes();
        if (children.getLength() == 0) {
            return true;
        } else {
            for (int i = 0; i < children.getLength(); i++) {
                Node node = children.item(i);
                if (node instanceof Element && !isEmpty((Element) node)) {
                    return false;
                } else if (node instanceof Text) {
                    if (!node.getTextContent().trim().isEmpty()) {
                        return false;
                    }
                }
            }
            return true;
        }
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
        return ids.toArray(new String[ids.size()]);
    }

    public String getSavedConceptName() {
        return savedTypeName;
    }

    public String getBeforeSavingMessage() {
        return next.getBeforeSavingMessage();
    }
}
