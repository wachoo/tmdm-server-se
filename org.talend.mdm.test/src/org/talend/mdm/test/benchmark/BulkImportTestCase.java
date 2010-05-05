package org.talend.mdm.test.benchmark;

import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.talend.mdm.bulkload.client.BulkloadClient;
import org.talend.mdm.bulkload.client.BulkloadOptions;
import org.talend.mdm.commmon.util.time.TimeMeasure;
import org.talend.mdm.test.WebserviceTestCase;

import urn_com_amalto_xtentis_webservice.WSDataCluster;
import urn_com_amalto_xtentis_webservice.WSDataModel;
import urn_com_amalto_xtentis_webservice.WSDataModelPK;
import urn_com_amalto_xtentis_webservice.WSPutDataCluster;
import urn_com_amalto_xtentis_webservice.WSPutDataModel;

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
		
		//create data model
		InputStream in=ConcurrencyTestCase.class.getResourceAsStream("Order.xsd");
		byte[] buf=new byte[in.available()];
		in.read(buf);		
		String str=new String(buf);		
		defaultPort.putDataModel(new WSPutDataModel(new WSDataModel("Order", "", str)));
		in.close();	
		
		// create data container
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

	//2010-05-05T00:00:00
	final static SimpleDateFormat xsdDateTime = new SimpleDateFormat("yyyy.MM.dd'T'HH:mm:ss");
	
	private String genData(long itotal) {
		
		StringBuffer sb=new StringBuffer();
		
		String now = xsdDateTime.format(new Date());

		sb.append("<PurchaseOrder>")
			.append("<PurchaseOrderId>").append(itotal).append("</PurchaseOrderId>")
			.append("<Customer>Hannaford Bros. Co.</Customer>")
			.append("<Date>").append(now).append("</Date>")
			.append("<ListItems>")
			.append("<POItem>poitem1</POItem>")
			.append("<POItem>poitem2</POItem>")
			.append("<POItem>poitem3</POItem>")
			.append("<POItem>poitem4</POItem>")
			.append("</ListItems>")
			.append("<Shipper>UK Mail, Express</Shipper>")
			.append("</PurchaseOrder>");

		return sb.toString();
	}

	
}
