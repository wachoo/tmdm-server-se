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
package com.amalto.webapp.core.dmagent;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.talend.mdm.commmon.util.datamodel.management.BusinessConcept;
import org.talend.mdm.commmon.util.datamodel.management.DataModelBean;
import org.talend.mdm.commmon.util.datamodel.management.DataModelID;
import org.talend.mdm.commmon.util.datamodel.management.ReusableType;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.amalto.webapp.core.bean.Configuration;
import com.amalto.webapp.core.dwr.CommonDWR;
import com.amalto.webapp.core.util.Util;
import com.amalto.webapp.core.util.XtentisWebappException;
import com.amalto.webapp.util.webservices.WSDataModel;
import com.amalto.webapp.util.webservices.WSDataModelPK;
import com.amalto.webapp.util.webservices.WSGetCurrentUniverse;
import com.amalto.webapp.util.webservices.WSGetDataModel;
import com.amalto.webapp.util.webservices.WSUniverse;
import com.amalto.webapp.util.webservices.WSUniverseXtentisObjectsRevisionIDs;
import com.sun.xml.xsom.XSAnnotation;
import com.sun.xml.xsom.XSComplexType;
import com.sun.xml.xsom.XSElementDecl;
import com.sun.xml.xsom.XSParticle;

/**
 * DOC HSHU class global comment. Detailled comment
 */
public class SchemaWebAgent extends SchemaAbstractWebAgent {

    private static SchemaWebAgent agent;

    /**
     * DOC HSHU SchemaWebAgent constructor comment.
     */
    private SchemaWebAgent() {

    }

    /**
     * DOC HSHU Comment method "getInstance".
     * 
     * @return
     */
    public static SchemaWebAgent getInstance() {

        if (agent == null)

            agent = new SchemaWebAgent();

        return agent;

    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.talend.mdm.commmon.util.datamodel.management.SchemaManager#addToPool(org.talend.mdm.commmon.util.datamodel
     * .management.DataModelID, org.talend.mdm.commmon.util.datamodel.management.DataModelBean)
     */
    @Override
    protected void addToPool(DataModelID dataModelID, DataModelBean dataModelBean) {
        DataModelWebPool.getUniqueInstance().put(dataModelID, dataModelBean);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.talend.mdm.commmon.util.datamodel.management.SchemaManager#existInPool(org.talend.mdm.commmon.util.datamodel
     * .management.DataModelID)
     */
    @Override
    protected boolean existInPool(DataModelID dataModelID) {
        DataModelBean dataModelBean = DataModelWebPool.getUniqueInstance().get(dataModelID);
        return !(dataModelBean == null);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.talend.mdm.commmon.util.datamodel.management.SchemaManager#removeFromPool(org.talend.mdm.commmon.util.datamodel
     * .management.DataModelID)
     */
    @Override
    protected void removeFromPool(DataModelID dataModelID) {
        DataModelWebPool.getUniqueInstance().remove(dataModelID);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.talend.mdm.commmon.util.datamodel.management.SchemaManager#getFromPool(org.talend.mdm.commmon.util.datamodel
     * .management.DataModelID)
     */
    @Override
    protected DataModelBean getFromPool(DataModelID dataModelID) throws Exception {
        DataModelBean dataModelBean = DataModelWebPool.getUniqueInstance().get(dataModelID);
        if (dataModelBean == null) {

            boolean matchedRevison = false;
            String targetRevision = dataModelID.getRevisionID();
            String currentRevision = getMyDatamodelRevision();
            if (currentRevision == null) {
                if (targetRevision == null)
                    matchedRevison = true;
            } else {
                if (targetRevision != null && targetRevision.equals(currentRevision))
                    matchedRevison = true;
            }

            if (matchedRevison) {
                // reload it
                WSDataModel wsDataModel = Util.getPort().getDataModel(
                        new WSGetDataModel(new WSDataModelPK(dataModelID.getUniqueID())));
                String xsdSchema = wsDataModel.getXsdSchema();
                dataModelBean = updateToDatamodelPool(dataModelID.getRevisionID(), dataModelID.getUniqueID(), xsdSchema);
            } else {
                // FIXME:NPE will happened here
            }

        }
        return dataModelBean;
    }

