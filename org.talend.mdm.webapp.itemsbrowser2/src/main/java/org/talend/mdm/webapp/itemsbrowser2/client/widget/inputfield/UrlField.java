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
package org.talend.mdm.webapp.itemsbrowser2.client.widget.inputfield;

import org.talend.mdm.webapp.itemsbrowser2.client.resources.icon.Icons;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.core.El;
import com.extjs.gxt.ui.client.core.XDOM;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.PropertyEditor;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.FormData;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Image;

/**
 * DOC chliu  class global comment. Detailled comment
 */
public class UrlField extends Field<String> {

    protected El wrap = new El(DOM.createSpan());

    protected El input = new El(DOM.createAnchor());

    protected Element handler = new Image(Icons.INSTANCE.add_element()).getElement();

    private EditWindow editWin = new EditWindow();

    public UrlField() {
        setFireChangeEventOnSetValue(true);
        editWin.setHeading("Edit Url");
        editWin.setSize(600, 150);
        regJs(handler);
        propertyEditor = new PropertyEditor<String>() {

            public String getStringValue(String value) {
                return value.toString();
            }

            public String convertStringValue(String value) {
                return UrlField.this.value;
            }
        };
    }

    public UrlField(String value) {
        this();
        setValue(value);
    }

    protected void onRender(Element target, int index) {
        input.setId(XDOM.getUniqueId());
        input.makePositionable();

        input.dom.setAttribute("target", "_blank");
        input.dom.getStyle().setMarginRight(5, Unit.PX);
        wrap.dom.appendChild(input.dom);
        wrap.dom.appendChild(handler);

        setElement(wrap.dom, target, index);

        super.onRender(target, index);
    }

    private void handlerClick() {
        editWin.setValue(getValue());
        editWin.show();
    }

    private native void regJs(Element el)/*-{
        var instance = this;
        el.onclick = function(){
        	instance.@org.talend.mdm.webapp.itemsbrowser2.client.widget.inputfield.UrlField::handlerClick()();
        };
    }-*/;

    public void setValue(String value) {
        super.setValue(value);
        if (value != null){
            String[] addr = value.split("@@");
            if (addr.length == 2){
                input.dom.setInnerText(addr[0]);
                input.dom.setAttribute("href", addr[1]);
            }
        }
    }

    class EditWindow extends Window {

        SelectionListener<ButtonEvent> listener = new SelectionListener<ButtonEvent>() {

            public void componentSelected(ButtonEvent ce) {
                Button button = ce.getButton();
                if (button == saveButton) {
                    String value = firstName.getValue() + "@@" + url.getValue();
                    UrlField.this.setValue(value);
                    UrlField.this.editWin.hide();
                } else {
                    UrlField.this.editWin.hide();
                }
            }
        };

        TextField<String> firstName = new TextField<String>();

        TextField<String> url = new TextField<String>();

        Button saveButton = new Button("Save", listener);

        Button cancelButton = new Button("Cancel", listener);

        public EditWindow() {
            super();
            this.setLayout(new FitLayout());
            FormData formData = new FormData("-10");
            FormPanel editForm = new FormPanel();
            editForm.setHeaderVisible(false);
            editForm.setBodyBorder(false);
            firstName.setFieldLabel("Name");
            firstName.setAllowBlank(false);
            editForm.add(firstName, formData);
            url.setFieldLabel("Url");
            url.setAllowBlank(false);
            editForm.add(url, formData);

            this.add(editForm);

            setButtonAlign(HorizontalAlignment.CENTER);
            addButton(saveButton);
            addButton(cancelButton);
        }

        public void setValue(String value) {
            if (value != null){
                String[] addr = value.split("@@");
                if (addr.length == 2){
                    firstName.setValue(addr[0]);
                    url.setValue(addr[1]);
                }
            }
        }
    }
}
