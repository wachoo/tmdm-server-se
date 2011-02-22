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
package com.amalto.webapp.v3.itemsbrowser.util;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.dom4j.Document;
import org.talend.mdm.commmon.util.datamodel.management.BusinessConcept;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.amalto.webapp.core.util.XmlUtil;
import com.amalto.webapp.v3.itemsbrowser.bean.DisplayRule;
import com.amalto.webapp.v3.itemsbrowser.bean.TreeNode;
import com.amalto.webapp.v3.itemsbrowser.dwr.ItemsBrowserDWR;
import com.sun.xml.xsom.XSElementDecl;
import com.sun.xml.xsom.XSParticle;


/**
 * DOC HSHU class global comment. Detailled comment
 */
public class DisplayRulesUtil{

    private XSElementDecl root;
    private List<TemplateBean> tmpls;
    
    public DisplayRulesUtil(XSElementDecl root) {
        super();
        this.root = root;
        this.tmpls = new ArrayList<TemplateBean>();
    }

    /**
     * DOC HSHU Comment method "genStyle".
     */
    public String genStyle() {
        
        // translate schema
        String style = translateSchema();

        StringBuffer sb = new StringBuffer();
        sb.append("<xsl:stylesheet xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\" xmlns:t=\"http://www.talend.com/2010/MDM\" version=\"1.0\">");
        sb.append("<xsl:output method=\"xml\" indent=\"yes\" omit-xml-declaration=\"yes\"/>");
        sb.append(style);
        sb.append("</xsl:stylesheet>");

        return sb.toString();

    }

    private String translateSchema() {
        StringBuffer style=new StringBuffer();
        travelXSElement(root);
        
        for (TemplateBean tmp : tmpls) {
            XSElementDecl self=tmp.getSelfElement();
            List<XSElementDecl> children=tmp.getChildrenElements();
            
            style.append("<xsl:template match=\""+self.getName()+"\">")
                 .append("<xsl:copy>");
            
            List<DisplayRule> dspRules = getRules(self);
            for (DisplayRule displayRule : dspRules) {
                String dspType=displayRule.getType();
                if(dspType.equals(BusinessConcept.APPINFO_X_DEFAULT_VALUE_RULE)) {
                    
                    style.append("<xsl:choose> "); 
                    style.append("<xsl:when test=\"not(text())\"> "); 
                    style.append("<xsl:value-of select=\""+displayRule.getValue()+"\"/> "); 
                    style.append("</xsl:when> "); 
                    style.append("<xsl:otherwise><xsl:value-of select=\".\"/></xsl:otherwise> "); 
                    style.append("</xsl:choose> ");
                    
                }else if(dspType.equals(BusinessConcept.APPINFO_X_VISIBLE_RULE)) {
                    
                    style.append("<xsl:if test=\"not("+displayRule.getValue()+")\"> "); 
                    style.append("<xsl:attribute name=\"t:visible\">false</xsl:attribute> "); 
                    style.append("</xsl:if> ");
                    
                }
            }
            
            if(children!=null&&children.size()>0) {
                for (XSElementDecl child : children) {
                    if(child.getType().isComplexType()||hasRules(child))
                      style.append("<xsl:apply-templates select=\""+child.getName()+"\"/>");
                    else 
                      style.append("<xsl:copy-of select=\""+child.getName()+"\"/>");
                } 
            }
            
            style.append("</xsl:copy>")
                 .append("</xsl:template>");
           
        }
        
        return style.toString();
    }
    
    private void travelXSElement(XSElementDecl e) {
        if (e != null) {
            
            if (e.getType().isSimpleType()) {
                
                if(hasRules(e)) {
                    tmpls.add(new TemplateBean(e));
                }
                
                
            }else if (e.getType().isComplexType()) {
                
                TemplateBean templateBean = new TemplateBean(e);
                tmpls.add(templateBean);
                
                XSParticle[] subParticles = e.getType().asComplexType().getContentType().asParticle().getTerm().asModelGroup().getChildren();
                if (subParticles != null) {
                    for (int i = 0; i < subParticles.length; i++) {
                        XSParticle xsParticle = subParticles[i];
                        travelParticle(xsParticle,templateBean);
                    }
                }
                
            }

        }
    }

    private void travelParticle(XSParticle xsParticle,TemplateBean templateBean) {
        if (xsParticle.getTerm().asModelGroup() != null) {
            XSParticle[] xsps = xsParticle.getTerm().asModelGroup().getChildren();
            for (int j = 0; j < xsps.length; j++) {
                travelParticle(xsps[j],templateBean);
            }
        }else if(xsParticle.getTerm().asElementDecl()!=null) {
            XSElementDecl subElement = xsParticle.getTerm().asElementDecl();
            templateBean.addChildElement(subElement);
            travelXSElement(subElement);
        }
    }

