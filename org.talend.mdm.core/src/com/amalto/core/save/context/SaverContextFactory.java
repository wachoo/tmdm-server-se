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
import com.amalto.core.save.DOMDocument;
import com.amalto.core.save.DocumentSaverContext;
import com.amalto.core.save.ReportDocumentSaverContext;
import org.talend.mdm.commmon.util.webapp.XObjectType;
import org.talend.mdm.commmon.util.webapp.XSystemObjects;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import java.io.InputStream;
import java.util.Map;

public class SaverContextFactory {

    public static final DocumentBuilder DOM_PARSER_FACTORY;

    private static final Map<String, XSystemObjects> SYSTEM_DATA_CLUSTERS = XSystemObjects.getXSystemObjects(XObjectType.DATA_CLUSTER);

    private static final SAXParserFactory SAX_PARSER_FACTORY = SAXParserFactory.newInstance();

    static {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        factory.setIgnoringComments(true);
        factory.setValidating(false);
        factory.setIgnoringElementContentWhitespace(true);
        try {
            DOM_PARSER_FACTORY = factory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            throw new RuntimeException("Unable to initialize DOM parser.", e);
        }

        SAX_PARSER_FACTORY.setNamespaceAware(false);
        SAX_PARSER_FACTORY.setValidating(false);
    }

    public DocumentSaverContext create(String dataCluster,
                                       String dataModelName,
                                       InputStream documentStream) {
        // Parsing
        MutableDocument userDocument;
        try {
            Document userDomDocument = DOM_PARSER_FACTORY.parse(new InputSource(documentStream));
            userDocument = new DOMDocument(userDomDocument);
        } catch (Exception e) {
            throw new RuntimeException("Unable to parse document to save.", e);
        }

        // Choose right context implementation
        DocumentSaverContext context;
        if (dataCluster.startsWith("amalto") || XSystemObjects.isXSystemObject(SYSTEM_DATA_CLUSTERS, XObjectType.DATA_CLUSTER, dataCluster)) { //$NON-NLS-1$
            context = new SystemContext(dataCluster, userDocument);
        } else {
            context = new UserContext(dataCluster, dataModelName, userDocument, false, false);
        }

        return context;
    }

    public DocumentSaverContext create(String dataCluster,
                                       String dataModelName,
                                       String changeSource,
                                       InputStream documentStream,
                                       boolean updateReport,
                                       boolean invokeBeforeSaving) {
        if (invokeBeforeSaving && !updateReport) {
            throw new IllegalArgumentException("Must generate update report to invoke before saving.");
        }

        // Parsing
        MutableDocument userDocument;
        try {
            Document userDomDocument = DOM_PARSER_FACTORY.parse(new InputSource(documentStream));
            userDocument = new DOMDocument(userDomDocument);
        } catch (Exception e) {
            throw new RuntimeException("Unable to parse document to save.", e);
        }

        // Choose right context implementation
        DocumentSaverContext context;
        if (dataCluster.startsWith("amalto") || XSystemObjects.isXSystemObject(SYSTEM_DATA_CLUSTERS, XObjectType.DATA_CLUSTER, dataCluster)) { //$NON-NLS-1$
            context = new SystemContext(dataCluster, userDocument);
        } else {
            context = new UserContext(dataCluster, dataModelName, userDocument, updateReport, invokeBeforeSaving);
        }

        return ReportDocumentSaverContext.decorate(context, changeSource);
    }
}
