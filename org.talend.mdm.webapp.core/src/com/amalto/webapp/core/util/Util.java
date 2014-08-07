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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.Stack;
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
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.jxpath.AbstractFactory;
import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.jxpath.Pointer;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.log4j.Logger;
import org.exolab.castor.types.Date;
import org.jboss.security.Base64Encoder;
import org.jboss.security.SimpleGroup;
import org.talend.mdm.commmon.util.core.EDBType;
import org.talend.mdm.commmon.util.core.MDMConfiguration;
import org.talend.mdm.commmon.util.datamodel.management.BusinessConcept;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import com.amalto.commons.core.utils.XMLUtils;
import com.amalto.core.delegator.ILocalUser;
import com.amalto.core.ejb.ItemPOJO;
import com.amalto.core.ejb.UpdateReportPOJO;
import com.amalto.core.ejb.local.XmlServerSLWrapperLocal;
import com.amalto.core.objects.transformers.v2.ejb.TransformerV2POJOPK;
import com.amalto.core.objects.universe.ejb.UniversePOJO;
import com.amalto.core.objects.view.ejb.ViewPOJO;
import com.amalto.core.util.LocalUser;
import com.amalto.core.util.XtentisException;
import com.amalto.webapp.core.bean.Configuration;
import com.amalto.webapp.core.bean.UpdateReportItem;
import com.amalto.webapp.core.dmagent.SchemaWebAgent;
import com.amalto.webapp.core.json.JSONArray;
import com.amalto.webapp.core.json.JSONObject;
import com.amalto.webapp.util.webservices.WSBase64KeyValue;
import com.amalto.webapp.util.webservices.WSConceptKey;
import com.amalto.webapp.util.webservices.WSConnectorResponseCode;
import com.amalto.webapp.util.webservices.WSCount;
import com.amalto.webapp.util.webservices.WSCountItemsByCustomFKFilters;
import com.amalto.webapp.util.webservices.WSDataClusterPK;
import com.amalto.webapp.util.webservices.WSDataModelPK;
import com.amalto.webapp.util.webservices.WSGetBusinessConceptKey;
import com.amalto.webapp.util.webservices.WSGetItem;
import com.amalto.webapp.util.webservices.WSGetItemsByCustomFKFilters;
import com.amalto.webapp.util.webservices.WSGetUniverse;
import com.amalto.webapp.util.webservices.WSItem;
import com.amalto.webapp.util.webservices.WSItemPK;
import com.amalto.webapp.util.webservices.WSPutItem;
import com.amalto.webapp.util.webservices.WSRouteItemV2;
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
import com.sun.xml.xsom.XSAnnotation;
import com.sun.xml.xsom.XSElementDecl;

public class Util {

    private static String port = null;

    static {
        port = MDMConfiguration.getHttpPort();
    }

    private static String endpoint_address = "http://localhost:" + port + "/talend/TalendPort"; //$NON-NLS-1$ //$NON-NLS-2$

    private static Pattern TOTAL_COUNT_PATTERN = Pattern.compile("<totalCount>(.*)</totalCount>"); //$NON-NLS-1$

    public static int _AUTO_ = 0;

    public static int _FORCE_RMI_ = 1;

    public static int _FORCE_WEB_SERVICE_ = 2;

    public static String DATE_FORMAT_PREFIX = "%t"; //$NON-NLS-1$  

    private static final String PROVISIONING_CONCEPT = "User"; //$NON-NLS-1$

    private static final String DATACLUSTER_PK = "PROVISIONING"; //$NON-NLS-1$

    public static final String DEFAULT_LANGUAGE = "en"; //$NON-NLS-1$

    private static final Logger LOGGER = Logger.getLogger(Util.class);

    /*********************************************************************
     * WEB SERVICES
     *********************************************************************/

