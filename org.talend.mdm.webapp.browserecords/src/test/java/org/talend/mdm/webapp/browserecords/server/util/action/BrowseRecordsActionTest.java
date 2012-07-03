// ============================================================================
//
// Copyright (C) 2006-2012 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.mdm.webapp.browserecords.server.util.action;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import junit.framework.TestCase;

import org.talend.mdm.commmon.util.datamodel.management.DataModelID;
import org.talend.mdm.webapp.base.shared.TypeModel;
import org.talend.mdm.webapp.browserecords.client.model.ItemBean;
import org.talend.mdm.webapp.browserecords.client.model.ItemNodeModel;
import org.talend.mdm.webapp.browserecords.client.util.CommonUtil;
import org.talend.mdm.webapp.browserecords.server.actions.BrowseRecordsAction;
import org.talend.mdm.webapp.browserecords.server.bizhelpers.DataModelHelper;
import org.talend.mdm.webapp.browserecords.server.bizhelpers.SchemaMockAgent;
import org.talend.mdm.webapp.browserecords.server.util.TestData;
import org.talend.mdm.webapp.browserecords.shared.EntityModel;
import org.talend.mdm.webapp.browserecords.shared.ViewBean;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.amalto.webapp.core.util.Util;
import com.extjs.gxt.ui.client.data.ModelData;


@SuppressWarnings("nls")
public class BrowseRecordsActionTest extends TestCase {

    private BrowseRecordsAction action = new BrowseRecordsAction();

    private String xml = "<Agency><Name>Newark</Name><Name>Newark1</Name><City>Newark</City><State>NJ</State><Zip>07107</Zip><Region>EAST</Region><MoreInfo>Map@@http://maps.google.com/maps?q=40.760667,-74.1879&amp;ll=40.760667,-74.1879&amp;z=9</MoreInfo><Id>NJ01</Id></Agency>"; //$NON-NLS-1$
    
    public void testDynamicAssembleByResultOrder() throws Exception{
    	String xml = "<result><numeroContrat xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance'>5005007</numeroContrat><xsi:type xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance'>AP-RE</xsi:type></result>";
    	ItemBean itemBean = new ItemBean();
    	itemBean.setItemXml(xml);
    	ViewBean viewBean = new ViewBean();
    	viewBean.addViewableXpath("Contrat/numeroContrat");
    	viewBean.addViewableXpath("Contrat/detailContrat/@xsi:type");
    	EntityModel entityModel = new EntityModel();
    	entityModel.setMetaDataTypes(new HashMap<String, TypeModel>());
    	action.dynamicAssembleByResultOrder(itemBean, viewBean, entityModel);
    	assertEquals("5005007",itemBean.get("Contrat/numeroContrat"));
    	assertEquals("AP-RE",itemBean.get("Contrat/detailContrat/@xsi:type"));    	
    }
    
    public void testMultiOccurenceNode() throws Exception {
        String language = "en"; //$NON-NLS-1$
        ItemNodeModel model = action.getItemNodeModel(getItemBean(), TestData.getEntityModel(), language);
        List<ModelData> child = model.getChildren();

        for (int i = 0; i < child.size(); i++) {
            String value = ((ItemNodeModel) child.get(i)).getObjectValue().toString();
            switch (i) {
            case 0: {
                assertEquals("NJ01", value); //$NON-NLS-1$
                break;
            }
            case 1: {
                assertEquals("Newark", value); //$NON-NLS-1$
                break;
            }
            case 2: {
                assertEquals("Newark1", value); //$NON-NLS-1$
                break;
            }
            case 3: {
                assertEquals("Newark", value); //$NON-NLS-1$
                break;
            }
            case 4: {
                assertEquals("NJ", value); //$NON-NLS-1$
                break;
            }
            case 5: {
                assertEquals("07107", value); //$NON-NLS-1$
                break;
            }
            case 6: {
                assertEquals("EAST", value); //$NON-NLS-1$
                break;
            }
            case 7: {
                assertEquals("Map@@http://maps.google.com/maps?q=40.760667,-74.1879&ll=40.760667,-74.1879&z=9", value); //$NON-NLS-1$
                break;
            }
            default: {
            }
            }

        }
    }

