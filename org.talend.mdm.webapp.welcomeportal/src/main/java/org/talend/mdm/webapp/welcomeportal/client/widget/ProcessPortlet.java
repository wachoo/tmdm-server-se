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

import java.util.List;

import org.talend.mdm.webapp.base.client.SessionAwareAsyncCallback;
import org.talend.mdm.webapp.base.client.util.UrlUtil;
import org.talend.mdm.webapp.welcomeportal.client.MainFramePanel;
import org.talend.mdm.webapp.welcomeportal.client.WelcomePortal;
import org.talend.mdm.webapp.welcomeportal.client.i18n.MessagesFactory;
import org.talend.mdm.webapp.welcomeportal.client.resources.icon.Icons;

import com.extjs.gxt.ui.client.event.IconButtonEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.Label;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.ToolButton;
import com.extjs.gxt.ui.client.widget.custom.Portal;
import com.extjs.gxt.ui.client.widget.form.FieldSet;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.HTML;

public class ProcessPortlet extends BasePortlet {

    public ProcessPortlet(Portal portal) {
        super(WelcomePortal.PROCESS, portal);

        this.getHeader().addTool(new ToolButton("x-tool-refresh", new SelectionListener<IconButtonEvent>() { //$NON-NLS-1$

                    @Override
                    public void componentSelected(IconButtonEvent ce) {
                        refresh();
                    }

                }));

        initLinks();
    }

    @Override
    public void refresh() {
        initLinks();
    }

    private void initLinks() {

        final Label label = (Label) this.getItemByItemId(portletName + "Label"); //$NON-NLS-1$
        label.setText(MessagesFactory.getMessages().process_desc());

        final FieldSet set = (FieldSet) this.getItemByItemId(portletName + "Set"); //$NON-NLS-1$        
        set.removeAll();

        service.getStandaloneProcess(UrlUtil.getLanguage(), new SessionAwareAsyncCallback<List<String>>() {

            @Override
            public void onSuccess(List<String> list) {
                if (list.isEmpty()) {
                    label.setText(MessagesFactory.getMessages().no_standalone_process());
                    set.setVisible(false);
                } else {
                    for (int j = 0; j < list.size(); j = j + 2) {
                        final String str = list.get(j);
                        String strDesc = list.get(j + 1);
                        HTML processHtml = new HTML();
                        StringBuilder sb = new StringBuilder();
                        sb.append("<span id=\"processes"); //$NON-NLS-1$
                        sb.append(str);
                        sb.append("\" style=\"padding-right:8px;cursor: pointer;\" class=\"labelStyle\">"); //$NON-NLS-1$
                        sb.append("<IMG SRC=\"/talendmdm/secure/img/genericUI/runnable_bullet.png\"/>&nbsp;"); //$NON-NLS-1$
                        sb.append(strDesc.replace("Runnable#", "")); //$NON-NLS-1$ //$NON-NLS-2$
                        sb.append("</span>"); //$NON-NLS-1$
                        processHtml.setHTML(sb.toString());
                        processHtml.addClickHandler(new ClickHandler() {

                            @Override
                            public void onClick(ClickEvent arg0) {
                                service.isExpired(UrlUtil.getLanguage(), new SessionAwareAsyncCallback<Boolean>() {

                                    @Override
                                    public void onSuccess(Boolean result) {
                                        final MessageBox box = MessageBox.wait(null, MessagesFactory.getMessages().waiting_msg(),
                                                MessagesFactory.getMessages().waiting_desc());
                                        Timer t = new Timer() {

                                            @Override
                                            public void run() {
                                                box.close();
                                            }
                                        };
                                        t.schedule(600000);
                                        service.runProcess(str, new SessionAwareAsyncCallback<String>() {

                                            @Override
                                            protected void doOnFailure(Throwable caught) {
                                                box.close();
                                                MessageBox.alert(MessagesFactory.getMessages().run_status(), MessagesFactory
                                                        .getMessages().run_fail(), null);

                                            }

                                            @Override
                                            public void onSuccess(final String result) {
                                                box.close();
                                                MessageBox.alert(MessagesFactory.getMessages().run_status(), MessagesFactory
                                                        .getMessages().run_done(), new Listener<MessageBoxEvent>() {

                                                    @Override
                                                    public void handleEvent(MessageBoxEvent be) {
                                                        if (result.length() > 0) {
                                                            ((MainFramePanel) portal).openWindow(result);
                                                        }
                                                    }
                                                });
                                            }

                                        });
                                    }
                                });
                            }

                        });
                        set.add(processHtml);
                        set.layout(true);
                    }
                }
            }

        });

    }

    @Override
    public void setHeading() {
        this.setHeading(MessagesFactory.getMessages().process_title());
    }

    @Override
    public void setIcon() {
        this.setIcon(AbstractImagePrototype.create(Icons.INSTANCE.transformer()));

    }

}
