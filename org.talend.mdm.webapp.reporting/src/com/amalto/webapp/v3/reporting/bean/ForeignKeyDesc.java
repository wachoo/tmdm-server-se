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
package com.amalto.webapp.v3.reporting.bean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.amalto.webapp.core.dwr.CommonDWR;
import com.sun.xml.xsom.XSAnnotation;
import com.sun.xml.xsom.XSComplexType;
import com.sun.xml.xsom.XSElementDecl;
import com.sun.xml.xsom.XSParticle;

/**
 * DOC achen  class global comment. Detailled comment
 */
public class ForeignKeyDesc {

    private HashMap<String, String> foreignKeys = new HashMap<String, String>();

    private HashMap<String, Boolean> retrieveFKinfos = new HashMap<String, Boolean>();

    private String concept;

    private String dataModelPK;

    private String dataCluster;

    private HashMap<String, List<String>> fkInfoLists = new HashMap<String, List<String>>();

    private static final Logger LOG = Logger.getLogger(ForeignKeyDesc.class);

    public ForeignKeyDesc(String concept, String dataModelPK, String dataCluster) {
        this.concept = concept;
        this.dataModelPK = dataModelPK;
        this.dataCluster = dataCluster;
    }

    private void traveseChildrenParticle(XSParticle xsp, String xpathParent) {
        if (xsp.getTerm().asModelGroup() != null) { // is complex type
            XSParticle[] xsps = xsp.getTerm().asModelGroup().getChildren();
            for (int i = 0; i < xsps.length; i++) {
                traveseChildrenParticle(xsps[i], xpathParent);
            }
        }
        if (xsp.getTerm().asElementDecl() == null)
            return;
        String xPath = xpathParent + '/' + xsp.getTerm().asElementDecl().getName();
        if (xsp.getTerm().asElementDecl().getType().isComplexType()) {
            XSComplexType type = (XSComplexType) xsp.getTerm().asElementDecl().getType();
            XSParticle[] xsps = type.getContentType().asParticle().getTerm().asModelGroup().getChildren();
            for (int i = 0; i < xsps.length; i++) {
                traveseChildrenParticle(xsps[i], xPath);
            }
        } else {
            getAnnotation(xpathParent, xsp.getTerm().asElementDecl());
        }
    }

    private void getAnnotation(String xpathParent, XSElementDecl decl) {
        XSAnnotation xsa = decl.getAnnotation();
        if (xsa != null && xsa.getAnnotation() != null) {
            Element el = (Element) xsa.getAnnotation();
            NodeList annotList = el.getChildNodes();
            List<String> fkInfoList = new ArrayList<String>();
            String xPath = xpathParent + '/' + decl.getName();
            for (int k = 0; k < annotList.getLength(); k++) {
                if ("appinfo".equals(annotList.item(k).getLocalName())) { //$NON-NLS-1$
                    Node source = annotList.item(k).getAttributes().getNamedItem("source"); //$NON-NLS-1$
                    if (source == null)
                        continue;
                    String appinfoSource = annotList.item(k).getAttributes().getNamedItem("source").getNodeValue(); //$NON-NLS-1$
                    if ("X_ForeignKey".equals(appinfoSource)) { //$NON-NLS-1$
                        foreignKeys.put(xPath, annotList.item(k).getFirstChild().getNodeValue());
                    } else if ("X_ForeignKeyInfo".equals(appinfoSource)) { //$NON-NLS-1$
                        fkInfoList.add(annotList.item(k).getFirstChild().getNodeValue());
                    } else if ("X_Retrieve_FKinfos".equals(appinfoSource)) { //$NON-NLS-1$
                        retrieveFKinfos.put(xPath, "true".equals(annotList.item(k).getFirstChild().getNodeValue())); //$NON-NLS-1$
                    }
                }
            }
            fkInfoLists.put(xPath, fkInfoList);
        }
    }

    public void fetchAnnotations() throws Exception {
        Map<String, XSElementDecl> map = CommonDWR.getConceptMap(dataModelPK);
        XSComplexType xsct = (XSComplexType) (map.get(concept).getType());
        if (xsct == null) {
            String err = "Concept '" + concept + "' is not found in model '" + dataModelPK + "'";
            LOG.error(err);
            return;
        }
        XSParticle[] xsp = xsct.getContentType().asParticle().getTerm().asModelGroup().getChildren();
        for (XSParticle particle : xsp) {
            traveseChildrenParticle(particle, concept);
        }
    }


    public String getConcept() {
        return concept;
    }

    public void setConcept(String concept) {
        this.concept = concept;
    }


    public String getDataCluster() {
        return dataCluster;
    }

    public void setDataCluster(String dataCluster) {
        this.dataCluster = dataCluster;
    }

    public HashMap<String, String> getForeignKeys() {
        return foreignKeys;
    }

    public void setForeignKeys(HashMap<String, String> foreignKeys) {
        this.foreignKeys = foreignKeys;
    }

    public HashMap<String, Boolean> getRetrieveFKinfos() {
        return retrieveFKinfos;
    }

    public void setRetrieveFKinfos(HashMap<String, Boolean> retrieveFKinfos) {
        this.retrieveFKinfos = retrieveFKinfos;
    }

    public HashMap<String, List<String>> getFkInfoLists() {
        return fkInfoLists;
    }

    public void setFkInfoLists(HashMap<String, List<String>> fkInfoLists) {
        this.fkInfoLists = fkInfoLists;
    }

}
