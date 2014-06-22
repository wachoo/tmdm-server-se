package org.talend.mdm.webapp.browserecords.client.widget.treedetail;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.talend.mdm.webapp.base.client.model.DataTypeConstants;
import org.talend.mdm.webapp.base.client.model.ForeignKeyBean;
import org.talend.mdm.webapp.base.shared.ComplexTypeModel;
import org.talend.mdm.webapp.base.shared.EntityModel;
import org.talend.mdm.webapp.base.shared.FacetModel;
import org.talend.mdm.webapp.base.shared.SimpleTypeModel;
import org.talend.mdm.webapp.base.shared.TypeModel;
import org.talend.mdm.webapp.browserecords.client.BrowseRecords;
import org.talend.mdm.webapp.browserecords.client.creator.DataTypeCreator;
import org.talend.mdm.webapp.browserecords.client.i18n.BrowseRecordsMessages;
import org.talend.mdm.webapp.browserecords.client.i18n.MessagesFactory;
import org.talend.mdm.webapp.browserecords.client.model.ItemNodeModel;
import org.talend.mdm.webapp.browserecords.client.util.UserSession;
import org.talend.mdm.webapp.browserecords.client.widget.ItemDetailToolBar;
import org.talend.mdm.webapp.browserecords.client.widget.ItemPanel;
import org.talend.mdm.webapp.browserecords.client.widget.ItemsDetailPanel;
import org.talend.mdm.webapp.browserecords.client.widget.ItemsDetailPanel.ForeignKeyHandler;
import org.talend.mdm.webapp.browserecords.client.widget.inputfield.FormatTextField;
import org.talend.mdm.webapp.browserecords.client.widget.treedetail.TreeDetail.DynamicTreeItem;
import org.talend.mdm.webapp.browserecords.client.widget.treedetail.TreeDetail.GhostTreeItem;
import org.talend.mdm.webapp.browserecords.shared.AppHeader;
import org.talend.mdm.webapp.browserecords.shared.ViewBean;
import org.talend.mdm.webapp.browserecords.shared.VisibleRuleResult; 

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.google.gwt.junit.client.GWTTestCase;
import com.google.gwt.user.client.DOM;

@SuppressWarnings("nls")
public class TreeDetailGWTTest extends GWTTestCase {

    @Override
    protected void gwtSetUp() throws Exception {
        super.gwtSetUp();
        UserSession session = new UserSession();
        session.put(UserSession.APP_HEADER, new AppHeader());
        Registry.register(BrowseRecords.USER_SESSION, session);
    }
    
    public void testValidateNode(){
        Map<String, Field<?>> fieldMap = new HashMap<String, Field<?>>();
        EntityModel entity = new EntityModel();
        
        ItemNodeModel root = new ItemNodeModel("Product");
        ItemNodeModel id = new ItemNodeModel("Id");
        id.setKey(true);
        id.setMandatory(true);
        id.setValid(true);
        root.add(id);
        fieldMap.put(id.getId().toString(), new FormatTextField());
        
        ItemNodeModel feature = new ItemNodeModel("Feature");
        feature.setMandatory(false);
        root.add(feature);
        
        ItemNodeModel size = new ItemNodeModel("Size");
        size.setMandatory(true);
        size.setValid(true);
        Field<?> field = new FormatTextField();
        field.render(DOM.createElement("Size"));
        fieldMap.put(size.getId().toString(), field);
        feature.add(size);
                
        ItemNodeModel color = new ItemNodeModel("Color");
        color.setMandatory(true);
        color.setValid(false);
        field = new FormatTextField();
        field.render(DOM.createElement("Color"));
        color.setTypePath("Product/Feature/Color");
        fieldMap.put(color.getId().toString(), field);
        entity.getMetaDataTypes().put(color.getTypePath(), new SimpleTypeModel());
        feature.add(color);
        
        TreeDetail detail = new TreeDetail(null); 
        ViewBean viewBean = new ViewBean();
        viewBean.setBindingEntityModel(entity);
        detail.setViewBean(viewBean);

    }

