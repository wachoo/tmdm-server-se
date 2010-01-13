package com.amalto.core.delegator;

import java.rmi.RemoteException;

import com.amalto.core.webservice.*;

public interface IXtentisWSDelegator {
	/***************************************************************************
	 * 
	 * S E R V I C E S
	 *  
	 *	 **************************************************************************/

	/***************************************************************************
	 * Components Management
	 * **************************************************************************/
	
	/**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * @ejb.permission 
	 * 	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
	 */
	public WSVersion getComponentVersion(WSGetComponentVersion wsGetComponentVersion) throws RemoteException ;

	
	/***************************************************************************
	 * Ping
	 * **************************************************************************/

	/**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * @ejb.permission 
	 * 	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
	 */
	public WSString ping(WSPing wsPing) throws RemoteException ;

	/***************************************************************************
	 * Logout
	 * **************************************************************************/

	/**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * @ejb.permission 
	 * 	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
	 */
	public WSString logout(WSLogout logout) throws RemoteException ;
	

	
	/***************************************************************************
	 * Initialize
	 * **************************************************************************/
	

	/**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * @ejb.permission 
	 * 	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
	 */
	public WSInt initMDM(WSInitData initData) throws RemoteException ;
	
	/**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * @ejb.permission 
	 * 	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
	 */
	public WSMDMConfig getMDMConfiguration() throws RemoteException ;
	/***************************************************************************
	 * Data Model
	 * **************************************************************************/
    
	/**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * @ejb.permission 
	 * 	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
	 */
    public WSDataModel getDataModel(WSGetDataModel wsDataModelget)
    throws RemoteException ;
    
	/**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * @ejb.permission 
	 * 	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
	 */
    public WSBoolean existsDataModel(WSExistsDataModel wsExistsDataModel)
    throws RemoteException ;
    
	/**
	 * *ejb.interface-method view-type = "service-endpoint"
	 * *ejb.permission 
	 * 	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
	 * 	
	 */ 
    /*
    public WSDataModelArray getDataModels(WSRegexDataModels regexp)
    throws RemoteException {
		try {
						
		    WSDataModelArray array = new WSDataModelArray();
		    DataModelPOJO[] dataModels = Util.getDataModelCtrlLocalHome().create().getAllDataModels();
		    ArrayList<WSDataModel> l = new ArrayList<WSDataModel>();
		    String regex = (
		    		(regexp.getRegex()==null) || 
					("".equals(regexp.getRegex())) ||
					("*".equals(regexp.getRegex())) ?
							".*":regexp.getRegex()
			);
			for (int i = 0; i < dataModels.length; i++) {
				if (dataModels[i].getName().matches(regex))
					l.add(VO2WS(dataModels[i]));
            }
			array.setWsDataModels(l.toArray(new WSDataModel[l.size()]));
			return array;
		} catch (Exception e) {
			throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()));
		}
    }
    */
    
	/**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * @ejb.permission 
	 * 	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
	 * 	
	 */       
    public WSDataModelPKArray getDataModelPKs(WSRegexDataModelPKs regexp)
    throws RemoteException ;
    
	/**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * @ejb.permission 
	 * 	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
	 */
    public WSDataModelPK deleteDataModel(WSDeleteDataModel wsDeleteDataModel)
    throws RemoteException ;	
    
	/**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * 
	 * @ejb.permission 
	 * 	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
	 */
    public WSDataModelPK putDataModel(WSPutDataModel wsDataModel)
    throws RemoteException ;
 
    
	/**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * 
	 * @ejb.permission 
	 * 	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
	 */    
	public WSString checkSchema(WSCheckSchema wsSchema) throws RemoteException ;
	
	
	/**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * 
	 * @ejb.permission 
	 * 	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
	 */
    public WSString putBusinessConcept(WSPutBusinessConcept wsPutBusinessConcept)
    throws RemoteException ;
     
	/**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * 
	 * @ejb.permission 
	 * 	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
	 */
    public WSString putBusinessConceptSchema(WSPutBusinessConceptSchema wsPutBusinessConceptSchema)
    throws RemoteException ;
    
	    
	/**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * @ejb.permission 
	 * 	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
	 */ 
    public WSString deleteBusinessConcept(
            WSDeleteBusinessConcept wsDeleteBusinessConcept)
            throws RemoteException ;
    
    
    
