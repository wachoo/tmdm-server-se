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
package org.talend.mdm.webapp.gxt.itemsbrowser2.client;

import org.talend.mdm.webapp.gxt.framework.client.AppEvents;
import org.talend.mdm.webapp.gxt.framework.client.page.MenuAppPage;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.Info;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.layout.FlowData;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * DOC HSHU class global comment. Detailled comment
 */
public class Itemsbrowser2MenuAppPage extends MenuAppPage {

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.mdm.webapp.gxt.framework.client.action.MenuAppPage#defineCheckId()
     */
    @Override
    public String defineCheckId() {
        // TODO Auto-generated method stub
        return "itemsbrowser.ItemsBrowser";
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.mdm.webapp.gxt.framework.client.action.MenuAppPage#renderPage()
     */
    @Override
    public void renderPage() {

        Html h = new Html("<div class=text style='padding:5px'>"
                + "<span class=\"sample-text\">To be continued...</span>" + "</div>");

        Button testButton = new Button("Test RCP service", new SelectionListener<ButtonEvent>() {

            @Override
            public void componentSelected(ButtonEvent ce) {
                Itemsbrowser2ServiceAsync service=Itemsbrowser2Service.Util.getInstance();
                service.pingMDM("Hello MDM server! ", new AsyncCallback<String>() { //$NON-NLS-1$

                    public void onFailure(Throwable caught) {
                        Dispatcher.forwardEvent(AppEvents.Error, caught);
                    }

                    public void onSuccess(String result) {
                        Info.display("Info","[MDM server responsed]:"+result);
                    }
                });
                

            }

        });

        ContentPanel cp = new ContentPanel();
        cp.setButtonAlign(HorizontalAlignment.CENTER);
        cp.add(testButton);
        cp.add(h);
        add(cp, new FlowData(10));

    }

}
