// ============================================================================
//
// Copyright (C) 2006-2010 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.mdm.webapp.itemsbrowser2.server.util;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.talend.mdm.webapp.itemsbrowser2.shared.ComplexTypeModel;
import org.talend.mdm.webapp.itemsbrowser2.shared.FacetModel;
import org.talend.mdm.webapp.itemsbrowser2.shared.SimpleTypeModel;
import org.talend.mdm.webapp.itemsbrowser2.shared.TypeModel;
import org.xml.sax.SAXException;

import com.sun.xml.xsom.XSAnnotation;
import com.sun.xml.xsom.XSComplexType;
import com.sun.xml.xsom.XSElementDecl;
import com.sun.xml.xsom.XSFacet;
import com.sun.xml.xsom.XSModelGroup;
import com.sun.xml.xsom.XSParticle;
import com.sun.xml.xsom.XSRestrictionSimpleType;
import com.sun.xml.xsom.XSSchema;
import com.sun.xml.xsom.XSSchemaSet;
import com.sun.xml.xsom.XSSimpleType;
import com.sun.xml.xsom.XSType;
import com.sun.xml.xsom.parser.XSOMParser;
import com.sun.xml.xsom.util.DomAnnotationParserFactory;


public class XsdUtil {
    
    private static final Logger logger = Logger.getLogger(XsdUtil.class);
    
    private static Map<String, TypeModel> xpathToType = new HashMap<String, TypeModel>();
    
    private static String getLabel(XSElementDecl xsed, String x_Label) {
        String label = "";
        try {
            XSAnnotation xsa = xsed.getAnnotation();

            org.w3c.dom.Element elem = (org.w3c.dom.Element) xsa.getAnnotation();
            if (elem != null) {
                org.w3c.dom.NodeList list = elem.getChildNodes();
                for (int k = 0; k < list.getLength(); k++) {
                    if ("appinfo".equals(list.item(k).getLocalName())) {
                        org.w3c.dom.Node source = list.item(k).getAttributes().getNamedItem("source");
                        if (source == null)
                            continue;
                        String appinfoSource = source.getNodeValue();
                        if (x_Label.equals(appinfoSource)) {
                            label = list.item(k).getFirstChild().getNodeValue();
                            break;
                        }
                    }
                }
            }
        } catch (Exception e) {
            return "";
        }
        return label;
    }
    
    public static void parseXSD(String xsd, String viewPk) {
        try {
            XSOMParser reader = new XSOMParser();
            reader.setAnnotationParser(new DomAnnotationParserFactory());
            reader.parse(new StringReader(xsd));
            XSSchemaSet xss = reader.getResult();

            XSElementDecl eleDecl = getElementDeclByName(viewPk, xss);
            if (eleDecl != null){
                parseElementDecl("", eleDecl);
            }
        } catch (SAXException e) {
            logger.error(e.getMessage(), e);
        }
    }

    public static XSElementDecl getElementDeclByName(String eleName, XSSchemaSet xss){
        Collection<XSSchema> schemas = xss.getSchemas();
        for (XSSchema xsa : schemas){
            Map<String, XSElementDecl> elems = xsa.getElementDecls();
            XSElementDecl eleDecl = elems.get(eleName);
            if(eleDecl != null){
                return eleDecl;
            }
        }
        return null;
    }
    
    public static TypeModel parseElementDecl(String path, XSElementDecl eleDecl){
        
        TypeModel typeModel = null;
        XSType type = eleDecl.getType();
        String fullPath = path + eleDecl.getName();
        String label = getLabel(eleDecl, "X_Label_EN");
        if (type.isSimpleType()){
            typeModel = parseSimpleType(type.asSimpleType());
            xpathToType.put(fullPath , typeModel);
        } else if (type.isComplexType()){
            typeModel = parseComplexType(fullPath + "/", type.asComplexType());
            xpathToType.put(fullPath , typeModel);
        }
        typeModel.setLabel(label);
        return typeModel;
    }
    
    public static SimpleTypeModel parseSimpleType(XSSimpleType simpleType){
        SimpleTypeModel simpleTypeModel = new SimpleTypeModel();
        simpleTypeModel.setTypeName(simpleType.getName());
        if (simpleType.isRestriction()){
            XSRestrictionSimpleType resType = simpleType.asRestriction();
            Collection<? extends XSFacet> facetes = resType.getDeclaredFacets();
            List<FacetModel> fms = new ArrayList<FacetModel>();
            for (XSFacet facet : facetes){
                fms.add(new FacetModel(facet.getName(), facet.getValue().toString()));
            }
            simpleTypeModel.setFacets(fms);
        }
        return simpleTypeModel;
    }
    
    public static void parseParticle(ComplexTypeModel complexTypeModel, String path, XSParticle xsp){
        
        XSElementDecl eleDecl = xsp.getTerm().asElementDecl();
        if (eleDecl == null)
            return;
        TypeModel typeModel = parseElementDecl(path, eleDecl);
        if (typeModel.isSimpleType()){
            complexTypeModel.getSubSimpleTypes().add((SimpleTypeModel) typeModel);  
        } else {
            complexTypeModel.getSubComplexTypes().add((ComplexTypeModel) typeModel);
        }
        
        XSModelGroup xsGroup = xsp.getTerm().asModelGroup();
        if (xsGroup != null){
            XSParticle[] xsps = xsGroup.getChildren();
            for (XSParticle p : xsps){
                parseParticle(complexTypeModel, path, p);
            }
        }
        
    }
    
    public static ComplexTypeModel parseComplexType(String path, XSComplexType complexType){
        ComplexTypeModel complexTypeModel = new ComplexTypeModel();
        complexTypeModel.setTypeName(complexType.getName());
        XSParticle[] xsps = complexType.getContentType().asParticle().getTerm().asModelGroup().getChildren();
        if (xsps != null){
            for (XSParticle xsp : xsps){
                parseParticle(complexTypeModel, path, xsp);
            }
        }
        return complexTypeModel;
    }

    
    public static Map<String, TypeModel> getXpathToType() {
        return xpathToType;
    }
    
}