	/**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * @ejb.permission 
	 * 	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
	 */   
    public WSStringArray getBusinessConcepts(
            WSGetBusinessConcepts wsGetBusinessConcepts) throws RemoteException ;
    
	/**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * @ejb.permission 
	 * 	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
	 */   
    public WSConceptKey getBusinessConceptKey(
            WSGetBusinessConceptKey wsGetBusinessConceptKey) throws RemoteException ;
	


	
	/***************************************************************************
	 * DataCluster
	 * **************************************************************************/
    
	/**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * @ejb.permission 
	 * 	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
	 */	
	   public WSDataCluster getDataCluster(WSGetDataCluster wsDataClusterGet)
	    throws RemoteException ;
	    
		/**
		 * @ejb.interface-method view-type = "service-endpoint"
		 * @ejb.permission 
		 * 	role-name = "authenticated"
		 * 	view-type = "service-endpoint"
		 */	
		   public WSBoolean existsDataCluster(WSExistsDataCluster wsExistsDataCluster)
		    throws RemoteException ;
			/**
			 * @ejb.interface-method view-type = "service-endpoint"
			 * @ejb.permission 
			 * 	role-name = "authenticated"
			 * 	view-type = "service-endpoint"
			 */	
			   public WSBoolean existsDBDataCluster(WSExistsDBDataCluster wsExistsDataCluster)
			    throws RemoteException ;


		/**
		* @ejb.interface-method view-type = "service-endpoint"
		* @ejb.permission 
		* 	role-name = "authenticated"
		* 	view-type = "service-endpoint"
		*/    
	    public WSDataClusterPKArray getDataClusterPKs(WSRegexDataClusterPKs regexp)
	    throws RemoteException ;

	    
		/**
		 * @ejb.interface-method view-type = "service-endpoint"
		 * @ejb.permission 
		 * 	role-name = "authenticated"
		 * 	view-type = "service-endpoint"
		 */
	    public WSDataClusterPK deleteDataCluster(WSDeleteDataCluster wsDeleteDataCluster)
	    throws RemoteException ;	
	    
		/**
		 * @ejb.interface-method view-type = "service-endpoint"
		 * @ejb.permission 
		 * 	role-name = "authenticated"
		 * 	view-type = "service-endpoint"
		 */   
	    public WSDataClusterPK putDataCluster(WSPutDataCluster wsDataCluster)
	    throws RemoteException ;
		/**
		 * @ejb.interface-method view-type = "service-endpoint"
		 * @ejb.permission 
		 * 	role-name = "authenticated"
		 * 	view-type = "service-endpoint"
		 */   
	    public WSBoolean putDBDataCluster(WSPutDBDataCluster wsDataCluster)
	    throws RemoteException ;

	    /**
		 * @ejb.interface-method view-type = "service-endpoint"
		 * @ejb.permission 
		 * 	role-name = "authenticated"
		 * 	view-type = "service-endpoint"
		 */	
		public WSStringArray getConceptsInDataCluster(WSGetConceptsInDataCluster wsGetConceptsInDataCluster) throws RemoteException ;
		

		/**
		 * @ejb.interface-method view-type = "service-endpoint"
		 * @ejb.permission 
		 * 	role-name = "authenticated"
		 * 	view-type = "service-endpoint"
		 */	
		public WSConceptRevisionMap getConceptsInDataClusterWithRevisions(WSGetConceptsInDataClusterWithRevisions wsGetConceptsInDataClusterWithRevisions) throws RemoteException ;
	
	/***************************************************************************
	 * View
	 * **************************************************************************/
	
	/**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * @ejb.permission 
	 * 	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
	 */	
   public WSView getView(WSGetView wsViewGet)
    throws RemoteException ;

   
	/**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * @ejb.permission 
	 * 	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
	 */	
  public WSBoolean existsView(WSExistsView wsExistsView)
   throws RemoteException ;
   

