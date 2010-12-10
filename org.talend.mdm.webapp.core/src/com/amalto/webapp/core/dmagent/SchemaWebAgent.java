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
package com.amalto.webapp.core.dmagent;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import org.talend.mdm.commmon.util.datamodel.management.BusinessConcept;
import org.talend.mdm.commmon.util.datamodel.management.DataModelBean;
import org.talend.mdm.commmon.util.datamodel.management.DataModelID;
import org.talend.mdm.commmon.util.datamodel.management.ReusableType;
import org.talend.mdm.commmon.util.datamodel.management.SchemaManager;

import com.amalto.webapp.core.bean.Configuration;
import com.amalto.webapp.core.util.Util;
import com.amalto.webapp.core.util.XtentisWebappException;
import com.amalto.webapp.util.webservices.WSDataModel;
import com.amalto.webapp.util.webservices.WSDataModelPK;
import com.amalto.webapp.util.webservices.WSGetCurrentUniverse;
import com.amalto.webapp.util.webservices.WSGetDataModel;
import com.amalto.webapp.util.webservices.WSUniverse;
import com.amalto.webapp.util.webservices.WSUniverseXtentisObjectsRevisionIDs;

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
        DataModelBean dataModelBean = DataModelWebPool.getUniqueInstance().remove(dataModelID);
        dataModelBean = null;
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
     * DOC HSHU Comment method "getSubtypes".
     * 
     * @param parentTypeName
     * @throws Exception
     */
    public List<ReusableType> getMySubtypes(String parentTypeName) throws Exception {
        List<ReusableType> subTypes = new ArrayList<ReusableType>();

        DataModelBean dataModelBean = getFromPool(getMyDataModelTicket());
        List<ReusableType> reusableTypes = dataModelBean.getReusableTypes();
        for (ReusableType reusableType : reusableTypes) {
            if (reusableType.getParentName() != null && reusableType.getParentName().equals(parentTypeName)) {
                subTypes.add(reusableType);
            }
        }
        return subTypes;

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
        Configuration config = Configuration.getInstance();
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
            if (objectName != null && objectName.equals("Data Model"))
                revision = wsUniverseXtentisObjectsRevisionIDs.getRevisionID();
        }

        return revision;
    }

}
