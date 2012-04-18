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

import com.amalto.core.ejb.local.XmlServerSLWrapperLocal;
import com.amalto.core.history.MutableDocument;
import com.amalto.core.load.action.LoadAction;
import com.amalto.core.save.DOMDocument;
import com.amalto.core.save.DocumentSaverContext;
import com.amalto.core.save.ReportDocumentSaverContext;
import com.amalto.core.util.XSDKey;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.talend.mdm.commmon.util.webapp.XObjectType;
import org.talend.mdm.commmon.util.webapp.XSystemObjects;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.SAXParserFactory;
import java.io.InputStream;
import java.util.Map;

public class SaverContextFactory {

    public static final DocumentBuilderFactory DOM_PARSER_FACTORY;

    private static final Map<String, XSystemObjects> SYSTEM_DATA_CLUSTERS = XSystemObjects.getXSystemObjects(XObjectType.DATA_CLUSTER);

    private static final SAXParserFactory SAX_PARSER_FACTORY = SAXParserFactory.newInstance();

    private static final String SYSTEM_CONTAINER_PREFIX = "amalto";  //$NON-NLS-1$

    private static DocumentSaverExtension saverExtension;

    static DocumentSaver invokeSaverExtension(DocumentSaver saver) {
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
        DOM_PARSER_FACTORY = DocumentBuilderFactory.newInstance();
        DOM_PARSER_FACTORY.setNamespaceAware(true);
        DOM_PARSER_FACTORY.setIgnoringComments(true);
        DOM_PARSER_FACTORY.setValidating(false);
        DOM_PARSER_FACTORY.setIgnoringElementContentWhitespace(true);
        DOM_PARSER_FACTORY.setValidating(false);

        SAX_PARSER_FACTORY.setNamespaceAware(false);
        SAX_PARSER_FACTORY.setValidating(false);
    }

    /**
     * Creates a {@link DocumentSaverContext} for bulk load operations.
     *
     * @param dataCluster    Data container name (must exist).
     * @param dataModelName  Data model name (must exist).
     * @param keyMetadata    Key for all records contained in <code>documentStream</code>
     * @param documentStream AData model name (must exist).
     * @param loadAction     {@link LoadAction} to be used to bulk load records.
     * @param server         Abstraction of the underlying MDM database.
     * @return A context configured for bulk load.
     */
    public DocumentSaverContext createBulkLoad(String dataCluster,
                                               String dataModelName,
                                               XSDKey keyMetadata,
                                               InputStream documentStream,
                                               LoadAction loadAction,
                                               XmlServerSLWrapperLocal server) {
        return new BulkLoadContext(dataCluster, dataModelName, keyMetadata, documentStream, loadAction, server);
    }

    /**
     * Creates a {@link DocumentSaverContext} to save a unique record in MDM.
     *
     * @param dataCluster    Data container name (must exist).
     * @param dataModelName  Data model name (must exist).
     * @param documentStream A stream that contains one XML document.
     * @return A context configured to save a record in MDM.
     */
    public DocumentSaverContext create(String dataCluster,
                                       String dataModelName,
                                       InputStream documentStream) {
        return create(dataCluster, dataModelName, StringUtils.EMPTY, documentStream, true, false, false);
    }

    /**
     * Creates a {@link DocumentSaverContext} to save a unique record in MDM, with update report/before saving options.
     *
     * @param dataCluster        Data container name (must exist).
     * @param dataModelName      Data model name (must exist).
     * @param changeSource       Source of change (for update report). Common values includes 'genericUI'...
     * @param documentStream     A stream that contains one XML document.
     * @param validate           <code>true</code> to validate XML document before saving it, <code>false</code> otherwise.
     * @param updateReport       <code>true</code> to generate an update report, <code>false</code> otherwise.
     * @param invokeBeforeSaving <code>true</code> to invoke any existing before saving process, <code>false</code> otherwise.
     * @return A context configured to save a record in MDM.
     */
    public DocumentSaverContext create(String dataCluster,
                                       String dataModelName,
                                       String changeSource,
                                       InputStream documentStream,
                                       boolean validate,
                                       boolean updateReport,
                                       boolean invokeBeforeSaving) {
        if (invokeBeforeSaving && !updateReport) {
            throw new IllegalArgumentException("Must generate update report to invoke before saving.");
        }

        // Parsing
        MutableDocument userDocument;
        try {
            DocumentBuilder documentBuilder = DOM_PARSER_FACTORY.newDocumentBuilder();
            InputSource source = new InputSource(documentStream);
            Document userDomDocument = documentBuilder.parse(source);
            userDocument = new DOMDocument(userDomDocument);
        } catch (Exception e) {
            throw new RuntimeException("Unable to parse document to save.", e);
        }

        // Choose right context implementation
        DocumentSaverContext context;
        if (dataCluster.startsWith(SYSTEM_CONTAINER_PREFIX) || XSystemObjects.isXSystemObject(SYSTEM_DATA_CLUSTERS, XObjectType.DATA_CLUSTER, dataCluster)) { //$NON-NLS-1$
            context = new SystemContext(dataCluster, dataModelName, userDocument);
        } else {
            context = new UserContext(dataCluster, dataModelName, userDocument, validate, updateReport, invokeBeforeSaving);
        }

        if (updateReport) {
            return ReportDocumentSaverContext.decorate(context, changeSource);
        } else {
            return context;
        }
    }
}
