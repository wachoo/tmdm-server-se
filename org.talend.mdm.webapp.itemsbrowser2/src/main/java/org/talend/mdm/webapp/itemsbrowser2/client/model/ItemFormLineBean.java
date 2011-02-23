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
package org.talend.mdm.webapp.itemsbrowser2.client.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.extjs.gxt.ui.client.data.BaseModel;

/**
 * DOC HSHU class global comment. Detailled comment
 */
public class ItemFormLineBean extends BaseModel {

    /**
     * 
     */
    private static final long serialVersionUID = 6830378208452308947L;
    
    public static final String FIELD_TYPE_TEXTFIELD = "string";
    public static final String FIELD_TYPE_URL = "URL";

    private String fieldType;

    private String fieldLabel;

    private Serializable fieldValue;

    private boolean hasForeignKey;

    List<ItemFormLineBean> children = new ArrayList<ItemFormLineBean>();
	ItemFormLineBean parent;
    
    /**
     * DOC HSHU ItemFormBean constructor comment.
     */
    public ItemFormLineBean() {

    }

    public String getFieldType() {
        return fieldType;
    }

    public void setFieldType(String fieldType) {
        this.fieldType = fieldType;
    }

    public String getFieldLabel() {
        return fieldLabel;
    }

    public void setFieldLabel(String fieldLabel) {
        this.fieldLabel = fieldLabel;
    }

    public Serializable getFieldValue() {
        return fieldValue;
    }

    public void setFieldValue(Serializable fieldValue) {
        this.fieldValue = fieldValue;
    }

    public boolean isHasForeignKey() {
        return hasForeignKey;
    }

    public void setHasForeignKey(boolean hasForeignKey) {
        this.hasForeignKey = hasForeignKey;
    }

//    private TextField genTextField() {
//
//        ComponentPlugin plugin = new ComponentPlugin() {
//
//            public void init(Component component) {
//                component.addListener(Events.Render, new Listener<ComponentEvent>() {
//
//                    public void handleEvent(ComponentEvent be) {
//                        El elem = be.getComponent().el().findParent(".x-form-element", 3);
//                        // should style in external CSS rather than directly
//                        // TODO customize to link
//                        elem.appendChild(XDOM.create("<div style='color: 615f5f;padding: 1 0 2 0px;'>"
//                                + be.getComponent().getData("text") + "</div>"));
//                    }
//                });
//            }
//        };
//
//        TextField<D> field = new TextField<D>();
//        
//        field.setAllowBlank(false);
//        field.setFieldLabel(fieldLabel);
//        field.setValue(fieldValue);
//        if (hasForeignKey) {
//            field.addPlugin(plugin);
//            field.setData("text", "Has Foreign Key");
//        }
//
//        return field;
//    }

	public List<ItemFormLineBean> getChildren() {
		return children;
	}

	public void setChildren(List<ItemFormLineBean> children) {
		this.children = children;
	}

	public ItemFormLineBean getParent() {
		return parent;
	}

	public void setParent(ItemFormLineBean parent) {
		this.parent = parent;
	}

    
}
