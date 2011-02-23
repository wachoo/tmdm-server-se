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

import org.talend.mdm.webapp.itemsbrowser2.client.model.UrlBean;
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

public class UrlField extends Field<UrlBean> {

    protected El wrap = new El(DOM.createSpan());

    protected El input = new El(DOM.createAnchor());

    protected Element handler = new Image(Icons.INSTANCE.add_element()).getElement();

    private EditWindow editWin = new EditWindow();

    public UrlField() {
        editWin.setHeading("Edit Url");
        editWin.setSize(600, 150);
        regJs(handler);
        propertyEditor = new PropertyEditor<UrlBean>() {

            public String getStringValue(UrlBean value) {
                return value.toString();
            }

            public UrlBean convertStringValue(String value) {
                return UrlField.this.value;
            }
        };
    }

    public UrlField(UrlBean value) {
        this();
        setValue(value);
    }

    @Override
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

    @Override
    public void setValue(UrlBean value) {
        super.setValue(value);
        input.dom.setAttribute("href", value.getAddress());
        input.dom.setInnerText(value.getName());

    }

    class EditWindow extends Window {

        SelectionListener<ButtonEvent> listener = new SelectionListener<ButtonEvent>() {

            @Override
            public void componentSelected(ButtonEvent ce) {
                Button button = ce.getButton();
                if (button == saveButton) {
                    UrlBean value = UrlField.this.getValue();
                    value.setName(firstName.getValue());
                    value.setAddress(url.getValue());
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

        public void setValue(UrlBean value) {
            firstName.setValue(value.getName());
            url.setValue(value.getAddress());
        }
    }
}
