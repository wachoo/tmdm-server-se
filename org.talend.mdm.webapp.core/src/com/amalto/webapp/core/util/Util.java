/*
 * Created on 22 sept. 2005
 */
package com.amalto.webapp.core.util;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.rmi.RemoteException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Principal;
import java.security.acl.Group;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.ejb.CreateException;
import javax.naming.NamingException;
import javax.security.auth.Subject;
import javax.security.jacc.PolicyContext;
import javax.security.jacc.PolicyContextException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.rpc.Stub;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.jxpath.AbstractFactory;
import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.jxpath.Pointer;
import org.apache.log4j.Logger;
import org.exolab.castor.types.Date;
import org.jboss.security.Base64Encoder;
import org.jboss.security.SimpleGroup;
import org.talend.mdm.commmon.util.core.EDBType;
import org.talend.mdm.commmon.util.core.MDMConfiguration;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import com.amalto.core.ejb.ItemPOJO;
import com.amalto.core.ejb.local.XmlServerSLWrapperLocal;
import com.amalto.core.objects.transformers.v2.ejb.TransformerV2POJOPK;
import com.amalto.core.objects.universe.ejb.UniversePOJO;
import com.amalto.core.util.LocalUser;
import com.amalto.core.util.XtentisException;
import com.amalto.webapp.core.bean.Configuration;
import com.amalto.webapp.core.json.JSONArray;
import com.amalto.webapp.core.json.JSONObject;
import com.amalto.webapp.util.webservices.WSBase64KeyValue;
import com.amalto.webapp.util.webservices.WSConnectorResponseCode;
import com.amalto.webapp.util.webservices.WSCount;
import com.amalto.webapp.util.webservices.WSCountItemsByCustomFKFilters;
import com.amalto.webapp.util.webservices.WSDataClusterPK;
import com.amalto.webapp.util.webservices.WSGetItem;
import com.amalto.webapp.util.webservices.WSGetItemsByCustomFKFilters;
import com.amalto.webapp.util.webservices.WSGetUniverse;
import com.amalto.webapp.util.webservices.WSItem;
import com.amalto.webapp.util.webservices.WSItemPK;
import com.amalto.webapp.util.webservices.WSStringArray;
import com.amalto.webapp.util.webservices.WSStringPredicate;
import com.amalto.webapp.util.webservices.WSUniverse;
import com.amalto.webapp.util.webservices.WSUniversePK;
import com.amalto.webapp.util.webservices.WSWhereAnd;
import com.amalto.webapp.util.webservices.WSWhereCondition;
import com.amalto.webapp.util.webservices.WSWhereItem;
import com.amalto.webapp.util.webservices.WSWhereOperator;
import com.amalto.webapp.util.webservices.WSWhereOr;
import com.amalto.webapp.util.webservices.WSXPathsSearch;
import com.amalto.webapp.util.webservices.XtentisPort;
import com.amalto.webapp.util.webservices.XtentisService_Impl;
import com.sun.org.apache.xpath.internal.XPathAPI;
import com.sun.org.apache.xpath.internal.objects.XObject;

/**
 * @author bgrieder
 */
public class Util {

    private static String port = null;
    static {
        port = MDMConfiguration.getConfiguration().getProperty("xmldb.server.port");
        port = port == null ? "8080" : port;
    }

    private static String endpoint_address = "http://localhost:" + port + "/talend/TalendPort";

    public static int _AUTO_ = 0;

    public static int _FORCE_RMI_ = 1;

    public static int _FORCE_WEB_SERVICE_ = 2;

    /*********************************************************************
     * WEB SERVICES
     *********************************************************************/

    public static XtentisPort getPort() throws XtentisWebappException {
        AjaxSubject as;
        try {
            as = Util.getAjaxSubject();
        } catch (Exception e) {
            throw new XtentisWebappException("Unable to access the logged user data");
        }
        if (as == null)
            throw new XtentisWebappException("Session Expired");
        // org.apache.log4j.Category.getInstance(Util.class).debug("getPort() ");
        String[] mdm = as.getMDMData();
        String url = "http://" + mdm[0] + "/talend/TalendPort";
        return Util.getPort(url, mdm[1], mdm[2]);
    }

    public static XtentisPort getPort(String username, String password) throws XtentisWebappException {
        return getPort(endpoint_address, username, password, _AUTO_);
    }

    public static XtentisPort getPort(String endpointAddress, String username, String password) throws XtentisWebappException {
        return getPort(endpointAddress, username, password, _AUTO_);
    }

    public static XtentisPort getPort(String endpointAddress, String username, String password, int force)
            throws XtentisWebappException {

        if (force == _FORCE_RMI_)
            return getRMIEndPoint();
        if (force == _FORCE_WEB_SERVICE_)
            return getWSPort(endpointAddress, username, password);

        // Auto
        if (endpointAddress.contains("localhost"))
            return getRMIEndPoint();

        return getWSPort(endpointAddress, username, password);

    }

    private static XtentisPort getPort(String username, String password, int force) throws XtentisWebappException {
        return getPort(endpoint_address, username, password, force);
    }

    private static XtentisPort getWSPort(String endpointAddress, String username, String password) throws XtentisWebappException {
        try {
            Stub stub = (Stub) (new XtentisService_Impl()).getXtentisPort();
            stub._setProperty(Stub.ENDPOINT_ADDRESS_PROPERTY, endpointAddress);
            stub._setProperty(Stub.USERNAME_PROPERTY, username);
            stub._setProperty(Stub.PASSWORD_PROPERTY, password);

            return (XtentisPort) stub;
        } catch (Exception e) {
            e.printStackTrace();
            throw new XtentisWebappException("Unable to access endpoint at: " + endpointAddress + ": " + e.getLocalizedMessage());
        }
    }