    /**
     * DOC HSHU Comment method "getBusinessConcept".
     * 
     * @param dataModelID
     * @return
     * @throws Exception
     */
    public BusinessConcept getBusinessConcept(String conceptName) throws Exception {

        DataModelID dataModelID = getMyDataModelTicket();
        return super.getBusinessConcept(conceptName, dataModelID);

    }

    /**
     * DOC HSHU Comment method "getFirstBusinessConceptFromRootType".
     * 
     * @param typeName
     * @return
     * @throws Exception
     */
    public BusinessConcept getFirstBusinessConceptFromRootType(String typeName) throws Exception {
        BusinessConcept targetBusinessConcept = null;
        DataModelID dataModelID = getMyDataModelTicket();
        List<BusinessConcept> businessConcepts = super.getBusinessConcepts(dataModelID);
        for (BusinessConcept businessConcept : businessConcepts) {
            String businessConceptTypeName = businessConcept.getCorrespondTypeName();
            if (businessConceptTypeName != null && businessConceptTypeName.equals(typeName)) {
                targetBusinessConcept = businessConcept;
                break;
            }
        }
        return targetBusinessConcept;
    }

    /**
     * DOC HSHU Comment method "getReusableType".
     * 
     * @param typeName
     * @return
     * @throws Exception
     */
    public ReusableType getReusableType(String typeName) throws Exception {

        DataModelID dataModelID = getMyDataModelTicket();
        return super.getReusableType(typeName, dataModelID);

    }

    /**
     * DOC HSHU Comment method "getMySubtypes".
     * 
     * @param parentTypeName
     * @return
     * @throws Exception
     */
    public List<ReusableType> getMySubtypes(String parentTypeName) throws Exception {
        return getMySubtypes(parentTypeName, false);
    }

    /**
     * DOC HSHU Comment method "getMySubtypes".
     * 
     * @param parentTypeName
     * @param deep
     * @return
     * @throws Exception
     */
    public List<ReusableType> getMySubtypes(String parentTypeName, boolean deep) throws Exception {
        return getMySubtypes(parentTypeName, deep, getFromPool(getMyDataModelTicket()));
    }


    public List<ReusableType> getMyParents(String subTypeName) throws Exception {
        DataModelBean dataModelBean = getFromPool(getMyDataModelTicket());
        List<ReusableType> reusableTypes = dataModelBean.getReusableTypes();
        List<ReusableType> parentsTypes = new ArrayList<ReusableType>();

        getParents(subTypeName, reusableTypes, parentsTypes);

        return parentsTypes;
    }

    private void getParents(String subTypeName, List<ReusableType> reusableTypes, List<ReusableType> parentsTypes) {
        for (ReusableType reusableType : reusableTypes) {
            if (reusableType.getName().equals(subTypeName)) {
                String parentName = reusableType.getParentName();
                ReusableType type = findParentType(parentName, reusableTypes);
                if (type != null) {
                    parentsTypes.add(type);
                    getParents(parentName, reusableTypes, parentsTypes);
                }
            }
        }
    }

    private ReusableType findParentType(String parName, List<ReusableType> reusableTypes) {
        ReusableType resuable = null;

        for (ReusableType reusableType : reusableTypes) {
            if (reusableType.getName().equals(parName)) {
                resuable = reusableType;
                break;
            }
        }

        return resuable;
    }



    /**
     * DOC HSHU Comment method "isMySubType".
     * 
     * @throws Exception
     */
    public boolean isMySubType(String parentTypeName, String subTypeName) throws Exception {

        List<ReusableType> subTypes = getMySubtypes(parentTypeName);

        return subTypes.contains(new ReusableType(subTypeName));

    }

