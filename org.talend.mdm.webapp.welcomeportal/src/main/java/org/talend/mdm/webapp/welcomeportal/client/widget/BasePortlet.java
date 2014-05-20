// ============================================================================
//
// Copyright (C) 2006-2014 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.mdm.webapp.welcomeportal.client.widget;

import org.talend.mdm.webapp.welcomeportal.client.WelcomePortal;
import org.talend.mdm.webapp.welcomeportal.client.WelcomePortalServiceAsync;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.event.IconButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.Label;
import com.extjs.gxt.ui.client.widget.button.ToolButton;
import com.extjs.gxt.ui.client.widget.custom.Portal;
import com.extjs.gxt.ui.client.widget.custom.Portlet;
import com.extjs.gxt.ui.client.widget.form.FieldSet;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;

public abstract class BasePortlet extends Portlet {

    protected WelcomePortalServiceAsync service = (WelcomePortalServiceAsync) Registry.get(WelcomePortal.WELCOMEPORTAL_SERVICE);

    protected Portal portal;

    protected String portletName;

    public BasePortlet() {
        super();
        this.setLayout(new FitLayout());
        this.setStyleAttribute("padding", "5px"); //$NON-NLS-1$ //$NON-NLS-2$
        this.setCollapsible(true);
        this.setAnimCollapse(false);
        this.setAutoHeight(true);

        this.getHeader().addTool(new ToolButton("x-tool-close", new SelectionListener<IconButtonEvent>() { //$NON-NLS-1$

                    @Override
                    public void componentSelected(IconButtonEvent ce) {
                        removeFromParent();
                    }

                }));
    }

    public BasePortlet(String name, Portal portal) {

        this();

        this.portletName = name;

        this.portal = portal;

        this.setItemId(name + "Portlet"); //$NON-NLS-1$

        Label label = new Label();
        label.setItemId(name + "Label"); //$NON-NLS-1$
        label.setStyleAttribute("font-weight", "bold"); //$NON-NLS-1$ //$NON-NLS-2$
        label.setAutoHeight(true);
        this.add(label);

        FieldSet set = new FieldSet();
        set.setItemId(name + "Set"); //$NON-NLS-1$
        set.setStyleAttribute("padding", "5px"); //$NON-NLS-1$ //$NON-NLS-2$
        set.setStyleAttribute("margin-left", "10px");//$NON-NLS-1$ //$NON-NLS-2$
        set.setStyleAttribute("margin-right", "10px");//$NON-NLS-1$ //$NON-NLS-2$
        set.setBorders(false);

        this.add(set);

        this.setHeading();
        this.setIcon();
    }

    abstract public void setHeading();

    abstract public void setIcon();

    abstract public void refresh();
}
