package org.talend.mdm.workflow.client;

import java.util.Map;

import org.talend.mdm.workflow.client.util.CommonUtil;
import org.talend.mdm.workflow.client.util.XmlFromSimpleXpathsBuilder;

import urn_com_amalto_xtentis_webservice.WSDataClusterPK;
import urn_com_amalto_xtentis_webservice.WSDataModelPK;
import urn_com_amalto_xtentis_webservice.WSItemPK;
import urn_com_amalto_xtentis_webservice.WSPutItem;
import urn_com_amalto_xtentis_webservice.WSPutItemWithReport;

public class TalendMDMItemUpdater extends TalendMDMAdapter{
	

	//item info
	private String dataCluster;//mdm data cluster name
	private String dataModel;//mdm data model name
	private Map<String,Object> itemUpdateMap;//mdm xpath-values pairs of an item, you want to update

	/**
	 * @param url
	 * @param username
	 * @param password
	 * @param universe
	 * 
	 */
	public TalendMDMItemUpdater(String url, String username, String password, String universe){
		super(url,username,password,universe);
	}
	
	public TalendMDMItemUpdater(String url, String username, String password, String universe,String dataCluster,String dataModel,Map<String,Object> itemUpdateMap) {
		super(url,username,password,universe);
		this.dataCluster=dataCluster;
		this.dataModel=dataModel;
		this.itemUpdateMap=itemUpdateMap;
	}
	
	public String getDataCluster() {
		return dataCluster;
	}

	public void setDataCluster(String dataCluster) {
		this.dataCluster = dataCluster;
	}

	public String getDataModel() {
		return dataModel;
	}

	public void setDataModel(String dataModel) {
		this.dataModel = dataModel;
	}

	public Map<String, Object> getItemUpdateMap() {
		return itemUpdateMap;
	}

	public void setItemUpdateMap(Map<String, Object> itemUpdateMap) {
		this.itemUpdateMap = itemUpdateMap;
	}

	/**
	 * @throws Exception
	 * 
	 *  update an item of MDM
	 */
	public void update() throws Exception{
		// Create WSPutItem instance
		WSDataClusterPK wsDataCluster = new WSDataClusterPK();
		wsDataCluster.setPk(dataCluster);
		
		WSDataModelPK wsDataModelPK = new WSDataModelPK();
		wsDataModelPK.setPk(dataModel);
		
		String inputSegment = XmlFromSimpleXpathsBuilder.parse2String(itemUpdateMap);
		
		WSPutItem wsPutItem = new WSPutItem(); 
		wsPutItem.setWsDataClusterPK(wsDataCluster);
		wsPutItem.setWsDataModelPK(wsDataModelPK);
		wsPutItem.setIsUpdate(true);// isUpdate true:update the item,false: create the item.
		wsPutItem.setXmlString(inputSegment);
		
		WSPutItemWithReport wsPutItemWithReport = new WSPutItemWithReport();
		wsPutItemWithReport.setWsPutItem(wsPutItem);
		wsPutItemWithReport.setSource("workflow");
		wsPutItemWithReport.setInvokeBeforeSaving(new Boolean(false));
		
		WSItemPK wsItemPK=port.putItemWithReport(wsPutItemWithReport);
		System.out.println(CommonUtil.filterNullString(dataCluster)+"."+CommonUtil.filterNullString(wsItemPK.getConceptName())+"."+CommonUtil.joinStrings(wsItemPK.getIds(),".")+" has been updated! ");

	}

}
