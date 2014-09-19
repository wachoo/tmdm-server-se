/*
 * Copyright (C) 2006-2014 Talend Inc. - www.talend.com
 *
 * This source code is available under agreement available at
 * %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
 *
 * You should have received a copy of the agreement
 * along with this program; if not, write to Talend SA
 * 9 rue Pages 92150 Suresnes, France
 */

package com.amalto.core.server.lifecycle.tomcat;

import com.amalto.core.save.SaverHelper;
import com.amalto.core.save.SaverSession;
import com.amalto.core.save.context.DocumentSaver;
import com.amalto.core.webservice.*;
import com.amalto.core.webservice.WSAssignTask;
import com.amalto.core.webservice.WSAutoIncrement;
import com.amalto.core.webservice.WSBackgroundJob;
import com.amalto.core.webservice.WSBackgroundJobPK;
import com.amalto.core.webservice.WSBackgroundJobPKArray;
import com.amalto.core.webservice.WSBoolean;
import com.amalto.core.webservice.WSCategoryData;
import com.amalto.core.webservice.WSCheckSchema;
import com.amalto.core.webservice.WSCheckServiceConfigRequest;
import com.amalto.core.webservice.WSCheckServiceConfigResponse;
import com.amalto.core.webservice.WSConceptKey;
import com.amalto.core.webservice.WSConceptRevisionMap;
import com.amalto.core.webservice.WSConnectorInteraction;
import com.amalto.core.webservice.WSConnectorInteractionResponse;
import com.amalto.core.webservice.WSCount;
import com.amalto.core.webservice.WSCountItemsByCustomFKFilters;
import com.amalto.core.webservice.WSDELMDMJob;
import com.amalto.core.webservice.WSDataCluster;
import com.amalto.core.webservice.WSDataClusterPK;
import com.amalto.core.webservice.WSDataClusterPKArray;
import com.amalto.core.webservice.WSDataModel;
import com.amalto.core.webservice.WSDataModelPK;
import com.amalto.core.webservice.WSDataModelPKArray;
import com.amalto.core.webservice.WSDeleteBusinessConcept;
import com.amalto.core.webservice.WSDeleteDataCluster;
import com.amalto.core.webservice.WSDeleteDataModel;
import com.amalto.core.webservice.WSDeleteItem;
import com.amalto.core.webservice.WSDeleteItemWithReport;
import com.amalto.core.webservice.WSDeleteItems;
import com.amalto.core.webservice.WSDeleteMenu;
import com.amalto.core.webservice.WSDeleteRole;
import com.amalto.core.webservice.WSDeleteRoutingOrderV2;
import com.amalto.core.webservice.WSDeleteRoutingRule;
import com.amalto.core.webservice.WSDeleteStoredProcedure;
import com.amalto.core.webservice.WSDeleteSynchronizationItem;
import com.amalto.core.webservice.WSDeleteSynchronizationPlan;
import com.amalto.core.webservice.WSDeleteTransformer;
import com.amalto.core.webservice.WSDeleteTransformerV2;
import com.amalto.core.webservice.WSDeleteUniverse;
import com.amalto.core.webservice.WSDeleteView;
import com.amalto.core.webservice.WSDropItem;
import com.amalto.core.webservice.WSDroppedItem;
import com.amalto.core.webservice.WSDroppedItemPK;
import com.amalto.core.webservice.WSDroppedItemPKArray;
import com.amalto.core.webservice.WSExecuteRoutingOrderV2Asynchronously;
import com.amalto.core.webservice.WSExecuteRoutingOrderV2Synchronously;
import com.amalto.core.webservice.WSExecuteStoredProcedure;
import com.amalto.core.webservice.WSExecuteTransformerV2;
import com.amalto.core.webservice.WSExecuteTransformerV2AsJob;
import com.amalto.core.webservice.WSExistsDBDataCluster;
import com.amalto.core.webservice.WSExistsDataCluster;
import com.amalto.core.webservice.WSExistsDataModel;
import com.amalto.core.webservice.WSExistsItem;
import com.amalto.core.webservice.WSExistsMenu;
import com.amalto.core.webservice.WSExistsRole;
import com.amalto.core.webservice.WSExistsRoutingOrderV2;
import com.amalto.core.webservice.WSExistsRoutingRule;
import com.amalto.core.webservice.WSExistsStoredProcedure;
import com.amalto.core.webservice.WSExistsSynchronizationItem;
import com.amalto.core.webservice.WSExistsSynchronizationPlan;
import com.amalto.core.webservice.WSExistsTransformer;
import com.amalto.core.webservice.WSExistsTransformerPluginV2;
import com.amalto.core.webservice.WSExistsTransformerV2;
import com.amalto.core.webservice.WSExistsUniverse;
import com.amalto.core.webservice.WSExistsView;
import com.amalto.core.webservice.WSExtractThroughTransformerV2;
import com.amalto.core.webservice.WSExtractUsingTransformer;
import com.amalto.core.webservice.WSExtractUsingTransformerThruView;
import com.amalto.core.webservice.WSFindAllDroppedItemsPKs;
import com.amalto.core.webservice.WSFindBackgroundJobPKs;
import com.amalto.core.webservice.WSGetBackgroundJob;
import com.amalto.core.webservice.WSGetBusinessConceptKey;
import com.amalto.core.webservice.WSGetBusinessConceptValue;
import com.amalto.core.webservice.WSGetBusinessConcepts;
import com.amalto.core.webservice.WSGetChildrenItems;
import com.amalto.core.webservice.WSGetComponentVersion;
import com.amalto.core.webservice.WSGetConceptsInDataCluster;
import com.amalto.core.webservice.WSGetConceptsInDataClusterWithRevisions;
import com.amalto.core.webservice.WSGetCurrentUniverse;
import com.amalto.core.webservice.WSGetDataCluster;
import com.amalto.core.webservice.WSGetDataModel;
import com.amalto.core.webservice.WSGetFullPathValues;
import com.amalto.core.webservice.WSGetItem;
import com.amalto.core.webservice.WSGetItemPKsByCriteria;
import com.amalto.core.webservice.WSGetItemPKsByFullCriteria;
import com.amalto.core.webservice.WSGetItems;
import com.amalto.core.webservice.WSGetItemsByCustomFKFilters;
import com.amalto.core.webservice.WSGetItemsPivotIndex;
import com.amalto.core.webservice.WSGetItemsSort;
import com.amalto.core.webservice.WSGetMenu;
import com.amalto.core.webservice.WSGetMenuPKs;
import com.amalto.core.webservice.WSGetObjectsForRoles;
import com.amalto.core.webservice.WSGetObjectsForSynchronizationPlans;
import com.amalto.core.webservice.WSGetObjectsForUniverses;
import com.amalto.core.webservice.WSGetRole;
import com.amalto.core.webservice.WSGetRolePKs;
import com.amalto.core.webservice.WSGetRoutingOrderV2;
import com.amalto.core.webservice.WSGetRoutingOrderV2PKsByCriteria;
import com.amalto.core.webservice.WSGetRoutingOrderV2SByCriteria;
import com.amalto.core.webservice.WSGetRoutingRule;
import com.amalto.core.webservice.WSGetRoutingRulePKs;
import com.amalto.core.webservice.WSGetServicesList;
import com.amalto.core.webservice.WSGetStoredProcedure;
import com.amalto.core.webservice.WSGetSynchronizationItem;
import com.amalto.core.webservice.WSGetSynchronizationItemPKs;
import com.amalto.core.webservice.WSGetSynchronizationPlan;
import com.amalto.core.webservice.WSGetSynchronizationPlanItemsAlgorithms;
import com.amalto.core.webservice.WSGetSynchronizationPlanObjectsAlgorithms;
import com.amalto.core.webservice.WSGetSynchronizationPlanPKs;
import com.amalto.core.webservice.WSGetTransformer;
import com.amalto.core.webservice.WSGetTransformerPKs;
import com.amalto.core.webservice.WSGetTransformerPluginV2Details;
import com.amalto.core.webservice.WSGetTransformerPluginV2SList;
import com.amalto.core.webservice.WSGetTransformerV2;
import com.amalto.core.webservice.WSGetTransformerV2PKs;
import com.amalto.core.webservice.WSGetUniverse;
import com.amalto.core.webservice.WSGetUniverseByRevision;
import com.amalto.core.webservice.WSGetUniversePKs;
import com.amalto.core.webservice.WSGetVersioningSystemConfiguration;
import com.amalto.core.webservice.WSGetView;
import com.amalto.core.webservice.WSGetViewPKs;
import com.amalto.core.webservice.WSInitData;
import com.amalto.core.webservice.WSInt;
import com.amalto.core.webservice.WSIsItemModifiedByOther;
import com.amalto.core.webservice.WSItem;
import com.amalto.core.webservice.WSItemPK;
import com.amalto.core.webservice.WSItemPKArray;
import com.amalto.core.webservice.WSItemPKsByCriteriaResponse;
import com.amalto.core.webservice.WSLoadDroppedItem;
import com.amalto.core.webservice.WSLogout;
import com.amalto.core.webservice.WSMDMConfig;
import com.amalto.core.webservice.WSMDMJobArray;
import com.amalto.core.webservice.WSMDMNULL;
import com.amalto.core.webservice.WSMenu;
import com.amalto.core.webservice.WSMenuPK;
import com.amalto.core.webservice.WSMenuPKArray;
import com.amalto.core.webservice.WSPUTMDMJob;
import com.amalto.core.webservice.WSPartialPutItem;
import com.amalto.core.webservice.WSPing;
import com.amalto.core.webservice.WSPipeline;
import com.amalto.core.webservice.WSProcessBytesUsingTransformer;
import com.amalto.core.webservice.WSProcessBytesUsingTransformerAsBackgroundJob;
import com.amalto.core.webservice.WSProcessFileUsingTransformer;
import com.amalto.core.webservice.WSProcessFileUsingTransformerAsBackgroundJob;
import com.amalto.core.webservice.WSProcessInstanceArray;
import com.amalto.core.webservice.WSProcessTaskInstanceArray;
import com.amalto.core.webservice.WSPutBackgroundJob;
import com.amalto.core.webservice.WSPutBusinessConcept;
import com.amalto.core.webservice.WSPutBusinessConceptSchema;
import com.amalto.core.webservice.WSPutDBDataCluster;
import com.amalto.core.webservice.WSPutDataCluster;
import com.amalto.core.webservice.WSPutDataModel;
import com.amalto.core.webservice.WSPutItem;
import com.amalto.core.webservice.WSPutItemArray;
import com.amalto.core.webservice.WSPutItemByOperatorType;
import com.amalto.core.webservice.WSPutItemWithCustomReport;
import com.amalto.core.webservice.WSPutItemWithReport;
import com.amalto.core.webservice.WSPutItemWithReportArray;
import com.amalto.core.webservice.WSPutMenu;
import com.amalto.core.webservice.WSPutRole;
import com.amalto.core.webservice.WSPutRoutingRule;
import com.amalto.core.webservice.WSPutStoredProcedure;
import com.amalto.core.webservice.WSPutSynchronizationItem;
import com.amalto.core.webservice.WSPutSynchronizationPlan;
import com.amalto.core.webservice.WSPutTransformer;
import com.amalto.core.webservice.WSPutTransformerV2;
import com.amalto.core.webservice.WSPutUniverse;
import com.amalto.core.webservice.WSPutVersioningSystemConfiguration;
import com.amalto.core.webservice.WSPutView;
import com.amalto.core.webservice.WSQuickSearch;
import com.amalto.core.webservice.WSRecoverDroppedItem;
import com.amalto.core.webservice.WSRefreshCache;
import com.amalto.core.webservice.WSRegexDataClusterPKs;
import com.amalto.core.webservice.WSRegexDataModelPKs;
import com.amalto.core.webservice.WSRegexStoredProcedure;
import com.amalto.core.webservice.WSRemoveDroppedItem;
import com.amalto.core.webservice.WSResolveSynchronizationItem;
import com.amalto.core.webservice.WSResumeTask;
import com.amalto.core.webservice.WSRole;
import com.amalto.core.webservice.WSRolePK;
import com.amalto.core.webservice.WSRolePKArray;
import com.amalto.core.webservice.WSRouteItemV2;
import com.amalto.core.webservice.WSRoutingEngineV2Action;
import com.amalto.core.webservice.WSRoutingEngineV2Status;
import com.amalto.core.webservice.WSRoutingOrderV2;
import com.amalto.core.webservice.WSRoutingOrderV2Array;
import com.amalto.core.webservice.WSRoutingOrderV2PK;
import com.amalto.core.webservice.WSRoutingOrderV2PKArray;
import com.amalto.core.webservice.WSRoutingRule;
import com.amalto.core.webservice.WSRoutingRulePK;
import com.amalto.core.webservice.WSRoutingRulePKArray;
import com.amalto.core.webservice.WSRunQuery;
import com.amalto.core.webservice.WSServiceAction;
import com.amalto.core.webservice.WSServiceGetConfiguration;
import com.amalto.core.webservice.WSServiceGetDocument;
import com.amalto.core.webservice.WSServicePutConfiguration;
import com.amalto.core.webservice.WSServicesList;
import com.amalto.core.webservice.WSSetTaskPriority;
import com.amalto.core.webservice.WSStartProcessInstance;
import com.amalto.core.webservice.WSStoredProcedure;
import com.amalto.core.webservice.WSStoredProcedurePK;
import com.amalto.core.webservice.WSStoredProcedurePKArray;
import com.amalto.core.webservice.WSString;
import com.amalto.core.webservice.WSStringArray;
import com.amalto.core.webservice.WSSuspendTask;
import com.amalto.core.webservice.WSSynchronizationGetItemXML;
import com.amalto.core.webservice.WSSynchronizationGetObjectXML;
import com.amalto.core.webservice.WSSynchronizationGetUnsynchronizedItemPKs;
import com.amalto.core.webservice.WSSynchronizationGetUnsynchronizedObjectsIDs;
import com.amalto.core.webservice.WSSynchronizationItem;
import com.amalto.core.webservice.WSSynchronizationItemPK;
import com.amalto.core.webservice.WSSynchronizationItemPKArray;
import com.amalto.core.webservice.WSSynchronizationPlan;
import com.amalto.core.webservice.WSSynchronizationPlanAction;
import com.amalto.core.webservice.WSSynchronizationPlanPK;
import com.amalto.core.webservice.WSSynchronizationPlanPKArray;
import com.amalto.core.webservice.WSSynchronizationPlanStatus;
import com.amalto.core.webservice.WSSynchronizationPutItemXML;
import com.amalto.core.webservice.WSSynchronizationPutObjectXML;
import com.amalto.core.webservice.WSTransformer;
import com.amalto.core.webservice.WSTransformerContext;
import com.amalto.core.webservice.WSTransformerPK;
import com.amalto.core.webservice.WSTransformerPKArray;
import com.amalto.core.webservice.WSTransformerPluginV2Details;
import com.amalto.core.webservice.WSTransformerPluginV2GetConfiguration;
import com.amalto.core.webservice.WSTransformerPluginV2PutConfiguration;
import com.amalto.core.webservice.WSTransformerPluginV2SList;
import com.amalto.core.webservice.WSTransformerV2;
import com.amalto.core.webservice.WSTransformerV2PK;
import com.amalto.core.webservice.WSTransformerV2PKArray;
import com.amalto.core.webservice.WSUnassignTask;
import com.amalto.core.webservice.WSUniverse;
import com.amalto.core.webservice.WSUniversePK;
import com.amalto.core.webservice.WSUniversePKArray;
import com.amalto.core.webservice.WSUpdateItemArrayMetadata;
import com.amalto.core.webservice.WSUpdateMetadataItem;
import com.amalto.core.webservice.WSVersion;
import com.amalto.core.webservice.WSVersioningCommitItems;
import com.amalto.core.webservice.WSVersioningGetInfo;
import com.amalto.core.webservice.WSVersioningGetItemContent;
import com.amalto.core.webservice.WSVersioningGetItemHistory;
import com.amalto.core.webservice.WSVersioningGetItemsVersions;
import com.amalto.core.webservice.WSVersioningGetObjectsVersions;
import com.amalto.core.webservice.WSVersioningGetUniverseVersions;
import com.amalto.core.webservice.WSVersioningInfo;
import com.amalto.core.webservice.WSVersioningItemHistory;
import com.amalto.core.webservice.WSVersioningItemsVersions;
import com.amalto.core.webservice.WSVersioningObjectsVersions;
import com.amalto.core.webservice.WSVersioningRestoreItemByRevision;
import com.amalto.core.webservice.WSVersioningRestoreItems;
import com.amalto.core.webservice.WSVersioningRestoreObjects;
import com.amalto.core.webservice.WSVersioningRestoreUniverse;
import com.amalto.core.webservice.WSVersioningSystemConfiguration;
import com.amalto.core.webservice.WSVersioningTagItems;
import com.amalto.core.webservice.WSVersioningTagObjects;
import com.amalto.core.webservice.WSVersioningTagUniverse;
import com.amalto.core.webservice.WSVersioningUniverseVersions;
import com.amalto.core.webservice.WSView;
import com.amalto.core.webservice.WSViewPK;
import com.amalto.core.webservice.WSViewPKArray;
import com.amalto.core.webservice.WSViewSearch;
import com.amalto.core.webservice.WSWorkflowDeleteProcessInstancesRequest;
import com.amalto.core.webservice.WSWorkflowDeploy;
import com.amalto.core.webservice.WSWorkflowGetProcessDefinitions;
import com.amalto.core.webservice.WSWorkflowGetProcessInstances;
import com.amalto.core.webservice.WSWorkflowGetTaskList;
import com.amalto.core.webservice.WSWorkflowProcessDefinitionUUID;
import com.amalto.core.webservice.WSWorkflowProcessDefinitionUUIDArray;
import com.amalto.core.webservice.WSWorkflowUnDeploy;
import com.amalto.core.webservice.WSXPathsSearch;
import org.apache.log4j.Logger;

