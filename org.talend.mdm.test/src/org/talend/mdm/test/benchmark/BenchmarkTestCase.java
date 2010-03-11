package org.talend.mdm.test.benchmark;

import java.rmi.RemoteException;

import org.talend.mdm.commmon.util.time.TimeMeasure;
import org.talend.mdm.test.WebserviceTestCase;

import urn_com_amalto_xtentis_webservice.WSDataCluster;
import urn_com_amalto_xtentis_webservice.WSDataClusterPK;
import urn_com_amalto_xtentis_webservice.WSDataModelPK;
import urn_com_amalto_xtentis_webservice.WSPutDataCluster;
import urn_com_amalto_xtentis_webservice.WSPutItem;

public class BenchmarkTestCase extends WebserviceTestCase {
	WSDataModelPK dmpk=new WSDataModelPK("Order");
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	public void testPutItemArray100k() {
		PutItemArray(100000,10);
		PutItemArray(100000,50);
		PutItemArray(100000,100);
		PutItemArray(100000,200);
		PutItemArray(100000,500);
	}
	public void testPutItemArray500k() {
		PutItemArray(500000,10);
		PutItemArray(500000,50);
		PutItemArray(500000,100);
		PutItemArray(500000,200);
		PutItemArray(500000,500);
	}
	public void testPutItemArray1M() {
		PutItemArray(1000000,10);
		PutItemArray(1000000,50);
		PutItemArray(1000000,100);
		PutItemArray(1000000,200);
		PutItemArray(1000000,500);
	}
	public void testPutItemArray3M() {
		PutItemArray(3000000,10);
		PutItemArray(3000000,50);
		PutItemArray(3000000,100);
		PutItemArray(3000000,200);
		PutItemArray(3000000,500);
	}
	public void testPutItemArray10M() {
		PutItemArray(10000000,10);
		PutItemArray(10000000,50);
		PutItemArray(10000000,100);
		PutItemArray(10000000,200);
		PutItemArray(10000000,500);
	}
	public void testPutItem100k() {
		PutItem(100000);
	}
	public void testPutItem500k() {
		PutItem(500000);
	}	
	public void testPutItem1M() {
		PutItem(1000000);
	}	
	public void testPutItem3M() {
		PutItem(3000000);
	}	
	public void testPutItem10M() {
		PutItem(10000000);
	}	
	private void PutItemArray(long total, int arraysize) {
		try {
			//create datacluster Order100k
			String tk=total/1000+"k";
			String dcpk="Order"+tk;
			defaultPort.putDataCluster(new WSPutDataCluster(new WSDataCluster(dcpk, "", "")));
			TimeMeasure.begin("PutItemArray"+tk+"arraysize"+arraysize);

			long count=(total/arraysize);
			long itotal=0;
			for(int j=0; j<count; j++) {
				WSPutItem[] items=new WSPutItem[200];
				for(int i=0; i<200; i++) {				
					String xmlString="<Country><isoCode>"+itotal+"</isoCode><label>label</label><Continent>Continent</Continent></Country>";
					items[i]=new WSPutItem(new WSDataClusterPK(dcpk), xmlString, dmpk, false);
					itotal++;
				}
				defaultPort.putItemArray(items);
			}
			
			TimeMeasure.end("PutItemArray"+tk+"arraysize"+arraysize);
		} catch (RemoteException e) {
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
