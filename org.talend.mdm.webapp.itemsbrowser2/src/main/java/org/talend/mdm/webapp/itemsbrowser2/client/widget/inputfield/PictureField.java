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
package org.talend.mdm.webapp.itemsbrowser2.client.widget.inputfield;

import org.talend.mdm.webapp.itemsbrowser2.client.resources.icon.Icons;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.core.El;
import com.extjs.gxt.ui.client.core.XDOM;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FormEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.form.FileUploadField;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.PropertyEditor;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.FormData;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONBoolean;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Image;

public class PictureField extends Field<String> {

    private static final String DefaultImage = "/itemsbrowser2/images/icons/no_image.gif"; //$NON-NLS-1$

    protected El wrap = new El(DOM.createSpan());

    Image image = new Image(DefaultImage);

    protected El input = new El(image.getElement());

    protected Element delHandler = new Image(Icons.INSTANCE.clear_icon()).getElement();

    protected Element addHandler = new Image(Icons.INSTANCE.image_add()).getElement();

    private EditWindow editWin = new EditWindow();

    private Dialog dialog = new Dialog() {

        @Override
        protected void onButtonPressed(Button button) {
            super.onButtonPressed(button);
            if (button == getButtonBar().getItemByItemId(YES)) {
                
                RequestBuilder reqBuilder = new RequestBuilder(RequestBuilder.GET, "/imageserver/secure/ImageDeleteServlet?uri=" + value);

                reqBuilder.setCallback(new RequestCallback() {

                    public void onResponseReceived(Request request, Response response) {
                        String json = response.getText();
                        JSONObject jsObject = JSONParser.parse(json).isObject();
                        JSONBoolean success = jsObject.get("success").isBoolean(); //$NON-NLS-1$
                        JSONString message = jsObject.get("message").isString(); //$NON-NLS-1$
                        boolean succeed = success.booleanValue();
                        MessageBox.alert(succeed ? "Success" : "Failed", message.stringValue(), null);
                        if (succeed) {
                            setValue(null);
                        }
                        dialog.hide();
                    }

                    public void onError(Request request, Throwable exception) {
                        MessageBox.alert("Error", exception.getMessage(), null);
                    }
                });
                try {
                    reqBuilder.send();
                } catch (RequestException e) {
                    MessageBox.alert("RequestException", e.getMessage(), null);
                }

            } else if (button == getButtonBar().getItemByItemId(NO)) {

            }
        }
    };

    public PictureField() {
        setFireChangeEventOnSetValue(true);
        regJs(delHandler);
        regJs(addHandler);
        dialog.setHeading("Confirm");
        dialog.setModal(true);
        dialog.setBlinkModal(true);
        dialog.setButtons(Dialog.YESNO);

        propertyEditor = new PropertyEditor<String>() {

            public String getStringValue(String value) {
                return value.toString();
            }

            public String convertStringValue(String value) {
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
        wrap.dom.appendChild(delHandler);
        wrap.dom.appendChild(addHandler);

        setElement(wrap.dom, target, index);
        super.onRender(target, index);
    }

    private void handlerClick(Element el) {
        if (el == addHandler) {
            editWin.show();
        } else if (el == delHandler) {
            dialog.show();
        }
    }

    private native void regJs(Element el)/*-{
        var instance = this;
        el.onclick = function(){
            instance.@org.talend.mdm.webapp.itemsbrowser2.client.widget.inputfield.PictureField::handlerClick(Lcom/google/gwt/user/client/Element;)(this);
        };
    }-*/;

    @Override
    public void setValue(String value) {
        super.setValue(value);
        if (value != null && value.length() != 0) { //$NON-NLS-1$
            image.setUrl("/imageserver/" + value);
        } else {
            image.setUrl(DefaultImage);
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
            this.setSize(380, 150);
            this.setModal(true);
            this.setBlinkModal(true);
            FormData formData = new FormData();
            editForm.setEncoding(FormPanel.Encoding.MULTIPART);
            editForm.setMethod(FormPanel.Method.POST);
            editForm.setAction("/imageserver/secure/ImageUploadServlet"); //$NON-NLS-1$
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
                    JSONBoolean success = jsObject.get("success").isBoolean(); //$NON-NLS-1$
                    JSONString message = jsObject.get("message").isString(); //$NON-NLS-1$
                    com.google.gwt.user.client.Window.alert(success.booleanValue() + ", " + message.stringValue()); //$NON-NLS-1$
                    if (success.booleanValue()) {
                        setValue(message.stringValue());
                    } else {
                        setValue(null);
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
