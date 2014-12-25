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

import org.talend.mdm.webapp.base.client.SessionAwareAsyncCallback;
import org.talend.mdm.webapp.base.client.util.UrlUtil;
import org.talend.mdm.webapp.welcomeportal.client.MainFramePanel;
import org.talend.mdm.webapp.welcomeportal.client.WelcomePortal;
import org.talend.mdm.webapp.welcomeportal.client.i18n.MessagesFactory;

import com.extjs.gxt.ui.client.event.IconButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.button.ToolButton;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.HTML;

public class StartPortlet extends BasePortlet {

    public StartPortlet(MainFramePanel portal) {
        super(WelcomePortal.START, portal);

        label.setText(MessagesFactory.getMessages().useful_links_desc());

        this.getHeader().removeTool(refreshBtn);
        this.getHeader().addTool(new ToolButton("x-tool-refresh", new SelectionListener<IconButtonEvent>() { //$NON-NLS-1$

                    @Override
                    public void componentSelected(IconButtonEvent ce) {
                        service.getMenuLabel(UrlUtil.getLanguage(), WelcomePortal.BROWSEAPP,
                                new SessionAwareAsyncCallback<String>() {

                                    @Override
                                    public void onSuccess(String id) {
                                        refresh();
                                    }
                                });
                    }

                }));

        initLinks();
    }

    @Override
    public void refresh() {
        // same content, no need to refresh
        return;
    }

    private void initLinks() {
        service.getMenuLabel(UrlUtil.getLanguage(), WelcomePortal.BROWSEAPP, new SessionAwareAsyncCallback<String>() {

            @Override
            public void onSuccess(String id) {

                StringBuilder sb1 = new StringBuilder(
                        "<span id=\"ItemsBrowser\" style=\"padding-right:8px;cursor: pointer; width:150;\" class=\"labelStyle\" title=\"" + MessagesFactory.getMessages().browse_items() + "\">"); //$NON-NLS-1$ //$NON-NLS-2$
                sb1.append("<IMG SRC=\"/talendmdm/secure/img/menu/browse.png\"/>&nbsp;"); //$NON-NLS-1$
                sb1.append(MessagesFactory.getMessages().browse_items());
                sb1.append("</span>"); //$NON-NLS-1$
                HTML browseHtml = new HTML(sb1.toString());
                browseHtml.addClickHandler(new ClickHandler() {

                    @Override
                    public void onClick(ClickEvent event) {
                        portal.itemClick(WelcomePortal.BROWSECONTEXT, WelcomePortal.BROWSEAPP);
                    }

                });
                set.add(browseHtml);
                StringBuilder sb2 = new StringBuilder(
                        "<span id=\"Journal\" style=\"padding-right:8px;cursor: pointer; width:150;\" class=\"labelStyle\" title=\"" + MessagesFactory.getMessages().journal() + "\">"); //$NON-NLS-1$ //$NON-NLS-2$
                sb2.append("<IMG SRC=\"/talendmdm/secure/img/menu/updatereport.png\"/>&nbsp;"); //$NON-NLS-1$
                sb2.append(MessagesFactory.getMessages().journal());
                sb2.append("</span>"); //$NON-NLS-1$
                HTML journalHtml = new HTML(sb2.toString());
                journalHtml.addClickHandler(new ClickHandler() {

                    @Override
                    public void onClick(ClickEvent event) {
                        portal.itemClick(WelcomePortal.JOURNALCONTEXT, WelcomePortal.JOURNALAPP);
                    }

                });

                set.add(journalHtml);
                set.layout(true);
            }
        });
    }

}
