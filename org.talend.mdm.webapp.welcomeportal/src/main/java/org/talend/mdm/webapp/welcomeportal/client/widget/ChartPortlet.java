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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.talend.mdm.webapp.base.client.SessionAwareAsyncCallback;
import org.talend.mdm.webapp.welcomeportal.client.mvc.ConfigModel;
import org.talend.mdm.webapp.welcomeportal.client.mvc.PortalProperties;
import org.talend.mdm.webapp.welcomeportal.client.resources.icon.Icons;
import org.talend.mdm.webapp.welcomeportal.client.widget.PortletConfigDialog.PortletConfigListener;

import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.widget.custom.Portal;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.googlecode.gflot.client.SimplePlot;
import com.googlecode.gflot.client.options.GlobalSeriesOptions;
import com.googlecode.gflot.client.options.PlotOptions;

public abstract class ChartPortlet extends BasePortlet {

    protected SimplePlot plot;

    protected Map<String, Object> chartData;

    protected String dc;

    protected boolean dataContainerChanged;

    private int plotWidth;

    private int plotHeight;

    public ChartPortlet(String name, Portal portal) {
        super(name, portal);

    }

    @Override
    protected PortletConfigListener initConfigListener() {
        PortletConfigListener configListener = new PortletConfigListener() {

            @Override
            public void onConfigUpdate(ConfigModel configModel) {
                if (!ChartPortlet.this.configModel.equals(configModel)) {
                    configModelChanged = true;
                    if (ChartPortlet.this.configModel.isAutoRefresh().equals(configModel.isAutoRefresh())) {
                        ChartPortlet.this.configModel = configModel;
                        // Cookies.setValue(cookieskeyConfig, configModel.getSetting());
                        refresh();
                    } else {
                        ChartPortlet.this.configModel = configModel;
                        // Cookies.setValue(cookieskey, configModel.isAutoRefresh());
                        // Cookies.setValue(cookieskeyConfig, configModel.getSetting());
                        ChartPortlet.this.autoRefresh(configModel.isAutoRefresh());
                    }

                    //
                    portalConfigs.add(PortalProperties.KEY_AUTO_ONOFFS, portletName, configModel.isAutoRefresh().toString());
                    portalConfigs.add(PortalProperties.KEY_CHART_SETTINGS, portletName, configModel.getSetting());

                    service.savePortalConfig(portalConfigs, new SessionAwareAsyncCallback<Void>() {

                        @Override
                        public void onSuccess(Void result) {
                            return;
                        }

                        @Override
                        protected void doOnFailure(Throwable caught) {
                            super.doOnFailure(caught);
                        }
                    });
                } else {
                    configModelChanged = false;
                    return;
                }
            }
        };

        return configListener;
    }

    protected void initPlot() {
        PlotOptions plotOptions = PlotOptions.create();
        plotOptions.setGlobalSeriesOptions(GlobalSeriesOptions.create());
        plot = new SimplePlot(plotOptions);
        plotWidth = this.getWidth() - 50;
        plotHeight = this.getWidth() - 60;
        plot.setWidth(plotWidth);
        plot.setHeight(plotHeight);
    }

    protected void resizePlot() {
        plot.setWidth(plotWidth);
        plot.setHeight(plotHeight);
        plot.redraw();
        set.layout(true);
    }

    protected void refreshPlot() {
        updatePlot();
        plot.redraw();
        set.layout(true);
    }

    protected void initAndShow() {
        initPlot();
        set.removeAll();
        set.add(plot);
        set.layout(true);
        this.autoRefresh(configModel.isAutoRefresh());

        this.addListener(Events.Resize, new Listener<BaseEvent>() {

            @Override
            public void handleEvent(BaseEvent be) {
                plotWidth = ChartPortlet.this.getWidth() - 50;
                plotHeight = plotWidth - 10;
                resizePlot();
            }
        });
    }

    protected void doRefreshWith(Map<String, Object> newData) {
        if (dataContainerChanged || configModelChanged || isDifferentFrom(newData)) {
            chartData = newData;
            refreshPlot();
            if (configModelChanged) {
                configModelChanged = !configModelChanged;
            }
        }
    }

    protected List<String> sort(Set<String> names) {
        List<String> appnamesSorted = new ArrayList<String>(names);
        Collections.sort(appnamesSorted);

        return appnamesSorted;
    }

    @Override
    protected void setIcon() {
        this.setIcon(AbstractImagePrototype.create(Icons.INSTANCE.chart()));
    }

    abstract Map<String, Object> parseJSONData(JSONArray jsonArray);

    abstract protected void updatePlot();

    abstract protected boolean isDifferentFrom(Map<String, Object> newData);
}
