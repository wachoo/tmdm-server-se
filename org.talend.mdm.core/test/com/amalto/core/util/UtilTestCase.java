// ============================================================================
//
// Copyright (C) 2006-2011 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package com.amalto.core.util;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.ParserConfigurationException;

import junit.framework.TestCase;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.amalto.core.ejb.DroppedItemPOJO;
import com.amalto.core.ejb.ItemPOJO;
import com.amalto.core.objects.datacluster.ejb.DataClusterPOJOPK;
import com.amalto.core.objects.transformers.v2.ejb.TransformerV2POJOPK;
import com.amalto.core.objects.transformers.v2.util.TransformerContext;
import com.sun.org.apache.xpath.internal.XPathAPI;

/**
 * DOC achen  class global comment. Detailled comment
 */
@SuppressWarnings("nls")
public class UtilTestCase extends TestCase {

    public void testDefaultValidate() throws IOException, ParserConfigurationException, SAXException {
        // missing mandontory field cvc-complex-type.2.4.b
        InputStream in = UtilTestCase.class.getResourceAsStream("Agency_ME02.xml");
        String xml = getStringFromInputStream(in);
        Element element = Util.parse(xml).getDocumentElement();
        InputStream inxsd = UtilTestCase.class.getResourceAsStream("DStar.xsd");
        String schema = getStringFromInputStream(inxsd);

        try {
            Util.defaultValidate(element, schema);
        } catch (Exception e) {
            String str = e.getLocalizedMessage();
            assertTrue(str
                    .contains("cvc-complex-type.2.4.b: The content of element 'Agency' is not complete. One of '{Id}' is expected"));
        }
        // invalid content cvc-complex-type.2.4.

        String invalidXml = "<Agency>aa</Agency>";
        element = Util.parse(invalidXml).getDocumentElement();
        try {
            Util.defaultValidate(element, schema);
        } catch (Exception e) {
            String str = e.getLocalizedMessage();
            assertTrue(str.contains("cvc-complex-type.2.4"));
        }

        // correct xmlstring
        String xmlString = "<Agency xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" + "<Name>Portland</Name>"
                + "<City>Portland</City>" + "<State>ME</State>" + "<Zip>04102</Zip>" + "<Region>EAST</Region>" + "<Id>ME03</Id>"
                + "</Agency>";
        element = Util.parse(xmlString).getDocumentElement();
        try {
            Util.defaultValidate(element, schema);
        } catch (Exception e) {
            throw new SAXException(e);
        }

         // non mandontory field contains empty mandontory child fields is OK
        String xml1 = "<Product><Picture>htt:aa</Picture><Id>id1</Id><Name>name2</Name><Description>des1</Description>"
                + "<Features><Sizes><Size/></Sizes><Colors><Color/></Colors></Features>"
                + "<Availability>false</Availability><Price>0.0</Price><Family></Family><OnlineStore>gg2@d</OnlineStore></Product>";
        element = Util.parse(xml1).getDocumentElement();
        try {
            Util.defaultValidate(element, schema);
        } catch (Exception e) {
            throw new SAXException(e);
        }

    }

    private static String getStringFromInputStream(InputStream in) throws IOException {
        int total = in.available();
        byte[] buf = new byte[total];
        in.read(buf);
        return new String(buf);
    }
    
    
    
    String[] ids=new String[]{"1"};
    String concept="Product";
    String projection="<Product xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"><Id>1</Id><Name>name1</Name><Description>desc1</Description><Price>1</Price></Product>";
    DataClusterPOJOPK dcpk=new DataClusterPOJOPK("Product");

    /**
     * Simulate test beforeDeleting
     * DOC Administrator Comment method "testBeforeDeleting".
     * @throws Exception
     */
    public void testBeforeDeleting()throws Exception{
    	//delete item in recyclebin
    	ItemPOJO pojo=null;
    	String ret=beforeDeleting(pojo);
    	assertTrue(ret!=null && ret.contains("[EN:ok]"));
    	
    	//delete item in data container
    	pojo=new ItemPOJO();
    	pojo.setDataClusterPK(dcpk);
    	pojo.setItemIds(ids);
    	pojo.setConceptName(concept);
    	pojo.setProjectionAsString(projection);
    	
    	ret=beforeDeleting(pojo);
    	
    	assertTrue(ret!=null && ret.contains("[EN:ok]"));
    }
    
