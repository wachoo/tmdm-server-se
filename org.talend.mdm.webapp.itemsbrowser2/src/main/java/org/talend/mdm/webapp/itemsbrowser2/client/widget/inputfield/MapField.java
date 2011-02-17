package org.talend.mdm.webapp.itemsbrowser2.client.widget.inputfield;

import org.talend.mdm.webapp.itemsbrowser2.client.model.MapBean;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.FormData;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Image;

public class MapField extends Composite{

	class EditWindow extends Window{
		TextField<String> firstName = new TextField<String>();
		TextField<String> url = new TextField<String>();
		
		Button saveButton = new Button("Save");
		Button cancelButton = new Button("Cancel");
		
		SelectionListener<ButtonEvent> listener = new SelectionListener<ButtonEvent>() {
			
			@Override
			public void componentSelected(ButtonEvent ce) {
				Button button = ce.getButton();
				if (button == saveButton){
					MapBean value = MapField.this.getValue();
					value.setName(firstName.getValue());
					value.setAddress(url.getValue());
					MapField.this.setValue(value);
				} else {
					MapField.this.editWin.hide();
				}
			}
		};
		
		@Override  
		protected void onRender(Element parent, int index) {
			FormData formData = new FormData("-20");  
			FormPanel editForm = new FormPanel();
			editForm.setHeaderVisible(false);
		    editForm.setFrame(true);  
		    editForm.setWidth(350);

		    firstName.setFieldLabel("Name");  
		    firstName.setAllowBlank(false);  
		    editForm.add(firstName, formData);  
		    url.setFieldLabel("Url");  
		    url.setAllowBlank(false);  
		    editForm.add(url, formData); 
		    
		    addButton(saveButton);
		    addButton(cancelButton);
		    this.setButtonAlign(HorizontalAlignment.CENTER);
		    saveButton.addSelectionListener(listener);
		    cancelButton.addSelectionListener(listener);
		}
		
		public void setValue(MapBean value){
			firstName.setValue(value.getName());
			url.setValue(value.getAddress());
		}
	}
	
	EditWindow editWin = new EditWindow();
	MapBean value;
	
	Grid grid = new Grid(1,2);
	Anchor link = new Anchor();
	Image addEl = new Image("./images/genericUI/add-element.gif");
	
	public MapField(){
		grid.setWidget(0, 0, link);
		grid.setWidget(0, 1, addEl);
		editWin.setSize(300, 200);
		initEvent();
		this.initWidget(grid);
	}
	
	private void initEvent(){
		addEl.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				editWin.setValue(value);
				editWin.center();
			}
		});
	}
	
	public void setValue(MapBean value){
		this.value = value;
		link.setText(value.getName());
		link.setHref(value.getAddress());
	}
	
	public MapBean getValue(){
		return value;
	}
	
}
