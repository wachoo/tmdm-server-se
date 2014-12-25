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

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.HTML;

public class AlertPortlet extends BasePortlet {

    private static String mesgPrefix = "<span id=\"licenseAlert\" style=\"padding-right:8px;cursor: pointer;\" class=\"labelStyle\" title=\"" //$NON-NLS-1$
            + MessagesFactory.getMessages().alerts_title() + "\">"; //$NON-NLS-1$

    private static String alertIcon = "<IMG SRC=\"/talendmdm/secure/img/genericUI/alert-icon.png\"/>&nbsp;"; //$NON-NLS-1$

    private String linkData;

    public AlertPortlet(MainFramePanel portal) {
        super(WelcomePortal.ALERT, portal);

        initConfigSettings();

        label.setText(MessagesFactory.getMessages().loading_alert_msg());

        initLinks();
    }

    @Override
    public void refresh() {
        updateLinks();

    }

    private void updateLinks() {

        final StringBuilder sb = new StringBuilder(mesgPrefix);

        service.getAlertMsg(UrlUtil.getLanguage(), new SessionAwareAsyncCallback<String>() {

            @Override
            public void onSuccess(String msg) {
                if (msg == null) {
                    service.getLicenseWarning(UrlUtil.getLanguage(), new SessionAwareAsyncCallback<String>() {

                        @Override
                        public void onSuccess(String message) {
                            if (!message.isEmpty()) {
                                sb.append(alertIcon);
                                sb.append(message);
                                label.setText(MessagesFactory.getMessages().alerts_desc());
                            } else {
                                label.setText(MessagesFactory.getMessages().no_alerts());
                                set.setVisible(false);
                            }
                            sb.append("</span>"); //$NON-NLS-1$

                            refreshWithNewData(sb.toString());
                        }

                    });
                } else {
                    if (msg.equals(WelcomePortal.NOLICENSE)) {
                        sb.append(alertIcon);
                        sb.append(MessagesFactory.getMessages().no_license_msg());
                    } else if (msg.equals(WelcomePortal.EXPIREDLICENSE)) {
                        sb.append(alertIcon);
                        sb.append(MessagesFactory.getMessages().license_expired_msg());
                    } else {
                        // error msg
                        sb.append(msg);
                    }
                    label.setText(MessagesFactory.getMessages().alerts_desc());
                    sb.append("</span>"); //$NON-NLS-1$

                    refreshWithNewData(sb.toString());
                }
            }
        });
    }

    private void initLinks() {

        final HTML alertHtml = new HTML();
        final StringBuilder sb = new StringBuilder(mesgPrefix);

        service.getAlertMsg(UrlUtil.getLanguage(), new SessionAwareAsyncCallback<String>() {

            @Override
            public void onSuccess(String msg) {
                if (msg == null) {
                    service.getLicenseWarning(UrlUtil.getLanguage(), new SessionAwareAsyncCallback<String>() {

                        @Override
                        public void onSuccess(String message) {
                            if (!message.isEmpty()) {
                                sb.append(alertIcon);
                                sb.append(message);
                                label.setText(MessagesFactory.getMessages().alerts_desc());
                            } else {
                                label.setText(MessagesFactory.getMessages().no_alerts());
                                set.setVisible(false);
                            }
                            sb.append("</span>"); //$NON-NLS-1$
                            linkData = sb.toString();
                            alertHtml.setHTML(linkData);
                        }
                    });
                } else {
                    if (msg.equals(WelcomePortal.NOLICENSE)) {
                        sb.append(alertIcon);
                        sb.append(MessagesFactory.getMessages().no_license_msg());
                    } else if (msg.equals(WelcomePortal.EXPIREDLICENSE)) {
                        sb.append(alertIcon);
                        sb.append(MessagesFactory.getMessages().license_expired_msg());
                    } else {
                        // error msg
                        sb.append(msg);
                    }
                    label.setText(MessagesFactory.getMessages().alerts_desc());
                    sb.append("</span>"); //$NON-NLS-1$
                    linkData = sb.toString();
                    alertHtml.setHTML(linkData);
                }

                alertHtml.addClickHandler(new ClickHandler() {

                    @Override
                    public void onClick(ClickEvent event) {
                        portal.initUI(WelcomePortal.LICENSECONTEXT, WelcomePortal.LICENSEAPP);
                    }

                });
                set.add(alertHtml);
                set.layout(true);
                autoRefresh(configModel.isAutoRefresh());
            }
        });

    }

    private void refreshWithNewData(String newData) {
        if (!linkData.equals(newData)) {
            linkData = newData;
            set.removeAll();

            HTML alertHtml = new HTML(linkData);
            alertHtml.addClickHandler(new ClickHandler() {

                @Override
                public void onClick(ClickEvent event) {
                    portal.initUI(WelcomePortal.LICENSECONTEXT, WelcomePortal.LICENSEAPP);
                }

            });
            set.add(alertHtml);
            set.layout(true);
        }
    }
}
