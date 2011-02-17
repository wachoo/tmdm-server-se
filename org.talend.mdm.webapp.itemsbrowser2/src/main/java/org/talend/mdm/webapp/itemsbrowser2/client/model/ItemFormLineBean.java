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

import com.extjs.gxt.ui.client.core.El;
import com.extjs.gxt.ui.client.core.XDOM;
import com.extjs.gxt.ui.client.data.BaseModel;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.ComponentPlugin;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.google.gwt.user.client.ui.Widget;

/**
 * DOC HSHU class global comment. Detailled comment
 */
public class ItemFormLineBean extends BaseModel {

    /**
     * 
     */
    private static final long serialVersionUID = 6830378208452308947L;

    public static final String FIELD_TYPE_TEXTFIELD = "FIELD_TYPE_TEXTFIELD";

    private String fieldType;

    private String fieldLabel;

    private String fieldValue;

    private boolean hasForeignKey;

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

    public String getFieldValue() {
        return fieldValue;
    }

    public void setFieldValue(String fieldValue) {
        this.fieldValue = fieldValue;
    }

    public boolean isHasForeignKey() {
        return hasForeignKey;
    }

    public void setHasForeignKey(boolean hasForeignKey) {
        this.hasForeignKey = hasForeignKey;
    }

    /**
     * DOC HSHU Comment method "genField".
     */
    public Widget genField() {

        Field field = null;

        if (fieldType.equals(FIELD_TYPE_TEXTFIELD)) {
            field = genTextField();
        } else {

        }

        return field;

    }

    private TextField genTextField() {

        ComponentPlugin plugin = new ComponentPlugin() {

            public void init(Component component) {
                component.addListener(Events.Render, new Listener<ComponentEvent>() {

                    public void handleEvent(ComponentEvent be) {
                        El elem = be.getComponent().el().findParent(".x-form-element", 3);
                        // should style in external CSS rather than directly
                        // TODO customize to link
                        elem.appendChild(XDOM.create("<div style='color: 615f5f;padding: 1 0 2 0px;'>"
                                + be.getComponent().getData("text") + "</div>"));
                    }
                });
            }
        };

        TextField<String> field = new TextField<String>();
        
        field.setAllowBlank(false);
        field.setFieldLabel(fieldLabel);
        field.setValue(fieldValue);
        if (hasForeignKey) {
            field.addPlugin(plugin);
            field.setData("text", "Has Foreign Key");
        }

        return field;
    }

}
