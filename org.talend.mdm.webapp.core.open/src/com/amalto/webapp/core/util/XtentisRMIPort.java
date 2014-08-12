package com.amalto.webapp.core.util;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import com.amalto.core.objects.role.ejb.RolePOJO;
import com.amalto.core.objects.role.ejb.RolePOJOPK;
import com.amalto.core.objects.role.ejb.local.RoleCtrlLocal;
import com.amalto.core.util.XConverter;
import com.amalto.core.webservice.*;

/**
 * The list of web services implemented as RMI local calls
 * 
 * @author Bruno Grieder
 * 
 */
@SuppressWarnings({ "deprecation", "unchecked" })
public class XtentisRMIPort extends IXtentisRMIPort {

    /**
	 *  
	 */
    public XtentisRMIPort() {
        super();
        org.apache.log4j.Logger.getLogger(this.getClass()).trace("XtentisRMIPort() Using RMI");
    }

    public WSRolePK deleteRole(WSDeleteRole wsRoleDelete) throws RemoteException {
        
        return null;
    }

    public WSSynchronizationItemPK deleteSynchronizationItem(WSDeleteSynchronizationItem wsSynchronizationItemDelete)
            throws RemoteException {
        
        return null;
    }

    public WSSynchronizationPlanPK deleteSynchronizationPlan(WSDeleteSynchronizationPlan wsSynchronizationPlanDelete)
            throws RemoteException {
        
        return null;
    }

    public WSUniversePK deleteUniverse(WSDeleteUniverse wsUniverseDelete) throws RemoteException {
        
        return null;
    }

    public WSBoolean existsRole(WSExistsRole wsExistsRole) throws RemoteException {
        
        return null;
    }

    public WSBoolean existsSynchronizationItem(WSExistsSynchronizationItem wsExistsSynchronizationItem) throws RemoteException {
        
        return null;
    }

    public WSBoolean existsSynchronizationPlan(WSExistsSynchronizationPlan wsExistsSynchronizationPlan) throws RemoteException {
        
        return null;
    }

    public WSBoolean existsUniverse(WSExistsUniverse wsExistsUniverse) throws RemoteException {
        
        return null;
    }

    @Override
    public WSString refreshCache(WSRefreshCache refreshCache) throws RemoteException {
        return null;
    }

    public WSConceptRevisionMap getConceptsInDataClusterWithRevisions(
            WSGetConceptsInDataClusterWithRevisions wsGetConceptsInDataClusterWithRevisions) throws RemoteException {
        
        return null;
    }

    public WSUniverse getCurrentUniverse(WSGetCurrentUniverse wsGetCurrentUniverse) throws RemoteException {
        
        return null;
    }

    @Override
    public WSRoutingOrderV2Array getRoutingOrderV2ByCriteriaWithPaging(WSGetRoutingOrderV2ByCriteriaWithPaging wsGetRoutingOrderV2ByCriteriaWithPaging) throws RemoteException {
        return null;
    }

    public WSStringArray getObjectsForRoles(WSGetObjectsForRoles wsRoleDelete) throws RemoteException {
        
        return null;
    }

    public WSStringArray getObjectsForSynchronizationPlans(WSGetObjectsForSynchronizationPlans regex) throws RemoteException {
        
        return null;
    }

    public WSStringArray getObjectsForUniverses(WSGetObjectsForUniverses regex) throws RemoteException {
        
        return null;
    }

    public WSRole getRole(WSGetRole wsGetRole) throws RemoteException {
        try {
            RoleCtrlLocal ctrl = com.amalto.core.util.Util.getRoleCtrlLocal();
            RolePOJO pojo = ctrl.getRole(new RolePOJOPK(wsGetRole.getWsRolePK().getPk()));
            return XConverter.POJO2WS(pojo);
        } catch (Exception e) {
            String err = "ERROR SYSTRACE: " + e.getMessage();
            org.apache.log4j.Logger.getLogger(this.getClass()).debug(err, e);
            throw new RemoteException(e.getClass().getName() + ": " + e.getLocalizedMessage());
        }
    }

