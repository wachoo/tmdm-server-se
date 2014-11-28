// ============================================================================
//
// Copyright (C) 2006-2014 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package com.amalto.core.webservice;

import com.amalto.core.delegator.BeanDelegatorContainer;
import com.amalto.core.integrity.FKIntegrityCheckResult;
import org.apache.commons.lang.NotImplementedException;

import javax.jws.WebService;
import java.rmi.RemoteException;

@SuppressWarnings({ "deprecation", "unchecked" })
@WebService(name="datamanager", serviceName="datamanager")
public class XtentisWSBean implements XtentisPort {

    public XtentisWSBean() {
    }

    @Override
    public WSVersion getComponentVersion(WSGetComponentVersion wsGetComponentVersion) throws RemoteException {
        return BeanDelegatorContainer.getInstance().getXtentisWSDelegator().getComponentVersion(wsGetComponentVersion);
    }

    @Override
    public WSString ping(WSPing wsPing) throws RemoteException {
        return BeanDelegatorContainer.getInstance().getXtentisWSDelegator().ping(wsPing);
    }

    @Override
    public WSString logout(WSLogout logout) throws RemoteException {
        return BeanDelegatorContainer.getInstance().getXtentisWSDelegator().logout(logout);
    }

    @Override
    public WSInt initMDM(WSInitData initData) throws RemoteException {
        return BeanDelegatorContainer.getInstance().getXtentisWSDelegator().initMDM(initData);
    }

    @Override
    public WSMDMConfig getMDMConfiguration() throws RemoteException {
        return BeanDelegatorContainer.getInstance().getXtentisWSDelegator().getMDMConfiguration();
    }

    @Override
    public WSDataModel getDataModel(WSGetDataModel wsDataModelget) throws RemoteException {
        return BeanDelegatorContainer.getInstance().getXtentisWSDelegator().getDataModel(wsDataModelget);
    }

    @Override
    public WSBoolean existsDataModel(WSExistsDataModel wsExistsDataModel) throws RemoteException {
        return BeanDelegatorContainer.getInstance().getXtentisWSDelegator().existsDataModel(wsExistsDataModel);
    }

    @Override
    public WSDataModelPKArray getDataModelPKs(WSRegexDataModelPKs regexp) throws RemoteException {
        return BeanDelegatorContainer.getInstance().getXtentisWSDelegator().getDataModelPKs(regexp);
    }

    @Override
    public WSDataModelPK deleteDataModel(WSDeleteDataModel wsDeleteDataModel) throws RemoteException {
        return BeanDelegatorContainer.getInstance().getXtentisWSDelegator().deleteDataModel(wsDeleteDataModel);
    }

    @Override
    public WSDataModelPK putDataModel(WSPutDataModel wsDataModel) throws RemoteException {
        return BeanDelegatorContainer.getInstance().getXtentisWSDelegator().putDataModel(wsDataModel);
    }

    @Override
    public WSString checkSchema(WSCheckSchema wsSchema) throws RemoteException {
        return BeanDelegatorContainer.getInstance().getXtentisWSDelegator().checkSchema(wsSchema);
    }

    @Override
    public WSString putBusinessConcept(WSPutBusinessConcept wsPutBusinessConcept) throws RemoteException {
        return BeanDelegatorContainer.getInstance().getXtentisWSDelegator().putBusinessConcept(wsPutBusinessConcept);
    }

    @Override
    public WSString putBusinessConceptSchema(WSPutBusinessConceptSchema wsPutBusinessConceptSchema) throws RemoteException {
        return BeanDelegatorContainer.getInstance().getXtentisWSDelegator().putBusinessConceptSchema(wsPutBusinessConceptSchema);
    }

    @Override
    public WSString deleteBusinessConcept(WSDeleteBusinessConcept wsDeleteBusinessConcept) throws RemoteException {
        return BeanDelegatorContainer.getInstance().getXtentisWSDelegator().deleteBusinessConcept(wsDeleteBusinessConcept);
    }

    @Override
    public WSStringArray getBusinessConcepts(WSGetBusinessConcepts wsGetBusinessConcepts) throws RemoteException {
        return BeanDelegatorContainer.getInstance().getXtentisWSDelegator().getBusinessConcepts(wsGetBusinessConcepts);
    }

    @Override
    public WSConceptKey getBusinessConceptKey(WSGetBusinessConceptKey wsGetBusinessConceptKey) throws RemoteException {
        return BeanDelegatorContainer.getInstance().getXtentisWSDelegator().getBusinessConceptKey(wsGetBusinessConceptKey);
    }

