package org.talend.mdm.webapp.itemsbrowser2.client.widget.inputfield;

import org.talend.mdm.webapp.itemsbrowser2.client.model.PictureBean;
import org.talend.mdm.webapp.itemsbrowser2.client.resources.icon.Icons;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.core.El;
import com.extjs.gxt.ui.client.core.XDOM;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.EventType;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.form.FileUploadField;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.FormData;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Image;

public class PictureField extends Field<PictureBean> {
	private static final String defaultImage = "./images/icons/no_image.gif";
	protected El wrap = new El(DOM.createSpan());
	Image image = new Image(defaultImage);
	protected El input = new El(image.getElement());
	protected Element handler = new Image(Icons.INSTANCE.image_add()).getElement();
	
	private EditWindow editWin = new EditWindow();
	
	public PictureField(){
		editWin.setHeading("Upload Picture");
		editWin.setSize(400, 200);
		regJs(handler);
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
	
	private void handlerClick(){
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
		image.setUrl(value.getUrl());
//		image.setWidth(value.getWidth() + "px");
//		image.setHeight(value.getHeight() + "px");
	}
	
	class EditWindow extends Window{
		FormPanel editForm = new FormPanel();
		SelectionListener<ButtonEvent> listener = new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				Button button = ce.getButton();
				if (button == uploadButton){
					editForm.submit();
					EditWindow.this.hide();
				} else {
					EditWindow.this.hide();
				}
			}
		};
		
		TextField<String> firstName = new TextField<String>();
		TextField<String> url = new TextField<String>();
		
		Button uploadButton = new Button("Upload", listener);
		Button resetButton = new Button("Reset", listener);
		
		public EditWindow(){
			super();
			this.setLayout(new FitLayout());
			
			FormData formData = new FormData("-10");  
			editForm.setEncoding (FormPanel.Encoding.MULTIPART);
			editForm.setMethod(FormPanel.Method.POST);  
			editForm.setAction("/UploadServlet.do");  
			editForm.setHeaderVisible(false);
			editForm.setBodyBorder(false);
			
			FileUploadField file = new FileUploadField();  
		    file.setAllowBlank(false);  
		    file.setName("uploadedfile");  
		    file.setFieldLabel("File");  
		    
		    editForm.add(file, formData);

		    add(editForm);
		    setButtonAlign(HorizontalAlignment.CENTER);
			addButton(uploadButton);
			addButton(resetButton);
		}
	}
}
