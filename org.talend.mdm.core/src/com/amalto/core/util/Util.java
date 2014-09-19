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
package com.amalto.core.util;

import com.amalto.core.delegator.BeanDelegatorContainer;
import com.amalto.core.delegator.IXtentisWSDelegator;
import com.amalto.core.ejb.*;
import com.amalto.core.jobox.JobContainer;
import com.amalto.core.objects.datacluster.ejb.DataClusterPOJOPK;
import com.amalto.core.objects.datamodel.ejb.DataModelPOJO;
import com.amalto.core.objects.datamodel.ejb.DataModelPOJOPK;
import com.amalto.core.objects.transformers.v2.ejb.TransformerV2POJOPK;
import com.amalto.core.objects.transformers.v2.util.TransformerCallBack;
import com.amalto.core.objects.transformers.v2.util.TransformerContext;
import com.amalto.core.objects.transformers.v2.util.TypedContent;
import com.amalto.core.objects.universe.ejb.UniversePOJO;
import com.amalto.core.schema.manage.SchemaCoreAgent;
import com.amalto.core.server.*;
import com.amalto.core.webservice.WSMDMJob;
import com.amalto.core.webservice.WSVersion;
import com.amalto.xmlserver.interfaces.IWhereItem;
import com.amalto.xmlserver.interfaces.WhereCondition;
import com.amalto.xmlserver.interfaces.WhereLogicOperator;
import com.amalto.xmlserver.interfaces.XmlServerException;
import com.sun.xml.xsom.XSElementDecl;
import com.sun.xml.xsom.XSSchema;
import com.sun.xml.xsom.XSSchemaSet;
import com.sun.xml.xsom.XSType;
import com.sun.xml.xsom.parser.XSOMParser;
import com.sun.xml.xsom.util.DomAnnotationParserFactory;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.jxpath.AbstractFactory;
import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.jxpath.Pointer;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.talend.mdm.commmon.util.core.ITransformerConstants;
import org.talend.mdm.commmon.util.core.MDMConfiguration;
import org.talend.mdm.commmon.util.datamodel.management.BusinessConcept;
import org.talend.mdm.commmon.util.datamodel.management.SchemaManager;
import com.amalto.core.server.api.*;
import org.w3c.dom.*;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.security.auth.Subject;
import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import java.io.*;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLConnection;
import java.security.Principal;
import java.security.acl.Group;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@SuppressWarnings("deprecation")
public class Util {

    private static final Logger LOGGER = Logger.getLogger(Util.class);

    private static final TransformerFactory transformerFactory = TransformerFactory.newInstance();

    private static final String JAXP_SCHEMA_LANGUAGE = "http://java.sun.com/xml/jaxp/properties/schemaLanguage"; //$NON-NLS-1$

    private static final String W3C_XML_SCHEMA = "http://www.w3.org/2001/XMLSchema"; //$NON-NLS-1$

    private static final String JAXP_SCHEMA_SOURCE = "http://java.sun.com/xml/jaxp/properties/schemaSource"; //$NON-NLS-1$

    private static final String USER_PROPERTY_PREFIX = "${user_context"; //$NON-NLS-1$

    private static DocumentBuilderFactory nonValidatingDocumentBuilderFactory;

    private static ScriptEngineManager SCRIPTFACTORY;

    static {
        LoginModuleDelegator.setDelegateClassLoader(Util.class.getClassLoader());
        SCRIPTFACTORY = new ScriptEngineManager();
    }

    private static DefaultXmlServer defaultXmlServer;

    private static DataModel defaultDataModel;

    private static DefaultConfigurationInfo configurationInfo;

    private static DefaultMenu menu;

    private static DefaultRole role;

    private static DefaultDataCluster defaultDataCluster;

    private static DefaultCustomForm defaultCustomForm;

    private static DefaultBackgroundJob defaultBackgroundJob;

    private static DefaultView defaultView;

    private static DefaultStoredProcedure defaultStoredProcedure;

    private static DefaultItem defaultItem;

    private static DefaultDroppedItem defaultDroppedItem;

    private static DefaultRoutingRule defaultRoutingRule;

    private static DefaultRoutingEngine defaultRoutingEngine;

    private static DefaultRoutingOrder defaultRoutingOrder;

    private static DefaultTransformer defaultTransformer;

    public static Document parse(String xmlString) throws ParserConfigurationException, IOException, SAXException {
        return parse(xmlString, null);
    }

    public static Document parseXSD(String xsd) throws ParserConfigurationException, IOException, SAXException {
        return parse(xsd, null);
    }

    private static synchronized DocumentBuilderFactory getDocumentBuilderFactory() {
        if (nonValidatingDocumentBuilderFactory == null) {
            nonValidatingDocumentBuilderFactory = DocumentBuilderFactory.newInstance();
            nonValidatingDocumentBuilderFactory.setNamespaceAware(true);
            nonValidatingDocumentBuilderFactory.setValidating(false);
            nonValidatingDocumentBuilderFactory.setAttribute(JAXP_SCHEMA_LANGUAGE, W3C_XML_SCHEMA);
        }
        return nonValidatingDocumentBuilderFactory;
    }

    public static Document parse(String xmlString, String schema) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory factory;
        if (schema != null) {
            factory = DocumentBuilderFactory.newInstance();
            // Schema validation based on schemaURL
            factory.setNamespaceAware(true);
            factory.setValidating(true);
            factory.setAttribute(JAXP_SCHEMA_LANGUAGE, W3C_XML_SCHEMA);
            factory.setAttribute(JAXP_SCHEMA_SOURCE, new InputSource(new StringReader(schema)));
        } else {
            factory = getDocumentBuilderFactory();
        }

        DocumentBuilder builder = factory.newDocumentBuilder();
        SAXErrorHandler seh = new SAXErrorHandler();
        builder.setErrorHandler(seh);
        Document d = builder.parse(new InputSource(new StringReader(xmlString)));