    @Override
    public WSDataCluster getDataCluster(WSGetDataCluster wsDataClusterGet) throws RemoteException {
        return BeanDelegatorContainer.getInstance().getXtentisWSDelegator().getDataCluster(wsDataClusterGet);
    }

    @Override
    public WSBoolean existsDataCluster(WSExistsDataCluster wsExistsDataCluster) throws RemoteException {
        return BeanDelegatorContainer.getInstance().getXtentisWSDelegator().existsDataCluster(wsExistsDataCluster);
    }

    @Override
    public WSBoolean existsDBDataCluster(WSExistsDBDataCluster wsExistsDataCluster) throws RemoteException {
        return BeanDelegatorContainer.getInstance().getXtentisWSDelegator().existsDBDataCluster(wsExistsDataCluster);
    }

    @Override
    public WSDataClusterPKArray getDataClusterPKs(WSRegexDataClusterPKs regexp) throws RemoteException {
        return BeanDelegatorContainer.getInstance().getXtentisWSDelegator().getDataClusterPKs(regexp);
    }

    @Override
    public WSDataClusterPK deleteDataCluster(WSDeleteDataCluster wsDeleteDataCluster) throws RemoteException {
        return BeanDelegatorContainer.getInstance().getXtentisWSDelegator().deleteDataCluster(wsDeleteDataCluster);
    }

    @Override
    public WSDataClusterPK putDataCluster(WSPutDataCluster wsDataCluster) throws RemoteException {
        return BeanDelegatorContainer.getInstance().getXtentisWSDelegator().putDataCluster(wsDataCluster);
    }

    @Override
    public WSBoolean putDBDataCluster(WSPutDBDataCluster wsDataCluster) throws RemoteException {
        return BeanDelegatorContainer.getInstance().getXtentisWSDelegator().putDBDataCluster(wsDataCluster);
    }

    @Override
    public WSStringArray getConceptsInDataCluster(WSGetConceptsInDataCluster wsGetConceptsInDataCluster) throws RemoteException {
        return BeanDelegatorContainer.getInstance().getXtentisWSDelegator().getConceptsInDataCluster(wsGetConceptsInDataCluster);
    }

    @Override
    public WSConceptRevisionMap getConceptsInDataClusterWithRevisions(
            WSGetConceptsInDataClusterWithRevisions wsGetConceptsInDataClusterWithRevisions) throws RemoteException {
        return BeanDelegatorContainer.getInstance().getXtentisWSDelegator()
                .getConceptsInDataClusterWithRevisions(wsGetConceptsInDataClusterWithRevisions);
    }

    @Override
    public WSView getView(WSGetView wsViewGet) throws RemoteException {
        return BeanDelegatorContainer.getInstance().getXtentisWSDelegator().getView(wsViewGet);
    }

    @Override
    public WSBoolean existsView(WSExistsView wsExistsView) throws RemoteException {
        return BeanDelegatorContainer.getInstance().getXtentisWSDelegator().existsView(wsExistsView);
    }

    @Override
    public WSViewPKArray getViewPKs(WSGetViewPKs regexp) throws RemoteException {
        return BeanDelegatorContainer.getInstance().getXtentisWSDelegator().getViewPKs(regexp);
    }

    @Override
    public WSViewPK deleteView(WSDeleteView wsDeleteView) throws RemoteException {
        return BeanDelegatorContainer.getInstance().getXtentisWSDelegator().deleteView(wsDeleteView);
    }

    @Override
    public WSViewPK putView(WSPutView wsView) throws RemoteException {
        return BeanDelegatorContainer.getInstance().getXtentisWSDelegator().putView(wsView);
    }

    @Override
    public WSStringArray viewSearch(WSViewSearch wsViewSearch) throws RemoteException {
        return BeanDelegatorContainer.getInstance().getXtentisWSDelegator().viewSearch(wsViewSearch);
    }

    @Override
    public WSStringArray xPathsSearch(WSXPathsSearch wsXPathsSearch) throws RemoteException {
        return BeanDelegatorContainer.getInstance().getXtentisWSDelegator().xPathsSearch(wsXPathsSearch);
    }

    @Override
    public WSString count(WSCount wsCount) throws RemoteException {
        return BeanDelegatorContainer.getInstance().getXtentisWSDelegator().count(wsCount);
    }

    @Override
    public WSStringArray getItems(WSGetItems wsGetItems) throws RemoteException {
        return BeanDelegatorContainer.getInstance().getXtentisWSDelegator().getItems(wsGetItems);
    }