	/**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * @ejb.permission 
	 * 	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
	 */    
    public WSViewPKArray getViewPKs(WSGetViewPKs regexp) throws RemoteException ;
		    
	/**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * @ejb.permission 
	 * 	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
	 */
    public WSViewPK deleteView(WSDeleteView wsDeleteView)
    throws RemoteException ;
    
	/**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * @ejb.permission 
	 * 	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
	 */   
    public WSViewPK putView(WSPutView wsView)
    throws RemoteException ;

   
	/***************************************************************************
	 * Search
	 * **************************************************************************/
	/**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * @ejb.permission 
	 * 	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
	 */
	public WSStringArray viewSearch(WSViewSearch wsViewSearch) throws RemoteException ;
	
	/**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * @ejb.permission 
	 * 	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
	 */
	public WSStringArray xPathsSearch(WSXPathsSearch wsXPathsSearch) throws RemoteException ;
	

	
	/**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * @ejb.permission 
	 * 	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
	 */
	public WSStringArray getItemsPivotIndex(WSGetItemsPivotIndex wsGetItemsPivotIndex) throws RemoteException ;

	/**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * @ejb.permission 
	 * 	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
	 */
	public WSString count(WSCount wsCount) throws RemoteException ;

	
	
	/**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * @ejb.permission 
	 * 	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
	 */	
	public WSStringArray getItems(WSGetItems wsGetItems) throws RemoteException ;
	
	/**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * @ejb.permission 
	 * 	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
	 */	
	public WSItemPKsByCriteriaResponse getItemPKsByCriteria(WSGetItemPKsByCriteria wsGetItemPKsByCriteria) throws RemoteException ;

	/**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * @ejb.permission 
	 * 	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
	 */
	public WSItem getItem(WSGetItem wsGetItem) throws RemoteException ;
	
	/**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * @ejb.permission 
	 * 	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
	 */
	public WSBoolean existsItem(WSExistsItem wsExistsItem) throws RemoteException ;	
	
	/**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * @ejb.permission 
	 * 	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
	 */
	public WSStringArray quickSearch(WSQuickSearch wsQuickSearch) throws RemoteException ;
	

	/**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * @ejb.permission 
	 * 	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
	 */		
	public WSString getBusinessConceptValue(
			WSGetBusinessConceptValue wsGetBusinessConceptValue)
			throws RemoteException ;
	
	/**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * @ejb.permission 
	 * 	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
	 */	
	public WSStringArray getFullPathValues(WSGetFullPathValues wsGetFullPathValues)
			throws RemoteException ;

    

	/**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * @ejb.permission 
	 * 	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
	 */
	public WSItemPK putItem(WSPutItem wsPutItem) throws RemoteException ;
	/**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * @ejb.permission 
	 * 	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
	 */
	public WSItemPKArray putItemArray(WSPutItemArray wsPutItemArray) throws RemoteException ;
	

	/**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * @ejb.permission 
	 * 	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
	 */
	public WSItemPKArray putItemWithReportArray(com.amalto.core.webservice.WSPutItemWithReportArray wsPutItemWithReportArray) throws RemoteException ;	
	/**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * @ejb.permission 
	 * 	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
	 */
	public WSItemPK putItemWithReport(com.amalto.core.webservice.WSPutItemWithReport wsPutItemWithReport) throws RemoteException ;


	/**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * @ejb.permission 
	 * 	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
	 */
	public WSItemPK putItemWithCustomReport(com.amalto.core.webservice.WSPutItemWithCustomReport wsPutItemWithCustomReport) throws RemoteException;
    
	/***************************************************************************
	 *Extract Items
	 * **************************************************************************/

	/**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * @ejb.permission 
	 * 	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
	 */
	public WSPipeline extractUsingTransformer(WSExtractUsingTransformer wsExtractUsingTransformer) throws RemoteException ;

	/**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * @ejb.permission 
	 * 	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
	 */
	public WSPipeline extractUsingTransformerThruView(WSExtractUsingTransformerThruView wsExtractUsingTransformerThruView) throws RemoteException ;



    
	
