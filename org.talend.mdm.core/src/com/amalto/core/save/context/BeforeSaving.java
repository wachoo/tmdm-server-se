/*
 * Copyright (C) 2006-2012 Talend Inc. - www.talend.com
 * 
 * This source code is available under agreement available at
 * %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
 * 
 * You should have received a copy of the agreement along with this program; if not, write to Talend SA 9 rue Pages
 * 92150 Suresnes, France
 */

package com.amalto.core.save.context;

import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.amalto.core.history.MutableDocument;
import com.amalto.core.save.DOMDocument;
import com.amalto.core.save.DocumentSaverContext;
import com.amalto.core.save.SaverSession;
import com.amalto.core.save.UserAction;
import com.amalto.core.util.OutputReport;
import com.amalto.core.util.Util;
import com.sun.org.apache.xpath.internal.XPathAPI;

public class BeforeSaving implements DocumentSaver {

    public static final String BEFORE_SAVING_VALIDATION_MESSAGE_PREFIX = "BeforeSaving Validation Error --> "; //$NON-NLS-1$

    private DocumentSaver next;

    private String message = StringUtils.EMPTY;

    BeforeSaving(DocumentSaver next) {
        this.next = next;
    }

    @Override
    public void save(SaverSession session, DocumentSaverContext context) {
        // Invoke the beforeSaving
        MutableDocument updateReportDocument = context.getUpdateReportDocument();
        if (updateReportDocument == null) {
            throw new IllegalStateException("Update report is missing.");
        }
        OutputReport outputreport = session.getSaverSource().invokeBeforeSaving(context, updateReportDocument);

        if (outputreport != null) { // when a process was found
            String errorCode;
            message = outputreport.getMessage();
            try {
                Document doc = Util.parse(message);
                // handle output_report
                String xpath = "//report/message"; //$NON-NLS-1$
                Node errorNode = XPathAPI.selectSingleNode(doc, xpath);
                errorCode = null;
                if (errorNode instanceof Element) {
                    Element errorElement = (Element) errorNode;
                    errorCode = errorElement.getAttribute("type"); //$NON-NLS-1$
                    Node child = errorElement.getFirstChild();
                    if (child instanceof org.w3c.dom.Text) {
                        message = child.getTextContent();
                    }
                }

                if (!"info".equals(errorCode)) { //$NON-NLS-1$
                    throw new RuntimeException(BEFORE_SAVING_VALIDATION_MESSAGE_PREFIX + message);
                }

                // handle output_item
                if (outputreport.getItem() != null) {
                    xpath = "//exchange/item"; //$NON-NLS-1$
                    doc = Util.parse(outputreport.getItem());
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
                            context.setUserDocument(new DOMDocument(node));
                            if (context.getUserAction() == UserAction.CREATE) {
                                context.setId(new String[0]); // Will re-read id from document.
                            }
                            // Redo a set of actions and security checks.
                            // TMDM-4599: Adds UpdateReport phase so a new update report is generated based on latest changes.
                            next = new GenerateActions(new Security(new UpdateReport(next)));
                        }
                    }
                }
            } catch (Exception e) {
                throw new RuntimeException("Exception occurred during before saving phase.", e);
            }
        }

        next.save(session, context);
    }

    @Override
    public String[] getSavedId() {
        return next.getSavedId();
    }

    @Override
    public String getSavedConceptName() {
        return next.getSavedConceptName();
    }

    @Override
    public String getBeforeSavingMessage() {
        return message;
    }
}
