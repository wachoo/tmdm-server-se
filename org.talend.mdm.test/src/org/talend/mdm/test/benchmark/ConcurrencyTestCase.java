package org.talend.mdm.test.benchmark;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.rmi.RemoteException;

import org.talend.mdm.test.util.Util;

import urn_com_amalto_xtentis_webservice.WSDataCluster;
import urn_com_amalto_xtentis_webservice.WSDataClusterPK;
import urn_com_amalto_xtentis_webservice.WSDataModel;
import urn_com_amalto_xtentis_webservice.WSDataModelPK;
import urn_com_amalto_xtentis_webservice.WSDeleteItem;
import urn_com_amalto_xtentis_webservice.WSItemPK;
import urn_com_amalto_xtentis_webservice.WSPutDataCluster;
import urn_com_amalto_xtentis_webservice.WSPutDataModel;
import urn_com_amalto_xtentis_webservice.WSPutItem;
import urn_com_amalto_xtentis_webservice.XtentisPort;

public class ConcurrencyTestCase {
	static WSDataModelPK dmpk=new WSDataModelPK("Order");
	static WSDataClusterPK dcpk=new WSDataClusterPK("Order_concurency");
	protected static final String URL = "http://localhost:8080/talend/TalendPort";
	protected static final String User = "admin";
	protected static final String PASSWORD = "talend";
	protected static final String Universe = "";
	protected static final String SCHEMA = "<?xml version='1.0' encoding='UTF-8'?><xsd:schema xmlns:xsd='http://www.w3.org/2001/XMLSchema' attributeFormDefault='unqualified' blockDefault='' elementFormDefault='unqualified' finalDefault=''><xsd:element abstract='false' name='Conf' nillable='false'><xsd:complexType mixed='false'><xsd:sequence maxOccurs='1' minOccurs='1'><xsd:element maxOccurs='1' minOccurs='1' name='id' nillable='false' type='xsd:string'/><xsd:element maxOccurs='1' minOccurs='1' name='Config' nillable='false' type='xsd:string'/></xsd:sequence></xsd:complexType><xsd:unique name='Conf'><xsd:selector xpath='.'/><xsd:field xpath='id'/></xsd:unique></xsd:element></xsd:schema>";
	protected static final String BUSINESSCONCEPTSCHEMA = "<xsd:import namespace='http://www.w3.org/2001/XMLSchema'/><xsd:element abstract='false' name='Student' nillable='false' type='StudentType'><xsd:unique name='Student'><xsd:selector xpath='.'/><xsd:field xpath='id'/></xsd:unique></xsd:element><xsd:complexType abstract='false' mixed='false' name='StudentType'><xsd:sequence maxOccurs='1' minOccurs='1'><xsd:element maxOccurs='1' minOccurs='1' name='id' nillable='false' type='xsd:string'/><xsd:element maxOccurs='1' minOccurs='1' name='name' nillable='false' type='xsd:string'/><xsd:element maxOccurs='1' minOccurs='1' name='age' nillable='false' type='xsd:string'/></xsd:sequence></xsd:complexType>";
	protected static XtentisPort defaultPort = null;
	static {
		try {
			defaultPort = (XtentisPort) Util.getPort(URL, Universe, User,
					PASSWORD);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}	
	static class Concurrency extends Thread{
		boolean sameRecord;
		int count;
		Concurrency(boolean sameRecord,int count){
			this.sameRecord=sameRecord;
			this.count=count;
		}
		public void run() {
			if(sameRecord){
				//add Item
				String xmlString="<Country><isoCode>zh</isoCode><label>label</label><Continent>Continent</Continent></Country>";
				WSPutItem item=new WSPutItem(dcpk, xmlString, dmpk, false);
				try {
					defaultPort.putItem(item);
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				//update item
				xmlString="<Country><isoCode>zh</isoCode><label>label_update</label><Continent>Continent_update</Continent></Country>";
				item=new WSPutItem(dcpk, xmlString, dmpk, true);
				try {
					defaultPort.putItem(item);
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}			
				//delete item
				try {
					defaultPort.deleteItem(new WSDeleteItem(new WSItemPK(dcpk,"Country",new String[]{"zh"})));
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}else{
				//add Item
				String xmlString="<Country><isoCode>zh"+count+"</isoCode><label>label</label><Continent>Continent</Continent></Country>";
				WSPutItem item=new WSPutItem(dcpk, xmlString, dmpk, false);
				try {
					defaultPort.putItem(item);
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				//update item
				xmlString="<Country><isoCode>zh"+count+"</isoCode><label>label_update</label><Continent>Continent_update</Continent></Country>";
				item=new WSPutItem(dcpk, xmlString, dmpk, true);
				try {
					defaultPort.putItem(item);
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}			
				//delete item
				try {
					defaultPort.deleteItem(new WSDeleteItem(new WSItemPK(dcpk,"Country",new String[]{"zh"+count})));
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}				
			}
		}
	}
	static void init()throws Exception{
		
		//create datacluster
		try {
			defaultPort.putDataCluster(new WSPutDataCluster(new WSDataCluster(dcpk.getPk(), "", "")));
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//create data model
		InputStream in=ConcurrencyTestCase.class.getResourceAsStream("Order.xsd");
		byte[] buf=new byte[in.available()];
		in.read(buf);		
		String str=new String(buf);		
		defaultPort.putDataModel(new WSPutDataModel(new WSDataModel("Order", "", str)));
		in.close();
	}
	public static void main(String[] args) {
		try {
			init();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//same record
		for(int i=0; i<10; i++){
			Concurrency t=new Concurrency(true,i);
			t.start();
		}
		//different record
		for(int i=0; i<10; i++){
			Concurrency t=new Concurrency(false,i);
			t.start();
		}
	}
}
