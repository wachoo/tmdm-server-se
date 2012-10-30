// ============================================================================
//
// Copyright (C) 2006-2012 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.mdm.webapp.stagingareacontrol.client.view;

import org.talend.mdm.webapp.base.client.model.UserContextModel;
import org.talend.mdm.webapp.base.client.util.UserContextUtil;
import org.talend.mdm.webapp.stagingareacontrol.client.controller.ControllerContainer;
import org.talend.mdm.webapp.stagingareacontrol.client.model.PieChartData;
import org.talend.mdm.webapp.stagingareacontrol.client.model.StagingContainerModel;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.IconAlign;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.Label;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.google.gwt.core.client.GWT;
import com.google.gwt.editor.client.Editor.Path;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.sencha.gxt.chart.client.chart.Chart;
import com.sencha.gxt.chart.client.chart.Chart.Position;
import com.sencha.gxt.chart.client.chart.Legend;
import com.sencha.gxt.chart.client.chart.series.PieSeries;
import com.sencha.gxt.chart.client.chart.series.Series.LabelPosition;
import com.sencha.gxt.chart.client.chart.series.SeriesLabelConfig;
import com.sencha.gxt.chart.client.chart.series.SeriesLabelProvider;
import com.sencha.gxt.chart.client.chart.series.SeriesToolTipConfig;
import com.sencha.gxt.chart.client.draw.Gradient;
import com.sencha.gxt.chart.client.draw.RGB;
import com.sencha.gxt.chart.client.draw.Stop;
import com.sencha.gxt.chart.client.draw.sprite.TextSprite;
import com.sencha.gxt.chart.client.draw.sprite.TextSprite.TextAnchor;
import com.sencha.gxt.chart.client.draw.sprite.TextSprite.TextBaseline;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.LabelProvider;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;



public class StagingContainerSummaryView extends AbstractView {

    public static final String STAGING_AREA_TITLE = "staging_area_title"; //$NON-NLS-1$

    public static final String STAGING_AREA_WAITING = "staging_area_waiting"; //$NON-NLS-1$

    public static final String STAGING_AREA_INVALID = "staging_area_invalid"; //$NON-NLS-1$

    public static final String STAGING_AREA_VALID = "staging_area_valid"; //$NON-NLS-1$

    private StagingContainerModel stagingContainerModel;

    private Button startValidate;

    private Grid titleGrid;

    private Label titleLabel;

    private Label containerLabel;

    private Label containerName;

    private Label modelLabel;

    private Label dataModelName;

    private SimplePanel chartPanel;

    private Chart<PieChartData> chart;

    private ListStore<PieChartData> chartStore;

    private final int CHART_WIDTH = 400;

    private final int CHART_HEIGHT = 200;

    private HTMLPanel detailPanel;

    public interface DataPropertyAccess extends PropertyAccess<PieChartData> {

        ValueProvider<PieChartData, Integer> value();

        ValueProvider<PieChartData, String> name();

        ValueProvider<PieChartData, PieChartData> self();

        @Path("name")
        ModelKeyProvider<PieChartData> nameKey();
    }

    private static final DataPropertyAccess dataAccess = GWT.create(DataPropertyAccess.class);

