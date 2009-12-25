package com.amalto.core.delegator;

import java.rmi.RemoteException;

import com.amalto.core.webservice.*;

public interface IXtentisWSBeanDelegator extends BeanDelegator{
	
	public WSString ping(WSPing wsPing) throws RemoteException;
	public WSString logout(WSLogout logout) throws RemoteException;
	public WSInt initMDM(WSInitData initData) throws RemoteException;
	public WSMDMConfig getMDMConfiguration() throws RemoteException;
	
	/*********************Data model******************
	 * ************************************************/
	public WSDataModel getDataModel(WSGetDataModel wsDataModelget)    throws RemoteException;
	public WSBoolean existsDataModel(WSExistsDataModel wsExistsDataModel) throws RemoteException;
	public WSDataModelPKArray getDataModelPKs(WSRegexDataModelPKs regexp)
    throws RemoteException;
	public WSDataModelPK deleteDataModel(WSDeleteDataModel wsDeleteDataModel)
    throws RemoteException;
	public WSDataModelPK putDataModel(WSPutDataModel wsDataModel)
	    throws RemoteException;
	
	/******************************Schema*****************
	 * ****************************************************/
	public WSString checkSchema(WSCheckSchema wsSchema) throws RemoteException;
	public WSString putBusinessConcept(WSPutBusinessConcept wsPutBusinessConcept)
    throws RemoteException;
	public WSString putBusinessConceptSchema(WSPutBusinessConceptSchema wsPutBusinessConceptSchema)
    throws RemoteException;
	public WSString deleteBusinessConcept(
            WSDeleteBusinessConcept wsDeleteBusinessConcept)
            throws RemoteException;
	public WSStringArray getBusinessConcepts(
            WSGetBusinessConcepts wsGetBusinessConcepts) throws RemoteException;
	public WSConceptKey getBusinessConceptKey(
            WSGetBusinessConceptKey wsGetBusinessConceptKey) throws RemoteException;
	
	
	/***************************************************************************
	 * DataCluster
	 * **************************************************************************/	
	public WSDataCluster getDataCluster(WSGetDataCluster wsDataClusterGet)
    throws RemoteException;
	public WSBoolean existsDataCluster(WSExistsDataCluster wsExistsDataCluster)
    throws RemoteException;
	public WSBoolean existsDBDataCluster(WSExistsDBDataCluster wsExistsDataCluster)
    throws RemoteException;
	public WSDataClusterPKArray getDataClusterPKs(WSRegexDataClusterPKs regexp)
    throws RemoteException;
	public WSDataClusterPK deleteDataCluster(WSDeleteDataCluster wsDeleteDataCluster)
    throws RemoteException;
	public WSDataClusterPK putDataCluster(WSPutDataCluster wsDataCluster)
    throws RemoteException;
	public WSBoolean putDBDataCluster(WSPutDBDataCluster wsDataCluster)
    throws RemoteException;
	public WSStringArray getConceptsInDataCluster(WSGetConceptsInDataCluster wsGetConceptsInDataCluster) throws RemoteException;
	public WSConceptRevisionMap getConceptsInDataClusterWithRevisions(WSGetConceptsInDataClusterWithRevisions wsGetConceptsInDataClusterWithRevisions) throws RemoteException;
	
	
	/***************************************************************************
	 * View
	 * **************************************************************************/
	public WSView getView(WSGetView wsViewGet)
    throws RemoteException;
	public WSBoolean existsView(WSExistsView wsExistsView)
	   throws RemoteException;
	public WSViewPKArray getViewPKs(WSGetViewPKs regexp) throws RemoteException ;
	public WSViewPK deleteView(WSDeleteView wsDeleteView)
    throws RemoteException;
	public WSViewPK putView(WSPutView wsView)
    throws RemoteException;
	
