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
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import org.talend.mdm.webapp.base.client.SessionAwareAsyncCallback;
import org.talend.mdm.webapp.base.client.util.Cookies;
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
import com.extjs.gxt.ui.client.event.PortalEvent;
import com.extjs.gxt.ui.client.widget.custom.Portal;
import com.extjs.gxt.ui.client.widget.custom.Portlet;


/**
 * DOC Administrator class global comment. Detailled comment
 */
public class MainFramePanel extends Portal {

    private static final int DEFAULT_REFRESH_INTERVAL = 10000;
    
    private static final boolean DEFAULT_REFRESH_STARTASON = false;
    
    private static final List<String> DEFAULT_ORDERING_INDEX = Arrays.asList(WelcomePortal.START, WelcomePortal.PROCESS, WelcomePortal.ALERT, WelcomePortal.SEARCH, 
            WelcomePortal.TASKS, WelcomePortal.CHART_DATA, WelcomePortal.CHART_ROUTING_EVENT, WelcomePortal.CHART_JOURNAL, WelcomePortal.CHART_MATCHING);

    private static final String COOKIES_PORTLET_LOCATIONS = "portletLocations"; //$NON-NLS-1$
    
    private boolean startedAsOn;

    private int interval;
    
    private boolean hiddenWorkFlowTask;
        
    private boolean hiddenDSCTask;
    
    private List<BasePortlet> portlets;

    private int pos;
    
    private Map<String, List<Integer>> portletToLocations = new LinkedHashMap<String, List<Integer>>();
    
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
                
                portletToLocations.put(portletName, Arrays.asList(column, row));
                int index = DEFAULT_ORDERING_INDEX.indexOf(portletName);
                if (index < DEFAULT_ORDERING_INDEX.size() - 1) {
                    initializePortlet(DEFAULT_ORDERING_INDEX.get(index + 1));
                } else {
                    //init cookies value after all portlet initialized
                    Cookies.setValue(COOKIES_PORTLET_LOCATIONS, portletToLocations);
                }
            }
        });
        
        this.addListener(Events.Drop, new Listener<PortalEvent>() {

            @Override
            public void handleEvent(PortalEvent pe) {
                updateLocations();
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
        if (Cookies.getValue(COOKIES_PORTLET_LOCATIONS) == null) {
            BasePortlet portlet;
            //start portlets initialization, see ContainerEvent listener
            portlet = new StartPortlet(this);
            portlets.add(portlet);
            this.add(portlet);
        } else {
            portletToLocations = (Map<String, List<Integer>>) Cookies.getValue(COOKIES_PORTLET_LOCATIONS);
            initializePortlets(portletToLocations);
        }
                
    }
    
    //called when portlet locations are restored from cookies -- back in synchronous mode
    private void initializePortlets(Map<String, List<Integer>> locs) {
        Comparator<List<Integer>> locationComparator = new Comparator<List<Integer>>(){

            @Override
            public int compare(List<Integer> loc1, List<Integer> loc2) {
                assert loc1.size() == 2;
                assert loc2.size() == 2;
                
                int row1 = loc1.get(1);
                int row2 = loc2.get(1);
                
                int diff = row1 - row2;
                if ( diff != 0) {
                    return diff;
                } else {
                    return loc1.get(0) - loc2.get(0);
                }
            }
          
        };
        
        //sort locations in order to insert portlets in portal in ascending order and avoid out-of-index error
        SortedMap<List<Integer>, String> locationToPortlets = new TreeMap<List<Integer>, String>(locationComparator);
        for (String portletName : locs.keySet()) {
            locationToPortlets.put(locs.get(portletName), portletName);
        }
 
        for (List<Integer> loc : locationToPortlets.keySet()) {
            initializePortlet(locationToPortlets.get(loc), loc);
        }
    }
    
    private void initializePortlet(String portletName) {
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
            portlets.add(portlet);
            this.add(portlet);
        } 
    }
    
    private void initializePortlet(String portletName, List<Integer> loc) {
        BasePortlet portlet = null;
        if (WelcomePortal.START.equals(portletName)) {
            portlet = new StartPortlet(this);
        } else if (WelcomePortal.PROCESS.equals(portletName)) {
            portlet = new ProcessPortlet(this);
        } else if (WelcomePortal.ALERT.equals(portletName)) {
            portlet = new AlertPortlet(this);
        } else if (WelcomePortal.SEARCH.equals(portletName)) {
            portlet = new SearchPortlet(this);
        } else if (WelcomePortal.TASKS.equals(portletName)) {
            portlet = new TaskPortlet(this);
        } else if (WelcomePortal.CHART_DATA.equals(portletName)) {
            portlet = new DataChart(this);
        } else if (WelcomePortal.CHART_ROUTING_EVENT.equals(portletName)) {
            portlet = new RoutingChart(this);
        } else if (WelcomePortal.CHART_JOURNAL.equals(portletName)) {
            portlet = new JournalChart(this);
        } else if (WelcomePortal.CHART_MATCHING.equals(portletName)) {
            portlet = new MatchingChart(this);
        } else {
            assert false;
        }
        
        if (portlet != null) {
            portlets.add(portlet);
            this.add(portlet, loc);
        } 
    }
    
    private void add(Portlet portlet) {
        this.add(portlet, pos%2);
        pos++;
        fireEvent(Events.Add, new ContainerEvent<Portal, Portlet>(this, portlet));
    }
    
    private void add(Portlet portlet, List<Integer> loc) {
        insert(portlet, loc.get(1), loc.get(0));
    }
    
    private List<String> getPortletNames() {
        List<String> names = new ArrayList<String>(portlets.size());
        for (BasePortlet portlet : portlets) {
            names.add(portlet.getPortletName());
        }
        return names;
    }

    private void updateLocations() {

        int column;
        int row;
        for (BasePortlet portlet : portlets) {
            column = this.getPortletColumn(portlet);
            row = this.getPortletIndex(portlet);
            
            portletToLocations.put(portlet.getPortletName(), Arrays.asList(column, row));
        }
        
        Cookies.setValue(COOKIES_PORTLET_LOCATIONS, portletToLocations);
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

    private void initAlertPortlet() {
        
        service.isHiddenLicense(new SessionAwareAsyncCallback<Boolean>() {

            @Override
            public void onSuccess(Boolean hideMe) {
                if (!hideMe) {
                    BasePortlet portlet = new AlertPortlet(MainFramePanel.this);
                    portlets.add(portlet);
                    MainFramePanel.this.add(portlet);
                } else {
                    int index = DEFAULT_ORDERING_INDEX.indexOf(WelcomePortal.ALERT);
                    initializePortlet(DEFAULT_ORDERING_INDEX.get(index + 1));
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
                    portlets.add(portlet);
                    MainFramePanel.this.add(portlet);
                } else {
                    int index = DEFAULT_ORDERING_INDEX.indexOf(WelcomePortal.SEARCH);
                    initializePortlet(DEFAULT_ORDERING_INDEX.get(index + 1));
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
                            portlets.add(portlet);
                            MainFramePanel.this.add(portlet);
                        } else {
                            int index = DEFAULT_ORDERING_INDEX.indexOf(WelcomePortal.TASKS);
                            initializePortlet(DEFAULT_ORDERING_INDEX.get(index + 1));
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
                    portlets.add(portlet);
                    MainFramePanel.this.add(portlet);
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