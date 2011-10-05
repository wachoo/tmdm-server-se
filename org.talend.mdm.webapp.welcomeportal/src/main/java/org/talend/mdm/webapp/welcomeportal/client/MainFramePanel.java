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
package org.talend.mdm.webapp.welcomeportal.client;

import java.util.List;

import org.talend.mdm.webapp.base.client.SessionAwareAsyncCallback;
import org.talend.mdm.webapp.base.client.util.UrlUtil;
import org.talend.mdm.webapp.welcomeportal.client.i18n.MessagesFactory;
import org.talend.mdm.webapp.welcomeportal.client.resources.icon.Icons;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.IconButtonEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.HorizontalPanel;
import com.extjs.gxt.ui.client.widget.Label;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.button.ToolButton;
import com.extjs.gxt.ui.client.widget.custom.Portal;
import com.extjs.gxt.ui.client.widget.custom.Portlet;
import com.extjs.gxt.ui.client.widget.form.FieldSet;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.HTML;

/**
 * DOC Administrator class global comment. Detailled comment
 */
public class MainFramePanel extends Portal {

    private WelcomePortalServiceAsync service = (WelcomePortalServiceAsync) Registry.get(WelcomePortal.WELCOMEPORTAL_SERVICE);

    public MainFramePanel(int numColumns) {
        super(numColumns);
        setBorders(true);
        setStyleAttribute("backgroundColor", "white"); //$NON-NLS-1$ //$NON-NLS-2$
        setColumnWidth(0, .5);
        setColumnWidth(1, .5);

        initStartPortlet();
        initAlertPortlet();
        initTaskPortlet();
        initProcessPortlet();
    }

    private void itemClick(final String context, final String application) {
        service.isExpired(new SessionAwareAsyncCallback<Boolean>() {

            public void onSuccess(Boolean result) {
                initUI(context, application);
            }
        });
    }

    private void initStartPortlet() {
        String name = WelcomePortal.START;
        Portlet start = configPortlet(name);
        start.setIcon(AbstractImagePrototype.create(Icons.INSTANCE.start()));
        start.setHeading(MessagesFactory.getMessages().start_title());
        ((Label) start.getItemByItemId(name + "Label")).setText(MessagesFactory.getMessages().useful_links_desc()); //$NON-NLS-1$

        applyStartPortlet(start);
        this.add(start, 0);
    }

    private void applyStartPortlet(Portlet start) {
        FieldSet set = (FieldSet) start.getItemByItemId(WelcomePortal.START + "Set"); //$NON-NLS-1$
        set.setHeading(MessagesFactory.getMessages().useful_links());
        set.removeAll();
        StringBuilder sb1 = new StringBuilder(
                "<span id=\"ItemsBrowser\" style=\"padding-right:8px;cursor: pointer; width:150;\" title=\"" + MessagesFactory.getMessages().browse_items() + "\""); //$NON-NLS-1$ //$NON-NLS-2$
        sb1.append("<IMG SRC=\"/talendmdm/secure/img/menu/browse.png\"/>&nbsp;"); //$NON-NLS-1$
        sb1.append(MessagesFactory.getMessages().browse_items());
        sb1.append("</span>"); //$NON-NLS-1$
        HTML browseHtml = new HTML(sb1.toString());
        browseHtml.addClickHandler(new ClickHandler() {

            public void onClick(ClickEvent event) {
                itemClick(WelcomePortal.BROWSECONTEXT, WelcomePortal.BROWSEAPP);
            }

        });
        set.add(browseHtml);
        StringBuilder sb2 = new StringBuilder(
                "<span id=\"Journal\" style=\"padding-right:8px;cursor: pointer; width:150;\" title=\"" + MessagesFactory.getMessages().journal() + "\""); //$NON-NLS-1$ //$NON-NLS-2$
        sb2.append("<IMG SRC=\"/talendmdm/secure/img/menu/updatereport.png\"/>&nbsp;"); //$NON-NLS-1$
        sb2.append(MessagesFactory.getMessages().journal());
        sb2.append("</span>"); //$NON-NLS-1$
        HTML journalHtml = new HTML(sb2.toString());
        journalHtml.addClickHandler(new ClickHandler() {

            public void onClick(ClickEvent event) {
                itemClick(WelcomePortal.JOURNALCONTEXT, WelcomePortal.JOURNALAPP);
            }

        });

        set.add(journalHtml);
        set.layout(true);
    }

    private void initAlertPortlet() {

        service.isHiddenLicense(new SessionAwareAsyncCallback<Boolean>() {

            public void onSuccess(Boolean hidden) {
                if (!hidden) {
                    String name = WelcomePortal.ALERT;
                    Portlet alert = configPortlet(name);
                    alert.setHeading(MessagesFactory.getMessages().alerts_title());
                    alert.setIcon(AbstractImagePrototype.create(Icons.INSTANCE.alert()));

                    applyAlertPortlet(alert);
                    MainFramePanel.this.add(alert, 0);
                }
            }

        });
    }