	/***************************************************************************
	 * Search
	 * **************************************************************************/
	public WSStringArray viewSearch(WSViewSearch wsViewSearch) throws RemoteException;
	public WSStringArray xPathsSearch(WSXPathsSearch wsXPathsSearch) throws RemoteException;
	public WSStringArray getItemsPivotIndex(WSGetItemsPivotIndex wsGetItemsPivotIndex) throws RemoteException;
	public WSString count(WSCount wsCount) throws RemoteException;
	public WSStringArray getItems(WSGetItems wsGetItems) throws RemoteException;
	public WSItemPKsByCriteriaResponse getItemPKsByCriteria(WSGetItemPKsByCriteria wsGetItemPKsByCriteria) throws RemoteException;
	public WSItem getItem(WSGetItem wsGetItem) throws RemoteException;
	public WSBoolean existsItem(WSExistsItem wsExistsItem) throws RemoteException;
	public WSStringArray quickSearch(WSQuickSearch wsQuickSearch) throws RemoteException;
	public WSString getBusinessConceptValue(
			WSGetBusinessConceptValue wsGetBusinessConceptValue)
			throws RemoteException;
	public WSStringArray getFullPathValues(WSGetFullPathValues wsGetFullPathValues)
	throws RemoteException;
	
	/***************************************************************************
	 *Put Item
	 * **************************************************************************/
	public WSItemPK putItem(WSPutItem wsPutItem) throws RemoteException;
	public WSItemPKArray putItemArray(WSPutItemArray wsPutItemArray) throws RemoteException ;
	public WSItemPKArray putItemWithReportArray(com.amalto.core.webservice.WSPutItemWithReportArray wsPutItemWithReportArray) throws RemoteException;
	public WSItemPK putItemWithReport(com.amalto.core.webservice.WSPutItemWithReport wsPutItemWithReport) throws RemoteException;
	public WSItemPK putItemWithCustomReport(com.amalto.core.webservice.WSPutItemWithCustomReport wsPutItemWithCustomReport) throws RemoteException;
	
	/***************************************************************************
	 *Extract Items
	 * **************************************************************************/
	public WSPipeline extractUsingTransformer(WSExtractUsingTransformer wsExtractUsingTransformer) throws RemoteException;
	public WSPipeline extractUsingTransformerThruView(WSExtractUsingTransformerThruView wsExtractUsingTransformerThruView) throws RemoteException;
	
	/***************************************************************************
	 * Delete Items
	 * **************************************************************************/
	public WSItemPK deleteItem(WSDeleteItem wsDeleteItem)
	throws RemoteException;
	public WSInt deleteItems(WSDeleteItems wsDeleteItems)
	throws RemoteException;
	public WSDroppedItemPK dropItem(WSDropItem wsDropItem)
	throws RemoteException;
	
	/***************************************************************************
	 * DirectQuery
	 * **************************************************************************/
	public WSStringArray runQuery(WSRunQuery wsRunQuery) throws RemoteException;
	
	/***************************************************************************
	 * RoutingRule
	 * **************************************************************************/
	public WSRoutingRule getRoutingRule(WSGetRoutingRule wsRoutingRuleGet)
    throws RemoteException;
	public WSBoolean existsRoutingRule(WSExistsRoutingRule wsExistsRoutingRule)
	   throws RemoteException;
	public WSRoutingRulePK deleteRoutingRule(WSDeleteRoutingRule wsDeleteRoutingRule)
    throws RemoteException;
	public WSRoutingRulePK putRoutingRule(WSPutRoutingRule wsRoutingRule)
    throws RemoteException;
	public WSRoutingRulePKArray getRoutingRulePKs(WSGetRoutingRulePKs regex) throws RemoteException;
	
