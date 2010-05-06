package org.talend.mdm.test.benchmark;

import java.io.InputStream;
import java.rmi.RemoteException;

import org.talend.mdm.commmon.util.time.TimeMeasure;
import org.talend.mdm.test.WebserviceTestCase;

import urn_com_amalto_xtentis_webservice.WSDataCluster;
import urn_com_amalto_xtentis_webservice.WSDataClusterPK;
import urn_com_amalto_xtentis_webservice.WSDataModel;
import urn_com_amalto_xtentis_webservice.WSDataModelPK;
import urn_com_amalto_xtentis_webservice.WSDeleteItem;
import urn_com_amalto_xtentis_webservice.WSDeleteItems;
import urn_com_amalto_xtentis_webservice.WSGetItem;
import urn_com_amalto_xtentis_webservice.WSGetItems;
import urn_com_amalto_xtentis_webservice.WSInt;
import urn_com_amalto_xtentis_webservice.WSItemPK;
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
//	public void testPutItem10M() {
//		PutItem(10000000);
//	}	
	private void testSearch(long total){
		
		String tk=total/1000+"k";
		TimeMeasure.begin("getItems"+tk);
		//getItems
		this.getItems(total);
		TimeMeasure.end("getItems"+tk);
		TimeMeasure.begin("xPathSearch"+tk);
		//xPathSearch()
		this.xPathSearch(total);
		TimeMeasure.end("xPathSearch"+tk);
	}
	private void testDelete(long total){
		WSDeleteItems wsDeleteItems=new WSDeleteItems(); 
		wsDeleteItems.setConceptName("Country");
		String tk=total/1000+"k";
		String dcpk="Order"+tk;	
		wsDeleteItems.setWsDataClusterPK(new WSDataClusterPK(dcpk));		
		try {
			
			TimeMeasure.begin("deleteItems"+tk);
			WSInt wsint=defaultPort.deleteItems(wsDeleteItems);
			TimeMeasure.end("deleteItems"+tk);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}	
	public void testPerformance(){
		int total=1000;//1000,5000,10000
		PutItemArray(total,200);
		testSearch(total);
		testDelete(total);
	}
	
	private void testPutOne()throws Exception{
		
		String xmlString = "<Country><isoCode>testOne</isoCode><label>testOne</label><Continent>testOne</Continent></Country>";
		WSPutItem item = new WSPutItem(new WSDataClusterPK("Order"), xmlString,
				dmpk, false);
		TimeMeasure.begin("testPutOneItem");
		defaultPort.putItem(item);
		TimeMeasure.end("testPutOneItem");
	}
	private void testGetOne()throws Exception{
			WSGetItem wsGetItem=new WSGetItem();
			WSItemPK wsItemPK=new WSItemPK();
			wsItemPK.setConceptName("Country");
			wsItemPK.setWsDataClusterPK(new WSDataClusterPK("Order"));
			String[] ids={"testOne"};
			wsItemPK.setIds(ids);
			wsGetItem.setWsItemPK(wsItemPK);
			TimeMeasure.begin("testGetOneItem");
			defaultPort.getItem(wsGetItem);
			TimeMeasure.end("testGetOneItem");
	
	}
	private void testDeleteOne()throws Exception{
			WSDeleteItem wsDeleteItem=new WSDeleteItem();
			WSItemPK wsItemPK=new WSItemPK();
			wsItemPK.setConceptName("Country");
			wsItemPK.setWsDataClusterPK(new WSDataClusterPK("Order"));
			String[] ids={"testOne"};
			wsItemPK.setIds(ids);
			wsDeleteItem.setWsItemPK(wsItemPK);
			TimeMeasure.begin("testDeleteOneItem");
			defaultPort.deleteItem(wsDeleteItem);
			TimeMeasure.end("testDeleteOneItem");
		
	}
	public void testOne(){
		try {
			testPutOne();
			testGetOne();
			testDeleteOne();
		} catch (Exception e) {
			
			e.printStackTrace();
		}
	}
	public void test3And10And20Element1000AndTestOne(){
		testOne();
		System.out.println("------------------------------------------");
		test3Elements1000();
		System.out.println("------------------------------------------");
		test10Elements1000();
		System.out.println("------------------------------------------");
		test20Elements1000();
	}
	private void test3Elements1000(){
		try {
			//Put
			TimeMeasure.begin("testPut3Elements1000");
			int itotal=1;
			for(int i=0; i<5; i++) {
				WSPutItem[] items=new WSPutItem[200];
				for(int j=0; j<200; j++){
					String xmlString="<Test3Elements>"+
										"<subelement0>Test3Elements"+itotal+"</subelement0>"+
										"<subelement1>Test3Elements"+itotal+"</subelement1>"+
										"<subelement2>Test3Elements"+itotal+"</subelement2>"+
									  "</Test3Elements>";
						
					WSPutItem item=new WSPutItem(new WSDataClusterPK("Order"), xmlString, new WSDataModelPK("Order"), false);
					items[j]=item;
					itotal++;
				}
				defaultPort.putItemArray(items);
			}
			TimeMeasure.end("testPut3Elements1000");
			
			//Search
			WSGetItems wsGetItems=new WSGetItems();
			wsGetItems.setConceptName("Test3Elements");
			wsGetItems.setWsDataClusterPK(new WSDataClusterPK("Order"));
			wsGetItems.setSkip(0);
			wsGetItems.setMaxItems(20);
			TimeMeasure.begin("testGet3Elements1000");
			defaultPort.getItems(wsGetItems);
			TimeMeasure.end("testGet3Elements1000");
			
			//delete
			WSDeleteItems wsDeleteItems=new WSDeleteItems(); 
			wsDeleteItems.setConceptName("Test3Elements");
			wsDeleteItems.setWsDataClusterPK(new WSDataClusterPK("Order"));	
			TimeMeasure.begin("testDelete3Elements1000");
			defaultPort.deleteItems(wsDeleteItems);
			TimeMeasure.end("testDelete3Elements1000");
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}
	private void test10Elements1000(){
		try {
			//Put
			TimeMeasure.begin("testPut10Elements1000");
			int itotal=1;
			for(int i=0; i<5; i++) {
				WSPutItem[] items=new WSPutItem[200];
				for(int j=0; j<200; j++){		
					String xmlString="<Test10Elements>"+
										"<subelement0>Test10Elements"+itotal+"</subelement0>"+
										"<subelement1>Test10Elements"+itotal+"</subelement1>"+
										"<subelement2>Test10Elements"+itotal+"</subelement2>"+
										"<subelement3>Test10Elements"+itotal+"</subelement3>"+
										"<subelement4>Test10Elements"+itotal+"</subelement4>"+
										"<subelement5>Test10Elements"+itotal+"</subelement5>"+
										"<subelement6>Test10Elements"+itotal+"</subelement6>"+
										"<subelement7>Test10Elements"+itotal+"</subelement7>"+
										"<subelement8>Test10Elements"+itotal+"</subelement8>"+
										"<subelement9>Test10Elements"+itotal+"</subelement9>"+
									  "</Test10Elements>";
						
					WSPutItem item=new WSPutItem(new WSDataClusterPK("Order"), xmlString, new WSDataModelPK("Order"), false);
					items[j]=item;
					itotal++;
				}
				defaultPort.putItemArray(items);
			}
			TimeMeasure.end("testPut10Elements1000");
			
			//Search
			WSGetItems wsGetItems=new WSGetItems();
			wsGetItems.setConceptName("Test10Elements");
			wsGetItems.setWsDataClusterPK(new WSDataClusterPK("Order"));
			wsGetItems.setSkip(0);
			wsGetItems.setMaxItems(20);
			TimeMeasure.begin("testGet10Elements1000");
			defaultPort.getItems(wsGetItems);
			TimeMeasure.end("testGet10Elements1000");
			
			//delete
			WSDeleteItems wsDeleteItems=new WSDeleteItems(); 
			wsDeleteItems.setConceptName("Test10Elements");
			wsDeleteItems.setWsDataClusterPK(new WSDataClusterPK("Order"));	
			TimeMeasure.begin("testDelete10Elements1000");
			defaultPort.deleteItems(wsDeleteItems);
			TimeMeasure.end("testDelete10Elements1000");
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}
	private void test20Elements1000(){
		try {
			//Put
			TimeMeasure.begin("testPut20Elements1000");
			int itotal=1;
			for(int i=0; i<5; i++) {
				WSPutItem[] items=new WSPutItem[200];
				for(int j=0; j<200; j++){		
					String xmlString="<Test20Elements>"+
										"<subelement0>Test20Elements"+itotal+"</subelement0>"+
										"<subelement1>Test20Elements"+itotal+"</subelement1>"+
										"<subelement2>Test20Elements"+itotal+"</subelement2>"+
										"<subelement3>Test20Elements"+itotal+"</subelement3>"+
										"<subelement4>Test20Elements"+itotal+"</subelement4>"+
										"<subelement5>Test20Elements"+itotal+"</subelement5>"+
										"<subelement6>Test20Elements"+itotal+"</subelement6>"+
										"<subelement7>Test20Elements"+itotal+"</subelement7>"+
										"<subelement8>Test20Elements"+itotal+"</subelement8>"+
										"<subelement9>Test20Elements"+itotal+"</subelement9>"+
										"<subelement10>Test20Elements"+itotal+"</subelement10>"+
										"<subelement11>Test20Elements"+itotal+"</subelement11>"+
										"<subelement12>Test20Elements"+itotal+"</subelement12>"+
										"<subelement13>Test20Elements"+itotal+"</subelement13>"+
										"<subelement14>Test20Elements"+itotal+"</subelement14>"+
										"<subelement15>Test20Elements"+itotal+"</subelement15>"+
										"<subelement16>Test20Elements"+itotal+"</subelement16>"+
										"<subelement17>Test20Elements"+itotal+"</subelement17>"+
										"<subelement18>Test20Elements"+itotal+"</subelement18>"+
										"<subelement19>Test20Elements"+itotal+"</subelement19>"+
									  "</Test20Elements>";
						
					WSPutItem item=new WSPutItem(new WSDataClusterPK("Order"), xmlString, new WSDataModelPK("Order"), false);
					items[j]=item;
					itotal++;
				}
				defaultPort.putItemArray(items);
			}
			TimeMeasure.end("testPut20Elements1000");
			
			//Search
			WSGetItems wsGetItems=new WSGetItems();
			wsGetItems.setConceptName("Test20Elements");
			wsGetItems.setWsDataClusterPK(new WSDataClusterPK("Order"));
			wsGetItems.setSkip(0);
			wsGetItems.setMaxItems(20);
			TimeMeasure.begin("testGet20Elements1000");
			defaultPort.getItems(wsGetItems);
			TimeMeasure.end("testGet20Elements1000");
		
			//delete
			WSDeleteItems wsDeleteItems=new WSDeleteItems(); 
			wsDeleteItems.setConceptName("Test20Elements");
			wsDeleteItems.setWsDataClusterPK(new WSDataClusterPK("Order"));	
			TimeMeasure.begin("testDelete20Elements1000");
			defaultPort.deleteItems(wsDeleteItems);
			TimeMeasure.end("testDelete20Elements1000");
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}
	private void xPathSearch(long total){
		WSXPathsSearch wsXPathsSearch=new WSXPathsSearch();
		String tk=total/1000+"k";
		String dcpk="Order"+tk;		
		wsXPathsSearch.setWsDataClusterPK(new WSDataClusterPK(dcpk));
		wsXPathsSearch.setPivotPath("Country/isoCode");
		wsXPathsSearch.setViewablePaths(new String[]{"Country/isoCode"});
		wsXPathsSearch.setSkip(0);
		wsXPathsSearch.setMaxItems(20);
		try {
			String[] itemArray=defaultPort.xPathsSearch(wsXPathsSearch);
			System.out.println(itemArray.length);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private void getItems(long total ){
		WSGetItems wsGetItems=new WSGetItems();
		wsGetItems.setConceptName("Country");
		String tk=total/1000+"k";
		String dcpk="Order"+tk;
		wsGetItems.setWsDataClusterPK(new WSDataClusterPK(dcpk));
		wsGetItems.setSkip(0);
		wsGetItems.setMaxItems(20);
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
			put(total,arraysize,dcpk);						
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