    private void applyAlertPortlet(Portlet alert) {
        final Label label = (Label) alert.getItemByItemId(WelcomePortal.ALERT + "Label"); //$NON-NLS-1$
        label.setText(MessagesFactory.getMessages().loading_alert_msg());

        final FieldSet set = (FieldSet) alert.getItemByItemId(WelcomePortal.ALERT + "Set"); //$NON-NLS-1$
        set.setHeading(MessagesFactory.getMessages().alerts_title());
        set.removeAll();
        final HTML alertHtml = new HTML();
        final StringBuilder sb = new StringBuilder(
                "<span id=\"licenseAlert\" style=\"padding-right:8px;cursor: pointer;\" title=\"" + MessagesFactory.getMessages().alerts_title() + "\""); //$NON-NLS-1$ //$NON-NLS-2$

        service.getAlertMsg(UrlUtil.getLanguage(), new SessionAwareAsyncCallback<String>() {

            public void onSuccess(String msg) {
                if (msg == null) {
                    label.setText(MessagesFactory.getMessages().no_alerts());
                    set.setVisible(false);
                } else {
                    if (msg.equals(WelcomePortal.NOLICENSE)) {
                        String noStr = "<IMG SRC=\"/talendmdm/secure/img/genericUI/alert-icon.png\"/>&nbsp;" //$NON-NLS-1$
                                + MessagesFactory.getMessages().no_license_msg();
                        sb.append(noStr);
                    } else if (msg.equals(WelcomePortal.EXPIREDLICENSE)) {
                        String expiredStr = "<IMG SRC=\"/talendmdm/secure/img/genericUI/alert-icon.png\"/>&nbsp;" //$NON-NLS-1$
                                + MessagesFactory.getMessages().license_expired_msg();
                        sb.append(expiredStr);
                    } else {
                        // error msg
                        sb.append(msg);
                    }
                    label.setText(MessagesFactory.getMessages().alerts_desc());
                }
                sb.append("</span>"); //$NON-NLS-1$
                alertHtml.setHTML(sb.toString());
            }

        });
        alertHtml.addClickHandler(new ClickHandler() {

            public void onClick(ClickEvent event) {
                initUI(WelcomePortal.LICENSECONTEXT, WelcomePortal.LICENSEAPP);
            }

        });
        set.add(alertHtml);
        set.layout(true);
    }

    private void initTaskPortlet() {
        service.isHiddenTask(new SessionAwareAsyncCallback<Boolean>() {

            public void onSuccess(Boolean hidden) {
                if (!hidden) {
                    String name = WelcomePortal.TASK;
                    Portlet task = configPortlet(name);
                    task.setHeading(MessagesFactory.getMessages().tasks_title());
                    task.setIcon(AbstractImagePrototype.create(Icons.INSTANCE.task()));

                    applyTaskPortlet(task);
                    MainFramePanel.this.add(task, 1);
                }
            }
        });
    }

    private void applyTaskPortlet(Portlet task) {
        final Label label = (Label) task.getItemByItemId(WelcomePortal.TASK + "Label"); //$NON-NLS-1$
        label.setText(MessagesFactory.getMessages().loading_task_msg());

        final FieldSet set = (FieldSet) task.getItemByItemId(WelcomePortal.TASK + "Set"); //$NON-NLS-1$
        set.setHeading(MessagesFactory.getMessages().tasks_title());
        set.removeAll();
        final HTML taskHtml = new HTML();
        final StringBuilder sb = new StringBuilder(
                "<span id=\"workflowtasks\" style=\"padding-right:8px;cursor: pointer;\" title=\"" + MessagesFactory.getMessages().tasks_title() + "\""); //$NON-NLS-1$ //$NON-NLS-2$
        service.getTaskMsg(new SessionAwareAsyncCallback<Integer>() {

            public void onSuccess(Integer num) {
                if (num.equals(0)) {
                    label.setText(MessagesFactory.getMessages().no_tasks());
                    set.setVisible(false);
                } else {
                    StringBuilder sbNum = new StringBuilder(
                            "<IMG SRC=\"/talendmdm/secure/img/genericUI/task-list-icon.png\"/>&nbsp;"); //$NON-NLS-1$                                
                    sbNum.append(MessagesFactory.getMessages().waiting_task_prefix());
                    sbNum.append(num);
                    sbNum.append(MessagesFactory.getMessages().waiting_task_suffix());
                    sb.append(sbNum.toString());
                    label.setText(MessagesFactory.getMessages().tasks_desc());
                }
                sb.append("</span>"); //$NON-NLS-1$
                taskHtml.setHTML(sb.toString());
            }

        });
        taskHtml.addClickHandler(new ClickHandler() {

            public void onClick(ClickEvent event) {
                itemClick(WelcomePortal.TASKCONTEXT, WelcomePortal.TASKAPP);
            }

        });
        set.add(taskHtml);
        set.layout(true);
    }

    private void initProcessPortlet() {
        String name = WelcomePortal.PROCESS;
        Portlet process = configPortlet(name);
        process.setHeading(MessagesFactory.getMessages().process_title());
        process.setIcon(AbstractImagePrototype.create(Icons.INSTANCE.transformer()));

        applyProcessPortlet(process);
        this.add(process, 1);
    }

