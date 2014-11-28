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

import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.custom.Portal;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.HTML;

public class ProcessPortlet extends BasePortlet {

    private List<String> data;

    public ProcessPortlet(Portal portal) {
        super(WelcomePortal.PROCESS, portal);

        initConfigSettings();

        label.setText(MessagesFactory.getMessages().process_desc());

        initLinks();
    }

    @Override
    public void refresh() {
        updateLinks();
    }

    private void initLinks() {

        service.getStandaloneProcess(UrlUtil.getLanguage(), new SessionAwareAsyncCallback<List<String>>() {

            @Override
            public void onSuccess(List<String> list) {
                data = list;

                buildLinks(list);

                autoRefresh(configModel.isAutoRefresh());
            }

        });

    }

    private void buildLinks(List<String> list) {
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
                sb.append("<IMG SRC=\"secure/img/genericUI/runnable_bullet.png\"/>&nbsp;"); //$NON-NLS-1$
                sb.append(strDesc.replace("Runnable#", "")); //$NON-NLS-1$ //$NON-NLS-2$
                sb.append("</span>"); //$NON-NLS-1$
                processHtml.setHTML(sb.toString());
                processHtml.addClickHandler(new ClickHandler() {

                    @Override
                    public void onClick(ClickEvent arg0) {
                        service.isExpired(UrlUtil.getLanguage(), new SessionAwareAsyncCallback<Boolean>() {

                            @Override
                            public void onSuccess(Boolean result1) {
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
                                    public void onSuccess(final String result2) {
                                        box.close();
                                        MessageBox.alert(MessagesFactory.getMessages().run_status(), MessagesFactory
                                                .getMessages().run_done(), new Listener<MessageBoxEvent>() {

                                            @Override
                                            public void handleEvent(MessageBoxEvent be) {
                                                if (result2.length() > 0) {
                                                    ((MainFramePanel) portal).openWindow(result2);
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

    private void updateLinks() {

        service.getStandaloneProcess(UrlUtil.getLanguage(), new SessionAwareAsyncCallback<List<String>>() {

            @Override
            public void onSuccess(List<String> list) {
                if (isDataDifferentFrom(list)) {
                    data = list;
                    set.removeAll();
                    buildLinks(list);
                }
            }

        });

    }

    protected boolean isDataDifferentFrom(List<String> list) {
        if (data.size() != list.size()) {
            return true;
        } else {
            int i = 0;
            for (String action : data) {
                if (!action.equals(list.get(i++))) {
                    return true;
                }
            }
        }
        return false;
    }
}
