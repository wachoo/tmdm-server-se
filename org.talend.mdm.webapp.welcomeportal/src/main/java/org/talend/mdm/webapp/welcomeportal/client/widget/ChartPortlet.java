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

import org.talend.mdm.webapp.welcomeportal.client.resources.icon.Icons;

import com.extjs.gxt.ui.client.widget.custom.Portal;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.googlecode.gflot.client.SimplePlot;
import com.googlecode.gflot.client.options.GlobalSeriesOptions;
import com.googlecode.gflot.client.options.PlotOptions;

public abstract class ChartPortlet extends BasePortlet {

    private static int PLOT_WIDTH = 400;

    private static int PLOT_HEIGHT = 300;

    protected SimplePlot plot;

    protected Map<String, Object> chartData;

    protected String dc;

    protected boolean dataContainerChanged;

    public ChartPortlet(String name, Portal portal) {
        super(name, portal);
        initAutoRefresher();
    }

    protected void initPlot() {
        PlotOptions plotOptions = PlotOptions.create();
        plotOptions.setGlobalSeriesOptions(GlobalSeriesOptions.create());
        plot = new SimplePlot(plotOptions);
        plot.setWidth(PLOT_WIDTH);
        plot.setHeight(PLOT_HEIGHT);
    }

    protected void refreshPlot() {
        updatePlot();
        plot.redraw();
        set.layout(true);
    }

    protected void initAndShow() {
        initPlot();
        set.add(plot);
        set.layout(true);
        this.autoRefresh(autoRefreshBtn.isOn());
    }

    protected void doRefreshWith(Map<String, Object> newData) {
        if (dataContainerChanged || isDifferentFrom(newData)) {
            chartData = newData;
            refreshPlot();
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
