package org.talend.mdm.test.benchmark;

import java.util.ArrayList;
import java.util.List;

import org.talend.mdm.bulkload.client.BulkloadClient;
import org.talend.mdm.bulkload.client.BulkloadOptions;
import org.talend.mdm.commmon.util.time.TimeMeasure;
import org.talend.mdm.test.WebserviceTestCase;

import urn_com_amalto_xtentis_webservice.WSDataCluster;
import urn_com_amalto_xtentis_webservice.WSDataModelPK;
import urn_com_amalto_xtentis_webservice.WSPutDataCluster;

public class BulkImportTestCase extends WebserviceTestCase {
	
	WSDataModelPK dmpk=new WSDataModelPK("Order");
	String dcpkn="HugeBox";
	
	public void testBulkPutItemsNewMethod() {
		try {
		String url= "http://localhost:8080/datamanager/loadServlet";
		String username="admin";
		String password="talend";
		String cluster= dcpkn;
		String concept= "PurchaseOrder";
		String datamodel= "Order";
		boolean validate= false;
		boolean smartpk= false;
		
		defaultPort.putDataCluster(new WSPutDataCluster(new WSDataCluster(dcpkn, "", "")));
		
		BulkloadClient bulkloadClient=new BulkloadClient(url,username,password,null,cluster,concept,datamodel);
		bulkloadClient.setOptions(new BulkloadOptions(smartpk,validate,500));
		
		List<String> itemdatas= new ArrayList<String>();
		int total=500*1000;//500k
		int arraysize=500;
		
		TimeMeasure.begin("testBulkPutItemsNewMethod");
		long count=(total/arraysize);
		long itotal=0;
		for(int j=0; j<count; j++) {
			
			for(int i=0; i<arraysize; i++) {
				String xmlString=genData(itotal);
				itemdatas.add(xmlString);
				itotal++;
			}
			
			bulkloadClient.load(itemdatas);
			if((j+1)%2==0)TimeMeasure.step("testBulkPutItemsNewMethod","Step"+(j+1));
			itemdatas.clear();
		}
		TimeMeasure.end("testBulkPutItemsNewMethod");
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	private String genData(long itotal) {
		
		StringBuffer sb=new StringBuffer();
		sb.append("<PurchaseOrder>")
		  .append("<id>").append(itotal).append("</id>")
		  .append("<customer>Hannaford Bros. Co.</customer>")
		  .append("<product>Cranberries</product>")
		  .append("<price>0.0075984869072116545</price>")
		  .append("<amount>49955</amount>")
		  .append("<time>1267583681953</time>")
		  .append("<shipper>UK Mail, Express</shipper>")
		  .append("</PurchaseOrder>");

		return sb.toString();
	}

	
}
