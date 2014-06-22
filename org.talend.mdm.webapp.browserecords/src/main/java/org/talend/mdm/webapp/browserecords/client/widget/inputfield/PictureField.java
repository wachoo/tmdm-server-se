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
package org.talend.mdm.webapp.browserecords.client.widget.inputfield;

import org.talend.mdm.webapp.browserecords.client.i18n.MessagesFactory;
import org.talend.mdm.webapp.browserecords.client.resources.icon.Icons;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.core.El;
import com.extjs.gxt.ui.client.core.XDOM;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FormEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.ComponentHelper;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.FileUploadField;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.PropertyEditor;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.FormData;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.json.client.JSONBoolean;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Image;

public class PictureField extends TextField<String> {
	
	private static final int DEFAULT_IMAGE_SCALE_SIZE = 150;

    private static final String CONTEXT_PATH = GWT.getModuleBaseURL().replaceFirst(GWT.getModuleName() + "/", ""); //$NON-NLS-1$ //$NON-NLS-2$

    public static final String DefaultImage = CONTEXT_PATH + "resources/images/talend/no_image.png"; //$NON-NLS-1$

    protected El wrap = new El(DOM.createSpan());

    Image image = new Image(DefaultImage);

    protected El input = new El(image.getElement());

    protected Element delHandler = new Image(Icons.INSTANCE.clear_icon()).getElement();

    protected Element addHandler = new Image(Icons.INSTANCE.image_add()).getElement();

    private EditWindow editWin = new EditWindow();

    private boolean readOnly;
 
    private Dialog dialog = new Dialog() {

        @Override
        protected void onButtonPressed(Button button) {
            super.onButtonPressed(button);
            if (button == getButtonBar().getItemByItemId(YES)) {

                // Only delete it from client side
            	setValue(null);// reset value when delete
                dialog.hide();  

            } else if (button == getButtonBar().getItemByItemId(NO)) {
                dialog.hide();
            }
        }
    };

    public PictureField() {
        setFireChangeEventOnSetValue(true);
        regJs(delHandler);
        regJs(addHandler);

        dialog.setHeading(MessagesFactory.getMessages().confirm_title());
        dialog.addText(MessagesFactory.getMessages().confirm_delete_img());
        dialog.setModal(true);
        dialog.setBlinkModal(true);
        dialog.setButtons(Dialog.YESNO);

        propertyEditor = new PropertyEditor<String>() {

            public String getStringValue(String value) {
                return value;
            }

            public String convertStringValue(String value) {
                return PictureField.this.value;
            }
        };
        
        image.addLoadHandler(new LoadHandler(){
            public void onLoad(LoadEvent event){
                com.google.gwt.dom.client.Element element = event.getRelativeElement();
                if (element == image.getElement() && !isInternalImageURL(image.getUrl())) {
                    int width = image.getWidth();
                    int height = image.getHeight();
                    int size = DEFAULT_IMAGE_SCALE_SIZE;
                    if (width > 0 && width > height) {
                        if (width > size)
                            image.setPixelSize(size, (int) (height * size / width));
                    } else if (height > size) {
                        image.setPixelSize((int) (width * size / height), size);
                    }
                }
            }
        });
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
        ComponentHelper.doAttach(image);
        super.onRender(target, index);
    }

    private void handlerClick(Element el) {
        if (!readOnly) {
            if (el == addHandler) {
                editWin.show();
            } else if (el == delHandler) {
                dialog.show();
            }
        }
    }

    private native void regJs(Element el)/*-{
		var instance = this;
		el.onclick = function() {
			instance.@org.talend.mdm.webapp.browserecords.client.widget.inputfield.PictureField::handlerClick(Lcom/google/gwt/user/client/Element;)(this);
		};
    }-*/;

    @Override
    public void setValue(String value) {
    	// external source
        if (value != null && (value.toLowerCase().startsWith("http") || value.toLowerCase().startsWith("https"))) { //$NON-NLS-1$//$NON-NLS-2$
            image.setUrl(value);
            return;
        } 
    	
        String oldValue = this.value;
        this.value = value;

        if (value != null && value.length() != 0) {

            if (!value.startsWith("/imageserver/")) //$NON-NLS-1$
                this.value = "/imageserver/" + value; //$NON-NLS-1$
            image.setUrl(scaleInternalUrl(this.value, DEFAULT_IMAGE_SCALE_SIZE));

        } else {
            image.setUrl(DefaultImage);
        }

        if (isFireChangeEventOnSetValue()) {
            fireChangeEvent(oldValue, value);
        }
    }
    
    public String getImageURL(){
    	return image.getUrl();    	
    }
    
    private boolean isInternalImageURL(String url) {
        if (url == null || url.trim().length() == 0)
            return false;
        return url.contains("/imageserver"); //$NON-NLS-1$
    }
    
    private String scaleInternalUrl(String inputValue, int size) {

        if (inputValue == null || inputValue.trim().length() == 0)
            return inputValue;

        return inputValue += "?width=" + size + "&height=" + size + "&preserveAspectRatio=true"; //$NON-NLS-1$ //$NON-NLS-2$//$NON-NLS-3$
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public void setReadOnly(boolean readOnly) {
        super.setReadOnly(readOnly);
        this.readOnly = readOnly;
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

        private Button uploadButton = new Button(MessagesFactory.getMessages().button_upload(), listener);

        private Button resetButton = new Button(MessagesFactory.getMessages().button_reset(), listener);

        public EditWindow() {
            super();
            this.setLayout(new FitLayout());
            this.setHeading(MessagesFactory.getMessages().picture_field_title());
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
            file.setName("imageFile");//$NON-NLS-1$
            file.setFieldLabel(MessagesFactory.getMessages().picture_field_label());

            editForm.add(file, formData);
            editForm.addListener(Events.Submit, new Listener<FormEvent>() {

                public void handleEvent(FormEvent be) {
                    String json = be.getResultHtml();
                    JSONObject jsObject = JSONParser.parse(json).isObject();
                    JSONBoolean success = jsObject.get("success").isBoolean(); //$NON-NLS-1$
                    JSONString message = jsObject.get("message").isString(); //$NON-NLS-1$
                    if (success.booleanValue())
                        MessageBox.alert(MessagesFactory.getMessages().info_title(), MessagesFactory.getMessages()
                                .upload_pic_ok(), null);
                    else
                        MessageBox.alert(MessagesFactory.getMessages().error_title(), MessagesFactory.getMessages()
                                .upload_pic_fail(), null);
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
