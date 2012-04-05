package org.talend.mdm.webapp.browserecords.client.widget.treedetail;

import java.util.HashMap;
import java.util.Map;

import org.talend.mdm.webapp.base.shared.SimpleTypeModel;
import org.talend.mdm.webapp.browserecords.client.model.ItemNodeModel;
import org.talend.mdm.webapp.browserecords.client.widget.inputfield.FormatTextField;
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
    
    public String getModuleName() {
        return "org.talend.mdm.webapp.browserecords.TestBrowseRecords"; //$NON-NLS-1$
    }
}