    @Override
    public WSStringArray getItemsSort(WSGetItemsSort wsGetItemsSort) throws RemoteException {
        return BeanDelegatorContainer.getInstance().getXtentisWSDelegator().getItemsSort(wsGetItemsSort);
    }

    @Override
    public WSItemPKsByCriteriaResponse getItemPKsByCriteria(WSGetItemPKsByCriteria wsGetItemPKsByCriteria) throws RemoteException {
        return BeanDelegatorContainer.getInstance().getXtentisWSDelegator().getItemPKsByCriteria(wsGetItemPKsByCriteria);
    }

    @Override
    public WSItemPKsByCriteriaResponse getItemPKsByFullCriteria(WSGetItemPKsByFullCriteria wsGetItemPKsByFullCriteria)
            throws RemoteException {
        return BeanDelegatorContainer.getInstance().getXtentisWSDelegator().getItemPKsByFullCriteria(wsGetItemPKsByFullCriteria);
    }

    @Override
    public WSItem getItem(WSGetItem wsGetItem) throws RemoteException {
        return BeanDelegatorContainer.getInstance().getXtentisWSDelegator().getItem(wsGetItem);
    }

    @Override
    public WSBoolean existsItem(WSExistsItem wsExistsItem) throws RemoteException {
        return BeanDelegatorContainer.getInstance().getXtentisWSDelegator().existsItem(wsExistsItem);
    }

    @Override
    public WSStringArray quickSearch(WSQuickSearch wsQuickSearch) throws RemoteException {
        return BeanDelegatorContainer.getInstance().getXtentisWSDelegator().quickSearch(wsQuickSearch);
    }

    @Override
    public WSString getBusinessConceptValue(WSGetBusinessConceptValue wsGetBusinessConceptValue) throws RemoteException {
        return BeanDelegatorContainer.getInstance().getXtentisWSDelegator().getBusinessConceptValue(wsGetBusinessConceptValue);
    }

    @Override
    public WSStringArray getFullPathValues(WSGetFullPathValues wsGetFullPathValues) throws RemoteException {
        return BeanDelegatorContainer.getInstance().getXtentisWSDelegator().getFullPathValues(wsGetFullPathValues);
    }

    @Override
    public WSItemPK putItem(WSPutItem wsPutItem) throws RemoteException {
        return BeanDelegatorContainer.getInstance().getXtentisWSDelegator().putItem(wsPutItem);
    }

    @Override
    public WSItemPKArray putItemArray(WSPutItemArray wsPutItemArray) throws RemoteException {
        return BeanDelegatorContainer.getInstance().getXtentisWSDelegator().putItemArray(wsPutItemArray);
    }

    @Override
    public WSItemPKArray putItemWithReportArray(com.amalto.core.webservice.WSPutItemWithReportArray wsPutItemWithReportArray)
            throws RemoteException {
        return BeanDelegatorContainer.getInstance().getXtentisWSDelegator().putItemWithReportArray(wsPutItemWithReportArray);
    }

    @Override
    public WSItemPK putItemWithReport(com.amalto.core.webservice.WSPutItemWithReport wsPutItemWithReport) throws RemoteException {
        return BeanDelegatorContainer.getInstance().getXtentisWSDelegator().putItemWithReport(wsPutItemWithReport);
    }

    @Override
    public WSItemPK putItemWithCustomReport(com.amalto.core.webservice.WSPutItemWithCustomReport wsPutItemWithCustomReport)
            throws RemoteException {
        return BeanDelegatorContainer.getInstance().getXtentisWSDelegator().putItemWithCustomReport(wsPutItemWithCustomReport);
    }

    @Override
    public WSPipeline extractUsingTransformer(WSExtractUsingTransformer wsExtractUsingTransformer) throws RemoteException {
        return BeanDelegatorContainer.getInstance().getXtentisWSDelegator().extractUsingTransformer(wsExtractUsingTransformer);
    }

    @Override
    public WSPipeline extractUsingTransformerThruView(WSExtractUsingTransformerThruView wsExtractUsingTransformerThruView)
            throws RemoteException {
        return BeanDelegatorContainer.getInstance().getXtentisWSDelegator()
                .extractUsingTransformerThruView(wsExtractUsingTransformerThruView);
    }

    @Override
    public WSItemPK deleteItem(WSDeleteItem wsDeleteItem) throws RemoteException {
        return BeanDelegatorContainer.getInstance().getXtentisWSDelegator().deleteItem(wsDeleteItem);
    }

