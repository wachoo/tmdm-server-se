// ============================================================================
//
// Copyright (C) 2006-2015 Talend Inc. - www.talend.com
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

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.talend.mdm.webapp.base.client.SessionAwareAsyncCallback;
import org.talend.mdm.webapp.base.client.widget.PortletConstants;
import org.talend.mdm.webapp.welcomeportal.client.MainFramePanel;
import org.talend.mdm.webapp.welcomeportal.client.i18n.MessagesFactory;
import org.talend.mdm.webapp.welcomeportal.client.mvc.TimeframeConfigModel;
import org.talend.mdm.webapp.welcomeportal.client.rest.StatisticsRestServiceHandler;
import org.talend.mdm.webapp.welcomeportal.client.widget.options.AxeTicks;

import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.googlecode.gflot.client.DataPoint;
import com.googlecode.gflot.client.PlotModel;
import com.googlecode.gflot.client.Series;
import com.googlecode.gflot.client.SeriesHandler;
import com.googlecode.gflot.client.Tick;
import com.googlecode.gflot.client.event.PlotItem;
import com.googlecode.gflot.client.options.AxesOptions;
import com.googlecode.gflot.client.options.AxisOptions;
import com.googlecode.gflot.client.options.BarSeriesOptions;
import com.googlecode.gflot.client.options.BarSeriesOptions.BarAlignment;
import com.googlecode.gflot.client.options.GlobalSeriesOptions;
import com.googlecode.gflot.client.options.GridOptions;
import com.googlecode.gflot.client.options.LegendOptions;
import com.googlecode.gflot.client.options.LineSeriesOptions;
import com.googlecode.gflot.client.options.PlotOptions;

public class RoutingChart extends ChartPortlet {

    private static String ROUTING_STATUS_FAILED = "failed"; //$NON-NLS-1$

    private static String ROUTING_STATUS_COMPLETED = "completed"; //$NON-NLS-1$

    public RoutingChart(MainFramePanel portal) {
        super(PortletConstants.ROUTING_EVENT_CHART_NAME, portal);
        setHeading(MessagesFactory.getMessages().chart_routing_event_title());
        String setting = portalConfigs.getChartSetting(portletName);
        if (setting != null) {
            configModel = new TimeframeConfigModel(startedAsOn, setting);
        } else {
            configModel = new TimeframeConfigModel(startedAsOn);
        }

        initConfigSettings();

        initChart();
    }

    @Override
    public void refresh() {
        StatisticsRestServiceHandler.getInstance().getRoutingEventStats(configModel, new SessionAwareAsyncCallback<JSONArray>() {

            @Override
            public void onSuccess(JSONArray jsonArray) {
                Map<String, Object> newData = parseJSONData(jsonArray);
                doRefreshWith(newData);
            }
        });

    }

    private void initChart() {
        StatisticsRestServiceHandler.getInstance().getRoutingEventStats(configModel, new SessionAwareAsyncCallback<JSONArray>() {

            @Override
            public void onSuccess(JSONArray jsonArray) {
                chartData = parseJSONData(jsonArray);
                initAndShow();
            }
        });
    }

    @Override
    protected void initPlot() {
        super.initPlot();
        PlotModel model = plot.getModel();
        PlotOptions plotOptions = plot.getOptions();
        entityNamesSorted = sort(chartData.keySet());

        plotOptions
                .setGlobalSeriesOptions(
                        GlobalSeriesOptions
                                .create()
                                .setHighlightColor("rgba(255, 255, 255, 0.3)") //$NON-NLS-1$
                                .setLineSeriesOptions(LineSeriesOptions.create().setShow(false).setSteps(false))
                                .setBarsSeriesOptions(
                                        BarSeriesOptions.create().setShow(true).setBarWidth(0.9).setFill(1)
                                                .setAlignment(BarAlignment.CENTER)).setStack(false))
                .setYAxesOptions(AxesOptions.create().addAxisOptions(AxisOptions.create().setTickDecimals(0).setMinimum(0)))
                .setXAxesOptions(getXAxesOptions());

        plotOptions.setLegendOptions(LegendOptions.create().setShow(true));
        plotOptions.setGridOptions(GridOptions.create().setHoverable(true).setBorderWidth(0).setColor(COLOR)
                .setBackgroundColor(BACKGROUND_COLOR));

        // create series
        SeriesHandler seriesCompleted = model.addSeries(Series.of(MessagesFactory.getMessages().chart_routing_event_completed())
                .setColor(SERIES_1_COLOR));
        SeriesHandler seriesFailed = model.addSeries(Series.of(MessagesFactory.getMessages().chart_routing_event_failed())
                .setColor(SERIES_2_COLOR));

        // add data
        addDataToSeries(seriesCompleted, seriesFailed);
    }

