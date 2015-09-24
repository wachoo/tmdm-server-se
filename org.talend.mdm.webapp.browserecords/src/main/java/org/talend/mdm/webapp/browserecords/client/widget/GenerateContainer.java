// ============================================================================
//
// Copyright (C) 2006-2015 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.mdm.webapp.browserecords.client.widget;

import org.talend.mdm.webapp.base.client.widget.PortletConstants;
import org.talend.mdm.webapp.browserecords.client.BrowseRecordsEvents;
import org.talend.mdm.webapp.browserecords.client.i18n.MessagesFactory;

import com.extjs.gxt.ui.client.GXT;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.Window;

public class GenerateContainer {

    private static ContentPanel instance;

    private static String defaultViewPk = ""; //$NON-NLS-1$

    public static void generateContentPanel(String panelId, String heading) {
        if (instance != null) {
            instance.removeFromParent();
        }

        instance = new ContentPanel() {

            @Override
            public void onAttach() {
                monitorWindowResize = true;
                Window.enableScrolling(true);
                super.onAttach();
                GXT.hideLoadingPanel("loading");//$NON-NLS-1$
            }

        };
        instance.setHeaderVisible(false);
        instance.setBorders(false);
        instance.setId(panelId);
        instance.setHeading(heading);
    }

    public static void setDefaultView() {
        String parameter = Cookies.getCookie(PortletConstants.PARAMETER_ENTITY);
        Cookies.removeCookie(PortletConstants.PARAMETER_ENTITY);
        setDefaultViewPk(parameter == null ? "" : parameter); //$NON-NLS-1$
        if (parameter != null) {
            Dispatcher dispatcher = Dispatcher.get();
            dispatcher.dispatch(BrowseRecordsEvents.DefaultView);
        }
    }

    public static ContentPanel getContentPanel() {
        return instance;
    }

    public static String defaultTitle() {
        return MessagesFactory.getMessages().browse_record_title();
    }

    public static String getDefaultViewPk() {
        return defaultViewPk;
    }

    public static void setDefaultViewPk(String defaultViewPk) {
        GenerateContainer.defaultViewPk = defaultViewPk;
    }

}
