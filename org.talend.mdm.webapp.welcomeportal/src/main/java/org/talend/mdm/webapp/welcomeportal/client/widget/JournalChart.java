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

import java.util.ArrayList;
import java.util.HashMap;
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
import com.google.gwt.user.client.ui.HTML;
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

public class JournalChart extends ChartPortlet {

    private static String JOURNAL_ACTION_CREATE = "create"; //$NON-NLS-1$

    private static String JOURNAL_ACTION_UPDATE = "update"; //$NON-NLS-1$

    public JournalChart(MainFramePanel portal) {
        super(PortletConstants.JOURNAL_CHART_NAME, portal);
        setHeading(MessagesFactory.getMessages().chart_journal_title());
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

        service.getCurrentDataContainer(new SessionAwareAsyncCallback<String>() {

            @Override
            public void onSuccess(String dataContainer) {
                dataContainerChanged = !dc.equals(dataContainer);
                dc = dataContainer;

                StatisticsRestServiceHandler.getInstance().getContainerJournalStats(dataContainer, configModel,
                        new SessionAwareAsyncCallback<JSONArray>() {

                            @Override
                            public void onSuccess(JSONArray jsonArray) {
                                Map<String, Object> newData = parseJSONData(jsonArray);
                                if (plot == null) {
                                    chartData = newData;
                                    initAndShow();
                                } else {
                                    doRefreshWith(newData);
                                }
                            }
                        });
            }
        });

    }

