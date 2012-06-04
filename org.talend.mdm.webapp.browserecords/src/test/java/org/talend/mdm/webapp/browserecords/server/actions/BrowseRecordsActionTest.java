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
package org.talend.mdm.webapp.browserecords.server.actions;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit3.PowerMockSuite;
import org.powermock.reflect.Whitebox;
import org.talend.mdm.commmon.util.datamodel.management.DataModelID;
import org.talend.mdm.webapp.base.client.model.ItemBasePageLoadResult;
import org.talend.mdm.webapp.browserecords.client.model.ItemBean;
import org.talend.mdm.webapp.browserecords.client.model.ItemNodeModel;
import org.talend.mdm.webapp.browserecords.client.model.QueryModel;
import org.talend.mdm.webapp.browserecords.client.model.RecordsPagingConfig;
import org.talend.mdm.webapp.browserecords.client.util.CommonUtil;
import org.talend.mdm.webapp.browserecords.server.bizhelpers.DataModelHelper;
import org.talend.mdm.webapp.browserecords.server.bizhelpers.SchemaMockAgent;
import org.talend.mdm.webapp.browserecords.server.bizhelpers.ViewHelper;
import org.talend.mdm.webapp.browserecords.server.provider.SmartViewProvider;
import org.talend.mdm.webapp.browserecords.server.util.SmartViewUtil;
import org.talend.mdm.webapp.browserecords.server.util.TestData;
import org.talend.mdm.webapp.browserecords.shared.EntityModel;
import org.talend.mdm.webapp.browserecords.shared.SmartViewDescriptions;
import org.talend.mdm.webapp.browserecords.shared.ViewBean;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.amalto.core.util.Util;
import com.amalto.webapp.util.webservices.WSStringArray;
import com.amalto.webapp.util.webservices.WSView;
import com.amalto.webapp.util.webservices.WSViewSearch;
import com.amalto.webapp.util.webservices.XtentisPort;
import com.extjs.gxt.ui.client.data.ModelData;

@PrepareForTest({ Util.class, org.talend.mdm.webapp.base.server.util.CommonUtil.class, XtentisPort.class, WSViewSearch.class,
        BrowseRecordsAction.class, SmartViewProvider.class, SmartViewUtil.class, SmartViewDescriptions.class })
@SuppressWarnings("nls")
public class BrowseRecordsActionTest extends TestCase {

    private BrowseRecordsAction action = new BrowseRecordsAction();

    private String xml = "<Agency><Name>Newark</Name><Name>Newark1</Name><City>Newark</City><State>NJ</State><Zip>07107</Zip><Region>EAST</Region><MoreInfo>Map@@http://maps.google.com/maps?q=40.760667,-74.1879&amp;ll=40.760667,-74.1879&amp;z=9</MoreInfo><Id>NJ01</Id></Agency>"; //$NON-NLS-1$