    /**
     * the simulate droppedItem
     * DOC Administrator Comment method "getDroppedItem".
     * @return
     */
    private DroppedItemPOJO getDroppedItem(){
    	DroppedItemPOJO pojo=new DroppedItemPOJO();
    	pojo.setProjection("<ii><c>Product</c><n>Product</n><dmn>Product</dmn><i>1</i><t>1330671403828</t><p>"+
    			projection +
    			"</p></ii>");
    	
    	return pojo;
    }
    
    /**
     * the simulate beforeDeleting() according to the Util.beforeDeleting()    
     * DOC Administrator Comment method "beforeDeleting".
     * @param pj
     * @return
     * @throws Exception
     */
    private  String beforeDeleting(ItemPOJO pj) throws Exception {
        // check before deleting transformer
        boolean isBeforeDeletingTransformerExist = true;
        

        if (isBeforeDeletingTransformerExist) {
            try {
                // call before deleting transformer
                // load the item
                //ItemPOJOPK itempk = new ItemPOJOPK(new DataClusterPOJOPK(clusterName), concept, ids);
                ItemPOJO pojo= pj;//ItemPOJO.load(itempk);
                String xml=null;
                if(pojo==null){//load from recyclebin
                	//DroppedItemPOJOPK dpitempk=new DroppedItemPOJOPK(null,itempk,"/");//$NON-NLS-1$ 
                	DroppedItemPOJO dpPojo=getDroppedItem(); //Util.getDroppedItemCtrlLocal().loadDroppedItem(dpitempk);
                	if(dpPojo!=null){
                		xml=dpPojo.getProjection();             		
                		Document doc = Util.parse(xml);
    	                Node item = XPathAPI.selectSingleNode(doc, "//ii/p"); //$NON-NLS-1$ 
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
    	                        xml = Util.nodeToString(node);
    	                    }
    	                }
                	}
                }else{
                	xml=pojo.getProjectionAsString();
                }
                String resultUpdateReport = null;//Util.createUpdateReport(ids, concept, "PHYSICAL_DELETE", null,                         "", dcpk.getUniqueId()); //$NON-NLS-1$
                String exchangeData = Util.mergeExchangeData(xml, resultUpdateReport);
                final String RUNNING = "XtentisWSBean.executeTransformerV2.beforeDeleting.running";
                TransformerContext context = new TransformerContext(new TransformerV2POJOPK("beforeDeleting_Product"));
                context.put(RUNNING, Boolean.TRUE);
                String outputErrorMessage="<message type=\"info\">[EN:ok]</message>";
                if(xml==null){
                	outputErrorMessage=null;
                }
                // handle error message
                if (outputErrorMessage != null && outputErrorMessage.length() > 0) {
                    return outputErrorMessage;
                } else {
                    return "<report><message type=\"error\"/></report> "; //$NON-NLS-1$
                }
            } catch (Exception e) {
                Logger.getLogger(Util.class).error(e);
                throw e;
            }
        }
        // TODO Scan the entries - in priority, taka the content of the specific
        // entry
        return null;
    }
    
    public void testSetNullNode() throws Exception{
    	InputStream in = UtilTestCase.class.getResourceAsStream("test1.xml");
        String xml = getStringFromInputStream(in);
        Element element = Util.parse(xml).getDocumentElement();
        Util.setNullNode(element);
        String xmlStr = Util.nodeToString(element);
        assertFalse(xmlStr.contains("NullValue"));
        assertFalse(xmlStr.contains("optionalElement"));
        assertFalse(xmlStr.contains("EntireNullNode"));
        assertFalse(xmlStr.contains("NullSubNode1"));
        assertFalse(xmlStr.contains("NullSubNode2"));
        assertFalse(xmlStr.contains("PartialNullNode1"));
        assertTrue(xmlStr.contains("PartialNullNode2"));
        assertFalse(xmlStr.contains("PartialNullNode3"));        
    }
}