    @Override
    public WSString deleteItemWithReport(WSDeleteItemWithReport wsDeleteItem) throws RemoteException {
        return BeanDelegatorContainer.getInstance().getXtentisWSDelegator().deleteItemWithReport(wsDeleteItem);
    }

    @Override
    public WSInt deleteItems(WSDeleteItems wsDeleteItems) throws RemoteException {
        return BeanDelegatorContainer.getInstance().getXtentisWSDelegator().deleteItems(wsDeleteItems);
    }

    @Override
    public WSDroppedItemPK dropItem(WSDropItem wsDropItem) throws RemoteException {
        return BeanDelegatorContainer.getInstance().getXtentisWSDelegator().dropItem(wsDropItem);
    }

    @Override
    public WSStringArray runQuery(WSRunQuery wsRunQuery) throws RemoteException {
        return BeanDelegatorContainer.getInstance().getXtentisWSDelegator().runQuery(wsRunQuery);
    }

    @Override
    public WSServiceGetDocument getServiceDocument(WSString serviceName) throws RemoteException {
        throw new NotImplementedException();
    }

    @Override
    public WSString getServiceConfiguration(WSServiceGetConfiguration wsGetConfiguration) throws RemoteException {
        throw new NotImplementedException(); // TODO
    }

    @Override
    public WSCheckServiceConfigResponse checkServiceConfiguration(WSCheckServiceConfigRequest serviceName) throws RemoteException {
        throw new NotImplementedException(); // TODO
    }

    @Override
    public WSString putServiceConfiguration(WSServicePutConfiguration wsPutConfiguration) throws RemoteException {
        throw new NotImplementedException(); // TODO
    }

    @Override
    public WSString serviceAction(WSServiceAction wsServiceAction) throws RemoteException {
        throw new NotImplementedException(); // TODO
    }

    @Override
    public WSServicesList getServicesList(WSGetServicesList wsGetServicesList) throws RemoteException {
        throw new NotImplementedException(); // TODO
    }

    @Override
    public WSStoredProcedurePK deleteStoredProcedure(WSDeleteStoredProcedure wsStoredProcedureDelete) throws RemoteException {
        return BeanDelegatorContainer.getInstance().getXtentisWSDelegator().deleteStoredProcedure(wsStoredProcedureDelete);
    }

    @Override
    public WSStringArray executeStoredProcedure(WSExecuteStoredProcedure wsExecuteStoredProcedure) throws RemoteException {
        return BeanDelegatorContainer.getInstance().getXtentisWSDelegator().executeStoredProcedure(wsExecuteStoredProcedure);
    }

    @Override
    public WSStoredProcedure getStoredProcedure(WSGetStoredProcedure wsGetStoredProcedure) throws RemoteException {
        return BeanDelegatorContainer.getInstance().getXtentisWSDelegator().getStoredProcedure(wsGetStoredProcedure);
    }

    @Override
    public WSBoolean existsStoredProcedure(WSExistsStoredProcedure wsExistsStoredProcedure) throws RemoteException {
        return BeanDelegatorContainer.getInstance().getXtentisWSDelegator().existsStoredProcedure(wsExistsStoredProcedure);
    }

    @Override
    public WSStoredProcedurePKArray getStoredProcedurePKs(WSRegexStoredProcedure regex) throws RemoteException {
        return BeanDelegatorContainer.getInstance().getXtentisWSDelegator().getStoredProcedurePKs(regex);
    }

    @Override
    public WSStoredProcedurePK putStoredProcedure(WSPutStoredProcedure wsStoredProcedure) throws RemoteException {
        return BeanDelegatorContainer.getInstance().getXtentisWSDelegator().putStoredProcedure(wsStoredProcedure);
    }

    @Override
    public WSMenuPK deleteMenu(WSDeleteMenu wsMenuDelete) throws RemoteException {
        return BeanDelegatorContainer.getInstance().getXtentisWSDelegator().deleteMenu(wsMenuDelete);
    }

    @Override
    public WSMenu getMenu(WSGetMenu wsGetMenu) throws RemoteException {
        return BeanDelegatorContainer.getInstance().getXtentisWSDelegator().getMenu(wsGetMenu);
    }

    @Override
    public WSBoolean existsMenu(WSExistsMenu wsExistsMenu) throws RemoteException {
        return BeanDelegatorContainer.getInstance().getXtentisWSDelegator().existsMenu(wsExistsMenu);
    }

