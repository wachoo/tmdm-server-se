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
import org.talend.mdm.webapp.stagingareacontrol.client.model.StagingContainerModel;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.IconAlign;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.Label;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.RepeatingCommand;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.SimplePanel;

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

    private Frame chartFrame;

    private JavaScriptObject chartOpt;

    private final int CHART_WIDTH = 400;

    private final int CHART_HEIGHT = 200;

    private HTMLPanel detailPanel;

    private void initPieChart() {
        initChartCallback();
        chartFrame = new Frame("/stagingarea/chart/Chart.html"); //$NON-NLS-1$
        chartFrame.setSize("400px", "200px"); //$NON-NLS-1$ //$NON-NLS-2$
        chartFrame.getElement().getStyle().setBorderWidth(0D, Unit.PX);
        chartFrame.getElement().getStyle().setOverflow(Overflow.HIDDEN);
        chartFrame.getElement().setAttribute("frameborder", "no"); //$NON-NLS-1$//$NON-NLS-2$
        chartFrame.getElement().setAttribute("scrolling", "no"); //$NON-NLS-1$//$NON-NLS-2$
        chartPanel = new SimplePanel();
        chartPanel.setWidget(chartFrame);
    }

    private native void initChartCallback()/*-{
		var instance = this;
		$wnd.chartReady = function(opt) {
			instance.@org.talend.mdm.webapp.stagingareacontrol.client.view.StagingContainerSummaryView::chartOpt = opt;
		};
    }-*/;

    private native void updateChart(String waitstr, double waiting, String invalidStr, double invalid, String validStr, double valid)/*-{
		var opt = this.@org.talend.mdm.webapp.stagingareacontrol.client.view.StagingContainerSummaryView::chartOpt;
		if (opt) {
			opt.updateData(waitstr, waiting, invalidStr, invalid, validStr,
					valid);
		}
    }-*/;

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
            int invalid = stagingContainerModel.getInvalidRecords();
            int valid = stagingContainerModel.getValidRecords();
            double sum = waiting + invalid + valid;
            NumberFormat format = NumberFormat.getFormat("#0"); //$NON-NLS-1$
            final double waitingPer = format.parse(format.format(waiting / sum * 100));
            final double invalidPer = format.parse(format.format(invalid / sum * 100));
            final double validPer = format.parse(format.format(valid / sum * 100));

            if (chartOpt == null) {
                Scheduler.get().scheduleIncremental(new RepeatingCommand() {

                    public boolean execute() {
                        if (chartOpt == null) {
                            return true;
                        }
                        updateChart(messages.waiting(), waitingPer, messages.invalid(), invalidPer, messages.valid(), validPer);
                        return false;
                    }
                });
            } else {
                updateChart(messages.waiting(), waitingPer, messages.invalid(), invalidPer, messages.valid(), validPer);
            }
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
