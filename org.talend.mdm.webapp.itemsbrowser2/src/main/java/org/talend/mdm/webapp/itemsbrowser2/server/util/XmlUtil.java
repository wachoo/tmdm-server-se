// ============================================================================
//
// Copyright (C) 2006-2010 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.mdm.webapp.itemsbrowser2.server.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamSource;

import org.apache.log4j.Logger;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.VisitorSupport;
import org.dom4j.io.DocumentResult;
import org.dom4j.io.DocumentSource;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.SAXValidator;
import org.dom4j.io.XMLWriter;
import org.dom4j.util.XMLErrorHandler;
import org.talend.mdm.webapp.itemsbrowser2.server.util.callback.AttributeProcess;
import org.talend.mdm.webapp.itemsbrowser2.server.util.callback.DocumentCreate;
import org.talend.mdm.webapp.itemsbrowser2.server.util.callback.ElementProcess;
import org.talend.mdm.webapp.itemsbrowser2.server.util.callback.NodeProcess;

/**
 * DOC Starkey class global comment. Detailled comment
 */
public final class XmlUtil {

    private static final Logger logger = Logger.getLogger(XmlUtil.class);

    public static Document parse(URL url) throws DocumentException {
        SAXReader reader = new SAXReader();
        Document document = reader.read(url);
        return document;
    }

    public static Document parse(File file) throws DocumentException {
        SAXReader reader = new SAXReader();
        Document document = reader.read(file);
        return document;
    }

    public static Document parse(InputStream in) throws DocumentException {
        SAXReader reader = new SAXReader();
        Document document = reader.read(in);
        return document;
    }

    public static Document parse(String fileName) throws DocumentException {
        InputStream is = XmlUtil.class.getResourceAsStream("/" + fileName); //$NON-NLS-1$
        Document document = parse(is);
        return document;
    }

    /**
     * DOC HSHU Comment method "parse".
     * 
     * @throws DocumentException
     */
    public static Document parseText(String text) throws DocumentException {
        return DocumentHelper.parseText(text);
    }

    public static void iterate(Document document, ElementProcess elementProcess) throws DocumentException {
        Element root = document.getRootElement();
        iterate(root, elementProcess);
    }

    public static void iterate(Element parentElement, ElementProcess elementProcess) {
        // iterate through child elements of element
        for (Iterator i = parentElement.elementIterator(); i.hasNext();) {
            Element element = (Element) i.next();
            // do something
            elementProcess.process(element);
        }
    }

    public static void iterate(Document document, String elementName, ElementProcess elementProcess) throws DocumentException {

        Element root = document.getRootElement();

        iterate(root, elementName, elementProcess);

    }

    public static void iterate(Element parentElement, String elementName, ElementProcess elementProcess) {

        // iterate through child elements of element with element specific element
        // name
        for (Iterator i = parentElement.elementIterator(elementName); i.hasNext();) {
            Element element = (Element) i.next();
            // do something
            elementProcess.process(element);
        }

    }

    public static void iterateAttribute(Document document, AttributeProcess attributeProcess) throws DocumentException {

        Element root = document.getRootElement();

        iterateAttribute(root, attributeProcess);
    }

    public static void iterateAttribute(Element element, AttributeProcess attributeProcess) throws DocumentException {

        // iterate through attributes of element
        for (Iterator i = element.attributeIterator(); i.hasNext();) {
            Attribute attribute = (Attribute) i.next();
            // do something
            attributeProcess.process(attribute);
        }
    }

    public static void treeWalk(Document document, NodeProcess nodeProcess) {
        treeWalk(document.getRootElement(), nodeProcess);
    }

    public static void treeWalk(Element element, NodeProcess nodeProcess) {
        for (int i = 0, size = element.nodeCount(); i < size; i++) {
            Node node = element.node(i);

            if (node instanceof Element) {
                treeWalk((Element) node, nodeProcess);
            } else {
                nodeProcess.process(element);
            }
        }
    }

    public static void visit(Document document, VisitorSupport visitor) {
        visit(document.getRootElement(), visitor);
    }

    public static void visit(Element element, VisitorSupport visitor) {
        element.accept(visitor);
    }

    public static Node queryNode(Document document, String xPath) {

        Node node = document.selectSingleNode(xPath);

        return node;
    }

    public static List queryList(Document document, String xPath) {

        List list = document.selectNodes(xPath);

        return list;
    }

    public static List<String> findLinks(Document document) {

        List<String> urls = new ArrayList<String>();

        List list = document.selectNodes("//a/@href"); //$NON-NLS-1$

        for (Iterator iter = list.iterator(); iter.hasNext();) {
            Attribute attribute = (Attribute) iter.next();
            String url = attribute.getValue();
            urls.add(url);
        }

        return urls;
    }