    public void testIsFKDisplayedIntoTab(){
        Map<String, TypeModel> metaDataTypes = new HashMap<String, TypeModel>();

        ItemNodeModel product = new ItemNodeModel("Produce");
        ComplexTypeModel productType = new ComplexTypeModel();
        productType.setTypePath("Product");
        metaDataTypes.put(productType.getTypePath(), productType);
        product.setTypePath(productType.getTypePath());

        ItemNodeModel picture = new ItemNodeModel("Picture");
        SimpleTypeModel pictureType = new SimpleTypeModel();
        pictureType.setTypePath("Product/Picture");
        picture.setTypePath(pictureType.getTypePath());
        metaDataTypes.put(pictureType.getTypePath(), pictureType);
        product.add(picture);

        ItemNodeModel name = new ItemNodeModel("Name");
        SimpleTypeModel nameType = new SimpleTypeModel();
        nameType.setTypePath("Product/Name");
        name.setTypePath(nameType.getTypePath());
        metaDataTypes.put(nameType.getTypePath(), nameType);
        product.add(name);

        ItemNodeModel description = new ItemNodeModel("Description");
        SimpleTypeModel descriptionType = new SimpleTypeModel();
        descriptionType.setTypePath("Product/Description");
        description.setTypePath(descriptionType.getTypePath());
        metaDataTypes.put(descriptionType.getTypePath(), descriptionType);
        product.add(description);

        ItemNodeModel family = new ItemNodeModel("Family");
        SimpleTypeModel familyType = new SimpleTypeModel();
        familyType.setTypePath("Product/Family");
        familyType.setForeignkey("ProductFamily/Id");
        familyType.setNotSeparateFk(false);
        family.setTypePath(familyType.getTypePath());
        metaDataTypes.put(familyType.getTypePath(), familyType);
        product.add(family);

        ItemNodeModel stores = new ItemNodeModel("Stores");
        ComplexTypeModel storesType = new ComplexTypeModel();
        storesType.setTypePath("Product/Stores");
        stores.setTypePath(storesType.getTypePath());
        metaDataTypes.put(storesType.getTypePath(), storesType);
        product.add(stores);

        SimpleTypeModel storeType = new SimpleTypeModel();
        storeType.setTypePath("Product/Store");
        storeType.setForeignkey("Store/Id");
        storeType.setNotSeparateFk(false);
        
        ItemNodeModel store1 = new ItemNodeModel("Store");
        store1.setTypePath(storeType.getTypePath());
        stores.add(store1);
        ItemNodeModel store2 = new ItemNodeModel("Store");
        store2.setTypePath(storeType.getTypePath());
        stores.add(store2);
        ItemNodeModel store3 = new ItemNodeModel("Store");
        store3.setTypePath(storeType.getTypePath());
        stores.add(store3);
        metaDataTypes.put(storeType.getTypePath(), storeType);
        storesType.addSubType(storeType);

        ItemNodeModel otherNode = new ItemNodeModel("OtherNode");
        ComplexTypeModel otherNodeType = new ComplexTypeModel();
        otherNodeType.setTypePath("Product/OtherNodeType");
        otherNode.setTypePath(otherNodeType.getTypePath());
        metaDataTypes.put(otherNodeType.getTypePath(), otherNodeType);
        product.add(otherNode);
        
        ItemNodeModel oNode1 = new ItemNodeModel("O1");
        SimpleTypeModel o1Type = new SimpleTypeModel();
        o1Type.setTypePath("Product/OtherNodeType/O1");
        o1Type.setForeignkey("Other/Id");
        o1Type.setNotSeparateFk(false);
        metaDataTypes.put(o1Type.getTypePath(), o1Type);
        otherNodeType.addSubType(o1Type);
        oNode1.setTypePath(o1Type.getTypePath());
        otherNode.add(oNode1);
        
        ItemNodeModel oNode2 = new ItemNodeModel("O2");
        SimpleTypeModel o2Type = new SimpleTypeModel();
        o2Type.setTypePath("Product/OtherNodeType/O2");
        metaDataTypes.put(o2Type.getTypePath(), o2Type);
        otherNodeType.addSubType(o2Type);
        oNode2.setTypePath(o2Type.getTypePath());
        otherNode.add(oNode2);

        ItemNodeModel oNode3 = new ItemNodeModel("O3");
        SimpleTypeModel o3Type = new SimpleTypeModel();
        o3Type.setTypePath("Product/OtherNodeType/O3");
        metaDataTypes.put(o3Type.getTypePath(), o3Type);
        otherNodeType.addSubType(o3Type);
        oNode3.setTypePath(o3Type.getTypePath());
        otherNode.add(oNode3);
        
        
        assertEquals(TreeDetail.isFKDisplayedIntoTab(product, productType, metaDataTypes), false);
        assertEquals(TreeDetail.isFKDisplayedIntoTab(picture, pictureType, metaDataTypes), false);
        assertEquals(TreeDetail.isFKDisplayedIntoTab(name, nameType, metaDataTypes), false);
        assertEquals(TreeDetail.isFKDisplayedIntoTab(description, descriptionType, metaDataTypes), false);
        assertEquals(TreeDetail.isFKDisplayedIntoTab(family, familyType, metaDataTypes), true);
        assertEquals(TreeDetail.isFKDisplayedIntoTab(stores, storesType, metaDataTypes), false);
        assertEquals(TreeDetail.isFKDisplayedIntoTab(store1, storeType, metaDataTypes), true);
        assertEquals(TreeDetail.isFKDisplayedIntoTab(store2, storeType, metaDataTypes), true);
        assertEquals(TreeDetail.isFKDisplayedIntoTab(store3, storeType, metaDataTypes), true);
        assertEquals(false, TreeDetail.isFKDisplayedIntoTab(otherNode, otherNodeType, metaDataTypes));
        assertEquals(false, TreeDetail.isFKDisplayedIntoTab(oNode1, o1Type, metaDataTypes));
        assertEquals(false, TreeDetail.isFKDisplayedIntoTab(oNode2, o2Type, metaDataTypes));
        assertEquals(false, TreeDetail.isFKDisplayedIntoTab(oNode3, o3Type, metaDataTypes));
        
        familyType.setNotSeparateFk(true);
        assertEquals(TreeDetail.isFKDisplayedIntoTab(product, productType, metaDataTypes), false);
        assertEquals(TreeDetail.isFKDisplayedIntoTab(picture, pictureType, metaDataTypes), false);
        assertEquals(TreeDetail.isFKDisplayedIntoTab(name, nameType, metaDataTypes), false);
        assertEquals(TreeDetail.isFKDisplayedIntoTab(description, descriptionType, metaDataTypes), false);
        assertEquals(TreeDetail.isFKDisplayedIntoTab(family, familyType, metaDataTypes), false);
        assertEquals(TreeDetail.isFKDisplayedIntoTab(stores, storesType, metaDataTypes), false);
        assertEquals(TreeDetail.isFKDisplayedIntoTab(store1, storeType, metaDataTypes), true);
        assertEquals(TreeDetail.isFKDisplayedIntoTab(store2, storeType, metaDataTypes), true);
        assertEquals(TreeDetail.isFKDisplayedIntoTab(store3, storeType, metaDataTypes), true);
        assertEquals(false, TreeDetail.isFKDisplayedIntoTab(otherNode, otherNodeType, metaDataTypes));
        assertEquals(false, TreeDetail.isFKDisplayedIntoTab(oNode1, o1Type, metaDataTypes));
        assertEquals(false, TreeDetail.isFKDisplayedIntoTab(oNode2, o2Type, metaDataTypes));
        assertEquals(false, TreeDetail.isFKDisplayedIntoTab(oNode3, o3Type, metaDataTypes));

        storeType.setNotSeparateFk(true);

        assertEquals(TreeDetail.isFKDisplayedIntoTab(product, productType, metaDataTypes), false);
        assertEquals(TreeDetail.isFKDisplayedIntoTab(picture, pictureType, metaDataTypes), false);
        assertEquals(TreeDetail.isFKDisplayedIntoTab(name, nameType, metaDataTypes), false);
        assertEquals(TreeDetail.isFKDisplayedIntoTab(description, descriptionType, metaDataTypes), false);
        assertEquals(TreeDetail.isFKDisplayedIntoTab(family, familyType, metaDataTypes), false);
        assertEquals(TreeDetail.isFKDisplayedIntoTab(stores, storesType, metaDataTypes), false);
        assertEquals(TreeDetail.isFKDisplayedIntoTab(store1, storeType, metaDataTypes), false);
        assertEquals(TreeDetail.isFKDisplayedIntoTab(store2, storeType, metaDataTypes), false);
        assertEquals(TreeDetail.isFKDisplayedIntoTab(store3, storeType, metaDataTypes), false);
        assertEquals(false, TreeDetail.isFKDisplayedIntoTab(otherNode, otherNodeType, metaDataTypes));
        assertEquals(false, TreeDetail.isFKDisplayedIntoTab(oNode1, o1Type, metaDataTypes));
        assertEquals(false, TreeDetail.isFKDisplayedIntoTab(oNode2, o2Type, metaDataTypes));
        assertEquals(false, TreeDetail.isFKDisplayedIntoTab(oNode3, o3Type, metaDataTypes));
        
        o1Type.setNotSeparateFk(true);
        
        assertEquals(TreeDetail.isFKDisplayedIntoTab(product, productType, metaDataTypes), false);
        assertEquals(TreeDetail.isFKDisplayedIntoTab(picture, pictureType, metaDataTypes), false);
        assertEquals(TreeDetail.isFKDisplayedIntoTab(name, nameType, metaDataTypes), false);
        assertEquals(TreeDetail.isFKDisplayedIntoTab(description, descriptionType, metaDataTypes), false);
        assertEquals(TreeDetail.isFKDisplayedIntoTab(family, familyType, metaDataTypes), false);
        assertEquals(TreeDetail.isFKDisplayedIntoTab(stores, storesType, metaDataTypes), false);
        assertEquals(TreeDetail.isFKDisplayedIntoTab(store1, storeType, metaDataTypes), false);
        assertEquals(TreeDetail.isFKDisplayedIntoTab(store2, storeType, metaDataTypes), false);
        assertEquals(TreeDetail.isFKDisplayedIntoTab(store3, storeType, metaDataTypes), false);
        assertEquals(false, TreeDetail.isFKDisplayedIntoTab(otherNode, otherNodeType, metaDataTypes));
        assertEquals(false, TreeDetail.isFKDisplayedIntoTab(oNode1, o1Type, metaDataTypes));
        assertEquals(false, TreeDetail.isFKDisplayedIntoTab(oNode2, o2Type, metaDataTypes));
        assertEquals(false, TreeDetail.isFKDisplayedIntoTab(oNode3, o3Type, metaDataTypes));
        
    }

