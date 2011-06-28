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
package org.talend.mdm.webapp.recyclebin.client.mvc;

import org.talend.mdm.webapp.recyclebin.client.RecycleBin;
import org.talend.mdm.webapp.recyclebin.client.RecycleBinEvents;
import org.talend.mdm.webapp.recyclebin.client.RecycleBinServiceAsync;

import com.allen_sauer.gwt.log.client.Log;
import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.event.EventType;
import com.extjs.gxt.ui.client.mvc.AppEvent;
import com.extjs.gxt.ui.client.mvc.Controller;


/**
 * DOC Administrator  class global comment. Detailled comment
 */
public class RecycleBinController extends Controller {

    private RecycleBinView view;

    private RecycleBinServiceAsync service;

    public RecycleBinController() {
        registerEventTypes(RecycleBinEvents.InitFrame);
        registerEventTypes(RecycleBinEvents.Error);
    }

    public void initialize() {
        service = (RecycleBinServiceAsync) Registry.get(RecycleBin.RECYCLEBIN_SERVICE);
        view = new RecycleBinView(this);
    }

    public void handleEvent(AppEvent event) {
        EventType type = event.getType();
        if (type == RecycleBinEvents.InitFrame) {
            forwardToView(view, event);
        } else if (type == RecycleBinEvents.Error) {
            onError(event);
        }

    }

    protected void onError(AppEvent ae) {
        Log.error("error: " + ae.<Object> getData()); //$NON-NLS-1$
        // MessageBox.alert(MessagesFactory.getMessages().error_title(), ae.<Object> getData().toString(), null);
    }

}
