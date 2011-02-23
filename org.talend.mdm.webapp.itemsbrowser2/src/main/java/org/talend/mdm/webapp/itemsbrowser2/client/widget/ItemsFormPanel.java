/*
 * Ext GWT - Ext for GWT Copyright(c) 2007-2009, Ext JS, LLC. licensing@extjs.com
 * 
 * http://extjs.com/license
 */
package org.talend.mdm.webapp.itemsbrowser2.client.widget;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;

import org.talend.mdm.webapp.itemsbrowser2.client.model.ItemFormBean;
import org.talend.mdm.webapp.itemsbrowser2.client.model.ItemFormLineBean;

import com.extjs.gxt.ui.client.widget.Composite;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.form.FieldSet;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.FormLayout;
import com.google.gwt.user.client.ui.Widget;

public class ItemsFormPanel extends Composite {

	ItemFormBean itemFormBean;
	
	FormPanel content = new FormPanel();
	
	public ItemsFormPanel(){
		content.setFrame(true);
		content.setHeaderVisible(false);
		this.initComponent(content);
	}
	
	 public ItemsFormPanel(ItemFormBean itemFormBean) {
		this();
		this.itemFormBean = itemFormBean;
	 }
	
    public void setItemFormBean(ItemFormBean itemFormBean) {
        this.itemFormBean = itemFormBean;
    }

    public String getDisplayTitle() {
        String title="Item's form";
        if(itemFormBean!=null)title=itemFormBean.getName();
        return title;
    }
    
	private Widget buildItemGroup(ItemFormLineBean lineBean){
		FieldSet fs = new FieldSet();
		fs.setHeading(lineBean.getFieldLabel());
		fs.setCollapsible(true);
		
		FormLayout layout = new FormLayout();  
	    layout.setLabelWidth(75);  
	    fs.setLayout(layout);  
		
		List<ItemFormLineBean> children = lineBean.getChildren();
		for (ItemFormLineBean child : children){
			Widget field = buildItem(child);
			fs.add(field);
		}
		return fs;
	}
	
	private Widget buildItem(ItemFormLineBean lineBean){
		
		List<ItemFormLineBean> children = lineBean.getChildren();
		if (children != null && children.size() > 0){
			return buildItemGroup(lineBean);
		} else {
			Field<Serializable> field = FieldCreator.createField(lineBean.getFieldType());
			field.setFieldLabel(lineBean.getFieldLabel());
			field.setValue(lineBean.getFieldValue());
			return field;
		}
	}
	
	public void showItem() {
        showItem(null,false);
    }

    public void showItem(ItemFormBean _itemForm, boolean override) {
    	if(override)setItemFormBean(_itemForm);

    	content.removeAll();
    	if (itemFormBean != null) {
    		Iterator<ItemFormLineBean> lineIter = itemFormBean.iteratorLine();
    		while (lineIter.hasNext()){
    			ItemFormLineBean lineBean = lineIter.next();
    			Widget w = buildItem(lineBean);
				content.add(w);
    		}
    	}
    	content.layout(true);
    }
}