    private void initPieChart() {
        chartStore = new ListStore<PieChartData>(dataAccess.nameKey());

        chart = new Chart<PieChartData>();
        chart.setDefaultInsets(10);
        chart.setStore(chartStore);
        chart.setShadowChart(true);
        chart.setAnimated(false);

        Gradient slice1 = new Gradient("sliceWaiting", 45); //$NON-NLS-1$
        slice1.addStop(new Stop(0, RGB.BLUE));
        slice1.addStop(new Stop(100, RGB.BLUE));
        chart.addGradient(slice1);
        //
        Gradient slice2 = new Gradient("sliceInvalid", 45); //$NON-NLS-1$
        slice2.addStop(new Stop(0, RGB.RED));
        slice2.addStop(new Stop(100, RGB.RED));
        chart.addGradient(slice2);
        //
        Gradient slice3 = new Gradient("sliceValid", 45); //$NON-NLS-1$
        slice3.addStop(new Stop(0, RGB.GREEN));
        slice3.addStop(new Stop(100, RGB.GREEN));
        chart.addGradient(slice3);

        final PieSeries<PieChartData> series = new PieSeries<PieChartData>();
        SeriesToolTipConfig<PieChartData> tipConfig = new SeriesToolTipConfig<PieChartData>();
        tipConfig.setLabelProvider(new SeriesLabelProvider<PieChartData>() {

            public String getLabel(PieChartData item, ValueProvider<? super PieChartData, ? extends Number> valueProvider) {
                return item.getName() + ": " + item.getValue(); //$NON-NLS-1$
            }
        });

        tipConfig.setShowDelay(250);
        series.setToolTipConfig(tipConfig);
        series.setAngleField(dataAccess.value());
        series.addColor(slice1);
        series.addColor(slice2);
        series.addColor(slice3);
        TextSprite textConfig = new TextSprite();
        textConfig.setTextBaseline(TextBaseline.MIDDLE);
        textConfig.setTextAnchor(TextAnchor.MIDDLE);
        SeriesLabelConfig<PieChartData> labelConfig = new SeriesLabelConfig<PieChartData>();
        labelConfig.setSpriteConfig(textConfig);
        labelConfig.setLabelPosition(LabelPosition.START);
        labelConfig.setValueProvider(dataAccess.value(), new LabelProvider<Integer>() {

            public String getLabel(Integer item) {
                if (item == 0)
                    return null;
                double sum = 0D;
                for (int i = 0; i < chartStore.size(); i++) {
                    PieChartData data = chartStore.get(i);
                    sum += data.getValue();
                }
                return NumberFormat.getFormat("00%").format(item / sum); //$NON-NLS-1$
            }
        });

        series.setLabelConfig(labelConfig);
        series.setHighlighting(true);
        series.setLegendValueProvider(dataAccess.self(), new LabelProvider<PieChartData>() {

            public String getLabel(PieChartData item) {
                return item.getName();
            }
        });
        chart.addSeries(series);

        final Legend<PieChartData> legend = new Legend<PieChartData>();
        legend.setPosition(Position.RIGHT);
        legend.setItemHighlighting(true);
        legend.setItemHiding(true);
        chart.setLegend(legend);
        chart.setSize(CHART_WIDTH + "px", CHART_HEIGHT + "px"); //$NON-NLS-1$ //$NON-NLS-2$

        chartPanel = new SimplePanel();
        chartPanel.setWidget(chart);
    }

    @Override
    protected void initComponents() {
        UserContextModel ucx = UserContextUtil.getUserContext();
        initPieChart();

        titleLabel = new Label(messages.staging_area_title());
        titleLabel.setTagName("div"); //$NON-NLS-1$
        titleLabel.setStyleAttribute("font-weight", "bold"); //$NON-NLS-1$//$NON-NLS-2$
        titleLabel.setStyleAttribute("font-size", "14px"); //$NON-NLS-1$//$NON-NLS-2$
        containerLabel = new Label(messages.data_container());
        containerName = new Label(ucx.getDataContainer());
        containerName.setStyleAttribute("font-weight", "bold"); //$NON-NLS-1$//$NON-NLS-2$

        modelLabel = new Label(messages.data_model());
        dataModelName = new Label(ucx.getDataModel());
        dataModelName.setStyleAttribute("font-weight", "bold"); //$NON-NLS-1$//$NON-NLS-2$

        titleGrid = new Grid(2, 6);


        StringBuilder buffer = new StringBuilder();
        buffer.append("<div style='margin-bottom:10px; font-weight: bold;' id='" + STAGING_AREA_TITLE + "'></div>"); //$NON-NLS-1$ //$NON-NLS-2$
        buffer.append("<div style='margin-left:20px; color:#0000ff; margin-bottom:5px;' id='" + STAGING_AREA_WAITING + "'></div>"); //$NON-NLS-1$ //$NON-NLS-2$
        buffer.append("<div style='margin-left:20px; color:#ff0000; margin-bottom:5px;' id='" + STAGING_AREA_INVALID + "'></div>"); //$NON-NLS-1$//$NON-NLS-2$
        buffer.append("<div style='margin-left:20px; color:#00aa00; margin-bottom:5px;' id='" + STAGING_AREA_VALID + "'></div>"); //$NON-NLS-1$//$NON-NLS-2$

        detailPanel = new HTMLPanel(buffer.toString());

        detailPanel.setSize("400px", "80px"); //$NON-NLS-1$//$NON-NLS-2$

        startValidate = new Button(messages.start_validation());
        startValidate.setSize(200, 30);
        startValidate.setEnabled(false);
        startValidate.setIconAlign(IconAlign.TOP);

        mainPanel.setAutoHeight(true);
        mainPanel.setBodyBorder(false);
    }