    public static Document createDocument(DocumentCreate documentCreate) {
        Document document = DocumentHelper.createDocument();
        documentCreate.create(document);
        if (logger.isDebugEnabled())
            logger.info("New Document has bean created"); ////$NON-NLS-1$

        return document;
    }

    public static void write(Document document, String filePath, String printMode, String encoding) throws IOException {

        OutputFormat format;

        if (printMode.toLowerCase().equals("pretty")) {//$NON-NLS-1$
            // Pretty print the document
            format = OutputFormat.createPrettyPrint();
        } else if (printMode.toLowerCase().equals("compact")) {//$NON-NLS-1$
            // Compact format
            format = OutputFormat.createCompactFormat();
        } else
            format = null;

        format.setEncoding(encoding);

        // lets write to a file
        XMLWriter writer = new XMLWriter(new FileOutputStream(filePath), format);
        writer.write(document);
        writer.close();

        if (logger.isDebugEnabled())
            logger.debug("New xml file has bean exported on " + filePath); //$NON-NLS-1$

    }

    public static String toXml(Document document) {
        return document.asXML();
    }

    public static Document fromXml(String text) throws DocumentException {
        return DocumentHelper.parseText(text);
    }

    public static void print(Document document) {
        String text = toXml(document);
        logger.info(text);
    }

    public static Document styleDocument(Document document, String stylesheet) throws Exception {

        // load the transformer using JAXP
        TransformerFactory factory = TransformerFactory.newInstance();
        Transformer transformer = factory.newTransformer(new StreamSource(stylesheet));

        // now lets style the given document
        DocumentSource source = new DocumentSource(document);
        DocumentResult result = new DocumentResult();
        transformer.transform(source, result);

        // return the transformed document
        Document transformedDoc = result.getDocument();

        if (logger.isDebugEnabled())
            logger.debug("The xml file style transformed successfully "); //$NON-NLS-1$
        return transformedDoc;
    }

    public static String format(Document document, OutputFormat format, String encoding) {

        StringWriter writer = new StringWriter();
        format.setEncoding(encoding);
        format.setNewLineAfterDeclaration(false);

        XMLWriter xmlwriter = new XMLWriter(writer, format);

        try {
            xmlwriter.write(document);

        } catch (Exception e) {

            logger.error(e.getMessage(), e);
        }

        return writer.toString().replaceAll("<\\?xml.*?\\?>", "").trim(); //$NON-NLS-1$

    }

    public static String formatPretty(String xml, String encoding) throws DocumentException {
        Document document = fromXml(xml);
        return format(document, OutputFormat.createPrettyPrint(), encoding);
    }

    public static String formatCompact(String xml, String encoding) throws DocumentException {
        Document document = fromXml(xml);
        return format(document, OutputFormat.createCompactFormat(), encoding);
    }

    public static boolean validateXMLByXSD(String inputXml, File xsdFile) {

        boolean isValidated = false;

        String xsdFileName = xsdFile.getAbsolutePath();

        try {

            XMLErrorHandler errorHandler = new XMLErrorHandler();

            SAXParserFactory factory = SAXParserFactory.newInstance();

            factory.setValidating(true);

            factory.setNamespaceAware(true);

            SAXParser parser = factory.newSAXParser();

            Document xmlDocument = DocumentHelper.parseText(inputXml);

            // [url]http://sax.sourceforge.net/?selected=get-set[/url]
            parser.setProperty("http://java.sun.com/xml/jaxp/properties/schemaLanguage", "http://www.w3.org/2001/XMLSchema"); //$NON-NLS-1$ //$NON-NLS-2$
            parser.setProperty("http://java.sun.com/xml/jaxp/properties/schemaSource", "file:" + xsdFileName); //$NON-NLS-1$

            SAXValidator validator = new SAXValidator(parser.getXMLReader());

            validator.setErrorHandler(errorHandler);
            validator.validate(xmlDocument);
            XMLWriter writer = new XMLWriter(OutputFormat.createPrettyPrint());

            if (errorHandler.getErrors().hasContent()) {
                isValidated = false;
                logger.warn("XML file validation failed! ");//$NON-NLS-1$
                writer.write(errorHandler.getErrors());
            } else {
                isValidated = true;
                if (logger.isDebugEnabled())
                    logger.debug("XML file validation succeeded! "); //$NON-NLS-1$
            }
        } catch (Exception ex) {
            isValidated = false;
            logger.error("Failed to validate XML file through '" + xsdFileName, ex);//$NON-NLS-1$
        }

        return isValidated;
    }

    public static String getTextValueFromXpath(Document doc, String xpath) {
        // FIXME
        Element root = doc.getRootElement();
        Node node = root.selectSingleNode(xpath);
        return node == null ? "" : node.getText();
    }
    
    public static List getValuesFromXPath(Document doc, String xpath){
        Element root = doc.getRootElement();
        return root.selectNodes(xpath);
    }

    
}
