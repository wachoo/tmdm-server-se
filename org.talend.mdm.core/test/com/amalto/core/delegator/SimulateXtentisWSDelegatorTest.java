package com.amalto.core.delegator;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.amalto.core.util.OutputReport;
import com.amalto.core.util.Util;
import com.amalto.core.webservice.WSDataClusterPK;
import com.amalto.core.webservice.WSDataModelPK;
import com.amalto.core.webservice.WSPutItem;
import com.amalto.core.webservice.WSPutItemWithReport;
import com.sun.org.apache.xpath.internal.XPathAPI;

import junit.framework.TestCase;

@SuppressWarnings("nls")

/**
 * Simulate Test IXtentisWSDelegator
 */
public class SimulateXtentisWSDelegatorTest extends TestCase {
	 static String product = "Product";
	 static String itemxml = "<Product><Id>1</Id><Name>beforeSaving_Product</Name><Description>beforeSavingProduct junit test</Description><Price>1</Price></Product>";
	 static String updatereportxml = "<Update><UserName>administrator</UserName><Source>genericUI</Source><TimeInMillis>1330398525160</TimeInMillis><OperationType>UPDATE</OperationType><RevisionID>null</RevisionID><DataCluster>Product</DataCluster><DataModel>Product</DataModel><Concept>Product</Concept><Key>1</Key><Item>   <path>Name</path>   <oldValue>name1</oldValue>   <newValue>beforeSaving_Product</newValue></Item></Update>";
	 
		/**
		 * 
		 * DOC Administrator Comment method "getBeforeSavingOutputReport".
		 * @param newOutput 
		 * @return
		 */
		private static OutputReport getBeforeSavingOutputReport(boolean newOutput, boolean isOK){
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
		
		/**
		 * the simulate beforeSaving() according to IXtentisWSDelegator.beforeSaving()
		 * DOC Administrator Comment method "beforeSaving".
		 * @param wsPutItemWithReport
		 * @param concept
		 * @param xml
		 * @param resultUpdateReport
		 * @param newOutput
		 * @return
		 * @throws Exception
		 */
	    private static boolean beforeSaving(com.amalto.core.webservice.WSPutItemWithReport wsPutItemWithReport, String concept, String xml,
	            String resultUpdateReport, boolean newOutput, boolean isOk) throws Exception {
	        //Do we call Triggers&Before processes?
	        // by default xml string to null to ensure nothing is modified by process
	        wsPutItemWithReport.getWsPutItem().setXmlString(null);
	        if (wsPutItemWithReport.getInvokeBeforeSaving()) {
	            // invoke BeforeSaving process if it exists
	            OutputReport outputreport = getBeforeSavingOutputReport(newOutput, isOk);//Util.beforeSaving(concept, xml, resultUpdateReport);
	            if (outputreport != null) { // when a process was found
	                String message = outputreport.getMessage();
	                Document doc = Util.parse(message);
	                // handle output_report
	                String xpath = "//report/message"; //$NON-NLS-1$
	                Node errorNode = XPathAPI.selectSingleNode(doc, xpath);
	                String errorCode = null;
	                if (errorNode instanceof Element) {
	                    Element errorElement = (Element) errorNode;
	                    errorCode = errorElement.getAttribute("type"); //$NON-NLS-1$
	                    org.w3c.dom.Node child = errorElement.getFirstChild();
	                    if (child instanceof org.w3c.dom.Text) {
	                        message = child.getTextContent();
	                    }
	                }
	                // handle output_item
	                if(outputreport.getItem()!=null){
		                xpath = "//exchange/item"; //$NON-NLS-1$
		                doc = Util.parse(outputreport.getItem());
		                Node item = XPathAPI.selectSingleNode(doc, xpath);
		                if (item != null && item instanceof Element) {
		                    NodeList list = item.getChildNodes();
		                    Node node = null;
		                    for (int i = 0; i < list.getLength(); i++) {
		                        if (list.item(i) instanceof Element) {
		                            node = list.item(i);
		                            break;
		                        }
		                    }
		                    if (node != null) {
		                        String xmlString = Util.nodeToString(node);
		                        // set back the modified item by the process
		                        wsPutItemWithReport.getWsPutItem().setXmlString(xmlString);
		                    }
		                }
	                }
	                // Be Careful, this is not the same as IXtentisRMIPort
	                wsPutItemWithReport.setSource(message);
	                return "info".equals(errorCode); //$NON-NLS-1$
	            } else { //when no process was found 
	                return true;
	            }
	        } else {
	            // TMDM-2932 when getInvokeBeforeSaving() returns false, this method must return true.
	            // xml string to null to ensure nothing is modified by process
	            wsPutItemWithReport.getWsPutItem().setXmlString(null);
	            return true;
	        }
	    }	 
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
        boolean ret=beforeSaving(itemReport, product, itemxml,updatereportxml, false, false);
        // message is error
        assertNull((itemReport.getWsPutItem().getXmlString()));
        assertTrue(!ret);
        //message is info
        ret=beforeSaving(itemReport, product, itemxml,updatereportxml, false, true);
        assertTrue(ret);
        
        // the new output_item, it will change the item
        //message is error
        ret=beforeSaving(itemReport, product, itemxml,updatereportxml, true, false);
        assertNotNull((itemReport.getWsPutItem().getXmlString()));        
        assertTrue(!ret);
        
        //message is info
        ret=beforeSaving(itemReport, product, itemxml,updatereportxml, false, true);
        assertTrue(ret);
	}
	


}
