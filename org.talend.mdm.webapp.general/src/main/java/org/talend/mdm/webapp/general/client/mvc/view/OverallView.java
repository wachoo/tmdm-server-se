package org.talend.mdm.webapp.general.client.mvc.view;

import org.talend.mdm.webapp.general.client.layout.BorderLayoutContainer;
import org.talend.mdm.webapp.general.client.mvc.OverallEvent;

import com.extjs.gxt.ui.client.event.EventType;
import com.extjs.gxt.ui.client.mvc.AppEvent;
import com.extjs.gxt.ui.client.mvc.Controller;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.mvc.View;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.google.gwt.user.client.ui.RootPanel;

public class OverallView extends View {

	public OverallView(Controller controller) {
		super(controller);
	}

	@Override
	protected void handleEvent(AppEvent event) {
		EventType type = event.getType();
		if (type == OverallEvent.InitFrame){
			initFrame(event);
		} else if (type == OverallEvent.Error){
			onError(event);
		}
	}

	private void initFrame(AppEvent event){
		BorderLayoutContainer mainLayout = new BorderLayoutContainer();
		RootPanel.get().add(mainLayout);
		Dispatcher dispatcher = Dispatcher.get();
		dispatcher.dispatch(OverallEvent.LoadMenus);
	}
	
	private void onError(AppEvent ae) {
        MessageBox.alert("", "", null);
    }
}
