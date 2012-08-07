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
package org.talend.mdm.webapp.stagingarea.client.view;

import org.talend.mdm.webapp.stagingarea.client.model.StagingContainerModel;

import com.extjs.gxt.charts.client.Chart;
import com.extjs.gxt.charts.client.model.ChartModel;
import com.extjs.gxt.charts.client.model.Legend;
import com.extjs.gxt.charts.client.model.Legend.Position;
import com.extjs.gxt.charts.client.model.charts.PieChart;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.layout.VBoxLayout;
import com.extjs.gxt.ui.client.widget.layout.VBoxLayoutData;



public class StagingContainerSummaryView extends AbstractView {

    private StagingContainerModel model;

    private Button startValidate;

    private static final String Chart_Url = "/core/secure/gxt/resources/chart/open-flash-chart.swf"; //$NON-NLS-1$

    private Chart chart;

    @Override
    protected void initComponents() {
        chart = new Chart(Chart_Url);
        chart.setBorders(true);
        chart.setChartModel(getPieChartData());
        chart.setSize(500, 180);
        chart.setBorders(false);

        startValidate = new Button("Start Validation"); //$NON-NLS-1$

        mainPanel.setHeight(220);
        mainPanel.setBodyBorder(false);
    }

    @Override
    protected void registerEvent() {
        startValidate.addSelectionListener(new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent ce) {

            }
        });
    }

    @Override
    protected void initLayout() {
        mainPanel.setLayout(new VBoxLayout());
        mainPanel.add(chart);
        mainPanel.add(startValidate, new VBoxLayoutData(0, 0, 0, 400));
    }

    private ChartModel getPieChartData() {
        ChartModel cm = new ChartModel("STAGING AREA &lt;data_container&gt;", //$NON-NLS-1$
                "font-size: 14px; font-family: Verdana; text-align: center;"); //$NON-NLS-1$
        cm.setBackgroundColour("#FFFFFF"); //$NON-NLS-1$
        Legend lg = new Legend(Position.RIGHT, true);
        lg.setPadding(1);
        cm.setLegend(lg);

        if (model != null) {

            int waiting = model.getWaitingValidationRecords();
            int valid = model.getValidRecords();
            int invalid = model.getInvalidRecords();

            PieChart pie = new PieChart();
            pie.setAlpha(1.0f);
            pie.setNoLabels(true);
            pie.setTooltip("#label# record #val#<br>#percent#"); //$NON-NLS-1$
            pie.setColours("#0000ff", "#ff0000", "#00aa00"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            pie.addSlices(new PieChart.Slice(waiting, "Waiting", "Waiting for validation <" + waiting + "> records")); //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$
            pie.addSlices(new PieChart.Slice(invalid, "Invalid", "Invalid: <" + invalid + "> records open invalid records...")); //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$
            pie.addSlices(new PieChart.Slice(valid, "Valid", "Valid: <" + valid + "> records")); //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$

            cm.addChartConfig(pie);
        }
        return cm;
    }
    
    public void refresh(StagingContainerModel model) {
        this.model = model;
        chart.setChartModel(getPieChartData());
    }
}
