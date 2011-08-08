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
package org.talend.mdm.webapp.adaptor.smtp2.client.mvc;

import org.talend.mdm.webapp.adaptor.smtp2.client.Smtp2Events;
import org.talend.mdm.webapp.adaptor.smtp2.client.widget.GenerateContainer;
import org.talend.mdm.webapp.adaptor.smtp2.client.widget.Smtp2Form;

import com.extjs.gxt.ui.client.GXT;
import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.mvc.AppEvent;
import com.extjs.gxt.ui.client.mvc.Controller;
import com.extjs.gxt.ui.client.mvc.View;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.google.gwt.user.client.Window;

/**
 * DOC Administrator  class global comment. Detailled comment
 */
public class Smtp2View extends View {

    ContentPanel container = new ContentPanel() {

        public void onAttach() {
            monitorWindowResize = true;
            Window.enableScrolling(true);
            setSize(Window.getClientWidth(), Window.getClientHeight());
            super.onAttach();
            GXT.hideLoadingPanel("loading");//$NON-NLS-1$
        }

        protected void onWindowResize(int width, int height) {
            setSize(width, height);
            this.doLayout(true);
        }
    };

    public Smtp2View(Controller controller) {
        super(controller);
    }

    protected void handleEvent(AppEvent event) {
        if (event.getType() == Smtp2Events.InitFrame) {
            onInitFrame(event);
        }
    }

    private void onInitFrame(AppEvent event) {
        ContentPanel container = GenerateContainer.getContentPanel();
        container.setHeaderVisible(false);
        container.setLayout(new RowLayout(Orientation.VERTICAL));
        container.setStyleAttribute("height", "100%");//$NON-NLS-1$ //$NON-NLS-2$  
        container.add(new Smtp2Form(), new RowData(1, 1, new Margins(1)));
    }

}