    public void testProgressBar() {
        BrowseRecordsMessages msg = MessagesFactory.getMessages();
        // 1. rendering item
        MessageBox progressBar = MessageBox.wait(msg.rendering_title(), msg.render_message(), msg.rendering_progress());
        assertEquals(msg.rendering_title(), progressBar.getTitle());
        assertEquals(msg.render_message(), progressBar.getMessage());
        assertEquals(msg.rendering_progress(), progressBar.getProgressText());
        assertEquals(true, progressBar.isVisible());
        progressBar.close();
        assertEquals(false, progressBar.isVisible());
        // 2. deleting item
        progressBar = MessageBox.wait(msg.delete_item_title(), null, msg.delete_item_progress());
        assertEquals(msg.delete_item_title(), progressBar.getTitle());
        assertEquals(null, progressBar.getMessage());
        assertEquals(msg.delete_item_progress(), progressBar.getProgressText());
        progressBar.close();
    }
    
    public void testForeignKeyLazyRender() {
        final ItemsDetailPanel itemsDetailPanel = new ItemsDetailPanel();
        // add PK tabPanel
        ItemPanel pkTabPanel = new ItemPanel(itemsDetailPanel);
        itemsDetailPanel.addTabItem("PKTab", pkTabPanel, ItemsDetailPanel.SINGLETON, "PK");
        // add FK tabPanel (FK lazy render)
        ItemPanel fkTabPanel = new ItemPanel(itemsDetailPanel);
        // add ForeignKeyTablePanel
        final ForeignKeyTablePanel fkPanel = new ForeignKeyTablePanel("FK Lazy render");
        fkTabPanel.add(fkPanel, new RowData(1, 1));
        itemsDetailPanel.addTabItem("FKTab", fkTabPanel, ItemsDetailPanel.MULTIPLE, "FK");
        // lazy render FK
        itemsDetailPanel.addFkHandler(fkTabPanel, new ForeignKeyHandler() {

            public void onSelect() {
                // render FK
                EntityModel entityModel = new EntityModel();
                entityModel.setConceptName("ProductFamily");
                entityModel.setKeys(new String[] { "ProductFamily" });
                LinkedHashMap<String, TypeModel> metaDataTypes = new LinkedHashMap<String, TypeModel>();
                metaDataTypes.put("ProductFamily", new ComplexTypeModel("ProductFamily", DataTypeConstants.STRING));
                metaDataTypes.put("ProductFamily/Id", new SimpleTypeModel("Id", DataTypeConstants.STRING));
                metaDataTypes.put("ProductFamily/Name", new SimpleTypeModel("Name", DataTypeConstants.STRING));
                entityModel.setMetaDataTypes(metaDataTypes);
                ItemNodeModel parent = new ItemNodeModel("Product");
                List<ItemNodeModel> fkModels = new ArrayList<ItemNodeModel>();
                TypeModel fkTypeModel = new SimpleTypeModel("Family", DataTypeConstants.STRING);
                fkTypeModel.setForeignkey("ProductFamily/Id");
                List<String> foreignKeyInfo = new ArrayList<String>();
                foreignKeyInfo.add("ProductFamily/Name");
                fkTypeModel.setForeignKeyInfo(foreignKeyInfo);
                ItemNodeModel fkModel = new ItemNodeModel();
                ForeignKeyBean fkBean = new ForeignKeyBean();
                fkBean.setConceptName("ProductFamily");
                Map<String, String> foreignKeyInfos = new HashMap<String, String>();
                foreignKeyInfos.put("ProductFamily/Name", "TestFkLazyRender");
                fkBean.setForeignKeyInfo(foreignKeyInfos);
                fkBean.setId("1");
                fkModel.setObjectValue(fkBean);
                fkModels.add(fkModel);
                Map<String, Field<?>> fieldMap = new HashMap<String, Field<?>>();
                fkPanel.initContent(entityModel, parent, fkModels, fkTypeModel, fieldMap, itemsDetailPanel, null);
                itemsDetailPanel.layout(true);
            }
        });
        // before render FK
        assertNotNull(fkPanel.getTopComponent());
        assertNotNull(fkPanel.getBottomComponent());
        assertEquals(0, fkPanel.getItemCount());
        assertEquals(2, itemsDetailPanel.getTabCount());
        // render FK
        itemsDetailPanel.selectTabAtIndex(1);
        // fkTable
        assertEquals(1, fkPanel.getItemCount());
        // fkTable is a grid
        assertTrue(fkPanel.getItem(0) instanceof Grid);
        Grid<?> grid = (Grid<?>) fkPanel.getItem(0);
        // fire attach event
        grid.fireEvent(Events.Attach);
        // store include one record
        assertEquals(1, grid.getStore().getCount());
        // record is ItemNodeModel object
        assertTrue(grid.getStore().getAt(0) instanceof ItemNodeModel);
        ItemNodeModel fkNodeModel = (ItemNodeModel) grid.getStore().getAt(0);
        // nodeModel's objectValue is ForeignKeyBean object
        assertTrue(fkNodeModel.getObjectValue() instanceof ForeignKeyBean);
        ForeignKeyBean fk = (ForeignKeyBean) fkNodeModel.getObjectValue();
        // validate foreignKey info
        assertEquals("1", fk.getId());
        assertEquals("ProductFamily", fk.getConceptName());
        assertEquals(1, fk.getForeignKeyInfo().size());
        assertTrue(fk.getForeignKeyInfo().containsKey("ProductFamily/Name"));
        assertTrue(fk.getForeignKeyInfo().containsValue("TestFkLazyRender"));
    }