    /**
     * DOC HSHU Comment method "getMyDataModelTicket".
     * 
     * @return
     * @throws Exception
     * @throws RemoteException
     * @throws XtentisWebappException
     */
    private DataModelID getMyDataModelTicket() throws Exception {
        Configuration config = Configuration.getConfiguration();
        String dataModelPK = config.getModel();
        String dataModelRevision = getMyDatamodelRevision();
        DataModelID dataModelID = new DataModelID(dataModelPK, dataModelRevision);
        return dataModelID;
    }

    /**
     * DOC HSHU Comment method "getMyDatamodelRevision".
     * 
     * @return
     * @throws RemoteException
     * @throws XtentisWebappException
     */
    private String getMyDatamodelRevision() throws RemoteException, XtentisWebappException {

        String revision = null;

        WSUniverse wsUniverse = Util.getPort().getCurrentUniverse(new WSGetCurrentUniverse());
        if (wsUniverse == null)
            return revision;

        WSUniverseXtentisObjectsRevisionIDs[] wsUniverseXtentisObjectsRevisionIDsArray = wsUniverse
                .getXtentisObjectsRevisionIDs();
        for (WSUniverseXtentisObjectsRevisionIDs wsUniverseXtentisObjectsRevisionIDs : wsUniverseXtentisObjectsRevisionIDsArray) {
            String objectName = wsUniverseXtentisObjectsRevisionIDs.getXtentisObjectName();
            if (objectName != null && objectName.equals("Data Model")) //$NON-NLS-1$
                revision = wsUniverseXtentisObjectsRevisionIDs.getRevisionID();
        }

        return revision;
    }

    public Map<String, String> getReferenceEntities(ReusableType reusableType, String entityName) throws Exception {
        Map<String, String> references = new HashMap<String, String>();
        XSParticle xsparticle = reusableType.getXsParticle();
        List<XSParticle> xsps = reusableType.getAllChildren(xsparticle);
        String reusableName = reusableType.getName();

        if (xsps.size() > 0) {
            for (XSParticle xsp : xsps) {
                XSAnnotation xsa = xsp.getTerm().asElementDecl() == null ? null : xsp.getTerm().asElementDecl().getAnnotation();
                String name = xsp.getTerm().asElementDecl().getName();
                if (xsa != null && xsa.getAnnotation() != null) {
                    Element el = (Element) xsa.getAnnotation();
                    NodeList annotList = el.getChildNodes();
                    for (int k = 0; k < annotList.getLength(); k++) {
                        if ("appinfo".equals(annotList.item(k).getLocalName())) { //$NON-NLS-1$
                            Node source = annotList.item(k).getAttributes().getNamedItem("source"); //$NON-NLS-1$
                            if (source == null)
                                continue;
                            String appinfoSource = annotList.item(k).getAttributes().getNamedItem("source").getNodeValue(); //$NON-NLS-1$
                            if ("X_ForeignKey".equals(appinfoSource)) { //$NON-NLS-1$
                                String nodeValue = annotList.item(k).getFirstChild().getNodeValue();
                                if (nodeValue.startsWith(entityName)) {
                                    references.put(reusableName + "/" + name, nodeValue); //$NON-NLS-1$
                                }
                            }
                        }
                    }
                }
            }
        }

        return references;
    }