	/***************************************************************************
	 * Delete Items
	 * **************************************************************************/
	/**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * @ejb.permission 
	 * 	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
	 */
	public WSItemPK deleteItem(WSDeleteItem wsDeleteItem)
	throws RemoteException ;    
    
	/**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * @ejb.permission 
	 * 	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
	 */
	public WSInt deleteItems(WSDeleteItems wsDeleteItems)
	throws RemoteException ;
	
	/**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * @ejb.permission 
	 * 	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
	 */
	public WSDroppedItemPK dropItem(WSDropItem wsDropItem)
		throws RemoteException ;
	
	
	/***************************************************************************
	 * DirectQuery
	 * **************************************************************************/
	/**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * @ejb.permission 
	 * 	role-name = "administration, DataManagerAdministration"
	 * 	view-type = "service-endpoint"
	 */
	public WSStringArray runQuery(WSRunQuery wsRunQuery) throws RemoteException ;  
	
	

	/***************************************************************************
	 * SERVICES
	 * **************************************************************************/
	/**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * @ejb.permission 
	 * 	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
	 */
	public WSServiceGetDocument getServiceDocument(WSString serviceName) throws RemoteException ;

	
	/**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * @ejb.permission 
	 * 	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
	 */
	public WSString getServiceConfiguration(WSServiceGetConfiguration wsGetConfiguration) throws RemoteException ;
	/**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * @ejb.permission 
	 * 	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
	 */
	public WSCheckServiceConfigResponse checkServiceConfiguration(WSCheckServiceConfigRequest serviceName) throws RemoteException ;
	/**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * @ejb.permission 
	 * 	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
	 */
	public WSString putServiceConfiguration(WSServicePutConfiguration wsPutConfiguration) throws RemoteException ;

	/**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * @ejb.permission 
	 * 	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
	 */
	public WSString serviceAction(WSServiceAction wsServiceAction) throws RemoteException ;
	
	/**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * @ejb.permission 	 
	 *  	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
	 */
	public WSServicesList getServicesList(WSGetServicesList wsGetServicesList) throws RemoteException ;
	
	
	
