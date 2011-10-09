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
package org.talend.mdm.webapp.browserecords.client.widget;

import java.util.List;
import java.util.Map;

import org.talend.mdm.webapp.browserecords.client.BrowseRecordsEvents;
import org.talend.mdm.webapp.browserecords.client.model.ItemBean;

import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;

public class BreadCrumb extends Composite {

    private HorizontalPanel pWidget = new HorizontalPanel();

    public static String DEFAULTNAME = "Talend MDM", DEFAULTLINK = "../talendmdm/secure"; //$NON-NLS-1$ //$NON-NLS-2$

    public BreadCrumb(Map<String, ItemBean> list) {
        int i = 0;

        for (String name : list.keySet()) {
            HTML h = null;
            if (list.get(name) != null) {
                h = new HTML("<a>" + name + "</a>");//$NON-NLS-1$ //$NON-NLS-2$
                final ItemBean item = list.get(name);
                h.addClickHandler(new ClickHandler() {

                    public void onClick(ClickEvent arg0) {
                        Dispatcher.forwardEvent(BrowseRecordsEvents.ViewItem, item);
                    }

                });

            } else {
                h = new HTML("<font style=\"color: #2D5593\">" + name + "</font>");
            }
            pWidget.add(h);
            ++i;
            if (i != list.size())
                pWidget.add(new HTML("&nbsp;&#187;&nbsp;"));//$NON-NLS-1$            
        }

        initWidget(pWidget);
    }

    public BreadCrumb(List<String> names) {
        int i = 0;

        for (String name : names) {

            HTML h = new HTML("<a>" + name + "</a>");//$NON-NLS-1$ //$NON-NLS-2$
            pWidget.add(h);
            ++i;
            if (i != names.size())
                pWidget.add(new HTML("&nbsp;&#187;&nbsp;"));//$NON-NLS-1$
        }

        initWidget(pWidget);
    }
}