    /**
     * DOC HSHU Comment method "getReferenceEntities".
     * 
     * @throws Exception
     */
    public List<String> getReferenceEntities(String entityName) throws Exception {

        List<String> references = new ArrayList<String>();
        DataModelID dataModelID = getMyDataModelTicket();
        DataModelBean dataModelBean = getFromPool(dataModelID);
        List<BusinessConcept> businessConcepts = dataModelBean.getBusinessConcepts();
        List<ReusableType> reuseTypeList = dataModelBean.getReusableTypes();
        Map<String, ReusableType> reusableTypeMap = dataModelBean.getReusableTypeMap();

        // Pre-load Reusable Types
        for (ReusableType type : reuseTypeList) {
            type.load(reusableTypeMap);// load/parse the reusableType
        }

        // Find possible ReusableTypes which has FK to point to the target entity
        Map<String,ReusableType> possibleReusableTypeMap = new HashMap<String,ReusableType>();
        for (ReusableType type : reuseTypeList) {
            Map<String, String> foreignKeyMap = type.getForeignKeyMap();
            // does this reUsableType point to the target entity
            if (foreignKeyMap != null && foreignKeyMap.size() > 0) {
                for (Iterator<String> iterator = foreignKeyMap.keySet().iterator(); iterator.hasNext();) {
                    String xpathOnEntity = (String) iterator.next();
                    String fkpath = foreignKeyMap.get(xpathOnEntity);
                    String myEntityName=getEntityNameFromXPath(fkpath);
                    if (isValidatedEntityName(myEntityName, dataModelBean) && myEntityName.equals(entityName)) {
                        // if true, add it to the possible map
                        possibleReusableTypeMap.put(getEntityNameFromXPath(xpathOnEntity), type);
                        // find all types which has this type
                        List<ReusableType> containTypes = getMyContainsType(reuseTypeList, type);
                        if (containTypes != null && containTypes.size() > 0) {
                            for (ReusableType containType : containTypes) {
                                possibleReusableTypeMap.put(containType.getName(), containType);
                            }
                        }
                        // also find all parent types if have
                        List<ReusableType> myParentTypes = getMyParents(type.getName());
                        if (myParentTypes != null && myParentTypes.size() > 0) {
                            for (ReusableType parentType : myParentTypes) {
                                possibleReusableTypeMap.put(parentType.getName(), parentType);
                            }
                        }
                    }

                }
            }
        }

        // Add possible entities to the result set based on the business concept schema
        for (BusinessConcept businessConcept : businessConcepts) {
            businessConcept.setReuseTypeList(reuseTypeList);
            businessConcept.load();

            Collection<String> fkPaths = null;
            Map<String, String> myForeignKeyMap = businessConcept.getForeignKeyMap();
            if (myForeignKeyMap != null)
                fkPaths = myForeignKeyMap.values();

            if (fkPaths != null) {
                for (String fkPath : fkPaths) {
                    // if the fkpath is pointing to this entity
                    if (fkPath != null && getEntityNameFromXPath(fkPath) != null
                            && getEntityNameFromXPath(fkPath).equals(entityName)) {

                        if (!references.contains(businessConcept.getName()))
                            references.add(businessConcept.getName());
                    }

                }// end for
            }// end if
        }

        // Add possible entities to the result set based on the reusable type schema
        // Imply possible entities based on a possible reusableTypeList
        for (Iterator<String> iterator = possibleReusableTypeMap.keySet().iterator(); iterator.hasNext();) {
            String theTypeName = (String) iterator.next();
            // ReusableType theReusableType = possibleReusableTypeMap.get(theTypeName);

            // if we could find the corresponding entities of this reusable type then add it/them to the list
            for (BusinessConcept businessConcept : businessConcepts) {
                // check root type
                if (businessConcept.getCorrespondTypeName() != null
                        && businessConcept.getCorrespondTypeName().equals(theTypeName)) {
                    if (!references.contains(businessConcept.getName()))
                        references.add(businessConcept.getName());
                    continue;
                }// end if
                
                // check children node via a flat result map
                Map<String, ReusableType> subTypesOfTargetEntity = businessConcept.getSubReuseTypeMap();
                for (Iterator<String> iterator2 = subTypesOfTargetEntity.keySet().iterator(); iterator2.hasNext();) {
                    String keyPath = (String) iterator2.next();
                    if (subTypesOfTargetEntity.get(keyPath) != null
                            && subTypesOfTargetEntity.get(keyPath).getName().equals(theTypeName)) {
                        if (!references.contains(businessConcept.getName()))
                            references.add(businessConcept.getName());
                        break;
                    }// end if
                }// end for
            }
        }

        return references;

    }