	/***************************************************************************
	 * Ping - test that we can authenticate by getting a server response
	 * **************************************************************************/
	/**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * @ejb.permission 
	 * 	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
	 */
	public WSString ping()	throws RemoteException ;    
	
    	

//	/***************************************************************************
//	 * Xtentis JCA Connector support
//	 * **************************************************************************/
//
//	private transient ConnectionFactory cxFactory = null;
//	
//	/**
//	 * @ejb.interface-method view-type = "service-endpoint"
//	 * @ejb.permission 
//	 * 	role-name = "authenticated"
//	 * 	view-type = "service-endpoint"
//	 */
//	public WSConnectorInteractionResponse connectorInteraction(WSConnectorInteraction wsConnectorInteraction) throws RemoteException {
//		// This one does not call an EJB
//		
////		WSConnectorInteractionResponse response = new WSConnectorInteractionResponse();
////		Connection conx = null;
////		try {
////
////			String JNDIName = wsConnectorInteraction.getJNDIName();
////			conx = getConnection(JNDIName);
////			
////			Interaction interaction = conx.createInteraction();
////	    	InteractionSpecImpl interactionSpec = new InteractionSpecImpl();
////	    	
////			MappedRecord recordIn = new RecordFactoryImpl().createMappedRecord(RecordFactoryImpl.RECORD_IN);
////			
////			WSConnectorFunction cf = wsConnectorInteraction.getFunction();
////			if ((WSConnectorFunction.GET_STATUS).equals(cf)) {
////				interactionSpec.setFunctionName(InteractionSpecImpl.FUNCTION_GET_STATUS);
////			} else 	if ((WSConnectorFunction.PULL).equals(cf)) {
////				interactionSpec.setFunctionName(InteractionSpecImpl.FUNCTION_PULL);
////			} else 	if ((WSConnectorFunction.PUSH).equals(cf)) {
////				interactionSpec.setFunctionName(InteractionSpecImpl.FUNCTION_PUSH);
////			} else 	if ((WSConnectorFunction.START).equals(cf)) {
////				interactionSpec.setFunctionName(InteractionSpecImpl.FUNCTION_START);
////			} else 	if ((WSConnectorFunction.STOP).equals(cf)) {
////				interactionSpec.setFunctionName(InteractionSpecImpl.FUNCTION_STOP);
////			}
////			
////			recordIn.put(RecordFactoryImpl.PARAMS_HASHMAP_IN, getMapFromKeyValues(wsConnectorInteraction.getParameters()));
////						
////			MappedRecord recordOut = (MappedRecord)interaction.execute(interactionSpec, recordIn);
////
////			String code = (String)recordOut.get(RecordFactoryImpl.STATUS_CODE_OUT);
////			HashMap map = (HashMap)recordOut.get(RecordFactoryImpl.PARAMS_HASHMAP_OUT);
////			
////			if ("OK".equals(code)) {
////				response.setCode(WSConnectorResponseCode.OK);
////			} else if ("STOPPED".equals(code)) {
////				response.setCode(WSConnectorResponseCode.STOPPED);
////			} else if ("ERROR".equals(code)) {
////				response.setCode(WSConnectorResponseCode.ERROR);
////			} else {
////				throw new RemoteException("Unknown code: "+code);
////			}
////			response.setParameters(getKeyValuesFromMap(map));
////			
////		} catch (ResourceException e) {
////			throw new RemoteException(e.getLocalizedMessage());
////		} catch (Exception e) {
////			throw new RemoteException(e.getClass().getName()+": "+e.getLocalizedMessage());
////		} finally {
////			try {conx.close();} catch (Exception cx) {
////				org.apache.log4j.Category.getInstance(this.getClass()).debug("connectorInteraction() Connection close exception: "+cx.getLocalizedMessage());
////			}
////		}
////		return response;		
//		
//		
//	}
//
//    private Connection getConnection(String JNDIName) throws RemoteException {
//    	try {
//    		if (cxFactory == null)
//    			cxFactory = (ConnectionFactory)(new InitialContext()).lookup(JNDIName);
//	    	return cxFactory.getConnection();
//    	} catch (Exception e) {
//    		throw new RemoteException(e.getClass().getName()+": "+e.getLocalizedMessage());
//    	}
//    }
    

	/***************************************************************************
	 * Stored Procedure
	 * **************************************************************************/
	/**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * @ejb.permission 
	 * 	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
	 */
    public WSStoredProcedurePK deleteStoredProcedure(WSDeleteStoredProcedure wsStoredProcedureDelete) throws RemoteException ;
    
	/**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * @ejb.permission 
	 * 	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
	 */
	public WSStringArray executeStoredProcedure(WSExecuteStoredProcedure wsExecuteStoredProcedure) throws RemoteException ;

	/**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * @ejb.permission 
	 * 	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
	 */
	public WSStoredProcedure getStoredProcedure(WSGetStoredProcedure wsGetStoredProcedure) throws RemoteException ;
	
	/**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * @ejb.permission 
	 * 	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
	 */
	public WSBoolean existsStoredProcedure(WSExistsStoredProcedure wsExistsStoredProcedure) throws RemoteException ;

	/**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * @ejb.permission 
	 * 	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
	 */
	public WSStoredProcedurePKArray getStoredProcedurePKs(WSRegexStoredProcedure regex) throws RemoteException ;

	/**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * @ejb.permission 
	 * 	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
	 */
	public WSStoredProcedurePK putStoredProcedure(WSPutStoredProcedure wsStoredProcedure) throws RemoteException ;
    

	/***************************************************************************
	 * Menu
	 * **************************************************************************/
	/**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * @ejb.permission 
	 * 	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
	 */
    public WSMenuPK deleteMenu(WSDeleteMenu wsMenuDelete) throws RemoteException ;
    


	/**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * @ejb.permission 
	 * 	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
	 */
	public WSMenu getMenu(WSGetMenu wsGetMenu) throws RemoteException ;


	/**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * @ejb.permission 
	 * 	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
	 */
	public WSBoolean existsMenu(WSExistsMenu wsExistsMenu) throws RemoteException ;
	
