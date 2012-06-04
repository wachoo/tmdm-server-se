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

import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.Viewport;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.Element;

public class BorderLayoutContainer extends Viewport {

    Html messageHtml = new Html();

    @Override
    protected void onRender(Element target, int index) {
        super.onRender(target, index);
        final BorderLayout layout = new BorderLayout();
        setLayout(layout);
        this.setBorders(false);

        ContentPanel north = BrandingBar.getInstance();
        LayoutContainer west = AccordionMenus.getInstance();
        WorkSpace center = WorkSpace.getInstance();
        center.setId("MDMCenterWorkspace"); //$NON-NLS-1$
        center.setBorders(false);

        ActionsPanel east = ActionsPanel.getInstance();
        east.setBorders(false);

        BorderLayoutData northData = new BorderLayoutData(LayoutRegion.NORTH, 40);
        northData.setCollapsible(false);
        northData.setFloatable(false);
        northData.setHideCollapseTool(true);

        BorderLayoutData westData = new BorderLayoutData(LayoutRegion.WEST, 250);
        westData.setMaxSize(300);
        westData.setSplit(true);
        westData.setCollapsible(true);
        westData.setMargins(new Margins(0, 5, 0, 0));

        BorderLayoutData centerData = new BorderLayoutData(LayoutRegion.CENTER);
        centerData.setMargins(new Margins(0));

        BorderLayoutData eastData = new BorderLayoutData(LayoutRegion.EAST, 240);
        eastData.setMaxSize(300);
        eastData.setSplit(true);
        eastData.setCollapsible(true);
        eastData.setFloatable(false);
        eastData.setMargins(new Margins(0, 0, 0, 5));

        add(north, northData);
        add(west, westData);
        add(center, centerData);
        add(east, eastData);
        registerWorkingMessage();
        this.setId("MDMGeneral"); //$NON-NLS-1$
        this.getElement().getStyle().setPosition(Position.ABSOLUTE);
        this.getElement().getStyle().setTop(0D, Unit.PX);
        this.getElement().getStyle().setLeft(0D, Unit.PX);
    }

    public void setMessage(String message) {
        messageHtml.setHtml(message);
    }

    private static native void registerWorkingMessage()/*-{
                                                       var instance = this;
                                                       $wnd.working = function(message){
                                                       instance.@org.talend.mdm.webapp.general.client.layout.BorderLayoutContainer::setMessage(Ljava/lang/String;)(message);
                                                       };
                                                       }-*/;
}
