package org.talend.mdm.webapp.general.client.mvc.view;

import org.talend.mdm.webapp.general.client.layout.BorderLayoutContainer;
import org.talend.mdm.webapp.general.client.mvc.GeneralEvent;

import com.extjs.gxt.ui.client.event.EventType;
import com.extjs.gxt.ui.client.mvc.AppEvent;
import com.extjs.gxt.ui.client.mvc.Controller;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.mvc.View;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.google.gwt.user.client.ui.RootPanel;

public class GeneralView extends View {

	public GeneralView(Controller controller) {
		super(controller);
	}

	@Override
	protected void handleEvent(AppEvent event) {
		EventType type = event.getType();
		if (type == GeneralEvent.InitFrame){
			initFrame(event);
		} else if (type == GeneralEvent.Error){
			onError(event);
		}
	}

	private void initFrame(AppEvent event){
		BorderLayoutContainer mainLayout = new BorderLayoutContainer();
		RootPanel.get().add(mainLayout);
		Dispatcher dispatcher = Dispatcher.get();
		dispatcher.dispatch(GeneralEvent.LoadMenus);
	}
	
	private void onError(AppEvent ae) {
        MessageBox.alert("", "", null);
    }
}
