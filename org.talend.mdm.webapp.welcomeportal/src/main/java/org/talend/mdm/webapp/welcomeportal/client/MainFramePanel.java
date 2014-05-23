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
import java.util.Map;

import org.talend.mdm.webapp.base.client.SessionAwareAsyncCallback;
import org.talend.mdm.webapp.base.client.util.UrlUtil;
import org.talend.mdm.webapp.welcomeportal.client.widget.AlertPortlet;
import org.talend.mdm.webapp.welcomeportal.client.widget.BasePortlet;
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



/**
 * DOC Administrator class global comment. Detailled comment
 */
public class MainFramePanel extends Portal {

    private static final int DEFAULT_REFRESH_INTERVAL = 10000;
    
    private static final boolean DEFAULT_REFRESH_STARTASON = false;
    
    private boolean startedAsOn;
    private int interval;
    private boolean hiddenWorkFlowTask;
    private boolean hiddenDSCTask;
    private List<BasePortlet> portlets;
    private List<BasePortlet> charts;

    private WelcomePortalServiceAsync service = (WelcomePortalServiceAsync) Registry.get(WelcomePortal.WELCOMEPORTAL_SERVICE);
    
    public MainFramePanel(int numColumns) {
        super(numColumns);
        
        setBorders(true);
        setStyleAttribute("backgroundColor", "white"); //$NON-NLS-1$ //$NON-NLS-2$
        setColumnWidth(0, .5);
        setColumnWidth(1, .5);


        service.getWelcomePortletConfig(new SessionAwareAsyncCallback<Map<Boolean, Integer>>() {

            @Override
            public void onSuccess(Map<Boolean, Integer> config) {
                if (!config.containsKey(startedAsOn)) {
                    startedAsOn = !startedAsOn;
                }

                interval = config.get(startedAsOn);
                initializePortlets();
            }

            @Override
            public void doOnFailure(Throwable e) {
                startedAsOn = DEFAULT_REFRESH_STARTASON;
                interval = DEFAULT_REFRESH_INTERVAL;
                initializePortlets();
            }
        });
    

    }

    private void initializePortlets() {
        portlets = new ArrayList<BasePortlet>();
        BasePortlet portlet;
        
        portlet = new StartPortlet(this);
        this.add(portlet, 0);
        portlets.add(portlet);
        
        portlet = new ProcessPortlet(this);
        this.add(portlet, 1);
        portlets.add(portlet);
        
        initAlertPortlet();

        initSearchPortlet();
        
        initTaskPortlet();
        
        initChartPortlets();
    }

    public void refreshPortlets() {
        for (BasePortlet portlet : portlets) {
            portlet.refresh();
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
    
    public boolean getStartedAsOn() {
        return this.startedAsOn;
    }

    public int getInterval() {
        return this.interval;
    }
    
    private void initSearchPortlet() {
        service.isEnterpriseVersion(new SessionAwareAsyncCallback<Boolean>() {

            @Override
            public void onSuccess(Boolean isEnterprise) {
                if (isEnterprise) {
                    BasePortlet searchPortlet = new SearchPortlet(MainFramePanel.this);
                    MainFramePanel.this.add(searchPortlet, 1);
                    portlets.add(searchPortlet);
                }
            }
        });
    }

    private void initAlertPortlet() {
        service.isHiddenLicense(new SessionAwareAsyncCallback<Boolean>() {

            @Override
            public void onSuccess(Boolean hideMe) {
                if (!hideMe) {
                    BasePortlet alertPortlet = new AlertPortlet(MainFramePanel.this);
                    MainFramePanel.this.add(alertPortlet, 0);
                    portlets.add(alertPortlet);
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
                            BasePortlet taskPortlet = new TaskPortlet(MainFramePanel.this);
                            MainFramePanel.this.add(taskPortlet, 1);
                            portlets.add(taskPortlet);
                        }
                        
                    }
                });                
            }
        });
    }  

    private void initChartPortlets() {
        charts = new ArrayList<BasePortlet>(4);

        BasePortlet dataChart = new DataChart(this);
        BasePortlet journalChart = new JournalChart(this);
        BasePortlet routingChart = new RoutingChart(this);
        BasePortlet matchingChart = new MatchingChart(this);

        charts.add(dataChart);
        charts.add(journalChart);
        charts.add(routingChart);
        charts.add(matchingChart);

        int position = 0;
        for (BasePortlet chart : charts) {
            this.add(chart, position%2);
            portlets.add(chart);
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