	/***************************************************************************
	 * SERVICES
	 * **************************************************************************/
	public WSServiceGetDocument getServiceDocument(WSString serviceName) throws RemoteException;
	public WSString getServiceConfiguration(WSServiceGetConfiguration wsGetConfiguration) throws RemoteException;
	public WSCheckServiceConfigResponse checkServiceConfiguration(WSCheckServiceConfigRequest serviceName) throws RemoteException;
	public WSString putServiceConfiguration(WSServicePutConfiguration wsPutConfiguration) throws RemoteException;
	public WSString serviceAction(WSServiceAction wsServiceAction) throws RemoteException ;
	public WSServicesList getServicesList(WSGetServicesList wsGetServicesList) throws RemoteException;
	
	/***************************************************************************
	 * Stored Procedure
	 * **************************************************************************/
	public WSStoredProcedurePK deleteStoredProcedure(WSDeleteStoredProcedure wsStoredProcedureDelete) throws RemoteException;
	public WSStringArray executeStoredProcedure(WSExecuteStoredProcedure wsExecuteStoredProcedure) throws RemoteException;
	public WSStoredProcedure getStoredProcedure(WSGetStoredProcedure wsGetStoredProcedure) throws RemoteException;
	public WSBoolean existsStoredProcedure(WSExistsStoredProcedure wsExistsStoredProcedure) throws RemoteException;
	public WSStoredProcedurePKArray getStoredProcedurePKs(WSRegexStoredProcedure regex) throws RemoteException;
	public WSStoredProcedurePK putStoredProcedure(WSPutStoredProcedure wsStoredProcedure) throws RemoteException;
	
	/***************************************************************************
	 * TransformerV2
	 * **************************************************************************/
	public WSTransformerV2PK deleteTransformerV2(WSDeleteTransformerV2 wsTransformerV2Delete) throws RemoteException;
	public WSTransformerV2 getTransformerV2(WSGetTransformerV2 wsGetTransformerV2) throws RemoteException;
	public WSBoolean existsTransformerV2(WSExistsTransformerV2 wsExistsTransformerV2) throws RemoteException;
	public WSTransformerV2PKArray getTransformerV2PKs(WSGetTransformerV2PKs regex) throws RemoteException;
	public WSTransformerV2PK putTransformerV2(WSPutTransformerV2 wsTransformerV2) throws RemoteException;
	public WSTransformerContext executeTransformerV2(WSExecuteTransformerV2 wsExecuteTransformerV2) throws RemoteException;
	public WSBackgroundJobPK executeTransformerV2AsJob(WSExecuteTransformerV2AsJob wsExecuteTransformerV2AsJob) throws RemoteException;
	public WSTransformerContext extractThroughTransformerV2(WSExtractThroughTransformerV2 wsExtractThroughTransformerV2) throws RemoteException;
	
	/***************************************************************************
	 * TRANSFORMER PLUGINS V2
	 * **************************************************************************/
	public WSBoolean existsTransformerPluginV2(WSExistsTransformerPluginV2 wsExistsTransformerPlugin) throws RemoteException;
	public WSString getTransformerPluginV2Configuration(WSTransformerPluginV2GetConfiguration wsGetConfiguration) throws RemoteException;
	public WSString putTransformerPluginV2Configuration(WSTransformerPluginV2PutConfiguration wsPutConfiguration) throws RemoteException;
	public WSTransformerPluginV2Details getTransformerPluginV2Details(WSGetTransformerPluginV2Details wsGetTransformerPluginDetails) throws RemoteException;
	public WSTransformerPluginV2SList getTransformerPluginV2SList(WSGetTransformerPluginV2SList wsGetTransformerPluginsList) throws RemoteException;
	/***************************************************************************
	 * Role
	 * **************************************************************************/
	public WSRolePK deleteRole(WSDeleteRole wsRoleDelete) throws RemoteException;
	public WSRole getRole(WSGetRole wsGetRole) throws RemoteException ;
	public WSBoolean existsRole(WSExistsRole wsExistsRole) throws RemoteException;
	public WSRolePKArray getRolePKs(WSGetRolePKs regex) throws RemoteException;
	public WSRolePK putRole(WSPutRole wsRole) throws RemoteException;
	public WSStringArray getObjectsForRoles(WSGetObjectsForRoles wsRoleDelete) throws RemoteException;
	
