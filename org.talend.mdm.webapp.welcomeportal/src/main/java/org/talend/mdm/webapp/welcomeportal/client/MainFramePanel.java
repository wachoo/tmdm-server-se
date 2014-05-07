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
package org.talend.mdm.webapp.welcomeportal.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.talend.mdm.webapp.base.client.SessionAwareAsyncCallback;
import org.talend.mdm.webapp.base.client.util.UrlUtil;
import org.talend.mdm.webapp.welcomeportal.client.i18n.MessagesFactory;
import org.talend.mdm.webapp.welcomeportal.client.resources.icon.Icons;
import org.talend.mdm.webapp.welcomeportal.client.rest.StatisticsRestServiceHandler;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.IconButtonEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
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
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.TextBox;
import com.googlecode.gflot.client.DataPoint;
import com.googlecode.gflot.client.PieDataPoint;
import com.googlecode.gflot.client.PlotModel;
import com.googlecode.gflot.client.Series;
import com.googlecode.gflot.client.SeriesHandler;
import com.googlecode.gflot.client.SimplePlot;
import com.googlecode.gflot.client.options.AxesOptions;
import com.googlecode.gflot.client.options.BarSeriesOptions;
import com.googlecode.gflot.client.options.CategoriesAxisOptions;
import com.googlecode.gflot.client.options.GlobalSeriesOptions;
import com.googlecode.gflot.client.options.GridOptions;
import com.googlecode.gflot.client.options.LegendOptions;
import com.googlecode.gflot.client.options.LineSeriesOptions;
import com.googlecode.gflot.client.options.PieSeriesOptions;
import com.googlecode.gflot.client.options.PieSeriesOptions.Label.Background;
import com.googlecode.gflot.client.options.PieSeriesOptions.Label.Formatter;
import com.googlecode.gflot.client.options.PlotOptions;

/**
 * DOC Administrator class global comment. Detailled comment
 */
public class MainFramePanel extends Portal {