    /**
     * @param reuseTypeList
     * @param type
     * @return
     */
    private List<ReusableType> getMyContainsType(List<ReusableType> reuseTypeList, ReusableType type) {
        List<ReusableType> containTypes = new ArrayList<ReusableType>();
        for (ReusableType mytype : reuseTypeList) {
            if (mytype.getxPathReusableTypeMap() != null && mytype.getxPathReusableTypeMap().size() > 0) {
                for (ReusableType toCheckType : mytype.getxPathReusableTypeMap().values()) {
                    if (toCheckType != null && toCheckType.getName() != null && toCheckType.getName().equals(type.getName()))
                        containTypes.add(toCheckType);
                }
            }
        }
        return containTypes;
    }

    /**
     * DOC Starkey Comment method "isValidatedEntityName".
     * 
     * @param entityName
     * @param dataModelBean
     * @return
     */
    private boolean isValidatedEntityName(String entityName,DataModelBean dataModelBean) {
        
        if(entityName==null||entityName.trim().length()==0)
            return false;
        
        if(dataModelBean.getBusinessConceptMap().keySet()!=null&&dataModelBean.getBusinessConceptMap().keySet().contains(entityName))
            return true;

        return false;

    }

    /**
     * DOC Starkey Comment method "getEntityNameFromXPath".
     * 
     * @param xpath
     * @return
     */
    private String getEntityNameFromXPath(String xpath) {

        if (xpath == null || xpath.trim().length() == 0)
            return null;

        if (xpath.startsWith("//")) //$NON-NLS-1$
            xpath = xpath.substring(2);
        else if (xpath.startsWith("/")) //$NON-NLS-1$
            xpath = xpath.substring(1);

        String[] xPortions = xpath.split("/"); //$NON-NLS-1$

        String entityName = null;

        if (xPortions != null && xPortions.length > 0) {
            entityName = xPortions[0];
        }

        return entityName;
    }

    public List<String> getBindingType(XSElementDecl e) throws Exception {
        List<String> types = new ArrayList<String>();

        if (e.getType().isComplexType()) {
            XSParticle[] subParticles = e.getType().asComplexType().getContentType().asParticle().getTerm().asModelGroup()
                    .getChildren();
            if (subParticles != null) {
                for (int i = 0; i < subParticles.length; i++) {
                    XSParticle xsParticle = subParticles[i];
                    if (xsParticle.getTerm().asElementDecl() == null) {
                        continue;
                    }
                    String type = xsParticle.getTerm().asElementDecl().getType().getName();
                    List<ReusableType> subTypes = getMySubtypes(type, true);
                    if (subTypes.size() > 0) {
                        types.add(type);
                    }
                }
            }
        }

        return types;
    }

    private boolean isFkPoint2Entity(String fkPath, List<String> extendType) {
        if (fkPath == null || fkPath.length() == 0)
            return false;
        if (fkPath.startsWith("/")) //$NON-NLS-1$
            fkPath = fkPath.substring(1);
        boolean contained = false;
        for (String type : extendType) {
            if (fkPath.startsWith(type + "/") || fkPath.equals(type)) //$NON-NLS-1$
                contained = true;
        }

        return contained;
    }

