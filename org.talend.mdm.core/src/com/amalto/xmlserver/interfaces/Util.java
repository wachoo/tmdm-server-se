// ============================================================================
//
// Copyright (C) 2006-2014 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================

package com.amalto.xmlserver.interfaces;

import com.sun.org.apache.xpath.internal.XPathAPI;
import com.sun.org.apache.xpath.internal.objects.XObject;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;

public final class Util {

    /**
     * Parsed an XML String into a {@link Document} without schema validation
     */
    public static Document parse(String xmlString) throws XmlServerException {
        return parse(xmlString, null);
    }

    /**
     * Parses an XML String into a Document<br>
     * The thrown Exception will contain validation errors when a schema is provided.
     * @return The org.w3c.dom.Document
     */
    public static Document parse(String xmlString, String schema) throws XmlServerException {
        // parse
        Document d;
        SAXErrorHandler seh = new SAXErrorHandler();
        try {
            // initialize the sax parser which uses Xerces
            System.setProperty("javax.xml.parsers.DocumentBuilderFactory", "org.apache.xerces.jaxp.DocumentBuilderFactoryImpl");
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            // Schema validation based on schemaURL
            factory.setNamespaceAware(true);
            factory.setValidating((schema != null));
            factory.setAttribute("http://java.sun.com/xml/jaxp/properties/schemaLanguage", "http://www.w3.org/2001/XMLSchema");
            if (schema != null) {
                factory.setAttribute("http://java.sun.com/xml/jaxp/properties/schemaSource", new InputSource(new StringReader(
                        schema)));
            }
            DocumentBuilder builder;
            builder = factory.newDocumentBuilder();
            builder.setErrorHandler(seh);
            d = builder.parse(new InputSource(new StringReader(xmlString)));
        } catch (Exception e) {
            String err = "Unable to parse the document" + ": " + e.getClass().getName() + ": " + e.getLocalizedMessage() + "\n "
                    + xmlString.substring(0, Math.min(100, xmlString.length()));
            org.apache.log4j.Logger.getLogger(Util.class).error(err, e);
            throw new XmlServerException(err);
        }
        // check if document parsed correctly against the schema
        if (schema != null) {
            String errors = seh.getErrors();
            if (!errors.equals("")) {
                String err = "Document  did not parse against schema: \n" + errors + "\n"
                        + xmlString.substring(0, Math.min(100, xmlString.length()));
                org.apache.log4j.Logger.getLogger(Util.class).error(err);
                throw new XmlServerException(err);
            }
        }
        return d;
    }

    /**
     * Return the String values of the Text Nodes below an xPath. When the xPath contains namespace, a context node
	 * holding the namespaces definitions can be provided.
     * @return a String Array of the text node values
     */
    public static String[] getTextNodes(Node contextNode, String xPath, Node namespaceNode) throws XmlServerException {
        String[] results;
        // test for hard-coded values
        if (xPath.startsWith("\"") && xPath.endsWith("\"")) {
			return new String[] { xPath.substring(1, xPath.length() - 1) };
		}
		// test for incomplete path (elements missing /text())
        if (!xPath.matches(".*@[^/\\]]+")) { // attribute
			if (!xPath.endsWith(")")) { // function
				xPath += "/text()";
			}
		}
		try {
            XObject xo = XPathAPI.eval(contextNode, xPath, namespaceNode);
            if (xo.getType() == XObject.CLASS_NODESET) {
                NodeList l = xo.nodelist();
                int len = l.getLength();
                results = new String[len];
                for (int i = 0; i < len; i++) {
                    Node n = l.item(i);
                    results[i] = n.getNodeValue();
                }
            } else {
                results = new String[] { xo.toString() };
            }
        } catch (Exception e) {
            String err = "Unable to get the text node(s) of " + xPath + ": " + e.getClass().getName() + ": "
                    + e.getLocalizedMessage();
            org.apache.log4j.Logger.getLogger(Util.class).error(err, e);
            throw new XmlServerException(err);
        }
        return results;
    }

    /**
     * The value of the first text node at the xPath.
     */
    public static String getFirstTextNode(Node contextNode, String xPath, Node namespaceNode) throws XmlServerException {
        String[] res = getTextNodes(contextNode, xPath, namespaceNode);
        if (res.length == 0)
            return null;
        return res[0];
    }

    /**
     * The value of the first text node at the xPath.
     */
    public static String getFirstTextNode(Node contextNode, String xPath) throws XmlServerException {
        return getFirstTextNode(contextNode, xPath, contextNode);
    }
}