    private WelcomePortalServiceAsync service = (WelcomePortalServiceAsync) Registry.get(WelcomePortal.WELCOMEPORTAL_SERVICE);
    private boolean isHiddenWorkFlowTask = true;
    private boolean isHiddenDSCTask = true;
    private static String DSCTASKTYPE_NEW = "new"; //$NON-NLS-1$
    private static String DSCTASKTYPE_PENDING = "pending"; //$NON-NLS-1$
    private static String ROUTING_STATUS_FAILED = "failed"; //$NON-NLS-1$
    private static String ROUTING_STATUS_COMPLETED = "completed"; //$NON-NLS-1$
    private List<Portlet> charts = new ArrayList<Portlet>(4);
    
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
        initSearchPortlet();
        initChartPortlets();

    }

    public void refreshPortlets() {
        this.applyChartPortlets(charts);
        applyAlertPortlet(this.getPortletById(WelcomePortal.ALERT + "Portlet")); //$NON-NLS-1$
        applyTaskPortlet(this.getPortletById(WelcomePortal.WORKFLOW_TASK + "Portlet")); //$NON-NLS-1$
        applyProcessPortlet(this.getPortletById(WelcomePortal.PROCESS + "Portlet")); //$NON-NLS-1$
    }

    private void itemClick(final String context, final String application) {
        service.isExpired(UrlUtil.getLanguage(), new SessionAwareAsyncCallback<Boolean>() {

            @Override
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

    private void initSearchPortlet() {
        final MainFramePanel mainFramePanel = this;
        service.isEnterpriseVersion(new SessionAwareAsyncCallback<Boolean>() {

            @Override
            public void onSuccess(Boolean isEnterprise) {
                if (isEnterprise) { // This feature is MDM EE only.
                    String name = WelcomePortal.SEARCH;
                    Portlet searchPortlet = configPortlet(name);
                    searchPortlet.setIcon(AbstractImagePrototype.create(Icons.INSTANCE.find()));
                    searchPortlet.setHeading(MessagesFactory.getMessages().search_title());

                    FieldSet set = (FieldSet) searchPortlet.getItemByItemId(WelcomePortal.SEARCH + "Set"); //$NON-NLS-1$
                    set.setBorders(false);
                    set.removeAll();
                    Grid grid = new Grid(1, 2);
                    final TextBox textBox = new TextBox();
                    textBox.addKeyUpHandler(new KeyUpHandler() {

                        @Override
                        public void onKeyUp(KeyUpEvent keyUpEvent) {
                            if (keyUpEvent.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
                                doSearch(textBox);
                            }
                        }
                    });
                    grid.setWidget(0, 0, textBox);

                    Button button = new Button(MessagesFactory.getMessages().search_button_text());
                    button.addSelectionListener(new SelectionListener<ButtonEvent>() {

                        @Override
                        public void componentSelected(ButtonEvent buttonEvent) {
                            doSearch(textBox);
                        }
                    });
                    grid.setWidget(0, 1, button);
                    set.add(grid);

                    mainFramePanel.add(searchPortlet, 1);
                }
            }
        });
    }

    private void doSearch(TextBox textBox) {
        // TODO TMDM-2598 Temp code (how to pass a parameter to an application?).
        if (Cookies.isCookieEnabled()) {
            Cookies.setCookie("org.talend.mdm.search.query", textBox.getText()); //$NON-NLS-1$
        }
        itemClick("search", "Search"); //$NON-NLS-1$ //$NON-NLS-2$
    }

    private void applyStartPortlet(final Portlet start) {
        service.getMenuLabel(UrlUtil.getLanguage(), WelcomePortal.BROWSEAPP, new SessionAwareAsyncCallback<String>() {

            @Override
            public void onSuccess(String id) {
                FieldSet set = (FieldSet) start.getItemByItemId(WelcomePortal.START + "Set"); //$NON-NLS-1$                
                set.removeAll();
                StringBuilder sb1 = new StringBuilder(
                        "<span id=\"ItemsBrowser\" style=\"padding-right:8px;cursor: pointer; width:150;\" class=\"labelStyle\" title=\"" + MessagesFactory.getMessages().browse_items() + "\">"); //$NON-NLS-1$ //$NON-NLS-2$
                sb1.append("<IMG SRC=\"/talendmdm/secure/img/menu/browse.png\"/>&nbsp;"); //$NON-NLS-1$
                sb1.append(MessagesFactory.getMessages().browse_items());
                sb1.append("</span>"); //$NON-NLS-1$
                HTML browseHtml = new HTML(sb1.toString());
                browseHtml.addClickHandler(new ClickHandler() {

                    @Override
                    public void onClick(ClickEvent event) {
                        itemClick(WelcomePortal.BROWSECONTEXT, WelcomePortal.BROWSEAPP);
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
                        itemClick(WelcomePortal.JOURNALCONTEXT, WelcomePortal.JOURNALAPP);
                    }

                });

                set.add(journalHtml);
                set.layout(true);
            }
        });
    }

    private void initAlertPortlet() {

        service.isHiddenLicense(new SessionAwareAsyncCallback<Boolean>() {

            @Override
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
        set.removeAll();
        final HTML alertHtml = new HTML();
        final StringBuilder sb = new StringBuilder(
                "<span id=\"licenseAlert\" style=\"padding-right:8px;cursor: pointer;\" class=\"labelStyle\" title=\"" + MessagesFactory.getMessages().alerts_title() + "\">"); //$NON-NLS-1$ //$NON-NLS-2$
        final String alertIcon = "<IMG SRC=\"/talendmdm/secure/img/genericUI/alert-icon.png\"/>&nbsp;"; //$NON-NLS-1$

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
                            alertHtml.setHTML(sb.toString());
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
                    alertHtml.setHTML(sb.toString());
                }
            }
        });
        alertHtml.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                initUI(WelcomePortal.LICENSECONTEXT, WelcomePortal.LICENSEAPP);
            }

        });
        set.add(alertHtml);
        set.layout(true);
    }

    private void initTaskPortlet() {
        service.isHiddenWorkFlowTask(new SessionAwareAsyncCallback<Boolean>() {

            @Override
            public void onSuccess(Boolean hidden) {
                isHiddenWorkFlowTask = hidden;
                
                service.isHiddenDSCTask(new SessionAwareAsyncCallback<Boolean>() {

                    @Override
                    public void onSuccess(Boolean hidden) {
                        isHiddenDSCTask = hidden;
                        
                        if (!isHiddenWorkFlowTask || !isHiddenDSCTask) {
                            String name = WelcomePortal.TASKS;
                            Portlet task = configPortlet(name);
                            task.setHeading(MessagesFactory.getMessages().tasks_title());
                            task.setIcon(AbstractImagePrototype.create(Icons.INSTANCE.task()));
                            
                            applyTaskPortlet(task);
                            MainFramePanel.this.add(task, 1);
                        }
                    }
                });                
            }
        });
                
    }

    private void applyTaskPortlet(Portlet task) {

        final Label label = (Label) task.getItemByItemId(WelcomePortal.TASKS + "Label"); //$NON-NLS-1$
        label.setText(MessagesFactory.getMessages().loading_task_msg());

        final FieldSet set = (FieldSet) task.getItemByItemId(WelcomePortal.TASKS + "Set"); //$NON-NLS-1$        
        set.removeAll();
        
        final HTML taskHtml_workflow = new HTML();        

        final StringBuilder sbForWorkflowtasks = new StringBuilder(
                "<span id=\"workflowtasks\" style=\"padding-right:8px;cursor: pointer;\" class=\"labelStyle\" title=\"" + MessagesFactory.getMessages().tasks_title() + "\">"); //$NON-NLS-1$ //$NON-NLS-2$
        final StringBuilder sbForDsctasks = new StringBuilder(
                "<span id=\"dsctasks\" style=\"padding-right:8px;cursor: pointer;\" class=\"labelStyle\" title=\"" + MessagesFactory.getMessages().tasks_title() + "\">"); //$NON-NLS-1$ //$NON-NLS-2$
        final ClickHandler workflowClikcHanlder = new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                itemClick(WelcomePortal.WORKFLOW_TASKCONTEXT, WelcomePortal.WORKFLOW_TASKAPP);
            }

        };
        
        final ClickHandler dscClikcHanlder = new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                itemClick(WelcomePortal.DSC_TASKCONTEXT, WelcomePortal.DSC_TASKAPP);
            }

        };
        
        if (!isHiddenWorkFlowTask && isHiddenDSCTask) {
            service.getWorkflowTaskMsg(new SessionAwareAsyncCallback<Integer>() {

                @Override
                public void onSuccess(Integer num) {

                    if (num.equals(0)) {

                        label.setText(MessagesFactory.getMessages().no_tasks());
                        set.setVisible(false);
                    } else {
                        String sbNum = buildMessageForWorkflowTasks(num);
                        sbForWorkflowtasks.append(sbNum);
                        label.setText(MessagesFactory.getMessages().tasks_desc());
                        set.setVisible(true);
                    }
                    sbForWorkflowtasks.append("</span>"); //$NON-NLS-1$

                    taskHtml_workflow.setHTML(sbForWorkflowtasks.toString());
                }

            });
            taskHtml_workflow.addClickHandler(workflowClikcHanlder);
            set.add(taskHtml_workflow);
            set.layout(true);
        }

        final HTML taskHtml_dsc = new HTML();
        if (!isHiddenDSCTask && isHiddenWorkFlowTask) {
            service.getDSCTaskMsg(new SessionAwareAsyncCallback<Map<String, Integer>>() {

                @Override
                public void onSuccess(Map<String, Integer> dscTasks) {
                    int total = dscTasks.get(DSCTASKTYPE_NEW) + dscTasks.get(DSCTASKTYPE_PENDING);
                    if (total <= 0 ) {

                        label.setText(MessagesFactory.getMessages().no_tasks());
                        set.setVisible(false);
                    } else {
                        String sbNum = buildMessageForDSCTasks(dscTasks);
                        sbForDsctasks.append(sbNum);
                        label.setText(MessagesFactory.getMessages().tasks_desc());
                        set.setVisible(true);
                    }
                    sbForDsctasks.append("</span>"); //$NON-NLS-1$

                    taskHtml_dsc.setHTML(sbForDsctasks.toString());
                }

            });
            taskHtml_dsc.addClickHandler(dscClikcHanlder);
            
            set.add(taskHtml_dsc);
            set.layout(true);
        }
        
        if (!isHiddenWorkFlowTask && !isHiddenDSCTask) {
            service.getWorkflowTaskMsg(new SessionAwareAsyncCallback<Integer>() {

                @Override
                public void onSuccess(final Integer workflowNum) {
                    service.getDSCTaskMsg(new SessionAwareAsyncCallback<Map<String, Integer>>() {

                        @Override
                        public void onSuccess(Map<String, Integer> dscTasks) {
                            int dscNum = dscTasks.get(DSCTASKTYPE_NEW) + dscTasks.get(DSCTASKTYPE_PENDING);
                            if (workflowNum + dscNum <= 0 ) {
                                label.setText(MessagesFactory.getMessages().no_tasks());
                                set.setVisible(false);
                            } else if (workflowNum > 0 && dscNum == 0){
                                String sbNum = buildMessageForWorkflowTasks(workflowNum);
                                sbForWorkflowtasks.append(sbNum);
                                sbForWorkflowtasks.append("</span>"); //$NON-NLS-1$
                                label.setText(MessagesFactory.getMessages().tasks_desc());
                                set.setVisible(true);
                                
                                taskHtml_workflow.setHTML(sbForWorkflowtasks.toString());
                                
                                taskHtml_workflow.addClickHandler(workflowClikcHanlder);
                                
                                set.add(taskHtml_workflow);
                            } else if (workflowNum == 0 && dscNum > 0){
                                String sbNum = buildMessageForDSCTasks(dscTasks);
                                sbForDsctasks.append(sbNum);
                                sbForDsctasks.append("</span>"); //$NON-NLS-1$
                                label.setText(MessagesFactory.getMessages().tasks_desc());
                                set.setVisible(true);
                                
                                taskHtml_dsc.setHTML(sbForDsctasks.toString());
                                
                                taskHtml_dsc.addClickHandler(dscClikcHanlder);
                                
                                set.add(taskHtml_dsc);
                            } else {
                                String sbNumWFT = buildMessageForWorkflowTasks(workflowNum);
                                sbForWorkflowtasks.append(sbNumWFT);
                                sbForWorkflowtasks.append("</span>"); //$NON-NLS-1$
                                
                                String sbNumDSC = buildMessageForDSCTasks(dscTasks);
                                sbForDsctasks.append(sbNumDSC);
                                sbForDsctasks.append("</span>"); //$NON-NLS-1$
                                
                                taskHtml_workflow.setHTML(sbForWorkflowtasks.toString());
                                taskHtml_dsc.setHTML(sbForDsctasks.toString());
                                
                                taskHtml_workflow.addClickHandler(workflowClikcHanlder);
                                
                                taskHtml_dsc.addClickHandler(dscClikcHanlder);

                                set.add(taskHtml_workflow);
                                set.add(taskHtml_dsc);

                                label.setText(MessagesFactory.getMessages().tasks_desc());
                                set.setVisible(true);
                            }
                            set.layout(true);   
                        }
                    });
                }
            });
        }
                                                                        
    }

    private String buildMessageForWorkflowTasks(int num) {
        StringBuilder mseeage = new StringBuilder(
                "<IMG SRC=\"/talendmdm/secure/img/genericUI/task-list-icon.png\"/>&nbsp;"); //$NON-NLS-1$                                
        mseeage.append(MessagesFactory.getMessages().waiting_task_prefix());
        mseeage.append("&nbsp;<b style=\"color: red;\">" + num + "</b>&nbsp;"); //$NON-NLS-1$ //$NON-NLS-2$
        mseeage.append(MessagesFactory.getMessages().waiting_workflowtask_suffix());
        
        return mseeage.toString();
    }
    
    private String buildMessageForDSCTasks(Map<String, Integer> dscTasks) {
        StringBuilder message = new StringBuilder(
                "<IMG SRC=\"/talendmdm/secure/img/genericUI/task-list-icon.png\"/>&nbsp;"); //$NON-NLS-1$                                
        message.append(MessagesFactory.getMessages().waiting_task_prefix());
        message.append("&nbsp;<b style=\"color: red;\">" + dscTasks.get(DSCTASKTYPE_NEW) + "&nbsp;new</b>&nbsp;and"); //$NON-NLS-1$ //$NON-NLS-2$
        message.append("&nbsp;<b style=\"color: red;\">" + dscTasks.get(DSCTASKTYPE_PENDING) + "&nbsp;pending</b>&nbsp;"); //$NON-NLS-1$ //$NON-NLS-2$
        message.append(MessagesFactory.getMessages().waiting_dsctask_suffix());

        return message.toString();
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
                        set.add(processHtml);
                        set.layout(true);
                    }
                }
            }

        });
    }

    private void initChartPortlets() {
        Map<String, String> chart_titles = new HashMap<String, String>(4);
        chart_titles.put(WelcomePortal.CHART_DATA, MessagesFactory.getMessages().chart_data_title());
        chart_titles.put(WelcomePortal.CHART_JOURNAL, MessagesFactory.getMessages().chart_journal_title());
        chart_titles.put(WelcomePortal.CHART_ROUTING_EVENT, MessagesFactory.getMessages().chart_routing_event_title());
        
        Portlet chart;
        Set<String> chartNames = chart_titles.keySet();
        for (Iterator<String> iterator= chartNames.iterator(); iterator.hasNext(); ) {
            String name = iterator.next();
            chart = configPortlet(name);
            chart.setHeading(chart_titles.get(name));
            chart.setIcon(AbstractImagePrototype.create(Icons.INSTANCE.chart()));
            if (chart.getItemId().startsWith(WelcomePortal.CHART_DATA)) {
                applyChartDATAPortlet(chart);
            } else if (chart.getItemId().startsWith(WelcomePortal.CHART_JOURNAL)) {
                applyChartJournalPortlet(chart);
            } else if (chart.getItemId().startsWith(WelcomePortal.CHART_ROUTING_EVENT)) {
                applyChartRoutingEventPortlet(chart);
            }
            charts.add(chart);
            this.add(chart, 0);
        }
    }

    private void applyChartPortlets(List<Portlet> charts) {
        for (Portlet chart : charts) {
            if (chart.getItemId().startsWith(WelcomePortal.CHART_DATA) ) {
                applyChartDATAPortlet(chart);
            } else if (chart.getItemId().startsWith(WelcomePortal.CHART_JOURNAL)) {
                applyChartJournalPortlet(chart);
            } else if (chart.getItemId().startsWith(WelcomePortal.CHART_ROUTING_EVENT)) {
                applyChartRoutingEventPortlet(chart);
            }
        }
    }
    
    private void applyChartDATAPortlet(Portlet chart) {
        final FieldSet set = (FieldSet) chart.getItemByItemId(WelcomePortal.CHART_DATA + "Set"); //$NON-NLS-1$        
        set.removeAll();
        service.getCurrentDataContainer(new SessionAwareAsyncCallback<String>() {

            @Override
            public void onSuccess(String dataContainer) {

                StatisticsRestServiceHandler.getInstance().getContainerDataStats(dataContainer,
                        new SessionAwareAsyncCallback<JSONArray>() {

                            @Override
                            public void onSuccess(JSONArray jsonArray) {
                                set.add(createEntityPlot(jsonArray));
                                set.layout(true);
                            }
                        });
            }
        });

    }

    private void applyChartJournalPortlet(Portlet chart) {
        final FieldSet set = (FieldSet) chart.getItemByItemId(WelcomePortal.CHART_JOURNAL + "Set"); //$NON-NLS-1$        
        set.removeAll();
        service.getCurrentDataContainer(new SessionAwareAsyncCallback<String>() {

            @Override
            public void onSuccess(String dataContainer) {

                StatisticsRestServiceHandler.getInstance().getContainerJournalStats(dataContainer,
                        new SessionAwareAsyncCallback<JSONArray>() {

                            @Override
                            public void onSuccess(JSONArray jsonArray) {
                                set.add(createJournalPlot(jsonArray));
                                set.layout(true);
                            }
                        });
            }
        });
    }
    
    private void applyChartRoutingEventPortlet(Portlet chart) {
        final FieldSet set = (FieldSet) chart.getItemByItemId(WelcomePortal.CHART_ROUTING_EVENT + "Set"); //$NON-NLS-1$        
        set.removeAll();
        StatisticsRestServiceHandler.getInstance().getRoutingEventStats(
                new SessionAwareAsyncCallback<JSONArray>() {

                    @Override
                    public void onSuccess(JSONArray jsonArray) {
                        set.add(createRoutingEventPlot(jsonArray));
                        set.layout(true);
                    }
                });
    }
    
    private SimplePlot createRoutingEventPlot(JSONArray jsonArray) {
        
        //prepare data
        Map<String, Map<String, Integer>> routingData = new HashMap<String, Map<String, Integer>>();
        assert (jsonArray.size() == 2);
        JSONObject failedJSONObj;
        JSONObject completedJSONObj;
        JSONArray failures;
        JSONArray completes;
        failedJSONObj = (JSONObject) jsonArray.get(0);
        completedJSONObj = (JSONObject) jsonArray.get(1);
        String currApp;
        Set<String> appNames = new HashSet<String>();
        
        
        failures = failedJSONObj.get(ROUTING_STATUS_FAILED).isArray();
        JSONObject curFailure;
        Map<String, Integer> failureMap = new HashMap<String, Integer>();
        int numOfFailed = 0;
        for (int i = 0; i < failures.size(); i++) {
            curFailure = failures.get(i).isObject();
            currApp = curFailure.keySet().iterator().next();
            numOfFailed = (int) curFailure.get(currApp).isNumber().getValue();
            failureMap.put(currApp, numOfFailed);
            appNames.add(currApp);
        }
        
        completes = completedJSONObj.get(ROUTING_STATUS_COMPLETED).isArray();
        JSONObject curComplete;
        int numOfCompleted = 0;
        Map<String, Integer> completesMap = new HashMap<String, Integer>();
        for (int i = 0; i < completes.size(); i++) {
            curComplete = completes.get(i).isObject();
            currApp = curComplete.keySet().iterator().next();
            numOfCompleted = (int) curComplete.get(currApp).isNumber().getValue();
            completesMap.put(currApp, numOfCompleted);
            appNames.add(currApp);
        }        
 
        Map<String, Integer> status;
        for (String appName : appNames) {
            status = new HashMap<String, Integer>(2);
            status.put(ROUTING_STATUS_FAILED, !failureMap.containsKey(appName)? 0 : failureMap.get(appName));
            status.put(ROUTING_STATUS_COMPLETED, !completesMap.containsKey(appName)? 0 : completesMap.get(appName));
            routingData.put(appName, status);
        }
        
        PlotModel model = new PlotModel();
        PlotOptions plotOptions = PlotOptions.create();
        plotOptions.setGlobalSeriesOptions(
                GlobalSeriesOptions.create().setLineSeriesOptions(LineSeriesOptions.create().setShow(false).setFill(true))
                .setBarsSeriesOptions(BarSeriesOptions.create().setShow(true).setBarWidth(0.6)).setStack(true))
                .setXAxesOptions(
                        AxesOptions.create().addAxisOptions(
                                CategoriesAxisOptions.create().setCategories(appNames.toArray(new String[appNames.size()]))));
        plotOptions.setLegendOptions(LegendOptions.create().setShow(true));

        // create series
        SeriesHandler series1 = model.addSeries(Series.of("Completed")); //$NON-NLS-1$
        SeriesHandler series2 = model.addSeries(Series.of("Failed")); //$NON-NLS-1$

        // add data
        for (String appName : appNames) {
            series1.add(DataPoint.of(appName, routingData.get(appName).get(ROUTING_STATUS_COMPLETED)));
            series2.add(DataPoint.of(appName, routingData.get(appName).get(ROUTING_STATUS_FAILED)));
        }

        // create the plot
        SimplePlot plot = new SimplePlot(model, plotOptions);
        plot.setWidth(400);
        plot.setHeight(300);
        return plot;

    }
    
    private SimplePlot createJournalPlot(JSONArray jsonArray) {
        //prepare data
        int numOfEntities = jsonArray.size();
        JSONObject currentJSONObj;
        JSONArray events;
        JSONObject creations;
        int lengthOfCreates;
        JSONObject updates;
        int lengthOfUpdates;
        List<String> entities = new ArrayList<String>(numOfEntities);
        Map<String, Map<String, Integer>> journalData = new HashMap<String, Map<String, Integer>>(numOfEntities);
        for (int i = 0; i < numOfEntities; i++) {
            currentJSONObj = (JSONObject) jsonArray.get(i);
            Set<String> entityNames = currentJSONObj.keySet();
            String entityName = entityNames.iterator().next();
            entities.add(entityName);
            events = currentJSONObj.get(entityName).isArray();
            creations = events.get(0).isObject();
            updates = events.get(1).isObject();
            lengthOfCreates = creations.size();
            lengthOfUpdates = updates.size();

            Map<String, Integer> eventMap = new HashMap<String, Integer>(2);
            int numOfUpdates = 0;
            int numOfCreates = 0;
            for (int j = 0; j < lengthOfCreates; j++) {
                JSONArray createArray = creations.get("creations").isArray(); //$NON-NLS-1$
                JSONObject curCreate;
                for (int k = 0; k < createArray.size(); k++) {
                    curCreate = createArray.get(k).isObject();
                    numOfCreates += (int) curCreate.get("create").isNumber().getValue(); //$NON-NLS-1$
                }
            }
            
            for (int j = 0; j < lengthOfUpdates; j++) {
                JSONArray updateArray = updates.get("updates").isArray(); //$NON-NLS-1$
                JSONObject curUpdate;
                for (int k = 0; k < updateArray.size(); k++) {
                    curUpdate = updateArray.get(k).isObject();
                    numOfCreates += (int) curUpdate.get("update").isNumber().getValue(); //$NON-NLS-1$
                }
            }

            eventMap.put("create", numOfCreates); //$NON-NLS-1$
            eventMap.put("update", numOfUpdates); //$NON-NLS-1$
            journalData.put(entityName, eventMap);
        }

        PlotModel model = new PlotModel();
        PlotOptions plotOptions = PlotOptions.create();
        plotOptions.setGlobalSeriesOptions(
                GlobalSeriesOptions.create().setLineSeriesOptions(LineSeriesOptions.create().setShow(false).setFill(true))
                .setBarsSeriesOptions(BarSeriesOptions.create().setShow(true).setBarWidth(0.6)).setStack(true))
                .setXAxesOptions(
                        AxesOptions.create().addAxisOptions(
                                CategoriesAxisOptions.create().setCategories(entities.toArray(new String[entities.size()]))));
        plotOptions.setLegendOptions(LegendOptions.create().setShow(true));

        // create series
        SeriesHandler series1 = model.addSeries(Series.of("Creation")); //$NON-NLS-1$
        SeriesHandler series2 = model.addSeries(Series.of("Update")); //$NON-NLS-1$

        // add data
        for (String entityName : entities) {
            series1.add(DataPoint.of(entityName, journalData.get(entityName).get("create"))); //$NON-NLS-1$
            series2.add(DataPoint.of(entityName, journalData.get(entityName).get("update"))); //$NON-NLS-1$
        }

        // create the plot
        SimplePlot plot = new SimplePlot(model, plotOptions);
        plot.setWidth(400);
        plot.setHeight(300);
        return plot;
    }
    
    private SimplePlot createEntityPlot(JSONArray jsonArray) {

        SimplePlot plot;
        final NumberFormat formatter = NumberFormat.getFormat("0.#");
        final PlotModel model = new PlotModel();
        final PlotOptions plotOptions = PlotOptions.create();

        // activate the pie
        plotOptions.setGlobalSeriesOptions(GlobalSeriesOptions.create().setPieSeriesOptions(
                PieSeriesOptions
                        .create()
                        .setShow(true)
                        .setRadius(1)
                        .setInnerRadius(0.2)
                        .setLabel(
                                com.googlecode.gflot.client.options.PieSeriesOptions.Label.create().setShow(true)
                                        .setRadius(3d / 4d).setBackground(Background.create().setOpacity(0.8)).setThreshold(0.05)
                                        .setFormatter(new Formatter() {

                                            @Override
                                            public String format(String label, Series series) {
                                                return "<div style=\"font-size:8pt;text-align:center;padding:2px;color:white;\">"
                                                        + label + "<br/>" + formatter.format(series.getData().getY(0)) + " / "
                                                        + formatter.format(series.getPercent()) + "%</div>";
                                            }
                                        }))));
        plotOptions.setLegendOptions(LegendOptions.create().setShow(false));
        plotOptions.setGridOptions(GridOptions.create().setHoverable(true));

        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject jsonObject = jsonArray.get(i).isObject();
            String name = jsonObject.keySet().iterator().next();
            int value = new Double(jsonObject.get(name).isNumber().doubleValue()).intValue();
            // create series
            SeriesHandler series1 = model.addSeries(Series.of(name));
            series1.add(PieDataPoint.of(value));
        }
        // create the plot
        plot = new SimplePlot(model, plotOptions);
        plot.setWidth(400);
        plot.setHeight(300);
        return plot;
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
                            if (name.equals(WelcomePortal.START)) {
                                applyStartPortlet(selectedPortlet);
                            } else if (name.equals(WelcomePortal.ALERT)) {
                                applyAlertPortlet(selectedPortlet);
                            } else if (name.equals(WelcomePortal.TASKS)) {
                                applyTaskPortlet(selectedPortlet);
                            } else if (name.equals(WelcomePortal.PROCESS)) {
                                applyProcessPortlet(selectedPortlet);
                            } else if (name.equals(WelcomePortal.CHART_DATA)) {
                                applyChartDATAPortlet(selectedPortlet);
                            } else if (name.equals(WelcomePortal.CHART_JOURNAL)) {
                                applyChartJournalPortlet(selectedPortlet);
                            } else if (name.equals(WelcomePortal.CHART_ROUTING_EVENT)) {
                                applyChartRoutingEventPortlet(selectedPortlet);
                            }
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
        set.setBorders(false);

        port.add(set);
        return port;
    }

    private Portlet getPortletById(String itemId) {
        for (LayoutContainer container : this.getItems()) {
            if (container.getItemByItemId(itemId) != null) {
                return (Portlet) container.getItemByItemId(itemId);
            }
        }
        return null;
    }

    private native void openWindow(String url)/*-{
		window.open(url);
    }-*/;

    private native void initUI(String context, String application)/*-{
		$wnd.setTimeout(function() {
			$wnd.amalto[context][application].init();
		}, 50);
    }-*/;
}
