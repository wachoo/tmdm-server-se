// ============================================================================
//
// Copyright (C) 2006-2011 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.mdm.webapp.browserecords.server.defaultrule;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.talend.mdm.commmon.util.datamodel.management.BusinessConcept;
import org.talend.mdm.webapp.base.shared.TypeModel;
import org.talend.mdm.webapp.browserecords.shared.ComplexTypeModel;
import org.talend.mdm.webapp.browserecords.shared.EntityModel;
import org.talend.mdm.webapp.browserecords.shared.ViewBean;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.amalto.webapp.core.util.Util;
import com.amalto.webapp.core.util.XmlUtil;


public class DefVRule {

    private Map<String, TypeModel> metaTypeModels;

    public DefVRule(Map<String, TypeModel> metaTypeModels) throws Exception {
        this.metaTypeModels = metaTypeModels;
    }

    public String setDefaultValue(String xml, String language) throws Exception {
        Document doc = Util.parse(xml);
        org.dom4j.Document doc4j = XmlUtil.parseDocument(doc);
        List<DefaultValueRule> rules = getDefaultValueRules(doc);
        for (DefaultValueRule rule : rules) {
            List nodes = doc4j.selectNodes(rule.getXpath());
            if (nodes != null) {
                for (int i = 0; i < nodes.size(); i++) {
                    org.dom4j.Node node = (org.dom4j.Node) nodes.get(i);
                    if (node.getText() == null || node.getText().trim().length() == 0)
                        node.setText(rule.getValue());
                }
            }
        }
        return doc4j.getRootElement().asXML();
    }

    public Document getDefaultXML(ViewBean viewBean, String language) throws Exception {

        EntityModel entity = viewBean.getBindingEntityModel();
        Map<String, TypeModel> metaDataTypes = entity.getMetaDataTypes();
        TypeModel model = metaDataTypes.get(viewBean.getBindingEntityModel().getConceptName());

        return getSubXML(model, null, language);
    }

    public Document getSubXML(TypeModel typeModel, String realType, String language) throws Exception {

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.newDocument();

        List<Element> list = _getDefaultXML(typeModel, realType, doc, language);
        Element root = list.get(0);
        doc.appendChild(root);

        return doc;
    }

    public List<DefaultValueRule> getDefaultValueRules(Document doc)
            throws Exception {
        String style = genDefaultValueStyle(doc);
        org.dom4j.Document transformedDocumentValue = XmlUtil.styleDocument(doc, style);
        Set<String> xpathes = genDefaultValueRuleXpathes(transformedDocumentValue);
        List<DefaultValueRule> dfRules = new ArrayList<DefaultValueRule>();
        for (String xpath : xpathes) {
            String value = evalDefaultValueRuleResult(transformedDocumentValue, xpath);
            if (value != null) {
                dfRules.add(new DefaultValueRule(xpath, value));
            }
        }
        return dfRules;
    }

    private Set<String> genDefaultValueRuleXpathes(org.dom4j.Document doc) {
        Set<String> xpathes = new HashSet<String>();
        Iterator<String> iter = metaTypeModels.keySet().iterator();

        while (iter.hasNext()) {
            String typePath = iter.next();
            TypeModel tm = metaTypeModels.get(typePath);
            if (tm.getDefaultValueExpression() != null) {
                List nodes = doc.selectNodes(typePath);
                for (int i = 0; i < nodes.size(); i++) {
                    org.dom4j.Node node = (org.dom4j.Node) nodes.get(i);
                    xpathes.add(getRealXPath(node));

                }
            }
        }
        return xpathes;
    }

    public String genDefaultValueStyle(Document doc) throws Exception {

        String style = translateDefaultValueSchema(doc);

        StringBuffer sb = new StringBuffer();
        sb.append("<xsl:stylesheet xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\" xmlns:fn=\"http://www.w3.org/2005/xpath-functions\" xmlns:t=\"http://www.talend.com/2010/MDM\" version=\"2.0\">"); //$NON-NLS-1$
        sb.append("<xsl:output method=\"xml\" indent=\"yes\" omit-xml-declaration=\"yes\"/>"); //$NON-NLS-1$
        sb.append(style);
        sb.append("</xsl:stylesheet>"); //$NON-NLS-1$

        return sb.toString();
    }