        // check if document parsed correctly against the schema
        if (schema != null) {
            String errors = seh.getErrors();
            if (errors.length() != 0) {
                String err = "Document did not parse against schema: \n" + errors + "\n"
                        + xmlString.substring(0, Math.min(100, xmlString.length()));
                throw new SAXException(err);
            }
        }
        return d;
    }

    // bug:0009642: remove the null element to match the schema
    public static boolean setNullNode(Node element) {
        // String xml = Util.nodeToString(element);
        boolean removed = false;
        NodeList nodelist = element.getChildNodes();
        for (int i = 0; i < nodelist.getLength(); i++) {
            Node node = nodelist.item(i);
            int length = node.getChildNodes().getLength();
            if (length <= 1 && (node.getTextContent() == null || node.getTextContent().trim().equals(""))) {
                element.removeChild(node);
                // xml = Util.nodeToString(element);
                setNullNode(element);
                removed = true;
            } else if (length > 1) {
                if (setNullNode(node)) {
                    setNullNode(node.getParentNode());
                }
            }
        }
        return removed;
    }

    public static Document defaultValidate(Element element, String schema) throws Exception {
        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace("validate() " + element.getLocalName());
        }
        if (schema == null) {
            throw new IllegalArgumentException("Schema cannot be null.");
        }
        Node cloneNode = element.cloneNode(true);

        // parse
        Document d;
        SAXErrorHandler seh = new SAXErrorHandler();

        // initialize the sax parser which uses Xerces
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        // Schema validation based on schemaURL
        factory.setNamespaceAware(true);
        factory.setValidating(true);

        schema = schema.replaceFirst("<\\?xml.*\\?>", ""); //$NON-NLS-1$//$NON-NLS-2$
        schema = schema.replace("\r\n", "\n"); //$NON-NLS-1$//$NON-NLS-2$

        factory.setAttribute(JAXP_SCHEMA_LANGUAGE, W3C_XML_SCHEMA);
        if (schema != null) {
            factory.setAttribute(JAXP_SCHEMA_SOURCE, new InputSource(new StringReader(schema)));
        }

        DocumentBuilder builder;
        builder = factory.newDocumentBuilder();
        builder.setErrorHandler(seh);
        builder.setEntityResolver(new SecurityEntityResolver());

        // bug:0009642:remove the null element to match the schema
        setNullNode(cloneNode);

        // strip of attributes
        removeAllAttribute(cloneNode, "tmdm:type"); //$NON-NLS-1$

        String xmlAsString = Util.nodeToString(cloneNode);
        // if element is null, remove it see 7828
        xmlAsString = xmlAsString.replaceAll("<\\w+?/>", ""); //$NON-NLS-1$ //$NON-NLS-2$

        d = builder.parse(new InputSource(new StringReader(xmlAsString)));

        // check if document parsed correctly against the schema
        if (schema != null) {
            // ignore cvc-complex-type.2.3 error
            String errors = seh.getErrors();

            boolean isComplex23 = errors.contains("cvc-complex-type.2.3") && errors.endsWith("is element-only."); //$NON-NLS-1$ //$NON-NLS-2$
            if (!errors.equals("") && !isComplex23) { //$NON-NLS-1$  
                String xmlString = Util.nodeToString(element);
                String err = "The item " + element.getLocalName() + " did not validate against the model: \n" + errors + "\n" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                        + xmlString; // .substring(0, Math.min(100, xmlString.length()));
                LOGGER.debug(err);
                throw new CVCException(err);
            }
        }

        return d;
    }

    public static Document validate(Element element, String schema) throws Exception {
        return BeanDelegatorContainer.getInstance().getValidationDelegator().validation(element, schema);
    }

    public static void removeXpathFromDocument(Document document, String xpath, boolean reservedRoot) throws Exception {
        Element root = document.getDocumentElement();
        NodeList toDeleteNodeList = Util.getNodeList(document, xpath);
        if (toDeleteNodeList != null) {
            Node lastParentNode;
            Node formatSiblingNode;
            for (int i = 0; i < toDeleteNodeList.getLength(); i++) {
                Node node = toDeleteNodeList.item(i);
                if (root.isSameNode(node) && reservedRoot) {
                    while (node.hasChildNodes()) {
                        node.removeChild(node.getFirstChild());
                    }
                } else {
                    lastParentNode = node.getParentNode();
                    formatSiblingNode = node.getNextSibling();
                    if (lastParentNode != null) {
                        lastParentNode.removeChild(node);
                    }
                    if (formatSiblingNode != null && formatSiblingNode.getNodeValue() != null
                            && formatSiblingNode.getNodeValue().matches("\\s+")) {
                        if (lastParentNode != null) {
                            lastParentNode.removeChild(formatSiblingNode);
                        }
                    }
                }

            }
        }
    }

    public static String[] getTextNodes(Node contextNode, String xPath) throws TransformerException {
        return getTextNodes(contextNode, xPath, contextNode);
    }

    public static String[] getTextNodes(Node contextNode, String xPath, final Node namespaceNode) throws TransformerException {
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
            XPath path = XPathFactory.newInstance().newXPath();
            path.setNamespaceContext(new NamespaceContext() {

                @Override
                public String getNamespaceURI(String s) {
                    return namespaceNode.getNamespaceURI();
                }

                @Override
                public String getPrefix(String s) {
                    return namespaceNode.getPrefix();
                }

                @Override
                public Iterator getPrefixes(String s) {
                    return Arrays.asList(namespaceNode.getPrefix()).iterator();
                }
            });
            NodeList xo = (NodeList) path.evaluate(xPath, contextNode, XPathConstants.NODESET);
            results = new String[xo.getLength()];
            for (int i = 0; i < xo.getLength(); i++) {
                results[i] = xo.item(i).getTextContent();
            }
        } catch (Exception e) {
            String err = "Unable to get the text node(s) of " + xPath + ": " + e.getClass().getName() + ": "
                    + e.getLocalizedMessage();
            throw new TransformerException(err);
        }
        return results;

    }

    public static String getFirstTextNode(Node contextNode, String xPath, Node namespaceNode) throws TransformerException {
        String[] res = getTextNodes(contextNode, xPath, namespaceNode);
        if (res.length == 0) {
            return null;
        }
        return res[0];
    }

    public static String getFirstTextNode(Node contextNode, String xPath) throws TransformerException {
        return getFirstTextNode(contextNode, xPath, contextNode);
    }

    public static String getResponseFromURL(String url, String user, String pwd) {
        StringBuilder buffer = new StringBuilder();
        String credentials = new String(Base64.encodeBase64((user + ":" + pwd).getBytes()));

        try {
            URL urlCn = new URL(url);
            URLConnection conn = urlCn.openConnection();
            conn.setAllowUserInteraction(true);
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setRequestProperty("Authorization", "Basic " + credentials);
            conn.setRequestProperty("Expect", "100-continue");

            InputStreamReader doc = new InputStreamReader(conn.getInputStream());
            BufferedReader reader = new BufferedReader(doc);
            String line = reader.readLine();
            while (line != null) {
                buffer.append(line);
                line = reader.readLine();
            }
        } catch (Exception e) {
            Logger.getLogger(Util.class).error(e);
        }

        return buffer.toString();
    }

    /**
     * Get the method of a component by its name
     */
    public static Method getMethod(Object component, String methodName) {
        if(component == null) {
            return null;
        }
        Method[] methods = component.getClass().getMethods();
        for (Method method : methods) {
            if (methodName.equals(method.getName())) {
                return method;
            }
        }
        return null;
    }

    public static XSDKey getBusinessConceptKey(Document xsd, String businessConceptName) throws TransformerException {
        try {
            XSDKey key = null;
            String[] selectors;
            String[] fields;
            selectors = Util.getTextNodes(xsd.getDocumentElement(), "xsd:element/xsd:unique[@name='" + businessConceptName
                    + "']/xsd:selector/@xpath", getRootElement("nsholder", xsd.getDocumentElement().getNamespaceURI(), "xsd"));

            // Fix for TMDM-2351
            String[] businessConceptXSDTypeNameLookup = Util.getTextNodes(xsd.getDocumentElement(), "xsd:element[@name='"
                    + businessConceptName + "']/@type",
                    getRootElement("nsholder", xsd.getDocumentElement().getNamespaceURI(), "xsd"));
            String businessConceptXSDTypeName;
            if (businessConceptXSDTypeNameLookup.length > 0) {
                businessConceptXSDTypeName = businessConceptXSDTypeNameLookup[0];
            } else {
                businessConceptXSDTypeName = businessConceptName;
            }

            fields = Util.getTextNodes(xsd.getDocumentElement(), "xsd:element/xsd:unique[@name='" + businessConceptName
                    + "']/xsd:field/@xpath", getRootElement("nsholder", xsd.getDocumentElement().getNamespaceURI(), "xsd"));

            if (selectors.length == 0) {
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                factory.setNamespaceAware(true);
                factory.setValidating(false);
                DocumentBuilder builder;
                NodeList list;

                for (int xsdType = 0; xsdType < 2; xsdType++) {
                    if (xsdType == 0) {
                        list = Util.getNodeList(xsd, "./xsd:import");
                    } else {
                        list = Util.getNodeList(xsd, "./xsd:include");
                    }
                    for (int elemNum = 0; elemNum < list.getLength(); elemNum++) {
                        Node importNode = list.item(elemNum);
                        if (importNode.getAttributes().getNamedItem("schemaLocation") == null) {
                            continue;
                        }
                        String xsdLocation = importNode.getAttributes().getNamedItem("schemaLocation").getNodeValue();
                        Pattern httpUrl = Pattern.compile("(http|https|ftp):(//|\\\\)(.*):(.*)");
                        Matcher match = httpUrl.matcher(xsdLocation);
                        Document d;
                        if (match.matches()) {
                            List<String> authorizations = Util.getAuthorizationInfo();
                            String data = Util.getResponseFromURL(xsdLocation, authorizations.get(0), authorizations.get(1));
                            d = Util.parse(data);
                        } else {
                            builder = factory.newDocumentBuilder();
                            d = builder.parse(new FileInputStream(xsdLocation));
                        }

                        key = getBusinessConceptKey(d, businessConceptName);
                        if (key != null) {
                            break;
                        }
                    }
                }
                return key;
            } else {
                String[] fieldTypes = new String[fields.length];
                int fieldIndex = 0;
                for (String field : fields) {
                    String[] xpathExpressions = new String[] {
                            "xsd:element[@name='" + businessConceptXSDTypeName + "']//xsd:element[@name='" + field + "']/@type",
                            "xsd:complexType[@name='" + businessConceptXSDTypeName + "']//xsd:element[@name='" + field
                                    + "']/@type" };

                    for (String xpathExpression : xpathExpressions) {
                        String[] textNodes = Util.getTextNodes(xsd.getDocumentElement(), xpathExpression,
                                getRootElement("nsholder", xsd.getDocumentElement().getNamespaceURI(), "xsd"));
                        if (textNodes.length == 1) {
                            fieldTypes[fieldIndex] = textNodes[0];
                        } else if (textNodes.length > 1) {
                            throw new IllegalStateException("Field '" + field + "' is not unique within type '"
                                    + businessConceptName + "' (XSD type: " + businessConceptXSDTypeName + ".");
                        }
                    }

                    if (fieldTypes[fieldIndex] == null) {
                        // Fix for TMDM-2378: in case of inheritance, field might not be in this type. Actual and fix
                        // requires
                        // huge refactoring of this method (possible use of com.amalto.core.metadata.TypeMetadata for
                        // instance).
                        fieldTypes[fieldIndex] = "xsd:string";
                    } else {
                        fieldIndex++;
                    }
                }

                key = new XSDKey(selectors[0], fields, fieldTypes);
                return key;
            }

        } catch (Exception e) {
            String err = "Unable to get the keys for the Business Concept " + businessConceptName + ": "
                    + e.getLocalizedMessage();
            throw new TransformerException(err, e);
        }

    }

    public static List<String> getAuthorizationInfo() {
        ArrayList<String> authorizations = new ArrayList<String>();
        String user = "", pwd = "";
        try {
            Subject subject = LocalUser.getCurrentSubject();
            Set<Principal> set = subject.getPrincipals();
            for (Principal principal : set) {
                if (principal instanceof Group) {
                    Group group = (Group) principal;
                    if ("Username".equals(group.getName())) {
                        if (group.members().hasMoreElements()) {
                            user = group.members().nextElement().getName();
                        }
                    } else if ("Password".equals(group.getName())) {
                        if (group.members().hasMoreElements()) {
                            pwd = group.members().nextElement().getName();
                        }
                    }
                }
            }
            authorizations.add(user);
            authorizations.add(pwd);
        } catch (XtentisException e) {
            Logger.getLogger(Util.class).error(e);
            return null;
        }
        return authorizations;
    }

    public static Map<String, XSElementDecl> getConceptMap(String xsd) throws Exception {
        XSOMParser reader = new XSOMParser();
        reader.setAnnotationParser(new DomAnnotationParserFactory());
        reader.setEntityResolver(new SecurityEntityResolver());
        SAXErrorHandler seh = new SAXErrorHandler();
        reader.setErrorHandler(seh);
        reader.parse(new StringReader(xsd));
        XSSchemaSet xss = reader.getResult();
        String errors = seh.getErrors();
        if (errors.length() > 0) {
            throw new SAXException("DataModel parsing error--->" + errors);
        }
        Collection xssList = xss.getSchemas();
        Map<String, XSElementDecl> mapForAll = new HashMap<String, XSElementDecl>();
        Map<String, XSElementDecl> map;
        for (Object xmlSchema : xssList) {
            XSSchema schema = (XSSchema) xmlSchema;
            map = schema.getElementDecls();
            mapForAll.putAll(map);
        }
        return mapForAll;
    }

    public static Map<String, XSType> getConceptTypeMap(String xsd) throws Exception {
        XSOMParser reader = new XSOMParser();
        reader.setAnnotationParser(new DomAnnotationParserFactory());
        reader.setEntityResolver(new SecurityEntityResolver());
        SAXErrorHandler seh = new SAXErrorHandler();
        reader.setErrorHandler(seh);
        reader.parse(new StringReader(xsd));
        XSSchemaSet xss = reader.getResult();
        String errors = seh.getErrors();
        if (errors.length() > 0) {
            throw new SAXException("DataModel parsing error--->" + errors);
        }
        Collection xssList = xss.getSchemas();
        Map<String, XSType> mapForAll = new HashMap<String, XSType>();
        Map<String, XSType> map;
        for (Object aXssList : xssList) {
            XSSchema schema = (XSSchema) aXssList;
            map = schema.getTypes();
            mapForAll.putAll(map);
        }
        return mapForAll;
    }

    public static String[] getKeyValuesFromItem(Element item, XSDKey key) throws TransformerException {
        return getItemKeyValues(item, key);
    }

    public static String[] getItemKeyValues(Element item, XSDKey xsdKey) throws TransformerException {
        try {
            if (xsdKey != null) {
                String[] values = new String[xsdKey.getFields().length];
                for (int i = 0; i < xsdKey.getFields().length; i++) {
                    String xpath = xsdKey.getFields()[i];
                    // Fix for TMDM-2449 Fails to extract id if xpath does not start with '/'
                    xpath = xpath.replaceFirst("/*" + item.getLocalName() + "/", "");
                    values[i] = Util.getFirstTextNode(item, xsdKey.getSelector() + "/" + xpath);
                    if (values[i] != null) {
                        values[i] = values[i].trim(); // FIXME: Due to eXist trimming values @see ItemPOJO
                    }
                }
                return values;
            }
        } catch (TransformerException e) {
            String err = "Unable to get the key value for the item " + item.getLocalName() + ": " + e.getClass().getName() + ": "
                    + e.getLocalizedMessage();
            throw new TransformerException(err);
        }
        return null;
    }

    /**
     * Extracts the item PK by reading it and its model<br>
     * The less costly of all methods since all parsing is already done
     * 
     * @return the Item PK
     */
    public static ItemPOJOPK getItemPOJOPK(DataClusterPOJOPK dataClusterPOJOPK, Element item, Document dataModel)
            throws TransformerException {
        String conceptName = item.getLocalName();
        String[] itemKeyValues;
        try {
            XSDKey conceptKey = Util.getBusinessConceptKey(dataModel, conceptName);
            // get key values
            itemKeyValues = Util.getItemKeyValues(item, conceptKey);
        } catch (TransformerException e) {
            String err = "Unable to get item PK for the item " + item.getLocalName() + ": " + e.getClass().getName() + ": "
                    + e.getLocalizedMessage();
            throw new TransformerException(err);
        }
        return new ItemPOJOPK(dataClusterPOJOPK, conceptName, itemKeyValues);
    }

    /**
     * Returns a namespaced root element of a document Useful to create a namespace holder element
     * 
     * @return the root Element
     */
    public static Element getRootElement(String elementName, String namespace, String prefix) throws TransformerException {
        Element rootNS;
        try {

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            DocumentBuilder builder = factory.newDocumentBuilder();
            DOMImplementation impl = builder.getDOMImplementation();
            Document namespaceHolder = impl.createDocument(namespace, (prefix == null ? "" : prefix + ":") + elementName, null);
            rootNS = namespaceHolder.getDocumentElement();
            rootNS.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:" + prefix, namespace);

        } catch (Exception e) {
            String err = "Error creating a namespace holder document: " + e.getLocalizedMessage();
            throw new TransformerException(err, e);
        }

        return rootNS;
    }

    /**
     * Generates an xml string from a node (not pretty formatted)
     * 
     * @param n the node
     * @return the xml string
     * @throws TransformerException
     */
    public static String nodeToString(Node n) throws TransformerException {
        return nodeToString(n, true);
    }

    /**
     * Generates an xml string from a node with or without the xml declaration (not pretty formatted)
     * 
     * @param n the node
     * @return the xml string
     * @throws TransformerException
     */
    public static String nodeToString(Node n, boolean omitXMLDeclaration) throws TransformerException {
        return nodeToString(n, omitXMLDeclaration, LOGGER.isDebugEnabled());
    }

    public static String nodeToString(Node n, boolean omitXMLDeclaration, boolean indent) throws TransformerException {
        StringWriter sw = new StringWriter();
        Transformer transformer = transformerFactory.newTransformer();
        if (omitXMLDeclaration) {
            transformer.setOutputProperty("omit-xml-declaration", "yes");
        } else {
            transformer.setOutputProperty("omit-xml-declaration", "no");
        }
        if (indent) {
            transformer.setOutputProperty("indent", "yes");
        }
        transformer.transform(new DOMSource(n), new StreamResult(sw));
        return sw.toString().replaceAll("\r\n", "\n");
    }

    public static void removeAllAttribute(Node node, String attrName) {
        if (node.getNodeType() == Node.ELEMENT_NODE) {
            NamedNodeMap attributes = node.getAttributes();
            for (int i = 0; i < attributes.getLength(); i++) {
                if (attributes.item(i).getNodeName().equals(attrName)) {
                    attributes.removeNamedItem(attrName);
                    break;
                }
            }
        }

        NodeList list = node.getChildNodes();
        if (list.getLength() > 0) {
            for (int i = 0; i < list.getLength(); i++) {
                removeAllAttribute(list.item(i), attrName);
            }
        }

    }

    /**
     * Get a node list from an xPath
     * 
     * @throws XtentisException
     */
    public static NodeList getNodeList(Document d, String xPath) throws XtentisException {
        return getNodeList(d.getDocumentElement(), xPath, null, null);
    }

    /**
     * Get a node list from an xPath
     * 
     * @throws XtentisException
     */
    public static NodeList getNodeList(Node contextNode, String xPath) throws XtentisException {
        return getNodeList(contextNode, xPath, null, null);
    }

    /**
     * Get a node list from an xPath
     * 
     * @throws XtentisException
     */
    public static NodeList getNodeList(Node contextNode, String xPath, final String namespace, final String prefix)
            throws XtentisException {
        try {
            XPathFactory xPathFactory = XPathFactory.newInstance();
            XPath xPathParser = xPathFactory.newXPath();
            xPathParser.setNamespaceContext(new NamespaceContext() {

                @Override
                public String getNamespaceURI(String s) {
                    if (prefix != null && prefix.equals(s)) {
                        return namespace;
                    } else if ("xsd".equals(s)) { //$NON-NLS-1$
                        return XMLConstants.W3C_XML_SCHEMA_NS_URI;
                    }
                    return null;
                }

                @Override
                public String getPrefix(String s) {
                    if (namespace != null && namespace.equals(s)) {
                        return prefix;
                    } else if (XMLConstants.W3C_XML_SCHEMA_NS_URI.equals(s)) {
                        return "xsd"; //$NON-NLS-1$
                    }
                    return null;
                }

                @Override
                public Iterator getPrefixes(String s) {
                    return Collections.singletonList(s).iterator();
                }
            });
            return (NodeList) xPathParser.evaluate(xPath, contextNode, XPathConstants.NODESET);
        } catch (Exception e) {
            String err = "Unable to get the Nodes List for xpath '" + xPath + "'"
                    + ((contextNode == null) ? "" : " for Node " + contextNode.getLocalName()) + ": " + e.getLocalizedMessage();
            throw new XtentisException(err, e);
        }
    }

    /**
     * Join an array of strings into a single string using a separator
     * 
     * @return a single string or null
     */
    public static String joinStrings(String[] strings, String separator) {
        if (strings == null) {
            return null;
        }
        StringBuilder res = new StringBuilder();
        for (int i = 0; i < strings.length; i++) {
            res.append(i > 0 ? separator : StringUtils.EMPTY);
            res.append(strings[i] == null ? StringUtils.EMPTY : strings[i]);
        }
        return res.toString();
    }

    public static Subject getActiveSubject() throws XtentisException {
        return null; // TODO
    }

    /**
     * Extracts the username of the logged user from the {@link Subject}
     * 
     * @return The username
     * @throws XtentisException
     */
    public static String getUsernameFromSubject(Subject subject) throws XtentisException {
        Set<Principal> set = subject.getPrincipals();
        for (Principal principal : set) {
            if (!(principal instanceof Group)) {
                return principal.getName();
            }
        }
        return null;
    }

    /**
     * @return the username/password
     */
    public static String getUsernameAndPasswordToken() {
        String token = null;
        try {
            String userName = null;
            String password = null;
            Subject subject = LocalUser.getCurrentSubject();
            if (subject != null) {
                Set<Principal> set = subject.getPrincipals();
                if (set != null) {
                    for (Principal principal : set) {
                        if (principal instanceof Group) {
                            Group group = (Group) principal;
                            if ("Username".equals(group.getName())) {
                                if (group.members().hasMoreElements()) {
                                    userName = group.members().nextElement().getName();
                                }
                            } else if ("Password".equals(group.getName())) {
                                if (group.members().hasMoreElements()) {
                                    password = group.members().nextElement().getName();
                                }
                            }
                        }
                    }
                }
            }
            if (userName == null) {
                userName = "";
            }
            if (password == null) {
                password = "";
            }
            token = userName + "/" + password;
        } catch (XtentisException e) {
            Logger.getLogger(Util.class).error(e);
        }
        return token;
    }

    public static String getTimestamp() {
        return getTimestamp(new Date());
    }

    public static String getTimestamp(Date d) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd'T'HH:mm:ss.SSS' 'z");
        return sdf.format(d);
    }

    public static com.amalto.core.server.api.Role getRoleCtrlLocal() {
        if (role == null) {
            role = new DefaultRole();
        }
        return role;
    }

    public static CustomForm getCustomFormCtrlLocal() {
        if (defaultCustomForm == null) {
            defaultCustomForm = new DefaultCustomForm();
        }
        return defaultCustomForm;
    }

    public static RoutingOrder getRoutingOrderV2CtrlLocal() {
        if (defaultRoutingOrder == null) {
            defaultRoutingOrder = new DefaultRoutingOrder();
        }
        return defaultRoutingOrder;
    }

    public static RoutingRule getRoutingRuleCtrlLocal() {
        if (defaultRoutingRule == null) {
            defaultRoutingRule = new DefaultRoutingRule();
        }
        return defaultRoutingRule;
    }
    
    public static StoredProcedure getStoredProcedureCtrlLocal() {
        if (defaultStoredProcedure == null) {
            defaultStoredProcedure = new DefaultStoredProcedure();
        }
        return defaultStoredProcedure;
    }

    public static Item getItemCtrl2Local() {
        if (defaultItem == null) {
            defaultItem = new DefaultItem();
        }
        return defaultItem;
    }

    public static DroppedItem getDroppedItemCtrlLocal() {
        if (defaultDroppedItem == null) {
            defaultDroppedItem = new DefaultDroppedItem();
        }
        return defaultDroppedItem;
    }

    public static DataModel getDataModelCtrlLocal() {
        if (defaultDataModel == null) {
            defaultDataModel = new DefaultDataModel();
        }
        return defaultDataModel;
    }

    public static XmlServer getXmlServerCtrlLocal() {
        if (defaultXmlServer == null) {
            defaultXmlServer = new DefaultXmlServer();
        }
        return defaultXmlServer;
    }

    public static DataCluster getDataClusterCtrlLocal() {
        if (defaultDataCluster == null) {
            defaultDataCluster = new DefaultDataCluster();
        }
        return defaultDataCluster;
    }

    public static View getViewCtrlLocal() {
        if (defaultView == null) {
            defaultView = new DefaultView();
        }
        return defaultView;
    }

    public static com.amalto.core.server.api.Transformer getTransformerV2CtrlLocal() {
        if (defaultTransformer == null) {
            defaultTransformer = new DefaultTransformer();
        }
        return defaultTransformer;
    }

    public static Menu getMenuCtrlLocal() {
        if (menu == null) {
            menu = new DefaultMenu();
        }
        return menu;
    }

    public static BackgroundJob getBackgroundJobCtrlLocal() {
        if (defaultBackgroundJob == null) {
            defaultBackgroundJob = new DefaultBackgroundJob();
        }
        return defaultBackgroundJob;
    }

    public static ConfigurationInfo getConfigurationInfoCtrlLocal() {
        if (configurationInfo == null) {
            configurationInfo = new DefaultConfigurationInfo();
        }
        return configurationInfo;
    }

    public static RoutingEngine getRoutingEngineV2CtrlLocal() {
        if (defaultRoutingEngine == null) {
            defaultRoutingEngine = new DefaultRoutingEngine();
        }
        return defaultRoutingEngine;
    }

    private static Pattern extractCharsetPattern = Pattern.compile(".*charset\\s*=(.+)");

    /**
     * Extract the charset of a content type<br>
     * e.g 'utf-8' in 'text/xml; charset="utf-8"'
     * 
     * @return the charset
     */
    public static String extractCharset(String contentType) {
        String charset = org.apache.commons.lang.CharEncoding.UTF_8;
        String properties[] = StringUtils.split(contentType, ';');
        for (String property : properties) {
            String strippedProperty = property.trim().replaceAll("\"", "").replaceAll("'", "");
            Matcher m = extractCharsetPattern.matcher(strippedProperty);
            if (m.matches()) {
                charset = m.group(1).trim().toUpperCase();
                break;
            }
        }
        return charset;
    }

    /**
     * Extract the MIME type and sub type of a content type<br>
     * e.g 'text/xml' in 'text/xml; charset="utf-8"'
     * 
     * @return the MIME Type and SubType
     */
    public static String extractTypeAndSubType(String contentType) {
        if (contentType == null) {
            return null;
        }
        return contentType.split(";")[0].trim().toLowerCase();
    }

    /**
     * Extracts a byte array from an InputStream
     * 
     * @return the byte array
     * @throws IOException
     */
    public static byte[] getBytesFromStream(InputStream is) throws IOException {
        if (is == null) {
            return null;
        }
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        int b;
        while ((b = is.read()) != -1) {
            bos.write(b);
        }
        return bos.toByteArray();
    }

    public static ItemPOJO getItem(String dataCluster, String xml) throws Exception {
        Element root = Util.parse(xml).getDocumentElement();

        String concept = root.getLocalName();

        DataModelPOJO dataModel = Util.getDataModelCtrlLocal().getDataModel(new DataModelPOJOPK(dataCluster));
        Document schema = Util.parse(dataModel.getSchema());
        XSDKey conceptKey = com.amalto.core.util.Util.getBusinessConceptKey(schema, concept);

        // get key values
        String[] ids = com.amalto.core.util.Util.getKeyValuesFromItem(root, conceptKey);
        DataClusterPOJOPK dataClusterPOJOPK = new DataClusterPOJOPK(dataCluster);
        ItemPOJOPK itemPOJOPK = new ItemPOJOPK(dataClusterPOJOPK, concept, ids);
        return ItemPOJO.load(itemPOJOPK);
    }

    /**
     * Executes a BeforeSaving process if any
     * 
     * @param concept A concept/type name.
     * @param xml The xml of the document being saved.
     * @param resultUpdateReport Update report that corresponds to the save event.
     * @return Either <code>null</code> if no process was found or a status message (0=success; <>0=failure) of the form
     * &lt;error code='1'&gt;This is a message&lt;/error>
     * @throws Exception If something went wrong
     */
    @SuppressWarnings("unchecked")
    public static OutputReport beforeSaving(String concept, String xml, String resultUpdateReport) throws Exception {
        // check before saving transformer
        boolean isBeforeSavingTransformerExist = false;
        Collection<TransformerV2POJOPK> wst = getTransformerV2CtrlLocal().getTransformerPKs("*");
        for (TransformerV2POJOPK id : wst) {
            if (id.getIds()[0].equals("beforeSaving_" + concept)) { //$NON-NLS-1$
                isBeforeSavingTransformerExist = true;
                break;
            }
        }
        // call before saving transformer
        if (isBeforeSavingTransformerExist) {

            try {
                final String RUNNING = "XtentisWSBean.executeTransformerV2.running"; //$NON-NLS-1$
                TransformerContext context = new TransformerContext(new TransformerV2POJOPK("beforeSaving_" + concept)); //$NON-NLS-1$
                String exchangeData = mergeExchangeData(xml, resultUpdateReport);
                context.put(RUNNING, Boolean.TRUE);
                com.amalto.core.server.api.Transformer ctrl = getTransformerV2CtrlLocal();
                TypedContent wsTypedContent = new TypedContent(exchangeData.getBytes("UTF-8"), "text/xml; charset=utf-8"); //$NON-NLS-1$
                ctrl.execute(context, wsTypedContent, new TransformerCallBack() {

                    @Override
                    public void contentIsReady(TransformerContext context) throws XtentisException {
                        LOGGER.debug(
                                "XtentisWSBean.executeTransformerV2.contentIsReady() "); //$NON-NLS-1$
                    }

                    @Override
                    public void done(TransformerContext context) throws XtentisException {
                        LOGGER.debug("XtentisWSBean.executeTransformerV2.done() "); //$NON-NLS-1$
                        context.put(RUNNING, Boolean.FALSE);
                    }
                });
                while ((Boolean) context.get(RUNNING)) {
                    Thread.sleep(100);
                }
                // TODO process no plug-in issue
                String message = "<report><message type=\"error\"/></report> "; //$NON-NLS-1$;
                String item = null;
                // Scan the entries - in priority, aka the content of the 'output_error_message' entry,
                boolean hasOutputReport = false;
                for (Entry<String, TypedContent> entry : context.getPipelineClone().entrySet()) {
                    if (ITransformerConstants.VARIABLE_OUTPUT_OF_BEFORESAVINGTRANFORMER.equals(entry.getKey())) {
                        hasOutputReport = true;
                        message = new String(entry.getValue().getContentBytes(), "UTF-8"); //$NON-NLS-1$                        
                    }
                    if (ITransformerConstants.VARIABLE_OUTPUTITEM_OF_BEFORESAVINGTRANFORMER.equals(entry.getKey())) {
                        item = new String(entry.getValue().getContentBytes(), "UTF-8"); //$NON-NLS-1$                        
                    }
                }
                if (!hasOutputReport) {
                    throw new OutputReportMissingException("Output variable 'output_report' is missing"); //$NON-NLS-1$
                }
                return new OutputReport(message, item);
            } catch (Exception e) {
                Logger.getLogger(Util.class).error(e);
                throw e;
            }
        }
        return null;
    }

    public static boolean existsComponent(Object o, String jndiName) {
        return false;
    }

    public static Object retrieveComponent(Object o, String jndiName) {
        return null;
    }

    public static boolean isEnterprise() {
        return true; // TODO
    }

    public static class BeforeDeleteResult {

        public String type;

        public String message;
    }

    /**
     * Executes a BeforeDeleting process if any
     * 
     * @param clusterName A data cluster name
     * @param concept A concept/type name
     * @param ids Id of the document being deleted
     * @throws Exception If something went wrong
     */
    @SuppressWarnings("unchecked")
    public static BeforeDeleteResult beforeDeleting(String clusterName, String concept, String[] ids, String operationType) throws Exception {
        // check before deleting transformer
        boolean isBeforeDeletingTransformerExist = false;
        Collection<TransformerV2POJOPK> transformers = getTransformerV2CtrlLocal().getTransformerPKs("*");
        for (TransformerV2POJOPK id : transformers) {
            if (id.getIds()[0].equals("beforeDeleting_" + concept)) {
                isBeforeDeletingTransformerExist = true;
                break;
            }
        }
        if (isBeforeDeletingTransformerExist) {
            try {
                // call before deleting transformer
                // load the item
                ItemPOJOPK itemPk = new ItemPOJOPK(new DataClusterPOJOPK(clusterName), concept, ids);
                ItemPOJO pojo = ItemPOJO.load(itemPk);
                String xml = null;
                if (pojo == null) {// load from recyclebin
                    DroppedItemPOJOPK droppedItemPk = new DroppedItemPOJOPK(null, itemPk, "/");//$NON-NLS-1$
                    DroppedItemPOJO dpPojo = Util.getDroppedItemCtrlLocal().loadDroppedItem(droppedItemPk);
                    if (dpPojo != null) {
                        xml = dpPojo.getProjection();
                        Document doc = Util.parse(xml);
                        Node item = (Node) XPathFactory.newInstance().newXPath().evaluate("//ii/p", doc, XPathConstants.NODE); //$NON-NLS-1$
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
                                xml = Util.nodeToString(node);
                            }
                        }
                    }
                } else {
                    xml = pojo.getProjectionAsString();
                }
                String resultUpdateReport = Util.createUpdateReport(ids, concept, operationType, null, StringUtils.EMPTY, clusterName);
                String exchangeData = mergeExchangeData(xml, resultUpdateReport);
                final String runningKey = "XtentisWSBean.executeTransformerV2.beforeDeleting.running";
                TransformerContext context = new TransformerContext(new TransformerV2POJOPK("beforeDeleting_" + concept));
                context.put(runningKey, Boolean.TRUE);
                com.amalto.core.server.api.Transformer ctrl = getTransformerV2CtrlLocal();
                TypedContent wsTypedContent = new TypedContent(exchangeData.getBytes("UTF-8"), "text/xml; charset=utf-8");

                ctrl.execute(context, wsTypedContent, new TransformerCallBack() {

                    @Override
                    public void contentIsReady(TransformerContext context) throws XtentisException {
                        LOGGER.debug("XtentisWSBean.executeTransformerV2.beforeDeleting.contentIsReady() ");
                    }

                    @Override
                    public void done(TransformerContext context) throws XtentisException {
                        LOGGER.debug("XtentisWSBean.executeTransformerV2.beforeDeleting.done() ");
                        context.put(runningKey, Boolean.FALSE);
                    }
                });
                while ((Boolean) context.get(runningKey)) { // TODO Poor-man synchronization here
                    Thread.sleep(100);
                }
                // TODO process no plug-in issue
                String outputErrorMessage = null;
                // Scan the entries - in priority, aka the content of the 'output_error_message' entry.
                for (Entry<String, TypedContent> entry : context.getPipelineClone().entrySet()) {
                    if (ITransformerConstants.VARIABLE_OUTPUT_OF_BEFORESAVINGTRANFORMER.equals(entry.getKey())) {
                        outputErrorMessage = new String(entry.getValue().getContentBytes(), "UTF-8");
                        break;
                    }
                }
                // handle error message
                BeforeDeleteResult result = new BeforeDeleteResult();
                if (outputErrorMessage == null) {
                    LOGGER.warn("No message generated by before delete process.");
                    result.type = "info";
                    result.message = StringUtils.EMPTY;
                } else {
                    if (outputErrorMessage.length() > 0) {
                        Document doc = Util.parse(outputErrorMessage);
                        // TODO what if multiple error nodes ?
                        String xpath = "/report/message"; //$NON-NLS-1$
                        Node errorNode = (Node) XPathFactory.newInstance().newXPath().evaluate(xpath, doc, XPathConstants.NODE);
                        if (errorNode instanceof Element) {
                            Element errorElement = (Element) errorNode;
                            result.type = errorElement.getAttribute("type"); //$NON-NLS-1$
                            Node child = errorElement.getFirstChild();
                            if (child instanceof Text) {
                                result.message = child.getTextContent();
                            }
                        }
                    } else {
                        result.type = "error";
                        result.message = "<report><message type=\"error\"/></report>"; //$NON-NLS-1$
                    }
                }
                return result;
            } catch (Exception e) {
                LOGGER.error(e);
                throw e;
            }
        }
        return null;
    }

    static AbstractFactory factory = new AbstractFactory() {

        @Override
        public boolean createObject(JXPathContext context, Pointer pointer, Object parent, String name, int index) {
            if (parent instanceof Node) {
                try {
                    Node node = (Node) parent;
                    Document doc1 = node.getOwnerDocument();
                    Element e = doc1.createElement(name);
                    if (index > 0) { // list
                        Pointer p = context.getRelativeContext(pointer).getPointer(name + "[" + (index) + "]");
                        Node curNode = (Node) p.getNode();
                        if (curNode != null) {
                            if (curNode.getNextSibling() != null) {
                                node.insertBefore(e, curNode.getNextSibling());
                            } else {
                                node.appendChild(e);
                            }
                        } else {
                            node.appendChild(e);
                        }
                    } else {
                        node.appendChild(e);
                    }
                    return true;
                } catch (Exception e) {
                    return false;
                }
            } else {
                return false;
            }
        }

        @Override
        public boolean declareVariable(JXPathContext context, String name) {
            return false;
        }
    };

    /**
     * update the element according to updated path
     * 
     * @throws Exception
     */
    public static Node updateElement(Node old, Map<String, UpdateReportItem> updatedPath) throws Exception {
        if (updatedPath.size() == 0) {
            return old;
        }
        // use JXPathContext to update the old element
        JXPathContext jxpContext = JXPathContext.newContext(old);
        jxpContext.registerNamespace("xsi", "http://www.w3.org/2001/XMLSchema-instance");
        jxpContext.registerNamespace("tmdm", "http://www.talend.com/mdm");
        jxpContext.setLenient(true);

        jxpContext.setFactory(factory);
        String concept = old.getLocalName();
        for (Map.Entry<String, UpdateReportItem> entry : updatedPath.entrySet()) {
            String xpath = entry.getValue().getPath();
            if (xpath.startsWith("/" + concept + "/")) {
                xpath = xpath.replaceFirst("/" + concept + "/", "");
            }
            jxpContext.createPathAndSetValue(xpath, entry.getValue().newValue);
        }
        return (Node) jxpContext.getContextBean();
    }

    public static String mergeExchangeData(String xml, String resultUpdateReport) {
        String exchangeData = "<exchange>\n";
        exchangeData += "<report>" + resultUpdateReport + "</report>";
        exchangeData += "\n";
        exchangeData += "<item>" + xml + "</item>";
        exchangeData += "\n</exchange>";
        return exchangeData;
    }

    public static String createUpdateReport(String[] ids, String concept, String operationType,
            Map<String, UpdateReportItem> updatedPath, String dataModelPK, String dataClusterPK) throws Exception {
        String username;
        String revisionId = "";
        try {
            username = LocalUser.getLocalUser().getUsername();
            UniversePOJO pojo = LocalUser.getLocalUser().getUniverse();
            if (pojo != null) {
                revisionId = pojo.getConceptRevisionID(concept);
            }
        } catch (Exception e1) {
            Logger.getLogger(Util.class).error(e1);
            throw e1;
        }

        String key = "";
        if (ids != null) {
            for (int i = 0; i < ids.length; i++) {
                key += ids[i];
                if (i != ids.length - 1) {
                    key += ".";
                }
            }
        }
        String xml2 = "" + "<Update>" + "<UserName>" + username + "</UserName>" + "<Source>genericUI</Source>" + "<TimeInMillis>"
                + System.currentTimeMillis() + "</TimeInMillis>" + "<OperationType>" + StringEscapeUtils.escapeXml(operationType)
                + "</OperationType>" + "<RevisionID>" + revisionId + "</RevisionID>" + "<DataCluster>" + dataClusterPK
                + "</DataCluster>" + "<DataModel>" + dataModelPK + "</DataModel>" + "<Concept>"
                + StringEscapeUtils.escapeXml(concept) + "</Concept>" + "<Key>" + StringEscapeUtils.escapeXml(key) + "</Key>";
        if (UpdateReportPOJO.OPERATION_TYPE_UPDATE.equals(operationType)) {
            Collection<UpdateReportItem> list = updatedPath.values();
            boolean isUpdate = false;
            for (UpdateReportItem item : list) {
                String oldValue = item.getOldValue() == null ? "" : item.getOldValue();
                String newValue = item.getNewValue() == null ? "" : item.getNewValue();
                if (newValue.equals(oldValue)) {
                    continue;
                }
                xml2 += "<Item>" + "   <path>" + StringEscapeUtils.escapeXml(item.getPath()) + "</path>" + "   <oldValue>"
                        + StringEscapeUtils.escapeXml(oldValue) + "</oldValue>" + "   <newValue>"
                        + StringEscapeUtils.escapeXml(newValue) + "</newValue>" + "</Item>";
                isUpdate = true;
            }
            if (!isUpdate) {
                return null;
            }
        }
        xml2 += "</Update>";
        return xml2;
    }

    public static String checkOnVersionCompatibility(WSVersion old) {
        Version version = Version.getVersion(IXtentisWSDelegator.class);
        String oldVersion = old.getMajor() + "." + old.getMinor() + "." + old.getRevision();
        String newVersion = version.getMajor() + "." + version.getMinor() + "." + version.getRevision();
        String str = "The two MDM Servers is not compatible, one is " + oldVersion + ", another is " + newVersion;
        if (version.getMajor() != old.getMajor() || version.getMinor() != old.getMinor()) {
            return str;
        }
        return null;
    }

    public static String getAppServerDeployDir() {
        String appServerDeployDir = System.getenv("JBOSS_HOME");
        if (appServerDeployDir == null) {
            appServerDeployDir = System.getProperty("jboss.home.dir");
        }
        if (appServerDeployDir == null) {
            appServerDeployDir = System.getProperty("user.dir");
        }
        return appServerDeployDir;
    }

    public static String getJbossHomeDir() {
        String jbossHomePath = com.amalto.core.util.Util.getAppServerDeployDir();
        return new File(jbossHomePath).getAbsolutePath();
    }

    public static String getBarHomeDir() {
        return Util.getJbossHomeDir() + File.separator + "barfiles"; //$NON-NLS-1$
    }

    public static List<File> listFiles(FileFilter filter, File folder) {
        List<File> ret = new ArrayList<File>();
        File[] children = folder.listFiles(filter);
        for (File f : children) {
            if (f.isFile()) {
                ret.add(f);
            } else {
                ret.addAll(listFiles(filter, f));
            }
        }
        return ret;
    }

    public static void getView(Set<String> views, IWhereItem whereItem) {
        if (whereItem instanceof WhereLogicOperator) {
            Collection<IWhereItem> subItems = ((WhereLogicOperator) whereItem).getItems();
            if (subItems.size() == 1) {
                getView(views, subItems.iterator().next());
            }
            for (IWhereItem item : subItems) {
                getView(views, item);
            }
        } else if (whereItem instanceof WhereCondition) {
            WhereCondition whereCondition = (WhereCondition) whereItem;
            views.add(whereCondition.getLeftPath());
        }
        // FIXME do we to consider CustomWhereCondition
    }

    public static Element getLoginProvisioningFromDB() throws Exception {

        ItemPOJOPK itemPk = new ItemPOJOPK(new DataClusterPOJOPK("PROVISIONING"), "User", new String[] { LocalUser.getLocalUser()
                .getUsername() });
        ItemPOJO item = ItemPOJO.load(itemPk);
        if (item == null) {
            return null;
        }
        return (Element) Util.getNodeList(item.getProjection(), "//User").item(0);
    }

    public static String getUserDataModel() throws Exception {
        return getUserDataModel(getLoginProvisioningFromDB());
    }

    public static String getUserDataModel(Element item) throws Exception {
        NodeList nodeList = Util.getNodeList(item, "//property");
        if (nodeList != null) {
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                if ("model".equals(Util.getFirstTextNode(node, "name"))) {
                    Node firstChild = Util.getNodeList(node, "value").item(0).getFirstChild();
                    return firstChild.getNodeValue();
                }
            }
        }
        return null;
    }

    public static String getUserDataCluster() throws Exception {
        return getUserDataCluster(getLoginProvisioningFromDB());
    }

    public static String getUserDataCluster(Element item) throws Exception {
        NodeList nodeList = Util.getNodeList(item, "//property");
        if (nodeList != null) {
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                if ("cluster".equals(Util.getFirstTextNode(node, "name"))) {
                    // configuration.setCluster(Util.getNodeList(node, "value").item(0).getTextContent());
                    Node firstChild = Util.getNodeList(node, "value").item(0).getFirstChild();
                    return firstChild.getNodeValue();
                }
            }
        }
        return null;
    }

    public static Map<String, ArrayList<String>> getMetaDataTypes(IWhereItem whereItem) throws Exception {
        return getMetaDataTypes(whereItem, null);
    }

    // FIXME: Seems it is useless to set an ArrayList for each xpath
    public static Map<String, ArrayList<String>> getMetaDataTypes(IWhereItem whereItem, SchemaManager schemaManager)
            throws Exception {
        HashMap<String, ArrayList<String>> metaDataTypes = new HashMap<String, ArrayList<String>>();

        if (whereItem == null) {
            return null;
        }

        // Get concepts from where conditions
        Set<String> searchPaths = new HashSet<String>();
        Util.getView(searchPaths, whereItem);

        Set<String> concepts = new HashSet<String>();
        for (String searchPath : searchPaths) {
            concepts.add(searchPath.split("/")[0]);
        }

        // Travel concepts to parse metadata types
        for (String conceptName : concepts) {
            if (schemaManager == null) {
                schemaManager = SchemaCoreAgent.getInstance();
            }
            BusinessConcept bizConcept = schemaManager.getBusinessConceptForCurrentUser(conceptName);
            if (bizConcept == null) {
                break;
            }
            bizConcept.load();
            Map<String, String> xpathTypeMap = bizConcept.getXpathTypeMap();
            for (String xpath : xpathTypeMap.keySet()) {
                String elType = xpathTypeMap.get(xpath);
                ArrayList<String> elTypeWrapper = new ArrayList<String>() {
                };
                elTypeWrapper.add(elType);
                metaDataTypes.put(xpath, elTypeWrapper);
            }
        }

        return metaDataTypes;
    }

    public static IWhereItem fixWebConditions(IWhereItem whereItem, String userXML) throws Exception {
        if (whereItem == null) {
            return null;
        }
        if (whereItem instanceof WhereLogicOperator) {
            List<IWhereItem> subItems = ((WhereLogicOperator) whereItem).getItems();
            for (int i = subItems.size() - 1; i >= 0; i--) {
                IWhereItem item = subItems.get(i);
                item = fixWebConditions(item, userXML);
                if (item instanceof WhereLogicOperator) {
                    if (((WhereLogicOperator) item).getItems().size() == 0) {
                        subItems.remove(i);
                    }
                } else if (item instanceof WhereCondition) {
                    WhereCondition condition = (WhereCondition) item;
                    if (condition.getRightValueOrPath() != null && condition.getRightValueOrPath().length() > 0
                            && condition.getRightValueOrPath().contains(USER_PROPERTY_PREFIX)) {
                        subItems.remove(i);
                    }
                } else if (item == null) {
                    subItems.remove(i);
                }
            }
        } else if (whereItem instanceof WhereCondition) {
            WhereCondition condition = (WhereCondition) whereItem;
            if (condition.getRightValueOrPath() != null && condition.getRightValueOrPath().length() > 0
                    && condition.getRightValueOrPath().contains(USER_PROPERTY_PREFIX)) {
                // TMDM-7207: Only create the groovy script engine if needed (huge performance issues)
                // TODO Should there be some pool of ScriptEngine instances? (is reusing ok?)
                ScriptEngine scriptEngine = SCRIPTFACTORY.getEngineByName("groovy"); //$NON-NLS-1$
                if (userXML != null && !userXML.isEmpty()) {
                    User user = User.parse(userXML);
                    scriptEngine.put("user_context", user);//$NON-NLS-1$
                }
                String rightCondition = condition.getRightValueOrPath();
                String userExpression = rightCondition.substring(rightCondition.indexOf('{') + 1, rightCondition.indexOf('}'));
                try {
                    if (LOGGER.isDebugEnabled()) {
                        LOGGER.debug("Groovy engine evaluating " + userExpression + ".");//$NON-NLS-1$ //$NON-NLS-2$
                    }
                    Object expressionValue = scriptEngine.eval(userExpression);
                    if (expressionValue != null) {
                        String result = String.valueOf(expressionValue);
                        if (!"".equals(result.trim())) {
                            condition.setRightValueOrPath(result);
                        }
                    }
                } catch (Exception e) {
                    if (LOGGER.isDebugEnabled()) {
                        LOGGER.debug("No such property " + userExpression, e);
                    }
                }
            }

            whereItem = "*".equals(condition.getRightValueOrPath()) || condition.getRightValueOrPath().length() == 0 //$NON-NLS-1$
                    || ".*".equals(condition.getRightValueOrPath()) ? null : whereItem; //$NON-NLS-1$
        } else {
            throw new XmlServerException("Unknown Where Type : " + whereItem.getClass().getName());
        }
        if (whereItem instanceof WhereLogicOperator) {
            return ((WhereLogicOperator) whereItem).getItems().size() == 0 ? null : whereItem;
        }
        return whereItem;
    }

    /**
     * fix the conditions.
     * 
     * @param conditions in workbench.
     */

    public static void fixConditions(ArrayList conditions) {
        for (int i = conditions.size() - 1; i >= 0; i--) {
            if (conditions.get(i) instanceof WhereCondition) {
                WhereCondition condition = (WhereCondition) conditions.get(i);

                if (condition.getRightValueOrPath() == null
                        || (condition.getRightValueOrPath().length() == 0 && !condition.getOperator().equals(
                                WhereCondition.EMPTY_NULL))) {
                    conditions.remove(i);
                }
            }
        }
    }

    public static Boolean isContainUserProperty(List conditions) {
        for (int i = conditions.size() - 1; i >= 0; i--) {
            if (conditions.get(i) instanceof WhereCondition) {
                WhereCondition condition = (WhereCondition) conditions.get(i);

                if (condition.getRightValueOrPath() != null && condition.getRightValueOrPath().length() > 0
                        && condition.getRightValueOrPath().contains(USER_PROPERTY_PREFIX)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static void updateUserPropertyCondition(List conditions, String userXML) throws Exception {
        for (int i = conditions.size() - 1; i >= 0; i--) {
            if (conditions.get(i) instanceof WhereCondition) {
                WhereCondition condition = (WhereCondition) conditions.get(i);
                if (condition.getRightValueOrPath() != null && condition.getRightValueOrPath().length() > 0
                        && condition.getRightValueOrPath().contains(USER_PROPERTY_PREFIX)) {

                    String rightCondition = condition.getRightValueOrPath();
                    String userExpression = rightCondition
                            .substring(rightCondition.indexOf("{") + 1, rightCondition.indexOf("}"));//$NON-NLS-1$ //$NON-NLS-2$
                    try {
                        if (LOGGER.isDebugEnabled()) {
                            LOGGER.debug("Groovy engine evaluating " + userExpression + ".");//$NON-NLS-1$ //$NON-NLS-2$
                        }
                        ScriptEngine scriptEngine = SCRIPTFACTORY.getEngineByName("groovy"); //$NON-NLS-1$
                        User user = User.parse(userXML);
                        scriptEngine.put("user_context", user);//$NON-NLS-1$
                        Object expressionValue = scriptEngine.eval(userExpression);
                        if (expressionValue != null) {
                            String result = String.valueOf(expressionValue);
                            if (!"".equals(result.trim())) {
                                condition.setRightValueOrPath(result);
                            } else {
                                conditions.remove(i);
                            }
                        } else {
                            conditions.remove(i);
                        }
                    } catch (Exception e) {
                        if (LOGGER.isDebugEnabled()) {
                            LOGGER.debug(e.getMessage(), e);
                        }
                        LOGGER.warn("No such property " + userExpression);
                        conditions.remove(i);
                    }
                }
            }
        }
    }

    public static WSMDMJob[] getMDMJobs() {
        List<WSMDMJob> jobs = new ArrayList<WSMDMJob>();
        try {
            String jbossHomePath = Util.getAppServerDeployDir();
            String deployDir = "";
            try {
                deployDir = new File(jbossHomePath).getAbsolutePath();
            } catch (Exception e1) {
                Logger.getLogger(Util.class).error(e1);
            }
            deployDir = deployDir + File.separator + "server" + File.separator + "default" + File.separator + "deploy";
            Logger.getLogger(Util.class).info("deploy url:" + deployDir);
            if (!new File(deployDir).exists()) {
                throw new FileNotFoundException();
            }
            // TODO is it recursive
            FileFilter filter = new FileFilter() {

                @Override
                public boolean accept(File pathName) {
                    return pathName.isDirectory() || (pathName.isFile() && pathName.getName().toLowerCase().endsWith(".war"));
                }
            };
            List<File> warFiles = listFiles(filter, new File(deployDir));
            // zip
            filter = new FileFilter() {

                @Override
                public boolean accept(File pathName) {
                    return pathName.isDirectory() || (pathName.isFile() && pathName.getName().toLowerCase().endsWith(".zip"));
                }
            };
            List<File> zipFiles = listFiles(filter, new File(JobContainer.getUniqueInstance().getDeployDir()));
            for (File war : warFiles) {
                WSMDMJob job = getJobInfo(war.getAbsolutePath());
                if (job != null) {
                    jobs.add(job);
                }
            }
            for (File zip : zipFiles) {
                WSMDMJob job = getJobInfo(zip.getAbsolutePath());
                if (job != null) {
                    jobs.add(job);
                }
            }
        } catch (Exception e) {
            Logger.getLogger(Util.class).error(e);
        }
        return jobs.toArray(new WSMDMJob[jobs.size()]);
    }

    /**
     * get the JobInfo:jobName and version & suffix
     */
    public static WSMDMJob getJobInfo(String fileName) {
        WSMDMJob jobInfo = null;
        try {
            ZipInputStream in = new ZipInputStream(new FileInputStream(fileName));
            ZipEntry z;
            try {
                String jobName = "";
                String jobVersion = "";
                // war
                if (fileName.endsWith(".war")) {
                    while ((z = in.getNextEntry()) != null) {
                        String dirName = z.getName();
                        // get job version
                        if (dirName.endsWith("undeploy.wsdd")) {
                            Pattern p = Pattern.compile(".*?_(\\d_\\d)/undeploy.wsdd");
                            Matcher m = p.matcher(dirName);
                            m.groupCount();
                            if (m.matches()) {
                                jobVersion = m.group(1);
                            }
                        }
                        // get job name
                        Pattern p = Pattern.compile(".*?/(.*?)\\.wsdl");
                        Matcher m = p.matcher(dirName);
                        if (m.matches()) {
                            jobName = m.group(1);
                        }
                    }
                    if (jobName.length() > 0) {
                        jobInfo = new WSMDMJob(null, null, null);
                        jobInfo.setJobName(jobName);
                        jobInfo.setJobVersion(jobVersion.replaceAll("_", "."));
                        jobInfo.setSuffix(".war");
                        return jobInfo;
                    } else {
                        return null;
                    }
                }// war
                if (fileName.endsWith(".zip")) {
                    if ((z = in.getNextEntry()) != null) {
                        String dirName = z.getName();
                        int pos = dirName.indexOf('/');
                        if (pos == -1) {
                            pos = dirName.indexOf(File.separator);
                        }
                        String dir = dirName.substring(0, pos);
                        pos = dir.lastIndexOf('_');
                        jobName = dir.substring(0, pos);
                        jobVersion = dir.substring(pos + 1);
                    }
                    if (jobName.length() > 0) {
                        jobInfo = new WSMDMJob(null, null, null);
                        jobInfo.setJobName(jobName);
                        jobInfo.setJobVersion(jobVersion);
                        jobInfo.setSuffix(".zip");
                        return jobInfo;
                    } else {
                        return null;
                    }
                }
            } catch (FileNotFoundException e) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("getJobInfo error.", e);
                }
            } finally {
                in.close();
            }
        } catch (Exception e) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("getJobInfo error.", e);
            }
        }
        return jobInfo;
    }

    public static String convertAutoIncrement(Properties p) {
        StringBuilder buffer = new StringBuilder();
        buffer.append("<AutoIncrement>");
        buffer.append("<id>AutoIncrement</id>");
        for (Entry entry : p.entrySet()) {
            buffer.append("<entry>");
            buffer.append("<key>").append(entry.getKey()).append("</key>");
            buffer.append("<value>").append(entry.getValue()).append("</value>");
            buffer.append("</entry>");
        }
        buffer.append("</AutoIncrement>");
        return buffer.toString();
    }

    public static Properties convertAutoIncrement(String xml) throws Exception {
        Properties p = new Properties();
        Node n = parse(xml).getDocumentElement();
        NodeList list = getNodeList(n, "entry");
        for (int i = 0; i < list.getLength(); i++) {
            Node item = list.item(i);
            if (item.getNodeType() == Node.ELEMENT_NODE) {
                String key = getFirstTextNode(item, "key");
                String value = getFirstTextNode(item, "value");
                p.setProperty(key, value);
            }
        }
        return p;
    }

    public static String stripeOuterBracket(String rowData) {
        ArrayList<String> result = new ArrayList<String>();
        int aggregate = 0;
        int cordon = 0;
        for (int i = 0; i < rowData.length(); i++) {
            char ch = rowData.charAt(i);
            if (ch == '[') {
                aggregate++;
                if (aggregate == 1) {
                    cordon = i;
                }
            } else if (ch == ']') {
                aggregate--;
                if (aggregate == 0) {
                    result.add(rowData.substring(cordon + 1, i));
                }
            } else if (aggregate == 0) {
                result.add(ch + "");
            }
        }

        return StringUtils.join(result.toArray(), ",");
    }

    /**
     * Escape any single quote characters that are included in the specified message string.
     * 
     * @param string The string to be escaped
     */
    protected static String escape(String string) {
        if ((string == null) || (string.indexOf('\'') < 0)) {
            return string;
        }
        int n = string.length();
        StringBuilder builder = new StringBuilder(n);
        for (int i = 0; i < n; i++) {
            char ch = string.charAt(i);
            if (ch == '\'') {
                builder.append('\'');
            }

            builder.append(ch);
        }
        return builder.toString();
    }

    public static String getMessage(String value, Object... args) {
        try {
            MessageFormat format = new MessageFormat(escape(value));

            value = format.format(args);
            return value;
        } catch (MissingResourceException e) {
            return "???" + value + "???";
        }
    }

    public static <T> T getException(Throwable throwable, Class<T> cls) {
        if (cls.isInstance(throwable)) {
            return (T) throwable;
        }
        if (throwable.getCause() != null) {
            return getException(throwable.getCause(), cls);
        }
        return null;
    }

    public static String getDefaultSystemLocale() {
        return MDMConfiguration.getConfiguration().getProperty("system.locale.default");//$NON-NLS-1$
    }
}
