// ============================================================================
//
// Copyright (C) 2006-2010 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.mdm.webapp.gxt.framework.client.mvc;

import org.talend.mdm.webapp.gxt.framework.client.AppEvents;
import org.talend.mdm.webapp.gxt.framework.client.AppHeader;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.mvc.AppEvent;
import com.extjs.gxt.ui.client.mvc.Controller;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.mvc.View;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.HtmlContainer;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.TabItem;
import com.extjs.gxt.ui.client.widget.TabPanel;
import com.extjs.gxt.ui.client.widget.Text;
import com.extjs.gxt.ui.client.widget.Viewport;
import com.extjs.gxt.ui.client.widget.TabPanel.TabPosition;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.user.client.ui.RootPanel;

public class AppView extends View {

    public static final String VIEWPORT = "viewport";//$NON-NLS-1$

    public static final String NORTH_PANEL = "north_panel";//$NON-NLS-1$

    public static final String SIDE_PANEL = "side_panel";//$NON-NLS-1$

    public static final String CENTER_PANEL = "center_panel";//$NON-NLS-1$
    
    public static final String CENTER_TAB_PANEL = "center_tab_panel";//$NON-NLS-1$

    private Viewport viewport;

    private HtmlContainer north;

    private ContentPanel side;

    private LayoutContainer center;
    
    private TabPanel tabs;

    public AppView(Controller controller) {
        super(controller);
    }

    protected void initialize() {

    }
    
    protected void handleEvent(AppEvent event) {
        if (event.getType() == AppEvents.InitFrame) {
            initUI();
            Dispatcher.forwardEvent(AppEvents.InitSideBar);
        }
    }

    private void initUI() {
        viewport = new Viewport();
        viewport.setLayout(new BorderLayout());

        createNorth();
        createSide();
        createCenter();

        // registry serves as a global context
        Registry.register(VIEWPORT, viewport);
        Registry.register(NORTH_PANEL, north);
        Registry.register(SIDE_PANEL, side);
        Registry.register(CENTER_PANEL, center);
        Registry.register(CENTER_TAB_PANEL, tabs);

        RootPanel.get().add(viewport);
    }

    private void createNorth() {

        // prepare
        AppHeader appHeader = (AppHeader) Registry.get(AppHeader.APP_HEADER);
        String loginUsr = (String)appHeader.get(AppHeader.LOGIN_USER, "");//$NON-NLS-1$

        // build panel
        StringBuffer sb = new StringBuffer();

        sb.append("<div id='mdm-web-header' class='x-small-editor'>")
             .append("<div id='mdm-web-header-greeting'>hi</div>")
             .append("<div id='mdm-web-header-title'>Talend Master Data Management</div>")
          .append("</div>");//$NON-NLS-1$ //$NON-NLS-2$

        north = new HtmlContainer(sb.toString());
        north.setStateful(false);

        // hiText
        final Text hiText = new Text();

        String user = ""; //$NON-NLS-1$
        if (loginUsr == null || loginUsr.length() == 0) {
            user = "Unknown";
        } else {
            user = loginUsr;
        }

        hiText.setText("Hello, "+user+"!");
        hiText.setStyleAttribute("color", "white"); //$NON-NLS-1$ //$NON-NLS-2$
        north.add(hiText, "#mdm-web-header-greeting"); //$NON-NLS-1$

        // languages
        // TODO
        
        // logout
        // TODO

        BorderLayoutData data = new BorderLayoutData(LayoutRegion.NORTH, 33);
        data.setMargins(new Margins());
        viewport.add(north, data);

    }

    private void createSide() {

        LayoutRegion layoutRegion = LayoutRegion.WEST;

        BorderLayoutData data = new BorderLayoutData(layoutRegion, 200, 150, 350);
        data.setSplit(true);
        data.setCollapsible(true);
        data.setMargins(new Margins(5, 2, 5, 2));

        side = new ContentPanel();
        // VBoxLayout westLayout = new VBoxLayout();
        // westLayout.setVBoxLayoutAlign(VBoxLayoutAlign.STRETCH);

        side.setLayout(new FitLayout());
        side.setLayoutOnChange(true);
        side.setBodyBorder(false);
        side.setHeading("Menus");

        ToolBar toolBar = new ToolBar();
        side.setTopComponent(toolBar);

        viewport.add(side, data);
    }

    private void createCenter() {

        center = new LayoutContainer();
        center.setLayout(new FitLayout());

        BorderLayoutData data = new BorderLayoutData(LayoutRegion.CENTER);
        data.setMargins(new Margins(5, 5, 5, 5));

        // build a tab container
        LayoutContainer container = new LayoutContainer();

        BorderLayout layout = new BorderLayout();
        layout.setEnableState(false);
        container.setLayout(layout);

        tabs = new TabPanel();
        tabs.setResizeTabs(true);
        tabs.setAnimScroll(true);
        tabs.setTabScroll(true);
        tabs.setCloseContextMenu(true);
        tabs.setTabPosition(TabPosition.TOP);

        TabItem welcomeSheet = new TabItem();

        welcomeSheet.setId("Welcome");
        welcomeSheet.setText("Welcome");
        welcomeSheet.setLayout(new FitLayout());
        welcomeSheet.setClosable(true);

        tabs.add(welcomeSheet);

        container.add(tabs, new BorderLayoutData(LayoutRegion.CENTER));

        center.add(container);

        viewport.add(center, data);
    }

}
