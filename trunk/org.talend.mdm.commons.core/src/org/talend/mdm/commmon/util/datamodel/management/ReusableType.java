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
package org.talend.mdm.commmon.util.datamodel.management;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.sun.xml.xsom.XSParticle;
import com.sun.xml.xsom.XSType;

/**
 * DOC HSHU class global comment. Detailled comment
 */
public class ReusableType {

    private XSType xsType;

    private String name;

    private String parentName;

    private boolean isAbstract;
    
    private String orderValue = null;
    
    private Map<String, String> labelMap = null;

    // TODO: translate it from technique to business logic
    // mainly maintain the relationships among different business concepts

    public ReusableType(XSType xsType) {
        super();
        this.xsType = xsType;
        name = xsType.getName();
        parentName = xsType.getBaseType().getName();// Is this the best way?
        isAbstract = xsType.asComplexType().isAbstract();
    }
    
    @Deprecated
    public ReusableType(String name) {
        super();
        this.name = name;
    }
    
    /**
     * DOC HSHU Comment method "load".
     */
    public void load() {
         beforeLoad();
         parseRootAnnotation(this.xsType);
    }
    
    /**
     * DOC HSHU Comment method "beforeLoad".
     * 
     */
    private void beforeLoad() {
        //prepare
        labelMap=new HashMap<String, String>();
        orderValue=null;
    }

    /**
     * DOC HSHU Comment method "parseRootAnnotation".
     * @param xsType
     */
    private void parseRootAnnotation(XSType xsType) {
        if (xsType.getAnnotation() != null && xsType.getAnnotation().getAnnotation() != null) {
            Element annotations = (Element) xsType.getAnnotation().getAnnotation();
            NodeList annotList = annotations.getChildNodes();
            for (int k = 0; k < annotList.getLength(); k++) {
                if ("appinfo".equals(annotList.item(k).getLocalName())) {//$NON-NLS-1$
                    Node source = annotList.item(k).getAttributes().getNamedItem("source");//$NON-NLS-1$
                    if (source == null)
                        continue;
                    String appinfoSource = source.getNodeValue();
                    if (annotList.item(k) != null && annotList.item(k).getFirstChild() != null) {
                        String appinfoSourceValue = annotList.item(k).getFirstChild().getNodeValue();
                        if (appinfoSource.contains("X_Label")) {//$NON-NLS-1$
                            this.labelMap.put(getLangFromLabelAnnotation(appinfoSource), appinfoSourceValue);
                        } else if (appinfoSource.equals("X_Order_Value")) {//$NON-NLS-1$
                            this.orderValue=appinfoSourceValue;
                        } 
                    }

                }
            }
        }
    }

    /**
     * DOC HSHU Comment method "getLangFromLabelAnnotation".
     * @param label
     * @return
     */
    private String getLangFromLabelAnnotation(String label) {
        String format="X_Label_(.+)";//$NON-NLS-1$
        String lang = getLangFromAnnotation(label, format);
        return lang;
    }
    
    /**
     * DOC HSHU Comment method "getLangFromAnnotation".
     * @param label
     * @param format
     * @return
     */
    private String getLangFromAnnotation(String label, String format) {
        String lang="EN";//$NON-NLS-1$
        Pattern p = Pattern.compile(format);
        Matcher matcher = p.matcher(label);
        while(matcher.find()){
            lang=matcher.group(1);
        }
        if(lang!=null)lang=lang.toLowerCase();
        return lang;
    }

    public String getName() {
        return name;
    }

    public String getParentName() {
        return parentName;
    }
    
    public boolean isAbstract() {
        return isAbstract;
    }

    public void setAbstract(boolean isAbstract) {
        this.isAbstract = isAbstract;
    }
    
    public String getOrderValue() {
        return orderValue;
    }
   
    public Map<String, String> getLabelMap() {
        return labelMap;
    }

    public XSParticle getXsParticle() {
        return xsType.asComplexType().getContentType().asParticle();
    }

    public List<XSParticle> getAllChildren(XSParticle particle) {
        List<XSParticle> particles = new ArrayList<XSParticle>();
        XSParticle[] children = null;
        if (particle == null) {
            children = xsType.asComplexType().getContentType().asParticle().getTerm().asModelGroup().getChildren();
        } else {
            if (particle.getTerm().asModelGroup() != null)
                children = particle.getTerm().asModelGroup().getChildren();
            else {
                particles.add(particle);
                return particles;
            }
        }

        if (children != null) {
            for (XSParticle pt : children) {
                particles.addAll(getAllChildren(pt));
            }
        }

        return particles;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ReusableType other = (ReusableType) obj;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "[name=" + name + "]";//$NON-NLS-1$ //$NON-NLS-2$
    }

}