    @Override
    public WSUniverse getUniverse(Object wsGetUniverse) {
        return null;
    }

    public WSRolePKArray getRolePKs(WSGetRolePKs regex) throws RemoteException {
        
        return null;
    }

    public WSSynchronizationItem getSynchronizationItem(WSGetSynchronizationItem wsGetSynchronizationItem) throws RemoteException {
        
        return null;
    }

    public WSSynchronizationItemPKArray getSynchronizationItemPKs(WSGetSynchronizationItemPKs regex) throws RemoteException {
        
        return null;
    }

    public WSSynchronizationPlan getSynchronizationPlan(WSGetSynchronizationPlan wsGetSynchronizationPlan) throws RemoteException {
        
        return null;
    }

    public WSStringArray getSynchronizationPlanItemsAlgorithms(WSGetSynchronizationPlanItemsAlgorithms regex)
            throws RemoteException {
        
        return null;
    }

    public WSStringArray getSynchronizationPlanObjectsAlgorithms(WSGetSynchronizationPlanObjectsAlgorithms regex)
            throws RemoteException {
        
        return null;
    }

    public WSSynchronizationPlanPKArray getSynchronizationPlanPKs(WSGetSynchronizationPlanPKs regex) throws RemoteException {
        
        return null;
    }

    public WSUniverse getUniverse(WSGetUniverse wsGetUniverse) throws RemoteException {
        
        return null;
    }

    public WSUniversePKArray getUniverseByRevision(WSGetUniverseByRevision wsUniverseByRevision) throws RemoteException {
        
        return null;
    }

    public WSUniversePKArray getUniversePKs(WSGetUniversePKs regex) throws RemoteException {
        
        WSUniversePKArray array = new WSUniversePKArray();
        return array;
    }

    public WSVersioningSystemConfiguration getVersioningSystemConfiguration(
            WSGetVersioningSystemConfiguration wsGetVersioningSystemConfiguration) throws RemoteException {
        
        return null;
    }

    public WSRolePK putRole(WSPutRole wsRole) throws RemoteException {
        
        return null;
    }

    public WSSynchronizationItemPK putSynchronizationItem(WSPutSynchronizationItem wsSynchronizationItem) throws RemoteException {
        
        return null;
    }

    public WSSynchronizationPlanPK putSynchronizationPlan(WSPutSynchronizationPlan wsSynchronizationPlan) throws RemoteException {
        
        return null;
    }

    public WSUniversePK putUniverse(WSPutUniverse wsUniverse) throws RemoteException {
        
        return null;
    }

    public WSString putVersioningSystemConfiguration(WSPutVersioningSystemConfiguration wsPutVersioningSystemConfiguration)
            throws RemoteException {
        
        return null;
    }

    public WSSynchronizationItem resolveSynchronizationItem(WSResolveSynchronizationItem wsResolveSynchronizationItem)
            throws RemoteException {
        
        return null;
    }

    public WSString synchronizationGetItemXML(WSSynchronizationGetItemXML wsSynchronizationGetItemXML) throws RemoteException {
        
        return null;
    }

    public WSString synchronizationGetObjectXML(WSSynchronizationGetObjectXML wsSynchronizationGetObjectXML)
            throws RemoteException {
        
        return null;
    }

    public WSItemPKArray synchronizationGetUnsynchronizedItemPKs(
            WSSynchronizationGetUnsynchronizedItemPKs wsSynchronizationGetUnsynchronizedItemPKs) throws RemoteException {
        
        return null;
    }

    public WSStringArray synchronizationGetUnsynchronizedObjectsIDs(
            WSSynchronizationGetUnsynchronizedObjectsIDs wsSynchronizationGetUnsynchronizedObjectsIDs) throws RemoteException {
        
        return null;
    }

    public WSSynchronizationPlanStatus synchronizationPlanAction(WSSynchronizationPlanAction wsSynchronizationPlanAction)
            throws RemoteException {
        
        return null;
    }

    public WSItemPK synchronizationPutItemXML(WSSynchronizationPutItemXML wsSynchronizationPutItemXML) throws RemoteException {
        
        return null;
    }

