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
package com.amalto.webapp.core.dmagent;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.talend.mdm.commmon.util.datamodel.management.BusinessConcept;
import org.talend.mdm.commmon.util.datamodel.management.DataModelBean;
import org.talend.mdm.commmon.util.datamodel.management.DataModelID;
import org.talend.mdm.commmon.util.datamodel.management.ReusableType;
import org.talend.mdm.commmon.util.datamodel.management.SchemaManager;
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
import com.sun.xml.xsom.XSElementDecl;
import com.sun.xml.xsom.XSParticle;
/**
 * DOC HSHU class global comment. Detailled comment
 */
public class SchemaWebAgent extends SchemaManager {

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
     * @param typeName
     * @return
     * @throws Exception
     */
    public BusinessConcept getFirstBusinessConceptFromRootType(String typeName) throws Exception {
        BusinessConcept targetBusinessConcept = null;
        DataModelID dataModelID = getMyDataModelTicket();
        List<BusinessConcept> businessConcepts=super.getBusinessConcepts(dataModelID);
        for (BusinessConcept businessConcept : businessConcepts) {
            String businessConceptTypeName = businessConcept.getCorrespondTypeName();
            if (businessConceptTypeName!= null && businessConceptTypeName.equals(typeName)) {
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
     * @param parentTypeName
     * @return
     * @throws Exception
     */
    public List<ReusableType> getMySubtypes(String parentTypeName) throws Exception{
        return getMySubtypes(parentTypeName,false);
    }
    
    /**
     * DOC HSHU Comment method "getMySubtypes".
     * @param parentTypeName
     * @param deep
     * @return
     * @throws Exception
     */
    public List<ReusableType> getMySubtypes(String parentTypeName, boolean deep) throws Exception {
        List<ReusableType> subTypes = new ArrayList<ReusableType>();

        DataModelBean dataModelBean = getFromPool(getMyDataModelTicket());
        
        List<ReusableType> reusableTypes = dataModelBean.getReusableTypes();
        
        setMySubtypes(parentTypeName, subTypes, reusableTypes, deep);
        
        return subTypes;

    }

    public List<ReusableType> getMyParents(String subTypeName) throws Exception {
        DataModelBean dataModelBean = getFromPool(getMyDataModelTicket());
        List<ReusableType> reusableTypes = dataModelBean.getReusableTypes();
        List<ReusableType> parentsTypes = new ArrayList<ReusableType>();
        
        getParents(subTypeName, reusableTypes, parentsTypes);

        return parentsTypes;
    }
    
    private void getParents(String subTypeName, List<ReusableType> reusableTypes, List<ReusableType> parentsTypes) {
        for(ReusableType reusableType : reusableTypes) {
            if(reusableType.getName().equals(subTypeName)) {
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
     * DOC HSHU Comment method "setMySubtypes".
     * @param parentTypeName
     * @param subTypes
     * @param reusableTypes
     * @param deep
     */
    private void setMySubtypes(String parentTypeName, List<ReusableType> subTypes, List<ReusableType> reusableTypes, boolean deep) {
        List<String> checkList=new ArrayList<String>();
        
        for (ReusableType reusableType : reusableTypes) {
               if (reusableType.getParentName() != null && reusableType.getParentName().equals(parentTypeName)) {    
                       subTypes.add(reusableType);
                       checkList.add(reusableType.getName());
               }
        }
        
        if(deep) {
            if(checkList.size()>0) {
                for (String storedTypeName : checkList) {
                    setMySubtypes(storedTypeName, subTypes, reusableTypes,deep);
                }
            }
        }
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
     * @throws Exception 
     */
    public List<String> getReferenceEntities(String entityName) throws Exception {
        List<String> references=new ArrayList<String>();
        DataModelID dataModelID = getMyDataModelTicket();
        //check business concepts
        List<BusinessConcept> businessConcepts = getBusinessConcepts(dataModelID);
        List<String> extendType = new ArrayList<String>();
        extendType.add(entityName);

        for (BusinessConcept businessConcept : businessConcepts) {
            businessConcept.load();
            String bcName=businessConcept.getName();

            if (bcName.equals(entityName)) {
                List<ReusableType> parentTypes = getMyParents(getBusinessConcept(bcName).getCorrespondTypeName());
                List<String> parentConcept = new ArrayList<String>();
                for (ReusableType type : parentTypes) {
                    parentConcept.add(type.getName());
                }

                for (BusinessConcept concept : businessConcepts) {
                    if (parentConcept.contains(concept.getCorrespondTypeName())) {
                        extendType.add(concept.getName());
                    }
                }
            }

            Map<String, String> foreignKeyMap=businessConcept.getForeignKeyMap();
            Collection<String> fkPaths = foreignKeyMap.values();
            List<String> types = getBindingType(businessConcept.getE());

            for (String type : types) {
                List<ReusableType> subTypes = getMySubtypes(type);
                for (ReusableType reusableType : subTypes) {
                    Map<String, String> fk = getReferenceEntities(reusableType, entityName);
                    if (fk != null && fk.size() > 0) {
                        references.add(bcName);
                    }
                }
            }

            for (String fkPath : fkPaths) {
                if (isFkPoint2Entity(fkPath, extendType)) {
                    if(!references.contains(bcName))references.add(bcName);
                }
            }
        }
        
        return references;

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
    public boolean isEntityDenyPhysicalDeletable(String concept)throws Exception{
    	Configuration config = Configuration.getInstance();
    	Map<String,XSElementDecl> conceptMap=CommonDWR.getConceptMap(config.getModel());
        XSElementDecl decl = conceptMap.get(concept);
        if (decl == null) {
            //String err = "Concept '" + concept + "' is not found in model '" + config.getModel() + "'";
            return false;
        }
        XSAnnotation xsa = decl.getAnnotation();

        ArrayList<String> roles = Util.getAjaxSubject().getRoles();
        if (xsa != null && xsa.getAnnotation() != null) {
            Element el = (Element) xsa.getAnnotation();
            NodeList annotList = el.getChildNodes();

            for (int k = 0; k < annotList.getLength(); k++) {
                if ("appinfo".equals(annotList.item(k).getLocalName())) {
                    Node source = annotList.item(k).getAttributes().getNamedItem("source");
                    if (source == null)
                        continue;
                    String appinfoSource = annotList.item(k).getAttributes().getNamedItem("source").getNodeValue();
                    if ("X_Deny_PhysicalDelete".equals(appinfoSource)) {
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
    public boolean isEntityDenyCreatable(String concept)throws Exception{
    	Configuration config = Configuration.getInstance();
    	Map<String,XSElementDecl> conceptMap=CommonDWR.getConceptMap(config.getModel());
        XSElementDecl decl = conceptMap.get(concept);
        if (decl == null) {
            //String err = "Concept '" + concept + "' is not found in model '" + config.getModel() + "'";
            return false;
        }
        XSAnnotation xsa = decl.getAnnotation();

        ArrayList<String> roles = Util.getAjaxSubject().getRoles();
        if (xsa != null && xsa.getAnnotation() != null) {
            Element el = (Element) xsa.getAnnotation();
            NodeList annotList = el.getChildNodes();

            for (int k = 0; k < annotList.getLength(); k++) {
                if ("appinfo".equals(annotList.item(k).getLocalName())) {
                    Node source = annotList.item(k).getAttributes().getNamedItem("source");
                    if (source == null)
                        continue;
                    String appinfoSource = annotList.item(k).getAttributes().getNamedItem("source").getNodeValue();
                    if ("X_Deny_Create".equals(appinfoSource)) {
                        if (roles.contains(annotList.item(k).getFirstChild().getNodeValue())) {
                           return true;
                        }
                    }
                }
            }
        }
    	return false;
    }    
}