    @SuppressWarnings("unchecked")
    public static TestSuite suite() throws Exception {
        return new PowerMockSuite("Unit tests for " + BrowseRecordsActionTest.class.getSimpleName(),
                BrowseRecordsActionTest.class);
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

    /**
     * Using the Product Model to query ItemBeans<br>
     * DataModel File : Product.xsd<br>
     * View File : Browse_items_Product.item<br>
     * Data Result File : ProductQueryResult.properties
     * 
     * @throws Exception
     */
    public void test_queryItemBeans() throws Exception {
        // Create QueryModel parameter
        QueryModel config = new QueryModel();
        RecordsPagingConfig pagingConfig = new RecordsPagingConfig();
        pagingConfig.setLimit(10);
        pagingConfig.setSortDir("ASC");
        pagingConfig.setSortField("Product/Id");
        config.setPagingLoadConfig(pagingConfig);
        config.setLanguage("en");
        config.setDataClusterPK("Product");
        String concept = "Product";
        String fileName = "Product.xsd";
        String viewPK = "Browse_items_Product";
        // Create ViewBean
        ViewBean viewBean = getViewBean(concept, fileName);
        viewBean.setViewPK(viewPK);
        String keys[] = new String[] { "Product/Id" };
        viewBean.getBindingEntityModel().setKeys(keys);
        // Set viewable elements and searchable elements
        parseElements(concept, viewBean, getXml("Browse_items_Product.item"));
        // Reference View file: 'Browse_items_Product.item' to check the parsing results
        assertEquals(4, viewBean.getViewables().length);
        assertEquals("Product/Id", viewBean.getViewables()[0]);
        assertEquals("Product/Name", viewBean.getViewables()[1]);
        assertEquals("Product/Price", viewBean.getViewables()[2]);
        assertEquals("Product/Availability", viewBean.getViewables()[3]);
        assertEquals(4, viewBean.getSearchables().size());
        // Set entityModel and viewBean
        config.setModel(viewBean.getBindingEntityModel());
        config.setView(viewBean);
        // Mock get result from server-side
        PowerMockito.mockStatic(org.talend.mdm.webapp.base.server.util.CommonUtil.class);
        XtentisPort port = PowerMockito.mock(XtentisPort.class);
        String[] results = getMockResultsFromServer();
        WSStringArray wsStringArray = new WSStringArray(results);
        Mockito.when(org.talend.mdm.webapp.base.server.util.CommonUtil.getPort()).thenReturn(port);
        Mockito.when(port.viewSearch(Mockito.any(WSViewSearch.class))).thenReturn(wsStringArray);
        // Mock get SmartViewDescriptions
        PowerMockito.mockStatic(SmartViewUtil.class);
        SmartViewDescriptions svd = PowerMockito.mock(SmartViewDescriptions.class);
        Mockito.when(SmartViewUtil.build(Mockito.any(SmartViewProvider.class), Mockito.anyString(), Mockito.anyString()))
                .thenReturn(svd);
        // Mock private method
        BrowseRecordsAction newAction = PowerMockito.spy(action);
        Whitebox.<Boolean> invokeMethod(newAction, "checkSmartViewExists", Mockito.anyString(), Mockito.anyString());
        Whitebox.<Boolean> invokeMethod(newAction, "checkSmartViewExistsByOpt", Mockito.anyString(), Mockito.anyString());
        // Call queryItemBeans
        ItemBasePageLoadResult<ItemBean> itemBeans = action.queryItemBeans(config);
        // Total record size
        assertEquals(results[0], "<totalCount>" + itemBeans.getTotalLength() + "</totalCount>");
        // Query record size
        assertEquals(results.length - 1, itemBeans.getData().size());
        // First record
        ItemBean firstItemBean = itemBeans.getData().get(0);
        // First record (xml)
        String result = "<result><Id xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">1</Id><Name>Mug</Name><Price>1.99</Price><Availability>false</Availability></result>";
        assertEquals(result, results[1]);
        // First record (property name list size)
        assertEquals(viewBean.getViewables().length, firstItemBean.getPropertyNames().size());
        // First record (property: Id)
        assertEquals("1", firstItemBean.get("Product/Id"));
        // First record (property: Name)
        assertEquals("Mug", firstItemBean.get("Product/Name"));
        // First record (property: Price)
        assertEquals("1.99", firstItemBean.get("Product/Price"));
        // First record (property: Availability)
        assertEquals("false", firstItemBean.get("Product/Availability"));
        // Second record
        ItemBean secondItemBean = itemBeans.getData().get(1);
        // Second record (property name list size)
        assertEquals(viewBean.getViewables().length, secondItemBean.getPropertyNames().size());
        // Second record (property: Id)
        assertEquals("2", secondItemBean.get("Product/Id"));
        // Second record (property: Name)
        assertEquals("Car", secondItemBean.get("Product/Name"));
        // Second record (property: Price)
        assertEquals("2.99", secondItemBean.get("Product/Price"));
        // Second record (property: Availability)
        assertEquals("false", secondItemBean.get("Product/Availability"));
        // Third record
        ItemBean thirdItemBean = itemBeans.getData().get(2);
        // Third record (property name list size)
        assertEquals(viewBean.getViewables().length, thirdItemBean.getPropertyNames().size());
        // Third record (property: Id)
        assertEquals("3", thirdItemBean.get("Product/Id"));
        // Third record (property: Name)
        assertEquals("Talend", thirdItemBean.get("Product/Name"));
        // Third record (property: Price)
        assertEquals("3.99", thirdItemBean.get("Product/Price"));
        // Third record (property: Availability)
        assertEquals("false", thirdItemBean.get("Product/Availability"));
        // Fourth record
        ItemBean fourthItemBean = itemBeans.getData().get(3);
        // Fourth record (property name list size)
        assertEquals(viewBean.getViewables().length, fourthItemBean.getPropertyNames().size());
        // Fourth record (property: Id)
        assertEquals("4", fourthItemBean.get("Product/Id"));
        // Fourth record (property: Name)
        assertEquals("Shirts", fourthItemBean.get("Product/Name"));
        // Fourth record (property: Price)
        assertEquals("4.99", fourthItemBean.get("Product/Price"));
        // Fourth record (property: Availability)
        assertEquals("true", fourthItemBean.get("Product/Availability"));
        // Fifth record
        ItemBean fifthItemBean = itemBeans.getData().get(4);
        // Fifth record (property name list size)
        assertEquals(viewBean.getViewables().length, fifthItemBean.getPropertyNames().size());
        // Fifth record (property: Id)
        assertEquals("5", fifthItemBean.get("Product/Id"));
        // Fifth record (property: Name)
        assertEquals("Cup", fifthItemBean.get("Product/Name"));
        // Fifth record (property: Price)
        assertEquals("5.99", fifthItemBean.get("Product/Price"));
        // Fifth record (property: Availability)
        assertEquals("true", fifthItemBean.get("Product/Availability"));
    }

    /**
     * Using the ContractInheritance.xsd to test getItemNodeModel<br>
     * Data File: ContractSampleTwo.xml
     * 
     * @throws Exception
     */
    public void test_getItemNodeModel() throws Exception {
        String language = "en";
        String concept = "Contract";
        String ids = "1";
        ViewBean viewBean = getViewBean(concept, "ContractInheritance.xsd");
        ItemBean item = new ItemBean(concept, ids, getXml("ContractSampleTwo.xml"));
        // call getItemNodeModel
        ItemNodeModel root = action.getItemNodeModel(item, viewBean.getBindingEntityModel(), language);
        assertEquals(4, root.getChildCount());
        // the first node
        ItemNodeModel firstNode = (ItemNodeModel) root.getChild(0);
        assertEquals("id", firstNode.getName());
        assertEquals("1", firstNode.getObjectValue().toString());
        assertEquals("Contract/id", firstNode.getTypePath());
        // the second node
        ItemNodeModel secondNode = (ItemNodeModel) root.getChild(1);
        assertEquals("comment", secondNode.getName());
        assertEquals("1", secondNode.getObjectValue().toString());
        assertEquals("Contract/comment", secondNode.getTypePath());
        // the third node
        ItemNodeModel thirdNode = (ItemNodeModel) root.getChild(2);
        assertEquals("detail", thirdNode.getName());
        assertEquals("ContractDetailSubType", thirdNode.getRealType());
        assertEquals("Contract/detail", thirdNode.getTypePath());
        assertEquals(2, thirdNode.getChildCount());
        assertEquals("code", ((ItemNodeModel) thirdNode.getChild(0)).getName());
        assertEquals("subType", ((ItemNodeModel) thirdNode.getChild(1)).getName());
        // detail child node : subType node
        ItemNodeModel detail_subType_node = (ItemNodeModel) thirdNode.getChild(1);
        assertEquals(2, detail_subType_node.getChildCount());
        assertEquals("ContractDetailSubTypeTwo", detail_subType_node.getRealType());
        assertEquals("Contract/detail:ContractDetailSubType/subType", detail_subType_node.getTypePath());
        assertEquals("Contract/detail:ContractDetailSubType/subType:ContractDetailSubTypeTwo/type",
                ((ItemNodeModel) detail_subType_node.getChild(0)).getTypePath());
        assertEquals("Contract/detail:ContractDetailSubType/subType:ContractDetailSubTypeTwo/description",
                ((ItemNodeModel) detail_subType_node.getChild(1)).getTypePath());
        // the fourth node
        ItemNodeModel fourthNode = (ItemNodeModel) root.getChild(3);
        assertEquals("enumEle", fourthNode.getName());
        assertEquals("pending", fourthNode.getObjectValue().toString());
        assertEquals("Contract/enumEle", fourthNode.getTypePath());
    }

    private String[] getMockResultsFromServer() throws IOException {
        List<String> results = new ArrayList<String>();
        InputStream is = BrowseRecordsActionTest.class.getResourceAsStream("../../ProductQueryResult.properties");
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        String ln = br.readLine();
        while (ln != null) {
            results.add(ln);
            ln = br.readLine();
        }
        return results.toArray(new String[results.size()]);
    }

    private void parseElements(String concept, ViewBean viewBean, String xml) throws Exception {
        Document doc = com.amalto.webapp.core.util.Util.parse(xml);
        String viewableXpath = "viewableBusinessElements";
        String searchableXpath = "searchableBusinessElements";
        NodeList viewList = com.amalto.webapp.core.util.Util.getNodeList(doc.getDocumentElement(), viewableXpath);
        String viewables[] = new String[viewList.getLength()];
        for (int k = 0; k < viewList.getLength(); k++) {
            Node node = viewList.item(k);
            viewables[k] = node.getTextContent();
            viewBean.addViewableXpath(node.getTextContent());
        }
        viewBean.setViewables(viewables);
        NodeList searchList = com.amalto.webapp.core.util.Util.getNodeList(doc.getDocumentElement(), searchableXpath);
        WSView wsView = new WSView();
        String searchableElements[] = new String[searchList.getLength()];
        for (int i = 0; i < searchList.getLength(); i++) {
            Node node = searchList.item(i);
            searchableElements[i] = node.getTextContent();
        }
        wsView.setSearchableBusinessElements(searchableElements);
        wsView.setName("Browse_items_Product");
        viewBean.setSearchables(ViewHelper.getSearchables(wsView, concept, "en", viewBean.getBindingEntityModel()));
    }

    /**
     * Test polymorphism type change, It will get the correct typeModel
     * 
     * @throws Exception
     */
    public void test_createSubItemNodeModel() throws Exception {
        String language = "en";
        String concept = "Contract";
        String ids = "1";
        ViewBean viewBean = getViewBean(concept, "ContractInheritance.xsd");
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

    private String getXml(String fileName) throws IOException {
        InputStream stream = BrowseRecordsActionTest.class.getResourceAsStream("../../" + fileName);
        return inputStream2String(stream);
    }

    private ViewBean getViewBean(String concept, String fileName) throws IOException, SAXException {
        EntityModel entityModel = new EntityModel();
        String datamodelName = concept;
        String[] ids = { "" };
        String[] roles = { "Demo_Manager", "System_Admin", "authenticated", "administration" };
        InputStream stream = BrowseRecordsActionTest.class.getResourceAsStream("../../" + fileName);
        String xsd = inputStream2String(stream);

        PowerMockito.mockStatic(Util.class);
        Mockito.when(Util.isEnterprise()).thenReturn(false);
        DataModelHelper.overrideSchemaManager(new SchemaMockAgent(xsd, new DataModelID(datamodelName, null)));
        DataModelHelper.parseSchema(datamodelName, concept, DataModelHelper.convertXsd2ElDecl(concept, xsd), ids, entityModel,
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