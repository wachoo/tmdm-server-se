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
package org.talend.mdm.webapp.browserecords.server.displayrule;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.dom4j.Document;
import org.talend.mdm.commmon.util.datamodel.management.BusinessConcept;
import org.talend.mdm.webapp.browserecords.server.bizhelpers.DataModelHelper;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.amalto.webapp.core.util.XmlUtil;
import com.sun.xml.xsom.XSElementDecl;
import com.sun.xml.xsom.XSParticle;

public class DisplayRulesUtil {

    private static XSElementDecl root;

    public static XSElementDecl getRoot() {
		return root;
	}

	public static void setRoot(XSElementDecl root) {
		DisplayRulesUtil.root = root;
	}

	private List<TemplateBean> tmpls;

    private HashMap<String, Integer> countMap;
    
    private static Map<String, String> visibleRules = new HashMap<String, String>();
    
	public static Map<String, String> getVisibleRules() {
		return visibleRules;
	}

	public static void setVisibleRules(Map<String, String> visibleRules) {
		visibleRules = visibleRules;
	}

	private static DataModelHelper helper;

	public static DataModelHelper getHelper() {
		return helper;
	}

	public static void setHelper(DataModelHelper helper) {
		DisplayRulesUtil.helper = helper;
	}

	public DisplayRulesUtil() {
	}
	
	private static DisplayRulesUtil instance;
	
	public static DisplayRulesUtil getInstance() {
		if(instance == null) {
			instance = new DisplayRulesUtil(root);
		}
		
		return instance;
	}

	public DisplayRulesUtil(XSElementDecl root) {
        super();
        this.root = root;
        this.tmpls = new ArrayList<TemplateBean>();
        this.countMap = new HashMap<String, Integer>();
    }

    public String genVisibleStyle() {
        // translate schema
        String style = translateVisibleSchema();

        StringBuffer sb = new StringBuffer();
        sb.append("<xsl:stylesheet xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\" xmlns:fn=\"http://www.w3.org/2005/xpath-functions\" xmlns:t=\"http://www.talend.com/2010/MDM\" version=\"2.0\">"); //$NON-NLS-1$
        sb.append("<xsl:output method=\"xml\" indent=\"yes\" omit-xml-declaration=\"yes\"/>"); //$NON-NLS-1$
        sb.append(style);
        sb.append("</xsl:stylesheet>"); //$NON-NLS-1$

        return sb.toString();
    }

    private String getStyleElemName(String elemName, HashMap<String, Integer> indexMap) {
        String styleName = null;
        Pattern p = Pattern.compile("(.*?)(\\[\\d+\\])$"); //$NON-NLS-1$
        Matcher m = p.matcher(elemName);
        String name = elemName;
        if (m.matches()) {
            name = m.group(1);
        }

        if (indexMap.containsKey(name)) {
            int index = indexMap.get(name);
            styleName = name + "[" + (index + 1) + "]"; //$NON-NLS-1$ //$NON-NLS-2$
            indexMap.put(name, index + 1);
        } else {
            if (countMap.get(name) > 1) {
                styleName = name + "[1]"; //$NON-NLS-1$
                indexMap.put(name, 1);
            } else {
                styleName = name;
                indexMap.put(name, 1);
            }
        }
        return styleName;
    }

    private String getTemplateName(String elemName, HashMap<String, Integer> indexMap) {
        String styleName = null;
        Pattern p = Pattern.compile("(.*?)(\\[\\d+\\])$"); //$NON-NLS-1$
        Matcher m = p.matcher(elemName);
        String name = elemName;
        if (m.matches()) {
            name = m.group(1);
        }
        if (indexMap.containsKey(name) && countMap.get(name) > 1) {
            int index = indexMap.get(name);
            styleName = name + "[" + (countMap.get(name) - index + 1) + "]"; //$NON-NLS-1$ //$NON-NLS-2$
            indexMap.put(name, index - 1);
        } else {
            styleName = name;
            indexMap.put(name, 1);
        }
        return styleName;
    }