	/***************************************************************************
	 * Menu
	 * **************************************************************************/
	public WSMenuPK deleteMenu(WSDeleteMenu wsMenuDelete) throws RemoteException;
	public WSMenu getMenu(WSGetMenu wsGetMenu) throws RemoteException;
	public WSBoolean existsMenu(WSExistsMenu wsExistsMenu) throws RemoteException;
	public WSMenuPKArray getMenuPKs(WSGetMenuPKs regex) throws RemoteException;
	public WSMenuPK putMenu(WSPutMenu wsMenu) throws RemoteException;
	/***************************************************************************
	 * Versioning
	 * **************************************************************************/
	
	public WSBackgroundJobPK versioningCommitItems(WSVersioningCommitItems wsVersioningCommitItems) throws RemoteException;
	public WSBoolean versioningRestoreItemByRevision(WSVersioningRestoreItemByRevision wsVersioningRestoreItemByRevision) throws RemoteException;
	public WSVersioningItemHistory versioningGetItemHistory(WSVersioningGetItemHistory wsVersioningGetItemHistory) throws RemoteException;
	public WSVersioningItemsVersions versioningGetItemsVersions(WSVersioningGetItemsVersions wsVersioningGetItemsVersions) throws RemoteException;
	public WSString versioningGetItemContent(WSVersioningGetItemContent wsVersioningGetItemContent) throws RemoteException;
	public WSVersioningObjectsVersions versioningGetObjectsVersions(WSVersioningGetObjectsVersions wsVersioningGetObjectsVersions) throws RemoteException;
	public WSVersioningUniverseVersions versioningGetUniverseVersions(WSVersioningGetUniverseVersions wsVersioningGetUniverseVersions) throws RemoteException;
	public WSBackgroundJobPK versioningRestoreItems(WSVersioningRestoreItems wsVersioningRestoreItems) throws RemoteException;
	public WSBackgroundJobPK versioningRestoreUniverse(WSVersioningRestoreUniverse wsVersioningRestoreUniverse) throws RemoteException;
	public WSBackgroundJobPK versioningTagItems(WSVersioningTagItems wsVersioningTagItems) throws RemoteException;
	public WSBackgroundJobPK versioningTagObjects(WSVersioningTagObjects wsVersioningTagObjects) throws RemoteException;
	public WSBackgroundJobPK versioningTagUniverse(WSVersioningTagUniverse wsVersioningTagUniverse) throws RemoteException;
	public WSVersioningInfo versioningGetInfo(WSVersioningGetInfo wsVersioningGetInfo) throws RemoteException;
	public WSString putVersioningSystemConfiguration(WSPutVersioningSystemConfiguration wsPutVersioningSystemConfiguration) throws RemoteException;
	public WSBackgroundJobPK versioningRestoreObjects(WSVersioningRestoreObjects wsVersioningRestoreObjects) throws RemoteException;
	
	/***************************************************************************
	 * BackgroundJob
	 * **************************************************************************/

	public WSBackgroundJob getBackgroundJob(WSGetBackgroundJob wsBackgroundJobGet)
    throws RemoteException;
	public WSBackgroundJobPKArray findBackgroundJobPKs(WSFindBackgroundJobPKs wsFindBackgroundJobPKs)
    throws RemoteException;
	public WSBackgroundJobPK putBackgroundJob(WSPutBackgroundJob wsputjob)
	throws RemoteException;
	
