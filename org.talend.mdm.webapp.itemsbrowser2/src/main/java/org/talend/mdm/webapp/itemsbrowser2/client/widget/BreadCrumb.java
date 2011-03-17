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
package org.talend.mdm.webapp.itemsbrowser2.client.widget;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Hyperlink;

/**
 * DOC HSHU class global comment. Detailled comment
 */
public class BreadCrumb extends Composite {

    private HorizontalPanel pWidget = new HorizontalPanel();

    public BreadCrumb(String name, String link) {

        Hyperlink h = new Hyperlink(name, link);

        pWidget.add(h);
        pWidget.add(new HTML("&nbsp;&#187;&nbsp;"));//$NON-NLS-1$

        initWidget(pWidget);
    }

    public BreadCrumb(String name) {

        HTML h = new HTML("<a>" + name + "</a>");//$NON-NLS-1$ //$NON-NLS-2$

        pWidget.add(h);
        pWidget.add(new HTML("&nbsp;&#187;&nbsp;"));//$NON-NLS-1$

        initWidget(pWidget);
    }
}
