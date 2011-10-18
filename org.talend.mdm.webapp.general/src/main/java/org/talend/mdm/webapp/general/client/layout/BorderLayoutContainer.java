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
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.Viewport;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.user.client.Element;

public class BorderLayoutContainer extends Viewport {
	
    Html messageHtml = new Html();

	protected void onRender(Element target, int index) {
		super.onRender(target, index);  
		final BorderLayout layout = new BorderLayout();  
		setLayout(layout);  
		this.setBorders(false);
		
		ContentPanel north = BrandingBar.getInstance();
		LayoutContainer west = AccordionMenus.getInstance();
		WorkSpace center = WorkSpace.getInstance();  
		center.setBorders(false);
		center.setScrollMode(Scroll.AUTOX);  

	    ActionsPanel east = ActionsPanel.getInstance();
	    east.setBorders(false);
	    ContentPanel south = new ContentPanel();
        south.setId("status"); //$NON-NLS-1$
        south.setLayout(new FitLayout());
	    south.setHeaderVisible(false);
	    south.setBorders(false);
	    south.setFrame(true);
	  
	    BorderLayoutData northData = new BorderLayoutData(LayoutRegion.NORTH, 40);
	    northData.setCollapsible(false);  
	    northData.setFloatable(false);  
	    northData.setHideCollapseTool(true);  
	  
	    BorderLayoutData westData = new BorderLayoutData(LayoutRegion.WEST, 250);  
        westData.setMaxSize(300);
	    westData.setSplit(true);  
	    westData.setCollapsible(true);  
	    westData.setMargins(new Margins(0,5,0,0));  
	  
	    BorderLayoutData centerData = new BorderLayoutData(LayoutRegion.CENTER);  
	    centerData.setMargins(new Margins(0));  
	  
	    BorderLayoutData eastData = new BorderLayoutData(LayoutRegion.EAST, 240);  
        eastData.setMaxSize(300);
	    eastData.setSplit(true);  
	    eastData.setCollapsible(true);  
	    eastData.setMargins(new Margins(0,0,0,5));  
	  
        BorderLayoutData southData = new BorderLayoutData(LayoutRegion.SOUTH, 2);
	    southData.setSplit(false);  
	    southData.setCollapsible(false);  
	    southData.setFloatable(true);  

	    add(north, northData);  
	    add(west, westData);  
	    add(center, centerData);  
	    add(east, eastData);  
        add(south, southData);
        south.add(messageHtml);
        registerWorkingMessage();
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