    private void applyProcessPortlet(Portlet process) {
        final Label label = (Label) process.getItemByItemId(WelcomePortal.PROCESS + "Label"); //$NON-NLS-1$
        label.setText(MessagesFactory.getMessages().process_desc());

        final FieldSet set = (FieldSet) process.getItemByItemId(WelcomePortal.PROCESS + "Set"); //$NON-NLS-1$
        set.setHeading(MessagesFactory.getMessages().process_title());
        set.removeAll();
        service.getStandaloneProcess(UrlUtil.getLanguage(), new SessionAwareAsyncCallback<List<String>>() {

            public void onSuccess(List<String> list) {
                if (list.isEmpty()) {
                    label.setText(MessagesFactory.getMessages().no_standalone_process());
                    set.setVisible(false);
                } else {
                    for (final String str : list) {
                        HorizontalPanel panel = new HorizontalPanel();
                        Button btn = new Button();
                        btn.setIcon(AbstractImagePrototype.create(Icons.INSTANCE.launch()));
                        btn.setId(str + "Btn"); //$NON-NLS-1$                        
                        btn.addListener(Events.Select, new SelectionListener<ButtonEvent>() {

                            @Override
                            public void componentSelected(ButtonEvent ce) {
                                service.isExpired(new SessionAwareAsyncCallback<Boolean>() {

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

                                            public void onSuccess(final String result) {
                                                box.close();
                                                MessageBox.alert(MessagesFactory.getMessages().run_status(), MessagesFactory
                                                        .getMessages().run_done(), new Listener<MessageBoxEvent>() {

                                                    public void handleEvent(MessageBoxEvent be) {
                                                        if (result.length() > 0) {
                                                            openWindow(result);
                                                        }
                                                    }
                                                });
                                            }

                                        });
                                    }
                                });

                            }

                        });
                        panel.add(btn);
                        Label processLabel = new Label();
                        processLabel.setId(str + "label"); //$NON-NLS-1$
                        processLabel.setText(str.replace("Runnable#", "")); //$NON-NLS-1$ //$NON-NLS-2$
                        panel.add(processLabel);
                        set.add(panel);
                        set.layout(true);
                    }
                }
            }

        });
    }

    private Portlet configPortlet(final String name) {
        final Portlet port = new Portlet();
        port.setLayout(new FitLayout());
        port.setStyleAttribute("padding", "5px"); //$NON-NLS-1$ //$NON-NLS-2$
        port.setCollapsible(true);
        port.setAnimCollapse(false);
        port.setAutoHeight(true);
        port.setItemId(name + "Portlet"); //$NON-NLS-1$
        port.getHeader().addTool(new ToolButton("x-tool-refresh", new SelectionListener<IconButtonEvent>() { //$NON-NLS-1$

                    @Override
                    public void componentSelected(IconButtonEvent ce) {
                        Portlet selectedPortlet = getPortletById(name + "Portlet"); //$NON-NLS-1$
                        if (selectedPortlet != null) {
                            if (name.equals(WelcomePortal.START))
                                applyStartPortlet(selectedPortlet);
                            else if (name.equals(WelcomePortal.ALERT))
                                applyAlertPortlet(selectedPortlet);
                            else if (name.equals(WelcomePortal.TASK))
                                applyTaskPortlet(selectedPortlet);
                            else if (name.equals(WelcomePortal.PROCESS))
                                applyProcessPortlet(selectedPortlet);
                        }

                    }

                }));
        port.getHeader().addTool(new ToolButton("x-tool-close", new SelectionListener<IconButtonEvent>() { //$NON-NLS-1$

                    @Override
                    public void componentSelected(IconButtonEvent ce) {
                        port.removeFromParent();
                    }

                }));

        Label label = new Label();
        label.setItemId(name + "Label"); //$NON-NLS-1$
        label.setStyleAttribute("font-weight", "bold"); //$NON-NLS-1$ //$NON-NLS-2$
        label.setAutoHeight(true);
        port.add(label);

        FieldSet set = new FieldSet();
        set.setItemId(name + "Set"); //$NON-NLS-1$
        set.setStyleAttribute("padding", "5px"); //$NON-NLS-1$ //$NON-NLS-2$
        set.setStyleAttribute("margin-left", "10px");//$NON-NLS-1$ //$NON-NLS-2$
        set.setStyleAttribute("margin-right", "10px");//$NON-NLS-1$ //$NON-NLS-2$

        port.add(set);
        return port;
    }

    private Portlet getPortletById(String itemId) {
        for (LayoutContainer container : this.getItems()) {
            if (container.getItemByItemId(itemId) != null)
                return (Portlet) container.getItemByItemId(itemId);
        }
        return null;
    }

    private native void openWindow(String url)/*-{
        window.open(url);
    }-*/;

    private native void initUI(String context, String application)/*-{
        var initFunction = $wnd.amalto[context][application].init();
        setTimeout(initFunction,'50');
    }-*/;
}
