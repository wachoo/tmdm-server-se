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
import com.amalto.core.load.action.LoadAction;
import com.amalto.core.save.*;
import com.amalto.core.schema.validation.SkipAttributeDocumentBuilder;
import com.amalto.core.server.XmlServer;
import com.amalto.core.util.XSDKey;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.talend.mdm.commmon.util.webapp.XObjectType;
import org.talend.mdm.commmon.util.webapp.XSystemObjects;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.InputStream;
import java.util.Map;

public class SaverContextFactory {

    public static final DocumentBuilder DOCUMENT_BUILDER;

    private static final Map<String, XSystemObjects> SYSTEM_DATA_CLUSTERS = XSystemObjects.getXSystemObjects(XObjectType.DATA_CLUSTER);

    private static final String SYSTEM_CONTAINER_PREFIX = "amalto";  //$NON-NLS-1$

    private static DocumentSaverExtension saverExtension;

    @SuppressWarnings("unchecked")
    static synchronized DocumentSaver invokeSaverExtension(DocumentSaver saver) {
        if (saverExtension == null) {
            try {
                Class<DocumentSaverExtension> extension = (Class<DocumentSaverExtension>) Class.forName("com.amalto.core.save.DocumentSaverExtensionImpl"); //$NON-NLS-1$
                saverExtension = extension.newInstance();
            } catch (ClassNotFoundException e) {
                Logger.getLogger(UserContext.class).warn("No extension found for save.");
                saverExtension = new DocumentSaverExtension() {
                    public DocumentSaver invokeSaverExtension(DocumentSaver saver) {
                        return saver;
                    }
                };
            } catch (Exception e) {
                throw new RuntimeException("Unexpected exception occurred during saver extension lookup.");
            }
        }

        return saverExtension.invokeSaverExtension(saver);
    }

    static {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        factory.setIgnoringComments(true);
        factory.setValidating(false);
        try {
            factory.setFeature("http://apache.org/xml/features/dom/defer-node-expansion", false);
            DOCUMENT_BUILDER = factory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            throw new RuntimeException("Could not acquire a document builder.", e);
        }
    }

    /**
     * Creates a {@link DocumentSaverContext} for bulk load operations.
     *
     *
     * @param dataCluster    Data container name (must exist).
     * @param dataModelName  Data model name (must exist).
     * @param keyMetadata    Key for all records contained in <code>documentStream</code>
     * @param documentStream AData model name (must exist).
     * @param loadAction     {@link com.amalto.core.load.action.LoadAction} to be used to bulk load records.
     * @param server         Abstraction of the underlying MDM database.
     * @return A context configured for bulk load.
     */
    public DocumentSaverContext createBulkLoad(String dataCluster,
                                               String dataModelName,
                                               XSDKey keyMetadata,
                                               InputStream documentStream,
                                               LoadAction loadAction,
                                               XmlServer server) {
        return new BulkLoadContext(dataCluster, dataModelName, keyMetadata, documentStream, loadAction, server);
    }

    /**
     * Creates a {@link DocumentSaverContext} to save a unique record in MDM.
     *
     * @param dataCluster    Data container name (must exist).
     * @param dataModelName  Data model name (must exist).
     * @param isReplace      <code>true</code> to replace XML document if it exists in database, <code>false</code>
     *                       otherwise. If it is a creation, this parameter is ignored.
     * @param documentStream A stream that contains one XML document.
     * @return A context configured to save a record in MDM.
     */
    public DocumentSaverContext create(String dataCluster,
                                       String dataModelName,
                                       boolean isReplace,
                                       InputStream documentStream) {
        return create(dataCluster,
                dataModelName,
                StringUtils.EMPTY,
                documentStream,
                isReplace,
                true,
                false,
                false,
                XSystemObjects.DC_PROVISIONING.getName().equals(dataCluster));
    }