    private void initChart() {
        String noDCAlertPrefix = "<span id=\"licenseAlert\" style=\"padding-right:8px;cursor: pointer;\" class=\"labelStyle\" title=\"" //$NON-NLS-1$
                + MessagesFactory.getMessages().alerts_title() + "\">"; //$NON-NLS-1$
        final String alertIcon = "<IMG SRC=\"secure/img/genericUI/alert-icon.png\"/>&nbsp;"; //$NON-NLS-1$
        final HTML alertHtml = new HTML();
        final StringBuilder noDCAlertMsg = new StringBuilder(noDCAlertPrefix);

        service.getCurrentDataContainer(new SessionAwareAsyncCallback<String>() {

            @Override
            public void onSuccess(String dataContainer) {

                if (dataContainer != null) {
                    dc = dataContainer;
                    dataContainerChanged = false;

                    StatisticsRestServiceHandler.getInstance().getContainerJournalStats(dataContainer, configModel,
                            new SessionAwareAsyncCallback<JSONArray>() {

                                @Override
                                public void onSuccess(JSONArray jsonArray) {
                                    chartData = parseJSONData(jsonArray);
                                    initAndShow();
                                }
                            });
                } else {
                    noDCAlertMsg.append(alertIcon);
                    noDCAlertMsg.append(MessagesFactory.getMessages().no_container());
                    noDCAlertMsg.append("</span>"); //$NON-NLS-1$
                    alertHtml.setHTML(noDCAlertMsg.toString());
                    fieldSet.add(alertHtml);
                    fieldSet.layout(true);
                }
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
        SeriesHandler seriesCreation = model.addSeries(Series.of(MessagesFactory.getMessages().chart_journal_creation())
                .setColor(SERIES_1_COLOR));
        SeriesHandler seriesUpdate = model.addSeries(Series.of(MessagesFactory.getMessages().chart_journal_update()).setColor(
                SERIES_2_COLOR));
        addDataToSeries(seriesCreation, seriesUpdate);
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
        SeriesHandler seriesCreation = series.get(0);
        SeriesHandler seriesUpdate = series.get(1);

        seriesCreation.clear();
        seriesUpdate.clear();
        addDataToSeries(seriesCreation, seriesUpdate);
    }
    
    private AxesOptions getXAxesOptions() {
        return AxesOptions.create().addAxisOptions(
                AxisOptions.create().setAxisLabelAngle(70d).setTicks(getTicks()).setAutoscaleMargin(0.1));
    }

    private AxeTicks getTicks() {
        AxeTicks entityTicks = AxeTicks.create();
        double x = 1;
        for (String entityName : entityNamesSorted) {
            entityTicks.push(Tick.of(x, isDisplayText() ? entityName : "")); //$NON-NLS-1$
            x += 3;
        }
        return entityTicks;
    }

    @SuppressWarnings("unchecked")
    private void addDataToSeries(SeriesHandler seriesCreation, SeriesHandler seriesUpdate) {
        double x = 0;
        for (String entityName : entityNamesSorted) {
            Map<String, Integer> journalData = (Map<String, Integer>) chartData.get(entityName);
            seriesCreation.add(DataPoint.of(x, journalData.get(JOURNAL_ACTION_CREATE)));
            seriesUpdate.add(DataPoint.of(x + 1, journalData.get(JOURNAL_ACTION_UPDATE)));
            x += 3;
        }
    }

    @Override
    protected String getHoveringText(PlotItem item) {
        int valueY = (int) item.getDataPoint().getY();
        int valueX = (int) item.getDataPoint().getX();
        int entityNameIndex = valueX == 0 ? 0 : valueX / 3;
        return entityNamesSorted.get(entityNameIndex) + ": " + valueY + "(" + item.getSeries().getLabel() + ")"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    }

    @SuppressWarnings("deprecation")
    @Override
    protected Map<String, Object> parseJSONData(JSONArray jsonArray) {
        JSONObject currentJSONObj;
        JSONArray events;
        JSONObject creations;
        JSONObject updates;

        int numOfEntities = jsonArray.size();
        List<String> entities = new ArrayList<String>(numOfEntities);
        Map<String, Object> journalDataNew = new HashMap<String, Object>(numOfEntities);

        for (int i = 0; i < numOfEntities; i++) {
            currentJSONObj = (JSONObject) jsonArray.get(i);
            Set<String> entityNames = currentJSONObj.keySet();
            String entityName = entityNames.iterator().next();
            entities.add(entityName);
            events = currentJSONObj.get(entityName).isArray();
            assert (events.size() == 2);

            creations = events.get(0).isObject();
            updates = events.get(1).isObject();

            Map<String, Integer> eventMap = new HashMap<String, Integer>(2);
            int numOfUpdates = 0;
            int numOfCreates = 0;
            JSONArray createArray = creations.get("creations").isArray(); //$NON-NLS-1$
            JSONObject curCreate;
            for (int j = 0; j < createArray.size(); j++) {
                curCreate = createArray.get(j).isObject();
                numOfCreates += (int) curCreate.get(JOURNAL_ACTION_CREATE).isNumber().getValue();
            }

            JSONArray updateArray = updates.get("updates").isArray(); //$NON-NLS-1$
            JSONObject curUpdate;
            for (int k = 0; k < updateArray.size(); k++) {
                curUpdate = updateArray.get(k).isObject();
                numOfUpdates += (int) curUpdate.get(JOURNAL_ACTION_UPDATE).isNumber().getValue(); 
            }

            eventMap.put(JOURNAL_ACTION_CREATE, numOfCreates); 
            eventMap.put(JOURNAL_ACTION_UPDATE, numOfUpdates);
            journalDataNew.put(entityName, eventMap);
        }

        return journalDataNew;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected boolean isDifferentFrom(Map<String, Object> newData) {
        if (chartData.size() != newData.size()) {
            return true;
        } else {
            Map<String, Integer> events;
            Map<String, Integer> eventsNew;
            for (String entityName : chartData.keySet()) {
                events = (Map<String, Integer>) chartData.get(entityName);
                eventsNew = newData.get(entityName) != null ? (Map<String, Integer>) newData.get(entityName) : null;
                if (eventsNew == null) {
                    return true;
                }

                if (!events.get(JOURNAL_ACTION_CREATE).equals(eventsNew.get(JOURNAL_ACTION_CREATE))
                        || !events.get(JOURNAL_ACTION_UPDATE).equals(eventsNew.get(JOURNAL_ACTION_UPDATE))) {
                    return true;
                }

            }
        }
        return false;
    }
}