	/**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * @ejb.permission 
	 * 	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
	 */
	public WSMenuPKArray getMenuPKs(WSGetMenuPKs regex) throws RemoteException ;

	/**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * @ejb.permission 
	 * 	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
	 */
	public WSMenuPK putMenu(WSPutMenu wsMenu) throws RemoteException ;


	/***************************************************************************
	 * BackgroundJob
	 * **************************************************************************/
	
	/**
	 * @ejb.interface-method view-type = "service-endpoint"
	 */    
	/*
	public WSBackgroundJobPK deleteBackgroundJob(WSBackgroundJobDelete wsjobpk)
			throws RemoteException {
		try {
			BackgroundJobPK cpk = XtentisUtil.getLocalHome().create().deleteBackgroundJob(new BackgroundJobPK(wsjobpk.getPk()));
			WSBackgroundJobPK wspk = new WSBackgroundJobPK();
			wspk.setPk(cpk.getId());
			return wspk;
		} catch (Exception e) {
			throw new EJBException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()));
		}
	}
	*/

	/**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * @ejb.permission 
	 * 	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
	 */	
   public WSBackgroundJob getBackgroundJob(WSGetBackgroundJob wsBackgroundJobGet)
    throws RemoteException ;
	    
	/**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * @ejb.permission 
	 * 	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
	 */    
    public WSBackgroundJobPKArray findBackgroundJobPKs(WSFindBackgroundJobPKs wsFindBackgroundJobPKs)
    throws RemoteException ;

    
	/**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * @ejb.permission 
	 * 	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
	 */    
	public WSBackgroundJobPK putBackgroundJob(WSPutBackgroundJob wsputjob)
			throws RemoteException ;

	
	
	/***************************************************************************
	 * Universe
	 * **************************************************************************/
    /**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * @ejb.permission 
	 * 	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
     */	
	public WSUniverse getCurrentUniverse(WSGetCurrentUniverse wsGetCurrentUniverse) throws RemoteException ;
	

	/***************************************************************************
	 * 
	 * 
	 *   D E P R E C A T E D    S T U F F
	 * 
	 * 
	 * **************************************************************************/
	
	
	
	/***************************************************************************
	 * Transformer DEPRECATED
	 * **************************************************************************/
	/**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * @ejb.permission 
	 * 	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
	 */
    public WSTransformerPK deleteTransformer(WSDeleteTransformer wsTransformerDelete) throws RemoteException ;
    


	/**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * @ejb.permission 
	 * 	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
	 */
	public WSTransformer getTransformer(WSGetTransformer wsGetTransformer) throws RemoteException ;
	
	/**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * @ejb.permission 
	 * 	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
	 */
	public WSBoolean existsTransformer(WSExistsTransformer wsExistsTransformer) throws RemoteException ;

	/**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * @ejb.permission 
	 * 	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
	 */
	public WSTransformerPKArray getTransformerPKs(WSGetTransformerPKs regex) throws RemoteException ;

	/**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * @ejb.permission 
	 * 	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
	 */
	public WSTransformerPK putTransformer(WSPutTransformer wsTransformer) throws RemoteException ;
    

	/**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * @ejb.permission 
	 * 	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
	 */
    public WSPipeline processBytesUsingTransformer(WSProcessBytesUsingTransformer wsProjectBytes) throws RemoteException ;

	/**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * @ejb.permission 
	 * 	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
	 */
	public WSPipeline processFileUsingTransformer(WSProcessFileUsingTransformer wsProcessFile) throws RemoteException ;
	
	/**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * @ejb.permission 
	 * 	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
	 */
	public WSBackgroundJobPK processBytesUsingTransformerAsBackgroundJob(WSProcessBytesUsingTransformerAsBackgroundJob wsProcessBytesUsingTransformerAsBackgroundJob) throws RemoteException ;
	