    private String translateVisibleSchema() {
        StringBuffer style = new StringBuffer();
        if (tmpls.isEmpty())
            travelXSElement(root, "/" + root.getName()); //$NON-NLS-1$

        HashMap<String, Integer> indexMap = new HashMap<String, Integer>();
        for (TemplateBean tmp : tmpls) {
            XSElementDecl self = tmp.getSelfElement();
            List<DisplayRule> dspRules = getRules(self, BusinessConcept.APPINFO_X_VISIBLE_RULE);
            if (self.getType().isComplexType() || dspRules.size() > 0) {
                Map<XSElementDecl, String> children = tmp.getChildrenElements();

                style.append("<xsl:template match=\"" + getTemplateName(tmp.getXPath(), indexMap) + "\">") //$NON-NLS-1$ //$NON-NLS-2$
                        .append("<xsl:copy>"); //$NON-NLS-1$ 

                if (dspRules.size() > 0)
                    for (DisplayRule displayRule : dspRules) {
                        style.append("<xsl:if test=\"not(" + getPureValue(displayRule.getValue()) + ")\"> "); //$NON-NLS-1$ //$NON-NLS-2$ 
                        style.append("<xsl:attribute name=\"t:visible\">false</xsl:attribute> "); //$NON-NLS-1$ 
                        style.append("</xsl:if>"); //$NON-NLS-1$
                    }

                if (children != null && children.size() > 0) {
                    for (XSElementDecl child : children.keySet()) {
                        if (child.getType().isComplexType() || hasRules(child, BusinessConcept.APPINFO_X_VISIBLE_RULE))
                            style
                                    .append("<xsl:apply-templates select=\"" + getStyleElemName(children.get(child), indexMap) + "\"/>"); //$NON-NLS-1$ //$NON-NLS-2$
                        else
                            style.append("<xsl:copy-of select=\"" + child.getName() + "\"/>"); //$NON-NLS-1$ //$NON-NLS-2$
                    }
                }

                style.append("</xsl:copy>") //$NON-NLS-1$ 
                        .append("</xsl:template>"); //$NON-NLS-1$ 
            }
        }

        return style.toString();
    }

