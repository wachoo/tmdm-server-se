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

import java.util.List;

import org.talend.mdm.webapp.general.client.i18n.MessageFactory;
import org.talend.mdm.webapp.general.model.MenuBean;

import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.layout.FlowLayout;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.HTML;

public class AccordionMenus extends ContentPanel {

    private static AccordionMenus instance;

    private HTMLMenuItem activeItem;

    private AccordionMenus() {
        super();
        this.setHeading(MessageFactory.getMessages().menus());
        this.addStyleName("menus-list"); //$NON-NLS-1$
        this.setLayout(new FlowLayout());
        this.setScrollMode(Scroll.AUTO);

        // registerOpenPages();
    }

    public static AccordionMenus getInstance() {
        if (instance == null) {
            instance = new AccordionMenus();
        }
        return instance;
    }

    public void initMenus(List<MenuBean> menus) {

        for (int i = 0; i < menus.size(); i++) {
            MenuBean item = menus.get(i);
            String toCheckMenuID = item.getContext() + "." + item.getApplication(); //$NON-NLS-1$
            String icon = makeImageIconPart(item, toCheckMenuID);
            StringBuffer str = new StringBuffer();
            str.append("<span class='body'>"); //$NON-NLS-1$
            str.append("<img src='" + icon + "'/>&nbsp;&nbsp;"); //$NON-NLS-1$ //$NON-NLS-2$
            str.append("<span class='desc'>" + item.getName() + "</span></span>"); //$NON-NLS-1$ //$NON-NLS-2$
            HTML html = new HTMLMenuItem(item, str.toString());
            html.addClickHandler(clickHander);
            this.add(html);
        }
        this.layout();
    }

    private String makeImageIconPart(MenuBean item, String toCheckMenuID) {
        String icon = null;
        if (item.getIcon() != null && item.getIcon().trim().length() != 0) {
            icon = "/imageserver/" + item.getIcon() + "?width=16&height=16"; //$NON-NLS-1$ //$NON-NLS-2$
        } else if ("itemsbrowser.ItemsBrowser".equals(toCheckMenuID) || "viewbrowser.ViewBrowser".equals(toCheckMenuID) || "itemsbrowser2.ItemsBrowser2".equals(toCheckMenuID)) { //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            icon = "/talendmdm/secure/img/menu/browse.png"; //$NON-NLS-1$
        } else if ("crossreferencing.CrossReferencing".equals(toCheckMenuID)) { //$NON-NLS-1$
            icon = "/talendmdm/secure/img/menu/crossref.png"; //$NON-NLS-1$
        } else if ("hierarchical.GroupingHierarchy".equals(toCheckMenuID)) { //$NON-NLS-1$
            icon = "/talendmdm/secure/img/menu/grouping_hier.png"; //$NON-NLS-1$
        } else if ("ehierarchical.DerivedHierarchy".equals(toCheckMenuID)) { //$NON-NLS-1$
            icon = "/talendmdm/secure/img/menu/derived_hier.png"; //$NON-NLS-1$
        } else if ("usersandroles.Users".equals(toCheckMenuID)) { //$NON-NLS-1$
            icon = "/talendmdm/secure/img/menu/manage_users.png"; //$NON-NLS-1$
        } else if ("reporting.Reporting".equals(toCheckMenuID)) { //$NON-NLS-1$
            icon = "/talendmdm/secure/img/menu/reporting.png"; //$NON-NLS-1$
        } else if ("SynchronizationItem.SynchronizationItem".equals(toCheckMenuID)) { //$NON-NLS-1$
            icon = "/talendmdm/secure/img/menu/synchro_item.png"; //$NON-NLS-1$
        } else if ("SynchronizationAction.SynchronizationAction".equals(toCheckMenuID)) { //$NON-NLS-1$
            icon = "/talendmdm/secure/img/menu/synchronize.png"; //$NON-NLS-1$
        } else if ("ItemsTrash.ItemsTrash".equals(toCheckMenuID)) { //$NON-NLS-1$
            icon = "/talendmdm/secure/img/menu/trash.png"; //$NON-NLS-1$
        } else if ("updatereport.UpdateReport".equals(toCheckMenuID)) { //$NON-NLS-1$
            icon = "/talendmdm/secure/img/menu/updatereport.png"; //$NON-NLS-1$
        } else if ("workflowtasks.WorkflowTasks".equals(toCheckMenuID)) { //$NON-NLS-1$
            icon = "/talendmdm/secure/img/menu/workflowtasks.png"; //$NON-NLS-1$
        } else if ("license.License".equals(toCheckMenuID)) { //$NON-NLS-1$
            icon = "/talendmdm/secure/img/menu/license.png"; //$NON-NLS-1$
        } else if ("datastewardship.Datastewardship".equals(toCheckMenuID)) { //$NON-NLS-1$
            icon = "/talendmdm/secure/img/menu/stewardship.png"; //$NON-NLS-1$
        } else {
            // default menus icon
            icon = "/talendmdm/secure/img/menu/default.gif"; //$NON-NLS-1$
        }
        return icon;
    }

    public void selectedItem(HTMLMenuItem item) {
        if (activeItem == item)
            return;
        if (activeItem != null) {
            activeItem.removeStyleName("selected"); //$NON-NLS-1$
        }
        item.addStyleName("selected"); //$NON-NLS-1$
        activeItem = item;
    }

    ClickHandler clickHander = new ClickHandler() {

        public void onClick(ClickEvent event) {
            final HTMLMenuItem item = (HTMLMenuItem) event.getSource();
            selectedItem(item);
            MenuBean menuBean = item.getMenuBean();
            initUI(menuBean.getContext(), menuBean.getApplication());
        }
    };

    private native void initUI(String context, String application)/*-{
        $wnd.amalto[context][application].init();
    }-*/;

    class HTMLMenuItem extends HTML {

        MenuBean menuBean;

        public HTMLMenuItem(MenuBean menuBean, String html) {
            super(html);
            this.setStyleName("menu-item"); //$NON-NLS-1$
            this.menuBean = menuBean;
        }

        public MenuBean getMenuBean() {
            return menuBean;
        }
    }
    
    interface GetUrl{
        void getUrl(String url);
    }
}