    public WSString synchronizationPutObjectXML(WSSynchronizationPutObjectXML wsSynchronizationPutObjectXML)
            throws RemoteException {
        
        return null;
    }

    public WSBackgroundJobPK versioningCommitItems(WSVersioningCommitItems wsVersioningCommitItems) throws RemoteException {
        
        return null;
    }

    public WSVersioningInfo versioningGetInfo(WSVersioningGetInfo wsVersioningGetInfo) throws RemoteException {
        
        return null;
    }

    public WSVersioningItemHistory versioningGetItemHistory(WSVersioningGetItemHistory wsVersioningGetItemHistory)
            throws RemoteException {
        
        return null;
    }

    public WSVersioningItemsVersions versioningGetItemsVersions(WSVersioningGetItemsVersions wsVersioningGetItemsVersions)
            throws RemoteException {
        
        return null;
    }

    public WSVersioningObjectsVersions versioningGetObjectsVersions(WSVersioningGetObjectsVersions wsVersioningGetObjectsVersions)
            throws RemoteException {
        
        return null;
    }

    public WSVersioningUniverseVersions versioningGetUniverseVersions(
            WSVersioningGetUniverseVersions wsVersioningGetUniverseVersions) throws RemoteException {
        
        return null;
    }

    public WSBoolean versioningRestoreItemByRevision(WSVersioningRestoreItemByRevision wsVersioningRestoreItemByRevision)
            throws RemoteException {
        
        return null;
    }

    public WSBackgroundJobPK versioningRestoreItems(WSVersioningRestoreItems wsVersioningRestoreItems) throws RemoteException {
        
        return null;
    }

    public WSBackgroundJobPK versioningRestoreObjects(WSVersioningRestoreObjects wsVersioningRestoreObjects)
            throws RemoteException {
        
        return null;
    }

    public WSBackgroundJobPK versioningTagItems(WSVersioningTagItems wsVersioningTagItems) throws RemoteException {
        
        return null;
    }

    public WSBackgroundJobPK versioningTagObjects(WSVersioningTagObjects wsVersioningTagObjects) throws RemoteException {
        
        return null;
    }

    public WSBackgroundJobPK versioningTagUniverse(WSVersioningTagUniverse wsVersioningTagUniverse) throws RemoteException {
        
        return null;
    }

    public WSBoolean deleteMDMJob(WSDELMDMJob deleteMDMJobRequest) throws RemoteException {
        
        return null;
    }

    public WSMDMJobArray getMDMJob(WSMDMNULL mdmJobRequest) throws RemoteException {
        
        return null;
    }

    public WSItemPK putItemWithCustomReport(WSPutItemWithCustomReport wsPutItemWithCustomReport) throws RemoteException {
        
        return null;
    }

    @Override
    public WSString deleteItemWithReport(WSDeleteItemWithReport wsDeleteItem) throws RemoteException {
        return null;
    }

    public WSBoolean putMDMJob(WSPUTMDMJob putMDMJobRequest) throws RemoteException {
        
        return null;
    }

    public WSString versioningGetItemContent(WSVersioningGetItemContent wsVersioningGetItemContent) throws RemoteException {
        
        return null;
    }

    public WSBackgroundJobPK versioningRestoreUniverse(WSVersioningRestoreUniverse wsVersioningRestoreUniverse)
            throws RemoteException {
        
        return null;
    }

    public WSBoolean workflowDeleteProcessInstances(WSWorkflowDeleteProcessInstancesRequest deleteWolkflowRequest)
            throws RemoteException {
        
        return null;
    }

    public WSWorkflowProcessDefinitionUUID workflowDeploy(WSWorkflowDeploy wsWorkflowDeploy) throws RemoteException {
        
        return null;
    }

    public WSWorkflowProcessDefinitionUUIDArray workflowGetProcessDefinitions(
            WSWorkflowGetProcessDefinitions wsworkflowProcessDefinitions) throws RemoteException {
        
        return null;
    }