    public static XtentisPort getPort() throws XtentisWebappException {
        AjaxSubject as;
        try {
            as = Util.getAjaxSubject();
        } catch (Exception e) {
            LOGGER.error("Unable to access the logged user data", e); //$NON-NLS-1$
            throw new XtentisWebappException("Unable to access the logged user data"); //$NON-NLS-1$
        }
        if (as == null) {
            throw new XtentisWebappException("Session Expired"); //$NON-NLS-1$
        }
        // org.apache.log4j.Category.getInstance(Util.class).debug("getPort() ");
        String[] mdm = as.getMDMData();
        String url = "http://" + mdm[0] + "/talend/TalendPort"; //$NON-NLS-1$ //$NON-NLS-2$
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
        if (force == _FORCE_RMI_) {
            return getRMIEndPoint();
        }
        if (force == _FORCE_WEB_SERVICE_) {
            return getWSPort(endpointAddress, username, password);
        }
        if (endpointAddress.contains("localhost")) { //$NON-NLS-1$
            return getRMIEndPoint();
        }
        return getWSPort(endpointAddress, username, password);
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
            throw new XtentisWebappException("Unable to access endpoint at: " + endpointAddress + ": " + e.getLocalizedMessage()); //$NON-NLS-1$//$NON-NLS-2$
        }
    }

    private static XtentisPort getRMIEndPoint() throws XtentisWebappException {
        try {
            return (IXtentisRMIPort) Class.forName("com.amalto.webapp.core.util.XtentisRMIPort").newInstance(); //$NON-NLS-1$
        } catch (Exception e) {
            throw new XtentisWebappException(e);
        }
    }

    /*********************************************************************
     * LOCAL FILE UTILS
     *********************************************************************/

    public static String getXML(Class<?> c, String filename) throws Exception {
        BufferedReader in = new BufferedReader(new InputStreamReader(c.getResourceAsStream(filename)));
        String xml = ""; //$NON-NLS-1$
        String line;
        while ((line = in.readLine()) != null) {
            xml += line + "\n"; //$NON-NLS-1$
        }
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
     * @return a single string or null
     */
    public static String joinStrings(String[] strings, String separator) {
        if (strings == null) {
            return null;
        }
        String res = ""; //$NON-NLS-1$
        for (int i = 0; i < strings.length; i++) {
            res += (i > 0) ? separator : ""; //$NON-NLS-1$
            res += strings[i];
        }
        return res;
    }

    /**
     * Returns the first part - eg. the concept - from the path
     * 
     * @return the Concept Name
     */
    public static String getConceptFromPath(String path) {
        if (path == null || path.trim().length() == 0) {
            return null;
        }
        if (path.startsWith("/")) {
            path = path.substring(1);
        }

        Pattern p = Pattern.compile("(.*?)[\\[|/].*"); //$NON-NLS-1$
        if (!path.endsWith("/")) {
            path += "/"; //$NON-NLS-1$
        }
        Matcher m = p.matcher(path);
        if (m.matches()) {
            return m.group(1);
        }
        return null;
    }

    /**
     * Comment method "getFieldFromPath". Returns the last part - eg. the field name - from the path
     */
    public static String getFieldFromPath(String path) {
        String result = null;
        if (path != null) {
            if (path.endsWith("/")) { //$NON-NLS-1$
                path = path.substring(0, path.lastIndexOf("/")); //$NON-NLS-1$
            }
            String[] tmps = path.split("/"); //$NON-NLS-1$
            result = tmps[tmps.length - 1];
        }
        return result;
    }

    public static String getForeignPathFromPath(String path) {
        int pos = path.indexOf("["); //$NON-NLS-1$
        if (pos != -1) {
            return path.substring(0, pos);
        }
        return path;

    }

    public static WSWhereCondition getConditionFromPath(String path) {
        Pattern p = Pattern.compile("(.*?)\\[(.*?)(&=|!=|>=|<=|>|<|=)(.*?)\\].*"); //$NON-NLS-1$
        if (!path.endsWith("/")) { //$NON-NLS-1$
            path += "/"; //$NON-NLS-1$
        }
        Matcher m = p.matcher(path);
        if (m.matches()) {
            WSWhereCondition wc = new WSWhereCondition();
            wc.setLeftPath(m.group(2).trim());
            com.amalto.webapp.util.webservices.WSWhereOperator operator = changeToWSOperator(m.group(3));
            wc.setOperator(operator);
            wc.setRightValueOrPath(m.group(4).trim().replaceAll("'|\"", "")); //$NON-NLS-1$ //$NON-NLS-2$
            wc.setSpellCheck(true);
            wc.setStringPredicate(WSStringPredicate.NONE);
            return wc;
        }
        return null;
    }

    public static WSWhereItem getConditionFromFKFilter(String foreignKey, String foreignKeyInfo, String fkFilter)
            throws Exception {
        return getConditionFromFKFilter(foreignKey, foreignKeyInfo, fkFilter, true);
    }

    public static WSWhereItem getConditionFromFKFilter(String foreignKey, String foreignKeyInfo, String fkFilter,
            boolean formatFkValue) throws Exception {
        if (fkFilter == null || fkFilter.length() == 0) {
            return null;
        }
        if (fkFilter.equals("null")) {
            return null;
        }

        int additionalInfo = fkFilter.indexOf("-", fkFilter.lastIndexOf("#") > -1 ? fkFilter.lastIndexOf("#") + 1 : 0); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        String additionalValue = null;
        if (additionalInfo != -1) {
            additionalValue = fkFilter.substring(additionalInfo + 1);
            fkFilter = fkFilter.substring(0, additionalInfo);
        }
        // Iterator<String> keyInfoIter = bscpt.k
        String[] criterias = fkFilter.split("#"); //$NON-NLS-1$

        ArrayList<WSWhereItem> condition = new ArrayList<WSWhereItem>();
        for (String cria : criterias) {
            String[] values = cria.split("\\$\\$"); //$NON-NLS-1$
            // if (values.length == 3) {
            // values = new String[] { foreignKey, "Contains", values[0] };
            WSWhereCondition wc = Util.convertLine(values);
            if (wc != null) {
                if (formatFkValue) {
                    if (isFkPath(values[0])) {
                        wc.setRightValueOrPath(wrapFkValue(wc.getRightValueOrPath()));
                    } else {
                        wc.setRightValueOrPath(unwrapFkValue(wc.getRightValueOrPath()));
                    }
                }
                condition.add(new WSWhereItem(wc, null, null));
            }
            // }
        }
        if (additionalInfo != -1) {
            String[] keyInfos = (foreignKeyInfo != null && !foreignKeyInfo.trim().isEmpty()) ? foreignKeyInfo.split(",") //$NON-NLS-1$
                    : foreignKey.split(","); //$NON-NLS-1$
            for (String keyInfo : keyInfos) {
                String[] values = new String[] { keyInfo, "Contains", additionalValue.equals(".*") ? "." : additionalValue }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                WSWhereCondition wc = Util.convertLine(values);
                if (wc != null) {
                    condition.add(new WSWhereItem(wc, null, null));
                }
            }
        }

        if (condition.size() > 0) {
            WSWhereItem whereItem;
            if (MDMConfiguration.isSqlDataBase()) {
                whereItem = makeWhereItem(condition);
            } else {
                WSWhereAnd and = new WSWhereAnd(condition.toArray(new WSWhereItem[condition.size()]));
                whereItem = new WSWhereItem(null, and, null);
            }
            return whereItem;
        } else {
            return null;
        }
    }

    public static WSWhereItem makeWhereItem(List<WSWhereItem> conditions) {
        List<Object> conds = new ArrayList<Object>();
        for (int i = 0; i < conditions.size(); i++) {
            WSWhereItem item = conditions.get(i);
            conds.add(item);
            if (i < conditions.size() - 1) {
                String predicate = item.getWhereCondition().getStringPredicate().getValue();
                if (WSStringPredicate.NOT.getValue().equals(predicate)) {
                    predicate = WSStringPredicate.AND.getValue();
                } else if (WSStringPredicate.EXACTLY.getValue().equals(predicate)) {
                    predicate = WSStringPredicate.AND.getValue();
                } else if (WSStringPredicate.STRICTAND.getValue().equals(predicate)) {
                    predicate = WSStringPredicate.AND.getValue();
                } else if (WSStringPredicate.NONE.getValue().equals(predicate)) {
                    predicate = WSStringPredicate.AND.getValue();
                }
                conds.add(predicate);
            }
        }
        Stack<String> stackOp = new Stack<String>();
        List<Object> rpn = new ArrayList<Object>();
        for (Object item : conds) {
            if (item instanceof WSWhereItem) {
                rpn.add(item);
            } else {
                String predicate = (String) item;
                while (!stackOp.isEmpty()) {
                    rpn.add(stackOp.pop());
                }
                stackOp.push(predicate);
            }
        }
        while (!stackOp.isEmpty()) {
            rpn.add(stackOp.pop());
        }

        Stack<WSWhereItem> whereStack = new Stack<WSWhereItem>();
        for (Object o : rpn) {
            if (o instanceof WSWhereItem) {
                whereStack.push((WSWhereItem) o);
            } else if (o instanceof String) {
                if (WSStringPredicate.OR.getValue().equals(o)) {
                    WSWhereItem item1 = whereStack.pop();
                    WSWhereItem item2 = whereStack.pop();
                    WSWhereOr or = new WSWhereOr(new WSWhereItem[] { item2, item1 });
                    whereStack.push(new WSWhereItem(null, null, or));
                } else if (WSStringPredicate.AND.getValue().equals(o)) {
                    WSWhereItem item1 = whereStack.pop();
                    WSWhereItem item2 = whereStack.pop();
                    WSWhereAnd and = new WSWhereAnd(new WSWhereItem[] { item2, item1 });
                    whereStack.push(new WSWhereItem(null, and, null));
                }
            }
        }
        return whereStack.pop();
    }

    public static WSWhereCondition convertLine(String[] values) {
        if (values.length < 3) {
            return null;
        }
        WSWhereCondition wc = new WSWhereCondition();

        wc.setLeftPath(values[0]);

        if (values.length >= 3) {
            WSWhereOperator operator = null;
            if (values[1].equals("Contains")) {
                operator = WSWhereOperator.CONTAINS;
            } else if (values[1].equals("Contains Text Of")) {
                operator = WSWhereOperator.JOIN;
            } else if (values[1].equals("=")) {
                operator = WSWhereOperator.EQUALS;
            } else if (values[1].equals(">")) {
                operator = WSWhereOperator.GREATER_THAN;
            } else if (values[1].equals(">=")) {
                operator = WSWhereOperator.GREATER_THAN_OR_EQUAL;
            } else if (values[1].equals("<")) {
                operator = WSWhereOperator.LOWER_THAN;
            } else if (values[1].equals("<=")) {
                operator = WSWhereOperator.LOWER_THAN_OR_EQUAL;
            } else if (values[1].equals("!=")) {
                operator = WSWhereOperator.NOT_EQUALS;
            } else if (values[1].equals("Starts With")) {
                operator = WSWhereOperator.STARTSWITH;
            } else if (values[1].equals("Strict Contains")) {
                operator = WSWhereOperator.STRICTCONTAINS;
            }
            wc.setOperator(operator);
            if (values[2] != null && values[2].matches("^\".*\"$")) {
                values[2] = values[2].substring(1, values[2].length() - 1);
            }
            wc.setRightValueOrPath(values[2]);
        }

        if (values.length >= 4) {
            WSStringPredicate predicate = null;
            if (values[3].equals("")) {
                predicate = WSStringPredicate.NONE;
            } else if (values[3].equals("Or")) {
                predicate = WSStringPredicate.OR;
            }
            if (values[3].equals("And")) {
                predicate = WSStringPredicate.AND;
            }
            if (values[3].equals("Strict And")) {
                predicate = WSStringPredicate.STRICTAND;
            }
            if (values[3].equals("Exactly")) {
                predicate = WSStringPredicate.EXACTLY;
            }
            if (values[3].equals("Not")) {
                predicate = WSStringPredicate.NOT;
            }
            wc.setStringPredicate(predicate);
        } else {
            wc.setStringPredicate(WSStringPredicate.NONE);
        }

        return wc;
    }

    public static String wrapFkValue(String value) {
        if (value.startsWith("[") && value.endsWith("]")) { //$NON-NLS-1$//$NON-NLS-2$
            return value;
        }
        return "[" + value + "]"; //$NON-NLS-1$ //$NON-NLS-2$
    }

    public static String unwrapFkValue(String value) {
        if (value.startsWith("[") && value.endsWith("]")) { //$NON-NLS-1$ //$NON-NLS-2$
            if (value.contains("][")) { //$NON-NLS-1$
                return value;
            } else {
                return value.substring(1, value.length() - 1);
            }
        }
        return value;
    }

    public static boolean isFkPath(String fkPath) {
        String concept = fkPath.split("/")[0]; //$NON-NLS-1$
        try {
            BusinessConcept businessConcept = SchemaWebAgent.getInstance().getBusinessConcept(concept);
            businessConcept.load();
            Map<String, String> fkMap = businessConcept.getForeignKeyMap();
            if (fkMap != null && fkMap.containsKey("/" + fkPath)) { //$NON-NLS-1$
                return true;
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }

    /**
     * @return a specific value for Simple Type, "" for Complex Type
     * @throws Exception
     */
    public static boolean findXSDSimpleTypeInDocument(Document doc, Node elem, String type, ArrayList<String> typeInfo)
            throws Exception {
        if (type != null && type.trim().equals("")) { //$NON-NLS-1$
            if (Util.getNodeList(elem, ".//xsd:simpleType").getLength() > 0) { //$NON-NLS-1$
                NodeList list = Util.getNodeList(elem, ".//xsd:simpleType/xsd:restriction"); //$NON-NLS-1$
                if (list.getLength() > 0) {
                    if (Util.getNodeList(elem, ".//xsd:simpleType/xsd:restriction/xsd:enumeration").getLength() > 0) { //$NON-NLS-1$
                        NodeList emumList = Util.getNodeList(elem, ".//xsd:simpleType/xsd:restriction/xsd:enumeration"); //$NON-NLS-1$
                        typeInfo.add("enumeration"); //$NON-NLS-1$
                        for (int i = 0; i < emumList.getLength(); i++) {
                            typeInfo.add(emumList.item(i).getAttributes().getNamedItem("value").getNodeValue()); //$NON-NLS-1$
                        }
                    } else {
                        typeInfo.add(list.item(0).getAttributes().getNamedItem("base").getNodeValue()); //$NON-NLS-1$
                    }
                }
                return true;
            } else if (Util.getNodeList(elem, "/xsd:complexType").getLength() > 0) { //$NON-NLS-1$
                return false;
            }
        }

        String path = "//xsd:simpleType"; //$NON-NLS-1$
        if (type != null && !type.trim().equals("")) { //$NON-NLS-1$
            path += "[@name='" + type + "']"; //$NON-NLS-1$ //$NON-NLS-2$
        }
        if (Util.getNodeList(doc, path).getLength() > 0) {
            Node node = Util.getNodeList(doc, path).item(0);
            if (Util.getNodeList(node, "//xsd:restriction").getLength() > 0) { //$NON-NLS-1$
                Node resNode = Util.getNodeList(node, "//xsd:restriction").item(0); //$NON-NLS-1$
                NodeList enumList = Util.getNodeList(resNode, "/xsd:enumeration"); //$NON-NLS-1$
                if (enumList.getLength() > 0) {
                    // enumeration occurs
                    typeInfo.add("enumeration"); //$NON-NLS-1$
                    for (int i = 0; i < enumList.getLength(); i++) {
                        typeInfo.add(enumList.item(i).getAttributes().getNamedItem("value").getNodeValue()); //$NON-NLS-1$
                    }
                } else {
                    typeInfo.add(resNode.getAttributes().getNamedItem("base").getNodeValue()); //$NON-NLS-1$
                }
                return true;
            }
            return false;
        } else {
            NodeList importList;
            for (int nm = 0; nm < 2; nm++) {
                if (nm == 0) {
                    importList = Util.getNodeList(doc, "//xsd:import"); //$NON-NLS-1$
                } else {
                    importList = Util.getNodeList(doc, "//xsd:include"); //$NON-NLS-1$
                }

                for (int i = 0; i < importList.getLength(); i++) {
                    Node schemaLocation = importList.item(i).getAttributes().getNamedItem("schemaLocation"); //$NON-NLS-1$
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

        Pattern httpUrl = Pattern.compile("(http|https|ftp):(\\//|\\\\)(.*):(.*)"); //$NON-NLS-1$
        Matcher match = httpUrl.matcher(xsdLocation);
        Document d;
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
        String user = "", pwd = ""; //$NON-NLS-1$ //$NON-NLS-2$
        try {
            Subject subject = LocalUser.getCurrentSubject();
            Set<Principal> set = subject.getPrincipals();
            for (Principal principal : set) {
                if (principal instanceof Group) {
                    Group group = (Group) principal;
                    if ("Username".equals(group.getName())) { //$NON-NLS-1$
                        if (group.members().hasMoreElements()) {
                            user = group.members().nextElement().getName();
                        }
                    } else if ("Password".equals(group.getName())) { //$NON-NLS-1$
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
        StringBuilder buffer = new StringBuilder();
        String credentials = encoder.encode((user + ":" + pwd).getBytes()); //$NON-NLS-1$

        try {
            URL urlCn = new URL(url);
            URLConnection conn = urlCn.openConnection();
            conn.setAllowUserInteraction(true);
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setRequestProperty("Authorization", "Basic " + credentials); //$NON-NLS-1$ //$NON-NLS-2$
            conn.setRequestProperty("Expect", "100-continue"); //$NON-NLS-1$ //$NON-NLS-2$

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
        if ("=".equals(operator)) {
            return com.amalto.webapp.util.webservices.WSWhereOperator.EQUALS;
        }
        if ("!=".equals(operator)) {
            return com.amalto.webapp.util.webservices.WSWhereOperator.NOT_EQUALS;
        }
        if ("<".equals(operator)) {
            return com.amalto.webapp.util.webservices.WSWhereOperator.LOWER_THAN;
        }
        if ("<=".equals(operator)) {
            return com.amalto.webapp.util.webservices.WSWhereOperator.LOWER_THAN_OR_EQUAL;
        }
        if (">".equals(operator)) {
            return com.amalto.webapp.util.webservices.WSWhereOperator.GREATER_THAN;
        }
        if (">=".equals(operator)) {
            return com.amalto.webapp.util.webservices.WSWhereOperator.GREATER_THAN_OR_EQUAL;
        }
        if ("&=".equals(operator)) {
            return com.amalto.webapp.util.webservices.WSWhereOperator.CONTAINS;
        }
        return null;
    }

    public static Document copyDocument(Document doc) throws Exception {

        TransformerFactory tfactory = TransformerFactory.newInstance();
        Transformer tx = tfactory.newTransformer();
        DOMSource source = new DOMSource(doc);
        DOMResult result = new DOMResult();
        tx.transform(source, result);
        return (Document) result.getNode();

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
        transformer.setOutputProperty("omit-xml-declaration", "yes"); //$NON-NLS-1$ //$NON-NLS-2$
        transformer.setOutputProperty("indent", "yes"); //$NON-NLS-1$ //$NON-NLS-2$
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
        XObject xo = XPathAPI.eval(contextNode, xPath, (namespace == null) ? contextNode : Util.getRootElement("nsholder", //$NON-NLS-1$
                namespace, prefix));
        if (xo.getType() != XObject.CLASS_NODESET) {
            return null;
        }
        return xo.nodelist();
    }

    /**
     * Returns a namespaced root element of a document Useful to create a namespace holder element
     * 
     * @return the root Element
     */
    public static Element getRootElement(String elementName, String namespace, String prefix) throws Exception {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            DocumentBuilder builder = factory.newDocumentBuilder();
            DOMImplementation impl = builder.getDOMImplementation();
            Document namespaceHolder = impl.createDocument(namespace, (prefix == null ? "" : prefix + ":") + elementName, null); //$NON-NLS-1$ //$NON-NLS-2$
            Element rootNS = namespaceHolder.getDocumentElement();
            rootNS.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:" + prefix, namespace); //$NON-NLS-1$ //$NON-NLS-2$
            return rootNS;
        } catch (Exception e) {
            String err = "Error creating a namespace holder document: " + e.getLocalizedMessage(); //$NON-NLS-1$
            throw new Exception(err);
        }
    }

    public static Document parse(String xmlString) throws Exception {
        return parse(xmlString, null);
    }

    public static Document parse(String xmlString, String schema) throws Exception {
        // parse
        Document d;
        SAXErrorHandler seh = new SAXErrorHandler();
        try {
            // initialize the sax parser which uses Xerces
            System.setProperty("javax.xml.parsers.DocumentBuilderFactory", "org.apache.xerces.jaxp.DocumentBuilderFactoryImpl"); //$NON-NLS-1$ //$NON-NLS-2$
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            // Schema validation based on schemaURL
            factory.setNamespaceAware(true);
            factory.setValidating((schema != null));
            factory.setAttribute("http://java.sun.com/xml/jaxp/properties/schemaLanguage", "http://www.w3.org/2001/XMLSchema"); //$NON-NLS-1$ //$NON-NLS-2$
            if (schema != null) {
                factory.setAttribute("http://java.sun.com/xml/jaxp/properties/schemaSource", new InputSource(new StringReader( //$NON-NLS-1$
                        schema)));
            }
            DocumentBuilder builder;
            builder = factory.newDocumentBuilder();
            builder.setErrorHandler(seh);
            d = builder.parse(new InputSource(new StringReader(xmlString)));
        } catch (Exception e) {
            String err = "Unable to parse the document" + ": " + e.getClass().getName() + ": " + e.getLocalizedMessage() + "\n " //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
                    + xmlString;
            throw new Exception(err);
        }
        // check if document parsed correctly against the schema
        if (schema != null) {
            String errors = seh.getErrors();
            if (!errors.equals("")) { //$NON-NLS-1$
                String err = "Document  did not parse against schema: \n" + errors + "\n" + xmlString; //$NON-NLS-1$//$NON-NLS-2$
                throw new Exception(err);
            }
        }
        return d;
    }

    public static String[] getTextNodes(Node contextNode, String xPath) throws XtentisWebappException {
        return getTextNodes(contextNode, xPath, contextNode);
    }

    public static String[] getTextNodes(Node contextNode, String xPath, Node namespaceNode) throws XtentisWebappException {
        String[] results;
        // test for hard-coded values
        if (xPath.startsWith("\"") && xPath.endsWith("\"")) {
            return new String[] { xPath.substring(1, xPath.length() - 1) };
        }

        // test for incomplete path (elements missing /text())
        if (!xPath.matches(".*@[^/\\]]+")) {
            if (!xPath.endsWith(")")) {
                xPath += "/text()"; //$NON-NLS-1$
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
            String err = "Unable to get the text node(s) of " + xPath + ": " + e.getClass().getName() + ": " //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                    + e.getLocalizedMessage();
            throw new XtentisWebappException(err);
        }
        return results;
    }

    public static String getFirstTextNode(Node contextNode, String xPath, Node namespaceNode) throws XtentisWebappException {
        String[] res = getTextNodes(contextNode, xPath, namespaceNode);
        if (res.length == 0) {
            return null;
        }
        return res[0];
    }

    /**
     * Get the first text node matching the Xpath
     * 
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
        String SUBJECT_CONTEXT_KEY = "javax.security.auth.Subject.container"; //$NON-NLS-1$
        Subject sub = (Subject) PolicyContext.getContext(SUBJECT_CONTEXT_KEY);

        return new AjaxSubject(sub);

    }

    public static String getPrincipalMember(String key) throws Exception {
        String result = ""; //$NON-NLS-1$
        // Get the Authenticated Subject
        Subject subject = (Subject) PolicyContext.getContext("javax.security.auth.Subject.container"); //$NON-NLS-1$
        // Now look for a Group
        Set<Principal> principals = subject.getPrincipals(Principal.class);
        for (Principal p : principals) {
            if (p instanceof SimpleGroup) {
                SimpleGroup sg = (SimpleGroup) p;
                if (key.equals(sg.getName())) {
                    Enumeration<?> en = sg.members();
                    while (en.hasMoreElements()) {
                        String info = en.nextElement().toString();
                        result = result + "," + info; //$NON-NLS-1$
                    }
                }
            }
        }
        if (result.length() > 0) {
            result = result.substring(1);
        }
        return result;
    }

    public static String getLoginUserName() throws Exception {
        return getPrincipalMember("Username"); //$NON-NLS-1$
    }

    public static String getLoginUniverse() throws Exception {
        return getPrincipalMember("Universe"); //$NON-NLS-1$
    }

    public static String getLoginRoles() throws Exception {
        return getPrincipalMember("Roles"); //$NON-NLS-1$
    }

    public static String getRevisionIdFromUniverse(String universeName, String conceptName) throws Exception {
        WSUniverse wsUniverse = Util.getPort().getUniverse(new WSGetUniverse(new WSUniversePK(universeName)));
        UniversePOJO universe = XConverter.WS2POJO(wsUniverse);
        return universe.getConceptRevisionID(conceptName);
    }

    public static Element getLoginProvisioningFromDB() throws Exception {
        WSItem item = Util.getPort().getItem(
                new WSGetItem(new WSItemPK(new WSDataClusterPK("PROVISIONING"), "User", new String[] { Util //$NON-NLS-1$ //$NON-NLS-2$
                        .getLoginUserName() })));
        String userString = item.getContent();
        return (Element) Util.getNodeList(Util.parse(userString), "//User").item(0);
    }

    public static String getUserDataModel() throws Exception {
        Element item = getLoginProvisioningFromDB();
        NodeList nodeList = Util.getNodeList(item, "//property"); //$NON-NLS-1$
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if ("model".equals(Util.getFirstTextNode(node, "name"))) { //$NON-NLS-1$ //$NON-NLS-2$
                Node fchild = Util.getNodeList(node, "value").item(0).getFirstChild(); //$NON-NLS-1$
                return fchild.getNodeValue();
            }
        }
        return null;
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
            throw new NullPointerException("null text"); //$NON-NLS-1$
        }

        try {
            MessageDigest md = MessageDigest.getInstance("MD5"); //$NON-NLS-1$
            md.update(text.getBytes(charset));
            return md.digest();
        } catch (NoSuchAlgorithmException e) {
            // log.error("Cannot find MD5 algorithm", e);
            throw new RuntimeException("Cannot find MD5 algorithm"); //$NON-NLS-1$
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("No such encoding: " + charset); //$NON-NLS-1$
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
            throw new IllegalArgumentException("byte array must not be null"); //$NON-NLS-1$
        }
        StringBuilder hex = new StringBuilder(bytes.length * 2);
        for (byte aByte : bytes) {
            hex.append(Character.forDigit((aByte & 0XF0) >> 4, 16));
            hex.append(Character.forDigit((aByte & 0X0F), 16));
        }
        return hex.toString();
    }

    /**
     * ****************************************************************** WEB SERVICES
     * *******************************************************************
     */

    public static HashMap<String, Object> getMapFromKeyValues(WSBase64KeyValue[] params) throws RemoteException {
        try {
            HashMap<String, Object> map = new HashMap<String, Object>();
            if (params != null) {
                for (WSBase64KeyValue param : params) {
                    if (param != null) {
                        String key = param.getKey();
                        byte[] bytes = (new BASE64Decoder()).decodeBuffer(param.getBase64StringValue());
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
            throw new RemoteException(e.getClass().getName() + ": " + e.getLocalizedMessage()); //$NON-NLS-1$
        }
    }

    public static WSBase64KeyValue[] getKeyValuesFromMap(HashMap<String, Object> params) throws RemoteException {
        try {
            if (params == null) {
                return null;
            }
            WSBase64KeyValue[] keyValues = new WSBase64KeyValue[params.size()];
            Set<String> keys = params.keySet();
            int i = 0;
            for (String key : keys) {
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
            throw new RemoteException(e.getClass().getName() + ": " + e.getLocalizedMessage()); //$NON-NLS-1$
        }
    }

    public static String getCodeFromWSConnectorResponseCode(WSConnectorResponseCode code) {
        if (code.equals(WSConnectorResponseCode.OK)) {
            return "OK"; //$NON-NLS-1$
        }
        if (code.equals(WSConnectorResponseCode.STOPPED)) {
            return "STOPPED"; //$NON-NLS-1$
        }
        return "ERROR"; //$NON-NLS-1$
    }

    /*********************************************************************
     * VERSIONING
     *********************************************************************/

    private static final String PROP_FILE = "/version.properties"; //$NON-NLS-1$

    /**
     * Returns <code>String</code> representation of package version information.
     */
    public static String getVersion(Class<?> clazz) {
        Properties props = loadProps(clazz);
        return "v" + props.getProperty("major") + "." + props.getProperty("minor") + "." + props.getProperty("rev") + "_" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$
                + props.getProperty("build.number") + " " + props.getProperty("build.date") + " : " //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
                + props.getProperty("description"); //$NON-NLS-1$
    }

    // load props as resource on classpath
    private static Properties loadProps(Class<?> clazz) {
        InputStream is;
        Properties props = new Properties();
        is = clazz.getResourceAsStream(PROP_FILE);
        if (is == null) {
            throw new RuntimeException("Couldn't find: " + PROP_FILE + " on CLASSPATH"); //$NON-NLS-1$ //$NON-NLS-2$
        }
        try {
            props.load(is);
            is.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return props;
    }

    /**
     * store the info of datacluster and datamodel to PROVISIONING.
     */
    public static void storeProvisioning(String username, String xmlString) throws Exception {
        XmlServerSLWrapperLocal server = com.amalto.core.util.Util.getXmlServerCtrlLocal();
        server.start("PROVISIONING"); //$NON-NLS-1$
        server.putDocumentFromString(xmlString, "PROVISIONING" + "." + "User" + "." + username, "PROVISIONING", null); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
        server.commit("PROVISIONING"); //$NON-NLS-1$
        ItemPOJO.clearCache();
    }

    /**
     * @return gives the operator associated to the string 'option'
     */
    public static WSWhereOperator getOperator(String option) {
        WSWhereOperator res = null;
        if (option.equalsIgnoreCase("CONTAINS")) {
            res = WSWhereOperator.CONTAINS;
        } else if (option.equalsIgnoreCase("EQUALS")) {
            res = WSWhereOperator.EQUALS;
        } else if (option.equalsIgnoreCase("GREATER_THAN")) {
            res = WSWhereOperator.GREATER_THAN;
        } else if (option.equalsIgnoreCase("GREATER_THAN_OR_EQUAL")) {
            res = WSWhereOperator.GREATER_THAN_OR_EQUAL;
        } else if (option.equalsIgnoreCase("JOIN")) {
            res = WSWhereOperator.JOIN;
        } else if (option.equalsIgnoreCase("LOWER_THAN")) {
            res = WSWhereOperator.LOWER_THAN;
        } else if (option.equalsIgnoreCase("LOWER_THAN_OR_EQUAL")) {
            res = WSWhereOperator.LOWER_THAN_OR_EQUAL;
        } else if (option.equalsIgnoreCase("NOT_EQUALS")) {
            res = WSWhereOperator.NOT_EQUALS;
        } else if (option.equalsIgnoreCase("STARTSWITH")) {
            res = WSWhereOperator.STARTSWITH;
        } else if (option.equalsIgnoreCase("STRICTCONTAINS")) {
            res = WSWhereOperator.STRICTCONTAINS;
        } else if (option.equalsIgnoreCase("FULLTEXTSEARCH")) {
            res = WSWhereOperator.FULLTEXTSEARCH;
        } else if (option.equalsIgnoreCase("EMPTY_NULL")) {
            res = WSWhereOperator.EMPTY_NULL;
        }
        return res;
    }

    /**
     * check the certain column is digit
     */
    public static boolean checkDigist(ArrayList<String[]> itemsBrowserContent, int col) {
        if (col == -1) {
            return false;
        }
        for (String[] temp : itemsBrowserContent) {
            if (!temp[col].matches("^(-|)[0-9]+(\\.?)[0-9]*$")) {
                return false;
            }
        }
        return true;
    }

    /**
     * sort the ArrayList by col in direction of dir
     */
    public static void sortCollections(ArrayList<String[]> itemsBrowserContent, int col, String dir) {
        System.out.println(dir);
        if (col < 0) {
            return;
        }
        if ("descending".equals(dir)) { //$NON-NLS-1$
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
     */
    public static int getSortCol(String[] columns, String title) {
        int col = -1;
        for (int i = 0; i < columns.length; i++) {
            if (("/" + columns[i]).equals(title)) {
                return i;
            }
        }
        return col;
    }

    public static List<String> getElementValues(Node n) throws Exception {
        List<String> l = new ArrayList<String>();
        NodeList list = n.getChildNodes();
        for (int i = 0; i < list.getLength(); i++) {
            Node node = list.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                String nValue = com.amalto.core.util.Util.getFirstTextNode(node, "."); //$NON-NLS-1$
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
        Pattern p = Pattern.compile("\\((.*)\\)"); //$NON-NLS-1$
        Matcher m = p.matcher(criteria);
        if (m.matches()) {
            criteria = m.group(1);
        }

        String[] criterias = criteria.split("[\\s]+OR[\\s]+"); //$NON-NLS-1$
        ArrayList<WSWhereItem> conditions = new ArrayList<WSWhereItem>();

        for (String cria : criterias) {
            ArrayList<WSWhereItem> condition = new ArrayList<WSWhereItem>();

            m = p.matcher(cria);
            if (m.matches()) {
                cria = m.group(1);
            }
            String[] subCriterias = cria.split("[\\s]+AND[\\s]+"); //$NON-NLS-1$
            // add by ymli; fix the bug: 0011974. remove "(" at the left and ")" at the right
            for (String subCria : subCriterias) {
                if (subCria.startsWith("(")) { //$NON-NLS-1$
                    subCria = subCria.substring(1);
                }
                if (subCria.endsWith(")")) {
                    subCria = subCria.substring(0, subCria.length() - 1);
                }
                WSWhereItem whereItem = buildWhereItem(subCria);
                if (whereItem != null) {
                    condition.add(whereItem);
                }
            }
            if (condition.size() > 0) {
                WSWhereAnd and = new WSWhereAnd(condition.toArray(new WSWhereItem[condition.size()]));
                WSWhereItem whand = new WSWhereItem(null, and, null);
                conditions.add(whand);
            }
        }
        WSWhereOr or = new WSWhereOr(conditions.toArray(new WSWhereItem[conditions.size()]));
        return new WSWhereItem(null, null, or);
    }

    public static WSWhereItem buildWhereItem(String criteria) throws Exception {
        WSWhereItem wi;
        String[] filters = criteria.split(" "); //$NON-NLS-1$
        String filterXpaths, filterOperators, filterValues;

        filterXpaths = filters[0];
        filterOperators = filters[1];
        if (filters.length <= 2) {
            filterValues = " "; //$NON-NLS-1$
        } else if (filters.length == 3) {
            filterValues = filters[2];
        } else {// more than 3 mean filterValues contains whitespace
            StringBuilder sb = new StringBuilder();
            for (int i = 2; i < filters.length; i++) {
                sb.append(filters[i]);
                if (i < filters.length - 1) {
                    sb.append(" ");//$NON-NLS-1$
                }
            }
            filterValues = sb.toString();
        }

        if (filterXpaths == null || filterXpaths.trim().equals("")) {
            return null;
        }

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

    // added by lzhang, for 22001
    // explicit replaced current element with root element
    public static String getFormatedFKInfo(String info, String conceptName) {
        info = info.substring(info.startsWith("/") ? 1 : 0); //$NON-NLS-1$
        String formatedInfo = info;
        if (info.startsWith("./")) {
            formatedInfo = info.replaceFirst("./", conceptName + "/"); //$NON-NLS-1$ //$NON-NLS-2$
        }
        return formatedInfo;
    }

    public static String getForeignKeyList(int start, int limit, String value, String xpathForeignKey,
            String xpathInfoForeignKey, String fkFilter, boolean isCount) throws RemoteException, Exception {
        if (xpathForeignKey == null) {
            return null;
        }
        String initXpathForeignKey = getForeignPathFromPath(xpathForeignKey);

        WSWhereCondition whereCondition = getConditionFromPath(xpathForeignKey);
        WSWhereItem whereItem = null;
        if (whereCondition != null) {
            whereItem = new WSWhereItem(whereCondition, null, null);
        }

        if (!isCustomFilter(fkFilter)) {
            // get FK filter
            WSWhereItem fkFilterWi = getConditionFromFKFilter(xpathForeignKey, xpathInfoForeignKey, fkFilter);
            if (fkFilterWi != null) {
                whereItem = fkFilterWi;
            }
        }

        Configuration config = Configuration.getInstance();
        // aiming modify there is bug when initXpathForeignKey when it's like 'conceptname/key'
        // so we convert initXpathForeignKey to 'conceptname'
        initXpathForeignKey = initXpathForeignKey.split("/")[0]; //$NON-NLS-1$
        // end
        xpathInfoForeignKey = xpathInfoForeignKey == null ? "" : xpathInfoForeignKey; //$NON-NLS-1$
        // foreign key set by business concept
        if (initXpathForeignKey.split("/").length == 1) { //$NON-NLS-1$
            // determine if we have xPath Infos: e.g. labels to display
            String[] xpathInfos = new String[1];
            if (xpathInfoForeignKey.trim().length() != 0) {
                xpathInfos = xpathInfoForeignKey.split(","); //$NON-NLS-1$
            } else {
                xpathInfos[0] = initXpathForeignKey;
            }
            // aiming add .* to value
            value = value == null ? "" : value; //$NON-NLS-1$
            // end
            // build query - add a content condition on the pivot if we search for a particular value
            // hshu fix bug 0013849: Lazy loading of FK picker always get records from the first 20 records of all
            // records
            if (value != null && !"".equals(value.trim()) && !".*".equals(value.trim())) { //$NON-NLS-1$ //$NON-NLS-2$
                List<WSWhereItem> condition = new ArrayList<WSWhereItem>();
                if (whereItem != null) {
                    condition.add(whereItem);
                }
                String queryKeyWord = isCount ? " CONTAINS " : " EQUALS "; //$NON-NLS-1$ //$NON-NLS-2$
                String fkWhere = initXpathForeignKey + "/../*" + queryKeyWord + value; //$NON-NLS-1$ 
                if (xpathInfoForeignKey.trim().length() > 0) {
                    StringBuilder ids = new StringBuilder();
                    String realXpathForeignKey = null; // In studio, ForeignKey = ConceptName, but not ConceptName/Id
                    if (!xpathForeignKey.contains("/")) { //$NON-NLS-1$
                        String[] fks = getBusinessConceptKeys(initXpathForeignKey);
                        if (fks != null && fks.length > 0) {
                            realXpathForeignKey = fks[0];
                            for (int i = 0; i < fks.length; i++) {
                                ids.append(fks[i] + queryKeyWord + value);
                                if (i != fks.length - 1) {
                                    ids.append(" OR "); //$NON-NLS-1$
                                }
                            }
                        }
                    }
                    StringBuilder sb = new StringBuilder();
                    if (isCount) {
                        for (String fkInfo : xpathInfos) {
                            sb.append(
                                    fkInfo.startsWith(".") ? convertAbsolutePath( //$NON-NLS-1$
                                            (realXpathForeignKey != null && realXpathForeignKey.trim().length() > 0) ? realXpathForeignKey
                                                    : xpathForeignKey, fkInfo)
                                            : fkInfo).append(" CONTAINS ").append(value); //$NON-NLS-1$
                            sb.append(" OR "); //$NON-NLS-1$
                        }
                    }
                    if (realXpathForeignKey != null) {
                        sb.append(ids.toString());
                    } else {
                        sb.append(xpathForeignKey).append(queryKeyWord).append(value);
                    }
                    fkWhere = sb.toString();
                }
                WSWhereItem wc = buildWhereItems(fkWhere);
                condition.add(wc);
                WSWhereAnd and = new WSWhereAnd(condition.toArray(new WSWhereItem[condition.size()]));
                WSWhereItem whereAnd = new WSWhereItem(null, and, null);
                if (whereAnd != null) {
                    whereItem = whereAnd;
                }
            }
            // add the xPath Infos Path
            ArrayList<String> xPaths = new ArrayList<String>();
            for (String xpathInfo : xpathInfos) {
                xPaths.add(getFormatedFKInfo(xpathInfo.replaceFirst(initXpathForeignKey, initXpathForeignKey),
                        initXpathForeignKey));
            }
            // add the key paths last, since there may be multiple keys
            xPaths.add(initXpathForeignKey + "/../../i"); //$NON-NLS-1$
            // order by
            String orderByPath = null;
            if (!"".equals(xpathInfoForeignKey) && xpathInfoForeignKey != null) { //$NON-NLS-1$
                orderByPath = getFormatedFKInfo(xpathInfos[0].replaceFirst(initXpathForeignKey, initXpathForeignKey),
                        initXpathForeignKey);
            }
            // Run the query
            String[] results;
            if (!isCustomFilter(fkFilter)) {
                results = getPort().xPathsSearch(
                        new WSXPathsSearch(new WSDataClusterPK(config.getCluster()), null, new WSStringArray(xPaths
                                .toArray(new String[xPaths.size()])), whereItem, -1, start, limit, orderByPath, null, true))
                        .getStrings();
            } else {
                results = getPort().getItemsByCustomFKFilters(
                        new WSGetItemsByCustomFKFilters(new WSDataClusterPK(config.getCluster()), initXpathForeignKey,
                                new WSStringArray(xPaths.toArray(new String[xPaths.size()])), getInjectedXpath(fkFilter), start,
                                limit, orderByPath, null, true, whereItem)).getStrings();
            }
            if (results == null) {
                results = new String[] { "0" }; // No result (count = 0) //$NON-NLS-1$
            }
            JSONObject json = new JSONObject();
            JSONArray rows = new JSONArray();
            json.put("rows", rows); //$NON-NLS-1$
            // add (?i) to incasesensitive
            // parse the results - each result contains the xPathInfo values, followed by the keys
            // the first row is totalCount
            // TMDM-2834: Starts from second result (first one is count).
            for (int i = 1; i < results.length; i++) {
                // process no infos case
                if (!results[i].startsWith("<result>")) { //$NON-NLS-1$
                    results[i] = "<result>" + results[i] + "</result>"; //$NON-NLS-1$ //$NON-NLS-2$
                }
                results[i] = results[i].replaceAll("\\n", "");// replace \n //$NON-NLS-1$ //$NON-NLS-2$
                results[i] = results[i].replaceAll(">(\\s+)<", "><"); // replace spaces between elements //$NON-NLS-1$ //$NON-NLS-2$
                Element root = parse(results[i]).getDocumentElement();

                // recover keys - change the parsing method(It is different order when db is SQL but not XML db)
                String keys = ""; //$NON-NLS-1$
                NodeList nodes = getNodeList(root, "//i"); //$NON-NLS-1$
                if (nodes != null) {
                    // when isCount = false, SQL db result(<result><Name>test</Name><Id>1</Id></result>)
                    if (nodes.getLength() == 0) {
                        nodes = getNodeList(root, xpathForeignKey.split("/")[1]); //$NON-NLS-1$
                    }
                    for (int j = 0; j < nodes.getLength(); j++) {
                        if (nodes.item(j) instanceof Element) {
                            keys += "[" + (nodes.item(j).getTextContent() == null ? "" : nodes.item(j).getTextContent()) + "]"; //$NON-NLS-1$  //$NON-NLS-2$  //$NON-NLS-3$
                        }
                    }
                }

                // recover xPathInfos
                String infos = ""; //$NON-NLS-1$
                // if no xPath Infos given, use the key values
                if (xpathInfos.length == 0 || "".equals(xpathInfoForeignKey) || xpathInfoForeignKey == null) { //$NON-NLS-1$
                    infos = keys;
                } else {
                    // build a dash separated string of xPath Infos
                    for (String xpath : xpathInfos) {
                        String fkInfoValue = getFirstTextNode(root, xpath.split("/")[1]); //$NON-NLS-1$
                        fkInfoValue = fkInfoValue != null && fkInfoValue.trim().length() > 0 ? fkInfoValue : ""; //$NON-NLS-1$
                        if (infos.length() == 0) {
                            infos += fkInfoValue;
                        } else {
                            infos += "-" + fkInfoValue; //$NON-NLS-1$
                        }
                    }
                }

                if ((keys.equals("[]") || keys.equals("")) && (infos.equals("") || infos.equals("[]"))) { //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
                    // empty row
                } else {
                    JSONObject row = new JSONObject();
                    row.put("keys", keys); //$NON-NLS-1$
                    row.put("infos", infos); //$NON-NLS-1$
                    rows.put(row);
                }
            }
            // edit by ymli; fix the bug:0011918: set the pageSize correctly.
            // FIXME: why do you invoke this method twice
            if (isCount) {
                Matcher matcher = TOTAL_COUNT_PATTERN.matcher(results[0]);
                if (matcher.matches()) {
                    String count = matcher.group(1);
                    json.put("count", count); //$NON-NLS-1$
                } else {
                    throw new IllegalArgumentException("Total count '" + results[0] + "' does not match expected format"); //$NON-NLS-1$ //$NON-NLS-2$
                }

            }

            return json.toString();
        }

        throw new Exception("Unexpected concept name: '" + initXpathForeignKey + "'"); //$NON-NLS-1$ //$NON-NLS-2$
    }

    public static String countForeignKey_filter(String xpathForeignKey, String xpathForeignKeyinfo, String fkFilter)
            throws Exception {
        return countForeignKey_filter(xpathForeignKey, xpathForeignKeyinfo, fkFilter, null);
    }

    public static String countForeignKey_filter(String xpathForeignKey, String xpathForeignKeyinfo, String fkFilter,
            String criteriaValue) throws Exception {
        Configuration config = Configuration.getInstance();
        String conceptName = getConceptFromPath(xpathForeignKey);
        boolean isCustom = isCustomFilter(fkFilter);
        String count;
        if (!isCustom) {
            WSWhereCondition whereCondition = getConditionFromPath(xpathForeignKey);
            WSWhereItem whereItem = null;
            if (whereCondition != null) {
                whereItem = new WSWhereItem(whereCondition, null, null);
            }
            // get FK filter
            WSWhereItem fkFilterWi = getConditionFromFKFilter(xpathForeignKey, xpathForeignKeyinfo, fkFilter);
            if (fkFilterWi != null) {
                whereItem = fkFilterWi;
            }

            if (criteriaValue != null && criteriaValue.trim().length() > 0) {
                List<WSWhereItem> condition = new ArrayList<WSWhereItem>();
                if (whereItem != null) {
                    condition.add(whereItem);
                }
                String criteriaCondition;
                if (MDMConfiguration.getDBType().getName().equals(EDBType.QIZX.getName())) {
                    criteriaCondition = conceptName + "/../* CONTAINS "; //$NON-NLS-1$
                } else {
                    criteriaCondition = conceptName + "/../. CONTAINS "; //$NON-NLS-1$
                }
                criteriaCondition += criteriaValue;
                WSWhereItem wc = buildWhereItem(criteriaCondition);
                condition.add(wc);
                WSWhereAnd and = new WSWhereAnd(condition.toArray(new WSWhereItem[condition.size()]));
                WSWhereItem wAnd = new WSWhereItem(null, and, null);
                if (wAnd != null) {
                    whereItem = wAnd;
                }
            }
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

    public static String getInjectedXpath(String fkFilter) {
        return fkFilter.substring(6);
    }

    public static boolean isCustomFilter(String fkFilter) {
        boolean isCustom = false;
        if (fkFilter != null && fkFilter.startsWith("$CFFP:")) {
            isCustom = true;
        }
        return isCustom;
    }

    @SuppressWarnings("deprecation")
    public static String outputValidateDate(String dataValue, String format) throws ParseException {
        Pattern datePtn = Pattern
                .compile("((\\d{1,2}\\/){2}\\d{4})?(\\d{1,2}\\/\\d{4})?(\\d{1,2})?(\\d{1,2}\\/\\d{1,2})?(\\d{4})?((\\d{1,2}\\/)(\\d{1,2}\\/)(\\d{1,4}))?");//$NON-NLS-1$
        Matcher mtn = datePtn.matcher(dataValue);

        if (mtn.matches()) {
            if (mtn.group(1) != null) {
                SimpleDateFormat sdf = new SimpleDateFormat(format.equals("%tD") ? "MM/dd/yyyy" : "dd/MM/yyyy");//$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$
                java.util.Date date = sdf.parse(dataValue);
                sdf.applyPattern("yyyy-MM-dd");//$NON-NLS-1$
                dataValue = sdf.format(date);
                return dataValue;
            }

            java.util.Calendar now = Calendar.getInstance();
            now.setTime(new java.util.Date());

            if (mtn.group(1) == null && mtn.group(3) != null && mtn.group(5) != null) {
                dataValue = "01/" + dataValue;//$NON-NLS-1$
                return outputValidateDate(dataValue, format);
            } else if (mtn.group(3) != null) {
                dataValue = now.get(java.util.Calendar.DAY_OF_MONTH) + "/" + dataValue;//$NON-NLS-1$
                return outputValidateDate(dataValue, format);
            } else if (mtn.group(6) != null) {
                dataValue = now.get(java.util.Calendar.DAY_OF_MONTH)
                        + "/" + (now.get(java.util.Calendar.MONTH) + 1) + "/" + dataValue;//$NON-NLS-1$//$NON-NLS-2$
                return outputValidateDate(dataValue, format);
            } else if (mtn.group(4) != null && mtn.group(5) == null && mtn.group(7) == null) {
                dataValue = dataValue
                        + "/" + (now.get(java.util.Calendar.MONTH) + 1) + "/" + (now.get(java.util.Calendar.YEAR) + 1);//$NON-NLS-1$//$NON-NLS-2$
                return outputValidateDate(dataValue, format);
            } else if (mtn.group(5) != null) {
                dataValue += "/" + (now.get(java.util.Calendar.YEAR) + 1);//$NON-NLS-1$
                return outputValidateDate(dataValue, format);
            } else if (mtn.group(7) != null) {
                String year = 2000
                        + Integer.valueOf(mtn.group(10).startsWith("0") ? mtn.group(10).substring(1) : mtn.group(10)) + "";//$NON-NLS-1$//$NON-NLS-2$
                dataValue = (mtn.group(4).isEmpty() ? "" : mtn.group(4)) + mtn.group(8) + mtn.group(9) + year; //$NON-NLS-1$
                return outputValidateDate(dataValue, format);
            }
        }

        datePtn = Pattern.compile("(\\w*)\\s+(\\w*)\\s+(\\d*)\\s+(\\d{2}:\\d{2}:\\d{2})\\s+(\\w*)\\s+(\\d{4})");//$NON-NLS-1$
        mtn = datePtn.matcher(dataValue);
        if (mtn.matches()) {
            java.util.Calendar now = Calendar.getInstance();
            now.setTimeInMillis(java.util.Date.parse(dataValue));
            dataValue = now.get(java.util.Calendar.DAY_OF_MONTH)
                    + "/" + (now.get(java.util.Calendar.MONTH) + 1) + "/" + (now.get(java.util.Calendar.YEAR));//$NON-NLS-1$//$NON-NLS-2$
            return outputValidateDate(dataValue, format);
        }

        return dataValue;
    }

    /**
     * @author ymli
     * @param type
     * @param value
     * @return
     * @throws ParseException Byte, Short, Integer, and Long Float and Double date,time
     */

    public static Object getTypeValue(String lang, String type, String value, String format) throws ParseException {
        // time
        if (type.equals("date") && !value.equals("")) {//$NON-NLS-1$ //$NON-NLS-2$
            Calendar calendar;
            try {
                calendar = Date.parseDate(value.trim()).toCalendar();
            } catch (Exception ex) {
                calendar = Date.parseDate(outputValidateDate(value, format)).toCalendar();
            }
            return calendar;
        } else if (type.equals("dateTime") && !value.equals("")) { //$NON-NLS-1$ //$NON-NLS-2$
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss"); //$NON-NLS-1$
            Calendar dateTime = Calendar.getInstance();
            dateTime.setTime(sdf.parse(value.trim()));
            return dateTime;
        } else if (type.equals("byte")) {
            return Byte.valueOf(value);
        } else if (type.equals("short")) {
            return Short.valueOf(value);
        } else if (type.equals("int") || type.equals("integer")) {
            return Integer.valueOf(value);
        } else if (type.equals("long")) {
            return Long.valueOf(value);
        } else if (type.equals("float")) {
            return Float.valueOf(value);
        } else if (type.equals("double")) {
            return Double.valueOf(value);
        }
        return null;
    }

    public static boolean isTransformerExist(String transformerPK) {
        try {
            boolean isBeforeSavingTransformerExist = false;
            @SuppressWarnings("unchecked")
            Collection<TransformerV2POJOPK> wst = com.amalto.core.util.Util.getTransformerV2CtrlLocal().getTransformerPKs("*"); //$NON-NLS-1$
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

            @Override
            public boolean createObject(JXPathContext context, Pointer pointer, Object parent, String name, int index) {
                if (parent instanceof Node) {
                    try {
                        Node node = (Node) parent;
                        Document doc1 = node.getOwnerDocument();
                        Element e = doc1.createElement(name);
                        if (index > 0) { // list
                            Pointer p = context.getRelativeContext(pointer).getPointer(name + "[" + (index) + "]"); //$NON-NLS-1$ //$NON-NLS-2$
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

    public static String createUpdateReport(String[] ids, String concept, String operationType,
            HashMap<String, UpdateReportItem> updatedPath) throws Exception {

        String revisionId = null;

        Configuration config = Configuration.getInstance();
        String dataModelPK = config.getModel() == null ? "" : config.getModel(); //$NON-NLS-1$
        String dataClusterPK = config.getCluster() == null ? "" : config.getCluster(); //$NON-NLS-1$

        String username = Util.getLoginUserName();
        String universename = Util.getLoginUniverse();
        if (universename != null && universename.length() > 0) {
            revisionId = Util.getRevisionIdFromUniverse(universename, concept);
        }

        StringBuilder keyBuilder = new StringBuilder();
        if (ids != null) {
            for (int i = 0; i < ids.length; i++) {
                keyBuilder.append(ids[i]);
                if (i != ids.length - 1) {
                    keyBuilder.append("."); //$NON-NLS-1$
                }
            }
        }
        String key = keyBuilder.length() == 0 ? "null" : keyBuilder.toString(); //$NON-NLS-1$

        StringBuilder sb = new StringBuilder();
        sb.append("<Update><UserName>").append(username).append("</UserName><Source>genericUI</Source><TimeInMillis>") //$NON-NLS-1$ //$NON-NLS-2$
                .append(System.currentTimeMillis()).append("</TimeInMillis><OperationType>") //$NON-NLS-1$
                .append(StringEscapeUtils.escapeXml(operationType)).append("</OperationType><RevisionID>").append(revisionId) //$NON-NLS-1$
                .append("</RevisionID><DataCluster>").append(dataClusterPK).append("</DataCluster><DataModel>") //$NON-NLS-1$ //$NON-NLS-2$
                .append(dataModelPK).append("</DataModel><Concept>").append(StringEscapeUtils.escapeXml(concept)) //$NON-NLS-1$
                .append("</Concept><Key>").append(StringEscapeUtils.escapeXml(key)).append("</Key>"); //$NON-NLS-1$ //$NON-NLS-2$

        if (UpdateReportPOJO.OPERATION_TYPE_UPDATE.equals(operationType)) {
            Collection<UpdateReportItem> list = updatedPath.values();
            boolean isUpdate = false;
            for (UpdateReportItem item : list) {
                String oldValue = item.getOldValue() == null ? "" : item.getOldValue(); //$NON-NLS-1$
                String newValue = item.getNewValue() == null ? "" : item.getNewValue(); //$NON-NLS-1$
                if (newValue.equals(oldValue)) {
                    continue;
                }
                sb.append("<Item>   <path>").append(StringEscapeUtils.escapeXml(item.getPath())).append("</path>   <oldValue>")//$NON-NLS-1$ //$NON-NLS-2$
                        .append(StringEscapeUtils.escapeXml(oldValue)).append("</oldValue>   <newValue>")//$NON-NLS-1$
                        .append(StringEscapeUtils.escapeXml(newValue)).append("</newValue></Item>");//$NON-NLS-1$
                isUpdate = true;
            }
            if (!isUpdate) {
                return null;
            }
        }
        sb.append("</Update>");//$NON-NLS-1$
        return sb.toString();
    }

    public static String persistentUpdateReport(String xml2, boolean routeAfterSaving) throws Exception {
        if (xml2 == null) {
            return "OK"; //$NON-NLS-1$
        }

        WSItemPK itemPK = Util.getPort().putItem(
                new WSPutItem(new WSDataClusterPK("UpdateReport"), xml2, new WSDataModelPK("UpdateReport"), false)); //$NON-NLS-1$ //$NON-NLS-2$

        if (routeAfterSaving) {
            Util.getPort().routeItemV2(new WSRouteItemV2(itemPK));
        }

        return "OK"; //$NON-NLS-1$
    }

    public static String stripLeadingAndTrailingQuotes(String str) {

        if (str == null) {
            return ""; //$NON-NLS-1$
        }

        if (str.startsWith("\"")) //$NON-NLS-1$
        {
            str = str.substring(1, str.length());
        }
        if (str.endsWith("\"")) //$NON-NLS-1$
        {
            str = str.substring(0, str.length() - 1);
        }
        return str;
    }

    @Deprecated
    public static String formatDate(String formats, Calendar date) {
        String formatValue = formats;

        if (formats.startsWith(DATE_FORMAT_PREFIX)) {
            while (true) {
                if (!formatValue.contains(DATE_FORMAT_PREFIX)) {
                    break;
                }
                String format = formatValue.substring(formatValue.indexOf(DATE_FORMAT_PREFIX),
                        formatValue.indexOf(DATE_FORMAT_PREFIX) + 3);
                String valueStr = String.format(java.util.Locale.ENGLISH, format, date);
                formatValue = formatValue.replace(format, valueStr);
            }
        } else {
            formatValue = String.format(java.util.Locale.ENGLISH, formats, date);
        }

        return formatValue;
    }

    public static String convertAbsolutePath(String currentPath, String xpath) {
        StringBuilder sb = new StringBuilder();
        String[] ops = xpath.split("/"); //$NON-NLS-1$
        String[] eles = currentPath.split("/"); //$NON-NLS-1$
        int num = 0;
        if (xpath.startsWith("..")) { //$NON-NLS-1$
            for (String op : ops) {
                if (op.equals("..")) { //$NON-NLS-1$
                    num += 1;
                }
            }
            for (int i = 0; i < eles.length - num; i++) {
                sb.append(eles[i]);
                sb.append("/"); //$NON-NLS-1$
            }
        } else if (xpath.startsWith(".")) { //$NON-NLS-1$
            sb.append(eles[0]);
            sb.append("/"); //$NON-NLS-1$
        }
        sb.append(ops[ops.length - 1]);
        return sb.toString();
    }

    public static String[] getBusinessConceptKeys(String concept) throws Exception {
        Configuration config = Configuration.getConfiguration();
        String model = config.getModel();

        WSConceptKey key = getPort().getBusinessConceptKey(new WSGetBusinessConceptKey(new WSDataModelPK(model), concept));
        WSConceptKey copyKey = new WSConceptKey();
        copyKey.setFields((String[]) ArrayUtils.clone(key.getFields()));
        copyKey.setSelector(key.getSelector());

        String[] keys = copyKey.getFields();
        for (int i = 0; i < keys.length; i++) {
            if (".".equals(key.getSelector())) {
                keys[i] = concept + "/" + keys[i]; //$NON-NLS-1$ 
            } else {
                keys[i] = key.getSelector() + keys[i];
            }
        }
        return keys;
    }

    public static String convertDocument2String(Document doc, boolean isContainHead) throws Exception {
        DOMSource domSource = new DOMSource(doc);
        StringWriter writer = new StringWriter();
        StreamResult result = new StreamResult(writer);
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = tf.newTransformer();
        transformer.transform(domSource, result);
        if (isContainHead) {
            return writer.toString();
        } else {
            String xmlString = writer.toString();
            return xmlString.replaceAll("<\\?.*\\?>", ""); //$NON-NLS-1$ //$NON-NLS-2$
        }
    }

    public static boolean causeIs(Throwable throwable, Class<?> cls) {
        Throwable currentCause = throwable;
        while (currentCause != null) {
            if (cls.isInstance(currentCause)) {
                return true;
            }
            currentCause = currentCause.getCause();
        }
        return false;
    }

    public static <T> T cause(Throwable throwable, Class<T> cls) {
        Throwable currentCause = throwable;
        while (currentCause != null) {
            if (cls.isInstance(currentCause)) {
                return (T) currentCause;
            }
            currentCause = currentCause.getCause();
        }
        return null;
    }

    @Deprecated
    public static String getExceptionMessage(String message, String language) {
        if (message == null || message.contains("<msg/>")) {
            return ""; //$NON-NLS-1$
        }
        // Message tip : "<msg>[EN:validate error][FR:validate error]</msg>"
        if (message.contains("<msg>")) { //$NON-NLS-1$
            int index = message.indexOf(language.toUpperCase() + ":"); //$NON-NLS-1$
            if (index != -1) {
                return message.substring(index + language.length() + 1, index + message.substring(index).indexOf("]")); //$NON-NLS-1$
            } else {
                return message.substring(message.indexOf("<msg>"), message.indexOf("</msg>") + 6); //$NON-NLS-1$//$NON-NLS-2$
            }
        }
        return message;
    }

    public static Set<String> getNoAccessRoleSet(XSElementDecl decl) {
        Set<String> roleSet = new HashSet<String>();
        XSAnnotation xsa = decl.getAnnotation();
        if (xsa == null) {
            return roleSet;
        }
        Element el = (Element) xsa.getAnnotation();
        NodeList annotList = el.getChildNodes();
        for (int k = 0; k < annotList.getLength(); k++) {
            if ("appinfo".equals(annotList.item(k).getLocalName())) { //$NON-NLS-1$
                Node source1 = annotList.item(k).getAttributes().getNamedItem("source"); //$NON-NLS-1$         
                if (source1 == null) {
                    continue;
                }
                String appinfoSource = annotList.item(k).getAttributes().getNamedItem("source").getNodeValue(); //$NON-NLS-1$

                if ("X_Hide".equals(appinfoSource)) { //$NON-NLS-1$
                    roleSet.add(annotList.item(k).getFirstChild().getNodeValue());
                }
            }
        }
        return roleSet;
    }

    public static boolean isAuth(Set<String> roleSet) throws Exception {
        String roles = getPrincipalMember("Roles"); //$NON-NLS-1$
        String[] roleArr = roles.split(","); //$NON-NLS-1$
        for (String role : roleArr) {
            if (roleSet.contains(role)) {
                return false;
            }
        }
        return true;
    }

    public static void filterAuthViews(Map<String, String> viewMap) throws Exception {
        if (viewMap == null || viewMap.size() == 0) {
            return;
        }

        if (Webapp.INSTANCE.isEnterpriseVersion()) {
            for (String viewInstanceId : viewMap.keySet()) {
                ILocalUser localUser = LocalUser.getLocalUser();
                if (!localUser.userCanWrite(ViewPOJO.class, viewInstanceId)
                        && !localUser.userCanRead(ViewPOJO.class, viewInstanceId)) {
                    viewMap.remove(viewInstanceId);
                }
            }
        }
    }

    public static String getDefaultLanguage() throws Exception {
        String defaultLanguage = ""; //$NON-NLS-1$
        String userName = Util.getAjaxSubject().getUsername();
        WSItemPK itemPK = new WSItemPK(new WSDataClusterPK(DATACLUSTER_PK), PROVISIONING_CONCEPT, new String[] { userName });
        if (userName != null && userName.length() > 0) {
            Document doc = XMLUtils.parse(Util.getPort().getItem(new WSGetItem(itemPK)).getContent());
            if (doc.getElementsByTagName("language") != null) { //$NON-NLS-1$
                Node language = doc.getElementsByTagName("language").item(0); //$NON-NLS-1$
                if (language != null) {
                    return language.getTextContent();
                }
            }
        }
        return defaultLanguage;
    }
}