    public void test_TaskIdIsNull() {
        ItemBean itemBean = getItemBean();

        boolean taskIdNotNull = itemBean.getTaskId() != null && itemBean.getTaskId().trim().length() > 0
                && !"null".equalsIgnoreCase(itemBean.getTaskId().trim());
        assertEquals(false, taskIdNotNull);
        // 1 taskId = ""
        itemBean.setTaskId("");
        taskIdNotNull = itemBean.getTaskId() != null && itemBean.getTaskId().trim().length() > 0
                && !"null".equalsIgnoreCase(itemBean.getTaskId().trim());
        assertEquals(false, taskIdNotNull);
        // 2 taskId = " "
        itemBean.setTaskId(" ");
        taskIdNotNull = itemBean.getTaskId() != null && itemBean.getTaskId().trim().length() > 0
                && !"null".equalsIgnoreCase(itemBean.getTaskId().trim());
        assertEquals(false, taskIdNotNull);
        // 3 taskId = "null"
        itemBean.setTaskId("null");
        taskIdNotNull = itemBean.getTaskId() != null && itemBean.getTaskId().trim().length() > 0
                && !"null".equalsIgnoreCase(itemBean.getTaskId().trim());
        assertEquals(false, taskIdNotNull);
        // 4 taksId = "Null";
        itemBean.setTaskId("Null");
        taskIdNotNull = itemBean.getTaskId() != null && itemBean.getTaskId().trim().length() > 0
                && !"null".equalsIgnoreCase(itemBean.getTaskId().trim());
        assertEquals(false, taskIdNotNull);
        // 5 taskId = "null ";
        itemBean.setTaskId("null ");
        taskIdNotNull = itemBean.getTaskId() != null && itemBean.getTaskId().trim().length() > 0
                && !"null".equalsIgnoreCase(itemBean.getTaskId().trim());
        assertEquals(false, taskIdNotNull);
        // 6 taskId = "NULL";
        itemBean.setTaskId("NULL");
        taskIdNotNull = itemBean.getTaskId() != null && itemBean.getTaskId().trim().length() > 0
                && !"null".equalsIgnoreCase(itemBean.getTaskId().trim());
        assertEquals(false, taskIdNotNull);
        // 7 taskId = "123";
        itemBean.setTaskId("123");
        taskIdNotNull = itemBean.getTaskId() != null && itemBean.getTaskId().trim().length() > 0
                && !"null".equalsIgnoreCase(itemBean.getTaskId().trim());
        assertEquals(true, taskIdNotNull);
    }

    private ItemBean getItemBean() {
        ItemBean item = new ItemBean("Agency", "NJ01", xml); //$NON-NLS-1$//$NON-NLS-2$
        return item;
    }