    @Override
    public WSMenuPKArray getMenuPKs(WSGetMenuPKs regex) throws RemoteException {
        return BeanDelegatorContainer.getInstance().getXtentisWSDelegator().getMenuPKs(regex);
    }

    @Override
    public WSMenuPK putMenu(WSPutMenu wsMenu) throws RemoteException {
        return BeanDelegatorContainer.getInstance().getXtentisWSDelegator().putMenu(wsMenu);
    }

    @Override
    public WSBackgroundJob getBackgroundJob(WSGetBackgroundJob wsBackgroundJobGet) throws RemoteException {
        return BeanDelegatorContainer.getInstance().getXtentisWSDelegator().getBackgroundJob(wsBackgroundJobGet);
    }

    @Override
    public WSBackgroundJobPKArray findBackgroundJobPKs(WSFindBackgroundJobPKs wsFindBackgroundJobPKs) throws RemoteException {
        return BeanDelegatorContainer.getInstance().getXtentisWSDelegator().findBackgroundJobPKs(wsFindBackgroundJobPKs);
    }

    @Override
    public WSBackgroundJobPK putBackgroundJob(WSPutBackgroundJob wsputjob) throws RemoteException {
        return BeanDelegatorContainer.getInstance().getXtentisWSDelegator().putBackgroundJob(wsputjob);
    }

    @Override
    public WSUniverse getCurrentUniverse(WSGetCurrentUniverse wsGetCurrentUniverse) throws RemoteException {
        return BeanDelegatorContainer.getInstance().getXtentisWSDelegator().getCurrentUniverse(wsGetCurrentUniverse);
    }

    @Override
    public WSTransformer getTransformer(WSGetTransformer wsGetTransformer) throws RemoteException {
        throw new NotImplementedException(); // TODO
    }

    @Override
    public WSBoolean existsTransformer(WSExistsTransformer wsExistsTransformer) throws RemoteException {
        throw new NotImplementedException(); // TODO
    }

    @Override
    public WSTransformerPKArray getTransformerPKs(WSGetTransformerPKs regex) throws RemoteException {
        throw new NotImplementedException(); // TODO
    }

    @Override
    public WSTransformerPK putTransformer(WSPutTransformer wsTransformer) throws RemoteException {
        throw new NotImplementedException(); // TODO
    }

    @Override
    public WSPipeline processBytesUsingTransformer(WSProcessBytesUsingTransformer wsProjectBytes) throws RemoteException {
        throw new NotImplementedException(); // TODO
    }

    @Override
    public WSPipeline processFileUsingTransformer(WSProcessFileUsingTransformer wsProcessFile) throws RemoteException {
        throw new NotImplementedException(); // TODO
    }

    @Override
    public WSBackgroundJobPK processBytesUsingTransformerAsBackgroundJob(
            WSProcessBytesUsingTransformerAsBackgroundJob wsProcessBytesUsingTransformerAsBackgroundJob) throws RemoteException {
        throw new NotImplementedException();
    }

    @Override
    public WSBackgroundJobPK processFileUsingTransformerAsBackgroundJob(
            WSProcessFileUsingTransformerAsBackgroundJob wsProcessFileUsingTransformerAsBackgroundJob) throws RemoteException {
        throw new NotImplementedException(); // TODO
    }

    @Override
    public WSDroppedItemPKArray findAllDroppedItemsPKs(WSFindAllDroppedItemsPKs regex) throws RemoteException {
        return BeanDelegatorContainer.getInstance().getXtentisWSDelegator().findAllDroppedItemsPKs(regex);
    }

    @Override
    public WSDroppedItem loadDroppedItem(WSLoadDroppedItem wsLoadDroppedItem) throws RemoteException {
        return BeanDelegatorContainer.getInstance().getXtentisWSDelegator().loadDroppedItem(wsLoadDroppedItem);
    }

    @Override
    public WSItemPK recoverDroppedItem(WSRecoverDroppedItem wsRecoverDroppedItem) throws RemoteException {
        return BeanDelegatorContainer.getInstance().getXtentisWSDelegator().recoverDroppedItem(wsRecoverDroppedItem);
    }

    @Override
    public WSDroppedItemPK removeDroppedItem(WSRemoveDroppedItem wsRemoveDroppedItem) throws RemoteException {
        return BeanDelegatorContainer.getInstance().getXtentisWSDelegator().removeDroppedItem(wsRemoveDroppedItem);
    }

    @Override
    public WSRoutingRule getRoutingRule(WSGetRoutingRule wsRoutingRuleGet) throws RemoteException {
        return BeanDelegatorContainer.getInstance().getXtentisWSDelegator().getRoutingRule(wsRoutingRuleGet);
    }