    private static XtentisPort getRMIEndPoint() throws XtentisWebappException {

        // return new XtentisRMIPort();

        try {
            return (IXtentisRMIPort) Class.forName("com.amalto.webapp.core.util.XtentisRMIPort").newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /*********************************************************************
     * LOCAL FILE UTILS
     *********************************************************************/

    public static String getXML(Class<?> c, String filename) throws Exception {
        BufferedReader in = null;
        in = new BufferedReader(new InputStreamReader(c.getResourceAsStream(filename)));

        String xml = "";
        String line;
        while ((line = in.readLine()) != null)
            xml += line + "\n";
        return xml;
    }

    public static String getPackageFilePath(Class<?> c, String filename) {
        return c.getResource(filename).getPath();
    }

    /*********************************************************************
     * NODE UTILS
     *********************************************************************/

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
            res += strings[i];
        }
        return res;
    }

    /**
     * Returns the first part - eg. the concept - from the path
     * 
     * @param path
     * @return the Concept Name
     */
    public static String getConceptFromPath(String path) {
        Pattern p = Pattern.compile("(.*?)[\\[|/].*");
        if (!path.endsWith("/"))
            path += "/";
        Matcher m = p.matcher(path);
        if (m.matches())
            return m.group(1);
        return null;
    }

    /**
     * DOC HSHU Comment method "getFieldFromPath". Returns the last part - eg. the field name - from the path
     * 
     * @param path
     * @return
     */
    public static String getFieldFromPath(String path) {
        String result = null;
        if (path != null) {
            if (path.endsWith("/"))
                path = path.substring(0, path.lastIndexOf("/"));
            String[] tmps = path.split("/");
            result = tmps[tmps.length - 1];
        }
        return result;
    }

    public static String getForeignPathFromPath(String path) {
        int pos = path.indexOf("[");
        if (pos != -1) {
            return path.substring(0, pos);
        }
        return path;

    }

    public static WSWhereCondition getConditionFromPath(String path) {
        Pattern p = Pattern.compile("(.*?)\\[(.*?)(&=|!=|>=|<=|>|<|=)(.*?)\\].*");
        if (!path.endsWith("/"))
            path += "/";
        Matcher m = p.matcher(path);
        if (m.matches()) {
            WSWhereCondition wc = new WSWhereCondition();
            wc.setLeftPath(m.group(2).trim());
            com.amalto.webapp.util.webservices.WSWhereOperator operator = changeToWSOperator(m.group(3));
            wc.setOperator(operator);
            wc.setRightValueOrPath(m.group(4).trim().replaceAll("'|\"", ""));
            wc.setSpellCheck(true);
            wc.setStringPredicate(WSStringPredicate.NONE);
            return wc;
        }
        return null;
    }

    private static WSWhereItem getConditionFromFKFilter(String fkFilter) {
        if (fkFilter == null || fkFilter.length() == 0)
            return null;
        if (fkFilter.equals("null"))
            return null;

        String[] criterias = fkFilter.split("#");

        ArrayList<WSWhereItem> condition = new ArrayList<WSWhereItem>();
        for (String cria : criterias) {
            String[] values = cria.split("\\$\\$");

            WSWhereCondition wc = Util.convertLine(values);
            if (wc != null) {
                condition.add(new WSWhereItem(wc, null, null));
            }
        }
        if (condition.size() > 0) {
            WSWhereAnd and = new WSWhereAnd(condition.toArray(new WSWhereItem[condition.size()]));
            WSWhereItem whand = new WSWhereItem(null, and, null);
            return whand;
        } else {
            return null;
        }
    }

    public static WSWhereCondition convertLine(String[] values) {
        if (values.length < 3)
            return null;
        WSWhereCondition wc = new WSWhereCondition();

        wc.setLeftPath(values[0]);

        if (values.length >= 3) {
            WSWhereOperator operator = null;
            if (values[1].equals("Contains"))
                operator = WSWhereOperator.CONTAINS;
            else if (values[1].equals("Contains Text Of"))
                operator = WSWhereOperator.JOIN;
            else if (values[1].equals("="))
                operator = WSWhereOperator.EQUALS;
            else if (values[1].equals(">"))
                operator = WSWhereOperator.GREATER_THAN;
            else if (values[1].equals(">="))
                operator = WSWhereOperator.GREATER_THAN_OR_EQUAL;
            else if (values[1].equals("<"))
                operator = WSWhereOperator.LOWER_THAN;
            else if (values[1].equals("<="))
                operator = WSWhereOperator.LOWER_THAN_OR_EQUAL;
            else if (values[1].equals("!="))
                operator = WSWhereOperator.NOT_EQUALS;
            else if (values[1].equals("Starts With"))
                operator = WSWhereOperator.STARTSWITH;
            else if (values[1].equals("Strict Contains"))
                operator = WSWhereOperator.STRICTCONTAINS;
            wc.setOperator(operator);
            if (values[2] != null && values[2].matches("^\".*\"$"))
                values[2] = values[2].substring(1, values[2].length() - 1);
            wc.setRightValueOrPath(values[2]);
        }

        if (values.length >= 4) {
            WSStringPredicate predicate = null;
            if (values[3].equals(""))
                predicate = WSStringPredicate.NONE;
            else if (values[3].equals("Or"))
                predicate = WSStringPredicate.OR;
            if (values[3].equals("And"))
                predicate = WSStringPredicate.AND;
            if (values[3].equals("Strict And"))
                predicate = WSStringPredicate.STRICTAND;
            if (values[3].equals("Exactly"))
                predicate = WSStringPredicate.EXACTLY;
            if (values[3].equals("Not"))
                predicate = WSStringPredicate.NOT;
            wc.setStringPredicate(predicate);
        } else {
            wc.setStringPredicate(WSStringPredicate.NONE);
        }

        return wc;
    }