    /**
     * Creates a {@link DocumentSaverContext} to save a unique record in MDM, with update report/before saving options.
     *
     * @param dataCluster        Data container name (must exist).
     * @param dataModelName      Data model name (must exist).
     * @param changeSource       Source of change (for update report). Common values includes 'genericUI'...
     * @param documentStream     A stream that contains one XML document.
     * @param isReplace          <code>true</code> to replace XML document if it exists in database, <code>false</code>
     *                           otherwise. If it is a creation, this parameter is ignored.
     * @param validate           <code>true</code> to validate XML document before saving it, <code>false</code> otherwise.
     * @param updateReport       <code>true</code> to generate an update report, <code>false</code> otherwise.
     * @param invokeBeforeSaving <code>true</code> to invoke any existing before saving process, <code>false</code> otherwise.
     * @param autoCommit         <code>true</code> to perform a call to {@link SaverSession#end()} when a record is ready for save.
     * @return A context configured to save a record in MDM.
     */
    public DocumentSaverContext create(String dataCluster,
                                       String dataModelName,
                                       String changeSource,
                                       InputStream documentStream,
                                       boolean isReplace,
                                       boolean validate,
                                       boolean updateReport,
                                       boolean invokeBeforeSaving,
                                       boolean autoCommit) {
        if (invokeBeforeSaving && !updateReport) {
            throw new IllegalArgumentException("Must generate update report to invoke before saving.");
        }
        // Parsing
        MutableDocument userDocument;
        try {
            // Don't ignore talend internal attributes when parsing this document
            DocumentBuilder documentBuilder = new SkipAttributeDocumentBuilder(DOCUMENT_BUILDER, false);
            InputSource source = new InputSource(documentStream);
            Document userDomDocument = documentBuilder.parse(source);
            userDocument = new DOMDocument(userDomDocument);
        } catch (Exception e) {
            throw new RuntimeException("Unable to parse document to save.", e);
        }
        // Choose right user action
        UserAction userAction = UserAction.UPDATE;
        if (isReplace) {
            userAction = UserAction.REPLACE;
        }
        // TMDM-5587: workflow uses 'update' for both 'update' and 'create' (so choose 'auto').
        if ("workflow".equalsIgnoreCase(changeSource)) { //$NON-NLS-1$
            userAction = UserAction.AUTO;
        }
        // Choose right context implementation
        DocumentSaverContext context;
        if (dataCluster.startsWith(SYSTEM_CONTAINER_PREFIX)
                || XSystemObjects.isXSystemObject(SYSTEM_DATA_CLUSTERS, dataCluster)) {
            context = new SystemContext(dataCluster, dataModelName, userDocument, userAction);
        } else {
            context = new UserContext(dataCluster, dataModelName, userDocument, userAction, validate, updateReport, invokeBeforeSaving);
        }
        // Additional options (update report, auto commit).
        if (updateReport) {
            context = ReportDocumentSaverContext.decorate(context, changeSource);
        }
        if (autoCommit) {
            context = AutoCommitSaverContext.decorate(context);
        }
        return context;
    }

    /**
     * Creates a {@link DocumentSaverContext} to save a unique record in MDM, with update report/before saving options.
     * This method is dedicated to partial update (with the <code>overwrite</code> parameter).
     *
     * @param dataCluster    Data container name (must exist).
     * @param dataModelName  Data model name (must exist).
     * @param changeSource   Source of change (for update report). Common values includes 'genericUI'...
     * @param documentStream A stream that contains one XML document.
     * @param validate       <code>true</code> to validate XML document before saving it, <code>false</code> otherwise.
     * @param updateReport   <code>true</code> to generate an update report, <code>false</code> otherwise.
     * @param pivot          XPath to be used when iterating over a many valued element.
     * @param key            XPath to uniquely identify a element within the many valued element reachable from <code>pivot</code>.
     * @param overwrite      <code>false</code> will preserve all collections values in original document (new values
     *                       will be added at the end of the collection). <code>true</code> will overwrite all previous
     */
    public DocumentSaverContext createPartialUpdate(String dataCluster,
                                                    String dataModelName,
                                                    String changeSource,
                                                    InputStream documentStream,
                                                    boolean validate,
                                                    boolean updateReport,
                                                    String pivot,
                                                    String key,
                                                    boolean overwrite) {
        // TODO Support before saving in case of partial update (set to "true" beforeSaving parameter to support it).
        DocumentSaverContext context = create(dataCluster,
                dataModelName,
                changeSource,
                documentStream,
                false, // Never do a "replace" when doing a partial update.
                validate,
                updateReport,
                false, XSystemObjects.DC_PROVISIONING.getName().equals(dataCluster)); // Before saving is not supported
        return PartialUpdateSaverContext.decorate(context, pivot, key, overwrite);
    }
}
