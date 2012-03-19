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

import com.amalto.core.history.Action;
import com.amalto.core.history.action.FieldUpdateAction;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

class ActionsBuilder extends DefaultHandler {

    private final List<Action> actions = new LinkedList<Action>();

    private final Document document;

    private Node currentNode = null;

    ActionsBuilder(Document document) {
        this.document = document;
    }

    public List<Action> getActions() {
        return actions;
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        if (currentNode == null) {
            currentNode = document.getDocumentElement();
        } else {
            currentNode = ((Element) currentNode).getElementsByTagName(qName).item(0);
        }

        if (currentNode == null || !qName.equals(currentNode.getLocalName())) {
            actions.add(new FieldUpdateAction(new Date(System.currentTimeMillis()), "source", "admin", qName, "", ""));  // TODO
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (currentNode != null) {
            currentNode = currentNode.getParentNode();
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        if (currentNode != null) {
            String originalText = new String(ch, start, length);
            if (!originalText.trim().isEmpty()) {
                String text = currentNode.getFirstChild().getNodeValue();

                if (!originalText.equals(text)) {
                    actions.add(new FieldUpdateAction(new Date(System.currentTimeMillis()), "source", "admin", currentNode.getNodeName(), originalText, text));  // TODO
                }
            }
        }
    }
}
