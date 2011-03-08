// ============================================================================
//
// Copyright (C) 2006-2010 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.mdm.webapp.itemsbrowser2.client.widget.inputfield.plugin;

import org.talend.mdm.webapp.itemsbrowser2.client.resources.icon.Icons;
import org.talend.mdm.webapp.itemsbrowser2.shared.TypeModel;

import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.ComponentHelper;
import com.extjs.gxt.ui.client.widget.ComponentPlugin;
import com.extjs.gxt.ui.client.widget.WidgetComponent;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.google.gwt.user.client.ui.Image;


/**
 * DOC chliu  class global comment. Detailled comment
 */
public class AddRemovePlugin implements ComponentPlugin {

    public interface Add_Remove_Listener {
        void onAdd(Field field, TypeModel dataType);
        void onRemove(Field field, TypeModel dataType);
    }
    
    TypeModel dataType;
    
    Add_Remove_Listener listener;
    
    Field field;
    WidgetComponent add = new WidgetComponent(new Image(Icons.INSTANCE.drop_add()));
    WidgetComponent remove = new WidgetComponent(new Image(Icons.INSTANCE.drop_no()));
    
    boolean rendered;
    boolean validated = true;
    
    public AddRemovePlugin(TypeModel dataType){
        this.dataType = dataType;
    }
    
    public void init(Component component) {
        field = (Field) component;
        initEvent();
        
        field.addListener(Events.Render, new Listener<BaseEvent>() {
            public void handleEvent(BaseEvent be) {
                if (!rendered){
                    add.render(field.el().getParent().dom);
                    remove.render(field.el().getParent().dom);
                    adjust();
                    ComponentHelper.doAttach(add);
                    ComponentHelper.doAttach(remove);
                    rendered = true;
                }
            }
        });
    }

    private void adjust(){
        if (!rendered)
            return;
        int space = 2;
        if (!validated){
            space = 18;
        }
        add.el().alignTo(field.getElement(), "tl-tr", new int[] {space + 2, 3});
        remove.el().alignTo(field.getElement(), "tl-tr", new int[] {space + 18, 3});
    }
    
    private void initEvent(){
        add.addListener(Events.OnClick, new Listener<BaseEvent>() {
            public void handleEvent(BaseEvent be) {
                if (listener != null){
                    listener.onAdd(field, dataType);
                }
                
            }
        });
        remove.addListener(Events.OnClick, new Listener<BaseEvent>() {
            public void handleEvent(BaseEvent be) {
                if (listener != null){
                    listener.onRemove(field, dataType);
                }
            }
        });
       
        field.addListener(Events.Resize, new Listener<BaseEvent>() {
            public void handleEvent(BaseEvent be) {
                adjust();
            }
        });
        field.addListener(Events.Valid, new Listener<BaseEvent>() {

            public void handleEvent(BaseEvent be) {
                validated = true;
                adjust();
            }
        });
        field.addListener(Events.Invalid, new Listener<BaseEvent>() {

            public void handleEvent(BaseEvent be) {
                validated = false;
                adjust();
            }
        });
    }
    
    public Add_Remove_Listener getListener() {
        return listener;
    }
    
    public void setListener(Add_Remove_Listener listener) {
        this.listener = listener;
    }
}