	/***************************************************************************
	 * Routing Order V2
	 * **************************************************************************/
	public WSRoutingOrderV2 getRoutingOrderV2(WSGetRoutingOrderV2 wsGetRoutingOrder) throws RemoteException;
	public WSRoutingOrderV2 existsRoutingOrderV2(WSExistsRoutingOrderV2 wsExistsRoutingOrder) throws RemoteException;
	public WSRoutingOrderV2PK deleteRoutingOrderV2(WSDeleteRoutingOrderV2 wsDeleteRoutingOrder) throws RemoteException;
	public WSRoutingOrderV2PK executeRoutingOrderV2Asynchronously(WSExecuteRoutingOrderV2Asynchronously wsExecuteRoutingOrderAsynchronously) throws RemoteException;
	public WSString executeRoutingOrderV2Synchronously(WSExecuteRoutingOrderV2Synchronously wsExecuteRoutingOrderSynchronously) throws RemoteException;
	public WSRoutingOrderV2PKArray getRoutingOrderV2PKsByCriteria(WSGetRoutingOrderV2PKsByCriteria wsGetRoutingOrderV2PKsByCriteria) throws RemoteException;
	public WSRoutingOrderV2Array getRoutingOrderV2SByCriteria(WSGetRoutingOrderV2SByCriteria wsGetRoutingOrderV2SByCriteria) throws RemoteException;
	
	/***************************************************************************
	 * Routing Engine V2
	 * **************************************************************************/
	public WSRoutingRulePKArray routeItemV2(WSRouteItemV2 wsRouteItem) throws RemoteException;
	public WSRoutingEngineV2Status routingEngineV2Action(WSRoutingEngineV2Action wsRoutingEngineAction) throws RemoteException;
	/***************************************************************************
	 * Universe
	 * **************************************************************************/

	public WSUniversePK deleteUniverse(WSDeleteUniverse wsUniverseDelete) throws RemoteException;
	public WSUniverse getUniverse(WSGetUniverse wsGetUniverse) throws RemoteException;
	public WSBoolean existsUniverse(WSExistsUniverse wsExistsUniverse) throws RemoteException;
	public WSUniversePKArray getUniverseByRevision(WSGetUniverseByRevision revision) throws RemoteException;
	public WSUniversePKArray getUniversePKs(WSGetUniversePKs regex) throws RemoteException;
	public WSUniversePK putUniverse(WSPutUniverse wsUniverse) throws RemoteException;
	public WSStringArray getObjectsForUniverses(WSGetObjectsForUniverses wsUniverseDelete) throws RemoteException;
	public WSUniverse getCurrentUniverse(WSGetCurrentUniverse wsGetCurrentUniverse) throws RemoteException;
	/***************************************************************************
	 * SynchronizationPlan
	 * **************************************************************************/
	public WSSynchronizationPlanPK deleteSynchronizationPlan(WSDeleteSynchronizationPlan wsSynchronizationPlanDelete) throws RemoteException;
	public WSSynchronizationPlan getSynchronizationPlan(WSGetSynchronizationPlan wsGetSynchronizationPlan) throws RemoteException ;
	public WSBoolean existsSynchronizationPlan(WSExistsSynchronizationPlan wsExistsSynchronizationPlan) throws RemoteException;
	public WSSynchronizationPlanPKArray getSynchronizationPlanPKs(WSGetSynchronizationPlanPKs regex) throws RemoteException;
	public WSSynchronizationPlanPK putSynchronizationPlan(WSPutSynchronizationPlan wsSynchronizationPlan) throws RemoteException;
	public WSStringArray getObjectsForSynchronizationPlans(WSGetObjectsForSynchronizationPlans wsSynchronizationPlanDelete) throws RemoteException;
	public WSStringArray getSynchronizationPlanObjectsAlgorithms(WSGetSynchronizationPlanObjectsAlgorithms regex) throws RemoteException;
	public WSStringArray getSynchronizationPlanItemsAlgorithms(WSGetSynchronizationPlanItemsAlgorithms regex) throws RemoteException;
	public WSSynchronizationPlanStatus synchronizationPlanAction(WSSynchronizationPlanAction wsSynchronizationPlan) throws RemoteException;
	public WSStringArray synchronizationGetUnsynchronizedObjectsIDs(WSSynchronizationGetUnsynchronizedObjectsIDs wsSynchronizationGetUnsynchronizedObjectsIDs) throws RemoteException;
	public WSString synchronizationGetObjectXML(WSSynchronizationGetObjectXML wsSynchronizationGetObjectXML) throws RemoteException ;
	public WSString synchronizationPutObjectXML(WSSynchronizationPutObjectXML wsSynchronizationPutObjectXML) throws RemoteException;
	public WSItemPKArray synchronizationGetUnsynchronizedItemPKs(WSSynchronizationGetUnsynchronizedItemPKs wsSynchronizationGetUnsynchronizedItemPKs) throws RemoteException;
	public WSString synchronizationGetItemXML(WSSynchronizationGetItemXML wsSynchronizationGetItemXML) throws RemoteException;
	public WSItemPK synchronizationPutItemXML(WSSynchronizationPutItemXML wsSynchronizationPutItemXML) throws RemoteException;
	/***************************************************************************
	 * Synchronization Item
	 * **************************************************************************/
	public WSSynchronizationItemPK deleteSynchronizationItem(WSDeleteSynchronizationItem wsSynchronizationItemDelete) throws RemoteException;
	public WSSynchronizationItem getSynchronizationItem(WSGetSynchronizationItem wsGetSynchronizationItem) throws RemoteException;
	public WSBoolean existsSynchronizationItem(WSExistsSynchronizationItem wsExistsSynchronizationItem) throws RemoteException;
	public WSSynchronizationItemPKArray getSynchronizationItemPKs(WSGetSynchronizationItemPKs regex) throws RemoteException;
	public WSSynchronizationItemPK putSynchronizationItem(WSPutSynchronizationItem wsSynchronizationItem) throws RemoteException;
	public WSSynchronizationItem resolveSynchronizationItem(WSResolveSynchronizationItem wsResolveSynchronizationItem) throws RemoteException;
	
