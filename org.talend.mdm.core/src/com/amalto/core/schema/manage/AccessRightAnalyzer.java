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
package com.amalto.core.schema.manage;

import org.talend.mdm.commmon.util.datamodel.management.BusinessConcept;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.sun.xml.xsom.XSElementDecl;
import com.sun.xml.xsom.XSParticle;

/**
 *
 */
public class AccessRightAnalyzer {

    private BusinessConcept businessConcept;

    private AppinfoSourceHolder appinfoSourceHolder;

    public AccessRightAnalyzer(BusinessConcept businessConcept, AppinfoSourceHolder appinfoSourceHolder) {
        super();
        this.businessConcept = businessConcept;
        this.appinfoSourceHolder = appinfoSourceHolder;
    }

    public void calculate() {
        XSElementDecl element = businessConcept.getE();
        // TMDM-2979: Be careful when handling element, accessing it (even only for reading it) in MT context
        // is dangerous.
        synchronized (element) { // Local variable, but businessConcept is cached... so shared by all threads.
            travelXSElement(element, "/" + businessConcept.getName(), appinfoSourceHolder);
        }
    }

    private void travelXSElement(XSElementDecl e, String currentXPath, AppinfoSourceHolder appinfoSourceHolder) {
        if (e != null) {

            parseAnnotation(currentXPath, e, appinfoSourceHolder);

            if (e.getType().isComplexType()) {
                XSParticle[] subParticles = e.getType().asComplexType().getContentType().asParticle().getTerm().asModelGroup().getChildren();
                if (subParticles != null) {
                    for (XSParticle xsParticle : subParticles) {
                        travelParticle(xsParticle, currentXPath, appinfoSourceHolder);
                    }
                }
            }

        }
    }

    private void travelParticle(XSParticle xsParticle, String currentXPath, AppinfoSourceHolder appinfoSourceHolder) {
        if (xsParticle.getTerm().asModelGroup() != null) {
            XSParticle[] xsps = xsParticle.getTerm().asModelGroup().getChildren();
            for (XSParticle xsp : xsps) {
                travelParticle(xsp, currentXPath, appinfoSourceHolder);
            }
        }else if(xsParticle.getTerm().asElementDecl()!=null) {
            XSElementDecl subElement = xsParticle.getTerm().asElementDecl();
            travelXSElement(subElement, currentXPath + "/" + subElement.getName(), appinfoSourceHolder);
        }
    }

    private void parseAnnotation(String currentXPath, XSElementDecl e, AppinfoSourceHolder appinfoSourceHolder) {
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
                        if (appinfoSource.equals(BusinessConcept.APPINFO_X_HIDE)) {
                            appinfoSourceHolder.addSource(BusinessConcept.APPINFO_X_HIDE, currentXPath, appinfoSourceValue);
                        } else if (appinfoSource.equals(BusinessConcept.APPINFO_X_WRITE)) {
                            appinfoSourceHolder.addSource(BusinessConcept.APPINFO_X_WRITE, currentXPath, appinfoSourceValue);
                        }
                    }

                }
            }
        }
    }

}
