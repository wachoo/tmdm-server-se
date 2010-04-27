package org.talend.mdm.test.benchmark;

import java.io.InputStream;
import java.rmi.RemoteException;

import org.talend.mdm.commmon.util.time.TimeMeasure;
import org.talend.mdm.test.WebserviceTestCase;

import urn_com_amalto_xtentis_webservice.WSDataCluster;
import urn_com_amalto_xtentis_webservice.WSDataClusterPK;
import urn_com_amalto_xtentis_webservice.WSDataModel;
import urn_com_amalto_xtentis_webservice.WSDataModelPK;
import urn_com_amalto_xtentis_webservice.WSDeleteItems;
import urn_com_amalto_xtentis_webservice.WSGetItems;
import urn_com_amalto_xtentis_webservice.WSInt;
import urn_com_amalto_xtentis_webservice.WSPutDataCluster;
import urn_com_amalto_xtentis_webservice.WSPutDataModel;
import urn_com_amalto_xtentis_webservice.WSPutItem;
import urn_com_amalto_xtentis_webservice.WSXPathsSearch;

public class BenchmarkTestCase extends WebserviceTestCase {
	WSDataModelPK dmpk=new WSDataModelPK("Order");
	static void init()throws Exception{
		//create data model
		InputStream in=ConcurrencyTestCase.class.getResourceAsStream("Order.xsd");
		byte[] buf=new byte[in.available()];
		in.read(buf);		
		String str=new String(buf);		
		defaultPort.putDataModel(new WSPutDataModel(new WSDataModel("Order", "", str)));
		in.close();		
	}
	static{
		try {
			init();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * @param args
	 */
	private static void main(String[] args) {
	

	}
//	public void testPutItemArray1k(){
//		PutItemArray(1000,10);
//		PutItemArray(1000,50);
//		PutItemArray(1000,100);
//		PutItemArray(1000,200);
//		PutItemArray(1000,500);
//	}
//	public void testPutItemArray100k() {
//		PutItemArray(100000,200);
//	}
//	public void testPutItemArray500k() {
//		PutItemArray(500000,200);
//	}
//	public void testPutItemArray1M() {
//		PutItemArray(1000000,200);
//	}
//	public void testPutItemArray3M() {
//		PutItemArray(3000000,200);
//	}
//	public void testPutItemArray10M() {
//		PutItemArray(10000000,200);
//	}
//	public void testPutItem100k() {
//		PutItem(100000);
//	}
//	public void testPutItem500k() {
//		PutItem(500000);
//	}	
//	public void testPutItem1M() {
//		PutItem(1000000);
//	}	
	private void testPutItem3M() {
		PutItem(3000000);
	}	
//	public void testPutItem10M() {
//		PutItem(10000000);
//	}	
	private void testSearch(long total){
		
		String tk=total/1000+"k";
		TimeMeasure.begin("getItems"+tk);
		//getItems
		this.getItems();
		TimeMeasure.end("getItems"+tk);
		TimeMeasure.begin("xPathSearch"+tk);
		//xPathSearch()
		this.xPathSearch();
		TimeMeasure.end("xPathSearch"+tk);
	}
	private void testDelete(long total){
		WSDeleteItems wsDeleteItems=new WSDeleteItems(); 
		wsDeleteItems.setConceptName("Country");
		wsDeleteItems.setWsDataClusterPK(new WSDataClusterPK("Order"));
		try {
			
			String tk=total/1000+"k";
			TimeMeasure.begin("deleteItems"+tk);
			WSInt wsint=defaultPort.deleteItems(wsDeleteItems);
			TimeMeasure.end("deleteItems"+tk);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}	
	public void testPerformance(){
		int total=500000;//1000,5000,10000
		PutItemArray(total,200);
		testSearch(total);
		testDelete(total);
	}
	private void xPathSearch(){
		WSXPathsSearch wsXPathsSearch=new WSXPathsSearch(); 
		wsXPathsSearch.setWsDataClusterPK(new WSDataClusterPK("Order"));
		wsXPathsSearch.setPivotPath("Country/isoCode");
		wsXPathsSearch.setViewablePaths(new String[]{"Country/isoCode"});
		try {
			String[] itemArray=defaultPort.xPathsSearch(wsXPathsSearch);
			System.out.println(itemArray.length);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private void getItems(){
		WSGetItems wsGetItems=new WSGetItems();
		wsGetItems.setConceptName("Country");
		wsGetItems.setWsDataClusterPK(new WSDataClusterPK("Order"));
		try {
			String[] itemArray=defaultPort.getItems(wsGetItems);
			System.out.println(itemArray.length);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}
	private void put(long total, int arraysize,String dcpk) {
		try {
			long count=(total/arraysize);
			long itotal=0;
			for(int j=0; j<count; j++) {
				WSPutItem[] items=new WSPutItem[arraysize];
				for(int i=0; i<arraysize; i++) {				
					String xmlString="<Country><isoCode>"+itotal+"</isoCode><label>label</label><Continent>Continent</Continent></Country>";
					items[i]=new WSPutItem(new WSDataClusterPK(dcpk), xmlString, dmpk, false);
					itotal++;
				}
				defaultPort.putItemArray(items);
			}
		}catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}			
		
	}
	private void PutItemArray(long total, int arraysize) {
		try {
			//create datacluster Order100k
			String tk=total/1000+"k";
			String dcpk="Order"+tk;
			defaultPort.putDataCluster(new WSPutDataCluster(new WSDataCluster(dcpk, "", "")));
						
			TimeMeasure.begin("PutItemArray"+tk+"arraysize"+arraysize);
			//total >20000
			if(total>20000) {
				int gap=(int)total/20000;
				long m=total%20000;
				for(int i=0; i<gap;i++) {
					put(20000,arraysize,dcpk);
					Thread.sleep(1000);
				}
				if(m>0)put(m,arraysize,dcpk);
			}else {
				put(total,arraysize,dcpk);			
			}
			TimeMeasure.end("PutItemArray"+tk+"arraysize"+arraysize);				
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void PutItem(long total) {
		try {
			//create datacluster Order100k
			String tk=total/1000+"k";
			String dcpk="Order"+tk;
			defaultPort.putDataCluster(new WSPutDataCluster(new WSDataCluster(dcpk, "", "")));
			TimeMeasure.begin("PutItem"+tk);
			long itotal=0;
			for(int i=0; i<total; i++) {				
				String xmlString="<Country><isoCode>"+itotal+"</isoCode><label>label</label><Continent>Continent</Continent></Country>";
				WSPutItem item=new WSPutItem(new WSDataClusterPK(dcpk), xmlString, dmpk, false);
				defaultPort.putItem(item);
				itotal++;
			}
			TimeMeasure.end("PutItem"+tk);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