	/**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * @ejb.permission 
	 * 	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
	 */
	public WSBackgroundJobPK processFileUsingTransformerAsBackgroundJob(WSProcessFileUsingTransformerAsBackgroundJob wsProcessFileUsingTransformerAsBackgroundJob) throws RemoteException ;

//	private WSOutputDecisionTable POJO2WS(HashMap<String, String> decisionTable) {
//		if ((decisionTable == null) || decisionTable.size() == 0) return null;
//		ArrayList<WSProcessBytesUsingTransformerWsOutputDecisionTableDecisions> decisions = new ArrayList<WSProcessBytesUsingTransformerWsOutputDecisionTableDecisions>();
//		Set<String> outputs = decisionTable.keySet();
//		for (Iterator iter = outputs.iterator(); iter.hasNext(); ) {
//			String output = (String) iter.next();
//			String decision = decisionTable.get(output);
//			decisions.add(new WSProcessBytesUsingTransformerWsOutputDecisionTableDecisions(output,decision));
//		}
//		WSOutputDecisionTable table = new WSOutputDecisionTable(decisions.toArray(new WSProcessBytesUsingTransformerWsOutputDecisionTableDecisions[decisions.size()]));
//		return table;
//	}
	

	/**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * @ejb.permission 
	 * 	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
	 */
	public WSDroppedItemPKArray findAllDroppedItemsPKs(WSFindAllDroppedItemsPKs regex)
			throws RemoteException ;
	
	/**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * @ejb.permission 
	 * 	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
	 */
	public WSDroppedItem loadDroppedItem(WSLoadDroppedItem wsLoadDroppedItem)
			throws RemoteException ;

	/**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * @ejb.permission 
	 * 	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
	 */
	public WSItemPK recoverDroppedItem(WSRecoverDroppedItem wsRecoverDroppedItem)
			throws RemoteException ;

	/**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * @ejb.permission 
	 * 	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
	 */
	public WSDroppedItemPK removeDroppedItem(WSRemoveDroppedItem wsRemoveDroppedItem)
			throws RemoteException ;

	/***************************************************************************
	 * RoutingRule
	 * **************************************************************************/
	
	/**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * @ejb.permission 
	 * 	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
	 */	
   public WSRoutingRule getRoutingRule(WSGetRoutingRule wsRoutingRuleGet)
    throws RemoteException ;
  
	/**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * @ejb.permission 
	 * 	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
	 */	
	  public WSBoolean existsRoutingRule(WSExistsRoutingRule wsExistsRoutingRule)
	   throws RemoteException ;	    

		    
	/**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * @ejb.permission 
	 * 	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
	 */
    public WSRoutingRulePK deleteRoutingRule(WSDeleteRoutingRule wsDeleteRoutingRule)
    throws RemoteException ;
    
	/**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * @ejb.permission 
	 * 	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
	 */   
    public WSRoutingRulePK putRoutingRule(WSPutRoutingRule wsRoutingRule)
    throws RemoteException ;
    
    
	/**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * @ejb.permission 
	 * 	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
	 */
	public WSRoutingRulePKArray getRoutingRulePKs(WSGetRoutingRulePKs regex) throws RemoteException ;

 
	/***************************************************************************
	 * TransformerV2
	 * **************************************************************************/
	/**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * @ejb.permission 
	 * 	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
	 */
    public WSTransformerV2PK deleteTransformerV2(WSDeleteTransformerV2 wsTransformerV2Delete) throws RemoteException ;
    


	/**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * @ejb.permission 
	 * 	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
	 */
	public WSTransformerV2 getTransformerV2(WSGetTransformerV2 wsGetTransformerV2) throws RemoteException ;
	
	/**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * @ejb.permission 
	 * 	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
	 */
	public WSBoolean existsTransformerV2(WSExistsTransformerV2 wsExistsTransformerV2) throws RemoteException ;

	/**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * @ejb.permission 
	 * 	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
	 */
	public WSTransformerV2PKArray getTransformerV2PKs(WSGetTransformerV2PKs regex) throws RemoteException ;

	/**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * @ejb.permission 
	 * 	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
	 */
	public WSTransformerV2PK putTransformerV2(WSPutTransformerV2 wsTransformerV2) throws RemoteException ;
	
	
	/**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * @ejb.permission 
	 * 	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
	 */
	public WSTransformerContext executeTransformerV2(WSExecuteTransformerV2 wsExecuteTransformerV2) throws RemoteException ;

