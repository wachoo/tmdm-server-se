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

import java.util.Map;

import org.talend.mdm.webapp.base.client.SessionAwareAsyncCallback;
import org.talend.mdm.webapp.welcomeportal.client.MainFramePanel;
import org.talend.mdm.webapp.welcomeportal.client.WelcomePortal;
import org.talend.mdm.webapp.welcomeportal.client.i18n.MessagesFactory;

import com.extjs.gxt.ui.client.event.IconButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.button.ToolButton;
import com.extjs.gxt.ui.client.widget.custom.Portal;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.HTML;

public class TaskPortlet extends BasePortlet {

    private static String DSCTASKTYPE_NEW = "new"; //$NON-NLS-1$

    private static String DSCTASKTYPE_PENDING = "pending"; //$NON-NLS-1$

    private boolean isHiddenWorkFlowTask = true;

    private boolean isHiddenDSCTask = true;

    public TaskPortlet(Portal portal) {
        super(WelcomePortal.TASKS, portal);

        isHiddenWorkFlowTask = ((MainFramePanel) portal).isHiddenWorkFlowTask();
        isHiddenDSCTask = ((MainFramePanel) portal).isHiddenDSCTask();
        initAutoRefresher();
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

        label.setText(MessagesFactory.getMessages().loading_task_msg());

        set.removeAll();

        final HTML taskHtml_workflow = new HTML();

        final StringBuilder sbForWorkflowtasks = new StringBuilder(
                "<span id=\"workflowtasks\" style=\"padding-right:8px;cursor: pointer;\" class=\"labelStyle\" title=\"" + MessagesFactory.getMessages().tasks_title() + "\">"); //$NON-NLS-1$ //$NON-NLS-2$
        final StringBuilder sbForDsctasks = new StringBuilder(
                "<span id=\"dsctasks\" style=\"padding-right:8px;cursor: pointer;\" class=\"labelStyle\" title=\"" + MessagesFactory.getMessages().tasks_title() + "\">"); //$NON-NLS-1$ //$NON-NLS-2$
        final ClickHandler workflowClikcHanlder = new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                ((MainFramePanel) portal).itemClick(WelcomePortal.WORKFLOW_TASKCONTEXT, WelcomePortal.WORKFLOW_TASKAPP);
            }

        };

        final ClickHandler dscClikcHanlder = new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                ((MainFramePanel) portal).itemClick(WelcomePortal.DSC_TASKCONTEXT, WelcomePortal.DSC_TASKAPP);
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
                    if (total <= 0) {

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
                            if (workflowNum + dscNum <= 0) {
                                label.setText(MessagesFactory.getMessages().no_tasks());
                                set.setVisible(false);
                            } else if (workflowNum > 0 && dscNum == 0) {
                                String sbNum = buildMessageForWorkflowTasks(workflowNum);
                                sbForWorkflowtasks.append(sbNum);
                                sbForWorkflowtasks.append("</span>"); //$NON-NLS-1$
                                label.setText(MessagesFactory.getMessages().tasks_desc());
                                set.setVisible(true);

                                taskHtml_workflow.setHTML(sbForWorkflowtasks.toString());

                                taskHtml_workflow.addClickHandler(workflowClikcHanlder);

                                set.add(taskHtml_workflow);
                            } else if (workflowNum == 0 && dscNum > 0) {
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
        autoRefresh(autoRefreshBtn.isOn());
    }

    private String buildMessageForWorkflowTasks(int num) {
        StringBuilder mseeage = new StringBuilder("<IMG SRC=\"/talendmdm/secure/img/genericUI/task-list-icon.png\"/>&nbsp;"); //$NON-NLS-1$                                
        mseeage.append(MessagesFactory.getMessages().waiting_task_prefix());
        mseeage.append("&nbsp;<b style=\"color: red;\">" + num + "</b>&nbsp;"); //$NON-NLS-1$ //$NON-NLS-2$
        mseeage.append(MessagesFactory.getMessages().waiting_workflowtask_suffix());

        return mseeage.toString();
    }

    private String buildMessageForDSCTasks(Map<String, Integer> dscTasks) {
        StringBuilder message = new StringBuilder("<IMG SRC=\"/talendmdm/secure/img/genericUI/task-list-icon.png\"/>&nbsp;"); //$NON-NLS-1$                                
        message.append(MessagesFactory.getMessages().waiting_task_prefix());
        message.append("&nbsp;<b style=\"color: red;\">" + dscTasks.get(DSCTASKTYPE_NEW) + "&nbsp;new</b>&nbsp;and"); //$NON-NLS-1$ //$NON-NLS-2$
        message.append("&nbsp;<b style=\"color: red;\">" + dscTasks.get(DSCTASKTYPE_PENDING) + "&nbsp;pending</b>&nbsp;"); //$NON-NLS-1$ //$NON-NLS-2$
        message.append(MessagesFactory.getMessages().waiting_dsctask_suffix());

        return message.toString();
    }
}
