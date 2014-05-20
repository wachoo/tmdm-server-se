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
import com.extjs.gxt.ui.client.widget.form.FieldSet;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.googlecode.gflot.client.DataPoint;
import com.googlecode.gflot.client.PlotModel;
import com.googlecode.gflot.client.Series;
import com.googlecode.gflot.client.SeriesHandler;
import com.googlecode.gflot.client.options.GlobalSeriesOptions;
import com.googlecode.gflot.client.options.GridOptions;
import com.googlecode.gflot.client.options.LegendOptions;
import com.googlecode.gflot.client.options.PieSeriesOptions;
import com.googlecode.gflot.client.options.PieSeriesOptions.Label.Background;
import com.googlecode.gflot.client.options.PieSeriesOptions.Label.Formatter;
import com.googlecode.gflot.client.options.PlotOptions;

public class DataChart extends ChartPortlet {

    private Map<String, Integer> entityData;

    public DataChart(Portal portal) {
        super(WelcomePortal.CHART_DATA, portal);

        this.getHeader().addTool(new ToolButton("x-tool-refresh", new SelectionListener<IconButtonEvent>() { //$NON-NLS-1$

                    @Override
                    public void componentSelected(IconButtonEvent ce) {
                        service.getCurrentDataContainer(new SessionAwareAsyncCallback<String>() {

                            @Override
                            public void onSuccess(String dataContainer) {

                                StatisticsRestServiceHandler.getInstance().getContainerDataStats(dataContainer,
                                        new SessionAwareAsyncCallback<JSONArray>() {

                                            @Override
                                            public void onSuccess(JSONArray jsonArray) {
                                                entityData = parseJSONData(jsonArray);
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
        final FieldSet set = (FieldSet) this.getItemByItemId(WelcomePortal.CHART_DATA + "Set"); //$NON-NLS-1$

        service.getCurrentDataContainer(new SessionAwareAsyncCallback<String>() {

            @Override
            public void onSuccess(String dataContainer) {

                StatisticsRestServiceHandler.getInstance().getContainerDataStats(dataContainer,
                        new SessionAwareAsyncCallback<JSONArray>() {

                            @Override
                            public void onSuccess(JSONArray jsonArray) {
                                entityData = parseJSONData(jsonArray);
                                initPlot();
                                set.add(plot);
                                // FIXME: needed?
                                set.layout(true);
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

                StatisticsRestServiceHandler.getInstance().getContainerDataStats(dataContainer,
                        new SessionAwareAsyncCallback<JSONArray>() {

                            @Override
                            public void onSuccess(JSONArray jsonArray) {
                                entityData = parseJSONData(jsonArray);
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
        final NumberFormat formatter = NumberFormat.getFormat("0.#"); //$NON-NLS-1$
        Set<String> entityNames = entityData.keySet();
        List<String> entityNamesSorted = sort(entityNames);

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
                                                return "<div style=\"font-size:8pt;text-align:center;padding:2px;color:white;\">" //$NON-NLS-1$
                                                        + label + "<br/>" + formatter.format(series.getData().getY(0)) + " / " //$NON-NLS-1$//$NON-NLS-2$
                                                        + formatter.format(series.getPercent()) + "%</div>"; //$NON-NLS-1$
                                            }
                                        }))));
        plotOptions.setLegendOptions(LegendOptions.create().setShow(false));
        plotOptions.setGridOptions(GridOptions.create().setHoverable(true));

        // create series and add data
        for (String entityName : entityNamesSorted) {
            SeriesHandler seriesEntity = model.addSeries(Series.of(entityName));
            seriesEntity.add(DataPoint.of(entityName, entityData.get(entityName)));
        }

    }

    @Override
    protected void updatePlot() {
        PlotModel model = plot.getModel();
        model.clear();
        Set<String> entityNames = entityData.keySet();
        List<String> entityNamesSorted = sort(entityNames);

        List<? extends SeriesHandler> series = model.getHandlers();

        // new entities maybe added to DM and needs to be reflected on the new chart
        Map<String, SeriesHandler> seriesMap = new HashMap<String, SeriesHandler>(entityNamesSorted.size());
        int count = 0;
        int prevTotal = series.size();
        for (String entityName : entityNamesSorted) {
            if (count < prevTotal) {
                seriesMap.put(entityName, series.get(count++));
            } else {
                seriesMap.put(entityName, model.addSeries(Series.of(entityName)));
            }
        }

        for (String entityName : entityNamesSorted) {
            seriesMap.get(entityName).add(DataPoint.of(entityName, entityData.get(entityName)));
        }
    }

    // TODO: change return type to void ?
    private Map<String, Integer> parseJSONData(JSONArray jsonArray) {
        Map<String, Integer> entityData = new HashMap<String, Integer>(jsonArray.size());
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject jsonObject = jsonArray.get(i).isObject();
            String name = jsonObject.keySet().iterator().next();
            int value = new Double(jsonObject.get(name).isNumber().doubleValue()).intValue();
            entityData.put(name, value);
        }
        return entityData;
    }

}