    private String getPureValue(String displayValue) {
        return displayValue.replaceAll("\r\n", "").replaceAll("\n", ""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ 
    }

    private String travelXSElement(XSElementDecl e, String currentXPath) {
        if (e != null) {
            Pattern p = Pattern.compile("(.*?)(00000\\d+00000)$"); //$NON-NLS-1$ //add separator in left and right sides to avoid same named in xsd, correspond to getDspXpathName function in ItemBrowserDWR
            Matcher m = p.matcher(e.getName());
            String name = e.getName();
            if (m.matches()) {
                name = m.group(1);
            }
            String currentName = currentXPath.substring(0, currentXPath.lastIndexOf("/") + 1) + name; //$NON-NLS-1$

            if (e.getType().isSimpleType()) {
                if (hasRules(e, null)) {

                    if (countMap.containsKey(currentName))
                        countMap.put(currentName, countMap.get(currentName) + 1);
                    else
                        countMap.put(currentName, 1);
                    currentXPath = currentName + "[" + countMap.get(currentName) + "]"; //$NON-NLS-1$ //$NON-NLS-2$

                    tmpls.add(new TemplateBean(e, currentXPath));
                }
            } else if (e.getType().isComplexType()) {
                if (countMap.containsKey(currentName))
                    countMap.put(currentName, countMap.get(currentName) + 1);
                else
                    countMap.put(currentName, 1);
                currentXPath = currentName + "[" + countMap.get(currentName) + "]"; //$NON-NLS-1$ //$NON-NLS-2$
                TemplateBean templateBean = new TemplateBean(e, currentXPath);
                tmpls.add(templateBean);

                XSParticle[] subParticles = e.getType().asComplexType().getContentType().asParticle().getTerm().asModelGroup()
                        .getChildren();
                if (subParticles != null) {
                    for (int i = 0; i < subParticles.length; i++) {
                        XSParticle xsParticle = subParticles[i];
                        travelParticle(xsParticle, templateBean, currentXPath);
                    }
                }

            }

            return currentXPath;
        }
        return null;
    }

    private void travelParticle(XSParticle xsParticle, TemplateBean templateBean, String currentXPath) {
        if (xsParticle.getTerm().asModelGroup() != null) {
            XSParticle[] xsps = xsParticle.getTerm().asModelGroup().getChildren();
            for (int j = 0; j < xsps.length; j++) {
                travelParticle(xsps[j], templateBean, currentXPath);
            }
        } else if (xsParticle.getTerm().asElementDecl() != null) {
            XSElementDecl subElement = xsParticle.getTerm().asElementDecl();
            String refXPath = travelXSElement(subElement, currentXPath + "/" + subElement.getName()); //$NON-NLS-1$ 
            templateBean.addChildElement(subElement, refXPath);
        }
    }

    private boolean hasRules(XSElementDecl e, String type) {
        if (e.getAnnotation() != null && e.getAnnotation().getAnnotation() != null) {
            Element annotations = (Element) e.getAnnotation().getAnnotation();
            NodeList annotList = annotations.getChildNodes();
            
            for (int k = 0; k < annotList.getLength(); k++) {
                if ("appinfo".equals(annotList.item(k).getLocalName())) { //$NON-NLS-1$
                    Node source = annotList.item(k).getAttributes().getNamedItem("source"); //$NON-NLS-1$
                    if (source == null)
                        continue;
                    String appinfoSource = source.getNodeValue();
                    if (annotList.item(k) != null && annotList.item(k).getFirstChild() != null) {
                        if (type.equals(BusinessConcept.APPINFO_X_VISIBLE_RULE))
                            if (appinfoSource.equals(BusinessConcept.APPINFO_X_VISIBLE_RULE))
                                return true;
                    }
                }
            }
        }
        
        return false;
    }

    private List<DisplayRule> getRules(XSElementDecl e, String type) {
        List<DisplayRule> displayRules = new ArrayList<DisplayRule>();
        
        if (e.getAnnotation() != null && e.getAnnotation().getAnnotation() != null) {
            Element annotations = (Element) e.getAnnotation().getAnnotation();
            NodeList annotList = annotations.getChildNodes();
            
            for (int k = 0; k < annotList.getLength(); k++) {
                if ("appinfo".equals(annotList.item(k).getLocalName())) { //$NON-NLS-1$
                    Node source = annotList.item(k).getAttributes().getNamedItem("source"); //$NON-NLS-1$
                    if (source == null)
                        continue;
                    String appinfoSource = source.getNodeValue();
                    if (annotList.item(k) != null && annotList.item(k).getFirstChild() != null) {
                        String appinfoSourceValue = annotList.item(k).getFirstChild().getNodeValue();
                        
                        if (type.equals(BusinessConcept.APPINFO_X_VISIBLE_RULE)) {
                            if (appinfoSource.equals(BusinessConcept.APPINFO_X_VISIBLE_RULE)) {
                                displayRules.add(new DisplayRule(BusinessConcept.APPINFO_X_VISIBLE_RULE, appinfoSourceValue));
                            }
                        }
                    }

                }
            }
        }
        
        return displayRules;

    }

    public String evalDefaultValueRuleResult(Document transformedDoc, String xPath) {
        if (transformedDoc == null)
            return null;
        org.dom4j.Node node = transformedDoc.selectSingleNode(xPath);
        return node == null ? null : node.getText();

    }

    public String evalVisibleRuleResult(Document transformedDoc, String xPath) {
        if (transformedDoc == null)
            return null;
        org.dom4j.Node node = transformedDoc.selectSingleNode(xPath);
        if (node == null)
            return null;
        org.dom4j.Element elem = (org.dom4j.Element) node;
        return elem.attributeValue("visible"); //$NON-NLS-1$

    }

    class TemplateBean {

        private String xpath = null;

        private XSElementDecl selfElement = null;

        private LinkedHashMap<XSElementDecl, String> childrenElements = null;

        public TemplateBean(XSElementDecl selfElement, String xpath) {
            super();
            this.selfElement = selfElement;
            this.xpath = xpath;
        }

        public XSElementDecl getSelfElement() {
            return selfElement;
        }

        public String getXPath() {
            return xpath;
        }

        public LinkedHashMap<XSElementDecl, String> getChildrenElements() {
            return childrenElements;
        }

        /**
         * DOC HSHU Comment method "addChildElement".
         */
        public void addChildElement(XSElementDecl childElement, String xpath) {
            if (childrenElements == null)
                childrenElements = new LinkedHashMap<XSElementDecl, String>();
            childrenElements.put(childElement, xpath);
        }

        @Override
        public String toString() {
            String print = "TemplateBean [selfElement=" + selfElement.getName() + "]{"; //$NON-NLS-1$ //$NON-NLS-2$
            if (childrenElements != null) {
                for (XSElementDecl child : childrenElements.keySet()) {
                    print += child.getName() + "|"; //$NON-NLS-1$
                }
            }
            print += "}"; //$NON-NLS-1$
            return print;

        }

    }

	public List<DisplayRule> handleVisibleRules(org.dom4j.Document doc) {
		List<DisplayRule> displayRules = new ArrayList<DisplayRule>();
		
		try {
			Document transformedDocumentVisible = XmlUtil.styleDocument(doc, genVisibleStyle());
			
			for (Iterator<String> iterator = visibleRules.keySet().iterator(); iterator.hasNext();) {
                String xpath = (String) iterator.next();
                String value = evalVisibleRuleResult(transformedDocumentVisible, xpath);
                if (value != null && value.equals("false")) {//$NON-NLS-1$
                	displayRules.add(new DisplayRule(xpath, "false"));//$NON-NLS-1$
                } else if (value == null || (value != null && value.equals("true"))) {//$NON-NLS-1$
                	displayRules.add(new DisplayRule(xpath, "true"));//$NON-NLS-1$
                }
            }
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return displayRules;
	}
}