	/**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * @ejb.permission 
	 * 	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
	 */
	public WSBackgroundJobPK executeTransformerV2AsJob(WSExecuteTransformerV2AsJob wsExecuteTransformerV2AsJob) throws RemoteException ;
	
	
	/**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * @ejb.permission 
	 * 	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
	 */
	public WSTransformerContext extractThroughTransformerV2(WSExtractThroughTransformerV2 wsExtractThroughTransformerV2) throws RemoteException ;
	
	/***************************************************************************
	 * TRANSFORMER PLUGINS V2
	 * **************************************************************************/
	
	/**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * @ejb.permission 	 
	 *  	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
	 */
	public WSBoolean existsTransformerPluginV2(WSExistsTransformerPluginV2 wsExistsTransformerPlugin) throws RemoteException ;
	
	/**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * @ejb.permission 
	 * 	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
	 */
	public WSString getTransformerPluginV2Configuration(WSTransformerPluginV2GetConfiguration wsGetConfiguration) throws RemoteException ;

	/**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * @ejb.permission 
	 * 	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
	 */
	public WSString putTransformerPluginV2Configuration(WSTransformerPluginV2PutConfiguration wsPutConfiguration) throws RemoteException ;

	/**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * @ejb.permission 	 
	 *  	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
	 */
	public WSTransformerPluginV2Details getTransformerPluginV2Details(WSGetTransformerPluginV2Details wsGetTransformerPluginDetails) throws RemoteException ;


	/**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * @ejb.permission 	 
	 *  	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
	 */
	public WSTransformerPluginV2SList getTransformerPluginV2SList(WSGetTransformerPluginV2SList wsGetTransformerPluginsList) throws RemoteException ;
	

	/***************************************************************************
	 * Routing Order V2
	 * **************************************************************************/
	
	/**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * @ejb.permission 
	 * 	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
	 */
	public WSRoutingOrderV2 getRoutingOrderV2(WSGetRoutingOrderV2 wsGetRoutingOrder) throws RemoteException ;
	
	/**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * @ejb.permission 
	 * 	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
	 */
	public WSRoutingOrderV2 existsRoutingOrderV2(WSExistsRoutingOrderV2 wsExistsRoutingOrder) throws RemoteException ;
	
	/**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * @ejb.permission 
	 * 	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
	 */
	public WSRoutingOrderV2PK deleteRoutingOrderV2(WSDeleteRoutingOrderV2 wsDeleteRoutingOrder) throws RemoteException ;
	
	/**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * @ejb.permission 
	 * 	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
	 */
	public WSRoutingOrderV2PK executeRoutingOrderV2Asynchronously(WSExecuteRoutingOrderV2Asynchronously wsExecuteRoutingOrderAsynchronously) throws RemoteException ;
	
	/**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * @ejb.permission 
	 * 	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
	 */
	public WSString executeRoutingOrderV2Synchronously(WSExecuteRoutingOrderV2Synchronously wsExecuteRoutingOrderSynchronously) throws RemoteException ;
	

	/**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * @ejb.permission 
	 * 	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
	 */
	public WSRoutingOrderV2PKArray getRoutingOrderV2PKsByCriteria(WSGetRoutingOrderV2PKsByCriteria wsGetRoutingOrderV2PKsByCriteria) throws RemoteException ;
	
	/**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * @ejb.permission 
	 * 	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
	 */
	public WSRoutingOrderV2Array getRoutingOrderV2SByCriteria(WSGetRoutingOrderV2SByCriteria wsGetRoutingOrderV2SByCriteria) throws RemoteException ;
	

	
	

	/***************************************************************************
	 * Routing Engine V2
	 * **************************************************************************/
	
	/**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * @ejb.permission 
	 * 	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
	 */
	public WSRoutingRulePKArray routeItemV2(WSRouteItemV2 wsRouteItem) throws RemoteException ;
	
	/**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * @ejb.permission 
	 * 	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
	 */
	public WSRoutingEngineV2Status routingEngineV2Action(WSRoutingEngineV2Action wsRoutingEngineAction) throws RemoteException ;
		
}