    @Override
    public WSBoolean existsRoutingRule(WSExistsRoutingRule wsExistsRoutingRule) throws RemoteException {
        return BeanDelegatorContainer.getInstance().getXtentisWSDelegator().existsRoutingRule(wsExistsRoutingRule);
    }

    @Override
    public WSRoutingRulePK deleteRoutingRule(WSDeleteRoutingRule wsDeleteRoutingRule) throws RemoteException {
        return BeanDelegatorContainer.getInstance().getXtentisWSDelegator().deleteRoutingRule(wsDeleteRoutingRule);
    }

    @Override
    public WSRoutingRulePK putRoutingRule(WSPutRoutingRule wsRoutingRule) throws RemoteException {
        return BeanDelegatorContainer.getInstance().getXtentisWSDelegator().putRoutingRule(wsRoutingRule);
    }

    @Override
    public WSRoutingRulePKArray getRoutingRulePKs(WSGetRoutingRulePKs regex) throws RemoteException {
        return BeanDelegatorContainer.getInstance().getXtentisWSDelegator().getRoutingRulePKs(regex);
    }

    @Override
    public WSTransformerV2PK deleteTransformerV2(WSDeleteTransformerV2 wsTransformerV2Delete) throws RemoteException {
        return BeanDelegatorContainer.getInstance().getXtentisWSDelegator().deleteTransformerV2(wsTransformerV2Delete);
    }

    @Override
    public WSTransformerV2 getTransformerV2(WSGetTransformerV2 wsGetTransformerV2) throws RemoteException {
        return BeanDelegatorContainer.getInstance().getXtentisWSDelegator().getTransformerV2(wsGetTransformerV2);
    }

    @Override
    public WSBoolean existsTransformerV2(WSExistsTransformerV2 wsExistsTransformerV2) throws RemoteException {
        return BeanDelegatorContainer.getInstance().getXtentisWSDelegator().existsTransformerV2(wsExistsTransformerV2);
    }

    @Override
    public WSTransformerV2PKArray getTransformerV2PKs(WSGetTransformerV2PKs regex) throws RemoteException {
        return BeanDelegatorContainer.getInstance().getXtentisWSDelegator().getTransformerV2PKs(regex);
    }

    @Override
    public WSTransformerV2PK putTransformerV2(WSPutTransformerV2 wsTransformerV2) throws RemoteException {
        return BeanDelegatorContainer.getInstance().getXtentisWSDelegator().putTransformerV2(wsTransformerV2);
    }

    @Override
    public WSTransformerContext executeTransformerV2(WSExecuteTransformerV2 wsExecuteTransformerV2) throws RemoteException {
        return BeanDelegatorContainer.getInstance().getXtentisWSDelegator().executeTransformerV2(wsExecuteTransformerV2);
    }

    @Override
    public WSBackgroundJobPK executeTransformerV2AsJob(WSExecuteTransformerV2AsJob wsExecuteTransformerV2AsJob)
            throws RemoteException {
        return BeanDelegatorContainer.getInstance().getXtentisWSDelegator()
                .executeTransformerV2AsJob(wsExecuteTransformerV2AsJob);
    }

    @Override
    public WSTransformerContext extractThroughTransformerV2(WSExtractThroughTransformerV2 wsExtractThroughTransformerV2)
            throws RemoteException {
        return BeanDelegatorContainer.getInstance().getXtentisWSDelegator()
                .extractThroughTransformerV2(wsExtractThroughTransformerV2);
    }

    @Override
    public WSBoolean existsTransformerPluginV2(WSExistsTransformerPluginV2 wsExistsTransformerPlugin) throws RemoteException {
        return BeanDelegatorContainer.getInstance().getXtentisWSDelegator().existsTransformerPluginV2(wsExistsTransformerPlugin);
    }

    @Override
    public WSString getTransformerPluginV2Configuration(WSTransformerPluginV2GetConfiguration wsGetConfiguration)
            throws RemoteException {
        return BeanDelegatorContainer.getInstance().getXtentisWSDelegator()
                .getTransformerPluginV2Configuration(wsGetConfiguration);
    }

    @Override
    public WSString putTransformerPluginV2Configuration(WSTransformerPluginV2PutConfiguration wsPutConfiguration)
            throws RemoteException {
        return BeanDelegatorContainer.getInstance().getXtentisWSDelegator()
                .putTransformerPluginV2Configuration(wsPutConfiguration);
    }

