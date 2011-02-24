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

import org.talend.mdm.webapp.itemsbrowser2.client.model.PictureBean;
import org.talend.mdm.webapp.itemsbrowser2.client.resources.icon.Icons;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.core.El;
import com.extjs.gxt.ui.client.core.XDOM;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FormEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.form.FileUploadField;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.PropertyEditor;
import com.extjs.gxt.ui.client.widget.layout.FillLayout;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.FormData;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.json.client.JSONBoolean;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Image;

public class PictureField extends Field<PictureBean> {

    private static final String DefaultImage = "/itemsbrowser2/images/icons/no_image.gif";

    protected El wrap = new El(DOM.createSpan());

    Image image = new Image(DefaultImage);

    protected El input = new El(image.getElement());

    protected Element handler = new Image(Icons.INSTANCE.image_add()).getElement();

    private EditWindow editWin = new EditWindow();

    public PictureField() {
        regJs(handler);
        propertyEditor = new PropertyEditor<PictureBean>() {

            public String getStringValue(PictureBean value) {
                return value.toString();
            }

            public PictureBean convertStringValue(String value) {
                return PictureField.this.value;
            }
        };
    }

    @Override
    protected void onRender(Element target, int index) {
        input.setId(XDOM.getUniqueId());
        input.makePositionable();

        input.dom.getStyle().setMarginRight(5, Unit.PX);
        wrap.dom.appendChild(input.dom);
        wrap.dom.appendChild(handler);

        setElement(wrap.dom, target, index);
        regJs(handler);
        super.onRender(target, index);
    }

    private void handlerClick() {
        editWin.show();
    }

    private native void regJs(Element el)/*-{
        var instance = this;
        el.onclick = function(){
        	instance.@org.talend.mdm.webapp.itemsbrowser2.client.widget.inputfield.PictureField::handlerClick()();
        };
    }-*/;

    @Override
    public void setValue(PictureBean value) {
        super.setValue(value);
        if (value.getUrl() != null && !"".equals(value.getUrl())) {
            image.setUrl(value.getUrl());
        }
    }

    class EditWindow extends Window {

        private FormPanel editForm = new FormPanel();

        private FileUploadField file = new FileUploadField();

        private SelectionListener<ButtonEvent> listener = new SelectionListener<ButtonEvent>() {

            @Override
            public void componentSelected(ButtonEvent ce) {
                Button button = ce.getButton();
                if (button == uploadButton) {
                    editForm.submit();
                } else if (button == resetButton) {
                    editForm.reset();
                }
            }
        };

        private Button uploadButton = new Button("Upload", listener);

        private Button resetButton = new Button("Reset", listener);

        public EditWindow() {
            super();
            this.setLayout(new FitLayout());
            this.setHeading("Upload Picture");
            this.setSize(350, 150);
            FormData formData = new FormData("-10");
            editForm.setLayout(new FillLayout());
            editForm.setEncoding(FormPanel.Encoding.MULTIPART);
            editForm.setMethod(FormPanel.Method.POST);
            editForm.setAction("/imageserver/secure/ImageUploadServlet");
            editForm.setHeaderVisible(false);
            editForm.setBodyBorder(false);

            file.setAllowBlank(false);
            file.setName("imageFile");
            file.setFieldLabel("File Name");

            editForm.add(file, formData);
            editForm.addListener(Events.Submit, new Listener<FormEvent>() {

                public void handleEvent(FormEvent be) {
                    String json = be.getResultHtml();
                    JSONObject jsObject = JSONParser.parse(json).isObject();
                    JSONBoolean success = jsObject.get("success").isBoolean();
                    JSONString message = jsObject.get("message").isString();
                    com.google.gwt.user.client.Window.alert(success.booleanValue() + ", " + message.stringValue());
                    if (success.booleanValue()) {
                        image.setUrl("/imageserver/" + message.stringValue());
                        value.setUrl(message.stringValue());
                    } else {
                        image.setUrl(DefaultImage);
                        value.setUrl(null);
                    }

                    EditWindow.this.hide();
                }

            });

            add(editForm);

            setButtonAlign(HorizontalAlignment.CENTER);
            addButton(uploadButton);
            addButton(resetButton);
        }

        @Override
        protected void onShow() {
            super.onShow();
            editForm.reset();
        }
    }
}
