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
package org.talend.mdm.webapp.itemsbrowser2.server.bizhelpers;

import java.io.StringReader;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.log4j.Logger;
import org.talend.mdm.webapp.base.server.BaseConfiguration;
import org.talend.mdm.webapp.base.server.util.CommonUtil;
import org.talend.mdm.webapp.base.shared.ComplexTypeModel;
import org.talend.mdm.webapp.base.shared.EntityModel;
import org.talend.mdm.webapp.base.shared.FacetModel;
import org.talend.mdm.webapp.base.shared.SimpleTypeModel;
import org.talend.mdm.webapp.base.shared.TypeModel;
import org.talend.mdm.webapp.itemsbrowser2.client.creator.DataTypeCreator;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.amalto.webapp.core.dmagent.SchemaWebAgent;
import com.amalto.webapp.core.util.XtentisWebappException;
import com.amalto.webapp.util.webservices.WSConceptKey;
import com.amalto.webapp.util.webservices.WSDataModelPK;
import com.amalto.webapp.util.webservices.WSGetBusinessConceptKey;
import com.amalto.webapp.util.webservices.WSGetDataModel;
import com.sun.xml.xsom.XSElementDecl;
import com.sun.xml.xsom.XSFacet;
import com.sun.xml.xsom.XSModelGroup;
import com.sun.xml.xsom.XSParticle;
import com.sun.xml.xsom.XSRestrictionSimpleType;
import com.sun.xml.xsom.XSSchema;
import com.sun.xml.xsom.XSSchemaSet;
import com.sun.xml.xsom.XSType;
import com.sun.xml.xsom.parser.XSOMParser;
import com.sun.xml.xsom.util.DomAnnotationParserFactory;

public class DataModelHelper {

    private static final Logger logger = Logger.getLogger(DataModelHelper.class);

    private static final String SCHEMA_NAMESPACE = "http://www.w3.org/2001/XMLSchema"; //$NON-NLS-1$

    /**
     * DOC HSHU Comment method "parseSchema".
     * 
     * @param model
     * @param concept
     */
    public static void parseSchema(String model, String concept, EntityModel entityModel, List<String> roles) {
        entityModel.setConceptName(concept);
        // set pk
        entityModel.setKeys(getBusinessConceptKeys(model, concept));
        // analyst model
        XSElementDecl eleDecl = getBusinessConcept(model, concept);
        if (eleDecl != null) {
            travelXSElement(eleDecl, eleDecl.getName(), entityModel, null, roles);
        }
    }

