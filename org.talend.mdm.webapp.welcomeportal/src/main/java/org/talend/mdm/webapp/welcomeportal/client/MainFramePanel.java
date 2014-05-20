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
import java.util.List;
import org.talend.mdm.webapp.base.client.SessionAwareAsyncCallback;
import org.talend.mdm.webapp.base.client.util.UrlUtil;
import org.talend.mdm.webapp.welcomeportal.client.widget.AlertPortlet;
import org.talend.mdm.webapp.welcomeportal.client.widget.ChartPortlet;
import org.talend.mdm.webapp.welcomeportal.client.widget.DataChart;
import org.talend.mdm.webapp.welcomeportal.client.widget.JournalChart;
import org.talend.mdm.webapp.welcomeportal.client.widget.MatchingChart;
import org.talend.mdm.webapp.welcomeportal.client.widget.ProcessPortlet;
import org.talend.mdm.webapp.welcomeportal.client.widget.RoutingChart;
import org.talend.mdm.webapp.welcomeportal.client.widget.SearchPortlet;
import org.talend.mdm.webapp.welcomeportal.client.widget.StartPortlet;
import org.talend.mdm.webapp.welcomeportal.client.widget.TaskPortlet;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.widget.custom.Portal;
import com.extjs.gxt.ui.client.widget.custom.Portlet;


/**
 * DOC Administrator class global comment. Detailled comment
 */
public class MainFramePanel extends Portal {

    private WelcomePortalServiceAsync service = (WelcomePortalServiceAsync) Registry.get(WelcomePortal.WELCOMEPORTAL_SERVICE);
    
    public boolean isHiddenWorkFlowTask() {
        return this.hiddenWorkFlowTask;
    }

    
    public void setHiddenWorkFlowTask(boolean hiddenWorkFlowTask) {
        this.hiddenWorkFlowTask = hiddenWorkFlowTask;
    }

    
    public boolean isHiddenDSCTask() {
        return this.hiddenDSCTask;
    }

    
    public void setHiddenDSCTask(boolean hiddenDSCTask) {
        this.hiddenDSCTask = hiddenDSCTask;
    }

    private boolean hiddenWorkFlowTask = true;
    private boolean hiddenDSCTask = true;
    private List<ChartPortlet> charts = new ArrayList<ChartPortlet>(4);
    
    StartPortlet startPortlet;
    ProcessPortlet procesPortlet;
    AlertPortlet alertPortlet;
    TaskPortlet taskPortlet;
    
    SearchPortlet searchPortlet;
    
    DataChart dataChart;
    JournalChart journalChart;
    RoutingChart routingChart;
    MatchingChart matchingChart;
    
    
    public MainFramePanel(int numColumns) {
        super(numColumns);
        setBorders(true);
        setStyleAttribute("backgroundColor", "white"); //$NON-NLS-1$ //$NON-NLS-2$
        setColumnWidth(0, .5);
        setColumnWidth(1, .5);

        startPortlet = new StartPortlet(this);
        this.add(startPortlet, 0);
        

        procesPortlet = new ProcessPortlet(this);
        this.add(procesPortlet, 1);
        
        initAlertPortlet(this);

        searchPortlet = new SearchPortlet(this);
        
        initTaskPortlet();
        
        initChartPortlets();

    }

    public void refreshPortlets() {
        alertPortlet.refresh();
        taskPortlet.refresh();
        procesPortlet.refresh();
        
        for (ChartPortlet chart : charts) {
            chart.refresh();
        }
    }

    public void itemClick(final String context, final String application) {
        service.isExpired(UrlUtil.getLanguage(), new SessionAwareAsyncCallback<Boolean>() {

            @Override
            public void onSuccess(Boolean result) {
                initUI(context, application);
            }
        });
    }

    private void initSearchPortlet() {
        service.isEnterpriseVersion(new SessionAwareAsyncCallback<Boolean>() {

            @Override
            public void onSuccess(Boolean isEnterprise) {
                if (isEnterprise) {
                    searchPortlet = new SearchPortlet(MainFramePanel.this);
                    MainFramePanel.this.add(alertPortlet, 1);
                }
            }
        });
    }

    private void initAlertPortlet(final Portal portal) {
        service.isHiddenLicense(new SessionAwareAsyncCallback<Boolean>() {

            @Override
            public void onSuccess(Boolean hideMe) {
                if (!hideMe) {
                    alertPortlet = new AlertPortlet(portal);
                    portal.add(alertPortlet, 0);
                }
            }

        });
    }

    private void initTaskPortlet() {
        service.isHiddenWorkFlowTask(new SessionAwareAsyncCallback<Boolean>() {

            @Override
            public void onSuccess(Boolean hideMe) {
                setHiddenWorkFlowTask(hideMe);
                
                service.isHiddenDSCTask(new SessionAwareAsyncCallback<Boolean>() {

                    @Override
                    public void onSuccess(Boolean hideMeToo) {
                        setHiddenDSCTask(hideMeToo);
                        
                        if (!isHiddenWorkFlowTask() || !isHiddenDSCTask()) {
                            taskPortlet = new TaskPortlet(MainFramePanel.this);
                            MainFramePanel.this.add(taskPortlet, 1);
                        }
                        
                    }
                });                
            }
        });
    }  

    private void initChartPortlets() {

        dataChart = new DataChart(this);
        journalChart = new JournalChart(this);
        routingChart = new RoutingChart(this);
        matchingChart = new MatchingChart(this);

        charts.add(dataChart);
        charts.add(journalChart);
        charts.add(routingChart);
        charts.add(matchingChart);

        int position = 0;
        for (Portlet chart : charts) {
            this.add(chart, position%2);
            position++;
        }
    }

    public native void openWindow(String url)/*-{
        window.open(url);
    }-*/;

    public native void initUI(String context, String application)/*-{
        $wnd.setTimeout(function() {
            $wnd.amalto[context][application].init();
        }, 50);
    }-*/;
}