    /**
     * Test Model Structure: <br>
     * Root<br>
     *    |_id<br>
     *    |_name<br>
     *    |_cp(autoExpand=false)<br>
     *       |_title<br>
     *       |_content<br>
     */
    public void testNodeLazyLoading() {
        ViewBean viewBean = getViewBean();
        TreeDetail treeDetail = new TreeDetail(new ItemsDetailPanel());
        treeDetail.setViewBean(viewBean);
        ItemNodeModel rootNode = builderItemNode();
        DynamicTreeItem item = treeDetail.buildGWTTree(rootNode, null, false, ItemDetailToolBar.VIEW_OPERATION);
        // validate result
        assertNotNull(item);
        assertEquals(3, item.getChildCount());
        // cp node lazy loading
        DynamicTreeItem cpItem = (DynamicTreeItem) item.getChild(2);
        assertNotNull(cpItem);
        assertEquals(1, cpItem.getChildCount());
        assertTrue(cpItem.getChild(0) instanceof GhostTreeItem);
        // render cp node
        cpItem.setState(true);
        assertEquals(2, cpItem.getChildCount());
        assertTrue(((ItemNodeModel) cpItem.getChild(0).getUserObject()).getName().equals("title"));
        assertTrue(((ItemNodeModel) cpItem.getChild(1).getUserObject()).getName().equals("content"));
    }
    
