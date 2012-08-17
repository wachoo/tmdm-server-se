package org.talend.mdm.webapp.browserecords.client.widget.treedetail;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.talend.mdm.webapp.base.shared.SimpleTypeModel;
import org.talend.mdm.webapp.base.shared.TypeModel;
import org.talend.mdm.webapp.browserecords.client.BrowseRecords;
import org.talend.mdm.webapp.browserecords.client.i18n.BrowseRecordsMessages;
import org.talend.mdm.webapp.browserecords.client.i18n.MessagesFactory;
import org.talend.mdm.webapp.browserecords.client.model.ItemNodeModel;
import org.talend.mdm.webapp.browserecords.client.util.UserSession;
import org.talend.mdm.webapp.browserecords.client.widget.inputfield.FormatTextField;
import org.talend.mdm.webapp.browserecords.shared.AppHeader;
import org.talend.mdm.webapp.browserecords.shared.ComplexTypeModel;
import org.talend.mdm.webapp.browserecords.shared.EntityModel;
import org.talend.mdm.webapp.browserecords.shared.ViewBean;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.google.gwt.junit.client.GWTTestCase;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Timer;

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
    
    public void testProgressBar(){
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
    
    public void testAutoFillValue4MandatoryBooleanField() {
    	boolean enable=true;
    	
    	List<ModelData> toUpdateNodes=new ArrayList<ModelData>();
    	ItemNodeModel root=new ItemNodeModel();
    	root.set("id", 48);
    	root.set("name", "testBoolean");
    	ItemNodeModel node=new ItemNodeModel();
    	node.set("id", 51);
    	node.set("name", "complext");
    	node.setParent(root);
    	ItemNodeModel node1=new ItemNodeModel();
    	node1.set("id", 52);
    	node1.set("name", "b2");
    	node1.setMandatory(true);
    	node1.setParent(node);
    	ItemNodeModel node2=new ItemNodeModel();
    	node2.set("id", 53);
    	node2.set("name", "e1");
    	node2.setObjectValue("test");
    	node2.setMandatory(true);
    	node2.setParent(node);
       	ItemNodeModel node3=new ItemNodeModel();
    	node3.set("id", 54);
    	node3.set("name", "b3");
    	node3.setParent(node);
    	
    	toUpdateNodes.add(node1);
    	toUpdateNodes.add(node2);
    	toUpdateNodes.add(node3);
    	
    	Map<String, Field<?>> fieldMap=new HashMap<String, Field<?>>();
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