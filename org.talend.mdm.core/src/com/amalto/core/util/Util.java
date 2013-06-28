// ============================================================================
//
// Copyright (C) 2006-2012 Talend Inc. - www.talend.com
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

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.net.URL;
import java.net.URLConnection;
import java.security.Principal;
import java.security.acl.Group;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.MissingResourceException;
import java.util.Properties;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.ejb.CreateException;
import javax.ejb.EJBException;
import javax.ejb.EJBLocalHome;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NameClassPair;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.resource.cci.Connection;
import javax.resource.cci.ConnectionFactory;
import javax.security.auth.Subject;
import javax.security.jacc.PolicyContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.jxpath.AbstractFactory;
import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.jxpath.Pointer;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.xerces.jaxp.DocumentBuilderFactoryImpl;
import org.talend.mdm.commmon.util.core.EUUIDCustomType;
import org.talend.mdm.commmon.util.core.ITransformerConstants;
import org.talend.mdm.commmon.util.core.MDMConfiguration;
import org.talend.mdm.commmon.util.datamodel.management.BusinessConcept;
import org.talend.mdm.commmon.util.datamodel.management.SchemaManager;
import org.talend.mdm.commmon.util.webapp.XObjectType;
import org.talend.mdm.commmon.util.webapp.XSystemObjects;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import sun.misc.BASE64Encoder;

import com.amalto.core.delegator.BeanDelegatorContainer;
import com.amalto.core.delegator.IXtentisWSDelegator;
import com.amalto.core.ejb.DroppedItemPOJO;
import com.amalto.core.ejb.DroppedItemPOJOPK;
import com.amalto.core.ejb.ItemPOJO;
import com.amalto.core.ejb.ItemPOJOPK;
import com.amalto.core.ejb.ObjectPOJO;
import com.amalto.core.ejb.UpdateReportPOJO;
import com.amalto.core.ejb.local.DroppedItemCtrlLocal;
import com.amalto.core.ejb.local.DroppedItemCtrlLocalHome;
import com.amalto.core.ejb.local.ItemCtrl2Local;
import com.amalto.core.ejb.local.ItemCtrl2LocalHome;
import com.amalto.core.ejb.local.ServiceLocalHome;
import com.amalto.core.ejb.local.TransformerCtrlLocal;
import com.amalto.core.ejb.local.TransformerCtrlLocalHome;
import com.amalto.core.ejb.local.XmlServerSLWrapperLocal;
import com.amalto.core.ejb.local.XmlServerSLWrapperLocalHome;
import com.amalto.core.jobox.JobContainer;
import com.amalto.core.metadata.ComplexTypeMetadata;
import com.amalto.core.metadata.FieldMetadata;
import com.amalto.core.metadata.MetadataRepository;
import com.amalto.core.objects.backgroundjob.ejb.local.BackgroundJobCtrlLocal;
import com.amalto.core.objects.backgroundjob.ejb.local.BackgroundJobCtrlLocalHome;
import com.amalto.core.objects.configurationinfo.ejb.local.ConfigurationInfoCtrlLocal;
import com.amalto.core.objects.configurationinfo.ejb.local.ConfigurationInfoCtrlLocalHome;
import com.amalto.core.objects.customform.ejb.local.CustomFormCtrlLocal;
import com.amalto.core.objects.customform.ejb.local.CustomFormCtrlLocalHome;
import com.amalto.core.objects.datacluster.ejb.DataClusterPOJOPK;
import com.amalto.core.objects.datacluster.ejb.local.DataClusterCtrlLocal;
import com.amalto.core.objects.datacluster.ejb.local.DataClusterCtrlLocalHome;
import com.amalto.core.objects.datamodel.ejb.DataModelPOJO;
import com.amalto.core.objects.datamodel.ejb.DataModelPOJOPK;
import com.amalto.core.objects.datamodel.ejb.local.DataModelCtrlLocal;
import com.amalto.core.objects.datamodel.ejb.local.DataModelCtrlLocalHome;
import com.amalto.core.objects.menu.ejb.local.MenuCtrlLocal;
import com.amalto.core.objects.menu.ejb.local.MenuCtrlLocalHome;
import com.amalto.core.objects.role.ejb.local.RoleCtrlLocal;
import com.amalto.core.objects.role.ejb.local.RoleCtrlLocalHome;
import com.amalto.core.objects.routing.v2.ejb.local.RoutingEngineV2CtrlLocal;
import com.amalto.core.objects.routing.v2.ejb.local.RoutingEngineV2CtrlLocalHome;
import com.amalto.core.objects.routing.v2.ejb.local.RoutingOrderV2CtrlLocal;
import com.amalto.core.objects.routing.v2.ejb.local.RoutingOrderV2CtrlLocalHome;
import com.amalto.core.objects.routing.v2.ejb.local.RoutingRuleCtrlLocal;
import com.amalto.core.objects.routing.v2.ejb.local.RoutingRuleCtrlLocalHome;
import com.amalto.core.objects.storedprocedure.ejb.local.StoredProcedureCtrlLocal;
import com.amalto.core.objects.storedprocedure.ejb.local.StoredProcedureCtrlLocalHome;
import com.amalto.core.objects.transformers.v2.ejb.TransformerV2POJOPK;
import com.amalto.core.objects.transformers.v2.ejb.local.TransformerV2CtrlLocal;
import com.amalto.core.objects.transformers.v2.ejb.local.TransformerV2CtrlLocalHome;
import com.amalto.core.objects.transformers.v2.util.TransformerCallBack;
import com.amalto.core.objects.transformers.v2.util.TransformerContext;
import com.amalto.core.objects.transformers.v2.util.TypedContent;
import com.amalto.core.objects.universe.ejb.UniversePOJO;
import com.amalto.core.objects.view.ejb.local.ViewCtrlLocal;
import com.amalto.core.objects.view.ejb.local.ViewCtrlLocalHome;
import com.amalto.core.schema.manage.SchemaCoreAgent;
import com.amalto.core.webservice.WSMDMJob;
import com.amalto.core.webservice.WSVersion;
import com.amalto.xmlserver.interfaces.IWhereItem;
import com.amalto.xmlserver.interfaces.WhereCondition;
import com.amalto.xmlserver.interfaces.WhereLogicOperator;
import com.amalto.xmlserver.interfaces.XmlServerException;
import com.sun.org.apache.xpath.internal.XPathAPI;
import com.sun.org.apache.xpath.internal.objects.XObject;
import com.sun.xml.xsom.XSComplexType;
import com.sun.xml.xsom.XSContentType;
import com.sun.xml.xsom.XSElementDecl;
import com.sun.xml.xsom.XSFacet;
import com.sun.xml.xsom.XSParticle;
import com.sun.xml.xsom.XSSchema;
import com.sun.xml.xsom.XSSchemaSet;
import com.sun.xml.xsom.XSSimpleType;
import com.sun.xml.xsom.XSType;
import com.sun.xml.xsom.impl.FacetImpl;
import com.sun.xml.xsom.parser.XSOMParser;
import com.sun.xml.xsom.util.DomAnnotationParserFactory;

/**
 * @author Bruno GRieder
 *
 */
@SuppressWarnings("deprecation")
public class Util {

    /*********************************************************************
     * System wide properties
     *********************************************************************/
    /**
     * Home Caches spped up execution but make deployment debugging
     */
    public final static boolean USE_HOME_CACHES = Boolean.getBoolean("com.amalto.use.home.caches"); //$NON-NLS-1$

    private static final Logger LOG = Logger.getLogger(Util.class);

    private static final TransformerFactory transformerFactory = TransformerFactory.newInstance();

    private static final String JAXP_SCHEMA_LANGUAGE = "http://java.sun.com/xml/jaxp/properties/schemaLanguage"; //$NON-NLS-1$

    private static final String W3C_XML_SCHEMA = "http://www.w3.org/2001/XMLSchema"; //$NON-NLS-1$

    private static final String JAXP_SCHEMA_SOURCE = "http://java.sun.com/xml/jaxp/properties/schemaSource"; //$NON-NLS-1$

    private static LRUCache<String, Document> xmlCache = new LRUCache<String, Document>(20);

    private static DocumentBuilderFactory nonValidatingDocumentBuilderFactory;

    static {
        System.setProperty("javax.xml.parsers.DocumentBuilderFactory", DocumentBuilderFactoryImpl.class.getName()); //$NON-NLS-1$
    }

    /**
     * helper class
     */
    public static List<String> getRuntimeServiceJndiList(boolean withPrefix) {
        List<String> serviceJndiList = new ArrayList<String>();
        String serviceJndiPrefix = "amalto/local/service"; //$NON-NLS-1$
        try {
            InitialContext ctx = new InitialContext();
            NamingEnumeration<NameClassPair> list = ctx.list(serviceJndiPrefix);
            while (list.hasMore()) {
                NameClassPair nc;

                nc = list.next();

                if (withPrefix) {
                    serviceJndiList.add(serviceJndiPrefix + "/" + nc.getName()); //$NON-NLS-1$
                } else {
                    serviceJndiList.add(nc.getName());
                }
            }
        } catch (NamingException e) {
            Logger.getLogger(Util.class).error(e);
        }
        return serviceJndiList;
    }

    public static List<String> getRuntimeServiceJndiList() {
        return getRuntimeServiceJndiList(true);
    }

    /*********************************************************************
     * Parsing Stuff
     *********************************************************************/

    public static Document parse(String xmlString) throws ParserConfigurationException, IOException, SAXException {
        Document doc = parse(xmlString, null);
        return doc;
    }

