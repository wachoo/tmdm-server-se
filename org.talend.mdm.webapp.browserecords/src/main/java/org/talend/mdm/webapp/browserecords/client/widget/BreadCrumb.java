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

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Hyperlink;


public class BreadCrumb extends Composite {

    private HorizontalPanel pWidget = new HorizontalPanel();

    public static String DEFAULTNAME = "Talend MDM", DEFAULTLINK = "../talendmdm/secure"; //$NON-NLS-1$ //$NON-NLS-2$

    public BreadCrumb(Map<String, String> list) {
        int i = 0;

        for (String name : list.keySet()) {
            Hyperlink h = new Hyperlink(name, list.get(name).toString());
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