    /**
     * 
     * @param doc
     * @param type
     * @return a specific value for Simple Type, "" for Complex Type
     * @throws Exception
     */
    public static boolean findXSDSimpleTypeInDocument(Document doc, Node elem, String type, ArrayList<String> typeInfo)
            throws Exception {
        if (type != null && type.trim().equals("")) {
            if (Util.getNodeList(elem, ".//xsd:simpleType").getLength() > 0) {
                NodeList list = Util.getNodeList(elem, ".//xsd:simpleType/xsd:restriction");
                if (list.getLength() > 0) {
                    if (Util.getNodeList(elem, ".//xsd:simpleType/xsd:restriction/xsd:enumeration").getLength() > 0) {
                        NodeList emumList = Util.getNodeList(elem, ".//xsd:simpleType/xsd:restriction/xsd:enumeration");
                        typeInfo.add("enumeration");
                        for (int i = 0; i < emumList.getLength(); i++) {
                            typeInfo.add(emumList.item(i).getAttributes().getNamedItem("value").getNodeValue());
                        }
                    } else {
                        typeInfo.add(list.item(0).getAttributes().getNamedItem("base").getNodeValue());
                    }
                }
                return true;
            } else if (Util.getNodeList(elem, "/xsd:complexType").getLength() > 0) {
                return false;
            }
        }

        String path = "//xsd:simpleType";
        if (type != null && !type.trim().equals("")) {
            path += "[@name='" + type + "']";
        }
        if (Util.getNodeList(doc, path).getLength() > 0) {
            Node node = Util.getNodeList(doc, path).item(0);
            if (Util.getNodeList(node, "//xsd:restriction").getLength() > 0) {
                Node resNode = Util.getNodeList(node, "//xsd:restriction").item(0);
                NodeList enumList = Util.getNodeList(resNode, "/xsd:enumeration");
                if (enumList.getLength() > 0) {
                    // enumeration occurs
                    typeInfo.add("enumeration");
                    for (int i = 0; i < enumList.getLength(); i++) {
                        typeInfo.add(enumList.item(i).getAttributes().getNamedItem("value").getNodeValue());
                    }
                } else {
                    typeInfo.add(resNode.getAttributes().getNamedItem("base").getNodeValue());
                }
                return true;
            }
            return false;
        } else {
            NodeList importList = null;
            for (int nm = 0; nm < 2; nm++) {
                if (nm == 0) {
                    importList = Util.getNodeList(doc, "//xsd:import");
                } else {
                    importList = Util.getNodeList(doc, "//xsd:include");
                }

                for (int i = 0; i < importList.getLength(); i++) {
                    Node schemaLocation = importList.item(i).getAttributes().getNamedItem("schemaLocation");
                    if (schemaLocation == null) {
                        continue;
                    }
                    String location = schemaLocation.getNodeValue();
                    Document subDoc = parseImportedFile(location);
                    return findXSDSimpleTypeInDocument(subDoc, importList.item(i), type, typeInfo);
                }
            }
        }

        return false;
    }

