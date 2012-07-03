package com.amalto.webapp.core.util;

import java.rmi.RemoteException;
import java.util.List;

import com.amalto.webapp.util.webservices.*;

import junit.framework.TestCase;


public class IXtentisRMIPortTest extends TestCase {
    
    public void testPutItemWithReportArray () {
        MockIXtentisRMIPort port = new MockIXtentisRMIPort();    
        WSPutItemWithReport[] reportArray = new WSPutItemWithReport[] {null, null, null};
        WSPutItemWithReportArray reportArrayObj = new WSPutItemWithReportArray(reportArray);
        
        try {
            WSItemPKArray result = port.putItemWithReportArray(reportArrayObj);
            assertTrue(port.putItemWithReportCallCount == reportArray.length);
            assertTrue(result.getWsItemPK().length == reportArray.length);
        }
        catch (RemoteException e) {
            fail();
        }
    }

       
    private class MockIXtentisRMIPort extends IXtentisRMIPort
    {
        public int putItemWithReportCallCount = 0;
        
        @Override
        public WSItemPK putItemWithReport(WSPutItemWithReport wsPutItemWithReport) throws RemoteException {
            
            ++putItemWithReportCallCount;
            
            return null;
        }
        
        @Override
        public WSConceptRevisionMap getConceptsInDataClusterWithRevisions(
                WSGetConceptsInDataClusterWithRevisions wsGetConceptsInDataClusterWithRevisions) throws RemoteException {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public WSItemPK updateItemMetadata(WSUpdateMetadataItem wsUpdateMetadataItem) throws RemoteException {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public WSItemPK partialPutItem(WSPartialPutItem wsPartialPutItem) throws RemoteException {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public WSItemPKArray updateItemArrayMetadata(WSUpdateItemArrayMetadata wsUpdateItemArrayMetadata) throws RemoteException {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public WSItemPK putItemWithCustomReport(WSPutItemWithCustomReport wsPutItemWithCustomReport) throws RemoteException {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public WSRole getRole(WSGetRole wsGetRole) throws RemoteException {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public WSBoolean existsRole(WSExistsRole wsExistsRole) throws RemoteException {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public WSRolePKArray getRolePKs(WSGetRolePKs regex) throws RemoteException {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public WSRolePK putRole(WSPutRole wsRole) throws RemoteException {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public WSRolePK deleteRole(WSDeleteRole wsRoleDelete) throws RemoteException {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public WSStringArray getObjectsForRoles(WSGetObjectsForRoles wsRoleDelete) throws RemoteException {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public WSBackgroundJobPK versioningCommitItems(WSVersioningCommitItems wsVersioningCommitItems) throws RemoteException {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public WSBoolean versioningRestoreItemByRevision(WSVersioningRestoreItemByRevision wsVersioningRestoreItemByRevision)
                throws RemoteException {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public WSVersioningItemHistory versioningGetItemHistory(WSVersioningGetItemHistory wsVersioningGetItemHistory)
                throws RemoteException {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public WSVersioningItemsVersions versioningGetItemsVersions(WSVersioningGetItemsVersions wsVersioningGetItemsVersions)
                throws RemoteException {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public WSString versioningGetItemContent(WSVersioningGetItemContent wsVersioningGetItemContent) throws RemoteException {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public WSVersioningObjectsVersions versioningGetObjectsVersions(
                WSVersioningGetObjectsVersions wsVersioningGetObjectsVersions) throws RemoteException {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public WSVersioningUniverseVersions versioningGetUniverseVersions(
                WSVersioningGetUniverseVersions wsVersioningGetUniverseVersions) throws RemoteException {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public WSVersioningSystemConfiguration getVersioningSystemConfiguration(
                WSGetVersioningSystemConfiguration wsGetVersioningSystemConfiguration) throws RemoteException {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public WSString putVersioningSystemConfiguration(WSPutVersioningSystemConfiguration wsPutVersioningSystemConfiguration)
                throws RemoteException {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public WSVersioningInfo versioningGetInfo(WSVersioningGetInfo wsVersioningGetInfo) throws RemoteException {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public WSBackgroundJobPK versioningTagObjects(WSVersioningTagObjects wsVersioningTagObjects) throws RemoteException {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public WSBackgroundJobPK versioningTagUniverse(WSVersioningTagUniverse wsVersioningTagUniverse) throws RemoteException {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public WSBackgroundJobPK versioningTagItems(WSVersioningTagItems wsVersioningTagItems) throws RemoteException {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public WSBackgroundJobPK versioningRestoreObjects(WSVersioningRestoreObjects wsVersioningRestoreObjects)
                throws RemoteException {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public WSBackgroundJobPK versioningRestoreUniverse(WSVersioningRestoreUniverse wsVersioningRestoreUniverse)
                throws RemoteException {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public WSBackgroundJobPK versioningRestoreItems(WSVersioningRestoreItems wsVersioningRestoreItems) throws RemoteException {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public WSUniverse getUniverse(WSGetUniverse wsGetUniverse) throws RemoteException {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public WSBoolean existsUniverse(WSExistsUniverse wsExistsUniverse) throws RemoteException {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public WSUniversePKArray getUniversePKs(WSGetUniversePKs regex) throws RemoteException {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public WSUniversePKArray getUniverseByRevision(WSGetUniverseByRevision wsUniverseByRevision) throws RemoteException {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public WSUniversePK putUniverse(WSPutUniverse wsUniverse) throws RemoteException {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public WSUniversePK deleteUniverse(WSDeleteUniverse wsUniverseDelete) throws RemoteException {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public WSStringArray getObjectsForUniverses(WSGetObjectsForUniverses regex) throws RemoteException {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public WSUniverse getCurrentUniverse(WSGetCurrentUniverse wsGetCurrentUniverse) throws RemoteException {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public WSSynchronizationPlan getSynchronizationPlan(WSGetSynchronizationPlan wsGetSynchronizationPlan)
                throws RemoteException {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public WSBoolean existsSynchronizationPlan(WSExistsSynchronizationPlan wsExistsSynchronizationPlan)
                throws RemoteException {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public WSSynchronizationPlanPKArray getSynchronizationPlanPKs(WSGetSynchronizationPlanPKs regex) throws RemoteException {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public WSSynchronizationPlanPK putSynchronizationPlan(WSPutSynchronizationPlan wsSynchronizationPlan)
                throws RemoteException {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public WSSynchronizationPlanPK deleteSynchronizationPlan(WSDeleteSynchronizationPlan wsSynchronizationPlanDelete)
                throws RemoteException {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public WSStringArray getObjectsForSynchronizationPlans(WSGetObjectsForSynchronizationPlans regex) throws RemoteException {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public WSStringArray getSynchronizationPlanObjectsAlgorithms(WSGetSynchronizationPlanObjectsAlgorithms regex)
                throws RemoteException {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public WSStringArray getSynchronizationPlanItemsAlgorithms(WSGetSynchronizationPlanItemsAlgorithms regex)
                throws RemoteException {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public WSSynchronizationPlanStatus synchronizationPlanAction(WSSynchronizationPlanAction wsSynchronizationPlanAction)
                throws RemoteException {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public WSStringArray synchronizationGetUnsynchronizedObjectsIDs(
                WSSynchronizationGetUnsynchronizedObjectsIDs wsSynchronizationGetUnsynchronizedObjectsIDs) throws RemoteException {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public WSString synchronizationGetObjectXML(WSSynchronizationGetObjectXML wsSynchronizationGetObjectXML)
                throws RemoteException {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public WSString synchronizationPutObjectXML(WSSynchronizationPutObjectXML wsSynchronizationPutObjectXML)
                throws RemoteException {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public WSItemPKArray synchronizationGetUnsynchronizedItemPKs(
                WSSynchronizationGetUnsynchronizedItemPKs wsSynchronizationGetUnsynchronizedItemPKs) throws RemoteException {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public WSString synchronizationGetItemXML(WSSynchronizationGetItemXML wsSynchronizationGetItemXML) throws RemoteException {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public WSItemPK synchronizationPutItemXML(WSSynchronizationPutItemXML wsSynchronizationPutItemXML) throws RemoteException {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public WSSynchronizationItem getSynchronizationItem(WSGetSynchronizationItem wsGetSynchronizationItem)
                throws RemoteException {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public WSBoolean existsSynchronizationItem(WSExistsSynchronizationItem wsExistsSynchronizationItem)
                throws RemoteException {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public WSSynchronizationItemPKArray getSynchronizationItemPKs(WSGetSynchronizationItemPKs regex) throws RemoteException {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public WSSynchronizationItemPK putSynchronizationItem(WSPutSynchronizationItem wsSynchronizationItem)
                throws RemoteException {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public WSSynchronizationItemPK deleteSynchronizationItem(WSDeleteSynchronizationItem wsSynchronizationItemDelete)
                throws RemoteException {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public WSSynchronizationItem resolveSynchronizationItem(WSResolveSynchronizationItem wsResolveSynchronizationItem)
                throws RemoteException {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public WSWorkflowProcessDefinitionUUIDArray workflowGetProcessDefinitions(
                WSWorkflowGetProcessDefinitions wsworkflowProcessDefinitions) throws RemoteException {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public WSWorkflowProcessDefinitionUUID workflowDeploy(WSWorkflowDeploy wsWorkflowDeploy) throws RemoteException {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public WSBoolean workflowUnDeploy(WSWorkflowUnDeploy wsWorkflowUnDeploy) throws RemoteException {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public WSProcessTaskInstanceArray workflowGetTaskList(WSWorkflowGetTaskList uuid) throws RemoteException {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public WSProcessInstanceArray workflowGetProcessInstances(WSWorkflowGetProcessInstances uuid) throws RemoteException {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public WSBoolean workflowDeleteProcessInstances(WSWorkflowDeleteProcessInstancesRequest deleteWolkflowRequest)
                throws RemoteException {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public WSBoolean workflowUnassignTask(WSUnassignTask task) throws RemoteException {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public WSBoolean workflowAssignTask(WSAssignTask task) throws RemoteException {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public WSBoolean workflowSetTaskPriority(WSSetTaskPriority task) throws RemoteException {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public WSBoolean workflowSuspendTask(WSSuspendTask task) throws RemoteException {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public WSBoolean workflowStartProcessInstance(WSStartProcessInstance task) throws RemoteException {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public WSMDMJobArray getMDMJob(WSMDMNULL mdmJobRequest) throws RemoteException {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public WSBoolean putMDMJob(WSPUTMDMJob putMDMJobRequest) throws RemoteException {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public WSBoolean deleteMDMJob(WSDELMDMJob deleteMDMJobRequest) throws RemoteException {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public WSAutoIncrement getAutoIncrement(WSAutoIncrement wsAutoIncrementRequest) throws RemoteException {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public List<String> globalSearch(String dataCluster, String keyword, int start, int end) throws RemoteException {
            // TODO Auto-generated method stub
            return null;
        }
        
    }
}
