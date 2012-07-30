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
import com.amalto.core.save.SaverSession;
import com.amalto.core.save.UserAction;
import com.amalto.core.util.OutputReport;
import com.amalto.core.util.Util;
import com.sun.org.apache.xpath.internal.XPathAPI;
import org.apache.commons.lang.StringUtils;
import org.jboss.logging.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class BeforeSaving implements DocumentSaver {

    public static final String BEFORE_SAVING_VALIDATION_MESSAGE_PREFIX = "BeforeSaving Validation Error --> "; //$NON-NLS-1$

    private static final Logger LOGGER = Logger.getLogger(BeforeSaving.class);

    private DocumentSaver next;

    private String message = StringUtils.EMPTY;

    BeforeSaving(DocumentSaver next) {
        this.next = next;
    }

    public void save(SaverSession session, DocumentSaverContext context) {
        // Invoke the beforeSaving
        MutableDocument updateReportDocument = context.getUpdateReportDocument();
        if (updateReportDocument == null) {
            throw new IllegalStateException("Update report is missing.");
        }
        OutputReport outputreport = session.getSaverSource().invokeBeforeSaving(context, updateReportDocument);

        if (outputreport != null) { // when a process was found
            String errorCode;
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Output report: message present (" + String.valueOf(outputreport.getMessage() != null) + ") ");
                LOGGER.debug("Output report: item present (" + String.valueOf(outputreport.getItem() != null) + ") ");
            }

            try {
                if (outputreport.getMessage() == null && outputreport.getItem() == null) {
                    throw new RuntimeException(BEFORE_SAVING_VALIDATION_MESSAGE_PREFIX + " No message");
                }

                if (outputreport.getMessage() != null) {
                    message = outputreport.getMessage();
                    if (LOGGER.isDebugEnabled()) {
                        LOGGER.debug("Output report found from before saving process");
                        LOGGER.debug(message);
                    }
                    Document doc = Util.parse(message);
                    // handle output_report
                    String xpath = "//report/message"; //$NON-NLS-1$
                    Node errorNode = XPathAPI.selectSingleNode(doc, xpath);
                    if (errorNode instanceof Element) {
                        Element errorElement = (Element) errorNode;
                        errorCode = errorElement.getAttribute("type"); //$NON-NLS-1$
                        Node child = errorElement.getFirstChild();
                        if (child instanceof org.w3c.dom.Text) {
                            message = child.getTextContent();
                        }
                        if (!"info".equals(errorCode)) { //$NON-NLS-1$
                            throw new RuntimeException(BEFORE_SAVING_VALIDATION_MESSAGE_PREFIX + message);
                        }
                    }
                } else {
                    if (LOGGER.isDebugEnabled()) {
                        LOGGER.debug("No output report from before saving process");
                    }
                }

                // handle output_item
                if (outputreport.getItem() != null) {
                    if (LOGGER.isDebugEnabled()) {
                        LOGGER.debug("Found modified item from before saving process");
                    }
                    String xpath = "//exchange/item"; //$NON-NLS-1$
                    Document doc = Util.parse(outputreport.getItem());
                    Node item = XPathAPI.selectSingleNode(doc, xpath);
                    if (item != null && item instanceof Element) {
                        NodeList list = item.getChildNodes();
                        Node node = null;
                        for (int i = 0; i < list.getLength(); i++) {
                            if (list.item(i) instanceof Element) {
                                node = list.item(i);
                                break;
                            }
                        }
                        if (node != null) {
                            // set back the modified item by the process
                            if (!node.getNodeName().equals(context.getType().getName())) {
                                message = StringUtils.EMPTY;
                                throw new IllegalArgumentException("Before saving returned a document typed '" + node.getNodeName() + "' but expected '" + context.getType().getName() + "'");
                            }
                            context.setUserDocument(new DOMDocument(node));
                            if (context.getUserAction() == UserAction.CREATE) {
                                context.setId(new String[0]); // Will re-read id from document.
                            }
                            // Redo a set of actions and security checks.
                            next = new GenerateActions(new Security(next));
                        }
                    }
                } else {
                    if (LOGGER.isDebugEnabled()) {
                        LOGGER.debug("No modified item from before saving process");
                    }
                }
            } catch (Exception e) {
                throw new RuntimeException("Exception occurred during before saving phase.", e);
            }
        }

        next.save(session, context);
    }

    public String[] getSavedId() {
        return next.getSavedId();
    }

    public String getSavedConceptName() {
        return next.getSavedConceptName();
    }

    public String getBeforeSavingMessage() {
        return message;
    }
}


