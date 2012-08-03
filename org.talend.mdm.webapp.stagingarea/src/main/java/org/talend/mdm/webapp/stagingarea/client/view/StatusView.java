package org.talend.mdm.webapp.stagingarea.client.view;

import com.extjs.gxt.charts.client.Chart;
import com.extjs.gxt.charts.client.event.ChartEvent;
import com.extjs.gxt.charts.client.event.ChartListener;
import com.extjs.gxt.charts.client.model.ChartModel;
import com.extjs.gxt.charts.client.model.Legend;
import com.extjs.gxt.charts.client.model.Legend.Position;
import com.extjs.gxt.charts.client.model.charts.PieChart;
import com.extjs.gxt.ui.client.widget.HorizontalPanel;
import com.extjs.gxt.ui.client.widget.Info;
import com.extjs.gxt.ui.client.widget.Label;
import com.extjs.gxt.ui.client.widget.layout.VBoxLayout;



public class StatusView extends AbstractView {

    private HorizontalPanel topPanel;

    private HorizontalPanel bottomPanel;

    private Label stagingAreaLabel;

    private Label dataContainerLabel;

    private Label dataModelLabel;

    private static final String Chart_Url = "/core/secure/gxt/resources/chart/open-flash-chart.swf"; //$NON-NLS-1$

    private Chart chart;

    @Override
    protected void initComponents() {
        topPanel = new HorizontalPanel();
        bottomPanel = new HorizontalPanel();
        stagingAreaLabel = new Label("Staging Area"); //$NON-NLS-1$
        dataContainerLabel = new Label("data_container"); //$NON-NLS-1$
        dataModelLabel = new Label("data_model"); //$NON-NLS-1$
        chart = new Chart(Chart_Url);
    }

    @Override
    protected void initEvent() {
        // TODO Auto-generated method stub
    }

    @Override
    protected void initView() {
        mainPanel.setLayout(new VBoxLayout());

        mainPanel.add(topPanel);
        mainPanel.add(bottomPanel);

        topPanel.add(stagingAreaLabel);
        topPanel.add(dataContainerLabel);
        topPanel.add(dataModelLabel);

        chart.setBorders(true);
        chart.setChartModel(getPieChartData());
        chart.setSize(400, 200);
        bottomPanel.add(chart);
    }

    private ChartModel getPieChartData() {
        ChartModel cm = new ChartModel("Sales by Region", "font-size: 14px; font-family: Verdana; text-align: center;");
        cm.setBackgroundColour("#fffff5");
        Legend lg = new Legend(Position.RIGHT, true);
        lg.setPadding(1);
        cm.setLegend(lg);

        PieChart pie = new PieChart();
        pie.setAlpha(0.5f);
        pie.setNoLabels(true);
        pie.setTooltip("#label# $#val#M<br>#percent#");
        pie.setColours("#ff0000", "#00aa00", "#0000ff", "#ff9900", "#ff00ff");
        pie.addSlices(new PieChart.Slice(100, "AU", "Australia"));
        pie.addSlices(new PieChart.Slice(200, "US", "USA"));
        pie.addSlices(new PieChart.Slice(150, "JP", "Japan"));
        pie.addSlices(new PieChart.Slice(120, "DE", "Germany"));
        pie.addSlices(new PieChart.Slice(60, "UK", "United Kingdom"));
        pie.addChartListener(listener);

        cm.addChartConfig(pie);
        return cm;
    }
    
    private ChartListener listener = new ChartListener() {
        
        public void chartClick(ChartEvent ce) {  
          Info.display("Chart Clicked", "You selected {0}.", "" + ce.getValue());  
        }  
    };
}