	/***************************************************************************
	 * Drop Item
	 * **************************************************************************/
	public WSDroppedItemPKArray findAllDroppedItemsPKs(WSFindAllDroppedItemsPKs regex)throws RemoteException ;
	public WSDroppedItem loadDroppedItem(WSLoadDroppedItem wsLoadDroppedItem)
	throws RemoteException;
	public WSItemPK recoverDroppedItem(WSRecoverDroppedItem wsRecoverDroppedItem)
	throws RemoteException;
	public WSDroppedItemPK removeDroppedItem(WSRemoveDroppedItem wsRemoveDroppedItem)
	throws RemoteException;
	/***************************************************************************
	 * Workflow
	 * **************************************************************************/
	public WSWorkflowProcessDefinitionUUIDArray workflowGetProcessDefinitions(WSWorkflowGetProcessDefinitions wsWorkflowGetProcessDefinitions) throws RemoteException;
	public WSWorkflowProcessDefinitionUUID workflowDeploy(WSWorkflowDeploy deploy) throws RemoteException;
	public WSBoolean workflowUnDeploy(WSWorkflowUnDeploy undeploy) throws RemoteException;
	public WSProcessInstanceArray workflowGetProcessInstances(WSWorkflowGetProcessInstances instance) throws RemoteException;
	public WSBoolean workflowDeleteProcessInstances(WSWorkflowDeleteProcessInstancesRequest deleteWolkflowRequest) throws RemoteException;
	public WSProcessTaskInstanceArray workflowGetTaskList(WSWorkflowGetTaskList tasklist) throws RemoteException;
	/***************************************************************************
	 * Job
	 * **************************************************************************/
	public WSBoolean putMDMJob(WSPUTMDMJob job)throws RemoteException;
	public WSBoolean deleteMDMJob(WSDELMDMJob job)throws RemoteException;
	public WSMDMJobArray getMDMJob(WSMDMNULL job)throws RemoteException;
	 
}