    @Override
    public WSTransformerPluginV2Details getTransformerPluginV2Details(
            WSGetTransformerPluginV2Details wsGetTransformerPluginDetails) throws RemoteException {
        return BeanDelegatorContainer.getInstance().getXtentisWSDelegator()
                .getTransformerPluginV2Details(wsGetTransformerPluginDetails);
    }

    @Override
    public WSTransformerPluginV2SList getTransformerPluginV2SList(WSGetTransformerPluginV2SList wsGetTransformerPluginsList)
            throws RemoteException {
        return BeanDelegatorContainer.getInstance().getXtentisWSDelegator()
                .getTransformerPluginV2SList(wsGetTransformerPluginsList);
    }

    @Override
    public WSRoutingOrderV2 getRoutingOrderV2(WSGetRoutingOrderV2 wsGetRoutingOrder) throws RemoteException {
        return BeanDelegatorContainer.getInstance().getXtentisWSDelegator().getRoutingOrderV2(wsGetRoutingOrder);
    }

    @Override
    public WSRoutingOrderV2 existsRoutingOrderV2(WSExistsRoutingOrderV2 wsExistsRoutingOrder) throws RemoteException {
        return BeanDelegatorContainer.getInstance().getXtentisWSDelegator().existsRoutingOrderV2(wsExistsRoutingOrder);
    }

    @Override
    public WSRoutingOrderV2PK deleteRoutingOrderV2(WSDeleteRoutingOrderV2 wsDeleteRoutingOrder) throws RemoteException {
        return BeanDelegatorContainer.getInstance().getXtentisWSDelegator().deleteRoutingOrderV2(wsDeleteRoutingOrder);
    }

    @Override
    public WSRoutingOrderV2PK executeRoutingOrderV2Asynchronously(
            WSExecuteRoutingOrderV2Asynchronously wsExecuteRoutingOrderAsynchronously) throws RemoteException {
        return BeanDelegatorContainer.getInstance().getXtentisWSDelegator()
                .executeRoutingOrderV2Asynchronously(wsExecuteRoutingOrderAsynchronously);
    }

    @Override
    public WSString executeRoutingOrderV2Synchronously(WSExecuteRoutingOrderV2Synchronously wsExecuteRoutingOrderSynchronously)
            throws RemoteException {
        return BeanDelegatorContainer.getInstance().getXtentisWSDelegator()
                .executeRoutingOrderV2Synchronously(wsExecuteRoutingOrderSynchronously);
    }

    @Override
    public WSRoutingOrderV2PKArray getRoutingOrderV2PKsByCriteria(
            WSGetRoutingOrderV2PKsByCriteria wsGetRoutingOrderV2PKsByCriteria) throws RemoteException {
        return BeanDelegatorContainer.getInstance().getXtentisWSDelegator()
                .getRoutingOrderV2PKsByCriteria(wsGetRoutingOrderV2PKsByCriteria);
    }

    @Override
    public WSRoutingOrderV2Array getRoutingOrderV2SByCriteria(WSGetRoutingOrderV2SByCriteria wsGetRoutingOrderV2SByCriteria)
            throws RemoteException {
        return BeanDelegatorContainer.getInstance().getXtentisWSDelegator()
                .getRoutingOrderV2SByCriteria(wsGetRoutingOrderV2SByCriteria);
    }

    @Override
    public WSRoutingRulePKArray routeItemV2(WSRouteItemV2 wsRouteItem) throws RemoteException {
        return BeanDelegatorContainer.getInstance().getXtentisWSDelegator().routeItemV2(wsRouteItem);
    }

    @Override
    public WSRoutingEngineV2Status routingEngineV2Action(WSRoutingEngineV2Action wsRoutingEngineAction) throws RemoteException {
        return BeanDelegatorContainer.getInstance().getXtentisWSDelegator().routingEngineV2Action(wsRoutingEngineAction);

    }

    @Override
    public WSCategoryData getMDMCategory(WSCategoryData request) throws RemoteException {
        return BeanDelegatorContainer.getInstance().getXtentisWSDelegator().getMDMCategory(request);
    }

    @Override
    public WSBoolean putMDMJob(WSPUTMDMJob job) throws RemoteException {
        return BeanDelegatorContainer.getInstance().getXtentisWSDelegator().putMDMJob(job);
    }

    @Override
    public WSBoolean deleteMDMJob(WSDELMDMJob job) throws RemoteException {
        return BeanDelegatorContainer.getInstance().getXtentisWSDelegator().deleteMDMJob(job);
    }