    public WSProcessInstanceArray workflowGetProcessInstances(WSWorkflowGetProcessInstances uuid) throws RemoteException {
        
        return null;
    }

    public WSProcessTaskInstanceArray workflowGetTaskList(WSWorkflowGetTaskList uuid) throws RemoteException {
        
        return null;
    }

    public WSBoolean workflowUnDeploy(WSWorkflowUnDeploy wsWorkflowUnDeploy) throws RemoteException {
        
        return null;
    }

    public WSAutoIncrement getAutoIncrement(WSAutoIncrement wsAutoIncrementRequest) throws RemoteException {
        
        return null;
    }

    @Override
    public WSBoolean isXmlDB() throws RemoteException {
        return null;
    }

    @Override
    public WSDigest getDigest(WSDigestKey wsDigestKey) throws RemoteException {
        return null;
    }

    @Override
    public WSLong updateDigest(WSDigest wsDigest) throws RemoteException {
        return null;
    }

    /*
     * (non-Jsdoc)
     * 
     * @see com.amalto.webapp.util.webservices.XtentisPort#updateItemMetadata(com.amalto.webapp.util.webservices.
     * WSUpdateMetadataItem)
     */
    public WSItemPK updateItemMetadata(WSUpdateMetadataItem wsUpdateMetadataItem) throws RemoteException {
        
        return null;
    }

    /*
     * (non-Jsdoc)
     * 
     * @see
     * com.amalto.webapp.util.webservices.XtentisPort#partialPutItem(com.amalto.webapp.util.webservices.WSPartialPutItem
     * )
     */
    public WSItemPK partialPutItem(WSPartialPutItem wsPartialPutItem) throws RemoteException {
        
        return null;
    }

    /*
     * (non-Jsdoc)
     * 
     * @see com.amalto.webapp.util.webservices.XtentisPort#updateItemArrayMetadata(com.amalto.webapp.util.webservices.
     * WSUpdateItemArrayMetadata)
     */
    public WSItemPKArray updateItemArrayMetadata(WSUpdateItemArrayMetadata wsUpdateItemArrayMetadata) throws RemoteException {
        
        return null;
    }

    /*
     * (non-Jsdoc)
     * 
     * @see
     * com.amalto.webapp.util.webservices.XtentisPort#workflowUnassignTask(com.amalto.webapp.util.webservices.WSUnassignTask
     * )
     */
    public WSBoolean workflowUnassignTask(WSUnassignTask task) throws RemoteException {
        
        return null;
    }

    /*
     * (non-Jsdoc)
     * 
     * @see
     * com.amalto.webapp.util.webservices.XtentisPort#workflowAssignTask(com.amalto.webapp.util.webservices.WSAssignTask
     * )
     */
    public WSBoolean workflowAssignTask(WSAssignTask task) throws RemoteException {
        
        return null;
    }

    /*
     * (non-Jsdoc)
     * 
     * @see com.amalto.webapp.util.webservices.XtentisPort#workflowSetTaskPriority(com.amalto.webapp.util.webservices.
     * WSSetTaskPriority)
     */
    public WSBoolean workflowSetTaskPriority(WSSetTaskPriority task) throws RemoteException {
        
        return null;
    }

    /*
     * (non-Jsdoc)
     * 
     * @see
     * com.amalto.webapp.util.webservices.XtentisPort#workflowSuspendTask(com.amalto.webapp.util.webservices.WSSuspendTask
     * )
     */
    public WSBoolean workflowSuspendTask(WSSuspendTask task) throws RemoteException {
        
        return null;
    }

    /*
     * (non-Jsdoc)
     * 
     * @see
     * com.amalto.webapp.util.webservices.XtentisPort#workflowStartProcessInstance(com.amalto.webapp.util.webservices
     * .WSStartProcessInstance)
     */
    public WSBoolean workflowStartProcessInstance(WSStartProcessInstance task) throws RemoteException {
        
        return null;
    }

    public List<String> globalSearch(String dataCluster, String keyword, int start, int end) throws RemoteException {
        return new ArrayList<String>(0); // CE edition doesn't support this feature.
    }
}
