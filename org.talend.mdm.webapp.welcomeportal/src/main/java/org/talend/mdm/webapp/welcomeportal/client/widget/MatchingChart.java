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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.talend.mdm.webapp.base.client.SessionAwareAsyncCallback;
import org.talend.mdm.webapp.welcomeportal.client.WelcomePortal;
import org.talend.mdm.webapp.welcomeportal.client.rest.StatisticsRestServiceHandler;

import com.extjs.gxt.ui.client.event.IconButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.button.ToolButton;
import com.extjs.gxt.ui.client.widget.custom.Portal;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.googlecode.gflot.client.DataPoint;
import com.googlecode.gflot.client.PlotModel;
import com.googlecode.gflot.client.Series;
import com.googlecode.gflot.client.SeriesHandler;
import com.googlecode.gflot.client.options.AxesOptions;
import com.googlecode.gflot.client.options.AxisOptions;
import com.googlecode.gflot.client.options.BarSeriesOptions;
import com.googlecode.gflot.client.options.BarSeriesOptions.BarAlignment;
import com.googlecode.gflot.client.options.CategoriesAxisOptions;
import com.googlecode.gflot.client.options.GlobalSeriesOptions;
import com.googlecode.gflot.client.options.LegendOptions;
import com.googlecode.gflot.client.options.LineSeriesOptions;
import com.googlecode.gflot.client.options.PlotOptions;

public class MatchingChart extends ChartPortlet {

    private Map<String, Integer> matchingData;

    public MatchingChart(Portal portal) {
        super(WelcomePortal.CHART_MATCHING, portal);

        this.getHeader().addTool(new ToolButton("x-tool-refresh", new SelectionListener<IconButtonEvent>() { //$NON-NLS-1$

                    @Override
                    public void componentSelected(IconButtonEvent ce) {
                        service.getCurrentDataContainer(new SessionAwareAsyncCallback<String>() {

                            @Override
                            public void onSuccess(String dataContainer) {

                                StatisticsRestServiceHandler.getInstance().getContainerMatchingStats(dataContainer,
                                        new SessionAwareAsyncCallback<JSONArray>() {

                                            @Override
                                            public void onSuccess(JSONArray jsonArray) {
                                                parseJSONData(jsonArray);
                                                refreshPlot();
                                            }
                                        });
                            }
                        });
                    }

                }));

        initChart();
    }

    private void initChart() {

        service.getCurrentDataContainer(new SessionAwareAsyncCallback<String>() {

            @Override
            public void onSuccess(String dataContainer) {

                StatisticsRestServiceHandler.getInstance().getContainerMatchingStats(dataContainer,
                        new SessionAwareAsyncCallback<JSONArray>() {

                            @Override
                            public void onSuccess(JSONArray jsonArray) {
                                parseJSONData(jsonArray);
                                initAndShow();
                            }
                        });
            }
        });
    }

    @Override
    public void refresh() {
        service.getCurrentDataContainer(new SessionAwareAsyncCallback<String>() {

            @Override
            public void onSuccess(String dataContainer) {

                StatisticsRestServiceHandler.getInstance().getContainerMatchingStats(dataContainer,
                        new SessionAwareAsyncCallback<JSONArray>() {

                            @Override
                            public void onSuccess(JSONArray jsonArray) {
                                parseJSONData(jsonArray);
                                refreshPlot();
                            }
                        });
            }
        });
    }

    @Override
    protected void initPlot() {
        super.initPlot();
        PlotModel model = plot.getModel();
        PlotOptions plotOptions = plot.getOptions();
        Set<String> entityNames = matchingData.keySet();
        List<String> entityNamesSorted = sort(entityNames);

        plotOptions
                .setGlobalSeriesOptions(
                        GlobalSeriesOptions
                                .create()
                                .setLineSeriesOptions(LineSeriesOptions.create().setShow(false).setFill(true))
                                .setBarsSeriesOptions(
                                        BarSeriesOptions.create().setShow(true).setBarWidth(0.6)
                                                .setAlignment(BarAlignment.CENTER)).setStack(true))
                .setYAxesOptions(AxesOptions.create().addAxisOptions(AxisOptions.create().setTickDecimals(0).setMinimum(0)))
                .setXAxesOptions(
                        AxesOptions.create().addAxisOptions(
                                CategoriesAxisOptions.create().setCategories(
                                        entityNamesSorted.toArray(new String[entityNamesSorted.size()]))));
        plotOptions.setLegendOptions(LegendOptions.create().setShow(false));

        // create series
        SeriesHandler seriesMatched = model.addSeries(Series.of("Matched")); //$NON-NLS-1$

        // add data
        for (String entityName : entityNamesSorted) {
            seriesMatched.add(DataPoint.of(entityName, matchingData.get(entityName)));
        }
    }

    @Override
    protected void updatePlot() {
        PlotModel model = plot.getModel();
        PlotOptions plotOptions = plot.getOptions();
        Set<String> entityNames = matchingData.keySet();
        List<String> entityNamesSorted = sort(entityNames);

        plotOptions.setXAxesOptions(AxesOptions.create().addAxisOptions(
                CategoriesAxisOptions.create().setCategories(entityNamesSorted.toArray(new String[entityNamesSorted.size()]))));

        List<? extends SeriesHandler> series = model.getHandlers();
        assert series.size() == 1;
        SeriesHandler seriesMatched = series.get(0);

        seriesMatched.clear();
        for (String entityName : entityNamesSorted) {
            seriesMatched.add(DataPoint.of(entityName, matchingData.get(entityName)));
        }
    }

    private void parseJSONData(JSONArray jsonArray) {
        matchingData = new HashMap<String, Integer>(jsonArray.size());
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject jsonObject = jsonArray.get(i).isObject();
            String name = jsonObject.keySet().iterator().next();
            int value = new Double(jsonObject.get(name).isNumber().doubleValue()).intValue();
            matchingData.put(name, value);
        }
    }

}
