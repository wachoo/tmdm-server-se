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
package org.talend.mdm.webapp.general.client.mvc.view;

import java.util.List;

import org.talend.mdm.webapp.general.client.i18n.MessageFactory;
import org.talend.mdm.webapp.general.client.layout.AccordionMenus;
import org.talend.mdm.webapp.general.client.layout.BorderLayoutContainer;
import org.talend.mdm.webapp.general.client.layout.WorkSpace;
import org.talend.mdm.webapp.general.client.mvc.GeneralEvent;
import org.talend.mdm.webapp.general.model.MenuBean;

import com.extjs.gxt.ui.client.event.EventType;
import com.extjs.gxt.ui.client.mvc.AppEvent;
import com.extjs.gxt.ui.client.mvc.Controller;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.mvc.View;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.google.gwt.user.client.ui.RootPanel;

public class GeneralView extends View {

    public static final String WELCOMECONTEXT = "welcomeportal", WELCOMEAPP = "WelcomePortal", WELCOMEID = "Welcome"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

    public static final String DSCCONTEXT = "datastewardship", DSCAPP = "Datastewardship", DSCID = "tdscPanel"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

    public GeneralView(Controller controller) {
        super(controller);
    }

    @Override
    protected void handleEvent(AppEvent event) {
        EventType type = event.getType();
        if (type == GeneralEvent.InitFrame) {
            initFrame(event);
        } else if (type == GeneralEvent.Error) {
            onError(event);
        } else if (type == GeneralEvent.LoadMenus) {
            AccordionMenus.getInstance().initMenus((List<MenuBean>) event.getData());
            Dispatcher dispatcher = Dispatcher.get();
            dispatcher.dispatch(GeneralEvent.LoadActions);
        } else if (type == GeneralEvent.LoadWelcome) {
            loadWelcome(event);
        }
    }

    private void loadWelcome(AppEvent event) {
        WorkSpace.getInstance().loadApp(WELCOMECONTEXT, WELCOMEAPP);
    }

    private void initFrame(AppEvent event) {
        BorderLayoutContainer mainLayout = new BorderLayoutContainer();
        RootPanel.get().add(mainLayout);
        Dispatcher dispatcher = Dispatcher.get();
        dispatcher.dispatch(GeneralEvent.LoadMenus);
        dispatcher.dispatch(GeneralEvent.LoadWelcome);
    }

    private void onError(AppEvent ae) {
        MessageBox.alert(MessageFactory.getMessages().error(), ((Exception) ae.getData()).getMessage(), null);
    }
}