    @Override
    protected void resizePlot(){
        plot.getOptions().setXAxesOptions(getXAxesOptions());
        super.resizePlot();
    }
    
    @Override
    protected void updatePlot() {
        PlotModel model = plot.getModel();
        PlotOptions plotOptions = plot.getOptions();
        entityNamesSorted = sort(chartData.keySet());

        plotOptions.setXAxesOptions(getXAxesOptions());

        List<? extends SeriesHandler> series = model.getHandlers();
        assert series.size() == 2;
        SeriesHandler seriesCompleted = series.get(0);
        SeriesHandler seriesFailed = series.get(1);

        seriesCompleted.clear();
        seriesFailed.clear();
        addDataToSeries(seriesCompleted, seriesFailed);
    }

    private AxesOptions getXAxesOptions() {
        return AxesOptions.create().addAxisOptions(
                AxisOptions.create().setAxisLabelAngle(70d).setTicks(getTicks()).setAutoscaleMargin(0.1));
    }

    private AxeTicks getTicks() {
        AxeTicks routingTicks = AxeTicks.create();
        double x = 1;
        for (String routingName : entityNamesSorted) {
            routingTicks.push(Tick.of(x, isDisplayText() ? routingName : "")); //$NON-NLS-1$
            x += 3;
        }
        return routingTicks;
    }

    @SuppressWarnings("unchecked")
    private void addDataToSeries(SeriesHandler seriesCompleted, SeriesHandler seriesFailed) {
        double x = 0;
        for (String routingName : entityNamesSorted) {
            Map<String, Integer> routingData = (Map<String, Integer>) chartData.get(routingName);
            seriesCompleted.add(DataPoint.of(x, routingData.get(ROUTING_STATUS_COMPLETED)));
            seriesFailed.add(DataPoint.of(x + 1, routingData.get(ROUTING_STATUS_FAILED)));
            x += 3;
        }
    }

    @Override
    protected String getHoveringText(PlotItem item) {
        int valueY = (int) item.getDataPoint().getY();
        int valueX = (int) item.getDataPoint().getX();
        int routingNameIndex = valueX == 0 ? 0 : valueX / 3;
        return entityNamesSorted.get(routingNameIndex) + ": " + valueY + "(" + item.getSeries().getLabel() + ")"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    }

    @SuppressWarnings("deprecation")
    @Override
    protected Map<String, Object> parseJSONData(JSONArray jsonArray) {
        assert (jsonArray.size() == 2);

        Map<String, Object> routingDataNew = new HashMap<String, Object>();

        JSONObject failedJSONObj;
        JSONObject completedJSONObj;
        JSONArray failures;
        JSONArray completes;
        failedJSONObj = (JSONObject) jsonArray.get(0);
        completedJSONObj = (JSONObject) jsonArray.get(1);
        String currApp;
        Set<String> appNames = new HashSet<String>();
        failures = failedJSONObj.get(ROUTING_STATUS_FAILED).isArray();
        Map<String, Integer> completesMap = new HashMap<String, Integer>();

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
            status.put(ROUTING_STATUS_FAILED, !failureMap.containsKey(appName) ? 0 : failureMap.get(appName));
            status.put(ROUTING_STATUS_COMPLETED, !completesMap.containsKey(appName) ? 0 : completesMap.get(appName));
            routingDataNew.put(appName, status);
        }

        return routingDataNew;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected boolean isDifferentFrom(Map<String, Object> newData) {
        if (chartData.size() != newData.size()) {
            return true;
        } else {
            Map<String, Integer> status;
            Map<String, Integer> statusNew;
            for (String appName : chartData.keySet()) {
                status = (Map<String, Integer>) chartData.get(appName);
                statusNew = newData.get(appName) != null ? (Map<String, Integer>) newData.get(appName) : null;
                if (statusNew == null) {
                    return true;
                }

                if (!status.get(ROUTING_STATUS_FAILED).equals(statusNew.get(ROUTING_STATUS_FAILED))
                        || !status.get(ROUTING_STATUS_COMPLETED).equals(statusNew.get(ROUTING_STATUS_COMPLETED))) {
                    return true;
                }

            }
        }
        return false;
    }

}