import javax.jws.WebService;
import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.LinkedList;
import java.util.List;

@WebService(name="datamanager", serviceName="datamanager")
public class LegacyServices implements Serializable {

    private static final Logger LOG = Logger.getLogger(LegacyServices.class);

    
    public WSVersion getComponentVersion(WSGetComponentVersion wsGetComponentVersion) throws RemoteException {
        return null;
    }

    
    public WSString ping(WSPing wsPing) throws RemoteException {
        return null;
    }

    
    public WSString refreshCache(WSRefreshCache refreshCache) throws RemoteException {
        return null;
    }

    
    public WSString logout(WSLogout wsLogout) throws RemoteException {
        return null;
    }

    
    public WSInt initMDM(WSInitData initData) throws RemoteException {
        return null;
    }

    
    public WSDataModelPKArray getDataModelPKs(WSRegexDataModelPKs regexp) throws RemoteException {
        return null;
    }

    
    public WSDataModel getDataModel(WSGetDataModel wsDataModelget) throws RemoteException {
        return null;
    }

    
    public WSBoolean existsDataModel(WSExistsDataModel wsDataModelExists) throws RemoteException {
        return null;
    }

    
    public WSDataModelPK putDataModel(WSPutDataModel wsDataModel) throws RemoteException {
        return null;
    }

    
    public WSDataModelPK deleteDataModel(WSDeleteDataModel wsDeleteDataModel) throws RemoteException {
        return null;
    }

    
    public WSString checkSchema(WSCheckSchema wsSchema) throws RemoteException {
        return null;
    }

    
    public WSString deleteBusinessConcept(WSDeleteBusinessConcept wsDeleteBusinessConcept) throws RemoteException {
        return null;
    }

    
    public WSStringArray getBusinessConcepts(WSGetBusinessConcepts wsGetBusinessConcepts) throws RemoteException {
        return null;
    }

    
    public WSString putBusinessConcept(WSPutBusinessConcept wsPutBusinessConcept) throws RemoteException {
        return null;
    }

    
    public WSString putBusinessConceptSchema(WSPutBusinessConceptSchema wsPutBusinessConceptSchema) throws RemoteException {
        return null;
    }

    
    public WSConceptKey getBusinessConceptKey(WSGetBusinessConceptKey wsGetBusinessConceptKey) throws RemoteException {
        return null;
    }

    
    public WSDataClusterPKArray getDataClusterPKs(WSRegexDataClusterPKs regexp) throws RemoteException {
        return null;
    }

    
    public WSDataCluster getDataCluster(WSGetDataCluster wsDataClusterPK) throws RemoteException {
        return null;
    }

    
    public WSBoolean existsDataCluster(WSExistsDataCluster wsExistsDataCluster) throws RemoteException {
        return null;
    }

    
    public WSBoolean existsDBDataCluster(WSExistsDBDataCluster wsExistsDBDataCluster) throws RemoteException {
        return null;
    }

    
    public WSDataClusterPK putDataCluster(WSPutDataCluster wsDataCluster) throws RemoteException {
        return null;
    }

    
    public WSBoolean putDBDataCluster(WSPutDBDataCluster wsDataCluster) throws RemoteException {
        return null;
    }

    
    public WSDataClusterPK deleteDataCluster(WSDeleteDataCluster wsDeleteDataCluster) throws RemoteException {
        return null;
    }

    
    public WSStringArray getConceptsInDataCluster(WSGetConceptsInDataCluster wsGetConceptsInDataCluster) throws RemoteException {
        return null;
    }

    
    public WSConceptRevisionMap getConceptsInDataClusterWithRevisions(WSGetConceptsInDataClusterWithRevisions wsGetConceptsInDataClusterWithRevisions) throws RemoteException {
        return null;
    }

    
    public WSViewPKArray getViewPKs(WSGetViewPKs regexp) throws RemoteException {
        return null;
    }

    
    public WSView getView(WSGetView wsViewPK) throws RemoteException {
        return null;
    }

    
    public WSBoolean existsView(WSExistsView wsViewPK) throws RemoteException {
        return null;
    }

    
    public WSViewPK putView(WSPutView wsView) throws RemoteException {
        return null;
    }

    
    public WSViewPK deleteView(WSDeleteView wsViewDel) throws RemoteException {
        return null;
    }

    
    public WSString getBusinessConceptValue(WSGetBusinessConceptValue wsGetBusinessConceptValue) throws RemoteException {
        return null;
    }

    
    public WSStringArray getFullPathValues(WSGetFullPathValues wsGetFullPathValues) throws RemoteException {
        return null;
    }

    
    public WSItem getItem(WSGetItem wsGetItem) throws RemoteException {
        return null;
    }

    
    public WSBoolean existsItem(WSExistsItem wsExistsItem) throws RemoteException {
        return null;
    }

    
    public WSStringArray getItems(WSGetItems wsGetItems) throws RemoteException {
        return null;
    }

    
    public WSStringArray getItemsSort(WSGetItemsSort wsGetItemsSort) throws RemoteException {
        return null;
    }

    
    public WSItemPKsByCriteriaResponse getItemPKsByCriteria(WSGetItemPKsByCriteria wsGetItemPKsByCriteria) throws RemoteException {
        return null;
    }

    
    public WSItemPKsByCriteriaResponse getItemPKsByFullCriteria(WSGetItemPKsByFullCriteria wsGetItemPKsByFullCriteria) throws RemoteException {
        return null;
    }

    
    public WSString countItemsByCustomFKFilters(WSCountItemsByCustomFKFilters wsCountItemsByCustomFKFilters) throws RemoteException {
        return null;
    }

    
    public WSStringArray getItemsByCustomFKFilters(WSGetItemsByCustomFKFilters wsGetItemsByCustomFKFilters) throws RemoteException {
        return null;
    }

    
    public WSStringArray viewSearch(WSViewSearch wsViewSearch) throws RemoteException {
        return null;
    }

    
    public WSStringArray xPathsSearch(WSXPathsSearch wsXPathsSearch) throws RemoteException {
        return null;
    }

    
    public WSStringArray getItemsPivotIndex(WSGetItemsPivotIndex wsGetItemsPivotIndex) throws RemoteException {
        return null;
    }

    
    public WSStringArray getChildrenItems(WSGetChildrenItems wsGetChildrenItems) throws RemoteException {
        return null;
    }

    
    public WSString count(WSCount wsCount) throws RemoteException {
        return null;
    }

    
    public WSStringArray quickSearch(WSQuickSearch wsQuickSearch) throws RemoteException {
        return null;
    }

    
    public WSItemPK putItem(WSPutItem wsPutItem) throws RemoteException {
        try {
            WSDataClusterPK dataClusterPK = wsPutItem.getWsDataClusterPK();
            WSDataModelPK dataModelPK = wsPutItem.getWsDataModelPK();

            String dataClusterName = dataClusterPK.getPk();
            String dataModelName = dataModelPK.getPk();

            SaverSession session = SaverSession.newSession();
            DocumentSaver saver;
            try {
                session.begin(dataClusterPK.getPk());
                saver = SaverHelper.saveItem(wsPutItem.getXmlString(), session, !wsPutItem.getIsUpdate(), dataClusterName,
                        dataModelName);
                // Cause items being saved to be committed to database.
                session.end();
            } catch (Exception e) {
                try {
                    session.abort();
                } catch (Exception e1) {
                    LOG.error("Exception occurred during rollback.", e1);
                }
                throw new RuntimeException(e);
            }

            String[] savedId = saver.getSavedId();
            String savedConceptName = saver.getSavedConceptName();
            return new WSItemPK(dataClusterPK, savedConceptName, savedId);
        } catch (Exception e) {
            LOG.error("Error during save.", e);
            throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()), e);
        }

    }

    
    public WSItemPK updateItemMetadata(WSUpdateMetadataItem wsUpdateMetadataItem) throws RemoteException {
        return null;
    }

    
    public WSItemPK partialPutItem(WSPartialPutItem wsPartialPutItem) throws RemoteException {
        return null;
    }

    
    public WSItemPK putItemByOperatorType(WSPutItemByOperatorType putItemByOperatorType) throws RemoteException {
        return null;
    }

    
    public WSItemPKArray putItemArray(WSPutItemArray wsPutItemArray) throws RemoteException {
        WSPutItem[] items = wsPutItemArray.getWsPutItem();
        try {
            List<WSItemPK> pks = new LinkedList<WSItemPK>();
            SaverSession session = SaverSession.newSession();
            for (WSPutItem item : items) {
                String dataClusterName = item.getWsDataClusterPK().getPk();
                String dataModelName = item.getWsDataModelPK().getPk();
                DocumentSaver saver = SaverHelper.saveItem(item.getXmlString(), session, !item.getIsUpdate(), dataClusterName,
                        dataModelName);
                pks.add(new WSItemPK(new WSDataClusterPK(), saver.getSavedConceptName(), saver.getSavedId()));
            }
            // Cause items being saved to be committed to database.
            session.end();

            return new WSItemPKArray(pks.toArray(new WSItemPK[pks.size()]));
        } catch (Exception e) {
            LOG.error("Error during save.", e);
            throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()), e);
        }
    }

    
    public WSItemPKArray updateItemArrayMetadata(WSUpdateItemArrayMetadata wsUpdateItemArrayMetadata) throws RemoteException {
        return null;
    }

    
    public WSItemPKArray putItemWithReportArray(WSPutItemWithReportArray wsPutItemWithReportArray) throws RemoteException {
        return null;
    }

    
    public WSItemPK putItemWithReport(WSPutItemWithReport wsPutItemWithReport) throws RemoteException {
        return null;
    }

    
    public WSItemPK putItemWithCustomReport(WSPutItemWithCustomReport wsPutItemWithCustomReport) throws RemoteException {
        return null;
    }

    
    public WSBoolean isItemModifiedByOther(WSIsItemModifiedByOther wsItem) throws RemoteException {
        return null;
    }

    
    public WSPipeline extractUsingTransformer(WSExtractUsingTransformer wsExtractUsingTransformer) throws RemoteException {
        return null;
    }

    
    public WSPipeline extractUsingTransformerThruView(WSExtractUsingTransformerThruView wsExtractUsingTransformerThruView) throws RemoteException {
        return null;
    }

    
    public WSItemPK deleteItem(WSDeleteItem wsDeleteItem) throws RemoteException {
        return null;
    }

    
    public WSString deleteItemWithReport(WSDeleteItemWithReport wsDeleteItem) throws RemoteException {
        return null;
    }

    
    public WSInt deleteItems(WSDeleteItems wsDeleteItems) throws RemoteException {
        return null;
    }

    
    public WSDroppedItemPK dropItem(WSDropItem wsDropItem) throws RemoteException {
        return null;
    }

    
    public WSStringArray runQuery(WSRunQuery wsRunQuery) throws RemoteException {
        return null;
    }

    
    public WSConnectorInteractionResponse connectorInteraction(WSConnectorInteraction wsConnectorInteraction) throws RemoteException {
        return null;
    }

    
    public WSRoutingRulePKArray getRoutingRulePKs(WSGetRoutingRulePKs regexp) throws RemoteException {
        return null;
    }

    
    public WSRoutingRule getRoutingRule(WSGetRoutingRule wsRoutingRulePK) throws RemoteException {
        return null;
    }

    
    public WSBoolean existsRoutingRule(WSExistsRoutingRule wsExistsRoutingRule) throws RemoteException {
        return null;
    }

    
    public WSRoutingRulePK putRoutingRule(WSPutRoutingRule wsRoutingRule) throws RemoteException {
        return null;
    }

    
    public WSRoutingRulePK deleteRoutingRule(WSDeleteRoutingRule wsRoutingRuleDel) throws RemoteException {
        return null;
    }

    
    public WSString serviceAction(WSServiceAction wsServiceAction) throws RemoteException {
        return null;
    }

    
    public WSString getServiceConfiguration(WSServiceGetConfiguration wsGetConfiguration) throws RemoteException {
        return null;
    }

    
    public WSString putServiceConfiguration(WSServicePutConfiguration wsPutConfiguration) throws RemoteException {
        return null;
    }

    
    public WSServicesList getServicesList(WSGetServicesList wsGetServicesList) throws RemoteException {
        return null;
    }

    
    public WSServiceGetDocument getServiceDocument(WSString serviceName) throws RemoteException {
        return null;
    }

    
    public WSStoredProcedure getStoredProcedure(WSGetStoredProcedure wsGetStoredProcedure) throws RemoteException {
        return null;
    }

    
    public WSBoolean existsStoredProcedure(WSExistsStoredProcedure wsExistsStoredProcedure) throws RemoteException {
        return null;
    }

    
    public WSStoredProcedurePKArray getStoredProcedurePKs(WSRegexStoredProcedure regex) throws RemoteException {
        return null;
    }

    
    public WSStoredProcedurePK putStoredProcedure(WSPutStoredProcedure wsStoredProcedure) throws RemoteException {
        return null;
    }

    
    public WSStoredProcedurePK deleteStoredProcedure(WSDeleteStoredProcedure wsStoredProcedureDelete) throws RemoteException {
        return null;
    }

    
    public WSStringArray executeStoredProcedure(WSExecuteStoredProcedure wsExecuteStoredProcedure) throws RemoteException {
        return null;
    }

    
    public WSTransformer getTransformer(WSGetTransformer wsGetTransformer) throws RemoteException {
        return null;
    }

    
    public WSBoolean existsTransformer(WSExistsTransformer wsExistsTransformer) throws RemoteException {
        return null;
    }

    
    public WSTransformerPKArray getTransformerPKs(WSGetTransformerPKs regex) throws RemoteException {
        return null;
    }

    
    public WSTransformerPK putTransformer(WSPutTransformer wsTransformer) throws RemoteException {
        return null;
    }

    
    public WSTransformerPK deleteTransformer(WSDeleteTransformer wsTransformerDelete) throws RemoteException {
        return null;
    }

    
    public WSPipeline processBytesUsingTransformer(WSProcessBytesUsingTransformer wsProcessBytesUsingTransformer) throws RemoteException {
        return null;
    }

    
    public WSPipeline processFileUsingTransformer(WSProcessFileUsingTransformer wsProcessFileUsingTransformer) throws RemoteException {
        return null;
    }

    
    public WSBackgroundJobPK processBytesUsingTransformerAsBackgroundJob(WSProcessBytesUsingTransformerAsBackgroundJob wsProcessBytesUsingTransformerAsBackgroundJob) throws RemoteException {
        return null;
    }

    
    public WSBackgroundJobPK processFileUsingTransformerAsBackgroundJob(WSProcessFileUsingTransformerAsBackgroundJob wsProcessFileUsingTransformerAsBackgroundJob) throws RemoteException {
        return null;
    }

    
    public WSTransformerV2 getTransformerV2(WSGetTransformerV2 wsGetTransformerV2) throws RemoteException {
        return null;
    }

    
    public WSBoolean existsTransformerV2(WSExistsTransformerV2 wsExistsTransformerV2) throws RemoteException {
        return null;
    }

    
    public WSTransformerV2PKArray getTransformerV2PKs(WSGetTransformerV2PKs regex) throws RemoteException {
        return null;
    }

    
    public WSTransformerV2PK putTransformerV2(WSPutTransformerV2 wsTransformerV2) throws RemoteException {
        return null;
    }

    
    public WSTransformerV2PK deleteTransformerV2(WSDeleteTransformerV2 wsDeleteTransformerV2) throws RemoteException {
        return null;
    }

    
    public WSTransformerContext executeTransformerV2(WSExecuteTransformerV2 wsExecuteTransformerV2) throws RemoteException {
        return null;
    }

    
    public WSBackgroundJobPK executeTransformerV2AsJob(WSExecuteTransformerV2AsJob wsExecuteTransformerV2AsJob) throws RemoteException {
        return null;
    }

    
    public WSTransformerContext extractThroughTransformerV2(WSExtractThroughTransformerV2 wsExtractThroughTransformerV2) throws RemoteException {
        return null;
    }

    
    public WSBoolean existsTransformerPluginV2(WSExistsTransformerPluginV2 wsExistsTransformerPluginV2) throws RemoteException {
        return null;
    }

    
    public WSString getTransformerPluginV2Configuration(WSTransformerPluginV2GetConfiguration wsGetConfiguration) throws RemoteException {
        return null;
    }

    
    public WSString putTransformerPluginV2Configuration(WSTransformerPluginV2PutConfiguration wsPutConfiguration) throws RemoteException {
        return null;
    }

    
    public WSTransformerPluginV2Details getTransformerPluginV2Details(WSGetTransformerPluginV2Details wsGetTransformerPluginV2Details) throws RemoteException {
        return null;
    }

    
    public WSTransformerPluginV2SList getTransformerPluginV2SList(WSGetTransformerPluginV2SList wsGetTransformerPluginV2SList) throws RemoteException {
        return null;
    }

    
    public WSRole getRole(WSGetRole wsGetRole) throws RemoteException {
        return null;
    }

    
    public WSBoolean existsRole(WSExistsRole wsExistsRole) throws RemoteException {
        return null;
    }

    
    public WSRolePKArray getRolePKs(WSGetRolePKs regex) throws RemoteException {
        return null;
    }

    
    public WSRolePK putRole(WSPutRole wsRole) throws RemoteException {
        return null;
    }

    
    public WSRolePK deleteRole(WSDeleteRole wsRoleDelete) throws RemoteException {
        return null;
    }

    
    public WSStringArray getObjectsForRoles(WSGetObjectsForRoles wsRoleDelete) throws RemoteException {
        return null;
    }

    
    public WSCustomForm getCustomForm(WSGetCustomForm wsGetCustomForm) throws RemoteException {
        return null;
    }

    
    public WSBoolean existsCustomForm(WSExistsCustomForm wsExistsCustomForm) throws RemoteException {
        return null;
    }

    
    public WSCustomFormPKArray getCustomFormPKs(WSGetCustomFormPKs regex) throws RemoteException {
        return null;
    }

    
    public WSCustomFormPK putCustomForm(WSPutCustomForm wsCustomForm) throws RemoteException {
        return null;
    }

    
    public WSCustomFormPK deleteCustomForm(WSDeleteCustomForm wsCustomFormDelete) throws RemoteException {
        return null;
    }

    
    public WSMenu getMenu(WSGetMenu wsGetMenu) throws RemoteException {
        return null;
    }

    
    public WSBoolean existsMenu(WSExistsMenu wsExistsMenu) throws RemoteException {
        return null;
    }

    
    public WSMenuPKArray getMenuPKs(WSGetMenuPKs regex) throws RemoteException {
        return null;
    }

    
    public WSMenuPK putMenu(WSPutMenu wsMenu) throws RemoteException {
        return null;
    }

    
    public WSMenuPK deleteMenu(WSDeleteMenu wsMenuDelete) throws RemoteException {
        return null;
    }

    
    public WSBackgroundJobPK versioningCommitItems(WSVersioningCommitItems wsVersioningCommitItems) throws RemoteException {
        return null;
    }

    
    public WSBoolean versioningRestoreItemByRevision(WSVersioningRestoreItemByRevision wsVersioningRestoreItemByRevision) throws RemoteException {
        return null;
    }

    
    public WSVersioningItemHistory versioningGetItemHistory(WSVersioningGetItemHistory wsVersioningGetItemHistory) throws RemoteException {
        return null;
    }

    
    public WSVersioningItemsVersions versioningGetItemsVersions(WSVersioningGetItemsVersions wsVersioningGetItemsVersions) throws RemoteException {
        return null;
    }

    
    public WSString versioningGetItemContent(WSVersioningGetItemContent wsVersioningGetItemContent) throws RemoteException {
        return null;
    }

    
    public WSVersioningObjectsVersions versioningGetObjectsVersions(WSVersioningGetObjectsVersions wsVersioningGetObjectsVersions) throws RemoteException {
        return null;
    }

    
    public WSVersioningUniverseVersions versioningGetUniverseVersions(WSVersioningGetUniverseVersions wsVersioningGetUniverseVersions) throws RemoteException {
        return null;
    }

    
    public WSVersioningSystemConfiguration getVersioningSystemConfiguration(WSGetVersioningSystemConfiguration wsGetVersioningSystemConfiguration) throws RemoteException {
        return null;
    }

    
    public WSString putVersioningSystemConfiguration(WSPutVersioningSystemConfiguration wsPutVersioningSystemConfiguration) throws RemoteException {
        return null;
    }

    
    public WSVersioningInfo versioningGetInfo(WSVersioningGetInfo wsVersioningGetInfo) throws RemoteException {
        return null;
    }

    
    public WSBackgroundJobPK versioningTagObjects(WSVersioningTagObjects wsVersioningTagObjects) throws RemoteException {
        return null;
    }

    
    public WSBackgroundJobPK versioningTagUniverse(WSVersioningTagUniverse wsVersioningTagUniverse) throws RemoteException {
        return null;
    }

    
    public WSBackgroundJobPK versioningTagItems(WSVersioningTagItems wsVersioningTagItems) throws RemoteException {
        return null;
    }

    
    public WSBackgroundJobPK versioningRestoreObjects(WSVersioningRestoreObjects wsVersioningRestoreObjects) throws RemoteException {
        return null;
    }

    
    public WSBackgroundJobPK versioningRestoreUniverse(WSVersioningRestoreUniverse wsVersioningRestoreUniverse) throws RemoteException {
        return null;
    }

    
    public WSBackgroundJobPK versioningRestoreItems(WSVersioningRestoreItems wsVersioningRestoreItems) throws RemoteException {
        return null;
    }

    
    public WSBackgroundJobPKArray findBackgroundJobPKs(WSFindBackgroundJobPKs status) throws RemoteException {
        return null;
    }

    
    public WSBackgroundJob getBackgroundJob(WSGetBackgroundJob wsGetBackgroundJob) throws RemoteException {
        return null;
    }

    
    public WSBackgroundJobPK putBackgroundJob(WSPutBackgroundJob wsPutBackgroundJob) throws RemoteException {
        return null;
    }

    
    public WSRoutingOrderV2 getRoutingOrderV2(WSGetRoutingOrderV2 wsGetRoutingOrderV2) throws RemoteException {
        return null;
    }

    
    public WSRoutingOrderV2 existsRoutingOrderV2(WSExistsRoutingOrderV2 wsExistsRoutingOrder) throws RemoteException {
        return null;
    }

    
    public WSRoutingOrderV2PK deleteRoutingOrderV2(WSDeleteRoutingOrderV2 wsDeleteRoutingOrder) throws RemoteException {
        return null;
    }

    
    public WSRoutingOrderV2PK executeRoutingOrderV2Asynchronously(WSExecuteRoutingOrderV2Asynchronously wsExecuteRoutingOrderAsynchronously) throws RemoteException {
        return null;
    }

    
    public WSString executeRoutingOrderV2Synchronously(WSExecuteRoutingOrderV2Synchronously wsExecuteRoutingOrderSynchronously) throws RemoteException {
        return null;
    }

    
    public WSRoutingOrderV2PKArray getRoutingOrderV2PKsByCriteria(WSGetRoutingOrderV2PKsByCriteria wsGetRoutingOrderV2PKsByCriteria) throws RemoteException {
        return null;
    }

    
    public WSRoutingOrderV2Array getRoutingOrderV2SByCriteria(WSGetRoutingOrderV2SByCriteria wsGetRoutingOrderV2SByCriteria) throws RemoteException {
        return null;
    }

    
    public WSRoutingOrderV2Array getRoutingOrderV2ByCriteriaWithPaging(WSGetRoutingOrderV2ByCriteriaWithPaging wsGetRoutingOrderV2ByCriteriaWithPaging) throws RemoteException {
        return null;
    }

    
    public WSRoutingRulePKArray routeItemV2(WSRouteItemV2 wsRouteItem) throws RemoteException {
        return null;
    }

    
    public WSRoutingEngineV2Status routingEngineV2Action(WSRoutingEngineV2Action wsRoutingEngineAction) throws RemoteException {
        return null;
    }

    
    public WSUniverse getUniverse(WSGetUniverse wsGetUniverse) throws RemoteException {
        return null;
    }

    
    public WSBoolean existsUniverse(WSExistsUniverse wsExistsUniverse) throws RemoteException {
        return null;
    }

    
    public WSUniversePKArray getUniversePKs(WSGetUniversePKs regex) throws RemoteException {
        return null;
    }

    
    public WSUniversePKArray getUniverseByRevision(WSGetUniverseByRevision wsUniverseByRevision) throws RemoteException {
        return null;
    }

    
    public WSUniversePK putUniverse(WSPutUniverse wsUniverse) throws RemoteException {
        return null;
    }

    
    public WSUniversePK deleteUniverse(WSDeleteUniverse wsUniverseDelete) throws RemoteException {
        return null;
    }

    
    public WSStringArray getObjectsForUniverses(WSGetObjectsForUniverses regex) throws RemoteException {
        return null;
    }

    
    public WSUniverse getCurrentUniverse(WSGetCurrentUniverse wsGetCurrentUniverse) throws RemoteException {
        return null;
    }

    
    public WSSynchronizationPlan getSynchronizationPlan(WSGetSynchronizationPlan wsGetSynchronizationPlan) throws RemoteException {
        return null;
    }

    
    public WSBoolean existsSynchronizationPlan(WSExistsSynchronizationPlan wsExistsSynchronizationPlan) throws RemoteException {
        return null;
    }

    
    public WSSynchronizationPlanPKArray getSynchronizationPlanPKs(WSGetSynchronizationPlanPKs regex) throws RemoteException {
        return null;
    }

    
    public WSSynchronizationPlanPK putSynchronizationPlan(WSPutSynchronizationPlan wsSynchronizationPlan) throws RemoteException {
        return null;
    }

    
    public WSSynchronizationPlanPK deleteSynchronizationPlan(WSDeleteSynchronizationPlan wsSynchronizationPlanDelete) throws RemoteException {
        return null;
    }

    
    public WSStringArray getObjectsForSynchronizationPlans(WSGetObjectsForSynchronizationPlans regex) throws RemoteException {
        return null;
    }

    
    public WSStringArray getSynchronizationPlanObjectsAlgorithms(WSGetSynchronizationPlanObjectsAlgorithms regex) throws RemoteException {
        return null;
    }

    
    public WSStringArray getSynchronizationPlanItemsAlgorithms(WSGetSynchronizationPlanItemsAlgorithms regex) throws RemoteException {
        return null;
    }

    
    public WSSynchronizationPlanStatus synchronizationPlanAction(WSSynchronizationPlanAction wsSynchronizationPlanAction) throws RemoteException {
        return null;
    }

    
    public WSStringArray synchronizationGetUnsynchronizedObjectsIDs(WSSynchronizationGetUnsynchronizedObjectsIDs wsSynchronizationGetUnsynchronizedObjectsIDs) throws RemoteException {
        return null;
    }

    
    public WSString synchronizationGetObjectXML(WSSynchronizationGetObjectXML wsSynchronizationGetObjectXML) throws RemoteException {
        return null;
    }

    
    public WSString synchronizationPutObjectXML(WSSynchronizationPutObjectXML wsSynchronizationPutObjectXML) throws RemoteException {
        return null;
    }

    
    public WSItemPKArray synchronizationGetUnsynchronizedItemPKs(WSSynchronizationGetUnsynchronizedItemPKs wsSynchronizationGetUnsynchronizedItemPKs) throws RemoteException {
        return null;
    }

    
    public WSString synchronizationGetItemXML(WSSynchronizationGetItemXML wsSynchronizationGetItemXML) throws RemoteException {
        return null;
    }

    
    public WSItemPK synchronizationPutItemXML(WSSynchronizationPutItemXML wsSynchronizationPutItemXML) throws RemoteException {
        return null;
    }

    
    public WSSynchronizationItem getSynchronizationItem(WSGetSynchronizationItem wsGetSynchronizationItem) throws RemoteException {
        return null;
    }

    
    public WSBoolean existsSynchronizationItem(WSExistsSynchronizationItem wsExistsSynchronizationItem) throws RemoteException {
        return null;
    }

    
    public WSSynchronizationItemPKArray getSynchronizationItemPKs(WSGetSynchronizationItemPKs regex) throws RemoteException {
        return null;
    }

    
    public WSSynchronizationItemPK putSynchronizationItem(WSPutSynchronizationItem wsSynchronizationItem) throws RemoteException {
        return null;
    }

    
    public WSSynchronizationItemPK deleteSynchronizationItem(WSDeleteSynchronizationItem wsSynchronizationItemDelete) throws RemoteException {
        return null;
    }

    
    public WSSynchronizationItem resolveSynchronizationItem(WSResolveSynchronizationItem wsResolveSynchronizationItem) throws RemoteException {
        return null;
    }

    
    public WSItemPK recoverDroppedItem(WSRecoverDroppedItem wsRecoverDroppedItem) throws RemoteException {
        return null;
    }

    
    public WSDroppedItemPKArray findAllDroppedItemsPKs(WSFindAllDroppedItemsPKs regex) throws RemoteException {
        return null;
    }

    
    public WSDroppedItem loadDroppedItem(WSLoadDroppedItem wsLoadDroppedItem) throws RemoteException {
        return null;
    }

    
    public WSDroppedItemPK removeDroppedItem(WSRemoveDroppedItem wsRemoveDroppedItem) throws RemoteException {
        return null;
    }

    
    public WSMDMConfig getMDMConfiguration() throws RemoteException {
        return null;
    }

    
    public WSCheckServiceConfigResponse checkServiceConfiguration(WSCheckServiceConfigRequest serviceName) throws RemoteException {
        return null;
    }

    
    public WSWorkflowProcessDefinitionUUIDArray workflowGetProcessDefinitions(WSWorkflowGetProcessDefinitions wsworkflowProcessDefinitions) throws RemoteException {
        return null;
    }

    
    public WSWorkflowProcessDefinitionUUID workflowDeploy(WSWorkflowDeploy wsWorkflowDeploy) throws RemoteException {
        return null;
    }

    
    public WSBoolean workflowUnDeploy(WSWorkflowUnDeploy wsWorkflowUnDeploy) throws RemoteException {
        return null;
    }

    
    public WSProcessTaskInstanceArray workflowGetTaskList(WSWorkflowGetTaskList uuid) throws RemoteException {
        return null;
    }

    
    public WSProcessInstanceArray workflowGetProcessInstances(WSWorkflowGetProcessInstances uuid) throws RemoteException {
        return null;
    }

    
    public WSBoolean workflowDeleteProcessInstances(WSWorkflowDeleteProcessInstancesRequest deleteWolkflowRequest) throws RemoteException {
        return null;
    }

    
    public WSBoolean workflowUnassignTask(WSUnassignTask task) throws RemoteException {
        return null;
    }

    
    public WSBoolean workflowAssignTask(WSAssignTask task) throws RemoteException {
        return null;
    }

    
    public WSBoolean workflowSetTaskPriority(WSSetTaskPriority task) throws RemoteException {
        return null;
    }

    
    public WSBoolean workflowSuspendTask(WSSuspendTask task) throws RemoteException {
        return null;
    }

    
    public WSBoolean workflowResumeTask(WSResumeTask task) throws RemoteException {
        return null;
    }

    
    public WSBoolean workflowStartProcessInstance(WSStartProcessInstance task) throws RemoteException {
        return null;
    }

    
    public WSMDMJobArray getMDMJob(WSMDMNULL mdmJobRequest) throws RemoteException {
        return null;
    }

    
    public WSBoolean putMDMJob(WSPUTMDMJob putMDMJobRequest) throws RemoteException {
        return null;
    }

    
    public WSBoolean deleteMDMJob(WSDELMDMJob deleteMDMJobRequest) throws RemoteException {
        return null;
    }

    
    public WSCategoryData getMDMCategory(WSCategoryData wsCategoryDataRequest) throws RemoteException {
        return null;
    }

    
    public WSAutoIncrement getAutoIncrement(WSAutoIncrement wsAutoIncrementRequest) throws RemoteException {
        return null;
    }

    
    public WSBoolean isXmlDB() throws RemoteException {
        return null;
    }

    
    public WSDigest getDigest(WSDigestKey wsSDigestKey) throws RemoteException {
        return null;
    }

    
    public WSLong updateDigest(WSDigest wsDigest) throws RemoteException {
        return null;
    }

    
    public WSMatchRulePK putMatchRule(WSPutMatchRule wsPutMatchRule) throws RemoteException {
        return null;
    }

    
    public WSMatchRulePK deleteMatchRule(WSDeleteMatchRule wsDeleteMatchRule) throws RemoteException {
        return null;
    }
}
