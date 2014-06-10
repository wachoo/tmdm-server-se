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
import java.util.Arrays;
import java.util.HashMap;
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
import com.extjs.gxt.ui.client.event.ContainerEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.widget.custom.Portal;
import com.extjs.gxt.ui.client.widget.custom.Portlet;


/**
 * DOC Administrator class global comment. Detailled comment
 */
public class MainFramePanel extends Portal {

    private static final int DEFAULT_REFRESH_INTERVAL = 10000;
    
    private static final boolean DEFAULT_REFRESH_STARTASON = false;
    
    private static final List<String> ORDERING_INDEX_DEFAULT = Arrays.asList(WelcomePortal.START, WelcomePortal.PROCESS, WelcomePortal.ALERT, WelcomePortal.SEARCH, 
            WelcomePortal.TASKS, WelcomePortal.CHART_DATA, WelcomePortal.CHART_ROUTING_EVENT, WelcomePortal.CHART_JOURNAL, WelcomePortal.CHART_MATCHING);

    private static int pos = 0;
    
    private boolean startedAsOn;

    private int interval;
    
    private boolean hiddenWorkFlowTask;
        
    private boolean hiddenDSCTask;
    
    private List<BasePortlet> portlets;

    private Map<String, int[]> portletLocations = new HashMap<String, int[]>();

    private WelcomePortalServiceAsync service = (WelcomePortalServiceAsync) Registry.get(WelcomePortal.WELCOMEPORTAL_SERVICE);
    
    public MainFramePanel(int numColumns) {
        super(numColumns);
        
        setBorders(true);
        setStyleAttribute("backgroundColor", "white"); //$NON-NLS-1$ //$NON-NLS-2$
        setColumnWidth(0, .5);
        setColumnWidth(1, .5);
        
        this.addListener(Events.Add, new Listener<ContainerEvent>() {

            @Override
            public void handleEvent(ContainerEvent be) {
                
                BasePortlet portlet = (BasePortlet)  be.getItem();
                String portletName = portlet.getPortletName();
                int column = MainFramePanel.this.getPortletColumn(portlet);
                int row = MainFramePanel.this.getPortletIndex(portlet);
                portletLocations.put(portletName, new int[]{column, row});
                int index = ORDERING_INDEX_DEFAULT.indexOf(portletName);
                if (index < ORDERING_INDEX_DEFAULT.size()) {
                    initializePortlet(ORDERING_INDEX_DEFAULT.get(index + 1));
                }
            }
        });
        
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
        //start portlets initialization, see ContainerEvent listener
        portlet = new StartPortlet(this);
        // add to the portal according the ordering
        this.add(portlet);
        portlets.add(portlet);
                
    }
    
    private void add(Portlet portlet) {
        this.add(portlet, pos%2);
        pos++;
        fireEvent(Events.Add, new ContainerEvent<Portal, Portlet>(this, portlet));
    }
    private void initializePortlet(String portletName) {
        //WelcomePortal.PROCESS, WelcomePortal.ALERT, WelcomePortal.SEARCH, WelcomePortal.TASKS, 
        //WelcomePortal.CHART_DATA, WelcomePortal.CHART_ROUTING_EVENT, WelcomePortal.CHART_JOURNAL, WelcomePortal.CHART_MATCHING
        BasePortlet portlet = null;

        if (WelcomePortal.PROCESS.equals(portletName)) {
            portlet = new ProcessPortlet(this);
        } else if (WelcomePortal.ALERT.equals(portletName)) {
            initAlertPortlet();
        } else if (WelcomePortal.SEARCH.equals(portletName)) {
            initSearchPortlet();
        } else if (WelcomePortal.TASKS.equals(portletName)) {
            initTaskPortlet();
        } else if (WelcomePortal.CHART_DATA.equals(portletName)) {
            portlet = new DataChart(this);
        } else if (WelcomePortal.CHART_ROUTING_EVENT.equals(portletName)) {
            portlet = new RoutingChart(this);
        } else if (WelcomePortal.CHART_JOURNAL.equals(portletName)) {
            portlet = new JournalChart(this);
        } else if (WelcomePortal.CHART_MATCHING.equals(portletName)) {
            initMatchingChart();
        } 
        
        if (portlet != null) {
            this.add(portlet);
            portlets.add(portlet);
        } 
    }

//    public void add(Portlet portlet) {
//        int[] loc = portlet_locations.get(((BasePortlet)portlet).getPortletName());
//        this.insert(portlet, loc[1], loc[0]);
//    }

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

    private void initAlertPortlet() {
        
        service.isHiddenLicense(new SessionAwareAsyncCallback<Boolean>() {

            @Override
            public void onSuccess(Boolean hideMe) {
                if (!hideMe) {
                    BasePortlet portlet = new AlertPortlet(MainFramePanel.this);
                    MainFramePanel.this.add(portlet);
                    portlets.add(portlet);
                } else {
                    int index = ORDERING_INDEX_DEFAULT.indexOf(WelcomePortal.ALERT);
                    initializePortlet(ORDERING_INDEX_DEFAULT.get(index + 1));
                }
            }

        });
        
    }
    
    private void initSearchPortlet() {
        
        service.isEnterpriseVersion(new SessionAwareAsyncCallback<Boolean>() {

            @Override
            public void onSuccess(Boolean isEnterprise) {
                if (isEnterprise) {
                    BasePortlet portlet = new SearchPortlet(MainFramePanel.this);
                    MainFramePanel.this.add(portlet);
                    portlets.add(portlet);
                } else {
                    int index = ORDERING_INDEX_DEFAULT.indexOf(WelcomePortal.SEARCH);
                    initializePortlet(ORDERING_INDEX_DEFAULT.get(index + 1));
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
                            BasePortlet portlet = new TaskPortlet(MainFramePanel.this);
                            MainFramePanel.this.add(portlet);
                            portlets.add(portlet);
                        } else {
                            int index = ORDERING_INDEX_DEFAULT.indexOf(WelcomePortal.TASKS);
                            initializePortlet(ORDERING_INDEX_DEFAULT.get(index + 1));
                        }
                        
                    }
                });                
            }
        });
        
    }  

    private void initMatchingChart() {
        
        service.isEnterpriseVersion(new SessionAwareAsyncCallback<Boolean>() {

            @Override
            public void onSuccess(Boolean isEnterprise) {
                if (isEnterprise) {
                    BasePortlet portlet = new MatchingChart(MainFramePanel.this);
                    MainFramePanel.this.add(portlet);
                    portlets.add(portlet);
                }
            }
        });
        
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