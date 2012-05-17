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
package org.talend.mdm.webapp.browserecords.server.defaultrule;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Namespace;
import org.dom4j.Node;
import org.dom4j.QName;
import org.talend.mdm.webapp.base.client.exception.ServiceException;
import org.talend.mdm.webapp.base.client.model.DataTypeConstants;
import org.talend.mdm.webapp.base.shared.ExpressionUtil;
import org.talend.mdm.webapp.base.shared.TypeModel;
import org.talend.mdm.webapp.browserecords.shared.ComplexTypeModel;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.amalto.webapp.core.util.XmlUtil;

@Deprecated
public class DefVRule {

    private static final Logger LOG = Logger.getLogger(DefVRule.class);

    private Namespace namespace = new Namespace("xsi", "http://www.w3.org/2001/XMLSchema-instance"); //$NON-NLS-1$//$NON-NLS-2$

    private Map<String, TypeModel> types;

    public DefVRule(Map<String, TypeModel> types) {
        this.types = types;
    }

    public List<TypeModel> orderProcess(org.dom4j.Document doc4j) {
        List<TypeModel> hasBeenProcessed = new ArrayList<TypeModel>();
        Set<TypeModel> stack = new HashSet<TypeModel>();
        Iterator<String> typeIter = types.keySet().iterator();
        while (typeIter.hasNext()) {
            String typePath = typeIter.next();
            TypeModel bean = types.get(typePath);
            if (bean.getDefaultValueExpression() != null) {
                handleProcess(hasBeenProcessed, stack, bean, doc4j);
            }
        }
        return hasBeenProcessed;
    }

    private void handleProcess(List<TypeModel> hasBeenProcessed, Set<TypeModel> stack, TypeModel bean, org.dom4j.Document doc4j) {
        stack.add(bean);
        ExpressionUtil expUtil = new ExpressionUtil(bean.getDefaultValueExpression());
        List<String> typePathes = expUtil.getDepTypes();
        if (typePathes != null && typePathes.size() > 0) {
            for (String typePath : typePathes) {
                if (typePath.startsWith("./") || typePath.startsWith("../")) { //$NON-NLS-1$//$NON-NLS-2$
                    Node node = doc4j.selectSingleNode(bean.getXpath());
                    if (node != null) {
                        node = node.selectSingleNode(typePath);
                        if (node != null) {
                            typePath = getRealTypePath((org.dom4j.Element) node);
                        }
                    }
                }
                TypeModel depExpression = types.get(typePath);
                if (depExpression != null && !stack.contains(depExpression)) {
                    if (depExpression.getDefaultValueExpression() != null) {
                        handleProcess(hasBeenProcessed, stack, depExpression, doc4j);
                    }
                }
            }
        }
        if (!hasBeenProcessed.contains(bean)) {
            hasBeenProcessed.add(bean);
        }
    }

    public void setDefaultValue(String path, String concept, org.dom4j.Document dom4jDoc, String expression)
            throws ServiceException {
        String style = genDefaultValueStyle(concept, path, expression);
        try {
            org.dom4j.Document transformedDocumentValue = XmlUtil.styleDocument(dom4jDoc, style);
            int beginIndex = path.lastIndexOf("/"); //$NON-NLS-1$
            String matchPath = beginIndex != -1 ? path.substring(beginIndex) : path;
            org.dom4j.Node node = transformedDocumentValue.selectSingleNode(concept + "/" + matchPath); //$NON-NLS-1$
            if (node != null && node.getText() != null && node.getText().length() > 0) {
                org.dom4j.Node docNode = dom4jDoc.selectSingleNode(path);
                if (docNode != null)
                    docNode.setText(node.getText());
            }
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            throw new ServiceException(e.getMessage());
        }
    }

