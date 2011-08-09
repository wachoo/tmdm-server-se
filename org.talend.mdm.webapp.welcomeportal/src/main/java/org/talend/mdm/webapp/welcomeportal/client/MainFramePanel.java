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

import org.talend.mdm.webapp.welcomeportal.client.Util.UrlUtil;
import org.talend.mdm.webapp.welcomeportal.client.i18n.MessagesFactory;
import org.talend.mdm.webapp.welcomeportal.client.resources.icon.Icons;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.IconButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.widget.HorizontalPanel;
import com.extjs.gxt.ui.client.widget.Label;
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
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.HTML;


/**
 * DOC Administrator  class global comment. Detailled comment
 */
public class MainFramePanel extends Portal {

    private static MainFramePanel instance;

    private WelcomePortalServiceAsync service = (WelcomePortalServiceAsync) Registry.get(WelcomePortal.WELCOMEPORTAL_SERVICE);

    public static MainFramePanel getInstance() {
        if (instance == null)
            instance = new MainFramePanel(2);
        return instance;
    }

    private MainFramePanel(int numColumns) {
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

    private void initStartPortlet() {
        String name = "start"; //$NON-NLS-1$
        Portlet start = configPortlet(name);
        start.setIcon(AbstractImagePrototype.create(Icons.INSTANCE.start()));
        start.setHeading(MessagesFactory.getMessages().start_title());

        ((Label) start.getItemByItemId(name + "Label")).setText(MessagesFactory.getMessages().useful_links_desc()); //$NON-NLS-1$

        FieldSet set = (FieldSet) start.getItemByItemId(name + "Set"); //$NON-NLS-1$
        set.setHeading(MessagesFactory.getMessages().useful_links());
        StringBuilder sb1 = new StringBuilder(
                "<span id=\"ItemsBrowser\" style=\"padding-right:8px;cursor: pointer; width:150;\" title=\"" + MessagesFactory.getMessages().browse_items() + "\""); //$NON-NLS-1$ //$NON-NLS-2$
        sb1.append("<IMG SRC=\"/talendmdm/secure/img/menu/browse.png\"/>&nbsp;"); //$NON-NLS-1$
        sb1.append(MessagesFactory.getMessages().browse_items());
        sb1.append("</span>"); //$NON-NLS-1$
        HTML browseHtml = new HTML(sb1.toString());
        browseHtml.addClickHandler(new ClickHandler() {

            public void onClick(ClickEvent event) {
                initUI(WelcomePortal.BROWSECONTEXT, WelcomePortal.BROWSEAPP);
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
                initUI(WelcomePortal.JOURNALCONTEXT, WelcomePortal.JOURNALAPP);
            }

        });
        set.add(journalHtml);

        this.add(start, 0);
    }

    private void initAlertPortlet() {

        service.isHiddenLicense(new AsyncCallback<Boolean>() {

            public void onFailure(Throwable caught) {
                Dispatcher.forwardEvent(WelcomePortalEvents.Error, caught);
            }

            public void onSuccess(Boolean hidden) {
                if (!hidden) {
                    String name = "alert"; //$NON-NLS-1$
                    Portlet alert = configPortlet(name);
                    alert.setHeading(MessagesFactory.getMessages().alerts_title());
                    alert.setIcon(AbstractImagePrototype.create(Icons.INSTANCE.alert()));

                    final Label label = (Label) alert.getItemByItemId(name + "Label"); //$NON-NLS-1$
                    label.setText(MessagesFactory.getMessages().loading_alert_msg());

                    final FieldSet set = (FieldSet) alert.getItemByItemId(name + "Set"); //$NON-NLS-1$
                    set.setHeading(MessagesFactory.getMessages().alerts_title());
                    final HTML alertHtml = new HTML();
                    final StringBuilder sb = new StringBuilder(
                            "<span id=\"licenseAlert\" style=\"padding-right:8px;cursor: pointer;\" title=\"" + MessagesFactory.getMessages().alerts_title() + "\""); //$NON-NLS-1$ //$NON-NLS-2$

                    service.getAlertMsg(UrlUtil.getLanguage(), new AsyncCallback<String>() {

                        public void onFailure(Throwable caught) {
                            Dispatcher.forwardEvent(WelcomePortalEvents.Error, caught);
                        }

                        public void onSuccess(String msg) {
                            if (msg == null) {
                                label.setText(MessagesFactory.getMessages().no_alerts());
                                set.setVisible(false);
                            } else {
                                if (msg.equals(MessagesFactory.getMessages().no_license_msg())) {
                                    String noStr = "<IMG SRC=\"/talendmdm/secure/img/genericUI/alert-icon.png\"/>&nbsp;" //$NON-NLS-1$
                                        + MessagesFactory.getMessages().no_license_msg();
                                    sb.append(noStr);
                                } else if (msg.equals(MessagesFactory.getMessages().license_expired_msg())) {
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
                    MainFramePanel.this.add(alert, 0);
                }
            }

        });
    }

    private void initTaskPortlet() {
        service.isHiddenTask(new AsyncCallback<Boolean>() {

            public void onFailure(Throwable caught) {
                Dispatcher.forwardEvent(WelcomePortalEvents.Error, caught);
            }

            public void onSuccess(Boolean hidden) {
                if (!hidden) {
                    String name = "task"; //$NON-NLS-1$
                    Portlet task = configPortlet(name);
                    task.setHeading(MessagesFactory.getMessages().tasks_title());
                    task.setIcon(AbstractImagePrototype.create(Icons.INSTANCE.task()));

                    final Label label = (Label) task.getItemByItemId(name + "Label"); //$NON-NLS-1$
                    label.setText(MessagesFactory.getMessages().loading_task_msg());

                    final FieldSet set = (FieldSet) task.getItemByItemId(name + "Set"); //$NON-NLS-1$
                    set.setHeading(MessagesFactory.getMessages().tasks_title());
                    final HTML taskHtml = new HTML();
                    final StringBuilder sb = new StringBuilder(
                            "<span id=\"workflowtasks\" style=\"padding-right:8px;cursor: pointer;\" title=\"" + MessagesFactory.getMessages().tasks_title() + "\""); //$NON-NLS-1$ //$NON-NLS-2$
                    service.getTaskMsg(new AsyncCallback<Integer>() {

                        public void onFailure(Throwable caught) {
                            Dispatcher.forwardEvent(WelcomePortalEvents.Error, caught);
                        }

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
                            initUI(WelcomePortal.TASKCONTEXT, WelcomePortal.TASKAPP);
                        }

                    });
                    set.add(taskHtml);
                    MainFramePanel.this.add(task, 1);
                }
            }

        });
    }

    private void initProcessPortlet() {
        String name = "process"; //$NON-NLS-1$
        Portlet process = configPortlet(name);
        process.setHeading(MessagesFactory.getMessages().process_title());
        process.setIcon(AbstractImagePrototype.create(Icons.INSTANCE.transformer()));

        final Label label = (Label) process.getItemByItemId(name + "Label"); //$NON-NLS-1$
        label.setText(MessagesFactory.getMessages().process_desc());

        final FieldSet set = (FieldSet) process.getItemByItemId(name + "Set"); //$NON-NLS-1$
        set.setHeading(MessagesFactory.getMessages().process_title());
        service.getStandaloneProcess(new AsyncCallback<List<String>>() {

            public void onFailure(Throwable caught) {
                Dispatcher.forwardEvent(WelcomePortalEvents.Error, caught);
            }

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

                            public void componentSelected(ButtonEvent ce) {
                                final MessageBox box = MessageBox.wait(null, MessagesFactory.getMessages().waiting_msg(),
                                        MessagesFactory.getMessages().waiting_desc());                                
                                Timer t = new Timer() {

                                    public void run() {
                                        box.close();
                                    }
                                };
                                t.schedule(600000);
                                service.runProcess(str, new AsyncCallback<String>() {

                                    public void onFailure(Throwable caught) {
                                        Dispatcher.forwardEvent(WelcomePortalEvents.Error, caught);
                                        box.close();
                                    }

                                    public void onSuccess(String result) {
                                        if (result.indexOf("ok") >= 0) { //$NON-NLS-1$
                                            MessageBox.alert(MessagesFactory.getMessages().run_status(), MessagesFactory
                                                    .getMessages().run_done(), null);
                                            
                                            if (result.length() > 2) {
                                                String url = result.substring(2);
                                                openWindow(url);
                                            }

                                         }else{
                                            MessageBox.alert(MessagesFactory.getMessages().run_status(), MessagesFactory
                                                    .getMessages().run_fail(), null);
                                            box.close();
                                         }
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
                    }
                }
            }

        });

        this.add(process, 1);
    }

    private Portlet configPortlet(String name) {
        final Portlet port = new Portlet();
        port.setLayout(new FitLayout());
        port.setStyleAttribute("padding", "5px"); //$NON-NLS-1$ //$NON-NLS-2$
        port.setCollapsible(true);
        port.setAnimCollapse(false);
        port.getHeader().addTool(new ToolButton("x-tool-gear")); //$NON-NLS-1$
        port.getHeader().addTool(new ToolButton("x-tool-close", new SelectionListener<IconButtonEvent>() { //$NON-NLS-1$

            public void componentSelected(IconButtonEvent ce) {
                port.removeFromParent();
            }

        }));

        Label label = new Label();
        label.setItemId(name + "Label"); //$NON-NLS-1$
        label.setStyleAttribute("font-weight", "bold"); //$NON-NLS-1$ //$NON-NLS-2$
        port.add(label);

        FieldSet set = new FieldSet();
        set.setItemId(name + "Set"); //$NON-NLS-1$
        set.setStyleAttribute("padding", "5px"); //$NON-NLS-1$ //$NON-NLS-2$
        set.setStyleAttribute("margin-left", "10px");//$NON-NLS-1$ //$NON-NLS-2$
        set.setStyleAttribute("margin-right", "10px");//$NON-NLS-1$ //$NON-NLS-2$

        port.add(set);
        return port;
    }

    private native void openWindow(String url)/*-{
        window.open(url);
    }-*/;

    private native void initUI(String context, String application)/*-{
        var initFunction = $wnd.amalto[context][application].init();
        setTimeout(initFunction,'50');
    }-*/;
}