    public static Document parseImportedFile(String xsdLocation) {
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
            ex.printStackTrace();
            return null;
        }
        return d;
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
            e.printStackTrace();
            return null;
        }

        return authorizations;
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
            e.printStackTrace();
        }

        return buffer.toString();
    }

    public static com.amalto.webapp.util.webservices.WSWhereOperator changeToWSOperator(String operator) {
        if ("=".equals(operator))
            return com.amalto.webapp.util.webservices.WSWhereOperator.EQUALS;
        if ("!=".equals(operator))
            return com.amalto.webapp.util.webservices.WSWhereOperator.NOT_EQUALS;
        if ("<".equals(operator))
            return com.amalto.webapp.util.webservices.WSWhereOperator.LOWER_THAN;
        if ("<=".equals(operator))
            return com.amalto.webapp.util.webservices.WSWhereOperator.LOWER_THAN_OR_EQUAL;
        if (">".equals(operator))
            return com.amalto.webapp.util.webservices.WSWhereOperator.GREATER_THAN;
        if (">=".equals(operator))
            return com.amalto.webapp.util.webservices.WSWhereOperator.GREATER_THAN_OR_EQUAL;
        if ("&=".equals(operator))
            return com.amalto.webapp.util.webservices.WSWhereOperator.CONTAINS;
        return null;
    }

    /**
     * Generates an xml string from a node (not pretty formatted)
     * 
     * @param n the node
     * @return the xml string
     * @throws Exception
     */
    public static String nodeToString(Node n) throws Exception {
        StringWriter sw = new StringWriter();
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty("omit-xml-declaration", "yes");
        transformer.setOutputProperty("indent", "yes");
        transformer.transform(new DOMSource(n), new StreamResult(sw));
        return sw.toString();
    }

    // This method walks the document and removes all nodes
    // of the specified type and specified name.
    // If name is
    // null, then the node is removed if the type matches.
    public static void removeAll(Node node, short nodeType, String name) {
        if ((nodeType == -1 || node.getNodeType() == nodeType) && (name == null || node.getNodeName().equals(name))) {
            node.getParentNode().removeChild(node);
        } else {
            NodeList list = node.getChildNodes();
            for (int i = 0; i < list.getLength(); i++) {
                removeAll(list.item(i), nodeType, name);
            }
        }
    }

    /**
     * Get a nodelist from an xPath
     * 
     * @throws Exception
     */
    public static NodeList getNodeList(Document d, String xPath) throws Exception {
        return getNodeList(d.getDocumentElement(), xPath, null, null);
    }

    /**
     * Get a nodelist from an xPath
     * 
     * @throws Exception
     */
    public static NodeList getNodeList(Node contextNode, String xPath) throws Exception {
        return getNodeList(contextNode, xPath, null, null);
    }

    /**
     * Get a nodelist from an xPath
     * 
     * @throws Exception
     */
    public static NodeList getNodeList(Node contextNode, String xPath, String namespace, String prefix) throws Exception {
        XObject xo = XPathAPI.eval(contextNode, xPath,
                (namespace == null) ? contextNode : Util.getRootElement("nsholder", namespace, prefix));
        if (xo.getType() != XObject.CLASS_NODESET)
            return null;
        return xo.nodelist();
    }

    /**
     * Returns a namespaced root element of a document Useful to create a namespace holder element
     * 
     * @param namespace
     * @return the root Element
     */
    public static Element getRootElement(String elementName, String namespace, String prefix) throws Exception {
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
            throw new Exception(err);
        }
        return rootNS;
    }

    public static Document parse(String xmlString) throws Exception {
        return parse(xmlString, null);
    }

    public static Document parse(String xmlString, String schema) throws Exception {

        // parse
        Document d = null;
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
                    + xmlString;
            throw new Exception(err);
        }

        // check if dcument parsed correctly against the schema
        if (schema != null) {
            String errors = seh.getErrors();
            if (!errors.equals("")) {
                String err = "Document  did not parse against schema: \n" + errors + "\n" + xmlString;
                throw new Exception(err);
            }
        }
        return d;
    }

    public static String[] getTextNodes(Node contextNode, String xPath) throws XtentisWebappException {
        return getTextNodes(contextNode, xPath, contextNode);
    }

    public static String[] getTextNodes(Node contextNode, String xPath, Node namespaceNode) throws XtentisWebappException {
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
        } catch (Exception e) {
            String err = "Unable to get the text node(s) of " + xPath + ": " + e.getClass().getName() + ": "
                    + e.getLocalizedMessage();
            throw new XtentisWebappException(err);
        }
        return results;

    }

    public static String getFirstTextNode(Node contextNode, String xPath, Node namespaceNode) throws XtentisWebappException {
        String[] res = getTextNodes(contextNode, xPath, namespaceNode);
        if (res.length == 0)
            return null;
        return res[0];
    }

    /**
     * Get the first text node matching the Xpath
     * 
     * @param contextNode
     * @param xPath
     * @return the String or null if not found
     * @throws XtentisWebappException
     */
    public static String getFirstTextNode(Node contextNode, String xPath) throws XtentisWebappException {
        return getFirstTextNode(contextNode, xPath, contextNode);
    }

    /*********************************************************************
     * JACC - JAAS
     *********************************************************************/
    public static AjaxSubject getAjaxSubject() throws PolicyContextException {
        // Retrieve the subject
        String SUBJECT_CONTEXT_KEY = "javax.security.auth.Subject.container";
        Subject sub = (Subject) PolicyContext.getContext(SUBJECT_CONTEXT_KEY);

        return new AjaxSubject(sub);

    }

    public static String getPrincipalMember(String key) throws Exception {
        String result = "";
        // Get the Authenticated Subject

        Subject subject = (Subject) PolicyContext.getContext("javax.security.auth.Subject.container");

        // Now look for a Group

        Set principals = subject.getPrincipals(Principal.class);

        Iterator iter = principals.iterator();

        while (iter.hasNext())

        {

            Principal p = (Principal) iter.next();
            if (p instanceof SimpleGroup) {

                SimpleGroup sg = (SimpleGroup) p;

                if (key.equals(sg.getName()))

                {

                    Enumeration en = sg.members();

                    while (en.hasMoreElements())

                    {

                        String info = en.nextElement().toString();

                        result = result + "," + info;

                    }

                }

            }

        }

        if (result.length() > 0)
            result = result.substring(1);
        return result;
    }

    public static String getLoginUserName() throws Exception {
        return getPrincipalMember("Username");

    }

    public static String getLoginUniverse() throws Exception {
        return getPrincipalMember("Universe");

    }

    public static String getLoginRoles() throws Exception {
        return getPrincipalMember("Roles");

    }

    public static String getRevisionIdFromUniverse(String universeName, String conceptName) throws Exception {
        String revisonId = "";
        WSUniverse wsUniverse = Util.getPort().getUniverse(new WSGetUniverse(new WSUniversePK(universeName)));
        UniversePOJO universe = XConverter.WS2POJO(wsUniverse);
        revisonId = universe.getConceptRevisionID(conceptName);
        return revisonId;
    }

    public static Element getLoginProvisioningFromDB() throws Exception {

        WSItem item = Util.getPort()
                .getItem(
                        new WSGetItem(new WSItemPK(new WSDataClusterPK("PROVISIONING"), "User", new String[] { Util
                                .getLoginUserName() })));
        String userString = item.getContent();

        Element user = (Element) Util.getNodeList(Util.parse(userString), "//User").item(0);
        return user;
        // return com.amalto.core.util.Util.getLoginProvisioningFromDB();
    }

    /*********************************************************************
     * PASSWORD UTILS
     *********************************************************************/

    /**
     * Computes an md5 hash of a string.
     * 
     * @param text the hashed string
     * @return the string hash
     * @exception NullPointerException if text is null
     */
    public static byte[] md5(String text, String charset) {
        // arguments check
        if (text == null) {
            throw new NullPointerException("null text");
        }

        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(text.getBytes(charset));
            return md.digest();
        } catch (NoSuchAlgorithmException e) {
            // log.error("Cannot find MD5 algorithm", e);
            throw new RuntimeException("Cannot find MD5 algorithm");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("No such encoding: " + charset);
        }
    }

    /**
     * Computes an md5 hash and returns the result as a string in hexadecimal format.
     * 
     * @param text the hashed string
     * @return the string hash
     * @exception NullPointerException if text is null
     */
    public static String md5AsHexString(String text, String charset) {
        return toHexString(md5(text, charset));
    }

    /**
     * Returns a string in the hexadecimal format.
     * 
     * @param bytes the converted bytes
     * @return the hexadecimal string representing the bytes data
     * @throws IllegalArgumentException if the byte array is null
     */
    public static String toHexString(byte[] bytes) {
        if (bytes == null) {
            throw new IllegalArgumentException("byte array must not be null");
        }
        StringBuffer hex = new StringBuffer(bytes.length * 2);
        for (int i = 0; i < bytes.length; i++) {
            hex.append(Character.forDigit((bytes[i] & 0XF0) >> 4, 16));
            hex.append(Character.forDigit((bytes[i] & 0X0F), 16));
        }
        return hex.toString();
    }

    /*********************************************************************
     * WEB SERVICES
     *********************************************************************/

    public static HashMap<String, Object> getMapFromKeyValues(WSBase64KeyValue[] params) throws RemoteException {
        try {
            HashMap<String, Object> map = new HashMap<String, Object>();
            if (params != null) {
                for (int i = 0; i < params.length; i++) {
                    if (params[i] != null) {
                        String key = params[i].getKey();
                        byte[] bytes = (new BASE64Decoder()).decodeBuffer(params[i].getBase64StringValue());
                        if (bytes != null) {
                            ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
                            ObjectInputStream ois = new ObjectInputStream(bais);
                            map.put(key, ois.readObject());
                        } else {
                            map.put(key, null);
                        }
                    }
                }
            }
            return map;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RemoteException(e.getClass().getName() + ": " + e.getLocalizedMessage());
        }
    }

    public static WSBase64KeyValue[] getKeyValuesFromMap(HashMap<String, Object> params) throws RemoteException {
        try {
            if (params == null)
                return null;
            WSBase64KeyValue[] keyValues = new WSBase64KeyValue[params.size()];
            Set<String> keys = params.keySet();
            int i = 0;
            for (Iterator<String> iter = keys.iterator(); iter.hasNext();) {
                String key = iter.next();
                Object value = params.get(key);
                if (value != null) {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    ObjectOutputStream oos = new ObjectOutputStream(baos);
                    oos.writeObject(value);
                    String base64Value = Base64Encoder.encode(baos.toByteArray());
                    keyValues[i] = new WSBase64KeyValue();
                    keyValues[i].setKey(key);
                    keyValues[i].setBase64StringValue(base64Value);
                    i++;
                }
            }
            return keyValues;
        } catch (Exception e) {
            throw new RemoteException(e.getClass().getName() + ": " + e.getLocalizedMessage());
        }
    }

    public static String getCodeFromWSConnectorResponseCode(WSConnectorResponseCode code) {
        if (code.equals(WSConnectorResponseCode.OK))
            return "OK";
        if (code.equals(WSConnectorResponseCode.STOPPED))
            return "STOPPED";
        return "ERROR";
    }

    /*********************************************************************
     * VERSIONING
     *********************************************************************/

    private static final String PROP_FILE = "/version.properties";

    /**
     * Returns <code>String</code> representation of package version information.
     */
    public static String getVersion(Class<?> clazz) {
        Properties props = loadProps(clazz);
        return "v" + props.getProperty("major") + "." + props.getProperty("minor") + "." + props.getProperty("rev") + "_"
                + props.getProperty("build.number") + " " + props.getProperty("build.date") + " : "
                + props.getProperty("description");
    }

    // load props as resource on classpath
    private static Properties loadProps(Class<?> clazz) {
        InputStream is;
        Properties props = new Properties();
        is = clazz.getResourceAsStream(PROP_FILE);
        if (is == null) {
            throw new RuntimeException("Couldn't find: " + PROP_FILE + " on CLASSPATH");
        }
        try {
            props.load(is);
            is.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return props;
    }

    // public static void main(String[] args) {
    // getConditionFromPath("Country[Country/isoCode!=CN]");
    //
    // }

    /**
     * store the info of datacluster and datamodel to PROVISIONING.
     */
    public static void storeProvisioning(String username, String xmlString) throws Exception {
        XmlServerSLWrapperLocal server = com.amalto.core.util.Util.getXmlServerCtrlLocal();
        server.putDocumentFromString(xmlString, "PROVISIONING" + "." + "User" + "." + username, "PROVISIONING", null);
        ItemPOJO.clearCache();
    }

    /**
     * gives the operator associated to the string 'option'
     * 
     * @param option
     * @return
     */
    public static WSWhereOperator getOperator(String option) {
        WSWhereOperator res = null;
        if (option.equalsIgnoreCase("CONTAINS"))
            res = WSWhereOperator.CONTAINS;
        else if (option.equalsIgnoreCase("EQUALS"))
            res = WSWhereOperator.EQUALS;
        else if (option.equalsIgnoreCase("GREATER_THAN"))
            res = WSWhereOperator.GREATER_THAN;
        else if (option.equalsIgnoreCase("GREATER_THAN_OR_EQUAL"))
            res = WSWhereOperator.GREATER_THAN_OR_EQUAL;
        else if (option.equalsIgnoreCase("JOIN"))
            res = WSWhereOperator.JOIN;
        else if (option.equalsIgnoreCase("LOWER_THAN"))
            res = WSWhereOperator.LOWER_THAN;
        else if (option.equalsIgnoreCase("LOWER_THAN_OR_EQUAL"))
            res = WSWhereOperator.LOWER_THAN_OR_EQUAL;
        else if (option.equalsIgnoreCase("NOT_EQUALS"))
            res = WSWhereOperator.NOT_EQUALS;
        else if (option.equalsIgnoreCase("STARTSWITH"))
            res = WSWhereOperator.STARTSWITH;
        else if (option.equalsIgnoreCase("STRICTCONTAINS"))
            res = WSWhereOperator.STRICTCONTAINS;
        else if (option.equalsIgnoreCase("FULLTEXTSEARCH"))
            res = WSWhereOperator.FULLTEXTSEARCH;
        return res;
    }

    /**
     * check the certain column is digit
     * 
     * @author ymli
     * @param itemsBrowserContent
     * @param col
     * @return
     */
    public static boolean checkDigist(ArrayList<String[]> itemsBrowserContent, int col) {
        if (col == -1)
            return false;
        for (String[] temp : itemsBrowserContent) {
            if (!temp[col].matches("^(-|)[0-9]+(\\.?)[0-9]*$"))
                return false;
        }
        return true;
    }

    /**
     * sort the ArrayList by col in direction of dir
     * 
     * @author ymli
     * @param itemsBrowserContent
     * @param col
     * @param dir
     */
    public static void sortCollections(ArrayList<String[]> itemsBrowserContent, int col, String dir) {
        System.out.println(dir);
        if (col < 0)
            return;
        if ("descending".equals(dir)) {
            for (int j = 1; j < itemsBrowserContent.size(); j++) {
                String temp[] = itemsBrowserContent.get(j);
                int i = j;
                while (i > 0
                        && (itemsBrowserContent.get(i - 1)[col].length() == 0 || (itemsBrowserContent.get(i - 1)[col].length() > 0
                                && temp[col].length() > 0 && Double.parseDouble(itemsBrowserContent.get(i - 1)[col]) < Double
                                .parseDouble(temp[col])))) {
                    itemsBrowserContent.set(i, itemsBrowserContent.get(i - 1));
                    i--;
                }
                itemsBrowserContent.set(i, temp);
            }
        } else {
            for (int j = 1; j < itemsBrowserContent.size(); j++) {
                String temp[] = itemsBrowserContent.get(j);
                int i = j;
                while ((i > 0 && itemsBrowserContent.get(i - 1)[col].length() > 0 && temp[col].length() > 0 && Double
                        .parseDouble(itemsBrowserContent.get(i - 1)[col]) > Double.parseDouble(temp[col]))
                        || i > 0
                        && temp[col].length() == 0) {
                    itemsBrowserContent.set(i, itemsBrowserContent.get(i - 1));
                    i--;
                }
                itemsBrowserContent.set(i, temp);

            }
        }
    }

    /**
     * get the column number of the certain title in Array columns
     * 
     * @author ymli
     * @param columns
     * @param title
     * @return
     */
    public static int getSortCol(String[] columns, String title) {
        int col = -1;
        for (int i = 0; i < columns.length; i++)
            if (("/" + columns[i]).equals(title))
                return i;
        return col;
    }

    public static List<String> getElementValues(String parentPath, Node n) throws Exception {
        List<String> l = new ArrayList<String>();
        NodeList list = n.getChildNodes();
        for (int i = 0; i < list.getLength(); i++) {
            Node node = list.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                // String nName=node.getNodeName();
                // String xPath=parentPath+"/"+nName;
                String nValue = com.amalto.core.util.Util.getFirstTextNode(node, ".");
                if (!hasChildren(node)) {
                    l.add(nValue);
                }
            }
        }
        return l;
    }

    public static boolean hasChildren(Node node) {
        NodeList list = node.getChildNodes();
        for (int i = 0; i < list.getLength(); i++) {
            if (list.item(i).getNodeType() == Node.ELEMENT_NODE) {
                return true;
            }
        }
        return false;
    }

    public static WSWhereItem buildWhereItems(String criteria) throws Exception {
        Pattern p = Pattern.compile("\\((.*)\\)");
        Matcher m = p.matcher(criteria);
        if (m.matches()) {
            criteria = m.group(1);
        }

        String[] criterias = criteria.split("[\\s]+OR[\\s]+");
        ArrayList<WSWhereItem> conditions = new ArrayList<WSWhereItem>();

        for (String cria : criterias) {
            ArrayList<WSWhereItem> condition = new ArrayList<WSWhereItem>();

            m = p.matcher(cria);
            if (m.matches()) {
                cria = m.group(1);
            }
            String[] subCriterias = cria.split("[\\s]+AND[\\s]+");
            // add by ymli; fix the bug: 0011974. remove "(" at the left and ")" at the right
            for (String subCria : subCriterias) {
                // if (subCria.startsWith("(")) {
                // subCria = subCria.substring(1);
                // }
                // if(subCria.endsWith(")"))
                // subCria = subCria.substring(0, subCria.length() - 1);
                WSWhereItem whereItem = buildWhereItem(subCria);
                if (whereItem != null)
                    condition.add(whereItem);
            }
            if (condition.size() > 0) {
                WSWhereAnd and = new WSWhereAnd(condition.toArray(new WSWhereItem[condition.size()]));
                WSWhereItem whand = new WSWhereItem(null, and, null);
                conditions.add(whand);
            }
        }
        WSWhereOr or = new WSWhereOr(conditions.toArray(new WSWhereItem[conditions.size()]));
        WSWhereItem wi = new WSWhereItem(null, null, or);

        return wi;
    }

    public static WSWhereItem buildWhereItem(String criteria) throws Exception {
        WSWhereItem wi;
        String[] filters = criteria.split(" ");
        String filterXpaths, filterOperators, filterValues;

        filterXpaths = filters[0];
        filterOperators = filters[1];
        if (filters.length <= 2)
            filterValues = " ";
        else
            filterValues = filters[2];

        if (filterXpaths == null || filterXpaths.trim().equals(""))
            return null;

        WSWhereCondition wc = new WSWhereCondition(filterXpaths, Util.getOperator(filterOperators), filterValues,
                WSStringPredicate.NONE, false);
        ArrayList<WSWhereItem> conditions = new ArrayList<WSWhereItem>();
        WSWhereItem item = new WSWhereItem(wc, null, null);
        conditions.add(item);

        if (conditions.size() == 0) {
            wi = null;
        } else {
            WSWhereAnd and = new WSWhereAnd(conditions.toArray(new WSWhereItem[conditions.size()]));
            wi = new WSWhereItem(null, and, null);
        }

        return wi;

    }

    public static String getForeignKeyList(int start, int limit, String value, String xpathForeignKey,
            String xpathInfoForeignKey, String fkFilter, boolean isCount) throws RemoteException, Exception {
        if (xpathForeignKey == null)
            return null;
        String initxpathForeignKey = "";
        initxpathForeignKey = getForeignPathFromPath(xpathForeignKey);

        WSWhereCondition whereCondition = getConditionFromPath(xpathForeignKey);
        WSWhereItem whereItem = null;
        if (whereCondition != null) {
            whereItem = new WSWhereItem(whereCondition, null, null);
        }

        // get FK filter
        WSWhereItem fkFilterWi = null;
        fkFilterWi = getConditionFromFKFilter(fkFilter);
        if (fkFilterWi != null)
            whereItem = fkFilterWi;
        Configuration config = Configuration.getInstance();
        // aiming modify there is bug when initxpathForeignKey when it's like 'conceptname/key'
        // so we convert initxpathForeignKey to 'conceptname'
        initxpathForeignKey = initxpathForeignKey.split("/")[0];
        // end
        xpathInfoForeignKey = xpathInfoForeignKey == null ? "" : xpathInfoForeignKey;
        // foreign key set by business concept
        if (initxpathForeignKey.split("/").length == 1) {
            String conceptName = initxpathForeignKey;

            // determine if we have xPath Infos: e.g. labels to display
            String[] xpathInfos = new String[1];
            if (!"".equals(xpathInfoForeignKey) && xpathInfoForeignKey != null)
                xpathInfos = xpathInfoForeignKey.split(",");
            else
                xpathInfos[0] = conceptName;
            // aiming add .* to value
            value = value == null ? "" : value;
            // end
            // build query - add a content condition on the pivot if we search for a particular value
            String filteredConcept = conceptName;
            // hshu fix bug 0013849: Lazy loading of FK picker always get records from the first 20 records of all
            // records
            if (value != null && !"".equals(value.trim()) && !".*".equals(value.trim())) {
                List<WSWhereItem> condition = new ArrayList<WSWhereItem>();
                if (whereItem != null)
                    condition.add(whereItem);
                WSWhereItem wc = null;
                // hshu fixed the regression: the filter works only on FK, not on FK Info.
                // if(xpathForeignKey.equals(conceptName))wc=buildWhereItem(conceptName+"/. CONTAINS "+value);
                // else wc=buildWhereItem(xpathForeignKey+" CONTAINS "+value);
                String strConcept = conceptName + "/. CONTAINS ";
                if (MDMConfiguration.getDBType().getName().equals(EDBType.QIZX.getName())) {
                    strConcept = conceptName + "//* CONTAINS ";
                }
                wc = buildWhereItem(strConcept + value);
                condition.add(wc);
                WSWhereAnd and = new WSWhereAnd(condition.toArray(new WSWhereItem[condition.size()]));
                WSWhereItem whand = new WSWhereItem(null, and, null);
                if (whand != null)
                    whereItem = whand;
            }
            // boolean isKey = false;
            // StringBuffer sb = new StringBuffer();
            //
            // if(value!=null && !"".equals(value.trim())) {
            // Pattern p = Pattern.compile("\\[(.*?)\\]");
            // Matcher m = p.matcher(value);
            //
            // while(m.find()){//key
            // sb = sb.append("[matches(. , \""+m.group(1)+"\", \"i\")]");
            // if(EDBType.ORACLE.getName().equals(MDMConfiguration.getDBType().getName()))
            // sb = sb.append("[ora:matches(. , \""+m.group(1)+"\", \"i\")]");
            // isKey = true;
            // }
            // if(isKey)
            // filteredConcept += sb.toString();
            // else{
            // value=value.equals(".*")? "":value+".*";
            // //Value is unlikely to be in attributes
            // filteredConcept+="[matches(. , \""+value+"\", \"i\")]";
            // if(EDBType.ORACLE.getName().equals(MDMConfiguration.getDBType().getName())) {
            // filteredConcept+="[ora:matches(. , \""+value+"\", \"i\")]";
            // }
            // }
            // }

            // add the xPath Infos Path
            ArrayList<String> xPaths = new ArrayList<String>();
            for (int i = 0; i < xpathInfos.length; i++) {
                xPaths.add(xpathInfos[i].replaceFirst(conceptName, filteredConcept));
            }
            // add the key paths last, since there may be multiple keys
            xPaths.add(filteredConcept + "/../../i");
            // order by
            String orderbyPath = null;
            if (!"".equals(xpathInfoForeignKey) && xpathInfoForeignKey != null) {
                orderbyPath = xpathInfos[0].replaceFirst(conceptName, filteredConcept);
            } else {

            }

            // Run the query
            String[] results = null;
            if (!isCustomFilter(fkFilter)) {
                results = getPort().xPathsSearch(
                        new WSXPathsSearch(new WSDataClusterPK(config.getCluster()), null, new WSStringArray(xPaths
                                .toArray(new String[xPaths.size()])), whereItem, -1, start, limit, orderbyPath, null))
                        .getStrings();
            } else {
                results = getPort().getItemsByCustomFKFilters(
                        new WSGetItemsByCustomFKFilters(new WSDataClusterPK(config.getCluster()), conceptName, new WSStringArray(
                                xPaths.toArray(new String[xPaths.size()])), getInjectedXpath(fkFilter), start, limit,
                                orderbyPath, null)).getStrings();
            }

            if (results == null)
                results = new String[0];

            JSONObject json = new JSONObject();
            // json.put("count", results.length);

            JSONArray rows = new JSONArray();
            json.put("rows", rows);

            // add (?i) to incasesensitive
            // parse the results - each result contains the xPathInfo values, followed by the keys
            // the first row is totalCount
            for (int i = 0; i < results.length; i++) {
                // process no infos case
                if (!results[i].startsWith("<result>")) {
                    results[i] = "<result>" + results[i] + "</result>";
                }
                results[i] = results[i].replaceAll("\\n", "");// replace \n
                results[i] = results[i].replaceAll(">(\\s+)<", "><"); // replace spaces between elements
                Element root = parse(results[i]).getDocumentElement();
                NodeList list = root.getChildNodes();

                // recover keys - which are last
                String keys = "";
                for (int j = "".equals(xpathInfoForeignKey) ? 1 : xpathInfos.length; j < list.getLength(); j++) {
                    Node textNode = list.item(j).getFirstChild();
                    keys += "[" + (textNode == null ? "" : textNode.getNodeValue()) + "]";
                }

                // recover xPathInfos
                String infos = null;

                // if no xPath Infos given, use the key values
                if (xpathInfos.length == 0 || "".equals(xpathInfoForeignKey) || xpathInfoForeignKey == null) {
                    infos = keys;
                } else {
                    // build a dash separated string of xPath Infos
                    for (int j = 0; j < xpathInfos.length; j++) {
                        infos = (infos == null ? "" : infos + "-");
                        Node textNode = list.item(j).getFirstChild();
                        infos += textNode == null ? "" : textNode.getNodeValue();
                    }
                }

                if ((keys.equals("[]") || keys.equals("")) && (infos.equals("") || infos.equals("[]"))) {
                    // empty row
                } else {
                    JSONObject row = new JSONObject();
                    row.put("keys", keys);
                    row.put("infos", infos);
                    rows.put(row);
                }
            }
            // edit by ymli; fix the bug:0011918: set the pageSize correctly.
            if (isCount) {
                json.put("count", countForeignKey_filter(xpathForeignKey, fkFilter));
            }

            return json.toString();
        }

        throw new Exception("this should not happen");

    }

    public static String countForeignKey_filter(String xpathForeignKey, String fkFilter) throws Exception {
        Configuration config = Configuration.getInstance();
        String conceptName = getConceptFromPath(xpathForeignKey);

        boolean isCustom = isCustomFilter(fkFilter);

        String count = "0";

        if (!isCustom) {
            WSWhereCondition whereCondition = getConditionFromPath(xpathForeignKey);
            WSWhereItem whereItem = null;
            if (whereCondition != null) {
                whereItem = new WSWhereItem(whereCondition, null, null);
            }
            // get FK filter
            WSWhereItem fkFilterWi = getConditionFromFKFilter(fkFilter);
            if (fkFilterWi != null)
                whereItem = fkFilterWi;

            count = getPort().count(new WSCount(new WSDataClusterPK(config.getCluster()), conceptName, whereItem,// null,
                    -1)).getValue();
        } else {
            String injectedXpath = getInjectedXpath(fkFilter);

            count = getPort().countItemsByCustomFKFilters(
                    new WSCountItemsByCustomFKFilters(new WSDataClusterPK(config.getCluster()), conceptName, injectedXpath))
                    .getValue();
        }

        return count;

    }

    private static String getInjectedXpath(String fkFilter) {
        String injectedXpath = null;
        injectedXpath = fkFilter.substring(6);
        return injectedXpath;
    }

    public static boolean isCustomFilter(String fkFilter) {
        boolean isCustom = false;
        if (fkFilter != null && fkFilter.startsWith("$CFFP:"))
            isCustom = true;
        return isCustom;
    }

    /**
     * @author ymli
     * @param type
     * @param value
     * @return
     * @throws ParseException Byte, Short, Integer, and Long Float and Double date,time
     */

    public static Object getTypeValue(String lang, String type, String value) throws ParseException {

        // time
        if (type.equals("date"))
            return Date.parseDate(value.trim()).toCalendar();
        /*
         * else if(type.equals("time")) return BigInteger.//Time.parseTime(value.trim());
         */
        /*
         * else if(type.equals("dateTime")) return DateTime
         */
        // numberic
        /*
         * else if(type.equals("byte")||type.equals("short")||type.equals("int")
         * ||type.equals("integer")||type.equals("long")||type.equals("float")||type.equals("double")){ Locale locale =
         * new Locale(lang); NumberFormat instance = DecimalFormat.getNumberInstance(locale);
         * instance.setParseIntegerOnly(false); return instance.parse(value);
         * 
         * }
         */

        else if (type.equals("byte"))
            return Byte.valueOf(value);
        else if (type.equals("short"))
            return Short.valueOf(value);
        else if (type.equals("int") || type.equals("integer"))
            return Integer.valueOf(value);
        else if (type.equals("long"))
            return Long.valueOf(value);
        else if (type.equals("float"))
            return Float.valueOf(value);
        else if (type.equals("double"))
            return Double.valueOf(value);
        return null;
    }

    public static boolean isTransformerExist(String transformerPK) {
        try {
            boolean isBeforeSavingTransformerExist = false;
            Collection<TransformerV2POJOPK> wst = com.amalto.core.util.Util.getTransformerV2CtrlLocal().getTransformerPKs("*");
            for (TransformerV2POJOPK id : wst) {
                if (id.getIds()[0].equals(transformerPK)) {
                    isBeforeSavingTransformerExist = true;
                    break;
                }
            }
            return isBeforeSavingTransformerExist;
        } catch (XtentisException e) {
            e.printStackTrace();
        } catch (NamingException e) {
            e.printStackTrace();
        } catch (CreateException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void createOrUpdateNode(String xpath, String value, Document doc) {
        JXPathContext ctx = JXPathContext.newContext(doc);
        AbstractFactory factory = new AbstractFactory() {

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
        ctx.setFactory(factory);
        ctx.createPathAndSetValue(xpath, value);
    }

    public static boolean checkDCAndDM(String dataContainer, String dataModel) {
        Configuration config;

        try {
            config = Configuration.getInstance(true);
            return config.getCluster().equals(dataContainer) && config.getModel().equals(dataModel);
        } catch (Exception e) {
            Logger.getLogger(Util.class).error(e.getMessage(), e);
            return false;
        }
    }
}
