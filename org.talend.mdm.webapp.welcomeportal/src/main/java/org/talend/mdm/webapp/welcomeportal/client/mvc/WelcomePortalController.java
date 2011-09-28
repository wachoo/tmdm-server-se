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
package org.talend.mdm.webapp.welcomeportal.client.mvc;

import org.talend.mdm.webapp.welcomeportal.client.WelcomePortalEvents;
import org.talend.mdm.webapp.welcomeportal.client.i18n.MessagesFactory;

import com.allen_sauer.gwt.log.client.Log;
import com.extjs.gxt.ui.client.event.EventType;
import com.extjs.gxt.ui.client.mvc.AppEvent;
import com.extjs.gxt.ui.client.mvc.Controller;
import com.extjs.gxt.ui.client.widget.MessageBox;

/**
 * DOC Administrator class global comment. Detailled comment
 */
public class WelcomePortalController extends Controller {

    private WelcomePortalView view;

    public WelcomePortalController() {
        registerEventTypes(WelcomePortalEvents.InitFrame);
        registerEventTypes(WelcomePortalEvents.Error);
    }

    @Override
    public void initialize() {
        view = new WelcomePortalView(this);
    }

    @Override
    public void handleEvent(AppEvent event) {
        EventType type = event.getType();
        if (type == WelcomePortalEvents.InitFrame) {
            forwardToView(view, event);
        } else if (type == WelcomePortalEvents.Error) {
            onError(event);
        }
    }

    protected void onError(AppEvent ae) {
        Log.error("error: " + ae.<Object> getData()); //$NON-NLS-1$
        MessageBox.alert(MessagesFactory.getMessages().error_title(), ((Exception) ae.<Object> getData()).getMessage(), null);
    }

}
