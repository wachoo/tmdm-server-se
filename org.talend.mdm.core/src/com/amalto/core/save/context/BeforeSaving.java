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
import com.amalto.core.save.SaverSession;
import com.amalto.core.util.OutputReport;
import com.amalto.core.util.Util;
import com.sun.org.apache.xpath.internal.XPathAPI;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

class BeforeSaving implements DocumentSaver {

    private DocumentSaver next;

    BeforeSaving(DocumentSaver next) {
        this.next = next;
    }

    public void save(SaverSession session, DocumentSaverContext context) {
        if (!(context instanceof ReportDocumentSaverContext)) {
            throw new IllegalArgumentException("Context is expected to contain an update report.");
        }

        // Invoke the beforeSaving
        MutableDocument updateReportDocument = ((ReportDocumentSaverContext) context).getUpdateReportDocument();
        if (updateReportDocument == null) {
            throw new IllegalStateException("Update report is missing.");
        }
        OutputReport outputreport = context.getSaverSource().invokeBeforeSaving(context, updateReportDocument);

        if (outputreport != null) { // when a process was found
            String errorCode;
            String message = outputreport.getMessage();
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
                    throw new RuntimeException("BeforeSaving Validation Error --> " + message);
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
                            // Redo a set of actions and security checks.
                            next = new GenerateActions(new Security(next));
                        }
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


}


