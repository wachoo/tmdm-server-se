package org.talend.mdm.webapp.browserecords.client.widget.treedetail;

import java.util.HashMap;
import java.util.Map;

import org.talend.mdm.webapp.base.shared.SimpleTypeModel;
import org.talend.mdm.webapp.base.shared.TypeModel;
import org.talend.mdm.webapp.browserecords.client.model.ItemNodeModel;
import org.talend.mdm.webapp.browserecords.client.widget.inputfield.FormatTextField;
import org.talend.mdm.webapp.browserecords.shared.ComplexTypeModel;
import org.talend.mdm.webapp.browserecords.shared.EntityModel;
import org.talend.mdm.webapp.browserecords.shared.ViewBean;

import com.extjs.gxt.ui.client.widget.form.Field;
import com.google.gwt.junit.client.GWTTestCase;
import com.google.gwt.user.client.DOM;

@SuppressWarnings("nls")
public class TreeDetailGWTTest extends GWTTestCase {

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
        
        boolean flag = true;
        flag = detail.validateNode(root, flag);
        assertFalse(flag);
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

        assertEquals(TreeDetail.isFKDisplayedIntoTab(product, productType, metaDataTypes), false);
        assertEquals(TreeDetail.isFKDisplayedIntoTab(picture, pictureType, metaDataTypes), false);
        assertEquals(TreeDetail.isFKDisplayedIntoTab(name, nameType, metaDataTypes), false);
        assertEquals(TreeDetail.isFKDisplayedIntoTab(description, descriptionType, metaDataTypes), false);
        assertEquals(TreeDetail.isFKDisplayedIntoTab(family, familyType, metaDataTypes), true);
        assertEquals(TreeDetail.isFKDisplayedIntoTab(stores, storesType, metaDataTypes), false);
        assertEquals(TreeDetail.isFKDisplayedIntoTab(store1, storeType, metaDataTypes), true);
        assertEquals(TreeDetail.isFKDisplayedIntoTab(store2, storeType, metaDataTypes), true);
        assertEquals(TreeDetail.isFKDisplayedIntoTab(store3, storeType, metaDataTypes), true);
        
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

    }

    public String getModuleName() {
        return "org.talend.mdm.webapp.browserecords.TestBrowseRecords"; //$NON-NLS-1$
    }
}