    @Override
    public WSMDMJobArray getMDMJob(WSMDMNULL job) {
        return BeanDelegatorContainer.getInstance().getXtentisWSDelegator().getMDMJob(job);
    }

    @Override
    public WSAutoIncrement getAutoIncrement(WSAutoIncrement wsAutoIncrementRequest) throws RemoteException {
        return BeanDelegatorContainer.getInstance().getXtentisWSDelegator().getAutoIncrement(wsAutoIncrementRequest);
    }

    @Override
    public WSBoolean isItemModifiedByOther(WSIsItemModifiedByOther wsItem) throws RemoteException {
        return BeanDelegatorContainer.getInstance().getXtentisWSDelegator().isItemModifiedByOther(wsItem.getWsItem());
    }

    @Override
    public WSString countItemsByCustomFKFilters(WSCountItemsByCustomFKFilters wsCountItemsByCustomFKFilters)
            throws RemoteException {
        return BeanDelegatorContainer.getInstance().getXtentisWSDelegator()
                .countItemsByCustomFKFilters(wsCountItemsByCustomFKFilters);
    }

    @Override
    public WSStringArray getItemsByCustomFKFilters(WSGetItemsByCustomFKFilters wsGetItemsByCustomFKFilters)
            throws RemoteException {
        return BeanDelegatorContainer.getInstance().getXtentisWSDelegator()
                .getItemsByCustomFKFilters(wsGetItemsByCustomFKFilters);
    }

    @Override
    public WSString refreshCache(WSRefreshCache refreshCache) throws RemoteException {
        return BeanDelegatorContainer.getInstance().getXtentisWSDelegator().refreshCache(refreshCache);
    }

    @Override
    public WSItemPK partialPutItem(WSPartialPutItem wsPartialPutItem) throws RemoteException {
        return BeanDelegatorContainer.getInstance().getXtentisWSDelegator().partialPutItem(wsPartialPutItem);
    }

    @Override
    public WSRoutingOrderV2Array getRoutingOrderV2ByCriteriaWithPaging(
            WSGetRoutingOrderV2ByCriteriaWithPaging wsGetRoutingOrderV2ByCriteriaWithPaging) throws RemoteException {
        return BeanDelegatorContainer.getInstance().getXtentisWSDelegator()
                .getRoutingOrderV2ByCriteriaWithPaging(wsGetRoutingOrderV2ByCriteriaWithPaging);
    }

    @Override
    public WSDigest getDigest(WSDigestKey wsDigestKey) throws RemoteException {
        return BeanDelegatorContainer.getInstance().getXtentisWSDelegator().getDigest(wsDigestKey);
    }

    @Override
    public WSLong updateDigest(WSDigest wsDigest) throws RemoteException {
        return BeanDelegatorContainer.getInstance().getXtentisWSDelegator().updateDigest(wsDigest);
    }

    @Override
    public WSRole getRole(WSGetRole wsGetRole) throws RemoteException {
        return new WSRole(wsGetRole.getWsRolePK().getPk(), "", new WSRoleSpecification[0]);
    }

    @Override
    public WSBoolean isPagingAccurate(WSInt wsInt) throws RemoteException {
        return BeanDelegatorContainer.getInstance().getXtentisWSDelegator().isPagingAccurate(wsInt);
    }

    @Override
    public WSBoolean supportStaging(WSDataClusterPK dataClusterPK) throws RemoteException {
        return BeanDelegatorContainer.getInstance().getXtentisWSDelegator().supportStaging(dataClusterPK);
    }

    @Override
    public WSUniversePKArray getUniversePKs(WSGetUniversePKs wsGetUniversePKs) throws RemoteException {
        return new WSUniversePKArray(new WSUniversePK[] { new WSUniversePK("HEAD")}); //$NON-NLS-1$
    }

    @Override
    public FKIntegrityCheckResult checkFKIntegrity(WSDeleteItem deleteItem) throws RemoteException {
        return BeanDelegatorContainer.getInstance().getXtentisWSDelegator().checkFKIntegrity(deleteItem);
    }

    @Override
    public WSRolePKArray getRolePKs(WSGetRolePKs ks) throws RemoteException {
        return null; // TODO Not supported in Open version
    }

    public WSItemPK updateItemMetadata(WSUpdateMetadataItem wsUpdateMetadataItem) throws RemoteException {
        return (BeanDelegatorContainer.getInstance().getXtentisWSDelegator()).updateItemMetadata(wsUpdateMetadataItem);
    }
}