    /**
     * Model Structure: <br>
     * Test<br>
     *    |_id<br>
     *    |_name<br>
     *    |_oem(visible rule: fn:matches(../name ,"test"))<br>
     *       |_oem_type(enumeration:a,b,c)<br>
     *       |_a(visible rule: fn:starts-with(../oem_type,"a"))<br>
     *       |_b(visible rule: fn:starts-with(../oem_type,"b"))<br>
     *       |_c(visible rule: fn:starts-with(../oem_type,"c"))<br>
     */
    public void testRecrusiveSetItems() {
        // 1. Build Parameter(ItemNodeModel testNode)
        ItemNodeModel testNode = new ItemNodeModel("Test");
        testNode.setTypePath("Test");
        ItemNodeModel idNode = new ItemNodeModel("id");
        idNode.setTypePath("Test/id");
        idNode.setKey(true);
        testNode.add(idNode);
        ItemNodeModel nameNode = new ItemNodeModel("name");
        nameNode.setObjectValue("test");
        nameNode.setTypePath("Test/name");
        testNode.add(nameNode);
        ItemNodeModel oemNode = new ItemNodeModel("oem");
        oemNode.setHasVisiblueRule(true);
        oemNode.setTypePath("Test/oem");
        testNode.add(oemNode);
        ItemNodeModel oem_typeNode = new ItemNodeModel("oem_type");
        oem_typeNode.setTypePath("Test/oem/oem_type");
        oem_typeNode.setObjectValue("a");
        oemNode.add(oem_typeNode);
        ItemNodeModel oem_aNode = new ItemNodeModel("a");
        oem_aNode.setHasVisiblueRule(true);
        oem_aNode.setObjectValue("VisibleRule_TestSuccessfully");
        oem_aNode.setTypePath("Test/oem/a");
        oemNode.add(oem_aNode);
        ItemNodeModel oem_bNode = new ItemNodeModel("b");
        oem_bNode.setHasVisiblueRule(true);
        oem_bNode.setTypePath("Test/oem/b");
        oemNode.add(oem_bNode);
        ItemNodeModel oem_cNode = new ItemNodeModel("c");
        oem_cNode.setHasVisiblueRule(true);
        oem_cNode.setTypePath("Test/oem/c");
        oemNode.add(oem_cNode);
        // 2. Build Parameter(List<VisibleRuleResult> visibleResults)
        List<VisibleRuleResult> visibleResults = new ArrayList<VisibleRuleResult>();
        VisibleRuleResult oem_VisibleRule = new VisibleRuleResult("Test/oem[1]", true);
        VisibleRuleResult a_VisibleRule = new VisibleRuleResult("Test/oem[1]/a[1]", true);
        VisibleRuleResult b_VisibleRule = new VisibleRuleResult("Test/oem[1]/b[1]", false);
        VisibleRuleResult c_VisibleRule = new VisibleRuleResult("Test/oem[1]/c[1]", false);
        visibleResults.add(oem_VisibleRule);
        visibleResults.add(a_VisibleRule);
        visibleResults.add(b_VisibleRule);
        visibleResults.add(c_VisibleRule);
        // 3. Call TreeDetail.recrusiveSetItems(List<VisibleRuleResult>, ItemNodeModel)
        TreeDetail treeDetail = new TreeDetail(new ItemsDetailPanel());
        treeDetail.recrusiveSetItems(visibleResults, testNode);
        // 4. Validate result
        assertEquals(true, oemNode.isVisible());
        assertEquals(true, oem_aNode.isVisible());
        assertEquals(false, oem_bNode.isVisible());
        assertEquals(false, oem_cNode.isVisible());
    }