    public static Document parseXSD(String xsd) throws ParserConfigurationException, IOException, SAXException {
        Document doc = xmlCache.get(xsd);
        if (doc != null)
            return doc;
        doc = parse(xsd, null);
        xmlCache.put(xsd, doc);
        return doc;
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
            factory.setValidating((schema != null));
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

    /**
     * @author ymli fix the bug:0009642: remove the null element to match the shema
     * @param element
     */
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
            } else if (length > 1 && node != null) {
                if (setNullNode(node))
                    setNullNode(node.getParentNode());
            }
        }
        return removed;
    }

    public static Document defaultValidate(Element element, String schema) throws Exception {
        org.apache.log4j.Logger.getLogger(Util.class).trace("validate() " + element.getLocalName());
        Node cloneNode = element.cloneNode(true);

        // parse
        Document d = null;
        SAXErrorHandler seh = new SAXErrorHandler();

        // initialize the sax parser which uses Xerces
        System.setProperty("javax.xml.parsers.DocumentBuilderFactory", "org.apache.xerces.jaxp.DocumentBuilderFactoryImpl"); //$NON-NLS-1$ //$NON-NLS-2$
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        // Schema validation based on schemaURL
        factory.setNamespaceAware(true);
        factory.setValidating((schema != null));

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

        // add by ymli; fix the bug:0009642:remove the null element to match the shema
        setNullNode(cloneNode);

        // strip of attributes
        removeAllAttribute(cloneNode, "tmdm:type"); //$NON-NLS-1$

        String xmlstr = Util.nodeToString(cloneNode);
        // if element is null, remove it aiming added
        // see 7828

        xmlstr = xmlstr.replaceAll("<\\w+?/>", ""); //$NON-NLS-1$//$NON-NLS-2$
        // xmlstr=xmlstr.replaceAll("<\\w+?>\\s+?</\\w+?>", "");

        d = builder.parse(new InputSource(new StringReader(xmlstr)));

        // check if dcument parsed correctly against the schema
        if (schema != null) {
            // ignore cvc-complex-type.2.3 error
            String errors = seh.getErrors();

            boolean isComplex23 = errors.indexOf("cvc-complex-type.2.3") != -1 && errors.endsWith("is element-only."); //$NON-NLS-1$ //$NON-NLS-2$
            if (!errors.equals("") && !isComplex23) { //$NON-NLS-1$  
                String xmlString = Util.nodeToString(element);
                String err = "The item " + element.getLocalName() + " did not validate against the model: \n" + errors + "\n" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                        + xmlString; // .substring(0, Math.min(100, xmlString.length()));
                LOG.debug(err);
                throw new CVCException(err);
            }
        }

        return d;
    }

    public static Document validate(Element element, String schema) throws Exception {
        return BeanDelegatorContainer.getUniqueInstance().getValidationDelegator().validation(element, schema);
    }

    public static Map<String, String> getNamespaceFromImportXSD(Element element, boolean type) {
        HashMap<String, String> nsMap = new HashMap<String, String>();
        NodeList list = null;
        // for Import
        try {
            list = Util.getNodeList(element, "//xsd:import");
            for (int i = 0; i < list.getLength(); i++) {
                Node node = list.item(i);
                String ns = node.getAttributes().getNamedItem("namespace").getNodeValue();
                if (node.getAttributes().getNamedItem("schemaLocation") == null) {
                    continue;
                }
                String location = node.getAttributes().getNamedItem("schemaLocation").getNodeValue();
                if (location == null || location.equals("")) {
                    continue;
                }
                Document subDoc = parseImportedFile(location);
                if (type)
                    parseTypeFromImport(subDoc.getDocumentElement(), nsMap, ns);
                else
                    parseElementFromImport(subDoc.getDocumentElement(), nsMap, ns);
                nsMap.putAll(getNamespaceFromImportXSD(subDoc.getDocumentElement(), type));
            }
        } catch (Exception e) {
            Logger.getLogger(Util.class).error(e);
            return nsMap;
        }

        // for Include
        try {
            list = Util.getNodeList(element, "//xsd:include");
            for (int i = 0; i < list.getLength(); i++) {
                Node node = list.item(i);
                String ns = node.getAttributes().getNamedItem("schemaLocation").getNodeValue();
                if (ns == null) {
                    continue;
                }
                Document subDoc = parseImportedFile(ns);
                if (type)
                    parseTypeFromImport(subDoc.getDocumentElement(), nsMap, ns);
                else
                    parseElementFromImport(subDoc.getDocumentElement(), nsMap, ns);
                nsMap.putAll(getNamespaceFromImportXSD(subDoc.getDocumentElement(), type));
            }
        } catch (XtentisException e) {
            Logger.getLogger(Util.class).error(e);
            return nsMap;
        }

        return nsMap;
    }

    private static void parseTypeFromImport(Element elem, HashMap<String, String> map, String location) {
        NodeList list;
        try {
            for (int i = 0; i < 2; i++) {
                if (i == 0) {
                    list = Util.getNodeList(elem, "//xsd:complexType");
                } else {
                    list = Util.getNodeList(elem, "//xsd:simpleType");
                }

                for (int idx = 0; idx < list.getLength(); idx++) {
                    Node node = list.item(idx);
                    if (node.getAttributes().getNamedItem("name") == null)
                        continue;
                    String typeName = node.getAttributes().getNamedItem("name").getNodeValue();
                    map.put(typeName + " : " + location, location);
                }
            }
        } catch (XtentisException e) {
            Logger.getLogger(Util.class).error(e);
        }

    }

    private static void parseElementFromImport(Element elem, HashMap<String, String> map, String location) {
        NodeList list;
        try {
            list = Util.getNodeList(elem, "/xsd:schema/xsd:element");
            for (int idx = 0; idx < list.getLength(); idx++) {
                Node node = list.item(idx);
                if (node.getAttributes().getNamedItem("name") == null)
                    continue;
                String typeName = node.getAttributes().getNamedItem("name").getNodeValue();
                String typeCatg = null;
                if (node.getAttributes().getNamedItem("type") != null) {
                    typeCatg = node.getAttributes().getNamedItem("type").getNodeValue();
                }
                NodeList subList = null;
                if (typeCatg == null) {
                    subList = Util.getNodeList(node, "//xsd:complexType//xsd:element");
                } else {
                    subList = Util.getNodeList(node, "//xsd:complexType[@name='" + typeCatg + "']" + "//xsd:element");
                }
                String subNames = "";
                for (int i = 0; i < subList.getLength(); i++) {
                    subNames += subList.item(i).getAttributes().getNamedItem("name").getNodeValue() + " ";
                }
                if (!subNames.equals("")) {
                    map.put(typeName + " : " + location, subNames.trim());
                }
            }

        } catch (XtentisException e) {
            Logger.getLogger(Util.class).error(e);
        }

    }

    static LRUCache<String, Document> xsdCache = new LRUCache<String, Document>(30);

    private static Document parseImportedFile(String xsdLocation) {
        Document doc = xsdCache.get(xsdLocation);
        if (doc != null)
            return doc;

        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        documentBuilderFactory.setNamespaceAware(true);
        documentBuilderFactory.setValidating(false);

        Pattern httpUrl = Pattern.compile("(http|https|ftp):(\\//|\\\\)(.*):(.*)");
        Matcher match = httpUrl.matcher(xsdLocation);
        Document d = null;
        try {
            if (match.matches()) {
                List<String> authorizations = Util.getAuthorizationInfo();
                String xsd = Util.getResponseFromURL(xsdLocation, authorizations.get(0), authorizations.get(1));
                d = Util.parse(xsd);
            } else {
                DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
                d = documentBuilder.parse(new FileInputStream(xsdLocation));
            }
        } catch (Exception ex) {
            Logger.getLogger(Util.class).error(ex);
            return null;
        }
        xsdCache.put(xsdLocation, d);
        return d;
    }

    public static void removeXpathFromDocument(Document document, String xpath, boolean reservedRoot) throws Exception {
        Element root = document.getDocumentElement();
        NodeList toDeleteNodeList = Util.getNodeList(document, xpath);
        if (toDeleteNodeList != null) {
            Node lastParentNode = null;
            Node formatSiblingNode = null;
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
                        lastParentNode.removeChild(formatSiblingNode);
                    }
                }

            }
        }
    }

    public static String[] getTextNodes(Node contextNode, String xPath) throws TransformerException {
        return getTextNodes(contextNode, xPath, contextNode);
    }

    public static String[] getTextNodes(Node contextNode, String xPath, Node namespaceNode) throws TransformerException {
        String[] results = null;
        ;

        // test for hard-coded values
        if (xPath.startsWith("\"") && xPath.endsWith("\""))
            return new String[] { xPath.substring(1, xPath.length() - 1) };

        // test for incomplete path (elements missing /text())
        if (!xPath.matches(".*@[^/\\]]+")) // attribute
            if (!xPath.endsWith(")")) // function
                xPath += "/text()";

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
        } catch (TransformerException e) {
            String err = "Unable to get the text node(s) of " + xPath + ": " + e.getClass().getName() + ": "
                    + e.getLocalizedMessage();
            throw new TransformerException(err);
        }
        return results;

    }

    public static String getFirstTextNodeNotNull(Node contextNode, String xPath) throws TransformerException {
        String val = getFirstTextNode(contextNode, xPath, contextNode);
        return val == null ? "" : val;
    }

    public static String getFirstTextNode(Node contextNode, String xPath, Node namespaceNode) throws TransformerException {
        String[] res = getTextNodes(contextNode, xPath, namespaceNode);
        if (res.length == 0)
            return null;
        return res[0];
    }

    public static String getFirstTextNode(Node contextNode, String xPath) throws TransformerException {
        return getFirstTextNode(contextNode, xPath, contextNode);
    }

    public static String[] getAttributeNodeValue(Node contextNode, String xPath, Node namespaceNode) throws TransformerException {
        String[] results;

        // test for hard-coded values
        if (xPath.startsWith("\"") && xPath.endsWith("\""))
            return new String[] { xPath.substring(1, xPath.length() - 1) };

        // test for incomplete path
        // if (! xPath.endsWith(")")) xPath+="/text()";

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
        } catch (TransformerException e) {
            String err = "Unable to get the text node(s) of " + xPath + ": " + e.getClass().getName() + ": "
                    + e.getLocalizedMessage();
            throw new TransformerException(err);
        }
        return results;

    }

    static LRUCache<String, List<String>> xsdNodesCache = new LRUCache<String, List<String>>(20);

    public static List<String> getALLNodesFromSchema(String xsd) throws Exception {
        List<String> list = xsdNodesCache.get(xsd);
        if (list != null)
            return list;
        Node node = Util.parseXSD(xsd).getDocumentElement();
        list = getALLNodesFromSchema(node);
        xsdNodesCache.put(xsd, list);
        return list;
    }

    public static List<String> getALLNodesFromSchema(Node doc) throws XtentisException {
        List<String> list = new ArrayList<String>();
        String prefix = doc.getPrefix();
        NodeList l = Util.getNodeList(doc, "./" + prefix + ":element/@name");

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        factory.setValidating(false);
        DocumentBuilder builder;

        for (int i = 0; i < l.getLength(); i++) {
            Node n = l.item(i);
            list.add(n.getNodeValue());
        }

        try {
            for (int xsdType = 0; xsdType < 2; xsdType++) {
                try {
                    if (xsdType == 0)
                        l = Util.getNodeList(doc, "./xsd:import");
                    else
                        l = Util.getNodeList(doc, "./xsd:include");
                } catch (XtentisException e) {
                    continue;
                }
                for (int elemNum = 0; elemNum < l.getLength(); elemNum++) {
                    Node importNode = l.item(elemNum);
                    Node schemaLocaton = importNode.getAttributes().getNamedItem("schemaLocation");
                    if (schemaLocaton == null) {
                        continue;
                    }
                    String xsdLocation = schemaLocaton.getNodeValue();
                    Pattern httpUrl = Pattern.compile("(http|https|ftp):(\\//|\\\\)(.*):(.*)");
                    Matcher match = httpUrl.matcher(xsdLocation);
                    Document d = null;
                    if (match.matches()) {
                        List<String> authorizations = Util.getAuthorizationInfo();
                        String xsd = Util.getResponseFromURL(xsdLocation, authorizations.get(0), authorizations.get(1));
                        d = Util.parse(xsd);
                    } else {
                        builder = factory.newDocumentBuilder();
                        FileInputStream fio = new FileInputStream(xsdLocation);
                        try {
                            d = builder.parse(fio);
                        } finally {
                            fio.close();
                        }
                    }

                    list.addAll(getALLNodesFromSchema(d.getDocumentElement()));
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(Util.class).error(ex);
        }

        return list;
    }

    /**
     *
     * @param doc
     * @param type
     * @return
     * @throws XtentisException
     * @throws ParserConfigurationException
     * @throws IOException
     * @throws SAXException
     * @throws
     */
    public static Element getNameSpaceFromSchema(Node doc, String typeName) throws XtentisException {
        Pattern mask = Pattern.compile("(.*?):(.*?)");
        Matcher matcher = mask.matcher(typeName);
        String prefix = "";
        if (matcher.matches()) {
            prefix = matcher.group(1);
        } else
            prefix = null;

        Element ns = null;
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        // Schema validation based on schemaURL
        factory.setNamespaceAware(true);
        factory.setValidating(false);
        DocumentBuilder builder;
        try {
            builder = factory.newDocumentBuilder();
            NodeList list = Util.getNodeList(doc, ".//" + "xsd:import");
            for (int id = 0; id < list.getLength() && prefix != null; id++) {
                Node node = list.item(id);
                if (node.getAttributes().getNamedItem("schemaLocation") == null) {
                    continue;
                }
                String schemalocation = node.getAttributes().getNamedItem("schemaLocation").getNodeValue();
                String namespace = node.getAttributes().getNamedItem("namespace").getNodeValue();
                NodeList l = Util.getNodeList(doc, "//*[@type='" + typeName + "']", namespace, prefix);
                if (l.getLength() > 0)
                    return (Element) l.item(0);
                Document d = builder.parse(new FileInputStream(schemalocation));
                ns = getNameSpaceFromSchema(d, typeName);
                if (ns != null)
                    return ns;
            }
            if (list.getLength() == 0 || prefix == null) {
                list = Util.getNodeList(doc, ".//" + "xsd:include");
                for (int id = 0; id < list.getLength(); id++) {
                    Node node = list.item(id);
                    Document d;
                    String schemalocation = node.getAttributes().getNamedItem("schemaLocation").getNodeValue();
                    Pattern httpUrl = Pattern.compile("(http|https|ftp):(\\//|\\\\)(.*):(.*)");
                    Matcher match = httpUrl.matcher(schemalocation);
                    if (match.matches()) {
                        // to-fix : we need to provide a flexible way to find the user/pwd, rather than the hard code
                        // out here.
                        List<String> authorizations = Util.getAuthorizationInfo();
                        String xsd = Util.getResponseFromURL(schemalocation, authorizations.get(0), authorizations.get(1));
                        d = Util.parse(xsd);
                    } else {
                        d = builder.parse(new FileInputStream(schemalocation));
                    }

                    ns = getNameSpaceFromSchema(d, typeName);
                    if (ns != null)
                        return ns;
                }
                list = Util.getNodeList(doc, "//*[@name='" + typeName + "']");
                if (list.getLength() > 0)
                    return (Element) list.item(0);
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            Logger.getLogger(Util.class).error(e);
        }

        return ns;
    }

    public static String getResponseFromURL(String url, String user, String pwd) {
        BASE64Encoder encoder = new BASE64Encoder();
        StringBuffer buffer = new StringBuffer();
        String credentials = encoder.encode(new String(user + ":" + pwd).getBytes());

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
     * Check if a local or remote component for the JNDI Name exists
     *
     * @param RMIProviderURL
     * @param jndiName
     */
    public static boolean existsComponent(String RMIProviderURL, String jndiName) throws XtentisException {

        if ((RMIProviderURL == null) || (RMIProviderURL.equals("")))
            RMIProviderURL = "LOCAL";

        Hashtable<String, String> env = null;
        if (!"LOCAL".equals(RMIProviderURL)) {
            // FIXME: JBoss specific
            env = new Hashtable<String, String>(3);
            env.put(Context.INITIAL_CONTEXT_FACTORY, "org.jnp.interfaces.NamingContextFactory");
            env.put("java.naming.factory.url.pkgs", "org.jboss.naming:org.jnp.interfaces");
            env.put(Context.PROVIDER_URL, RMIProviderURL);
        }

        Object home = null;
        InitialContext initialContext = null;
        try {
            initialContext = new InitialContext(env);
            home = initialContext.lookup(jndiName);
        } catch (NamingException e) {
        } finally {
            try {
                initialContext.close();
            } catch (Exception e) {
            }
            ;
        }

        return (home != null);
    }

    /**
     * Retrieve the local (if RMIProvideURL is null) or remote component for the particular local JNDI Name (c) bgrieder
     * - lend to Amalto
     *
     * @param RMIProviderURL
     * @param jndiName
     */
    public static Object retrieveComponent(String RMIProviderURL, String jndiName) throws XtentisException {

        if ((RMIProviderURL == null) || (RMIProviderURL.equals("")))
            RMIProviderURL = "LOCAL";

        Hashtable<String, String> env = null;
        if (!"LOCAL".equals(RMIProviderURL)) {
            // FIXME: JBoss specific
            env = new Hashtable<String, String>(3);
            env.put(Context.INITIAL_CONTEXT_FACTORY, "org.jnp.interfaces.NamingContextFactory");
            env.put("java.naming.factory.url.pkgs", "org.jboss.naming:org.jnp.interfaces");
            env.put(Context.PROVIDER_URL, RMIProviderURL);

        }

        Object home = null;
        InitialContext initialContext = null;
        try {
            initialContext = new InitialContext(env);
            home = initialContext.lookup(jndiName);
        } catch (NamingException e) {
            String err = "Unable to lookup \"" + jndiName + "\"" + ": " + e.getClass().getName() + ": " + e.getLocalizedMessage();
            throw new XtentisException(err);
        } finally {
            try {
                initialContext.close();
            } catch (Exception e) {
            }
            ;
        }

        // find create and call it
        Method[] m = home.getClass().getMethods();
        Method create = null;
        for (int i = 0; i < m.length; i++) {
            if ("create".equals(m[i].getName())) {
                create = m[i];
                break;
            }
        }
        if (create == null) {
            String err = "Unable to find create method on home of component \"" + jndiName + "\"";
            throw new XtentisException(err);
        }
        Object component = null;
        try {
            component = create.invoke(home, (Object[]) null);
        } catch (Exception e) {
            String err = "Unable to call the create method of remote component \"" + jndiName + "\"" + ": "
                    + e.getClass().getName() + ": " + e.getLocalizedMessage();
            throw new XtentisException(err);
        }

        return component;

    }

    /**
     * Get the method of a component by its name (c) bgrieder - lend to Amalto
     *
     * @param component
     * @param methodName
     * @return the Method - niull if not found
     * @throws EJBException
     */
    public static Method getMethod(Object component, String methodName) throws EJBException {
        Method[] methods = component.getClass().getMethods();
        for (int i = 0; i < methods.length; i++) {
            if (methodName.equals(methods[i].getName())) {
                return methods[i];
            }
        }
        return null;
    }

    /**
     * Dumps info on a class
     *
     * @param clazz
     */
    public static void dumpClass(Class<?> clazz) {
        org.apache.log4j.Logger.getLogger(Util.class).debug("dumpClass() CLASS " + clazz.getName());
        Class<?>[] interfaces = clazz.getInterfaces();
        if (interfaces != null) {
            for (int i = 0; i < interfaces.length; i++) {
                org.apache.log4j.Logger.getLogger(Util.class).debug("()  Class Interface " + i + ": " + interfaces[i].getName());
            }
        }
        Class<?>[] classes = clazz.getClasses();
        if (classes != null) {
            for (int i = 0; i < classes.length; i++) {
                org.apache.log4j.Logger.getLogger(Util.class).debug("()  Classes " + i + ": " + classes[i].getName());
            }
        }
        org.apache.log4j.Logger.getLogger(Util.class).debug("()  Super Class " + clazz.getSuperclass().getName());
        org.apache.log4j.Logger.getLogger(Util.class).debug("()  Generic Super Class " + clazz.getGenericSuperclass());
        Type[] types = clazz.getGenericInterfaces();
        if (types != null) {
            for (int i = 0; i < types.length; i++) {
                org.apache.log4j.Logger.getLogger(Util.class).debug("()  Generic Interface" + i + ": " + types[i]);
            }
        }
        Method[] methods = clazz.getMethods();
        for (int i = 0; i < types.length; i++) {
            Logger.getLogger(Util.class).debug("Types " + types[i]);
        }
        for (int i = 0; i < methods.length; i++) {
            org.apache.log4j.Logger.getLogger(Util.class).debug("Method " + methods[i].getName());
        }
    }

    /**
     * XSLT Utils
     *
     * @param xslt
     * @return teh Top Business Concept
     */
    public static String getTopBusinessConceptName(Document xslt) throws TransformerException {
        String prefix = xslt.getDocumentElement().getPrefix();
        // String uri = xslt.getDocumentElement().getNamespaceURI();
        String xpath = prefix + ":template[@match='/']/" + prefix + ":apply-templates/@mode";
        return Util.getFirstTextNode(xslt.getDocumentElement(), xpath);
        /*
         * String s = getAttributeNodeValue( xslt.getDocumentElement(),
         * "xs:template[@match='/']/xs:apply-templates/@mode",
         * getRootElement("nsholder","http://www.w3.org/1999/XSL/Transform","xs") )[0]; return s;
         */
    }

    public static String getTopBusinessConceptPath(Document xslt) throws TransformerException {
        String prefix = xslt.getDocumentElement().getPrefix();
        // String uri = xslt.getDocumentElement().getNamespaceURI();
        String xpath = prefix + ":template[@match='/']/" + prefix + ":apply-templates/@select";
        return Util.getFirstTextNode(xslt.getDocumentElement(), xpath);
        /*
         * String s = getAttributeNodeValue( xslt.getDocumentElement(),
         * "xs:template[@match='/']/xs:apply-templates/@select",
         * getRootElement("nsholder","http://www.w3.org/1999/XSL/Transform","xs") )[0]; return s;
         */
    }

    public static Document setTopBusinessConceptPath(Document xslt) throws XtentisException {
        try {
            String prefix = xslt.getDocumentElement().getPrefix();
            // String uri = xslt.getDocumentElement().getNamespaceURI();
            String xpath = prefix + ":template[@match='/']/" + prefix + ":apply-templates";
            NodeList l = Util.getNodeList(xslt.getDocumentElement(), xpath);
            if (l == null)
                throw new XtentisException("The root template match (match='/') of the transform cannot be found");
            int len = l.getLength();
            if (len != 1)
                throw new XtentisException("The root template match (match='/') of the transform is not univoque");
            ((Element) l.item(0)).setAttribute("select", ".");
            return xslt;
        } catch (Exception e) {
            String err = "Unable to process the transform" + ": " + e.getClass().getName() + ": " + e.getLocalizedMessage();
            org.apache.log4j.Logger.getLogger(Util.class).error(err, e);
            throw new XtentisException(err);
        }

    }

    private static final LRUCache<String, XSDKey> xsdkeyCache = new LRUCache<String, XSDKey>(40);
    
    public static XSDKey getBusinessConceptKey(Document xsd, String businessConceptName) throws TransformerException {
        try {
            String schema = nodeToString(xsd);
            MetadataRepository repository = new MetadataRepository();
            repository.load(new ByteArrayInputStream(schema.getBytes("UTF-8"))); //$NON-NLS-1$
            ComplexTypeMetadata type = repository.getComplexType(businessConceptName);
            List<FieldMetadata> keyFields = type.getKeyFields();
            String[] fields = new String[keyFields.size()];
            String[] fieldTypes = new String[keyFields.size()];
            int i = 0;
            for (FieldMetadata keyField : keyFields) {
                fields[i] = keyField.getName();
                fieldTypes[i] = keyField.getType().getName();
                i++;
            }
            XSDKey key = xsdkeyCache.get(schema + "#" + businessConceptName); //$NON-NLS-1$
            if (key != null) {
                return key;
            }
            key = new XSDKey(".", fields, fieldTypes); //$NON-NLS-1$
            xsdkeyCache.put(schema + "#" + businessConceptName, key); //$NON-NLS-1$
            return key;
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
            for (Iterator<Principal> iter = set.iterator(); iter.hasNext();) {
                Principal principal = iter.next();
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
            }// for
            authorizations.add(user);
            authorizations.add(pwd);
        } catch (XtentisException e) {
            Logger.getLogger(Util.class).error(e);
            return null;
        }

        return authorizations;
    }

    public static boolean isUUIDType(String type) {
        return EUUIDCustomType.allTypes().contains(type);

    }

    public static Node processUUID(Element root, String schema, String dataCluster, String concept) throws Exception {
        return Util.generateUUIDForElement(schema, dataCluster, concept, root, false, false);
    }

    public static Node processUUID(boolean saveUnUsedAutoIncrement, Element root, String schema, String dataCluster,
            String concept) throws Exception {
        return Util.generateUUIDForElement(schema, dataCluster, concept, root, false, saveUnUsedAutoIncrement);
    }

    public static Node processUUID(Element root, String schema, String dataCluster, String concept, boolean pseudoAutoIncrement)
            throws Exception {

        return Util.generateUUIDForElement(schema, dataCluster, concept, root, pseudoAutoIncrement, false);

    }

    public static List<UUIDPath> getUUIDNodes(String schema, String concept) throws Exception {

        HashSet<String> set = new HashSet<String>();
        _multipleOccuranceNodeSetThreadLocal.set(set);

        List<UUIDPath> list = new ArrayList<UUIDPath>();
        Map<String, XSElementDecl> map = getConceptMap(schema);
        if (map.get(concept) != null) {
            XSComplexType xsct = (XSComplexType) (map.get(concept).getType());
            XSParticle[] xsp = xsct.getContentType().asParticle().getTerm().asModelGroup().getChildren();
            for (int i = 0; i < xsp.length; i++) {
                getChildren("/" + concept, xsp[i], list);
            }
        }
        return list;
    }

    private static ThreadLocal<HashSet<String>> _multipleOccuranceNodeSetThreadLocal = new ThreadLocal<HashSet<String>>();

    private static void getChildren(String parentPath, XSParticle xsp, List<UUIDPath> list) {

        if (xsp.getTerm().asModelGroup() != null) { // is complex type
            XSParticle[] xsps = xsp.getTerm().asModelGroup().getChildren();
            // String path = parentPath + "/" + xsp.getTerm().asElementDecl().getName();
            for (int i = 0; i < xsps.length; i++) {
                getChildren(parentPath, xsps[i], list);
            }
        }

        if (xsp.getTerm().asElementDecl() == null)
            return;

        if (xsp.getMaxOccurs() == -1) {
            String particleName = xsp.getTerm().asElementDecl().getName();
            if (!_multipleOccuranceNodeSetThreadLocal.get().contains(particleName))
                _multipleOccuranceNodeSetThreadLocal.get().add(parentPath + "/" + particleName);
        }

        if (xsp.getTerm().asElementDecl().getType().isComplexType() == false) {
            String type = xsp.getTerm().asElementDecl().getType().getName();
            if (EUUIDCustomType.AUTO_INCREMENT.getName().equalsIgnoreCase(type)
                    || EUUIDCustomType.UUID.getName().equalsIgnoreCase(type)) {
                String path = parentPath + "/" + xsp.getTerm().asElementDecl().getName();
                UUIDPath uuid = new UUIDPath();
                uuid.setXpath(path);
                uuid.setType(type);
                list.add(uuid);
            }
        }
        if (xsp.getTerm().asElementDecl().getType().isComplexType() == true) {
            XSParticle particle = xsp.getTerm().asElementDecl().getType().asComplexType().getContentType().asParticle();
            if (particle != null) {
                XSParticle[] xsps = particle.getTerm().asModelGroup().getChildren();
                String path = parentPath + "/" + xsp.getTerm().asElementDecl().getName();
                for (int i = 0; i < xsps.length; i++) {
                    getChildren(path, xsps[i], list);
                }
            }
        }
    }

    // conceptmap Cache
    static LRUCache<String, Map<String, XSElementDecl>> conceptMapCache = new LRUCache<String, Map<String, XSElementDecl>>(10);

    public static Map<String, XSElementDecl> getConceptMap(String xsd) throws Exception {
        if (conceptMapCache.get(xsd) != null)
            return conceptMapCache.get(xsd);
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
        Map<String, XSElementDecl> map = null;
        for (Iterator iter = xssList.iterator(); iter.hasNext();) {
            XSSchema schema = (XSSchema) iter.next();
            map = schema.getElementDecls();
            mapForAll.putAll(map);
        }
        conceptMapCache.put(xsd, mapForAll);
        return mapForAll;
    }

    static LRUCache<String, Map<String, XSType>> cTypeMapCache = new LRUCache<String, Map<String, XSType>>(10);

    public static Map<String, XSType> getConceptTypeMap(String xsd) throws Exception {
        if (cTypeMapCache.get(xsd) != null)
            return cTypeMapCache.get(xsd);
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
        Map<String, XSType> map = null;
        for (Iterator iter = xssList.iterator(); iter.hasNext();) {
            XSSchema schema = (XSSchema) iter.next();
            map = schema.getTypes();

            mapForAll.putAll(map);
        }
        cTypeMapCache.put(xsd, mapForAll);
        return mapForAll;
    }

    /**
     * update the node according to the schema
     *
     * @param concept
     * @param xsd
     * @param updateNode
     * @return
     * @throws Exception
     */
    public static Node updateNodeBySchema(String concept, String xsd, Node updateNode) {
        try {
            Element oldNode = createItem(concept, xsd);

            // see 0011615: Complex type sequences are truncated when saving a record in both web app & studio
            Map<String, UpdateReportItem> updatedPath = compareElement("/" + concept, updateNode, oldNode);
            return updateElement(oldNode, updatedPath);
        } catch (Exception e) {
            return updateNode;
        }
    }

    /**
     * check concept's datamodel contains UUID or Auto_increment type fields and it's empty
     *
     * @param concept
     * @param xsd
     * @return
     * @throws Exception
     */
    public static boolean containsUUIDType(String concept, String xsd, Element root) {
        try {
            Map<String, XSElementDecl> map = getConceptMap(xsd);
            XSComplexType xsct = (XSComplexType) (map.get(concept).getType());
            XSParticle[] xsp = xsct.getContentType().asParticle().getTerm().asModelGroup().getChildren();
            for (int j = 0; j < xsp.length; j++) {
                if (containsUUIDType(xsp[j], "/" + concept, root.getOwnerDocument())) {
                    return true;
                }
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }

    private static boolean containsUUIDType(XSParticle xsp, String xpathParent, Document d) {
        try {
            if (xsp.getTerm().asModelGroup() != null) { // is complex type
                XSParticle[] xsps = xsp.getTerm().asModelGroup().getChildren();
                for (int i = 0; i < xsps.length; i++) {
                    containsUUIDType(xsps[i], xpathParent, d);
                }
            }
            if (xsp.getTerm().asElementDecl() == null)
                return false;
            String type = xsp.getTerm().asElementDecl().getType().getName();
            String xpath = xpathParent + "/" + xsp.getTerm().asElementDecl().getName();
            if ((EUUIDCustomType.UUID.getName().equals(type) || EUUIDCustomType.AUTO_INCREMENT.getName().equals(type))
                    && getNodeList(d, xpath).getLength() == 0)
                return true;
            if (xsp.getTerm().asElementDecl().getType().isComplexType() == true) {
                XSParticle particle = xsp.getTerm().asElementDecl().getType().asComplexType().getContentType().asParticle();
                if (particle != null) {
                    XSParticle[] xsps = particle.getTerm().asModelGroup().getChildren();
                    String xpathParent1 = xpathParent + "/" + xsp.getTerm().asElementDecl().getName();
                    for (int i = 0; i < xsps.length; i++) {
                        containsUUIDType(xsps[i], xpathParent1, d);
                    }
                }
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }

    public static boolean isOnlyUpdateKey(Node oldNode, String concept, XSDKey xsdKey, String[] keyvalues) {
        try {
            String xml1 = "<" + concept + "></" + concept + ">";
            Node node = Util.parse(xml1).getDocumentElement();
            JXPathContext jxpContext = JXPathContext.newContext(node);
            jxpContext.setLenient(true);
            jxpContext.setFactory(factory);
            for (int i = 0; i < xsdKey.getFields().length && i < keyvalues.length; i++) {
                String xpath = xsdKey.getFields()[i];
                jxpContext.createPathAndSetValue(xpath, keyvalues[i]);
            }
            node = (Node) jxpContext.getContextBean();
            String xmlstring = getXMLStringFromNode(oldNode);
            xmlstring = xmlstring.replaceAll(">\\s+<", "><");
            String keystring = getXMLStringFromNode(node);
            keystring = keystring.replaceAll(">\\s+<", "><");
            return xmlstring.equals(keystring);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * create an "empty" item from scratch, set every text node to empty
     *
     * @param concept
     * @param xsd
     * @return
     * @throws Exception
     */
    public static Element createItem(String concept, String xsd) throws Exception {

        String xml1 = "<" + concept + "></" + concept + ">";
        Document d = parse(xml1, null);
        Map<String, XSElementDecl> map = getConceptMap(xsd);
        XSComplexType xsct = (XSComplexType) (map.get(concept).getType());
        XSParticle[] xsp = xsct.getContentType().asParticle().getTerm().asModelGroup().getChildren();
        for (int j = 0; j < xsp.length; j++) {
            // why don't set up children element? FIXME
            if (!checkHidden(xsp[j])) {
                setChilden(xsp[j], "/" + concept, d);
            }
        }

        return d.getDocumentElement();
    }

    /**
     * check is hidden for specify element.
     *
     * @param xsp
     * @return
     */
    public static boolean checkHidden(XSParticle xsp) {
        boolean hidden = false;
        if (xsp.getTerm().getAnnotation() == null)
            return hidden;
        Element annotations = (Element) xsp.getTerm().getAnnotation().getAnnotation();
        NodeList annotList = annotations.getChildNodes();

        for (int k = 0; k < annotList.getLength(); k++) {
            if ("appinfo".equals(annotList.item(k).getLocalName())) {
                Node source = annotList.item(k).getAttributes().getNamedItem("source");
                if (source == null)
                    continue;
                String appinfoSource = source.getNodeValue();

                if (annotList.item(k) != null && annotList.item(k).getFirstChild() != null) {
                    if (appinfoSource.equals(BusinessConcept.APPINFO_X_HIDE)) {
                        return true;
                    }
                }
            }
        }

        return hidden;
    }

    /**
     * the return type maybe all/sequence/choice
     *
     * @param concept
     * @param xsd
     * @return
     * @throws Exception
     */
    public static String getConceptModelType(String concept, String xsd) {
        try {
            Map<String, XSElementDecl> map = getConceptMap(xsd);
            XSComplexType xsct = (XSComplexType) (map.get(concept).getType());
            return xsct.getContentType().asParticle().getTerm().asModelGroup().getCompositor().toString();
        } catch (Exception e) {
            return "all";
        }
    }

    private static void setChilden(XSParticle xsp, String xpathParent, Document d) throws Exception {
        // aiming added see 0009563
        if (xsp.getTerm().asModelGroup() != null) { // is complex type
            XSParticle[] xsps = xsp.getTerm().asModelGroup().getChildren();
            for (int i = 0; i < xsps.length; i++) {
                setChilden(xsps[i], xpathParent, d);
            }
        }
        if (xsp.getTerm().asElementDecl() == null)
            return;
        // end

        Element el = d.createElement(xsp.getTerm().asElementDecl().getName());
        Node node = Util.getNodeList(d, xpathParent).item(0);
        node.appendChild(el);
        if (xsp.getTerm().asElementDecl().getType().isComplexType() == true) {
            XSParticle particle = xsp.getTerm().asElementDecl().getType().asComplexType().getContentType().asParticle();
            if (particle != null) {
                XSParticle[] xsps = particle.getTerm().asModelGroup().getChildren();
                xpathParent = xpathParent + "/" + xsp.getTerm().asElementDecl().getName();
                for (int i = 0; i < xsps.length; i++) {
                    setChilden(xsps[i], xpathParent, d);
                }
            }
        }
    }

    public static String[] getTargetSystemsFromSchema(Document schema, String concept) throws Exception {
        String[] targetSystems = null;

        Element rootNS = Util.getRootElement("nsholder", schema.getDocumentElement().getNamespaceURI(), "xsd");
        String xpath = "//xsd:element[@name='" + concept + "']//xsd:appinfo[@source='X_TargetSystem']";
        NodeList tsList = Util.getNodeList(schema.getDocumentElement(), xpath, rootNS.getNamespaceURI(), "xsd");

        if (tsList != null) {
            targetSystems = new String[tsList.getLength()];
            for (int i = 0; i < tsList.getLength(); i++) {
                Node tsNode = tsList.item(i);
                targetSystems[i] = tsNode.getTextContent();
            }
        }

        return targetSystems;
    }

    /**
     *
     * @param schema
     * @param dataCluster
     * @param concept
     * @param elementname null:
     * @param conceptRoot
     * @throws Exception
     */
    private static Node generateUUIDForElement(String schema, String dataCluster, String concept, Element conceptRoot,
            boolean pseudoAutoIncrement, boolean saveUnUsedAutoIncrement) throws Exception {
        List<UUIDPath> uuidLists = getUUIDNodes(schema, concept);
        JXPathContext jxpContext = JXPathContext.newContext(conceptRoot);
        jxpContext.setLenient(true);

        jxpContext.setFactory(factory);
        for (int i = 0; i < uuidLists.size(); i++) {
            UUIDPath uuid = uuidLists.get(i);
            String xpath = uuid.getXpath();
            String xpathCpy = xpath;
            String type = uuid.getType();
            xpath = xpath.replaceFirst("/" + concept + "/", "");
            String value = "";
            if (EUUIDCustomType.AUTO_INCREMENT.getName().equalsIgnoreCase(type)) {
                if (pseudoAutoIncrement) {
                    Random rand = new Random();
                    value = rand.nextInt(10000) + "";
                } else {
                    String universe = LocalUser.getLocalUser().getUniverse().getName();
                    Object o = jxpContext.getValue(xpath);
                    if (o == null || o.toString().trim().length() == 0) {
                        long id;
                        if (saveUnUsedAutoIncrement)
                            id = AutoIncrementGenerator.generateNum(conceptRoot.hashCode(), universe, dataCluster,
                                concept + "." + xpath.replaceAll("/", "."));
                        else {
                            id = AutoIncrementGenerator.generateNum(universe, dataCluster,
                                    concept + "." + xpath.replaceAll("/", "."));
                            AutoIncrementGenerator.saveToDB();
                        }
                        value = String.valueOf(id);
                    } else
                        value = o.toString();
                }
                jxpContext.createPathAndSetValue(xpath, value);
            }
            if (EUUIDCustomType.UUID.getName().equalsIgnoreCase(type)) {
                ArrayList<String> xpathes = new ArrayList<String>();
                for (String path : _multipleOccuranceNodeSetThreadLocal.get()) {
                    if (xpathCpy.indexOf(path) >= 0) {
                        String pathPrefix = path.replaceFirst("/" + concept, "");
                        String pathTail = xpathCpy.substring(path.length());
                        Double cnt = (Double) jxpContext.getValue("count(" + pathPrefix + ")");
                        for (double db = 1; db <= cnt; db++) {
                            String valueToMerge = pathPrefix + "[" + (int) db + "]" + pathTail;
                            mergeValueIntoArrayList(xpathes, valueToMerge);
                        }
                    }
                }
                if (xpathes.isEmpty()) {
                    xpathes.add(xpath);
                }
                for (String newPath : xpathes) {
                    Object o = jxpContext.getValue(newPath);
                    if (o == null || o.toString().trim().length() == 0)
                        value = UUID.randomUUID().toString();
                    else
                        value = o.toString();
                    if (o != null)
                        jxpContext.createPathAndSetValue(newPath, value);
                }
            }

        }
        return (Node) jxpContext.getContextBean();
    }

    private static void mergeValueIntoArrayList(ArrayList<String> list, String value) {
        if (list.isEmpty()) {
            list.add(value);
            return;
        }
        if (list.contains(value))
            return;

        String mergeValue = "";

        for (int i = 0; i < list.size(); i++) {
            int merge = -1;
            int del = -1;
            String[] existItems = list.get(i).split("/");
            String[] newItems = value.split("/");
            if (existItems.length != newItems.length) {
                continue;
            }
            if (list.get(i).equals(value))
                continue;
            Pattern cdatabracket = Pattern.compile("([^\\[]*)(\\[.*\\])");
            merge = i;
            for (int item = 0; item < existItems.length; item++) {
                Matcher matchNewItem = cdatabracket.matcher(newItems[item]);
                Matcher matchExistItem = cdatabracket.matcher(existItems[item]);
                if (matchNewItem.matches() && !matchExistItem.matches() && matchNewItem.group(1).equals(existItems[item])) {
                    if (newItems[item].length() > 0) {
                        mergeValue += "/";
                    }
                    mergeValue += newItems[item];
                    del = i;
                    merge = -1;
                } else if (!matchNewItem.matches() && matchExistItem.matches() && matchExistItem.group(1).equals(newItems[item])) {
                    if (existItems[item].length() > 0) {
                        mergeValue += "/";
                    }
                    mergeValue += existItems[item];
                } else if (matchNewItem.matches() && matchExistItem.matches()) {
                    mergeValue += "/";
                    mergeValue += newItems[item];
                    if (!existItems[item].equals(newItems[item]))
                        merge = -1;
                } else {
                    if (existItems[item].length() > 0) {
                        mergeValue += "/";
                    }
                    mergeValue += existItems[item];
                }
            }

            if (merge == -1) {
                if (del != -1) {
                    list.add(del, mergeValue);
                    list.remove(del + 1);
                } else if (!list.contains(mergeValue)) {
                    list.add(mergeValue);
                }

            } else {
                list.add(merge, mergeValue);
                list.remove(merge + 1);
            }
            mergeValue = "";
        }
    }

    public static String getXMLStringFromNode(Node d) throws TransformerException {
        return nodeToString(d);
    }

    /**
     * @author achen
     * @param xsd
     * @param businessConceptName
     * @param keyName
     * @return
     * @throws TransformerException
     */
    public static String getBusinessConceptKeyType(Document xsd, String businessConceptName, String keyName)
            throws TransformerException {
        try {
            String type;
            type = Util.getTextNodes(xsd.getDocumentElement(), "xsd:element[@name='" + businessConceptName
                    + "']/xsd:complexType//xsd:element[@name='" + keyName + "']/@type",
                    getRootElement("nsholder", xsd.getDocumentElement().getNamespaceURI(), "xsd"))[0];

            return type;
        } catch (TransformerException e) {
            String err = "Unable to get the keys for the Business Concept " + businessConceptName + ": "
                    + e.getLocalizedMessage();
            throw new TransformerException(err, e);
        }
    }

    /**
     *
     * @param item
     * @param key
     * @return the key ids
     * @throws XtentisException
     */
    public static String[] getKeyValuesFromItem(Element item, XSDKey key) throws TransformerException {
        return getItemKeyValues(item, key);
    }

    /**
     * Return the Item Primary key values
     *
     * @param item
     * @param xsdKey
     * @return the Item Primary Key values
     * @throws TransformerException
     * @see #getBusinessConceptKey(Document, String)
     */
    public static String[] getItemKeyValues(Element item, XSDKey xsdKey) throws TransformerException {
        try {
            if (xsdKey != null) {
                String[] vals = new String[xsdKey.getFields().length];
                for (int i = 0; i < xsdKey.getFields().length; i++) {
                    String xpath = xsdKey.getFields()[i];
                    // Fix for TMDM-2449 Fails to extract id if xpath does not start with '/'
                    xpath = xpath.replaceFirst("/*" + item.getLocalName() + "/", "");
                    vals[i] = Util.getFirstTextNode(item, xsdKey.getSelector() + "/" + xpath);
                    if (vals[i] != null)
                        vals[i] = vals[i].trim(); // FIXME: Due to eXist trimming values @see ItemPOJO
                }
                return vals;
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
     * @param item
     * @param dataModel
     * @return the Item PK
     */
    public static ItemPOJOPK getItemPOJOPK(DataClusterPOJOPK dataClusterPOJOPK, Element item, Document dataModel)
            throws TransformerException {
        String conceptName = item.getLocalName();
        String[] itemKeyValues = null;
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

    /*
     * public static Document getExtractorXSLT(Document original) throws XtentisException{
     * 
     * String prefix = original.getDocumentElement().getPrefix(); String topXpath =
     * Util.getTopBusinessConceptPath(original);
     * 
     * if (topXpath == null) { throw new
     * XtentisException("Unable to build Extractor XSLT: the Concept cannot be found"); }
     * 
     * String rootAttrs = ""; NamedNodeMap attsMap = original.getDocumentElement().getAttributes(); int len =
     * attsMap.getLength(); for (int i =0; i<len; i++) { Attr att = (Attr)attsMap.item(i); rootAttrs
     * +=" "+att.getName()+"='"+att.getValue()+"'"; }
     * 
     * 
     * topXpath = topXpath.replace('"','\''); String xsl = "<?xml version='1.0' encoding='UTF-8'?>"+
     * "<xs:stylesheet "+rootAttrs+">"+
     * "    <xs:output method='xml' indent='yes' omit-xml-declaration='yes' standalone='yes'/>"+
     * "    <xs:template mode='extract' match='*'><xs:copy-of select='.'/></xs:template>"+
     * "   <xs:template match='/'><xs:apply-templates mode='extract' select=\""+topXpath+"\"/></xs:template>"+
     * "</xs:stylesheet>";
     * 
     * xsl = xsl.replaceAll("xs:",prefix+":");
     * 
     * return Util.parse(xsl);
     * 
     * }
     */

    /**
     * Returns a namespaced root element of a document Useful to create a namespace holder element
     *
     * @param namespace
     * @return the root Element
     */
    public static Element getRootElement(String elementName, String namespace, String prefix) throws TransformerException {
        Element rootNS = null;
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
        return nodeToString(n, omitXMLDeclaration, LOG.isDebugEnabled());
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
        String s = sw.toString().replaceAll("\r\n", "\n");
        return s;
    }

    /**
     * DOC HSHU Comment method "removeAllAttributes".
     *
     * @param node
     */
    public static void removeAllAttribute(Node node, String attrName) {

        if (node.getNodeType() == Node.ELEMENT_NODE) {
            NamedNodeMap attrs = node.getAttributes();
            for (int i = 0; i < attrs.getLength(); i++) {
                if (attrs.item(i).getNodeName().equals(attrName)) {
                    attrs.removeNamedItem(attrName);
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
     * Get a nodelist from an xPath
     *
     * @throws XtentisException
     */
    public static NodeList getNodeList(Document d, String xPath) throws XtentisException {
        return getNodeList(d.getDocumentElement(), xPath, null, null);
    }

    /**
     * Get a nodelist from an xPath
     *
     * @throws XtentisException
     */
    public static NodeList getNodeList(Node contextNode, String xPath) throws XtentisException {
        return getNodeList(contextNode, xPath, null, null);
    }

    /**
     * Get a nodelist from an xPath
     *
     * @throws XtentisException
     */
    public static NodeList getNodeList(Node contextNode, String xPath, String namespace, String prefix) throws XtentisException {
        try {
            XObject xo = XPathAPI.eval(contextNode, xPath,
                    (namespace == null) ? contextNode : Util.getRootElement("nsholder", namespace, prefix));
            if (xo.getType() != XObject.CLASS_NODESET)
                return null;
            return xo.nodelist();
        } catch (TransformerException e) {
            String err = "Unable to get the Nodes List for xpath '" + xPath + "'"
                    + ((contextNode == null) ? "" : " for Node " + contextNode.getLocalName()) + ": " + e.getLocalizedMessage();
            throw new XtentisException(err);
        }
    }

    /**
     * Join an array of strings into a single string using a separator
     *
     * @param strings
     * @param separator
     * @return a single string or null
     */
    public static String joinStrings(String[] strings, String separator) {
        if (strings == null)
            return null;
        String res = "";
        for (int i = 0; i < strings.length; i++) {
            res += (i > 0) ? separator : "";
            res += (strings[i] == null ? "" : strings[i]);
        }
        return res;
    }

    private static Pattern conceptFromPathPattern = Pattern.compile("^/*(.*?)[\\[|/].*");

    /**
     * Returns the first part - eg. the concept - from the path
     *
     * @param path
     * @return The concept extracted from the path
     */
    public static String getConceptFromPath(String path) {
        if (!path.endsWith("/"))
            path += "/";
        Matcher m = conceptFromPathPattern.matcher(path);
        if (m.matches()) {
            return m.group(1);
        } else {
            return null;
        }
    }

    /**
     * Returns the list of items that look like parts numbers
     *
     * @param value The value to match
     * @return The list
     */
    public static Collection<String> getAllPartNumbers(String value) {
        ArrayList<String> l = new ArrayList<String>();
        l.add(value);
        Pattern p = Pattern.compile("([0-9]+[\\*]?)[\\p{Punct}]"); // -_'#~`\\\\\\/
        Matcher m = p.matcher(value);
        try {
            String s = m.replaceAll("$1 ");
            if (!s.equals(value)) {
                l.add(s.trim());
                l.add(s.trim().replaceAll(" ", ""));
            }
        } catch (Exception e) {
        }
        return l;
    }

    /*********************************************************************
     * PROFILING
     *********************************************************************/

    private static ArrayList<long[]> timeMarkers = new ArrayList<long[]>(); // [totalTops,lastTime,totalPeriod]

    /**
     * Mark and record the time
     *
     * @param marker
     * @return the period of time
     * @throws Exception
     */
    public static long topTime(int marker) throws Exception {
        long time = System.currentTimeMillis();
        if (timeMarkers.size() <= marker) {
            for (int i = timeMarkers.size(); i <= marker; i++) {
                if (i == 0)
                    timeMarkers.add(new long[] { 0L, System.currentTimeMillis(), 0L });
                else
                    timeMarkers.add(new long[] { 0L, 0L, 0L });
            }
        }
        long period = 0;
        if (marker == 0)
            period = time - (timeMarkers.get(marker))[1];
        else
            period = time - (timeMarkers.get(marker - 1))[1];
        long totalPeriod = (timeMarkers.get(marker))[2] + period;
        long totalTops = (timeMarkers.get(marker))[0] + 1;
        timeMarkers.set(marker, new long[] { totalTops, time, totalPeriod });
        return period;
    }

    public static void resetTimeMarkers() {
        timeMarkers = new ArrayList<long[]>();
    }

    public static ArrayList<long[]> getTimeMarkers() {
        return timeMarkers;
    }

    /**
     * Dumps the time markers to the console
     *
     * @param msg
     */
    public static void dumpTimeMarkers(String msg) {
        Logger.getLogger(Util.class).debug("TIME MARKERS: " + msg);
        int i = 0;
        long totalProcessing = 0;
        for (Iterator<long[]> iter = timeMarkers.iterator(); iter.hasNext();) {
            long[] values = iter.next();
            if (i > 0)
                totalProcessing += values[2];
            Logger.getLogger(Util.class).debug(
                    "Marker " + (i++) + ":" + "tops: " + values[0] + " -- " + "totalPeriods: " + values[2] + " -- " + "average: "
                            + (values[2] / values[0]));
        }
        if (i > 0)
            Logger.getLogger(Util.class).debug(
                    "Total Processing: " + totalProcessing + " -- average: " + totalProcessing / (timeMarkers.get(0))[0]);
    }

    /*********************************************************************
     * SUBJECT - AUTHENTICATION
     *********************************************************************/

    public static Subject getActiveSubject() throws XtentisException {
        // Get active Subject
        try {
            String SUBJECT_CONTEXT_KEY = "javax.security.auth.Subject.container";
            return (Subject) PolicyContext.getContext(SUBJECT_CONTEXT_KEY);
        } catch (Exception e) {
            throw new XtentisException(e.getMessage());
        }

        /*
         * InitialContext ictx = new InitialContext(); JaasSecurityManager jsm = (JaasSecurityManager)
         * ictx.lookup("java:jaas/xtentisSecurity"); return jsm.getActiveSubject();
         */
    }

    /**
     * Extracts the username of the logged user from the {@link Subject}
     *
     * @param subject
     * @return The username
     * @throws XtentisException
     */
    public static String getUsernameFromSubject(Subject subject) throws XtentisException {
        Set<Principal> set = subject.getPrincipals();
        for (Iterator<Principal> iter = set.iterator(); iter.hasNext();) {
            Principal principal = iter.next();
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
                if (set != null)
                    for (Iterator<Principal> iter = set.iterator(); iter.hasNext();) {
                        Principal principal = iter.next();
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
                    }// for
            }
            if (userName == null)
                userName = "";
            if (password == null)
                password = "";
            token = userName + "/" + password;
        } catch (XtentisException e) {
            Logger.getLogger(Util.class).error(e);
        }
        return token;
    }

    /**
     * Extract the role names of the logged user from the Subject
     *
     * @param subject
     * @return The collection of roles
     * @throws XtentisException
     */
    public static Collection<String> getRoleNamesFromSubject(Subject subject) throws XtentisException {
        ArrayList<String> roleNames = new ArrayList<String>();
        Set<Principal> set = subject.getPrincipals();
        for (Iterator<Principal> iter = set.iterator(); iter.hasNext();) {
            Principal principal = iter.next();
            if (principal instanceof Group) {
                Group group = (Group) principal;
                // @see XtentisMDMLoginModule
                if ("Roles".equals(group.getName())) {
                    Enumeration<? extends Principal> principals = group.members();
                    while (principals.hasMoreElements()) {
                        roleNames.add(((Principal) principals.nextElement()).getName());
                    }
                }
            }
        }
        return roleNames;
    }

    /*********************************************************************
     * DATES
     *********************************************************************/

    public static String getTimestamp() {
        return getTimestamp(new Date());
    }

    public static String getTimestamp(Date d) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd'T'HH:mm:ss.SSS' 'z");
        return sdf.format(d);
    }

    /*********************************************************************
     * XML STUFF
     *********************************************************************/
    private static Pattern regexLT = Pattern.compile("&lt;");

    private static Pattern regexGT = Pattern.compile("&gt;");

    private static Pattern regexAMP = Pattern.compile("&amp;");

    public static String xmlDecode(String string) {
        if (string == null)
            return null;
        string = regexLT.matcher(string).replaceAll("<");
        string = regexGT.matcher(string).replaceAll(">");
        string = regexAMP.matcher(string).replaceAll("&");
        return string;
    }

    private static Pattern regexSup = Pattern.compile(">");

    private static Pattern regexInf = Pattern.compile("<");

    private static Pattern regexEt = Pattern.compile("&");

    public static String xmlEncode(String string) {
        string = regexSup.matcher(string).replaceAll("&gt;");
        string = regexInf.matcher(string).replaceAll("&lt;");
        string = regexEt.matcher(string).replaceAll("&amp;");
        return string;
    }

    /*********************************************************************
     *
     * LOCAL HOME GETTERS This cache system requires a JBoss restart on every core deployment
     *
     *********************************************************************/

    // The only Static HashMap around (hopefully)
    private static HashMap<String, EJBLocalHome> localHomes = new HashMap<String, javax.ejb.EJBLocalHome>();

    public static void flushLocalHomes() throws NamingException {
        localHomes = new HashMap<String, javax.ejb.EJBLocalHome>();
    }

    public static EJBLocalHome getLocalHome(String jndi) throws NamingException {
        EJBLocalHome localHome = null;
        if (true) {
            localHome = localHomes.get(jndi);
            if (localHome == null) {
                localHome = (EJBLocalHome) new InitialContext().lookup(jndi);
                localHomes.put(jndi, localHome);
            }
        } else {
            localHome = (EJBLocalHome) new InitialContext().lookup(jndi);
        }
        // dumpClass(localHome.getClass());
        return localHome;
    }

    public static RoleCtrlLocalHome getRoleCtrlLocalHome() throws NamingException {
        return (RoleCtrlLocalHome) getLocalHome(RoleCtrlLocalHome.JNDI_NAME);
    }

    public static RoleCtrlLocal getRoleCtrlLocal() throws NamingException, CreateException {
        return getRoleCtrlLocalHome().create();
    }

    public static CustomFormCtrlLocalHome getCustomFormCtrlLocalHome() throws NamingException {
        return (CustomFormCtrlLocalHome) getLocalHome(CustomFormCtrlLocalHome.JNDI_NAME);
    }

    public static CustomFormCtrlLocal getCustomFormCtrlLocal() throws NamingException, CreateException {
        return getCustomFormCtrlLocalHome().create();
    }
    public static RoutingOrderV2CtrlLocal getRoutingOrderV2CtrlLocal() throws NamingException, CreateException {
        return getRoutingOrderV2CtrlLocalHome().create();
    }

    public static RoutingRuleCtrlLocalHome getRoutingRuleCtrlLocalHome() throws NamingException {
        return (RoutingRuleCtrlLocalHome) getLocalHome(RoutingRuleCtrlLocalHome.JNDI_NAME);
    }

    public static RoutingRuleCtrlLocal getRoutingRuleCtrlLocal() throws NamingException, CreateException {
        return getRoutingRuleCtrlLocalHome().create();
    }

    public static RoutingOrderV2CtrlLocalHome getRoutingOrderV2CtrlLocalHome() throws NamingException {
        return (RoutingOrderV2CtrlLocalHome) getLocalHome(RoutingOrderV2CtrlLocalHome.JNDI_NAME);
    }

    public static StoredProcedureCtrlLocalHome getStoredProcedureCtrlLocalHome() throws NamingException {
        return (StoredProcedureCtrlLocalHome) getLocalHome(StoredProcedureCtrlLocalHome.JNDI_NAME);
    }

    public static StoredProcedureCtrlLocal getStoredProcedureCtrlLocal() throws NamingException, CreateException {
        return getStoredProcedureCtrlLocalHome().create();
    }

    public static ServiceLocalHome getServiceLocalHome() throws NamingException {
        return (ServiceLocalHome) getLocalHome(ServiceLocalHome.JNDI_NAME);
    }

    public static ItemCtrl2LocalHome getItemCtrl2LocalHome() throws NamingException {
        return (ItemCtrl2LocalHome) getLocalHome(ItemCtrl2LocalHome.JNDI_NAME);
    }

    public static ItemCtrl2Local getItemCtrl2Local() throws NamingException, CreateException {
        return getItemCtrl2LocalHome().create();
    }

    public static DroppedItemCtrlLocalHome getDroppedItemCtrlLocalHome() throws NamingException {
        return (DroppedItemCtrlLocalHome) getLocalHome(DroppedItemCtrlLocalHome.JNDI_NAME);
    }

    public static DroppedItemCtrlLocal getDroppedItemCtrlLocal() throws NamingException, CreateException {
        return getDroppedItemCtrlLocalHome().create();
    }

    public static DataModelCtrlLocalHome getDataModelCtrlLocalHome() throws NamingException {
        return (DataModelCtrlLocalHome) getLocalHome(DataModelCtrlLocalHome.JNDI_NAME);
    }

    public static DataModelCtrlLocal getDataModelCtrlLocal() throws NamingException, CreateException {
        return getDataModelCtrlLocalHome().create();
    }

    public static XmlServerSLWrapperLocal getXmlServerCtrlLocal() throws XtentisException {
        XmlServerSLWrapperLocal server = null;
        try {
            server = ((XmlServerSLWrapperLocalHome) getLocalHome(XmlServerSLWrapperLocalHome.JNDI_NAME)).create();
        } catch (Exception e) {
            String err = "Error : unable to access the XML Server wrapper";
            org.apache.log4j.Logger.getLogger(ObjectPOJO.class).error(err, e);
            throw new XtentisException(err, e);
        }
        return server;
    }

    public static DataClusterCtrlLocalHome getDataClusterCtrlLocalHome() throws NamingException {
        return (DataClusterCtrlLocalHome) getLocalHome(DataClusterCtrlLocalHome.JNDI_NAME);
    }

    public static DataClusterCtrlLocal getDataClusterCtrlLocal() throws NamingException, CreateException {
        return getDataClusterCtrlLocalHome().create();
    }

    public static ViewCtrlLocalHome getViewCtrlLocalHome() throws NamingException {
        return (ViewCtrlLocalHome) getLocalHome(ViewCtrlLocalHome.JNDI_NAME);
    }

    public static ViewCtrlLocal getViewCtrlLocal() throws NamingException, CreateException {
        return getViewCtrlLocalHome().create();
    }

    @Deprecated
    public static TransformerCtrlLocalHome getTransformerCtrlLocalHome() throws NamingException {
        return (TransformerCtrlLocalHome) getLocalHome(TransformerCtrlLocalHome.JNDI_NAME);
    }

    @Deprecated
    public static TransformerCtrlLocal getTransformerCtrlLocal() throws NamingException, CreateException {
        return getTransformerCtrlLocalHome().create();
    }

    public static TransformerV2CtrlLocalHome getTransformerV2CtrlLocalHome() throws NamingException {
        return (TransformerV2CtrlLocalHome) getLocalHome(TransformerV2CtrlLocalHome.JNDI_NAME);
    }

    public static TransformerV2CtrlLocal getTransformerV2CtrlLocal() throws NamingException, CreateException {
        return getTransformerV2CtrlLocalHome().create();
    }

    public static MenuCtrlLocalHome getMenuCtrlLocalHome() throws NamingException {
        return (MenuCtrlLocalHome) getLocalHome(MenuCtrlLocalHome.JNDI_NAME);
    }

    public static MenuCtrlLocal getMenuCtrlLocal() throws NamingException, CreateException {
        return getMenuCtrlLocalHome().create();
    }

    public static BackgroundJobCtrlLocalHome getBackgroundJobCtrlLocalHome() throws NamingException {
        return (BackgroundJobCtrlLocalHome) getLocalHome(BackgroundJobCtrlLocalHome.JNDI_NAME);
    }

    public static BackgroundJobCtrlLocal getBackgroundJobCtrlLocal() throws NamingException, CreateException {
        return getBackgroundJobCtrlLocalHome().create();
    }

    public static ConfigurationInfoCtrlLocalHome getConfigurationInfoCtrlLocalHome() throws NamingException {
        return (ConfigurationInfoCtrlLocalHome) getLocalHome(ConfigurationInfoCtrlLocalHome.JNDI_NAME);
    }

    public static ConfigurationInfoCtrlLocal getConfigurationInfoCtrlLocal() throws NamingException, CreateException {
        return getConfigurationInfoCtrlLocalHome().create();
    }

    public static RoutingEngineV2CtrlLocalHome getRoutingEngineV2CtrlLocalHome() throws NamingException {
        return (RoutingEngineV2CtrlLocalHome) getLocalHome(RoutingEngineV2CtrlLocalHome.JNDI_NAME);
    }

    public static RoutingEngineV2CtrlLocal getRoutingEngineV2CtrlLocal() throws NamingException, CreateException {
        return getRoutingEngineV2CtrlLocalHome().create();
    }

    // private static ConnectionFactory cachedConnectionFactory = null;
    public static Connection getConnection(String JNDIName) throws XtentisException {
        try {
            // if (cachedConnectionFactory == null) - Removed - no more caching of connections
            // cachedConnectionFactory = (ConnectionFactory)(new InitialContext()).lookup(JNDIName);
            // return cachedConnectionFactory.getConnection();
            return ((ConnectionFactory) (new InitialContext()).lookup(JNDIName)).getConnection();
        } catch (Exception e) {
            String err = "JNDI lookup error: " + e.getClass().getName() + ": " + e.getLocalizedMessage();
            org.apache.log4j.Logger.getLogger(Util.class).error(err);
            throw new XtentisException(err);
        }
    }

    // @Deprecated
    // public static TransformerCtrlLocalHome getTransformerCtrlLocalHome() throws NamingException {
    // return (TransformerCtrlLocalHome) getLocalHome(TransformerCtrlLocalHome.JNDI_NAME);
    // }
    // @Deprecated
    // public static TransformerCtrlLocal getTransformerCtrlLocal() throws NamingException,CreateException {
    // return getTransformerCtrlLocalHome().create();
    // }

    /***********************************************************************************************************
     * Typed Content Manipulation
     ***********************************************************************************************************/

    private static Pattern extractCharsetPattern = Pattern.compile(".*charset\\s*=[\"|']?(.+)[\"|']([\\s|;].*)?");

    /**
     * Extract the charset of a content type<br>
     * e.g 'utf-8' in 'text/xml; charset="utf-8"'
     *
     * @param contentType
     * @return the charset
     */
    public static String extractCharset(String contentType) {
        String charset = "UTF8";
        Matcher m = extractCharsetPattern.matcher(contentType);
        if (m.matches()) {
            charset = m.group(1).trim().toUpperCase();
        }
        // if ("UTF-8".equals(charset)) charset = "UTF8";
        return charset;
    }

    /**
     * Extract the MIME type and sub type of a content type<br>
     * e.g 'text/xml' in 'text/xml; charset="utf-8"'
     *
     * @param contentType
     * @return the MIME Type and SubType
     */
    public static String extractTypeAndSubType(String contentType) {
        if (contentType == null)
            return null;
        return contentType.split(";")[0].trim().toLowerCase();
    }

    /**
     * Extracts a byte array from an InputStream
     *
     * @param is
     * @return the byte array
     * @throws IOException
     */
    public static byte[] getBytesFromStream(InputStream is) throws IOException {
        if (is == null)
            return null;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        int b;
        while ((b = is.read()) != -1)
            bos.write(b);
        return bos.toByteArray();
    }

    /*********************************************************************
     *
     * GUID Generator
     *
     *********************************************************************/

    /** Cached per JVM server IP. */
    private static String hexServerIP = null;

    // initialise the secure random instance
    private static final java.security.SecureRandom seeder = new java.security.SecureRandom();

    /**
     * A 32 byte GUID generator (Globally Unique ID). These artificial keys SHOULD <strong>NOT </strong> be seen by the
     * user, not even touched by the DBA but with very rare exceptions, just manipulated by the database and the
     * programs.
     *
     * Usage: Add an id field (type java.lang.String) to your EJB, and add setId(XXXUtil.generateGUID(this)); to the
     * ejbCreate method.
     */
    public static final String generateGUID(Object o) {
        StringBuffer tmpBuffer = new StringBuffer(16);
        if (hexServerIP == null) {
            java.net.InetAddress localInetAddress = null;
            try {
                // get the inet address

                localInetAddress = java.net.InetAddress.getLocalHost();
            } catch (java.net.UnknownHostException uhe) {
                Logger.getLogger(Util.class).error(
                        "JobUtil: Could not get the local IP address using InetAddress.getLocalHost()!", uhe);
                // todo: find better way to get around this...
                return null;
            }
            byte serverIP[] = localInetAddress.getAddress();
            hexServerIP = hexFormat(getInt(serverIP), 8);
        }

        String hashcode = hexFormat(System.identityHashCode(o), 8);
        tmpBuffer.append(hexServerIP);
        tmpBuffer.append(hashcode);

        long timeNow = System.currentTimeMillis();
        int timeLow = (int) timeNow & 0xFFFFFFFF;
        int node = seeder.nextInt();

        StringBuffer guid = new StringBuffer(32);
        guid.append(hexFormat(timeLow, 8));
        guid.append(tmpBuffer.toString());
        guid.append(hexFormat(node, 8));
        return guid.toString();
    }

    private static int getInt(byte bytes[]) {
        int i = 0;
        int j = 24;
        for (int k = 0; j >= 0; k++) {
            int l = bytes[k] & 0xff;
            i += l << j;
            j -= 8;
        }
        return i;
    }

    private static String hexFormat(int i, int j) {
        String s = Integer.toHexString(i);
        return padHex(s, j) + s;
    }

    private static String padHex(String s, int i) {
        StringBuffer tmpBuffer = new StringBuffer();
        if (s.length() < i) {
            for (int j = 0; j < i - s.length(); j++) {
                tmpBuffer.append('0');
            }
        }
        return tmpBuffer.toString();
    }

    public static ItemPOJO getItem(String datacluster, String xml) throws Exception {
        String projection = xml;
        Element root = Util.parse(projection).getDocumentElement();

        String concept = root.getLocalName();

        DataModelPOJO dataModel = Util.getDataModelCtrlLocal().getDataModel(new DataModelPOJOPK(datacluster));
        Document schema = Util.parse(dataModel.getSchema());
        XSDKey conceptKey = com.amalto.core.util.Util.getBusinessConceptKey(schema, concept);

        // get key values
        String[] ids = com.amalto.core.util.Util.getKeyValuesFromItem(root, conceptKey);
        DataClusterPOJOPK dcpk = new DataClusterPOJOPK(datacluster);
        ItemPOJOPK itemPOJOPK = new ItemPOJOPK(dcpk, concept, ids);
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
                TransformerV2CtrlLocal ctrl = getTransformerV2CtrlLocal();
                TypedContent wsTypedContent = new TypedContent(exchangeData.getBytes("UTF-8"), "text/xml; charset=utf-8"); //$NON-NLS-1$
                ctrl.execute(context, wsTypedContent, new TransformerCallBack() {

                    public void contentIsReady(TransformerContext context) throws XtentisException {
                        org.apache.log4j.Logger.getLogger(this.getClass()).debug(
                                "XtentisWSBean.executeTransformerV2.contentIsReady() "); //$NON-NLS-1$
                    }

                    public void done(TransformerContext context) throws XtentisException {
                        org.apache.log4j.Logger.getLogger(this.getClass()).debug("XtentisWSBean.executeTransformerV2.done() "); //$NON-NLS-1$
                        context.put(RUNNING, Boolean.FALSE);
                    }
                });
                while ((Boolean) context.get(RUNNING)) {
                    Thread.sleep(100);
                }
                // TODO process no plug-in issue
                String message = "<report><message type=\"error\"/></report> "; //$NON-NLS-1$;
                String item = null;
                // Scan the entries - in priority, taka the content of the 'output_error_message' entry,
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
                if (!hasOutputReport){
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

    /**
     * Executes a BeforeDeleting process if any
     *
     *
     *
     *
     *
     * @param clusterName A data cluster name
     * @param concept A concept/type name
     * @param ids Id of the document being deleted
     * @throws Exception If something went wrong
     */
    @SuppressWarnings("unchecked")
    public static String beforeDeleting(String clusterName, String concept, String[] ids) throws Exception {
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
                ItemPOJOPK itempk = new ItemPOJOPK(new DataClusterPOJOPK(clusterName), concept, ids);
                ItemPOJO pojo= ItemPOJO.load(itempk);
                String xml=null;
                if(pojo==null){//load from recyclebin
                	DroppedItemPOJOPK dpitempk=new DroppedItemPOJOPK(null,itempk,"/");//$NON-NLS-1$ 
                	DroppedItemPOJO dpPojo=Util.getDroppedItemCtrlLocal().loadDroppedItem(dpitempk);
                	if(dpPojo!=null){
                		xml=dpPojo.getProjection();             		
                		Document doc = Util.parse(xml);
    	                Node item = XPathAPI.selectSingleNode(doc, "//ii/p"); //$NON-NLS-1$ 
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
                }else{
                	xml=pojo.getProjectionAsString();
                }
                String resultUpdateReport = Util.createUpdateReport(ids, concept, UpdateReportPOJO.OPERATION_TYPE_PHYSICAL_DELETE, null,
                        "", clusterName); //$NON-NLS-1$
                String exchangeData = mergeExchangeData(xml, resultUpdateReport);
                final String RUNNING = "XtentisWSBean.executeTransformerV2.beforeDeleting.running";
                TransformerContext context = new TransformerContext(new TransformerV2POJOPK("beforeDeleting_" + concept));
                context.put(RUNNING, Boolean.TRUE);
                TransformerV2CtrlLocal ctrl = getTransformerV2CtrlLocal();
                TypedContent wsTypedContent = new TypedContent(exchangeData.getBytes("UTF-8"),
                        "text/xml; charset=utf-8");

                ctrl.execute(context, wsTypedContent, new TransformerCallBack() {

                    public void contentIsReady(TransformerContext context) throws XtentisException {
                        org.apache.log4j.Logger.getLogger(this.getClass()).debug(
                                "XtentisWSBean.executeTransformerV2.beforeDeleting.contentIsReady() ");
                    }

                    public void done(TransformerContext context) throws XtentisException {
                        org.apache.log4j.Logger.getLogger(this.getClass()).debug(
                                "XtentisWSBean.executeTransformerV2.beforeDeleting.done() ");
                        context.put(RUNNING, Boolean.FALSE);
                    }
                });

                while (((Boolean) context.get(RUNNING)).booleanValue()) {
                    Thread.sleep(100);
                }
                // TODO process no plug-in issue
                String outputErrorMessage = null;
                // Scan the entries - in priority, taka the content of the
                // 'output_error_message' entry,
                for (Entry<String, TypedContent> entry : context.getPipelineClone().entrySet()) {

                    if (ITransformerConstants.VARIABLE_OUTPUT_OF_BEFORESAVINGTRANFORMER.equals(entry.getKey())) {
                        outputErrorMessage = new String(entry.getValue().getContentBytes(), "UTF-8");
                        break;
                    }
                }
                // handle error message
                if (outputErrorMessage != null && outputErrorMessage.length() > 0) {
                    return outputErrorMessage;
                } else {
                    return "<report><message type=\"error\"/></report> "; //$NON-NLS-1$
                }
            } catch (Exception e) {
                Logger.getLogger(Util.class).error(e);
                throw e;
            }
        }
        // TODO Scan the entries - in priority, taka the content of the specific
        // entry
        return null;
    }

    public static String buildItemPKString(String clusterName, String conceptName, String[] ids) {

        StringBuffer itemPKXmlString = new StringBuffer();

        if (clusterName == null || clusterName.length() == 0)
            return itemPKXmlString.toString();
        if (conceptName == null || conceptName.length() == 0)
            return itemPKXmlString.toString();
        if (ids == null)
            return itemPKXmlString.toString();

        itemPKXmlString.append("<item-pOJOPK><concept-name>").append(conceptName).append("</concept-name><ids>")
                .append(joinStrings(ids, ".")).append("</ids><data-cluster-pOJOPK><ids>").append(clusterName)
                .append("</ids></data-cluster-pOJOPK></item-pOJOPK>");

        return itemPKXmlString.toString();
    }

    public static Map<String, UpdateReportItem> compareElement(String parentPath, Node newElement, Node oldElement) throws Exception {
        HashMap<String, UpdateReportItem> map = new LinkedHashMap<String, UpdateReportItem>();
        Set<String> complexNodes = new LinkedHashSet<String>();
        Set<String> xpaths = getXpaths(parentPath, newElement, complexNodes);
        JXPathContext jxpContextOld = JXPathContext.newContext(oldElement);
        jxpContextOld.setLenient(true);
        JXPathContext jxpContextNew = JXPathContext.newContext(newElement);
        jxpContextNew.setLenient(true);
        String concept = newElement.getLocalName();

        for (String cnodePath : complexNodes) {
            if (cnodePath.startsWith("/" + concept + "/")) {
                cnodePath = cnodePath.replaceFirst("/" + concept + "/", "");
                checkDiffsWhenComparingElement(map, jxpContextOld, jxpContextNew, cnodePath, true);
            }
        }

        for (String xpath : xpaths) {
            NodeList listnew = getNodeList(newElement, xpath);
            NodeList listold = getNodeList(oldElement, xpath);
            int num = Math.max(listnew.getLength(), listold.getLength());
            if (xpath.startsWith("/" + concept + "/")) {
                xpath = xpath.replaceFirst("/" + concept + "/", "");
            }
            if (num > 1) {// list
                for (int i = 1; i <= num; i++) {
                    String xpath1 = xpath + "[" + i + "]";
                    if (i > 1) {
                        String fixPath = getFixedListXpath(xpath, newElement, oldElement, i);
                        if (fixPath != null)
                            xpath1 = fixPath;
                    }
                    checkDiffsWhenComparingElement(map, jxpContextOld, jxpContextNew, xpath1, false);
                }
            } else {
                checkDiffsWhenComparingElement(map, jxpContextOld, jxpContextNew, xpath, false);
            }
        }

        return map;
    }

    /**
     * DOC HSHU Comment method "checkDiffsWhenComparingElement".
     *
     * @param map
     * @param jxpContextOld
     * @param jxpContextNew
     * @param xpath
     */
    private static void checkDiffsWhenComparingElement(HashMap<String, UpdateReportItem> map, JXPathContext jxpContextOld,
            JXPathContext jxpContextNew, String xpath, boolean attributeOnly) {

        String oldvalue = null;
        String newvalue = null;

        // content text
        if (!attributeOnly) {
            oldvalue = (String) jxpContextOld.getValue(xpath, String.class);
            newvalue = (String) jxpContextNew.getValue(xpath, String.class);
            if (newvalue != null && newvalue.length() > 0 && !newvalue.equals(oldvalue) || oldvalue != null
                    && oldvalue.length() > 0 && !oldvalue.equals(newvalue)) {
                UpdateReportItem item = new UpdateReportItem(xpath, oldvalue, newvalue);
                map.put(xpath, item);
            }
        }

        // attributes
        String attrXpath1 = xpath + "/@xsi:type";
        oldvalue = (String) jxpContextOld.getValue(attrXpath1, String.class);
        newvalue = (String) jxpContextNew.getValue(attrXpath1, String.class);
        if (newvalue != null && newvalue.length() > 0 && !newvalue.equals(oldvalue) || oldvalue != null && oldvalue.length() > 0
                && !oldvalue.equals(newvalue)) {
            UpdateReportItem item = new UpdateReportItem(attrXpath1, oldvalue, newvalue);
            map.put(attrXpath1, item);
        }

        String attrXpath2 = xpath + "/@tmdm:type";
        oldvalue = (String) jxpContextOld.getValue(attrXpath2, String.class);
        newvalue = (String) jxpContextNew.getValue(attrXpath2, String.class);
        if (newvalue != null && newvalue.length() > 0 && !newvalue.equals(oldvalue) || oldvalue != null && oldvalue.length() > 0
                && !oldvalue.equals(newvalue)) {
            UpdateReportItem item = new UpdateReportItem(attrXpath2, oldvalue, newvalue);
            map.put(attrXpath2, item);
        }

    }

    /**
     * this method only used in a list like a/b/c/d/e, e.g index=2, the right xpath maybe one of
     * a/b/c/d[2]/e,a/b/c[2]/d/e,a/b/c[2]/d/e,a/b[2]/c/e,a[2]/b/c/d/e that is null in oldElement
     *
     * @param xpath
     * @param jxpContextOld
     * @param jxpContextNew
     * @param index
     * @return
     */
    private static String getFixedListXpath(String xpath, Node newElement, Node oldElement, int index) throws Exception {
        if (xpath.endsWith("/"))
            xpath = xpath.substring(0, xpath.length() - 1);
        int pos = xpath.lastIndexOf('/');
        if (pos > 0) {
            String parentPath = xpath.substring(0, pos);
            String lastPath = xpath.substring(pos + 1);
            NodeList listnew = getNodeList(newElement, parentPath);
            NodeList listold = getNodeList(oldElement, parentPath);
            int num = Math.max(listnew.getLength(), listold.getLength());
            if (num > 1) {// list
                String[] paths = parentPath.split("/");
                for (int i = 0; i < paths.length; i++) {
                    String str = paths[i];
                    if (str.matches("\\w+\\[\\d\\]")) {
                        continue;
                    }
                    StringBuffer sb = new StringBuffer();
                    for (int j = 0; j < paths.length; j++) {
                        if (i == j) {
                            sb.append(paths[j] + "[" + index + "]/");
                        } else {
                            sb.append(paths[j] + "/");
                        }
                    }
                    sb.append(lastPath);
                    JXPathContext jxpContextOld = JXPathContext.newContext(oldElement);
                    jxpContextOld.setLenient(true);
                    if (jxpContextOld.getValue(sb.toString()) != null) {
                        return sb.toString();
                    }
                }

            }
        }
        return null;
    }

    private static Set<String> getXpaths(String parentPath, Node node, Set<String> complexNodes) throws Exception {
        Set<String> set = new LinkedHashSet<String>();
        NodeList list = node.getChildNodes();
        for (int i = 0; i < list.getLength(); i++) {
            Node n = list.item(i);

            if (n.getNodeType() == Node.ELEMENT_NODE) {
                String nName = n.getNodeName();
                String xPath = parentPath + "/" + nName;
                NodeList list1 = getNodeList(node, xPath);
                int j = 1;
                for (int ii = 0; ii < list1.getLength(); ii++) {
                    Node node1 = list1.item(ii);
                    if (node1.getNodeType() == Node.ELEMENT_NODE) {
                        if (getElementNum(list1) > 1) {
                            j++;
                        }
                    }
                }
                if (!hasChildren(n)) {
                    set.add(xPath);
                } else {
                    // if list
                    if (j > 1) {
                        for (int ii = 1; ii <= j; ii++) {
                            complexNodes.add(xPath + "[" + ii + "]");
                            set.addAll(getXpaths(xPath + "[" + ii + "]", n, complexNodes));
                        }
                    } else {
                        complexNodes.add(xPath);
                        set.addAll(getXpaths(xPath, n, complexNodes));
                    }
                }
            }
        }
        return set;
    }

    static AbstractFactory factory = new AbstractFactory() {

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
            } else
                return false;
        }

        public boolean declareVariable(JXPathContext context, String name) {
            return false;
        }
    };

    /**
     * update the element according to updatedpath
     *
     * @param old
     * @param updatedpath
     * @return
     * @throws Exception
     */
    public static Node updateElement(Node old, Map<String, UpdateReportItem> updatedpath) throws Exception {
        if (updatedpath.size() == 0)
            return old;
        // use JXPathContext to update the old element
        JXPathContext jxpContext = JXPathContext.newContext(old);
        jxpContext.registerNamespace("xsi", "http://www.w3.org/2001/XMLSchema-instance");
        jxpContext.registerNamespace("tmdm", "http://www.talend.com/mdm");
        jxpContext.setLenient(true);

        jxpContext.setFactory(factory);
        String concept = old.getLocalName();
        for (Map.Entry<String, UpdateReportItem> entry : updatedpath.entrySet()) {
            String xpath = entry.getValue().getPath();
            if (xpath.startsWith("/" + concept + "/")) {
                xpath = xpath.replaceFirst("/" + concept + "/", "");
            }
            jxpContext.createPathAndSetValue(xpath, entry.getValue().newValue);
        }
        return (Node) jxpContext.getContextBean();
    }

    private static UpdateReportItem getUpdatedItem(HashMap<String, UpdateReportItem> updatedpath, String xpath) {
        for (Map.Entry<String, UpdateReportItem> entry : updatedpath.entrySet()) {
            if (entry.getKey().startsWith(xpath)) {
                return entry.getValue();
            }
        }
        return null;
    }

    private static boolean hasChildren(Node node) {
        NodeList list = node.getChildNodes();
        for (int i = 0; i < list.getLength(); i++) {
            if (list.item(i).getNodeType() == Node.ELEMENT_NODE) {
                return true;
            }
        }
        return false;
    }

    private static Node getElementChild(Node parent, int index) {
        NodeList list = parent.getChildNodes();
        int j = 0;
        for (int i = 0; i < list.getLength(); i++) {
            if (list.item(i).getNodeType() == Node.ELEMENT_NODE) {
                if (j == index)
                    return list.item(i);
                j++;
            }
        }
        return null;
    }

    private static int getElementNum(NodeList list) {
        int j = 0;
        for (int i = 0; i < list.getLength(); i++) {
            if (list.item(i).getNodeType() == Node.ELEMENT_NODE) {
                j++;
            }
        }
        return j;
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
        String username = "";
        String revisionId = "";

        try {

            username = LocalUser.getLocalUser().getUsername();
            UniversePOJO pojo = LocalUser.getLocalUser().getUniverse();
            if (pojo != null)
                revisionId = pojo.getConceptRevisionID(concept);

        } catch (Exception e1) {
            Logger.getLogger(Util.class).error(e1);
            throw e1;
        }

        String key = "";
        if (ids != null) {
            for (int i = 0; i < ids.length; i++) {
                key += ids[i];
                if (i != ids.length - 1)
                    key += ".";
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
            for (Iterator<UpdateReportItem> iter = list.iterator(); iter.hasNext();) {
                UpdateReportItem item = iter.next();
                String oldValue = item.getOldValue() == null ? "" : item.getOldValue();
                String newValue = item.getNewValue() == null ? "" : item.getNewValue();
                if (newValue.equals(oldValue))
                    continue;
                xml2 += "<Item>" + "   <path>" + StringEscapeUtils.escapeXml(item.getPath()) + "</path>" + "   <oldValue>"
                        + StringEscapeUtils.escapeXml(oldValue) + "</oldValue>" + "   <newValue>"
                        + StringEscapeUtils.escapeXml(newValue) + "</newValue>" + "</Item>";
                isUpdate = true;
            }
            if (!isUpdate)
                return null;
        }
        xml2 += "</Update>";
        return xml2;
    }

    public static boolean isItemCanVisible(ItemPOJOPK itempk) throws XtentisException {
        return LocalUser.getLocalUser().userItemCanRead(itempk)
                || LocalUser.getLocalUser().userItemCanWrite(itempk, itempk.getDataClusterPOJOPK().getUniqueId(),
                        itempk.getConceptName());
    }

    public static String checkOnVersionCompatibility(WSVersion old) {
        Version version = Version.getVersion(IXtentisWSDelegator.class);
        String oldv = old.getMajor() + "." + old.getMinor() + "." + old.getRevision();
        String newv = version.getMajor() + "." + version.getMinor() + "." + version.getRevision();
        String str = "The two MDM Servers is not compatible, one is " + oldv + ", another is " + newv;
        if (version.getMajor() != old.getMajor() || version.getMinor() != old.getMinor()) {
            return str;
        }
        return null;
    }

    /*********************************************************************
     * TESTS
     *********************************************************************/

    public static void testSpellCheck() throws Exception {
        // String xml =
        // getXML("/home/bgrieder/workspace/com.amalto.total.poc/src/com/amalto/total/poc/data/Amalto2.xml");
        // DataClusterBMP dcb = new DataClusterBMP();
        // dcb.addToVocabulary(xml);
        // System.out.println("VOCABULARY:\n"+dcb.getVocabulary());
        //
        // System.setProperty("jazzy.config", "com.amalto.core.util.JazzyConfiguration");
        //
        // SpellDictionary dictionary = new SpellDictionaryHashMap(
        // new StringReader(dcb.getVocabulary()),
        // SpellCheckHandler.getPhonetsReader("fr")
        // );
        //
        // SpellChecker spellCheck = new SpellChecker(dictionary);
        // SpellCheckHandler handler = new SpellCheckHandler();
        // spellCheck.addSpellCheckListener(handler);
        //
        // String toCheck = "Aalto dNs boneur boner bonh pascl pcheri 123-456 stars crstal";
        // int errors = spellCheck.checkSpelling(new StringWordTokenizer(toCheck.toLowerCase()));
        // if (errors ==0) {
        // System.out.println("Nothing I can do");
        // return;
        // }
        //
        // boolean IGNORE_NON_EXISTENT_WORDS = false;
        // //int depth = 4;
        //
        // ArrayList suggestions = new ArrayList();
        // Pattern p = Pattern.compile("\\p{Space}*([^\\p{Space}]{3,}?)\\p{Space}+");
        // Matcher m = p.matcher(" "+toCheck+" ");
        // int pos = 0;
        // while (m.find(pos)) {
        // pos = m.end()-1;
        // String word = m.group(1).trim().toLowerCase();
        // if (IGNORE_NON_EXISTENT_WORDS) {
        // if (handler.getSuggestions().containsKey(word)) {
        // //the spell hcecker di not ignore the word
        // Collection results = (Collection)handler.getSuggestions().get(word);
        // if (results.size()>0) {
        // suggestions.add(new ArrayList(results));
        // } // else we ignore the word because no suggestion
        // } else {
        // //the word exists (or has been ignored on purpose by the spell checker)
        // //we suggest the original word
        // ArrayList results = new ArrayList();
        // results.add(new Word(word,0));
        // suggestions.add(new ArrayList(results));
        // }
        // } else {
        // Collection results = (Collection)handler.getSuggestions().get(word);
        // if ((results==null) || (results.size()==0)) {
        // results = new ArrayList();
        // results.add(new Word(word,0));
        // }
        // suggestions.add(new ArrayList(results));
        // }
        // }
        //
        // System.out.println("TO CHECK: "+toCheck);
        //
        // ArrayList proposals = new ArrayList();
        // for (Iterator iter = suggestions.iterator(); iter.hasNext();) {
        // ArrayList sug = (ArrayList) iter.next();
        // ArrayList newProposals = new ArrayList();
        // for (Iterator iterator = sug.iterator(); iterator.hasNext(); ) {
        // Word suggestion = (Word) iterator.next();
        // if (proposals.size()==0) { // first run
        // newProposals.add(suggestion.getWord());
        // } else {
        // for (Iterator iterator2 = proposals.iterator(); iterator2.hasNext(); ) {
        // String proposal = (String) iterator2.next();
        // newProposals.add(proposal+" "+suggestion.getWord());
        // }
        // }
        // } //for suggestions
        // proposals = newProposals;
        // }//for words
        //
        // int i=0;
        // for (Iterator iter = proposals.iterator(); iter.hasNext(); ) {
        // String proposal = (String) iter.next();
        // System.out.println("P"+(++i)+": "+proposal);
        // }

        /*
         * //iterate to build possibilities int proposalNum = 0; int firstWord = 0; int endWord = 0; while (true) {
         * 
         * //build a proposal String proposal = ""; for (int i = 0; i < words.size(); i++) {
         * proposal+="".equals(proposal) ? "" : " "; int currentSugg =((Integer)currentSuggestion.get(i)).intValue();
         * proposal+=((ArrayList)suggestions.get(i)).get(currentSugg); } System.out.println("PROPOSAL: "+proposal);
         * proposalNum++;
         * 
         * if (proposalNum == depth) break;
         * 
         * boolean noPossibility = true; boolean goOn = true; while(goOn) { //try incrementing the first word int
         * currSugg = ((Integer)currentSuggestion.get(firstWord)).intValue(); int currMax =
         * ((Integer)maxSuggestion.get(firstWord)).intValue(); if (currSugg<currMax) { currentSuggestion.set(firstWord,
         * new Integer(currSugg+1)); noPossibility = false; goOn=false; break; }
         * 
         * //reset the words for (int i = 0; i < endWord; i++) { currentSuggestion.set(i, new Integer(0)); } firstWord =
         * 0; //try incrementing second word while (goOn) { currSugg =
         * ((Integer)currentSuggestion.get(endWord)).intValue(); currMax =
         * ((Integer)maxSuggestion.get(endWord)).intValue(); if (currSugg<currMax) { currentSuggestion.set(endWord, new
         * Integer(currSugg+1)); noPossibility = false; goOn=false; break; } if(++endWord==words.size()) { noPossibility
         * = true; //no more possibility goOn=false; break; } }//incrementing the second word }//while find next
         * 
         * if (noPossibility) break;
         * 
         * }//while next proposal
         */
    }

    // private static String getXML(String filename) throws Exception{
    // BufferedReader in = null;
    // in = new BufferedReader(
    // new InputStreamReader(
    // new FileInputStream(filename),
    // "utf-8"
    // )
    // );
    // String xsl="";
    // String line;
    // while ((line=in.readLine())!=null) xsl+=line+"\n";
    // return xsl;
    // }

    /**
     * check current server is Enterprise or Open
     */
    public static boolean isEnterprise() {
        try {
            Object home = null;
            InitialContext initialContext = null;
            String jndiName = "amalto/local/service/workflow";
            try {
                initialContext = new InitialContext(null);
                home = initialContext.lookup(jndiName);
            } catch (NamingException e) {
                String err = "Unable to lookup \"" + jndiName + "\"" + ": " + e.getClass().getName() + ": "
                        + e.getLocalizedMessage();
                throw new XtentisException(err);
            } finally {
                try {
                    initialContext.close();
                } catch (Exception e) {
                }
                ;
            }
            return home != null;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isDefaultSVNUP() throws Exception {
        if (isEnterprise()) {
            Object service = Util.retrieveComponent(null, "amalto/local/service/svn");
            if (service == null)
                return false;
            Boolean result = (Boolean) Util.getMethod(service, "isUp").invoke(service, new Object[] {});
            return result.booleanValue();
        } else {
            return false;
        }
    }

    public static boolean isSVNAutocommit() throws Exception {
        if (isDefaultSVNUP()) {
            Object service = Util.retrieveComponent(null, "amalto/local/service/svn");

            Boolean isauto = (Boolean) Util.getMethod(service, "isAutocommittosvn").invoke(service, new Object[] {});
            return isauto;
        }
        return false;
    }

    public static String getAppServerDeployDir() {
        String jbossHomePath = System.getenv("JBOSS_HOME");
        if (jbossHomePath == null)
            jbossHomePath = System.getProperty("jboss.home.dir");
        return jbossHomePath;
    }

    public static String getJbossHomeDir() {
        String jbossHomePath = com.amalto.core.util.Util.getAppServerDeployDir();
        String jbossHome = new File(jbossHomePath).getAbsolutePath();
        return jbossHome;
    }

    public static String getBarHomeDir() {
        return Util.getJbossHomeDir() + File.separator + "barfiles";
    }

    /*********************************************************************
     * MAIN
     *********************************************************************/
    public static List<File> listFiles(FileFilter filter, File folder) {
        List<File> ret = new ArrayList<File>();
        File[] childs = folder.listFiles(filter);
        for (File f : childs) {
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
            int i = 0;
            for (Iterator<IWhereItem> iter = subItems.iterator(); iter.hasNext();) {
                IWhereItem item = iter.next();
                getView(views, item);
            }
        } else if (whereItem instanceof WhereCondition) {
            WhereCondition whereCondition = (WhereCondition) whereItem;
            views.add(whereCondition.getLeftPath());
        }
        // FIXME do we to consider CustomWhereCondition
    }

    public static Map<String, XSElementDecl> getConceptMap(DataModelPOJO dataModelPOJO) throws Exception {

        String xsd = dataModelPOJO.getSchema();
        return getConceptMap(xsd);
    }

    private static XSElementDecl parseMetaDataTypes(XSElementDecl elem, String pathSlice, ArrayList<String> valuesHolder,
            boolean forFkValue) {
        valuesHolder.clear();
        XSContentType conType;
        if (elem == null)
            return null;
        XSType type = elem.getType();
        if (elem.getName().equals(pathSlice)) {
            if (elem.getType() instanceof XSComplexType) {
                valuesHolder.add("complex type");
            } else {
                XSSimpleType simpType = (XSSimpleType) elem.getType();
                valuesHolder.add(simpType.getName());
            }
            return elem;
        }
        if (type instanceof XSComplexType) {
            XSComplexType cmpxType = (XSComplexType) type;
            conType = cmpxType.getContentType();
            XSParticle[] children = conType.asParticle().getTerm().asModelGroup().getChildren();
            for (XSParticle child : children) {
                if (child.getTerm() instanceof XSElementDecl) {
                    XSElementDecl childElem = (XSElementDecl) child.getTerm();
                    if (childElem.getName().equals(pathSlice)) {

                        if (childElem.getType() instanceof XSSimpleType) {
                            XSSimpleType simpType = (XSSimpleType) childElem.getType();
                            Collection<FacetImpl> facets = (Collection<FacetImpl>) simpType.asRestriction().getDeclaredFacets();
                            for (XSFacet facet : facets) {
                                if (facet.getName().equals("enumeration")) {
                                    valuesHolder.add("enumeration");
                                    break;
                                }
                            }
                            if (!valuesHolder.contains("enumeration")) {
                                String basicName = simpType.getBaseType().getName();
                                String simpTypeName = simpType.getName();
                                if (simpType.getTargetNamespace().equals(W3C_XML_SCHEMA)) {
                                    simpTypeName = "xsd:" + simpTypeName;
                                } else
                                    simpTypeName = "xsd:" + basicName;
                                valuesHolder.add(simpTypeName);
                            } else if (simpType.asRestriction() != null && valuesHolder.contains("enumeration")) {
                                XSType xsType = simpType.asRestriction().getBaseType();
                                String simpTypeName = "xsd:" + xsType.getName();
                                valuesHolder.set(valuesHolder.indexOf("enumeration"), simpTypeName);
                            }
                        } else {
                            XSComplexType cmpType = (XSComplexType) childElem.getType();
                            valuesHolder.add("complex type");
                        }
                        return childElem;
                    }
                }
            }
        }

        return null;

    }

    public static Element getLoginProvisioningFromDB() throws Exception {

        ItemPOJOPK itempk = new ItemPOJOPK(new DataClusterPOJOPK("PROVISIONING"), "User", new String[] { LocalUser.getLocalUser()
                .getUsername() });
        ItemPOJO item = ItemPOJO.load(itempk);
        if (item == null)
            return null;
        Element user = (Element) Util.getNodeList(item.getProjection(), "//User").item(0);
        return user;
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
                    // configuration.setCluster(Util.getNodeList(node, "value").item(0).getTextContent());
                    Node fchild = Util.getNodeList(node, "value").item(0).getFirstChild();
                    return fchild.getNodeValue();
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
                    Node fchild = Util.getNodeList(node, "value").item(0).getFirstChild();
                    return fchild.getNodeValue();
                }
            }
        }
        return null;
    }

    public static Map<String, ArrayList<String>> getMetaDataTypes(IWhereItem whereItem)
            throws Exception {
        return getMetaDataTypes(whereItem, null);
    }

    // FIXME: Seems it is useless to set an ArrayList for each xpath
    public static Map<String, ArrayList<String>> getMetaDataTypes(IWhereItem whereItem, SchemaManager schemaManager)
            throws Exception {
        HashMap<String, ArrayList<String>> metaDataTypes = new HashMap<String, ArrayList<String>>();

        if (whereItem == null)
            return null;

        // Get concepts from where conditions
        Set<String> searchPaths = new HashSet<String>();
        Util.getView(searchPaths, whereItem);

        Set<String> concepts = new HashSet<String>();
        for (String searchPath : searchPaths) {
            concepts.add(searchPath.split("/")[0]);
        }

        // Travel concepts to parse metadata types
        for (String conceptName : concepts) {
            if (schemaManager == null)
                schemaManager = SchemaCoreAgent.getInstance();
            BusinessConcept bizConcept=schemaManager.getBusinessConceptForCurrentUser(conceptName); 
            if (bizConcept == null)
                break;
            bizConcept.load();
            Map<String, String> xpathTypeMap = bizConcept.getXpathTypeMap();
            for (String xpath : xpathTypeMap.keySet()) {
                String elType = xpathTypeMap.get(xpath);
                ArrayList<String> elTypeWrapper=new ArrayList<String>(){};
                elTypeWrapper.add(elType);
                metaDataTypes.put(xpath, elTypeWrapper);
            }
        }    

        return metaDataTypes;
    }


    /**
     * fix the web conditions.
     *
     * @param conditions in web ui.
     */
    public static IWhereItem fixWebCondtions(IWhereItem whereItem) throws XmlServerException {
        if (whereItem == null)
            return null;
        if (whereItem instanceof WhereLogicOperator) {
            List<IWhereItem> subItems = ((WhereLogicOperator) whereItem).getItems();

            for (int i = subItems.size() - 1; i >= 0; i--) {
                IWhereItem item = subItems.get(i);
                item = fixWebCondtions(item);

                if (item instanceof WhereLogicOperator) {
                    if (((WhereLogicOperator) item).getItems().size() == 0) {
                        subItems.remove(i);
                    }
                } else if (item == null) {
                    subItems.remove(i);
                }
            }
        } else if (whereItem instanceof WhereCondition) {
            WhereCondition condition = (WhereCondition) whereItem;
            whereItem = "*".equals(condition.getRightValueOrPath()) || condition.getRightValueOrPath().length() == 0
                    || ".*".equals(condition.getRightValueOrPath()) ? null : whereItem;
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

    public static void fixCondtions(ArrayList conditions) {
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

    public static WSMDMJob[] getMDMJobs() {
        List<WSMDMJob> jobs = new ArrayList<WSMDMJob>();
        try {
            String jbossHomePath = Util.getAppServerDeployDir();

            String deploydir = "";
            try {
                deploydir = new File(jbossHomePath).getAbsolutePath();
            } catch (Exception e1) {
                Logger.getLogger(Util.class).error(e1);
            }
            deploydir = deploydir + File.separator + "server" + File.separator + "default" + File.separator + "deploy";
            Logger.getLogger(Util.class).info("deploy url:" + deploydir);
            if (!new File(deploydir).exists())
                throw new FileNotFoundException();
            // TODO is it recursive
            FileFilter filter = new FileFilter() {

                public boolean accept(File pathname) {
                    if (pathname.isDirectory() || (pathname.isFile() && pathname.getName().toLowerCase().endsWith(".war"))) {
                        return true;
                    }
                    return false;
                }
            };
            List<File> warFiles = listFiles(filter, new File(deploydir));
            // zip
            filter = new FileFilter() {

                public boolean accept(File pathname) {
                    if (pathname.isDirectory() || (pathname.isFile() && pathname.getName().toLowerCase().endsWith(".zip"))) {
                        return true;
                    }
                    return false;
                }
            };
            List<File> zipFiles = listFiles(filter, new File(JobContainer.getUniqueInstance().getDeployDir()));
            for (File war : warFiles) {
                String jobpath = war.getAbsolutePath().replace(new File(deploydir).getAbsolutePath(), "");
                WSMDMJob job = getJobInfo(war.getAbsolutePath());
                if (job != null)
                    jobs.add(job);
            }
            for (File zip : zipFiles) {
                String jobpath = zip.getAbsolutePath().replace(new File(deploydir).getAbsolutePath(), "");
                WSMDMJob job = getJobInfo(zip.getAbsolutePath());
                if (job != null)
                    jobs.add(job);
            }
        } catch (Exception e) {
            Logger.getLogger(Util.class).error(e);
        }
        return jobs.toArray(new WSMDMJob[jobs.size()]);
    }

    /**
     * get the JobInfo:jobName and version & suffix
     *
     * @param fileName
     * @return
     */
    public static WSMDMJob getJobInfo(String fileName) {
        WSMDMJob jobInfo = null;
        try {
            ZipInputStream in = new ZipInputStream(new FileInputStream(fileName));
            ZipEntry z = null;
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
                    while ((z = in.getNextEntry()) != null) {
                        String dirName = z.getName();
                        int pos = dirName.indexOf('/');
                        if (pos == -1) {
                            pos = dirName.indexOf(File.separator);
                        }
                        String dir = dirName.substring(0, pos);
                        pos = dir.lastIndexOf('_');
                        jobName = dir.substring(0, pos);
                        jobVersion = dir.substring(pos + 1);
                        break;

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
            } finally {
                in.close();
            }
        } catch (Exception e) {

        }

        return jobInfo;
    }

    public static String convertAutoIncrement(Properties p) {
        StringBuffer sb = new StringBuffer();
        sb.append("<AutoIncrement>");
        sb.append("<id>AutoIncrement</id>");
        for (Entry entry : p.entrySet()) {
            sb.append("<entry>");
            sb.append("<key>").append(entry.getKey()).append("</key>");
            sb.append("<value>").append(entry.getValue()).append("</value>");
            sb.append("</entry>");
        }
        sb.append("</AutoIncrement>");
        String xmlString = sb.toString();
        return xmlString;
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
            } else if (aggregate == 0)
                result.add(ch + "");
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
        StringBuffer sb = new StringBuffer(n);

        for (int i = 0; i < n; i++) {
            char ch = string.charAt(i);

            if (ch == '\'') {
                sb.append('\'');
            }

            sb.append(ch);
        }

        return sb.toString();

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

    public static String printWithFormat(Locale locale, String format, Object value) {

        String formattedContent = "";

        try {

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PrintStream ps = new PrintStream(baos);

            ps.format(locale, format, value);
            formattedContent = baos.toString();

            baos.close();

        } catch (IOException ioe) {
            Logger.getLogger(Util.class).error(ioe);
        }

        return formattedContent;

    }

    public static String getExistHome() {
        String home = getJbossHomeDir() + "/eXist";
        home = new File(home).getAbsolutePath();
        System.setProperty("exist.home", home);
        Logger.getLogger(Util.class).info("exist.home===" + home);
        return home;
    }

    public static String replaceXpathPivot(String concept, String xpath, String replacement) {
        String[] xpathParts = xpath.split("/");
        if (xpathParts[0].equals(concept))
            xpathParts[0] = replacement;
        xpath = joinStrings(xpathParts, "/");
        return xpath;
    }

    public static String[] getParentAndLeafPath(String pivot) throws XtentisException {
        if (pivot.endsWith(")")) {
            String err = "Invalid pivot '" + pivot + "': pivots must be 'pure' path, with no functions";

            throw new XtentisException(err);
        }
        // Normalize path
        pivot = pivot.startsWith("/") ? pivot.substring(1) : pivot; // remove leading slash
        pivot = pivot.endsWith("/") ? pivot.substring(0, pivot.length() - 1) : pivot; // remove trailing slash
        String[] pivotPaths = pivot.split("\\/");
        if (pivotPaths.length < 2) {
            String err = "Invalid pivot '" + pivot + "': partial updates cannot be applied to the root element";
            throw new XtentisException(err);
        }
        // build parent pivot
        String parentPivot = "";
        for (int i = 0; i < pivotPaths.length - 1; i++) {
            parentPivot += "/" + pivotPaths[i];
        }
        // assign pivotElement
        String pivotLeaf = pivotPaths[pivotPaths.length - 1];
        return new String[] { parentPivot, pivotLeaf };
    }
    
    public static <T> T getException(Throwable throwable, Class<T> cls){
        if(cls.isInstance(throwable))
            return (T) throwable;
        
        if(throwable.getCause() != null)
            return getException(throwable.getCause(), cls);
        return null;
    }

    public static String getDefaultSystemLocale() {
        return MDMConfiguration.getConfiguration().getProperty("system.locale.default");//$NON-NLS-1$
    }

    public static boolean isSystemDC(DataClusterPOJOPK dataClusterPOJOPK) {
        if (dataClusterPOJOPK != null && dataClusterPOJOPK.getUniqueId() != null) {
            String dcName = dataClusterPOJOPK.getUniqueId();
            Map<String, XSystemObjects> xDataClustersMap = XSystemObjects.getXSystemObjects(XObjectType.DATA_CLUSTER);
            if (XSystemObjects.isXSystemObject(xDataClustersMap, XObjectType.DATA_CLUSTER, dcName)
                    || dcName.startsWith("amalto") //$NON-NLS-1$
                    || "MDMDomainObjects".equals(dcName) //$NON-NLS-1$
                    || "FailedAutoCommitSvnMessage".equals(dcName)//$NON-NLS-1$
                    || "twitter".equals(dcName) //$NON-NLS-1$
                    || "system".equals(dcName))
                return true;
        }
        return false;
    }
}
