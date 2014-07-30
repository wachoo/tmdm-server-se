package com.amalto.webapp.core.dwr;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeSet;

import javax.security.jacc.PolicyContextException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.TransformerException;

import org.directwebremoting.WebContext;
import org.directwebremoting.WebContextFactory;
import org.talend.mdm.commmon.util.webapp.XObjectType;
import org.talend.mdm.commmon.util.webapp.XSystemObjects;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.amalto.webapp.core.bean.Configuration;
import com.amalto.webapp.core.util.FieldLabel;
import com.amalto.webapp.core.util.Util;
import com.amalto.core.webservice.WSDataClusterPK;
import com.amalto.core.webservice.WSDataModelPK;
import com.amalto.core.webservice.WSGetDataModel;
import com.amalto.core.webservice.WSRegexDataClusterPKs;
import com.amalto.core.webservice.WSRegexDataModelPKs;
import com.sun.org.apache.xpath.internal.XPathAPI;
import com.sun.org.apache.xpath.internal.objects.XObject;
import com.sun.xml.xsom.XSAnnotation;
import com.sun.xml.xsom.XSComplexType;
import com.sun.xml.xsom.XSElementDecl;
import com.sun.xml.xsom.XSParticle;

/**
 * 
 * @author asaintguilhem
 *
 */

public class CommonDWR {

