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
import java.io.StringReader;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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
import org.xml.sax.SAXException;

import com.sun.xml.xsom.XSAnnotation;
import com.sun.xml.xsom.XSComplexType;
import com.sun.xml.xsom.XSElementDecl;
import com.sun.xml.xsom.XSParticle;
import com.sun.xml.xsom.XSSchema;
import com.sun.xml.xsom.XSSchemaSet;
import com.sun.xml.xsom.parser.XSOMParser;
import com.sun.xml.xsom.util.DomAnnotationParserFactory;

/**
 * DOC Starkey class global comment. Detailled comment
 */
public final class XmlUtil {

    private static final Logger logger = Logger.getLogger(XmlUtil.class);

    private static Map<String, String> xpathToLabel = new HashMap<String, String>();

    private static Map<String, String> xpathToType = new HashMap<String, String>();

    public static Document parse(URL url) throws DocumentException {
        SAXReader reader = new SAXReader();
        Document document = reader.read(url);
        return document;
    }

    public static Document parse(File file) throws MalformedURLException, DocumentException {
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
        InputStream is = null;
        is = XmlUtil.class.getResourceAsStream("/" + fileName);
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

    public static void iterate(Element parentElement, ElementProcess elementProcess) throws DocumentException {
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

    public static void iterate(Element parentElement, String elementName, ElementProcess elementProcess) throws DocumentException {

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

    public static List findLinks(Document document) throws DocumentException {

        List<String> urls = new ArrayList();

        List list = document.selectNodes("//a/@href");

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

        logger.info("New Document has bean created");

        return document;
    }

    public static void write(Document document, String filePath, String printMode, String encoding) throws IOException {

        OutputFormat format = null;

        if (printMode.toLowerCase().equals("pretty")) {
            // Pretty print the document
            format = OutputFormat.createPrettyPrint();
        } else if (printMode.toLowerCase().equals("compact")) {
            // Compact format
            format = OutputFormat.createCompactFormat();
        }

        format.setEncoding(encoding);

        // lets write to a file
        XMLWriter writer = new XMLWriter(new FileOutputStream(filePath), format);

        // XMLWriter logger = new XMLWriter( System.out, format );

        writer.write(document);

        logger.info("New xml file has bean exported on " + filePath);

        // logger.write( document );

        // logger.close();

        writer.close();
    }

    public static String toXml(Document document) {

        String text = document.asXML();

        return text;
    }

    public static Document fromXml(String text) throws DocumentException {

        Document document = DocumentHelper.parseText(text);

        return document;
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

        logger.info("The xml file style transformed successfully ");

        return transformedDoc;
    }

    public static String format(Document document, OutputFormat format, String encoding) {

        StringWriter writer = new StringWriter();

        format.setEncoding(encoding);

        format.setNewLineAfterDeclaration(false);
        // format.setSuppressDeclaration(suppressDeclaration);

        XMLWriter xmlwriter = new XMLWriter(writer, format);

        try {

            xmlwriter.write(document);

        } catch (Exception e) {

            logger.error(e);
        }

        return writer.toString().replaceAll("<\\?xml.*?\\?>", "").trim();

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

            SAXReader xmlReader = new SAXReader();

            Document xmlDocument = DocumentHelper.parseText(inputXml);
            ;

            // [url]http://sax.sourceforge.net/?selected=get-set[/url]
            parser.setProperty("http://java.sun.com/xml/jaxp/properties/schemaLanguage", "http://www.w3.org/2001/XMLSchema");
            parser.setProperty("http://java.sun.com/xml/jaxp/properties/schemaSource", "file:" + xsdFileName);

            SAXValidator validator = new SAXValidator(parser.getXMLReader());

            validator.setErrorHandler(errorHandler);
            validator.validate(xmlDocument);
            XMLWriter writer = new XMLWriter(OutputFormat.createPrettyPrint());

            if (errorHandler.getErrors().hasContent()) {
                isValidated = false;
                logger.warn("XML file validation failed! ");
                writer.write(errorHandler.getErrors());
            } else {
                isValidated = true;
                logger.info("XML file validation successfully! ");
            }
        } catch (Exception ex) {
            isValidated = false;
            logger.error("Failed to validate XML file through '" + xsdFileName, ex);
        }

        return isValidated;
    }

    public static String getTextValueFromXpath(Document doc, String xpath) {

        // FIXME
        String label = "";
        if (xpath.indexOf("/") != -1)
            label = xpath.substring(xpath.lastIndexOf("/") + 1);
        Element elem = doc.getRootElement();
        Iterator iter = elem.elementIterator();
        while (iter.hasNext()) {
            Element el = (Element) iter.next();
            if (el.getName().endsWith(label)) {
                return el.getText();
            }

        }

        return null;

    }

    private static String getLabel(XSElementDecl xsed, String x_Label) {
        String label = "";
        try {
            XSAnnotation xsa = xsed.getAnnotation();

            org.w3c.dom.Element elem = (org.w3c.dom.Element) xsa.getAnnotation();
            if (elem != null) {
                org.w3c.dom.NodeList list = elem.getChildNodes();
                for (int k = 0; k < list.getLength(); k++) {
                    if ("appinfo".equals(list.item(k).getLocalName())) {
                        org.w3c.dom.Node source = list.item(k).getAttributes().getNamedItem("source");
                        if (source == null)
                            continue;
                        String appinfoSource = source.getNodeValue();
                        if (x_Label.equals(appinfoSource)) {
                            label = list.item(k).getFirstChild().getNodeValue();
                            break;
                        }
                    }
                }
            }
        } catch (Exception e) {
            return "";
        }
        return label;
    }

    private static void getChildren(XSParticle xsp, String xpathParent, String x_Label, boolean includeComplex,
            boolean includeFKReference) {
        // aiming added see 0009563
        if (xsp.getTerm().asModelGroup() != null) { // is complex type
            XSParticle[] xsps = xsp.getTerm().asModelGroup().getChildren();
            for (int i = 0; i < xsps.length; i++) {
                getChildren(xsps[i], xpathParent, x_Label, includeComplex, includeFKReference);
            }
        }
        if (xsp.getTerm().asElementDecl() == null)
            return;
        // end
        if (xsp.getTerm().asElementDecl().getType().isComplexType() == false || includeComplex == true) {
            // Hidden the NO_ACCESS elment

            // TODO getroles
            // if (isElementHidden(xsp)) {
            // return;
            // }

            String toPutKey = xpathParent + "/" + xsp.getTerm().asElementDecl().getName();
            if (includeFKReference) {
                String foreignkeyPath = getLabel(xsp.getTerm().asElementDecl(), "X_ForeignKey");
                if (foreignkeyPath != null)
                    toPutKey += "@FK_" + foreignkeyPath;
            }

            // FIXME:USE XPATH WITHOUT CONCEPT AS LABEL, MAYBE CAUSE SOME BUGS ON OLD INVOKING PLACES
            String xlabel = "";
            if (getLabel(xsp.getTerm().asElementDecl(), x_Label).equals("")) {
                xlabel = xpathParent + "/" + xsp.getTerm().asElementDecl().getName();
                if (xlabel.indexOf("/") != -1) {
                    xlabel = xlabel.substring(xlabel.indexOf("/") + 1);
                }
            } else {
                xlabel = getLabel(xsp.getTerm().asElementDecl(), x_Label);
            }

            xpathToLabel.put(toPutKey, xlabel);
            xpathToType.put(toPutKey, xsp.getTerm().asElementDecl().getType().getName());
        }
        if (xsp.getTerm().asElementDecl().getType().isComplexType() == true) {
            XSParticle particle = xsp.getTerm().asElementDecl().getType().asComplexType().getContentType().asParticle();
            if (particle != null) {
                XSParticle[] xsps = particle.getTerm().asModelGroup().getChildren();
                for (int i = 0; i < xsps.length; i++) {
                    getChildren(xsps[i], xpathParent + "/" + xsp.getTerm().asElementDecl().getName(), x_Label, includeComplex,
                            includeFKReference);
                }
            }
        }
    }

    public static void parseXSD(String xsd, String viewPk) {
        try {
            XSOMParser reader = new XSOMParser();
            reader.setAnnotationParser(new DomAnnotationParserFactory());

            reader.parse(new StringReader(xsd));

            XSSchemaSet xss = reader.getResult();
            Collection xssList = xss.getSchemas();
            XSComplexType xsct = null;
            String x_Label = "X_Label_" + "en".toUpperCase();
            for (Iterator iter = xssList.iterator(); iter.hasNext();) {
                XSSchema schema = (XSSchema) iter.next();
                XSElementDecl element = schema.getElementDecls().get(viewPk);
                if (element != null) {
                    xsct = (XSComplexType) (element.getType());
                    String label = getLabel(element, x_Label);
                    xpathToLabel.put(viewPk, label.equals("") ? element.getName() : label);
                    xpathToType.put(viewPk, xsct.getName());
                    break;
                }
            }

            XSParticle[] xsp = xsct.getContentType().asParticle().getTerm().asModelGroup().getChildren();

            for (int j = 0; j < xsp.length; j++) {
                getChildren(xsp[j], "" + viewPk, x_Label, true, false);
            }
        } catch (SAXException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            logger.error(e.getMessage(), e);
        }
    }

    public static Map<String, String> getXpathToLabel() {
        return xpathToLabel;
    }

    public static Map<String, String> getXpathToType() {
        return xpathToType;
    }
}