    private ViewBean getViewBean() {
        ViewBean viewBean = new ViewBean();
        EntityModel bindingEntityModel = new EntityModel();
        LinkedHashMap<String, TypeModel> metaDataTypes = new LinkedHashMap<String, TypeModel>();

        ComplexTypeModel testType = new ComplexTypeModel("Root", DataTypeCreator.getDataType("Root", "anyType"));
        testType.setTypePath("Root");
        testType.addLabel("en", "root");
        metaDataTypes.put(testType.getTypePath(), testType);

        SimpleTypeModel idType = new SimpleTypeModel("id", DataTypeCreator.getDataType("string", "anyType"));
        idType.setTypePath("Root/id");
        idType.addLabel("en", "pk");
        idType.setFacets(new ArrayList<FacetModel>());
        metaDataTypes.put(idType.getTypePath(), idType);

        SimpleTypeModel nameType = new SimpleTypeModel("name", DataTypeCreator.getDataType("string", "anyType"));
        nameType.setTypePath("Root/name");
        nameType.addLabel("en", "name");
        nameType.setFacets(new ArrayList<FacetModel>());
        metaDataTypes.put(nameType.getTypePath(), nameType);

        ComplexTypeModel cpType = new ComplexTypeModel("cp", DataTypeCreator.getDataType("CP", "anyType"));
        cpType.setTypePath("Root/cp");
        cpType.addLabel("en", "complex type");
        cpType.setAutoExpand(false);
        metaDataTypes.put(cpType.getTypePath(), cpType);

        testType.addSubType(idType);
        testType.addSubType(nameType);
        testType.addSubType(cpType);

        SimpleTypeModel cp_titleType = new SimpleTypeModel("title", DataTypeCreator.getDataType("string", "anyType"));
        cp_titleType.setTypePath("Root/cp/title");
        cp_titleType.addLabel("en", "title");
        cp_titleType.setFacets(new ArrayList<FacetModel>());
        metaDataTypes.put(cp_titleType.getTypePath(), cp_titleType);

        SimpleTypeModel cp_contentType = new SimpleTypeModel("content", DataTypeCreator.getDataType("string", "anyType"));
        cp_contentType.setTypePath("Root/cp/content");
        cp_contentType.addLabel("en", "contnet info");
        cp_contentType.setFacets(new ArrayList<FacetModel>());
        metaDataTypes.put(cp_contentType.getTypePath(), cp_contentType);

        cpType.addSubType(cp_titleType);
        cpType.addSubType(cp_contentType);

        bindingEntityModel.setMetaDataTypes(metaDataTypes);
        viewBean.setBindingEntityModel(bindingEntityModel);

        return viewBean;
    }

