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
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Namespace;
import org.dom4j.Node;
import org.dom4j.QName;
import org.talend.mdm.webapp.base.shared.ExpressionUtil;
import org.talend.mdm.webapp.base.shared.TypeModel;

import com.amalto.webapp.core.util.XmlUtil;

public class XSLTUtil {
	
	// typePath, expression
    private Namespace namespace = new Namespace("xsi", "http://www.w3.org/2001/XMLSchema-instance"); //$NON-NLS-1$//$NON-NLS-2$
    private Map<String, TypeModel> types;
	
	public XSLTUtil(Map<String, TypeModel> types) {
		this.types = types;
	}
	
    public List<TypeModel> orderProcess(Document doc4j) throws IOException {
        List<TypeModel> hasBeenProcessed = new ArrayList<TypeModel>();
        Set<TypeModel> stack = new HashSet<TypeModel>();
		Iterator<String> typeIter = types.keySet().iterator();
		while (typeIter.hasNext()){
			String typePath = typeIter.next();
            TypeModel bean = types.get(typePath);
            if (bean.getDefaultValueExpression() != null) {
                handleProcess(hasBeenProcessed, stack, bean, doc4j);
            }
		}
		return hasBeenProcessed;
	}
	
    private void handleProcess(List<TypeModel> hasBeenProcessed, Set<TypeModel> stack, TypeModel bean, Document doc4j)
            throws IOException {
		stack.add(bean);
        ExpressionUtil expUtil = new ExpressionUtil(bean.getDefaultValueExpression());
        List<String> typePathes = expUtil.getDepTypes();
		if (typePathes != null && typePathes.size() > 0){
			for (String typePath : typePathes){
                if (typePath.startsWith("./") || typePath.startsWith("../")) { //$NON-NLS-1$//$NON-NLS-2$
                    Node node = doc4j.selectSingleNode(bean.getXpath());
                    if (node != null) {
                        node = node.selectSingleNode(typePath);
                        if (node != null) {
                            //                            typePath = node.getPath().replaceFirst("/", ""); //$NON-NLS-1$ //$NON-NLS-2$
                            typePath = getRealTypePath((Element) node);
                        }
                    }
                }
                TypeModel depExpression = types.get(typePath);
				if (depExpression != null && !stack.contains(depExpression)){
                    if (depExpression.getDefaultValueExpression() != null) {
                        handleProcess(hasBeenProcessed, stack, depExpression, doc4j);
                    }
				}
			}
		}
		if (!hasBeenProcessed.contains(bean)){
			hasBeenProcessed.add(bean);
		}
	}


    public void setDefaultValue(String path, String concept, org.dom4j.Document dom4jDoc, String expression)
            throws Exception {
        String style = genDefaultValueStyle(concept, path, expression);
        org.dom4j.Document transformedDocumentValue = XmlUtil.styleDocument(dom4jDoc, style);
        int beginIndex = path.lastIndexOf("/"); //$NON-NLS-1$
        String matchPath = beginIndex != -1 ? path.substring(beginIndex) : path;
        org.dom4j.Node node = transformedDocumentValue.selectSingleNode(concept + "/" + matchPath); //$NON-NLS-1$
        if (node != null && node.getText() != null && node.getText().length() > 0) {
            org.dom4j.Node docNode = dom4jDoc.selectSingleNode(path);
            if (docNode != null)
                docNode.setText(node.getText());
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

    public String getRealTypePath(Element el) {
        String realXPath = ""; //$NON-NLS-1$
        Element current = (Element) el;
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

}
