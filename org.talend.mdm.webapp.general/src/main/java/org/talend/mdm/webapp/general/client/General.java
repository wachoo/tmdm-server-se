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
package org.talend.mdm.webapp.general.client;

import org.talend.mdm.webapp.general.client.boundary.PubService;
import org.talend.mdm.webapp.general.client.message.PublicMessageService;
import org.talend.mdm.webapp.general.client.mvc.GeneralEvent;
import org.talend.mdm.webapp.general.client.mvc.controller.GeneralController;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;

public class General implements EntryPoint {

	public static final String OVERALL_SERVICE = "GeneralService"; //$NON-NLS-1$
	
	public static final String USER_BEAN = "UserBean"; //$NON-NLS-1$
	
	public void onModuleLoad() {
        registerPubServices();
		Registry.register(OVERALL_SERVICE, GWT.create(GeneralService.class));
		PublicMessageService.registerMessageService();
		
		Dispatcher dispatcher = Dispatcher.get();
		dispatcher.addController(new GeneralController());
		dispatcher.dispatch(GeneralEvent.LoadUser);

	}

    private void registerPubServices() {
        PubService.registerLanguageService();
    }
}