    private String genDefaultValueStyle(String concept, String xpath, String valueExpression) {
        StringBuffer style = new StringBuffer();
        style.append("<xsl:stylesheet xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\" xmlns:fn=\"http://www.w3.org/2005/xpath-functions\" xmlns:t=\"http://www.talend.com/2010/MDM\" version=\"2.0\">"); //$NON-NLS-1$

        style.append("<xsl:output method=\"xml\" indent=\"yes\" omit-xml-declaration=\"yes\"/>"); //$NON-NLS-1$
        style.append("<xsl:template match=\"/" + concept + "\">"); //$NON-NLS-1$//$NON-NLS-2$
        style.append("<xsl:copy>"); //$NON-NLS-1$
        style.append("<xsl:apply-templates select=\"/" + xpath + "\"/>"); //$NON-NLS-1$ //$NON-NLS-2$
        style.append("</xsl:copy>"); //$NON-NLS-1$
        style.append("</xsl:template>"); //$NON-NLS-1$

        style.append("<xsl:template match=\"/" + xpath + "\">"); //$NON-NLS-1$ //$NON-NLS-2$
        style.append("<xsl:copy>"); //$NON-NLS-1$
        style.append("<xsl:choose>"); //$NON-NLS-1$
        style.append("<xsl:when test=\"not(text())\">"); //$NON-NLS-1$
        style.append("<xsl:value-of select=\"" + XmlUtil.escapeXml(valueExpression) + "\"/>"); //$NON-NLS-1$ //$NON-NLS-2$
        style.append("</xsl:when> "); //$NON-NLS-1$
        style.append("<xsl:otherwise>"); //$NON-NLS-1$
        style.append("<xsl:value-of select=\".\"/>"); //$NON-NLS-1$
        style.append("</xsl:otherwise>"); //$NON-NLS-1$
        style.append("</xsl:choose> "); //$NON-NLS-1$
        style.append("</xsl:copy>"); //$NON-NLS-1$
        style.append("</xsl:template>"); //$NON-NLS-1$
        style.append("</xsl:stylesheet>"); //$NON-NLS-1$

        return style.toString();
    }

    private String getRealTypePath(org.dom4j.Element el) {
        String realXPath = ""; //$NON-NLS-1$
        org.dom4j.Element current = (org.dom4j.Element) el;
        boolean isFirst = true;
        while (current != null) {
            String name;
            String realType = current.attributeValue(new QName("type", namespace, "xsi:type")); //$NON-NLS-1$ //$NON-NLS-2$
            if (realType != null && realType.trim().length() > 0) {
                name = current.getName() + ":" + realType; //$NON-NLS-1$
            } else {
                name = current.getName();
            }

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

    public Document getSubXML(TypeModel typeModel, String realType, String language) throws ServiceException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.newDocument();
            List<Element> list = _getDefaultXML(typeModel, realType, doc, language);
            Element root = list.get(0);
            doc.appendChild(root);
            return doc;
        } catch (ParserConfigurationException e) {
            LOG.error(e.getMessage(), e);
            throw new ServiceException(e.getMessage());
        }
    }

    public org.dom4j.Document mergeDoc(org.dom4j.Document mainDoc, org.dom4j.Document subDoc, String contextPath) {
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

    public Document getSubDoc(org.dom4j.Document mainDoc, String contextPath) throws ServiceException {
        org.dom4j.Element el = (org.dom4j.Element) mainDoc.selectSingleNode(contextPath);
        if (el.getParent() != null) {
            el.getParent().remove(el);
        }
        org.dom4j.Document doc = DocumentHelper.createDocument(el);
        return parseDocument(doc);
    }

    public Document parseDocument(org.dom4j.Document doc4j) throws ServiceException {
        org.dom4j.io.DOMWriter d4Writer = new org.dom4j.io.DOMWriter();
        try {
            return d4Writer.write(doc4j);
        } catch (DocumentException e) {
            LOG.error(e.getMessage(), e);
            throw new ServiceException(e.getMessage());
        }
    }

    private List<Element> _getDefaultXML(TypeModel model, String realType, Document doc, String language) {
        List<Element> itemNodes = new ArrayList<Element>();
        if (model.getMinOccurs() > 1) {
            for (int i = 0; i < model.getMinOccurs(); i++) {
                Element el = doc.createElement(model.getName());
                applySimpleTypesDefaultValue(model, el);
                itemNodes.add(el);
            }
        } else {
            Element el = doc.createElement(model.getName());
            applySimpleTypesDefaultValue(model, el);
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

    private void applySimpleTypesDefaultValue(TypeModel nodeTypeModel,Element el) {
        
        if (nodeTypeModel != null && el != null) {
            
            if (nodeTypeModel.isSimpleType()) {
                if (nodeTypeModel.getType().equals(DataTypeConstants.BOOLEAN)) {
                    el.setTextContent("false"); //$NON-NLS-1$
                } else if (nodeTypeModel.getType().equals(DataTypeConstants.INT)
                        || nodeTypeModel.getType().equals(DataTypeConstants.INTEGER)
                        || nodeTypeModel.getType().equals(DataTypeConstants.SHORT)
                        || nodeTypeModel.getType().equals(DataTypeConstants.LONG)) {
                    el.setTextContent("0"); //$NON-NLS-1$
                } else if (nodeTypeModel.getType().equals(DataTypeConstants.DECIMAL)
                        || nodeTypeModel.getType().equals(DataTypeConstants.DOUBLE)
                        || nodeTypeModel.getType().equals(DataTypeConstants.FLOAT)) {
                    el.setTextContent("0.0"); //$NON-NLS-1$
                }
                // TODO is there any more?
            }
            
        }
    }


}