    public static String[] getClusters() {
        try {
            WSDataClusterPK[] wsDataClustersPK = Util.getPort().getDataClusterPKs(new WSRegexDataClusterPKs("*"))
                    .getWsDataClusterPKs();
            ArrayList<String> list = new ArrayList<String>();
            filterSystemClustersPK(wsDataClustersPK, list);
            return list.toArray(new String[list.size()]);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String[] getModels() {
        try {
            WSDataModelPK[] wsDataModelsPK = Util.getPort().getDataModelPKs(new WSRegexDataModelPKs("*")).getWsDataModelPKs();
            ArrayList<String> list = new ArrayList<String>();
            filterSystemDataModelsPK(wsDataModelsPK, list);
            return list.toArray(new String[list.size()]);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static void filterSystemClustersPK(WSDataClusterPK[] wsDataClustersPK, ArrayList<String> list) {
        Map<String, XSystemObjects> xDataClustersMap = XSystemObjects.getXSystemObjects(XObjectType.DATA_CLUSTER);
        for (WSDataClusterPK aWsDataClustersPK : wsDataClustersPK) {
            if (!XSystemObjects.isXSystemObject(xDataClustersMap, aWsDataClustersPK.getPk())) {
                list.add(aWsDataClustersPK.getPk());
            }
        }
    }

    private static void filterSystemDataModelsPK(WSDataModelPK[] wsDataModelsPK, ArrayList<String> list) {
        Map<String, XSystemObjects> xDataModelsMap = XSystemObjects.getXSystemObjects(XObjectType.DATA_MODEL);
        for (WSDataModelPK aWsDataModelsPK : wsDataModelsPK) {
            if (!XSystemObjects.isXSystemObject(xDataModelsMap, aWsDataModelsPK.getPk())) {
                list.add(aWsDataModelsPK.getPk());
            }
        }
    }

    public static String getConceptLabel(String dataModelPK, String concept, String language) throws Exception {
        String x_Label = (new FieldLabel(language)).getLabelAnnotation();
        Map<String, XSElementDecl> map = getConceptMap(dataModelPK);
        return getLabel(map.get(concept), x_Label).equals("") ? map.get(concept).getName() : getLabel(map.get(concept), x_Label);
    }

    public static HashMap<String, String> getFieldsByDataModel(String dataModelPK, String concept, String language,
            boolean includeComplex) throws Exception {
        return getFieldsByDataModel(dataModelPK, concept, language, includeComplex, false);
    }

    public static HashMap<String, String> getFieldsByDataModel(String dataModelPK, String concept, String language,
            boolean includeComplex, boolean includeFKReference) throws Exception {
        return getFieldsByDataModel(dataModelPK, null, concept, language, includeComplex, includeFKReference);
    }

    public static HashMap<String, String> getFieldsByDataModel(String dataModelPK, Map<String, XSElementDecl> inputConceptMap,
            String concept, String language, boolean includeComplex, boolean includeFKReference) throws Exception {
        WebContext ctx = WebContextFactory.get();
        String x_Label = (new FieldLabel(language)).getLabelAnnotation();
        Map<String, XSElementDecl> conceptMap;
        if (inputConceptMap == null || inputConceptMap.size() == 0) {
            conceptMap = getConceptMap(dataModelPK);
        } else {
            conceptMap = inputConceptMap;
        }
        XSComplexType xsct = (XSComplexType) (conceptMap.get(concept).getType());
        HashMap<String, String> xpathToLabel = new HashMap<String, String>();
        xpathToLabel.put(concept, getLabel(conceptMap.get(concept), x_Label).equals("") ? conceptMap.get(concept).getName()
                : getLabel(conceptMap.get(concept), x_Label));
        XSParticle[] xsp = xsct.getContentType().asParticle().getTerm().asModelGroup().getChildren();
        for (XSParticle aXsp : xsp) {
            getChildren(aXsp, "" + concept, x_Label, includeComplex, includeFKReference, xpathToLabel);
        }
        // FIXME
        // Why we need to update session attribute in this place?
        // decouple please
        if (ctx != null && ctx.getSession() != null)
            ctx.getSession().setAttribute("xpathToLabel", xpathToLabel);
        return xpathToLabel;
    }

    public static boolean isElementHidden(XSParticle xsp) {
        try {
            ArrayList<String> roles = Util.getAjaxSubject().getRoles();
            XSAnnotation xsa = xsp.getTerm().asElementDecl().getAnnotation();
            if (xsa != null && xsa.getAnnotation() != null) {
                Element el = (Element) xsa.getAnnotation();
                NodeList annotList = el.getChildNodes();
                for (int k = 0; k < annotList.getLength(); k++) {
                    if ("appinfo".equals(annotList.item(k).getLocalName())) {
                        Node source = annotList.item(k).getAttributes().getNamedItem("source");
                        if (source == null)
                            continue;
                        String appinfoSource = annotList.item(k).getAttributes().getNamedItem("source").getNodeValue();
                        if ("X_Hide".equals(appinfoSource)) {
                            if (roles.contains(annotList.item(k).getFirstChild().getNodeValue())) {
                                return true;
                            }
                        }
                    }
                }
            }
        } catch (PolicyContextException e) {
            e.printStackTrace();
        }
        return false;
    }

    private static void getChildren(XSParticle xsp, String xpathParent, String x_Label, boolean includeComplex,
            boolean includeFKReference, HashMap<String, String> xpathToLabel) {
        // aiming added see 0009563
        if (xsp.getTerm().asModelGroup() != null) { // is complex type
            XSParticle[] xsps = xsp.getTerm().asModelGroup().getChildren();
            for (XSParticle xsp1 : xsps) {
                getChildren(xsp1, xpathParent, x_Label, includeComplex, includeFKReference, xpathToLabel);
            }
        }
        if (xsp.getTerm().asElementDecl() == null)
            return;
        // end
        if (!xsp.getTerm().asElementDecl().getType().isComplexType() || includeComplex) {
            // Hidden the NO_ACCESS elment
            if (isElementHidden(xsp)) {
                return;
            }
            String toPutKey = xpathParent + "/" + xsp.getTerm().asElementDecl().getName();
            if (includeFKReference) {
                String foreignkeyPath = getForeignkeyPath(xsp.getTerm().asElementDecl());
                if (foreignkeyPath != null)
                    toPutKey += "@FK_" + foreignkeyPath;
            }
            // FIXME:USE XPATH WITHOUT CONCEPT AS LABEL, MAYBE CAUSE SOME BUGS ON OLD INVOKING PLACES
            String xlabel = "";
            if (getLabel(xsp.getTerm().asElementDecl(), x_Label).equals("")) {
                xlabel = xpathParent + "/" + xsp.getTerm().asElementDecl().getName();
                if (xlabel.contains("/")) {
                    xlabel = xlabel.substring(xlabel.indexOf("/") + 1);
                }
            } else {
                xlabel = getLabel(xsp.getTerm().asElementDecl(), x_Label);
            }

            xpathToLabel.put(toPutKey, xlabel);
        }
        if (xsp.getTerm().asElementDecl().getType().isComplexType()) {
            XSParticle particle = xsp.getTerm().asElementDecl().getType().asComplexType().getContentType().asParticle();
            if (particle != null) {
                XSParticle[] xsps = particle.getTerm().asModelGroup().getChildren();
                for (XSParticle xsp1 : xsps) {
                    getChildren(xsp1, xpathParent + "/" + xsp.getTerm().asElementDecl().getName(), x_Label, includeComplex,
                            includeFKReference, xpathToLabel);
                }
            }
        }
    }

    private static String getForeignkeyPath(XSElementDecl elementDecl) {
        String foreignkeyPath = null;
        // annotation support
        XSAnnotation xsa = elementDecl.getAnnotation();
        if (xsa != null && xsa.getAnnotation() != null) {
            Element el = (Element) xsa.getAnnotation();
            NodeList annotList = el.getChildNodes();
            for (int k = 0; k < annotList.getLength(); k++) {
                if ("appinfo".equals(annotList.item(k).getLocalName())) {
                    Node source = annotList.item(k).getAttributes().getNamedItem("source");
                    if (source == null)
                        continue;
                    String appinfoSource = annotList.item(k).getAttributes().getNamedItem("source").getNodeValue();
                    if ("X_ForeignKey".equals(appinfoSource)) {
                        foreignkeyPath = annotList.item(k).getFirstChild().getNodeValue();
                        break;
                    }
                }
            }
        }

        return foreignkeyPath;

    }

    public static String[] getBusinessConceptKeyPaths(String dataModelPK, String businessConceptName) throws Exception {

        String[] keyPaths = null;

        try {
            String xsdXml = Util.getPort().getDataModel(new WSGetDataModel(new WSDataModelPK(dataModelPK))).getXsdSchema();
            Document xsd = Util.parse(xsdXml);

            String selector = null;
            String[] fields = null;
            selector = Util.getTextNodes(xsd.getDocumentElement(), "xsd:element/xsd:unique[@name='" + businessConceptName
                    + "']/xsd:selector/@xpath", getRootElement("nsholder", xsd.getDocumentElement().getNamespaceURI(), "xsd"))[0];

            fields = Util.getTextNodes(xsd.getDocumentElement(), "xsd:element/xsd:unique[@name='" + businessConceptName
                    + "']/xsd:field/@xpath", getRootElement("nsholder", xsd.getDocumentElement().getNamespaceURI(), "xsd"));

            String prefixPath = "";
            if (selector != null) {
                if (selector.length() > 0 && !selector.equals("."))
                    prefixPath = selector + "/";
            }
            keyPaths = new String[fields.length];
            for (int i = 0; i < keyPaths.length; i++) {
                keyPaths[i] = businessConceptName + "/" + prefixPath + fields[i];
            }

        } catch (TransformerException e) {
            String err = "Unable to get the keys for the Business Concept " + businessConceptName + ": "
                    + e.getLocalizedMessage();
            throw new TransformerException(err);
        } catch (RemoteException e) {
            e.printStackTrace();
            throw new RemoteException();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return keyPaths;
    }

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
            throw new TransformerException(err);
        }
        return rootNS;
    }

    private static String getLabel(XSElementDecl xsed, String x_Label) {
        String label = "";
        try {
            XSAnnotation xsa = xsed.getAnnotation();
            Element el = (Element) xsa.getAnnotation();
            NodeList list = el.getChildNodes();
            for (int k = 0; k < list.getLength(); k++) {
                if ("appinfo".equals(list.item(k).getLocalName())) {
                    Node source = list.item(k).getAttributes().getNamedItem("source");
                    if (source == null)
                        continue;
                    String appinfoSource = source.getNodeValue();
                    if (x_Label.equals(appinfoSource)) {
                        label = list.item(k).getFirstChild().getNodeValue();
                        // System.out.println("xlabel found :"+label);
                        break;
                    }
                }
            }
        } catch (Exception e) {
        }
        return label;
    }

    public static Map<String, XSElementDecl> getConceptMap(String dataModelPK) throws Exception {
        String xsd = Util.getPort().getDataModel(new WSGetDataModel(new WSDataModelPK(dataModelPK))).getXsdSchema();
        return com.amalto.core.util.Util.getConceptMap(xsd);
    }

    public static String getConceptFromBrowseItemView(String viewPK) {
        String concept = viewPK.replaceAll("Browse_items_", "");
        concept = concept.replaceAll("#.*", "");
        return concept;
    }

    public static String getXMLStringFromDocument(Document d) throws Exception {
        return Util.nodeToString(d.getDocumentElement());
    }

    public static NodeList getNodeList(Node contextNode, String xPath) throws TransformerException {
        XObject xo = XPathAPI.eval(contextNode, xPath);
        if (xo.getType() != XObject.CLASS_NODESET)
            return null;
        return xo.nodelist();
    }

    public static LinkedHashMap<String, String> getMapSortedByValue(Map<String, String> map) {
        TreeSet<Map.Entry> set = new TreeSet<Map.Entry>(new Comparator() {

            public int compare(Object obj, Object obj1) {
                return ((Comparable) ((Map.Entry) obj).getValue()).compareTo(((Map.Entry) obj1).getValue());
            }
        });
        set.addAll(map.entrySet());
        LinkedHashMap<String, String> sortedMap = new LinkedHashMap<String, String>();
        for (Map.Entry entry : set) {
            sortedMap.put((String) entry.getKey(), (String) entry.getValue());
        }

        return sortedMap;
    }
}
