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
package org.talend.mdm.webapp.adaptor.smtp2.client.mvc;

import org.talend.mdm.webapp.adaptor.smtp2.client.Smtp2;
import org.talend.mdm.webapp.adaptor.smtp2.client.Smtp2Events;
import org.talend.mdm.webapp.adaptor.smtp2.client.Smtp2ServiceAsync;

import com.allen_sauer.gwt.log.client.Log;
import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.event.EventType;
import com.extjs.gxt.ui.client.mvc.AppEvent;
import com.extjs.gxt.ui.client.mvc.Controller;

/**
 * DOC Administrator class global comment. Detailled comment
 */
public class Smtp2Controller extends Controller {

    private Smtp2ServiceAsync service;

    private Smtp2View view;

    public Smtp2Controller() {
        registerEventTypes(Smtp2Events.Error);
        registerEventTypes(Smtp2Events.InitFrame);
    }

    public void initialize() {
        service = (Smtp2ServiceAsync) Registry.get(Smtp2.Smtp2_SERVICE);
        view = new Smtp2View(this);
    }

    public void handleEvent(AppEvent event) {
        EventType type = event.getType();
        if (type == Smtp2Events.InitFrame) {
            forwardToView(view, event);
        } else if (type == Smtp2Events.Error) {
            onError(event);
        }
    }

    protected void onError(AppEvent ae) {
        Log.error("error: " + ae.<Object> getData()); //$NON-NLS-1$
        // MessageBox.alert(MessagesFactory.getMessages().error_title(), ae.<Object> getData().toString(), null);
    }

}