    @Override
    protected void registerEvent() {
        startValidate.addSelectionListener(new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent ce) {
                ControllerContainer.get().getSummaryController().startValidation();
            }
        });
    }

    @Override
    protected void initLayout() {
        mainPanel.setLayout(new TableLayout(2));
        TableData titleData = new TableData();
        titleData.setColspan(2);
        titleData.setRowspan(1);

        titleGrid.setWidget(0, 0, titleLabel);
        titleGrid.setWidget(1, 0, containerLabel);
        titleGrid.setWidget(1, 1, containerName);
        containerName.setStyleAttribute("margin-right", "10px"); //$NON-NLS-1$//$NON-NLS-2$
        titleGrid.setWidget(1, 2, modelLabel);
        titleGrid.setWidget(1, 3, dataModelName);
        dataModelName.setStyleAttribute("margin-right", "5px");//$NON-NLS-1$//$NON-NLS-2$

        mainPanel.add(titleGrid, titleData);
        TableData chartData = new TableData();
        chartData.setColspan(1);
        chartData.setRowspan(2);
        mainPanel.add(chartPanel, chartData);
        TableData detailData = new TableData();
        detailData.setColspan(1);
        detailData.setRowspan(1);
        mainPanel.add(detailPanel, detailData);
        TableData startData = new TableData();
        startData.setHorizontalAlign(HorizontalAlignment.CENTER);
        mainPanel.add(startValidate, startData);
    }

    public void initDetailPanel() {
        if (stagingContainerModel != null) {
            int waiting = stagingContainerModel.getWaitingValidationRecords();
            int valid = stagingContainerModel.getValidRecords();
            int invalid = stagingContainerModel.getInvalidRecords();

            Element titleEl = detailPanel.getElementById(STAGING_AREA_TITLE);
            titleEl.setInnerHTML(messages.total_desc("<b>" + stagingContainerModel.getTotalRecords() + "</b>")); //$NON-NLS-1$ //$NON-NLS-2$

            Element waitingEl = detailPanel.getElementById(STAGING_AREA_WAITING);
            waitingEl.setInnerHTML(messages.waiting_desc("<b>" + waiting + "</b>")); //$NON-NLS-1$ //$NON-NLS-2$

            Element invalidEl = detailPanel.getElementById(STAGING_AREA_INVALID);
            invalidEl.setInnerHTML(messages.invalid_desc("<span id=\"open_invalid_record\" style=\"color:red; text-decoration:underline; cursor:pointer;\">", "<b>" + invalid + "</b>", "</span>")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
            Element open_invalid_record = detailPanel.getElementById("open_invalid_record"); //$NON-NLS-1$
            addClickForRecord(2, open_invalid_record);

            Element validEl = detailPanel.getElementById(STAGING_AREA_VALID);
            validEl.setInnerHTML(messages.valid_desc("<span id=\"open_valid_record\" style=\"color:green; text-decoration:underline; cursor:pointer;\">", "<b>" + valid + "</b>", "</span>")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
            Element open_valid_record = detailPanel.getElementById("open_valid_record"); //$NON-NLS-1$
            addClickForRecord(3, open_valid_record);
        }
    }

    private native void addClickForRecord(Integer state, Element el)/*-{
		var instance = this;
		el.onclick = function() {
			instance.@org.talend.mdm.webapp.stagingareacontrol.client.view.StagingContainerSummaryView::onOpenRecord(Ljava/lang/Integer;)(state);
		};
    }-*/;

    void onOpenRecord(Integer state) {
        ControllerContainer.get().getSummaryController().openInvalidRecordToBrowseRecord(state);
    }

    public void refresh(StagingContainerModel stagingContainerModel) {
        this.stagingContainerModel = stagingContainerModel;
        initDetailPanel();
        updateChartData();
    }


    private void updateChartData() {
        if (stagingContainerModel != null) {
            int waiting = stagingContainerModel.getWaitingValidationRecords();
            int valid = stagingContainerModel.getValidRecords();
            int invalid = stagingContainerModel.getInvalidRecords();
            chartStore.clear();
            PieChartData waitingData = new PieChartData(messages.waiting(), waiting);
            chartStore.add(waitingData);
            PieChartData invalidData = new PieChartData(messages.invalid(), invalid);
            chartStore.add(invalidData);
            PieChartData validData = new PieChartData(messages.valid(), valid);
            chartStore.add(validData);
            chart.redrawChartForced();
        }
    }

    public StagingContainerModel getStagingContainerModel() {
        return stagingContainerModel;
    }

    public void setEnabledStartValidation(boolean enabled) {
        startValidate.setEnabled(enabled);
    }

    public boolean isEnabledStartValidation() {
        return startValidate.isEnabled();
    }


    public Button getStartValidateButton() {
        return startValidate;
    }

    @Override
    protected void onAttach() {
        super.onAttach();
        ControllerContainer.get().getSummaryController().refreshView();
    }
}