    public void test_createSubItemNodeModel() throws Exception {
        String language = "en";
        String concept = "Contract";
        String ids = "1";
        ViewBean viewBean = getViewBean();
        ItemBean item = new ItemBean(concept, ids, getXml("ContractSampleOne.xml"));
        ItemNodeModel root = action.getItemNodeModel(item, viewBean.getBindingEntityModel(), language);
        assertEquals(4, root.getChildCount());

        ItemNodeModel detailModel = (ItemNodeModel) root.getChild(2);
        assertEquals("ContractDetailSubType", detailModel.getRealType());
        assertEquals("Contract/detail", detailModel.getTypePath());
        assertEquals(2, detailModel.getChildCount());

        ItemNodeModel subTypeModel = (ItemNodeModel) detailModel.getChild(1);

        assertEquals("ContractDetailSubTypeOne", subTypeModel.getRealType());
        assertEquals("Contract/detail:ContractDetailSubType/subType", subTypeModel.getTypePath());
        assertEquals(1, subTypeModel.getChildCount());
        assertEquals("Contract/detail:ContractDetailSubType/subType/type",
                ((ItemNodeModel) subTypeModel.getChild(0)).getTypePath());

        // mock UI behavior: change subType(ContractDetailSubTypeOne) to subType(ContractDetailSubTypeTwo)
        subTypeModel.setRealType("ContractDetailSubTypeTwo");
        String contextPath = CommonUtil.getRealXPath(subTypeModel);
        String typePath = CommonUtil.getRealTypePath(subTypeModel);
        typePath = typePath.replaceAll(":" + subTypeModel.getRealType() + "$", ""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        String xml = getXml("ContractSampleTwo.xml");

        ItemNodeModel newSubTypeModel = action.createSubItemNodeModel(viewBean, xml, typePath, contextPath,
                subTypeModel.getRealType(), language);

        assertEquals("ContractDetailSubTypeTwo", newSubTypeModel.getRealType());
        assertEquals("Contract/detail:ContractDetailSubType/subType", newSubTypeModel.getTypePath());
        assertEquals(2, newSubTypeModel.getChildCount());
        assertEquals("Contract/detail:ContractDetailSubType/subType:ContractDetailSubTypeTwo/type",
                ((ItemNodeModel) newSubTypeModel.getChild(0)).getTypePath());
        assertEquals("Contract/detail:ContractDetailSubType/subType:ContractDetailSubTypeTwo/description",
                ((ItemNodeModel) newSubTypeModel.getChild(1)).getTypePath());
    }
    
    public void test_getNodeValue() throws Exception {
        String conceptName = "TaxonomyCategory";
        String xml = "<TaxonomyCategory><Id>1</Id><ZthesId>1</ZthesId><CrcCode>1</CrcCode><TaxonomyCategory>1</TaxonomyCategory><X-BusinessType><Id>2</Id></X-BusinessType></TaxonomyCategory>";
        Document docXml = Util.parse(xml);
        String xpath = "TaxonomyCategory/Id";
        assertEquals("1", parsingNodeValue(docXml, xpath, conceptName));
        xpath = "TaxonomyCategory/X-BusinessType/Id";
        assertEquals("2", parsingNodeValue(docXml, xpath, conceptName));
    }
    
    private String parsingNodeValue(Document docXml, String xpath, String conceptName) throws Exception {
        NodeList nodes = Util.getNodeList(docXml, xpath.replaceFirst(conceptName + "/", "./"));
        if (nodes.getLength() > 0) {
            if (nodes.item(0) instanceof Element) {
                Element value = (Element) nodes.item(0);
                return value.getTextContent();
            }
        }
        return null;
    }

    private String getXml(String fileName) throws IOException {
        InputStream stream = BrowseRecordsActionTest.class.getResourceAsStream("../../../" + fileName);
        return inputStream2String(stream);
    }

    private ViewBean getViewBean() throws IOException, SAXException {
        EntityModel entityModel = new EntityModel();
        String datamodelName = "Contract";
        String concept = "Contract";
        String[] ids = { "" };
        String[] roles = { "Demo_Manager", "System_Admin", "authenticated", "administration" };
        InputStream stream = BrowseRecordsActionTest.class.getResourceAsStream("../../../ContractInheritance.xsd");
        String xsd = inputStream2String(stream);

        DataModelHelper.alwaysEnterprise = true;
        DataModelHelper.overrideSchemaManager(new SchemaMockAgent(xsd, new DataModelID(datamodelName, null)));
        DataModelHelper.parseSchema("Contract", "Contract", DataModelHelper.convertXsd2ElDecl(concept, xsd), ids, entityModel,
                Arrays.asList(roles));
        ViewBean viewBean = new ViewBean();
        viewBean.setBindingEntityModel(entityModel);
        return viewBean;
    }

    private String inputStream2String(InputStream is) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(is));
        StringBuffer buffer = new StringBuffer();
        String line = "";
        while ((line = in.readLine()) != null) {
            buffer.append(line);
        }
        return buffer.toString();
    }   
    
}