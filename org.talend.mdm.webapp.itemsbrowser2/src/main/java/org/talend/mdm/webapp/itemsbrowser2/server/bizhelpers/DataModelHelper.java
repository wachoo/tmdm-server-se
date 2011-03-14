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
package org.talend.mdm.webapp.itemsbrowser2.server.bizhelpers;

import java.io.StringReader;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.talend.mdm.webapp.itemsbrowser2.client.model.DataTypeConstants;
import org.talend.mdm.webapp.itemsbrowser2.server.ItemsBrowserConfiguration;
import org.talend.mdm.webapp.itemsbrowser2.server.util.CommonUtil;
import org.talend.mdm.webapp.itemsbrowser2.shared.ComplexTypeModel;
import org.talend.mdm.webapp.itemsbrowser2.shared.EntityModel;
import org.talend.mdm.webapp.itemsbrowser2.shared.FacetModel;
import org.talend.mdm.webapp.itemsbrowser2.shared.SimpleTypeModel;
import org.talend.mdm.webapp.itemsbrowser2.shared.TypeModel;

import com.amalto.webapp.core.dmagent.SchemaWebAgent;
import com.amalto.webapp.core.util.XtentisWebappException;
import com.amalto.webapp.util.webservices.WSConceptKey;
import com.amalto.webapp.util.webservices.WSDataModelPK;
import com.amalto.webapp.util.webservices.WSGetBusinessConceptKey;
import com.amalto.webapp.util.webservices.WSGetDataModel;
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

public class DataModelHelper {

    private static final Logger logger = Logger.getLogger(DataModelHelper.class);