    private String translateDefaultValueSchema(Document doc) {
        StringBuffer style = new StringBuffer();
        Element root = doc.getDocumentElement();
        List<TemplateBean> temps = genTemplateBeans(root);

        for (TemplateBean temp : temps) {
            TypeModel tm = temp.getTypeModel();
            DefaultValueRule dfrule = getRule(tm);
            style.append("<xsl:template match=\"" + temp.getXpath() + "\">").append("<xsl:copy>"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            if (dfrule != null) {
                style.append("<xsl:choose> "); //$NON-NLS-1$
                style.append("<xsl:when test=\"not(text())\"> "); //$NON-NLS-1$
                if (isLiteralData(XmlUtil.escapeXml(dfrule.getValue()))) {
                    style.append("<xsl:text>" + Util.stripLeadingAndTrailingQuotes(XmlUtil.escapeXml(dfrule.getValue())) + "</xsl:text>"); //$NON-NLS-1$ //$NON-NLS-2$
                } else {
                    style.append("<xsl:value-of select=\"" + getPureValue(XmlUtil.escapeXml(dfrule.getValue())) + "\"/> "); //$NON-NLS-1$ //$NON-NLS-2$
                }
                style.append("</xsl:when> "); //$NON-NLS-1$                    
                style.append("<xsl:otherwise><xsl:value-of select=\".\"/></xsl:otherwise> "); //$NON-NLS-1$
                style.append("</xsl:choose> "); //$NON-NLS-1$      
            }

            List<TemplateBean> children = temp.getChildrenTemps();
            if (children != null) {
                for (TemplateBean childTemp : children) {
                    TypeModel childType = childTemp.getTypeModel();
                    if (!childType.isSimpleType() || getRule(childType) != null) {
                        style.append("<xsl:apply-templates select=\"" + childTemp.getXpath() + "\"/>"); //$NON-NLS-1$ //$NON-NLS-2$
                    } else {
                        style.append("<xsl:copy-of select=\"" + childType.getName() + "\"/>"); //$NON-NLS-1$ //$NON-NLS-2$
                    }
                }
            }
            style.append("</xsl:copy>") //$NON-NLS-1$ 
                    .append("</xsl:template>"); //$NON-NLS-1$ 
        }
        return style.toString();
    }

    private DefaultValueRule getRule(TypeModel tm) {
        if (tm.getDefaultValueExpression() != null)
            return new DefaultValueRule(BusinessConcept.APPINFO_X_DEFAULT_VALUE_RULE, tm.getDefaultValueExpression());
        else
            return null;
    }

    private String getPureValue(String displayValue) {
        return displayValue.replaceAll("\r\n", "").replaceAll("\n", ""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ 
    }

    private boolean isLiteralData(String input) {
        if (input == null || input.trim().equals("")) //$NON-NLS-1$ 
            return false;
        // detect literal
        if (input.startsWith("\"") && input.endsWith("\"")) //$NON-NLS-1$ //$NON-NLS-2$
            return true;

        return false;
    }

    public String evalDefaultValueRuleResult(org.dom4j.Document transformedDoc, String xPath) {
        if (transformedDoc == null)
            return null;
        org.dom4j.Node node = transformedDoc.selectSingleNode(xPath);
        return node == null ? null : node.getText();
    }

    public List<TemplateBean> genTemplateBeans(Element root) {
        List<TemplateBean> temps = new ArrayList<TemplateBean>();
        String realXpath;
        realXpath = getRealXPath(root);

        String typePath = getTypePath(realXpath);
        TypeModel tm = metaTypeModels.get(typePath);
        if (!tm.isSimpleType() || tm.getDefaultValueExpression() != null) {
            TemplateBean temp = new TemplateBean("/" + realXpath, tm); //$NON-NLS-1$
            temp.setChildrenTemps(getSubTemps(root));
            temps.add(temp);
        }

        NodeList children = root.getChildNodes();
        if (children != null) {
            for (int i = 0; i < children.getLength(); i++) {
                Node child = children.item(i);
                if (child.getNodeType() == Node.ELEMENT_NODE) {
                    temps.addAll(genTemplateBeans((Element) child));

                }
            }
        }


        return temps;
    }

    private List<TemplateBean> getSubTemps(Element el) {
        List<TemplateBean> subTemps = new ArrayList<TemplateBean>();
        NodeList children = el.getChildNodes();
        if (children != null) {
            for (int i = 0; i < children.getLength(); i++) {
                Node child = children.item(i);
                if (child.getNodeType() == Node.ELEMENT_NODE) {
                    String realXpath = getRealXPath((Element) child);
                    String typePath = getTypePath(realXpath);
                    TypeModel tm = metaTypeModels.get(typePath);
                    TemplateBean temp = new TemplateBean("/" + realXpath, tm); //$NON-NLS-1$
                    subTemps.add(temp);
                }
            }
        }
        return subTemps;
    }

    private String getTypePath(String xpath) {
        return xpath.replaceAll("\\[\\d+\\]", ""); //$NON-NLS-1$//$NON-NLS-2$
    }
    
    public org.dom4j.Document mergeDoc(org.dom4j.Document mainDoc, org.dom4j.Document subDoc, String contextPath)
            throws Exception {
        org.dom4j.Element el = (org.dom4j.Element) mainDoc.selectSingleNode(contextPath);
        org.dom4j.Element root = (org.dom4j.Element) subDoc.getRootElement();
        List children = root.elements();
        for (int i = 0;i < children.size();i++){
            org.dom4j.Element child = (org.dom4j.Element) children.get(i);
            root.remove(child);
            el.add(child);
        }
        return mainDoc;
    }

    public Document getSubDoc(org.dom4j.Document mainDoc, String contextPath) throws Exception {
        org.dom4j.Element el = (org.dom4j.Element) mainDoc.selectSingleNode(contextPath);
        if (el.getParent() != null) {
            el.getParent().remove(el);
        }
        org.dom4j.Document doc = DocumentHelper.createDocument(el);
        return parseDocument(doc);
    }

    private String getRealXPath(org.dom4j.Node node) {
        String realXPath = ""; //$NON-NLS-1$
        org.dom4j.Node current = node;
        boolean isFirst = true;
        while (current != null) {
            String name = getNodeNameWithIndex(current);
            current = current.getParent();
            if (isFirst) {
                realXPath = name;
                isFirst = false;
                continue;
            }
            realXPath = name + "/" + realXPath; //$NON-NLS-1$

        }
        return realXPath;
    }

    private String getNodeNameWithIndex(org.dom4j.Node node) {
        org.dom4j.Element parent = node.getParent();
        if (parent != null) {
            List children = parent.selectNodes("./*"); //$NON-NLS-1$
            int index = 0;
            for (int i = 0; i < children.size(); i++) {
                org.dom4j.Node child = (org.dom4j.Node) children.get(i);
                if (child.getName().equals(node.getName())) {
                    index++;
                    if (child == node) {
                        return node.getName() + "[" + index + "]"; //$NON-NLS-1$//$NON-NLS-2$
                    }
                }
            }
        }
        return node.getName();
    }

    private String getRealXPath(Node node) {
        String realXPath = ""; //$NON-NLS-1$
        Node current = node;
        boolean isFirst = true;
        while (current != null && current.getNodeType() == Node.ELEMENT_NODE) {
            String name = getNodeNameWithIndex(current);
            current = current.getParentNode();
            if (isFirst) {
                realXPath = name;
                isFirst = false;
                continue;
            }
            realXPath = name + "/" + realXPath; //$NON-NLS-1$

        }
        return realXPath;
    }

    private String getNodeNameWithIndex(Node node) {
        Node parent = node.getParentNode();
        if (parent != null) {
            NodeList children = parent.getChildNodes();
            int index = 0;
            for (int i = 0; i < children.getLength(); i++) {
                Node child = children.item(i);
                if (child.getNodeType() == Node.ELEMENT_NODE) {
                    if (child.getNodeName().equals(node.getNodeName())) {
                        index++;
                        if (child == node) {
                            return node.getNodeName() + "[" + index + "]"; //$NON-NLS-1$//$NON-NLS-2$
                        }
                    }
                }

            }
        }
        return node.getNodeName();
    }

    public Document parseDocument(org.dom4j.Document doc4j) throws DocumentException {
        org.dom4j.io.DOMWriter d4Writer = new org.dom4j.io.DOMWriter();
        return d4Writer.write(doc4j);
    }

    private List<Element> _getDefaultXML(TypeModel model, String realType, Document doc, String language) {

        List<Element> itemNodes = new ArrayList<Element>();
        if (model.getMinOccurs() > 1 && model.getMaxOccurs() > model.getMinOccurs()) {
            for (int i = 0; i < model.getMinOccurs(); i++) {
                Element el = doc.createElement(model.getName());
                itemNodes.add(el);
                if (model.getForeignkey() != null)
                    break;
            }
        } else {
            Element el = doc.createElement(model.getName());
            itemNodes.add(el);
        }
        if (!model.isSimpleType()) {
            ComplexTypeModel complexModel = (ComplexTypeModel) model;
            ComplexTypeModel realTypeModel = complexModel.getRealType(realType);
            List<TypeModel> children;
            if (realTypeModel != null) {
                children = realTypeModel.getSubTypes();
            } else {
                children = complexModel.getSubTypes();
            }

            for (Element node : itemNodes) {
                if (realTypeModel != null) {
                    node.setAttributeNS("http://www.w3.org/2001/XMLSchema-instance", "xsi:type", realType); //$NON-NLS-1$ //$NON-NLS-2$
                }

                for (TypeModel typeModel : children) {
                    List<Element> els = _getDefaultXML(typeModel, realType, doc, language);
                    for (Element el : els) {
                        node.appendChild(el);
                    }
                }
            }
        }
        return itemNodes;
    }

    class TemplateBean {

        private String xpath = null;

        private TypeModel typeModel = null;

        private List<TemplateBean> childrenTemps = null;

        public TemplateBean(String xpath, TypeModel typeModel) {
            this.xpath = xpath;
            this.typeModel = typeModel;
        }

        public String getXpath() {
            return xpath;
        }

        public void setXpath(String xpath) {
            this.xpath = xpath;
        }

        public TypeModel getTypeModel() {
            return typeModel;
        }

        public void setTypeModel(TypeModel typeModel) {
            this.typeModel = typeModel;
        }

        public List<TemplateBean> getChildrenTemps() {
            return childrenTemps;
        }

        public void setChildrenTemps(List<TemplateBean> childrenTemps) {
            this.childrenTemps = childrenTemps;
        }

    }
}