    private ItemNodeModel builderItemNode() {
        ItemNodeModel testNode = new ItemNodeModel("Root");
        testNode.setTypePath("Root");

        ItemNodeModel idNode = new ItemNodeModel("id");
        idNode.setTypePath("Root/id");
        idNode.setKey(true);
        testNode.add(idNode);

        ItemNodeModel nameNode = new ItemNodeModel("name");
        nameNode.setTypePath("Root/name");
        testNode.add(nameNode);

        ItemNodeModel cpNode = new ItemNodeModel("cp");
        cpNode.setTypePath("Root/cp");
        testNode.add(cpNode);

        ItemNodeModel cpTitleNode = new ItemNodeModel("title");
        cpTitleNode.setTypePath("Root/cp/title");
        cpNode.add(cpTitleNode);

        ItemNodeModel cpContentNode = new ItemNodeModel("content");
        cpContentNode.setTypePath("Root/cp/content");
        cpNode.add(cpContentNode);

        return testNode;
    }

    public void testAutoFillValue4MandatoryBooleanField() {
        boolean enable = true;

        List<ModelData> toUpdateNodes = new ArrayList<ModelData>();
        ItemNodeModel root = new ItemNodeModel();
        root.set("id", 48);
        root.set("name", "testBoolean");
        ItemNodeModel node = new ItemNodeModel();
        node.set("id", 51);
        node.set("name", "complext");
        node.setParent(root);
        ItemNodeModel node1 = new ItemNodeModel();
        node1.set("id", 52);
        node1.set("name", "b2");
        node1.setMandatory(true);
        node1.setParent(node);
        ItemNodeModel node2 = new ItemNodeModel();
        node2.set("id", 53);
        node2.set("name", "e1");
        node2.setObjectValue("test");
        node2.setMandatory(true);
        node2.setParent(node);
        ItemNodeModel node3 = new ItemNodeModel();
        node3.set("id", 54);
        node3.set("name", "b3");
        node3.setParent(node);

        toUpdateNodes.add(node1);
        toUpdateNodes.add(node2);
        toUpdateNodes.add(node3);

        Map<String, Field<?>> fieldMap = new HashMap<String, Field<?>>();
        fieldMap.put("49", new FormatTextField());
        fieldMap.put("52", new CheckBox());
        fieldMap.put("53", new FormatTextField());
        fieldMap.put("54", new CheckBox());

        assertNull(node1.getObjectValue());
        TreeDetailGridFieldCreator.autoFillValue4MandatoryBooleanField(enable, toUpdateNodes, fieldMap);
        assertEquals(false, node1.getObjectValue());

    }

    public String getModuleName() {
        return "org.talend.mdm.webapp.browserecords.TestBrowseRecords";
    }
}