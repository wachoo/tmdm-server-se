package org.talend.mdm.webapp.general.client;

import org.talend.mdm.webapp.general.client.message.PublicMessageService;
import org.talend.mdm.webapp.general.client.mvc.GeneralEvent;
import org.talend.mdm.webapp.general.client.mvc.controller.GeneralController;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;

public class General implements EntryPoint {

	public static final String OVERALL_SERVICE = "GeneralService";
	
	public void onModuleLoad() {
		Registry.register(OVERALL_SERVICE, GWT.create(GeneralService.class));
		
		PublicMessageService.registerMessageService();
		
		Dispatcher dispatcher = Dispatcher.get();
		dispatcher.addController(new GeneralController());
		dispatcher.dispatch(GeneralEvent.InitFrame);

	}
}