    private static List<String> getLabels(XSElementDecl xsed, String x_Label) {
        List<String> labels;
        try {
            labels = new ArrayList<String>();
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
                            labels.add(list.item(k).getFirstChild().getNodeValue());
                        }
                    }
                }
            }
        } catch (Exception e) {
            return null;
        }
        return labels;
    }

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

    /**
     * DOC HSHU Comment method "getBusinessConcept".
     * 
     * @throws Exception
     */
    private static XSElementDecl getBusinessConcept(String model, String concept) {
        XSElementDecl eleDecl = null;
        try {
            if (!ItemsBrowserConfiguration.isStandalone()) {
                eleDecl = SchemaWebAgent.getInstance().getBusinessConcept(concept).getE();
            } else {
                String xsd = CommonUtil.getPort().getDataModel(new WSGetDataModel(new WSDataModelPK(model))).getXsdSchema();
                XSOMParser reader = new XSOMParser();
                reader.setAnnotationParser(new DomAnnotationParserFactory());
                reader.parse(new StringReader(xsd));
                XSSchemaSet xss = reader.getResult();
                eleDecl = getElementDeclByName(concept, xss);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return eleDecl;
    }

    /**
     * DOC HSHU Comment method "parseSchema".
     * 
     * @param model
     * @param concept
     */
    public static void parseSchema(String model, String concept, EntityModel entityModel) {
        entityModel.setConceptName(concept);
        //set pk
        entityModel.setKeys(getBusinessConceptKeys(model,concept));
        //analyst model
        XSElementDecl eleDecl = getBusinessConcept(model, concept);
        if (eleDecl != null) {
            parseElementDecl("", eleDecl, entityModel);
        }
    }

    private static XSElementDecl getElementDeclByName(String eleName, XSSchemaSet xss) {
        Collection<XSSchema> schemas = xss.getSchemas();
        for (XSSchema xsa : schemas) {
            Map<String, XSElementDecl> elems = xsa.getElementDecls();
            XSElementDecl eleDecl = elems.get(eleName);
            if (eleDecl != null) {
                return eleDecl;
            }
        }
        return null;
    }

    private static TypeModel parseElementDecl(String path, XSElementDecl eleDecl, EntityModel entityModel) {
        TypeModel typeModel = null;
        XSType type = eleDecl.getType();
        String fullPath = path + eleDecl.getName();
        String label = getLabel(eleDecl, "X_Label_EN");
        if (label.equals(""))
            label = eleDecl.getName();
        if (type.isSimpleType()) {
            typeModel = parseSimpleType(type.asSimpleType());
            entityModel.getMetaDataTypes().put(fullPath, typeModel);
        } else if (type.isComplexType()) {
            typeModel = parseComplexType(fullPath + "/", type.asComplexType(), entityModel);
            entityModel.getMetaDataTypes().put(fullPath, typeModel);
        }
        typeModel.setLabel(label);
        typeModel.setXpath(fullPath);
        String fk = getLabel(eleDecl, "X_ForeignKey");
        if (fk != "")
            typeModel.setForeignkey(fk);

        List<String> fkInfo = getLabels(eleDecl, "X_ForeignKeyInfo");
        if (fkInfo != null)
            typeModel.setForeignKeyInfo(fkInfo);

        return typeModel;
    }

    private static SimpleTypeModel parseSimpleType(XSSimpleType simpleType) {
        SimpleTypeModel simpleTypeModel = new SimpleTypeModel();
        simpleTypeModel.setTypeName(DataTypeConstants.getDataTypeByName(simpleType.getName()));
        if (simpleType.isRestriction()) {
            XSRestrictionSimpleType resType = simpleType.asRestriction();
            Collection<? extends XSFacet> facetes = resType.getDeclaredFacets();
            List<FacetModel> fms = new ArrayList<FacetModel>();
            for (XSFacet facet : facetes) {
                fms.add(new FacetModel(facet.getName(), facet.getValue().toString()));
            }
            simpleTypeModel.setFacets(fms);
        }
        return simpleTypeModel;
    }

    private static void parseParticle(ComplexTypeModel complexTypeModel, String path, XSParticle xsp, EntityModel entityModel) {

        XSElementDecl eleDecl = xsp.getTerm().asElementDecl();
        if (eleDecl == null)
            return;
        TypeModel typeModel = parseElementDecl(path, eleDecl, entityModel);
        typeModel.setMinOccurs(xsp.getMinOccurs());
        typeModel.setMaxOccurs(xsp.getMaxOccurs());
        if (typeModel.isSimpleType()) {
            complexTypeModel.getSubSimpleTypes().add((SimpleTypeModel) typeModel);
        } else {
            complexTypeModel.getSubComplexTypes().add((ComplexTypeModel) typeModel);
        }

        XSModelGroup xsGroup = xsp.getTerm().asModelGroup();
        if (xsGroup != null) {
            XSParticle[] xsps = xsGroup.getChildren();
            for (XSParticle p : xsps) {
                parseParticle(complexTypeModel, path, p, entityModel);
            }
        }

    }

    private static ComplexTypeModel parseComplexType(String path, XSComplexType complexType, EntityModel entityModel) {
        ComplexTypeModel complexTypeModel = new ComplexTypeModel();
        complexTypeModel.setTypeName(DataTypeConstants.getDataTypeByName(complexType.getName()));
        XSParticle[] xsps = complexType.getContentType().asParticle().getTerm().asModelGroup().getChildren();
        if (xsps != null) {
            for (XSParticle xsp : xsps) {
                parseParticle(complexTypeModel, path, xsp, entityModel);
            }
        }
        return complexTypeModel;
    }

    /**
     * DOC HSHU Comment method "getBusinessConceptKey".
     * 
     * @throws XtentisWebappException
     * @throws RemoteException
     */
    private static String[] getBusinessConceptKeys(String model, String concept) {
        
        String[] keys = null;
        try {
            WSConceptKey key = CommonUtil.getPort().getBusinessConceptKey(
                    new WSGetBusinessConceptKey(new WSDataModelPK(model), concept));

            keys = key.getFields();
            keys = Arrays.copyOf(keys, keys.length);
            for (int i = 0; i < keys.length; i++) {
                if (".".equals(key.getSelector())) //$NON-NLS-1$
                    keys[i] = concept + "/" + keys[i]; //$NON-NLS-1$ 
                else
                    keys[i] = key.getSelector() + keys[i];
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return keys;
        
    }

}