    private boolean hasRules(XSElementDecl e) {
        if (e.getAnnotation() != null && e.getAnnotation().getAnnotation() != null) {
            Element annotations = (Element) e.getAnnotation().getAnnotation();
            NodeList annotList = annotations.getChildNodes();
            for (int k = 0; k < annotList.getLength(); k++) {
                if ("appinfo".equals(annotList.item(k).getLocalName())) {
                    Node source = annotList.item(k).getAttributes().getNamedItem("source");
                    if (source == null)
                        continue;
                    String appinfoSource = source.getNodeValue();
                    if (annotList.item(k) != null && annotList.item(k).getFirstChild() != null) {
                        if (appinfoSource.equals(BusinessConcept.APPINFO_X_DEFAULT_VALUE_RULE)||appinfoSource.equals(BusinessConcept.APPINFO_X_VISIBLE_RULE)) {
                            return true;
                        } 
                    }

                }
            }
        }
        return false;
    }
    
    private List<DisplayRule> getRules(XSElementDecl e) {
        
        List<DisplayRule> displayRules=new ArrayList<DisplayRule>();
        if (e.getAnnotation() != null && e.getAnnotation().getAnnotation() != null) {
            Element annotations = (Element) e.getAnnotation().getAnnotation();
            NodeList annotList = annotations.getChildNodes();
            for (int k = 0; k < annotList.getLength(); k++) {
                if ("appinfo".equals(annotList.item(k).getLocalName())) {
                    Node source = annotList.item(k).getAttributes().getNamedItem("source");
                    if (source == null)
                        continue;
                    String appinfoSource = source.getNodeValue();
                    if (annotList.item(k) != null && annotList.item(k).getFirstChild() != null) {
                        String appinfoSourceValue = annotList.item(k).getFirstChild().getNodeValue();
                        if (appinfoSource.equals(BusinessConcept.APPINFO_X_DEFAULT_VALUE_RULE)) {
                            displayRules.add(new DisplayRule(BusinessConcept.APPINFO_X_DEFAULT_VALUE_RULE,appinfoSourceValue));
                        } else if (appinfoSource.equals(BusinessConcept.APPINFO_X_VISIBLE_RULE)) {
                            displayRules.add(new DisplayRule(BusinessConcept.APPINFO_X_VISIBLE_RULE,appinfoSourceValue));
                        }
                    }

                }
            }
        }
        return displayRules;
        
    }
    
    
    /**
     * DOC HSHU Comment method "evalDefaultValueRule".
     */
    public String evalDefaultValueRuleResult(Document transformedDoc,String xPath) {
        
        if(transformedDoc==null)return null;
        org.dom4j.Node node = transformedDoc.selectSingleNode(xPath);
        return node == null ? null : node.getText();

    }
    
    /**
     * DOC HSHU Comment method "evalDefaultValueRule".
     */
    public String evalVisibleRuleResult(Document transformedDoc,String xPath) {
        
        if(transformedDoc==null)return null;
        org.dom4j.Node node = transformedDoc.selectSingleNode(xPath);
        if(node==null)return null;
        org.dom4j.Element elem = (org.dom4j.Element)node;
        return elem.attributeValue("visible");

    }
    
    public static void filterByDisplayRules(List<TreeNode> nodesList, TreeNode node, List<DisplayRule> dspRules, int docIndex) throws ParseException {

            String xpath=node.getBindingPath();
            xpath=XmlUtil.normalizeXpath(xpath);
            for (DisplayRule displayRule : dspRules) {
                String xpathInRule=XmlUtil.normalizeXpath(displayRule.getXpath());
                if(displayRule.getType().equals(BusinessConcept.APPINFO_X_DEFAULT_VALUE_RULE)) {
                    if(xpath.equals(xpathInRule)){
                        if(node.getValue()==null||node.getValue().trim().equals("")) {
                            node.setValue(displayRule.getValue());
                            ItemsBrowserDWR.updateNode2(xpath, node.getValue(), docIndex);
                        }  
                    }
                }else if(displayRule.getType().equals(BusinessConcept.APPINFO_X_VISIBLE_RULE)) {
                    if(xpath.startsWith(xpathInRule)){
                        nodesList.remove(node);
                    }
                }
            }
        
    }
    
    class TemplateBean{
        
        private XSElementDecl selfElement=null;
        private List<XSElementDecl> childrenElements=null;
        
        public TemplateBean(XSElementDecl selfElement) {
            super();
            this.selfElement = selfElement;
        }
        

        public XSElementDecl getSelfElement() {
            return selfElement;
        }

        
        public List<XSElementDecl> getChildrenElements() {
            return childrenElements;
        }

        /**
         * DOC HSHU Comment method "addChildElement".
         */
        public void addChildElement(XSElementDecl childElement) {
            if(childrenElements==null)childrenElements=new ArrayList<XSElementDecl>();
            childrenElements.add(childElement);
        }


        @Override
        public String toString() {
            
            String print = "TemplateBean [selfElement=" + selfElement.getName() + "]{" ;
            if(childrenElements!=null) {
                for (XSElementDecl child : childrenElements) {
                    print+=child.getName()+"|";
                }
            }
            print+="}";
            return print;
            
        }
        
    }
    
}
