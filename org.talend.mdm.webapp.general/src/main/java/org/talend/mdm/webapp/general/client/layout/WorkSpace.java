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
package org.talend.mdm.webapp.general.client.layout;

import org.talend.mdm.webapp.general.client.layout.AccordionMenus.HTMLMenuItem;
import org.talend.mdm.webapp.general.model.MenuBean;

import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.TabItem;
import com.extjs.gxt.ui.client.widget.TabPanel;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.Frame;

public class WorkSpace extends LayoutContainer {

    private static WorkSpace instance;

    private TabPanel workTabPanel = new TabPanel();

    private WorkSpace() {
        super();
        this.setLayout(new FitLayout());
        workTabPanel.setMinTabWidth(115);
        workTabPanel.setResizeTabs(true);
        workTabPanel.setAnimScroll(true);
        workTabPanel.setTabScroll(true);
        this.add(workTabPanel);
    }

    public static WorkSpace getInstance() {
        if (instance == null) {
            instance = new WorkSpace();
        }
        return instance;
    }

    public void addWorkTab(final HTMLMenuItem menuItem) {
        MenuBean menuBean = menuItem.getMenuBean();
        TabItem item = workTabPanel.getItemByItemId(menuBean.getName());
        if (item == null) {
            item = new TabItem(menuBean.getName());
            item.addListener(Events.Select, new Listener<BaseEvent>() {

                public void handleEvent(BaseEvent be) {
                    AccordionMenus.getInstance().selectedItem(menuItem);
                }
            });
            item.setItemId(menuBean.getName());
            item.setClosable(true);
            item.setLayout(new FitLayout());
            Frame frame = new Frame("/" + menuBean.getContext()); //$NON-NLS-1$
            frame.getElement().getStyle().setBorderWidth(0.0D, Unit.PX);
            item.add(frame);
            workTabPanel.add(item);
        }
        workTabPanel.setSelection(item);
    }
}
