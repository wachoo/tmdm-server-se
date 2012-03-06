package com.amalto.core.delegator;

import junit.framework.TestCase;

import com.amalto.core.util.OutputReport;
import com.amalto.core.webservice.WSDataClusterPK;
import com.amalto.core.webservice.WSDataModelPK;
import com.amalto.core.webservice.WSPutItem;
import com.amalto.core.webservice.WSPutItemWithReport;

@SuppressWarnings("nls")

/**
 * Simulate Test IXtentisWSDelegator
 */
public class SimulateXtentisWSDelegatorTest extends TestCase {
	 static String product = "Product";
	 static String itemxml = "<Product><Id>1</Id><Name>beforeSaving_Product</Name><Description>beforeSavingProduct junit test</Description><Price>1</Price></Product>";
	 static String resultUpdateReport = "<Update><UserName>administrator</UserName><Source>genericUI</Source><TimeInMillis>1330398525160</TimeInMillis><OperationType>UPDATE</OperationType><RevisionID>null</RevisionID><DataCluster>Product</DataCluster><DataModel>Product</DataModel><Concept>Product</Concept><Key>1</Key><Item>   <path>Name</path>   <oldValue>name1</oldValue>   <newValue>beforeSaving_Product</newValue></Item></Update>";
	 
	 //test beforeSaving variables
	 static boolean isOK;
	 static boolean newOutput;
	 
	 /**
	  * the Simulator class	  
	  */
	 static class SimulateDelegator extends IXtentisWSDelegator{
		 /*
		 * mock the super.getBeforeSavingOutputReport()
		 */
		@Override
		protected OutputReport getBeforeSavingOutputReport(String concept,
				String xml, String resultUpdateReport) throws Exception {
			String message="<report><message type=\"info\">change the value sucessfully!</message></report>";
			if(!isOK){
				message="<report><message type=\"error\">change the value failed!</message></report>";
			}
			String item=null;
			OutputReport report=new OutputReport(message,item);
			
			if(newOutput){
				item="<exchange><item>"+"<Product><Id>1</Id><Name>beforeSaving_Product</Name>  <Description>beforeSavingProduct junit test</Description><Price>2</Price></Product>"+
				"</item></exchange>";
				report.setItem(item);
			}
			return report;	
		}
	 	}
		static SimulateDelegator simulator=new SimulateDelegator();
		
		
	/**
	 * simulate test beforeSaving() 
	 * DOC Administrator Comment method "testBeforeSaving".
	 * @throws Exception
	 */
	public static void testBeforeSaving()throws Exception{
        WSPutItemWithReport itemReport = new WSPutItemWithReport();
        WSPutItem item = new WSPutItem();
        item.setWsDataClusterPK(new WSDataClusterPK(product));
        item.setWsDataModelPK(new WSDataModelPK(product));
       
        item.setXmlString(itemxml);
        item.setIsUpdate(false);

        itemReport.setInvokeBeforeSaving(true);
        itemReport.setSource("junittest");
        itemReport.setWsPutItem(item);

        //the old output_report, it won't change the item
        isOK=false; newOutput=false;
        boolean ret=simulator.beforeSaving(itemReport, product, itemxml, resultUpdateReport);
        // message is error
        assertNull((itemReport.getWsPutItem().getXmlString()));
        assertTrue(!ret);
        //message is info
        isOK=true; newOutput=false;
        ret=simulator.beforeSaving(itemReport, product, itemxml, resultUpdateReport);
        assertTrue(ret);
        
        // the new output_item, it will change the item
        //message is error
        isOK=false; newOutput=true;
        ret=simulator.beforeSaving(itemReport, product, itemxml, resultUpdateReport);
        assertNotNull((itemReport.getWsPutItem().getXmlString()));        
        assertTrue(!ret);
        
        //message is info
        isOK=true; newOutput=true;
        ret=simulator.beforeSaving(itemReport, product, itemxml, resultUpdateReport);
        assertTrue(ret);
	}
	


}