    /**
     * 
     * @param concept
     * @return
     * @throws Exception
     */
    public boolean isEntityDenyPhysicalDeletable(String concept) throws Exception {
        Configuration config = Configuration.getInstance();
        Map<String, XSElementDecl> conceptMap = CommonDWR.getConceptMap(config.getModel());
        XSElementDecl decl = conceptMap.get(concept);
        if (decl == null) {
            return false;
        }
        XSAnnotation xsa = decl.getAnnotation();

        ArrayList<String> roles = Util.getAjaxSubject().getRoles();
        if (xsa != null && xsa.getAnnotation() != null) {
            Element el = (Element) xsa.getAnnotation();
            NodeList annotList = el.getChildNodes();

            for (int k = 0; k < annotList.getLength(); k++) {
                if ("appinfo".equals(annotList.item(k).getLocalName())) { //$NON-NLS-1$
                    Node source = annotList.item(k).getAttributes().getNamedItem("source"); //$NON-NLS-1$
                    if (source == null)
                        continue;
                    String appinfoSource = annotList.item(k).getAttributes().getNamedItem("source").getNodeValue(); //$NON-NLS-1$
                    if ("X_Deny_PhysicalDelete".equals(appinfoSource)) { //$NON-NLS-1$
                        if (roles.contains(annotList.item(k).getFirstChild().getNodeValue())) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    /**
     * 
     * @param concept
     * @return
     * @throws Exception
     */
    public boolean isEntityDenyCreatable(String concept) throws Exception {
        Configuration config = Configuration.getInstance();
        Map<String, XSElementDecl> conceptMap = CommonDWR.getConceptMap(config.getModel());
        XSElementDecl decl = conceptMap.get(concept);
        if (decl == null) {
            return false;
        }
        XSAnnotation xsa = decl.getAnnotation();

        ArrayList<String> roles = Util.getAjaxSubject().getRoles();
        if (xsa != null && xsa.getAnnotation() != null) {
            Element el = (Element) xsa.getAnnotation();
            NodeList annotList = el.getChildNodes();

            for (int k = 0; k < annotList.getLength(); k++) {
                if ("appinfo".equals(annotList.item(k).getLocalName())) { //$NON-NLS-1$
                    Node source = annotList.item(k).getAttributes().getNamedItem("source"); //$NON-NLS-1$
                    if (source == null)
                        continue;
                    String appinfoSource = annotList.item(k).getAttributes().getNamedItem("source").getNodeValue(); //$NON-NLS-1$
                    if ("X_Deny_Create".equals(appinfoSource)) { //$NON-NLS-1$
                        if (roles.contains(annotList.item(k).getFirstChild().getNodeValue())) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    /**
     * 
     * DOC achen Comment method "getXPath2ParticleMap".
     * 
     * @param concept
     * @return
     * @throws Exception
     */
    public Map<String, XSParticle> getXPath2ParticleMap(String concept) throws Exception {
        Configuration config = Configuration.getInstance();
        Map<String, XSElementDecl> conceptMap = CommonDWR.getConceptMap(config.getModel());
        XSComplexType xsct = (XSComplexType) (conceptMap.get(concept).getType());
        XSParticle[] xsp = xsct.getContentType().asParticle().getTerm().asModelGroup().getChildren();
        HashMap<String, XSParticle> xpathToParticle = new HashMap<String, XSParticle>();
        for (int j = 0; j < xsp.length; j++) {
            getChildrenXpath2Particle(xsp[j], concept, xpathToParticle);
        }
        return xpathToParticle;
    }

    private void getChildrenXpath2Particle(XSParticle xsp, String xpathParent, HashMap<String, XSParticle> xpathToParticle) {
        if (xsp.getTerm().asModelGroup() != null) { // is complex type
            XSParticle[] xsps = xsp.getTerm().asModelGroup().getChildren();
            for (int i = 0; i < xsps.length; i++) {
                getChildrenXpath2Particle(xsps[i], xpathParent, xpathToParticle);
            }
        }
        if (xsp.getTerm().asElementDecl() == null)
            return;
        String xpath = xpathParent + "/" + xsp.getTerm().asElementDecl().getName(); //$NON-NLS-1$
        if (xsp.getTerm().asElementDecl().getType().isComplexType()) {
            XSParticle particle = xsp.getTerm().asElementDecl().getType().asComplexType().getContentType().asParticle();
            if (particle != null) {
                XSParticle[] xsps = particle.getTerm().asModelGroup().getChildren();
                String pxpath = xpathParent + "/" + xsp.getTerm().asElementDecl().getName(); //$NON-NLS-1$
                xpathToParticle.put(pxpath, xsp);
                for (int i = 0; i < xsps.length; i++) {
                    getChildrenXpath2Particle(xsps[i], pxpath, xpathToParticle);
                }
            }
        } else {
            xpathToParticle.put(xpath, xsp);
        }

    }
    
    public List<BusinessConcept> getAllBusinessConcepts() throws Exception {
        return super.getBusinessConcepts(getMyDataModelTicket());
    }
}
