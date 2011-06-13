package org.talend.mdm.webapp.general.client.mvc.controller;



import java.util.List;

import org.talend.mdm.webapp.general.client.MdmAsyncCallback;
import org.talend.mdm.webapp.general.client.General;
import org.talend.mdm.webapp.general.client.GeneralServiceAsync;
import org.talend.mdm.webapp.general.client.layout.AccordionMenus;
import org.talend.mdm.webapp.general.client.mvc.GeneralEvent;
import org.talend.mdm.webapp.general.client.mvc.view.GeneralView;
import org.talend.mdm.webapp.general.model.MenuBean;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.event.EventType;
import com.extjs.gxt.ui.client.mvc.AppEvent;
import com.extjs.gxt.ui.client.mvc.Controller;
import com.google.gwt.user.client.Window;

public class GeneralController extends Controller {

	private GeneralServiceAsync service;
	
	private GeneralView view;
	
	public GeneralController(){
		registerEventTypes(GeneralEvent.InitFrame);
		registerEventTypes(GeneralEvent.Error);
		registerEventTypes(GeneralEvent.LoadMenus);
	}
	
	@Override
    public void initialize() {
		service = (GeneralServiceAsync) Registry.get(General.OVERALL_SERVICE);
		view = new GeneralView(this);
	}
	
	@Override
	public void handleEvent(final AppEvent event) {
		EventType type = event.getType();
		
		if (type == GeneralEvent.InitFrame){
//		    service.greetServer("hello", new MdmAsyncCallback<String>() {
//	            
//	            @Override
//	            public void onSuccess(String result) {
//	                Window.alert(result);
//	                forwardToView(view, event);
//	            }
//	        });
		    service.getMsg(new MdmAsyncCallback<String>() {

                public void onSuccess(String result) {
                    Window.alert(result);
                    forwardToView(view, event);
                }
            });
		} else if (type == GeneralEvent.Error){
			this.forwardToView(view, event);
		} else if (type == GeneralEvent.LoadMenus){
		    service.getMenus("en", new MdmAsyncCallback<List<MenuBean>>() {
                @Override
                public void onSuccess(List<MenuBean> result) {
                    AccordionMenus.getInstance().initMenus(result);
                }
            });
		}
	}
}