    /**
     * DOC HSHU Comment method "getBusinessConcept".
     * 
     * @throws Exception
     */
    private static XSElementDecl getBusinessConcept(String model, String concept) {
        XSElementDecl eleDecl = null;
        try {
            if (!BaseConfiguration.isStandalone()) {
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
     * DOC HSHU Comment method "getElementDeclByName".
     * 
     * @param eleName
     * @param xss
     * @return
     */
    public static XSElementDecl getElementDeclByName(String eleName, XSSchemaSet xss) {
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

    /**
     * DOC HSHU Comment method "travelXSElement". go through XSElement
     * 
     * @param e
     * @param currentXPath
     */
    public static void travelXSElement(XSElementDecl e, String currentXPath, EntityModel entityModel,
            ComplexTypeModel parentTypeModel, List<String> roles) {
        if (e != null) {

            TypeModel typeModel = null;

            // parse element
            typeModel = parseElement(currentXPath, e, typeModel);

            // parse annotation
            if (typeModel != null)
                parseAnnotation(currentXPath, e, typeModel, roles);

            // set parent typeModel
            if (parentTypeModel != null)
                parentTypeModel.addSubType(typeModel);

            if (typeModel instanceof ComplexTypeModel)
                parentTypeModel = (ComplexTypeModel) typeModel;

            // add to entityModel
            if (typeModel != null)
                entityModel.getMetaDataTypes().put(currentXPath, typeModel);

            // recursion travel
            if (e.getType().isComplexType()) {
                XSModelGroup group = e.getType().asComplexType().getContentType().asParticle().getTerm().asModelGroup();
                if (group != null) {
                    XSParticle[] subParticles = group.getChildren();
                    if (subParticles != null) {
                        for (int i = 0; i < subParticles.length; i++) {
                            XSParticle xsParticle = subParticles[i];
                            travelParticle(xsParticle, currentXPath, entityModel, parentTypeModel, roles);
                        }
                    }
                }
            }

        }
    }

    private static TypeModel parseElement(String currentXPath, XSElementDecl e, TypeModel typeModel) {
        XSParticle currentXsp = null;
        String typeName = e.getType().getName();
        String baseTypeName;

        if (e.getType().isComplexType()) {
            baseTypeName = e.getType().getBaseType().getName();
            typeModel = new ComplexTypeModel(e.getName(), DataTypeCreator.getDataType(typeName, baseTypeName));
            currentXsp = e.getType().asComplexType().getContentType().asParticle();

        } else if (e.getType().isSimpleType()) {
            baseTypeName = getBuiltinPrimitiveType(e.getType()).getName();
            typeModel = new SimpleTypeModel(e.getName(), DataTypeCreator.getDataType(typeName, baseTypeName));
            currentXsp = e.getType().asSimpleType().asParticle();

            // enumeration&&facet
            XSRestrictionSimpleType restirctionType = e.getType().asSimpleType().asRestriction();
            ArrayList<FacetModel> restrictions = new ArrayList<FacetModel>();
            ArrayList<String> enumeration = new ArrayList<String>();
            if (restirctionType != null) {
                Iterator<XSFacet> it = restirctionType.iterateDeclaredFacets();
                while (it.hasNext()) {
                    XSFacet xsf = it.next();
                    if ("enumeration".equals(xsf.getName())) { //$NON-NLS-1$
                        enumeration.add(xsf.getValue().toString());
                    } else {
                        FacetModel r = new FacetModel(xsf.getName(), xsf.getValue().toString());
                        restrictions.add(r);
                    }
                }// end while
                ((SimpleTypeModel) typeModel).setEnumeration(enumeration);
                ((SimpleTypeModel) typeModel).setFacets(restrictions);
            }// end if

        } else {
            // return null
        }
        if (typeModel != null) {
            typeModel.setXpath(currentXPath);
            if (currentXsp != null) {
                typeModel.setMinOccurs(currentXsp.getMinOccurs());
                typeModel.setMaxOccurs(currentXsp.getMaxOccurs());
            }
            typeModel.setNillable(e.isNillable());
        }
        return typeModel;
    }

    private static XSType getBuiltinPrimitiveType(XSType type) {
        // See http://www.w3.org/TR/xmlschema-2/#built-in-primitive-datatypes
        // Suitable for simple types only
        assert type.isSimpleType();

        // Special handling for non-primitive type integer
        if (SCHEMA_NAMESPACE.equals(type.getTargetNamespace()) && "integer".equals(type.getName())) //$NON-NLS-1$
            return type;

        XSType baseType = type.getBaseType();
        assert baseType != null;
        // Drill down to get the primitive type
        if (!SCHEMA_NAMESPACE.equals(baseType.getTargetNamespace()) || !"anySimpleType".equals(baseType.getName())) { //$NON-NLS-1$
            baseType = getBuiltinPrimitiveType(baseType);
            return baseType;
        }
        return type;
    }

    private static void travelParticle(XSParticle xsParticle, String currentXPath, EntityModel entityModel,
            ComplexTypeModel parentTypeModel, List<String> roles) {
        if (xsParticle.getTerm().asModelGroup() != null) {
            XSParticle[] xsps = xsParticle.getTerm().asModelGroup().getChildren();
            for (int j = 0; j < xsps.length; j++) {
                travelParticle(xsps[j], currentXPath, entityModel, parentTypeModel, roles);
            }
        } else if (xsParticle.getTerm().asElementDecl() != null) {
            XSElementDecl subElement = xsParticle.getTerm().asElementDecl();
            travelXSElement(subElement, currentXPath + "/" + subElement.getName(), entityModel, parentTypeModel, roles);//$NON-NLS-1$
        }
    }

    private static void parseAnnotation(String currentXPath, XSElementDecl e, TypeModel typeModel, List<String> roles) {
        if (e.getAnnotation() != null && e.getAnnotation().getAnnotation() != null) {
            Element annotations = (Element) e.getAnnotation().getAnnotation();
            NodeList annotList = annotations.getChildNodes();
            ArrayList<String> pkInfoList = new ArrayList<String>();
            ArrayList<String> fkInfoList = new ArrayList<String>();
            for (int k = 0; k < annotList.getLength(); k++) {
                if ("appinfo".equals(annotList.item(k).getLocalName())) {//$NON-NLS-1$
                    Node source = annotList.item(k).getAttributes().getNamedItem("source");//$NON-NLS-1$
                    if (source == null)
                        continue;
                    String appinfoSource = source.getNodeValue();
                    if (annotList.item(k) != null && annotList.item(k).getFirstChild() != null) {
                        String appinfoSourceValue = annotList.item(k).getFirstChild().getNodeValue();
                        if (appinfoSource.contains("X_Label")) {//$NON-NLS-1$
                            typeModel.addLabel(getLangFromLabelAnnotation(appinfoSource), appinfoSourceValue);
                        } else if (appinfoSource.contains("X_Description")) {//$NON-NLS-1$
                            String description = appinfoSourceValue;
                            String encodedDESP = description != null ? StringEscapeUtils.escapeHtml(description) : "";//$NON-NLS-1$ //Do we need escape?
                            typeModel.addDescription(getLangFromDescAnnotation(appinfoSource), encodedDESP);
                        } else if ("X_Write".equals(appinfoSource)) {//$NON-NLS-1$
                            if (roles.contains(appinfoSourceValue)) {
                                typeModel.setReadOnly(false);
                            }
                        } else if ("X_Hide".equals(appinfoSource)) {//$NON-NLS-1$
                            if (roles.contains(appinfoSourceValue)) {
                                typeModel.setVisible(false);
                            }
                        } else if ("X_ForeignKey".equals(appinfoSource)) {//$NON-NLS-1$
                            typeModel.setForeignkey(appinfoSourceValue);
                        } else if ("X_Retrieve_FKinfos".equals(appinfoSource)) {//$NON-NLS-1$
                            typeModel.setRetrieveFKinfos("true".equals(appinfoSourceValue));//$NON-NLS-1$
                        } else if ("X_ForeignKeyInfo".equals(appinfoSource)) {//$NON-NLS-1$
                            fkInfoList.add(appinfoSourceValue);
                        } else if ("X_ForeignKey_Filter".equals(appinfoSource)) {//$NON-NLS-1$
                            typeModel.setFkFilter(appinfoSourceValue);
                        } else if ("X_PrimaryKeyInfo".equals(appinfoSource)) {//$NON-NLS-1$
                            pkInfoList.add(appinfoSourceValue);
                        } else if (appinfoSource.indexOf("X_Facet_") != -1) {//$NON-NLS-1$
                            typeModel.addFacetErrorMsg(getLangFromFacetAnnotation(appinfoSource), appinfoSourceValue);
                        } else if (appinfoSource.indexOf("X_Display_Format_") != -1) {//$NON-NLS-1$
                            typeModel.addDisplayFomat(getLangFromDisplayAnnotation(appinfoSource), appinfoSourceValue);
                        } else if ("X_Deny_Create".equals(appinfoSource)) {//$NON-NLS-1$
                            if (roles.contains(appinfoSourceValue)) {
                                typeModel.setDenyCreatable(true);
                            }
                        } else if ("X_Deny_LogicalDelete".equals(appinfoSource)) {//$NON-NLS-1$
                            if (roles.contains(appinfoSourceValue)) {
                                typeModel.setDenyLogicalDeletable(true);
                            }
                        } else if ("X_Deny_PhysicalDelete".equals(appinfoSource)) {//$NON-NLS-1$
                            if (roles.contains(appinfoSourceValue)) {
                                typeModel.setDenyPhysicalDeleteable(true);
                            }
                        }

                    }

                }
            }// end for
            typeModel.setForeignKeyInfo(fkInfoList);
            typeModel.setPrimaryKeyInfo(pkInfoList);
        }
    }

    /**
     * DOC HSHU Comment method "getLangFromLabelAnnotation".
     */
    private static String getLangFromLabelAnnotation(String label) {
        String format = "X_Label_(.+)";//$NON-NLS-1$
        String lang = getLangFromAnnotation(label, format);
        return lang;
    }

    private static String getLangFromDescAnnotation(String label) {
        String format = "X_Description_(.+)";//$NON-NLS-1$
        String lang = getLangFromAnnotation(label, format);
        return lang;
    }

    private static String getLangFromFacetAnnotation(String label) {
        String format = "X_Facet_(.+)";//$NON-NLS-1$
        String lang = getLangFromAnnotation(label, format);
        return lang;
    }

    private static String getLangFromDisplayAnnotation(String label) {
        String format = "X_Display_(.+)";//$NON-NLS-1$
        String lang = getLangFromAnnotation(label, format);
        return lang;
    }

    private static String getLangFromAnnotation(String label, String format) {
        String lang = "EN";//$NON-NLS-1$
        Pattern p = Pattern.compile(format);
        Matcher matcher = p.matcher(label);
        while (matcher.find()) {
            lang = matcher.group(1);
        }
        if (lang != null)
            lang = lang.toLowerCase();
        return lang;
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

            WSConceptKey copyKey = new WSConceptKey();
            copyKey.setFields((String[]) ArrayUtils.clone(key.getFields()));
            copyKey.setSelector(new String(key.getSelector()));

            keys = copyKey.getFields();
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
